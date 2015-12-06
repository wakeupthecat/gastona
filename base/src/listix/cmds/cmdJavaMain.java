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
   <name>       JAVA STATIC
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdJavaMain
   <importance> 3
   <desc>       //To call a java method

   <help>
      //
      //  Calls a java static method within the same java virtual machine where Listix is running.
      //  Through this command it is possible to customize a lot of functionality, from fast calculations
      //  to long processes, the only limitation is the input parameters (void or String[]).
      //
      //  The method to call has to have one of the following signatures
      //
      //   1  public static String methodname ()
      //   2  public static String methodname (String [])
      //   3  public static String [] methodname ()
      //   4  public static String [] methodname (String [])
      //   5  public static anotherType methodname ()
      //   6  public static anotherType methodname (String [])
      //
      //  Use the option "ARG TYPE, void" if the method has no arguments (1, 3, 5)
      //
      //  If the method return's type is different from String or String [] (e.g. 5, 6) the return value will be ignored
      //  without producing any error.
      //
      //  In the cases 1 to 4 the returned String(s) will be sent to the current listix
      //  target (e.g. console, variable solving etc). Note though the difference between returning a String 
      //  or String[] and just printing out values using System.out within the method, for example
      //
      //       ...
      //       SETVAR, myVar, @<call method>
      //
      //    <call method> JAVA, class, method
      //
      //  The result will only be set into myVar if the method returns it, but nothing that the method
      //  simply prints out (e.g. using System.out.print) will be contained in myVar.
      //
      //  Like in the command CALL, this command waits for the termination of the execution before
      //  continue.
      //
      //  If the method to call needs extra libraries or classes these should be loaded previously
      //  using either the Javaj variable <external_libraries> or the command "EXTERNAL, PATH".
      //
      // (*) Strings can be also texts, for return value as much as for input arguments, for example
      //
      //    JAVA, myclass, myMethod, @<some Text>, arg2, etc
      //

   <aliases>
      alias
      JAVA MAIN
      JAVA

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Makes a call to a java public static method having String [] as parameter (e.g. main method)

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , javaClass    ,       , //Name of the class that holds the static method to call
         1   , method       , main  , //Name of the static method of 'javaClass' to call
         1   , parameter    ,       , //fist element of the parameter String [] of the method
         1   , ...          ,       , //further elements of the parameter String []

   <options>
      synIndx, optionName  , parameters     , defVal    , desc
         1   , ARG TYPE    , void / String[], "String[]", //Set it to "void" if the method to call has no arguments

   <examples>
      gastSample

      calling java methods
      testing math expresion
      Asiste expression sample1
      Asiste expression sample2

   <calling java methods>
      //#javaj#
      //
      //   <frames> F, "calling a java method"
      //
      //   <layout of F>
      //
      //      EVA, 10, 10, 5, 5
      //      ---,    X
      //         , bTestCall
      //       X , oConsola
      //
      //#listix#
      //
      //   <-- bTestCall>
      //       //Calling listix.util.lsx2Html, main ...
      //       JAVA STATIC, listix.util.lsx2Html, main
      //       //
      //       //Calling listix.lsxWriter main ...
      //       JAVA STATIC, listix.lsxWriter
      //       //
      //       //Calling the method de.elxala.langutil.filedir.naming::toVariableName
      //       // to convert "This is%20%NOT(a valid \\//á) file name, is it?"
      //       // into a valid variable name (e.g. for a column in a table etc)
      //       //
      //       JAVA, de.elxala.langutil.filedir.naming, toVariableName, //This is%20%NOT a valid (áé) file name, is it?
      //       //
      //       //Note : the last result can be obtained using the command STRCONV, TEXT-VARNAME
      //       //

   <testing math expresion>
      //#javaj#
      //
      //   <frames> F, "testing math expresion algebraic <-> RPN", 600, 400
      //
      //   <layout of F>
      //
      //      EVA, 10, 10, 5, 5
      //      ---,    , X
      //         , lExp. Algebraica, eAljbr
      //         , lExp. Polaca    , ePolaca
      //       X , oConsola, -
      //#data#
      //
      //	<eAljbr>  //sin (x) / x
      //	<ePolaca> //x 3 - 6 / cos
      //
      //#listix#
      //
      //   <-- eAljbr>
      //	    JAVA STATIC, de.elxala.math.polac.aljbr2polaca, main, @<eAljbr>
      //
      //   <-- ePolaca>
      //	    JAVA STATIC, de.elxala.math.polac.polaca2aljbr, main, @<ePolaca>


   <Asiste expression sample1>
      //#javaj#
      //
      //   <frames> F, "Asiste string utility", 600, 400
      //
      //   <layout of F>
      //
      //      EVA, 10, 10, 5, 5
      //      ---,    , X
      //         , lAsiste expression, eAsisteStr, bGo
      //       X , xSQLFilter, -, -
      //
      //   <sysDefaultFonts>
      //      Consolas, 13, 0, TextArea
      //
      //#data#
      //
      //   <eAsisteStr> +generation +GUI -XML
      //
      //   <xSQLFilter wrapLines> 1
      //
      //#listix#
      //
      //   <-- bGo>
      //      -->, xSQLFilter data!,, @<solve>
      //
      //   <-- eAsisteStr>
      //      -->, xSQLFilter data!,, @<solve>
      //
      //   <solve>
      //      JAVA STATIC, javaj.widgets.table.util.utilAsiste, getComposedWhereCondition, @<eAsisteStr>, desc
      //

   <Asiste expression sample2>
      //#javaj#
      //
      //   <frames> F, "Asiste string utility for more fields", 600, 400
      //
      //   <layout of F>
      //
      //      EVA, 10, 10, 5, 5
      //      ---,    , X
      //         , lAsiste expression, eAsisteStr, allany
      //       X , xSQLFilter, -, -
      //
      //   <layout of allany>
      //      RADIO, X
      //
      //      rAny, rAll
      //
      //   <sysDefaultFonts>
      //      Consolas, 13, 0, TextArea
      //
      //#data#
      //
      //   <rAny selected> 1
      //   <eAsisteStr> +generation +GUI -XML
      //
      //   <xSQLFilter wrapLines> 1
      //
      //#listix#
      //
      //   <-- allany>
      //      -->, xSQLFilter data!,, @<solve>
      //
      //   <-- eAsisteStr>
      //      -->, xSQLFilter data!,, @<solve>
      //
      //   <solve>
      //      JAVA STATIC, javaj.widgets.table.util.utilAsiste, @<method>, @<eAsisteStr>, desc, help
      //
      //
      //   <method>
      //      CHECK, VAR, rAny selected, //getComposedWhereCondition
      //      CHECK, ==, @<rAny selected>, 1, //getComposedWhereConditionAll
      //      getComposedWhereConditionAny
      //

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

