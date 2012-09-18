package com.nicta.scoobi.acceptance

import com.nicta.scoobi.Scoobi._
import com.nicta.scoobi.testing.NictaHadoop

class WordCountSpec extends NictaHadoop {

  "Counting words frequencies must return the frequency for each word" >> { c: SC =>

    val frequencies =
      DList(repeat("hello" -> 3, "world" -> 4, "universe" -> 2):_*).
      flatMap(_.split(" ")).map((_, 1)).
      groupByKey.
      filter { case (word, n) => word.length < 6 }.
      combine((i: Int, j: Int) => i + j).materialize

    persist(c)(frequencies).toSeq.sorted must_== Seq(("hello", 3), ("world", 4))

  }

  /** @return a Seq of strings where each key has been duplicated a number of times indicated by the value */
  def repeat(m: (String, Int)*): Seq[String] = m.flatMap { case (k, v) => Seq.fill(v)(k) }

}
