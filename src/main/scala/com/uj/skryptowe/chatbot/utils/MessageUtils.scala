package com.uj.skryptowe.chatbot.utils

import scala.io.Source

object MessageUtils {
  lazy val stopWords: Set[String] = Source.fromResource("stopwords.txt").getLines().toSet
  lazy val messageMappings: List[(String, String)] = Source.fromResource("messageMappings.txt").getLines()
    .map(line => line.split("="))
    .map(splitLine => (splitLine(0), splitLine(1))).toList

  implicit class PowerInt(i: Int) {
    def square: Int = i * i
  }

  implicit class ListVector(vector: List[Int]) {
    def vectorLength: Double = Math.sqrt(vector.map(_.square).sum)
  }

  def getMostSimilarAnswer(message: String) = messageMappings
    .map(mapping => (calculateSentenceSimilarity(message, mapping._1), mapping._2))
    .maxBy(value => value._1)

  def calculateSentenceSimilarity(sentence1: String, sentence2: String): Double = {
    val wordSet1: Set[String] = prepareSentenceAsWordSet(sentence1)
    val wordSet2: Set[String] = prepareSentenceAsWordSet(sentence2)
    val wordUnion = wordSet1 union wordSet2
    val v1 = wordUnion.toList.map(word => if (wordSet1 contains word) 1 else 0)
    val v2 = wordUnion.toList.map(word => if (wordSet2 contains word) 1 else 0)
    val dotProduct = v1.zip(v2).map(pair => pair._1 * pair._2).sum.toDouble
    val nominator = v1.vectorLength * v2.vectorLength
    if (nominator == 0.0) 0 else dotProduct / nominator
  }

  def prepareSentenceAsWordSet(sentence: String): Set[String] = sentence.toLowerCase()
    .split(" +").toSet.diff(stopWords)
}