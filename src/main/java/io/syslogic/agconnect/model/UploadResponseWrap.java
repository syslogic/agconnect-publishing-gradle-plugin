package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: UploadResultWrap
 * @author Martin Zeitler
 */
public class UploadResponseWrap {

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("result")
    private UploadResponse result;

    /** Constructor */
    public UploadResponseWrap() {}

    /**
     * Upload Result Code
     * @return the result code.
     */
    public int getResultCode() {return this.resultCode;}

    /**
     * Upload Response Result
     * @return the upload response result.
     */
    public UploadResponse getResult() {
        return this.result;
    }
}
