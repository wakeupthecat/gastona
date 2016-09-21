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

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

public abstract class inCaseCommon
{
   public abstract String commandStr ();
   public abstract String processMainValue (listixCmdStruct cmd, String oper, String mainValue);
   public abstract String doCase (listixCmdStruct cmd, String oper, String par2);

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

      String mv = processMainValue (cmd, oper, mainValue);
      that.log().dbg (2, commandStr (), mv + oper);

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
         String doCaseStr = doCase (cmd, oper, cmd.getArg (2));
         if (doCaseStr != null)
         {
            that.log().dbg (2, "case value [", doCaseStr + "] (line 0) will be executed");
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
      boolean firstPrinted = false;
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
            String doCaseStr = doCase (cmd, oper, cmd.getListix ().solveStrAsString (commandEva.getValue (theCase, 1)));
            executeThat = doCaseStr != null;
            if (doCaseStr != null)
               that.log().dbg (2, "caseValue [" + doCaseStr + "] (option " + (theCase - indxComm) + ") will be executed");
         }

         if (executeThat)
         {
            executeOtherwise = false;

            // collect the command (or simply a text)
            subcommand.clear ();
            for (int ii = 2; ii < commandEva.cols (theCase); ii ++)
               subcommand.addCol (commandEva.getValue (theCase, ii));

            if (subcommand.cols (0) > 1)
                 cmd.getListix ().executeSingleCommand (subcommand);
            else 
            {
               // all strings of a case value print a return except the last one
               if (firstPrinted)
                    cmd.getListix ().newLineOnTarget ();
               else firstPrinted = true;
               cmd.getListix ().printTextLsx (subcommand.getValue (0, 0));
            }
         }

         theCase ++;
      }

//      return (endSwitch + 1); // ojo! es el nu'mero de li'neas que ha requerido el switch!
      return 1; // does not matter, they are void command for listix
   }
}
