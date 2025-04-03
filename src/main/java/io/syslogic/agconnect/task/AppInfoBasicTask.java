package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import org.gradle.api.tasks.TaskAction;

import java.nio.charset.StandardCharsets;

import io.syslogic.agconnect.constants.ConsoleUrl;
import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.model.AppInfo;

/**
 * TODO: Abstract AppInfo Basic Update {@link BaseTask}
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

    /** It updates basic app information. */
    public void updateAppInfoBasic() {
        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_INFO);
            builder.setParameter("appId", String.valueOf(this.appId));
            builder.setParameter("releaseType", String.valueOf(this.releaseType));

            HttpPut request = new HttpPut(builder.build());
            request.setHeaders(getDefaultHeaders());

            /* TODO... */
            AppInfo item = new AppInfo();
            request.setEntity(new StringEntity(new Gson().toJson(item), StandardCharsets.UTF_8));

            client.execute(request, response -> {
                int statusCode = response.getCode();

                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);

                if (statusCode == HttpStatus.SC_OK) {
                    // AppInfoResponse appInfo = new Gson().fromJson(result, AppInfoResponse.class);
                    if (getVerbose().get()) { /* Logging the URL to the "App information" page. */
                        this.stdOut(ConsoleUrl.APP_INFO.replace("{appId}", String.valueOf(this.appId)));
                    } else {
                        this.stdOut("> AppInfo updated");
                        this.stdOut("> " + result);
                    }
                } else {
                    this.stdErr("HTTP " + statusCode + " " + response.getReasonPhrase());
                }

                return null;
            });

        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
