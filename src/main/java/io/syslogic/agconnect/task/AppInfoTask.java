package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.Consts;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.syslogic.agconnect.model.AppInfoResponseWrap;

/**
 * Abstract AppInfo {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class AppInfoTask extends BaseTask {

    @Input
    public abstract Property<Boolean> getVerbose();

    @Input
    abstract public Property<String> getAppConfigFile();

    @Input
    abstract public Property<String> getApiConfigFile();

    @Input
    abstract public Property<String> getBuildType();

    // https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-reference-langtype-0000001158245079
    String lang = "en-US";

    @TaskAction
    public void run() {
        this.parseConfigFiles(getAppConfigFile().get(), getApiConfigFile().get(), getVerbose().get());
        System.out.println("Query AppInfo for appId: " + this.appId);
        this.authenticate(this.clientId, this.clientSecret, getVerbose().get());
        this.getAppInfo();
    }

    // https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-app-info-query-0000001158365045
    public void getAppInfo() {
        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            request.setURI(new URIBuilder(ENDPOINT_PUBLISH_APP_INFO)
                    .setParameter("appId", this.appId)
                    .setParameter("lang", this.lang)
                    .build()
            );

            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {

                InputStream stream = response.getEntity().getContent();
                InputStreamReader isr = new InputStreamReader(stream, Consts.UTF_8);
                BufferedReader br = new BufferedReader(isr);

                String result = br.readLine();
                AppInfoResponseWrap appInfo = new Gson().fromJson(result, AppInfoResponseWrap.class);
                System.out.println(appInfo.getAppInfo().toString());

            } else {
                System.err.println("HTTP " + statusCode + " " +
                        response.getStatusLine().getReasonPhrase());
            }
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
