package io.syslogic.agconnect;

import org.gradle.api.Project;
import org.gradle.internal.impldep.junit.framework.TestCase;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Abstract Base {@link TestCase}
 *
 * @author Martin Zeitler
 */
abstract class BaseTestCase extends TestCase {

    String identifier = "io.syslogic.agconnect.publishing";
    @TempDir File testProjectDir;
    File settingsFile;
    File buildFile;

    Project project;
    String clientId;
    String secret;

    /**
     * Generate buildscript & plugins block.
     * @see <a href="https://github.com/gradle/gradle/blob/master/subprojects/core/src/main/java/org/gradle/api/internal/initialization/DefaultScriptHandler.java">DefaultScriptHandler.java</a>
     */
    @BeforeEach
    @SuppressWarnings("deprecation")
    public void setup() {
        this.settingsFile = new File(testProjectDir, "settings.gradle");
        this.buildFile = new File(testProjectDir, "build.gradle");
        try {
            FileUtils.writeStringToFile(this.buildFile, "buildscript {\n" +
                "repositories {\n" +
                    "google()\n" +
                    "mavenCentral()\n" +
                    "gradlePluginPortal()\n" +
                    "maven { url 'https://developer.huawei.com/repo/' }\n" +
                "}\n" +
                "dependencies {\n" +
                    "classpath 'com.android.tools.build:gradle:7.2.1'\n" +
                    "classpath 'com.huawei.agconnect:agcp:1.7.0.300'\n" +
               "}\n" +
            "}\n\n"+
            "apply plugin: 'com.android.application'\n" +
            "apply plugin: 'com.huawei.agconnect'\n" +

            "android {\n" +
                "compileSdk 32\n" +
            "}");

            logFileContents(this.buildFile.getAbsolutePath());
        } catch (IOException ignore) {}
    }

    BuildResult getBuildResult(@SuppressWarnings("SameParameterValue") String arguments) {
        return GradleRunner.create()
                .withProjectDir(this.testProjectDir)
                .withArguments(arguments)
                .withPluginClasspath()
                .build();
    }

    void logFileContents(String path) {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(s -> sb.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sb);
    }
}
