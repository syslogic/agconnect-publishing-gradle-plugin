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

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import io.syslogic.agconnect.model.Endpoint;
import io.syslogic.agconnect.model.FileInfoUpdateRequest;
import io.syslogic.agconnect.model.FileInfoUpdateResponse;
import io.syslogic.agconnect.model.ResponseStatus;
import io.syslogic.agconnect.model.ResultCode;
import io.syslogic.agconnect.model.UploadFileListItem;
import io.syslogic.agconnect.model.UploadResponseWrap;
import io.syslogic.agconnect.model.UploadUrlResponse;

/**
 * Abstract Publishing {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class PublishingTask extends BaseTask {

    @Input
    abstract public Property<String> getAppConfigFile();

    @Input
    abstract public Property<String> getApiConfigFile();

    @Input
    abstract public Property<String> getArtifactType();

    @Input
    abstract public Property<String> getBuildType();

    @Input
    public abstract Property<Boolean> getLogHttp();

    @Input
    public abstract Property<Boolean> getVerbose();

    private int releaseType  = 1; /* 5 = phased. */

    private String authCode  = null;
    private String uploadUrl = null;
    private String chunkUrl  = null;

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get())) {
            this.authenticate();
            this.getUploadUrl(getArtifactType().get());
            if (checkBuildOutput()) {
                this.uploadFile(getArtifactPath());
            }
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
        String output = File.separator + "build" + File.separator + "outputs" + File.separator +
                getArtifactType().get().toLowerCase(Locale.ROOT);
        String basePath = getProject().getProjectDir().getAbsolutePath().concat(output);
        if (new File(basePath).exists()) {
            return basePath.concat(File.separator + buildType + File.separator +
                    name+ "-" + buildType + "." + suffix);
        }
        return null;
    }

    /** Check build output. */
    private boolean checkBuildOutput() {

        /* Check if the file exists. */
        String archivePath = getArtifactPath();
        assert archivePath != null;

        File file = new File(archivePath);
        if (file.exists() && file.canRead()) {
            return true;
        } else {
            /* Check if the file exists under an alternate name */
            String message = "Not found: " + archivePath;
            String unsigned = archivePath.replace(".apk", "-unsigned.apk");
            if (new File(unsigned).exists()) {message = "Not signed: " + unsigned;}
            this.stdErr(message);
            return false;
        }
    }

    /**
     * Obtaining the upload URL.
     *
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-upload-url-0000001158365047">Obtaining the File Upload URL</a>.
     */
    @SuppressWarnings("SameParameterValue")
    private void getUploadUrl(String archiveSuffix) {
        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            request.setURI( new URIBuilder(Endpoint.PUBLISH_UPLOAD_URL)
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
                HttpResponse response = this.client.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);
                if (statusCode == HttpStatus.SC_OK) {
                    UploadResponseWrap wrap = new Gson().fromJson(result, UploadResponseWrap.class);
                    if (wrap.getResult().getResultCode() == ResultCode.SUCCESS) {
                        List<UploadFileListItem> items = wrap.getResult().getResult().getFileList();
                        String sizeFormatted = "";
                        for (UploadFileListItem item : items) {
                            sizeFormatted = item.getSizeFormatted();
                            if (getVerbose().get()) {
                                // this.stdOut("Purified: " + item.getPurifiedForFile());
                                this.stdOut("  Download: " + item.getDestinationUrl());
                                this.stdOut("Disposable: " + item.getDisposableUrl());
                                this.stdOut("      Size: " + sizeFormatted);
                            }

                            /* update file upload info */
                            this.updateFileInfo(getFileName(archivePath), item.getDestinationUrl());
                        }
                         if (! getVerbose().get()) {
                            this.stdOut("Uploaded " + sizeFormatted);
                        }
                    } else {
                        ResponseStatus e = wrap.getResult().getStatus();
                        String msg = "Upload Status: " + e.getCode() + ": " + e.getMessage();
                        this.stdErr(msg);
                    }
                } else {
                    this.stdErr("Upload Status: HTTP " + statusCode + ": " + result);
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
            request.setURI( new URIBuilder(Endpoint.PUBLISH_APP_FILE_INFO)
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
                FileInfoUpdateResponse data = new Gson().fromJson(result, FileInfoUpdateResponse.class);
                /* If upload has failed */
                if (data.getStatus().getCode() != ResultCode.SUCCESS) {
                    this.stdErr("\nCode " + data.getStatus().getCode() + ": " +
                            data.getStatus().getMessage());
                    this.stdOut(Endpoint.PUBLISH_ERROR_CODES);
                }
            } else {
                this.stdErr(response.getStatusLine().toString());
            }
        } catch (IOException | URISyntaxException e) {
            this.stdErr(e.getMessage());
        }
    }
}
