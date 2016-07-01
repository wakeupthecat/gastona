/*
packages de.elxala
(c) Copyright 2005,2106 Alejandro Xalabarder Aulet

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

package de.elxala.zServices;

/**
   TODO:

   [] deprecar, comprobar que no se usan
         public static final int LOG_START = 20;
         public static final int LOG_STEP  = 21;
         public static final int LOG_END   = 30;

         // start index of custom logs
         public static final int LOG_CUSTOM_FIRST = 100;

   [] Implementar custom logs en javaj y mensaka, por ejemplo...

      CREATE TABLE logCustom_javaj_flow     (msgCounter, milliStamp, level, context, message);
      CREATE TABLE logCustom_mensaka_agents (msgCounter, milliStamp, level, agIndex, agName, agObjString);
      CREATE TABLE logCustom_mensaka_msgs   (msgCounter, milliStamp, level, agIndex, type(subscribe/declare/send/received/sendEnd), msgindx);

*/

import java.io.File;
import java.util.List;
import java.util.Vector;
import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.mensaka.*;

/**
   @date 10.04.2008 21:54
   @name logger
   @author Alejandro Xalabarder

   @brief
   Provides logging service


   Notes:
         - a module or any java class has to use an instance of logger (de.elxala.zServices.logger) to produce log

*/
public class logServer
{
   public static final String EVACONF_LOG_LEVELS_BY_CLIENT = "logLevels";

   // logs to stop the application
   public static final int LOG_PANIC    = 1; //only allowed (or expected) by base library, gastona, javaj, listix or mensaka
   public static final int LOG_FATAL    = 2; //thought for the rest of applications

   // error and warnings (application might continue)
   public static final int LOG_SEVERE_ERROR = 3;  // error that should be fixed, until this level the stack trace will be logged if log directory is provided
   public static final int LOG_ERROR    = 4;      // error that should be fixed
   public static final int LOG_WARNING  = 5;      // advise of malfunction
   public static final int LOG_MESSAGE  = 6;      // will be printed out even if not logging (like a System.out.println)

   // debug mesages
   public static final int LOG_DEBUG_0   = 10;  // minimal verbosity
   public static final int LOG_DEBUG_1   = 11;  // normal verbosity (default debug level)
   public static final int LOG_DEBUG_2   = 12;  // ...
   //...
   public static final int LOG_DEBUG_MAX = 19;  // deepest debug message

   // messages for flow control
   public static final int LOG_START = 20;
   public static final int LOG_STEP  = 21;
   public static final int LOG_END   = 30;

   // start index of custom logs
   public static final int LOG_CUSTOM_FIRST = 100;

   private static int MAX_TEXT = 10000; // aprox 10 Kb

   // same default values as in ::resetStatic ()
   //
   private static int firstClientID = 100;
   private static long globCounter = 10000;
   private static List listClientNames = new Vector ();     // client Names       vector<String>
   private static int globalLogLevel = LOG_DEBUG_1;
   private static String directory2Log = null;
   private static EvaUnit currentLogConfig = null;

   private static logClient meAsClient = null;
   private static int currentValidStamp = 10;

   private static String staticConfigName = "anonimus";
   private static String staticConfigNote = "";

   private static String   logErrorsFileName = "";
   private static TextFile logErrorsFile = null;
   private static TextFile logBatchFile = null;
   private static boolean errorDue2FopenDone = false;


   public static void resetStatic ()
   {
      MAX_TEXT = 10000; // aprox 10 Kb
      firstClientID = 100;
      globCounter = 10000;
      listClientNames = new Vector ();     // client Names       vector<String>

      configure (LOG_DEBUG_1);

      directory2Log = null;
      currentLogConfig = null;
      meAsClient = null;
      staticConfigName = "anonimus";
      staticConfigNote = "";

      logErrorsFileName = "";
      logErrorsFile = null;
      logBatchFile = null;
      errorDue2FopenDone = false;
   }


