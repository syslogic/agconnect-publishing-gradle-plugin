package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: ResponseStatus
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class ResponseStatus {

    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
