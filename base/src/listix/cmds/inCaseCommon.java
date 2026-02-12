/*
library listix (www.listix.org)
Copyright (C) 2016-2022 Alejandro Xalabarder Aulet

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
   public abstract String commandStr       ();
   public abstract String processMainValue (comparator compa, listixCmdStruct cmd, String oper, String mainValue);
   public abstract String doCase           (comparator compa, listixCmdStruct cmd, String oper, String par2);

   // struct that contains the comparison status for both cmdInCase and cmdInCaseNumeric
   //
   protected class comparator
   {
      public tableSimpleFilter filter = null;
      public double mainCalc = 0.f;
   }

   //protected tableSimpleFilter comparator = null;

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts

   */
   public int executeInCase (comparator compa, listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmdLsx = new listixCmdStruct (
                                    that,
                                    commandEva,
                                    indxComm,
                                    1,        // offsetOptions
                                    false     // normalizeOpts (options are indeed values or formulas)
                                  );

      String mainValue = cmdLsx.getArg (0);
      String oper      = cmdLsx.getArg (1);

      if (oper.equals(""))
         oper = "=";

      //since options are not normalized we need more cases, but don't accept mixed values (e.g. "Else" etc)
      //
      Eva elseFormat = cmdLsx.takeOptionAsEva (new String [] {
                  "else",
                  "ELSE",
                  "OTHERWISE",
                  "otherwise",
                  "NOCASE",
                  "nocase",
                  "NO CASE",
                  "no case",
                  });

      // set comparator
      //
      String mv = processMainValue (compa, cmdLsx, oper, mainValue);
      that.log().dbg (2, commandStr (), mv + oper);

      // NOTE: the eva subcommand is independent of the current EvaUnit data!
      Eva subcommand = new Eva ("subcommand");

      // is it a "one line" (except the ELSE option) command ?
      //
      if (cmdLsx.getArgSize() > 2)
      {
         // we have a initial value-command pair
         //       0           1       2       3       4      5    6   << index for commandEva
         //                   0       1       2       3      4    5   << index for cmdLsx.getArg
         //    IN CASE NUM, valueRef, oper, value, command, par, etc
         //
         String doCaseStr = doCase (compa, cmdLsx, oper, cmdLsx.getArg (2));
         if (doCaseStr != null)
         {
            that.log().dbg (2, "case value [", doCaseStr + "] (line 0) will be executed");

            // collect the command or simply the text
            for (int ii = 4; ii < commandEva.cols (indxComm); ii ++)
               subcommand.addCol (commandEva.getValue (indxComm, ii));

            cmdLsx.getListix ().doFormat (subcommand);
            // we continue evaluating option values
            // if for whatever reason there are more equal values in the options
            // these will be executed in a separated block (different eva)
         }
      }

      int nCase = 1;
      boolean somethingExecuted = false;

      // evaluate options matching the criteria
      //
      do {
         String [] remop = cmdLsx.getRemainingOptionNames ();
         if (remop.length == 0) break;
         String nextOpt = remop[0];

         // we get all lines with that not solved option name
         //
         Eva oneCaseFormat = cmdLsx.takeOptionAsEva (new String [] { nextOpt });
         that.log().dbg (3, "evaluating case #" + nCase + " (" + nextOpt + ") ...");

         // check the solved option name has to be executed
         String doCaseStr = doCase (compa, cmdLsx, oper, cmdLsx.getListix ().solveStrAsString (nextOpt));
         if (doCaseStr != null)
         {
            that.log().dbg (2, "case #" + nCase + " [" + doCaseStr + "] (option " + nextOpt + ") will be executed");
            cmdLsx.getListix ().doFormat (oneCaseFormat);
            somethingExecuted = true;
         }
         nCase ++;
      } while (true);

      if (!somethingExecuted && elseFormat != null)
      {
         that.log().dbg (2, "else case (lines " + elseFormat.rows () + ") will be executed");
         cmdLsx.getListix ().doFormat (elseFormat);
      }

      cmdLsx.checkRemainingOptions (true);
      return 1;
   }
}
