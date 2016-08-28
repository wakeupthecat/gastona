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
   //(o) WelcomeGastona_source_listix_command CHECK

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       CHECK
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdCheck
   <importance> 3
   <desc>       //For ensure some condition

   <help>
      //
      // Checks some condition (e.g. existence of a file) and only if the condition is met continues
      // the execution of the the current format, otherwise optionally a command might be invoqued
      // (elseSubCommand).
      //

   <aliases>
      alias
      REQUIRED


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Checks the existence of an Eva variable
         2   ,    3      , //Checks the existence of a System Property
         3   ,    3      , //Checks the existence of a file in the given path
         4   ,    3      , //Checks the existence of a file for read access, first in the given path, if not found there in the java classpath
         5   ,    3      , //Checks the existence of a directory
         6   ,    3      , //Checks a single string comparation between to values
         7   ,    3      , //Checks if the current operative system "seems" to be linux (indeed only checks if the file separator is '/')
         8   ,    3      , //Checks if the given 'EvaUnitName' can be loaded from the file 'fileName'
         9   ,    3      , //Checks a numeric expression or formula

   <syntaxParams>
      synIndx, name    , defVal         , desc
         1   , VAR     ,                , //
         1   , evaName ,                , //Name of the variable to be checked.
         1   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         2   , PROP    ,                , //
         2   , propertyName ,           , //Name of system property to be checked.
         2   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         3   , FILE    ,                , //
         3   , path    ,                , //Path of the file to be checked
         3   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         4   , RFILE   ,                , //
         4   , path    ,                , //Path of the file be checked
         4   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         5   , DIR     ,                , //
         5   , path    ,                , //Path of the directory be checked
         5   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         6   , =  <  >  <>  <=  >=,     , //String comparation for the following values (valueA operation valueB)
         6   , valueA  ,                , first value for the comparation
         6   , valueB  ,                , second value for the comparation
         6   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         7   , LINUX   ,                ,
         7   , elseSubCommand,          ,//If the operative system seems to be Windows, the given subcommand will be executed
         8   , LOAD UNIT,               ,
         8   , fileName ,               ,//File where the unit 'EvaUnitName' is to be found
         8   , EvaUnitName,             ,//Eva unit name to be checked

         9   , NUMEXPR ,               ,
         9   , numericExpression,      , //Numeric expression or formula, if the result is different from 0 the check succeds. Note that comparation opetators =, <, <= etc can be used in the numeric expression as well, but be careful using operator = in floating formulas, since for example 1.0 != 0.9999999999..

        10   , ANDROID ,                ,
        10   , elseSubCommand,          ,//If the target system seems to be not android (e.g. gastona.jar), the given subcommand will be executed

   <options>
      synIndx, optionName  , parameters , defVal, desc
          x  , ELSE        , sub-command,    0  , Aditionally to the else-sub-command in arguments other sub-command may be given using this option. If can ocuppy more than one line but option ELSE is mandatory in all lines!.
          x  , CONINUE     , 0 / 1      ,    0  , Even if the check does not success it is possible to force continuing with the format if this option is set to 1

   <examples>
      gastSample

      test check

   <test check>

      //#javaj#
      //
      //   <frames> F, "example check"
      //
      //   <layout of F>
      //
      //      EVA, 10, 10, 5, 5
      //      ---,  X
      //       X , oConsola
      //
      //#listix#
      //
      //   <main0>
      //      @<chk1>
      //      //
      //      @<chk2>
      //      //
      //      @<chk3>
      //      //
      //      @<chk4>
      //      //
      //      @<chk5>
      //      //
      //
      //   <chk1>
      //      CHECK, VAR, main0, //Not Ok!, VAR, main is False ??
      //      //Ok, variable main checked!
      //
      //   <chk2>
      //      CHECK, VAR, main2, //Ok, variable main2 not checked!
      //      //Not Ok! CHECK, VAR, main2 is True ??
      //
      //   <chk3>
      //      CHECK, FILE, gastona/gastona.class
      //           , ELSE, //Ok, gastona/gastona.class not found as file.
      //      //Strange!, gastona/gastona.class is found as file (?)
      //
      //   <chk4>
      //      CHECK, RFILE, gastona/gastona.class
      //           , ELSE , //FAIL! gastona/gastona.class should be found as read file!
      //      //Ok, gastona/gastona.class checked as read file
      //
      //   <chk5>
      //      CHECK, LINUX, //Operative system seems to be Windows
      //      //Operative system seems to be Linux
      //
      //   <chk6>
      //      CHECK, ANDROID, //Running NOT on an Android device
      //      //Running on an Android device
      //

#**FIN_EVA#
*/
package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.javaLoad;
import java.io.File;
import de.elxala.langutil.utilSys;
import de.elxala.langutil.filedir.*;

