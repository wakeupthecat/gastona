/*
library de.elxala
Copyright (C) 2010 Alejandro Xalabarder Aulet

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

package de.elxala.db.sqlite;

import java.io.File;

import java.util.Vector;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.streams.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.*;

import de.elxala.db.*;

/*
   31.12.2009 02:53
   16.11.2010 00:15

   SQL solver (old clientCaller)
   using Android SQLite API

*/
public class sqlSolverAApi
{
   public static String SESSION_TEMP_DB_ALIAS = "sessionTmpDB";

   private serialTextBuffer theSQLScript = new serialTextBuffer ();
   private boolean addTransaction = false;

   private String theSQLOutput = null;
   private String theSQLError = null;
   private Cursor theSQLCursor = null;

   private String theInputTextFile = null;
   private String theOutputTextFile = null;
   private String theErrorTextFile = null;

   private String lastUsedDatabase = null;
   private SQLiteDatabase openDB = null;
   private String openSQLSelect = null;
   private String openDBName = null;

   // private static String TRACE_QUERIES_FILE_NAME = "TRACE_BATCH_QUERIES.LOG";
   public static String SQLITE_LOG_FILE_NAME = "sqlLog.log";
   public static boolean firstCleanSqlLogFileDone = false;

   private logger log = new logger (this, "de.elxala.db.sqlite.sqlSolver", null);

   public boolean tracingOn ()
   {
      return log.getLogDirectory () != null;
   }

   public String traceFileName ()
   {
      if (log.getLogDirectory () == null) return null;

      return log.getLogDirectory () + SQLITE_LOG_FILE_NAME;
   }

//   04.04.2005 13:05
//   QUITADO PARA sqLite
//   PODRIA HACER UNA LISTA DE FICHERO .sqliteDB (p.e.) pero EN QUE DIRECTORIO ?
//
//   public String [] getDatabases ()
//   {
//      sqLiteCall ("show databases;");
//      return mSQLOutput;
//   }

   public void setInputScriptFile (String inputFile, boolean withTransaction)
   {
      theInputTextFile = inputFile;
      addTransaction = withTransaction;
   }

   public void setStdOutputFile (String outputFile)
   {
      theOutputTextFile = outputFile;
   }

   public void setErrOutputFile (String errorFile)
   {
      theErrorTextFile = errorFile;
   }


   public Cursor getLastCursor ()
   {
      if (openDB == null)
         log.severe ("getLastCursor", "cursor is invalid!");

      return theSQLCursor;
   }

//   public List getLastErrors ()
//   {
//      return theSQLError != null ? theSQLError.getAsList (): new Vector ();
//   }

   public String [] getTables (String dataBase)
   {
      return getTables (dataBase, "type = 'table'");
   }

   public String [] getViews (String dataBase)
   {
      return getTables (dataBase, "type = 'view'");
   }

   //
   // get the tables and or views names from the database
   //
   // condition samples :
   //    only tables "type = 'table'"
   //    only views  "type = 'view'"
   //    all but indexes  "type <> 'index'"
   //
   //
   public String [] getTables (String dataBase, String condition)
   {
      /*

      Example of select * from sqlite_master;

      sqlite> select * from sqlite_master;
      type|name|tbl_name|rootpage|sql
      table|mostlog|mostlog|2|CREATE TABLE mostlog (timems, fromNode, toNode, instance, message, dataAux)
      view|corcorcia|corcorcia|0|CREATE VIEW corcorcia as select * from mostlog order by timems
      table|gruprelo|gruprelo|1382|CREATE TABLE gruprelo (g1, g2)
      view|view555|view555|0|CREATE VIEW view555 AS select * from mostlog order by timems
      */

      String swhere = (condition.length() == 0) ? "": " WHERE " + condition;
      sqLiteCall (dataBase, "SELECT name FROM sqlite_master " + swhere, true);

      if (theSQLCursor == null) return new String [0];

      String [] out = new String [theSQLCursor.getCount ()];
      theSQLCursor.moveToFirst();
      for (int ii = 0; ii < out.length && !theSQLCursor.isAfterLast(); ii ++)
      {
         out[ii] = theSQLCursor.getString (0);
         theSQLCursor.moveToNext();
      }
      theSQLCursor.close ();
      theSQLCursor = null;
      closeDB ();

      return out;
   }

