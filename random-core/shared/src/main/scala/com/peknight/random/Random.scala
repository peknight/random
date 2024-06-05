package com.peknight.random

import scodec.bits.ByteVector

import scala.collection.{BuildFrom, Factory}

trait Random[F[_]]:
  protected[random] def next(bits: Int): F[(Random[F], Int)]

  def nextInt: F[(Random[F], Int)] = next(32)

  def nextIntBounded(bound: Int): F[(Random[F], Int)]

  def between(minInclusive: Int, maxExclusive: Int): F[(Random[F], Int)]

  def nextBytes(n: Int): F[(Random[F], ByteVector)]

  def nextLong: F[(Random[F], Long)]

  def nextLongBounded(bound: Long): F[(Random[F], Long)]

  def between(minInclusive: Long, maxExclusive: Long): F[(Random[F], Long)]

  def nextBoolean: F[(Random[F], Boolean)]

  def nextFloat: F[(Random[F], Float)]

  def between(minInclusive: Float, maxExclusive: Float): F[(Random[F], Float)]

  def nextDouble: F[(Random[F], Double)]

  def between(minInclusive: Double, maxExclusive: Double): F[(Random[F], Double)]

  def nextString(length: Int): F[(Random[F], String)]

  def nextPrintableChar: F[(Random[F], Char)]

  def nextAlphaNumeric: F[(Random[F], Char)]

  def shuffle[T, C](xs: IterableOnce[T])(using bf: BuildFrom[xs.type, T, C]): F[(Random[F], C)]
end Random

object Random:
  private[random] val doubleUnit: Double = 1.0 / (1L << 53)
  private[random] val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
end Random
