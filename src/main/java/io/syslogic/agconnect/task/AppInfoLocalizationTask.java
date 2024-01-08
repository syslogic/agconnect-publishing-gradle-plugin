package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.gradle.api.tasks.TaskAction;

import java.nio.charset.StandardCharsets;

import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.model.AppInfoLocalization;

/**
 * TODO: Abstract AppInfo Localization Update {@link BaseTask}
 *
 * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-language-info-update-0000001158245057">Updating App Localization Information</a>
 * @author Martin Zeitler
 */
abstract public class AppInfoLocalizationTask extends BaseTask {

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get(), null)) {
            if (getVerbose().get()) {this.stdOut("Update localized AppInfo for appId " + this.appId + ".");}
            if (this.authenticate()) {
                this.updateAppInfoLocalization();
            }
        }
    }

    /** It updates localized app information. */
    public void updateAppInfoLocalization() {

        HttpPut request = new HttpPut();
        request.setHeaders(getDefaultHeaders());

        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_LANG_INFO);
            builder.setParameter("appId", String.valueOf(this.appId));
            request.setURI(builder.build());

            /* TODO... */
            AppInfoLocalization item = new AppInfoLocalization();
            request.setEntity(new StringEntity(new Gson().toJson(item), StandardCharsets.UTF_8));

            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);

            if (statusCode == HttpStatus.SC_OK) {
                // AppInfoResponse appInfo = new Gson().fromJson(result, AppInfoResponse.class);
                if (getVerbose().get()) { /* Logging the URL to the "App information" page. */
                    this.stdOut(EndpointUrl.AG_CONNECT_APP_INFO.replace("{appId}", String.valueOf(this.appId)));
                } else {
                    this.stdOut("> AppInfo localization updated");
                    this.stdOut("> " + result);
                }
            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
