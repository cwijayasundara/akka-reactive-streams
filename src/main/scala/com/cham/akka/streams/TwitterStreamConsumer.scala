package com.cham.akka.streams

import java.util.concurrent.{ExecutorService, Executors}

import akka.{Done, NotUsed}
import akka.actor._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent._

object TwitterStreamConsumer extends App {

  // ActorSystem & thread pools
  val execService: ExecutorService = Executors.newCachedThreadPool()
  implicit val system: ActorSystem = ActorSystem("twitterActorSystem")
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(execService)
  implicit val materializer = ActorMaterializer()

  val theDressHashtag = Hashtag("#Trump")

  val startTime = System.nanoTime()

  val twitterStream = new TwitterStreamClient(system)
  twitterStream.init

  val tweetSourceActor: ActorRef = system.actorOf(Props[SourceActor], name = "twitterSourceActor")

  system.eventStream.subscribe(tweetSourceActor, classOf[Tweet])

  // create the source
  val tweets: Source[Tweet,ActorRef] = Source.actorPublisher(Props[SourceActor])

  // create a Flow (Processor)
  val filter: Flow[Tweet, Tweet, NotUsed] = Flow[Tweet].filter(t => t.hashtags.contains(theDressHashtag))

  // create a consumer
  val tweetSource: Source[Tweet, ActorRef] = tweets.via(filter)

  val consoleSink: Sink[Tweet, Future[Done]] = Sink.foreach[Tweet](println)

  tweetSource.runWith(consoleSink)

}
