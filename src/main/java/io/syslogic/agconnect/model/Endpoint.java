package io.syslogic.agconnect.model;

/**
 * API Endpoints.
 *
 * @author Martin Zeitler
 */
public class Endpoint {
    public static String OAUTH2_TOKEN          = "https://connect-api.cloud.huawei.com/api/oauth2/v1/token";
    public static String PUBLISH_UPLOAD_URL    = "https://connect-api.cloud.huawei.com/api/publish/v2/upload-url";
    public static String PUBLISH_APP_ID_LIST   = "https://connect-api.cloud.huawei.com/api/publish/v2/appid-list";
    public static String PUBLISH_APP_FILE_INFO = "https://connect-api.cloud.huawei.com/api/publish/v2/app-file-info";
    public static String PUBLISH_APP_INFO      = "https://connect-api.cloud.huawei.com/api/publish/v2/app-info";
    public static String PUBLISH_APP_SUBMIT    = "https://connect-api.cloud.huawei.com/api/publish/v2/app-submit";

    public static String PUBLISH_ERROR_CODES   = "https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-publishingapi-errorcode-0000001163523297";
}
