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
   //(o) WelcomeGastona_source_listix_command LOAD UNIT

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       LOAD UNIT
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdLoadUnit
   <importance> 4
   <desc>       //Load an EvaUnit into current data or listix formats ...

   <help>
      //
      // Loads from a file an eva unit either into the unit #data# or the unit #listix#.
      //
      // This can be used to load data or logic dynamically. Another use is to load the last state
      // of the variables of the application (see also DUMP), for example:
      //
      //       <main0>
      //          LOAD, data, myAppPersist.txt
      //
      //       <-- javaj exit>
      //          DUMP, data, myAppPersist.txt
      //
      //    NOTE: Loading data in this way produce variables of the #data# section being replaced
      //          by the variables found in the file. This is usually the desired behaviour for a
      //          final application, but during development it could be a problem: if we change in the
      //          script contents of variables in the data section this can be ovewritten by the command
      //          load, so it is a good idea to disable this LOAD during developement.
      //

   <aliases>
      alias
      LOAD

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Loads a unit either in data or listix formats

   <syntaxParams>
      synIndx, name                 , defVal             , desc
         1   , data|formats|listix  ,                    , //Target EvaUnit, either data or formats (or equivalently listix)
         1   , fileName             ,                    , //File name where the EvaUnit 'unitFormats' is to be found

   <options>
      synIndx, optionName  , parameters       ,  defVal             , desc

         1   , UNIT2LOAD   , nameOfEvaUnit    ,  data|formats|listix, //EvaUnit to be load from the file, if not specified it has the same name as the target EvaUnit ('data', 'formats' or 'listix')
         1   , MERGE       , CLEAN/REPLACE/ADD,  REPLACE            , //CLEAN: Clean current before merging, REPLACE: replacing existing Evas, ADD: Adding lines to existing Evas

   <examples>
      desc

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdLoadUnit implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "LOAD UNIT",
            "LOAD",
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

      String targetUnit = cmd.getArg(0);
      String fileName   = cmd.getArg(1);

      String mergeType  = cmd.takeOptionString ("MERGE"    , "REPLACE");
      String unit2Load  = cmd.takeOptionString ("UNIT2LOAD", targetUnit);

      // check number of arguments
      if (cmd.getArgSize() != 2)
      {
         cmd.getLog ().err ("LOAD UNIT", "LOAD command takes 2 and only 2 parameters, given " + cmd.getArgSize());
         return 1;
      }

      // Getting the unit target
      //
      EvaUnit uTarget = null;
      if (targetUnit.equals("data"))
      {
         uTarget = cmd.getListix ().getGlobalData ();
      }
      else if (targetUnit.equals("formats") || targetUnit.equals("listix"))
      {
         uTarget = cmd.getListix ().getGlobalFormats ();
      }
      else
      {
         cmd.getLog ().err ("LOAD UNIT", "LOAD wrong unit target (first parameter), given \"" + targetUnit + "\", it should be either 'data', 'formats' or 'listix'");
         return 1;
      }

      //         MERGE_ADD         'A'   the rows of the plusEva are added at the end
      //         MERGE_REPLACE     'R'   the eva is replaced (if it already exist)

      char iMergeType = Eva.MERGE_ADD;
      if (mergeType.equals ("CLEAN"))
      {
         uTarget.clear ();
         iMergeType = Eva.MERGE_REPLACE;
      }
      else if (mergeType.equals ("REPLACE"))
      {
         iMergeType = Eva.MERGE_REPLACE;
      }
      else if (mergeType.equals ("ADD"))
      {
         iMergeType = Eva.MERGE_ADD;
      }
      else
      {
         cmd.getLog ().err ("LOAD UNIT", "wrong MERGE option, given \"" + mergeType + "\", it should be either 'CLEAN', 'REPLACE' or 'ADD'");
         return 1;
      }

      // Getting the unit source (to merge)
      //
      cmd.getLog ().dbg (2, "LOAD UNIT", "load unit [" + unit2Load + "] from [" + fileName + "] merge type " + mergeType + " (" + iMergeType + ")");
      EvaUnit uSource = EvaFile.loadEvaUnit (fileName, unit2Load);

      uTarget.merge (uSource, iMergeType);

      cmd.checkRemainingOptions ();
      return 1;
   }
}