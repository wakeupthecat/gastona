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
   <groupInfo>  system_process
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
      //    entries in the database might be reduced using multiple pairs of : optFilter, textFilter
      //
      //    optFilter might be
      //
      //      +E  the extension 'textFilter' will be included (excluding all those not included)
      //      -E  the extension 'textFilter' will be excluded (including all those not excluded)
      //      +D  the relative directory 'textFilter' will be included (excluding all ...)
      //      -D  the relative directory 'textFilter' will be excluded (including all ...)
      //      +F  the file 'textFilter' will be included (excluding all ...)
      //      -F  the file 'textFilter' will be excluded (including all ...)
      //
      //      Java Regular expresions are accepted as 'textFilter'
      //         Examples:
      //
      //            -E, "obj"      exclude extensions obj
      //            -F, ".obj$"    exclude extensions obj
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
         3   ,  2       , //Puts the entries of a zip or jar file into a database
         2   ,  2       , //Removes entries from the database acording some criteria. The deletion might be achieved directly using sql, this option facilitates synchronized deletion in tables "roots" and "files"

   <syntaxParams>
      synIndx, name         , defVal      , desc
      1      , ADD FILES    ,             , //
      1      , sqliteDBName , (default db), //Database name (file name) where to add file information
      1      , pathRoot     ,             , //Root path where the files are to be found
      1      , optFilter    ,             , //Might be +/- for extensions, +D|-D for directories or +F|-F for file names. If specified then the text filter is spected as next parameter
      1      , textFilter   ,             , //Text related with the last 'optFilter' simply strings or java regular expresions are accepted

      2      , ADD ZIP      ,             , //
      2      , sqliteDBName , (default db), //Database name (file name) where to add file information
      2      , zipFile      ,             , //Zip (jar) file name

      3      ,  REMOVE      ,             , //
      3      ,  sqliteDBName, (default db), //Database name (file name) where to add file information


   <options>
      synIndx, optionName  , parameters, defVal, desc

      1      , PREFIX       ,  "tablePrefix" , "scan", //Specifies a table name prefix for the operation, all the tables will have this prefix
      1      , ROOTLABEL    ,  "rootDescription" , "local", //Allows setting a textual description or name for the root path, this will stored in a field with name 'rootLabel'. This option is sepecially useful for CD-ROM labels or Sticks where the root path might be always the same for all (e.g. D:\)
      1      , RECURSIVE    ,   Y/N              , "Y"    , //If 'Y' (default) then the seach of files will be recusive, otherwise simple
      1      , FILTER       ,  "option, string"  , ""     , //Option might be +/- E,F or D or </> T, S (see filter options help)

      2      , PREFIX       ,  "tablePrefix" , "scan", //Specifies a table name prefix for the operation, all the tables will have this prefix
      2      , ROOTLABEL    ,  "rootDescription" , "local", //Allows setting a textual description or name for the root path, this will stored in a field with name 'rootLabel'. This option is sepecially useful for CD-ROM labels or Sticks where the root path might be always the same for all (e.g. D:\)

      3      , PREFIX       ,  "tablePrefix" , "scan"  , //Specify a prefix for the deletion, if it is the only option given then all tables with this prefix will be deleted (dropped)
      3      , ROOTLABEL    ,  "rootLabel"   ,         , //Specify the rootlabel to be deleted or that that contain the paths to be deleted
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
      //      // @<fullPath>
      //      =,,
      //      //
      //      //MAIN EXTENSIONS:
      //      //
      //      LOOP, SQL,, //SELECT COUNT(*) AS nfiles, extension FROM scan_files GROUP BY extension ORDER BY nfiles DESC LIMIT 10;
      //      // extension [@<extension>] in @<nfiles> file(s)
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
      //      -->, eDir,, @<bSearch dir chosen>
      //      @<do scanDir>
      //
      //   <-- eDir>
      //      @<do scanDir>
      //
      //   <do scanDir>
      //      SCAN, ADD FILES,, @<eDir>
      //       -->, sFileViewer, sqlSelect, //@<select this dir>
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


