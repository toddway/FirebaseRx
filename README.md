## Usage

Work with Query (or DatabaseReference)
```java
Query query = FirebaseDatabase.getInstance().getReference("whatever");

//observe all changes to a Firebase query
Observable<Pojo> o = FirebaseRx.observeValue(query, Pojo.class);

//observe single change to a Firebase query
Observable<Pojo> o = FirebaseRx.observeValue(query, Pojo.class).take(1);

//observe set value
Observable<Void> o = FirebaseRx.observeSetValue(query, new Pojo());

//observe all children of a Firebase query as a map
Observable<Map<String, Pojo>> o = FirebaseRx.observeChildMap(query, Pojo.class);

//observe children of a Firebase query that match a set of keys
List<String> keys = Arrays.asList("one", "two", "five");
Observable<Map<String, Pojo>> o = FirebaseRx.observeChildMap(query, Pojo.class, keys);
```

Work with Task results (e.g. authentication calls)
```java
FirebaseAuth auth = FirebaseAuth.getInstance();

//observe sign in anonymously
Observable<AuthResult> o = FirebaseRx.observeTask(auth.signInAnonymously());

//observe sign in with credential
Observable<AuthResult> o = FirebaseRx.observeTask(auth.signInWithCredential(credential));
```

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

Add dependency to your Android module build.gradle:

```groovy
dependencies {
    compile ('com.github.toddway.FirebaseRx:android:X.X.X') {
        exclude group: 'com.google.firebase'
    }
}
```

or add dependency to your Java server module build.gradle:

```groovy
dependencies {
    compile ('com.github.toddway.FirebaseRx:server:X.X.X') {
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