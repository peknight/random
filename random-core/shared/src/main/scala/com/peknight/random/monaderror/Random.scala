package com.peknight.random.monaderror

import cats.syntax.apply.*
import cats.{Applicative, ApplicativeError, MonadError}
import com.peknight.random.Random as Rand
import com.peknight.random.algorithm.LinearCongruential
import com.peknight.random.monad.Random as MonadRandom

import scala.collection.Factory

trait Random[F[_]](using MonadError[F, Throwable]) extends MonadRandom[F]:
  private[this] def require(condition: Boolean, errorMessage: => String): F[Unit] =
    if condition then Applicative[F].unit
    else ApplicativeError[F, Throwable].raiseError(new IllegalArgumentException(errorMessage))

  override def nextIntBounded(bound: Int): F[(Rand[F], Int)] =
    require(bound > 0, s"bound must be positive, but was $bound") *>
      _nextIntBounded(bound)
  end nextIntBounded

  override def between(minInclusive: Int, maxExclusive: Int): F[(Rand[F], Int)] =
    require(minInclusive < maxExclusive, s"Invalid bounds, $minInclusive, $maxExclusive") *>
      _between(minInclusive, maxExclusive)
  end between

  override def nextBytes[C](n: Int)(factory: Factory[Byte, C]): F[(Rand[F], C)] =
    require(n >= 0, s"size must be non-negative, but was $n") *>
      _nextBytes[C](n)(factory)
  end nextBytes

  override def nextLongBounded(bound: Long): F[(Rand[F], Long)] =
    require(bound > 0, s"bound must be positive, but was $bound") *>
      _nextLongBounded(bound)
  end nextLongBounded

  override def between(minInclusive: Long, maxExclusive: Long): F[(Rand[F], Long)] =
    require(minInclusive < maxExclusive, s"Invalid bounds, $minInclusive, $maxExclusive") *>
      _between(minInclusive, maxExclusive)
  end between

  override protected[random] def between[N: Numeric](minInclusive: N, maxExclusive: N,
                                                     next: Rand[F] => F[(Rand[F], N)],
                                                     nextAfter: => N): F[(Rand[F], N)] =
    require(
      Numeric[N].compare(minInclusive, maxExclusive) < 0,
      s"Invalid bounds, $minInclusive, $maxExclusive"
    ) *> _between(minInclusive, maxExclusive, next, nextAfter)
  end between

end Random

object Random:
  def apply[F[_]](seed: Long)(using MonadError[F, Throwable]): Rand[F] =
    LinearCongruentialRandom[F](LinearCongruential.initialScramble(seed))
end Random