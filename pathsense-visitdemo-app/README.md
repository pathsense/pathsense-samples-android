Setup for Pathsense Visit Demo
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

5. Place **pathsense-android-sdk-bundle-release-1.1.0.0.aar** under **/libs**

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
    compile(name:'pathsense-android-sdk-bundle-release-1.1.0.0', ext:'aar')
    ```

7. Save build.gradle.

8. Re-build application.

Start Monitoring for Visits
-------------
1. Create a [Broadcast Receiver](http://developer.android.com/reference/android/content/BroadcastReceiver.html) that will receive visit events (i.e. arrival, departure).

    * For convenience, you can extend [PathsenseVisitEventReceiver](http://docs.pathsense.io/android/sdk/location/1.1.0.0/com/pathsense/android/sdk/location/PathsenseVisitEventReceiver.html)

    ```java
    public class PathsenseVisitDemoVisitEventReceiver extends BroadcastReceiver
    {
	  @Override
	  public void onReceive(Context context, Intent intent)
	  {  
        PathsenseVisitEvent visitEvent = PathsenseVisitEvent.fromIntent(intent);
        if (visitEvent != null)
        {
          if (visitEvent.isArrival())
          {
            // arrival
            // do something
          }
          else if (visitEvent.isDeparture())
          {
            // departure
            // do something
          }
        }
      }
    }
    ```

2. In **AndroidManifest.xml**, add the following element as a child of the &#060;application&#062; element, by inserting it just before the closing &#060;/application&#062; tag:

    ```xml
    <receiver	
      android:name=".PathsenseVisitDemoVisitEventReceiver" />
    ```

3. In **MapActivity** (or any other [context](http://developer.android.com/reference/android/content/Context.html) object), instantiate the [PathsenseLocationProviderApi](http://docs.pathsense.io/android/sdk/location/1.1.0.0/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html):

    ```java
    PathsenseLocationProviderApi api = PathsenseLocationProviderApi.getInstance(context);
    ```

4. Start monitoring for visits by calling [requestVisits](http://docs.pathsense.io/android/sdk/location/1.1.0.0/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#requestVisits-java.lang.Class-) with the receiver created in step #1:

    ```java
    api.requestVisits(PathsenseVisitDemoVisitEventReceiver.class);
    ```

    * until [removeVisits](http://docs.pathsense.io/android/sdk/location/1.1.0.0/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#removeVisits--) is called, the receiver will be notified whenever a visit event (i.e. arrival, departure) occurs.