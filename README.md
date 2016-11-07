Pathsense Samples for Android
=============================

A collection of sample applications demonstrating how to use the Pathsense SDK. For more information, take a look at the [Javadocs](http://docs.pathsense.io/android/sdk/location/2.3.0.0/) or connect with us on our [website](https://pathsense.com/) or [developer portal](https://developer.pathsense.com/).

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

4. Add new module **pathsense-android-sdk**

    * create folder **pathsense-android-sdk** to root
    
    * add ```':pathsense-android-sdk'``` module to **settings.gradle**


5. Place **pathsense-android-sdk-bundle-release-2.3.0.0.aar** in module **pathsense-android-sdk**

6. In module **pathsense-android-sdk** create file **build.gradle**
    ```groovy
    configurations.maybeCreate("default")
    artifacts.add("default", file('pathsense-android-sdk-bundle-release-2.3.0.0.aar'))
    ```


7. Add dependency to your module

    * to the **dependencies** element:

    ```groovy
    compile project(':pathsense-android-sdk')
    ```

8. Re-build application.
