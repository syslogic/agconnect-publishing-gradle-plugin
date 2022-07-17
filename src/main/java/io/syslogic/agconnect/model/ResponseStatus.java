package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: ResponseStatus
 *
 * @author Martin Zeitler
 */
public class ResponseStatus {

    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
