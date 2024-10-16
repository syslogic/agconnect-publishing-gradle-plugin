package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Data Model: AppConfig
 * This is file agconnect-services.json, where only appInfo.appId is of interest.
 * @author Martin Zeitler
 */
public class AppConfigFile {

    @SerializedName("configuration_version")
    private String configVersion;

    @SerializedName("appInfos")
    private ArrayList<AppConfigInfo> appInfos;

    /** @return the configVersion. */
    @SuppressWarnings({"unused"})
    public String getConfigVersion() {
        return this.configVersion;
    }

    /** @return the AppConfigInfo. */
    public ArrayList<AppConfigInfo> getAppInfos() {
        return this.appInfos;
    }
}
