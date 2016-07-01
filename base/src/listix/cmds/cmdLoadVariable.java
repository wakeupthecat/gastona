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
   <name>       LOAD VAR
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdLoadVariable
   <importance> 7
   <desc>       //Loads a variable from a file

   <help>
      //
      // Loads a variable from a file

   <aliases>
      alias
      LOAD VARIABLE
      LOAD EVA
      VAR&

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    7      , //Loads a variable from a file

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , variableName ,       , //Name of Eva variable to be set
         1   , srcVariableName,     , //Variable name in the file, if not provided variableName is used
         1   , filename     ,       , //Filename containing the variable to loadValue
         1   , unit         ,  data , //Name of eva unit that contains the variable

   <options>
      synIndx, optionName, parameters, defVal, desc
        1    , FILE      , "fileName",, //filename where to load the variable(s), if given it overrides the one given in 2nd parameters
        1    , UNIT      , "unitName",, //evaunit name to load, if given it overrides the one given in 3th parameter
        1    , SET       , "variableName, srcVarName",, //pair variableName-scrVarName (default variableName) to set
        1    , ADD       , "variableName, srcVarName",, //pair variableName-scrVarName (default variableName) to add or append

   <examples>
      gastSample

      sample loadvar 1

   <sample loadvar 1>
      //#listix#
      //
      //   <fileContent>
      //      //#dicc#
      //      //
      //      //   <number> 1556
      //      //   <people>
      //      //      ID, NAME
      //      //      8282, Ramon
      //      //      7273, Eli
      //
      //   <main>
      //     GEN, :mem fi, fileContent
      //     VAR&, gentes, people, :mem fi, dicc
      //         , SET, single, number
      //     //
      //     //People I know
      //     //
      //     LOOP, VAR, gentes
      //         ,, //@<ID>: @<NAME>
      //     //
      //     //Value of single @<single>
      //     //


#**FIN_EVA#
*/

package listix.cmds;

import java.util.*;
import listix.*;
import de.elxala.Eva.*;

public class cmdLoadVariable implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
         "LOAD VAR",
         "LOAD VARIABLE",
         "LOAD EVA",
         "VAR&",
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

      //     VAR&, gentes, :mem fi, dicc, people
      String variableName = cmd.getArg (0);
      String srcVarName   = cmd.getArg (1);
      String fileName     = cmd.getArg (2);
      String unitName     = cmd.getArg (3);

      if (unitName.length () == 0)
        unitName = "data";
     
      String op = cmd.takeOptionString (new String [] { "FILE", "FILENAME" }, "");
      if (op.length () > 0)
         fileName = op; // just override it without error or warning (feature!)

      op = cmd.takeOptionString (new String [] { "UNIT", "EVAUNIT" }, "");
      if (op.length () > 0)
         unitName = op; // just override it without error or warning (feature!)
      
      // create an array of pairs var-src for operation set
      List mapSetVar = new Vector ();
      mapSetVar.add (variableName);
      mapSetVar.add ((srcVarName.length () > 0) ? srcVarName: variableName);

      cmd.getLog().dbg (4, "LOAD VAR, fileName " + fileName + " unit " + unitName);
      do
      {
         String [] par = cmd.takeOptionParameters(new String [] { "SET", "VAR&", "&", "" } );
         if (par == null) break;
         if (par.length > 0)
         {
            mapSetVar.add (par[0]);
            mapSetVar.add ((par.length > 1) ? par[1]: par[0]); // default [0]
         }
         else cmd.getLog().err ("LOAD VAR", "option SET with no parameters!");
      } while (true);

      // create an array of pairs var-src for operation append
      List mapAppendVar = new Vector ();
      do
      {
         String [] par = cmd.takeOptionParameters(new String [] { "ADD", "APPEND", "VAR+&", "+&" } );
         if (par == null) break;
         if (par.length > 0)
         {
            mapAppendVar.add (par[0]);
            mapAppendVar.add ((par.length > 1) ? par[1]: par[0]); // default [0]
         }
         else cmd.getLog().err ("LOAD VAR", "option ADD with no parameters!");
      } while (true);

      // load unit from file
      //
      EvaUnit euSrc = EvaFile.loadEvaUnit (fileName, unitName);
      if (euSrc == null)
      {
         cmd.getLog().err ("LOAD VAR", "Either file " + fileName + " or EvaUnit " + unitName + " not found!");
         return 1;
      }

      // perform all SET's
      for (int ii = 0; ii+1 < mapSetVar.size (); ii += 2)
      {
         String p1 = (String) mapSetVar.get (ii);
         String p2 = (String) mapSetVar.get (ii + 1);
         Eva theVar = that.getSomeHowVarEva (p1);
         Eva eva = euSrc.getEva (p2);
         if (eva == null)
         {
            cmd.getLog().err ("LOAD VAR", "Source var " + p2 + " not found!");
            continue;
         }
         cmd.getLog().dbg (4, "LOAD VAR SET pair " + p1 + " - " + p2);
         theVar.merge (eva, Eva.MERGE_REPLACE, false);         
      }

      // perform all ADD's
      for (int ii = 0; ii+1 < mapAppendVar.size (); ii += 2)
      {
         String p1 = (String) mapAppendVar.get (ii);
         String p2 = (String) mapAppendVar.get (ii + 1);
         Eva theVar = that.getSomeHowVarEva (p1);
         Eva eva = euSrc.getEva (p2);
         if (eva == null)
         {
            cmd.getLog().err ("LOAD VAR", "Source var " + p2 + " not found!");
            continue;
         }
         theVar.merge (eva, Eva.MERGE_ADD, false);         
      }

      cmd.checkRemainingOptions ();
      return 1;
   }
}
