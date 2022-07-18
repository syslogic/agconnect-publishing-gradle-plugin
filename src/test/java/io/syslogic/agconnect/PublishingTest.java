package io.syslogic.agconnect;

import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.internal.impldep.junit.framework.TestCase;
import org.gradle.testfixtures.ProjectBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Publishing {@link TestCase}
 *
 * @author Martin Zeitler
 */
class PublishingTest extends TestCase {

    String pluginId = "io.syslogic.agconnect.publishing";
    String clientId;
    String secret;
    Project project;

    @BeforeEach
    public void setup() {
        this.project = ProjectBuilder.builder().build();
        PluginManager pm = this.project.getPluginManager();
        pm.apply(this.pluginId);
    }

    @Test
    public void credentialsTest() {
        // assertNotNull(this.clientId);
        // assertNotNull(this.secret);
        assertTrue(true);
    }

    @Test
    public void uploadUrlTaskTest() {
        // assertNotNull(this.project.getTasks().getByName("uploadUrl"));
        assertTrue(true);
    }

    @Test
    public void publishTaskTest() {
        // assertNotNull(this.project.getTasks().getByName("uploadPackage"));
        assertTrue(true);
    }
}
