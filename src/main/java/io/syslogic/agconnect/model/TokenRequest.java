package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: TokenRequest
 *
 * @author Martin Zeitler
 */
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class TokenRequest {

    @SerializedName("grant_type")
    private String grantType = "client_credentials";

    @SerializedName("client_id")
    private String clientId;

    @SerializedName("client_secret")
    private String clientSecret;

    public TokenRequest(String clientId, String secret) {
        this.clientId = clientId;
        this.clientSecret = secret;
    }
}
