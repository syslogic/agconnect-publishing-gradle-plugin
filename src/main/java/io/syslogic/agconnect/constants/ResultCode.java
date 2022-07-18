package io.syslogic.agconnect.constants;

/**
 * API Result Codes
 *
 * https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-publishingapi-errorcode-0000001163523297
 * @author Martin Zeitler
 */
public class ResultCode {
    public static final int SUCCESS                  = 0;
    public static final int INVALID_INPUT_PARAMETER  = 204144641;
    public static final int UNKNOWN_SYSTEM_ERROR     = 204144642;
    public static final int QUERY_DEV_ACCOUNT_INFO   = 204144643;
    public static final int QUERY_APP_INFO           = 204144644;
    public static final int OBTAIN_UPLOAD_AUTH_CODE  = 204144645;
    public static final int FAILED_TO_UPLOAD_PACKAGE = 204144646;
    public static final int ADD_APK_HAS_FAILED       = 204144662;
}