   public static int getDefaultMaxLogLevel ()
   {
      return LOG_DEBUG_1;
   }


   /**
      if you are using directory to log then just configure the logServer with a directory name
      the rest will has to be configured through the file "logServer.configure" in this directory
   */
   public static void configure (String logDirectory, String configuratorName, String configuratorNote)
   {
      {
         //(o) elxala_logServer_notes log clients and the exception of TextFile
         // Instanciate hier a dummy textfile to ensure that the default static log of
         // TextFile is registered while directory2Log is still null !!!
         // Note that TextFile is used in loadEvaUnit
         // Has to be find a better solution but note that simply using a variable (ACCEPT_NEW_CLIENTS = false;)
         // is not enough since if the logger is static (like in TextFile) the register is only made once, and if this
         // is avoided then the client will be never registered for the rest of the program life!
         //
         TextFile vamos = new TextFile ();
         vamos = null;
      }

      staticConfigName = configuratorName;
      staticConfigNote = configuratorNote;

      directory2Log = logDirectory;
      if (directory2Log != null)
      {
         EvaUnit euConf = EvaFile.loadEvaUnit (directory2Log + "logConfiguration.eva", "data");
         if (euConf != null)
         {
            currentLogConfig = euConf;
            currentValidStamp ++;
         }
         else
         {
            // there is no current logConfiguration.eva file in log directory
            // we create an empty one to help the user, but it will not be used in this session! (currentLogConfig remains null)

            //(o) techNotes/logServer/Depencency with TextFile
            TextFile defConf = new TextFile (null);

            if (defConf.fopen (directory2Log + "logConfiguration.eva", "w"))
            {
               defConf.writeLine ("#data#");
               defConf.writeLine ("");
               defConf.writeLine ("    <" + EVACONF_LOG_LEVELS_BY_CLIENT + ">"); // <logLevels>
               defConf.writeLine ("          clientName, maxLogLevel");
               defConf.writeLine ("");
               defConf.writeLine ("#**FIN_EVA#");
               defConf.fclose ();
            }
         }
         // System.out.println ("TENGO .. TENGO UNA RAMISA NUEVA ..." + currentLogConfig);


         //System.out.println ("currentLogConfig is " + currentLogConfig);
         //System.out.println ("REGISTER TEMPRANOS!  con listClientNames.size () " + listClientNames.size ());
         // log all "early" log clients

         // to ensure that openBatchFile really create the tables
         if (logBatchFile != null) logBatchFile.fclose ();
         logBatchFile = null;

         if (listClientNames.size () > 0 && openBatchFile ())
         {
            for (int ii = 0; ii < listClientNames.size (); ii ++)
            {
               String cliName = (String) listClientNames.get (ii);
               //TEST-TEST-TEST-TEST-TEST-TEST
               //int cliLevel = -1; //getClientLogLevel (cliName);
               int cliLevel = getClientLogLevel (cliName);

               // System.out.println ("LOGO A \"" + cliName + "\" con su cliLevel " + cliLevel + " !!!");
               logOneRegisteredClient (firstClientID + ii, cliName, cliLevel, null);
            }
            closeBatchFile ();
         }
      }
   }

   public static void configure (int maxGlobalLevel)
   {
      if (maxGlobalLevel == -1) return;
      globalLogLevel = maxGlobalLevel;
      logNativePrinter.message ("logServer", "Configure globalLogLevel " + maxGlobalLevel);
   }

   public static void configure (int maxGlobalLevel, Eva clientConf)
   {
      //System.out.println ("ME LAMAN " + maxGlobalLevel + " eva::"  + clientConf);

      configure (maxGlobalLevel);
      if (clientConf != null)
      {
         logNativePrinter.message ("logServer", "given list of client configuration of size " + (clientConf.rows () - 1));
         if (currentLogConfig == null)
         {
            currentLogConfig = new EvaUnit ("data");
         }
         currentValidStamp ++;

         currentLogConfig.remove (EVACONF_LOG_LEVELS_BY_CLIENT);
         clientConf.setName (EVACONF_LOG_LEVELS_BY_CLIENT);
         currentLogConfig.add (clientConf);
      }
   }

