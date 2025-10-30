Setup for PathSense Activity Demo
===================================
1. Obtain a **PathSense SDK Client ID** and **API Key** by contacting [PathSense](https://pathsense.com/).
   > The legacy “GET STARTED” portal is no longer active. SDK credentials are issued privately.

2. In **AndroidManifest.xml**, add the following elements as children of the `<application>` element, by inserting them just before the closing `</application>` tag:

    ```xml
    <meta-data
      android:name="com.pathsense.android.sdk.CLIENT_ID"
      android:value="YOUR_PATHSENSE_SDK_CLIENT_ID" />

    <meta-data
      android:name="com.pathsense.android.sdk.API_KEY"
      android:value="YOUR_PATHSENSE_SDK_API_KEY" />
    ```

   * Substitute your CLIENT_ID key for `YOUR_PATHSENSE_SDK_CLIENT_ID` in the value attribute.  
     This element sets the key `com.pathsense.android.sdk.CLIENT_ID` to the value of your PathSense SDK Client ID.

   * Substitute your API_KEY key for `YOUR_PATHSENSE_SDK_API_KEY` in the value attribute.  
     This element sets the key `com.pathsense.android.sdk.API_KEY` to the value of your PathSense SDK API key.

3. Save **AndroidManifest.xml**.

4. Place **pathsense-android-5.0.0.0.aar** under **/libs**

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
    implementation(name:'pathsense-android-5.0.0.0', ext:'aar')
    ```

   * (Optional) For improved performance on Android O and above, add the latest Google Play Services Location dependency — **not required**:

    ```groovy
    implementation "com.google.android.gms:play-services-location:21.3.0"
    ```

6. Save **build.gradle**.

7. Re-build the application.

Requesting Activity Updates
-------------
1. Create a [Broadcast Receiver](https://developer.android.com/reference/android/content/BroadcastReceiver) that will receive activity updates.

   * For convenience, you can extend `com.pathsense.android.sdk.location.PathsenseActivityRecognitionReceiver`.

    ```java
    public class PathsenseActivityUpdateBroadcastReceiver extends BroadcastReceiver
    {
      @Override
      public void onReceive(Context context, Intent intent)
      {
        PathsenseDetectedActivities detectedActivities =
            PathsenseDetectedActivities.fromIntent(intent);
        if (detectedActivities != null)
        {
          // handle detected activities
        }
      }
    }
    ```

2. In **AndroidManifest.xml**, add the following element as a child of the `<application>` element, by inserting it just before the closing `</application>` tag:

    ```xml
    <receiver
      android:name=".PathsenseActivityUpdateBroadcastReceiver" />
    ```

3. In **MainActivity** (or any other [Context](https://developer.android.com/reference/android/content/Context) object), instantiate the API:

    ```java
    PathsenseLocationProviderApi api = PathsenseLocationProviderApi.getInstance(context);
    ```

4. Request activity updates by calling `requestActivityUpdates` with the receiver created in step #1:

    ```java
    api.requestActivityUpdates(PathsenseActivityUpdateBroadcastReceiver.class);
    ```

   * Until `removeActivityUpdates()` is called, the receiver will continue to receive activity update callbacks.

---

**SDK Version:** 5.0.0.0  **Android Target:** 16 (API 36)  **Play Services:** 21.3.0 or newer  
© PathSense Inc. — Updated for SDK 5.0.0.0 / Android 16 (API 36)