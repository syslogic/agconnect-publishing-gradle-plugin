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

`PublicationExtension.class` can be configured:

````
agcPublishing {
    apiConfigFile.set("${rootProject.getProjectDir().absolutePath}${File.separator}credentials${File.separator}agc-apiclient-1234567890-1234567890.json")
    verbose.set(true)
}

````

### Screenshots

![Gradle Plugin Tasks](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/screenshot_01.png)

[![MIT License](https://img.shields.io/github/license/syslogic/agconnect-publishing-gradle-plugin)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/blob/master/LICENSE)

[![Gradle CI](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml/badge.svg)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml)

[![](https://jitci.com/gh/syslogic/agconnect-publishing-gradle-plugin/svg)](https://jitci.com/gh/syslogic/agconnect-publishing-gradle-plugin) [![Release](https://jitpack.io/v/syslogic/agconnect-publishing-gradle-plugin.svg)](https://jitpack.io/#io.syslogic/agconnect-publishing-gradle-plugin)
