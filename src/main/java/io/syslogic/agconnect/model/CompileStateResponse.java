package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Abstract Model: CompileStatusResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class CompileStateResponse {

    @SerializedName("ret")
    private ResponseStatus ret;

    @SerializedName("pkgStateList")
    private List<CompilePackageState> packageState;

    public ResponseStatus getStatus() {
        return this.ret;
    }

    public List<CompilePackageState> getPackageState() {
        return this.packageState;
    }
}
