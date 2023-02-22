package com.peknight.random

import org.scalacheck.Gen

case class RandomTestParam(seed: Long, intBound: Int, intMin: Int, intMax: Int, byteSize: Int,
                           longBound: Long, longMin: Long, longMax: Long,
                           floatMin: Float, floatMax: Float, doubleMin: Double, doubleMax: Double,
                           stringLength: Int, list: List[Int])

object RandomTestParam:
  def range[N: Numeric](gen: Gen[N]): Gen[(N, N)] =
    for
      a <- gen
      b <- gen
    yield if Numeric[N].compare(a, b) < 0 then (a, b) else (b, a)

  val gen: Gen[RandomTestParam] =
    val longGen = Gen.choose(Long.MinValue, Long.MaxValue)
    for
      seed <- longGen
      intBound <- Gen.choose(1, Int.MaxValue)
      betweenIntRange <- range[Int](Gen.choose(Int.MinValue, Int.MaxValue))
      (intMin, intMax) = betweenIntRange
      byteSize <- Gen.choose(2, 16)
      longBound <- Gen.choose(1L, Long.MaxValue)
      betweenLongRange <- range[Long](longGen)
      (longMin, longMax) = betweenLongRange
      betweenFloatRange <- range[Float](Gen.choose(Float.MinValue, Float.MaxValue))
      (floatMin, floatMax) = betweenFloatRange
      betweenDoubleRange <- range[Double](Gen.choose(Double.MinValue, Double.MaxValue))
      (doubleMin, doubleMax) = betweenDoubleRange
      stringLength <- Gen.choose(2, 16)
      listSize <- Gen.choose(2, 16)
      list <- Gen.listOfN(listSize, Gen.choose(Int.MinValue, Int.MaxValue))
    yield
      RandomTestParam(seed, intBound, intMin, intMax, byteSize, longBound, longMin, longMax, floatMin, floatMax,
        doubleMin, doubleMax, stringLength, list)
end RandomTestParam