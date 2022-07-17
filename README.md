### Huawei AppGallery Connect Publishing Plugin

![Social Media Preview](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/repository.png)

Legal Disclaimer: This product is **NOT** officially endorsed or certified by Huawei Technologies Co., Ltd.<br/>
The trademarks are being referenced for identification purposes only, in terms of a nominative fair use.

The official Huawei repositories can be found there: [@HMS-Core](https://github.com/orgs/HMS-Core/repositories).

 ---

### Development

The plugin source can be installed into any Gradle project via `git clone`:

    git clone https://github.com/syslogic/agconnect-publishing-gradle-plugin.git ./buildSrc

### Installation

Plugin `io.syslogic.agconnect.publishing` depends on `com.android.application` and `com.huawei.agconnect`.

<details>
<summary>Plugins API</summary>
<p>

````
/* Still required due to AGCP plugin. */
buildscript {
    dependencies {
        classpath "com.android.tools.build:gradle:7.2.1"
    }
}
plugins {
    id 'com.android.application' version "7.2.1" apply false
    id 'com.huawei.agconnect.agcp' version "1.7.0.300" apply false
    id 'io.syslogic.agconnect.publishing' version "7.2.1.3" apply false
}
````

</p>
</details>

<details>
<summary>Buildscript</summary>
<p>

````
buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url "https://developer.huawei.com/repo/" }
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:7.2.1"
        classpath "com.huawei.agconnect:agcp:1.7.0.300"
        classpath "io.syslogic.agconnect:publishing:7.2.1.3"
    }
}
````

</p>
</details>

Module `build.gradle`:
````
plugins {
    id 'com.android.application'
    id 'com.huawei.agconnect'
    id 'io.syslogic.agconnect.publishing'
}
````

### Configuration

`PublicationExtension` can be configured with `apiConfigFile`, `logHttp` and `verbose`.<br/>
The path to the API client credentials file is absolute, in order to permit external locations.

````
agcPublishing {
    apiConfigFile = "${rootProject.getProjectDir().absolutePath}${File.separator}credentials${File.separator}agc-apiclient-000000000000000000-0000000000000000000.json"
    logHttp = true
    verbose = true
}
````

### Screenshots

![Gradle Plugin Tasks](https://raw.githubusercontent.com/syslogic/agconnect-publishing-gradle-plugin/master/screenshots/screenshot_01.png)

### Status

[![Gradle CI](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml/badge.svg)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/actions/workflows/gradle.yml)

[![Release](https://jitpack.io/v/syslogic/agconnect-publishing-gradle-plugin.svg)](https://jitpack.io/#io.syslogic/agconnect-publishing-gradle-plugin)

[![MIT License](https://img.shields.io/github/license/syslogic/agconnect-publishing-gradle-plugin)](https://github.com/syslogic/agconnect-publishing-gradle-plugin/blob/master/LICENSE)
