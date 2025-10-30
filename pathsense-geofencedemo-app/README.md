Setup for PathSense Geofence Demo
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

   * Substitute your API_KEY key for `YOUR_GOOGLE_MAPS_API_KEY` in the value attribute. This element sets the key `com.google.android.maps.v2.API_KEY` to the value of your Google Maps Android API key.

   * Substitute your CLIENT_ID key for `YOUR_PATHSENSE_SDK_CLIENT_ID` in the value attribute. This element sets the key `com.pathsense.android.sdk.CLIENT_ID` to the value of your PathSense SDK Client ID.

   * Substitute your API_KEY key for `YOUR_PATHSENSE_SDK_API_KEY` in the value attribute. This element sets the key `com.pathsense.android.sdk.API_KEY` to the value of your PathSense SDK API key.

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

8. Re-build application.

Adding a Geofence
-------------
1. Create a [Broadcast Receiver](https://developer.android.com/reference/android/content/BroadcastReceiver) that will receive geofence events (i.e. ingress, egress).

   * For convenience, you can extend `com.pathsense.android.sdk.location.PathsenseGeofenceEventReceiver`.

    ```java
    public class PathsenseGeofenceDemoGeofenceEventReceiver extends BroadcastReceiver
    {
      @Override
      public void onReceive(Context context, Intent intent)
      {  
        PathsenseGeofenceEvent geofenceEvent = PathsenseGeofenceEvent.fromIntent(intent);
        if (geofenceEvent != null)
        {
          if (geofenceEvent.isIngress())
          {
            // ingress
            // do something
          }
          else if (geofenceEvent.isEgress())
          {
            // egress
            // do something
          }
        }
      }
    }
    ```

2. In **AndroidManifest.xml**, add the following element as a child of the &#060;application&#062; element, by inserting it just before the closing &#060;/application&#062; tag:

    ```xml
    <receiver  
      android:name=".PathsenseGeofenceDemoGeofenceEventReceiver" />
    ```

3. In **MapActivity** (or any other [context](https://developer.android.com/reference/android/content/Context) object), instantiate the API:

    ```java
    PathsenseLocationProviderApi api = PathsenseLocationProviderApi.getInstance(context);
    ```

4. Add a geofence to be monitored by calling `addGeofence` with an ID, latitude, longitude, radius, and the receiver created in step #1:

    ```java
    api.addGeofence("MYGEOFENCE", location.getLatitude(), location.getLongitude(), 100, PathsenseGeofenceDemoGeofenceEventReceiver.class);
    ```

   * Until `removeGeofence("MYGEOFENCE")` is called, the receiver will be notified whenever a geofence event (i.e. ingress, egress) occurs.