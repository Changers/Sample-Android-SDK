# Changers Android SDK

Be aware that the Changer Android SDK requires both [Background access to device Location](https://developer.android.com/about/versions/10/privacy/changes#app-access-device-location) and [Physical Activity Recognition](https://developer.android.com/about/versions/10/privacy/changes#physical-activity-recognition) permissions to run.

To get started, you need to register your application by contacting [Changers](https://changers.com). This process is done manually after a request. You're going to receive from us:

- An application identifier;
- A client id;
- A client secret.


# Integration - Step by step guide

This guide includes Kotlin code examples only. You can see the complete Kotlin code in our [GitHub repo](https://github.com/Changers/Sample-Android-SDK/tree/master/). 

## 1. Configure your build.gradle

No change at root build.gradle is required. Everything listed in this session can be done at module level.

     
1. Java 8 support is required, so you must have set:

```gradle
    android {
        compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
        }
        //...
    }
``` 
2. Setup your app name and credentials as environment variables, and then add them as build config fields:


```gradle
    android {
      //...
      defaultConfig {
        buildConfigField "String", "APP_NAME", "\"$SDK_APP_NAME\""
        buildConfigField "String", "CLIENT_ID", "\"$SDK_CLIENT_ID\""
        buildConfigField "String", "CLIENT_SECRET", "\"$SDK_CLIENT_SECRET\""
      }
    }
``` 

3. Add the following maven repository at module level:

```gradle
    repositories {
        maven { url 'https://jitpack.io' }
        maven { url "https://pkgs.dev.azure.com/motiontag/releases/_packaging/releases/maven/v1" }
        maven { url = "https://plugins.gradle.org/m2/" }
    }
``` 
4. And finally add the SDK as an implementation dependency:

```gradle
    dependencies {
        implementation 'com.github.Changers.Android-SDK:sdk:1.13.4'
    }
``` 



## 2. Integrate the SDK

### 2.1. Initialization

You need to initialize the SDK before launching the Web App. We recommend doing this at your Application.

```kotlin
    class App : android.app.Application() {
        override fun onCreate() {
            super.onCreate()
    
            initializeSdk()
        }
    
        private fun initializeSdk() {
            Changers.with(this,
                    Changers.Settings(
                            notification = createNotification(),
                            appName = BuildConfig.APP_NAME,
                            displayName = getString(R.string.app_name),
                            clientId = BuildConfig.CLIENT_ID,
                            clientSecret = BuildConfig.CLIENT_SECRET,
                            version = BuildConfig.VERSION_NAME
                    )
            )
        }
    }
``` 

You must create a notification so our foreground service can keep listening for location updates and tracking works properly.

### 2.2. Authenticate the client

Your client needs to be registered with [Changers](changers.com) to be able to make requests to our API. Once you got your client id and client secret, execute once:
```kotlin
   val clientToken = Changers.authenticateClient(
       BuildConfig.CLIENT_ID, 
       BuildConfig.CLIENT_SECRET
   )
```

*Important: This method does blocking network communication, and should be called from a background thread.*

This method returns a `ClientToken` object that contains client authentication information. You don't need to authenticate the client again until the `ClientToken` has expired. You need to store the `timestamp` under `ClientToken.expiresIn` and authenticate again once the token has expired, or else a `401 Unauthorized` error response is going to be encapsulated in an  `IOException` thrown by the HTTP library.

You can clear all state by executing `Changers.clear()`. 


### 2.3. Authenticate the user

Once the client is authenticated, the user also needs to be authenticated. For that, you can create a new user by executing:
```kotlin
   val userToken = Changers.signup()
```

The method accepts no parameter and returns a `UserToken` object. This should be done only once, but you need to store the  `UserToken.uuid` value in order to be able to log user in, for example, after a reinstall. You can also log the user in by providing the `UserToken.uuid` to the method:
```kotlin
    Changers.logUserIn(uuid: String)
```

In either the case of an existing or returning user, user authentication only needs to be done once per install. Whether you are signing up or logging in, the client must be authenticated first. You can check the clinet authentication status using the following flag:
```kotlin
    Changers.isClientAuthenticated
```
You can also check the state of the user authentication by using the following flag:
```kotlin
    Changers.isUserAuthenticated
```
You can clear all state by executing `Changers.clear()`. In that case, you need to authenticate the client again.


## 3. Changes in Manifest:

1. Make sure you've setup your App class
```
    <application android:name=".App"
```

2. Register WebActivity so it can be launched:
```
    <activity
        android:name="com.blacksquared.sdk.activity.WebActivity"
        android:screenOrientation="portrait"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar" />
``` 

3. Create a file provider so the Web Application can persist data in your internal memory. Under `<application>`, add:
```
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.blacksquared.sampleandroidsdk.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/changers_file_paths" />
        </provider>
``` 


## 4. Launch the Web Application:

Under the desired interaction (in our example, it's the click of a button), launch the Web Application:
```Kotlin
    startActivity(WebActivity.newIntent(this))
```


## 5. If anything fails, get in touch
You can use our [helpdesk](https://changers.zendesk.com/hc). We're friendly. ;)
