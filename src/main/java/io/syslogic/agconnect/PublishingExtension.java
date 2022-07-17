package io.syslogic.agconnect;

/**
 * Public API for Gradle build scripts.
 *
 * @author Martin Zeitler
 */
public interface PublishingExtension {

    /**
     * Define the path to the API client configuration JSON file.
     *
     * <code>agcPublishing {apiConfigFile = "agc-apiclient-*.json"}</code>
     * @return path to the JSON file.
     */
    String getApiConfigFile();

    /**
     * Enable HTTP logging.
     *
     * <code>agcPublishing { logHttp = true }</code>
     * @return HTTP logging on/off.
     */
    Boolean getLogHttp();

    /**
     * Enable verbose logging.
     *
     * <code>agcPublishing { verbose = true }</code>
     * @return verbose logging on/off.
     */
    Boolean getVerbose();
}
