package com.peknight.random

import cats.Applicative
import cats.data.StateT
import com.peknight.random.Random

import scala.collection.{BuildFrom, Factory}

package object state:

  def nextInt[F[_] : Applicative]: StateT[F, Random[F], Int] = StateT(_.nextInt)

  def nextIntBounded[F[_] : Applicative](bound: Int): StateT[F, Random[F], Int] = StateT(_.nextIntBounded(bound))

  def between[F[_] : Applicative](minInclusive: Int, maxExclusive: Int): StateT[F, Random[F], Int] =
    StateT(_.between(minInclusive, maxExclusive))

  def nextBytes[F[_] : Applicative, C](n: Int)(factory: Factory[Byte, C]): StateT[F, Random[F], C] =
    StateT(_.nextBytes(n)(factory))

  def nextLong[F[_] : Applicative]: StateT[F, Random[F], Long] = StateT(_.nextLong)

  def nextLongBounded[F[_] : Applicative](bound: Long): StateT[F, Random[F], Long] = StateT(_.nextLongBounded(bound))

  def between[F[_] : Applicative](minInclusive: Long, maxExclusive: Long): StateT[F, Random[F], Long] =
    StateT(_.between(minInclusive, maxExclusive))

  def nextBoolean[F[_] : Applicative]: StateT[F, Random[F], Boolean] = StateT(_.nextBoolean)

  def nextFloat[F[_] : Applicative]: StateT[F, Random[F], Float] = StateT(_.nextFloat)

  def between[F[_] : Applicative](minInclusive: Float, maxExclusive: Float): StateT[F, Random[F], Float] =
    StateT(_.between(minInclusive, maxExclusive))

  def nextDouble[F[_] : Applicative]: StateT[F, Random[F], Double] = StateT(_.nextDouble)

  def between[F[_] : Applicative](minInclusive: Double, maxExclusive: Double): StateT[F, Random[F], Double] =
    StateT(_.between(minInclusive, maxExclusive))

  def nextString[F[_] : Applicative](length: Int): StateT[F, Random[F], String] = StateT(_.nextString(length))

  def nextPrintableChar[F[_] : Applicative]: StateT[F, Random[F], Char] = StateT(_.nextPrintableChar)

  def nextAlphaNumeric[F[_] : Applicative]: StateT[F, Random[F], Char] = StateT(_.nextAlphaNumeric)

  def shuffle[F[_] : Applicative, T, C](xs: IterableOnce[T])(using BuildFrom[xs.type, T, C]): StateT[F, Random[F], C] =
    StateT(_.shuffle(xs))
end state
