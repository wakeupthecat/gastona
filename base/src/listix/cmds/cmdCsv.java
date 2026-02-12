/*
library listix (www.listix.org)
Copyright (C) 2020-2026 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command XMELON

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       CSV
   <groupInfo>  data_parsers
   <javaClass>  listix.cmds.cmdCsv
   <importance> 4
   <desc>       //Parses a CSV file and stores the data into a database

   <help>
      //
      //Parses CSV (Comma Separated Values) files creating a table to contain all records of the CSV file.
      //
      //A simplified syntax just requires the name of the CSV file and the target database, for example
      //
      //       CSV, FILE2DB, file2parse.csv, sortida.csv.db
      //
      //if the database does not exist it will be created.
      //
      //In the process two tables will be created and populated
      //
      //       table csvTable_records or if tablePrefix is given "tablePrefix"_records
      //       whith the columns:
      //             _numFileID   optional numeric file id, the same for all the records
      //             _strFileID   optional string value thought as unique id for the file, the same for all the records
      //             _hashLine    optional calculated hash line for each record
      //             _hashLineCnt 0 or number of previous contiguous records with the same hash
      //             _lineNr      line number of csv file
      //             _recNr       record number, it may be equal to _lineNr but it does not have to (e.g. incidences or start line different from 1)
      //             rest of columns found in the csv file where the names are changed with some rules like
      //                - all start with "c_"
      //                - replace special characters including spaces with "_"
      //                - ensuring there is no duplicated column name
      //
      //       table csvTable_incidences or if tablePrefix is given "tablePrefix"_incidences
      //       whith the columns:
      //             _numFileID   optional numeric file id, the same for all the records
      //             _strFileID   optional string value thought as unique id for the file, the same for all the records
      //             incidenceNr  incidence counter
      //             lineNr       line number of csv file where the incidence happens
      //             desc         description of the incidence
      //             text         text of the line causing the incidence
      //


   <aliases>
      alias

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Parse 'file2parse' and set the result into the database 'targetDbName' creating the schema if needed


   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , FILE2DB      ,             , //
         1   , file2parse   ,             , //Name of the comma separated value file (CSV) to be parsed
         1   , targetDbName , (default db), //Database name where to put the result of parsing
         1   , tablePrefix  , csvTable    , //Optional prefix for the result tables ('prefix'_records, 'prefix'_incidences)

   <options>
      synIndx, optionName  , parameters, defVal, desc
         1   , FILEID NUM  , number    ,       , //add a column _numFileID with that value for all records
         1   , FILEID STR  , string    ,       , //add a column _strFileID with that value for all records
         1   , HASH LINE   , "0|1|md5|sha1|sha256", 0 , //If different from "0" add a column _hashLine (and _hashLineCnt) containing the md5(default), sha1 or sha256 hash of the line parsed
         1   , COLUMN NAMES, string    ,       , //Comma separated column names. If given it will replace the first line in CSV file. If a column name is - (minus) then that column will be ignored.
         1   , START LINE  , number    , 1     , //Line number where to find the column names header, therefore where to start the parsing
         1   , START REGEXPR, "regexpr",       , //Regular expression for the first line to start with. Empty string has no effect, to start with an empty string give ^$ as regular expression
         1   , END LINE    , number    , -1    , //Line number for end parsing (default -1 = to the end of file)
         1   , MAX INCIDENCES, number  , 100   , //Maximum number of incidences during parse. If this number is exceeded the parse will be aborted.
         1   , MAX RECORDS,  number    , -1    , //Maximum of records to be parsed (default -1 = no limit)

   <examples>
      gastSample
      empassaCSV

   <empassaCSV>
      //#javaj#
      //
      //    <frames> main, CSV to table (using cmdCSV), 600
      //
      //    <layout of main>
      //       EVA, 7, 7, 3,3
      //
      //          , X
      //          , bDropCSV
      //        X , sContent
      //        X , sIncidents
      //        X , oSa
      //
      //#data#
      //
      //    <bDropCSV droppedFiles>
      //
      //#listix#
      //
      //   <-- bDropCSV droppedFiles>
      //      LOOP, VAR, bDropCSV droppedFiles
      //          ,, LSX, convertAndOpen, @<fullPath>
      //          ,, //it took @<:lsx elapsed sapiens>
      //
      //   <convertAndOpen>
      //      VAR=, dbtmp, @<:lsx tmp db>
      //      //
      //      //processing @<p1>
      //      //  in @<dbtmp>
      //      //
      //      CSV, FILE2DB, @<p1>, @<dbtmp>
      //
      //      DB CONFIG, DEFAULT, @<dbtmp>
      //      -->, sContent data!, sqlSelect, //SELECT * FROM csvTable_records
      //      -->, sIncidents data!, sqlSelect, //SELECT * FROM csvTable_incidences

#**FIN EVA#
*/

