organization := "ca.jakegreene"

name := "akka-failure-handling"

version := "0.1"

scalaVersion := "2.10.4"

resolvers ++= Seq(
   "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases/"
  ,"sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
  ,"typesafe release"   at "http://repo.typesafe.com/typesafe/releases/"
  ,"typesafe repo"      at "http://repo.typesafe.com/typesafe/repo/"
  ,"typesafe snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
  ,"maven central"      at "http://repo1.maven.org/maven2/"
)

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka"    %% "akka-actor"                    % "2.4-SNAPSHOT"         withSources(),
    "com.typesafe.akka"    %% "akka-slf4j"                    % "2.3.2"                withSources(),
    "com.typesafe.akka"    %% "akka-testkit"                  % "2.3.2"       % "test" withSources()
  )
}
