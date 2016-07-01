/*
library listix (www.listix.org)
Copyright (C) 2012 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command JSON

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       JSON
   <groupInfo>  data_db
   <javaClass>  listix.cmds.cmdJSON
   <importance> 5
   <desc>       //Commands for building and parsing JSON results

   <help>
      //
      // Commands for building and parsing JSON results
      // (right now just parsing)
      //


   <aliases>
      alias
      JSON
      JSONX

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Parses a file containing a JSON object and creates a XMeLon schema with it
         2   ,    5      , //@@ HERE THE DESCRIPTION OF SINTAX 2

   <syntaxParams>
      synIndx, name           , defVal      , desc
         1   , FILE2DB        ,             , 
         1   , JSONSourceFile ,             , //Name of JSON text file to be parsed
         1   , targetDbName   , (default db), //Database name where to put the result of parsing
         1   , tablePrefix    , jsonx       , //Optional prefix for the result tables ('prefix'_files, 'prefix'_tagDef, 'prefix'_pathDef and 'prefix'_data)

         2   , FILE2VARS      ,             , 
         2   , JSONSourceFile ,             , //Name of JSON text file to be parsed
         2   , tablePrefix    , jsonVars    , //Prefix for the result variable names (quasi tables)

         3   , VAR2VARS       ,             , 
         3   , EvaVariable    ,             , //Name of eva variable containing JSON text to be parsed
         3   , tablePrefix    , jsonVars    , //Prefix for the result variable names (quasi tables)

   <options>
      synIndx, optionName    , parameters, defVal  , desc
         2   , TABLE POLICY  , REDUCED / PROLIFIC,  REDUCED, //With table policy PROLIFIC a table for each JSON object type will be created 
         3   , TABLE POLICY  , REDUCED / PROLIFIC,  REDUCED, //With table policy PROLIFIC a table for each JSON object type will be created 

         
   <examples>
      gastSample

      JSON example

   <JSON example>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "JSON example", 200, 300
      //
      //#data#
      //
      //    <JSONData>
      //       //{
      //       // menu:{"1":"sql", "2":"android", "3":"mvc"},
      //       // "rumbero": "afrosio",
      //       // identiti: framio,
      //       // telephones: [
      //       //   {
      //       //      loc: casa,
      //       //      tel: "123213"
      //       //   },
      //       //   {
      //       //      loc: treball,
      //       //      tel: "43453453"
      //       //   }
      //       //   ]
      //       //}
      //       //
      //
      //#listix#
      //
      //   <main>
      //      JSON, EVA2VARS, JSONData
      //      DUMP, data
      //

#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.parse.json.*;


public class cmdJSON implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "JSON",
          "JSONX",
          "JSONMELON",
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      // ::execute - some helpful comment for all the possible syntaxes
      //
      //      comm____          oper______    par1_____   par2______
      //      JSON, ope         , p1         , p2
      //

      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);
      
      // ::execute - getting arguments (usually syntax identifier = operation and parameters)
      //
      //   cmd.getArgSize ()        count of arguments after the command (including operation if any)
      //   cmd.getArg (indx)        get the argument indx (0..n-1) solved
      //   cmd.getArg (indx, false) get the argument indx (0..n-1) NOT solved
      //
      String oper = cmd.getArg(0);
      String fileJSON = cmd.getArg(1);
      String prefixTables = cmd.getArg(2);
      String evaJSON = fileJSON; // it is the same parameter from diferent syntaxis!

      if (prefixTables == null || prefixTables.length () == 0)
         prefixTables = "JSONX";
      
      // ::execute - detecting options normalized (upper case and removing intermediate blanks) and allowing aliases
      //
      boolean opt2EVA = cmd.meantConstantString (oper, new String [] { "FILE2EVAS", "FILE2VARS" });
      boolean optEVA2EVA = cmd.meantConstantString (oper, new String [] { "EVA2EVAS", "EVA2VARS", "VAR2VARS", "VAR2EVAS" });
      
      if (!opt2EVA && !optEVA2EVA)
      {
         cmd.getLog().err ("JSON", "option " + oper + " not supported (yet)");
         return 1;
      }

      String optTablePolicy = cmd.takeOptionString(new String [] { "TABLEPOLICY", "TABLES" }, "REDUCED" );
      boolean optClearData = "1".equals (cmd.takeOptionString(new String [] { "CLEAR", "CLEAN" }, "1" ));
      
      if (optClearData)
      {
         EvaUnit eu2Clean = cmd.getListix().getGlobalData ();
         for (int ee = eu2Clean.size ()-1; ee >= 0; ee --)
         {
            Eva eva = eu2Clean.getEva (ee);
            if (eva != null && eva.getName ().startsWith (prefixTables))
            {
               cmd.getLog().dbg (4, "JSON", "remove previous variable " + eva.getName () + "");
               eu2Clean.remove (ee);
            }
         }
      }
   
      json2EVA jo = new json2EVA (optTablePolicy.equals ("REDUCED") ? json2EVA.TABLE_POLICY_REDUCED: json2EVA.TABLE_POLICY_PROLIFIC);
      
      if (optEVA2EVA)
      {
         Eva evaJSONcontent = cmd.getListix().getVarEva (evaJSON);
         if (evaJSONcontent == null)
         {
            cmd.getLog().err ("JSON", "No variable with JSON content found <" + evaJSON + ">)");
            return 1;
         }
         jo.buildEvaUnitFromJSONString (evaJSONcontent.getAsText (), prefixTables, (cmd.getListix ()).getGlobalData ());
      }
      if (opt2EVA)
      {
         jo.buildEvaUnitFromJSONFile (fileJSON, prefixTables, (cmd.getListix ()).getGlobalData ());
      }
      
      // ::execute - miscelanea
      //
      // cmd.getLog().dbg (2, "JSON", "@@ ERROR MESSAGE XX")   write an error message
      // cmd.reloadOptions ()

      cmd.checkRemainingOptions ();
      return 1;
   }
}

