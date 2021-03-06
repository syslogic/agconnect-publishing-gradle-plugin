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
    private ResponseStatus status;

    @SerializedName("UploadFileRsp")
    private UploadFileResponse result;

    public int getResultCode() {return this.resultCode;}

    public ResponseStatus getStatus() {return this.status;}

    public UploadFileResponse getResult() {
        return this.result;
    }
}
