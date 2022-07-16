package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Abstract Model: UploadFileResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class UploadFileResponse {

    @SerializedName("ifSuccess")
    private int ifSuccess;

    @SerializedName("resultCode")
    private int resultCode;

    @SerializedName("fileInfoList")
    private List<UploadFileListItem> fileList;

    public int getResultCode() {
        return this.resultCode;
    }

    public List<UploadFileListItem> getFileList() {
        return this.fileList;
    }
}
