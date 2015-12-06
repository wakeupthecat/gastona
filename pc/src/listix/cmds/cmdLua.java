/*
Copyright (C) 2013 Alejandro Xalabarder Aulet, Wakeupthecat UG

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
   <name>       LUA
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdLua
   <importance> 3
   <desc>       //To call Lua with a script

   <help>
      //
      //  Calls Lua EXPERIMENTAL for Windows platform
      //
      //    LUA, ONFLY, Lua expresion
      //
      //    LUA, FILES, fileIn, fileOut, fileErrors
      //

   <aliases>
      alias
      LUA
      GASTLUA

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Makes a call to a java public static method having String [] as parameter (e.g. main method)

   <syntaxParams>
      synIndx, name          , defVal, desc
         1   , ONFLY         ,       ,
         1   , Lua expresion,       , //Lua expresion

   <options>
      synIndx, optionName  , parameters     , defVal    , desc

   <examples>
      gastSample

      calling Lua1
      calling Lua2

   <calling Lua1>
      //#javaj#
      //
      //   <frames> oConsola
      //
      //#listix#
      //
      //   <main>
      //      LUA, ONFLY, //print "Hola Luarca!"

   <calling Lua2>
      //#javaj#
      //
      //   <frames> main, Luarca
      //
      //   <layout of main>
      //      EVA, 10, 10, 4, 4
      //
      //         , X
      //       X , xLuarca
      //         , bRun
      //       X , oConso
      //
      //   <sysDefaultFonts>
      //      Consolas, 13, 0, TextArea
      //
      //#data#
      //
      //   <xLuarca>
      //      //function factorial(n)
      //      //   if n == 0 then
      //      //      return 1
      //      //   else
      //      //      return n * factorial(n - 1)
      //      //   end
      //      //end
      //      //
      //      // print "Calculation of 5!"
      //      // print (factorial(5))
      //      //
      //
      //#listix#
      //
      //   <-- bRun>
      //      MSG, oConso clear
      //      LUA,, @<xLuarca>

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import listix.cmds.callCaptureInpOut;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.streams.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

/**
   Listix command LUA

*/
public class cmdLua implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "LUA",
          "LUARCA",
          "GASTLUA",
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
      String opt         = cmd.getArg(0);
      String firstInline = cmd.getArg(1);
      if (opt.equals (""))
         opt = "onfly";

      if (opt.equalsIgnoreCase ("onfly"))
      {
         that.log().dbg (4, "LUA", "inline script");
         String luaProcess = utilSys.isOSWindows () ? microToolInstaller.getExeToolPath("lua"): "lua";

         callCaptureInpOut.callCapture ("LUA", luaProcess, firstInline, that, commandEva, indxComm);
      }

      // callCapture already process the options but it is not updated in this variable "cmd"!
      // so we cannot check the remaining options now
      // we should pass cmd to callCapture, then remaining options will be ok at this point
      //
      //cmd.checkRemainingOptions (true);
      return 1;
   }
}
