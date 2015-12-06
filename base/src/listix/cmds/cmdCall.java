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
   //(o) WelcomeGastona_source_listix_command CALL

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       CALL
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdCall
   <importance> 6
   <desc>       //To call an external program waiting for its termination

   <gastonaSecure> 0

   <help>
      //
      //  Calls an external program or system command using the standard output streams (stdout and stderr)
      //  and waiting for the termination of the new process.
      //
      //  WARNING: Avoid, or be very careful, calling critical system commands like file deletion etc.
      //           and, in general, call system commands or other executables always with the proper
      //           knowledge about them. Specially in this case it is strongly recomendable NOT to
      //           practice the try-error approach.
      //
      //
      //  Note that calling system commands is strictly OS dependent, so if you want your script to
      //  work properly in all operative systems you may have to make the adecuate checks before
      //  perform any call.
      //
      //
      //  ------ Notes for Windows ------
      //
      //  In order to execute batch files or command line programs (not windows programs) you may use
      //  the windows command CMD.exe, for example to call the command "dir"
      //
      //        CALL, "CMD /C dir"
      //
      //  Do not forget the option /C, since without it the call would hang and you would have to
      //  close the application using the Task Manager.
      //
      //  A useful commnad in windows is "start". It opens the system associated application with a
      //  given extension, for instance (using LAUNCH instead of CALL)
      //
      //       LAUNCH, //CMD /C start "" "test.html"
      //
      //
      //  ------ General Troubleshooting ------
      //
      //  A very typical issue is using the quotation marks in parameters. In many cases (files and
      //  directories) these might be needed, for example supposing a system command accepts a file
      //  as parameter, the call
      //
      //    CALL, //myCommandXY @<myFile1>
      //
      //  can still work in many cases but it would very probably fail in case the name contained in
      //  myFile1 has white spaces (e.g. "first file.txt"), and then this other call
      //
      //    CALL, //myCommandXY "@<myFile1>"
      //
      //  could solve the problem. Unfortunatelly there is no golden rule for using quotation marks in
      //  system commands or executables, although using them is usually the proper way, for each new
      //  command we want to call it has to be checked.
      //
      //  In other problems it is recomended to get the final call line (with all variables solved)
      //  and test it directly into a system shell or DOS window. If the command there fails then
      //  the problem is clearly in our call and it can be fixed with more knowledge about the
      //  system command or executable.
      //
      //  In very rare ocassions a call that fails through gastona CALL command will work if executed
      //  directly in a DOS window or system shell, in these cases try if the option "ON BATCH, 1"
      //  helps.
      //
      //  With Gastona is also possible to build your own batch file or system script and call it
      //  either with CALL or LAUNCH. In problematic cases this technique can also be used.
      //
      //  For example:
      //
      //      <myCall>
      //          GEN, myBatch.bat, batchBody
      //          CALL, //CMD /C myBatch.bat
      //
      //      <batchBody>
      //          //@echo off
      //          //dir
      //
      // NOTE: In Linux and Mac OS X, might need to perform a previous call to "chmod"
      //
      //          CALL, //chmod 777 myBatch.bat
      //          CALL, //./myBatch.bat

   <aliases>
      alias

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    6      , //Executes a command through Runtime's exec method using defaults output streams

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , command      ,       , //System command or program to call
         1   , ...          ,       , //further parameters might be given in separate columns, although it is also possible to write them all together within a single line

   <options>
      synIndx, optionName  , parameters, defVal, desc
          1  , ON BATCH    , 1 / 0    ,    0  , If true (1) executes the command using a batch file or a shell script in linux. Note in Windows it might be necessary in some special cases due to a bug in the java function Runtime.getRuntime().exec (I guess)
          1  , VERBOSE     , 0 / 1/ 2 ,    0  , If 1 prints out the command at the beginning ("CALL [command]"), if (2) prints out the consuming time at the end of the command as well
          1  , *CONSOLE    , 1 / 0    ,    0  , If true (1) a special command console is shown ...
          1  , *PROMPT     , 1 / 0    ,    0  , If true (1) prompts through a dialog for the execution of the command
          1  , *TIME       , 1 / 0    ,    0  , If true (1) prints out a string with the consuming time of the command.

   <examples>
      gastSample
      calling java

   <calling java>

      //#javaj#
      //
      //   <frames> F, "listix command CALL example"
      //
      //   <layout of F>
      //
      //   EVA, 10, 10, 5, 5
      //   ---,          ,         , X
      //      , bCallJava, bCallDir
      //    X , oConsola , -       , -
      //
      //#listix#
      //
      //   <-- bCallJava>    CALL, java
      //
      //   <-- bCallDir>
      //     CHECK, LINUX,
      //          , ELSE, CALL, //CMD /C dir
      //     CALL, //ls -la
      //

   <Windows NET help>
      //#javaj#
      //
      //   <frames> fNETHelp, NET commands help, 700, 500
      //
      //   <layout of fNETHelp>
      //      EVA, 10, 10, 5, 5
      //
      //         ,          ,  X
      //       X , tCommands, xHelp
      //
      //   <sysDefaultFonts>  Consolas, 12, 0, TextArea
      //
      //#data#
      //
      //
      //   <tCommands>
      //      name
      //
      //      NAMES
      //      SERVICES
      //      SYNTAX
      //      ""
      //      ACCOUNTS
      //      COMPUTER
      //      CONFIG
      //      CONFIG SERVER
      //      CONFIG WORKSTATION
      //      CONTINUE
      //      FILE
      //      GROUP
      //      HELP
      //      HELPMSG
      //      LOCALGROUP
      //      NAME
      //      PAUSE
      //      PRINT
      //      SEND
      //      SESSION
      //      SHARE
      //      START
      //      STATISTICS
      //      STOP
      //      TIME
      //      USE
      //      USER
      //      VIEW
      //
      //#listix#
      //
      //   <main0>
      //      LSX, exit if linux
      //      VAR=, tmp  , @<:lsx tmp text>
      //      VAR=, xHelp fileName, @<tmp>
      //
      //   <exit if linux>
      //      CHECK, LINUX
      //
      //      BOX, I, This is a Windows specific sample
      //      MSG, javaj doExit
      //
      //   <-- tCommands>
      //      CALL, //CMD /C net help @<tCommands selected.name> > "@<tmp>"
      //      MSG, xHelp load
      
