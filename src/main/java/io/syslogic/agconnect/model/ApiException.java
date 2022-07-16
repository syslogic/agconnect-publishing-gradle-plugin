package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: ApiException
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class ApiException {

    @SerializedName("errorCode")
    private int errorCode;

    @SerializedName("errorDesc")
    private String errorDesc;

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorDesc() {
        return this.errorDesc;
    }
}
