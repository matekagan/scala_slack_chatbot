package com.uj.skryptowe.chatbot.app

import akka.actor.ActorSystem
import com.uj.skryptowe.chatbot.bot.WeatherBot
import com.uj.skryptowe.chatbot.utils.Properties
import slack.rtm.SlackRtmClient

import scala.concurrent.ExecutionContextExecutor

object WeatherApp extends App {
  implicit val system: ActorSystem = ActorSystem("slack")
  implicit val ecd: ExecutionContextExecutor = system.dispatcher

  val token = Properties get "slack-api-token"
  val client = SlackRtmClient(token)

  val bot = new WeatherBot(client)

  client.onMessage {
    bot.handleChatMessage
  }
}