   // NOTE : it is protected but might be accessed through an instance of logger
   protected static String getLogDirectory()
   {
      return directory2Log;
   }

//   public static void configure (String client, int [] levels)
//   {
//      System.err.println ("ERROR: logServer esto no funciona todavi'a campeo'n!");
//       // futuro
//   }

   private static boolean openBatchFile ()
   {
      String modus = "a";
      if (logBatchFile == null)
      {
         //(o) techNotes/logServer/Depencency with TextFile
         logBatchFile = new TextFile (null);
         modus = "w";
      }
      if (! logBatchFile.fopen (getLogDirectory () + "logSession.sql", modus))
      {
         if (! errorDue2FopenDone)
         {
            // write just once a message to note this problem
            System.err.println ("FATAL ERROR: de.elxala.zServices.logServer the file \"" + getLogDirectory () + "logSession.sql" + "\" could not be opened for output at least once!");
            errorDue2FopenDone = true;
         }

         return false;
      }

      if (modus.equals("w"))
      {
         //(o) elxala_logServer create tables (register header)
         //
         logBatchFile.writeLine ("CREATE TABLE logClients (clientID int, milliStamp int, clientName text, maxLogLevel int, clientFirstObj text, UNIQUE(clientID));");
         logBatchFile.writeLine ("CREATE TABLE logMessages (msgCounter int, milliStamp int, clientID int, level int, context text, message text, UNIQUE(msgCounter));");
         logBatchFile.writeLine ("CREATE TABLE logStack4Errors (msgCounter int, stackInfo text, UNIQUE(msgCounter));");
         logBatchFile.writeLine ("CREATE TABLE logStacksOnError (msgCounter int, stackType text, nLine int, stackItem text, UNIQUE(msgCounter, stackType, nLine));");

         // o-o  Add dbMore connections info
         logBatchFile.writeLine (deepSqlUtil.getSQL_CreateTableConnections ());
         logBatchFile.writeLine (deepSqlUtil.getSQL_InsertConnection("cli", "logMessages", "clientID", "logClients", "clientID"));
         logBatchFile.writeLine (deepSqlUtil.getSQL_InsertConnection("msg", "logStack4Errors", "msgCounter", "logMessages", "msgCounter"));
         logBatchFile.writeLine (deepSqlUtil.getSQL_InsertConnection("msg", "logStacksOnError", "msgCounter", "logMessages", "msgCounter"));

         // trigger for the first message (log server message)
         pendingFirstMessage = true;

//         //( o) elxala_logServer very first message! from logServer time stamp of currentMillis 0
//         //
//         String time0Stamp = (new DateFormat ("yyyy.MM.dd HH:mm:ss.S", new java.util.Date(time0milliseconds))).get ();
//
//         globCounter ++;
//         logBatchFile.writeLine ("INSERT INTO logMessages VALUES (" +
//                                (globCounter + "") + ", '" +
//                                (elapsedMillis() + "") + "', " +
//                                "0" + ", '" +                   // client 0 = logServer
//                                (LOG_DEBUG_0 + "") + "', '" +   // it could be for example 0 since there is no message level associated to 0, but it wouldn't be a nice value ;)
//                                "startLogServer" + "', '" +
//                                time0Stamp + "'" +
//                                ");");
      }
      return true;
   }

   private static void closeBatchFile ()
   {
      logBatchFile.fclose ();
   }

   // give a filename to store all errors, if any
   // it works independently of other log configurations
   public static void configureErrorLog (String fileName)
   {
      logErrorsFileName = fileName;
      //(o) elxala_logServer_notes log clients and the exception of TextFile
      logErrorsFile = new TextFile (null);
   }

   private static void logError (String context, String errorMsg)
   {
      if (context != null)
         logNativePrinter.error (context, errorMsg);
      storeError (errorMsg);
   }

