


## Install
Add jitpack to your root build.gradle:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
[![](https://jitpack.io/v/toddway/FirebaseRx.svg)](https://jitpack.io/#toddway/FirebaseRx)

Add dependency to your module build.gradle:

```groovy
dependencies {
    compile ('com.github.toddway.FirebaseRx:firebaserx:X.X.X') {
        exclude group: 'com.google.firebase'
    }
}
```


License
-------

    Copyright 2017-Present Todd Way

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.