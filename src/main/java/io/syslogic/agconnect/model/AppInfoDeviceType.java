package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: AppInfoDeviceType
 * @author Martin Zeitler
 */
public class AppInfoDeviceType {

    @SerializedName("deviceType")
    private int deviceType;

    /** @return the deviceType. */
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
