package com.cham.akka.streams

import akka.stream.actor.ActorPublisher


class SourceActor extends ActorPublisher[Tweet] {

  override def receive: Receive = {
    case s: Tweet => {
      if (isActive && totalDemand > 0) onNext(s)
    }
    case _ =>
  }

  override def postStop(): Unit = {
    context.system.eventStream.unsubscribe(self)
  }

}
