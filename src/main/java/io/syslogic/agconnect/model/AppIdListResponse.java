package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Abstract Model: AppIdListResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppIdListResponse {

    @SerializedName("ret")
    private ResponseStatus ret;

    @SerializedName("appids")
    private List<AppInfoAppId> appIds;

    public ResponseStatus getStatus() {
        return this.ret;
    }

    public List<AppInfoAppId> getAppIds() {
        return this.appIds;
    }
}
