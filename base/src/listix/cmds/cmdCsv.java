/*
library listix (www.listix.org)
Copyright (C) 2020 Alejandro Xalabarder Aulet

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
      // The XMELON schema is an approach to parse xml data and store it in a database. This command
      // allows to parse xml files creating and filling the XMELON tables.
      //
      // ====== Parsing XML files into XMELON tables
      //
      // To parse a XML file simply give the file name of the xml file and the target database, for example
      //
      //       CSV, FILE2DB, file2parse.csv, sortida.csv.db
      //
      // if the database does not exist it will be created.
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
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample

#**FIN EVA#
*/

package listix.cmds;

//import java.io.*;
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

   private int fileID = -1;

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

      String oper        = cmd.getArg(0);
      String fileSource  = cmd.getArg(1);
      String dbName      = cmd.getArg(2);
      String tablePrefix = cmd.getArg(3);
      if (tablePrefix.length () == 0)
         tablePrefix = "csvTable";

      if (dbName.length () == 0)
         dbName = cmd.getListix ().getDefaultDBName ();

      boolean optCSVFile2DB = cmd.meantConstantString (oper, new String [] { "FILE2DB", "XML", "XML2DB" } );

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

      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_END, null); // ... )))

      cmd.checkRemainingOptions ();
      return 1;
   }

   protected void parseCSVFile (listix that, String csvFileName, String dbName, String tablePrefix)
   {
      TextFile fitx = new TextFile ();

      if (!fitx.fopen (csvFileName, "r"))
      {
         that.log ().err ("CSV", "File " + csvFileName + " cannot be opened for input!");
         return;
      }

      sqlSolver myDB = new sqlSolver ();

      CsvParserV70 coso = new CsvParserV70 ();
      int lineCnt = 0;
      int incidCnt = 0;

      myDB.openScript ();

      if (fitx.readLine ())
      {
         coso.processHeader (fitx.TheLine ());
         myDB.writeScript ("DROP TABLE IF EXISTS " + tablePrefix + "_records ;");
         myDB.writeScript ("DROP TABLE IF EXISTS " + tablePrefix + "_incidences ;");
         myDB.writeScript ("CREATE TABLE " + tablePrefix + "_records (");
         for (int ii = 0; ii < coso.headColNames.size (); ii ++)
            myDB.writeScript ((ii != 0 ? ", ": "") + coso.headColNames.get (ii));
         myDB.writeScript (");");
         myDB.writeScript ("CREATE TABLE " + tablePrefix + "_incidences (incidenceNr, lineNr, desc, text);");
      }

      while (fitx.readLine ())
      {
         lineCnt ++;
         List columns = coso.parseCsvLine(fitx.TheLine ());

         if (columns.size () != coso.headColNames.size ())
         {
            incidCnt ++;
            myDB.writeScript ("INSERT INTO " + tablePrefix + "_incidences VALUES (" + (incidCnt++) + ", " + lineCnt + ", '" +
                                columns.size () + " columns found but " +
                                coso.headColNames.size () + " are required', '" + utilEscapeStr.escapeStr (fitx.TheLine ()) + "');");
            if (incidCnt >= 100)
            {
               myDB.writeScript ("INSERT INTO " + tablePrefix + "_incidences VALUES (" + (incidCnt++) + ", " + lineCnt + ", " +
                                  "'too many incidences, parse aborted', '');");
               break;
            }
         }
         else
         {
            myDB.writeScript ("INSERT INTO " + tablePrefix + "_records VALUES (");
            for (int ii = 0; ii < columns.size (); ii ++)
               myDB.writeScript ((ii == 0 ? "": ", ") + "'" + utilEscapeStr.escapeStr ((String) columns.get(ii)) + "'");
            myDB.writeScript (");");
         }
      }

      fitx.fclose ();
      myDB.closeScript ();
      myDB.runSQL (dbName);
   }

   private boolean filterOutThisName (String name)
   {
      // implement filters later
      return false;
   }

}
