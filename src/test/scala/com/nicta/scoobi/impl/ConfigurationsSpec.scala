package com.nicta.scoobi.impl

import org.specs2.mutable.Specification
import com.nicta.scoobi.impl.Configurations._
import com.nicta.scoobi.testing.mutable.Unit
import org.apache.hadoop.conf.Configuration
import org.specs2.matcher.Matcher

class ConfigurationsSpec extends Specification with Unit {

  "A configuration can be updated with the keys existing in another configuration" >> {
    val updated = configuration("a" -> "1", "b" -> "2").updateWith(configuration("c" -> "3", "d" -> "4")) {
      case (k, v) => ("got: "+k, "and: "+v)
    }
    updated.toMap must_== configuration("a" -> "1", "b" -> "2", "got: c" -> "and: 3", "got: d" -> "and: 4").toMap
  }

  "It is possible to increment the Int value of a configuration key" >> {
    "for a simple key" >> {
      val c1 = configuration("a" -> "1")
      c1.increment("b")
      c1 must beTheSameAs(configuration("a" -> "1", "b" -> "1"))

      val c2 = configuration("a" -> "1")
      c2.increment("a")
      c2 must beTheSameAs(configuration("a" -> "2"))
    }
  }

  "it is possible to temporarily use a configuration without its classLoader" >> {
    val conf = configuration("a" -> "1")
    val cl = getClass.getClassLoader
    conf.setClassLoader(cl)
    conf.withoutClassLoader { c: Configuration =>
      c.getClassLoader must beNull
    }
    conf.getClassLoader must beTheSameAs(cl)
  }

  def beTheSameAs(other: Configuration): Matcher[Configuration] = (c: Configuration) =>
    (c.show == other.show, c.show+"\n\nis not the same as\n\n"+other.show)
}
