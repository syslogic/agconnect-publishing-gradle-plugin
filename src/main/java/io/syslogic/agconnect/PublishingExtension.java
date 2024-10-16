package io.syslogic.agconnect;

/**
 * Public API for Gradle build scripts.
 *
 * @author Martin Zeitler
 */
public interface PublishingExtension {

    /**
     * Define the path to the API client configuration JSON file.
     * <code>agcPublishing {apiConfigFile = "agc-apiclient-*.json"}</code>
     * @return path to the JSON file.
     */
    String getConfigFile();

    /**
     * Release-Type
     * <code>agcPublishing { releaseType = 1 }</code>
     * @return value 1=network, 3=phased.
     */
    Integer getReleaseType();

    /**
     * HTTP logging
     * <code>agcPublishing { logHttp = true }</code>
     * @return HTTP logging on/off.
     */
    Boolean getLogHttp();

    /**
     * Verbose logging
     * <code>agcPublishing { verbose = true }</code>
     * @return verbose logging on/off.
     */
    Boolean getVerbose();

    /**
     * Set the path to the API client configuration JSON file.
     * @param value path.
     */
    void setConfigFile(String value);

    /**
     * Set the Release Type.
     * <code>agcPublishing { releaseType = 1 }</code>
     * @param value 1=network, 3=phased.
     */
    @SuppressWarnings({"unused"})
     void setReleaseType(Integer value);

    /**
     * Set HTTP logging.
     * @param value on/off.
     */
    void setLogHttp(Boolean value);

    /**
     * Set Verbose logging.
     * @param value on/off.
     */
    void setVerbose(Boolean value);
}
