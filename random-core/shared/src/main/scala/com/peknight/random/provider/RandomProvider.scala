package com.peknight.random.provider

import cats.effect.{Ref, Sync}
import cats.syntax.functor.*
import com.peknight.random.Random
import com.peknight.random.algorithm.LinearCongruential

trait RandomProvider[F[_]]:
  def random: F[Random[F]]
end RandomProvider
object RandomProvider:
  def of[F[_]: Sync](random: Long => Random[F]): F[RandomProvider[F]] =
    Ref.of[F, Long](LinearCongruential.seedUniquifier).map(seedUniquifier =>
      LinearCongruentialRandomProvider(seedUniquifier)(random)
    )
  def apply[F[_]](using provider: RandomProvider[F]): RandomProvider[F] = provider
end RandomProvider
