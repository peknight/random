package com.peknight.random.algorithm

object LinearCongruential:
  val multiplier: Long = 0x5DEECE66DL
  val addend: Long = 0xBL
  val mask: Long = (1L << 48) - 1

  val seedUniquifier: Long = 8682522807148012L

  def initialScramble(seed: Long): Long = (seed ^ multiplier) & mask

  def next(seed: Long, bits: Int): (Long, Int) =
    val nextSeed = (seed * multiplier + addend) & mask
    val r = (nextSeed >>> (48 - bits)).toInt
    (nextSeed, r)
  end next

  def nanoTime(seedUniquifier: Long, nanoTime: Long): (Long, Long) =
    val next = seedUniquifier * 1181783497276652981L
    (next, next ^ nanoTime)
  end nanoTime
end LinearCongruential
