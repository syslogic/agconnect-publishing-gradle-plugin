package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppInfo Simple
 *
 * @author Martin Zeitler
 */
public class AppInfoSimple {

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

    @Override
    public String toString() {
        return "AppInfoSimple {" +
            "appId: \"" + this.getAppId() + "\", " +
            "packageName: \"" + this.getPackageName() + "\" " +
        "}";
    }
}
