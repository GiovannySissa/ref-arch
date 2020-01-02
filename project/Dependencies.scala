import sbt._

object Versions {
  lazy val cats = "2.0.0"
  lazy val catsEffects = "2.0.0"
  lazy val miniTest = "2.7.0"
  lazy val mouse = "0.23"
  lazy val scalaCheck = "1.14.2"
}

object Dependencies {

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"          % Versions.cats withSources () withJavadoc (),
    "org.typelevel" %% "cats-effect"        % Versions.catsEffects withSources () withJavadoc (),
    "org.typelevel" %% "mouse"              % Versions.mouse withSources () withJavadoc ()
  )
  lazy val test: Seq[ModuleID] = Seq(
    "io.monix" %% "minitest"                                      % Versions.miniTest      % s"it,$Test",
    "org.scalacheck" %% "scalacheck"                              % Versions.scalaCheck    % s"it,$Test"
  )
}
