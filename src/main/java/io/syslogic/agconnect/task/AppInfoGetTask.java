package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import org.apache.http.util.EntityUtils;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import io.syslogic.agconnect.model.AppInfoResponse;
import io.syslogic.agconnect.model.Endpoint;

/**
 * Abstract AppInfo {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class AppInfoGetTask extends BaseTask {

    @Input
    abstract public Property<String> getAppConfigFile();

    @Input
    abstract public Property<String> getApiConfigFile();

    @Input
    abstract public Property<String> getBuildType();

    @Input
    public abstract Property<Boolean> getLogHttp();

    @Input
    public abstract Property<Boolean> getVerbose();

    /**
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-reference-langtype-0000001158245079">Languages</a>.
     */
    // String lang = "en-US";
    String lang = null;

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get())) {
            if (getVerbose().get()) {this.stdOut("Query AppInfo for appId " + this.appId + ".");}
            this.authenticate();
            this.getAppInfo();
        }
    }

    /**
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-app-info-query-0000001158365045">Querying App Information</a>.
     */
    @SuppressWarnings("UnusedReturnValue")
    public void getAppInfo() {
        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            URIBuilder builder = new URIBuilder(Endpoint.PUBLISH_APP_INFO);
            builder.setParameter("appId", String.valueOf(this.appId));

            /* If this parameter is not passed, app information in all languages is queried. */
            if (this.lang != null) {builder.setParameter("lang", this.lang);}

            request.setURI(builder.build());
            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);

            if (statusCode == HttpStatus.SC_OK) {

                AppInfoResponse appInfo = new Gson().fromJson(result, AppInfoResponse.class);
                appInfo.getAppInfo().setPackageName(this.packageName); // adding an additional field.

                /* always logging the response */
                this.stdOut(appInfo.getAppInfo().toString());

                if (getVerbose().get()) {
                    this.stdOut("https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/myApp");
                }
            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
