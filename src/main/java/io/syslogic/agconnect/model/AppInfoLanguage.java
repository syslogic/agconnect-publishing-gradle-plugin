package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Abstract Model: AppInfoLanguage
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppInfoLanguage {

    @SerializedName("lang")
    private String lang;

    @SerializedName("appName")
    private String appName;

    @SerializedName("deviceMaterials")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private List<AppInfoDeviceMaterial> deviceMaterials;

    @Override
    public String toString() {
        return "AppInfo {"+
            "lang: " + this.lang + ", " +
            "appName: " + this.appName + ", " +
            "deviceMaterials: " + this.deviceMaterials.size() +
        "}";
    }
}
