package io.syslogic.agconnect;

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

    @TempDir File testProject;
    File buildFile;

    File credentials;
    File apiConfig;
    File srcDebug;
    File srcRelease;
    File appConfigDebug;
    File appConfigRelease;

    /**
     * Generate buildscript & plugins block.
     *
     * @see <a href="https://github.com/gradle/gradle/blob/master/subprojects/core/src/main/java/org/gradle/api/internal/initialization/DefaultScriptHandler.java">DefaultScriptHandler.java</a>
     */
    @BeforeEach
    public void setup() {

        /* credentials/agc-apiclient.json */
        this.credentials = new File(testProject, "credentials");
        if (this.credentials.mkdir()) {
            this.apiConfig = new File(credentials, "agc-apiclient.json");
            this.writeFile(this.apiConfig, "{}");
        }

        /* src/java/debug/agconnect-services.json */
        this.srcDebug = new File(testProject, "src" + File.separator + "java" + File.separator + "debug");
        if (this.srcDebug.mkdir()) {
            this.appConfigDebug = new File(srcDebug, "agconnect-services.json");
            this.writeFile(this.appConfigDebug, "{}");
        }

        /* src/java/release/agconnect-services.json */
        this.srcRelease = new File(testProject, "src" + File.separator + "java" + File.separator + "release");
        if (this.srcRelease.mkdir()) {
            this.appConfigRelease = new File(srcRelease, "agconnect-services.json");
            this.writeFile(this.appConfigRelease, "{}");
        }

        /* build.gradle */
        this.buildFile = new File(testProject, "build.gradle");
        this.writeFile(this.buildFile, "buildscript {\n" +
            "repositories {\n" +
                "google()\n" +
                "mavenCentral()\n" +
                "maven { url 'https://developer.huawei.com/repo/' }\n" +
                "maven { url 'https://jitpack.io' }\n" +
                "mavenLocal()\n" +
            "}\n" +
            "dependencies {\n" +
                "classpath 'com.android.tools.build:gradle:7.2.1'\n" +
                "classpath 'com.huawei.agconnect:agcp:1.7.0.300'\n" +
                "classpath 'io.syslogic:agconnect-publishing-gradle-plugin:7.2.1.6'\n" +
           "}\n" +
        "}\n"+

        "apply plugin: 'com.android.application'\n" +
        "apply plugin: 'com.huawei.agconnect'\n" +
        "apply plugin: '" + this.identifier + "'\n" +

        "android {\n" +
            "compileSdk 32\n" +
            "defaultConfig {\n" +
                "minSdk 23\n" +
                "targetSdk 32\n" +
            "}\n" +
            "signingConfigs {\n" +
                "debug {\n" +
                "}\n" +
                "release {\n" +
                "}\n"+
            "}\n" +
            "buildTypes {\n" +
                "debug {\n" +
                    "signingConfig signingConfigs.debug\n" +
                    "applicationIdSuffix '.debug'\n" +
                    "debuggable true\n" +
                    "jniDebuggable true\n" +
                    "zipAlignEnabled true\n" +
                    "renderscriptDebuggable true\n" +
                    "pseudoLocalesEnabled false\n" +
                    "shrinkResources false\n" +
                    "minifyEnabled false\n" +
                "}\n" +
                "release {\n" +
                    "signingConfig signingConfigs.release\n" +
                    "shrinkResources true\n" +
                    "testCoverageEnabled false\n" +
                    "zipAlignEnabled true\n" +
                    "pseudoLocalesEnabled false\n" +
                    "renderscriptDebuggable false\n" +
                    "minifyEnabled true\n" +
                    "jniDebuggable false\n" +
                    "debuggable false\n" +
                "}\n"+
            "}\n"+
        "}\n\n" +

        "agcPublishing {\n" +

        "}\n");
    }

    BuildResult getBuildResult(@SuppressWarnings("SameParameterValue") String arguments) {
        return GradleRunner.create()
                .withProjectDir(this.testProject)
                .withArguments(arguments)
                .withPluginClasspath()
                .build();
    }

    String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            stream.forEach(s -> sb.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    void writeFile(File file, String data) {
        try {
            FileUtils.writeStringToFile(file, data);
        } catch (IOException ignore) {}
    }
}
