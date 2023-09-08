/**
 * JetBrains Space Automation
 * This Kotlin script file lets you automate build activities
 * For more info, see https://www.jetbrains.com/help/space/automation.html
 */

/** Build AGConnect publishing */
job("Build AGConnect publishing") {
    startOn {
        gitPush { enabled = false }
    }
    parameters {
        text("GRADLE_TASKS", value = "build", description = "Gradle tasks") {
            options("build", "build publish") {
                allowMultiple = false
            }
        }
    }
    container(displayName = "Gradle build", image = "{{ project:DOCKER_IMAGE }}:lts") {
        env["JB_SPACE_MAVEN_REPO"] = "{{ project:JB_SPACE_MAVEN_REPO }}"
        env["GRADLE_TASKS"] = "{{ GRADLE_TASKS }}"
        shellScript {
            content = "gradle --init-script $mountDir/system/gradle/init.gradle -Dorg.gradle.parallel=false ${'$'}GRADLE_TASKS"
        }
    }
}
