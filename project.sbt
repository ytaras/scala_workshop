scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.0",
    "org.mongodb" %% "casbah" % "2.6.0",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
    "org.specs2" %% "specs2" % "1.14" % "test"
)

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")
