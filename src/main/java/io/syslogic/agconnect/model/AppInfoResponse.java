package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: AppInfoResponse
 * @author Martin Zeitler
 */
public class AppInfoResponse {

    @SerializedName("ret")
    private ResponseStatus ret;

    @SerializedName("appInfo")
    private AppInfo appInfo;

    /** Constructor */
    public AppInfoResponse() {}

    /**
     * ResponseStatus
     * @return response status.
     */
    @SuppressWarnings({"unused"})
    public ResponseStatus getRet() {
        return this.ret;
    }

    /**
     * AppInfo
     * @return app information.
     */
    public AppInfo getAppInfo() {
        return this.appInfo;
    }
}
