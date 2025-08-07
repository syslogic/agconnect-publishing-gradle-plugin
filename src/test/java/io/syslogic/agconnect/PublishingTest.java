package io.syslogic.agconnect;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * AGConnect Publishing Test
 * @author Martin Zeitler
 */
class PublishingTest {

    GradleRunner runner;
    List<String> buildArgs = List.of(":build");

    /**
     * Setting up {@link GradleRunner}, when running from directory `buildSrc`.
     * Eventually could also copy to @tmpDir and then inject the configuration.
     */
    @BeforeEach
    public void setup() {
        if (new File("").getAbsoluteFile().getName().equals("buildSrc")) {
            this.runner = GradleRunner.create()
                    .withProjectDir(new File("../"))
                    .withPluginClasspath()
                    .withDebug(true)
                    .forwardOutput();
        } else {
            // This needs a Huawei Android project to test with.
        }
    }

    @Test
    public void testLibraryBuild() {
        BuildResult result = this.runner.withArguments(this.buildArgs).build();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getOutput().contains("BUILD SUCCESSFUL"));
    }

    private void writeFile(File destination, String content) throws IOException {
        try (BufferedWriter output = new BufferedWriter(new FileWriter(destination))) {
            output.write(content);
        }
    }
}
