package io.syslogic.agconnect.constants;

/**
 * AppGallery Connect API Endpoint URLs
 *
 * @author Martin Zeitler
 */
public class EndpointUrl {

    /** API Endpoint URLs */
    public static String OAUTH2_TOKEN            = "https://connect-api.cloud.huawei.com/api/oauth2/v1/token";
    public static String PUBLISH_UPLOAD_URL      = "https://connect-api.cloud.huawei.com/api/publish/v2/upload-url";
    public static String PUBLISH_APP_ID_LIST     = "https://connect-api.cloud.huawei.com/api/publish/v2/appid-list";
    public static String PUBLISH_APP_FILE_INFO   = "https://connect-api.cloud.huawei.com/api/publish/v2/app-file-info";
    public static String PUBLISH_APP_INFO        = "https://connect-api.cloud.huawei.com/api/publish/v2/app-info";
    public static String PUBLISH_APP_LANG_INFO   = "https://connect-api.cloud.huawei.com/api/publish/v2/app-language-info";
    public static String PUBLISH_APP_SUBMIT      = "https://connect-api.cloud.huawei.com/api/publish/v2/app-submit";
    public static String PUBLISH_PHASED_STATUS   = "https://connect-api.cloud.huawei.com/api/publish/v2/phased-release/state";
    public static String PUBLISH_PHASED_RELEASE  = "https://connect-api.cloud.huawei.com/api/publish/v2/phased-release";
    public static String PUBLISH_ON_SHELF_TIME   = "https://connect-api.cloud.huawei.com/api/publish/v2/on-shelf-time";
    public static String PUBLISH_COMPILE_STATUS  = "https://connect-api.cloud.huawei.com/api/publish/v2/package/compile/status";

    /** Console Pages */
    public static String AG_CONNECT_CERTIFICATES = "https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/myApp/{appId}/9249519184596012000";
    public static String AG_CONNECT_API_CLIENT   = "https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/ups/9249519184595983326";
}
