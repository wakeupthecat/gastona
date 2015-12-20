2015.12.20

   Notes for developing and building gastona.jar (gastona PC)
   ----------------------------------------------------------

   Gatona has been compiled with java sdk j2sdk1.4.2_12 and in Windows platform
   you can do it with upper versions but note that from 1.5 you will get
   a lot of warnings.

   The main class to compile is gastona/gastona.java (found at pc/src/gastona)
   So just compiling this class will produce all needed classes to be compiled as well.

   Nevertheless gastona.jar include some important binaries (sqlite) as well as a database
   with all documentation and many other useful scripts and resources that has to be packed
   together in the jar file.

   Currently gastona scripts are used to generate gastona.jar (gastona generates itself!).
   For that you will need a gastona.jar binary which can be found at sourceforge

      http://sourceforge.net/projects/gastona/


   A way to do it in two steps is using gastona scripts

      1) Generate the gastona documentation database:

         This database is used by WelcomeGastona.gast (default script) to get the documentation and samples.

         go to META-GASTONA\WelcomeGastona\genDB and launch generateGastDocDB.gast
         for example: java -jar gastona.jar generateGastDocDB.gast
         it will generate gastonaDoc.db in META-GASTONA\WelcomeGastona\genDB

       2) Compile and generate gastona.jar

          go to BUILD\pc-jar and launch the gastona script GENJAR_GASTONA.gast
          for example: java -jar gastona.jar GENJAR_GASTONA.gast
          (eventually, you might enter the full path of the javac.exe you want to use)
          it will generate the gastona.jar file in BUILD\gastona.jar


   Useful scripts
   ----------------------------------------------------------

   META-GASTONA\WelcomeGastona\WelcomeGastona.gast            script to launch WelcomeGastona (default script)
   META-GASTONA\WelcomeGastona\genDB\generateGastDocDB.gast   script to generate gastona's documentation database
   BUILD\pc-jar\GENJAR_GASTONA.gast                           script to generate gastona.jar


   Notes for developing and building gastona.apk (gastona Android App)
   -------------------------------------------------------------------

   This procedure is described in the BUILD\android-apk\README_HOW_TO_COMPILE_GASTONA_FOR_ANDROID.txt


   Source code for both pc (desktop => jar) and android (dalvik => apk)
   -------------------------------------------------------------------------------

   Different java compilers has to be used to generate jar (pc) or apk (android) products. Also
   these two platforms offer different libraries, specially for GUI development.

   Nevertheless since both share the base language which is java, many classes can share the source
   code as well. Sharing source code for both compilers is done by using the javac compiler option "sourcepath"
   where two root paths are given, then if the compiler does not find the source to compile in the first path
   (the specific one, e.g. for pc) it takes it from the second one (common or base path).

      Source code for Gastona has following directory structure:

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

