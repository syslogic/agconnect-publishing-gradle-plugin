package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: ResponseStatus
 * @author Martin Zeitler
 */
public class ResponseStatus {

    @SerializedName(value="errorCode", alternate={"code"})
    private int code;

    @SerializedName(value="errorDesc", alternate={"msg"})
    private String message;

    /** @return status code. */
    public int getCode() {
        return this.code;
    }

    /** @return status message. */
    public String getMessage() {
        return this.message;
    }
}
