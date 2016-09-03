== HOW TO COMPILE GASTONA FOR ANDROID (building gastona.apk)

updated 2016.09.03

   The relevant information to compile gastona for android
   at least as java application is (choose the path separator you wish)
   
   manifest:
      ..\..\android\AndroidManifest.xml
      ../../android/AndroidManifest.xml

   main class:
      ..\..\android\src\org\gastona\gastonaMainActor.java
      ../../android/src/org/gastona/gastonaMainActor.java
   
   source path (order is important!):
      ..\..\android\src;..\..\base\src
      ../../android/src;../../base/src

   resources:
      ..\..\android\res
      ../../android/res

   resources:
      ..\..\META-GASTONA\js  mapped to META-GASTONA\js
      ..\..\android\assets   mapped to .

      ../../META-GASTONA/js  mapped to META-GASTONA/js
      ../../android/assets   mapped to .

   With this information and using some Android IDE or tools like Ant
   you should be able to build a basic gastona.apk.
   I can not guide you here since I do not use neither IDE or Ant to do it.
   
=== THE WAY I BUILD GASTONA.APK   
   
   Time ago, when I started compiling gastona for Android, I did manage to write
   a gastona script for the apk generation and I am using it since then. 
   This procedure is quite tricky, I have to do updates of the sdk with care
   since some tools that I need change its path or are simple removed!
   
   here a list of all commands used
   
      ...sdk\tools\android create project ...
      javac ...
      dx --dex ...
      aapt p -f -M ...
      call apkbuilder ...
      jarsigner
      zipalign
      adb install/uninstall

   If you want to try this way or just want to look at this build process all can be found
   in the scripts (to run in Windows! for linux adaptions are needed)
   
         BUILD/android-apk/APK_GENERATOR_v10.gast   
         BUILD/android-apk/GASTONA_ANDROID_CONFIG.gasti
         
   if you try to run APK_GENERATOR_v10.gast in this directory, for example
   
        java -jar gastona.jar APK_GENERATOR_v10.gast
   
   you would need to have or adapt following paths and parameters that you can find
   in variables in these two files
   
      <DIR android>       //\pavoTools\android-sdk
      <DIR javac high1.5> //\pavoTools\jdk1.7.0_04
      
  and very important, you have to add your signature or the default debug signature in
  
     <eSignatureArgs> //-sigalg MD5withRSA -digestalg SHA1 -keystore YOURKEYHERE.store -keypass YOURPASSHERE -storepass YOURSTOREPASSHERE
     
  Please note at this point that gastona is licensed under GPL v3. That means that if you plan to deliver a product 
  comming from this source and don't want to open its source you would require a special license or agreement 
  from our side www.wakeupthecat.com.
  
---- Thank you for checking out gastona!
