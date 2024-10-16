package io.syslogic.agconnect.task;

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
import org.gradle.api.Project;
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
import io.syslogic.agconnect.constants.ConsoleUrl;
import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.constants.ErrorMessage;
import io.syslogic.agconnect.constants.ResultCode;
import io.syslogic.agconnect.model.CompilePackageState;
import io.syslogic.agconnect.model.CompileStateResponse;
import io.syslogic.agconnect.model.FileInfoUpdateRequest;
import io.syslogic.agconnect.model.FileInfoUpdateResponse;
import io.syslogic.agconnect.model.ResponseStatus;
import io.syslogic.agconnect.model.UploadFileItem;
import io.syslogic.agconnect.model.UploadResponseWrap;
import io.syslogic.agconnect.model.UploadUrlResponse;

/**
 * Abstract Upload Package {@link BaseTask}
 * @author Martin Zeitler
 */
abstract public class UploadPackageTask extends BaseTask {

    /** @return ReleaseType */
    @Input
    abstract public Property<Integer> getReleaseType();

    /** @return ArtifactType */
    @Input
    abstract public Property<String> getArtifactType();

    /** @return ProductFlavor */
    @Input
    abstract public Property<String> getProductFlavor();

    /** @return BuildVariant */
    @Input
    abstract public Property<String> getBuildVariant();

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String chunkUrl  = null;
    private String uploadUrl = null;
    private String authCode  = null;

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get(), getReleaseType().get())) {
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
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-upload-url-0000001158365047">Obtaining the File Upload URL</a>.
     */
    private void getUploadUrl(String archiveSuffix) {
        try {
            HttpGet request = new HttpGet();
            request.setHeaders(getDefaultHeaders());
            request.setURI(new URIBuilder(EndpointUrl.PUBLISH_UPLOAD_URL)
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
                    this.stdOut("> Endpoint: " + this.uploadUrl);
                }
            } else {
                this.stdOut("> HTTP " + statusCode + " " +
                        response.getStatusLine().getReasonPhrase());
            }
        } catch (URISyntaxException | IOException e) {
            this.stdErr(e.getMessage());
        }
    }

    /**
     * Uploading the File.
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-upload-file-0000001158245059">Uploading a File</a>.
     */
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
                            long duration = System.currentTimeMillis() - timestamp;
                            this.stdOut("> " + getArtifactType().get().toUpperCase(Locale.ROOT) + " file " + getUploadFileName() + " has been uploaded.");
                            this.stdOut("> " + sizeFormatted +" in " + Math.round(duration/1000F) + "s equals a transfer-rate of " + getTransferRate(item.getSize(), duration) + ".");
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
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-app-file-info-0000001111685202">Updating App File Information</a>.
     */
    private void updateFileInfo(String fileName, String destFileUrl) {

        HttpPut request = new HttpPut();
        request.setHeaders(getDefaultHeaders());

        try {
            request.setURI(new URIBuilder(EndpointUrl.PUBLISH_APP_FILE_INFO)
                    .setParameter("appId", String.valueOf(this.appId))
                    .setParameter("releaseType", String.valueOf(this.releaseType))
                    .build()
            );

            String payload = new FileInfoUpdateRequest(fileName, destFileUrl).toJson();
            StringEntity entity = new StringEntity(payload);
            request.setEntity(entity);

            HttpResponse response = this.client.execute(request);
            logResponse(fileName, response);

        } catch (IOException | URISyntaxException e) {
            this.stdErr("> " +  e.getMessage());
        }
    }

    private void logResponse(@NotNull String fileName, @NotNull HttpResponse response) {
        try {
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                FileInfoUpdateResponse data = new Gson().fromJson(result, FileInfoUpdateResponse.class);
                String message = data.getStatus().getMessage();
                String [] versions = data.getVersions();
                int code = data.getStatus().getCode();

                /* Query compile status by the returned packageId. */
                if (code == ResultCode.SUCCESS) {
                    if (versions.length > 0) {
                        String packageIds = String.join(",", Arrays.asList(data.getVersions()));
                        this.getCompileStatus(packageIds);
                    }
                } else if (code == ResultCode.ADD_APK_HAS_FAILED && message.equals(ErrorMessage.APP_SIGNING_NOT_ENABLED)) {
                    this.stdErr("Please enable App Signing in order to publish App Bundle format (" + fileName + ").");
                    this.stdErr("In case the following URL does not lead to the expected package, validate agconnect-services.json.");
                    this.stdOut(ConsoleUrl.CERTIFICATES.replace("{appId}", String.valueOf(this.appId)));
                } else if (code == ResultCode.FAILED_TO_UPDATE_PACKAGE && message.equals(ErrorMessage.ONGOING_INTEGRATION_CHECK)) {
                    this.stdErr("The package may be under review or may already have been released (" + fileName + ").");
                    this.stdErr("If not released, please cancel the pending review if you wish to perform an update.");
                    this.stdOut(ConsoleUrl.INTEGRATION.replace("{appId}", String.valueOf(this.appId)));
                } else {
                    this.stdErr("\nCode " + code + ": " + message);
                }
            } else {
                this.stdErr("\n" + response.getStatusLine().toString());
            }
        } catch (IOException e) {
            this.stdErr(e.getMessage());
        }
    }

    /**
     * It logs the {@link CompilePackageState}
     * @param packageIds package IDs, separated by commas.
     */
    public void getCompileStatus(@NotNull String packageIds) {
        HttpGet request = new HttpGet();
        request.setHeaders(getDefaultHeaders());
        try {
            request.setURI(new URIBuilder(EndpointUrl.PUBLISH_COMPILE_STATUS)
                    .setParameter("appId", String.valueOf(this.appId))
                    .setParameter("pkgIds", packageIds)
                    .build()
            );
            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);
            if (statusCode == HttpStatus.SC_OK) {
                CompileStateResponse data = new Gson().fromJson(result, CompileStateResponse.class);
                for (CompilePackageState item : data.getPackageState()) {
                    if (item.getStatus() == 1) {
                        this.stdOut("> Package: " + ConsoleUrl.PACKAGE_INFO
                                .replace("{appId}", String.valueOf(this.appId))
                                .replace("{packageId}", item.getPackageId()));
                    }
                }
            } else {
                this.stdErr("> HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }

    /** Property `archivesBaseName` appears most reliable. */
    @Input
    @NotNull
    String getUploadFileName() {
        String baseName = String.valueOf(getProject().getProperties().get("archivesBaseName"));
        String flavor = getProductFlavor().get();
        String buildType = getBuildType().get();
        String suffix = getArtifactType().get().toLowerCase(Locale.ROOT);
        return baseName + "-" + flavor + "-" + buildType + "." + suffix;
    }

    /**
     * Obtain the artifact path from project property `archivesBaseName`.
     * Project property `archivesBaseName` defaults to `project.name`.
     * @return the absolute path to the artifact to upload.
     */
    @Nullable
    private String getArtifactPath() {
        if (getBuildType() == null) {
            return null; // return early.
        }
        String buildType = getBuildType().get();
        String flavor = getProductFlavor().get();
        String suffix = getArtifactType().get();
        String variant = getBuildVariant().get();

        String output = getProject().getProjectDir().getAbsolutePath().concat(
                File.separator + Project.DEFAULT_BUILD_DIR_NAME + File.separator + "outputs" + File.separator +
                (suffix.equals(ArtifactType.AAB) ? "bundle": "apk") + File.separator +
                variant + File.separator
        );
        if (new File(output).exists()) {
            String baseName = String.valueOf(getProject().getProperties().get("archivesBaseName"));
            String fileName = output.concat(baseName + "-" + buildType + "." + suffix);
            if (new File(fileName).exists()) {
                return fileName;
            } else {
                fileName = output.concat(baseName + "-" + flavor + "-" + buildType + "." + suffix);
                if (new File(fileName).exists()) {
                    return fileName;
                }
            }
        }
        return null;
    }

    /** Check build output. */
    private boolean checkBuildOutput() {

        /* Check if the file exists and can be read. */
        String archivePath = getArtifactPath();
        if (archivePath == null) {return false;}

        File file = new File(archivePath);
        if (file.exists() && file.canRead()) {
            return true;
        } else {
            this.stdErr("\n> Not found: " + archivePath);
            return false;
        }
    }
}
