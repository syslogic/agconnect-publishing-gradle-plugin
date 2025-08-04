// settings.gradle.kts

pluginManagement {
    repositories {
        gradlePluginPortal {
            content {
                excludeGroupByRegex("org.apache.*")
                includeGroupByRegex("com.gradle.*")
            }
        }
        mavenCentral {
            content {
                includeGroupByRegex("org.apache.*")
            }
        }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    versionCatalogs {
        create("buildSrc") {
            from(files("gradle/libs.versions.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
