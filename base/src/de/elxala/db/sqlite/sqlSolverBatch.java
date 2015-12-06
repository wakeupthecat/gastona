/*
library de.elxala
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

package de.elxala.db.sqlite;

import java.io.File;

import java.util.Vector;
import java.util.List;
import java.util.regex.*;  // Pattern Matcher etc for sqlite error detection


import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.streams.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.*;

import de.elxala.db.*;

/*
   31.12.2009 02:53

   SQL solver (old clientCaller)

*/
public class sqlSolverBatch
{
   public static String SESSION_TEMP_DB_ALIAS = "sessionTmpDB";

   private serialTextBuffer theSQLScript = new serialTextBuffer ();

   private abstractStreamTextReader theSQLOutput = null;
   private abstractStreamTextReader theSQLError = null;

   private String theInputTextFile = null;
   private String theOutputTextFile = null;
   private String theErrorTextFile = null;

   private String lastUsedDatabase = null;

   // private static String TRACE_QUERIES_FILE_NAME = "TRACE_BATCH_QUERIES.LOG";
   public static String SQLITE_LOG_FILE_NAME = "sqlLog.log";
   public static boolean firstCleanSqlLogFileDone = false;

   private boolean scriptTransaction = false;

   private logger log = new logger (this, "de.elxala.db.sqlite.sqlSolver", null);

   private String createSessionTempDB ()
   {
      // create the artificial temp database (not sqlite native)
      //
      String sessTmpDb = fileUtil.createTemporal (SESSION_TEMP_DB_ALIAS, ".db");
      log.dbg (2, "createSessionTempDB", "Session temporary database (will be attached as " + SESSION_TEMP_DB_ALIAS + ") created [" + sessTmpDb + "]");

      return sessTmpDb;
   }

   public String getApplicationTempDatabase ()
   {
      String sessDB = System.getProperty ("gastona." + SESSION_TEMP_DB_ALIAS, null);
      if (sessDB == null)
      {
         sessDB = createSessionTempDB ();
         System.setProperty ("gastona." + SESSION_TEMP_DB_ALIAS, sessDB);
      }

      return sessDB;
   }

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

   public void setInputScriptFile (String inputFile)
   {
      theInputTextFile = inputFile;
   }

   public void setStdOutputFile (String outputFile)
   {
      theOutputTextFile = outputFile;
   }

   public void setErrOutputFile (String errorFile)
   {
      theErrorTextFile = errorFile;
   }


   public List getLastOutput ()
   {
      return theSQLOutput != null ? theSQLOutput.getAsList (): new Vector ();
   }

   public List getLastErrors ()
   {
      return theSQLError != null ? theSQLError.getAsList (): new Vector ();
   }

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
      sqLiteCall (dataBase, "SELECT name FROM sqlite_master " + swhere + ";");

      if (theSQLOutput == null || theSQLOutput.countLines () == 0)
         return new String [0];

      String [] out = new String [theSQLOutput.countLines () -1];
      for (int ii = 0; ii < out.length; ii ++)
         out[ii] = theSQLOutput.getLine (ii+1);

