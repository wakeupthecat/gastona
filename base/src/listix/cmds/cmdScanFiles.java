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
   //(o) WelcomeGastona_source_listix_command SCAN

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       SCAN
   <groupInfo>  system_files
   <javaClass>  listix.cmds.cmdScanFiles
   <importance> 5
   <desc>       //For scan directories or zipp files into a database


   <help>
      //
      // Scan a directory or a zip (or jar) file and set the result into a database.
      // Having a directory into a database is convenient for searching and filtering
      // the files in an arbitrary way using sql selects.
      // If the database does not exists then it is created with the following schema
      //
      //       table (scan)_roots : rootID, rootLabel, pathRoot, rootType, timeLastScan
      //       table (scan)_files : rootID, fileID, pathFile, fileName, extension, date, size
      //       view  (scan)_all   : which is a join of the two tables plus the formed fields fullPath,
      //                            fullParentPath and subPath
      //
      // Example of entry in the database:
      //
      //       Scaning the directory c:\myDir gives just one file under subdir\text.txt, then the
      //       fields for the table scan_roots might be
      //
      //          rootID        1
      //          rootLabel    "local"
      //          pathRoot     "c:\myDir"
      //          rootType     "D"
      //          timeLastScan "21-06-2009 21:07"
      //
      //       and for the table scan_files
      //
      //          rootID      1
      //          fileID      1
      //          pathFile    "subdir"
      //          fileName    "text.txt"
      //          extension   "txt"
      //          date        "28-03-2003 09:02"
      //          size        182887
      //
      //       additionaly to all these fields, in the view scan_all we found as well
      //
      //          fullPath         "c:\myDir\subdir\text.txt"
      //          fullParentPath   "c:\myDir\subdir"
      //          subpath          "subdir\text.txt"
      //
      // Pre-filtering the scan
      //
      //    For big directories or just because we are only interesting in some kind of files the
      //    entries in the database might be reduced using multiple pairs of : optFilter, regexp
      //
      //    optFilter might be
      //
      //      +E  the extension matching 'regexp' will be included (excluding all those not included)
      //      -E  the extension matching 'regexp' will be excluded (including all those not excluded)
      //      +D  the relative directory matching 'regexp' will be included (excluding all ...)
      //      -D  the relative directory matching 'regexp' will be excluded (including all ...)
      //      +F  the file matching 'regexp' will be included (excluding all ...)
      //      -F  the file matching 'regexp' will be excluded (including all ...)
      //
      //      Java Regular expresions are accepted as 'textFilter'
      //         Examples:
      //
      //            -E, "obj"      exclude extensions obj
      //            -F, "\.obj$"    exclude extensions obj
      //            -D, "lint"     exclude the directory "/lint/" and its subdirectories
      //            +F, "Proxy",   add the files which name includes "Proxy"
      //

   <aliases>
      alias
      SCAN
      SCAN FILES
      SCAN DIR

   <syntaxHeader>
      synIndx, importance, desc
         1   ,  5       , //Performs a scan of a directory saving the results into a database
         2   ,  2       , //Puts the entries of a zip or jar file into a database
         3   ,  2       , //Removes entries from the database acording some criteria. The deletion might be achieved directly using sql, this option facilitates synchronized deletion in tables "roots" and "files"

   <syntaxParams>
      synIndx, name         , defVal      , desc
      1      , ADD FILES    ,             , //
      1      , sqliteDBName , (default db), //Database name (file name) where to add file information
      1      , pathRoot     ,             , //Root path where the files are to be found
      1      , optFilter    ,             , //Might be +/- for extensions, +D|-D for directories or +F|-F for file names. If specified then the text filter is expected as next parameter
      1      , textFilter   ,             , //Text related with the last 'optFilter' simply strings or java regular expresions are accepted

      2      , ADD ZIP      ,             , //
      2      , sqliteDBName , (default db), //Database name (file name) where to add file information
      2      , zipFile      ,             , //Zip (jar) file name

      3      ,  REMOVE      ,             , //
      3      ,  sqliteDBName, (default db), //Database name (file name) where to add file information


   <options>
      synIndx, optionName  , parameters, defVal, desc

      x      , PREFIX       ,  "tablePrefix" , "scan", //Specifies a table name prefix for the operation, all the tables will have this prefix
      x      , ROOTLABEL    ,  "rootDescription" , "local", //Allows setting a textual description or name for the root path, this will stored in a field with name 'rootLabel'. This option is sepecially useful for CD-ROM labels or Sticks where the root path might be always the same for all (e.g. D:\)
      1      , RECURSIVE    ,   1/0              , "1"    , //If '1' (default) then the seach of files will be recusive, otherwise simple
      1      , FILTER       ,  "option, string"  , ""     , //Option might be +/- E,F or D or </> T, S (see filter options help)
      1      , EXTENSIONS   ,  "string"          , ""     , //comma or space separated list of extensions to admit
      1      , ADD HASH     ,  "algorithm, limitMB", "md5", //Creates a new column with the hash applied to the file content, it can be either md5 or crc32. Additionally a limit 1000 x byte can be given to make a faster hash in case of huge files

      3      , ROOTPATH     ,  "rootPath"    ,         , //Specify the rootPath to be deleted

   <examples>
      gastSample

      scan sample
      scan sample 2
      look at scan schema

   <scan sample>
      //#javaj#
      //
      //   <frames> oConsole, SCAN sample, 600, 400
      //
      //#listix#
      //
      //   <main>
      //      //Scanning "." ..
      //      SCAN, ADD FILES,, .
      //      //done.
      //      //
      //      //LIST OF FIRST 10 FILES:
      //      //
      //      LOOP, SQL,, //SELECT fullPath FROM scan_all LIMIT 10;
      //          ,, // @<fullPath>
      //      //
      //      //MAIN EXTENSIONS:
      //      //
      //      LOOP, SQL,, //SELECT COUNT(*) AS nfiles, extension FROM scan_files GROUP BY extension ORDER BY nfiles DESC LIMIT 10;
      //          ,, // extension [@<extension>] in @<nfiles> file(s)
      //

   <scan sample 2>
      //#javaj#
      //
      //   <frames> Fmain, SCAN sample, 600, 400
      //
      //   <layout of Fmain>
      //      EVA, 10, 10, 7, 7
      //
      //         ,           , X
      //         , lDirectory, eDir, bSearch dir, gLus
      //       X , sFileViewer, -, -, -
      //
      //#data#
      //
      //   <bSearch dir DIALOG> DIR
      //   <bSearch dir image> javaj/img/explorer.png
      //
      //#listix#
      //
      //   <-- bSearch dir>
      //      -->, eDir data!,, @<bSearch dir chosen>
      //      @<do scanDir>
      //
      //   <-- eDir>
      //      @<do scanDir>
      //
      //   <do scanDir>
      //      SCAN, ADD FILES,, @<eDir>
      //       -->, sFileViewer data!, sqlSelect, //@<select this dir>
      //
      //   <select this dir>
      //      //SELECT pathFile, fileName, extension, date, size
      //      //  FROM scan_all
      //      //  WHERE pathRoot = '@<eDir>'
      //      // ;

   <look at scan schema>
      //#javaj#
      //
      //   <frames> oConsola, Look At DB schema of SCAN
      //
      //#listix#
      //
      //   <main0>
      //      SCAN, ADD FILES,, .
      //
      //      GEN,, print schema (db), listix, META-GASTONA/utilApp/std/printSchema.lsx
      //         , PARAMS, ""
      //      GEN,, lookAtDB (db), listix, META-GASTONA/utilApp/std/lookAtDB.lsx
      //         , PARAMS, ""

