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

import io.syslogic.agconnect.model.AppIdListResponse;
import io.syslogic.agconnect.model.AppInfoAppId;
import io.syslogic.agconnect.model.UploadFileListItem;

/**
 * Abstract AppIdList {@link BaseTask}
 *
 * @author Martin Zeitler
 */
abstract public class AppIdListTask extends BaseTask {

    @Input
    public abstract Property<Boolean> getVerbose();

    @Input
    abstract public Property<String> getAppConfigFile();

    @Input
    abstract public Property<String> getApiConfigFile();

    @Input
    abstract public Property<String> getBuildType();

    /** The default {@link TaskAction}. */
    @TaskAction
    public void run() {
        this.setup(getProject(), getAppConfigFile().get(), getApiConfigFile().get(), getVerbose().get());
        if (getVerbose().get()) {this.stdOut("Getting ID list for package " + this.packageName + ".");}
        this.authenticate();
        this.getAppIdList();
    }

    /** */
    @SuppressWarnings("UnusedReturnValue")
    public void getAppIdList() {

        HttpGet request = new HttpGet();
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken);
        request.setHeader("client_id", this.clientId);
        try {
            URIBuilder builder = new URIBuilder(ENDPOINT_PUBLISH_APP_ID_LIST);
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
