package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: TokenRequest
 * @author Martin Zeitler
 */
@SuppressWarnings({"FieldMayBeFinal"})
public class TokenRequest {

    @SerializedName("grant_type")
    private String grantType = "client_credentials";

    @SerializedName("client_id")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String clientId;
    
    @SerializedName("client_secret")
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String clientSecret;

    /**
     * Constructor
     * @param clientId the client ID
     * @param secret the client secret
     */
    public TokenRequest(String clientId, String secret) {
        this.clientId = clientId;
        this.clientSecret = secret;
    }
}
