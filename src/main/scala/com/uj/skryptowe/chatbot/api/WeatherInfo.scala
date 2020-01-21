package com.uj.skryptowe.chatbot.api

class WeatherInfo(
                   val description: String,
                   val temperature: Double,
                   val pressure: Int,
                   val humidity: Int,
                   val iconUrl: String
                 ) {
  override def toString = s"$iconUrl\n$description\ntemperature: $temperature °C\npressure: $pressure hPa\n humidity: $humidity"
}