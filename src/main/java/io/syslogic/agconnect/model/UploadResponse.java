package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: UploadResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class UploadResponse {

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("CException")
    private ApiException exception;

    @SerializedName("UploadFileRsp")
    private UploadFileResponse result;

    public int getResultCode() {return this.resultCode;}

    public ApiException getException() {return this.exception;}

    public UploadFileResponse getResult() {
        return this.result;
    }
}
