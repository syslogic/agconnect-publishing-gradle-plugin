package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppInfoAppId
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
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
