package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: ResponseStatus
 *
 * @author Martin Zeitler
 */
public class ResponseStatus {

    @SerializedName(value="errorCode", alternate={"code"})
    private int code;

    @SerializedName(value="errorDesc", alternate={"msg"})
    private String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
