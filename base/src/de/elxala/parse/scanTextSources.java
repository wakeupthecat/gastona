/*
library listix (www.listix.org)
Copyright (C) 2022-2026 Alejandro Xalabarder Aulet

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
   generalitzar les fonts de parseix : variable, fitxer o directori
*/

package de.elxala.parse;

import java.util.*;
import java.io.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end

/**
   Scans (source) either :
         all files of a folder
         a file
         a text contained in a variable
      or a string

   thought to be used by listix commands

   Example of use:

      scanTextSources pso = new scanTextSources ("parsedFiles");
      if (pso.openContentSource (cmd, "PARSONS", "DIR", ".."))
      {
         int fileID = 1000;

         // fetch the first file in case sourceFolder ...
         openNextSrcFile (that, myDB, fileID);
         do
         {
            String lineStr = "";
            while ((lineStr = getNextLineContentSource ()) != null)
            {
               // treat lineStr
            }
         }
         closeContentSource ();

      } while (openNextSrcFile (myDB, ++ fileID));


*/
public class scanTextSources
{
   protected TextFile             sourceFitxer = null;
   protected Eva                  sourceEvavar = null;
   protected tableAccessPathFiles sourceFolder = null;
   protected int                  sourceCurrentLine = -1;
   protected listixCmdStruct      objCmd = null;
   protected String               commandName = null;
   protected int filesInserted = 0;

   protected String FILES_TABLENAME = null;


   public scanTextSources (String filesTableName)
   {
      FILES_TABLENAME = filesTableName;
   }

   public String getOperName ()
   {
      return objCmd == null ? "unkown": objCmd.getArg (0);
   }

   public String getSourceName ()
   {
      return objCmd == null ? "unkown": objCmd.getArg (1);
   }

   public String getSourceNoFolder (sqlSolver myDB)
   {
      return sourceFitxer != null ? sourceFitxer.getFileName ():
             sourceEvavar != null ? (":var " + myDB.escapeString (sourceEvavar.getName ())):
             sourceFolder != null ? null: "?source";
   }

   // detects the operation given by "oper"
   //   FOLDER
   //   FILE2DB
   //   EVAVAR
   //   STR
   //
   public boolean openContentSource (listixCmdStruct cmd, String cmdName)
   {
      objCmd = cmd;
      String oper =  getOperName ();
      String scrName = getSourceName ();

      commandName = cmdName;

      sourceFitxer = null;
      sourceEvavar = null;
      sourceFolder = null;
      sourceCurrentLine = 0;
      filesInserted = 0;

      if (listixCmdStruct.meantConstantString (oper, new String [] { "FILE", "FILE2DB" }))
      {
         sourceFitxer = new TextFile ();
         if (!sourceFitxer.fopen (scrName, "r"))
         {
            objCmd.getListix ().log ().err (commandName, "File [" + scrName + "]" + " cannot be opened!");
            sourceCurrentLine = -1;
            sourceFitxer = null;
         }
      }
      else if (listixCmdStruct.meantConstantString (oper, new String [] { "VAR", "EVAVAR", "EVA" }))
      {
         sourceEvavar = objCmd.getListix ().getVarEva (scrName);
         if (sourceEvavar == null)
         {
            objCmd.getListix ().log ().err (commandName, "Variable [" + scrName + "]" + " not found!");
            sourceCurrentLine = -1;
         }
      }
      else if (listixCmdStruct.meantConstantString (oper, new String [] { "FOLDER", "DIR", "FOLDER2DB", "DIR2DB" }))
      {
         // ... , FOLDER, root, extensions, recursive
         //
         String  rootDir    = objCmd.getArg (1); // same as srcName
         String  extensions = objCmd.getArg (2);
         boolean recursive  = objCmd.getArg (3).length() == 0 ? true: (objCmd.getArg (3).charAt(0) == 1);

         sourceFolder = new tableAccessPathFiles ();
         sourceFolder.setCommand (objCmd, rootDir, extensions, recursive);
      }
      else if (listixCmdStruct.meantConstantString (oper, new String [] { "STR", "STRING", "TEXT" }))
      {
         sourceEvavar = new Eva ("intern.string"); // name does not matter!
         sourceEvavar.setValue (scrName, 0, 0);
      }
      return true;
   }

   public boolean openNextSrcFile (sqlSolver myDB, long fileId)
   {
      boolean validfileOpened = false;
      if (sourceFolder == null) return false;

      while (! sourceFolder.EOT ())
      {
         if (canOpenNextSrcFile (myDB, fileId))
         {
            validfileOpened = true;
            break;
         }
      }
      return validfileOpened;
   }

   protected boolean canOpenNextSrcFile (sqlSolver myDB, long fileId)
   {
      if (sourceFolder == null) return false;
      if (sourceFolder.EOT ()) return false;

      // get file and from file scanner
      String scrName = sourceFolder.getValue (sourceFolder.currRow, sourceFolder.colOf ("fullPath"));
      sourceFolder.incrementRow ();

      // fopen this file
      //
      if (sourceFitxer != null)
      {
         sourceFitxer.fclose ();
      }
      sourceFitxer = new TextFile ();
      if (!sourceFitxer.fopen (scrName, "r"))
      {
         objCmd.getListix ().log ().err (commandName, "File to scan [" + scrName + "]" + " cannot be opened!");
         sourceFitxer = null;
         sourceCurrentLine = -1;
         return false;
      }

      if (filesInserted ++ == 0)
      {
         myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + FILES_TABLENAME + " (fileID int, timeParse, fullPath, fileName, fileDate, extension, lastExtension, size, UNIQUE(fileID));");
      }

      File fi = fileUtil.getFileStruct (scrName);

      // insert it into files table
      //
      myDB.writeScript ("INSERT INTO " + FILES_TABLENAME + " VALUES (" +
                                fileId + ", '" +
                                myDB.escapeString (DateFormat.getTodayStr ()) + "', '" +
                                myDB.escapeString (scrName) +
                                myDB.escapeString (fileUtil.getJustNameAndExtension (scrName)) +
                                myDB.escapeString (fi.isFile () ? DateFormat.getStr (new Date(fi.lastModified ())): "") +
                                myDB.escapeString (fileUtil.getFullExtension (scrName)) +
                                myDB.escapeString (fileUtil.getLastExtension (scrName)) +
                                (fi.isFile () ? fi.length (): -1) +
                                "');");
      return true;
   }

   public String getNextLineContentSource ()
   {
      if (sourceFitxer != null)
      {
         if (sourceFitxer.readLine ())
            return sourceFitxer.TheLine ();
         return null;
      }
      if (sourceEvavar != null)
      {
         if (sourceCurrentLine < sourceEvavar.rows ())
            return sourceEvavar.getValue (sourceCurrentLine ++, 0);
         return null;
      }
      return null;
   }

   public void closeContentSource ()
   {
      if (sourceFitxer != null)
      {
         sourceFitxer.fclose ();
      }
   }
}
