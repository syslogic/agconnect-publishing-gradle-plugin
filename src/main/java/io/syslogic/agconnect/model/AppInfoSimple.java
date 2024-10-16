package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: AppInfo Simple
 * @author Martin Zeitler
 */
public class AppInfoSimple {

    @SerializedName("app_id")
    private Long appId;

    @SerializedName("package_name")
    private String packageName;

    /** @return the App ID. */
    public Long getAppId() {
        return this.appId;
    }

    /** @return the Package-Name. */
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
