package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: UploadResponse
 * @author Martin Zeitler
 */
public class UploadResponse {

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("CException")
    private ResponseStatus status;

    @SerializedName("UploadFileRsp")
    private UploadFileResponse result;

    /** @return the result code. */
    public int getResultCode() {return this.resultCode;}

    /** @return the response status. */
    public ResponseStatus getStatus() {return this.status;}

    /** @return the upload-file response result. */
    public UploadFileResponse getResult() {
        return this.result;
    }
}
