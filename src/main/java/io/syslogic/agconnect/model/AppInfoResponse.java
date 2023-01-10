package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppInfoResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppInfoResponse {

    @SerializedName("ret")
    private ResponseStatus ret;

    @SerializedName("appInfo")
    private AppInfo appInfo;

    public ResponseStatus getRet() {
        return this.ret;
    }

    public AppInfo getAppInfo() {
        return this.appInfo;
    }
}
