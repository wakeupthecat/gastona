2017.03.18

   Notes for developing and building gastona.jar (gastona PC)
   ----------------------------------------------------------

   Since the inclusion of Rhino (embedded Javascript language) gastona has to be compiled with
   javac 1.5 or above.

   The main class to compile is gastona/gastona.java (found at pc/src/gastona)
   So just compiling this class will produce all needed classes to be compiled as well.

   Nevertheless it is not enough to pack all these classes in a file (e.g. gastona.jar)
   A basic gastona.jar has to include also some important binaries (sqlite), own documentation 
   and many other useful scripts and resources.

   Currently two gastona scripts are used to generate gastona.jar (gastona generates itself!).

   For that you will need a gastona.jar binary which can be found at sourceforge

      http://sourceforge.net/projects/gastona/

   Once you have some version of gastona.jar you can use the scripts to gerate a new gastona.jar
   from your sources.

   1) Generate - if needed - the gastona documentation database

      This database is used by WelcomeGastona.gast (default script) to get the documentation and samples.
      It has to be found by gastona in the file

         META-GASTONA\WelcomeGastona\gastonaDocScript.sql

      If it is not there or simply want to generate it again due to some change
      in the documentation texts following commands will do it

         cd META-GASTONA\WelcomeGastona
         java -jar gastona.jar generateGastDocSQLScript.gast


    2) Compile and generate gastona.jar

       The script GENJAR_GASTONA.gast will generate gastona.jar properly

         cd BUILD\pc-jar
         java -jar gastona.jar GENJAR_GASTONA.gast

       eventually, you might want to give the full path of the javac.exe you want to use

       If the generation succeed the final product can be found at

          BUILD\gastona.jar


   Useful scripts
   ----------------------------------------------------------

   META-GASTONA\WelcomeGastona\WelcomeGastona.gast            script to launch WelcomeGastona (default script)
   META-GASTONA\WelcomeGastona\generateGastDocSQLScript.gast  script to generate gastona's documentation database
   BUILD\pc-jar\GENJAR_GASTONA.gast                           script to generate gastona.jar


   Notes for developing and building gastona.apk (gastona Android App)
   -------------------------------------------------------------------

   This procedure is described in the BUILD\android-apk\README_HOW_TO_COMPILE_GASTONA_FOR_ANDROID.txt


   Source code for both pc (desktop => jar) and android (dalvik => apk)
   -------------------------------------------------------------------------------

   Different java compilers has to be used to generate jar (pc) or apk (android) products. Also
   these two platforms offer different libraries, specially for GUI development.

   However since both share the same base java language many classes are identical(*). Sharing source code 
   for both compilers is done by using the javac compiler option "sourcepath" where two root paths are 
   given, then if the compiler does not find the source to compile in the first path
   (the specific one, e.g. for pc) it takes it from the second one (common or base path).

      Source code for Gastona has following directory structure:

         - base/src    : all common classes to pc and android
         - pc/src      : all specific classes for pc development (e.g. widgets, swing related code etc)
         - android/src : android specific classes (native widgets, activities, intents etc)

      then when invoquing the specific java compiler, the option "-sourcepath" is used to get the proper sources

         for pc (gastona.jar)      : -sourcepath pc/src;base/src
         for android (gastona.apk) : -sourcepath android/src;base/src

  (*) At some point the relation between the common classes and the system specific ones was

            subdir     # java files    bytes
            --------  -------------    ----------
            base          502           5.678.320
            pc            114             944.548
            android        79             510.283

   Code documentation
   --------------------------------------------------

   A small map of the source code structure:

   pc\src\gastona\gastona.java                    Main gastona for PC
   android\src\org\gastona\gastonaMainActor.java  Main activity for android


   Now starting path from src (e.g. pc/src, android/src or base/src):


   de/elxala/db            all db and sqlite related stuff
   de/elxala/Eva           base structure and format used in gastona scripts
   de/elxala/langutil      general java language utilities and facilities (e.g. javaRun, TextFile)
   de/elxala/math          math stuff including 2D calculations for drawing etc
   de/elxala/mensaka       gastona's component communication mechanism used by javaj widgets, listix messages ...
   de/elxala/parse         parsing classes: general text parsing, svg, xml and json
   de/elxala/zServices     logging system and microTool concept used to launch sqlite (also ruby, lua and perl in windows version)

   javaj                   all related with GUI engine (javaj) and its widgets (zWidgets)
   listix                  all related with Logic engine listix (main class listix/listix.java) and its commands (listix/cmds/cmdXXXX.java)

   org/gastona/net         features related with network communication UDP and the HTTP miniserver micoHTTP server

   org/mozilla             complete Rhino source code (https://github.com/mozilla/rhino)
