package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppSubmitResponse
 *
 * @author Martin Zeitler
 */
public class AppSubmitResponse {

    @SerializedName("ret")
    private ResponseStatus ret;

    /** @return response status. */
    public ResponseStatus getRet() {
        return this.ret;
    }
}
