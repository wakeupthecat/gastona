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
   //(o) WelcomeGastona_source_listix_command ADD TO VAR

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       ADD TO VAR
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdAddToVariable
   <importance> 2
   <desc>       //Adds a new row into a variable

   <help>
      //
      // Adds a new row to the given variable of the unit #data#.

   <aliases>
      alias
      ADD TO VARIABLE
      ADD TO

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
          1  , SOLVE LSX , 1 / 0    ,    1  , If false (0) the values parameters will be set without any listix resolve (variables @<..>) at all

   <examples>
      gastSample
      adding records

   <adding records>
      //#javaj#
      //   <frames> F, "listix command ADD TO VAR example"
      //
      //   <layout of F>  EVA, 10, 10, 5, 5
      //
      //   ---, A     , X
      //    X , tTable, -   , -   , -   , -
      //      , lName, eName, lTel, eTel, bAdd
      //
      //#data#
      //
      //   <tTable>
      //      name    , telephon
      //
      //      Rosa    ,  1111111111
      //      Silvia  ,  2222222222
      //      Gastona ,  6666666666
      //
      //#listix#
      //
      //   <-- bAdd>
      //      ADD TO VAR, tTable, @<eName> , @<eTel>
      //      MSG, tTable data!
      //

#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdAddToVariable implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "ADD TO VARIABLE",
          "ADDTO VAR",
          "ADDTO",
          "VAR+",
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

      boolean solveLsx = "1".equals (cmd.takeOptionString(new String [] { "SOLVE", "SOLVELSX", "SOLVELISTIX" }, "1" ));

      /*
         <setado>
            ADDTO VARIABLE, Reina De los Mares, @<campo>...etc...blabla

      */
      String variableName = cmd.getArg (0);  // parameter 1
      String value        = cmd.getArg (1, solveLsx);  // parameter 2

      // Set the first column value
      //
      Eva theVar = that.getSomeHowVarEva (variableName);
      int row = theVar.rows ();
      theVar.setValue (value, row, 0);

      // Continue with the rest of columns
      //
      for (int cc = 2; cc < cmd.getArgSize(); cc ++)
      {
         value = cmd.getArg (cc, solveLsx);
         theVar.setValue (value, row, cc-1);
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
