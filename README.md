### Huawei AppGallery Connect Publishing Plugin

![Social Media Preview](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/repository.png)

Legal Disclaimer: This product is **NOT** officially endorsed or certified by Huawei Technologies Co., Ltd.<br/>
The trademarks are being referenced for identification purposes only, in terms of a nominative fair use.

The official Huawei repositories can be found there: [@HMS-Core](https://github.com/orgs/HMS-Core/repositories).

 ---
### Features

 - It builds and uploads signed Android packages to the AppGallery Connect Publishing API.

### Development

The plugin source can be installed into any Android Gradle project with `git clone`:

````bash
git clone https://github.com/syslogic/agconnect-publishing-gradle-plugin.git ./buildSrc
````

### Package Installation

Plugin `io.syslogic.agconnect.publishing` depends on `com.android.application` and `com.huawei.agconnect`.

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
        classpath 'com.android.tools.build:gradle:7.2.1'
        classpath 'com.huawei.agconnect:agcp:1.7.0.300'
        classpath 'io.syslogic.agconnect:publishing:7.2.1.5'
    }
}
````

Then they can be applied in the module's `build.gradle`:
````groovy
plugins {
    id 'com.android.application'
    id 'com.huawei.agconnect'
    id 'io.syslogic.agconnect.publishing'
}
````

### Configuration

`PublicationExtension` can be configured with the following properties:

 - `configFile`: The path to the API client credentials file is absolute.
 - `assetDirectory`: Permits overriding the default asset directory name (work in progress).
 - `verbose`: Verbose logging on/off
 - `logHttp`: HTTP logging on/off.

````groovy
agcPublishing {
    configFile = "${rootProject.getProjectDir().absolutePath}${File.separator}credentials${File.separator}agc-apiclient-000000000000000000-0000000000000000000.json"
    assetDirectory = 'play'
    logHttp = false
    verbose = true
}
````

These properties are all optional, while:

 - providing the config file at the default location: `credentials/agc-apiclient.json`.
 - being happy with the default local assets name `agconnect`.
 - not needing any debug console output, but only results.

### Plugin Tasks

![Gradle Plugin Tasks](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/screenshot_01.png)

### Example Log Output

The log output for task `:mobile:publishReleaseAab` explains what it does.

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

AAB someapp-release-1.0.0.aab had been uploaded.
13.1 MB in 14s equals a transfer-rate of 957.0 kB/s
````

### Support
- [Documentation](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-obtain_token-0000001158365043)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-developers)
- [Issue Tracker](https://github.com/syslogic/agconnect-publishing-gradle-plugin/issues)

### Status

[![Gradle CI](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml/badge.svg)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml)

[![Release](https://jitpack.io/v/syslogic/agconnect-publishing-gradle-plugin.svg)](https://jitpack.io/#io.syslogic/agconnect-publishing-gradle-plugin)

[![MIT License](https://img.shields.io/github/license/syslogic/agconnect-publishing-gradle-plugin)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/blob/master/LICENSE)
