package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Abstract Model: AppInfoDeviceType
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppInfoDeviceType {

    @SerializedName("deviceType")
    private int deviceType;

    public int getDeviceType() {
        return this.deviceType;
    }

    @Override
    public String toString() {
        return "AppDeviceType {"+
            "deviceType: " + this.deviceType +
        "}";
    }
}
