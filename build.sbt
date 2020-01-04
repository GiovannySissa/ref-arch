import org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings

ThisBuild / turbo := true

lazy val commonSettings = Seq(
  scalaVersion := "2.12.10",
  fork in Test := true,
  organization := "co.bbt",
  name := "ref-arch",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "confluent" at "https://packages.confluent.io/maven/"
  ),
  libraryDependencies ++= Dependencies.common,
  coverageMinimum := 90,
  coverageFailOnMinimum := true,
  coverageHighlighting := true,
  scalafmtOnCompile in ThisBuild := true,
  wartremoverErrors ++= OwnWarts.all,
  testFrameworks += new TestFramework("minitest.runner.Framework"),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)
lazy val ItConfig = config("it") extend(Test)

lazy val testSettings =
  inConfig(ItConfig)(Defaults.testSettings ++ scalafmtConfigSettings)

lazy val core = project
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    libraryDependencies ++=  Dependencies.test,
    name += "-core",
    testSettings,
    coverageExcludedFiles := "<empty>;.*LoggerHandler.*"
  )


addCommandAlias("validate", ";clean;update;compile;test:scalafmt;it:scalafmt;coverage;test;it:test;coverageReport;coverageAggregate")