package listix.cmds;

import java.util.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.utilEscapeStr;
import de.elxala.parse.csv.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end
import de.elxala.math.hash.*;
import java.util.regex.*;  // for Matcher, Pattern

/**
*/
public class cmdCsv implements commandable
{
   //private static MessageHandle TX_FATALERROR    = new MessageHandle (); // "_lib scanFiles_error"
   private static MessageHandle LIGHT_MSG_START     = null; // new MessageHandle (); // "ledMsg parsons_start"
   private static MessageHandle LIGHT_MSG_PROGRESS  = null; // new MessageHandle (); // "ledMsg parsons_progresss"
   private static MessageHandle LIGHT_MSG_END       = null; // new MessageHandle (); // "ledMsg parsons_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   private String optColumnHeaderStr = null;
   private int optStartLine = 1;
   private int optEndLine = -1;
   private int optMaxIncidences = 100;
   private int optMaxRecords = -1;
   private String optHashLine = "0";
   private String optFileID   = "";
   private String optFileHash = "";
   private boolean optClear = false;
   private String optStartRegexp = null;

   private static boolean initOnce_msgHandles ()
   {
      if (LIGHT_MSG_START == null)
      {
         LIGHT_MSG_START     = new MessageHandle ();
         LIGHT_MSG_PROGRESS  = new MessageHandle ();
         LIGHT_MSG_END       = new MessageHandle ();

         //(o) TODO_parsons Unify ledMsg parsing_start etc for PARSONS and XMELON

         //NOTE! the message should be the same as in parsons, this signal should be general
         //      for parsing activity

         // this messages are not mandatory to be subscribed, the are provided just as internal information of parser command
         Mensaka.declare (null, LIGHT_MSG_START    , "ledMsg parsing_start"      , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_PROGRESS , "ledMsg parsing_progresss"  , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_END      , "ledMsg parsing_end"        , logServer.LOG_DEBUG_0);
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
          "CSV",
          "CSVPARSER",
          "PARSECSV",
          "PARSE-CSV",
          "EMPASSACSV",
       };
   }

