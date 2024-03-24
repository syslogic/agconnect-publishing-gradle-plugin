package io.syslogic.agconnect.constants;

/**
 * API Result Codes
 * @see <a href="https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-publishingapi-errorcode-0000001163523297">Result Codes</a>
 * @author Martin Zeitler
 */
@SuppressWarnings("unused")
public class ResultCode {

    /** API result code. */
    public static final int SUCCESS                  = 0;

    /** API result code. */
    public static final int INVALID_INPUT_PARAMETER  = 204144641;

    /** API result code. */
    public static final int UNKNOWN_SYSTEM_ERROR     = 204144642;

    /** API result code. */
    public static final int QUERY_DEV_ACCOUNT_INFO   = 204144643;

    /** API result code. */
    public static final int QUERY_APP_INFO           = 204144644;

    /** API result code. */
    public static final int OBTAIN_UPLOAD_AUTH_CODE  = 204144645;
    /** API result code. */

    public static final int FAILED_TO_UPLOAD_PACKAGE = 204144646;

    public static final int FAILED_TO_UPDATE_PACKAGE = 204144647;

    /** API result code. */
    public static final int ADD_APK_HAS_FAILED       = 204144662;
}
