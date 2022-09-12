package mikumiku

import spinal.core._

import scala.collection.mutable

/**
 * @author davine
 * @date 2022/9/12
 */
class Stageable[T <: Data](_dataType : => T) extends HardType[T](_dataType) with Nameable{
  def dataType = apply()
  setWeakName(this.getClass.getSimpleName.replace("$",""))
}

class Stage() extends Area {

  def outsideCondScope[T](that : => T) : T = {
    val body = Component.current.dslBody
    val ctx = body.push()
    val swapContext = body.swap()
    val ret = that
    ctx.restore()
    swapContext.appendBack()
    ret
  }

  def input[T <: Data](key : Stageable[T]) : T = {
    inputs.getOrElseUpdate(key.asInstanceOf[Stageable[Data]],outsideCondScope{
      val input,inputDefault = key()
      inputsDefault(key.asInstanceOf[Stageable[Data]]) = inputDefault
      input := inputDefault
      input.setPartialName(this, key.getName())
    }).asInstanceOf[T]
  }

  def output[T <: Data](key : Stageable[T]) : T = {
    outputs.getOrElseUpdate(key.asInstanceOf[Stageable[Data]],outsideCondScope{
      val output,outputDefault = key()
      outputsDefault(key.asInstanceOf[Stageable[Data]]) = outputDefault
      output := outputDefault
      output //.setPartialName(this,"output_" + key.getName())
    }).asInstanceOf[T]
  }

  def insert[T <: Data](key : Stageable[T]) : T = inserts.getOrElseUpdate(key.asInstanceOf[Stageable[Data]],outsideCondScope(key())).asInstanceOf[T]

  val inputs   = mutable.LinkedHashMap[Stageable[Data],Data]()
  val outputs  = mutable.LinkedHashMap[Stageable[Data],Data]()
  val signals  = mutable.LinkedHashMap[Stageable[Data],Data]()
  val inserts  = mutable.LinkedHashMap[Stageable[Data],Data]()

  val inputsDefault   = mutable.LinkedHashMap[Stageable[Data],Data]()
  val outputsDefault  = mutable.LinkedHashMap[Stageable[Data],Data]()

}
