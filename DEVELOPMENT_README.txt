28.04.2010 22:04

   Notes for developing and building gastona.jar
   ----------------------------------------------

   I compile gastona with java sdk j2sdk1.4.2_12 and in Windows platform
   you can do it with upper versions but note that from 1.5 you will get
   a lot of warnings.

   A way to do it in one step is

      go to META-GASTONA\Jarito and launch the gastona script GENJAR_GASTONA.gast
      (i.e. java -jar gastona.jar GENJAR_GASTONA.gast)
      it will generate the gastona.jar file into the OUT subdirectory

      eventually, you might enter the full path of the javac.exe you want to use

   Code documentation
   --------------------------------------------------

   Right now the code is not in a very standard way documented (java doc) :(

   Here a small map:

      -------GASTONA-------------

      gastona/gastona.java
         Is the main of gastona.jar (see manifest)

      gastona/cmds
         Contain the listix commands MESSAGE, JAVAJ, BOX, AND SETUPDATE

      -------JAVAJ-------------

      javaj/java36.java
         It is the javaj's kern

      javaj/laya.java
         Perform the layout work of javaj widgets

      javaj/onFlyWidgetSolver.java
         Here are instanciated all javaj primitive widgets

      javaj/widgets
         Allmost all widgets are contained in this directory, and all start with "z"
         (e.g. zButton.java etc)

      -------LISTIX-------------

      listix/listix.java
         It is the listix's kern

      listix/cmds
         Almost all listix commands are in this directory, they start with "cmd" (e.g. cmdSetVariable.java)

      listix/cmds/commander.java
         This class load all primitve listix's commands

      listix/table
         This directory contains the real implementation of the command LOOP (or LOOP TABLE)


      -------LIBRARY-------------

      de/elxala/db/sqlite/sqlSolver.java
         This class calls sqlite and retrieve the results (used for both widgets and commands)

      de/elxala/db/sqlite/tableROSelect.java
         Implements the transparent windowing mechanism for Read Only Selects
         (the users can make SQL selects with no limit of records, this will be windowed automatically)

      de/elxala/Eva
         Implementation of the Eva format, inclusive parsing eva files as well

      de/elxala/Eva/layout/EvaLayout.java
         Eva layout implementation (as java layout manager LayoutManager2)

      de/elxala/math/polac
         Implementation of formulas

      de/elxala/mensaka
         Implementation of the Mensaka mechanism

      de/elxala/parse/parsons/aLineParsons.java
         Kern of command PARSONS

      de/elxala/zServices
         Error/Log feature and micro Tool installer (e.g. sqlite.exe)

      de/elxala/langutil
         Several system and base utilities



---- Alejandro ----