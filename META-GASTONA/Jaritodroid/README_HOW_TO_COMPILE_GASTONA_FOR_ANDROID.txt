== HOW TO COMPILE GASTONA FOR ANDROID

2015.12.06

   The relevant information to compile gastona for android
   at least as java application is 
   
   manifest:
      ..\..\android\AndroidManifest.xml

   main class:
      ..\..\android\src/org/gastona/gastonaMainActor.java
   
   source path (order is important!):
      ..\..\android\src;..\..\base\src

   resources:
      ..\..\android\res

   resources:
      ..\..\META-GASTONA\js  mapped to META-GASTONA\js
      ..\..\android\assets   mapped to .

   With this information and using some Android IDE or tools like Ant
   you should be able to build a basic gastona.apk.
   I can not guide you here since I do not use neither IDE or Ant to do it.
   
=== THE WAY I BUILD GASTONA.APK   
   
   Time ago, when I started compiling gastona for Android, I did manage to write
   a gastona script for the apk generation that I am using since then. But this
   procedure is tricky since I do no update the development tools since I am afraid
   that my "batch" cease working if I do so. 

   If you want to try this way or just want to look which processes and parameters am I using
   take a look at 
   
         META-GASTONA\Jaritodroid\APK_GENERATOR_v10.gast   
         META-GASTONA\Jaritodroid\GASTONA_ANDROID_CONFIG.gasti
         
   if you try to run APK_GENERATOR_v10.gast in this directory, for example
   
        java -jar gastona.jar APK_GENERATOR_v10.gast
   
   you would need to have or adapt following paths and parameters that you can find
   in variables in these two files
   
      <DIR android>       //\pavoTools\android-sdk
      <DIR harmonySDK>    //\pavoTools\harmony-5.0-jdk
      <DIR javac high1.5> //\pavoTools\jdk1.7.0_04
      
  and very important, add your signature or the default debug signature in
  
     <eSignatureArgs> //-sigalg MD5withRSA -digestalg SHA1 -keystore YOURKEYHERE.store -keypass YOURPASSHERE -storepass YOURSTOREPASSHERE
     
  Please note at this point that gastona is licensed under GPL v3. That means that if you plan to deliver a product 
  comming from this source and don't want to open its source you would require a special license or agreement 
  from me and/or Wakeupthecat UG.
  
---- Thank you for checking out gastona!