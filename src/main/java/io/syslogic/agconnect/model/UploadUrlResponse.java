package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: UploadUrlResponse
 * @author Martin Zeitler
 */
public class UploadUrlResponse {

    @SerializedName("ret")
    private ResponseStatus status;

    @SerializedName("uploadUrl")
    private String uploadUrl;

    @SerializedName("chunkUploadUrl")
    private String chunkUploadUrl;

    @SerializedName("authCode")
    private String authCode;

    /** Constructor */
    public UploadUrlResponse() {}

    /**
     * ResponseStatus
     * @return the response status.
     */
    public ResponseStatus getStatus() {
        return this.status;
    }

    /**
     * UploadUrl
     * @return the upload URL.
     */
    public String getUploadUrl() {
        return this.uploadUrl;
    }

    /**
     * ChunkUploadUrl
     * @return the chunked upload URL.
     */
    public String getChunkUploadUrl() {
        return this.chunkUploadUrl;
    }

    /**
     * AuthCode
     * @return the upload auth-code.
     */
    public String getAuthCode() {
        return this.authCode;
    }
}
