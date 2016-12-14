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
         1   ,    4      , //Execute the javascript code
         2   ,    4      , //Execute the javascript code

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , [ script ]   , //Script to be executed

         2   , ONFLY        , //
         2   , script       , //Script to be executed

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      gorhino1
      javascriptExecutorGoRhino
      consecuencioDiagram
      files for goRhino

   <gorhino1>
      //#javaj#
      //
      //   <frames> oFmain, Sample calling Rhino to execute javascript code
      //
      //#listix#
      //
      //    <main>
      //       gorhino, //var ii = 10; ii+2;


   <javascriptExecutorGoRhino>
      //#javaj#
      //
      //   <frames> main, Javascript Executor using goRhino
      //
      //   <layout of main>
      //      EVA, 3, 3, 2, 2
      //
      //         , X
      //         , lEnter your javascript code
      //       X , xCodeArea
      //         , bEjecute
      //         , lSalida
      //       X , oSal
      //
      //   <sysDefaultFonts>
      //      Consolas, 14, 0, TextArea.font
      //
      //#data#
      //
      //   <OUTER_JS>
      //      //var __out__ = [];
      //      //function out (eso) { __out__.push (eso); }
      //      //
      //      //@<xCodeArea>
      //      //
      //      //__out__.join ("\n");
      //      //
      //
      //   <xCodeArea>
      //      //
      //      //function encript (t1, t2)
      //      //{
      //      //   return t2 + t1;
      //      //}
      //      //
      //      //out ("output of this function is ... " + encript ("TheCat", "WakeUp"));
      //      //
      //
      //#listix#
      //
      //   <-- bEjecute>
      //      MSG, oSal clear
      //      VAR=, startStamp, @<:lsx CLOCK>
      //      MSG, lSalida data!,, //Calling goRhino ...
      //      goRhino, @<OUTER_JS>
      //      MSG, lSalida data!,, //done in @<elapsed> s
      //
      //   <elapsed>
      //      =, (@<:lsx CLOCK> - startStamp) / 1000
      //

   <consecuencioDiagram>
      //#javaj#
      //
      //   <frames> Fmain, Sample conSecuencio.js using Rhino
      //
      //   <layout of Fmain>
      //    EVA, 10, 10, 7, 7
      //
      //       , X
      //       , lJavascript code
      //     X , xCodis
      //       , bgoRhino
      //     X , xSalida
      //   100 , oConsola
      //
      //    <sysDefaultFonts>
      //       Consolas, 11, 0, TextArea
      //
      //#data#
      //
      //   <xCodis>
      //      //var diagData = {
      //      //     sequenceTable : [
      //      //                ["time", "source", "target", "message" ],
      //      //                [ 0.122, "USER"  , "INTERFACE", "doThisAction" ],
      //      //                [ 2.234, "INTERFACE", "SERVER", "theAction" ],
      //      //                [ 3.543, "SERVER", "INTERFACE", "What action?" ],
      //      //                [ 8.558, "INTERFACE", "USER", "done!" ],
      //      //            ],
      //      //      // distanceAgents   : 40,
      //      //      distanceTimeUnit : 1,
      //      //      // maxGapTime       : 2,
      //      //      // autoElapsed      : false,
      //      //  };
      //      //
      //      //@<:infile META-GASTONA/js/conSecuencioPlain.js>
      //      //
      //      //conSecuencioPlain (diagData);
      //      //
      //
      //#listix#
      //
      //   <-- bgoRhino>
      //      -->, oConsola clear
      //      -->, xSalida data!,, @<rino>
      //
      //   <rino>
      //      goRhino, @<xCodis>
      //

   <files for goRhino>
      //#javaj#
      //
      //   <frames> oSal
      //
      //#listix#
      //
      //   <content>
      //      'First line
      //      'second ....
      //      'and last one.
      //
      //   <rewrite>
      //     'var fix = new gFile ();
      //     'var leos, nn = 0;
      //     'if (fix.fopen (":mem in", "r")) {
      //     '   var fo = new gFile ();
      //     '   if (fo.fopen (":mem out", "w")) {
      //     '       while ((leos = fix.readLine ()) !== null)
      //     '          fo.writeLine ("te comento: " + (++nn) + ") " + leos);
      //     '   }
      //     '   fo.fclose ();
      //     '   fix.fclose ();
      //     '}
      //     '"fin"
      //
      //   <main>
      //      GEN, :mem in, content
      //      '
      //      goRhino, @<rewrite>
      //      '
      //      '
      //      LOOP, TEXT FILE, :mem out
      //          ,, @<value>


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
   //NOTE!: It is not enough to compile
   //         org.mozilla.javascriptContext
   //         org.mozilla.javascript.Scriptable
   //         and all classes used by them
   //       which is done automatically by javac when compiling cmdGoRhino.java
   //       but we have to force the compilation of following classes (at least!) as well
   //       if we do not do it, Context.evaluateString returns always "undefined"
   //
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
         "GORINO",
         "JS",
         "JS=",
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

      int nn = cmd.getArgSize ();
      String opt  = cmd.getArg(0);
      String par1 = cmd.getArg(1);
      String strScript = null;

      // admit two syntaxes (onfly is default)
      //       gorhino, onfly, //script ...
      //       gorhino,      , //script ...
      //       gorhino, //script ...

      if (nn == 1)
      {
         strScript = opt;
         opt = "";
      }
      else if (nn == 2)
      {
         strScript = par1;
      }
      else
      {
         cmd.getLog().err ("GORHINO", "wrong number of parameters \"" + nn + "\"");
         return 1;
      }
      if (opt.length () > 0 && ! opt.equalsIgnoreCase ("onfly"))
      {
         cmd.getLog().err ("GORHINO", "unsupported option \"" + opt + "\"");
         return 1;
      }
      String result = "";

      if (strScript != null)
      {
         cmd.getLog().dbg (2, "GORHINO", "calling goRhino");
         result = callSingle (strScript, cmd.getLog());
         cmd.getLog().dbg (2, "GORHINO", "return from calling goRhino, result length = " + result.length ());
      }
      else cmd.getLog().dbg (2, "GORHINO", "no script given, nothing to do");

      // give the result
      //
      cmd.getListix ().printTextLsx (result);
      cmd.checkRemainingOptions ();
      return 1;
   }

   // this method is thought to be called directly using JAVA STATIC listix command
   // for instance:
   //
   //       JAVA STATIC, listix.cmds.cmdGoRhino, callSingle, //"amigo".match (/go/) ? "1": "0"
   //
   // this call is much faster (e.g. 0.32 ms) than calling listix command GORHINO (e.g. 1.11 ms)
   // however Android cannot use JAVA STATIC !
   //
   public static String callSingle (String [] jsSource)
   {
      // the parameter is a String array in order to be callable from JAVA STATIC but
      // actually it only make sense to send one element
      // anyway if there are more we will call all as separate scripts
      //
      StringBuffer sal = new StringBuffer ();

      for (int ii = 0; ii < jsSource.length; ii ++)
         sal.append (callSingle (jsSource[ii], org.gastona.commonGastona.log ));

      return sal.toString ();
   }

   protected static String introScript (String jsSource, logger log)
   {
      return (jsSource == null || log == null) ? null:
             jsSource.length () < 201 ?
             jsSource:
             jsSource.substring (0, 200) + " *** source truncate.";

   }

   public static String callSingle (String jsSource, logger log)
   {
      Context cx = Context.enter ();
      String sal = "";

      try
      {
         Scriptable scope = cx.initStandardObjects ();

         // access to TextFile through ScriptableObject gFile
         //
         ScriptableObject.defineClass(scope, de.elxala.langutil.filedir.gFile.class);

         Object result = cx.evaluateString(scope, jsSource, "<cmd>", 1, null);
         sal = Context.toString(result);
      }
      catch (org.mozilla.javascript.EcmaError e)
      {
         if (log != null)
          log.err ("GoRhino", "Ecma ERROR [" + e + "] in script : " + introScript (jsSource, log));
      }
      catch (Exception e)
      {
         if (log != null)
            log.err ("GoRhino", "exception running script [" + e + "] in script : " + introScript (jsSource, log));
      }
      finally { Context.exit(); }
      return sal;
   }
}


/*

   possible options to implement in future

         , onfly, script...
         , load , filename
         , compile, name            will compile the script with listix variables solved and store it with the name "name" for future recall
         , execute, name            execute the previous compiled script with name "name"
         , discard, name            will discard the compiled script or object with name "name"
         , outvar , varname         set the result as if were an eva variable (array of arrays) in the variable name
         , outintern, name          store the result in an js object with the name "name"


   poderle decir algo sobre el output

         ...
         , out, var, pepa

      - que lo meta en una eva

         ...
         , out, name, pepa

      - que lo guarde en una variable interna (?) como objeto js
        a modo de resultado intermedio



*/