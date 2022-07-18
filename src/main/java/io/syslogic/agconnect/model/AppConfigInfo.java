package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppConfigInfo
 *
 * This is file agconnect-services.json, where only appId is of interest.
 * "app_info":{
 *     "app_id":"106668641",
 *     "package_name":"io.syslogic.audio"
 * },
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppConfigInfo {

    @SerializedName("app_id")
    private Long appId;

    @SerializedName("package_name")
    private String packageName;

    public Long getAppId() {
        return this.appId;
    }

    public String getPackageName() {
        return this.packageName;
    }
}
