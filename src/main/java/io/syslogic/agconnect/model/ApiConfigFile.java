package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: ApiConfig
 * This is the API client config file.
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class ApiConfigFile {

    @SerializedName("type")
    private String type; // must be `team_client_id`.

    @SerializedName("configuration_version")
    private String configVersion;

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

    public Object getType() {
        return this.type;
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
