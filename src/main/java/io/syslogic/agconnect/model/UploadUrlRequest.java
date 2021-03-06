package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: UploadUrlRequest
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class UploadUrlRequest {

    @SerializedName("appId")
    private String appId;

    @SerializedName("suffix")
    private String suffix; // apk/rpk/pdf/jpg/jpeg/png/bmp/mp4/mov/aab

    @SerializedName("releaseType")
    private int releaseType; // 1, 8

    public UploadUrlRequest(String appId, String suffix, int type) {
        this.appId = appId;
        this.suffix = suffix;
        this.releaseType = type;
    }
}
