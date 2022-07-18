package io.syslogic.agconnect.task;

import com.android.build.api.dsl.ApplicationExtension;
import com.google.gson.Gson;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.syslogic.agconnect.constants.ArtifactType;
import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.model.FileInfoUpdateRequest;
import io.syslogic.agconnect.model.FileInfoUpdateResponse;
import io.syslogic.agconnect.model.ResponseStatus;
import io.syslogic.agconnect.constants.ResultCode;
import io.syslogic.agconnect.model.UploadFileItem;
import io.syslogic.agconnect.model.UploadResponseWrap;
import io.syslogic.agconnect.model.UploadUrlResponse;

/**
 * Abstract Publishing {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class PublishingTask extends BaseTask {

    @Input
    abstract public Property<String> getApiConfigFile();

    @Input
    abstract public Property<String> getAppConfigFile();

    @Input
    abstract public Property<String> getArtifactType();

    @Input
    abstract public Property<String> getBuildType();

    @Input
    public abstract Property<Boolean> getLogHttp();

    @Input
    public abstract Property<Boolean> getVerbose();

    /** Property `rootProject.name` can be defined in `settings.gradle`. */
    @Input
    @NotNull
    String getUploadFileName() {
        String name = getProject().getRootProject().getName();
        String suffix = getArtifactType().get().toLowerCase(Locale.ROOT);
        String buildType = getBuildType().get().toLowerCase(Locale.ROOT);
        return name + "-" + buildType + "-" + getVersionName() + "." + suffix;
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String chunkUrl  = null;
    private String uploadUrl = null;
    private String authCode  = null;

    /** Release Type: value 1=network, 5=phased. */
    @SuppressWarnings("FieldMayBeFinal")
    private int releaseType  = 1;

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get())) {
            if (this.authenticate()) {
                this.getUploadUrl(getArtifactType().get());
                if (checkBuildOutput()) {
                    this.uploadFile(getArtifactPath());
                }
            }
        }
    }

    /**
     * Obtaining the upload URL.
     *
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-upload-url-0000001158365047">Obtaining the File Upload URL</a>.
     */
    private void getUploadUrl(String archiveSuffix) {
        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            request.setURI( new URIBuilder(EndpointUrl.PUBLISH_UPLOAD_URL)
                    .setParameter("appId", String.valueOf(this.appId))
                    .setParameter("releaseType", String.valueOf(this.releaseType))
                    .setParameter("suffix", archiveSuffix)
                    .build()
            );

            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStream stream = response.getEntity().getContent();
                InputStreamReader isr = new InputStreamReader(stream, Consts.UTF_8);
                BufferedReader rd = new BufferedReader(isr);
                UploadUrlResponse result = new Gson().fromJson(rd.readLine(), UploadUrlResponse.class);
                this.authCode = result.getAuthCode();
                this.uploadUrl = result.getUploadUrl();
                this.chunkUrl = result.getChunkUploadUrl();
                if (getVerbose().get()) {
                    this.stdOut("  Endpoint: " + this.uploadUrl);
                }
            } else {
                this.stdOut("HTTP " + statusCode + " " +
                        response.getStatusLine().getReasonPhrase());
            }
        } catch (URISyntaxException | IOException e) {
            this.stdErr(e.getMessage());
        }
    }

    /**
     * Uploading the File.
     *
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-upload-file-0000001158245059">Uploading a File</a>.
     */
    @SuppressWarnings("SameParameterValue")
    private void uploadFile(String archivePath) {

        /* Check if the file exists. */
        File file = new File(archivePath);
        if (! file.exists()) {return;}

        if (this.uploadUrl != null && this.authCode != null) {
            HttpPost request = new HttpPost(this.uploadUrl);
            request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            request.setEntity(MultipartEntityBuilder.create()
                    .addPart("file", new FileBody(file))
                    .addTextBody("fileCount", "1")
                    .addTextBody("authCode", this.authCode)
                    .build()
            );

            try {
                long timestamp = System.currentTimeMillis();
                HttpResponse response = this.client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);
                if (statusCode == HttpStatus.SC_OK) {
                    UploadResponseWrap wrap = new Gson().fromJson(result, UploadResponseWrap.class);
                    if (wrap.getResult().getResultCode() == ResultCode.SUCCESS) {
                        List<UploadFileItem> items = wrap.getResult().getResult().getFileList();
                        String sizeFormatted;
                        for (UploadFileItem item : items) {

                            /* Verbose logging */
                            sizeFormatted = item.getSizeFormatted();
                            if (getVerbose().get()) {
                                // this.stdOut("Purified: " + item.getPurifiedForFile()); ?
                                this.stdOut("  Download: " + item.getDestinationUrl());
                                this.stdOut("Disposable: " + item.getDisposableUrl());
                                this.stdOut("      Size: " + sizeFormatted);
                            }

                            /* Update the information for the uploaded file. */
                            String filename = getUploadFileName();
                            this.updateFileInfo(filename, item.getDestinationUrl());

                            /* Log transfer stats before the task completes. */
                            float duration = System.currentTimeMillis() - timestamp;
                            float rate = item.getSize() / duration;
                            this.stdOut("\n" + getArtifactType().get().toUpperCase(Locale.ROOT) + " " + getUploadFileName() + " had been uploaded.");
                            this.stdOut(sizeFormatted +" in " + Math.round(duration/1000F) + "s equals a transfer-rate of " + rate + " MB/s");
                        }
                    } else {
                        ResponseStatus e = wrap.getResult().getStatus();
                        String msg = "Upload Error: " + e.getCode() + ": " + e.getMessage();
                        this.stdErr(msg);
                    }
                } else {
                    this.stdErr("Upload HTTP Error: " + statusCode + ": " + result);
                }
            } catch (IOException e) {
                this.stdErr(e.getMessage());
            }
        }
    }

    /**
     * Updating App File Information.
     *
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-app-file-info-0000001111685202">Updating App File Information</a>.
     */
    private void updateFileInfo(String fileName, String destFileUrl) {

        HttpPut request = new HttpPut();
        request.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);

        try {
            request.setURI( new URIBuilder(EndpointUrl.PUBLISH_APP_FILE_INFO)
                    .setParameter("appId", String.valueOf(this.appId))
                    .setParameter("releaseType", String.valueOf(this.releaseType))
                    .build()
            );

            String payload = new FileInfoUpdateRequest(fileName, destFileUrl).toJson();
            StringEntity entity = new StringEntity(payload);
            request.setEntity(entity);

            HttpResponse response = this.client.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {

                /* If upload has failed */
                FileInfoUpdateResponse data = new Gson().fromJson(result, FileInfoUpdateResponse.class);
                ResponseStatus status = data.getStatus();
                String [] versions = data.getVersions();
                if (status.getCode() != ResultCode.SUCCESS) {
                    this.stdErr("\nCode " + status.getCode() + ": " + status.getMessage());
                    this.stdOut(EndpointUrl.PUBLISH_ERROR_CODES);
                } else if (versions.length > 0) { /* Query compile status by packageId. */
                    String packageIds = String.join(",", Arrays.asList(data.getVersions()));
                    this.getCompileStatus(packageIds);
                }
            } else {
                this.stdErr(response.getStatusLine().toString());
            }
        } catch (IOException | URISyntaxException e) {
            this.stdErr(e.getMessage());
        }
    }


    /**
     * @param packageIds app package IDs, separated by commas.
     */
    @SuppressWarnings("UnusedReturnValue")
    public void getCompileStatus(@NotNull String packageIds) {

        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_COMPILE_STATUS);
            builder.setParameter("appId", String.valueOf(this.appId));
            builder.setParameter("pkgIds", packageIds);

            request.setURI(builder.build());
            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);
            this.stdOut(result);

            if (statusCode == HttpStatus.SC_OK) {

            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }

        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }

    /**
     * Obtain the artifact path.
     *
     * @return the absolute path to the artifact to upload.
     */
    @Nullable
    private String getArtifactPath() {
        String name = getProject().getName();
        String suffix = getArtifactType().get().toLowerCase(Locale.ROOT);
        String buildType = getBuildType().get().toLowerCase(Locale.ROOT);
        String output = File.separator + "build" + File.separator + "outputs" +
                File.separator + (suffix.equals(ArtifactType.AAB) ? "bundle": "apk");
        String basePath = getProject().getProjectDir().getAbsolutePath().concat(output);
        if (new File(basePath).exists()) {
            return basePath.concat(File.separator + buildType + File.separator +
                    name+ "-" + buildType + "." + suffix);
        }
        return null;
    }

    /** Check build output. */
    private boolean checkBuildOutput() {

        /* Check if the file exists and can be read. */
        String archivePath = getArtifactPath();
        assert archivePath != null;

        File file = new File(archivePath);
        if (file.exists() && file.canRead()) {
            return true;
        } else {
            this.stdErr("Not found: " + archivePath);
            return false;
        }
    }

    /** Obtain version name. */
    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    private String getVersionName() {
        String versionName = "0.0.0";
        ApplicationExtension android = (ApplicationExtension) getProject().getExtensions().getByName("android");
        if (android.getDefaultConfig().getVersionName() != null) {
            versionName = android.getDefaultConfig().getVersionName();
        }
        return versionName;
    }
}
