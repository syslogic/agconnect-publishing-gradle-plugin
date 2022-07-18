package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppConfig
 *
 * This is file agconnect-services.json, where only appInfo.appId is of interest.
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppConfigFile {

    @SerializedName("configuration_version")
    private String configVersion;

    @SerializedName("app_info")
    private AppConfigInfo appInfo;

    public String getConfigVersion() {
        return this.configVersion;
    }
    public AppConfigInfo getAppInfo() {
        return this.appInfo;
    }
}
