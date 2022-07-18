package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: CompilePackageState
 * Note: `aabCompileStatus` and `failReason` will be deprecated.
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class CompilePackageState {

     @SerializedName("pkgId")
    private String packageId;

    @SerializedName("successStatus")
    private int status;

    /**
     * App package ID.
     * @return the ID of the app package.
     */
    public String getPackageId() {
        return this.packageId;
    }

    /**
     * App package status
     *
     * 0: normal
     * 1: being parsed or failed (indicating that the app package is unavailable)
     * @return either 0 or 1.
     */
    public int getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "CompilePackageState {"+
            "packageId: \"" + this.packageId + "\", " +
            "status: \"" + this.status + "\"" +
        "}";
    }
}
