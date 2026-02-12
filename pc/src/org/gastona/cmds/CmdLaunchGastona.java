/*
library de.elxala
Copyright (C) 2005-2026 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package org.gastona.cmds;

import java.util.*;
import listix.*;
import listix.cmds.*;
import listix.cmds.commandable;

import de.elxala.db.utilEscapeStr;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.TextFile;


/**
   //(o) WelcomeGastona_source_listix_command GASTONA

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       GASTONA
   <groupInfo>  system_run
   <javaClass>  org.gastona.cmds.CmdLaunchGastona

   <importance> 3

   <desc> //To call gastona with a script
   <help>
       //
       //  Launches a gastona script whithout waiting for its finalization (see also command LAUNCH).
       //

   <aliases>
      alias
      GASTONA
      GAST
      LAUNCH GASTONA

   <syntaxHeader>
      synIndx, groupInfo, importance, desc
         1   , system   ,    3      , //Launches gastona (execute and do not wait)

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , script       ,       , //script gastona to be called
         1   , parameter    ,  " "  , //first parameter of the program
         1   , ...          ,  " "  , //further parameters

   <options>
      synIndx, optionName  , parameters, defVal, desc
          1  , ON BATCH    , 1 / 0    ,    0  , If true (1) executes the command using a batch file (or script in linux). Note in Windows it might be necessary in some special cases due to a bug in the java function Runtime.getRuntime().exec (I guess)
          1  , VERBOSE     , 0 / 1/ 2 ,    0  , If 1 prints out the command at the beginning ("GAST [script]"), if (2) prints out the consuming time at the end of the command as well

   <examples>
      gastSample
      launching gast standard scripts
      launching a gast script in a var

   <launching gast standard scripts>
      //#javaj#
      //
      //   <frames> F, "Utility standard scripts", 500, 400
      //
      //   <layout of F>
      //
      //      EVA, 6, 6, 4, 4
      //
      //         ,          , X
      //       X , iiScripts, xxScriptCode
      //         , lDesc    , lFileName
      //         , bLaunch  ,
      //
      //   <sysDefaultFonts>
      //      Consolas, 14, 0, TextArea.font
      //
      //
      //#data#
      //
      //    <iiScripts visibleColumns> name
      //
      //    <iiScripts>
      //       name      , desc                         , path
      //       arces     , sqlite3 db (+ deepdb) viewer , META-GASTONA/utilApp/arces/arces.gast
      //       xmelon    , xml to db util               , META-GASTONA/utilApp/xmelon/xmelon.gast
      //       dirMe     , search files                 , META-GASTONA/utilApp/dirMe/dirMe.gast
      //       editor    , simple text editor           , META-GASTONA/utilApp/editor/EmergencyEditor.gast
      //       dirComp   , compare directories          , META-GASTONA/utilApp/files/simpleCompareDirs.gast
      //       hashAndDup, Hash & duplicated files      , META-GASTONA/utilApp/files/hashAndDuplicatedFiles.gast
      //
      //#listix#
      //
      //   <-- iiScripts>
      //      -->, lDesc data!,, @<iiScripts selected.desc>
      //      -->, lFileName data!,, @<iiScripts selected.path>
      //      CHECK, RFILE, @<iiScripts selected.path>, -->, xxScriptCode data!,, //script not "@<iiScripts selected.path>" not found!
      //      -->, xxScriptCode load, @<iiScripts selected.path>
      //
      //   <-- iiScripts 2>  LSX, LANZA
      //   <-- bLaunch>     LSX, LANZA
      //   <LANZA>
      //       CHECK, VAR, iiScripts selected.path
      //       GAST, @<iiScripts selected.path>

   <launching a gast script in a var>
      //#listix#
      //
      //	<main>
      //     GAST, //:utf-8:@<:encode-utf-8 SCRIPT_ON_VAR>
      //
      //   <SCRIPT_ON_VAR>
      //     // #javaj#
      //     //   <frames> ooSansi's dog
      //     // #listix#
      //     //   <main> King

#**FIN_EVA#
*/
public class CmdLaunchGastona implements commandable
{
   public static final String EXTRA_VALUE_NAME = "gastonaFlex.LaunchGastonaParameters";

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "LAUNCH GASTONA",
          "GASTONA",
          "GAST",
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      String [] aa = cmd.getArgs (true);
      if (aa.length > 0)
         aa[0] = org.gastona.commonGastona.getGastConformFileName (aa[0]);

      String [] arrComanda = new String [aa.length + 4];
      arrComanda[0] = System.getProperty ("java.home", "") + "/bin/java";
      arrComanda[1] = "-cp";
      arrComanda[2] = System.getProperty ("java.class.path", ".");
      arrComanda[3] = "gastona.gastona";
      for (int ii = 0; ii < aa.length; ii ++)
         arrComanda[4+ii] = aa[ii];

      // copy parameters
      // java -cp classpath gastona.gastona script.gast pa1 pa2
      //

      cmd.getLog().dbg (2, "GAST", "passed parameters " + cmd.getArgSize ());

      // for (int ii = 0; ii < arrComanda.length; ii ++)
      //    System.out.println (arrComanda[ii]);
      javaRun.launch (arrComanda);

      cmd.checkRemainingOptions ();
      return 1;
   }
}
