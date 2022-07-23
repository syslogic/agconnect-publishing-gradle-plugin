package io.syslogic.agconnect;

import org.gradle.api.Project;
import org.gradle.internal.impldep.junit.framework.TestCase;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

/**
 * Publishing {@link TestCase}
 *
 * @author Martin Zeitler
 */
class PublishingTest extends TestCase {

    String identifier = "io.syslogic.agconnect.publishing";
    @TempDir File testProjectDir;
    File buildFile;

    Project project;

    String clientId;
    String secret;

    @BeforeEach
    public void setup() {
        this.buildFile = new File(testProjectDir, "build.gradle");
        try {
            //noinspection deprecation
            FileUtils.writeStringToFile(this.buildFile, "plugins {" +
                "id 'com.android.application'" +
                "id 'com.huawei.agconnect'" +
                "id '" + this.identifier + "'" +
            "}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void publishReleaseAabTest() {
        BuildResult result = GradleRunner.create()
                .withProjectDir(this.testProjectDir)
                .withArguments(":mobile:publishReleaseAab")
                .withPluginClasspath()
                .build();

        assertNotNull(result.getOutput());
    }
}
