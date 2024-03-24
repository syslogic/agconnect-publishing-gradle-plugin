### Huawei AppGallery Connect Publishing Gradle Plugin

![Social Media Preview](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/repository.png)

Legal Disclaimer: This product is **NOT** officially endorsed or certified by Huawei Technologies Co., Ltd.<br/>
The trademarks are being referenced for identification purposes only, in terms of a nominative fair use.

The official Huawei repositories can be found there: [@HMS-Core](https://github.com/orgs/HMS-Core/repositories).

 ---
### Features

 - It automates the Huawei AppGallery Connect Publishing API.
 - It can upload and release Android APK/ABB packages.
 
### Development

The plugin source code can be swiftly installed into any Android Gradle project with `git clone`:

````bash
git clone https://github.com/syslogic/agconnect-publishing-gradle-plugin.git ./buildSrc
````

### Package Installation

Plugin `io.syslogic.agconnect.publishing` depends on `com.android.application`.

These plugins can be set up in the `buildscript` block of the root project's `build.gradle`:
````groovy
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://developer.huawei.com/repo/' }
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.3.1'
        classpath 'com.huawei.agconnect:agcp:1.9.1.303'
        classpath 'io.syslogic:agconnect-publishing-gradle-plugin:1.3.3'
    }
}
````

Or in the version-catalog `gradle/libs.versions.toml`:
````toml
[versions]
agconnect_publishing_plugin = '1.3.3'

[plugins]
agconnect_publishing = { id = "io.syslogic.agconnect.publishing", version.ref = "agconnect_publishing_plugin" }
````

Then they can be applied in the module's `build.gradle`:
````groovy
plugins {
    id 'com.android.application'
    id 'com.huawei.agconnect'
    // id 'io.syslogic.agconnect.publishing'
    alias(libs.plugins.agconnect.publishing)
}
````

### Configuration

`PublicationExtension` can be configured with the following properties:

 - `configFile`: The path to the API client credentials file is absolute.
 - `releaseType`: Release Type, 1=network (default), 3=phased.
 - `verbose`: Verbose logging, on/off.
 - `logHttp`: HTTP logging, on/off.

````groovy
/** Huawei AppGallery Connect: agc-apiclient.json */
def json_agc = "distribution${File.separator}agconnect_apiclient.json"
if (rootProject.file(json_agc).exists()) {
    agcPublishing {
        configFile = rootProject.file(json_agc).absolutePath
        releaseType = 1
        verbose = false
        logHttp = true
    }
}
````

These properties are all optional, while:

 - providing the config file at the default location: `distribution/agconnect_apiclient.json`.

### Plugin Tasks

![Gradle Plugin Tasks](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/screenshot_01.png)

### Example Log Output

The log output for task `:mobile:publishReleaseAab` explains what it does.<br/>
To be precise, it only uploads APK/ABB packages, but does not release them.

````
> Task :mobile:bundleRelease
> Task :mobile:publishReleaseAab
> POST /api/oauth2/v1/token HTTP/1.1
> HTTP/1.1 200 OK
> GET /api/publish/v2/upload-url?appId=000000000&releaseType=1&suffix=aab HTTP/1.1
> HTTP/1.1 200 OK
> POST /FileServer/uploadFile HTTP/1.1
> HTTP/1.1 200 OK
> PUT /api/publish/v2/app-file-info?appId=000000000&releaseType=1 HTTP/1.1
> HTTP/1.1 200 OK
> GET /api/publish/v2/package/compile/status?appId=000000000&pkgIds=00000000000000000 HTTP/1.1
> HTTP/1.1 200 OK

AAB file someapp_1.0.0-huawei-release.aab has been uploaded.
13.1 MB in 14s equals a transfer-rate of 957.0 kB/s.
````

Note: After this, some app-information still needs to be manually completed.

The log output for task `:mobile:submitReleaseAab` explains what it does.<br/>
To be precise, it submits an <u>already uploaded</u> APK/ABB package for review.

````
> Task :mobile:submitReleaseAab
> POST /api/oauth2/v1/token HTTP/1.1
> HTTP/1.1 200 OK
> POST /api/publish/v2/app-submit?appId=000000000&releaseType=1 HTTP/1.1
> HTTP/1.1 200 OK
Submitted for release: org.acme.someapp (000000000).
````

### Support
- [Documentation](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-obtain_token-0000001158365043)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-developers)
- [Issue Tracker](https://github.com/syslogic/agconnect-publishing-gradle-plugin/issues)

### Status

[![Gradle CI](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml/badge.svg)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml)

[![Release](https://jitpack.io/v/syslogic/agconnect-publishing-gradle-plugin.svg)](https://jitpack.io/#io.syslogic/agconnect-publishing-gradle-plugin)

[![MIT License](https://img.shields.io/github/license/syslogic/agconnect-publishing-gradle-plugin)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/blob/master/LICENSE)<!-- @IGNORE PREVIOUS: link -->
