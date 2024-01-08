package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: ApiConfig
 *
 * This is file distribution/agc-apiclient.json.
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

    /** @return the configVersion. */
    public String getConfigVersion() {
        return this.configVersion;
    }
    /** @return the role. */
    public String getRole() {
        return this.role;
    }
    /** @return the developerId. */
    public String getDeveloperId() {
        return this.developerId;
    }
    /** @return the projectId. */
    public String getProjectId() {
        return this.projectId;
    }
    /** @return the clientId. */
    public String getClientId() {
        return this.clientId;
    }
    /** @return the clientSecret. */
    public String getClientSecret() {
        return this.clientSecret;
    }
    /** @return the region. */
    public String getRegion() {
        return this.region;
    }
}