#**FIN_EVA#
*/

package listix.cmds;

import listix.table.*;

import java.util.zip.*;
import java.io.*;
import java.util.List;
import java.util.Vector;
import listix.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end
import de.elxala.math.hash.*;


/**

         SCAN, CREATE DATABASE, sqliteDBName
         ------------------------------------------------

            Creates (if inexistent) a sqlite DB with name 'sqliteDBName' with following schema

               CREATE TABLE scan_roots (rootID, rootLabel, pathRoot, rootType, timeLastScan);
               CREATE TABLE scan_files (rootID, fileID, pathFile, fileName, extension, date, size [[, md5 or crc]] );
               ...
               CREATE VIEW scan_all ... all fields + 'fullPath' + 'fullParentPath'

            which will hold all files added with the command FILESYSTEMINFO, ADD FILES

         SCAN, ADD FILES, sqliteDBName, pathRoot, dirBase, [ optFilter, textFilter ] ...
         ----------------------------------------------------------------------------------------------


OLD OLD OLD OLD OLD -------------------------------------

            Adds to the files database with name 'sqliteDBName' the files that
            match with the given directories and options

            - rootLabel
               a name of the computer or external medium (i.e. CD name identifier)
               it is not required, if empty the files are suposed to be found in the local host

            - rootDir

               Absolute or relative directory path where the scan of files will be performed

               - add record in 'root' table -
                  each call to FILESYSTEMINFO, ADD FILES will check if there is an entry in root table
                  with same rootLabel and pathRoot,
                     if exists then the files of the table 'files' with the 'rootID' found will be deleted (new rescan of files)
                     if not exists then a new entry in the table 'roots' will be created

               - remove pathRoot from files pathFile -

                  all the files found in this command will have the pathFile without containing
                  the pathRoot (this will be removed)

            - dirBase

               Path relative to 'pathRoot' where the files are to be found

            - optFilter, textFilter

               optFilter might be

               +E  the extension 'textFilter' will be included (excluding, if not in other +E, the rest of extensions)
               -E  the extension 'textFilter' will be excluded (including, if not in other -E, the rest of extensions)
               +D  the relative directory 'textFilter' will be included (excluding ... the rest)
               -D  the relative directory 'textFilter' will be excluded (including ... the rest)
               +F  the file 'textFilter' will be included (excluding ... the rest)
               -F  the file 'textFilter' will be excluded (including ... the rest)

               Java Regular expresions are accepted as 'textFilter'
                  examples


                     -E, "obj"      exclude extensions obj
                     -F, ".obj$"    exclude extensions obj
                     -D, "lint"     exclude the directory "/lint/" and its subdirectories
                     +F, "Proxy",   add the files which name includes "Proxy"

OLD OLD OLD OLD OLD -------------------------------------
*/
public class cmdScanFiles implements commandable
{
   private static String DIR_SEP = "" + File.separatorChar;

