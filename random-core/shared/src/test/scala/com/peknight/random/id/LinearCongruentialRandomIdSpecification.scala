package com.peknight.random.id

import cats.Id
import com.peknight.random.{RandomTestParam, RandomTestResult, RandomTestState}
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import scala.deriving.Mirror

class LinearCongruentialRandomIdSpecification extends Properties("LinearCongruentialRandom[Id]"):

  def idRandomResult(param: RandomTestParam)(using mirror: Mirror.ProductOf[RandomTestResult]): RandomTestResult =
    RandomTestState.state[Id](param).runA(Random(param.seed))

  property("LinearCongruentialRandom[Id]'s result should equals scala.util.Random's") = forAll (RandomTestParam.gen) {
    (p: RandomTestParam) => idRandomResult(p) == RandomTestResult.scalaRandomResult(p)
  }
end LinearCongruentialRandomIdSpecification
