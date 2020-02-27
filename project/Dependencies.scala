import sbt._

object Versions {
  lazy val cats        = "2.1.1"
  lazy val catsEffects = "2.1.1"
  lazy val miniTest    = "2.7.0"
  lazy val mouse       = "0.23"
  lazy val scalaCheck  = "1.14.3"
  lazy val doobie      = "0.8.8"
  lazy val squants     = "1.6.0"
  lazy val slf4j       = "1.7.30"
  lazy val meowMtl     = "0.3.0-M1"
  lazy val circeConfig = "0.7.0"
  lazy val circe       = "0.13.0"
}

object Dependencies {
  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"       % Versions.cats withSources () withJavadoc (),
    "org.typelevel" %% "cats-effect"     % Versions.catsEffects withSources () withJavadoc (),
    "org.typelevel" %% "mouse"           % Versions.mouse withSources () withJavadoc (),
    "org.tpolecat"  %% "doobie-core"     % Versions.doobie,
    "org.tpolecat"  %% "doobie-postgres" % Versions.doobie,
    "org.typelevel" %% "squants"         % Versions.squants,
    "org.slf4j"     % "slf4j-api"        % Versions.slf4j,
    "com.olegpy"    %% "meow-mtl"        % Versions.meowMtl,
    "io.circe"      %% "circe-config"    % Versions.circeConfig,
    "io.circe"      %% "circe-core"      % Versions.circe,
    "io.circe"      %% "circe-parser"    % Versions.circe,
    "io.circe"      %% "circe-generic"   % Versions.circe
  )
  lazy val test: Seq[ModuleID] = Seq(
    "io.monix"       %% "minitest"   % Versions.miniTest   % s"it,$Test",
    "org.scalacheck" %% "scalacheck" % Versions.scalaCheck % s"it,$Test",
    "org.tpolecat"   %% "doobie-h2"  % Versions.doobie     % s"it,$Test"
  )

  lazy val protocol: Seq[ModuleID] = Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
  )
}
