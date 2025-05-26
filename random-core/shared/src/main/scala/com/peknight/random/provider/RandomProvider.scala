package com.peknight.random.provider

import cats.effect.Async
import cats.effect.std.AtomicCell
import cats.syntax.functor.*
import com.peknight.random.Random
import com.peknight.random.algorithm.LinearCongruential

trait RandomProvider[F[_]]:
  def random: F[Random[F]]
end RandomProvider
object RandomProvider:
  java.util.Random()
  def of[F[_]: Async](random: Long => Random[F]): F[RandomProvider[F]] =
    AtomicCell[F].of[Long](LinearCongruential.seedUniquifier).map(seedUniquifier =>
      LinearCongruentialRandomProvider(seedUniquifier)(random)
    )
  def apply[F[_]](using provider: RandomProvider[F]): RandomProvider[F] = provider
end RandomProvider
