package io.syslogic.agconnect;

import org.jetbrains.annotations.NotNull;

/**
 * Public API for Gradle build scripts.
 * @author Martin Zeitler
 */
public class PublishingExtensionImpl implements PublishingExtension {

    private String configFile = null;
    private Integer releaseType = 1;
    private Boolean verbose = false;
    private Boolean logHttp = false;

    /**
     * Define the path to the API client configuration file.
     * <br><br>
     * <code>agcPublishing {configFile = ""}</code>
     * @param value the absolute path to the configuration JSON.
     */
    @SuppressWarnings("unused")
    public void setConfigFile(@NotNull String value) {
        this.configFile = value;
    }

    /**
     * Release Type.
     * <br><br>
     * <code>agcPublishing { releaseType = 1 }</code>
     * @param value 1=network, 5=phased.
     */
    public void setReleaseType(@NotNull Integer value) {
        if (value == 1 || value == 5) {
            this.releaseType = value;
        }
    }

    /**
     * Verbose logging.
     * <br><br>
     * <code>agcPublishing {verbose = true}</code>
     * @param value whether true or false.
     */
    @SuppressWarnings("unused")
    public void setVerbose(@NotNull Boolean value) {
        this.verbose = value;
    }

    /**
     * HTTP logging.
     * <br><br>
     * <code>agcPublishing {logHttp = true}</code>
     * @param value whether true or false.
     */
    @SuppressWarnings("unused")
    public void setLogHttp(@NotNull Boolean value) {
        this.logHttp = value;
    }

    /** @return the path to the API client configuration JSON file. */
    @Override
    public String getConfigFile() {
        return this.configFile;
    }

    /**
     * Release Type.
     *
     * <code>agcPublishing { releaseType = 1 }</code>
     *
     * @return value 1=network, 5=phased.
     */
    @Override
    public Integer getReleaseType() {
        return this.releaseType;
    }

    /** @return the current value for verbose logging. */
    @Override
    public Boolean getVerbose() {
        return this.verbose;
    }

    /** @return the current value for HTTP logging. */
    @Override
    public Boolean getLogHttp() {
        return this.logHttp;
    }
}
