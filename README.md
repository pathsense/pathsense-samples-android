Pathsense Samples for Android
=============================

A collection of sample applications demonstrating how to use the Pathsense SDK. For more information, take a look at the [Javadocs](http://docs.pathsense.io/android/sdk/location/2.0.0.2/) or connect with us on our [website](https://pathsense.com/).

You can see the additional information for each sample in their respective README files.

  - [Activity API Demo](pathsense-activitydemo-app/README.md)
  - [GeoFence API Demo](pathsense-geofencedemo-app/README.md)

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

4. Place **pathsense-android-sdk-bundle-release-2.0.0.2.aar** under **/libs**

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
    compile(name:'pathsense-android-sdk-bundle-release-2.0.0.2', ext:'aar')
    ```

6. Save build.gradle.

7. Re-build application.
