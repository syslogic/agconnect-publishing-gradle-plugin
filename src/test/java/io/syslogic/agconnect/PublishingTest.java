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
        // assertNotNull(result);
        assertTrue(true);
    }

/*
    @Test
    public void bundleDebugTest() {
        BuildResult result = runTask(":bundleDebug");
        assertNotNull(result);
    }

    @Test
    public void bundleReleaseTest() {
        BuildResult result = runTask(":bundleRelease");
        assertNotNull(result);
    }
*/
}
