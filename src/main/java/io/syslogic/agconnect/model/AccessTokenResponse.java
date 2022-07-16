package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Abstract Model: AccessTokenResponse
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class AccessTokenResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private int expiresIn;

    public String getAccessToken() {
        return this.accessToken;
    }

    public int getExpiresIn() {
        return this.expiresIn;
    }
}