   public void getSchema (String dbName, Eva evaResult)
   {
      sqlGetSchemaAApi.getSchema (dbName, evaResult);
   }

////   public String [] getData (String dataBase, String table)
////   {
////      return getData (dataBase, table, null);
////   }
////
////   public String [] getData (String dataBase, String table, String limit)
////   {
////      if (limit == null)
////      {
////         limit = "LIMIT 0,200";
////      }
////      sqLiteCall (dataBase, "select * from " + table + " " + limit + ";");
////      return mSQLOutput;
////   }

   public void runSQL (String dataBase)
   {
      sqLiteCall (dataBase, false);
   }

   public void runSQL (String dataBase, String sql_expresion)
   {
      sqLiteCall (dataBase, sql_expresion, false);
   }

   private boolean check_misprogrammed_getSQL ()
   {
      if (theOutputTextFile == null) return true;
      log.severe ("getSQLCursor", "(misprogrammed controller?) call to getSQL but output text file for the query is given! (should call runSQL instead)");
      return false;
   }

   public List getSQL (String dataBase, String sql_expresion)
   {
      log.severe ("getSQL", "List getSQL NOT SUPPORTED!! change the code!");
      return new Vector ();
   }

   public Cursor getSQLCursor (String dataBase, String sql_expresion)
   {
      check_misprogrammed_getSQL ();
      sqLiteCall (dataBase, sql_expresion, true);

      return getLastCursor ();
   }

   public Cursor getSQLCursor (String dataBase)
   {
      check_misprogrammed_getSQL ();

      sqLiteCall (dataBase, true);

      return getLastCursor ();
   }

   public Cursor getSQLCursor () // from previous file!
   {
      check_misprogrammed_getSQL ();

      if (lastUsedDatabase == null)
      {
         // errDev : error in development, some javaj controller or listix external command is misprogrammed

         log.severe ("getSQLCursor", "(misprogrammed controller?) call to getSQL with no parameters but there is no lastUsedDatabase!");
         return null;
      }

      sqLiteCall (lastUsedDatabase, true);

      return getLastCursor ();
   }

   public boolean openScript ()
   {
      return openScript (true);
   }

   public boolean openScript (boolean withTransaction)
   {
      theSQLScript.clear ();
      addTransaction = withTransaction;
      return true;
   }

   public void writeScript (String sentence)
   {
      theSQLScript.writeln (sentence);
   }

   public void closeScript ()
   {
   }

   private boolean sqLiteCall (String database, String strCall, boolean isSelect)
   {
      // write input script file
      //
      if ( ! openScript (false))
      {
         log.fatal ("sqLiteCall", "cannot open Script!");
         return false;
      }

      writeScript (strCall + ";");
      closeScript ();

      return sqLiteCall (database, isSelect);
   }

