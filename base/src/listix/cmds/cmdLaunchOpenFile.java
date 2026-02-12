/*
Copyright (C) 2015-2026 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command LAUNCH FILE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       LAUNCH FILE
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdLaunchOpenFile
   <importance> 3
   <desc>       //Launches the associated application and opens the given file

   <help>
      //
      //  Launches the default application associated with the given url or file. This command tries to be
      //  OS independent. For windows calls directly the command "start" and for not windows (e.g. linux)
      //  calls 'xdg-open'. If another call is desired use instead the command LAUNCH.
      //

   <aliases>
      alias
      OPEN
      OPEN FILE
      LAUNCH OPEN

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Launches the default application for the given file

   <syntaxParams>
      synIndx, name          , defVal, desc
         1   , fileName ,    , //filename to open

   <options>
      synIndx, optionName  , parameters     , defVal    , desc

   <examples>
      gastSample

#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

/**
*/
public class cmdLaunchOpenFile implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "LAUNCHOPENFILE",
          "LAUNCHOPEN",
          "OPENFILE",
          "OPEN",
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

      cmd.getLog().dbg (4, "cmdLaunchOpenFile", "launch file [" + cmd.getArg(0) + "]");
      utilSys.launchOpenFile (cmd.getArg(0));

      cmd.checkRemainingOptions ();
      return 1;
   }
}
