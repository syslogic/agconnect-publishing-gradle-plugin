package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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

    private String basePath       = null;
    private String uploadUrl      = null;
    private String chunkUploadUrl = null;
    private String authCode       = null;

    @TaskAction
    public void run() {
        this.basePath = getProject().getProjectDir().getAbsolutePath().concat("/build/outputs/" + getArtifactType().get().toLowerCase(Locale.ROOT));
        this.parseConfigFiles(getAppConfigFile().get(), getApiConfigFile().get(), getVerbose().get());
        this.authenticate(this.clientId, this.clientSecret, getVerbose().get());
        this.getUploadUrl(getArtifactType().get(), 1);
        this.uploadFile(getArtifactPath());
    }

    @NotNull
    private String getArtifactPath() {
        String name = getProject().getName();
        String suffix = getArtifactType().get().toLowerCase(Locale.ROOT);
        String buildType = getBuildType().get().toLowerCase(Locale.ROOT);
        if (new File(basePath).exists()) {
            return basePath.concat(File.separator + buildType + File.separator + name+ "-" + buildType + "." + suffix);
        }
        return "";
    }

    @SuppressWarnings("SameParameterValue")
    private void getUploadUrl(String archiveSuffix, int releaseType) {
        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            request.setURI( new URIBuilder(ENDPOINT_PUBLISH_UPLOAD_URL)
                    .setParameter("appId", this.appId)
                    .setParameter("suffix", archiveSuffix)
                    .setParameter("releaseType", String.valueOf(releaseType))
                    .build()
            );

            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                InputStreamReader isr = new InputStreamReader(response.getEntity().getContent(), Consts.UTF_8);
                BufferedReader rd = new BufferedReader(isr);
                UploadUrlResponse result = new Gson().fromJson(rd.readLine(), UploadUrlResponse.class);
                this.chunkUploadUrl = result.getChunkUploadUrl();
                this.uploadUrl = result.getUploadUrl();
                this.authCode = result.getAuthCode();
                if (getVerbose().get()) {
                    System.out.println("  Endpoint: " + this.uploadUrl);
                }
            } else {
                System.out.println("HTTP " + statusCode + " " +
                        response.getStatusLine().getReasonPhrase());
            }
        } catch (URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /** post form multi-part encoded. */
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
                HttpEntity httpEntity = httpResponse.getEntity();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(httpEntity);
                if (statusCode == HttpStatus.SC_OK) {
                    UploadResponseWrap response = new Gson().fromJson(responseString, UploadResponseWrap.class);
                    if (response.getResult().getResultCode() == 0) {
                        if (getVerbose().get()) {
                            List<UploadFileListItem> items = response.getResult().getResult().getFileList();
                            for (UploadFileListItem item : items) {
                                // System.out.println("Purified for file: " + item.getPurifiedForFile());
                                System.out.println("  Download: " + item.getDestinationUrl());
                                System.out.println("Disposable: " + item.getDisposableUrl());
                                System.out.println("      Size: " + item.getSizeFormatted());
                            }
                            System.out.println("    Status: complete");
                        }
                    } else {
                        ApiException e = response.getResult().getException();
                        System.err.println("Upload Status: " + e.getErrorCode() + ": " + e.getErrorDesc());
                    }
                } else {
                    System.err.println("Upload Status: HTTP " + statusCode + ": " + responseString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
