package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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
import io.syslogic.agconnect.model.Endpoint;
import io.syslogic.agconnect.model.TokenRequest;
import io.syslogic.agconnect.model.TokenResponse;

/**
 * Abstract {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class BaseTask extends DefaultTask {

    String ua;
    HttpClient client;

    Long appId = 0L;
    String packageName = null;

    String clientId = null;
    String clientSecret = null;
    String accessToken = null;

    /** It sets up HttpClient and parses two JSON config files. */
    boolean configure(@NotNull Project project, String appConfig, String apiConfig, boolean logHttp, boolean verbose) {

        /* PoolingHttpClientConnectionManager is required for subsequent requests. */
        this.ua = "Gradle/" + project.getGradle().getGradleVersion();
        this.client = this.getHttpClient(logHttp);

        File file = new File(appConfig);
        if (file.exists() && file.canRead()) {
            if (verbose) {this.stdOut("App Config: " + appConfig);}
            AppConfigFile config = new Gson().fromJson(readFile(file), AppConfigFile.class);
            this.appId = config.getAppInfo().getAppId();
            this.packageName = config.getAppInfo().getPackageName();
        } else {
            this.stdErr("AppId not found:");
            this.stdErr(file.getAbsolutePath());
            return false;
        }

        file = new File(apiConfig);
        if (file.exists() && file.canRead()) {
            if (verbose) {this.stdOut("API Config: " + apiConfig);}
            ApiConfigFile config = new Gson().fromJson(readFile(file), ApiConfigFile.class);
            String role = config.getRole();
            if (role.equals("team_client_id")) {
                this.clientSecret = config.getClientSecret();
                this.clientId = config.getClientId();
            } else {
                this.stdErr("API client credentials must have role \"App administrator\"; provided: \"Administrator\"");
                this.stdOut(Endpoint.CONNECT_API_CONSOLE);
                return false;
            }
        } else {
            this.stdErr("API client credentials not found:");
            this.stdOut(file.getAbsolutePath());
            this.stdOut(Endpoint.CONNECT_API_CONSOLE);
            return false;
        }

        /* Log Configuration */
        if (verbose) {
            this.stdOut("     AppId: " + this.appId);
            if (this.clientId != null && this.clientSecret != null) {
                this.stdOut("  ClientId: " + this.clientId);
                this.stdOut("    Secret: " + this.clientSecret);
            }
        }
        return true;
    }

    boolean authenticate() {
        HttpPost request = new HttpPost(Endpoint.OAUTH2_TOKEN);
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
                return true;
            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch (IOException e) {
            this.stdErr(e.getMessage());
        }
        return false;
    }

    /**
     * @return an instance of {@link HttpClient}.
     */
    @NotNull
    private HttpClient getHttpClient(boolean logHttp) {

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(20);
        cm.setMaxTotal(100);

        HttpClientBuilder cb = HttpClientBuilder.create()
                .setConnectionManager(cm)
                .setUserAgent(this.ua);

        if (logHttp) {
            cb
                    .addInterceptorFirst((HttpRequestInterceptor) (request, context) ->
                            stdOut("> " + request.getRequestLine().toString()))
                    .addInterceptorLast((HttpResponseInterceptor) (request, context) ->
                            stdOut("> " + request.getStatusLine().toString()));
        }
        return cb.build();
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

    @NotNull
    String getFileName(@NotNull String archivePath) {
        String regex = File.separator.equals("\\") ? "\\u005c" : File.separator;
        String[] parts = archivePath.split(regex);
        return parts[ parts.length-1 ];
    }

    void stdOut(@NotNull String value) {
        System.out.println(value);
    }

    void stdErr(@NotNull String value) {
        System.err.println(value);
    }
}
