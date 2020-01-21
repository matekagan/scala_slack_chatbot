package com.uj.skryptowe.chatbot.utils

import scala.io.Source

object Properties {
  lazy private val properties: Map[String, String] = Source.fromResource("env.properties").getLines()
    .filter(_.contains("="))
    .map(parseLine).toMap

  private def parseLine(line: String): (String, String) = {
    val split = line.split("=")
    (split(0), split(1))
  }

  def get(key: String): String = properties.get(key).orNull
}
