package com.peknight.random.monad

import cats.Monad
import cats.syntax.applicative.*
import cats.syntax.either.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import com.peknight.random.Random as Rand
import com.peknight.random.algorithm.LinearCongruential
import scodec.bits.ByteVector

import scala.collection.mutable.ArrayBuffer
import scala.collection.{BuildFrom, Factory}

trait Random[F[_] : Monad] extends Rand[F]:

  def nextIntBounded(bound: Int): F[(Rand[F], Int)] =
    require(bound > 0, s"bound must be positive, but was $bound")
    _nextIntBounded(bound)
  end nextIntBounded

  protected[random] def _nextIntBounded(bound: Int): F[(Rand[F], Int)] =
    for
      randomTuple <- next(31)
      (random, r) = randomTuple
      m = bound - 1
      out <- if (bound & m) == 0 then (random, ((bound * r.toLong) >> 31).toInt).pure[F] else
        Monad[F].tailRecM((random, r, r % bound)) { case (random, bits, r) =>
          if bits - r + m < 0 then
            random.next(31).map { case (random, r) => (random, r, r % bound).asLeft }
          else (random, r).asRight.pure
        }
    yield out
  end _nextIntBounded

  def between(minInclusive: Int, maxExclusive: Int): F[(Rand[F], Int)] =
    require(minInclusive < maxExclusive, s"Invalid bounds, $minInclusive, $maxExclusive")
    _between(minInclusive, maxExclusive)
  end between

  protected[random] def _between(minInclusive: Int, maxExclusive: Int): F[(Rand[F], Int)] =
    val difference = maxExclusive - minInclusive
    if difference >= 0 then
      for
        randomTuple <- _nextIntBounded(difference)
        (random, r) = randomTuple
      yield (random, r + minInclusive)
    else
      Monad[F].tailRecM[Rand[F], (Rand[F], Int)](this) { random =>
        for
          randomTuple <- random.nextInt
          (random, r) = randomTuple
        yield
          if r >= minInclusive && r < maxExclusive then (random, r).asRight
          else random.asLeft
      }
  end _between

  def nextBytes(n: Int): F[(Rand[F], ByteVector)] =
    require(n >= 0, s"size must be non-negative, but was $n")
    _nextBytes(n)
  end nextBytes

  protected[random] def _nextBytes(n: Int): F[(Rand[F], ByteVector)] =
    Monad[F].tailRecM[(Rand[F], ByteVector), (Rand[F], ByteVector)]((this, ByteVector.empty)) { case (random, bytes) =>
      val length = n - bytes.length.toInt
      if length <= 0 then (random, bytes).asRight.pure else
        random.nextInt.map { case (nextRandom, r) => (nextRandom, appendIntToBytes(bytes, r, length)).asLeft }
    }
  end _nextBytes

  protected[random] def appendIntToBytes(bytes: ByteVector, r: Int, length: Int): ByteVector =
    val size = length min 4
    (0 until size).foldLeft(bytes) {
      case (bs, i) => bs :+ (r >> (8 * i)).toByte
    }
  end appendIntToBytes

  def nextLong: F[(Rand[F], Long)] =
    for
      highTuple <- nextInt
      (highRandom, high) = highTuple
      lowTuple <- highRandom.nextInt
      (lowRandom, low) = lowTuple
    yield (lowRandom, (high.toLong << 32) + low)
  end nextLong

  def nextLongBounded(bound: Long): F[(Rand[F], Long)] =
    require(bound > 0, s"bound must be positive, but was $bound")
    _nextLongBounded(bound)
  end nextLongBounded

  protected[random] def _nextLongBounded(bound: Long): F[(Rand[F], Long)] =
    for
      randomTuple <- Monad[F].tailRecM[(Rand[F], Long, Long), (Rand[F], Long, Long)]((this, 0L, bound)) {
        case (random, offset, bound) =>
          if bound < Int.MaxValue then (random, offset, bound).asRight.pure else
            for
              randomTuple <- random.nextIntBounded(2)
              (nextRandom, bits) = randomTuple
              halfBound = bound >>> 1
              nextBound = if (bits & 2) == 0 then halfBound else bound - halfBound
              nextOffset = if (bits & 1) == 0 then offset + bound - nextBound else offset
            yield (nextRandom, nextOffset, nextBound).asLeft
      }
      (random, offset, bound) = randomTuple
      randomTuple <- random.nextIntBounded(bound.toInt)
      (random, r) = randomTuple
    yield (random, r + offset)
  end _nextLongBounded

  def between(minInclusive: Long, maxExclusive: Long): F[(Rand[F], Long)] =
    require(minInclusive < maxExclusive, s"Invalid bounds, $minInclusive, $maxExclusive")
    _between(minInclusive, maxExclusive)
  end between

  protected[random] def _between(minInclusive: Long, maxExclusive: Long): F[(Rand[F], Long)] =
    val difference = maxExclusive - minInclusive
    if difference >= 0 then
      for
        randomTuple <- _nextLongBounded(difference)
        (random, r) = randomTuple
      yield (random, r + minInclusive)
    else
      Monad[F].tailRecM[Rand[F], (Rand[F], Long)](this) { random =>
        for
          randomTuple <- random.nextLong
          (random, r) = randomTuple
        yield
          if r >= minInclusive && r < maxExclusive then (random, r).asRight
          else random.asLeft
      }
  end _between

  def nextBoolean: F[(Rand[F], Boolean)] =
    for
      randomTuple <- next(1)
      (random, r) = randomTuple
    yield (random, r != 0)
  end nextBoolean

  def nextFloat: F[(Rand[F], Float)] =
    for
      randomTuple <- next(24)
      (random, r) = randomTuple
    yield (random, r / (1 << 24).toFloat)
  end nextFloat

  protected[random] def between[N: Numeric](minInclusive: N, maxExclusive: N,
                                                     next: Rand[F] => F[(Rand[F], N)],
                                                     nextAfter: => N): F[(Rand[F], N)] =
    require(Numeric[N].compare(minInclusive, maxExclusive) < 0, s"Invalid bounds, $minInclusive, $maxExclusive")
    _between(minInclusive, maxExclusive, next, nextAfter)
  end between

  protected[random] def _between[N: Numeric](minInclusive: N, maxExclusive: N, next: Rand[F] => F[(Rand[F], N)],
                                             nextAfter: => N): F[(Rand[F], N)] =
    for
      randomTuple <- next(this)
      (random, r) = randomTuple
      next = Numeric[N].plus(Numeric[N].times(r, Numeric[N].minus(maxExclusive, minInclusive)), minInclusive)
    yield
      if Numeric[N].compare(next, maxExclusive) < 0 then (random, next)
      else (random, nextAfter)
  end _between

  def between(minInclusive: Float, maxExclusive: Float): F[(Rand[F], Float)] =
    between(minInclusive, maxExclusive, _.nextFloat, Math.nextAfter(maxExclusive, Float.NegativeInfinity))
  end between

  def nextDouble: F[(Rand[F], Double)] =
    for
      highTuple <- next(26)
      (highRandom, high) = highTuple
      lowTuple <- highRandom.next(27)
      (lowRandom, low) = lowTuple
    yield (lowRandom, ((high.toLong << 27) + low) * Rand.doubleUnit)
  end nextDouble

  def between(minInclusive: Double, maxExclusive: Double): F[(Rand[F], Double)] =
    between(minInclusive, maxExclusive, _.nextDouble, Math.nextAfter(maxExclusive, Double.NegativeInfinity))
  end between

  def nextString(length: Int): F[(Rand[F], String)] =
    if length <= 0 then (this, "").pure
    else
      def safeChar(random: Rand[F]): F[(Rand[F], Char)] =
        val surrogateStart: Int = 0xD800
        for
          randomTuple <- random.nextIntBounded(surrogateStart - 1)
          (random, r) = randomTuple
        yield (random, (r + 1).toChar)
      end safeChar

      val arr = new Array[Char](length)
      Monad[F].tailRecM[(Rand[F], Int), (Rand[F], String)]((this, 0)) { case (random, index) =>
        if index >= length then (random, new String(arr)).asRight.pure else
          for
            randomTuple <- safeChar(random)
            (random, r) = randomTuple
            _ = arr(index) = r
          yield (random, index + 1).asLeft
      }
  end nextString

  def nextPrintableChar: F[(Rand[F], Char)] =
    val low = 33
    val high = 127
    for
      randomTuple <- _nextIntBounded(high - low)
      (random, r) = randomTuple
    yield (random, (r + low).toChar)
  end nextPrintableChar

  def nextAlphaNumeric: F[(Rand[F], Char)] =
    for
      randomTuple <- _nextIntBounded(Rand.chars.length)
      (random, r) = randomTuple
    yield (random, Rand.chars.charAt(r))
  end nextAlphaNumeric

  def shuffle[T, C](xs: IterableOnce[T])(using bf: BuildFrom[xs.type, T, C]): F[(Rand[F], C)] =
    val buf = new ArrayBuffer[T] ++= xs

    def swap(i1: Int, i2: Int): Unit =
      val tmp = buf(i1)
      buf(i1) = buf(i2)
      buf(i2) = tmp
    end swap

    Monad[F].tailRecM[(Rand[F], Range), (Rand[F], C)]((this, buf.length to 2 by -1)) {
      case (random, ns) if ns.isEmpty => (random, (bf.newBuilder(xs) ++= buf).result()).asRight.pure
      case (random, ns) =>
        val n = ns.head
        for
          randomTuple <- random.nextIntBounded(n)
          (random, r) = randomTuple
          _ = swap(n - 1, r)
        yield (random, ns.tail).asLeft
    }
  end shuffle
end Random
object Random:
  def apply[F[_] : Monad](seed: Long): Rand[F] = LinearCongruentialRandom[F](LinearCongruential.initialScramble(seed))
end Random
