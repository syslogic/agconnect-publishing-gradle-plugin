package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Data Model: UploadFileResponse
 * @author Martin Zeitler
 */
public class UploadFileResponse {

    @SerializedName("ifSuccess")
    private int ifSuccess;

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("fileInfoList")
    private List<UploadFileItem> fileList;

    /** Constructor */
    public UploadFileResponse() {}

    /**
     * Result Code
     * @return the result-code.
     */
    public int getResultCode() {
        return this.resultCode;
    }

    /**
     * List of {@link UploadFileItem}
     * @return a list of upload-file items.
     */
    public List<UploadFileItem> getFileList() {
        return this.fileList;
    }
}
