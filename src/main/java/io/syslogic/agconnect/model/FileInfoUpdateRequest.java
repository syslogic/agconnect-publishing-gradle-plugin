package io.syslogic.agconnect.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Data Model: FileInfoUpdateRequest
 * @author Martin Zeitler
 */
@SuppressWarnings({"FieldMayBeFinal"})
public class FileInfoUpdateRequest {

    @SerializedName("lang")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String lang = "en-US";

    @SerializedName("fileType")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int fileType = 5;

    // only one item!
    @SerializedName("files")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private FileUploadInfo fileInfo;

    /**
     * Constructor
     * @param fileName the local file-name
     * @param destFileUrl the destination URL
     */
    public FileInfoUpdateRequest(String fileName, String destFileUrl) {
        this.fileInfo = new FileUploadInfo(fileName, destFileUrl);
    }

    /** @return JSON string. */
    public String toJson() {
        return new Gson().toJson(this);
    }
}
