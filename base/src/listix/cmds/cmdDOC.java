/*
library listix (www.listix.org)
Copyright (C) 2018 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command DOC

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       DOC
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdDOC
   <importance> 5
   <desc>       //No operation (nop) command thought for script documentation

   <help>
      //
      // This command does nothing. Listix will skip it even any solving of its parameters or
      // options will be performed at all. As it name indicates it is thought for containing 
      // documentation about the script. Although the command - except for the fact that it has to be skiped -
      // has no influence in the run of the script it belongs to it, in this sense it is different from a pure
      // comment which will be discarded in the parsing phase when loading the script. So if the script is merged
      // dumped or send remotely the DOC command will be there as well.
      //
      // Three syntaxes are provided for general use, but just as recommendation since they will not be 
      // enforced nor checked in any way by listix while running the script.
      //
      //       DOC, //some description of the point of the script or the weather
      //
      //       DOC, DOC, TODO/engine/parser, // blah blah ...
      //
      //       DOC, SEQ, engine/parser, 1, user, scanner, scan directories for source files
      //       DOC, SEQ, engine/parser, 2, scanner, parser, do parse one file
      //

   <aliases>
      alias
      DOK

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    1      , //Simple description line
         2   ,    1      , //Documents some part of the script
         3   ,    1      , //Documents a step in a sequence diagram


   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , descr        ,       , //Any text, it can be used to describe anything
         2   , DOC          ,       , //
         2   , node         ,       , //A specific documentation node using for example the / as separator
         2   , text         ,       , //Note about the node
         3   , SEQ          ,       , //
         3   , node         ,       , //A specific documentation node using for example the / as separator
         3   , step         ,       , //It can be a number, for example between 1 and 1000
         3   , agent        ,       , //Name of the source agent of the sequence
         3   , target       ,       , //Name of the target agent of the sequence
         3   , message      ,       , //Description of the sequence step
         
         
   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdDOC implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "DOK",
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
      // all is all right!
      // no action at all, even no debug message
      // from the point of view of the running script a DOC command is nothing!

      return 1;
   }
}
