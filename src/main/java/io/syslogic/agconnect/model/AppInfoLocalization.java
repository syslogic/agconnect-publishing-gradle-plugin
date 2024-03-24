package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppInfoLocalization
 *
 * @author Martin Zeitler
 */
public class AppInfoLocalization {

    @SerializedName("lang")
    private String lang;

    @SerializedName("appName")
    private String appName;

    @SerializedName("appDesc")
    private String appDesc;

    @SerializedName("briefInfo")
    private String briefInfo;

    @SerializedName("newFeatures")
    private String newFeatures;

    @Override
    public String toString() {
        return "AppInfoLocalization {"+
            "lang: \"" + this.lang + "\", " +
            "appName: \"" + this.appName + "\", " +
            "appDesc: \"" + this.appDesc + "\", " +
            "briefInfo: \"" + this.briefInfo + "\", " +
            "newFeatures: \"" + this.newFeatures + "\", " +
        "}";
    }
}
