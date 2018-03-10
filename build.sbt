name := "testtaxii"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.github.workingDog" %% "taxii2lib" % "0.3",
  "com.github.workingDog" %% "scalastix" % "0.7"
)

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

