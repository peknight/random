package com.peknight.random

import cats.Monad
import cats.data.StateT
import com.peknight.generic.tuple.Lifted
import com.peknight.generic.tuple.ops.TupleOps
import com.peknight.random.state.*

import scala.deriving.Mirror

object RandomTestState:

  def state[F[_] : Monad](param: RandomTestParam)(using mirror: Mirror.ProductOf[RandomTestResult])
  : StateT[F, Random[F], RandomTestResult] =
    type Repr = mirror.MirroredElemTypes
    type G[A] = StateT[F, Random[F], A]
    TupleOps.mapN[Repr, G, RandomTestResult]((
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
    ).asInstanceOf[Lifted[G, Repr]])(mirror.fromProduct)

end RandomTestState
