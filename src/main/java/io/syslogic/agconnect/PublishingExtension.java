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
    String getConfigFile();

    /**
     * Release Type.
     *
     * <code>agcPublishing { releaseType = 1 }</code>
     * @return value 1=network, 5=phased.
     */
    Integer getReleaseType();

    /**
     * Define the dirname for the directory into which to download the assets.
     * Note: It is intended to provide compatibility to to Play Store publisher.
     *
     * <code>agcPublishing {assetDirName = "play"}</code>
     * @return the name of the asset directory.
     */
    String getAssetDirectory();

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