   private static String ROOT_TYPE_DIR = "D";
   private static String ROOT_TYPE_ZIP = "Z";
   private static String ROOT_TYPE_URL = "U";

   //private static MessageHandle TX_FATALERROR    = new MessageHandle (); // "ledMsg scanFiles_error"
   private static MessageHandle TX_SCAN_START     = null; // new MessageHandle (); // "ledMsg scanFiles_start"
   private static MessageHandle TX_SCAN_PROGRESS  = null; // new MessageHandle (); // "ledMsg scanFiles_progresss"
   private static MessageHandle TX_SCAN_END       = null; // new MessageHandle (); // "ledMsg scanFiles_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   private static boolean initOnce_msgHandles ()
   {
      if (TX_SCAN_START == null)
      {
         TX_SCAN_START     = new MessageHandle ();
         TX_SCAN_PROGRESS  = new MessageHandle ();
         TX_SCAN_END       = new MessageHandle ();

         // this messages are not mandatory to be subscribed, the are provided just as internal information of parser command
         Mensaka.declare (null, TX_SCAN_START    , "ledMsg scanFiles_start"      , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, TX_SCAN_PROGRESS , "ledMsg scanFiles_progresss"  , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, TX_SCAN_END      , "ledMsg scanFiles_end"        , logServer.LOG_DEBUG_0);
      }
      return true;
   }


   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "SCAN FILES",
          "SCAN DIR",
          "SCAN",
       };
   }

   private String currentPrefix = "scan";

   private String ROOTS_TABLE ()   { return currentPrefix + "_roots"; }
   private String FILES_TABLE ()   { return currentPrefix + "_files"; }
   private String ALL_VIEW ()      { return currentPrefix + "_all"; }

   // to keep the current logger
   private logger theLog = new logger (null, "dummylogger", null);

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
      theLog = cmd.getLog ();

      //      SCAN,  ADD FILES       , DBname, pathRoot, [ optFilter, textFilter ]
      //      SCAN,  REMOVE          , DBname
      //
      String oper      = cmd.getArg(0);
      String dbName    = cmd.getArg(1);
      String pathRoot  = cmd.getArg(2);

      if (dbName.length () == 0)
         dbName = that.getDefaultDBName ();

      boolean optAdd     = cmd.meantConstantString (oper, new String [] { "ADD", "ADDFILES", "FILES" } );
      boolean optRemove  = cmd.meantConstantString (oper, new String [] { "REMOVE", "DEL", "DELETE" } );
      boolean optZip     = cmd.meantConstantString (oper, new String [] { "ADDZIP", "ZIP", "ADDJAR", "JAR" } );

      // collect filter criteria (last parameters)
      //
      fileMultiFilter filtrum = new fileMultiFilter ();
      for (int ii = 3; ii+1 < cmd.getArgSize (); ii += 2)
      {
         String optFilt = cmd.getArg(ii);
         String valFilt = cmd.getArg(ii + 1);

         theLog.dbg (2, "SCAN", "optFilt = " + optFilt + "value = " + valFilt);

         filtrum.addCriteria (optFilt, valFilt);
      }

      // collect options
      //

      //option PREFIX
      //
      currentPrefix = "scan";
      String optStr = cmd.takeOptionString("PREFIX");
      if (optStr.length () > 0)
      {
         currentPrefix = optStr;
         theLog.dbg (2, "SCAN", "option PREFIX = '" + currentPrefix + "'");
      }

      //option ROOTLABEL
      //
      String currentRootLabel = (optRemove) ? "": "local";
      optStr = cmd.takeOptionString("ROOTLABEL");
      if (optStr.length () > 0)
      {
         currentRootLabel = optStr;
         theLog.dbg (2, "SCAN", "option ROOTLABEL = '" + currentRootLabel + "'");
      }

      //option ROOTLABEL
      //
      String [] optHash = cmd.takeOptionParameters(new String [] { "HASH", "ADDHASH" });
      if (optHash != null && optHash.length > 0)
      {
         theLog.dbg (2, "SCAN", "option HASH = '" + optHash[0] + "'" + (optHash.length > 1 ? (" limit " + optHash[1] + " x1000 bytes"): ""));
      }

      boolean currentIsRecursive = true;
      if (optAdd)
      {
         //option RECURSIVE
         //
         currentIsRecursive = -1 != "1yYSs".indexOf (cmd.takeOptionString(new String [] { "RECURSIVE", "RECURSE", "REC" }, "1").substring(0,1));

         //option FILTERS
         //
         String [] optArr = cmd.takeOptionParameters("FILTERS");
         if (optArr != null)
         {
            for (int ff = 0; ff+1 < optArr.length; ff += 2)
            {
               filtrum.addCriteria (optArr[ff], optArr[ff + 1]);
            }
         }


         String [] extensionStr = cmd.takeOptionParameters("EXTENSIONS");
         if (extensionStr != null)
         {
            if (extensionStr.length != 1)
               theLog.err ("SCAN", "option EXTENSIONS accept only one parameter (string)");
            else
            {
               String [] extArr = extensionStr[0].split ("[\\s,;:\\.]"); // space, comma, semicolon, colon or point
               for (int ii = 0; ii < extArr.length; ii ++)
               {
                  filtrum.addCriteria ("+", extArr[ii]);
               }
            }
          }
      }

      if (optRemove)
      {
         String rootPath2Delete = cmd.takeOptionString("ROOTPATH");
         doRemove (dbName, currentRootLabel, rootPath2Delete);
         cmd.checkRemainingOptions ();
         return 1;
      }

      if (! (optAdd || optZip))
      {
         theLog.err ("SCAN", "operation \"" + oper + "\" not defined!");
         return 1;
      }

      createSchema (dbName, optHash);
      // preparation root ID
      //

      String rootType = "X";

      if (optAdd) rootType = ROOT_TYPE_DIR;
      if (optZip) rootType = ROOT_TYPE_ZIP;

      int rootID = storeRootID (dbName, currentRootLabel, pathRoot, rootType);
      long fileID = sqlUtil.getNextID(dbName, FILES_TABLE (), "fileID", 1000);

      sqlSolver myDB = new sqlSolver ();

      // CLEAN OLD SCAN FOR THE rootID
      //
      theLog.dbg (2, "SCAN", "clearing files for rootID  " + rootID);
      myDB.openScript ();
      myDB.writeScript ("DELETE FROM " + FILES_TABLE () + " WHERE rootID+0 = " +  rootID + " ;");
      myDB.closeScript ();
      myDB.runSQL (dbName);


      if (! entriesScanStart (pathRoot, optZip, currentIsRecursive, filtrum))
      {
         theLog.dbg (2, "SCAN", "no entries or previous error scanning \"" + pathRoot + "\"");
         return 1;
      }
      // pathGetFiles moto = new pathGetFiles();
      // moto.initScan (pathRoot, "", currentIsRecursive, filtrum);


      // scan & save
      //
      myDB.openScript ();

      if (sendMessages) Mensaka.sendPacket (TX_SCAN_START, null); // ... )))

      // String [] ristra = new String [] { dirSolo, nameSolo, extension, dateStr, "" + farray[indxToca].length () };
      List cosas = null;
      String algo = (optHash != null && optHash.length > 0) ? optHash[0]: null;
      int hashLimMB = (optHash != null && optHash.length > 1) ? stdlib.atoi(optHash[1]): 0;
      do
      {
         // cosas = moto.scanN (100);
         cosas = entriesScanNext (500);
         theLog.dbg (2, "SCAN", "adding next " + cosas.size () + " entries");
         for (int jj = 0; jj < cosas.size (); jj++)
         {
            String [] record = (String []) cosas.get (jj);
            String hashstr = null;

            if (algo != null)
            {
               // fullpath is record[5]
               if (algo.equalsIgnoreCase ("md5"))
                  hashstr = hashos.md5 (record[5], hashLimMB);
               else if (algo.equalsIgnoreCase ("crc") ||
                        algo.equalsIgnoreCase ("crc32"))
                  hashstr = hashos.crc32 (record[5], hashLimMB);
            }

            String values = rootID + ", " + (fileID ++) + ", ";
            values += "'" + myDB.escapeString(record[0]) + "', '" +
                      myDB.escapeString(record[1]) + "', '" +
                      myDB.escapeString(record[2]) + "', '" +
                      myDB.escapeString(record[3]) + "', " +
                      myDB.escapeString(record[4]) +
                      (hashstr == null ? "": ", '" + myDB.escapeString(hashstr) + "'");  // probably it does not need escapeString ...

            myDB.writeScript ("INSERT INTO " + FILES_TABLE () + " VALUES (" + values + ") ;");
         }
         if (sendMessages) Mensaka.sendPacket (TX_SCAN_PROGRESS, null); // ... )))
      }
      while (cosas.size () > 0);

      // send message here or after myDB.dispose () is a matter of taste
      if (sendMessages) Mensaka.sendPacket (TX_SCAN_END, null); // ... )))

      entriesScanEnd ();
      theLog.dbg (2, "SCAN", "scan completed storing in db [" + dbName + "]");
      myDB.closeScript ();
      myDB.runSQL (dbName);

      theLog.dbg (2, "SCAN", "scan done.");
      cmd.checkRemainingOptions ();
      return 1;
   }


   // create a database
   //
   private void createSchema (String dbName, String [] optHash)
   {
      sqlSolver myDB = new sqlSolver ();

      //(o) listix_sql_schemas SCAN schema creation

      myDB.openScript ();
      myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + ROOTS_TABLE () +
                        " (rootID int, rootLabel text, pathRoot text, rootType text, timeLastScan text, UNIQUE(rootID));");
      myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + FILES_TABLE () +
                        " (rootID int, fileID int, pathFile text, fileName text, extension text, date text, size int, " +
                        ((optHash != null && optHash.length > 0) ? optHash[0] + ", " : "") +
                        " UNIQUE(rootID, fileID));");

      // o-o  Add deepSql connections info
      myDB.writeScript (deepSqlUtil.getSQL_CreateTableConnections ());
      myDB.writeScript (deepSqlUtil.getSQL_InsertConnection("root", FILES_TABLE (), "rootID", ROOTS_TABLE (), "rootID"));

      //Note : pathRoot is supossed not to be "", at least has to be "."
      String fieldFullPath       = "(pathRoot || SUBSTR('" + DIR_SEP + "', 1, MIN(1,MAX(LENGTH(pathFile),0))) || pathFile || '" + DIR_SEP + "' || fileName) AS fullPath";
      String fieldFullParentPath = "(pathRoot || SUBSTR('" + DIR_SEP + "', 1, MIN(1,MAX(LENGTH(pathFile),0))) || pathFile) AS fullParentPath";
      String fieldSubPath        = "(pathFile || SUBSTR('" + DIR_SEP + "', 1, MIN(1,MAX(LENGTH(pathFile),0))) || fileName) AS subPath";

      //12.10.2008 16:48
      // ALTHOUGH it is described in SQLITE DOCUMENTATION the option "CREATE VIEW IF NOT EXISTS" DOES NOT WORK !!!!!
      //
      //myDB.writeScript ("CREATE VIEW IF NOT EXISTS " + ALL_VIEW () + " AS SELECT *, " + fieldFullPath + " FROM " + ROOTS_TABLE () + " LEFT JOIN " + FILES_TABLE () + " USING (rootID) ;");
      myDB.writeScript ("DROP VIEW IF EXISTS " + ALL_VIEW () + ";");
      myDB.writeScript ("CREATE VIEW " + ALL_VIEW () + " AS SELECT *, " + fieldFullPath + ", " + fieldFullParentPath + ", " + fieldSubPath + " FROM " + ROOTS_TABLE () + " LEFT JOIN " + FILES_TABLE () + " USING (rootID) ;");

      myDB.closeScript ();
      myDB.runSQL (dbName);
   }

   // create a database
   //
   private void doRemove (String dbName, String rootLabel, String rootPath)
   {
      sqlSolver myDB = new sqlSolver ();

      myDB.openScript ();

      if (rootLabel.length () == 0 && rootPath.length () == 0)
      {
         // remove the whole PREFIX
         //
         myDB.writeScript ("DROP VIEW IF EXISTS " + ALL_VIEW () + ";");
         myDB.writeScript ("DROP TABLE IF EXISTS " + ROOTS_TABLE () + ";");
         myDB.writeScript ("DROP TABLE IF EXISTS " + FILES_TABLE () + ";");
      }
      else
      {
         // given host and path => delete only the path 'path' of this host
         //
         String thewhere = "";
         if (rootLabel.length () > 0)
         {
            thewhere = "rootLabel = '" + myDB.escapeString(rootLabel) + "'";
         }
         if (rootPath.length () > 0)
         {
            if (thewhere.length () > 0) thewhere += " AND ";
            thewhere += "pathRoot = '" + myDB.escapeString(rootPath) + "'";
         }

         myDB.writeScript ("DELETE FROM " + FILES_TABLE () + " WHERE rootID IN (SELECT rootID FROM " + ROOTS_TABLE () + " WHERE " + thewhere + ");");
         myDB.writeScript ("DELETE FROM " + ROOTS_TABLE () + " WHERE " + thewhere + ";");
      }

      myDB.closeScript ();
      myDB.runSQL (dbName);
   }

   private int storeRootID (String dbName, String rootLabel, String pathRoot, String rootType)
   {
      // (rootId, rootLabel, pathRoot, timeLastScan)

      tableROSelect taRO = new tableROSelect (dbName, null);
      taRO.setSelectQuery (dbName, "SELECT * FROM " + ROOTS_TABLE () + " WHERE rootLabel = '" + taRO.escapeString (rootLabel) + "' AND pathRoot = '" + taRO.escapeString (pathRoot) + "' LIMIT 1;");

      int lastID = 1000;
      if (taRO.getRecordCount () == 0)
      {
         // does not exist, create it

         // get the last ID and increment it
         taRO.setSelectQuery ("SELECT rootID FROM " + ROOTS_TABLE () + " ORDER BY rootID DESC LIMIT 1");
         if (taRO.getRecordCount () > 0)
            lastID = stdlib.atoi (taRO.getValue ("rootID", 0));
         lastID ++;

         // create new record for the root
         //
         sqlSolver myDB = new sqlSolver ();
         myDB.openScript ();
         myDB.writeScript ("INSERT INTO " + ROOTS_TABLE () + " VALUES (" + lastID + ", '" + myDB.escapeString (rootLabel) + "', '" + myDB.escapeString (pathRoot) + "', '"  + rootType + "', '" + myDB.escapeString (DateFormat.getTodayStr ()) + "' ) ;");
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

   // Abstract files /Zip entries scanner
   //
   private pathGetFiles filesGetter = null;       // for scanning directory entries
   private ZipInputStream zipInpStream = null;     // for SCANNING zipEntries not all in one loop

   private boolean entriesScanStart (String rootPath, boolean isZip, boolean currentIsRecursive, fileMultiFilter filtrum)
   {
      filesGetter = null;
      zipInpStream = null;
      if (isZip)
      {
         boolean ok = true;

         theLog.dbg (2, "SCAN", "open zip file [" + rootPath + "] for reading entries");

         try
         {
            zipInpStream = new ZipInputStream(new FileInputStream(rootPath));
         }
         catch (Exception e)
         {
            ok = false;
            theLog.severe ("SCAN", "opening ZipInputStream [" + rootPath + "]" + e);
         }

         return ok;
      }

      theLog.dbg (2, "SCAN", "scan directory entries of [" + rootPath + "] for reading entries");
      filesGetter = new pathGetFiles();
      filesGetter.initScan (rootPath, "", currentIsRecursive, filtrum);
      return true;
   }

   private List entriesScanNext (int amount)
   {
      if (filesGetter != null)
      {
         theLog.dbg (2, "SCAN", "request next " + amount + " entries");
         return filesGetter.scanN (amount);
      }

      if (zipInpStream != null)
      {
         List myList = new Vector();
         try
         {
            // red zip entries
            ZipEntry entry;
            while (amount > 0 && (entry = zipInpStream.getNextEntry()) != null)
            {
               if (! entry.isDirectory ())
               {
                  amount --;
                  String [] ent = new String []
                                 {
                                    fileUtil.getParent (entry.getName()),
                                    fileUtil.getJustNameAndExtension (entry.getName()),
                                    fileUtil.getExtension (entry.getName()),
                                    "",
                                    "" + entry.getSize ()
                                 };

                  theLog.dbg (2, "SCAN", "read next zip entry [" + entry.getName() + "]");
                  myList.add (ent);
               }
            }
         }
         catch(Exception e)
         {
            theLog.severe ("SCAN", "reading zip entries" + e);
         }
         return myList;
      }

      theLog.err ("SCAN", "entriesScanNext called but filesGetter == null and  zipInpStream == null!");
      return new Vector ();
   }

   private void entriesScanEnd ()
   {
      if (zipInpStream != null)
      {
         try
         {
            zipInpStream.close();
         }
         catch (Exception e)
         {
            theLog.severe ("SCAN", "closing ZipInputStream " + e);
         }
         zipInpStream = null;
      }
   }
}
