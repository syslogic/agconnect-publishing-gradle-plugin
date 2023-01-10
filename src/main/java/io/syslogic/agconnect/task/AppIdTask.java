package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import org.gradle.api.tasks.TaskAction;

import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.model.AppIdListResponse;
import io.syslogic.agconnect.model.AppInfoAppId;

/**
 * Abstract AppId {@link BaseTask}
 *
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
    @SuppressWarnings("UnusedReturnValue")
    public void getAppIdList() {

        HttpGet request = new HttpGet();
        request.setHeaders(getDefaultHeaders());

        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_ID_LIST);
            builder.setParameter("packageName", String.valueOf(this.packageName));

            request.setURI(builder.build());
            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);

            if (statusCode == HttpStatus.SC_OK) {
                AppIdListResponse appIds = new Gson().fromJson(result, AppIdListResponse.class);
                for (AppInfoAppId item : appIds.getAppIds()) {
                    this.stdOut(item.toString());
                }
            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }

        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
