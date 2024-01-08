package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: UploadUrlRequest
 *
 * @author Martin Zeitler
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class UploadUrlRequest {

    @SerializedName("appId")
    private String appId;

    @SerializedName("suffix")
    private String suffix; // apk/rpk/pdf/jpg/jpeg/png/bmp/mp4/mov/aab

    @SerializedName("releaseType")
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
