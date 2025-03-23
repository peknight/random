package com.peknight.random.provider

import cats.effect.{Clock, Ref, Sync}
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import com.peknight.random.Random
import com.peknight.random.algorithm.LinearCongruential

case class LinearCongruentialRandomProvider[F[_]: Sync](seedUniquifier: Ref[F, Long])(randomF: Long => Random[F])
  extends RandomProvider[F]:
  def random: F[Random[F]] =
    for
      monotonic <- Clock[F].monotonic
      seed <- seedUniquifier.modify(seedUniquifier => LinearCongruential.nanoTime(seedUniquifier, monotonic.toNanos))
    yield
      randomF(seed)
end LinearCongruentialRandomProvider
