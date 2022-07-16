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

    public ResponseStatus getStatus() {
        return this.status;
    }

    public String getUploadUrl() {
        return this.uploadUrl;
    }

    public String getChunkUploadUrl() {
        return this.chunkUploadUrl;
    }

    public String getAuthCode() {
        return this.authCode;
    }
}
