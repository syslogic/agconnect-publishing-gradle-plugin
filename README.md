### Huawei AppGallery Connect Publishing Plugin

Legal Disclaimer: This product is **NOT** officially endorsed or certified by Huawei Technologies Co., Ltd.<br/>
The trademarks are being referenced for identification purposes only, in terms of a nominative fair use.

The official Huawei repositories can be found there: [@HMS-Core](https://github.com/orgs/HMS-Core/repositories).


### Installation

Currently the plugin can only be installed via `git clone`:

    git clone https://github.com/syslogic/agconnect-publishing-gradle-plugin.git ./buildSrc

### Configuration

````
plugins {
    id 'com.android.application'
    id 'com.huawei.agconnect'
    id 'io.syslogic.agconnect.publishing'
}
````

`PublicationExtension` can be configured with `apiConfigFile` and `verbose`:

````
agcPublishing {
    apiConfigFile = "${rootProject.getProjectDir().absolutePath}${File.separator}credentials${File.separator}agc-apiclient-000000000000000000-0000000000000000000.json"
    verbose = true
}
````

### Screenshots

![Gradle Plugin Tasks](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/screenshot_01.png)

### Status

[![Gradle CI](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml/badge.svg)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml)

[![Release](https://jitpack.io/v/syslogic/agconnect-publishing-gradle-plugin.svg)](https://jitpack.io/#io.syslogic/agconnect-publishing-gradle-plugin)

[![MIT License](https://img.shields.io/github/license/syslogic/agconnect-publishing-gradle-plugin)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/blob/master/LICENSE)
