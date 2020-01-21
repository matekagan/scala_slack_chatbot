package com.uj.skryptowe.chatbot.bot

import com.uj.skryptowe.chatbot.utils.OpenWeatherAPI
import slack.SlackUtil
import slack.models.Message
import slack.rtm.SlackRtmClient

class WeatherBot(val client: SlackRtmClient) {
  def handleMessage(message: Message): Any = {
    val selfId = client.state.self.id
    val mentionedIds = SlackUtil extractMentionedIds message.text
    val returnMessage: String = if (mentionedIds contains selfId) handleWeatherQuery(message)
    else s"Hey! this is interactive slack bot. You can contact me at <@$selfId>"

    client.sendMessage(message.channel,  returnMessage)
  }

  def handleWeatherQuery(message: Message): String = {
    val text = message.text.replaceAll("<@.*>", "").trim
    val weatherInfo = OpenWeatherAPI getWeatherInfoForCity text
    weatherInfo.toString
  }
}
