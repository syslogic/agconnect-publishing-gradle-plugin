package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

import org.gradle.api.tasks.TaskAction;

import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.model.AppIdListResponse;
import io.syslogic.agconnect.model.AppInfoAppId;

/**
 * Abstract AppId {@link BaseTask}
 * @author Martin Zeitler
 */
abstract public class AppIdTask extends BaseTask {

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get(), null)) {
            if (getVerbose().get()) {this.stdOut("Getting ID list for package " + this.packageName + ".");}
            if (this.authenticate()) {
                this.getAppIdList();
            }
        }
    }

    /** */
    public void getAppIdList() {
        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_ID_LIST);
            builder.setParameter("packageName", String.valueOf(this.packageName));
            HttpGet request = new HttpGet(builder.build());
            request.setHeaders(getDefaultHeaders());

            client.execute(request, response -> {
                int statusCode = response.getCode();

                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);

                AppIdListResponse appIds = null;
                if (statusCode == HttpStatus.SC_OK) {
                    appIds = new Gson().fromJson(result, AppIdListResponse.class);
                    for (AppInfoAppId item : appIds.getAppIds()) {
                        this.stdOut(item.toString());
                    }
                } else {
                    this.stdErr("HTTP " + statusCode + " " + response.getReasonPhrase());
                }
                return appIds;
            });

        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
