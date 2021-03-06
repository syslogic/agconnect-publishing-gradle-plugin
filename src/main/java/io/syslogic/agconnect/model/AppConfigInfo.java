package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppConfigInfo
 *
 * This is file agconnect-services.json, where only appId is of interest.
 *
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
