package io.syslogic.agconnect;

import org.apache.commons.lang3.StringUtils;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import io.syslogic.agconnect.task.AppInfoTask;
import io.syslogic.agconnect.task.PublishingTask;

/**
 * Huawei AppGallery Connect Publishing Plugin
 *
 * @see <a href="https://developer.huawei.com/consumer/en/service/josp/agc/index.html">Console</a>
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
class PublishingPlugin implements Plugin<Project> {

    @SuppressWarnings("FieldCanBeLocal")
    private @NotNull final String[] buildVariants = new String[]{"main", "debug", "release"};
    private @NotNull final String[] artifactTypes = new String[]{"apk", "aab"};
    private @NotNull final String taskGroup = "agconnect";
    private @Nullable PublishingExtension extension;

    /** It depends on :assembleRelease or :bundleRelease */
    @Override
    public void apply(@NotNull Project project) {

        /* Preconditions & Create Extension */
        if (! preconditionsMet(project)) {return;}
        else {this.createExtension(project);}

        /* Register tasks, when file `agconnect-services.json` is present */
        for (String artifactType : this.artifactTypes) {
            for (String buildVariant : this.buildVariants) {

                boolean verbose = true;

                String basePath = project.getProjectDir().getAbsolutePath() + File.separator;
                String appConfigFile = basePath + "src" + File.separator + buildVariant + File.separator + "agconnect-services.json";
                if (extension != null && new File(appConfigFile).exists()) {

                    String taskName = "publish" + StringUtils.capitalize(buildVariant) + StringUtils.capitalize(artifactType);
                    if (buildVariant.equals("main")) {taskName = "publish" + StringUtils.capitalize(artifactType);}
                    // System.out.println("Found " + appConfig + ", registering task :" + taskName + ".");

                    // TODO
                    String apiConfigFile = project.getRootProject().getProjectDir().getAbsolutePath() +
                            File.separator + "credentials" + File.separator + "agc-apiclient.json";

                    // always false ...
                    if (extension.getApiConfigFile().isPresent()) {
                        apiConfigFile = extension.getApiConfigFile().get();
                    }

                    // always false ...
                    if (extension.getVerbose().isPresent()) {
                        verbose = extension.getVerbose().get();
                    }

                    String finalApiConfigFile = apiConfigFile;
                    boolean finalVerbose = verbose;
                    project.getTasks().register(taskName, PublishingTask.class, task -> {
                        task.setGroup(this.taskGroup);
                        task.getApiConfigFile().set(finalApiConfigFile);
                        task.getAppConfigFile().set(appConfigFile);
                        task.getArtifactType().set(artifactType);
                        task.getBuildType().set(buildVariant);
                        task.getVerbose().set(finalVerbose);
                        task.dependsOn(getBuildTask(artifactType, buildVariant));
                    });

                    taskName = "appInfo" + StringUtils.capitalize(buildVariant);
                    if (project.getTasks().findByName(taskName) == null) {
                        String finalApiConfigFile1 = apiConfigFile;
                        project.getTasks().register(taskName, AppInfoTask.class, task -> {
                            task.setGroup(this.taskGroup);
                            task.getApiConfigFile().set(finalApiConfigFile1);
                            task.getAppConfigFile().set(appConfigFile);
                            task.getBuildType().set(buildVariant);
                            task.getVerbose().set(finalVerbose);
                        });
                    }
                }
            }
        }
    }

    @NotNull
    private String getBuildTask(@NotNull String artifactType, @NotNull String buildVariant) {
        switch (artifactType) {
            case "aab": return "bundle" + StringUtils.capitalize(buildVariant);
            case "apk": return "assemble" + StringUtils.capitalize(buildVariant);
            default: return "assembleRelease";
        }
    }

    /** Create project extension `agcPublishing` */
    protected void createExtension(@NotNull Project project) {
        extension = project.getExtensions().create("agcPublishing", PublishingExtension.class);
        // extension.getVerbose().set(true);
    }

    /** Check if Android and AGConnect Gradle plugins were loaded. */
    public boolean preconditionsMet(@NotNull Project project) {
        if (! project.getPluginManager().hasPlugin("com.android.application") || ! project.getPluginManager().hasPlugin("com.huawei.agconnect")) {
            if (! project.getPluginManager().hasPlugin("com.android.application")) {
                System.err.println("Plugin 'agconnect-publishing' depends on 'com.android.application'");
            }
            if (! project.getPluginManager().hasPlugin("com.huawei.agconnect")) {
                System.err.println("Plugin 'agconnect-publishing' depends on 'com.huawei.agconnect'");
            }
            return false;
        }
        return true;
    }
}