#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

public class cmdCall implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "CALL",
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
      boolean onBatch = "1".equals (cmd.takeOptionString(new String [] { "ONBATCH", "BATCH"} , "0"));
      String preShell = cmd.takeOptionString(new String [] { "PRESHELL", "SHELL"} , null);

      cmd.getLog().dbg ((verbose >= 1) ? 0: 2, "CALL", "CALL [" + arrComanda[0] + "]" + (onBatch ? " (on Batch)":"") + (preShell != null ? " (preShell [" + preShell + "]":""));

      if (onBatch)
      {
         prepareBatch (arrComanda);
         arrComanda = new String [] { TMP_BATCH_NAME };
         cmd.getLog().dbg (2, "CALL", "Command on batch [" + TMP_BATCH_NAME + "]");
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
         cmd.getLog().dbg ((verbose >= 1) ? 0: 2, "CALL", "One parameter in call, using exec(String)");
         javaRun.executePreShell (preShell, arrComanda[0], true);
      }
      else
      {
         cmd.getLog().dbg ((verbose >= 1) ? 0: 2, "CALL", "using exec(String[])");
         javaRun.executePreShell (preShell, arrComanda, true);
      }

      long incMilis = System.currentTimeMillis () - initMilis;
      cmd.getLog().dbg ((verbose >= 2) ? 0: 2, "CALL", "consuming time = " + (incMilis/1000.) + " seconds");

      cmd.checkRemainingOptions (true);
      return 1;
   }

   private String TMP_BATCH_NAME = null;

   private String getBatchName ()
   {
      if (TMP_BATCH_NAME != null)
         return TMP_BATCH_NAME;

      // for Windows has to be .bat for linux it doesn't matter
      TMP_BATCH_NAME = fileUtil.createTemporal ("callbatch", ".bat");

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
         javaRun.execute (new String [] { "chmod", "777",  getBatchName () });
   }
}
