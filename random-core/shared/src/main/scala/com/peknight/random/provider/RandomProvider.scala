package com.peknight.random.provider

import cats.effect.Async
import cats.effect.std.AtomicCell
import cats.syntax.functor.*
import com.peknight.random.Random
import com.peknight.random.algorithm.LinearCongruential
import com.peknight.random.monaderror.LinearCongruentialRandom

trait RandomProvider[F[_]]:
  def random: F[Random[F]]
end RandomProvider
object RandomProvider:
  def of[F[_]: Async](random: Long => Random[F]): F[RandomProvider[F]] =
    AtomicCell[F].of[Long](LinearCongruential.seedUniquifier).map(seedUniquifier =>
      LinearCongruentialRandomProvider(seedUniquifier)(random)
    )
  def linearCongruential[F[_]: Async]: F[RandomProvider[F]] = of[F](LinearCongruentialRandom[F])
  def apply[F[_]](using provider: RandomProvider[F]): RandomProvider[F] = provider
end RandomProvider
