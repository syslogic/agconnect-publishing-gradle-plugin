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

    /** Constructor */
    public UploadFileItem() {}

    /**
     * Destination URL
     * @return destination URL.
     */
    public String getDestinationUrl() {
        return this.destinationUrl;
    }

    /**
     * Disposable URL
     * @return disposable URL.
     */
    public String getDisposableUrl() {
        return this.disposableUrl;
    }

    /**
     * PurifiedForFile
     * @return purified for file.
     */
    public boolean getPurifiedForFile() {
        return this.purifiedForFile;
    }

    /**
     * Item size numeric
     * @return size numeric.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Item size formatted
     * @return size formatted.
     */
    public String getSizeFormatted() {
        int u = 0;
        for (; this.size > 1024*1024; this.size >>= 10) {u++;}
        if (this.size > 1024) {u++;}
        return String.format(Locale.ROOT, "%.1f %cB", this.size/1024f, " kMGTPE".charAt(u));
    }
}
