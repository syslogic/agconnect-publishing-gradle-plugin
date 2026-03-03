package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data Model: AppIdListResponse
 * @author Martin Zeitler
 */
public class AppIdListResponse {

    @SerializedName("ret")
    private ResponseStatus ret;

    @SerializedName("appids")
    private List<AppInfoAppId> appIds;

    /** Constructor */
    public AppIdListResponse() {}

    /**
     * The ResponseStatus.
     * @return the ResponseStatus.
     */
    public ResponseStatus getStatus() {
        return this.ret;
    }

    /**
     * The appIds.
     * @return list of appIds.
     */
    public List<AppInfoAppId> getAppIds() {
        return this.appIds;
    }
}
