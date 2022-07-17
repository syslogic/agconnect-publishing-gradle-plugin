package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AppClientConfig
 * This is the app config file; only appId is of interest.
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AppClientConfig {

    @SerializedName("project_id")
    private Long projectId;

    @SerializedName("app_id")
    private Long appId;

    @SerializedName("package_name")
    private String packageName;

    @SerializedName("cp_id")
    private String cpId;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("api_key")
    private String apiKey;

    public Long getAppId() {
        return this.appId;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public String getApiKey() {
        return this.apiKey;
    }
}
