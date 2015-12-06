/*
Copyright (C) 2013 Alejandro Xalabarder Aulet, Wakeupthecat UG

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
   //(o) WelcomeGastona_source_listix_command JAVA STATIC

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       REPBMP
   <groupInfo>  data_graph
   <javaClass>  listix.cmds.cmdReplaceBmp
   <importance> 3
   <desc>       //Replaces a bmp with a png

   <help>
      //
      //  Replaces a bmp with a png, optionally keeps the original bmp
      //
      //

   <aliases>
      alias
      REPBMP
      KILLBMP

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Replaces a bmp with a png, optionally keeps the original bmp

   <syntaxParams>
      synIndx, name          , defVal, desc
         1   , REPBMP        ,       ,
         1   , fileName      ,     , //Bitmap file name

   <options>
      synIndx, optionName  , parameters     , defVal    , desc
        1    , KEEP BMP    , 0/1            , 0         , //Set to 1 if want to keep the origial bmp

   <examples>
      gastSample
      
#**FIN_EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.streams.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

/**
   Listix command LUA

*/
public class cmdReplaceBmp implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "REPLACEBMP",
          "REPBMP",
          "KILLBMP",
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

      // pass parameters (solved)
      //
      String file       = cmd.getArg(0);
      if (!cmd.checkParamSize (1, 1)) return 1;

      boolean optKeepOld = "1".equals (cmd.takeOptionString(new String [] { "KEEPOLD", "KEEPBMP", "KEEP" }, "0" ));

      de.elxala.langutil.graph.replaceBmp.convertBMPtoPNG (new String [] { file }, optKeepOld);
      cmd.checkRemainingOptions (true);
      return 1;
   }
}
