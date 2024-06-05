package com.peknight.random.id

import cats.Id
import com.peknight.random.Random as Rand
import com.peknight.random.algorithm.LinearCongruential
import com.peknight.random.monad.Random as MonadRandom
import scodec.bits.ByteVector

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.collection.{BuildFrom, Factory}

trait Random extends MonadRandom[Id]:

  override def nextIntBounded(bound: Int): Id[(Rand[Id], Int)] =
    require(bound > 0, s"bound must be positive, but was $bound")
    val (random, r) = next(31)
    val m = bound - 1
    if (bound & m) == 0 then (random, ((bound * r.toLong) >> 31).toInt)
    else
      @tailrec def go(random: Rand[Id], bits: Int, r: Int): Id[(Rand[Id], Int)] =
        if bits - r + m < 0 then
          val (nextRandom, nextBits) = random.next(31)
          go(nextRandom, nextBits, nextBits % bound)
        else (random, r)
      val (nextRandom, rnd) = go(random, r, r % bound)
      (nextRandom, rnd)
  end nextIntBounded

  override def between(minInclusive: Int, maxExclusive: Int): Id[(Rand[Id], Int)] =
    require(minInclusive < maxExclusive, s"Invalid bounds, $minInclusive, $maxExclusive")
    val difference = maxExclusive - minInclusive
    if difference >= 0 then
      val (random, r) = nextIntBounded(difference)
      (random, r + minInclusive)
    else
      @tailrec def go(random: Rand[Id]): Id[(Rand[Id], Int)] =
        val (nextRandom, r) = random.nextInt
        if r >= minInclusive && r < maxExclusive then (nextRandom, r)
        else go(nextRandom)
      go(this)
  end between

  override def nextBytes(n: Int): Id[(Rand[Id], ByteVector)] =
    require(n >= 0, s"size must be non-negative, but was $n")
    @tailrec def go(random: Rand[Id], bytes: ByteVector): Id[(Rand[Id], ByteVector)] =
      val length = n - bytes.length.toInt
      if length <= 0 then (random, bytes)
      else
        val (nextRandom, r) = random.nextInt
        go(nextRandom, appendIntToBytes(bytes, r, length))
    go(this, ByteVector.empty)
  end nextBytes

  override def nextLong: Id[(Rand[Id], Long)] =
    val (highRandom, high) = nextInt
    val (lowRandom, low) = highRandom.nextInt
    (lowRandom, (high.toLong << 32) + low)
  end nextLong

  override def nextLongBounded(bound: Long): Id[(Rand[Id], Long)] =
    require(bound > 0, s"bound must be positive, but was $bound")
    @tailrec def go(random: Rand[Id], offset: Long, bound: Long): Id[(Rand[Id], Long, Long)] =
      if bound < Int.MaxValue then (random, offset, bound)
      else
        val (nextRandom, bits) = random.nextIntBounded(2)
        val halfBound = bound >>> 1
        val nextBound = if (bits & 2) == 0 then halfBound else bound - halfBound
        val nextOffset = if (bits & 1) == 0 then offset + bound - nextBound else offset
        go(nextRandom, nextOffset, nextBound)
    end go

    val (nextRandom, offset, nextBound) = go(this, 0L, bound)
    val (lastRandom, r) = nextRandom.nextIntBounded(nextBound.toInt)
    (lastRandom, r + offset)
  end nextLongBounded

  override def between(minInclusive: Long, maxExclusive: Long): Id[(Rand[Id], Long)] =
    require(minInclusive < maxExclusive, s"Invalid bounds, $minInclusive, $maxExclusive")
    val difference = maxExclusive - minInclusive
    if difference >= 0 then
      val (random, r) = nextLongBounded(difference)
      (random, r + minInclusive)
    else
      @tailrec def go(random: Rand[Id]): Id[(Rand[Id], Long)] =
        val (nextRandom, r) = random.nextLong
        if r >= minInclusive && r < maxExclusive then (nextRandom, r)
        else go(nextRandom)
      go(this)
  end between

  override def nextBoolean: Id[(Rand[Id], Boolean)] =
    val (random, r) = next(1)
    (random, r != 0)
  end nextBoolean

  override def nextFloat: Id[(Rand[Id], Float)] =
    val (random, r) = next(24)
    (random, r / (1 << 24).toFloat)
  end nextFloat

  override protected[random] def between[N: Numeric](minInclusive: N, maxExclusive: N,
                                                    next: Rand[Id] => Id[(Rand[Id], N)],
                                                    nextAfter: => N): Id[(Rand[Id], N)] =
    require(Numeric[N].compare(minInclusive, maxExclusive) < 0, s"Invalid bounds, $minInclusive, $maxExclusive")
    val (random, r) = next(this)
    val nextR = Numeric[N].plus(Numeric[N].times(r, Numeric[N].minus(maxExclusive, minInclusive)), minInclusive)
    if Numeric[N].compare(nextR, maxExclusive) < 0 then (random, nextR)
    else (random, nextAfter)
  end between

  override def nextDouble: Id[(Rand[Id], Double)] =
    val (highRandom, high) = next(26)
    val (lowRandom, low) = highRandom.next(27)
    (lowRandom, ((high.toLong << 27) + low) * Rand.doubleUnit)
  end nextDouble

  override def nextString(length: Int): Id[(Rand[Id], String)] =
    if length <= 0 then (this, "")
    else
      def safeChar(random: Rand[Id]): Id[(Rand[Id], Char)] =
        val surrogateStart: Int = 0xD800
        val (nextRandom, r) = random.nextIntBounded(surrogateStart - 1)
        (nextRandom, (r + 1).toChar)
      end safeChar
      val arr = new Array[Char](length)
      @tailrec def go(random: Rand[Id], index: Int): Id[(Rand[Id], String)] =
        if index >= length then (random, new String(arr))
        else
          val (nextRandom, r) = safeChar(random)
          arr(index) = r
          go(nextRandom, index + 1)
      go(this, 0)
  end nextString

  override def nextPrintableChar: Id[(Rand[Id], Char)] =
    val low = 33
    val high = 127
    val (random, r) = nextIntBounded(high - low)
    (random, (r + low).toChar)
  end nextPrintableChar

  override def nextAlphaNumeric: Id[(Rand[Id], Char)] =
    val (random, r) = nextIntBounded(Rand.chars.length)
    (random, Rand.chars.charAt(r))
  end nextAlphaNumeric

  override def shuffle[T, C](xs: IterableOnce[T])(using bf: BuildFrom[xs.type, T, C]): Id[(Rand[Id], C)] =
    val buf = new ArrayBuffer[T] ++= xs
    def swap(i1: Int, i2: Int): Unit = {
      val tmp = buf(i1)
      buf(i1) = buf(i2)
      buf(i2) = tmp
    }

    val nextRandom = (buf.length to 2 by -1).foldLeft[Rand[Id]](this) { (random, n) =>
      val (nextRandom, r) = random.nextIntBounded(n)
      swap(n - 1, r)
      nextRandom
    }
    (nextRandom, (bf.newBuilder(xs) ++= buf).result())
  end shuffle

  def alphanumeric: LazyList[Char] =
    LazyList.unfold[Char, Rand[Id]](this)(random => Some(random.nextAlphaNumeric.swap))

end Random

object Random:
  def apply(seed: Long): Rand[Id] = LinearCongruentialRandom(LinearCongruential.initialScramble(seed))
end Random
