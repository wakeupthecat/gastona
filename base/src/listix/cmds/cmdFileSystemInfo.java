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
//(o) TODO_listix_cmds DEPRECATE command FILESYS
   //( o ) WelcomeGastona_source_listix_command FILESYS

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#UNDOCUMENTED! gastonaDoc#

   <docType>    listix_command
   <name>       FILESYS
   <groupInfo>  system_files
   <javaClass>  listix.cmds.cmdFileSystemInfo
   <importance> 5
   <desc>       //A very interesting command

   <aliases>
      alias
      FILE SYSTEM INFO
      FILE SYSTEM

   <syntaxHeader>
      synIndx, importance, desc
         1   ,  5       , //Creates a sqlite DB with name 'sqliteDBName' with a schema to hold all roots and files
         2   ,  5       , //Adds to the files database with name 'sqliteDBName' the files that matches the option filters
         3   ,  2       , //Removes file entries from the database 'sqliteDBName' that matches the option filters

   <syntaxParams>
      synIndx, name         , defVal      , desc
      1      , CREATE       ,             , //
      1      , sqliteDBName , (default db), //Database name (file name) where to create the needed tables and views to hold the file information

      2      , ADD FILES    ,             , //
      2      , sqliteDBName , (default db), //Database name (file name) where to add file information
      2      , host         ,             , //A name to group the root path 'pathRoot', might be used the computer name, a CD-ROM identifier etc
      2      , pathRoot     ,             , //Root path where the files are to be found
      2      , R / S        ,   S         , //If specified R then the seach of file will be recusive, otherwise S = simple
      2      , optFilter    ,             , //Might be +/- for extensions, +D|-D for directories or +F|-F for file names. If specified then the text filter is spected as next parameter
      2      , textFilter   ,             , //Text related with the last 'optFilter' simply strings or java regular expresions are accepted

      3      ,  REMOVE      ,             , //
      3      ,  sqliteDBName, (default db), //Database name (file name) where to add file information
      3      ,  host / *    ,             , //Specify a host or * for all hosts
      3      ,  pathRoot / *,             , //Specify a root path or * for all root paths

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      desc


#**FIN_EVA#
*/

package listix.cmds;

import listix.table.*;

import java.util.List;
import listix.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.db.sqlite.*;


/**

         FILESYSTEMINFO, CREATE DATABASE, sqliteDBName
         ------------------------------------------------

            Creates a sqlite DB with name 'sqliteDBName' with following schema

               CREATE TABLE fsi_roots (rootID, hostLabel, pathRoot, timeLastScan);
               CREATE TABLE fsi_files (rootID, fileID, pathFile, fileName, extension, date, size);
               CREATE VIEW  fsi_rootAndFiles AS SELECT * FROM fsi_roots LEFT JOIN fsi_files USING (rootID) ;

            which will hold all files added with the command FILESYSTEMINFO, ADD FILES

         FILESYSTEMINFO, ADD FILES, sqliteDBName, host, pathRoot, dirBase, R, [ optFilter, textFilter ] ...
         ----------------------------------------------------------------------------------------------

            Adds to the files database with name 'sqliteDBName' the files that
            match with the given directories and options

            - hostLabel
               a name of the computer or external medium (i.e. CD name identifier)
               it is not required, if empty the files are suposed to be found in the local host

            - rootDir

               Absolute or relative directory path where the scan of files will be performed

               - add record in 'root' table -
                  each call to FILESYSTEMINFO, ADD FILES will check if there is an entry in root table
                  with same hostLabel and pathRoot,
                     if exists then the files of the table 'files' with the 'rootID' found will be deleted (new rescan of files)
                     if not exists then a new entry in the table 'roots' will be created

               - remove pathRoot from files pathFile -

                  all the files found in this command will have the pathFile without containing
                  the pathRoot (this will be removed)

            - dirBase

               Path relative to 'pathRoot' where the files are to be found

            - R
               'R' for recursive scaning and any other to not recursive

            - optFilter, textFilter

               optFilter might be

               +   the extension 'textFilter' will be included (all extensions do not added with + then will not be included)
               -   the extension 'textFilter' will be excluded (all extensions do not excluded with - then will be included)
               +D  the relative directory 'textFilter' will be included (all directories do not added with +D ...)
               -D  the relative directory 'textFilter' will be excluded (all directories do not excluded with -D ...)
               +F  the file 'textFilter' will be included (all files do not added with +F ...)
               -F  the file 'textFilter' will be excluded (all files do not excluded with -F ...)

               Java Regular expresions are accepted as 'textFilter'
                  examples


                     - , "obj"      exclude extensions obj
                     -F, ".obj$"    exclude extensions obj
                     -D, "lint"     exclude the directory "/lint/" and its subdirectories
                     +F, "Proxy",   add the files which name includes "Proxy"
*/
public class cmdFileSystemInfo implements commandable
{
   private static String DIR_SEP = System.getProperty("file.separator", "/");

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "FILESYSTEMINFO",
          "FILESYSTEM",
          "FILESYS",
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

