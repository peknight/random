package com.peknight.random.supplier

import com.peknight.random.Random
import com.peknight.random.algorithm.LinearCongruential

case class LinearCongruentialRandomSupplier(seedUniquifier: Long = LinearCongruential.seedUniquifier):
  def nanoTime[F[_]](nanoTime: Long)(random: Long => Random[F]): (LinearCongruentialRandomSupplier, Random[F]) =
    val (next, seed) = LinearCongruential.nanoTime(seedUniquifier, nanoTime)
    (LinearCongruentialRandomSupplier(next), random(seed))
  end nanoTime
end LinearCongruentialRandomSupplier
