### Huawei AppGallery Connect Publishing Plugin

![Social Media Preview](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/repository.png)

Legal Disclaimer: This product is **NOT** officially endorsed or certified by Huawei Technologies Co., Ltd.<br/>
The trademarks are being referenced for identification purposes only, in terms of a nominative fair use.

The official Huawei repositories can be found there: [@HMS-Core](https://github.com/orgs/HMS-Core/repositories).

 ---

### Development

The plugin source can be installed into any Gradle project via `git clone`:

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
        classpath 'io.syslogic.agconnect:publishing:7.2.1.3'
    }
}
````

Then they can be applid in the module's `build.gradle`:
````groovy
plugins {
    id 'com.android.application'
    id 'com.huawei.agconnect'
    id 'io.syslogic.agconnect.publishing'
}
````

### Configuration

`PublicationExtension` can be configured with `apiConfigFile`, `logHttp` and `verbose`.<br/>
The path to the API client credentials file is absolute, in order to permit external locations.

````groovy
agcPublishing {
    apiConfigFile = "${rootProject.getProjectDir().absolutePath}${File.separator}credentials${File.separator}agc-apiclient-000000000000000000-0000000000000000000.json"
    logHttp = false
    verbose = true
}
````

### Screenshots

![Gradle Plugin Tasks](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/screenshot_01.png)

### Support
- [Documentation](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-References/agcapi-obtain_token-0000001158365043)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-developers)
- [Issue Tracker](https://github.com/syslogic/agconnect-publishing-gradle-plugin/issues)

### Status

[![Gradle CI](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml/badge.svg)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml)

[![Release](https://jitpack.io/v/syslogic/agconnect-publishing-gradle-plugin.svg)](https://jitpack.io/#io.syslogic/agconnect-publishing-gradle-plugin)

[![MIT License](https://img.shields.io/github/license/syslogic/agconnect-publishing-gradle-plugin)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/blob/master/LICENSE)
