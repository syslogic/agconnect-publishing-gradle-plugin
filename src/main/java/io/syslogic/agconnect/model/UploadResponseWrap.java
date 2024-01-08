package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: UploadResultWrap
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class UploadResponseWrap {

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("result")
    private UploadResponse result;

    /** @return the result code. */
    public int getResultCode() {return this.resultCode;}

    /** @return the upload response result. */
    public UploadResponse getResult() {
        return this.result;
    }
}