public class cmdCheck implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "CHECK",
            "REQUIRE",
            "RETURN IF NOT",
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
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      //NOTE: BE CAREFUL getting arguments solved (default by getArg) the else-sub-command
      //      arguments has to be not solved!
      int nargRead = 0;

      String chkType = cmd.getArg(nargRead ++).toUpperCase ();
      //String second  = cmd.getArg(1);

      that.log().dbg (2, "CHECK", chkType);

      int jumpToEnd = commands.rows () - indxComm;
      boolean checked = false;

      if (chkType.equals("EVA") || chkType.equals ("VAR"))
      {
         //e.g.  CHECK, EVA, jajaja, [adondesino]
         //
         String evaName = cmd.getArg(nargRead ++);
         checked = true;
         if (that.getVarEva (evaName) == null)
         {
            // it is not directly an Eva variable but it still might be a loop variable

            if (that.getTableCursorStack ().findValueColumn (evaName) == null)
               checked = false;  // checked fails!
         }
      }
      else if (listixCmdStruct.meantConstantString (chkType, new String [] {"PROP", "PROPERTY"}))
      {
         //e.g.  CHECK, PROP, java.io.temp, [adondesino]
         //
         String propName = cmd.getArg(nargRead ++);
         checked = System.getProperty (propName, null) != null;
      }
      else if (chkType.equals("FILE"))
      {
         //e.g.   CHECK, FILE, path, [adondesino]
         //
         String fileName = cmd.getArg(nargRead ++);
         File target = fileUtil.getNewFile (fileName);
         checked = target.exists () && target.isFile ();
      }
      else if (chkType.equals("RFILE"))
      {
         //e.g.   CHECK, RFILE, path, [adondesino]
         //
         String fileName = cmd.getArg(nargRead ++);
         File target = fileUtil.getNewFile (fileName);
         checked = (target.exists () && target.isFile ()) || (javaLoad.existsResource (fileName));
      }
      else if (chkType.equals("DIR"))
      {
         //e.g.   CHECK, DIR  , path, [adondesino]
         //
         String dirName = cmd.getArg(nargRead ++);
         File target = fileUtil.getNewFile (dirName);
         checked = target.exists () && target.isDirectory ();
      }
      else if (chkType.equals("LINUX"))
      {
         //e.g.   CHECK, LINUX
         //
         checked = utilSys.isSysUnix;
      }
      else if (chkType.equals("ANDROID"))
      {
         //e.g.   CHECK, LINUX
         //
         checked = utilSys.isSysUnix;
      }
      else if (listixCmdStruct.meantConstantString (chkType, new String [] {"LOADUNIT"}))
      {
         //e.g.   CHECK, LOAD UNIT, fileName, unitName
         //
         String fileName = cmd.getArg(nargRead ++);
         String evaUnitName = cmd.getArg(nargRead ++);

         checked = EvaFile.existsEvaUnitInFile (fileName, evaUnitName);
      }
      else if (listixCmdStruct.meantConstantString (chkType, new String [] {"NUMEXPR", "NUM", "EXPR", "FORMULA" }))
      {
         //e.g.   CHECK, EXPRE, "a * 10 > log (20)"
         //
         String formula = cmd.getArg(nargRead ++);

         checked = (calcFormulas.calcFormula (that, formula) != 0.f);
      }
      else // it must be an operation
      {
         //e.g.   CHECK, =|<|>|<=|>=|!=, a, b, [adondesino]
         //
         String valueA  = cmd.getArg(nargRead ++);
         String valueB  = cmd.getArg(nargRead ++);
         tableSimpleFilter comparator = new tableSimpleFilter (chkType, valueA);
         if (!comparator.isValidOperation())
         {
            that.log().err ("CHECK", "Operation " + chkType + " not valid, check returns false (format not completed)!");
            return jumpToEnd; // or 1 = "check not performed" ?
         }
         checked = comparator.passOperand2 (valueB);
      }

      if (checked)
      {
         // check pass
         that.log().dbg (2, "CHECK", "(pass) " + ((EvaLine) commands.get(indxComm)));

         //just consume not used options for the proper checkRemaining...
         cmd.checkRemainingOptions (true, new String [] { "CONTINUE", "ELSE" });
         return 1;
      }

      //
      // checked is FALSE ! check did not pass
      //
      that.log().dbg (2, "CHECK", "(do not pass) " + ((EvaLine) commands.get(indxComm)));

      // execute elseSubCommand if given in the arguments
      //
      if (nargRead < cmd.getArgSize ())
      {
         // NOTE: the eva subcommand is independent of the current EvaUnit data!
         Eva subcommand = new Eva ("subcommand");

         // collect the command (or simply text)
         while (nargRead < cmd.getArgSize ())
            subcommand.addCol (cmd.getArg (nargRead ++, false));

         that.log().dbg (2, "CHECK", "execute else-sub-command [" + ((EvaLine) subcommand.get(0)));

         if (subcommand.cols (0) > 1)
              that.executeSingleCommand (subcommand);
         else that.printTextLsx (subcommand.getValue (0, 0));

      }

      //  Collect all sub-commands in option(s) ELSE
      //
      Eva elseSubCommand = new Eva ("!else-sub-command found in format [" + commands.getName () + "]");
      int rowElseSubCmd = 0;
      String [] arrElseCommand = null;
      while (null != (arrElseCommand = cmd.takeOptionParameters(new String [] { "ELSE" }, false)))
      {
         // collect the command (or simply text)
         for (int ii = 0; ii < arrElseCommand.length; ii ++)
         {
            elseSubCommand.setValue (arrElseCommand[ii], rowElseSubCmd, ii);
         }
         rowElseSubCmd ++;
      }

      if (rowElseSubCmd > 0)
      {
         that.log().dbg (2, "CHECK", "execute else-sub-command of " + elseSubCommand.rows () + " rows starting with [" + ((EvaLine) elseSubCommand.get(0)) + "]");

         that.doFormat (elseSubCommand);
      }

      // continue anyway ?
      boolean bForceContinue = "1".equals (cmd.takeOptionString("CONTINUE", "0"));
      that.log().dbg (2, "CHECK", "force continue = " + bForceContinue);

      // jumpToEnd is an end format (like a return)
      cmd.checkRemainingOptions ();
      return (bForceContinue) ? 1: jumpToEnd;
   }
}

/*
   Note :
   it is quite difficult to read in a clear way the code related
   with checkRemainingOptions in this command ("check" ... "check"),
   and it is in this command where I have had more problems with this checking.
   If the command gets more complex maybe it would be better just to remove
   the checking ( of remaining options, of course! )
*/