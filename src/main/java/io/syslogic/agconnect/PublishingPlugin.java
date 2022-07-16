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

    private @Nullable PublishingExtension extension = null;
    private @Nullable String apiConfigFile = null;
    private @NotNull Boolean verbose = false;

    /** It depends on :assembleRelease or :bundleRelease */
    @Override
    public void apply(@NotNull Project project) {

        /* Check Preconditions */
        if (! preconditionsMet(project)) {return;}

        /* Create project extension `agcPublishing` */
        this.extension = project.getExtensions().create("agcPublishing", PublishingExtensionImpl.class);
        this.apiConfigFile = project.getRootProject().getProjectDir().getAbsolutePath() +
                File.separator + "credentials" + File.separator + "agc-apiclient.json";

        /* Project after evaluate */
        project.afterEvaluate(it -> {

            /* Register tasks, when file `agconnect-services.json` is present */
            for (String artifactType : this.artifactTypes) {
                for (String buildVariant : this.buildVariants) {

                    String basePath = project.getProjectDir().getAbsolutePath() + File.separator;
                    String appConfigFile = basePath + "src" + File.separator + buildVariant + File.separator + "agconnect-services.json";
                    if (new File(appConfigFile).exists()) {

                        String taskName = "publish" + StringUtils.capitalize(buildVariant) + StringUtils.capitalize(artifactType);
                        if (buildVariant.equals("main")) {taskName = "publish" + StringUtils.capitalize(artifactType);}

                        if (! extension.getApiConfigFile().isEmpty()) {apiConfigFile = extension.getApiConfigFile();}
                        if (extension.getVerbose()) {verbose = extension.getVerbose();}
                        // System.out.println("Found " + appConfig + ", registering task :" + taskName + ".");

                        project.getTasks().register(taskName, PublishingTask.class, task -> {
                            task.setGroup(this.taskGroup);
                            task.getApiConfigFile().set(apiConfigFile);
                            task.getAppConfigFile().set(appConfigFile);
                            task.getArtifactType().set(artifactType);
                            task.getBuildType().set(buildVariant);
                            task.getVerbose().set(verbose);

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
                                task.getVerbose().set(verbose);
                            });
                        }
                    }
                }
            }

        });
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

    @NotNull
    private String getBuildTask(@NotNull String artifactType, @NotNull String buildVariant) {
        switch (artifactType) {
            case "aab": return "bundle" + StringUtils.capitalize(buildVariant);
            case "apk": return "assemble" + StringUtils.capitalize(buildVariant);
            default: return "assembleRelease";
        }
    }
}
