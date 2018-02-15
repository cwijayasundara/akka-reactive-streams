package com.cham.akka.streams

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.{Done, NotUsed}

import scala.concurrent.Future

object SimpleStreamExample extends App{

    implicit val system       = ActorSystem("System")
    implicit val materializer = ActorMaterializer()

    def isPrime(n: Int) = Range(2, n-1).filter(n % _ == 0).length == 0

    val numbers = 1 to 100
    // create the source - Producer
    val numberSource: Source[Int, NotUsed] = Source.fromIterator(() => numbers.iterator)
    // create a Flow (Processor)
    val isPrimeNumberFlow:Flow[Int, Int, NotUsed] = Flow[Int].filter((num) => isPrime(num))
    // create a consumer
    val primeNumberSource: Source[Int, NotUsed] = numberSource.via(isPrimeNumberFlow)

    val consoleSink: Sink[Int, Future[Done]] = Sink.foreach[Int](println)

    primeNumberSource.runWith(consoleSink)
}
