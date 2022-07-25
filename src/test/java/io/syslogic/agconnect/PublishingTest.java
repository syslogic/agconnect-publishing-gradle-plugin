package io.syslogic.agconnect;

import org.gradle.testkit.runner.BuildResult;

import org.junit.jupiter.api.Test;

/**
 * Publishing {@link BaseTestCase}
 *
 * @author Martin Zeitler
 */
class PublishingTest extends BaseTestCase {

    @Test
    public void buildEnvironmentTest() {
        BuildResult result = runTask(":mobile:buildEnvironment");
        // assertTrue(true);
        assertNotNull(result);
    }
}
