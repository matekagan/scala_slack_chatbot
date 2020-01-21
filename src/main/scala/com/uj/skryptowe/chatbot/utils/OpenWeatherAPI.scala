package com.uj.skryptowe.chatbot.utils

import com.uj.skryptowe.chatbot.api.WeatherInfo
import play.api.libs.json.Json

import scala.io.Source

object OpenWeatherAPI {
  val baseURL = "https://api.openweathermap.org/data/2.5/weather"

  def getWeatherInfoForCity(cityName: String): WeatherInfo = {
    val response = OpenWeatherAPI getCurrentWeatherForCity cityName
    val parsedResponse = Json.parse(response)
    val parsedMain = parsedResponse \ "main"
    val weather = parsedResponse \ "weather" \ 0
    val desc = (weather \ "description").as[String]
    val icon = (weather \ "icon").as[String]
    val temp = (parsedMain \ "temp").as[Double]
    val pressure = (parsedMain \ "pressure").as[Int]
    val humidity = (parsedMain \ "humidity").as[Int]
    new WeatherInfo(desc, temp, pressure, humidity, s"shttp://openweathermap.org/img/wn/$icon@2x.png")
  }

  def getCurrentWeatherForCity(cityName: String): String = {
    val apiKey = Properties get "open-weather-api-token"
    val otherParams = s"&units=metric&appid=$apiKey"
    val url = s"$baseURL?q=$cityName$otherParams"
    Source.fromURL(url).mkString
  }
}
