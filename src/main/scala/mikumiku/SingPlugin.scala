package mikumiku

import spinal.core._

/**
 * @author davine
 * @date 2022/9/12
 */
class SingPlugin extends Plugin[Miku] {

  object SING extends Stageable(UInt(32 bits))

  override def setup(pipeline: Miku): Unit = {
    // do nothing
  }

  override def build(pipeline: Miku): Unit = {
    import pipeline._

    stage0 plug new Area {
      import stage0._
      insert(SING) := U(1)
    }

    stage1 plug new Area {
      import stage1._
      val aha = input(SING) + U(1)
    }
  }
}

object RapDanceSharedScope {
  object RAP extends Stageable(UInt(32 bits))
}

class RapPlugin extends Plugin[Miku] {

  override def setup(pipeline: Miku): Unit = {
    // do nothing
  }

  override def build(pipeline: Miku): Unit = {
    import pipeline._
    import RapDanceSharedScope._
    stage2 plug new Area {
      import stage2._
      insert(RAP) := U(666)
    }

    stage3 plug new Area {
      import stage3._
      val rap = input(RAP) + U(3)
    }
  }
}

class DancePlugin extends Plugin[Miku] {

  object DANCE extends Stageable(UInt(32 bits))

  override def setup(pipeline: Miku): Unit = {
    // do nothing
  }

  override def build(pipeline: Miku): Unit = {
    import pipeline._

    stage2 plug new Area {
      import stage2._
      import RapDanceSharedScope._
      insert(DANCE) := U(2) + input(RAP)
      output(DANCE)
    }

    stage4 plug new Area {
      import stage4._
      val dance = input(DANCE)
    }
  }
}