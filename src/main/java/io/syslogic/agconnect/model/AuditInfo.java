package io.syslogic.agconnect.model;

import com.google.gson.annotations.SerializedName;

/**
 * Data Model: AuditInfo
 * @author Martin Zeitler
 */
public class AuditInfo {

    /** Overall review comments of an app. */
    @SerializedName("auditOpinion")
    private String auditOpinion;

    /**
     * Copyright review result.
     * The options are 0 (passed) and 1 (failed).
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("copyRightAuditResult")
    private Integer copyrightAuditResult;

    /**
     * Copyright review comments.
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("copyRightAuditOpinion")
    private String copyrightAuditOpinion;

    /**
     * Publication approval number review result.
     * The options are 0 (passed) and 1 (failed).
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("copyRightCodeAuditResult")
    private Integer copyrightCodeAuditResult;

    /**
     * Publication approval number review comments.
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("copyRightCodeAuditOpinion")
    private String copyrightCodeAuditOpinion;

    /**
     * ICP information review result.
     * The options are 0 (passed) and 1 (failed).
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("recordAuditResult")
    private Integer recordAuditResult;

    /**
     * ICP information review comments.
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("recordAuditOpinion")
    private String recordAuditOpinion;

    /** Constructor */
    public AuditInfo() {}

    /**
     * AuditOpinion
     * @return audit opinion.
     */
    @SuppressWarnings({"unused"})
    public String getAuditOpinion() {
        return this.auditOpinion;
    }

    /**
     * CopyrightAuditResult
     * @return copyright audit result.
     */
    @SuppressWarnings({"unused"})
    public Integer getCopyrightAuditResult() {
        return this.copyrightAuditResult;
    }

    /**
     * CopyrightAuditOpinion
     * @return copyright audit opinion.
     */
    @SuppressWarnings({"unused"})
    public String getCopyrightAuditOpinion() {
        return this.copyrightAuditOpinion;
    }

    /**
     * CopyrightCodeAuditResult
     * @return copyright code-audit result.
     */
    @SuppressWarnings({"unused"})
    public Integer getCopyrightCodeAuditResult() {
        return this.copyrightCodeAuditResult;
    }

    /**
     * CopyrightCodeAuditOpinion
     * @return copyright code-audit opinion.
     */
    @SuppressWarnings({"unused"})
    public String getCopyrightCodeAuditOpinion() {
        return this.copyrightCodeAuditOpinion;
    }

    /**
     * RecordAuditResult
     * @return record audit result.
     */
    @SuppressWarnings({"unused"})
    public Integer getRecordAuditResult() {
        return this.recordAuditResult;
    }

    /**
     * RecordAuditOpinion
     * @return record audit opinion.
     */
    @SuppressWarnings({"unused"})
    public String getRecordAuditOpinion() {
        return this.recordAuditOpinion;
    }

    @Override
    public String toString() {
        return "AuditInfo {"+
            "auditOpinion: \"" + this.auditOpinion + "\", " +
            "copyrightAuditResult: \"" + this.copyrightAuditResult + "\", " +
            "copyrightAuditOpinion: \"" + this.copyrightAuditOpinion + "\", " +
            "copyrightCodeAuditResult: \"" + this.copyrightCodeAuditResult + "\", " +
            "copyrightCodeAuditOpinion: \"" + this.copyrightCodeAuditOpinion + "\", " +
            "recordAuditResult: \"" + this.recordAuditResult + "\", " +
            "recordAuditOpinion: \"" + this.recordAuditOpinion + "\" " +
        "}";
    }
}
