package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: FileInfoUpdateResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class FileInfoUpdateResponse {

    @SerializedName("ret")
    private ResponseStatus status;

    @SerializedName("pkgVersion")
    private String[] versions;

    public ResponseStatus getStatus() {
        return this.status;
    }

    public String[] getVersions() {
        return this.versions;
    }

    @Override
    public String toString() {
        return "FileUploadInfo {" +
            "status: \"" + this.getStatus() + "\", " +
            "versions: \"" + String.join(",", this.getVersions()) + "\" " +
        "}";
    }
}
