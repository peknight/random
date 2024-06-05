package com.peknight.random.effect

import cats.Functor
import cats.effect.std.Random as CERandom
import cats.syntax.functor.*
import com.peknight.random.Random as Rand
import scodec.bits.ByteVector

import scala.collection.{BuildFrom, Factory}

private[random] case class CatsEffectRandom[F[_] : Functor](random: CERandom[F]) extends Rand[F]:
  protected[random] def next(bits: Int): F[(Rand[F], Int)] = ???

  override def nextInt: F[(Rand[F], Int)] = random.nextInt.map((this, _))

  def nextIntBounded(bound: Int): F[(Rand[F], Int)] = random.nextIntBounded(bound).map((this, _))

  def between(minInclusive: Int, maxExclusive: Int): F[(Rand[F], Int)] =
    random.betweenInt(minInclusive, maxExclusive).map((this, _))

  def nextBytes(n: Int): F[(Rand[F], ByteVector)] =
    random.nextBytes(n).map(bytes => (this, ByteVector(bytes)))

  def nextLong: F[(Rand[F], Long)] = random.nextLong.map((this, _))

  def nextLongBounded(bound: Long): F[(Rand[F], Long)] = random.nextLongBounded(bound).map((this, _))

  def between(minInclusive: Long, maxExclusive: Long): F[(Rand[F], Long)] =
    random.betweenLong(minInclusive, maxExclusive).map((this, _))

  def nextBoolean: F[(Rand[F], Boolean)] = random.nextBoolean.map((this, _))

  def nextFloat: F[(Rand[F], Float)] = random.nextFloat.map((this, _))

  def between(minInclusive: Float, maxExclusive: Float): F[(Rand[F], Float)] =
    random.betweenFloat(minInclusive, maxExclusive).map((this, _))

  def nextDouble: F[(Rand[F], Double)] = random.nextDouble.map((this, _))

  def between(minInclusive: Double, maxExclusive: Double): F[(Rand[F], Double)] =
    random.betweenDouble(minInclusive, maxExclusive).map((this, _))

  def nextString(length: Int): F[(Rand[F], String)] = random.nextString(length).map((this, _))

  def nextPrintableChar: F[(Rand[F], Char)] = random.nextPrintableChar.map((this, _))

  def nextAlphaNumeric: F[(Rand[F], Char)] = random.nextAlphaNumeric.map((this, _))

  def shuffle[T, C](xs: IterableOnce[T])(using bf: BuildFrom[xs.type, T, C]): F[(Rand[F], C)] =
    random.shuffleList[T](xs.iterator.toList).map(l => (this, (bf.newBuilder(xs) ++= l).result()))

end CatsEffectRandom
