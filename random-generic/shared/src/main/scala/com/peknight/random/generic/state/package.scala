package com.peknight.random.generic

import cats.data.StateT
import cats.syntax.functor.*
import cats.{Applicative, Functor}
import com.peknight.generic.migration.Migration
import com.peknight.generic.migration.syntax.id.*
import com.peknight.generic.updater.Updater
import com.peknight.generic.updater.syntax.id.*
import com.peknight.random.Random

import scala.collection.{BuildFrom, Factory}
package object state:

  def run[F[_], A, B](f: Random[F] => F[(Random[F], B)])
                       (using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, B] =
    StateT(a => f(a.migrateTo[Random[F]]).map {
      case (random, b) => (a.update(random), b)
    })

  def nextInt[F[_], A](using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Int] =
    run(_.nextInt)

  def nextIntBounded[F[_], A](bound: Int)(using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]])
  : StateT[F, A, Int] = run(_.nextIntBounded(bound))

  def between[F[_], A](minInclusive: Int, maxExclusive: Int)
                      (using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Int] =
    run(_.between(minInclusive, maxExclusive))

  def nextBytes[F[_], A, C](n: Int)(factory: Factory[Byte, C])
                           (using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, C] =
    run(_.nextBytes(n)(factory))

  def nextLong[F[_], A](using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Long] =
    run(_.nextLong)

  def nextLongBounded[F[_], A](bound: Long)(using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]])
  : StateT[F, A, Long] = run(_.nextLongBounded(bound))

  def between[F[_], A](minInclusive: Long, maxExclusive: Long)
                      (using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Long] =
    run(_.between(minInclusive, maxExclusive))

  def nextBoolean[F[_], A](using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]])
  : StateT[F, A, Boolean] = run(_.nextBoolean)

  def nextFloat[F[_], A](using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Float] =
    run(_.nextFloat)

  def between[F[_], A](minInclusive: Float, maxExclusive: Float)
                      (using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Float] =
    run(_.between(minInclusive, maxExclusive))

  def nextDouble[F[_], A](using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Double] =
    run(_.nextDouble)

  def between[F[_], A](minInclusive: Double, maxExclusive: Double)
                      (using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, Double] =
    run(_.between(minInclusive, maxExclusive))

  def nextString[F[_], A](length: Int)(using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]])
  : StateT[F, A, String] = run(_.nextString(length))

  def nextPrintableChar[F[_], A](using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]])
  : StateT[F, A, Char] = run(_.nextPrintableChar)

  def nextAlphaNumeric[F[_], A](using Applicative[F], Migration[A, Random[F]], Updater[A, Random[F]])
  : StateT[F, A, Char] = run(_.nextAlphaNumeric)

  def shuffle[F[_], A, T, C](xs: IterableOnce[T])(using Applicative[F], BuildFrom[xs.type, T, C],
                                                  Migration[A, Random[F]], Updater[A, Random[F]]): StateT[F, A, C] =
    run(_.shuffle(xs))
end state
