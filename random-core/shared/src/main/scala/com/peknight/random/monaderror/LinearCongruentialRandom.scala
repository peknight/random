package com.peknight.random.monaderror

import cats.{Applicative, MonadError}
import com.peknight.random.Random as Rand
import com.peknight.random.algorithm.LinearCongruential

private[random] case class LinearCongruentialRandom[F[_]](seed: Long)(using MonadError[F, Throwable]) extends Random[F]:
  def next(bits: Int): F[(Rand[F], Int)] =
    val (nextSeed, r) = LinearCongruential.next(seed, bits)
    Applicative[F].pure((LinearCongruentialRandom(nextSeed), r))
  end next
end LinearCongruentialRandom