   public static void storeError (String errorMsg)
   {
      if (logErrorsFile == null) return; // not configured to be logged

      if (logErrorsFile.fopen (logErrorsFileName, "a"))
      {
         logErrorsFile.writeLine (errorMsg);
         logErrorsFile.fclose ();
      }
      else
         onceErrorLogCannotBeOpened (logErrorsFileName);

   }

   private static void onceErrorLogCannotBeOpened (String fileName)
   {
      if (errorDue2FopenDone) return;

      // write just once a message to note this problem
      String errMsg = "FATAL ERROR: de.elxala.zServices.logServer the file \"" + fileName + "logSession.sql" + "\" could not be opened for output at least once!";
      logError ("logServer", errMsg);
      errorDue2FopenDone = true;
   }

   public static void registerClient (logClient cli)
   {
      if (cli == null || cli.clientStr.equals (""))
      {
         //System.out.println ("Anonym log client!");
         return ;
      }

      //System.out.println ("VEAMOS si REGISTRAMOS A \"" + cli.clientStr + "\" !!");
      boolean alreadyRec = listClientNames.contains (cli.clientStr);

      //System.out.println ("VAYA! YA " + ((alreadyRec) ? "SI": "NO") + "ESTABA!");

      //(o) elxala_logServer_TODO Syncronize to allow multithreading

      //(o) elxala_logServer tracking just log clients with different names (design decission)
      if (alreadyRec)
      {
         if (cli.clientID == -1) // The client is registered but clientID not set yet ?
         {
            //System.out.println ("ENTRAMOS EN EL CASO OBSCURO!");
            // This happens when a class that holds the client is more times instanciated
            // of course this is allowed but logServer will treat it as the same log client since
            // we track just different client names. Note that this could be done in other way

            // just look for its first ID and set to it
            int indx = listClientNames.indexOf (cli.clientStr);
            if (indx != -1)
               cli.clientID = firstClientID + indx;
            // else "strange!"

            //cli.myMaxLevel = getClientLogLevel (cli.clientStr);
         }
         // else alreadyRec and clientID assigned .. nothing to do
      }
      else // not alreadyRec
      {
         //System.out.println ("BIEN VAMOS PAYA MACHO \"" + cli.clientStr + "\"");
         // record it in the list
         cli.clientID = firstClientID + listClientNames.size ();
         listClientNames.add (cli.clientStr);

         //System.out.println ("YA ESTAS METID \"" + cli.clientStr + "\"");

         // CRITIC STEP :

         cli.myMaxLevel = getClientLogLevel (cli.clientStr);
         cli.levelValidStamp = currentValidStamp;
         // System.out.println ("LOGO AL MONOLITICO \"" + cli.clientStr + "\" con su cliLevel " + cli.myMaxLevel + " !!!");
         logRegisteredClient (cli);
         //System.out.println ("HEMOS registerado a '" + cli.clientStr + "'");
      }
   }

