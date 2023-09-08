/**
 * JetBrains Space Automation
 * This Kotlin script file lets you automate build activities
 * For more info, see https://www.jetbrains.com/help/space/automation.html
 */

/** Build library */
job("Build library") {
    env["JB_SPACE_MAVEN_REPO"] = "{{ project:JB_SPACE_MAVEN_REPO }}"
    gradlew("amazoncorretto:17-alpine", "build")
}
