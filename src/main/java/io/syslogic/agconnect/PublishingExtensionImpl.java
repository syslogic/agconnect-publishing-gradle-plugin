package io.syslogic.agconnect;

/**
 * Public API for Gradle build scripts.
 *
 * @author Martin Zeitler
 */
public class PublishingExtensionImpl implements PublishingExtension {

    private String apiConfigFile = null;
    private Boolean verbose = false;

    /**
     * Define the path to the API client configuration JSON file.
     *
     * @param value the absolute path to the configuration JSON.
     */
    @SuppressWarnings("unused")
    public void setApiConfigFile(String value) {
        this.apiConfigFile = value;
    }
    @Override
    public String getApiConfigFile() {
        return this.apiConfigFile;
    }

    /**
     * Enable verbose logging.
     *
     * @param value wether true or false.
     */
    @SuppressWarnings("unused")
    public void setVerbose(Boolean value) {
        this.verbose = value;
    }
    @Override
    public Boolean getVerbose() {
        return this.verbose;
    }
}
