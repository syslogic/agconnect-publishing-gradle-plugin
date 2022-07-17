package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.syslogic.agconnect.model.ApiConfigFile;
import io.syslogic.agconnect.model.AppConfigFile;
import io.syslogic.agconnect.model.TokenRequest;
import io.syslogic.agconnect.model.TokenResponse;

/**
 * Abstract {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class BaseTask extends DefaultTask {

    static String ENDPOINT_OAUTH2_TOKEN = "https://connect-api.cloud.huawei.com/api/oauth2/v1/token";
    static String ENDPOINT_PUBLISH_UPLOAD_URL = "https://connect-api.cloud.huawei.com/api/publish/v2/upload-url";
    static String ENDPOINT_PUBLISH_APP_ID_LIST = "https://connect-api.cloud.huawei.com/api/publish/v2/appid-list";
    static String ENDPOINT_PUBLISH_APP_FILE_INFO = "https://connect-api.cloud.huawei.com/api/publish/v2/app-file-info";
    static String ENDPOINT_PUBLISH_APP_INFO = "https://connect-api.cloud.huawei.com/api/publish/v2/app-info";

    Long appId = 0L;
    Long projectId = 0L;
    String packageName = null;;

    String clientId = null;
    String clientSecret = null;
    String accessToken = null;

    HttpClient client;

    /** It sets up HttpClient and parses two JSON config files. */
    void setup(@NotNull Project project, String appConfig, String apiConfig, boolean verbose) {

        String ua = "Gradle/" + project.getGradle().getGradleVersion();
        this.client = HttpClientBuilder.create().setUserAgent(ua).build();

        File file = new File(appConfig);
        if (file.exists() && file.canRead()) {
            if (verbose) {this.stdOut("App Config: " + appConfig);}
            AppConfigFile config = new Gson().fromJson(readFile(file), AppConfigFile.class);
            this.appId = config.getClient().getAppId();
            this.packageName = config.getClient().getPackageName();
            this.projectId = config.getClient().getProjectId();
        } else {
            this.stdErr("AppId not found:");
            this.stdErr(file.getAbsolutePath());
        }

        file = new File(apiConfig);
        if (file.exists() && file.canRead()) {
            if (verbose) {this.stdOut("API Config: " + apiConfig);}
            ApiConfigFile config = new Gson().fromJson(readFile(file), ApiConfigFile.class);
            if (config.getType().equals("team_client_id")) {
                this.clientSecret = config.getClientSecret();
                this.clientId = config.getClientId();
            } else {
                this.stdOut("API Config: credentials JSON must have role \"App administrator\"");
                this.stdOut("https://developer.huawei.com/consumer/en/service/josp/agc/index.html");
            }
        } else {
            this.stdErr("API Config not found:");
            this.stdErr(file.getAbsolutePath());
        }

        /* Log Configuration */
        if (verbose) {
            this.stdOut("     AppId: " + this.appId);
            if (this.clientId != null && this.clientSecret != null) {
                this.stdOut("  ClientId: " + this.clientId);
                this.stdOut("    Secret: " + this.clientSecret);
            }
        }
    }

    void authenticate() {
        HttpPost request = new HttpPost(ENDPOINT_OAUTH2_TOKEN);
        String payload = new Gson().toJson(new TokenRequest(this.clientId, this.clientSecret));
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try {
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                TokenResponse result = new Gson().fromJson(rd.readLine(), TokenResponse.class);
                this.accessToken = result.getAccessToken();
            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            this.stdErr(e.getMessage());
        }
    }

    @NotNull
    private String readFile(@NotNull File file) {
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            while ((line = reader.readLine()) != null) {sb.append(line);}
        } catch (IOException e) {
            this.stdErr(e.getMessage());
        }
        return sb.toString();
    }

    void stdOut(@NotNull String value) {
        System.out.println(value);
    }

    void stdErr(@NotNull String value) {
        System.err.println(value);
    }
}
