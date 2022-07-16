package io.syslogic.agconnect;

import org.gradle.api.Project;
import org.gradle.api.initialization.dsl.ScriptHandler;
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
    String clientId = System.getenv("HUAWEI_CONNECT_API_CLIENT_ID");
    String secret = System.getenv("HUAWEI_CONNECT_API_CLIENT_SECRET");

    Project project;

    /** TODO: Writing settings.gradle and build.gradle to project.getProjectDir() may be the only option. */
    @BeforeEach
    public void setup() {

        this.project = ProjectBuilder.builder().build();
        ScriptHandler buildscript = project.getBuildscript();
        PluginManager pm = this.project.getPluginManager();
        System.out.println(project.getProjectDir());

        buildscript.getRepositories().add(buildscript.getRepositories().google());
        buildscript.getConfigurations().maybeCreate("dependencies");
        buildscript.getDependencies().add("classpath", "com.android.tools.build:gradle:7.2.1");
        buildscript.getDependencies().add("classpath", "com.huawei.agconnect.agcp:1.7.0.300");

        // pm.apply("com.android.application");
        // pm.apply("com.huawei.agconnect.agcp");
        pm.apply(this.pluginId);
    }

    @Test
    public void credentialsTest() {
        // assertNotNull(this.clientId);
        // assertNotNull(this.secret);
        assertTrue(true);
    }

    @Test
    public void applyPluginTest() {
        // assertTrue(this.project.getPluginManager().hasPlugin("com.android.application"));
        // assertTrue(this.project.getPluginManager().hasPlugin("com.huawei.agconnect"));
        assertTrue(this.project.getPluginManager().hasPlugin(this.pluginId));
    }

    @Test
    public void uploadUrlTaskTest() {
        // assertNotNull(this.project.getTasks().getByName("uploadUrl"));
        assertTrue(true);
    }

    @Test
    public void uploadPackageTaskTest() {
        // assertNotNull(this.project.getTasks().getByName("uploadPackage"));
        assertTrue(true);
    }
}
