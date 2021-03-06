organization := "com.holdenkarau"

name := "spark-testing-base"

publishMavenStyle := true

version := "0.4.6"

sparkVersion := "2.0.0"

scalaVersion := {
  if (sparkVersion.value >= "2.0.0") {
    "2.11.8"
  } else {
    "2.10.6"
  }
}

crossScalaVersions := {
  if (sparkVersion.value > "2.0.0") {
    Seq("2.11.8")
  } else {
    Seq("2.10.6", "2.11.8")
  }
}

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

//tag::spName[]
spName := "holdenk/spark-testing-base"
//end::spName[]

sparkComponents := {
  if (sparkVersion.value >= "2.0.0") Seq("core", "streaming", "sql", "catalyst", "hive", "yarn", "mllib")
  else Seq("core", "streaming", "sql", "catalyst", "hive", "streaming-kafka", "yarn", "mllib")
}

parallelExecution in Test := false
fork := true


coverageHighlighting := {
  if (scalaBinaryVersion.value == "2.10") false
  else true
}

// Allow kafka (and other) utils to have version specific files
unmanagedSourceDirectories in Compile  := {
  if (sparkVersion.value >= "2.0.0") Seq(
    (sourceDirectory in Compile)(_ / "2.0/scala"),
    (sourceDirectory in Compile)(_ / "1.6/scala"),
    (sourceDirectory in Compile)(_ / "1.5/scala"),
    (sourceDirectory in Compile)(_ / "1.4/scala"),
    (sourceDirectory in Compile)(_ / "1.3/scala"), (sourceDirectory in Compile)(_ / "1.3/java")
  ).join.value
  else if (sparkVersion.value >= "1.6") Seq(
    (sourceDirectory in Compile)(_ / "pre-2.0/scala"),
    (sourceDirectory in Compile)(_ / "1.6/scala"),
    (sourceDirectory in Compile)(_ / "1.5/scala"),
    (sourceDirectory in Compile)(_ / "1.4/scala"),
    (sourceDirectory in Compile)(_ / "kafka/scala"),
    (sourceDirectory in Compile)(_ / "1.3/scala"), (sourceDirectory in Compile)(_ / "1.3/java")
  ).join.value
  else if (sparkVersion.value >= "1.5") Seq(
    (sourceDirectory in Compile)(_ / "pre-2.0/scala"),
    (sourceDirectory in Compile)(_ / "1.5/scala"),
    (sourceDirectory in Compile)(_ / "1.4/scala"),
    (sourceDirectory in Compile)(_ / "kafka/scala"),
    (sourceDirectory in Compile)(_ / "1.3/scala"), (sourceDirectory in Compile)(_ / "1.3/java")
  ).join.value
  else if (sparkVersion.value >= "1.4") Seq(
    (sourceDirectory in Compile)(_ / "pre-2.0/scala"),
    (sourceDirectory in Compile)(_ / "pre-1.5/scala"),
    (sourceDirectory in Compile)(_ / "1.4/scala"),
    (sourceDirectory in Compile)(_ / "kafka/scala"),
    (sourceDirectory in Compile)(_ / "1.3/scala"), (sourceDirectory in Compile)(_ / "1.3/java")
  ).join.value
  else Seq(
    (sourceDirectory in Compile)(_ / "pre-2.0/scala"),
    (sourceDirectory in Compile)(_ / "pre-1.5/scala"),
    (sourceDirectory in Compile)(_ / "1.3/scala"), (sourceDirectory in Compile)(_ / "1.3/java"),
    (sourceDirectory in Compile)(_ / "1.3-only/scala")
  ).join.value
}

