// :buildSrc
plugins {
    alias(buildSrc.plugins.maven.publish)
    alias(buildSrc.plugins.gradle.plugin)
    alias(buildSrc.plugins.gradle.publish)
}

val pluginId: String by extra(buildSrc.versions.plugin.id.get())
val pluginCls: String by extra(buildSrc.versions.plugin.cls.get())
val pluginGroup: String by extra(buildSrc.versions.plugin.group.get())
val pluginVersion: String by extra(buildSrc.versions.plugin.version.get())
val pluginName: String by extra(buildSrc.versions.plugin.name.get())
val pluginDesc: String by extra(buildSrc.versions.plugin.desc.get())
val pluginIdentifier: String by extra(buildSrc.versions.plugin.identifier.get())
val githubEmail: String by extra(buildSrc.versions.github.email.get())
val githubDev: String by extra(buildSrc.versions.github.dev.get())
val githubHandle: String by extra(buildSrc.versions.github.handle.get())


gradlePlugin {
    plugins {
        create("PublishingPlugin") {
            id = pluginId
            implementationClass = pluginCls
            displayName = pluginName
            description = pluginDesc
        }
    }
}

dependencies {

    api(dependencyNotation = gradleApi())
    api(dependencyNotation = buildSrc.android.gradle)

    implementation(dependencyNotation = buildSrc.annotations)
    implementation(dependencyNotation = buildSrc.bundles.http.components)

    testImplementation(dependencyNotation = buildSrc.junit)
    testImplementation(dependencyNotation = gradleTestKit())
    testImplementation(dependencyNotation = buildSrc.annotations)
    testImplementation(dependencyNotation = project)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set(pluginIdentifier)
    archiveVersion.set(pluginVersion)
}

// Gradle 9.0 deprecation fix
val implCls: Configuration by configurations.creating {
    extendsFrom(configurations.getByName("implementation"))
    isCanBeResolved = true
}

val javadocs by tasks.registering(Javadoc::class) {
    title = "$pluginName $pluginVersion API"
    classpath += implCls.asFileTree.filter {it.extension == "jar"}
    destinationDir = rootProject.file("build/javadoc")
    source = sourceSets.main.get().allJava
    // options.links = "https://docs.oracle.com/en/java/javase/17/docs/api/"
    // options.linkSource = true
    // options.author = true
    isFailOnError = false
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(rootProject.file("build/javadoc"))
    dependsOn(javadocs)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().java.srcDirs)
}

artifacts {
    archives(javadocJar)
    archives(sourcesJar)
}

group = pluginGroup
version = pluginVersion

configure<PublishingExtension> {

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${githubHandle}/${pluginIdentifier}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("Plugin") {
            from(components.getByName("java"))
            groupId = pluginGroup
            artifactId = pluginIdentifier
            version = pluginVersion
            pom {
                name = pluginName
                description = pluginDesc
                url = "https://github.com/${githubHandle}/${pluginIdentifier}"
                scm {
                    connection = "scm:git:git://github.com/${githubHandle}/${pluginIdentifier}.git"
                    developerConnection = "scm:git:ssh://github.com/${githubHandle}/${pluginIdentifier}.git"
                    url = "https://github.com/${githubHandle}/${pluginIdentifier}/"
                }
                developers {
                    developer {
                        name = githubDev
                        email = githubEmail
                        id = githubHandle
                    }
                }
                licenses {
                    license {
                        name = "MIT License"
                        url = "http://www.opensource.org/licenses/mit-license.php"
                    }
                }
            }
        }
    }
}
