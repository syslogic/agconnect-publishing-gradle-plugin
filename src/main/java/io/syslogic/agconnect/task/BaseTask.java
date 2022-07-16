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

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.syslogic.agconnect.model.ApiConfigFile;
import io.syslogic.agconnect.model.AppConfigFile;
import io.syslogic.agconnect.model.AccessTokenRequest;
import io.syslogic.agconnect.model.AccessTokenResponse;

/**
 * Abstract {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class BaseTask extends DefaultTask {

    static String ENDPOINT_OAUTH2_TOKEN = "https://connect-api.cloud.huawei.com/api/oauth2/v1/token";
    static String ENDPOINT_PUBLISH_UPLOAD_URL = "https://connect-api.cloud.huawei.com/api/publish/v2/upload-url";
    static String ENDPOINT_PUBLISH_APP_INFO = "https://connect-api.cloud.huawei.com/api/publish/v2/app-info";

    HttpClient client;
    String ua = "Gradle/7.2.1";

    long projectId = -1L;
    String appId = null;
    String clientId = null;
    String clientSecret = null;
    String accessToken = null;

    /** It sets up HttpClient and parses config files. */
    void parseConfigFiles(String appConfig, String apiConfig, boolean verbose) {
        this.client = HttpClientBuilder.create().setUserAgent(this.ua).build();

        File file = new File(appConfig);
        if (file.exists() && file.canRead()) {
            System.out.println("App Config: " + appConfig);
            AppConfigFile config = new Gson().fromJson(readFile(file), AppConfigFile.class);
            this.appId = config.getClient().getAppId();
            this.projectId = config.getClient().getProjectId();
        } else {
            System.err.println("AppId not found:");
            System.err.println(file.getAbsoluteFile());
        }

        file = new File(apiConfig);
        if (file.exists() && file.canRead()) {
            System.out.println("API Config: " + apiConfig);
            ApiConfigFile config = new Gson().fromJson(readFile(file), ApiConfigFile.class);
            if (config.getType().equals("team_client_id")) {
                this.clientSecret = config.getClientSecret();
                this.clientId = config.getClientId();
            } else {
                System.out.println("API Config: credentials JSON must have role \"App administrator\"");
                System.out.println("https://developer.huawei.com/consumer/en/service/josp/agc/index.html");
            }
        } else {
            System.err.println("API Config not found:");
            System.err.println(file.getAbsoluteFile());
        }

        /* Log Configuration */
        if (verbose) {
            System.out.println("     AppId: " + this.appId);
            if (this.clientId != null && this.clientSecret != null) {
                System.out.println("  ClientId: " + this.clientId);
                System.out.println("    Secret: " + this.clientSecret);
            }
        }
    }

    void authenticate(String clientId, String clientSecret, boolean verbose) {
        HttpPost request = new HttpPost(ENDPOINT_OAUTH2_TOKEN);
        String payload = new Gson().toJson(new AccessTokenRequest(clientId, clientSecret));
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try {
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                AccessTokenResponse result = new Gson().fromJson(rd.readLine(), AccessTokenResponse.class);
                this.accessToken = result.getAccessToken();
                if (verbose) {System.out.println("     Token: " + this.accessToken);}
            } else {
                System.err.println("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @NotNull
    private String readFile(File file) {
        StringBuilder data = new StringBuilder();
        String line;
        try (
                FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(isr)) {
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
