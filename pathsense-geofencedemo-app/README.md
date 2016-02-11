Setup for Pathsense Geofence Demo
===================================
1. Obtain a **Google Maps Android API Key** from [here](https://developers.google.com/maps/documentation/android/signup).

2. Obtain a **Pathsense SDK Client ID** and **API Key** from [here](https://pathsense.com/). Click “GET STARTED” and enter your email address.

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
    
    * Substitute your API_KEY key for YOUR_GOOGLE_MAPS_API_KEY in the value attribute. This element sets the key com.google.android.maps.v2.API_KEY to the value of your Google Maps Android API key.

    * Substitute your CLIENT_ID key for YOUR_PATHSENSE_SDK_CLIENT_ID in the value attribute. This element sets the key com.pathsense.android.sdk.CLIENT_ID to the value of your Pathsense SDK Client ID.

    * Substitute your API_KEY key for YOUR_PATHSENSE_SDK_API_KEY in the value attribute. This element sets the key com.pathsense.android.sdk.API_KEY to the value of your Pathsense SDK API key.

4. Save AndroidManifest.xml.

5. Place **pathsense-android-sdk-bundle-release-1.2.0.0.aar** under **/libs**

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
    compile(name:'pathsense-android-sdk-bundle-release-1.2.0.0', ext:'aar')
    ```

7. Save build.gradle.

8. Re-build application.

Adding a Geofence
-------------
1. Create a [Broadcast Receiver](http://developer.android.com/reference/android/content/BroadcastReceiver.html) that will receive geofence events (i.e. ingress, egress).

    * For convenience, you can extend [PathsenseGeofenceEventReceiver](http://docs.pathsense.io/android/sdk/location/1.2.0.0/com/pathsense/android/sdk/location/PathsenseGeofenceEventReceiver.html)

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

3. In **MapActivity** (or any other [context](http://developer.android.com/reference/android/content/Context.html) object), instantiate the [PathsenseLocationProviderApi](http://docs.pathsense.io/android/sdk/location/1.2.0.0/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html):

    ```java
    PathsenseLocationProviderApi api = PathsenseLocationProviderApi.getInstance(context);
    ```

4. Add a geofence to be monitored by calling [addGeofence](http://docs.pathsense.io/android/sdk/location/1.2.0.0/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#addGeofence-java.lang.String-double-double-int-java.lang.Class-) with an ID, latitude, longitude, radius, and the receiver created in step #1:

    ```java
    api.addGeofence("MYGEOFENCE", location.getLatitude(), location.getLongitude(), 100, PathsenseGeofenceDemoGeofenceEventReceiver.class);
    ```

    * until [removeGeofence](http://docs.pathsense.io/android/sdk/location/1.2.0.0/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#removeGeofence-java.lang.String-) is called, the receiver will be notified whenever a geofence event (i.e. ingress, egress) occurs.
