package mikumiku

import spinal.core._

/**
 * @author davine
 * @date 2022/9/12
 */
class Miku extends Component with Pipeline {
  type T = Miku
  def newStage(): Stage = { val s = new Stage; stages += s; s }

  val stage0 = newStage()
  val stage1 = newStage()
  val stage2 = newStage()
  val stage3 = newStage()
  val stage4 = newStage()
  val stage5 = newStage()
  val stage6 = newStage()

  plugins += new SingPlugin
  plugins += new DancePlugin
  plugins += new RapPlugin
}

object MyTopLevelVerilog {
  def main(args: Array[String]) {
    SpinalConfig(mode = SystemVerilog, targetDirectory = "miku", oneFilePerComponent = true)
      .generate {
        val topLevel = new Miku
        topLevel
      }
  }
}
