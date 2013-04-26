scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.0",
    "org.specs2" %% "specs2" % "1.14" % "test"
)

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "releases"  at "http://oss.sonatype.org/content/repositories/releases")
