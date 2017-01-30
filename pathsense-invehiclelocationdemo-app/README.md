Setup for Pathsense In-Vehicle Location Demo
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

5. Place **pathsense-android-sdk-bundle-release-2.3.0.18.aar** under **/libs**

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
    compile(name:'pathsense-android-sdk-bundle-release-2.3.0.18', ext:'aar')
    ```

7. Save build.gradle.

8. Re-build application.

Requesting In-Vehicle Location Updates
-------------
1. Create a [Broadcast Receiver](http://developer.android.com/reference/android/content/BroadcastReceiver.html) that will receive in-vehicle location updates.

    * For convenience, you can extend [PathsenseInVehicleLocationUpdateReceiver](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseInVehicleLocationUpdateReceiver.html)

    ```java
    public class PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver extends BroadcastReceiver
    {
      @Override
      public void onReceive(Context context, Intent intent)
      {  
        PathsenseInVehicleLocation inVehicleLocation = PathsenseInVehicleLocation.fromIntent(intent);
        if (inVehicleLocation != null)
        {
          // do something
        }
      }
    }
    ```

2. In **AndroidManifest.xml**, add the following element as a child of the &#060;application&#062; element, by inserting it just before the closing &#060;/application&#062; tag:

    ```xml
    <receiver  
      android:name=".PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver" />
    ```

3. In **MapActivity** (or any other [context](http://developer.android.com/reference/android/content/Context.html) object), instantiate the [PathsenseLocationProviderApi](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html):

    ```java
    PathsenseLocationProviderApi api = PathsenseLocationProviderApi.getInstance(context);
    ```

4. Request in-vehicle location updates by calling [requestInVehicleLocationUpdates](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#requestInVehicleLocationUpdates-java.lang.Class-) with the receiver created in step #1:

    ```java
    api.requestInVehicleLocationUpdates(PathsenseInVehicleLocationDemoInVehicleLocationUpdateReceiver.class);
    ```

    * until [removeInVehicleLocationUpdates](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#removeInVehicleLocationUpdates--) is called, the receiver will be sent in-vehicle location updates.
