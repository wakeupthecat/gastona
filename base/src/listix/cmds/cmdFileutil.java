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

   <syntaxParams>
      synIndx, name         , defVal, desc
      1      , COPY         ,       , //
      1      , sourceFileName,       , //Source file name
      1      , targetFileName,       , //Target file name

      2      , ENSURE DIR 4 FILE,   , //
      2      , filePath     ,       , //File path whose directory path has to be ensured. Note that the file name is not relevant since it will not be created or changed.

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
import de.elxala.mensaka.*;   // for messages start, progress, end
import de.elxala.zServices.*;


public class cmdFileutil implements commandable
{
   private static MessageHandle LIGHT_MSG_START     = null;
   private static MessageHandle LIGHT_MSG_END       = null;

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

      if (LIGHT_MSG_START == null)
      {
         LIGHT_MSG_START     = new MessageHandle ();
         LIGHT_MSG_END       = new MessageHandle ();

         //(o) TODO_parsons Unify ledMsg parsing_start etc for PARSONS and XMELON

         // this messages are not mandatory to be subscribed, the are provided just as internal information of parser command
         Mensaka.declare (null, LIGHT_MSG_START    , "ledMsg parsing_start"      , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_END      , "ledMsg parsing_end"        , logServer.LOG_DEBUG_0);
      }

      String oper = cmd.getArg(0).toUpperCase ();
      //String p3   = cmd.getArg(3);
      //String strResult = "";

      boolean OptSolveVar = ("1".equals (cmd.takeOptionString(new String [] {"SOLVE", "SOLVEVAR", "SOLVELSX", "SOLVELISTIX" }, "1"))) &&
                            ("0".equals (cmd.takeOptionString(new String [] {"ASTEXT" }, "0")));

      if (oper.equals ("COPY"))
      {
         String sourceFile = cmd.getArg(1);
         String targetFile = cmd.getArg(2);
         
         TextFile src = new TextFile();
         if (! src.fopen (sourceFile, "rb"))
         {
            cmd.getLog ().err ("FILEUTIL", "cannot open source file " + sourceFile + "!");
            return 1;
         }

         fileUtil.ensureDirsForFile (targetFile);
         TextFile trg = new TextFile ();         
         if (! trg.fopen (targetFile, "wb"))
         {
            cmd.getLog ().err ("FILEUTIL", "cannot open target file " + targetFile + "!");
            return 1;
         }
         
         Mensaka.sendPacket (LIGHT_MSG_START, null);
         int rr = 0;
         int nread = 0;
         byte [] puffer = new byte [1024];
         do
         {
            Mensaka.sendPacket ((rr % 2) == 0 ? LIGHT_MSG_END:LIGHT_MSG_START, null);
            nread = src.readBytes (puffer);
            // System.out.println (nread + " bytes");
            trg.writeBytes (puffer, nread);
         }
         while (nread > 0 && ! src.feof ());
         
         src.fclose ();
         trg.fclose ();
         Mensaka.sendPacket (LIGHT_MSG_END, null);
      }
      else if (cmd.meantConstantString (oper, new String [] {"ENSUREDIR4FILE", "ENSUREDIRFORFILE"}))
      {
         Mensaka.sendPacket (LIGHT_MSG_START, null);
         fileUtil.ensureDirsForFile (cmd.getArg(1));
         Mensaka.sendPacket (LIGHT_MSG_END, null);
      }
      else 
      {
         cmd.getLog ().err ("FILEUTIL", "syntax [" + oper + "] not recognized!");
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}