   private static void logRegisteredClient (logClient cli)
   {
      //(o) elxala_logServer logging registered clients

      if (directory2Log == null) return; // not yet possible ... wait until having directory2Log (if this can happens!)

      if (pendingFirstMessage)
      {
         doFirstMessage ();
      }
      if (openBatchFile ())
      {
         //System.out.println ("logRegisteredClient \"" + cli.clientStr + "\"");
         // normal log. directory2Log exists and the client has already logLevel assigned (or default)
         //
         logOneRegisteredClient (cli.clientID, cli.clientStr, cli.myMaxLevel, cli.clientObj);

         // if it is a custom log client, let's create its custom table
         // NOTE: if there are more instances of clients with the same client name note that
         //       we create the table just for the first one, therefore all these clients must have the same custom extra fieds array!
         //       Unfortunately this cannot be easily checked here :(
         //       At least when the second "wrong" clients wants to write a message
         //       a severe error will be throught if the sizes of arrExtraFields and extraInfo arrays wouldn't match (:|
         //
         if (cli.arrExtraFields != null)
         {
            StringBuffer specialFields = new StringBuffer ();
            for (int ii = 0; ii < cli.arrExtraFields.length; ii ++)
            {
               specialFields.append (", " + cli.arrExtraFields[ii] + " text");
            }
            logBatchFile.writeLine ("CREATE TABLE logCustom_" + cli.clientStr + " (msgCounter int, milliStamp int, clientID int, level int, context text, message text" + specialFields.toString () + ");");

            // o-o  Add deepSql connections info
            logBatchFile.writeLine (deepSqlUtil.getSQL_InsertConnection("cli", "logCustom_" + cli.clientStr, "clientID", "logClients", "clientID"));

            //it is not related on logMessages (right now all in a separate table) 31.01.2010 21:17
            //logBatchFile.writeLine (deepSqlUtil.getSQL_InsertConnection("msg", "logCustom_" + cli.clientStr, "msgCounter", "logMessages", "msgCounter"));
         }

         if (cli.ownConnections != null)
         {
            for (int ii = 0; ii < cli.ownConnections.length; ii ++)
            {
               String [] oneConn = cli.ownConnections[ii];
               // o-o  Add deepSql connections info
               logBatchFile.writeLine (deepSqlUtil.getSQL_InsertConnection(oneConn[0], "logCustom_" + oneConn[1], oneConn[2], "logCustom_" + oneConn[3], oneConn[4]));
            }
         }

         closeBatchFile ();
      }
   }

   private static void logOneRegisteredClient (int cliID, String cliName, int cliMaxLevel, Object cliObj)
   {
      // NOTE : BatchFile has to be opened!

      logBatchFile.writeLine ("INSERT INTO logClients VALUES (" +
                             (cliID + "") + ", '" +
                             (elapsedMillis() + "") + "', '" +
                             cliName + "', '" +
                             cliMaxLevel + "', '" +
                             de.elxala.db.utilEscapeStr.escapeStrTruncate("" + cliObj, MAX_TEXT) + "'" +
                             ");");
   }

   protected static int getClientLogLevel (String cliName)
   {
      // returns the configured maxLogLevel for the client from <logLevels> of #data# of logConfiguration.eva

      // get the default max log level
      int maxLevel = getDefaultMaxLogLevel();

      if (currentLogConfig != null)
      {
         Eva logLevels = currentLogConfig.getEva (EVACONF_LOG_LEVELS_BY_CLIENT);
         if (logLevels == null)
         {
            logNativePrinter.warning ("logServer", "WARNING: no <logLevels> found in logConfiguration.eva!");
            return maxLevel;
         }

         //avoid problem of rowOf a partir de la primera columna!
         String colName00 = logLevels.getValue (0,0);
         logLevels.setValue ("*", 0,0);
         int row = logLevels.rowOf (cliName);
         logLevels.setValue (colName00, 0,0);

         //System.out.println ("row of client [" + cliName + "] is " + row + " in <logLevels>");
         if (row != -1)
         {
            maxLevel = stdlib.atoi (logLevels.getValue (row, 1));
            //System.out.println ("max level is " + currentLogConfig);
         }
      }
      //else  System.out.println ("CAHARAMBAS! currentLogConfig is null ???????");

      return maxLevel;
   }

