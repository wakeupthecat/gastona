/*
Copyright (C) 2013 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command CALL STREAMS

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       CALL CAPTURE
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdCallCapture
   <importance> 3
   <desc>       //To call a process using streams stdin, stdout and stderr

   <help>
      //
      //  Call a process providing a stdin content and captuting the stdout
      //
      //    CALL CAPTURE, binaryPath, inputContent
      //                , FILE ERROR, :mem problems
      //

   <aliases>
      alias
      CALL IO
      CALL CAP
      CALL CAPTURE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Executes the process 'binaryPath' streaming 'inputContent' as stdin and capturing the stdout (into default listix target)

   <syntaxParams>
      synIndx, name          , defVal, desc
         1   , binaryPath    ,       , //path of the binary to execute
         1   , inputContent  ,       , //String containg the input to the process
         

   <options>
      synIndx, optionName  , parameters     , defVal    , desc
         1   , FILE IN     , fileName       ,           , //Filename containing input to the process (stdin)
         1   , FILE OUT    , fileName       ,           , //Filename containing output of the process (stdout)
         1   , FILE ERR    , fileName       ,           , //Filename containing errors of the process (stderr)

   <examples>
      gastSample

      calling dir

   <calling dir>
      //#javaj#
      //
      //   <frames> main
      //
      //   <layout of main>
      //       EVA, 10, 10, 5, 5
      //       
      //          , X
      //          , lOutput
      //        X , xOut
      //        X , oConsole
      //
      //#listix#
      //
      //   <main>
      //      CALL CAPTURE, @<:mutool sqlite>, .help
      //                  , FILE OUT, :mem salida
      //      -->, xOut load, :mem salida
      //


#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.streams.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

/**
*/
public class cmdCallCapture implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "CALLCAPTURE",
          "CALLCAP",
          "CALLSTREAMS",
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

      // pass parameters (solved)
      //
      String strProcess  = cmd.getArg(0);
      String firstInline = cmd.getArg(1);

      callCaptureInpOut.callCapture ("CALLCAP", strProcess, firstInline, that, commandEva, indxComm);
      return 1;
   }
}
