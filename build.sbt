
lazy val pumlview = (project in file("."))
	.settings(
		name := "pumlview",
		version := "0.1",
		scalaVersion := "2.12.4",
		scalacOptions ++= Seq(
			"-target:jvm-1.8",
			"-encoding", "UTF-8",
			"-unchecked",
			"-deprecation",
			"-Xfuture",
			"-Yno-adapted-args",
			"-Ypartial-unification",
			"-Ywarn-dead-code",
			"-Ywarn-numeric-widen",
			"-Ywarn-value-discard",
			"-Ywarn-unused",
			//		"-Xfatal-warnings"
			//		"-Xlog-implicits"
		),
		resolvers ++= Seq(Resolver.jcenterRepo),
		javacOptions ++= Seq(
			"-target", "1.8",
			"-source", "1.8",
			"-Xlint:deprecation"),
		libraryDependencies ++= Seq(
			"net.sourceforge.plantuml" % "plantuml" % "8059",
			"org.fxmisc.richtext" % "richtextfx" % "0.8.2",
			"net.kurobako.gesturefx" % "gesturefx" % "0.2.0",
			"com.github.pathikrit" %% "better-files" % "3.4.0",
			"org.scalafx" %% "scalafx" % "8.0.144-R12",
			"org.fxmisc.easybind" % "easybind" % "1.0.3",
			"com.beachape" %% "enumeratum" % "1.5.12",
			"com.google.guava" % "guava" % "24.0-jre"
		)
	)



