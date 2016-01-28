#What is TedPicker?

TedPicker is image selector library for android.<br />
Do not waste your time for image select function. You can take a picture or select image from gallary.<br />

Also you can customize color, drawable, select count, etc for your application.


##Demo

[Watch video at youtube](https://youtu.be/fGnJ03h1cK0)

<br />
![Screenshot](https://github.com/ParkSangGwon/TedPicker/blob/master/Screenshot.png?raw=true)    
           

##Setup

#####Gradle
We will use cwac-camera for take a picture. And get library from  [jitpack.io](https://jitpack.io/)
```javascript


repositories {
    maven { 
url "https://repo.commonsware.com.s3.amazonaws.com" }

    maven {
        url "https://jitpack.io"
    }

}

dependencies {
     compile 'com.github.ParkSangGwon:tedpicker:v1.0.2'
}



```

#####Permission
Add permission for Camera, External Storage.

```javascript
<uses-feature android:name="android.hardware.camera" />
<uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

#####Activity
Declare Activity in your  `AndroidManifest.xml`


```javascript
        <activity android:name="com.gun0912.tedpicker.ImagePickerActivity"        />
```






##How to use

#####1. Start Activity
Add your request code for `startActivityForResult()` and start `ImagePickerActivity`

```javascript

    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private void getImages() {

        Intent intent  = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);

    }

```


#####2. Receive Activity
If you finish image select, you will recieve image path array (Uri type)
```javascript



    @Override
    protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

            if (requestCode == INTENT_REQUEST_GET_IMAGES && resuleCode == Activity.RESULT_OK ) {

                ArrayList<Uri>  image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                //do something
            }
    }


```





##Customize
You can change color, drawable, height ...<br />
Before call `startActivityForResult()`, set your  `Config` instance to `ImagePickerActivity`

#####Example
```javascript


        Config config = new Config();
        config.setCameraHeight(R.dimen.app_camera_height);
        config.setToolbarTitleRes(R.string.custom_title);
        config.setSelectionMin(2);
        config.setSelectionLimit(4);
        config.setSelectedBottomHeight(R.dimen.bottom_height);

        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);

   


```

#####Function

* `setCameraHeight(R.dimen.xxx) (default: 250dp)`

* `setSelectedBottomHeight(R.dimen.xxx) (default: 90dp)`

* `setSelectedBottomColor(R.color.xxx) (default: R.attr.colorAccent)`

* `setToolbarTitleRes(R.string.xxx) (default: Choice Image / 사진선택)`

* `setTabBackgroundColor(R.color.xxx) (default: #fff)`

* `setTabSelectionIndicatorColor(R.color.xxx) (default: R.attr.colorPrimary)`

* `setSelectionLimit(int)` 

* `setSelectionMin(int)`

* `setCameraBtnImage(R.drawable.xxx)`

* `setCameraBtnBackground(R.drawable.xxx)`

* `setSelectedCloseImage(R.drawable.xxx)`

* `setSavedDirectoryName(R.string.xxx) (default: Pictures)`



##Thanks 
* This project is based on [Poly-Picker](https://github.com/jaydeepw/poly-picker) library project 
* [Cwac-Camera](https://github.com/commonsguy/cwac-camera) - Taking Pictures. Made Sensible.
* [Glide](https://github.com/bumptech/glide) - An image loading and caching library 
* [Android Support Design](http://android-developers.blogspot.kr/2015/05/android-design-support-library.html) 


##License 
 ```code
Copyright 2010 Jason Johnston

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.```
