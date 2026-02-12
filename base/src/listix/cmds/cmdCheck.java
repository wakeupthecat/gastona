 /*
library listix (www.listix.org)
Copyright (C) 2005-2026 Alejandro Xalabarder Aulet

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
      // Checks some condition (e.g. existence of a file) and optionally runs some sub-commands
      // in positive or negative case of the check result.
      //
      // The command shows a special behaviour depending on the presence of the option BODY (or ""
      // since BODY is the default option). Basically if the BODY is not present then it returns
      // from the current format if the check result is negative but on the contrary if the BODY is
      // present this does not happen and it continues with the format regardless of the check result.
      //
      // For example:
      //
      //       CHECK, whatever, ...
      //       //continue only if check positive
      //       ...
      //
      //       CHECK, whatever, ...
      //            , BODY, subcomand
      //       //continue always, with check positive or negative !
      //       ...
      //
      //  This last behaviour can be forced as well by using the option CONTINUE with 1
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
         5   ,    3      , //Checks that a file exists and has a maximum size
         6   ,    3      , //Checks the existence of a directory
         7   ,    3      , //Checks a single string comparison between to values
         8   ,    3      , //Checks if the current operative system "seems" to be LINUX (indeed only checks if the file separator is '/')
         9   ,    3      , //Checks if the given 'EvaUnitName' can be loaded from the file 'fileName'
        10   ,    3      , //Checks a numeric expression or formula
        11   ,    3      , //Checks if the current operative system "seems" to be ANDROID
        12   ,    3      , //Checks if first parameter is contained in the set given by the rest of parameters. If finished with -CASE the comparison will be case-sensitive
        13   ,    3      , //Checks if first parameter is NOT contained in the set given by the rest of parameters. If finished with -CASE the comparison will be case-sensitive

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

         5   , FILE-MAXSIZE,            , //
         5   , path    ,                , //Path of the file to be checked
         5   , maxSize ,                , //Maximum size for the file to pass the check. It given as a number or a formula
         5   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed

         6   , DIR     ,                , //
         6   , path    ,                , //Path of the directory be checked
         6   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         7   , =  <  >  <>  <=  >=,     , //String comparison for the following values (valueA operation valueB)
         7   , valueA  ,                , //First value for the comparison
         7   , valueB  ,                , //Second value for the comparison
         7   , elseSubCommand,          , //If the check does not success, the given subcommand will be executed
         8   , LINUX   ,                ,
         8   , elseSubCommand,          ,//If the operative system seems to be Windows, the given subcommand will be executed
         9   , LOAD UNIT,               ,
         9   , fileName ,               ,//File where the unit 'EvaUnitName' is to be found
         9   , EvaUnitName,             ,//Eva unit name to be checked

        10   , NUMEXPR ,               ,
        10   , numericExpression,      , //Numeric expression or formula, if the result is different from 0 the check succeds. Note that comparison opetators =, <, <= etc can be used in the numeric expression as well, but be careful using operator = in floating formulas, since for example 1.0 != 0.9999999999..

        11   , ANDROID ,                ,
        11   , elseSubCommand,          ,//If the target system seems to be not android (e.g. gastona.jar), the given subcommand will be executed

        12   , WITHIN(-CASE),           ,
        12   , valueA       ,           , //Value to compare
        12   , value1       ,           , //First value of the set
        12   , ...          ,           , //next values of the set

        13   , NOT WITHIN(-CASE),           ,
        13   , valueA       ,           , //Value to compare
        13   , value1       ,           , //First value of the set
        13   , ...          ,           , //next values of the set

   <options>
      synIndx, optionName  , parameters , defVal, desc
          x  , BODY        , sub-command,    0  , If given and check is positive the sub-command will be executed. If given and check is negative it will cause the continuity with the current format!
          x  , ELSE        , sub-command,    0  , Additionally to the else-sub-command in arguments other sub-command may be given using this option. It may ocuppy more than one line but option ELSE is mandatory in all lines.
          x  , CONINUE     , 0 / 1      ,    0  , Even if the check does not success it is possible to force continuing with the format if this option is set to 1

   <examples>
      gastSample

      test check
      check-within

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

   <check-within>
      //#listix#
      //
      //   <main>
      //       LSX, check-pic, mpeg
      //       LSX, check-pic, TIFF
      //
      //   <check-pic>
      //      CHECK, WITHIN, @<p1>, png, jpeg, tiff
      //           , BODY, //@<p1> is a picture
      //           , ELSE, //@<p1> might not be a picture
      //      // (end of check)
      //      //

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
            "ASSERT",
            "REQUIRE",
            "RETURN IF NOT",
         };
   }

   protected boolean someEqual (String chkType, listixCmdStruct cmd, int nargRead)
   {
      boolean caseSensitive = chkType.indexOf ("-CASE") != -1;

      tableSimpleFilter comparator = new tableSimpleFilter ("=", cmd.getArg(nargRead ++), -1, caseSensitive);
      while (nargRead < cmd.getArgSize ())
      {
         if (comparator.passOperand2 (cmd.getArg (nargRead ++)))
            return true;
      }
      return false;
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
      boolean elseAllowedInParameters = true;

      String chkType = cmd.getArg(nargRead ++).toUpperCase ();
      //String second  = cmd.getArg(1);

      that.log().dbg (2, "CHECK", chkType);

      int jumpToEnd = commands.rows () - indxComm;
      boolean checked = false;

      // 1) evaluate properly the check and put the result in "checked"
      //
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
      else if (chkType.equals("FILE-MAXSIZE"))
      {
         //e.g.   CHECK, FILE-SIZE, path, maxsize-formul, [adondesino]
         //
         String fileName = cmd.getArg(nargRead ++);
         String formulaMax = cmd.getArg(nargRead ++);

         File target = fileUtil.getNewFile (fileName);
         checked = target.exists () && target.isFile () && target.length () <= calcFormulas.calcFormula (that, formulaMax);
         //@TOCHECK if file has size 0 and MAXSIZE is 0 then check should PASS !! but it does not, why ?!
         //
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

         // by now a little bit expensive ... we load the whole file!
         //
         EvaFile efi = new EvaFile (fileName);
         checked = efi.getUnit (evaUnitName) != null;
      }
      else if (listixCmdStruct.meantConstantString (chkType, new String [] {"NUMEXPR", "NUM", "EXPR", "FORMULA" }))
      {
         //e.g.   CHECK, EXPRE, "a * 10 > log (20)"
         //
         String formula = cmd.getArg(nargRead ++);

         checked = (calcFormulas.calcFormula (that, formula) != 0.f);
      }
      else if (listixCmdStruct.meantConstantString (chkType, new String [] { "IN", "INSET", "CONTAINED", "WITHIN", "IN-CASE", "INSET-CASE", "CONTAINED-CASE", "WITHIN-CASE" }))
      {
         //e.g.   CHECK, IN SET, @<myVar>, png, jpeg, tiff
         //
         checked = someEqual (chkType, cmd, nargRead);
         elseAllowedInParameters = false;
      }
      else if (listixCmdStruct.meantConstantString (chkType, new String [] {"NOTIN", "OUTOFSET", "NOTCONTAINED", "NOTWITHIN", "NOTIN-CASE", "OUTOFSET-CASE", "NOTCONTAINED-CASE", "NOTWITHIN-CASE" }))
      {
         //e.g.   CHECK, NOT IN SET, @<myVar>, png, jpeg, tiff
         //
         checked = ! someEqual (chkType, cmd, nargRead);
         elseAllowedInParameters = false;
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

      // 2) if checked is positive execute the body if any and return
      if (checked)
      {
         // check pass
         that.log().dbg (2, "CHECK", "(pass) " + ((EvaLine) commands.get(indxComm)));

         Eva positiveBody = cmd.takeOptionAsEva (new String [] { "", "BODY" });
         if (positiveBody != null)
         {
            that.log().dbg (2, "CHECK", "execute body of " + positiveBody.rows () + " rows starting with [" + ((EvaLine) positiveBody.get(0)) + "]");
            that.doFormat (positiveBody);
         }

         //just consume not used options for the proper checkRemaining...
         cmd.checkRemainingOptions (true, new String [] { "CONTINUE", "ELSE" });
         return 1;
      }

      //
      // checked is FALSE ! check did not pass
      //
      that.log().dbg (2, "CHECK", "(do not pass) " + ((EvaLine) commands.get(indxComm)));

      // 3) checked is negative, first try to execute the implicit ELSE subcommand from the not consumed arguments
      //
      //       for example
      //       CHECK, type, arg1, arg2, implicit else subcommand
      //       CHECK, =, @<extension>, "png", BOX, I, //this is not a png!
      //
      if (elseAllowedInParameters && nargRead < cmd.getArgSize ())
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

      // 4) Now collect and execute ELSE body if exists
      //
      Eva negativeBody = cmd.takeOptionAsEva (new String [] { "ELSE" });
      if (negativeBody != null)
      {
         that.log().dbg (2, "CHECK", "execute else-sub-command of " + negativeBody.rows () + " rows starting with [" + ((EvaLine) negativeBody.get(0)) + "]");
         that.doFormat (negativeBody);
      }

      // 5) find out if we have to leave the current format or continue
      //
      //    we continue if
      //       - there is the option CONTINUE, 1
      //       - there is some positive body !!! (new)
      //         if the check has a positive body it is safe to continue
      //
      //       CHECK, =, @<extension>, "png"
      //             , BODY, //the files is a png
      //             , ELSE, //the files is NOT a png!
      //       //continue anyway
      //
      //       CHECK, =, @<extension>, "png"
      //             , ELSE, //the files is NOT a png!
      //       //continue ONLY if @<extension> is "png" !!!!
      //

      // continue anyway ?
      boolean weContinue = cmd.takeOptionString (new String [] { "", "BODY" }, null, false) != null;
      if (weContinue)
         that.log().dbg (2, "CHECK", "continue because CHECK positive body is present");

      if (!weContinue)
      {
         weContinue = "1".equals (cmd.takeOptionString("CONTINUE", "0"));
         if (weContinue)
            that.log().dbg (2, "CHECK", "continue because CHECK option CONTINUE is 1");
      }
      else

      // jumpToEnd is an end format (like a return)
      cmd.checkRemainingOptions (true, new String [] { "CONTINUE", "", "BODY" });
      return (weContinue) ? 1: jumpToEnd;
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