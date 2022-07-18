package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: ApiConfig
 *
 * This is file credentials/agc-apiclient.json.
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class ApiConfigFile {

    @SerializedName("configuration_version")
    private String configVersion;

    /**
     * This value must be `team_client_id` and not `project_client_id`,
     * else the result is: HTTP/1.1 403 client token authorization fail.
     */
    @SerializedName("type")
    private String role;

    @SerializedName("developer_id")
    private String developerId;

    @SerializedName("project_id")
    private String projectId;

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String clientSecret;

    @SerializedName("region")
    private String region;

    public String getConfigVersion() {
        return this.configVersion;
    }
    public String getRole() {
        return this.role;
    }
    public String getDeveloperId() {
        return this.developerId;
    }
    public String getProjectId() {
        return this.projectId;
    }
    public String getClientId() {
        return this.clientId;
    }
    public String getClientSecret() {
        return this.clientSecret;
    }
    public String getRegion() {
        return this.region;
    }
}