unmanagedSourceDirectories in Test  := {
  if (sparkVersion.value >= "2.0.0") Seq(
    (sourceDirectory in Test)(_ / "1.6/scala"), (sourceDirectory in Test)(_ / "1.6/java"),
    (sourceDirectory in Test)(_ / "1.4/scala"),
    (sourceDirectory in Test)(_ / "1.3/scala"), (sourceDirectory in Test)(_ / "1.3/java")
  ).join.value
  else if (sparkVersion.value >= "1.6") Seq(
    (sourceDirectory in Test)(_ / "pre-2.0/scala"), (sourceDirectory in Test)(_ / "pre-2.0/java"),
    (sourceDirectory in Test)(_ / "1.6/scala"), (sourceDirectory in Test)(_ / "1.6/java"),
    (sourceDirectory in Test)(_ / "1.4/scala"),
    (sourceDirectory in Test)(_ / "kafka/scala"),
    (sourceDirectory in Test)(_ / "1.3/scala"), (sourceDirectory in Test)(_ / "1.3/java")
  ).join.value
  else if (sparkVersion.value >= "1.4") Seq(
    (sourceDirectory in Test)(_ / "pre-2.0/scala"), (sourceDirectory in Test)(_ / "pre-2.0/java"),
    (sourceDirectory in Test)(_ / "1.4/scala"),
    (sourceDirectory in Test)(_ / "kafka/scala"),
    (sourceDirectory in Test)(_ / "1.3/scala"), (sourceDirectory in Test)(_ / "1.3/java")
  ).join.value
  else Seq(
    (sourceDirectory in Test)(_ / "pre-2.0/scala"), (sourceDirectory in Test)(_ / "pre-2.0/java"),
    (sourceDirectory in Test)(_ / "1.3/scala"), (sourceDirectory in Test)(_ / "1.3/java")
  ).join.value
}


javaOptions ++= Seq("-Xms512M", "-Xmx2048M", "-XX:MaxPermSize=2048M", "-XX:+CMSClassUnloadingEnabled")

// additional libraries
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6",
  "io.github.nicolasstucki" %% "multisets" % "0.3",
  "org.scalacheck" %% "scalacheck" % "1.12.5",
  "junit" % "junit" % "4.11",
  "org.eclipse.jetty" % "jetty-util" % "9.3.11.v20160721",
  "com.novocode" % "junit-interface" % "0.11" % "test->default")

// Based on Hadoop Mini Cluster tests from Alpine's PluginSDK (Apache licensed)
// javax.servlet signing issues can be tricky, we can just exclude the dep
def excludeFromAll(items: Seq[ModuleID], group: String, artifact: String) =
  items.map(_.exclude(group, artifact))

def excludeJavaxServlet(items: Seq[ModuleID]) =
  excludeFromAll(items, "javax.servlet", "servlet-api")

lazy val miniClusterDependencies = excludeJavaxServlet(Seq(
  "org.apache.hadoop" % "hadoop-hdfs" % "2.6.4" % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-common" % "2.6.4" % "compile,test" classifier "" classifier "tests" ,
  "org.apache.hadoop" % "hadoop-client" % "2.6.4" % "compile,test" classifier "" classifier "tests" ,
  "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "2.6.4" % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-yarn-server-tests" % "2.6.4" % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-yarn-server-web-proxy" % "2.6.4" % "compile,test" classifier "" classifier "tests",
  "org.apache.hadoop" % "hadoop-minicluster" % "2.6.4"))

libraryDependencies ++= miniClusterDependencies

scalacOptions ++= Seq("-deprecation", "-unchecked")

pomIncludeRepository := { x => false }

resolvers ++= Seq(
  "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
  "Spray Repository" at "http://repo.spray.cc/",
  "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Twitter4J Repository" at "http://twitter4j.org/maven2/",
  "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
  "Twitter Maven Repo" at "http://maven.twttr.com/",
  "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
  Resolver.sonatypeRepo("public")
)

// publish settings
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

licenses := Seq("Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/holdenk/spark-testing-base"))

pomExtra := (
  <scm>
    <url>git@github.com:holdenk/spark-testing-base.git</url>
    <connection>scm:git@github.com:holdenk/spark-testing-base.git</connection>
  </scm>
  <developers>
    <developer>
      <id>holdenk</id>
      <name>Holden Karau</name>
      <url>http://www.holdenkarau.com</url>
      <email>holden@pigscanfly.ca</email>
    </developer>
  </developers>
)

//credentials += Credentials(Path.userHome / ".ivy2" / ".spcredentials")
credentials ++= Seq(Credentials(Path.userHome / ".ivy2" / ".sbtcredentials"), Credentials(Path.userHome / ".ivy2" / ".sparkcredentials"))

spIncludeMaven := true

useGpg := true
