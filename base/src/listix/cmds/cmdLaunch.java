/*
library listix (www.listix.org)
Copyright (C) 2005 Alejandro Xalabarder Aulet

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

/*
   //(o) WelcomeGastona_source_listix_command LAUNCH

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       LAUNCH
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdLaunch
   <importance> 3

   <gastonaSecure> 0

   <desc> To launch an external program
   <help>
       //
       //  Launches an external program or system command using the standard output streams (stdout stderr)
       //  whithout waiting for its finalization (see also command CALL).
       //  This kind of call is useful, for instance, to launch an external editor or html browser
       //  or whenever a process should not block the flow of Listix.
       //
       //  WARNING: Avoid, or be very careful, calling critical system commands like file deletion etc.
       //           and, in general, call system commands or other executables always with the proper
       //           knowledge about them. Specially in this case it is strongly recomendable NOT to
       //           practice the try-error approach.
       //
       //  ---- Notes for windows ----
       //      (the same as in command CALL)
       //
       //  ---- General Troubleshooting ----
       //      (the same as in command CALL)
       //

   <aliases>
      alias
      LANZA

   <syntaxHeader>
      synIndx, groupInfo, importance, desc
         1   , system   ,    3      , //Launches (execute and do not wait) a command through Runtime's exec method using defaults output streams

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , command      ,       , //program to call
         1   , parameter    ,  " "  , //first parameter of the program
         1   , ...          ,  " "  , //further parameters

   <options>
      synIndx, optionName  , parameters, defVal, desc
          1  , ON BATCH    , 1 / 0    ,    0  , If true (1) executes the command using a batch file (or script in linux). Note in Windows it might be necessary in some special cases due to a bug in the java function Runtime.getRuntime().exec (I guess)
          1  , VERBOSE     , 0 / 1/ 2 ,    0  , If 1 prints out the command at the beginning ("CALL [command]"), if (2) prints out the consuming time at the end of the command as well

   <examples>
      gastSample
      launching something

   <launching something>

      //#javaj#
      //
      //   <frames> F, "launching explorer"
      //
      //   <layout of F>
      //
      //      PANEL, X
      //      bTestCall
      //
      //#listix#
      //
      //   <-- bTestCall>
      //       CHECK, LINUX, LAUNCH, //explorer.exe /n,/e,/select,.
      //       LAUNCH, //vi
      //

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

public class cmdLaunch implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "LAUNCH",
          "LANZA",
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

      // build the parameter array (call to exec(String[])
      //
      String [] arrComanda = new String [cmd.getArgSize()];
      for (int ii = 0; ii < cmd.getArgSize(); ii ++)
      {
         arrComanda[ii] = cmd.getArg (ii);
      }

      int verbose = stdlib.atoi (cmd.takeOptionString("VERBOSE", "0"));
      boolean onBatch = "1".equals (cmd.takeOptionString("ONBATCH", "0"));
      String preShell = cmd.takeOptionString(new String [] { "PRESHELL", "SHELL"} , null);

      cmd.getLog().dbg ((verbose >= 1) ? 0: 2, "LAUNCH", "LAUNCH [" + arrComanda[0] + "]" + (onBatch ? " (on Batch)":"") + (preShell != null ? " (preShell [" + preShell + "]":""));

      if (onBatch)
      {
         prepareBatch (arrComanda);
         arrComanda = new String [] { TMP_BATCH_NAME };
         cmd.getLog().dbg (2, "LAUNCH", "Command on batch [" + TMP_BATCH_NAME + "]");
      }

      long initMilis = System.currentTimeMillis ();

      //(o) JAVA EXEC !!!!
      if (arrComanda.length == 1 && !onBatch)
      {
         //Bizarre solution
         //
         //    To allow calls with one String (e.g. [CMD /C start "" blabla]) in Windows
         //    (also in Linux but since java BUG XXXX linux programs should use CALL with more parameters
         //    in calls that involve files) we decide to call exec (String) instead of (String []) if we
         //    find only one parameter, this can result in a fail to the call if the only parameter is an exe
         //    file or a batch WITH BLANKS AND IT IS NOT EXPLICITY ENVOLVED WITH QUOTES. That is
         //       CALL, //My bizarre batch.exe
         //    will fail, while
         //       CALL, //"My bizarre batch.exe"
         //    will run properly
         //
         cmd.getLog().dbg ((verbose >= 1) ? 0: 2, "LAUNCH", "One parameter in launch call, using exec(String)");
         javaRun.executePreShell (preShell, arrComanda[0], false);
      }
      else
      {
         cmd.getLog().dbg ((verbose >= 1) ? 0: 2, "LAUNCH", "using exec(String[])");
         javaRun.executePreShell (preShell, arrComanda, false);
      }
      long incMilis = System.currentTimeMillis () - initMilis;
      cmd.getLog().dbg ((verbose >= 2) ? 0: 2, "LAUNCH", "time to launch = " + (incMilis/1000.) + " seconds");

      cmd.checkRemainingOptions (true);
      return 1;
   }

   private String TMP_BATCH_NAME = null;

   private String getBatchName ()
   {
      if (TMP_BATCH_NAME != null)
         return TMP_BATCH_NAME;

      // for Windows has to be .bat for linux it doesn't matter
      //(o) TOSEE_misc Que pasa si cerramos la app gastona (se borra el batch ?) a lo mejor no esta finalizado
      TMP_BATCH_NAME = fileUtil.createTemporal ("launchbatch", ".bat");

      return TMP_BATCH_NAME;
   }

   private void prepareBatch (String [] commandStrArr)
   {
      TextFile batch = new TextFile ();
      if (batch.fopen (getBatchName (), "w"))
      {
         if (! utilSys.isSysUnix)
            batch.writeLine ("@echo off");

         for (int ii = 0; ii < commandStrArr.length; ii ++)
         {
            if (ii > 0) batch.writeLine (" ");
            batch.writeLine (commandStrArr[ii]);
         }

         batch.fclose ();
      }

      // giving the linux script execution right (and read-write)
      //
      if (utilSys.isSysUnix)
         //(o) JAVA EXEC !!!!
         javaRun.executePreShell ("su", new String [] { "chmod", "777",  getBatchName () }, true);
   }
}

