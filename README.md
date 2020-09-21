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

3. [Get a token from GitHub](https://docs.github.com/pt/github/authenticating-to-github/creating-a-personal-access-token) so you can access GitHub packages. This is required even though our sdk is published in a non-private [repository](github.com/Changers/Sample-Android-SDK/). Then add our maven repository at module level:

```gradle
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/Changers/Sample-Android-SDK")
            credentials {
                username = "$System.env.GITHUB_USERNAME"
                password = "$System.env.GITHUB_TOKEN"
            }
    
            authentication {
                basic(BasicAuthentication)
            }
        }
    }
``` 
4. And finally add the SDK as an implementation dependency:

```gradle
    dependencies {
        implementation 'com.blacksquared:sdk:1.0.0'
    }
``` 



## 2. Initialize the SDK

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