      that.log().warn ("FILESYS", "FILESYSTEM command is DEPRECATED, PLEASE REPLACE IT WITH SCANFILES COMMAND!");
      //      FILESYS,  CREATE DATABASE , DBname
      //      FILESYS,  ADD             , DBname, host,  pathRoot, R, [ optFilter, textFilter ]
      //      FILESYS,  REMOVE          , DBname, [ host,  [ pathRoot ] ]
      //
      String oper      = that.solveStrAsString (commands.getValue (indxComm, 1));
      String dbName    = that.solveStrAsString (commands.getValue (indxComm, 2));
      String hostName  = that.solveStrAsString (commands.getValue (indxComm, 3));
      String pathRoot  = that.solveStrAsString (commands.getValue (indxComm, 4));
      String recursive = that.solveStrAsString (commands.getValue (indxComm, 5));

      if (dbName.length () == 0)
         dbName = that.getDefaultDBName ();

      boolean optCreate  = cmd.meantConstantString (oper, new String [] { "CREATE", "CREATEDB", "CREATEDATABASE", "CREATESCHEMA" } );
      boolean optAdd     = cmd.meantConstantString (oper, new String [] { "ADD", "ADDFILES" } );
      boolean optRemove  = cmd.meantConstantString (oper, new String [] { "REMOVE", "DEL", "DELETE" } );
      boolean isRecursive  = recursive.equalsIgnoreCase ("R");

      if (optCreate)
      {
         createSchema (dbName);
         cmd.checkRemainingOptions (true);
         return 1;
      }

      if (optRemove)
      {
         doRemove (dbName, hostName, pathRoot);
         cmd.checkRemainingOptions (true);
         return 1;
      }

      if (! optAdd)
      {
         that.log().err ("FILESYS", "operation \"" + oper + "\" not defined!");
         cmd.checkRemainingOptions (true);
         return 1;
      }

      // collect filter criteria
      //
      fileMultiFilter filtrum = new fileMultiFilter ();
      for (int ii = 6; ii < commands.cols(indxComm); ii += 2)
      {
         String optFilt = that.solveStrAsString (commands.getValue (indxComm, ii));
         String valFilt = that.solveStrAsString (commands.getValue (indxComm, ii + 1));

         //System.out.println ("optFilt = " + optFilt + "value = " + valFilt);

         filtrum.addCriteria (optFilt, valFilt);
      }

      // preparation root ID
      //

      int rootID = storeRootID (dbName, hostName, pathRoot);
      int fileID = getFirstFileID (dbName);

      sqlSolver myDB = new sqlSolver ();

      myDB.openScript ();
      myDB.writeScript ("DELETE FROM fsi_files WHERE rootID+0 = " +  rootID + " ;");
      myDB.closeScript ();
      myDB.runSQL (dbName);

      // scan & save
      //
      myDB.openScript ();

      pathGetFiles moto = new pathGetFiles();
      moto.initScan (pathRoot, "", isRecursive, filtrum);

      // String [] ristra = new String [] { dirSolo, nameSolo, extension, dateStr, "" + farray[indxToca].length () };
      List cosas = null;
      do
      {
         cosas = moto.scanN (100);
         for (int jj = 0; jj < cosas.size (); jj++)
         {
            String [] record = (String []) cosas.get (jj);

            String values = rootID + ", " + (fileID ++) + ", ";
            values += "'" + myDB.escapeString(record[0]) + "', '" + myDB.escapeString(record[1]) + "', '" + myDB.escapeString(record[2]) + "', '" + myDB.escapeString(record[3]) + "', " + myDB.escapeString(record[4]);

            myDB.writeScript ("INSERT INTO fsi_files VALUES (" + values + ") ;");
         }
      }
      while (cosas.size () > 0);

