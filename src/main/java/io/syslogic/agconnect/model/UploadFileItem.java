package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Data Model: UploadFileItem
 * @author Martin Zeitler
 */
public class UploadFileItem {

    @SerializedName("fileDestUlr")
    private String destinationUrl;

    @SerializedName("disposableURL")
    private String disposableUrl;

    @SerializedName("purifiedForFile")
    private boolean purifiedForFile;

    @SerializedName("size")
    private int size;

    /** @return destination URL. */
    public String getDestinationUrl() {
        return this.destinationUrl;
    }

    /** @return disposable URL. */
    public String getDisposableUrl() {
        return this.disposableUrl;
    }

    /** @return purified for file. */
    public boolean getPurifiedForFile() {
        return this.purifiedForFile;
    }

    /** @return size numeric. */
    public int getSize() {
        return this.size;
    }

    /** @return size formatted. */
    public String getSizeFormatted() {
        int u = 0;
        for (; this.size > 1024*1024; this.size >>= 10) {u++;}
        if (this.size > 1024) {u++;}
        return String.format(Locale.ROOT, "%.1f %cB", this.size/1024f, " kMGTPE".charAt(u));
    }
}
