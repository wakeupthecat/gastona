/*
library listix (www.listix.org)
Copyright (C) 2016 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command CGATE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       GORHINO
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdGoRhino
   <importance> 3
   <desc>       //Rhino caller

   <help>
      //
      // Calls Rhino with a javascript code
      //
      //    gorhino, //var ii = 10; ii+2;
      //

   <aliases>
      alias
      RHINO
      RINO
      GORRINO
      JS
      JAVASCRIPT

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    7      , //Execute the javascript code

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , RHINO        , //
         1   , [ script ]   , //Script to be executed

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      gorhino1

   <gorhino1>
      //#javaj#
      //
      //   <frames> oFmain, Sample calling Rhino to execute javascript code
      //
      //#listix#
      //
      //    <main>
      //       gorhino, //var ii = 10; ii+2;


#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

import org.mozilla.javascript.*;
import de.elxala.zServices.*;

public class cmdGoRhino implements commandable
{
   //NOTE!: For the correct build for jar and apk we need to force the compilation of these classes
   //      it is not enough to compile 
   //         org.mozilla.javascriptContext
   //         org.mozilla.javascript.Scriptable
   //         and all classes used by them
   private static org.mozilla.javascript.jdk13.VMBridge_jdk13 o1 = null;
   private static org.mozilla.javascript.jdk15.VMBridge_jdk15 o2 = null;

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
         "GORHINO",
         "RHINO",
         "RINO",
         "GORRINO",
         "JS",
         "JAVASCRIPT",
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      // ::execute - some helpful comment for all the possible syntaxes
      //
      //      comm____   , par1_____
      //      GORHINO    , sourcejs

      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String strScript = cmd.getArg(0);
      if (cmd.getArgSize () != 1)
      {
         cmd.getLog().warn ("GORHINO", "GoRhino expect only one parameter!");
         return 1;
      }

      cmd.getLog().dbg (2, "GORHINO", "calling goRhino");
      String result = callSingle (strScript, cmd.getLog());
      cmd.getLog().dbg (2, "GORHINO", "return from calling goRhino, result length = " + result.length ());

      // give the result
      //
      cmd.getListix ().printTextLsx (result);

      cmd.checkRemainingOptions ();
      return 1;
   }

   public static String callSingle (String jsSource, logger log)
   {
      Context cx = Context.enter ();
      String sal = "";
      try {
         Scriptable scope = cx.initStandardObjects ();
         Object result = cx.evaluateString(scope, jsSource, "<cmd>", 1, null);
         sal = Context.toString(result);
      }
      catch (Exception e)
      {
         if (log != null)
            log.err ("GoRhino", "exception running script " + e + " script starting with : " + jsSource.substring (0, 200) + " *** source truncate.");
      }
      finally { Context.exit(); }
      return sal;
   }
}
