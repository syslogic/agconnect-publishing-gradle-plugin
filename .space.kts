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
    container(displayName = "Gradle build", image = "{{ project:DOCKER_IMAGE }}:lts") {
        env["JB_SPACE_MAVEN_REPO"] = "{{ project:JB_SPACE_MAVEN_REPO }}"
        env["GRADLE_USER_HOME"] = "{{ project:GRADLE_USER_HOME }}"
        kotlinScript { api ->
            api.gradle("build")
            api.gradle("publish")
        }
    }
}
