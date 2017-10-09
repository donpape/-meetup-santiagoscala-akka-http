name := "meetup-santiagoscala-akka-http"

version := "1.0"

scalaVersion := "2.12.3"


resolvers += "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

val akkaActorV = "2.5.6"
val akkaHttpV = "10.0.10"

// akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaActorV
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaActorV
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaActorV
// akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpV
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV

// jdbc
libraryDependencies += "com.zaxxer" % "HikariCP" % "2.7.1"
libraryDependencies += "org.flywaydb" % "flyway-core" % "4.2.0"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.1"

// config
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

// json
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV
libraryDependencies += "io.spray" %% "spray-json" % "1.3.3"

// log
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.7.25"

// test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test