   // NOTE : it is protected but the information might be accessed through an instance of logger
   protected static boolean isLogging (logClient cli, int msgLevel)
   {
      if (msgLevel <= globalLogLevel)
      {
         // for all clients, if less than globalLogLevel then is logging true
         return true;
      }

      if (currentLogConfig == null)
      {
         // cannot determine specific level for any client, use default level for all
         return msgLevel <= globalLogLevel;
      }

      // (currentLogConfig != null)
      // specific level can be determined for all clients
      // check if level already assigned to the client
      if (cli.unknownLevel () || cli.levelValidStamp < currentValidStamp)
      {
         //System.out.println ("Este cliente es un desconocido total " + cli.clientStr);
         cli.myMaxLevel = getClientLogLevel (cli.clientStr);
         cli.levelValidStamp = currentValidStamp;
         //System.out.println ("AHORA tenemos " + cli.myMaxLevel);
      }
      //else
      //{
      //   System.out.println ("Es bien conocido que este cliente " + cli.clientStr + " tiene un level de " + cli.myMaxLevel);
      //}

      // now the client level must be ok
      if (msgLevel <= cli.myMaxLevel)
         return true;

      //(o) TODO_elxala_logServer (?) Decide if a finer array of level is needed
      //      if (cli.arrLevelsOn == null || cli.arrLevelsOn.length == 0)
      //         return false;
      //
      //      for (int ii = 0; ii < cli.arrLevelsOn.length; ii ++)
      //         if (cli.arrLevelsOn[ii] == msgLevel)
      //            return true;

      return false;
   }

   protected static void doFirstMessage ()
   {
      logClient meAsClient = new logClient();
      meAsClient.clientID = 0;

      String time0Stamp = (new DateFormat ("yyyy.MM.dd HH:mm:ss.S", new java.util.Date(time0milliseconds))).get ();

      long startMillis = elapsedMillis();
      storeMessage (meAsClient, globalLogLevel, "startLogServer", time0Stamp, null, null);
      storeMessage (meAsClient, globalLogLevel, staticConfigName, staticConfigNote, null, null);
      storeMessage (meAsClient, globalLogLevel, "estimatedStoreLogCost", (elapsedMillis() - startMillis) + "", null, null);
   }

   /**
   */
   private static boolean pendingFirstMessage = false;
   private static boolean yoMismo = false;

