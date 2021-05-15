import sbt._

object Versions {
  lazy val cats        = "2.1.1"
  lazy val catsEffects = "2.1.2"
  lazy val miniTest    = "2.7.0"
  lazy val mouse       = "0.23"
  lazy val scalaCheck  = "1.14.3"
  lazy val doobie      = "0.8.8"
  lazy val squants     = "1.6.0"
  lazy val slf4j       = "1.7.30"
  lazy val meowMtl     = "0.3.0-M1"
  lazy val circeConfig = "0.8.0"
  lazy val circe       = "0.13.0"
  lazy val fs2         = "2.5.6"
  lazy val monix       = "3.1.0"
}

object Dependencies {
  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"       % Versions.cats withSources () withJavadoc (),
    "org.typelevel" %% "cats-effect"     % Versions.catsEffects withSources () withJavadoc (),
    "org.typelevel" %% "mouse"           % Versions.mouse withSources () withJavadoc (),
    "org.tpolecat"  %% "doobie-core"     % Versions.doobie,
    "org.tpolecat"  %% "doobie-postgres" % Versions.doobie,
    "org.tpolecat"  %% "doobie-hikari"   % Versions.doobie,
    "org.typelevel" %% "squants"         % Versions.squants,
    "org.slf4j"     % "slf4j-api"        % Versions.slf4j,
    "com.olegpy"    %% "meow-mtl"        % Versions.meowMtl,
    "io.circe"      %% "circe-config"    % Versions.circeConfig,
    "io.circe"      %% "circe-core"      % Versions.circe,
    "io.circe"      %% "circe-parser"    % Versions.circe,
    "io.circe"      %% "circe-generic"   % Versions.circe,
    "co.fs2"        %% "fs2-core"        % Versions.fs2,
    "io.monix"      %% "monix"           % Versions.monix withSources () withJavadoc ()
  )
  lazy val test: Seq[ModuleID] = Seq(
    "io.monix"       %% "minitest"      % Versions.miniTest   % s"it,$Test",
    "io.monix"       %% "minitest-laws" % Versions.miniTest   % s"it,$Test",
    "org.scalacheck" %% "scalacheck"    % Versions.scalaCheck % s"it,$Test",
    "org.tpolecat"   %% "doobie-h2"     % Versions.doobie     % s"it,$Test"
  )

  lazy val protocol: Seq[ModuleID] = Seq(
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
  )

  lazy val grpc: Seq[ModuleID] = Seq(
    "io.grpc" % "grpc-netty"    % scalapb.compiler.Version.grpcJavaVersion,
    "io.grpc" % "grpc-services" % scalapb.compiler.Version.grpcJavaVersion
  )

}
