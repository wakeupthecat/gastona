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
   //(o) WelcomeGastona_source_listix_command DO FILE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.


#gastonaDoc#

   <docType>    listix_command
   <name>       DO FILE
   <groupInfo>  lang_files
   <javaClass>  listix.cmds.cmdInfile
   <importance> 2
   <desc>       //Execute a listix format which is contained into a file


   <help>
      //
      // Execute a listix format which is contained into a file. The whole file is treated as text
      // so no last line indicators (// or ') are necessary before each line, but it is still possible
      // the use of @ variables, that is calls to another formats.
      //
      // Note that there are other ways to work with formats in separate files (see commands LOAD and
      // GENERATE).
      //
      // This command is useful when the format is big and/or it is preferred to maintain it enterely
      // within a file.
      //


   <aliases>
      alias
      "FORMAT IN FILE"
      "TEMPLATE IN FILE"
      "TEMPLATE"
      "IN FILE"

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Processes a listix format wich is contained into a file. Each line of the file is treated as text.

   <syntaxParams>
      synIndx, name             , defVal    , desc
         1   , fileWithFormat   ,           , //File name containing the listix text format.

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      desc


#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;

public class cmdInfile implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
         "FORMAT IN FILE",
         "TEMPLATE IN FILE",
         "TEMPLATE",
         "IN FILE",
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
      /*
         <valorDelCampo>
            FORMAT IN FILE, @<variant>/htmlTemplateForTheOcasion.htmlsx

      */
      String fileName = that.solveStrAsString (commandEva.getValue (indxComm, 1));
      String [] arrFormat = TextFile.readFile (fileName);
      if (arrFormat == null)
      {
         that.log().err ("FORMATINFILE", "the file [" + fileName + "] could not be read!");
         return 1;
      }

      for (int ii = 0; ii < arrFormat.length; ii ++)
      {
         if (ii > 0) that.newLineOnTarget ();
         that.printTextLsx (arrFormat[ii]);
      }

      return 1;
   }
}
