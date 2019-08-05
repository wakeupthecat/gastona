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
   //(o) WelcomeGastona_source_listix_command GORHINO

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

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , [ script ]   , //Script to be executed

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      gorhino1
      consequenciDiagram
      files for goRhino
      sqlSelect for goRhino
      sqlRunner for goRhino

   <gorhino1>
      //#javaj#
      //
      //   <frames> oFmain, Sample calling Rhino to execute javascript code
      //
      //#listix#
      //
      //    <main>
      //       gorhino, //var ii = 10; ii+2;

   <consequenciDiagram>
      //#javaj#
      //
      //   <frames> Fmain, Sample conSequenci.js using Rhino
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
      //      //                [      , "A=USER", "", ""],
      //      //                [      , "B=INTERFACE", "", ""],
      //      //                [      , "C=SERVER", "", ""],
      //      //                [ 0.122, "A", "B", "doThisAction" ],
      //      //                [ 2.234, "B", "C", "theAction" ],
      //      //                [ 3.543, "C", "B", "What action?" ],
      //      //                [ 8.558, "B", "A", "done!" ],
      //      //            ],
      //      //      // distanceAgents   : 40,
      //      //      distanceTimeUnit : 1,
      //      //      // maxGapTime       : 2,
      //      //      // autoElapsed      : false,
      //      //  };
      //      //
      //      //@<:infile META-GASTONA/js/conSequenciPlain.js>
      //      //
      //      //conSequenciPlain (diagData);
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
      //      //First line
      //      //second ....
      //      //and last one.
      //
      //   <rewrite>
      //      //var fix = new goFile ();
      //      //var leos, nn = 0;
      //      //if (fix.fopen (":mem in", "r")) {
      //      //   var fo = new goFile ();
      //      //   if (fo.fopen (":mem out", "w")) {
      //      //       while ((leos = fix.readLine ()) !== null)
      //      //          fo.writeLine ("te comento: " + (++nn) + ") " + leos);
      //      //   }
      //      //   fo.fclose ();
      //      //   fix.fclose ();
      //      //}
      //      //"fin"
      //
      //   <main>
      //      GEN, :mem in, content
      //      //
      //      goRhino, @<rewrite>
      //      //
      //      //
      //      LOOP, TEXT FILE, :mem out
      //          ,, @<value>

   <sqlSelect for goRhino>
      //#javaj#
      //
      //   <frames> oSal
      //
      //#listix#
      //
      //   <mitabla>
      //      id, name
      //      1282, emilia
      //      3243, samba pati
      //
      //   <JS_readTable>
      //     //var fix = new goSqlSelect ("", "SELECT * FROM mitabla");
      //     //var out = [];
      //     //
      //     //for (var ii = 0; ii < fix.getRecordCount (); ii ++) {
      //     //    out.push ("record: " + fix.getValue (ii, 0) + ") " + fix.getValue (ii, 1));
      //     //}
      //     //out.join ("\n");
      //
      //   <main>
      //		  DB,, CREATE TABLE,  mitabla
      //      //
      //      goRhino, @<JS_readTable>


   <sqlRunner for goRhino>
      //#javaj#
      //
      //   <frames> oSal
      //
      //#listix#
      //
      //   <jsTail>
      //     //var fix = new goSqlRunner ();
      //     //fix.runSQL ("", "CREATE TABLE textos (id, text); INSERT INTO textos VALUES (18, 'mi texto');");
      //     //fix.openScript ();
      //     //fix.writeScript ("INSERT INTO textos VALUES (22, 'my text');");
      //     //fix.writeScript ("INSERT INTO textos VALUES (98, 'the last entry');");
      //     //fix.closeAndRunScript ("");
      //     //"fin"
      //
      //   <main>
      //      goRhino, @<jsTail>
      //      //
      //      //
      //      LOOP, SQL,, //SELECT * FROM textos
      //          ,, // @<id>: @<text>

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

      String script = cmd.getArg(0);

      // be compatible with old "ON FLY"
      if (cmd.getArgSize () > 1)
      {
         if (script.equals ("") || script.equalsIgnoreCase ("onfly") || script.equalsIgnoreCase ("on fly"))
            script = cmd.getArg(1);
         else {
            cmd.getLog().err ("GORHINO", "unsupported option \"" + script + "\"");
            return 1;
         }
      }

      String result = "";

      if (script != null)
      {
         cmd.getLog().dbg (2, "GORHINO", "calling goRhino");
         result = callSingle (script, cmd.getLog(), that);
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
         sal.append (callSingle (jsSource[ii], org.gastona.commonGastona.log, null ));

      return sal.toString ();
   }

   protected static String introScript (String jsSource, logger log)
   {
      return (jsSource == null || log == null) ? null:
             jsSource.length () < 201 ?
             jsSource:
             jsSource.substring (0, 200) + " *** source truncate.";

   }

   public static String callSingle (String jsSource, logger log, listix me)
   {
      Context cx = Context.enter ();
      String sal = "";

      try
      {
         Scriptable scope = cx.initStandardObjects ();

         // access to TextFile and sqlSelect through ScriptableObject goFile, goSqlSelect and goSqlRunner
         //
         ScriptableObject.defineClass(scope, de.elxala.langutil.filedir.goFile.class);
         ScriptableObject.defineClass(scope, de.elxala.db.sqlite.goSqlSelect.class);
         ScriptableObject.defineClass(scope, de.elxala.db.sqlite.goSqlRunner.class);
         //ScriptableObject.defineClass(scope, java.lang.System.class);

         // the power of passing java objects directly!!
         // a js rhino script can call all java methods of them!
         // so for example to generate directly listix output can call
         //    listix.printTextLsx ("blabla");
         // without having to accumulate the whole string and return it at the end
         //
         //scope.put("System", scope, java.lang.System);
         scope.put("log", scope, me.log());
         scope.put("listix", scope, me);

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

         , file input , filename
         , file output, filename
         , file error , filename
         , body       , js code



----- ver ejemplo 3 y 4
   https://javaranch.com/journal/200607/Scripting.html

   con scope.put le pasamos CUALQUIER objecto java y en js podemos manejarlo, llamar a sus metodos etc!!


   === JAVA
         import java.io.*;
         import org.mozilla.javascript.*;

         public class Example3 {

             public static void main (String args[]) throws Exception {
                 try {
                  // create context
                  Context cx = Context.enter();

                  // Scriptable represents the script environment
                     Scriptable scope = cx.initStandardObjects(null);

                  // put a few Java objects into the JavaScript namespace
                  scope.put("javaNumber", scope, new Integer(42));
                  scope.put("javaString", scope, "a Java String");
                  scope.put("systemOut", scope, System.out);

                  // evaluate the script
                  cx.evaluateReader(scope, new FileReader("scripts"+File.separator+"example3.js"), "example3.js", -1, null);

                  // retrieve the value of 'result'
                  System.out.println("result = "+scope.get("result", scope));
               } finally {
                     // Exit from the context
                     Context.exit();
                 }
             }
         }

   === JS

      // 'javaNumber', 'javaString' and 'systemOut' are passed in from Java

      systemOut.println(javaString);

      function example3 (x) {
         return 3 * x + 1;
      }

      result = example3(javaNumber);


----- compilar function

			// a Function is something that can be evaluated with respect to some parameters
			Function func = cx.compileFunction(scope, functionStr, "example2.js", 0, null);
         Object result = func.call(cx, scope, null, new Object[] { new Integer(55), new Integer(7) } );
         double dbl = cx.toNumber(result);


   === JS

      function example2 (x, y) {
         return 3 * x + y;
      }


----- ver ejemplo
   https://github.com/ianwalter/rhino-stock-example

   Notar el uso de
      Context.javaToJS(stock, scope);
      ScriptableObject.putProperty(scope, "stock", wrappedStock);

          Context cx = Context.enter();
          try {
              // Initialize the standard objects (Object, Function, etc.). This must be done before scripts can be
              // executed. The null parameter tells initStandardObjects
              // to create and return a scope object that we use
              // in later calls.
              Scriptable scope = cx.initStandardObjects();

              // Pass the Stock Java object to the JavaScript context
              Object wrappedStock = Context.javaToJS(stock, scope);
              ScriptableObject.putProperty(scope, "stock", wrappedStock);

              // Execute the script
              cx.evaluateString(scope, evaluationScript, "EvaluationScript", 1, null);
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              // Exit the Context. This removes the association between the Context and the current thread and is an
              // essential cleanup action. There should be a call to exit for every call to enter.
              Context.exit();
          }

*/