/**
   Listix command JAVA STATIC

*/
public class cmdJavaMain implements commandable
{
   private de.elxala.math.polac.polaca2aljbr mentionClass = null; // just to get compiled

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "JAVASTATIC",
          "JAVAMAIN",
          "JAVA",
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
      String className = cmd.getArg(0);
      String mainMeth  = cmd.getArg(1);
      if (mainMeth.equals (""))
         mainMeth = "main";

      // retrieve method parameters.
      //    Expected either
      //       1..N parameters then (String[N])
      //       0    parameters then (String[0])
      String [] aa = new String [0];
      if (cmd.getArgSize () > 2)
      {
         aa = new String [cmd.getArgSize () - 2];
         for (int ii = 2; ii < cmd.getArgSize (); ii ++)
         {
            aa [ii - 2] = cmd.getArg(ii);
         }
      }

      // if option is "void" then set arguments to null
      String argumentType = cmd.takeOptionString("ARGTYPE", "String[]");
      that.log().dbg(2, "JAVAMAIN", "className [" + className + "], public static method [" + mainMeth + " (" + argumentType + ")]");

      //call the method
      //
      Object [] retVal = new Object[1];
      javaLoad.callStaticMethodArgs (className, mainMeth, argumentType.equalsIgnoreCase("void") ? null: aa, retVal);
      if (retVal[0] != null)
      {
         if (retVal[0] instanceof String)
         {
            that.log().dbg (2, "JAVAMAIN", "return string [" + retVal[0] + "]");
            that.printTextLsx ((String) (retVal[0]));
         }
         else if (retVal[0] instanceof String[])
         {
            String [] arr = (String []) retVal[0];
            that.log().dbg (2, "JAVAMAIN", "return string [] of size " + arr.length);
            for (int ii = 0; ii < arr.length; ii ++)
            {
               that.printTextLsx (arr[ii]);
               that.newLineOnTarget ();
            }
         }
         else that.log().dbg (2, "JAVAMAIN", "return value of the called method [" + mainMeth + "] is not of type String, no return value");
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}

