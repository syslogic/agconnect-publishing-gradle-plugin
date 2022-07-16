package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppConfig
 * This is the app config file.
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppConfigFile {

    @SerializedName("client")
    private AppClientConfig client;

    public AppClientConfig getClient() {
        return this.client;
    }
}
