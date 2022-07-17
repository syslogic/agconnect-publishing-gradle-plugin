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

import io.syslogic.agconnect.model.ApiException;
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
    public abstract Property<Boolean> getVerbose();

    @Input
    abstract public Property<String> getAppConfigFile();

    @Input
    abstract public Property<String> getApiConfigFile();

    @Input
    abstract public Property<String> getArtifactType();

    @Input
    abstract public Property<String> getBuildType();

    private String basePath         = null;
    private String uploadUrl        = null;
    private String uploadUrlChunked = null;
    private String authCode         = null;

    /** Default {@link TaskAction} */
    @TaskAction
    public void run() {
        String output = "/build/outputs/" + getArtifactType().get().toLowerCase(Locale.ROOT);
        this.basePath = getProject().getProjectDir().getAbsolutePath().concat(output);
        this.setup(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getVerbose().get());
        this.authenticate();
        this.getUploadUrl(getArtifactType().get(), 1);
        this.uploadFile(getArtifactPath());
    }

    @Nullable
    private String getArtifactPath() {
        String name = getProject().getName();
        String suffix = getArtifactType().get().toLowerCase(Locale.ROOT);
        String buildType = getBuildType().get().toLowerCase(Locale.ROOT);
        if (new File(basePath).exists()) {
            return basePath.concat(File.separator + buildType + File.separator + name+ "-" + buildType + "." + suffix);
        }
        return null;
    }

    /**
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-upload-url-0000001158365047">Obtaining the File Upload URL</a>.
     */
    @SuppressWarnings("SameParameterValue")
    private void getUploadUrl(String archiveSuffix, int releaseType) {
        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            request.setURI( new URIBuilder(ENDPOINT_PUBLISH_UPLOAD_URL)
                    .setParameter("appId", String.valueOf(this.appId))
                    .setParameter("suffix", archiveSuffix)
                    .setParameter("releaseType", String.valueOf(releaseType))
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
                this.uploadUrlChunked = result.getChunkUploadUrl();
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
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-upload-file-0000001158245059">Uploading a File</a>.
     */
    @SuppressWarnings("SameParameterValue")
    private void uploadFile(String archivePath) {

        File file = new File(archivePath);
        if (! file.exists()) {return;}

        if (this.uploadUrl != null && this.authCode != null) {
            HttpPost request = new HttpPost(this.uploadUrl);
            request.addHeader("accept", "application/json");
            request.setEntity(MultipartEntityBuilder.create()
                    .addPart("file", new FileBody(file))
                    .addTextBody("fileCount", "1")
                    .addTextBody("authCode", this.authCode)
                    .build()
            );

            try {
                HttpResponse httpResponse = this.client.execute(request);
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                HttpEntity httpEntity = httpResponse.getEntity();
                String responseString = EntityUtils.toString(httpEntity);

                if (statusCode == HttpStatus.SC_OK) {
                    UploadResponseWrap response = new Gson()
                            .fromJson(responseString, UploadResponseWrap.class);
                    if (response.getResult().getResultCode() == 0) {
                        List<UploadFileListItem> items = response
                                .getResult().getResult().getFileList();

                        for (UploadFileListItem item : items) {
                            this.updateFileInfo(item);
                            if (getVerbose().get()) {
                                // this.stdOut("Purified for file: " + item.getPurifiedForFile());
                                this.stdOut("  Download: " + item.getDestinationUrl());
                                this.stdOut("Disposable: " + item.getDisposableUrl());
                                this.stdOut("      Size: " + item.getSizeFormatted());
                            }
                        }

                        if (getVerbose().get()) {
                            this.stdOut("    Status: complete");
                        }
                    } else {
                        ApiException e = response.getResult().getException();
                        this.stdErr("Upload Status: " +
                                e.getErrorCode() + ": " + e.getErrorDesc());
                    }
                } else {
                    this.stdErr("Upload Status: HTTP " +
                            statusCode + ": " + responseString);
                }
            } catch (IOException e) {
                this.stdErr(e.getMessage());
            }
        }
    }

    /**
     * TODO ...
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-app-file-info-0000001111685202">Updating App File Information</a>.
     */
    private void updateFileInfo(UploadFileListItem item) {
        HttpPut request = new HttpPut(ENDPOINT_PUBLISH_APP_FILE_INFO);
        request.addHeader("accept", "application/json");
    }
}