   private boolean sqLiteCall (String database, boolean isSelect)
   {
      theSQLOutput = null;
      theSQLError = null;
      //if (! checkClient ()) return false;

      if (database == null)
      {
         log.severe ("sqLiteCall", "(misprogrammed controller?), sqliteCall with null database. Query will not be executed.");
         // A possible cause if working with listix is an
         // attempt to use a not formed default database (null).
         // The client didn't created it and tries to use it.
         return false;
      }
      database = database.trim ();

      // ... )))
      sqlSignaler.signalStart ();


      log.dbg (2, "sqLiteCall", "start sqlite executing on database \"" + database + "\" tracing " + tracingOn ());

      // Open traceFile ?
      TextFile traceFile = null;
      if (tracingOn ())
      {
         traceFile = new TextFile ();

         String modus = firstCleanSqlLogFileDone ? "a": "w";
         firstCleanSqlLogFileDone = true;

         if (!traceFile.fopen (traceFileName (), modus))
         {
            log.err ("sqLiteCall", "cannot open trace file [" + traceFileName() + "] for append!");
            traceFile = null;
         }
      }


      long startingTime = System.currentTimeMillis ();
      if (traceFile != null)
      {
         String time_stamp = (new DateFormat ("yyyy.MM.dd HH:mm:ss.S", new java.util.Date())).get ();
         traceFile.writeLine ("*** START sqlite CALL WITH DB [" + database + "] on " + time_stamp);

         theSQLScript.rewind ();
         while (theSQLScript.getNextLine ())
            traceFile.writeLine (theSQLScript.getLastReadLine());
      }


      // call sqlite via api
      //
      if (isSelect)
         callSqliteSelect (database, theInputTextFile);
      else
         callSqliteExec (database, theInputTextFile);


      long endingTime = System.currentTimeMillis ();

      log.dbg (2, "sqLiteCall", "end executing it took " + (endingTime-startingTime) /1000.);
      if (traceFile != null)
      {
         String time_stamp = (new DateFormat ("yyyy.MM.dd HH:mm:ss.S", new java.util.Date())).get ();
         traceFile.writeLine ("*** STD-OUTPUT (" + (endingTime-startingTime) /1000. + " s) on " + time_stamp);

         if (theOutputTextFile != null) traceFile.writeFileContents (theOutputTextFile);
         else
         {
            traceFile.writeLine (theSQLOutput);
         }

         if (theSQLError != null)
         {
            traceFile.writeLine ("!!! ERROR-OUTPUT");
            if (theErrorTextFile != null) traceFile.writeFileContents (theErrorTextFile);
            else
            {
               traceFile.writeLine (theSQLError);
            }
         }
         traceFile.writeLine ("*** END-OUTPUT");
         traceFile.fclose ();
         traceFile = null;
      }


      // custom...FileName's has only one use!
      //
      lastUsedDatabase = database;

      // read output file
      //
      if (theSQLError == null)
      {
         sqlSignaler.signalEnd ();
      }
      else
      {
         sqlSignaler.signalError ();
         if (theErrorTextFile != null)
              log.err ("sqLiteCall", "SQL call with ouput errors (in file " + theErrorTextFile + ")");
         else log.err ("sqLiteCall", "SQL call with ouput errors : [" + theSQLError +"]");
      }

      return (theSQLError == null);
   }

   private void callSqliteSelect (String database, String inputFile)
   {
      String selectStr = (inputFile == null) ? theSQLScript.toString (): "";

      if (inputFile != null)
      {
         StringBuffer allTxt = TextFile.readFileIntoStringBuffer (inputFile);
         if (allTxt == null)
         {
            log.err ("callSqliteSelect", "the given file [" + inputFile + "] for the select cannot be opened!");
            return;
         }
         else
         {
            selectStr = allTxt.toString ();
         }
      }

      //open db
      if (openDB != null)
      {
         log.severe ("callSqliteSelect", "who didn't close the database ?");
         openDB.close ();
      }
      openSQLSelect = selectStr;
      openDBName = database;

      reopenDB ();
   }

   public void closeDB ()
   {
      if (theSQLCursor != null) theSQLCursor.close ();
      theSQLCursor = null;
      if (openDB != null) openDB.close ();
      openDB = null;
   }

   public void ensureOpenDB ()
   {
      if (openDB == null) reopenDB ();
   }

