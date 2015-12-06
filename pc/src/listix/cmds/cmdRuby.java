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
   //(o) WelcomeGastona_source_listix_command JAVA STATIC

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       RUBY
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdRuby
   <importance> 3
   <desc>       //To call ruby with a script

   <help>
      //
      //  Calls ruby EXPERIMENTAL for Windows platform
      //
      //    RUBY, ONFLY, ruby expresion
      //
      //    RUBY, FILES, fileIn, fileOut, fileErrors
      //

   <aliases>
      alias
      RUBY
      GARUBY

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Makes a call to a java public static method having String [] as parameter (e.g. main method)

   <syntaxParams>
      synIndx, name          , defVal, desc
         1   , ONFLY         ,       ,
         1   , ruby expresion,       , //Ruby expresion

   <options>
      synIndx, optionName  , parameters     , defVal    , desc

   <examples>
      gastSample

      calling ruby1
      calling ruby2

   <calling ruby1>
      //#javaj#
      //
      //   <frames> oConsola
      //
      //#listix#
      //
      //   <main>
      //      RUBY, ONFLY, //puts "Hola Rubyales!"

   <calling ruby2>
      //#javaj#
      //
      //   <frames> main, Rubio
      //
      //   <layout of main>
      //      EVA, 10, 10, 4, 4
      //
      //         , X
      //       X , xRubio
      //         , bRun
      //       X , oConso
      //
      //   <sysDefaultFonts>
      //      Consolas, 13, 0, TextArea
      //
      //#data#
      //
      //   <xRubio>
      //      // class Jello
      //      //   def initialize(name = "Mundos")
      //      //     @name = name
      //      //   end
      //      //   def diHola
      //      //     puts "Hola, #{@name}!"
      //      //   end
      //      //   def diAdios
      //      //     puts "Adios, #{@name}, hasta pronto!"
      //      //   end
      //      // end
      //      //
      //      // g = Jello.new("Romuaseldo")
      //      // g.diHola
      //      // g.diAdios
      //      //
      //      // puts Jello.instance_methods
      //      //
      //
      //#listix#
      //
      //   <-- bRun>
      //      MSG, oConso clear
      //      RUBY,, @<xRubio>

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
   Listix command RUBY

*/
public class cmdRuby implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "RUBY",
          "RUBYALES",
          "GASTRUBY",
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
         that.log().dbg (4, "RUBY", "inline script");
         String rubyProcess = utilSys.isOSWindows () ? microToolInstaller.getExeToolPath("ruby"): "ruby";

         callCaptureInpOut.callCapture ("RUBY", rubyProcess, firstInline, that, commandEva, indxComm);
         // por que no chuta esto ???
         //listix.cmds.callCaptureInpOut.callCapture ("RUBY", rubyProcess, firstInline, that, commandEva, indxComm);
      }

      // callCapture already process the options but it is not updated in this variable "cmd"!
      // so we cannot check the remaining options now
      // we should pass cmd to callCapture, then remaining options will be ok at this point
      //
      //cmd.checkRemainingOptions (true);
      return 1;
   }
}