   protected String setStrValue (String str, String defval)
   {
      return (str == null || str.length () == 0) ? defval: str;
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

      String oper        = cmd.getArg(0);
      String fileSource  = cmd.getArg(1);
      String dbName      = cmd.getListix ().resolveDBName (cmd.getArg(2));
      String tablePrefix = cmd.getArg(3);
      tablePrefix        = setStrValue (tablePrefix, "csvTable");

      boolean optCSVFile2DB = cmd.meantConstantString (oper, new String [] { "FILE2DB", "CSV2DB" } );

      optColumnHeaderStr = cmd.takeOptionString(new String [] {"COLUMNNAMES", "COLUMNS", "NAMES" }, null);
      optStartLine       = stdlib.atoi (cmd.takeOptionString(new String [] {"STARTLINE", "START", "BEGIN", "BEGINLINE" }, "1"));
      optStartRegexp     = cmd.takeOptionString(new String [] {"STARTREGEXPR", "BEGINREGEXPR", "STARTREGEXP", "BEGINREGEXP" }, null);
      optEndLine         = stdlib.atoi (cmd.takeOptionString(new String [] {"ENDLINE", "END" }, "-1"));
      optMaxIncidences   = stdlib.atoi (cmd.takeOptionString(new String [] {"MAXINCIDENCES", "MAXERRORS" }, "100"));
      optMaxRecords      = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITRECORDS", "MAXRECORDS", "RECORDS" }, "-1"));
      optHashLine        = cmd.takeOptionString(new String [] {"HASHLINE", "ADDHASHLINE" }, "0");
      optFileID          = cmd.takeOptionString(new String [] {"FILEID" }, null);
      optFileHash        = cmd.takeOptionString(new String [] {"FILEHASH" }, null);
      optClear           = (1 == stdlib.atoi (cmd.takeOptionString(new String [] {"CLEAN", "CLEAR" }, "-1")));

      optColumnHeaderStr = setStrValue (optColumnHeaderStr, null);
      optFileID          = setStrValue (optFileID, null);
      optFileHash        = setStrValue (optFileHash, null);

      if (cmd.getLog().isDebugging (2))
         cmd.getLog().dbg (2, "CSV", "execute with : oper [" + oper + "] fileSource [" + fileSource + "] dbName [" +  dbName + "] prefix [" + tablePrefix + "]");
      if (fileSource.length () == 0)
      {
         that.log ().err ("CSV", "No file to scan given!");
         return 1;
      }

      if (optCSVFile2DB)
      {
         parseCSVFile (that, fileSource, dbName, tablePrefix);
      }

      cmd.checkRemainingOptions ();
      return 1;
   }

