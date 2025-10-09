package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Data Model: AppInfo
 * @author Martin Zeitler
 */
public class AppInfo {

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

    // for easier identification.
    private String packageName;

    /** @param value the packageName. */
    public void setPackageName(String value) {
        this.packageName = value;
    }

    @NotNull
    private String getDeviceTypesAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < this.deviceTypes.size(); i++) {
            sb.append(this.deviceTypes.get(i).getDeviceType());
            if (i != this.deviceTypes.size()-1) {sb.append(",");}
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String lang = (this.languages == null ? "null" : String.valueOf(this.languages.size()));
        String audit = (this.auditInfo == null ? "null" : this.auditInfo.toString());
        return "AppInfo {" +
            "updateTime: \"" + this.updateTime + "\", " +
            "packageName: \"" + this.packageName + "\", " +
            "developerName: \"" + this.developerName + "\", " +
            "parentType: " + this.parentType + ", " +
            "deviceTypes: \"" + this.getDeviceTypesAsString() + "\", " +
            "releaseState: " + this.releaseState + ", " +
            "certificateURLs: \"" + this.certificateURLs + "\", " +
            "defaultLang: \"" + this.defaultLang + "\", " +
            "languages: \"" + lang + "\", " +
            "auditInfo: \"" + audit + "\"" +
        "}";
    }
}
