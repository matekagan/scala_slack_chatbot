package com.uj.skryptowe.chatbot

import akka.actor.ActorSystem
import slack.SlackUtil
import slack.rtm.SlackRtmClient

import scala.concurrent.ExecutionContextExecutor

object TestApp extends App {
  val token = "xoxb-893550387842-893559818882-CtI8IISqQe07x7hhzUgj4Y16"
  implicit val system: ActorSystem = ActorSystem("slack")
  implicit val ecd: ExecutionContextExecutor = system.dispatcher

  val client = SlackRtmClient(token)
  val selfId = client.state.self.id

  client.onMessage { message =>
    val mentionedIds = SlackUtil.extractMentionedIds(message.text)
    if(mentionedIds.contains(selfId)) {
      client.sendMessage(message.channel, s"<@${message.user}>: Hey! How Can I Help You")
    } else {
      client.sendMessage(message.channel, s"Hey! this is interactive slack bot. You can contact me at <@$selfId>")
    }
  }
}
