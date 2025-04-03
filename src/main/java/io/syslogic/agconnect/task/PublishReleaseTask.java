package io.syslogic.agconnect.task;

import com.google.gson.Gson;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

import org.gradle.api.tasks.TaskAction;

import io.syslogic.agconnect.constants.ConsoleUrl;
import io.syslogic.agconnect.constants.EndpointUrl;
import io.syslogic.agconnect.constants.ResultCode;
import io.syslogic.agconnect.model.AppSubmitResponse;
import io.syslogic.agconnect.model.ResponseStatus;

/**
 * Abstract Publish Release {@link BaseTask}
 * @author Martin Zeitler
 */
abstract public class PublishReleaseTask extends BaseTask {

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

        try {
            URIBuilder builder = new URIBuilder(EndpointUrl.PUBLISH_APP_SUBMIT);
            builder.setParameter("appId", String.valueOf(this.appId));
            builder.setParameter("releaseType", String.valueOf(this.releaseType));
            // builder.setParameter("releaseTime", String.valueOf(this.releaseTime));
            // builder.setParameter("remark", "");

            HttpPost request = new HttpPost(builder.build());
            request.setHeaders(getDefaultHeaders());

            client.execute(request, response -> {
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity);
                int statusCode = response.getCode();
                if (statusCode == HttpStatus.SC_OK) {
                    AppSubmitResponse response1 = new Gson().fromJson(result, AppSubmitResponse.class);
                    ResponseStatus status = response1.getRet();
                    if (status.getCode() == ResultCode.SUCCESS) {
                        this.stdOut("> Submitted for release: " + this.packageName + " (" + this.appId + ").");
                        this.stdOut(ConsoleUrl.INTEGRATION.replace("{appId}", String.valueOf(this.appId)));
                    } else if (status.getCode() == ResultCode.INVALID_INPUT_PARAMETER) {
                        this.stdErr("> Submitted for release: " + this.packageName + " (" + this.appId + ").");
                        this.stdErr("> Error " + status.getCode() + ": " + status.getMessage());
                    }
                } else {
                    this.stdErr("> HTTP " + statusCode + " " + response.getReasonPhrase());
                }
                return null;
            });

        } catch(Exception e) {
            this.stdErr(e.getMessage());
        }
    }
}
