package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppInfoResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppInfoResponseWrap {

    @SerializedName("ret")
    private AppInfoStatus ret;

    @SerializedName("appInfo")
    private AppInfoResponse appInfo;

    public AppInfoStatus getRet() {
        return this.ret;
    }

    public AppInfoResponse getAppInfo() {
        return this.appInfo;
    }
}
