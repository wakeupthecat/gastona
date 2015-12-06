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
   //(o) WelcomeGastona_source_listix_command ADD NUM TO VAR

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       ADD NUM TO VAR
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdAddNumericToVariable
   <importance> 2
   <desc>       //Adds a new row into a variable

   <help>
      //
      // Adds a new row to the given variable of the unit #data#.

   <aliases>
      alias
      ADD NUM
      ADD NUMERIC TO VARIABLE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Adds a row into a variable. The same as SET VAR but adding a new row to the variable 'variableName'

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , variableName ,       , //Name of variable where to add a row
         1   , value        ,       , //Value to be set in column 0
         1   , ...          ,       , //further values will be set in next columns : 1, 2 etc

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample
      adding numerical records

   <adding numerical records>
      //#javaj#
      //
      //   <frames> tTable, "listix command ADD NUM TO VAR example"
      //
      //#listix#
      //
      //   <main0>
      //      SET VAR, tTable, x, sin(x)
      //      LOOP, FOR, ii, 1, 20
      //          ,, @<addPar>
      //      MSG, tTable data!
      //
      //    <addPar>
      //      SET NUM, x, ii * 0.2
      //      ADD NUM, tTable, x, sin(x)

#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdAddNumericToVariable implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "ADD NUM",
          "ADD NUMERIC TO VARIABLE",
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

      /*
         <setado>
            ADDTO VARIABLE, Reina De los Mares, @<campo>...etc...blabla

      */
      String variableName = cmd.getArg (0);  // parameter 1
      String formula      = cmd.getArg (1);  // parameter 2

      cmd.getLog().dbg (2, "ADD NUM TO VAR", variableName + " [" + formula + "]");

      String value = calcFormulas.calculaFormula (that, formula);

      // Set the first column value
      //
      Eva theVar = that.getSomeHowVarEva (variableName);
      int row = theVar.rows ();
      theVar.setValue (value, row, 0);

      // Continue with the rest of columns
      //
      for (int cc = 2; cc < cmd.getArgSize(); cc ++)
      {
         value = calcFormulas.calculaFormula (that, cmd.getArg (cc));
         theVar.setValue (value, row, cc-1);
         cmd.getLog().dbg (2, "ADD NUM TO VAR", variableName + " [" + formula + "] = " + value);
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
