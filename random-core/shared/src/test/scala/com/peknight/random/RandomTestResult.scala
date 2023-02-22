package com.peknight.random

case class RandomTestResult(nextInt: Int, nextIntBounded: Int, betweenInt: Int, nextBytes: Vector[Byte],
                            nextLong: Long, nextLongBounded: Long, betweenLong: Long, nextBoolean: Boolean,
                            nextFloat: Float, betweenFloat: Float, nextDouble: Double, betweenDouble: Double,
                            nextString: String, nextPrintableChar: Char, shuffle: List[Int]) derives CanEqual

object RandomTestResult:

  def scalaRandomResult(param: RandomTestParam): RandomTestResult =
    val random = new scala.util.Random(param.seed)
    RandomTestResult(
      random.nextInt(),
      random.nextInt(param.intBound),
      random.between(param.intMin, param.intMax),
      random.nextBytes(param.byteSize).to(Vector),
      random.nextLong(),
      random.nextLong(param.longBound),
      random.between(param.longMin, param.longMax),
      random.nextBoolean(),
      random.nextFloat(),
      random.between(param.floatMin, param.floatMax),
      random.nextDouble(),
      random.between(param.doubleMin, param.doubleMax),
      random.nextString(param.stringLength),
      random.nextPrintableChar(),
      random.shuffle(param.list)
    )

end RandomTestResult

