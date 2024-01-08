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

    /** @return response status. */
    public ResponseStatus getStatus() {
        return this.ret;
    }

    /** @return list of compile package states. */
    public List<CompilePackageState> getPackageState() {
        return this.packageState;
    }
}
