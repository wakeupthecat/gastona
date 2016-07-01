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
   //(o) WelcomeGastona_source_listix_command DUMP UNIT

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       DUMP UNIT
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdDump
   <importance> 2
   <desc>       //Dumps into a file either the data unit or the listix formats ...


   <help>
      //
      // Writes into a file variables of a unit using the Eva format. The unit can be given in the
      // first argument, either "data" or "listix". In the second argument a file name can be given,
      // if left in blank the dump is performed into the current listix generation target (e.g. console).
      //
      // This command can be useful to examine the #data# variables while developing. Another use of the
      // command is to make persistent the current state of the application (almost all is contained
      // in the unit #data#), after saving it it can be loaded with the command LOAD on starting
      // the application. Typically the DUMP command can be performed on exiting the application
      // (message "javaj exit"), and the LOAD on staring the application (in format <main> or <main0>)
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
      DUMP
      DUMP VAR

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Dumps a variable or a whole unit (data or formats) into a file

   <syntaxParams>
      synIndx, name               , defVal                 , desc
         1   , data/formats/listix,                      , //EvaUnit to be dumped, either data or formats (or equivalently listix)
         1   , fileName         , (current listix target), //Name of the file to dump the variables, if not given the current listix target will be used
         1   , [variableName    ,                        , //Name of the Eva variable to be dumped, if not given the whole EvaUnit will be dumped
         1   , ... ]            ,                        , //further variableName might be given in separate columns

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      dump example

   <dump example>
      //#javaj#
      //
      //   <frames> oConsole, "DUMP example"
      //
      //#data#
      //
      //    <someData>  blah
      //
      //#listix#
      //
      //    <main0>
      //       DUMP, data,,  someData
      //       DUMP, listix
      //
      //


#**FIN_EVA#
*/

package listix.cmds;

import java.util.List;
import java.util.Vector;
import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.TextFile;
import de.elxala.langutil.filedir.fileUtil;

public class cmdDump implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "DUMP UNIT",
            "DUMP",
            "DUMP VAR",
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

      int nPar = commandEva.cols(indxComm);

      String whatDump = cmd.getArg(0);
      String fileName = cmd.getArg(1);

      // solve also evas to dump (?)
      //
      List evas2Dump = new Vector ();
      for (int ii = 2; ii < cmd.getArgSize(); ii ++)
      {
         evas2Dump.add (cmd.getArg(ii));
      }

      // Getting the unit source
      //
      EvaUnit uSource = null;
      if (whatDump.equals("data"))
      {
         uSource = cmd.getListix ().getGlobalData ();
      }
      else if (whatDump.equals("formats") || whatDump.equals("listix"))
      {
         uSource = cmd.getListix ().getGlobalFormats ();
      }
      else
      {
         cmd.getLog ().err ("DUMP UNIT", "wrong unit to dump (first parameter), given \"" + whatDump + "\", it should be either 'data', 'formats' or 'listix'");
         return 1;
      }

      EvaUnit filt = new EvaUnit (uSource.getName ());

      if (evas2Dump.size () == 0)
      {
         // not list of evas specified, thus all evas will be dumped
         filt = uSource;
      }
      else
      {
         // a list of regular expresion for the eva names is specified, thus include only these ones
         List selec = EvaUtil.getEvaNames (uSource, evas2Dump, true);

         for (int ii = 0; ii < selec.size(); ii ++)
            filt.add (uSource.getEva ((String) selec.get(ii)));
      }

      if (fileName.length () > 0)
      {
         if (!fileUtil.ensureDirsForFile (fileName) ||
             !TextFile.writeFile (fileName, filt.toString ()))
         {
            cmd.getLog ().err ("DUMP UNIT", "writting file \"" + fileName + "\", unsuccessful!");
         }
      }
      else
      {
         that.writeStringOnTarget ("" + filt);
      }

      cmd.checkRemainingOptions ();
      return 1;
   }
}