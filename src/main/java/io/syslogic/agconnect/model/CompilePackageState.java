package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: CompilePackageState
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class CompilePackageState {

    @SerializedName("pkgId")
    private String packageId;

    @SerializedName("aabCompileStatus")
    private int compileState;

    @SerializedName("failReason")
    private int failureReason;

    @SerializedName("successStatus")
    private int status;

    public String getPackageId() {
        return this.packageId;
    }

    public int getCompileState() {
        return this.compileState;
    }

    public int getFailureReason() {
        return this.failureReason;
    }

    public int getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "CompilePackageState {"+
            "packageId: \"" + this.packageId + "\", " +
            "compileState: \"" + this.compileState + "\", " +
            "failureReason: \"" + this.failureReason + "\", " +
            "status: \"" + this.status + "\"" +
        "}";
    }
}
