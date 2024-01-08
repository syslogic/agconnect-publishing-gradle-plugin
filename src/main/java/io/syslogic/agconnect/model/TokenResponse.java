package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: TokenResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class TokenResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private int expiresIn;

    /** @return the access-token. */
    public String getAccessToken() {
        return this.accessToken;
    }

    /** @return token expiry in seconds. */
    public int getExpiresIn() {
        return this.expiresIn;
    }
}
