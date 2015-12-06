/*
library de.elxala
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
   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       SET DATA!
   <groupInfo>  lang_comm
   <javaClass>  listix.cmds.CmdSetUpdate
   <importance> 4
   <desc>       //Same as SET VAR + MESSAGE update data


   <help>
      //
      // Set an attribute of a widget and send the message "data!" (update data) to the widget in one
      // step. Note that the alias command "-->" is used in most samples.
      // If the widget use more than one attribute for his data it is better to use SET VAR and only
      // SET DATA! on setting the last attribute.
      //
      //   Example:
      //
      //       SET VAR, tTabla, dbName, data/memory.db
      //       -->, tTabla, sqlSelect, //SELECT * FROM myTable
      //
      //       which is equivalent to
      //
      //       SET VAR, tTabla, dbName, data/memory.db
      //       SET VAR, tTabla, sqlSelect, //SELECT * FROM myTable
      //       MESSAGE, tTabla data!
      //

   <aliases>
         alias
         -->
         SET UPDATE
         SET AND UPDATE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Combination of SETVAR and update data message for the variable 'attributeName' of the widget 'widgetName'

   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , widgetName   ,             , //Name of the widget to set and update
         1   , attributeName,             , //Attribute name of the widget to set
         1   , value        ,             , //Value to set into 'attributeName' of 'widgetName'

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample

      setUpdate example

   <setUpdate example>
      //#javaj#
      //
      //    <frames> fMain, "Listix intro 2"
      //
      //    <layout of fMain>
      //       EVALAYOUT
      //       --- ,         , X
      //           , bPressMe, eField, bPressMe2
      //
      //#listix#
      //
      //    <-- bPressMe>
      //       SET VAR, eField, "well done!"
      //       MESSAGE, eField data!
      //
      //    <-- bPressMe2>
      //       -->, eField,, "this works as well!"

#**FIN EVA#

*/

package gastona.cmds;

import listix.*;
import listix.cmds.commandable;

import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import listix.cmds.*;

/**
   to allow sending mensaka messages from listix !


*/
public class CmdSetUpdate implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "SET DATA!",
          "-->",
          "SETUPDATE",
          "SETANDUPDATE",
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
            -->, bButon, enabled, @<si o no>

      */
      String widgetName   = cmd.getArg (0);
      String variableName = cmd.getArg (1);
      String value        = cmd.getArg (2);  // parameter 3

      if (cmd.getArgSize () < 3)
      {
         cmd.getLog().warn ("SET DATA!", "expected three arguments");
      }

      // form the property name (widget + " " + property)
      //
      if (variableName.length () == 0)
           variableName = widgetName;
      else variableName = widgetName + " " + variableName;

//// DEPRECADO!  29.10.2009 13:38
////      // Note: if solved value (parameter 3) is "=" MAYBE IT IS NOT A FORMULA BUT A VALUE!
////      //       for a formula is mandatory to write "=" in the parameter 3 and have only four parameters
////      boolean isFormula = commandEva.getValue (indxComm, 3).equals ("=");
////
////      if (isFormula)
////      {
////         if (cmd.getArgSize() == 4)
////         {
////            cmd.getLog().dbg (2, "SET AND UPDATE", "value is a formula [" + cmd.getArg (3) + "]");
////            value = calcFormulas.calculaFormula (that, cmd.getArg (3));
////         }
////         else
////         {
////            calcFormulas.badFormulaError (cmd.getLog(), "SET AND UPDATE", cmd.getArg (3));
////         }
////      }
////
////      if (!isFormula)
////         cmd.getLog().dbg (2, "SET AND UPDATE", "SET INTO VARIABLE [" + variableName + "]");


      cmd.getLog().dbg (2, "SET AND UPDATE", "SET INTO VARIABLE [" + variableName + "]");

//      Eva theVar = that.getGlobalData ().getSomeHowEva(variableName);
      Eva theVar = that.getSomeHowVarEva (variableName);

      theVar.clear ();

      // set the first value
      theVar.setValue (value);

      Mensaka.sendPacket (widgetName + " data!", that.getGlobalData ());
      Mensaka.sendPacket (widgetName + " control!", that.getGlobalData ());

      return 1;
   }
}
