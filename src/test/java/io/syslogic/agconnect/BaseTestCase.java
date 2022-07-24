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

    /** Plugin package identifier */
    String identifier = "io.syslogic.agconnect.publishing";

    /** Local environment, not to be used by CI */
    String sourceDirectory = "C:\\Home\\Applications\\androidx-audiolibrary";

    /** Local environment, not to be used by CI */
    String packageId = "io.syslogic.audio";

    /** Temporary directory, where to run the generated project */
    @TempDir File testProject;

    /** File `settings.gradle` is required to look up `aapt2` */
    File settingsFile;

    /** File `build.gradle` */
    File buildFile;

    /** Directory `credentials` */
    File credentials;
    String apiConfig;

    /** Directory `src` */
    File src;

    /** Directory `src/main/java` */
    File srcMain;

    /** Directory `src/debug/java` */
    File srcDebug;

    /** Directory `src/release/java` */
    File srcRelease;

    /** File `src/main/AndroidManifest.xml` */
    File manifest;

    /**
     * The configuration JSON string for debug builds;
     * either locally copied or inserted as GitHub secret.
     */
    String appConfigDebug;

    /**
     * The configuration JSON string for release builds;
     * either locally copied or inserted as GitHub secret.
     */
    String appConfigRelease;

    /**
     * Generate the configuration files required in order to test the plugin, which are:
     * `build.gradle`, `settings.gradle`, `agconnect-services.json`, `agc-apiclient.json`
     * and `AndroidManifest.xml`.
     *
     * @see <a href="https://github.com/gradle/gradle/blob/master/subprojects/core/src/main/java/org/gradle/api/internal/initialization/DefaultScriptHandler.java">DefaultScriptHandler.java</a>
     */
    @BeforeEach
    public void setup() {

        /* in order to support both environments */
        if (System.getenv().containsKey("CI")) {initCi();}

        /* always set up the strings */
        initDefault();

        /* src */
        this.src = new File(testProject, "src");
        if (src.exists() || src.mkdir()) {

            /* src/main/AndroidManifest.xml */
            this.srcMain = new File(testProject, "src" + File.separator + "main");
            if (srcMain.exists() || srcMain.mkdir()) {
                this.manifest = new File(testProject, "src" + File.separator + "main" +  File.separator + "AndroidManifest.xml");
                this.writeFile(manifest,"<?xml version='1.0' encoding='utf-8'?>\n<manifest package='" + this.packageId + "'/>");
            }

            /* src/debug/agconnect-services.json */
            this.srcDebug = new File(testProject, "src" + File.separator + "debug");
            if (srcDebug.exists() || srcDebug.mkdir()) {
                writeFile(new File(srcDebug, "agconnect-services.json"), appConfigDebug);
            }

            /* src/release/agconnect-services.json */
            this.srcRelease = new File(testProject, "src" + File.separator + "release");
            if (srcRelease.exists() || srcRelease.mkdir()) {
                writeFile(new File(srcRelease, "agconnect-services.json"), appConfigRelease);
            }
        }

        /* credentials/agc-apiclient.json */
        this.credentials = new File(testProject, "credentials");
        if (credentials.exists() || credentials.mkdir()) {
            writeFile(new File(credentials, "agc-apiclient.json"), apiConfig);
        }

        /* settings.gradle */
        this.settingsFile = new File(testProject, "settings.gradle");
        writeFile(settingsFile, "import org.gradle.api.initialization.resolve.RepositoriesMode\n" +
                "pluginManagement {\n" +
                "    repositories {\n" +
                "        gradlePluginPortal()\n" +
                "        google()\n" +
                "        maven { url \"https://developer.huawei.com/repo/\" }\n" +
                "        maven { url 'https://jitpack.io' }\n" +
                "        mavenCentral()\n" +
                "    }\n" +
                "}\n" +
                "dependencyResolutionManagement {\n" +
                "    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)\n" +
                "    repositories {\n" +
                "        google()\n" +
                "        maven { url \"https://developer.huawei.com/repo/\" }\n" +
                "        mavenCentral()\n" +
                "    }\n" +
                "}\n");

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
                "applicationId '" + this.packageId + "'\n" +
                "versionName '1.0.0'\n" +
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

    /** GitHub Environment */
    private void initCi() {
        this.packageId = System.getenv("AGC_PACKAGE_ID");
    }

    /** Local Environment */
    private void initDefault() {
        String rootDirectory = getRootProjectPath();
        this.apiConfig = readFile(rootDirectory + "credentials" +  File.separator + "agc-apiclient.json");
        if (! System.getenv().containsKey("CI")) {
            this.appConfigRelease = readFile(rootDirectory + "mobile" + File.separator + "src" + File.separator + "huaweiRelease" + File.separator + "agconnect-services.json");
            this.appConfigDebug = readFile(rootDirectory + "mobile" + File.separator + "src" + File.separator + "huaweiDebug" + File.separator + "agconnect-services.json");
        } else {
            this.appConfigRelease = readFile(rootDirectory + "mobile" + File.separator + "src" + File.separator + "release" + File.separator + "agconnect-services.json");
            this.appConfigDebug = readFile(rootDirectory + "mobile" + File.separator + "src" + File.separator + "debug" + File.separator + "agconnect-services.json");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String getRootProjectPath() {
        if (! System.getenv().containsKey("CI")) {
            return new File(this.sourceDirectory).getAbsolutePath() + File.separator;
        } else {
            return new File(System.getenv().get("GITHUB_WORKSPACE")).getAbsolutePath() + File.separator;
        }
    }

    @SuppressWarnings("SameParameterValue")
    BuildResult getBuildResult(String arguments) {
        GradleRunner runner = GradleRunner
                .create()
                .withProjectDir(this.testProject)
                .withArguments(arguments)
                .withPluginClasspath();

        if (! System.getenv().containsKey("CI")) {
            runner
                    .withDebug(true)
                    .forwardOutput();
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
}
