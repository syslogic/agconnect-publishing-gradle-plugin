package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import org.apache.http.util.EntityUtils;
import org.gradle.api.tasks.TaskAction;

import io.syslogic.agconnect.model.AppInfoResponse;
import io.syslogic.agconnect.constants.EndpointUrl;

/**
 * Abstract AppInfo {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class AppInfoTask extends BaseTask {

    /**
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-reference-langtype-0000001158245079">Languages</a>.
     */
    // String lang = "en-US";
    String lang = null;

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get(), null)) {
            if (getVerbose().get()) {this.stdOut("Query AppInfo for appId " + this.appId + ".");}
            if (this.authenticate()) {
                this.getAppInfo();
            }
        }
    }

    /**
     * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-app-info-query-0000001158365045">Querying App Information</a>
     */
    @SuppressWarnings("UnusedReturnValue")
    public void getAppInfo() {

        HttpGet request = new HttpGet();
        request.setHeaders(getDefaultHeaders());

        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_INFO);
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

                /* Always logging the response. */
                this.stdOut(appInfo.getAppInfo().toString());

                /* Logging the URL to the "App information" page. */
                if (getVerbose().get()) {
                    this.stdOut(EndpointUrl.AG_CONNECT_APP_INFO.replace("{appId}", String.valueOf(this.appId)));
                }
            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
