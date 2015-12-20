== HOW TO COMPILE GASTONA FOR ANDROID (building gastona.apk)

2015.12.20

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
   This procedure is tricky and for that I do no update the android development tools 
   since I am afraid that my "batch" cease working if I do so. 

   If you want to try this way or just want to look which processes and parameters am I using
   take a look at 
   
         BUILD/android-apk/APK_GENERATOR_v10.gast   
         BUILD/android-apk/GASTONA_ANDROID_CONFIG.gasti
         
   if you try to run APK_GENERATOR_v10.gast in this directory, for example
   
        java -jar gastona.jar APK_GENERATOR_v10.gast
   
   you would need to have or adapt following paths and parameters that you can find
   in variables in these two files
   
      <DIR android>       //\pavoTools\android-sdk
      <DIR harmonySDK>    //\pavoTools\harmony-5.0-jdk
      <DIR javac high1.5> //\pavoTools\jdk1.7.0_04
      
  and very important, to have to add your signature or the default debug signature in
  
     <eSignatureArgs> //-sigalg MD5withRSA -digestalg SHA1 -keystore YOURKEYHERE.store -keypass YOURPASSHERE -storepass YOURSTOREPASSHERE
     
  Please note at this point that gastona is licensed under GPL v3. That means that if you plan to deliver a product 
  comming from this source and don't want to open its source you would require a special license or agreement 
  from me and/or Wakeupthecat UG.
  
---- Thank you for giving a try to gastona!