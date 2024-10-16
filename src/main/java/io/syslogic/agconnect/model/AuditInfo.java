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
    private Integer copyRightAuditResult;

    /**
     * Copyright review comments.
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("copyRightAuditOpinion")
    private String copyRightAuditOpinion;

    /**
     * Publication approval number review result.
     * The options are 0 (passed) and 1 (failed).
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("copyRightCodeAuditResult")
    private Integer copyRightCodeAuditResult;

    /**
     * Publication approval number review comments.
     * This parameter is returned only for apps released in the Chinese mainland.
     */
    @SerializedName("copyRightCodeAuditOpinion")
    private String copyRightCodeAuditOpinion;

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

    /** @return audit opinion. */
    @SuppressWarnings({"unused"})
    public String getAuditOpinion() {
        return this.auditOpinion;
    }

    /** @return copyright audit result. */
    @SuppressWarnings({"unused"})
    public Integer getCopyRightAuditResult() {
        return this.copyRightAuditResult;
    }

    /** @return copyright audit opinion. */
    @SuppressWarnings({"unused"})
    public String getCopyRightAuditOpinion() {
        return this.copyRightAuditOpinion;
    }

    /** @return copyright code-audit result. */
    @SuppressWarnings({"unused"})
    public Integer getCopyRightCodeAuditResult() {
        return this.copyRightCodeAuditResult;
    }

    /** @return copyright code-audit opinion. */
    @SuppressWarnings({"unused"})
    public String getCopyRightCodeAuditOpinion() {
        return this.copyRightCodeAuditOpinion;
    }

    /** @return record audit result. */
    @SuppressWarnings({"unused"})
    public Integer getRecordAuditResult() {
        return this.recordAuditResult;
    }

    /** @return record audit opinion. */
    @SuppressWarnings({"unused"})
    public String getRecordAuditOpinion() {
        return this.recordAuditOpinion;
    }

    @Override
    public String toString() {
        return "AuditInfo {"+
            "auditOpinion: \"" + this.auditOpinion + "\", " +
            "copyRightAuditResult: \"" + this.copyRightAuditResult + "\", " +
            "copyRightAuditOpinion: \"" + this.copyRightAuditOpinion + "\", " +
            "copyRightCodeAuditResult: \"" + this.copyRightCodeAuditResult + "\", " +
            "copyRightCodeAuditOpinion: \"" + this.copyRightCodeAuditOpinion + "\", " +
            "recordAuditResult: \"" + this.recordAuditResult + "\", " +
            "recordAuditOpinion: \"" + this.recordAuditOpinion + "\" " +
        "}";
    }
}
