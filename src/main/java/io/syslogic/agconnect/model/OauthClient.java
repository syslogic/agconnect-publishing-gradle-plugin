package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: OauthClient
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class OauthClient {

    @SerializedName("cp_id")
    private String cpId;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("project_id")
    private String projectId;

    @SerializedName("app_id")
    private String appId;

    @SerializedName("api_key")
    private String apiKey;

    @SerializedName("package_name")
    private String packageName;

    public String getAppId() {
        return this.appId;
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
