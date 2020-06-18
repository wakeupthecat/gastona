/*
library listix (www.listix.org)
Copyright (C) 2015-2020 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command TOUCH

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       TOUCH
   <groupInfo>  system_files
   <javaClass>  listix.cmds.cmdTouch
   <importance> 3
   <desc>       //To change the data of a file

   <gastonaSecure> 9

   <help>
      //
      //  Change the data of a file or creates it if it does not exists
      //

   <aliases>
      alias

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Change the data of a file or creates it if it does not exists

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , fileName     ,       , //File to be changed
         1   , date         , now   , //Date to be set to the file

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>

#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import java.io.*;
import java.util.Date;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

public class cmdTouch implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "TOUCH",
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

      String fileName = cmd.getArg (0);
      String dateStr = cmd.getArg (1);

      File fil = new File2 (fileName);

      if (dateStr == null || dateStr.length () == 0 || dateStr.equalsIgnoreCase ("now"))
      {
         DateFormat hoy = new DateFormat (new Date ());
         fil.setLastModified (hoy.getAsLong ());
      }
      else
      {
         // expected yyyy-MM-dd HH:mm:ss
         fil.setLastModified (DateFormat.getAsLong (dateStr));         
      }

      cmd.checkRemainingOptions ();
      return 1;
   }
}
