package io.syslogic.agconnect;

import org.gradle.api.tasks.InputFile;
import org.gradle.internal.impldep.junit.framework.TestCase;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.jetbrains.annotations.NotNull;
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
    File settingsFile;
    File buildFile;

    File credentials;
    String apiConfig;

    File src;
    File srcMain;
    File srcDebug;
    File srcRelease;

    File manifest;

    String appConfigDebug;
    String appConfigRelease;

    /**
     * Generate buildscript & plugins block.
     *
     * @see <a href="https://github.com/gradle/gradle/blob/master/subprojects/core/src/main/java/org/gradle/api/internal/initialization/DefaultScriptHandler.java">DefaultScriptHandler.java</a>
     */
    @BeforeEach
    public void setup() {

        /* in order to support both environments */
        if (! System.getenv().containsKey("CI")) {
            initLocal();
        } else {
            initCi();
        }

        /* src */
        this.src = new File(testProject, "src");
        if (src.exists() || src.mkdir()) {

            /* src/main/AndroidManifest.xml */
            this.srcMain = new File(testProject, "src" + File.separator + "main");
            if (srcMain.exists() || srcMain.mkdir()) {
                this.manifest = new File(testProject, "src" + File.separator + "main" +  File.separator + "AndroidManifest.xml");
                this.writeFile(manifest,"<?xml version='1.0' encoding='utf-8'?>\n<manifest package='io.syslogic.audio'/>");
            }

            /* src/debug/agconnect-services.json */
            this.srcDebug = new File(testProject, "src" + File.separator + "debug");
            if (srcDebug.exists() || srcDebug.mkdir()) {
                writeFile(new File(srcDebug, "agconnect-services.json"), appConfigDebug);
            } else {
                error("missing: " + srcDebug.getAbsolutePath());
            }

            /* src/release/agconnect-services.json */
            this.srcRelease = new File(testProject, "src" + File.separator + "release");
            if (srcRelease.exists() || srcRelease.mkdir()) {
                writeFile( new File(srcRelease, "agconnect-services.json"), appConfigRelease);
            } else {
                error("missing: " + srcRelease.getAbsolutePath());
            }
        }

        /* credentials/agc-apiclient.json */
        this.credentials = new File(testProject, "credentials");
        if (credentials.exists() || credentials.mkdir()) {
            writeFile(new File(credentials, "agc-apiclient.json"), apiConfig);
        } else {
            error("missing: " + credentials.getAbsolutePath());
        }

        /* settings.gradle */
        this.settingsFile = new File(testProject, "settings.gradle");

        /* build.gradle */
        this.buildFile = new File(testProject, "build.gradle");
        writeFile(buildFile, "buildscript {\n" +
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
                "applicationId 'io.syslogic.audio'\n" +
                "versionName '0.0.1'\n" +
                "versionCode 1\n" +
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

        "dependencies {\n" +
        "}\n\n" +

        "agcPublishing {\n" +

        "}\n");
    }

    /** Local Environment */
    private void initLocal() {
        this.apiConfig = readFile(getRootProjectPath() + File.separator + "credentials" +  File.separator + "agc-apiclient.json");
        this.appConfigRelease = readFile(getRootProjectPath() + File.separator + "mobile" + File.separator + "src" + File.separator + "huaweiRelease" + File.separator + "agconnect-services.json");
        this.appConfigDebug = readFile(getRootProjectPath() + File.separator + "mobile" + File.separator + "src" + File.separator + "huaweiDebug" + File.separator + "agconnect-services.json");
    }

    /** TODO: GitHub Environment */
    private void initCi() {
        this.apiConfig = System.getenv("AGC_API_CONFIG");
        this.appConfigRelease = System.getenv("AGC_APP_RELEASE_CONFIG");
        this.appConfigDebug = System.getenv("AGC_APP_DEBUG_CONFIG");
    }

    @NotNull
    private String getRootProjectPath() {
        return new File("C:\\Home\\Applications\\androidx-audiolibrary").getAbsolutePath();
    }

    BuildResult getBuildResult(String arguments) {

        GradleRunner runner = GradleRunner
                .create()
                .withProjectDir(this.testProject)
                .withArguments(arguments)
                .withPluginClasspath();

        if (! System.getenv().containsKey("CI")) {
            runner.withDebug(true).forwardOutput();
        }
        return runner.build();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void error(String data) {
        System.err.println(data);
    }

    void log(String data) {
        System.out.println(data);
    }
}
