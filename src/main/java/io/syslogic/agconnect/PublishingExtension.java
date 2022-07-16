package io.syslogic.agconnect;

import org.gradle.api.provider.Property;

/**
 * Huawei AppGallery Connect {@link PublishingExtension}
 *
 * @author Martin Zeitler
 */
public interface PublishingExtension {
    Property<Boolean> getVerbose();
    Property<String> getArtifactType();
    Property<String> getApiConfigFile();
}
