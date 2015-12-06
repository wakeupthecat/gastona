2015.12.06

   Notes for developing and building gastona.jar (gastona PC)
   ----------------------------------------------------------

   Gatona has been compiled with java sdk j2sdk1.4.2_12 and in Windows platform
   you can do it with upper versions but note that from 1.5 you will get
   a lot of warnings.

   Currently gastona scripts are used to generate gastona.jar (so it generates itself!),
   for that you will need a gastona.jar binary

   A way to do it in two steps is using gastona scripts (a gastona.jar file is required)
   
      1) Generate the gastona documentation database:

         This database is used by WelcomeGastona.gast (default script) to get the documentation and samples.
         
         go to META-GASTONA\WelcomeGastona\genDB and launch generateGastDocDB.gast
         for example: java -jar gastona.jar generateGastDocDB.gast
         it will generate gastonaDoc.db in META-GASTONA\WelcomeGastona\genDB

       2) Compile and generate gastona.jar

          go to META-GASTONA\Jarito and launch the gastona script GENJAR_GASTONA.gast
          for example: java -jar gastona.jar GENJAR_GASTONA.gast
          (eventually, you might enter the full path of the javac.exe you want to use)
          it will generate the gastona.jar file in META-GASTONA\Jarito\OUT

          
   Useful scripts 
   ----------------------------------------------------------

   META-GASTONA\WelcomeGastona\WelcomeGastona.gast            script to launch WelcomeGastona (default script)
   META-GASTONA\WelcomeGastona\genDB\generateGastDocDB.gast   script to generate gastona's documentation database
   META-GASTONA\Jarito\GENJAR_GASTONA.gast                    script to generate gastona.jar
   META-GASTONA\Jarito\OUT\gastona.jar                        generated gastona.jar
   
          
   Notes for developing and building gastona.apk (gastona Android App)
   -------------------------------------------------------------------

   This procedure is described in the META-GASTONA\Jaritodroid\README_HOW_TO_COMPILE_GASTONA_FOR_ANDROID.txt


   Sources are splitted in 'base' / 'pc' and 'android'
   --------------------------------------------------------
   
      Gastona keeps as many features and code as possible in common in its version for Desktop (pc = windows, linux, mac-os)
      and android. For that, eventhough the needed java compilers are different, a big part of the source code (about 180 classes)
      is shared for both compilers. This can be done using the javac compiler option sourcepath and giving two paths there
      so if the class is not found in the first path it is taken from the second one (in this case base or common path).

      Gastona have following directory structure:
      
         - base/src    : all common classes to pc and android
         - pc/src      : all specific classes for pc development (e.g. widgets, swing related code etc)
         - android/src : android specific classes (native widgets, activities, intents etc)

      then when invoquing the specific java compiler, the option "-sourcepath" is used to get the proper sources
      
         for pc (gastona.jar)      : -sourcepath pc/src;base/src
         for android (gastona.apk) : -sourcepath android/src;base/src
         

   Code documentation
   --------------------------------------------------

   A small map of the source code structure:

   pc\src\gastona\gastona.java                    Main gastona for PC
   android\src\org\gastona\gastonaMainActor.java  Main activity for android

   
   Now starting path from src (e.g. pc/src, android/src or base/src):
   

   de/elxala/db            stuff related with sqlite db (native sql for gastona)
   de/elxala/Eva           base structure and format used in gastona scripts
   de/elxala/langutil      general java language utilities and facilities (e.g. javaRun, TextFile)
   de/elxala/math          math stuff including 2D calculations
   de/elxala/mensaka       gastona's component communication mechanism (used by javaj widgets, listix messages ...)
   de/elxala/parse         parsing classes: general text parsing, svg, xml and json
   de/elxala/zServices     logging system and microTool concept (used to launch sqlite and ruby, lua and perl in windows version)

   javaj                   all related with GUI engine (javaj) and its widgets (zWidgets)
   listix                  all related with Logic engine listix (main class listix/listix.java) and its commands (listix/cmds/cmdXXXX.java)

   org/gastona/net         features related with network communication UDP and the HTTP miniserver micoHTTP server
   
