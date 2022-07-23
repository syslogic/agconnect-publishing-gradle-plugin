package io.syslogic.agconnect;

import org.gradle.internal.impldep.junit.framework.TestCase;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Test;

/**
 * Publishing {@link TestCase}
 *
 * @author Martin Zeitler
 */
class PublishingTest extends BaseTestCase {

    @Test
    public void getAppIdTest() {
        BuildResult result = getBuildResult("getAppIdDebug");
        assertNotNull(result.getOutput());
    }
}
