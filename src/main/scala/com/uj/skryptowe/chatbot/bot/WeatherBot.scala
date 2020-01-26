package com.uj.skryptowe.chatbot.bot

import com.uj.skryptowe.chatbot.api.WeatherInfo
import com.uj.skryptowe.chatbot.app.SlackMessage
import com.uj.skryptowe.chatbot.utils.{MessageUtils, OpenWeatherAPI}
import slack.SlackUtil
import slack.models.Message
import slack.rtm.SlackRtmClient

import scala.util.Try

class WeatherBot(val client: SlackRtmClient) {

  val unrecognizedMessage = "I am not sure what do you mean."
  val userIDConst = "userID"
  val weatherConst = "weather"
  val cityNameConst = "cityName"
  val lineBreakConst = "br"

  implicit class SlackMessageUtil(format: String) {
    def braces(): String = s"{$format}"

    def entryReplace(entry: (String, String)): String = format.replace(entry._1.braces(), entry._2)

    def messageFormat(map: Map[String, String]): String = map.foldLeft(format)(
      (value: String, entry: (String, String)) => value entryReplace entry
    )

    def clean: String = format.replaceAll("<@.*>", "")
      .replaceAll("[!-@]", "")
      .trim

    def getCityName: String = format.split(" +").last
  }

  def handleChatMessage(message: Message): Any = {
    val selfId = client.state.self.id
    val mentionedIds = SlackUtil extractMentionedIds message.text
//    val returnMessage: String = if (mentionedIds contains selfId) handleMessage(
//      new SlackMessage(message.text.clean, message.user)
//    ) else s"Hey! This is simple weather bot. Contact me at <@$selfId> to receive current weather for any city."
    val returnMessage = handleMessage(new SlackMessage(message.text.clean, message.user))
    client.sendMessage(message.channel, returnMessage)
  }

  def handleMessage(slackMessage: SlackMessage): String = {

    val mostSimilarAnswer = MessageUtils.getMostSimilarAnswer(slackMessage.text)
    val returnMessage = if (mostSimilarAnswer._1 > 0.6)
      prepareResponse(slackMessage, mostSimilarAnswer._2)
    else unrecognizedMessage
    returnMessage
  }

  def prepareResponse(inMessage: SlackMessage, returnTemplate: String): String = {
    val paramsMap: Map[String, String] = Map(
      userIDConst -> inMessage.userID,
      lineBreakConst -> "\n"
    )
    if (inMessage.text.contains(weatherConst))
      preparePareMessageWithWeatherInfo(inMessage, returnTemplate)
    else returnTemplate messageFormat paramsMap
  }

  def preparePareMessageWithWeatherInfo(inMessage: SlackMessage, returnTemplate: String): String = {
    val cityName = inMessage.text.getCityName
    val weatherInfo = queryWeather(cityName)
    weatherInfo.map(info => buildReturnMessage(inMessage, cityName, info, returnTemplate))
      .getOrElse(s"I am not sure if a city $cityName actually exist")
  }

  def buildReturnMessage(inMessage: SlackMessage, cityName: String, weatherInfo: WeatherInfo, template: String): String = {
    val paramsMap: Map[String, String] = Map(
      userIDConst -> inMessage.userID,
      cityNameConst -> cityName,
      weatherConst -> weatherInfo.toString,
      lineBreakConst -> "\n"
    )
    template.messageFormat(paramsMap)
  }

  def queryWeather(cityName: String): Try[WeatherInfo] = {
    Try(OpenWeatherAPI getWeatherInfoForCity cityName)
  }
}
