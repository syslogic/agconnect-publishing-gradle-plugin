package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppConfigInfo
 * This is file agconnect-services.json, where only appId is of interest.
 *
 * @author Martin Zeitler
 */
public class AppConfigInfo {

    @SerializedName("package_name")
    private String packageName;

    @SerializedName("app_info")
    private AppInfoSimple appInfo;

    /** @return the appId. */
    public Long getAppId() {
        return this.appInfo.getAppId();
    }

    /** @return the packageName. */
    public String getPackageName() {
        return this.packageName;
    }
}