/**

         SCAN, CREATE DATABASE, sqliteDBName
         ------------------------------------------------

            Creates (if inexistent) a sqlite DB with name 'sqliteDBName' with following schema

               CREATE TABLE scan_roots (rootID, rootLabel, pathRoot, rootType, timeLastScan);
               CREATE TABLE scan_files (rootID, fileID, pathFile, fileName, extension, date, size);
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

   //private static MessageHandle TX_FATALERROR    = new MessageHandle (); // "_lib scanFiles_error"
   private static MessageHandle TX_SCAN_START     = null; // new MessageHandle (); // "_lib scanFiles_start"
   private static MessageHandle TX_SCAN_PROGRESS  = null; // new MessageHandle (); // "_lib scanFiles_progresss"
   private static MessageHandle TX_SCAN_END       = null; // new MessageHandle (); // "_lib scanFiles_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   private static boolean initOnce_msgHandles ()
   {
      if (TX_SCAN_START == null)
      {
         TX_SCAN_START     = new MessageHandle ();
         TX_SCAN_PROGRESS  = new MessageHandle ();
         TX_SCAN_END       = new MessageHandle ();

         // this messages are not mandatory to be suscribed, the are provided just as internal information of parser command
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

      // collect sub-options
      //

      //sub-option PREFIX
      //
      currentPrefix = "scan";
      String optStr = cmd.takeOptionString("PREFIX");
      if (optStr.length () > 0)
      {
         currentPrefix = optStr;
         theLog.dbg (2, "SCAN", "option PREFIX = '" + currentPrefix + "'");
      }

      //sub-option ROOTLABEL
      //
      String currentRootLabel = (optRemove) ? "": "local";
      optStr = cmd.takeOptionString("ROOTLABEL");
      if (optStr.length () > 0)
      {
         currentRootLabel = optStr;
         theLog.dbg (2, "SCAN", "option ROOTLABEL = '" + currentRootLabel + "'");
      }

      boolean currentIsRecursive = true;
      if (optAdd)
      {
         //sub-option RECURSIVE
         //
         optStr = cmd.takeOptionString("RECURSIVE");
         if (optStr.length () > 0)
         {
            currentIsRecursive = optStr.equalsIgnoreCase ("y");
            theLog.dbg (2, "SCAN", "option RECURSIVE = '" + optStr + "' (" + currentIsRecursive + ")");
         }

         //sub-option FILTERS
         //
         String [] optArr = cmd.takeOptionParameters("FILTERS");
         if (optArr != null)
         {
            for (int ff = 0; ff+1 < optArr.length; ff += 2)
            {
               filtrum.addCriteria (optArr[ff], optArr[ff + 1]);
            }
         }
      }

      if (optRemove)
      {
         String rootPath2Delete = cmd.takeOptionString("ROOTPATH");
         doRemove (dbName, currentRootLabel, rootPath2Delete);
         cmd.checkRemainingOptions (true);
         return 1;
      }

      if (! (optAdd || optZip))
      {
         theLog.err ("SCAN", "operation \"" + oper + "\" not defined!");
         return 1;
      }

      createSchema (dbName);
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
      do
      {
         // cosas = moto.scanN (100);
         cosas = entriesScanNext (500);
         theLog.dbg (2, "SCAN", "adding next " + cosas.size () + " entries");
         for (int jj = 0; jj < cosas.size (); jj++)
         {
            String [] record = (String []) cosas.get (jj);

            String values = rootID + ", " + (fileID ++) + ", ";
            values += "'" + myDB.escapeString(record[0]) + "', '" + myDB.escapeString(record[1]) + "', '" + myDB.escapeString(record[2]) + "', '" + myDB.escapeString(record[3]) + "', " + myDB.escapeString(record[4]);

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
      cmd.checkRemainingOptions (true);
      return 1;
   }


   // create a database
   //
   private void createSchema (String dbName)
   {
      sqlSolver myDB = new sqlSolver ();

      //(o) listix_sql_schemas SCAN schema creation

      myDB.openScript ();
      myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + ROOTS_TABLE () + " (rootID, rootLabel, pathRoot, rootType, timeLastScan, UNIQUE(rootID));");
      myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + FILES_TABLE () + " (rootID, fileID, pathFile, fileName, extension, date, size, UNIQUE(rootID, fileID));");

      // o-o  Add dbMore connections info
      myDB.writeScript (dbMore.getSQL_CreateTableConnections ());
      myDB.writeScript (dbMore.getSQL_InsertConnection("root", FILES_TABLE (), "rootID", ROOTS_TABLE (), "rootID"));

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
