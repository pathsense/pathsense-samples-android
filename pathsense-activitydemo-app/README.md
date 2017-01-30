Setup for Pathsense Activity Demo
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

4. Place **pathsense-android-sdk-bundle-release-2.3.0.18.aar** under **/libs**

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
    compile(name:'pathsense-android-sdk-bundle-release-2.3.0.18', ext:'aar')
    ```

6. Save build.gradle.

7. Re-build application.

Requesting Activity Updates
-------------
1. Create a [Broadcast Receiver](http://developer.android.com/reference/android/content/BroadcastReceiver.html) that will receive activity updates.

    * For convenience, you can extend [PathsenseActivityRecognitionReceiver](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseActivityRecognitionReceiver.html)

    ```java
    public class PathsenseActivityUpdateBroadcastReceiver extends BroadcastReceiver
    {
      @Override
      public void onReceive(Context context, Intent intent)
      {
        PathsenseDetectedActivities detectedActivities = PathsenseDetectedActivities.fromIntent(intent);
        if (detectedActivities != null)
        {
          // do something
        }
      }
    }
    ```

2. In **AndroidManifest.xml**, add the following element as a child of the &#060;application&#062; element, by inserting it just before the closing &#060;/application&#062; tag:

    ```xml
    <receiver
      android:name=".PathsenseActivityUpdateBroadcastReceiver" />
    ```

3. In **MainActivity** (or any other [context](http://developer.android.com/reference/android/content/Context.html) object), instantiate the [PathsenseLocationProviderApi](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html):

    ```java
    PathsenseLocationProviderApi api = PathsenseLocationProviderApi.getInstance(context);
    ```

4. Request activity updates by calling [requestActivityUpdates](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#requestActivityUpdates-java.lang.Class-) with the receiver created in step #1:

    ```java
    api.requestActivityUpdates(PathsenseActivityUpdateBroadcastReceiver.class);
    ```

    * until [removeActivityUpdates](http://docs.pathsense.io/android/sdk/location/2.3.0.18/com/pathsense/android/sdk/location/PathsenseLocationProviderApi.html#removeActivityUpdates--) is called, the receiver will be notified whenever an activity update occurs.
