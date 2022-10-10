package io.syslogic.agconnect;

import com.android.build.api.dsl.ApkSigningConfig;
import com.android.build.api.dsl.ApplicationBuildType;
import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.dsl.ProductFlavor;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import io.syslogic.agconnect.constants.ArtifactType;
import io.syslogic.agconnect.task.AppInfoTask;
import io.syslogic.agconnect.task.AppIdTask;
import io.syslogic.agconnect.task.HelpTask;
import io.syslogic.agconnect.task.PublishingTask;
import io.syslogic.agconnect.util.StringUtils;

/**
 * Huawei AppGallery Connect Publishing Plugin
 *
 * @see <a href="https://developer.huawei.com/consumer/en/service/josp/agc/index.html">Console</a>
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
class PublishingPlugin implements Plugin<Project> {

    private @NotNull final String taskGroup = "agconnect";
    private @Nullable PublishingExtension extension;
    private @Nullable String configFile = null;
    private @NotNull Boolean logHttp = false;
    private @NotNull Boolean verbose = false;

    /** It depends on :assembleRelease or :bundleRelease */
    @Override
    public void apply(@NotNull Project project) {

        /* Create project extension `agcPublishing`. */
        this.extension = project.getExtensions().create("agcPublishing", PublishingExtensionImpl.class);

        /* Apply the default path for the API client configuration file. */
        this.configFile = project.getRootProject().getProjectDir().getAbsolutePath() +
                File.separator + "distribution" + File.separator + "agc-apiclient.json";

        /* Project before evaluate: register task `:welp` */
        registerHelpTask(project,"welp");

        /* Project after evaluate. */
        project.afterEvaluate(it -> {

            /*
             * Apply buildTypes.release.signingConfig to buildTypes.debug.signingConfig.
             * This may permit uploading debug AAB. TODO: this should be configurable.
             */
            // setDebugSigningConfig(project);

            /* Loop product flavors and build-types. */
            String[] buildTypes = getBuildTypes(project);
            String[] productFlavors = getProductFlavors(project);
            String taskName;

            if (extension.getLogHttp()) {logHttp = extension.getLogHttp();}
            if (extension.getVerbose()) {verbose = extension.getVerbose();}

            /* TODO: consider absent productFlavors */
            if (productFlavors.length == 0) {

                System.out.println("\nNo product flavors detected.");

                /* Loop build-types. */
                for (String buildType : buildTypes) {

                    /* Loop artifact-types APK & AAB. */
                    for (String artifactType : new String[] {ArtifactType.APK, ArtifactType.AAB}) {

                        /* Check if file `agconnect-services.json` is present. */
                        String appConfigFile = getAppConfigPath(project, buildType);
                        if (appConfigFile != null) {

                            int releaseType = 1; // 1 is the default and the are only 2 possible values.
                            if (extension.getReleaseType() != null && extension.getReleaseType() == 5) {
                                releaseType = extension.getReleaseType();
                            }

                            /* Apply values provided by the PublishingExtension. */
                            if (! extension.getConfigFile().isEmpty()) {
                                if (! new File(extension.getConfigFile()).exists()) {
                                    System.err.println("AGConnect API config not found: " + extension.getConfigFile());
                                    System.err.println("Reverting to the default value: " + configFile);
                                } else {
                                    configFile = extension.getConfigFile();
                                    if (verbose) {System.out.println("AGConnect API config: " + configFile);}
                                }
                            }

                            /* Task :publishDebugAab always fails, because the AAB is not signed with the upload key. */
                            if (!artifactType.equals(ArtifactType.AAB) || !buildType.equals("debug")) {

                                /* Register Tasks: Publish. */
                                taskName = "publish" + StringUtils.capitalize(artifactType);
                                registerPublishingTask(project, taskName, appConfigFile, artifactType, buildType, null, null, releaseType);
                            }

                            /* Register Tasks: AppInfo */
                            taskName = "getAppInfo" + StringUtils.capitalize(buildType);
                            registerAppInfoTask(project, taskName, appConfigFile, buildType);

                            /* Register Tasks: AppId */
                            taskName = "getAppId" + StringUtils.capitalize(buildType);
                            registerAppIdTask(project, taskName, appConfigFile, buildType);

                        } else if (verbose) {
                            System.out.println("config not found for: " + artifactType);
                        }
                    }
                }

            } else {

                System.out.println("\n> " + productFlavors.length + " product flavors detected: " + stringArrayToCsv(productFlavors));

                /* Loop product flavors and build-types. */
                for (String productFlavor : productFlavors) {
                    for (String buildType : buildTypes) {

                        /* Loop the variants per each single flavor and build-type. */
                        String[] variants = getVariants(new String[] {productFlavor}, new String[] {buildType});
                        for (String buildVariant : variants) {

                            /* Loop artifact-types APK & AAB. */
                            for (String artifactType : new String[] {ArtifactType.APK, ArtifactType.AAB}) {

                                /* Check if file `agconnect-services.json` is present. */
                                String appConfigFile = getAppConfigPath(project, productFlavor, buildType, buildVariant);
                                if (appConfigFile != null) {

                                    int releaseType = 1; // 1 is the default and the are only 2 possible values.
                                    if (extension.getReleaseType() != null && extension.getReleaseType() == 5) {
                                        releaseType = extension.getReleaseType();
                                    }

                                    /* Apply values provided by the PublishingExtension. */
                                    if (extension.getConfigFile() != null && !extension.getConfigFile().isEmpty()) {
                                        if (! new File(extension.getConfigFile()).exists()) {
                                            System.err.println("AGConnect API config not found: " + extension.getConfigFile());
                                            System.err.println("Reverting to the default value: " + configFile);
                                        } else {
                                            configFile = extension.getConfigFile();
                                            if (verbose) {System.out.println("AGConnect API config: " + configFile);}
                                        }
                                    }

                                    /* Task :publishDebugAab will fail, because the AAB is signed with the upload key. */
                                    if (! artifactType.equals(ArtifactType.AAB) || !buildType.equals("debug")) {

                                        /* Register Tasks: Publish. */
                                        taskName = "publish" + StringUtils.capitalize(buildVariant) + StringUtils.capitalize(artifactType);
                                        if (verbose) {System.out.println("> " + buildVariant + " " + artifactType.toUpperCase(Locale.ROOT) + " :" + taskName);}
                                        registerPublishingTask(project, taskName, appConfigFile, artifactType, buildType, buildVariant, productFlavor, releaseType);
                                    }

                                    /* Register Tasks: AppInfo */
                                    taskName = "getAppInfo" + StringUtils.capitalize(buildType);
                                    registerAppInfoTask(project, taskName, appConfigFile, buildType);

                                    /* Register Tasks: AppId */
                                    taskName = "getAppId" + StringUtils.capitalize(buildType);
                                    registerAppIdTask(project, taskName, appConfigFile, buildType);

                                } else if (verbose) {
                                    System.out.println("config not found for: " + artifactType);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @SuppressWarnings("SameParameterValue")
    void registerHelpTask(@NotNull Project project, @NotNull String taskName) {
        if (project.getTasks().findByName(taskName) == null) {
            project.getTasks().register(taskName, HelpTask.class, task -> task.setGroup(taskGroup));
        }
    }

    void registerAppIdTask(
            @NotNull Project project, @NotNull String taskName,
            @NotNull String appConfigFile, @NotNull String buildType
    ) {
        if (project.getTasks().findByName(taskName) == null) {
            String apiConfigFile = configFile;
            project.getTasks().register(taskName, AppIdTask.class, task -> {
                task.setGroup(taskGroup);
                task.getApiConfigFile().set(apiConfigFile);
                task.getAppConfigFile().set(appConfigFile);
                task.getBuildType().set(buildType);
                task.getLogHttp().set(logHttp);
                task.getVerbose().set(verbose);
            });
        }
    }

    void registerAppInfoTask(
            @NotNull Project project, @NotNull String taskName,
            @NotNull String appConfigFile, @NotNull String buildType
    ) {
        if (project.getTasks().findByName(taskName) == null) {
            String apiConfigFile = configFile;
            project.getTasks().register(taskName, AppInfoTask.class, task -> {
                task.setGroup(taskGroup);
                task.getApiConfigFile().set(apiConfigFile);
                task.getAppConfigFile().set(appConfigFile);
                task.getBuildType().set(buildType);
                task.getLogHttp().set(logHttp);
                task.getVerbose().set(verbose);
            });
        }
    }

    void registerPublishingTask(
            @NotNull Project project, @NotNull String taskName, @NotNull String appConfigFile,
            @NotNull String artifactType, @NotNull String buildType,
            @Nullable String buildVariant, @Nullable String productFlavor,
            @Nullable Integer releaseType
    ) {
        if (project.getTasks().findByName(taskName) == null) {
            String apiConfigFile = configFile;
            project.getTasks().register(taskName, PublishingTask.class, task -> {
                task.setGroup(taskGroup);
                task.getReleaseType().set(releaseType);
                task.getApiConfigFile().set(apiConfigFile);
                task.getAppConfigFile().set(appConfigFile);
                task.getArtifactType().set(artifactType);
                task.getBuildType().set(buildType);
                if (buildVariant != null) {task.getBuildVariant().set(buildVariant);}
                if (productFlavor != null) {task.getProductFlavor().set(productFlavor);}
                task.getLogHttp().set(logHttp);
                task.getVerbose().set(verbose);

                /* :publish* tasks depend on :assemble or :bundle tasks; where the name of the build task may vary */
                String buildTask = getBuildTask(project, artifactType, buildVariant != null ? buildVariant : buildType);
                task.dependsOn(buildTask);
            });
        }
    }

    /** Obtain Android ApplicationBuildType, which have a ApkSigningConfig. */
    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    String[] getBuildTypes(@NotNull Project project) {
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
            }
        }
        return buildTypes.toArray(new String[0]);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void setDebugSigningConfig(@NotNull Project project) {
        ApplicationExtension android = (ApplicationExtension) project.getExtensions().getByName("android");
        NamedDomainObjectContainer<? extends ApplicationBuildType> buildTypes = android.getBuildTypes();
        ApplicationBuildType btRelease = buildTypes.getByName("release");
        ApplicationBuildType btDebug = buildTypes.getByName("debug");
        ApkSigningConfig apkSigningConfig = btRelease.getSigningConfig();
        btDebug.setSigningConfig(apkSigningConfig);
    }

    String[] getProductFlavors(@NotNull Project project) {
        ArrayList<String> items = new ArrayList<>();
        ApplicationExtension android = (ApplicationExtension) project.getExtensions().getByName("android");
        for (ProductFlavor productFlavor : android.getProductFlavors()) {items.add(productFlavor.getName());}
        return items.toArray(new String[0]);
    }

    String[] getVariants(@NotNull String[] productFlavors, @NotNull String[] buildTypes) {
        if (productFlavors.length == 0) {return buildTypes;}
        ArrayList<String> items = new ArrayList<>();
        for (String buildType : buildTypes) {
            for (String productFlavor : productFlavors) {
                items.add(productFlavor + StringUtils.capitalize(buildType));
            }
        }
        return items.toArray(new String[0]);
    }

    @NotNull
    private String stringArrayToCsv(@NotNull String[] data) {
        if (data.length == 0) {return "";}
        return String.join(", ", data);
    }

    @Nullable
    private String getAppConfigPath(@NotNull Project project, @NotNull String buildType) {
        String basePath = project.getProjectDir().getAbsolutePath() + File.separator;
        String path = basePath + "src" + File.separator + buildType + File.separator + "agconnect-services.json";
        if (new File(path).exists()) {return path;}
        path = basePath + "src" + File.separator + "agconnect-services.json";
        if (new File(path).exists()) {return path;}
        return null;
    }

    @Nullable
    private String getAppConfigPath(@NotNull Project project, @NotNull String productFlavor, @NotNull String buildType, @NotNull String variant) {
        String fileName = File.separator + "agconnect-services.json";

        /* try the buildType source set */
        String basePath = project.getProjectDir().getAbsolutePath() + File.separator;
        String path = basePath + "src" + File.separator + buildType + fileName;
        if (new File(path).exists()) {return path;}

        /* try the variant source set */
        path = basePath + "src" + File.separator + variant + fileName;
        if (new File(path).exists()) {return path;}

        /* try the product flavor source set */
        path = basePath + "src" + File.separator + productFlavor + fileName;
        if (new File(path).exists()) {return path;}

        /* try the main source set */
        path = basePath + "src" + File.separator + "main" + fileName;
        if (new File(path).exists()) {return path;}

        /* not found */
        return null;
    }

    @NotNull
    private String getBuildTask(@NotNull Project project, @NotNull String artifactType, @NotNull String variant) {
        String task = "assembleRelease";
        if (artifactType.equals(ArtifactType.AAB)) {task = "bundle" + StringUtils.capitalize(variant);}
        else if (artifactType.equals(ArtifactType.APK)) {task = "assemble" + StringUtils.capitalize(variant);}
        return ":" + project.getName() + ":" + task;
    }
}
