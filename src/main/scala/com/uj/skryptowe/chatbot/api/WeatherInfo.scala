package com.uj.skryptowe.chatbot.api

class WeatherInfo(
                   val description: String,
                   val temperature: Double,
                   val pressure: Int,
                   val humidity: Int
                 ) {
  override def toString = s"general description: $description\ntemperature: $temperature °C\npressure: $pressure hPa\n humidity: $humidity"
}