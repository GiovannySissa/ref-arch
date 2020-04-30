import org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings

ThisBuild / turbo := true

lazy val commonSettings = Seq(
  scalaVersion := "2.12.10",
  fork in Test := true,
  organization := "co.bbt",
  name         := "ref-arch",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    "confluent" at "https://packages.confluent.io/maven/"
  ),
  coverageMinimum                := 90,
  coverageFailOnMinimum          := true,
  coverageHighlighting           := true,
  scalafmtOnCompile in ThisBuild := true,
  wartremoverErrors ++= OwnWarts.all,
  libraryDependencies ++= Dependencies.common,
  testFrameworks += new TestFramework("minitest.runner.Framework"),
  dockerRepository := Some("registry.gitlab.com/gsissa/image-registry-repo"),
  daemonUser in Docker := "daemon",
  dockerBaseImage := "adoptopenjdk/openjdk11:alpine-jre",
  dockerExposedPorts := Seq(9999),
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
)
lazy val ItConfig = config("it") extend (IntegrationTest, Test)

lazy val testSettings =
  inConfig(ItConfig)(Defaults.testSettings ++ scalafmtConfigSettings)

lazy val core = project
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.test,
    name += "-core",
    testSettings,
    coverageExcludedFiles := "<empty>;.*LoggerHandler.*"
  )

lazy val grpc = project
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    name += "-grpc",
    libraryDependencies ++= Dependencies.grpc ++ Dependencies.test,
    coverageExcludedFiles := "<empty>;.*Main.*",
    mainClass in Compile  := Some("co.bbt.ref.grpc.Main")
  )
  .dependsOn(core % "compile->compile;test->test", core % "compile->compile;test->it", protocol)
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)

lazy val protocol = project
  .settings(
    name += "-protocol",
    libraryDependencies ++= Dependencies.protocol
  )
  .enablePlugins(Fs2Grpc)
  .disablePlugins(ScoverageSbtPlugin)

addCommandAlias("validate", ";clean;update;compile;scalafmtCheck;scalafmtSbtCheck;coverage;test;it:test;coverageReport")
