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

package de.elxala.langutil;

import java.io.File;

import java.util.Vector;
import java.util.List;
import java.util.regex.*;  // Pattern Matcher etc for sqlite error detection


import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.streams.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.*;

import de.elxala.db.*;

/*
   12.05.2012 23:40

   it is a variant of Runtime.getRuntime().exec
   with handle of input / output and error streams

   based on the method sqlCall of de/elxala/db/sqlite/sqlSolverBatch.java

*/
public class processExecutor
{
   private static logger log = new logger (null, "de.elxala.langutil.processExecutor", null);

//   public boolean tracingOn ()
//   {
//      return log.getLogDirectory () != null;
//   }

////   public String traceFileName ()
////   {
////      if (log.getLogDirectory () == null) return null;
////
////      return log.getLogDirectory () + SQLITE_LOG_FILE_NAME;
////   }

   private static String prepareOutputFile (String outfile)
   {
      if (outfile == null || outfile.length() == 0) return null;
      
      if (outfile.charAt(0) == '+')
      {
         return outfile.substring (1);
      }

      // Since oputput files are sent to streamReader2TextFile 
      // and this function only open the files for append, it is necessary that
      // we explicity reset the file here.
      //
      TextFile fix = new TextFile ();
      fix.fopen (outfile, "w");
      fix.fclose ();
      return outfile;
   }


   public static boolean executeProcess (String [] processAndArgs, String inputFile, String outputFile, String errorFile, serialTextBuffer theInputScript, abstractStreamTextReader [] stdOutStdErr)
   {
      if (processAndArgs.length < 1)
      {
         log.severe ("executeProcess", "no process to execute passed!");
         return false;
      }
      String processName = processAndArgs[0];

      String theInputTextFile =  (inputFile != null && inputFile.length () > 0) ? inputFile: null;
      String theOutputTextFile = prepareOutputFile (outputFile);
      String theErrorTextFile = prepareOutputFile (errorFile);

      abstractStreamTextReader theOutput = null;
      abstractStreamTextReader theError = null;

      // ... )))
      processSignaler.signalStart ();


      log.dbg (2, "executeProcess", "start process \"" + processName );

      long startingTime = System.currentTimeMillis ();
////LOGING
////
////         theInputScript.rewind ();
////         while (theInputScript.getNextLine ())
////            traceFile.writeLine (theInputScript.getLastReadLine());

      Process proc = null;
      try
      {
         // create the process
         proc = (processAndArgs.length == 1) ?
                  Runtime.getRuntime().exec (processAndArgs[0]):
                  Runtime.getRuntime().exec (processAndArgs);
      }
      catch (Exception e)
      {
         log.err ("executeProcess", "exception creating process [" + processName + "]!" + e);
         return false;
      }

      // create the streams
      //

      // create injector stream for the input to the process
      //
      Thread introd = (theInputTextFile != null) ?
                        (Thread) (new streamFileInjector (proc.getOutputStream (), theInputTextFile)):
                        (Thread) (new streamTextBufferInjector (proc.getOutputStream (), theInputScript, null));

      // create stdout stream
      //
      if (theOutputTextFile != null)
         theOutput = new streamReader2TextFile (proc.getInputStream (), theOutputTextFile);
      else
         theOutput = new streamReader2TextList (proc.getInputStream ());

      // create stderr stream
      //
      if (theErrorTextFile != null)
         theError  = new streamReader2TextFile (proc.getErrorStream (), theErrorTextFile);
      else
         theError  = new streamReader2TextList (proc.getErrorStream ());

      // star the streams
      //
      theError.start ();
      theOutput.start ();
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
         while (!theOutput.hasFinished () || !theError.hasFinished ())
            Thread.sleep (50);
      }
      catch (Exception e)
      {
         log.err ("executeProcess", "exception during process execution [" + processName + "]!" + e);
         //if (traceFile != null)
         //   traceFile.fclose ();
         return false;
      }

      //---- ALL HAS FINISHED
      long endingTime = System.currentTimeMillis ();

      log.dbg (2, "executeProcess", "end executing it took " + (endingTime-startingTime) /1000.);
////LOGING
//////      if (traceFile != null)
//////      {
//////         String time_stamp = (new DateFormat ("yyyy.MM.dd HH:mm:ss.S", new java.util.Date())).get ();
//////         traceFile.writeLine ("*** STD-OUTPUT (" + (endingTime-startingTime) /1000. + " s) on " + time_stamp);
//////
//////         if (theOutputTextFile != null) traceFile.writeFileContents (theOutputTextFile);
//////         else
//////         {
//////            for (int ii = 0; ii < theOutput.countLines (); ii ++)
//////               traceFile.writeLine (theOutput.getLine (ii));
//////         }
//////
//////         if (theError.countLines () > 0)
//////         {
//////            traceFile.writeLine ("!!! ERROR-OUTPUT");
//////            if (theErrorTextFile != null) traceFile.writeFileContents (theErrorTextFile);
//////            else
//////            {
//////               for (int ii = 0; ii < theError.countLines (); ii ++)
//////                  traceFile.writeLine (theError.getLine (ii));
//////            }
//////         }
//////         traceFile.writeLine ("*** END-OUTPUT");
//////         traceFile.fclose ();
//////         traceFile = null;
//////      }


      // ... )))
      if (exitVal == -1)
           processSignaler.signalError ();
      else processSignaler.signalEnd ();

      // read output file
      //
      if (theError.countLines () > 0)
      {
         if (theErrorTextFile != null)
              log.err ("executeProcess", "process call with ouput errors (in file " + theErrorTextFile + ")");
         else 
         {
            StringBuffer strErr = new StringBuffer ();
            for (int ii = 0; ii < theError.countLines (); ii ++)
            {
               strErr.append (theError.getLine (ii) + "\n");
            }
            log.err ("executeProcess", "process call with ouput errors : [" + strErr.toString () +"]");
         }
      }

      if (theError.countLines () > 0 && theInputTextFile == null)
      {
         int MAX = 5000;
         String ss = theInputScript.toTruncatedString(MAX);
         log.err ("executeProcess", "Input that produced error(s) [" + ss + "]" + (ss.length () >= MAX ? " input truncated!": ""));
      }

      if (stdOutStdErr != null && stdOutStdErr.length > 0)
         stdOutStdErr[0] = theOutput;
      if (stdOutStdErr != null && stdOutStdErr.length > 1)
         stdOutStdErr[1] = theError;

      return (exitVal != -1);
   }

//   public static String getClientExePath ()
//   {
//      SQLITE_CLIENT_EXE = microToolInstaller.getExeToolPath ("sqlite");
//   }


//   public static void main (String [] aa)
//   {
//   }
}
