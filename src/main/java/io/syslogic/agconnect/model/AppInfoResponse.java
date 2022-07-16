package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Abstract Model: AppInfoResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppInfoResponse {

    @SerializedName("releaseState")
    private int releaseState;

    @SerializedName("defaultLang")
    private String defaultLang;

    @SerializedName("parentType")
    private int parentType;

    @SerializedName("developerNameCn")
    private String developerName;

    @SerializedName("certificateURLs")
    private String certificateURLs;

    @SerializedName("updateTime")
    private String updateTime;

    @SerializedName("deviceTypes")
    private List<AppInfoDeviceType> deviceTypes;

    @SerializedName("languages")
    private List<AppInfoLanguage> languages;

    @SerializedName("auditInfo")
    private AuditInfo auditInfo;

    @Override
    public String toString() {
        return "AppInfo {"+
            "releaseState: " + this.releaseState + ", " +
            "defaultLang: " + this.defaultLang + ", " +
            "parentType: " + this.parentType + ", " +
            "developerName: " + this.developerName + ", " +
            "certificateURLs: " + this.certificateURLs + ", " +
            "updateTime: " + this.updateTime +
        "}";
    }
}
