package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: AppInfoAppId
 * @author Martin Zeitler
 */
public class AppInfoAppId {

    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private int value;

    @Override
    public String toString() {
        return "AppInfoAppId {"+
            "key: \"" + this.key + "\", " +
            "value: \"" + this.value + "\"" +
        "}";
    }
}
