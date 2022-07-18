package io.syslogic.agconnect;

import com.android.build.api.dsl.ApkSigningConfig;
import com.android.build.api.dsl.ApplicationBuildType;
import com.android.build.api.dsl.ApplicationExtension;

import org.apache.commons.lang3.StringUtils;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;

import io.syslogic.agconnect.constants.ArtifactType;
import io.syslogic.agconnect.task.AppInfoTask;
import io.syslogic.agconnect.task.AppIdTask;
import io.syslogic.agconnect.task.PublishingTask;

/**
 * Huawei AppGallery Connect Publishing Plugin
 *
 * @see <a href="https://developer.huawei.com/consumer/en/service/josp/agc/index.html">Console</a>
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
class PublishingPlugin implements Plugin<Project> {

    private @Nullable String apiConfigFile = null;
    private @NotNull Boolean logHttp = false;
    private @NotNull Boolean verbose = false;

    private @Nullable PublishingExtension extension;
    private @NotNull final String taskGroup = "agconnect";

    /** It depends on :assembleRelease or :bundleRelease */
    @Override
    public void apply(@NotNull Project project) {

        /* Check Preconditions. */
        if (! preconditionsMet(project)) {return;}

        /* Create project extension `agcPublishing`. */
        this.extension = project.getExtensions().create("agcPublishing", PublishingExtensionImpl.class);

        /* Apply the default path for the API client configuration file. */
        this.apiConfigFile = project.getRootProject().getProjectDir().getAbsolutePath() +
                File.separator + "credentials" + File.separator + "agc-apiclient.json";

        /* Project after evaluate. */
        project.afterEvaluate(it -> {

            /* Loop build-types which have a signing-config. */
            for (String buildType : getBuildTypes(project)) {

                /* Loop artifact-types APK & AAB. */
                for (String artifactType : new String[] {ArtifactType.APK, ArtifactType.AAB}) {

                    /* When file `agconnect-services.json` is also present. */
                    String appConfigFile = getAppConfigPath(project, buildType);
                    if (new File(appConfigFile).exists()) {

                        /* Apply values provided by the PublishingExtension. */
                        if (! extension.getApiConfigFile().isEmpty()) {
                            if (! new File(extension.getApiConfigFile()).exists()) {
                                System.err.println("Config file not found: " + extension.getApiConfigFile());
                                System.err.println("Keeping default value: " + apiConfigFile);
                            } else {
                                apiConfigFile = extension.getApiConfigFile();
                            }
                        }
                        if (extension.getLogHttp()) {logHttp = extension.getLogHttp();}
                        if (extension.getVerbose()) {verbose = extension.getVerbose();}

                        /* Register Tasks: Publish */
                        String taskName = "publish" + StringUtils.capitalize(buildType) + StringUtils.capitalize(artifactType);
                        project.getTasks().register(taskName, PublishingTask.class, task -> {
                            task.setGroup(taskGroup);
                            task.getApiConfigFile().set(apiConfigFile);
                            task.getAppConfigFile().set(appConfigFile);
                            task.getArtifactType().set(artifactType);
                            task.getBuildType().set(buildType);
                            task.getLogHttp().set(logHttp);
                            task.getVerbose().set(verbose);

                            /* This causes publish to depend on assemble or bundle. */
                            String buildTask = getBuildTask(project, artifactType, buildType);
                            task.dependsOn(buildTask);
                        });

                        /* Register Tasks: AppInfo */
                        taskName = "getAppInfo" + StringUtils.capitalize(buildType);
                        if (project.getTasks().findByName(taskName) == null) {
                            String finalApiConfigFile1 = apiConfigFile;
                            project.getTasks().register(taskName, AppInfoTask.class, task -> {
                                task.setGroup(taskGroup);
                                task.getApiConfigFile().set(finalApiConfigFile1);
                                task.getAppConfigFile().set(appConfigFile);
                                task.getBuildType().set(buildType);
                                task.getLogHttp().set(logHttp);
                                task.getVerbose().set(verbose);
                            });
                        }

                        /* Register Tasks: AppId */
                        taskName = "getAppId" + StringUtils.capitalize(buildType);
                        if (project.getTasks().findByName(taskName) == null) {
                            String finalApiConfigFile1 = apiConfigFile;
                            project.getTasks().register(taskName, AppIdTask.class, task -> {
                                task.setGroup(taskGroup);
                                task.getApiConfigFile().set(finalApiConfigFile1);
                                task.getAppConfigFile().set(appConfigFile);
                                task.getBuildType().set(buildType);
                                task.getLogHttp().set(logHttp);
                                task.getVerbose().set(verbose);
                            });
                        }
                    }
                }
            }
        });
    }

    /** Obtain Android ApplicationBuildType, which have a ApkSigningConfig. */
    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    private String[] getBuildTypes(@NotNull Project project) {
        ArrayList<String> buildTypes = new ArrayList<>();
        ApplicationExtension android = (ApplicationExtension) project.getExtensions().getByName("android");
        for (ApplicationBuildType buildType : android.getBuildTypes()) {
            ApkSigningConfig asc = buildType.getSigningConfig();
            if (asc != null) {
                // String path = "android.buildTypes." + buildType.getName() + ".signingConfig";
                if (asc.getStoreFile()     == null || !asc.getStoreFile().exists()    ) {continue;}
                if (asc.getStorePassword() == null || asc.getStorePassword().isEmpty()) {continue;}
                if (asc.getKeyPassword()   == null || asc.getKeyPassword().isEmpty()  ) {continue;}
                if (asc.getKeyAlias()      == null || asc.getKeyAlias().isEmpty()     ) {continue;}
                buildTypes.add(buildType.getName());
                // System.out.println(path + " OK");
            }
        }
        return buildTypes.toArray(new String[0]);
    }

    /** Check if Android and AGConnect Gradle plugins were loaded. */
    public boolean preconditionsMet(@NotNull Project project) {
        if (! project.getPluginManager().hasPlugin("com.android.application") || ! project.getPluginManager().hasPlugin("com.huawei.agconnect")) {
            if (! project.getPluginManager().hasPlugin("com.android.application")) {
                System.err.println("Plugin 'agconnect-publishing' depends on 'com.android.application'.");
            }
            if (! project.getPluginManager().hasPlugin("com.huawei.agconnect")) {
                System.err.println("Plugin 'agconnect-publishing' depends on 'com.huawei.agconnect'.");
            }
            return false;
        }
        return true;
    }

    @NotNull
    private String getAppConfigPath(@NotNull Project project, @NotNull String buildVariant) {
        String basePath = project.getProjectDir().getAbsolutePath() + File.separator;
        return basePath + "src" + File.separator + buildVariant + File.separator + "agconnect-services.json";
    }

    @NotNull
    private String getBuildTask(@NotNull Project project, @NotNull String artifactType, @NotNull String buildVariant) {
        String task = "assembleRelease";
        if (artifactType.equals(ArtifactType.AAB)) {task = "bundle" + StringUtils.capitalize(buildVariant);}
        else if (artifactType.equals(ArtifactType.APK)) {task = "assemble" + StringUtils.capitalize(buildVariant);}
        return ":" + project.getName() + ":" + task;
    }
}
