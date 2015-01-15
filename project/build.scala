import sbt.Keys._
import sbt._

object Settings {
  import LibgdxBuild.libgdxVersion

  lazy val core = plugins.JvmPlugin.projectSettings ++ Seq(
    version := (version in LocalProject("all-platforms")).value,
    libgdxVersion := (libgdxVersion in LocalProject("all-platforms")).value,
    scalaVersion := (scalaVersion in LocalProject("all-platforms")).value,
    libraryDependencies ++= Seq(
      "com.badlogicgames.gdx" % "gdx" % libgdxVersion.value
    ),
    javacOptions ++= Seq(
      "-Xlint",
      "-encoding", "UTF-8",
      "-source", "1.7",
      "-target", "1.7"
    ),
    scalacOptions ++= Seq(
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ywarn-numeric-widen",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-encoding", "UTF-8",
      "-target:jvm-1.7"
    )
  )

  lazy val desktop = core ++ Seq(
    libraryDependencies ++= Seq(
      "net.sf.proguard" % "proguard-base" % "4.11" % "provided",
      "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % libgdxVersion.value,
      "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion.value classifier "natives-desktop"
    )
  )

}

object LibgdxBuild extends Build {
  lazy val libgdxVersion = settingKey[String]("version of Libgdx library")

  lazy val noise = RootProject(uri("git://github.com/Reldan/Joise.git"))

  lazy val core = Project(
    id       = "core",
    base     = file("core"),
    settings = Settings.core
  ).dependsOn(noise)

  lazy val desktop = Project(
    id       = "desktop",
    base     = file("desktop"),
    settings = Settings.desktop
  ).dependsOn(core)


  lazy val all = Project(
    id       = "all-platforms",
    base     = file("."),
    settings = Settings.core
  ).aggregate(core, desktop, noise)
}

