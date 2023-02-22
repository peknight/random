package com.peknight.random.id

import cats.Id
import com.peknight.random.Random as Rand
import com.peknight.random.algorithm.LinearCongruential

private[random] case class LinearCongruentialRandom(seed: Long) extends Random:
  def next(bits: Int): Id[(Rand[Id], Int)] =
    val (nextSeed, r) = LinearCongruential.next(seed, bits)
    (LinearCongruentialRandom(nextSeed), r)
  end next
end LinearCongruentialRandom
