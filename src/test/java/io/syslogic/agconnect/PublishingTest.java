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
    public void helpTest() {
        BuildResult result = runTask(":welp");
        // assertTrue(true);
        assertNotNull(result);
    }
}
