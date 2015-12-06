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
      // with all 'caseValues' and execute the given 'subCommand' when the comparation matches.
      // Note that this command uses "string comparation" which may be not suitable for numberic
      // values, for numeric comparation use the command "IN CASE NUM".
      // If 'subCommand' contains just one column it behaves as an inline format of just one line.
      //
      // Examples:
      //
      //       IF, @<myVar>, =, "TRUE", SETVAR, myText, "Verified myVar is truly true"
      //       IF, @<myVar>, <>, "TRUE", //Verified myVar is truly not true"
      //
      //       IN CASE, @<myVar>,
      //              , 0       , //false
      //              , 1       , //true
      //              , TRUE    , //true
      //              , FALSE   , //false
      //              , ELSE    , BOX, W, "Why your myVar is not boolean?"
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
      //    IN CASE, @<:lsx paramCount>, ">"
      //           , 0, // "@<p1>"
      //           , 1, // , "@<p2>"
      //           , 2, // , "@<p3>"
      //           , 3, // , there are more arguments ...
      //
      //

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

public class cmdInCase implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "IN CASE",
            "IF",
            "SWITCH2"
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

      String mainValue = cmd.getArg (0);
      String oper      = cmd.getArg (1);

      if (oper.equals(""))
         oper = "=";

      tableSimpleFilter comparator = new tableSimpleFilter (oper, mainValue);

      that.log().dbg (2, "IN CASE", mainValue + " " + oper);

      boolean executeOtherwise = true;
      // NOTE: the eva subcommand is independent of the current EvaUnit data!
      Eva subcommand = new Eva ("subcommand");
      String value2 = "";

      if (cmd.getArgSize() > 2)
      {
         // we have a initial value-command pair
         //       0       1       2       3       4      5    6   << index for commandEva
         //               0       1       2       3      4    5   << index for cmd.getArg
         //    IN CASE, valueRef, oper, value, command, par, etc
         //
         value2 = cmd.getArg (2);
         if (comparator.passOperand2 (value2))
         {
            that.log().dbg (2, "case value [", value2 + "] (line 0) will be executed");
            executeOtherwise = false;

            // collect the command or simply the text
            for (int ii = 4; ii < commandEva.cols (indxComm); ii ++)
               subcommand.addCol (commandEva.getValue (indxComm, ii));

            if (subcommand.cols (0) > 1)
                 that.executeSingleCommand (subcommand);
            else that.printTextLsx (subcommand.getValue (0, 0));
         }
      }

      // look for the end of the switch
      int endSwitch = 0;
      String firstCol = "";
      while (indxComm + endSwitch + 1 < commandEva.rows ())
      {
         if (commandEva.cols (indxComm + endSwitch + 1) < 2)
            break;   // if no more than one column then end of IN CASE command

         firstCol = commandEva.getValue (indxComm + endSwitch + 1, 0);

         if (!firstCol.equals (""))
            break;   // if the first column is not "" then end of IN CASE command

         endSwitch ++;
      }

      // Now we have cases [1 .. endSwitch-1] and a default value endSwitch
      //
      int theCase = indxComm + 1;
      while (theCase <= indxComm + endSwitch)
      {
         boolean executeThat = false;
         boolean elseCase = false;

         // check if it is the ELSE case
         if (theCase == indxComm + endSwitch)
         {
            value2 = commandEva.getValue (theCase, 1); // Note: do not use solveStrAsString since we are looking for a constant value
            if (value2.equalsIgnoreCase ("ELSE") || value2.equalsIgnoreCase ("OTHERWISE") || value2.equalsIgnoreCase ("NOCASE"))
            {
               elseCase = true;
               executeThat = executeOtherwise;
            }
            if (executeThat)
               that.log().dbg (2, "ELSE (option " + (theCase - indxComm) + ") will be executed");
         }

         // check it as a normal case
         if (!elseCase)
         {
            value2 = that.solveStrAsString (commandEva.getValue (theCase, 1));
            executeThat = comparator.passOperand2 (value2);
            if (executeThat)
               that.log().dbg (2, "caseValue [" + value2 + "] (option " + (theCase - indxComm) + ") will be executed");
         }

         if (executeThat)
         {
            executeOtherwise = false;

            // collect the command (or simply a text)
            subcommand.clear ();
            for (int ii = 2; ii < commandEva.cols (theCase); ii ++)
               subcommand.addCol (commandEva.getValue (theCase, ii));

            if (subcommand.cols (0) > 1)
                 that.executeSingleCommand (subcommand);
            else that.printTextLsx (subcommand.getValue (0, 0));
         }

         theCase ++;
      }

//      return (endSwitch + 1); // ojo! es el nu'mero de li'neas que ha requerido el switch!
      return 1; // does not matter, they are void command for listix
   }
}
