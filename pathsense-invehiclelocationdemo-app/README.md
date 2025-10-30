Setup for PathSense In-Vehicle Location Demo
===================================
1. Obtain a **Google Maps Android API Key** from [here](https://developers.google.com/maps/documentation/android-sdk/get-api-key).

2. Obtain a **PathSense SDK Client ID** and **API Key** by contacting [PathSense](https://pathsense.com/).
   > The legacy “GET STARTED” portal is no longer active. SDK credentials are issued privately.

3. In **AndroidManifest.xml**, add the following elements as children of the &#060;application&#062; element, by inserting them just before the closing &#060;/application&#062; tag:

    ```xml
    <meta-data 
      android:name="com.google.android.maps.v2.API_KEY" 
      android:value="YOUR_GOOGLE_MAPS_API_KEY" />
          
    <meta-data 
      android:name="com.pathsense.android.sdk.CLIENT_ID" 
      android:value="YOUR_PATHSENSE_SDK_CLIENT_ID" />

    <meta-data 
      android:name="com.pathsense.android.sdk.API_KEY" 
      android:value="YOUR_PATHSENSE_SDK_API_KEY" />
    ```

   * Substitute your API key for `YOUR_GOOGLE_MAPS_API_KEY` in the value attribute.  
     This sets `com.google.android.maps.v2.API_KEY` to your Google Maps Android API key.

   * Substitute your Client ID for `YOUR_PATHSENSE_SDK_CLIENT_ID`.  
     This sets `com.pathsense.android.sdk.CLIENT_ID` to your PathSense SDK Client ID.

   * Substitute your API Key for `YOUR_PATHSENSE_SDK_API_KEY`.  
     This sets `com.pathsense.android.sdk.API_KEY` to your PathSense SDK API Key.

4. Save **AndroidManifest.xml**.

5. Place **pathsense-android-5.0.0.0.aar** under **/libs**

6. In **build.gradle**, add the following:

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

   * (Optional) For improved performance on Android O and above, add Google Play Services Location — **not required**:
    ```groovy
    implementation "com.google.android.gms:play-services-location:21.3.0"
    ```

7. Save **build.gradle**.

8. Re-build the application.

Requesting In-Vehicle Location Updates
-------------
1. Create a [Broadcast Receiver](https://developer.android.com/reference/android/content/BroadcastReceiver) that will receive in-vehicle location updates.

   * For convenience, you can extend `com.pathsense.android.sdk.location.PathsenseInVehicleLocationUpdateReceiver`.

    ```java
    public class PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver extends BroadcastReceiver
    {
      @Override
      public void onReceive(Context context, Intent intent)
      {  
        PathsenseInVehicleLocation inVehicleLocation =
            PathsenseInVehicleLocation.fromIntent(intent);
        if (inVehicleLocation != null)
        {
          // handle location update
        }
      }
    }
    ```

2. In **AndroidManifest.xml**, add the following element as a child of the &#060;application&#062; element, by inserting it just before the closing &#060;/application&#062; tag:

    ```xml
    <receiver  
      android:name=".PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver" />
    ```

3. In **MapActivity** (or any other [Context](https://developer.android.com/reference/android/content/Context) object), instantiate the API:

    ```java
    PathsenseLocationProviderApi api = PathsenseLocationProviderApi.getInstance(context);
    ```

4. Request in-vehicle location updates by calling `requestInVehicleLocationUpdates` with the receiver created in step #1:

    ```java
    api.requestInVehicleLocationUpdates(PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver.class);
    ```

   * Until `removeInVehicleLocationUpdates()` is called, the receiver will continue to receive in-vehicle location updates.

---

**SDK Version:** 5.0.0.0  **Android Target:** 16 (API 36)  **Play Services:** 21.3.0 or newer  
© PathSense Inc. — Updated for SDK 5.0.0.0 / Android 16 (API 36)