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
   //(o) WelcomeGastona_source_listix_command IN CASE NUM

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       IN CASE NUM
   <groupInfo>  lang_flow
   <javaClass>  listix.cmds.cmdInCaseNumeric
   <importance> 7
   <desc>       //For conditional execution of listix commands based on numeric values

   <help>
      //
      // For conditional execution of commands (short alias IF NUM). Compares the given 'mainValue'
      // with all 'caseValues' and execute the given 'subCommand' when the comparation matches.
      // If 'subCommand' contains just one column it behaves as an inline format of just one line.
      //
      // Examples:
      //
      //       IF NUM, myVar-1, =, 0, SETVAR, myText, "Verified myVar is truly true"
      //       IF NUM, myVar+1, >, 1, //Verified myVar is truly not true"
      //
      //       IN CASE NUM, myVar * yourVar + hisVar,
      //                  , 0       , //false
      //                  , 1       , //true
      //                  , ELSE    , BOX, W, "Why your myVar * yourVar + hisVar is not boolean?"
      //

   <aliases>
      alias
      IF NUM
      IF N
      IN CASE N


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Switch case execution of listix commands depending on the values of a variable or variables

   <syntaxParams>
      synIndx, name         , defVal         , desc
         1   , mainValue    ,                , //Main value to compare
         1   , = / < / > / <> / <= / >= , =  , //Operation to acomplish by the mainValue respect to the secondary values
         1   , [caseValue, subcommand],      , //A first pair caseValue-subcommand might be given as argument

   <options>
      synIndx, optionName  , parameters, defVal, desc
      1      , caseValue   , subCommand,       , //If 'mainValue' operate with 'caseValue' is true then the 'subCommand' given in the further parameters is executed
      1      , ...         ,           ,       , //More caseValue are accepted until ELSE option or end of command options
      1      , ELSE        , subCommand,       , //If given it has to be the last option. Will execute 'subCommand' if no other caseValue has matched.

   <examples>
      gastSample

#**FIN_EVA#
*/
package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

public class cmdInCaseNumeric implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "IN CASE N",
            "IN CASE NUM",
            "IF NUM",
            "IF N",
         };
   }

   boolean operationTrue (double op1, double op2, String oper)
   {
      if (oper.equals ("=") || oper.equals ("==")) return op1 == op2;
      if (oper.equals ("<>") || oper.equals ("!=")) return op1 != op2;
      if (oper.equals (">")) return op1 > op2;
      if (oper.equals (">=")) return op1 >= op2;
      if (oper.equals ("<")) return op1 < op2;
      if (oper.equals ("<=")) return op1 <= op2;
      if (oper.equals ("!=")) return op1 < op2;
      return false;
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

      double mainV = calcFormulas.calcFormula (that, mainValue);

      that.log().dbg (2, "IN CASE NUM", mainValue + "(" + mainV + ") " + oper);

      boolean executeOtherwise = true;
      // NOTE: the eva subcommand is independent of the current EvaUnit data!
      Eva subcommand = new Eva ("subcommand");
      double value2 = 0.;

      if (cmd.getArgSize() > 2)
      {
         // we have a initial value-command pair
         //       0           1       2       3       4      5    6   << index for commandEva
         //                   0       1       2       3      4    5   << index for cmd.getArg
         //    IN CASE NUM, valueRef, oper, value, command, par, etc
         //
         value2 = calcFormulas.calcFormula (that, cmd.getArg (2));
         if (operationTrue (mainV, value2, oper))
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
            String colVal = commandEva.getValue (theCase, 1); // Note: do not use solveStrAsString since we are looking for a constant value
            if (colVal.equalsIgnoreCase ("ELSE") || colVal.equalsIgnoreCase ("OTHERWISE") || colVal.equalsIgnoreCase ("NOCASE"))
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
            value2 = calcFormulas.calcFormula (that, that.solveStrAsString (commandEva.getValue (theCase, 1)));
            executeThat = operationTrue (mainV, value2, oper);
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
