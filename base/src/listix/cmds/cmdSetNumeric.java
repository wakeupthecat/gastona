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
   //(o) WelcomeGastona_source_listix_command SET NUM

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       SET NUM
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdSetNumeric
   <importance> 6
   <desc>       //Sets a numeric value into an Eva variable in the unit #data#

   <help>
      //
      // Sets a numeric value into an Eva variable in the unit #data#. If the variable does not exist
      // yet it will be created and if the variable already had contents it will be replaced. After
      // executing the command the variable will have just one row, although it might have more columns.
      // To add new rows use the command ADD NUM TO VAR.

   <aliases>
      alias
      SET NUM
      SET NUMERIC
      SET =

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Set an eva variable (at row, column 0,0) with the result of a formula

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , variableName  ,       , //Name of Eva variable to be set with the result of the formula
         1   , formulaToSolve,       , //Formula to be solved (see command FORMULA)
         1   , ...           ,       , //more formulas for next columns

   <options>
      synIndx, optionName, parameters, defVal, desc
          1  , ROUND     , decimals  ,    0  , Number of decimals to round to

   <examples>
      gastSample




#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

public class cmdSetNumeric implements commandable
{

   public class menciona
   {
      public calcFormulas o1=null;
   }

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
         "SET NUM",
         "SET NUMERIC",
         "SET =",
         "NUM=",
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
            SET VAR, Reina De los Mares, @<campo>...etc...blabla

      */
      String variableName = cmd.getArg (0);  // parameter 1
      String value        = cmd.getArg (1);  // parameter 2

      String roundDecimals = cmd.takeOptionString(new String [] { "ROUND", "FIX" }, null );

      cmd.getLog().dbg (2, "SET NUM", "value is a formula [" + value + "]");

      //System.out.println ("Formula es [" + value + "]");
      value = calcFormulas.calculaFormula (that, value);
      //System.out.println ("Value vale [" + value + "]");

      if (roundDecimals != null)
      {
         value = cmdStrconvert.formatFix(stdlib.atof (value), stdlib.atoi (roundDecimals));
         //System.out.println ("round " + roundDecimals);
      }

      // Set the first column value
      //
      Eva theVar = that.getSomeHowVarEva (variableName);
      theVar.setValueVar (value);

      // Continue with the rest of columns
      //
      for (int cc = 2; cc < cmd.getArgSize(); cc ++)
      {
         value = calcFormulas.calculaFormula (that, cmd.getArg (cc));
         if (roundDecimals != null)
            value = cmdStrconvert.formatFix(stdlib.atof (value), stdlib.atoi (roundDecimals));

         theVar.setValue (value, 0, cc-1);
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
