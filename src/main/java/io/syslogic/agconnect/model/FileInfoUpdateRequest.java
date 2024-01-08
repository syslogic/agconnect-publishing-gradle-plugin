package io.syslogic.agconnect.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: FileInfoUpdateRequest
 *
 * @author Martin Zeitler
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class FileInfoUpdateRequest {

    @SerializedName("lang")
    private String lang = "en-US";

    @SerializedName("fileType")
    private int fileType = 5;

    // only one item!
    @SuppressWarnings("FieldCanBeLocal")
    @SerializedName("files")
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
