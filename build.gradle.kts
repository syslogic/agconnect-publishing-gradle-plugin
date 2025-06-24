// :buildSrc
plugins {
    alias(plugin.plugins.maven.publish)
    alias(plugin.plugins.gradle.plugin)
    alias(plugin.plugins.gradle.publish)
}

project.ext.set("github_handle",       "syslogic")
project.ext.set("plugin_display_name", "AppGallery Connect Publishing Plugin")
project.ext.set("plugin_description",  "It uploads Android APK/ABB artifacts with AppGallery Connect Publishing API.")
project.ext.set("plugin_identifier",   "agconnect-publishing-gradle-plugin")
project.ext.set("plugin_class",        "io.syslogic.agconnect.PublishingPlugin")
project.ext.set("plugin_id",           "io.syslogic.agconnect.publishing")
project.ext.set("plugin_version",      plugin.versions.plugin.version.get())

dependencies {

    compileOnly(dependencyNotation = gradleApi())
    //noinspection DependencyNotationArgument
    implementation(dependencyNotation = plugin.android.gradle)
    //noinspection DependencyNotationArgument
    implementation(dependencyNotation = plugin.annotations)
    //noinspection DependencyNotationArgument
    implementation(dependencyNotation = plugin.bundles.http.components)

    testImplementation(dependencyNotation = plugin.junit)
    testImplementation(dependencyNotation = gradleTestKit())
    //noinspection DependencyNotationArgument
    testImplementation(dependencyNotation = plugin.annotations)
    testImplementation(dependencyNotation = project)
}

gradlePlugin {
    plugins {
        create("PublishingPlugin") {
            id = "${project.ext.get("plugin_id")}"
            implementationClass = "${project.ext.get("plugin_class")}"
            displayName = "${project.ext.get("plugin_display_name")}"
            description = "${project.ext.get("plugin_description")}"
        }
    }
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set("${project.ext.get("plugin_identifier")}")
    archiveVersion.set("${project.ext.get("plugin_version")}")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.register<Javadoc>("javadocs") {
    setDestinationDir(project.file("/build/outputs/javadoc"))
    title = "${project.ext.get("plugin_display_name")} ${project.ext.get("plugin_version")} API"
    source = sourceSets.getByName("main").java
    classpath = files(File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"))
    classpath += configurations.getByName("implementation")
    // options.links = "https://docs.oracle.com/en/java/javase/17/docs/api/"
    // options.linkSource = true
    // options.author = true
    isFailOnError = false
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(project.file("/build/outputs/javadoc"))
    dependsOn("javadocs")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").java.srcDirs)
}

group = "io.syslogic"
version = "${project.ext.get("plugin_version")}"
artifacts {
    archives(javadocJar)
    archives(sourcesJar)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.getByName("java"))
                groupId = "io.syslogic"
                artifactId = "${project.ext.get("plugin_identifier")}"
                version = "${project.ext.get("plugin_version")}"
                pom {
                    name = "${project.ext.get("plugin_display_name")}"
                    description = "${project.ext.get("plugin_description")}"
                    url = "https://github.com/${project.ext.get("github_handle")}/${project.ext.get("plugin_identifier")}"
                    scm {
                        connection = "scm:git:git://github.com/${project.ext.get("github_handle")}/${project.ext.get("plugin_identifier")}.git"
                        developerConnection = "scm:git:ssh://github.com/${project.ext.get("github_handle")}/${project.ext.get("plugin_identifier")}.git"
                        url = "https://github.com/${project.ext.get("github_handle")}/${project.ext.get("plugin_identifier")}/"
                    }
                }
            }
        }
        repositories {
            /*
             * JetBrains Spaces Repository (private)
             * https://lp.jetbrains.com/space-source-code-management/
             */
            if (System.getenv("JB_SPACE_MAVEN_REPO") != null) {
                maven {
                    url = uri(System.getenv("JB_SPACE_MAVEN_REPO"))
                    credentials {
                        username = System.getenv("JB_SPACE_CLIENT_ID")
                        password = System.getenv("JB_SPACE_CLIENT_SECRET")
                    }
                }
            }
        }
    }
}
