import sbt._

object Versions {
  lazy val cats = "2.0.0"
  lazy val catsEffects = "2.0.0"
  lazy val miniTest = "2.7.0"
  lazy val mouse = "0.23"
  lazy val scalaCheck = "1.14.2"
  lazy val doobie = "0.8.7"
  lazy val squants = "1.6.0"
}

object Dependencies {

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"          % Versions.cats withSources () withJavadoc (),
    "org.typelevel" %% "cats-effect"        % Versions.catsEffects withSources () withJavadoc (),
    "org.typelevel" %% "mouse"              % Versions.mouse withSources () withJavadoc (),
    "org.tpolecat" %% "doobie-core"     % Versions.doobie,
    "org.tpolecat" %% "doobie-postgres" % Versions.doobie,
    "org.typelevel"  %% "squants"  % Versions.squants

  )
  lazy val test: Seq[ModuleID] = Seq(
    "io.monix" %% "minitest"                                      % Versions.miniTest      % s"it,$Test",
    "org.scalacheck" %% "scalacheck"                              % Versions.scalaCheck    % s"it,$Test"
  )
}