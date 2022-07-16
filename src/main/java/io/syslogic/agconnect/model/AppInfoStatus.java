package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppInfoStatus
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppInfoStatus {

    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String message;
}
