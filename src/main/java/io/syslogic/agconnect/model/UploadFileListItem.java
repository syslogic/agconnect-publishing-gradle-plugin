package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Abstract Model: UploadFileListItem
 *
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class UploadFileListItem {

    @SerializedName("fileDestUlr")
    private String destinationUrl;

    @SerializedName("disposableURL")
    private String disposableUrl;

    @SerializedName("purifiedForFile")
    private boolean purifiedForFile;

    @SerializedName("size")
    private int size;

    public String getDestinationUrl() {
        return this.destinationUrl;
    }

    public String getDisposableUrl() {
        return this.disposableUrl;
    }

    public boolean getPurifiedForFile() {
        return this.purifiedForFile;
    }

    public int getSize() {
        return this.size;
    }

    public String getSizeFormatted() {
        int u = 0;
        for (; this.size > 1024*1024; this.size >>= 10) {u++;}
        if (this.size > 1024) {u++;}
        return String.format(Locale.ROOT, "%.1f %cB", this.size/1024f, " kMGTPE".charAt(u));
    }
}
