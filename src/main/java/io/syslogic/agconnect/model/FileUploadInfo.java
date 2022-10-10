package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: FileUploadInfo
 *
 * @author Martin Zeitler
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class FileUploadInfo {

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("fileDestUrl")
    private String fileDestUrl;

    public FileUploadInfo(String name, String url) {
        this.fileName = name;
        this.fileDestUrl = url;
    }

    @Override
    public String toString() {
        return "FileUploadInfo {" +
            "fileName: \"" + this.fileName + "\", " +
            "fileDestUrl: \"" + this.fileDestUrl + "\" " +
        "}";
    }
}
