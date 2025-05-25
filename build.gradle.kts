// :buildSrc
buildscript {
    extra.apply{
        set("github_handle", "syslogic")
        set("plugin_display_name", "AppGallery Connect Publishing Plugin")
        set("plugin_description", "It uploads Android APK/ABB artifacts with AppGallery Connect Publishing API")
        set("plugin_identifier", "agconnect-publishing-gradle-plugin")
        set("plugin_class", "io.syslogic.agconnect.PublishingPlugin")
        set("plugin_id", "io.syslogic.agconnect.publishing")
        set("plugin_version", libs.versions.plugin.version.get())
    }
}

plugins {
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.gradle.plugin)
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.kotlin.jvm)
}

group = extra.get("github_handle") as String
version = libs.versions.plugin.version.get()

dependencies {
    compileOnly(gradleApi())
    implementation(libs.android.gradle)
    implementation(libs.annotations)
    implementation(libs.bundles.http.components)
    testImplementation(libs.junit)
    testImplementation(gradleTestKit())
    testImplementation(libs.annotations)
    testImplementation(project)
}

gradlePlugin {
    plugins {
        create("PublishingPlugin") {
            id = extra.get("plugin_id") as String
            implementationClass = extra.get("plugin_class") as String
            displayName = extra.get("plugin_display_name") as String
            description = extra.get("plugin_description") as String
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set(rootProject.extra.get("plugin_identifier") as String)
    archiveVersion.set(rootProject.extra.get("plugin_version") as String)
}

val javadocs by tasks.registering(Javadoc::class) {
    source = java.sourceSets.named("main").get().allJava
    configurations["implementation"].isCanBeResolved = true
    classpath = files(File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar"))
    classpath += configurations.implementation.get() as FileCollection
    isFailOnError = false
    options.verbose()
    (options as StandardJavadocDocletOptions).links("https://docs.oracle.com/en/java/javase/17/docs/api/")
    (options as StandardJavadocDocletOptions).linkSource(true)
    (options as StandardJavadocDocletOptions).author(true)
    setDestinationDir(project.file("build/outputs/javadoc"))
}
val sourcesJar by tasks.registering(Jar::class) {
    from(java.sourceSets.named("main").get().allJava)
    archiveClassifier.set("sources")
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(javadocs.get())
    from(javadocs.get().destinationDir)
    archiveClassifier.set("javadoc")
}

artifacts {
    archives(javadocJar.get())
    archives(sourcesJar.get())
}

afterEvaluate {
    publishing {
        publications {
            val githubUrl = "github.com/${extra.get("github_handle")}/${extra.get("plugin_identifier")}/"
            register("java", MavenPublication::class) {
                from(components.getByName("java"))
                groupId = extra.get("github_handle") as String
                artifactId = extra.get("plugin_identifier") as String
                version = libs.versions.plugin.version.get()
                pom {
                    name = extra.get("plugin_display_name") as String
                    description = extra.get("plugin_description") as String
                    url = "https://${githubUrl}"
                    scm {
                        developerConnection = "scm:git:ssh://${githubUrl}.git"
                        connection = "scm:git:git://${githubUrl}.git"
                        url = "https://${githubUrl}"
                    }
                }
            }
        }

        repositories {
            // https://support.atlassian.com/bitbucket-cloud/docs/variables-and-secrets/
            if (System.getenv("BITBUCKET_BUILD_NUMBER") != null) {
                maven {
                    url = uri(System.getenv("BITBUCKET_MAVEN_REPO"))
                    credentials {
                        username = System.getenv("BITBUCKET_CLIENT_ID")
                        password = System.getenv("BITBUCKET_CLIENT_SECRET")
                    }
                }
            }
        }
    }
}
