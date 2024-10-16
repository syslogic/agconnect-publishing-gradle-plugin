package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: UploadUrlRequest
 * @author Martin Zeitler
 */
@SuppressWarnings({"FieldMayBeFinal"})
public class UploadUrlRequest {

    @SerializedName("appId")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String appId;

    @SerializedName("suffix")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String suffix; // apk/rpk/pdf/jpg/jpeg/png/bmp/mp4/mov/aab

    @SerializedName("releaseType")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int releaseType; // 1, 8

    /**
     * Constructor
     * @param appId the appId.
     * @param suffix the file-name suffix.
     * @param type the release type.
     */
    public UploadUrlRequest(String appId, String suffix, int type) {
        this.appId = appId;
        this.suffix = suffix;
        this.releaseType = type;
    }
}
