organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test

lazy val `lagom-assignment` = (project in file("."))
  .aggregate(`lagom-assignment-producer-api`, `lagom-assignment-producer-impl`, `lagom-assignment-consumer-api`, `lagom-assignment-consumer-impl`)

lazy val `lagom-assignment-producer-api` = (project in file("lagom-assignment-producer-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-assignment-producer-impl` = (project in file("lagom-assignment-producer-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-assignment-producer-api`)

lazy val `lagom-assignment-consumer-api` = (project in file("lagom-assignment-consumer-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `lagom-assignment-consumer-impl` = (project in file("lagom-assignment-consumer-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslKafkaClient,
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-assignment-consumer-api`, `lagom-assignment-producer-api`, `lagom-assignment-producer-impl`)

lagomUnmanagedServices in ThisBuild := Map("external-user-service" -> "https://jsonplaceholder.typicode.com:443",
"external-mock-service" -> "http://www.mocky.io:80")

//lagomKafkaEnabled in ThisBuild := false
//lagomKafkaAddress in ThisBuild := "localhost:9092"