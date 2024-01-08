package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: UploadUrlResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class UploadUrlResponse {

    @SerializedName("ret")
    private ResponseStatus status;

    @SerializedName("uploadUrl")
    private String uploadUrl;

    @SerializedName("chunkUploadUrl")
    private String chunkUploadUrl;

    @SerializedName("authCode")
    private String authCode;

    /** @return the response status. */
    public ResponseStatus getStatus() {
        return this.status;
    }

    /** @return the upload URL. */
    public String getUploadUrl() {
        return this.uploadUrl;
    }

    /** @return the chunked upload URL. */
    public String getChunkUploadUrl() {
        return this.chunkUploadUrl;
    }

    /** @return the upload auth-code. */
    public String getAuthCode() {
        return this.authCode;
    }
}