   public void reopenDB ()
   {
      closeDB ();
      openDB = null;
      try
      {
         log.dbg (2, "reopenDB", "SQLiteDatabase.openDatabase");
         //openDB = SQLiteDatabase.openOrCreateDatabase  (openDBName, null);
         openDB = SQLiteDatabase.openDatabase  (openDBName, null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
      }
      catch (Exception e) //SQLiteException
      {
         log.err ("reopenDB", "Error opening database [" + openDBName + "] " + e);
         if (openDB != null) openDB.close ();
         return;
      }

      //do select
      theSQLCursor = null;
      try
      {
         theSQLCursor = openDB.rawQuery (openSQLSelect, null);
      }
      catch (Exception e) //SQLiteException
      {
         theSQLError = e + "";
         log.err ("reopenDB", "Error opening database [" + openDBName + "] " + e);
         if (openDB != null) openDB.close ();
         return;
      }

      log.dbg (2, "reopenDB", "rawQuery result " + theSQLCursor.getCount () + " records of " + theSQLCursor.getColumnCount () + " columns each");

      if (log.isDebugging (4) && theSQLCursor.getCount () > 0)
      {
         theSQLCursor.moveToFirst ();
         for (int ii = 0; ii < theSQLCursor.getColumnCount (); ii ++)
            log.dbg (4, "reopenDB", "  col[" + ii + "] [" + theSQLCursor.getString (ii) + "]");
      }
   }


   private void callSqliteExec (String database, String inputFile)
   {
      // aqui leemos theSQLScript de otra forma!

      TextFile fi = null;
      if (inputFile != null)
      {
         fi = new TextFile ();
         if (!fi.fopen (inputFile, "r"))
         {
            log.err ("callSqliteExec", "the given file [" + inputFile + "] for the query does not exist!");
            return;
         }
      }
      else theSQLScript.rewind ();

      //open db
      SQLiteDatabase db = null;
      try
      {
         //db = SQLiteDatabase.openOrCreateDatabase  (database, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
         db = SQLiteDatabase.openDatabase  (database, null, SQLiteDatabase.CREATE_IF_NECESSARY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
      }
      catch (Exception e) //SQLiteException
      {
         log.err ("callSqliteExec", "Error opening database [" + database + "] " + e);
         if (db != null) db.close ();
         return;
      }

      if (db == null)
      {
         log.err ("callSqliteExec", "Error null db opening database [" + database + "]");
         return;
      }

      //do select
      boolean mas = false;

      //(o) TODO : detect "BEGIN TRANSACTION" AND "COMMIT" in the case of inputFile
      //
      // if (!transacc && linea.startsWithIgnorecase ("BEGIN"))
      //{
      //   transacc = true
      //}

      String linea = "";
      int lineNr = 0;
      if (addTransaction)
      {
         db.beginTransaction();
         log.dbg (2, "callSqliteExec", "begin transaction...");
      }
      try
      {
         do
         {
            mas = (inputFile != null) ? fi.readLine (): theSQLScript.getNextLine ();
            if (! mas ) break;
            linea = (inputFile != null) ? fi.TheLine (): theSQLScript.getLastReadLine ();
            lineNr ++;
            if (linea.trim().length () == 0) continue;

            log.dbg (2, "callSqliteExec", "execSQL [" + linea + "] ");
            db.execSQL (linea);
         } while (mas);
         if (addTransaction)
            db.setTransactionSuccessful();
      }
      catch (Exception e) //SQLiteException
      {
         theSQLError = e + "";
         log.err ("callSqliteExec", "Error executing line " + lineNr + " [" + linea + "] on database [" + database + "] " + e);
         if (addTransaction)
            db.endTransaction();
         db.close ();
         return;
      }
      if (addTransaction)
      {
         db.endTransaction();
         log.dbg (2, "callSqliteExec", "commit transaction...");
      }
      log.dbg (2, "callSqliteExec", "closing the database");
      db.close ();
      //db.finalize ();
      db = null;
   }

   private static boolean bCheckDone = false;
   private static String sqliteClientVersion = "?";

   public boolean checkClient ()
   {
      if (bCheckDone) return true;
//      try
//      {
//         SQLiteDatabase te = SQLiteDatabase.create(null);
//         sqliteClientVersion = "" + te.getVersion ();
//         te.close ();
//      }
//      catch (Exception e) {}
      log.dbg (2, "checkClient", "using SQLite android API, sqlite version : " + sqliteClientVersion);
      bCheckDone = true;
      return true;
   }


   // ===============================================================
   /**
      ----------- Escape / unEscape strings, compact and expand records facilities

      in future clientCaller will offer more escape policies in its constructor
      and these method will take the appropiate method. At the moment there is only one
      possible escape policy, the one from utilEscapeStr.

   */
   public static String escapeString (String str)
   {
      return utilEscapeStr.escapeStr (str);
   }

   public static String unEscapeString (String str)
   {
      return utilEscapeStr.desEscapeStr (str);
   }

   public static String compactRow (String [] strArray)
   {
      return utilEscapeStr.compactRow (strArray);
   }

   public static String [] expandRow (String campsStr, int offsetColumn)
   {
      return utilEscapeStr.expandRow (campsStr, offsetColumn);
   }

   public static String [] expandRow (String campsStr)
   {
      return utilEscapeStr.expandRow (campsStr);
   }
   // ===============================================================


//   public static void main (String [] aa)
//   {
//      sqlSolver cli = new sqlSolver ();
//      if (cli.checkClient())
//           System.out.println ("check " + cli.getClientExePath () + " ok version = " + sqliteClientVersion);
//      else System.out.println ("cannot find sqlite client " + cli.getClientExePath () + " !");
//   }
}