   protected static void storeMessage (logClient cli, int msgLevel, String context, String message, String [] extraInfo, StackTraceElement[] stackElements)
   {
      if (! isLogging (cli, msgLevel))
      {
         return;
      }

      if (pendingFirstMessage)
      {
         pendingFirstMessage = false;  //important since doFirstMessage call storeMessage!
         doFirstMessage ();
      }

      //(o) TODO_elxala_logServer Syncronize to allow multithreading

      if (yoMismo)
      {
         // Detect calls to storeMessage while storing a message!

         // estudiar si el nivel de entrada tiene ma's prioridad de lo que
         // que se esta' loggando y en ese caso quiza' "encolarlo"
         return;
      }
      yoMismo = true;

      // check extraFields for custom messages
      boolean isCustomLogMessage = false;
      if (extraInfo != null)
      {
         // check that it is conform custom message
         // Note that if extraInfo and arrExtraFields sizes are not equal the INSERT sql will not work at all
         //
         isCustomLogMessage = (cli.arrExtraFields != null && cli.arrExtraFields.length == extraInfo.length);

         if (! isCustomLogMessage)
         {
            // CHANGE PLANS! Now it would be an error! also change client!
            message = "BAD USE OF CUSTOM LOG MESSAGES! client " +
                      cli.clientID + " (" + cli.clientStr + ") " +
                      " wanted to write message [" + message + "] in context " + context +
                      " with a extraInfo of size " + extraInfo.length + " but extraInfo size has to be " +
                      (cli.arrExtraFields == null ? "0": "" + cli.arrExtraFields.length);
            context = "logServer::storeMessage";
            extraInfo = null;
            msgLevel = LOG_SEVERE_ERROR;
            cli.clientID = 0; // this is logServer client ID
         }
      }

      // always a console output if error
      //
      if (msgLevel <= LOG_ERROR)
      {
         StringBuffer strMsgError = new StringBuffer ();

         strMsgError.append ("ERROR(" + msgLevel + ") in " + cli.clientStr + " : " + context + " : " + message + "\n");


         // listix format call stack
         String [] listixStack = listix.listix.getLastFormatStack ();
         strMsgError.append ("LISTIX STACK:" + "\n");
         for (int ii = 0; ii < listixStack.length; ii ++)
            strMsgError.append ("   " + ii + ") [" + listixStack[ii] + "]\n");

         // mensaka message stack
         String [] mensakaStack = Mensaka.getLastMessageStack ();

         strMsgError.append ("MENSAKA STACK:" + "\n");
         for (int ii = 0; ii < mensakaStack.length; ii ++)
            strMsgError.append ("   " + ii + ") [" + mensakaStack[ii] + "]\n");

         if (msgLevel <= LOG_SEVERE_ERROR)
         {
            //(o) elxala_logServer_TOSEE for the dump stack, review if use Thread.dumpStack () instead

            // track java call stack
            //
            StackTraceElement [] arrStkElem = (stackElements != null) ? stackElements: jsys.getNowStackTrace();
            for (int ii = 0; ii < arrStkElem.length; ii ++)
            {
               strMsgError.append (arrStkElem[ii].toString () + "\n");
            }
         }
         logError (cli.clientStr, strMsgError.toString ());
      }
      else
      {
         StringBuffer msgForm = new StringBuffer ((context.length () > 0) ? context + " : ":"");
         msgForm.append (message);
         if (extraInfo != null)
         {
            msgForm.append (" [");
            for (int ii = 0; ii < extraInfo.length; ii ++)
               msgForm.append ((ii == 0 ? "": "|") + extraInfo[ii]);
            msgForm.append ("]");
         }

         logNativePrinter.message (cli.clientStr, msgForm.toString ());
      }

      if (getLogDirectory () != null)
      {
         if (openBatchFile ())
         {
            //(o) elxala_logServer record message
            //
            globCounter ++;

            if (isCustomLogMessage)
            {
               // Custom table message
               //write custom log
               StringBuffer specialValues = new StringBuffer ();
               for (int ii = 0; ii < extraInfo.length; ii ++)
               {
                  specialValues.append (", '" + de.elxala.db.utilEscapeStr.escapeStrTruncate(extraInfo[ii], MAX_TEXT) + "'");
               }
               logBatchFile.writeLine ("INSERT INTO logCustom_" + cli.clientStr + " VALUES (" +
                                      (globCounter + "") + ", '" +
                                      (elapsedMillis() + "") + "', " +
                                      cli.clientID + ", '" +
                                      (msgLevel + "") + "', '" +
                                      context + "', '" +
                                      de.elxala.db.utilEscapeStr.escapeStrTruncate(message, MAX_TEXT) + "'" +
                                      specialValues.toString () +
                                      ");");
            }
            else
            {
               // Normal message
               logBatchFile.writeLine ("INSERT INTO logMessages VALUES (" +
                                      (globCounter + "") + ", '" +
                                      (elapsedMillis() + "") + "', " +
                                      cli.clientID + ", '" +
                                      (msgLevel + "") + "', '" +
                                      context + "', '" +
                                      de.elxala.db.utilEscapeStr.escapeStrTruncate(message, MAX_TEXT) + "'" +
                                      ");");
            }

            // DUMP STACKS if needed, Note that all stacks use the same message counter (globCounter) as the current
            // message, thus globCounter cannot be incremented now
            //

            if (msgLevel <= LOG_ERROR) // or LOG_WARNING ? ...
            {
               // listix format call stack
               String [] listixStack = listix.listix.getLastFormatStack ();

               for (int ii = 0; ii < listixStack.length; ii ++)
               {
                  logBatchFile.writeLine ("INSERT INTO logStacksOnError VALUES (" +
                                         (globCounter + "") +
                                         ", 'listix formats'" +
                                         ", " + ii +
                                         ", '" + de.elxala.db.utilEscapeStr.escapeStr(listixStack[ii]) + "');");
               }

               // mensaka message stack
               String [] mensakaStack = Mensaka.getLastMessageStack ();

               for (int ii = 0; ii < mensakaStack.length; ii ++)
               {
                  logBatchFile.writeLine ("INSERT INTO logStacksOnError VALUES (" +
                                         (globCounter + "") +
                                         ", 'mensaka msgs'" +
                                         ", " + ii +
                                         ", '" + de.elxala.db.utilEscapeStr.escapeStr(mensakaStack[ii]) + "');");
               }
            }

            if (msgLevel <= LOG_SEVERE_ERROR)
            {
               //(o) elxala_logServer_TOSEE for the dump stack, review if use Thread.dumpStack () instead

               // Register/track current java call stack
               //
               StackTraceElement [] arrStkElem = (stackElements != null) ? stackElements: jsys.getNowStackTrace();

               // ========== NEW Stack log
               for (int ii = 0; ii < arrStkElem.length; ii ++)
               {
                  logBatchFile.writeLine ("INSERT INTO logStacksOnError VALUES (" +
                                         (globCounter + "") +
                                         ", 'java call'" +
                                         ", " + ii +
                                         ", '" + de.elxala.db.utilEscapeStr.escapeStr(arrStkElem[ii].toString ()) + "');");
               }
               // ===

               // ========== OLD Stack log
               StringBuffer stackMsg = new StringBuffer ();
               for (int ii = 0; ii < arrStkElem.length; ii ++)
               {
                  stackMsg.append ("\n");
                  stackMsg.append (arrStkElem[ii]);
               }
               logBatchFile.writeLine ("INSERT INTO logStack4Errors VALUES (" + (globCounter + "") + ", '" + de.elxala.db.utilEscapeStr.escapeStr(stackMsg.toString ()) + "');");
               // ===

            }

            closeBatchFile ();
         }
      }

//(o) TODO_elxala_logServer Reserve panic only for basic library classes (de.elxala / gastona / listix / javaj)
//      if (msgLevel == LOG_PANIC) &&
//         (! cli instance of de.elxala.mensaka) ||
//          ! cli instance of gastona.gastona) ||
//          ! cli instance of listix.listix) ||
//          ! cli instance of javaj.javaj)
//      {
//         msgLevel = LOG_FATAL;
//      }


      if (msgLevel == LOG_PANIC || msgLevel == LOG_FATAL)
      {
   //         Class caller = sun.reflect.Reflection.getCallerClass (1);
   //      do
   //      {
   //         Class cla = sun.reflect.Reflection.getCallerClass (stackLev);
   //         if (cla == null) return callerNo;
   //         if (callerNo != cla) return cla;
   //         // System.out.println ("\nJSYS: i throw " + cla.getName ());
   //         stackLev ++;
   //      } while (true);
   //         String stackMsg = caller.toString ();

         StringBuffer stackMsg = new StringBuffer ();
         StackTraceElement [] arrStkElem = (stackElements != null) ? stackElements: jsys.getNowStackTrace();
         for (int ii = 0; ii < arrStkElem.length; ii ++)
         {
            stackMsg.append ("\n");
            stackMsg.append (arrStkElem[ii]);
         }

         String mess = "FATAL ERROR \"" + message + "\" BY " + cli.clientStr + " AT " + context + "\n";

         // only store it
         logError (null, mess + stackMsg.toString ());

         //errorBox will exit the application !!
         logNativePrinter.errorBox (mess + stackMsg.toString (), "so sorry but the application will be closed!");
         yoMismo = false;
      }

      if (extraInfo != null)
      {
//(o) TODO_elxala_logServer Add extraInfo (records in a table special for a client)
         if (cli.arrExtraFields == null)
         {
            //MSG ERROR extra infos cannot be saved [
            yoMismo = false;
            return;
         }

         // create table log_"client"_extra if needed
         //server::storeExtraInfo (extraInfo);
      }

      yoMismo = false;
   }

   // TIME CONTROL

   private static long initMilis = System.currentTimeMillis ();
   private static long time0milliseconds = initMilis;

   public static long getMillisStartApplication ()
   {
      return time0milliseconds;
   }

   public static long elapsedMillis ()
   {
      long incTime = System.currentTimeMillis () - initMilis;
      if (incTime > 100000000L) // almost 28 hours
      {
         initMilis = System.currentTimeMillis ();
         incTime = 0;
      }

      return incTime;
   }
}
