
Pathsense Samples for Android
=============================

A collection of sample applications demonstrating how to use the Pathsense SDK. For more information, take a look at the [Javadocs](http://docs.pathsense.io/android/sdk/location/4.1.0.0/) or connect with us on our [website](https://pathsense.com/) or [developer portal](https://developer.pathsense.com/).

You can see the additional information for each sample in their respective README files.

  - [Activity API Demo](pathsense-activitydemo-app/README.md)
  - [GeoFence API Demo](pathsense-geofencedemo-app/README.md)
  - [In-Vehicle Location API Demo](pathsense-invehiclelocationdemo-app/README.md)

Setup for Pathsense Android SDK
===================================
1. Obtain a **Pathsense SDK Client ID** and **API Key** from [here](https://pathsense.com/). Click “GET STARTED” and enter your email address.

2. In **AndroidManifest.xml**, add the following elements as children of the &#060;application&#062; element, by inserting them just before the closing &#060;/application&#062; tag:

    ```xml
    <meta-data 
      android:name="com.pathsense.android.sdk.CLIENT_ID" 
      android:value="YOUR_PATHSENSE_SDK_CLIENT_ID" />
        
    <meta-data 
      android:name="com.pathsense.android.sdk.API_KEY" 
      android:value="YOUR_PATHSENSE_SDK_API_KEY" />
    ```

    * Substitute your CLIENT_ID key for YOUR_PATHSENSE_SDK_CLIENT_ID in the value attribute. This element sets the key com.pathsense.android.sdk.CLIENT_ID to the value of your Pathsense SDK Client ID.

    * Substitute your API_KEY key for YOUR_PATHSENSE_SDK_API_KEY in the value attribute. This element sets the key com.pathsense.android.sdk.API_KEY to the value of your Pathsense SDK API key.

3. Save AndroidManifest.xml.

4. Place **pathsense-android-sdk-location-bundle-release-4.1.0.0.aar** under **/libs**

5. In **build.gradle**, add the following:

    * to the **repositories** element:

    ```groovy
    repositories {
      flatDir {
        dirs 'libs'
      }
    }
    ```

    * to the **dependencies** element:

    ```groovy
    compile(name:'pathsense-android-sdk-location-bundle-release-4.1.0.0', ext:'aar')
    ```
    * for improved performance on Android Oreo and above add Google Play Services Location 15.0.1 or higher ***not required**
    ```groovy
    compile "com.google.android.gms:play-services-location:15.0.1"
    ```

6. Save build.gradle.

7. Re-build application.

How to customize foreground notification
===================================

The PathSense SDK runs as a foreground service and will post a foreground notification while in use.
<br />
By default, it will show the PathSense icon and read <i>"Pathsense is running"</i>. You can fully customize this notification by implementing <code>com.pathsense.android.sdk.location.PathsenseNotificationFactory</code> and providing your own foreground notification.
<br />
Setup includes the following:

1. Implement <code>com.pathsense.android.sdk.location.PathsenseNotificationFactory</code> and override the desired functionality. See [javadoc](http://docs.pathsense.io/android/sdk/location/4.1.0.0/com/pathsense/android/sdk/location/PathsenseNotificationFactory.html)

	<code>createForegroundNotification</code>: Returns the foreground notification used by PathsenseLocationProviderAPI.

	<code>createForegroundNotificationId</code>: Returns the ID used for foreground notification.

	<b>** Note: Returning the same notification and ID used by all other app foreground services allows the foreground notification to be shared and results in a single foreground notification. See [android javadoc](https://developer.android.com/reference/android/app/NotificationManager#notify(int,%20android.app.Notification))</b>

2. Add <b>pathsense.properties</b> under the <b>&lt;module-dir&gt;/src/main/assets</b> folder of your app. Add the <b>/assets</b> folder if not already there.

3. Set the following property <code>notification_factory_class</code> to the fully qualified class name.

	In <b>&lt;module-dir&gt;/src/main/assets/pathsense.properties</b>:
	```
	notification_factory_class=com.myapp.MyPathsenseNotificationFactory
	```
4. Update your proguard rules to prevent obfuscation of the notification factory class.

	In <b>&lt;module-dir&gt;/proguard-rules.pro</b>:
	```
	-keep class com.myapp.MyPathsenseNotificationFactory {
		*;
	}
	```