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
   //(o) WelcomeGastona_source_listix_command =

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       =
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdFormula
   <importance> 2
   <desc>       //Calculation of math expressions


   <help>
      //
      // Calculates a math expresion and prints out the result. Note that this command is also used
      // by one of the syntaxes of the command SET VAR. Variables whose names comply some minimal
      // conditions (e.g. do not having blanks like in "my var") can be expressed directly in the
      // formula. For example
      //
      //    <productCount>  892
      //    <productSum>    59982
      //
      //    <mean_value> =, "productSum / productCount"
      //
      //  this also includes formula variables, so it could be possible as well to express something
      //  like
      //
      //    <sum_100>  =, mean_value * 100
      //
      // Scientific calculations might be achieved as well.
      //
      //    Incorporated constants : pi, hpi (=pi/2), tpi (=2*pi), c, h, e, k, q
      //    Operators              : + - * / \ ^ > < <= >= mod and or xor not
      //    Functions              : exp, log, ln, sq, sqr, sin, cos, tan, atan, acos, asin, inv, abs
      //                             int, chs, deg_rad, rad_deg, min, max, atan2, r_p, p_r, rnd
      //

   <aliases>
      alias
      FORMULA
      EQUATION

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Solves the given 'formula' where the variables are referenced using directly its names and NOT using @<>

   <syntaxParams>
      synIndx, name             , defVal    , desc
         1   , formula          ,           , //Any formula usign the operands + - / * \ sin cos tan etc

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      desc


#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdFormula implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "FORMULA",
          "EQUATION",
          "=",
       };
   }
   
   //(o) DOC/listix/executing a command/3 Method execute of a command
   //    All commands perform the common tasks of getting and analyzing parameters
   //    and options and according to its contents execute the desired action.
   //    For the action they might implement all or part of it or use other
   //    objects to do the job, like it is done here with the class "calcFormulas"
   
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

      String formulae = cmd.getArg (0);
      String value = "";

      /*
         <count1>  17
         <multiplier>  2

         <calculate>
            ' the required size is
            =, // (count1 + 1) * multiplier
            ' meters

      */

      if (cmd.getArgSize() == 1)
      {
         cmd.getLog().dbg (2, "FORMULA", formulae);
         value = calcFormulas.calculaFormula (that, formulae);
      }
      else
      {
         calcFormulas.badFormulaError (cmd.getLog(), "FORMULA", formulae);
      }

      that.printTextLsx (value);
      cmd.checkRemainingOptions ();
      return 1;
   }
}