   protected void parseCSVFile (listix that, String csvFileName, String dbName, String tablePrefix)
   {
      boolean headerread = false;
      TextFile fitx = new TextFile ();

      String lastHash = "";
      int lastHashCtn = 0;

      String hashAlg = optHashLine.equals("0") ? null:
                       (optHashLine.equals("1") || optHashLine.equals("")) ? hashos.DEFAULT_HASH_ALG: optHashLine;

      String strFileKeysNames = (optFileID != null ? "_numFileID, ": "") +
                                (optFileHash != null ? "_strFileID, ": "");

      String strFileKeysValues = (optFileID != null ? optFileID + ", ": "") +
                                 (optFileHash != null ? "'" + optFileHash + "', ": "");


      if (!fitx.fopen (csvFileName, "r"))
      {
         that.log ().err ("CSV", "File " + csvFileName + " cannot be opened for input!");
         return;
      }

      if (sendMessages)
         Mensaka.sendPacket (LIGHT_MSG_START, null); // ... )))

      sqlSolver myDB = new sqlSolver ();

      CsvParserV70 coso = new CsvParserV70 ();
      int lineCnt = 1;
      int incidCnt = 0;
      int recordCnt = 0;

      myDB.openScript ();

      // skipping until optStartLine here will implies that
      // this option precedes optStartRegexpr if both are given
      // that means, we first skip a number of lines and then we search for the beginning

      // skip until reach optStartLine
      while (lineCnt < optStartLine && fitx.readLine ())
         lineCnt ++;

      // check if need to skip lines until startRegexpr
      // START REGEXPR, ""
      // has no effect
      //
      if (optStartRegexp != null && optStartRegexp.length () > 0)
      {
         Pattern pattStart = Pattern.compile (optStartRegexp);
         if (pattStart != null)
         {
            while (fitx.readLine ())
            {
               lineCnt ++;
               if (pattStart.matcher (fitx.TheLine ()).find ())
               {
                  that.log ().dbg (2, "parseCSVFile", "startRegexpr found at line " + lineCnt);
                  break;
               }
            }
         }
         else
         {
            that.log ().err ("parseCSVFile", "invalid startRegexpr [" + optStartRegexp + "]");
         }
      }
      else
      {
         fitx.readLine ();
         lineCnt ++;
      }

      // read headers and create tables
      //
      {
         // choose optColumnHeaderStr or the first line for the column names
         //
         boolean explicitCols = optColumnHeaderStr != null && optColumnHeaderStr.length() > 0;
         coso.processHeader (explicitCols ? optColumnHeaderStr: fitx.TheLine (),
                             !explicitCols);

         if (optClear)
         {
            myDB.writeScript ("DROP TABLE IF EXISTS " + tablePrefix + "_records ;");
            myDB.writeScript ("DROP TABLE IF EXISTS " + tablePrefix + "_incidences ;");
         }

         // table records
         //
         myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tablePrefix + "_records (" + strFileKeysNames);

         if (hashAlg != null)
            myDB.writeScript ("_hashLine, _hashLineCnt, ");

         myDB.writeScript ("_recNr, _lineNr, ");

         for (int ii = 0; ii < coso.columnCount (); ii ++)
            if (coso.validColumn (ii))
               myDB.writeScript ((ii != 0 ? ", ": "") + coso.headColNames.get (ii));

         myDB.writeScript (");");

         // table incidences
         //
         myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tablePrefix + "_incidences (" + strFileKeysNames +
                           "incidenceNr, lineNr, desc, text);");
      }

      while (fitx.readLine ())
      {
         lineCnt ++;
         if (sendMessages && (lineCnt % 100) == 0)
            Mensaka.sendPacket (LIGHT_MSG_PROGRESS, null); // ... )))

         if (optEndLine != -1 && lineCnt > optEndLine) break;
         if (optMaxRecords != -1 && recordCnt >= optMaxRecords) break;

         List columns = coso.parseCsvLine(fitx.TheLine ());
         if (columns.size () != coso.columnCount ())
         {
            myDB.writeScript ("INSERT INTO " + tablePrefix + "_incidences VALUES (" + strFileKeysValues +
                              incidCnt + ", " + lineCnt + ", '" +
                              columns.size () + " columns found but " +
                              coso.headColNames.size () + " are required', '" + utilEscapeStr.escapeStr (fitx.TheLine ()) + "');");
            if (optMaxIncidences != -1 && incidCnt >= optMaxIncidences)
            {
               myDB.writeScript ("INSERT INTO " + tablePrefix + "_incidences VALUES (" + strFileKeysValues +
                                 incidCnt + ", " + lineCnt + ", " +
                                 "'too many incidences (" + optMaxIncidences + "), parse aborted', '');");
               break;
            }
         }
         else
         {
            recordCnt ++;
            myDB.writeScript ("INSERT INTO " + tablePrefix + "_records VALUES (" + strFileKeysValues);
            if (hashAlg != null)
            {
               String aquestHash = hashos.hashStr (fitx.TheLine (), hashAlg);

               if (lastHash.equals (aquestHash))
                    lastHashCtn ++;
               else lastHashCtn = 0;
               myDB.writeScript ("'" + aquestHash + "', " + lastHashCtn +", ");
               lastHash = aquestHash;
            }
            myDB.writeScript (recordCnt + ", " + lineCnt + ", ");
            for (int ii = 0; ii < columns.size (); ii ++)
               if (coso.validColumn (ii))
                  myDB.writeScript ((ii == 0 ? "": ", ") + "'" + utilEscapeStr.escapeStr ((String) columns.get(ii)) + "'");
            myDB.writeScript (");");
         }
      }

      fitx.fclose ();
      myDB.closeScript ();
      myDB.runSQL (dbName);

      if (sendMessages)
         Mensaka.sendPacket (LIGHT_MSG_END, null); // ... )))
   }

   private boolean filterOutThisName (String name)
   {
      // implement filters later
      return false;
   }
}
