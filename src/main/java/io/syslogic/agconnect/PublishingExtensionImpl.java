package io.syslogic.agconnect;

/**
 * Public API for Gradle build scripts.
 *
 * @author Martin Zeitler
 */
public class PublishingExtensionImpl implements PublishingExtension {

    private String configFile = null;
    private String assetDirectory = null;
    private Boolean verbose = false;
    private Boolean logHttp = false;

    /**
     * Define the path to the API client configuration file.
     *
     * @param value the absolute path to the configuration JSON.
     */
    @SuppressWarnings("unused")
    public void setConfigFile(String value) {
        this.configFile = value;
    }

    /**
     * Define the dirname for the directory into which to download the assets.
     * Note: It is intended to provide compatibility to to Play Store publisher.
     *
     * <code>agcPublishing {assetDirName = "play"}</code>
     * @param value the directory name to use instead of `agconnect`.
     */
    @SuppressWarnings("unused")
    public void setAssetDirectory(String value) {
        this.assetDirectory = value;
    }

    /**
     * Verbose logging.
     *
     * @param value whether true or false.
     */
    @SuppressWarnings("unused")
    public void setVerbose(Boolean value) {
        this.verbose = value;
    }

    /**
     * HTTP logging.
     *
     * @param value whether true or false.
     */
    @SuppressWarnings("unused")
    public void setLogHttp(Boolean value) {
        this.logHttp = value;
    }

    /** @return the path to the API client configuration JSON file. */
    @Override
    public String getConfigFile() {
        return this.configFile;
    }

    /** @return the name of the asset directory. */
    @Override
    public String getAssetDirectory() {
        return this.assetDirectory;
    }

    /** @return verbose logging value. */
    @Override
    public Boolean getVerbose() {
        return this.verbose;
    }

    /** @return HTTP logging value. */
    @Override
    public Boolean getLogHttp() {
        return this.logHttp;
    }
}
