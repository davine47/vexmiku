package mikumiku

import spinal.core._
import spinal.lib._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * @author davine
 * @date 2022/9/12
 */
trait PipelineThing[T]

trait Pipeline {
  type T <: Pipeline
  val plugins = ArrayBuffer[Plugin[T]]()
  var stages = ArrayBuffer[Stage]()

  def stageBefore(stage : Stage) = stages(indexOf(stage)-1)
  def indexOf(stage : Stage) = stages.indexOf(stage)

  def build(): Unit ={
    plugins.foreach(_.pipeline = this.asInstanceOf[T])
    plugins.foreach(_.setup(this.asInstanceOf[T]))

    plugins.foreach{ p =>
      p.parentScope = Component.current.dslBody //Put the given plugin as a child of the current component
      p.reflectNames()
    }

    //Build plugins
    plugins.foreach(_.build(this.asInstanceOf[T]))

    //Interconnect stages
    class KeyInfo{
      var insertStageId = Int.MaxValue
      var lastInputStageId = Int.MinValue
      var lastOutputStageId = Int.MinValue

      def addInputStageIndex(stageId : Int): Unit = {
        require(stageId >= insertStageId)
        lastInputStageId = Math.max(lastInputStageId,stageId)
        lastOutputStageId = Math.max(lastOutputStageId,stageId-1)
      }


      def addOutputStageIndex(stageId : Int): Unit = {
        require(stageId >= insertStageId)
        lastInputStageId = Math.max(lastInputStageId,stageId)
        lastOutputStageId = Math.max(lastOutputStageId,stageId)
      }

      def setInsertStageId(stageId : Int) = insertStageId = stageId
    }

    val inputOutputKeys = mutable.LinkedHashMap[Stageable[Data],KeyInfo]()
    val insertedStageable = mutable.Set[Stageable[Data]]()

    for(stageIndex <- 0 until stages.length; stage = stages(stageIndex)){
      stage.inserts.keysIterator.foreach(signal => inputOutputKeys.getOrElseUpdate(signal,new KeyInfo).setInsertStageId(stageIndex))
      stage.inserts.keysIterator.foreach(insertedStageable += _)
    }

    val missingInserts = mutable.Set[Stageable[Data]]()
    for(stageIndex <- 0 until stages.length; stage = stages(stageIndex)){
      stage.inputs.keysIterator.foreach(key => if(!insertedStageable.contains(key)) missingInserts += key)
      stage.outputs.keysIterator.foreach(key => if(!insertedStageable.contains(key)) missingInserts += key)
    }

    if(missingInserts.nonEmpty){
      throw new Exception("Missing inserts : " + missingInserts.map(_.getName()).mkString(", "))
    }

    for(stageIndex <- 0 until stages.length; stage = stages(stageIndex)){
      stage.inputs.keysIterator.foreach(key => inputOutputKeys.getOrElseUpdate(key,new KeyInfo).addInputStageIndex(stageIndex))
      stage.outputs.keysIterator.foreach(key => inputOutputKeys.getOrElseUpdate(key,new KeyInfo).addOutputStageIndex(stageIndex))
    }

    for((key,info) <- inputOutputKeys) {
      //Interconnect inputs -> outputs
      for (stageIndex <- info.insertStageId to info.lastOutputStageId;
           stage = stages(stageIndex)) {
        stage.output(key)
        val outputDefault = stage.outputsDefault.getOrElse(key, null)
        if (outputDefault != null) {
          outputDefault := stage.input(key)
        }
      }

      //Interconnect outputs -> inputs
      for (stageIndex <- info.insertStageId to info.lastInputStageId) {
        val stage = stages(stageIndex)
        stage.input(key)
        val inputDefault = stage.inputsDefault.getOrElse(key, null)
        if (inputDefault != null) {
          if (stageIndex == info.insertStageId) {
            inputDefault := stage.inserts(key)
          } else {
            val stageBefore = stages(stageIndex - 1)
            val cond = True
            inputDefault := RegNextWhen(stageBefore.output(key), cond).setName(s"${stageBefore.getName()}_to_${stage.getName()}_${key.getName()}_r")
          }
        }
      }


    }
  }

  Component.current.addPrePopTask(() => build())
}
