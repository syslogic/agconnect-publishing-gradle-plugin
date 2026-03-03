package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data Model: AppInfoLanguage
 * @author Martin Zeitler
 */
public class AppInfoLanguage {

    @SerializedName("lang")
    private String lang;

    @SerializedName("appName")
    private String appName;

    @SuppressWarnings("unused")
    @SerializedName("deviceMaterials")
    private List<AppInfoDeviceMaterial> deviceMaterials;

    /** Constructor */
    public AppInfoLanguage() {}

    @Override
    public String toString() {
        return "AppInfo {"+
            "lang: " + this.lang + ", " +
            "appName: " + this.appName + ", " +
            "deviceMaterials: " + this.deviceMaterials.size() +
        "}";
    }
}
