/*
library listix (www.listix.org)
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
   //(o) WelcomeGastona_source_listix_command JAVA STATIC

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       PYTHON
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdPython
   <importance> 3
   <desc>       //To call python with a script

   <help>
      //
      //  Calls python EXPERIMENTAL for Windows platform
      //
      //    PYTHON, python expresion
      //


   <aliases>
      alias
      MONTY


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Makes a call to python binary with the given script, the output will be written in the current listix target

   <syntaxParams>
      synIndx, name          , defVal, desc
        1    , [python expresion],       , //Python expresion

   <options>
      synIndx, optionName  , parameters     , defVal    , desc
         1   , BODY        , pythonCode     ,           , Python code
         1   , FILE INPUT  , filename       ,           , File to be used for input
         1   , FILE OUTPUT , filename       ,           , File to be used for output

   <examples>
      gastSample

      calling python1
      calling python2

   <calling python1>
      //#javaj#
      //
      //   <frames> oConsola
      //
      //#listix#
      //
      //   <main>
      //      PYTHON, //print ("Hola Python!")

   <calling python2>
      //#javaj#
      //
      //   <frames> main, Monty
      //
      //   <layout of main>
      //      EVA, 10, 10, 4, 4
      //
      //         , X
      //       X , xMonty
      //         , bRun
      //       X , oConso
      //
      //   <sysDefaultFonts>
      //      Consolas, 13, 0, TextArea
      //
      //#data#
      //
      //   <xMonty>
      //      //def quickSort(arr):
      //      //     less = []
      //      //     pivotList = []
      //      //     more = []
      //      //     if len(arr) <= 1:
      //      //         return arr
      //      //     else:
      //      //         pivot = arr[0]
      //      //         for i in arr:
      //      //             if i < pivot:
      //      //                 less.append(i)
      //      //             elif i > pivot:
      //      //                 more.append(i)
      //      //             else:
      //      //                 pivotList.append(i)
      //      //         less = quickSort(less)
      //      //         more = quickSort(more)
      //      //         return less + pivotList + more
      //      //
      //      //a = [4, 65, 2, -31, 0, 99, 83, 782, 1]
      //      //a = quickSort(a)
      //      //
      //      //print (a)
      //      //
      //
      //#listix#
      //
      //   <-- bRun>
      //      MSG, oConso clear
      //      PYTHON, @<xMonty>

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import listix.cmds.callCaptureInpOut;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

/**
   Listix command PYTHON

*/
public class cmdPython implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "PYTHON",
          "MONTY",
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

      String script = cmd.getArg(0);

      // be compatible with old "ON FLY"
      if (cmd.getArgSize () > 1)
      {
         if (script.equals ("") || script.equalsIgnoreCase ("onfly") || script.equalsIgnoreCase ("on fly"))
            script = cmd.getArg(1);
         else {
            cmd.getLog().err ("PYTHON", "unsupported option \"" + script + "\"");
            return 1;
         }
      }

      that.log().dbg (4, "PYTHON", "inline script");
      String pythonProcess = utilSys.isOSWindows () ? microToolInstaller.getExeToolPath("python"): "python";

      callCaptureInpOut.callCapture ("PYTHON", pythonProcess, script, that, commandEva, indxComm);

      // callCapture already process the options but it is not updated in this variable "cmd"!
      // so we cannot check the remaining options now
      // we should pass cmd to callCapture, then remaining options will be ok at this point
      //
      //cmd.checkRemainingOptions ();
      return 1;
   }
}
