package com.peknight.random.monad

import cats.{Applicative, Monad}
import com.peknight.random.Random as Rand
import com.peknight.random.algorithm.LinearCongruential

case class LinearCongruentialRandom[F[_] : Monad](seed: Long) extends Random[F]:
  def next(bits: Int): F[(Rand[F], Int)] =
    val (nextSeed, r) = LinearCongruential.next(seed, bits)
    Applicative[F].pure((LinearCongruentialRandom(nextSeed), r))
  end next
end LinearCongruentialRandom
