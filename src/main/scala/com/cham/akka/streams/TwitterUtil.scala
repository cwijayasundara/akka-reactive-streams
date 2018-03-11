package com.cham.akka.streams

import akka.actor.{ActorSystem}
import twitter4j.{StallWarning, StatusListener, TwitterStreamFactory, _}
import twitter4j.auth.AccessToken
import twitter4j.conf.ConfigurationBuilder

import scala.collection._

final case class Author(handle: String)

final case class Hashtag(name: String)

case class Tweet(author: Author, timestamp: Long, body: String) {
  def hashtags: Set[Hashtag] =
    body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t)}.toSet
}

final object EmptyTweet extends Tweet(Author(""), 0L, "")

object CretentialsUtils {
  val appKey: String = ""
  val appSecret: String = ""
  val accessToken: String = ""
  val accessTokenSecret: String = ""

}

object TwitterClient {

  def apply(): Twitter = {
    val factory = new TwitterFactory(new ConfigurationBuilder().build())
    val twitter  = factory.getInstance()
    twitter.setOAuthConsumer(CretentialsUtils.appKey, CretentialsUtils.appSecret)
    twitter.setOAuthAccessToken(new AccessToken(CretentialsUtils.accessToken, CretentialsUtils.accessTokenSecret))
    twitter
  }
}

class TwitterStreamClient(val actorSystem: ActorSystem) {
  val factory = new TwitterStreamFactory(new ConfigurationBuilder().build())
  val twitterStream = factory.getInstance()

  def init = {
    twitterStream.setOAuthConsumer(CretentialsUtils.appKey, CretentialsUtils.appSecret)
    twitterStream.setOAuthAccessToken(new AccessToken(CretentialsUtils.accessToken, CretentialsUtils.accessTokenSecret))
    twitterStream.addListener(simpleStatusListener)
    // sample will return a stream of tweets
    twitterStream.sample()
  }


  def simpleStatusListener = new StatusListener() {
    def onStatus(s: Status) {
      actorSystem.eventStream.publish(Tweet(Author(s.getUser.getScreenName), s.getCreatedAt.getTime, s.getText))
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) {
      ex.printStackTrace
    }

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) {}
  }

  def stop = {
    twitterStream.cleanUp
    twitterStream.shutdown
  }
}