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
    
          //this should come before jitpack else MotionTag library will require authentication before download
        maven { url "https://pkgs.dev.azure.com/motiontag/releases/_packaging/releases/maven/v1" }
        maven { url 'https://jitpack.io' }
    }
``` 
4. And finally add the SDK as an implementation dependency:

```gradle
    dependencies {
        implementation 'com.github.Changers.Android-SDK:sdk:1.16.2'
        
        // for testing on stage use this
        //implementation "com.github.Changers.Android-SDK:sdkstaging:1.16.2"
    }
``` 



## 2. Integrate the SDK

### 2.1. Initialization

You need to initialize the SDK before launching the Web App. We recommend doing this in your Application class.

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
          android:fullBackupContent="@xml/backup_rules"
          android:dataExtractionRules="@xml/data_extraction_rules"
          android:allowBackup="false"
```

Where the file contents are as follows:
     

2. Register WebActivity so it can be launched:
```
    <activity
        android:name="com.blacksquared.sdk.activity.WebActivity"
        android:screenOrientation="portrait"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">
        
        ///Add this part to handle deep link
         <intent-filter android:autoVerify="true">
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:host="api.klima-taler.com"
                    android:scheme="https" />

                <data
                    android:host="api.coin-stage.de"
                    android:scheme="https" />
            </intent-filter>
    </activity>
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

## 5. Get user UUID
To fetch user uuid, use this function call

``` 
     Changers.uuid
     
```
It will return the logged in user uuid or empty string if the uuid have not been created. Uuid will be created once the WebActivity have been launched at least once.

## 6. Log in returning user
To log in returning user, use this function call 

```
     Changers.logUserIn("*****")
 
```
Passing in your uuid, The function can throw an exception if there is an error. It should be called in a background thread since it makes a network call. It should be called before launching the WebActivity.


## 5. If anything fails, get in touch
You can use our [helpdesk](https://changers.zendesk.com/hc). We're friendly. ;)
