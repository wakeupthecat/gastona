/*
library listix (www.listix.org)
Copyright (C) 2015 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command STRCONV

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    listix_command
   <name>       FILEUTIL
   <groupInfo>  lang_files
   <javaClass>  listix.cmds.cmdFileutil
   <importance> 3
   <desc>       //Utilities with files

   <help>
      //
      // Some utilities with files and directories
      //
      //    Example:
      //       storing a memory file into a physical file
      //
      //       FILEUTIL, COPY, :mem myfile, MyFile.txt
      //

   <aliases>
      alias
      FILE
      FILEUTIL


   <syntaxHeader>
      synIndx, importance, desc
         1   ,   3       , //Copies one file into another creating target directories as needed
         2   ,   3       , //Copies one file into another creating target directories as needed
         3   ,   3       , //Ensure directories are created for a given file path

   <syntaxParams>
      synIndx, name         , defVal, desc
      1      , COPY         ,       , //
      1      , sourceFileName,       , //Source file name
      1      , targetFileName,       , //Target file name

      2      , MOVE          ,       , //
      2      , sourceFileName,       , //Source file name
      2      , targetFileName,       , //Target file name

      3      , ENSURE DIR 4 FILE,   , //
      3      , filePath     ,       , //File path whose directory path has to be ensured. Note that the file name is not relevant since it will not be created or changed.

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

#**FIN_EVA#
*/

package listix.cmds;

import java.io.File;
import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.DateFormat;
import de.elxala.db.*;
//import de.elxala.mensaka.*;   // for messages start, progress, end
import de.elxala.zServices.*;


public class cmdFileutil implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "FILE",
          "FILEUTIL",
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
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String oper = cmd.getArg(0);

      boolean OptSolveVar = ("1".equals (cmd.takeOptionString(new String [] {"SOLVE", "SOLVEVAR", "SOLVELSX", "SOLVELISTIX" }, "1"))) &&
                            ("0".equals (cmd.takeOptionString(new String [] {"ASTEXT" }, "0")));

      if (cmd.meantConstantString (oper, new String [] { "COPY", "COPIA" } ))
      {
         String sourceFile = cmd.getArg(1);
         String targetFile = cmd.getArg(2);

         if (!fileUtil.copyFile (sourceFile, targetFile))
            that.log ().err ("FILEUTIL", "Copy from [" + sourceFile + "] to [" + targetFile + "] failed!");
      }
      else if (cmd.meantConstantString (oper, new String [] { "MOVE", "MUEVE", "MOU" } ))
      {
         String sourceFile = cmd.getArg(1);
         String targetFile = cmd.getArg(2);

         if (!fileUtil.moveFile (sourceFile, targetFile))
            that.log ().err ("FILEUTIL", "Move from [" + sourceFile + "] to [" + targetFile + "] failed!");
      }
      else if (cmd.meantConstantString (oper, new String [] {"ENSUREDIR4FILE", "ENSUREDIRFORFILE"}))
      {
         fileUtil.ensureDirsForFile (cmd.getArg(1));
      }
      else
         cmd.getLog ().err ("FILEUTIL", "syntax [" + oper + "] not recognized!");

      cmd.checkRemainingOptions ();
      return 1;
   }
}

