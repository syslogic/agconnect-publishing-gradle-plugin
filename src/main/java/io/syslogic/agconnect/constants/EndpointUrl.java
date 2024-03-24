package io.syslogic.agconnect.constants;

/**
 * AppGallery Connect API Endpoints
 *
 * @author Martin Zeitler
 */
public class EndpointUrl {

    /** Endpoint URL */
    public static final String OAUTH2_TOKEN            = "https://connect-api.cloud.huawei.com/api/oauth2/v1/token";
    /** Endpoint URL */
    public static final String PUBLISH_UPLOAD_URL      = "https://connect-api.cloud.huawei.com/api/publish/v2/upload-url";
    /** Endpoint URL */
    public static final String PUBLISH_APP_ID_LIST     = "https://connect-api.cloud.huawei.com/api/publish/v2/appid-list";
    /** Endpoint URL */
    public static final String PUBLISH_APP_FILE_INFO   = "https://connect-api.cloud.huawei.com/api/publish/v2/app-file-info";
    /** Endpoint URL */
    public static final String PUBLISH_APP_INFO        = "https://connect-api.cloud.huawei.com/api/publish/v2/app-info";
    /** Endpoint URL */
    public static final String PUBLISH_APP_LANG_INFO   = "https://connect-api.cloud.huawei.com/api/publish/v2/app-language-info";
    /** Endpoint URL */
    public static final String PUBLISH_APP_SUBMIT      = "https://connect-api.cloud.huawei.com/api/publish/v2/app-submit";
    /** Endpoint URL */
    public static final String PUBLISH_PHASED_STATUS   = "https://connect-api.cloud.huawei.com/api/publish/v2/phased-release/state";
    /** Endpoint URL */
    public static final String PUBLISH_PHASED_RELEASE  = "https://connect-api.cloud.huawei.com/api/publish/v2/phased-release";
    /** Endpoint URL */
    public static final String PUBLISH_ON_SHELF_TIME   = "https://connect-api.cloud.huawei.com/api/publish/v2/on-shelf-time";
    /** Endpoint URL */
    public static final String PUBLISH_COMPILE_STATUS  = "https://connect-api.cloud.huawei.com/api/publish/v2/package/compile/status";

    /** Link to AppGallery Connect Console */
    public static final String AG_CONNECT_PACKAGE_INFO = "https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/myApp/{appId}/v{packageId}";

    /** Link to AppGallery Connect Console */
    public static final String AG_CONNECT_CERTIFICATES = "https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/myApp/{appId}/9249519184596012000";

    /** Link to AppGallery Connect Console */
    public static final String AG_CONNECT_APP_INFO     = "https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/myApp/{appId}/97458334310914199";

    /** Link to AppGallery Connect Console */
    public static final String AG_CONNECT_INTEGRATION  = "https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/myProject/99536292102543196/97458334310914194?appId={appId}";

    /** Link to AppGallery Connect Console */
    public static final String AG_CONNECT_API_CLIENT   = "https://developer.huawei.com/consumer/en/service/josp/agc/index.html#/ups/9249519184595983326";
}
