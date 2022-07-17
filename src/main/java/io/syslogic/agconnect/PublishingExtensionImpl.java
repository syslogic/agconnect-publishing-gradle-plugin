package io.syslogic.agconnect;

/**
 * Public API for Gradle build scripts.
 *
 * @author Martin Zeitler
 */
public class PublishingExtensionImpl implements PublishingExtension {

    private String apiConfigFile = null;
    private Boolean verbose = false;
    private Boolean logHttp = false;

    /**
     * Define the path to the API client configuration file.
     *
     * @param value the absolute path to the configuration JSON.
     */
    @SuppressWarnings("unused")
    public void setApiConfigFile(String value) {
        this.apiConfigFile = value;
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
    public String getApiConfigFile() {
        return this.apiConfigFile;
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