      myDB.closeScript ();
      myDB.runSQL (dbName);

      cmd.checkRemainingOptions (true);
      return 1;
   }


   // create a database
   //
   private void createSchema (String dbName)
   {
      sqlSolver myDB = new sqlSolver ();

      myDB.openScript ();
      myDB.writeScript ("CREATE TABLE fsi_roots (rootID, hostLabel, pathRoot, timeLastScan);");
      myDB.writeScript ("CREATE TABLE fsi_files (rootID, fileID, pathFile, fileName, extension, date, size);");

      String fieldFullPath = "(pathRoot || SUBSTR('" + DIR_SEP + "', 1, MIN(1,MAX(LENGTH(pathFile),0))) || pathFile || '" + DIR_SEP + "' || fileName) AS fullPath";

      myDB.writeScript ("CREATE VIEW  fsi_rootAndFiles AS SELECT *, " + fieldFullPath + " FROM fsi_roots LEFT JOIN fsi_files USING (rootID) ;");

      myDB.closeScript ();
      myDB.runSQL (dbName);
   }

   // create a database
   //
   private void doRemove (String dbName, String host, String path)
   {
      sqlSolver myDB = new sqlSolver ();

      myDB.openScript ();

      if (host.equals ("*"))
      {
         // remove the whole schema
         //
         myDB.writeScript ("DELETE FROM fsi_roots;");
         myDB.writeScript ("DELETE FROM fsi_files;");
      }
      else if (path.equals ("*"))
      {
         // delete all paths in this host
         //
         myDB.writeScript ("DELETE FROM fsi_files WHERE rootID IN (SELECT rootID FROM fsi_roots WHERE hostLabel = '" + host + "');");
         myDB.writeScript ("DELETE FROM fsi_roots WHERE hostLabel = '" + myDB.escapeString(host) + "';");
      }
      else
      {
         // given host and path => delete only the path 'path' of this host
         //
         String thewhere = "hostLabel = '" + myDB.escapeString(host) + "' AND pathRoot = '" + myDB.escapeString(path) + "'";
         myDB.writeScript ("DELETE FROM fsi_files WHERE rootID IN (SELECT rootID FROM fsi_roots WHERE " + thewhere + ");");
         myDB.writeScript ("DELETE FROM fsi_roots WHERE " + thewhere + ";");
      }

      myDB.closeScript ();
      myDB.runSQL (dbName);
   }

   private int storeRootID (String dbName, String hostName, String pathRoot)
   {
      // (rootId, hostLabel, pathRoot, timeLastScan)

      tableROSelect taRO = new tableROSelect (dbName, null);
      taRO.setSelectQuery (dbName, "SELECT * FROM fsi_roots WHERE hostLabel = '" + taRO.escapeString (hostName) + "' AND pathRoot = '" + taRO.escapeString (pathRoot) + "' LIMIT 1;");

      int lastID = 1000;
      if (taRO.getRecordCount () == 0)
      {
         // does not exist, create it

         // get the last ID and increment it
         taRO.setSelectQuery ("SELECT rootID FROM fsi_roots ORDER BY rootID DESC LIMIT 1");
         if (taRO.getRecordCount () > 0)
            lastID = stdlib.atoi (taRO.getValue ("rootID", 0));
         lastID ++;

         // create new record for the root
         //
         sqlSolver myDB = new sqlSolver ();
         myDB.openScript ();
         myDB.writeScript ("INSERT INTO fsi_roots VALUES (" + lastID + ", '" + myDB.escapeString (hostName) + "', '" + myDB.escapeString (pathRoot) + "', '" + myDB.escapeString (DateFormat.getTodayStr ()) + "' ) ;");
         myDB.closeScript ();
         myDB.runSQL (dbName);
      }
      else
      {
         // get the ID
         lastID = stdlib.atoi (taRO.getValue ("rootID", 0));
      }

      taRO.dispose ();
      return lastID;
   }

   private int getFirstFileID (String dbName)
   {
      // (rootId, hostLabel, pathRoot, timeLastScan)

      tableROSelect taRO = new tableROSelect (dbName, "SELECT fileID FROM fsi_files ORDER BY fileID DESC LIMIT 1");
      int lastID = (taRO.getRecordCount () == 0) ? 1000: (1 + stdlib.atoi (taRO.getValue ("fileID", 0)));
      taRO.dispose ();

      return lastID;
   }
}
