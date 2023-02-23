package com.peknight.random

import cats.{Eval, Id}
import com.peknight.random.id.Random as IdRandom
import com.peknight.random.monad.Random as MonadRandom
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

import scala.deriving.Mirror

class LinearCongruentialRandomSpecification extends Properties("LinearCongruentialRandom"):

  def idRandomResult(param: RandomTestParam): RandomTestResult =
    RandomTestState.state[Id](param).runA(IdRandom(param.seed))

  def evalRandomResult(param: RandomTestParam): RandomTestResult =
    RandomTestState.state[Eval](param).runA(MonadRandom[Eval](param.seed)).value

  property("Id") = forAll (RandomTestParam.gen) {
    (p: RandomTestParam) => idRandomResult(p) == RandomTestResult.scalaRandomResult(p)
  }

  property("Eval") = forAll(RandomTestParam.gen) {
    (p: RandomTestParam) => evalRandomResult(p) == RandomTestResult.scalaRandomResult(p)
  }
end LinearCongruentialRandomSpecification
