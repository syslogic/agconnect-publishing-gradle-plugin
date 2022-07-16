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
     * ```groovy
     * agcPublishing {
     *     apiConfigFile = "..."
     * }
     * ```
     * @return path to the JSON file.
     */
    String getApiConfigFile();

    /**
     * Enable verbose logging.
     *
     * ```groovy
     * agcPublishing {
     *     verbose = true
     * }
     * ```
     * @return verbose logging on/off.
     */
    Boolean getVerbose();
}
