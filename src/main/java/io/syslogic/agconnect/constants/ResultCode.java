package io.syslogic.agconnect.constants;

/**
 * API Result Codes
 *
 * https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-publishingapi-errorcode-0000001163523297
 * @author Martin Zeitler
 */
public class ResultCode {
    public static int SUCCESS                  = 0;
    public static int INVALID_INPUT_PARAMETER  = 204144641;
    public static int UNKNOWN_SYSTEM_ERROR     = 204144642;
    public static int QUERY_DEV_ACCOUNT_INFO   = 204144643;
    public static int QUERY_APP_INFO           = 204144644;
    public static int OBTAIN_UPLOAD_AUTH_CODE  = 204144645;
    public static int FAILED_TO_UPLOAD_PACKAGE = 204144646;
    public static int ADD_APK_HAS_FAILED       = 204144662;
}