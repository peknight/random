package com.peknight.random.effect

import cats.effect.Sync
import cats.effect.std.SecureRandom as CESecureRandom
import cats.syntax.functor.*
import com.peknight.random.Random as Rand

object SecureRandom:
  def apply[F[_] : Sync]: F[Rand[F]] = CESecureRandom.javaSecuritySecureRandom[F].map(CatsEffectRandom.apply)
end SecureRandom