      return out;
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
      sqLiteCall (dataBase);
   }

   public void runSQL (String dataBase, String sql_expresion)
   {
      sqLiteCall (dataBase, sql_expresion);
   }

   private boolean check_misprogrammed_getSQL ()
   {
      if (theOutputTextFile == null) return true;
      log.severe ("getSQL", "(misprogrammed controller?) call to getSQL but output text file for the query is given! (should call runSQL instead)");
      return false;
   }

   public List getSQL (String dataBase, String sql_expresion)
   {
      check_misprogrammed_getSQL ();
      sqLiteCall (dataBase, sql_expresion);

      return getLastOutput ();
   }

   public List getSQL (String dataBase)
   {
      check_misprogrammed_getSQL ();

      sqLiteCall (dataBase);

      return getLastOutput ();
   }

   public List getSQL () // from previous file!
   {
      check_misprogrammed_getSQL ();

      if (lastUsedDatabase == null)
      {
         // errDev : error in development, some javaj controller or listix external command is misprogrammed

         log.severe ("getSQL", "(misprogrammed controller?) call to getSQL with no parameters but there is no lastUsedDatabase!");
         return new Vector();
      }

      sqLiteCall (lastUsedDatabase);

      return getLastOutput ();
   }

   public boolean openScript ()
   {
      return openScript (true);
   }

   public boolean openScript (boolean withTransaction)
   {
      theSQLScript.clear ();

      scriptTransaction = withTransaction;

      writeScript (".headers ON");
      writeScript ("ATTACH DATABASE \"" + getApplicationTempDatabase() + "\" AS " + SESSION_TEMP_DB_ALIAS + " ;");

      //(o) TODO_sqlite automatically attaching databases, by the moment only implemented through property "gastona.defaultDBaliasAttach"
      writeScript (sqlUtil.getGlobalDefaultDBaliasAttachQuery ());

      if (scriptTransaction)
      {
         writeScript ("BEGIN TRANSACTION;");
      }
      return true;
   }

   public void writeScript (String sentence)
   {
      theSQLScript.writeln (sentence);
   }

   public void closeScript ()
   {
      if (scriptTransaction)
         writeScript ("COMMIT ;");
      writeScript ("DETACH DATABASE " + SESSION_TEMP_DB_ALIAS + " ;");

      //(o) TODO_sqlite automatically attaching databases, by the moment only implemented through property "gastona.defaultDBaliasAttach"
      writeScript (sqlUtil.getGlobalDefaultDBaliasDetachQuery ());
   }

   private boolean sqLiteCall (String database, String strCall)
   {
      if (! checkClient ())
      {
         return false;
      }

      // write input script file
      //
      if ( ! openScript (false))
      {
         log.fatal ("sqLiteCall", "cannot open Script!");
         return false;
      }

      writeScript (strCall + ";");
      closeScript ();

      return sqLiteCall (database);
   }

   private boolean sqLiteCall (String database)
   {
      theSQLOutput = null;
      theSQLError = null;
      if (! checkClient ()) return false;

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

      Process proc = null;
      try
      {
         // create the process
         //
         //(o) JAVA EXEC !!!!
         proc = Runtime.getRuntime().exec (new String [] { getClientExePath (), database });
      }
      catch (Exception e)
      {
         log.err ("sqLiteCall", "exception creating process [" + getClientExePath() + "]!" + e);
         if (traceFile != null)
            traceFile.fclose ();
         return false;
      }

      // create the streams
      //

      // create injector stream for the input to sqlite
      //
      Thread introd = (theInputTextFile != null) ?
                        (Thread) (new streamFileInjector (proc.getOutputStream (), theInputTextFile)):
                        (Thread) (new streamTextBufferInjector (proc.getOutputStream (), theSQLScript, null));

      // create stdout stream
      //
      if (theOutputTextFile != null)
         theSQLOutput = new streamReader2TextFile (proc.getInputStream (), theOutputTextFile);
      else
         theSQLOutput = new streamReader2TextList (proc.getInputStream ());

      // create stderr stream
      //
      if (theErrorTextFile != null)
         theSQLError  = new streamReader2TextFile (proc.getErrorStream (), theErrorTextFile);
      else
         theSQLError  = new streamReader2TextList (proc.getErrorStream ());

      // star the streams
      //
      theSQLError.start ();
      theSQLOutput.start ();
      introd.start ();

      // wait for process & reading buffers finalization
      //
      int exitVal = -1;
      try
      {
         // wait for process finalization
         //
         exitVal = proc.waitFor();

         // wait for reader buffers finalization
         //
         while (!theSQLOutput.hasFinished () || !theSQLError.hasFinished ())
            Thread.sleep (50);
      }
      catch (Exception e)
      {
         log.err ("sqLiteCall", "exception during process execution [" + getClientExePath() + "]!" + e);
         if (traceFile != null)
            traceFile.fclose ();
         return false;
      }

      //---- ALL HAS FINISHED
      long endingTime = System.currentTimeMillis ();

      log.dbg (2, "sqLiteCall", "end executing it took " + (endingTime-startingTime) /1000.);
      if (traceFile != null)
      {
         String time_stamp = (new DateFormat ("yyyy.MM.dd HH:mm:ss.S", new java.util.Date())).get ();
         traceFile.writeLine ("*** STD-OUTPUT (" + (endingTime-startingTime) /1000. + " s) on " + time_stamp);

         if (theOutputTextFile != null) traceFile.writeFileContents (theOutputTextFile);
         else
         {
            for (int ii = 0; ii < theSQLOutput.countLines (); ii ++)
               traceFile.writeLine (theSQLOutput.getLine (ii));
         }

         if (theSQLError.countLines () > 0)
         {
            traceFile.writeLine ("!!! ERROR-OUTPUT");
            if (theErrorTextFile != null) traceFile.writeFileContents (theErrorTextFile);
            else
            {
               for (int ii = 0; ii < theSQLError.countLines (); ii ++)
                  traceFile.writeLine (theSQLError.getLine (ii));
            }
         }
         traceFile.writeLine ("*** END-OUTPUT");
         traceFile.fclose ();
         traceFile = null;
      }


      // custom...FileName's has only one use!
      //
      lastUsedDatabase = database;

      // ... )))
      if (exitVal == -1)
           sqlSignaler.signalError ();
      else sqlSignaler.signalEnd ();

      // read output file
      //
      if (theSQLError.countLines () > 0)
      {
         if (theErrorTextFile != null)
              log.err ("sqLiteCall", "SQL call with ouput errors (in file " + theErrorTextFile + ")");
         else log.err ("sqLiteCall", "SQL call with ouput errors : [" + theSQLError.getLine (0) +"]");
      }

      int errDetect = (theOutputTextFile != null) ?
                     sqliteErrorDetection ((streamReader2TextFile) theSQLOutput):
                     sqliteErrorDetection ((streamReader2TextList) theSQLOutput);
      if (errDetect >= 0)
         log.dbg (2, "sqLiteCall", "inspected output, " + (errDetect == 0 ? "no ": "") + " errors detected");

      return (theSQLOutput.countLines () > 0 && exitVal != -1);
   }

   public int sqliteErrorDetection (streamReader2TextList strOutput)
   {
      int readErrors = 0;
      ensureErrorPatterns ();

      for (int jj = 0; jj < strOutput.countLines(); jj ++)
      {
         for (int ii = 0; ii < sqliteErrorPatterns.length; ii ++)
         {
            String str = strOutput.getLine (jj);
            Matcher ma = sqliteErrorPatterns[ii].matcher(str);
            if (ma.find ())
            {
               int maxi = Math.min (str.length () - ma.start (), 200);
               log.err ("sqliteErrorDetection", str.substring (ma.start (), maxi));
               readErrors ++;
            }
         }
      }
      return readErrors;
   }

   public int sqliteErrorDetection (streamReader2TextFile strOutput)
   {
      int readErrors = 0;
      ensureErrorPatterns ();

      // read and parse getOutFileName() for known sqlite error ouputs
      //
      int readLines = 0;
      TextFile fie = new TextFile ();
      if (fie.fopen (theOutputTextFile, "r"))
      {
         while (fie.readLine ())
         {
            readLines ++;
            for (int ii = 0; ii < sqliteErrorPatterns.length; ii ++)
            {
               if (sqliteErrorPatterns[ii].matcher(fie.TheLine()).find ())
               {
                  log.err ("sqliteErrorDetection", fie.TheLine ());
                  readErrors ++;
               }
            }
         }
         fie.fclose ();
      }
      else
      {
         log.severe ("sqliteErrorDetection", "cannot read output file " + theOutputTextFile);
      }

      log.dbg (2, "sqliteErrorDetection", "parsed " + readLines + " lines, found " + readErrors + " errors");
      return readErrors;
   }

   /// sqlite error patterns
   private Pattern [] sqliteErrorPatterns = null;

   /// compile error patterns just once
   private void ensureErrorPatterns ()
   {
      if (sqliteErrorPatterns != null) return;

      sqliteErrorPatterns = new Pattern [6];

      try
      {
         sqliteErrorPatterns[0] = Pattern.compile ("^SQL error");
         sqliteErrorPatterns[1] = Pattern.compile ("^Incomplete SQL");
         sqliteErrorPatterns[2] = Pattern.compile ("^Error: near line");
         sqliteErrorPatterns[3] = Pattern.compile ("[\n\r]SQL error");
         sqliteErrorPatterns[4] = Pattern.compile ("[\n\r]Incomplete SQL");
         sqliteErrorPatterns[5] = Pattern.compile ("[\n\r]Error: near line");
      }
      catch (PatternSyntaxException e)
      {
         log.severe ("ensureErrorPatterns", "PatternSyntaxException for sqlite error patterns!, " + e);
      }
      catch (Exception e)
      {
         log.severe ("ensureErrorPatterns", "exception compiling expresion for sqlite error patterns!, " + e);
      }
   }

   private static boolean bCheckDone = false;
   private static boolean bClientOk  = false;
   private static String sqliteClientVersion = "?";
   private static String SQLITE_CLIENT_EXE = null;

   public static String getClientExePath ()
   {
      if (SQLITE_CLIENT_EXE != null && utilSys.isSysUnix)
      {
         return SQLITE_CLIENT_EXE;
      }
      // in Windows CHECK always the sqlite3.exe (see note in microToolInstaller)
      SQLITE_CLIENT_EXE = microToolInstaller.getExeToolPath ("sqlite");

      // even if ToolInstaller doesn't work try to find it somehow
      //
      if (SQLITE_CLIENT_EXE == null || SQLITE_CLIENT_EXE.length () == 0)
      {
         //log.warn ("checkClient", "Cannot found sqlite executable, simply try sqlite3!");
         SQLITE_CLIENT_EXE = "sqlite3";
      }
      return SQLITE_CLIENT_EXE;
   }

   public boolean checkClient ()
   {
      if (bCheckDone) return bClientOk;
      bCheckDone = true;

      // create the process
      //
      Process proc = null;
      try
      {
         // create the process
         //
         //(o) JAVA EXEC !!!!
         proc = Runtime.getRuntime().exec (new String [] { getClientExePath (), "-version"} );
      }
      catch (Exception e)
      {
         log.err ("checkClient", "exception creating process [" + getClientExePath() + " -version]!" + e);
         return false;
      }

      // create the stream passes (process "stderr" to our "stderr" and also with "stdout")
      //
      streamReader2TextList buffErr = new streamReader2TextList (proc.getErrorStream ());
      streamReader2TextList buffOut = new streamReader2TextList (proc.getInputStream ());

      // star them
      //
      buffErr.start ();
      buffOut.start ();

      // wait for process finalization
      //
      int exitVal = -1;
      try
      {
         exitVal = proc.waitFor();
         // wait for reader buffers finalization
         //
         while (!buffOut.hasFinished () || !buffErr.hasFinished ())
            Thread.sleep (50);
      }
      catch (Exception e)
      {
         log.err ("sqLiteCall", "exception during process execution [" + getClientExePath() + " -version]!" + e);
         return false;
      }

      // ... )))
      if (exitVal == -1)
      {
         // log fatal error
         log.severe ("checkClient", "Severe error calling sqlite client (command line sqlite) [" + getClientExePath () + "] !");

         // send it as message for possible widgets interested in (zLeds etc)
         sqlSignaler.signalError ();
         return false;
      }

      if (buffErr.countLines () > 0)
      {
         log.err ("checkClient", "return from sqlite3 -version [" + buffErr.getLine (0) + "]");
      }

      if (buffOut.countLines() > 0)
      {
         sqliteClientVersion = buffOut.getLine (0).trim ();
         log.dbg (2, "checkClient", "return from sqlite3 -version [" + sqliteClientVersion + "]");

         // check if the first character is a number
         char first = '?';
         if (sqliteClientVersion.length () > 1)
            first = sqliteClientVersion.charAt(0);

         bClientOk = first >= '0' && first <= '9';
      }

      if (!bClientOk)
      {
         //determine the cause!
         File fi = new File (getClientExePath ());
         if (fi.exists ())
         {
            //(o) TOSEE problems with this message. Why does not work "sqlite -version" sometimes ????
            log.dbg (2, "checkClient", "sqlite3 executable version cannot be checked! [" + sqliteClientVersion + "]");
            bClientOk = true; // que lo vuelva a intentar ...
         }
         else
         {
            log.severe ("checkClient", "sqlite3 executable [" + getClientExePath () + "] not found! (checking version = [" + sqliteClientVersion + "])");
         }

         // send it as message for possible widgets interested in (zLeds etc)
         sqlSignaler.signalError ();
      }

      return bClientOk;
   }


   // ===============================================================
   /**
      ----------- Escape / unEscape strings, compact and expand records facilities

      in future clientCaller will offer more escape policies in its constructor
      and these method will take the appropiate method. At the moment there is only one
      possible escape policy, the one from utilEscapeStr.

   */
   public String escapeString (String str)
   {
      return utilEscapeStr.escapeStr (str);
   }

   public String unEscapeString (String str)
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


   //(o) TODO_elxala_sqlite_clientCaller prepar some static method to call sqlite directly

   public static void main (String [] aa)
   {
      sqlSolver cli = new sqlSolver ();
      if (cli.checkClient())
           System.out.println ("check " + cli.getClientExePath () + " ok version = " + sqliteClientVersion);
      else System.out.println ("cannot find sqlite client " + cli.getClientExePath () + " !");
   }
}
