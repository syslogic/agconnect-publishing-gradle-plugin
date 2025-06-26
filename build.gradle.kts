// :buildSrc
plugins {
    alias(buildSrc.plugins.maven.publish)
    alias(buildSrc.plugins.gradle.plugin)
    alias(buildSrc.plugins.gradle.publish)
}

project.ext.set("github_handle",       "syslogic")
project.ext.set("group_id",            "io.syslogic")
project.ext.set("plugin_display_name", "AppGallery Connect Publishing Plugin")
project.ext.set("plugin_description",  "It uploads Android APK/ABB artifacts with AppGallery Connect Publishing API.")
project.ext.set("plugin_identifier",   "agconnect-publishing-gradle-plugin")
project.ext.set("plugin_class",        "io.syslogic.agconnect.PublishingPlugin")
project.ext.set("plugin_id",           "io.syslogic.agconnect.publishing")
project.ext.set("plugin_version",      buildSrc.versions.plugin.version.get())

dependencies {

    compileOnly(dependencyNotation = gradleApi())
    //noinspection DependencyNotationArgument
    implementation(dependencyNotation = buildSrc.android.gradle)
    //noinspection DependencyNotationArgument
    implementation(dependencyNotation = buildSrc.annotations)
    //noinspection DependencyNotationArgument
    implementation(dependencyNotation = buildSrc.bundles.http.components)

    testImplementation(dependencyNotation = buildSrc.junit)
    testImplementation(dependencyNotation = gradleTestKit())
    //noinspection DependencyNotationArgument
    testImplementation(dependencyNotation = buildSrc.annotations)
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


tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set("${project.ext.get("plugin_identifier")}")
    archiveVersion.set("${project.ext.get("plugin_version")}")
}

// Gradle 9.0 deprecation fix
val implCls: Configuration by configurations.creating {
    extendsFrom(configurations.getByName("implementation"))
    isCanBeResolved = true
}

val javadocs by tasks.registering(Javadoc::class) {
    title = "${project.ext.get("plugin_display_name")} ${project.ext.get("plugin_version")} API"
    classpath += implCls.asFileTree.filter {it.extension == "jar"}
    setDestinationDir(project.file("/build/outputs/javadoc"))
    source = sourceSets.main.get().allJava
    // options.links = "https://docs.oracle.com/en/java/javase/17/docs/api/"
    // options.linkSource = true
    // options.author = true
    isFailOnError = false
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(project.file("/build/outputs/javadoc"))
    dependsOn(javadocs)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().java.srcDirs)
}

group = "${project.ext.get("group_id")}"
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
                groupId = "${project.ext.get("group_id")}"
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
    }
}
