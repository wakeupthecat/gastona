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
   //(o) WelcomeGastona_source_listix_command SET VAR

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       SET VAR
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdSetVariable
   <importance> 7
   <desc>       //Sets a value into an Eva variable in the unit #data#

   <help>
      //
      // Sets a value into an Eva variable in the unit #data#. If the variable does not exist yet it will
      // be created and if the variable already had contents it will be replaced. After executing the command
      // the variable will have just one row, although it might have more columns. To add new rows use the
      // command ADD TO VAR.

   <aliases>
      alias
      SET VARIABLE
      SET EVA

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    7      , //Set an eva variable at row 0 with one or more values (using more than one column)

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , variableName ,       , //Name of Eva variable to be set
         1   , valueToSolve ,       , //Value to be set in column 0
         1   , ...          ,       , //further values will be set in next columns : 1, 2 etc

   <options>
      synIndx, optionName, parameters, defVal, desc
          1  , SOLVE LSX , 1 / 0    ,    1  , If false (0) the values parameters will be set without any listix resolve (variables @<..>) at all

   <examples>
      gastSample

      sample setvar 1
      setting an edit field

   <sample setvar 1>
      //#javaj#
      //
      //   <frames> oConsole, "SET VAR sample"
      //
      //#data#
      //
      //    <myOldVar>
      //       the default value contains
      //       several columns, and
      //       also, several, rows
      //
      //#listix#
      //
      //    <dumpVariables>
      //       //
      //       //=== variables @<p1>
      //       //
      //       DUMP, data,, myOldVar, myNewVar
      //
      //   <main0>
      //       LISTIX, dumpVariables, //at the beginning
      //       SET VAR, myNewVar, simple
      //       LISTIX, dumpVariables, //after "SET VAR, myNewVar, simple"
      //       SET VAR, myNewVar, more, complex, value, //one,two,three
      //       LISTIX, dumpVariables, //after SET VAR, myNewVar, more, complex,...
      //       SET VAR, myOldVar, end of sample
      //       LISTIX, dumpVariables, //after "SET VAR, myOldVar, end of sample"


   <setting an edit field>
      //#javaj#
      //   <frames> F, "listix command SET VAR example"
      //
      //   <layout of F>  EVA, 10, 10, 5, 5
      //
      //   ---, A
      //    X , iList
      //      , eCampo
      //
      //#data#
      //   <iList>
      //      name
      //
      //      Rosa
      //      Silvia
      //      Gastona
      //
      //#listix#
      //
      //   <-- iList>
      //      SET VAR, eCampo, @<iList selected.name>
      //      MSG, eCampo data!
      //



#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdSetVariable implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
         "SET VAR",
         "SET VARIABLE",
         "SET EVA",
         "VAR=",
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

      //(o) TODO_remove old compatibility
      boolean solveLsx = true;
      if ("0".equals (cmd.takeOptionString(new String [] { "RAWDATA", "NOSOLVE", "NOTSOLVE", "NOLSXSOLVE", "NOLISTIXSOLVE" }, "0" )))
         solveLsx = false;
      if ("1".equals (cmd.takeOptionString(new String [] { "SOLVE", "SOLVELSX", "SOLVELISTIX" }, "1" )))
         solveLsx = true;

      /*
         <setado>
            SET VAR, Reina De los Mares, @<campo>...etc...blabla

      */
      String variableName = cmd.getArg (0);  // parameter 1
      String value        = cmd.getArg (1, solveLsx);  // parameter 2

      // Note: if solved value (parameter 2) is "=" MAYBE IT IS NOT A FORMULA BUT A VALUE!
      //       for a formula is mandatory to write "=" in the parameter 1 and have only three parameters
      boolean isFormula = commandEva.getValue (indxComm, 2).equals ("=");

      if (isFormula)
      {
         cmd.getLog().err ("SET VAR", "\"SET VAR, varname, = ...\" is DEPRECATED! use \"SET NUM, varname, formula\" instead!");
         if (cmd.getArgSize() == 3)
         {
            cmd.getLog().dbg (2, "SET VAR", "value is a formula [" + cmd.getArg (2) + "]");
            value = calcFormulas.calculaFormula (that, cmd.getArg (2));
         }
         else
         {
            calcFormulas.badFormulaError (cmd.getLog(), "SET VAR", cmd.getArg (2));
         }
      }

      // Set the first column value
      //
      Eva theVar = that.getSomeHowVarEva (variableName);
      theVar.setValueVar (value);

      if (isFormula) return 1;   // if formula, all is done!

      // Continue with the rest of columns
      //
      for (int cc = 2; cc < cmd.getArgSize(); cc ++)
      {
         value = cmd.getArg (cc, solveLsx);
         theVar.setValue (value, 0, cc-1);
      }

      cmd.checkRemainingOptions ();
      return 1;
   }
}
