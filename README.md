# Pek Random

参考[Functional Programming in Scala](https://www.manning.com/books/functional-programming-in-scala)设计的纯函数式随机数API，
并提供了线性同余随机数的纯函数式实现。

## random-core

提供纯函数式随机数API与线性同余随机数实现

## random-effect

包装cats-effect的随机数类，主要为了使用cats-effect的跨平台`SecureRandom`类（`java.security.SecureRandom`并不支持scala.js平台与
scala.native平台）。