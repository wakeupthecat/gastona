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
   //(o) WelcomeGastona_source_listix_command IN CASE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       IN CASE
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdInCase
   <importance> 7
   <desc>       //For conditional execution of listix commands

   <help>
      //
      // For conditional execution of commands (short alias IF). Compares the given 'mainValue'
      // with all 'caseValues' and execute the given 'subCommand' when the comparison matches.
      // Note that this command uses "string comparison" which may be not suitable for numberic
      // values, for numeric comparison use the command "IN CASE NUM".
      // If 'subCommand' contains just one column it behaves as an inline format of just one line.
      //
      // Examples:
      //
      //       IF, @<myVar>, =, "TRUE",  //Verified myVar is exactly "TRUE"
      //       IF, @<myVar>, <>, "TRUE", //Verified myVar is not exactly "TRUE"
      //
      //       IN CASE, @<myColor>,
      //              , red     , //strong
      //              , blue    , //clever
      //              , lila    , //beautiful
      //              , ELSE    , //given another color
      //

   <aliases>
      alias
      IF
      IN CASE
      SWITCH2


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Switch execution of listix commands depending on the values of a variable or variables

   <syntaxParams>
      synIndx, name         , defVal         , desc
         1   , mainValue    ,                , //Main value to compare
         1   , = / < / > / <> / <= / >= , =  , //Operation to acomplish by the mainValue respect to the secondary values
         1   , [caseValue, subcommand],      , //A first pair caseValue-subcommand might be given as argument

   <options>
      synIndx, optionName  , parameters, defVal, desc
      1      , caseValue   , subCommand,       , //If 'mainValue' matches with 'caseValue' then the 'subCommand' given in the further parameters is executed
      1      , ...         ,           ,       , //More caseValue are accepted until ELSE option or end of command options
      1      , ELSE        , subCommand,       , //If given it has to be the last option. Will execute 'subCommand' if no other caseValue has matched.

   <examples>
      gastSample
      switch of one variable
      evaluating arguments
      detecting options


   <switch of one variable>
      //#javaj#
      //
      //   <frames> F, "listix command example"
      //
      //   <layout of F>
      //     EVA, 10, 10, 5, 5
      //
      //     ---,            ,  40   , 200
      //        , lMy Boolean, eMyVar, eMyVarText
      //        , lEnter a value and press enter key, -, -
      //
      //#listix#
      //
      //    <-- eMyVar>
      //         IN CASE, @<eMyVar>
      //                , 0   ,  SET VAR, eMyVarText, //FALSE, LIE, NOT
      //                , 1   ,  SET VAR, eMyVarText, //TRUE, REAL, YES
      //                , ELSE,  SET VAR, eMyVarText, //UNKNOWN, MAYBE, JAIN
      //         MSG, eMyVarText data!

   <evaluating arguments>
      //#javaj#
      //
      //   <frames> oConsole, "arguments evaluation sample"
      //
      //#listix#
      //
      //  <main0>
      //     LISTIX, my procedure, my first argument, yet another, third one, etc
      //
      //  <my procedure>
      //    IN CASE, @<:lsx paramCount>
      //           , 0   , //No arguments passed!
      //           , 1   , //Just one argument
      //           , 2   , //Only two arguments
      //           , 3   , //There are three arguments
      //           , ELSE, //There are lots of arguments
      //    //
      //    //passed (@<:lsx paramCount>) arguments :
      //    //
      //    IN CASE, @<:lsx paramCount>, ">"
      //           , 0, // "@<p1>"
      //           , 1, // ", @<p2>"
      //           , 2, // ", @<p3>"
      //           , ELSE, " and more "
      //           , ELSE, =, @<:lsx paramCount> - 2
      //           , ELSE, " arguments "

   <detecting options>
      //#javaj#
      //
      //   <frames> F, "listix command example", 500, 400
      //
      //   <layout of F>
      //      EVA, 10, 10, 5, 5
      //
      //      ---, X
      //         , kOption1
      //         , kOption2
      //      X  , oConsole
      //
      //#listix#
      //
      //
      //  <-- kOption1> @<describe>
      //  <-- kOption2> @<describe>
      //
      //  <describe>
      //       //
      //       //Current selection
      //       //
      //       IN CASE , 1
      //               , @<kOption1 selected>, // Option 1 is checked
      //               , @<kOption2 selected>, // Option 2 is checked
      //               , ELSE                , // No option is ckecked

#**FIN_EVA#
*/
package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

public class cmdInCase extends inCaseCommon implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "IN CASE",
            "CASE",
            "IF",
            "SWITCH2",
            "SWITCH",
         };
   }

   public String commandStr ()
   {
      return "IN CASE";
   }

   public String processMainValue (comparator compa, listixCmdStruct cmd, String oper, String mainValue)
   {
      compa.filter = new tableSimpleFilter (oper, mainValue);
      return mainValue;
   }

   public String doCase (comparator compa, listixCmdStruct cmd, String oper, String par2)
   {
      String value2 = par2;
      if (compa.filter.passOperand2 (value2))
         return "" + value2;
      return null;
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
      return executeInCase (new comparator (), that, commandEva, indxComm);
   }
}
