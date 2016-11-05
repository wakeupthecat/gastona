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
   <name>       IN FILE
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
      "DO FILE"

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Processes a listix format wich is contained into a file. Each line of the file is treated as text.
         2   ,    2      , //Set the once mode which makes the files to be loaded only once.

   <syntaxParams>
      synIndx, name             , defVal    , desc
         1   , fileWithFormat   ,           , //File name containing the listix text format.
         2   , ONCE MODE        ,           , //
         2   , mode             , off       , //Possible values are off (default), on, start, end. Start (set to on) and end (set to off) also clear the list of loaded files. 

   <options>
      synIndx, optionName  , parameters, defVal, desc
      1      , SOLVE VAR   , 1 / 0   ,  1, //Default is 1, if set to 0 the content of the variable will be treat as text (no solving variables like @<myvar>)
      1      , AS TEXT     , 0 / 1   ,  0, //Default is 0, setting to 1 has the same effect as set SOLVE VAR to 0

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
         "DO FILE",
         "FORMAT IN FILE",
         "TEMPLATE IN FILE",
         "TEMPLATE",
         "DO TEMPLATE",
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
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);
      
      if (cmd.getArgSize () == 1)
      {
         boolean OptSolveVar = ("1".equals (cmd.takeOptionString(new String [] {"SOLVE", "SOLVEVAR", "SOLVELSX", "SOLVELISTIX" }, "1"))) &&
                               ("0".equals (cmd.takeOptionString(new String [] {"ASTEXT" }, "0")));
         
         that.printFileLsx (cmd.getArg(0), OptSolveVar, false);
      }
      else if (cmd.getArgSize () == 2)
      {
         boolean optOnceMode = cmd.meantConstantString (cmd.getArg(0), new String [] { "ONCE", "ONCEMODE" });
         if (optOnceMode)
         {
            if (cmd.meantConstantString (cmd.getArg(1), new String [] { "on", "1" }))
               that.setOnceInfileMode (true, false);
            if (cmd.meantConstantString (cmd.getArg(1), new String [] { "off", "0" }))
               that.setOnceInfileMode (false, false);
            if (cmd.meantConstantString (cmd.getArg(1), new String [] { "start", "2" }))
               that.setOnceInfileMode (true, true);
            if (cmd.meantConstantString (cmd.getArg(1), new String [] { "end", "3" }))
               that.setOnceInfileMode (false, true);
         }
         else cmd.getLog().err ("IN FILE", "Unknown syntax [" + cmd.getArg(0) + "]");         
      }
      else cmd.getLog().err ("IN FILE", "Wrong number of parameters " + cmd.getArgSize ());         
      cmd.checkRemainingOptions ();
      return 1;
   }
}
