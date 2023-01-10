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
import io.syslogic.agconnect.model.AppInfo;

/**
 * TODO: Abstract AppInfo Basic Update {@link BaseTask}
 *
 * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-app-info-update-0000001111685198">Updating App Basic Information</a>
 * @author Martin Zeitler
 */
abstract public class AppInfoBasicTask extends BaseTask {

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get(), null)) {
            this.releaseType = getReleaseType().get();
            if (getVerbose().get()) {this.stdOut("Update Basic AppInfo for appId " + this.appId + ".");}
            if (this.authenticate()) {
                this.updateAppInfoBasic();
            }
        }
    }

    public void updateAppInfoBasic() {

        HttpPut request = new HttpPut();
        request.setHeaders(getDefaultHeaders());

        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_INFO);
            builder.setParameter("appId", String.valueOf(this.appId));
            builder.setParameter("releaseType", String.valueOf(this.releaseType));
            request.setURI(builder.build());

            /* TODO... */
            AppInfo item = new AppInfo();
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
                    this.stdOut("> AppInfo updated");
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
