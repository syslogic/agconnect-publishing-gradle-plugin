package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: FileUploadInfo
 *
 * @author Martin Zeitler
 */
@SuppressWarnings({"FieldMayBeFinal"})
public class FileUploadInfo {

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("fileDestUrl")
    private String fileDestUrl;

    /**
     * Constructor
     * @param fileName the local file-name
     * @param destFileUrl the destination URL
     */
    public FileUploadInfo(String fileName, String destFileUrl) {
        this.fileName = fileName;
        this.fileDestUrl = destFileUrl;
    }

    @Override
    public String toString() {
        return "FileUploadInfo {" +
            "fileName: \"" + this.fileName + "\", " +
            "fileDestUrl: \"" + this.fileDestUrl + "\" " +
        "}";
    }
}
