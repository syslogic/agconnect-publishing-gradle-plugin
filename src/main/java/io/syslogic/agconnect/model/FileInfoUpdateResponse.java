package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: FileInfoUpdateResponse
 *
 * @author Martin Zeitler
 */
public class FileInfoUpdateResponse {

    @SerializedName("ret")
    private ResponseStatus status;

    @SerializedName("pkgVersion")
    private String[] versions;

    /** @return response status. */
    public ResponseStatus getStatus() {
        return this.status;
    }

    /** @return an array of package versions. */
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
