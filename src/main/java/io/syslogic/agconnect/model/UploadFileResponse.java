package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Abstract Model: UploadFileResponse
 *
 * @author Martin Zeitler
 */
public class UploadFileResponse {

    @SerializedName("ifSuccess")
    private int ifSuccess;

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("fileInfoList")
    private List<UploadFileItem> fileList;

    /** @return the result-code. */
    public int getResultCode() {
        return this.resultCode;
    }

    /** @return a list of upload-file items. */
    public List<UploadFileItem> getFileList() {
        return this.fileList;
    }
}
