// settings.gradle.kts
pluginManagement {
    repositories {
        mavenCentral {
            content {
                includeGroupByRegex("org.apache.*")
            }
        }
        gradlePluginPortal {
            content {
                excludeGroupByRegex("org.apache.*")
                includeGroupByRegex("com.gradle.*")
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
