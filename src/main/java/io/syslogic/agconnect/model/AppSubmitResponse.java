package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: AppSubmitResponse
 * @author Martin Zeitler
 */
public class AppSubmitResponse {

    @SerializedName("ret")
    private ResponseStatus ret;

    /** Constructor */
    public AppSubmitResponse() {}

    /**
     * ResponseStatus
     * @return response status.
     */
    public ResponseStatus getRet() {
        return this.ret;
    }
}
