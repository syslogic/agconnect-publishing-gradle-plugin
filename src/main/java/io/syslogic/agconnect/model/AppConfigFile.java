package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppConfig
 *
 * This is file agconnect-services.json, where only appId is of interest.
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppConfigFile {

    @SerializedName("client")
    private AppConfigClient client;

    public AppConfigClient getClient() {
        return this.client;
    }
}
