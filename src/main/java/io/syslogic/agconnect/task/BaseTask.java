package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import io.syslogic.agconnect.constants.ConsoleUrl;
import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.model.ApiConfigFile;
import io.syslogic.agconnect.model.AppConfigFile;
import io.syslogic.agconnect.model.AppConfigInfo;
import io.syslogic.agconnect.model.AppIdListResponse;
import io.syslogic.agconnect.model.AppInfoAppId;
import io.syslogic.agconnect.model.TokenRequest;
import io.syslogic.agconnect.model.TokenResponse;

/**
 * Abstract {@link BaseTask}
 * @author Martin Zeitler
 */
abstract public class BaseTask extends DefaultTask {

    /** @return ApiConfigFile */
    @Input
    abstract public Property<String> getApiConfigFile();

    /** @return AppConfigFile */
    @Input
    abstract public Property<String> getAppConfigFile();

    /** @return BuildType */
    @Input
    abstract public Property<String> getBuildType();

    /** @return ReleaseType */
    @Input
    abstract public Property<Integer> getReleaseType();

    /** @return LogHttp */
    @Input
    public abstract Property<Boolean> getLogHttp();

    /** @return Verbose */
    @Input
    public abstract Property<Boolean> getVerbose();

    /** Release Type: value 1=network, 5=phased. */
    protected int releaseType  = 1;

    String ua;
    HttpClient client;

    String clientId = null;
    String clientSecret = null;
    String accessToken = null;

    String packageName = null;
    Long appId = 0L;

    /** It sets up HttpClient and parses two JSON config files. */
    boolean configure(@NotNull Project project, String appConfig, String apiConfig, Boolean logHttp, Boolean verbose, Integer releaseType) {

        if (releaseType != null && releaseType == 3) {
            this.releaseType = releaseType;
        }

        /* PoolingHttpClientConnectionManager is required for subsequent requests. */
        this.ua = "Gradle/" + project.getGradle().getGradleVersion();
        this.client = this.getHttpClient(logHttp);

        File file = new File(apiConfig);
        if (file.exists() && file.canRead()) {
            if (verbose) {this.stdOut("API Config: " + apiConfig);}
            ApiConfigFile config = new Gson().fromJson(readFile(file), ApiConfigFile.class);
            String role = config.getRole();
            if (role.equals("team_client_id")) {
                this.clientSecret = config.getClientSecret();
                this.clientId = config.getClientId();
            } else {
                this.stdErr("API client credentials must have role \"App administrator\"; provided: \"Administrator\"");
                this.stdOut(ConsoleUrl.API_CLIENT_CREDENTIALS);
                return false;
            }
        } else {
            this.stdErr("API client credentials not found:");
            this.stdOut(file.getAbsolutePath());
            this.stdOut(ConsoleUrl.API_CLIENT_CREDENTIALS);
            return false;
        }

        file = new File(appConfig);
        if (file.exists() && file.canRead()) {

            if (verbose) {this.stdOut("App Config: " + appConfig);}
            AppConfigFile config = new Gson().fromJson(readFile(file), AppConfigFile.class);

            // TODO: the detection could be improved.
            AppConfigInfo item = null;
            if (getBuildType().get().equals("release")) {
                item = config.getAppInfos().get(0);
            } else if (getBuildType().get().equals("debug")) {
                item = config.getAppInfos().get(1);
            }

            if (item != null) {
                this.appId       = item.getAppId();
                this.packageName = item.getPackageName();
            }
        }
        return true;
    }

    boolean authenticate() {

        HttpPost request = new HttpPost(EndpointUrl.OAUTH2_TOKEN);
        request.setHeaders(getDefaultHeaders());

        String payload = new Gson().toJson(new TokenRequest(this.clientId, this.clientSecret));
        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        try {
            client.execute(request, response -> {
                int statusCode = response.getCode();
                if (statusCode == HttpStatus.SC_OK) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    TokenResponse result = new Gson().fromJson(rd.readLine(), TokenResponse.class);
                    this.accessToken = result.getAccessToken();
                    return true;
                } else {
                    this.stdErr("\n> HTTP " + statusCode + " " + response.getReasonPhrase());
                }
                return null;
            });
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
                .addRequestInterceptorFirst((HttpRequestInterceptor) (request, details, context) ->
                        stdOut("> " + request.getRequestUri()))
                .addResponseInterceptorLast((HttpResponseInterceptor) (response, details, context) ->
                        stdOut("> " + response.toString()));
        }
        return cb.build();
    }

    @NotNull
    String getTransferRate(long kilobytes, long ms) {
        long rate = kilobytes / (ms / 1000) * 1024; // bytes per second
        int u = 0;
        for (; rate > 1024*1024; rate >>= 10) {u++;}
        if (rate > 1024) {u++;}
        return String.format(Locale.ROOT, "%.1f %cB", rate/1024f, " kMGTPE".charAt(u))+ "/s";
    }

    @Input
    Header[] getDefaultHeaders() {
        Header[] headers;
        if (this.accessToken == null) {
            headers = new Header[2];
            headers[0] = new BasicHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            headers[1] = new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        } else {
            headers = new Header[4];
            headers[0] = new BasicHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            headers[1] = new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            headers[2] = new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
            headers[3] = new BasicHeader("client_id", this.clientId);
        }
        return headers;
    }

    void stdOut(@NotNull String value) {
        System.out.println(value);
    }

    void stdErr(@NotNull String value) {
        System.err.println(value);
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
}
