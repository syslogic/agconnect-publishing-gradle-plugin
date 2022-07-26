package io.syslogic.agconnect;

import org.gradle.api.Project;
import org.gradle.internal.impldep.junit.framework.TestCase;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.UnexpectedBuildResultException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
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

    /** Temporary directory for the generated project */
    static @TempDir File testProject;

    /** Local environment, not to be used by CI */
    static String packageId = "io.syslogic.audio";

    /** File `keystore.properties` */
    static File propertiesFile;

    /** File `settings.gradle` is required to look up `aapt2` */
    static File settingsFile;

    /** File `build.gradle` */
    static File projectBuildFile;
    static File rootBuildFile;

    /** Directory `credentials` */
    static File credentials;

    static String apiConfig;

    /** Directory `src` */
    static File src;

    /** Directory `src/main/java` */
    static File srcMain;

    /** File `src/main/AndroidManifest.xml` */
    static File manifestMain;

    /** Directory `src/huaweiDebug/java` */
    static File srcDebug;

    /** Directory `src/huaweiRelease/java` */
    static File srcRelease;

    /**
     * The configuration JSON string for debug builds;
     * copied from local file (GitHub secrets being inserted).
     */
    static String appConfigDebug;

    /**
     * The configuration JSON string for release builds;
     * copied from local file (GitHub secrets being inserted).
     */
    static String appConfigRelease;

    static String artifactName = "agconnect-publishing-gradle-plugin";
    static String artifactVersion = "7.2.1.8";

    /** These config strings are being copied from the reference project */
    static void init() {
        apiConfig = readFile(getProjectRootPath() + "credentials" +
                File.separator + "agc-apiclient.json");
        appConfigRelease = readFile(getProjectRootPath() + "mobile" + File.separator + "src" +
                File.separator + "huaweiRelease" + File.separator + "agconnect-services.json");
        appConfigDebug = readFile(getProjectRootPath() + "mobile" + File.separator + "src" +
                File.separator + "huaweiDebug" + File.separator + "agconnect-services.json");
    }

    /**
     * Generate the configuration files required to test the plugin, which are: `build.gradle`,
     * `settings.gradle`, `agconnect-services.json`, `agc-apiclient.json` and `AndroidManifest.xml`.
     *
     * @see <a href="https://github.com/gradle/gradle/blob/master/subprojects/core/src/main/java/org/gradle/api/internal/initialization/DefaultScriptHandler.java">DefaultScriptHandler.java</a>
     */
    @BeforeAll
    static void setup() {

        init();

        /* GitHub Test Project `applicationId` */
        if (System.getenv().containsKey("CI")) {
            if (System.getenv().containsKey("AGC_PACKAGE_ID")) {
                packageId = System.getenv("AGC_PACKAGE_ID");
            }
            File projectDir = new File(getProjectRootPath());
            if (projectDir.exists() || projectDir.mkdir()) {
                generateProject(projectDir);
            }
        } else {
            generateProject(testProject);
            log(readFile(settingsFile.getAbsolutePath()));
            log(readFile(rootBuildFile.getAbsolutePath()));
            log(readFile(projectBuildFile.getAbsolutePath()));
        }
    }

    /**
     * Locally the root directory is a temporary directory
     * and on GitHub it is the workspace root.
     */
    static void generateProject(@NotNull File projectDir) {

        /* Locally: Copy `buildSrc/build/libs/*.jar` to temporary project `libs` directory */
        if (! System.getenv().containsKey("CI")) {
            File libsDir = new File(projectDir, "libs");
            if (libsDir.exists() || libsDir.mkdir()) {
                String jarFile = "libs" + File.separator + artifactName + "-" + artifactVersion + ".jar";
                File libs = new File(System.getProperty("user.dir") +  File.separator + "build" + File.separator + jarFile);
                copyDirectory(libs, new File(projectDir, jarFile));
            }
        }

        /* File `keystore.properties` */
        propertiesFile = new File(projectDir, "keystore.properties");
        writeFile(propertiesFile, readFile(getProjectRootPath() + File.separator + "keystore.properties"), false);

        /* File `credentials/agc-apiclient.json` */
        credentials = new File(projectDir, "credentials");
        if (credentials.exists() || credentials.mkdir()) {
            writeFile(new File(credentials, "agc-apiclient.json"), apiConfig, false);
        }

        /* Generic: root build.gradle */
        rootBuildFile = new File(projectDir, Project.DEFAULT_BUILD_FILE);
        writeFile(rootBuildFile, getBuildScriptString() + getKeystorePropertiesString(), false);

        /* Generic: settings.gradle */
        settingsFile = new File(projectDir, "settings.gradle");
        writeFile(settingsFile, getSettingsString(), false);

        /* Generic: `mobile/src` */
        File mobile = new File(projectDir, "mobile");
        if (mobile.exists() || mobile.mkdir()) {
            src = new File(mobile, "src");
            if (src.exists() || src.mkdir()) {

                /* Generic: `src/main/AndroidManifest.xml` */
                srcMain = new File(src, "main");
                if (srcMain.exists() || srcMain.mkdir()) {
                    manifestMain = new File(srcMain, "AndroidManifest.xml");
                    if (! manifestMain.exists()) {
                        writeFile(manifestMain, getManifestString(), false);
                    }
                }

                /* Vendor: `src/huaweiDebug/agconnect-services.json` */
                srcDebug = new File(src, "huaweiDebug");
                if (srcDebug.exists() || srcDebug.mkdir()) {
                    File configDebug = new File(srcDebug, "agconnect-services.json");
                    if (! configDebug.exists()) { // file may already be present
                        writeFile(configDebug, appConfigDebug, false);
                    }
                }

                /* Vendor: `src/huaweiRelease/agconnect-services.json` */
                srcRelease = new File(src, "huaweiRelease");
                if (srcRelease.exists() || srcRelease.mkdir()) {
                    File configRelease = new File(srcRelease, "agconnect-services.json");
                    if (! configRelease.exists()) { // file may already be present
                        writeFile(configRelease, appConfigRelease, false);
                    }
                }

                /* Generic: module build.gradle */
                projectBuildFile = new File(src, Project.DEFAULT_BUILD_FILE);
                if (! projectBuildFile.exists()) {
                    writeFile(projectBuildFile,
                        "apply plugin: \"com.android.application\"\n" +
                                "apply plugin: \"com.huawei.agconnect\"\n" +
                                "apply plugin: \"io.syslogic.agconnect.publishing\"\n\n" +
                                "android {\n" +
                                "    compileSdk 32\n" +
                                "    defaultConfig {\n" +
                                "        minSdk 23\n" +
                                "        targetSdk 32\n" +
                                "        applicationId \"" + packageId + "\"\n" +
                                "        versionName \"1.0.0\"\n" +
                                "        versionCode 1\n" +
                                "    }\n" +
                                "    signingConfigs {\n" +
                                "        debug {\n" +
                                "            storeFile file(\"" + getDebugKeystorePath() + "\")\n" +
                                "            storePassword rootProject.ext.get(\"debugKeystorePass\")\n" +
                                "            keyAlias rootProject.ext.get(\"debugKeyAlias\")\n" +
                                "            keyPassword rootProject.ext.get(\"debugKeyPass\")\n" +
                                "        }\n" +
                                "        release {\n" +
                                "            storeFile file(\"" + getUploadKeystorePath() + "\")\n" +
                                "            storePassword rootProject.ext.get(\"releaseKeystorePass\")\n" +
                                "            keyAlias rootProject.ext.get(\"releaseKeyAlias\")\n" +
                                "            keyPassword rootProject.ext.get(\"releaseKeyPass\")\n" +
                                "        }\n"+
                                "    }\n" +
                                "    sourceSets {\n" +
                                "        main {}\n" +
                                "        huawei {\n" +
                                "            java.srcDir \"src/huawei/java\"\n" +
                                "        }\n" +
                                "    }\n" +
                                "    flavorDimensions \"vendor\"\n" +
                                "    productFlavors {\n" +
                                "        huawei {\n" +
                                "            dimension \"vendor\"\n" +
                                "            versionNameSuffix \"-huawei\"\n" +
                                "        }\n" +
                                "    }\n" +
                                "    buildTypes {\n" +
                                "        debug {\n" +
                                "            signingConfig signingConfigs.debug\n" +
                                "            applicationIdSuffix \".debug\"\n" +
                                "            debuggable true\n" +
                                "            jniDebuggable true\n" +
                                "            zipAlignEnabled true\n" +
                                "            renderscriptDebuggable true\n" +
                                "            pseudoLocalesEnabled false\n" +
                                "            shrinkResources false\n" +
                                "            minifyEnabled false\n" +
                                "        }\n" +
                                "        release {\n" +
                                "            signingConfig signingConfigs.release\n" +
                                "            shrinkResources true\n" +
                                "            testCoverageEnabled false\n" +
                                "            zipAlignEnabled true\n" +
                                "            pseudoLocalesEnabled false\n" +
                                "            renderscriptDebuggable false\n" +
                                "            minifyEnabled true\n" +
                                "            jniDebuggable false\n" +
                                "            debuggable false\n" +
                                "        }\n"+
                                "    }\n"+
                                "}\n\n" + // `android`
                                "dependencies {\n" +
                                "}\n\n" +
                                "agcPublishing {\n" +
                                "}\n",false);
                }
            }
        }
    }

    @NotNull
    static String getSettingsString() {
        return
                "\nimport org.gradle.api.initialization.resolve.RepositoriesMode\n\n" +
                "pluginManagement {\n" +
                "    repositories {\n" +
                "        gradlePluginPortal()\n" +
                "        google()\n"  +
                "        mavenCentral()\n" +
                "        maven { url \"https://developer.huawei.com/repo/\" }\n" +
                "        flatDir { dirs \"libs\" }\n" +
                "    }\n" +
                "}\n" +
                "dependencyResolutionManagement {\n" +
                "    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)\n" +
                "    repositories {\n" +
                "        google()\n" +
                "        mavenCentral()\n" +
                "        maven { url \"https://developer.huawei.com/repo/\" }\n" +
                "    }\n" +
                "}\n" +
                "rootProject.name = \"PublishingPlugin\"\n" +
                "include \":mobile\"\n";
    }

    @NotNull
    static String getBuildScriptString() {
        return
                "buildscript {\n" +
                "    repositories {\n" +
                "        google()\n" +
                "        mavenCentral()\n" +
                "        maven { url \"https://developer.huawei.com/repo/\" }\n" +
                "        mavenLocal()\n" +
                "        flatDir { dirs \"libs\" }\n" +
                "    }\n" +
                "    dependencies {\n" +
                "        classpath \"com.android.tools.build:gradle:7.2.1\"\n" +
                "        classpath \"com.huawei.agconnect:agcp:1.7.0.300\"\n" +
                "        classpath \"io.syslogic:agconnect-publishing-gradle-plugin:7.2.1.8\"\n" +
                "    }\n" +
                "}\n\n";
    }

    @NotNull
    static String getKeystorePropertiesString() {
        return
            "if (rootProject.file('keystore.properties').exists()) {\n" +
            "    def is = new FileInputStream(rootProject.file('keystore.properties'))\n" +
            "    def keystore = new Properties()\n" +
            "    keystore.load(is)\n" +
            "    project.ext.set('debugKeystorePass',   keystore['debugKeystorePass'])\n" +
            "    project.ext.set('debugKeyAlias',       keystore['debugKeyAlias'])\n" +
            "    project.ext.set('debugKeyPass',        keystore['debugKeyPass'])\n" +
            "    project.ext.set('releaseKeystorePass', keystore['releaseKeystorePass'])\n" +
            "    project.ext.set('releaseKeyAlias',     keystore['releaseKeyAlias'])\n" +
            "    project.ext.set('releaseKeyPass',      keystore['releaseKeyPass'])\n" +
            "    is.close()\n" +
            "}\n\n";
    }

    @NotNull
    static String getManifestString() {
        return
                "<?xml version='1.0' encoding='utf-8'?>\n"  +
                "<manifest\n" +
                "    xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package='" + packageId + "'>\n" +
                "    <application android:hasCode=\"false\">\n" +
                "    </application>\n" +
                "</manifest>\n";
    }

    @Nullable
    @SuppressWarnings("SameParameterValue")
    BuildResult runTask(String arguments) {
        File projectDir = System.getenv().containsKey("CI") ?
                new File(getProjectRootPath()) : testProject;
        BuildResult result = null;
        try {
            GradleRunner runner = GradleRunner.create()
                    .withProjectDir(projectDir)
                    .withArguments(arguments)
                    .withPluginClasspath();
            if (!System.getenv().containsKey("CI")) {
                runner.withDebug(true).forwardOutput();
            }
            result = runner.build();
        } catch (UnexpectedBuildResultException e) {
            System.err.println(">> " + e.getBuildResult().getOutput());
            System.err.println(e.getMessage());
        }
        return result;
    }

    @NotNull
    static String readFile(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8);
            stream.forEach(s -> sb.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    static void writeFile(@NotNull File file, @NotNull String data, @SuppressWarnings("SameParameterValue") boolean append) {
        try (PrintWriter p = new PrintWriter(new FileOutputStream(file.getAbsolutePath(), append))) {
            p.println(data);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    static void copyDirectory(@NotNull File source, @NotNull File destination) {
        try (
            InputStream in = new BufferedInputStream(new FileInputStream(source));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(destination))
        ) {
            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    static String getProjectRootPath() {
        if (System.getenv().containsKey("CI")) {
            return new File(System.getenv().get("GITHUB_WORKSPACE")).getAbsolutePath() + File.separator;
        } else {
            return System.getProperty("user.dir").replace("buildSrc", "");
        }
    }

    @NotNull
    @SuppressWarnings("unused")
    static String getOutputDirectoryPath() {
        String value = System.getProperty("user.dir") + File.separator + "build" + File.separator + "libs";
        return value.replace("\\", "\\\\");
    }

    @NotNull
    static String getDebugKeystorePath() {
        String value = System.getProperty("user.home") + File.separator + ".android" + File.separator + "debug.keystore";
        return value.replace("\\", "\\\\");
    }

    @NotNull
    static String getUploadKeystorePath() {
        String value = System.getProperty("user.home") + File.separator + ".android" + File.separator + "upload.keystore";
        return value.replace("\\", "\\\\");
    }

    static void log(@NotNull String data) {
        if (! System.getenv().containsKey("CI")) {
            System.out.println(data);
        }
    }
}
