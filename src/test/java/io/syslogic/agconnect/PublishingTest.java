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
    public void getAppIdTest() {
        BuildResult result = getBuildResult("assembleDebug");
        assertNotNull(result.getOutput());
    }
}
