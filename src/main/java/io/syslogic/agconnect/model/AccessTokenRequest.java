package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AccessTokenRequest
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AccessTokenRequest {

    @SerializedName("grant_type")
    private String grantType = "client_credentials";

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String secret;

    public AccessTokenRequest(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }
}
