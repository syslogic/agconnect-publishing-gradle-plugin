package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.gradle.api.tasks.TaskAction;

import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.constants.ResultCode;
import io.syslogic.agconnect.model.AppSubmitResponse;
import io.syslogic.agconnect.model.ResponseStatus;

/**
 * Abstract Submit Release {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class SubmitReleaseTask extends BaseTask {

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        if (this.configure(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getLogHttp().get(), getVerbose().get(), null)) {
            if (getVerbose().get()) {this.stdOut("Submitting release: " + this.packageName + " (" + this.appId + ").");}
            if (this.authenticate()) {
                this.submitForRelease();
            }
        }
    }

    /** Submit an app for release. */
    public void submitForRelease() {

        HttpPost request = new HttpPost();
        request.setHeaders(getDefaultHeaders());

        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_SUBMIT);
            builder.setParameter("appId", String.valueOf(this.appId));
            builder.setParameter("releaseType", String.valueOf(this.releaseType));
            // builder.setParameter("releaseTime", String.valueOf(this.releaseTime));
            // builder.setParameter("remark", "");

            request.setURI(builder.build());
            HttpResponse response = this.client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            String result = EntityUtils.toString(httpEntity);

            if (statusCode == HttpStatus.SC_OK) {
                AppSubmitResponse response1 = new Gson().fromJson(result, AppSubmitResponse.class);
                ResponseStatus status = response1.getRet();
                if (status.getCode() == ResultCode.SUCCESS) {
                    this.stdOut("Submitted for release: " + this.packageName + " (" + this.appId + ").");
                    this.stdOut(EndpointUrl.AG_CONNECT_INTEGRATION.replace("{appId}", String.valueOf(this.appId)));
                } else if (status.getCode() == ResultCode.INVALID_INPUT_PARAMETER) {
                    this.stdErr("Submitted for release: " + this.packageName + " (" + this.appId + ").");
                    this.stdErr("Error " + status.getCode() + ": " + status.getMessage());
                }
            } else {
                this.stdErr("HTTP " + statusCode + " " + response.getStatusLine().getReasonPhrase());
            }
        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
