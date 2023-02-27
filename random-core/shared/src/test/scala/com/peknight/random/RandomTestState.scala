package com.peknight.random

import cats.Monad
import cats.data.StateT
import com.peknight.generic.tuple.Lifted
import com.peknight.generic.tuple.syntax.mapN
import com.peknight.random.state.*

import scala.deriving.Mirror

object RandomTestState:

  def state[F[_] : Monad](param: RandomTestParam)(using mirror: Mirror.ProductOf[RandomTestResult])
  : StateT[F, Random[F], RandomTestResult] =
    (
      nextInt,
      nextIntBounded(param.intBound),
      between(param.intMin, param.intMax),
      nextBytes(param.byteSize)(Vector),
      nextLong,
      nextLongBounded(param.longBound),
      between(param.longMin, param.longMax),
      nextBoolean,
      nextFloat,
      between(param.floatMin, param.floatMax),
      nextDouble,
      between(param.doubleMin, param.doubleMax),
      nextString(param.stringLength),
      nextPrintableChar,
      shuffle(param.list)
    ).asInstanceOf[Lifted[[A] =>> StateT[F, Random[F], A], mirror.MirroredElemTypes]].mapN(mirror.fromProduct)

end RandomTestState
