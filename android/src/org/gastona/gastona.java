/*
library de.elxala
Copyright (C) 2005 to 2012 Alejandro Xalabarder Aulet

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

package org.gastona;


import javaj.widgets.table.util.utilMetadata;

import java.io.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.mensaka.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;
import de.elxala.db.*;

import javaj.*;
import javaj.widgets.basics.*;
import javaj.widgets.table.util.*;
import listix.table.*;
import listix.cmds.*;
import listix.*;

import java.io.File;

/**
*/
public class gastona
{
   public static final String NAME_LINKED_GAST = "generated_linked.gast";
   public static final String MAINGAST_MEMORYFILE = ":mem _mainGast_";

   public static gastona lastGastona = null;

   private static final String UNIT_LISTIX        = "listix";
   private static final String UNIT_GASTONA       = "gastona";
   private static final String UNIT_JAVAJ         = "javaj";
   private static final String UNIT_DATA          = "data";

   public EvaUnit unitGastona = null;
   public EvaUnit unitListix  = null;
   public EvaUnit unitJavaj   = null;
   public EvaUnit unitData    = null;

   public static PrintStream  originalOutStream = System.out;
   public static PrintStream  originalErrStream = System.err;

   public mensaka4listix myMensaka4listix = null;

   protected logger log = org.gastona.commonGastona.log;

   public static void ensurePrintStackTraceException (Exception e)
   {
      //(o) gastona_errors ensuring the printout of stack trace of an exception in spite of zConsoles

      // if during initilization javaj instanciate a concole
      // this traps the strderr (System.err), if this console does not
      // reach the visibility and some fatal error occurs, the stack trace of the exception
      // will not be printed out ...
      // so with this method we ensure that the stack trace of the exception will
      // be printed out in the error stream at launching gastona
      e.printStackTrace ();
      if (System.err != originalErrStream)
         e.printStackTrace (originalErrStream);

      // does not work! due to "java.lang.Error: getenv no longer supported, use properties and -D instead: GASTONA_FATAL_ERROR_FILE
      //      String sOnFile = System.getenv("GASTONA_FATAL_ERROR_FILE");

   //(o) TOSEE extra logging exceptions initializing gastona (due to esporadic nature some times)

////      if (log.getLogDirectory () == null) return null;
////      String sOnFile = log.getLogDirectory () + "FATAL_ERROR_LOGS.txt";
////      if (sOnFile != null && sOnFile.length () > 0)
////      {
////         TextFile fi = new TextFile();
////         if (fi.fopen (sOnFile, "a"))
////         {
////            String time_stamp = (new DateFormat ("yyyy.MM.dd HH:mm:ss.S", new java.util.Date())).get ();
////            StackTraceElement[] stkEle = e.getStackTrace();
////
////            //e.printStackTrace (fi.getFileWriter ());
////            for (int ii = 0; ii < stkEle.length; ii ++)
////               fi.writeLine (stkEle[ii].toString());
////            fi.fclose ();
////         }
////      }
   }

   public static void main (String [] par)
   {
      try
      {
         lastGastona = new gastona(par);
      }
      catch (Exception e)
      {
         //e.printStackTrace (originalErrStream);
         ensurePrintStackTraceException (e);

         logger mylog = new logger (null, "gastonaError", null);
         mylog.fatal ("main",
                      "a not handled fatal error has ocurred initializing gastona, or in listix main or main0!\n" + e,
                      e.getStackTrace());
         if (! globalJavaj.logCat.isShowing ())
            System.exit (1);
      }
   }


   private boolean loadUnits (String fileName)
   {
      log.dbg (2, "loadUnits", "loading units");
      //System.out.println ("loadUnits " + fileName + " gastonap " + unitGastona);

      unitGastona = EvaFile.loadEvaUnit (fileName, UNIT_GASTONA);

      // given <sessionLog> ?
      if (unitGastona != null)
      {
         if (unitGastona.getEva ("UDP_DEBUG_PORT") != null)
         {
            int port = stdlib.atoi (unitGastona.getEva ("UDP_DEBUG_PORT").getValue ());
            logServer.setUDPDebugPort (port);
         }

         if (unitGastona.getEva ("sessionLog") != null)
         {
            String dir = unitGastona.getEva ("sessionLog").getValue ();
            log.dbg (2, "loadUnits", "sessionLogDir given [" + dir + "] changing log directory to it.");

            logDirDetectionAndTemp.setSessionLogDir (dir);
         }

         // specified debug levels in #gastona# ?
         //
         Eva evaDbgLevel = unitGastona.getEva ("logLevelGlobal");
         int dbgLevel = (evaDbgLevel == null) ? -1: stdlib.atoi (evaDbgLevel.getValue (0));
         android.util.Log.d ("gastona", " max debug level " + dbgLevel);
         //System.out.println ("OUT:gastona_max_debug_level = " + dbgLevel);

         Eva evaDbgClients = unitGastona.getEva ("logLevels");
         if (evaDbgClients != null)
         {
            android.util.Log.d ("gastona", " log clients : " + evaDbgClients);
            //System.out.println ("OUT:gastona_log_clients : " + evaDbgClients);
         }
         //else System.out.println ("OUT:gastona_log_clients : not given!");

         logServer.configure (dbgLevel, evaDbgClients);
      }
      // else System.out.println ("OUT:gastona_max_debug_level not clear...");

      unitListix  = EvaFile.loadEvaUnit (fileName, UNIT_LISTIX);
      unitJavaj   = EvaFile.loadEvaUnit (fileName, UNIT_JAVAJ);
      unitData    = EvaFile.loadEvaUnit (fileName, UNIT_DATA);

      if (unitGastona == null) unitGastona = new EvaUnit ("gastona");
      if (unitListix == null)  unitListix = new EvaUnit ("listix");
      if (unitJavaj == null)   unitJavaj = new EvaUnit ("javaj");
      if (unitData == null)    unitData = new EvaUnit ("data");

      log.dbg (2, "loadUnits", "loaded unit #gastona# with " + unitGastona.size () + " evas");
      log.dbg (2, "loadUnits", "loaded unit #listix# with " + unitListix.size () + " evas");
      log.dbg (2, "loadUnits", "loaded unit #javaj# with " + unitJavaj.size () + " evas");
      log.dbg (2, "loadUnits", "loaded unit #data# with " + unitData.size () + " evas");

      //(o) gastona_fusion Here takes place the fusion
      //
      Eva fusion = unitGastona.getEva ("fusion");
      if (fusion != null)
      {
         log.dbg (2, "loadUnits", "fusion found containing " + fusion.rows () + " rows");
         for (int ff = 0; ff < fusion.rows (); ff ++)
         {
            String fileFus = fusion.getValue (ff, 0);
            String kindFus = (fusion.getValue (ff, 1)).toUpperCase ();
            if (kindFus.equals ("")) kindFus = "A";

            char policy = kindFus.charAt (0);
            switch (policy)
            {
               case 'A':
               case 'R':
               case 'T':
                  break;
               default:
                  log.err ("loadUnits", "unknown merge type [" + kindFus + "], possible values are 'A'(append), 'R'(replace), 'T'(append table)");
                  policy = Eva.MERGE_ADD;
                  break;
            }

            if (log.isDebugging (3))
               log.dbg (3, "loadUnits", "fusion file [" + fileFus + "] type " + policy);

            unitGastona.merge (EvaFile.loadEvaUnit (fileFus, UNIT_GASTONA) , policy);
            unitJavaj.merge   (EvaFile.loadEvaUnit (fileFus, UNIT_JAVAJ)   , policy);
            unitListix.merge  (EvaFile.loadEvaUnit (fileFus, UNIT_LISTIX)  , policy);
            unitData.merge    (EvaFile.loadEvaUnit (fileFus, UNIT_DATA)    , policy);
         }
         log.dbg (2, "loadUnits", "fusion done");
      }

      if (unitGastona.getEva ("PAINT LAYOUT") != null)
      {
         globalJavaj.setPaintingLayout (true);
      }

      log.dbg (2, "loadUnits", "units loaded");

      if (unitJavaj.size () == 0 && unitListix.size () == 0)
      {
         log.fatal ("loadUnits", "no javaj or listix units found in [" + fileName + "], Nothing to do!");
         return false;
      }
      return true;
   }

   private static int nGastonaInstances = 0;

   public gastona (String [] aa)
   {
      nGastonaInstances  ++;
      log.dbg (2, "version", gastonaVersion.getVersion () + " built on " + gastonaVersion.getBuildDate ());

      //if (nGastonaInstances > 1)
      //{
      //   log.warn ("init", "Gastona instance #" + nGastonaInstances + ". Note that multiple instances of Gastona is not supported at the moment (just for experimental pruposes).");
      //   // allow it anyway
      //}
      String fileName = commonGastona.getGastFileNameAndProcessArgs (aa);
      if (fileName == null)
      {
         log.dbg (2, "searchingApp", "application not found, show copyright");
         showAboutGastona (null);
         log.err ("init", "not found gast file at all!");
         if (! globalJavaj.logCat.isShowing ())
            System.exit (0);
      }

      System.setProperty(gastonaCtes.PROP_GASTONA_FILEPATH, fileName);
      log.dbg (2, "init", "setting property "+ gastonaCtes.PROP_GASTONA_FILEPATH + " to [" + fileName + "]");

      //try to set the current dir to the one of the gast file
      {
         String dirOfGastFile = fileUtil.getParent (fileName);
         System.setProperty(gastonaCtes.PROP_GASTONA_FILEDIR, dirOfGastFile);
         log.dbg (2, "init", "setting property " + gastonaCtes.PROP_GASTONA_FILEDIR + " to [" + dirOfGastFile + "]");

         String oldCurrDir = System.getProperty ("user.dir");
         System.setProperty("gastona.oldCurrentDir", oldCurrDir);
         System.setProperty("gastona.currentDir", oldCurrDir);
         System.setProperty("gastona.gastFile.fromResource", "0");

         //Note that this will not work for gast's as resources (e.g. from jar file)
         //but such a gast should not refer to relative paths to its location
         File fi = fileUtil.getNewFile (dirOfGastFile);
         if (!fi.exists ())
         {
            log.dbg (2, "init", "gastona file is a resource");
            System.setProperty("gastona.gastFile.fromResource", "1");
         }
         else
         {
            if (!utilSys.isSysUnix)
               log.dbg (2, "init", "keep current dir (OS not Windows)");
            else if (!fi.canWrite ())
               log.dbg (2, "init", "keep current dir (gastona script in read only dir)");
            else
            {
               //check if tmp dir
               File tmpDir = new File (System.getProperty ("java.io.tmpdir", ""));
               File gasDir = new File (dirOfGastFile);

               String sTmpDir = tmpDir.getPath ();
               String sGasDir = gasDir.getPath ();

               try
               {
                  sTmpDir = tmpDir.getCanonicalPath ();
                  sGasDir = gasDir.getCanonicalPath ();
               }
               catch (Exception e) {}

               if (sTmpDir.equals (sGasDir))
               {
                  log.dbg (2, "init", "keep current dir (gastona script in temporary dir)");
               }
               else
               {
                  log.dbg (2, "init", "change current directory from [" + oldCurrDir + "] to [" + dirOfGastFile + "]");

                  System.setProperty("gastona.currentDir", dirOfGastFile);
                  System.setProperty("user.dir", dirOfGastFile);
               }
            }
         }
      }

      //(o) gast_INIT_Fusion
      // load and merge units
      //
      if (! loadUnits (fileName))
      {
         log.dbg (2, "init", "exit gastona");
         return;
      }

      //(o) gastona_traces save generated_linked.gast
      //
      if (log.getLogDirectory () != null)
      {
         EvaFile.saveEvaUnit (log.getLogDirectory () + NAME_LINKED_GAST, unitListix);
         EvaFile.saveEvaUnit (log.getLogDirectory () + NAME_LINKED_GAST, unitData);
         EvaFile.saveEvaUnit (log.getLogDirectory () + NAME_LINKED_GAST, unitJavaj);
         log.dbg (2, "init", log.getLogDirectory () + NAME_LINKED_GAST + " saved");
      }

      loadMetadata ();

      // ensure the service lsx2Html
      log.dbg (3, "init", "loading agent for messages listix to html");
      new servMsgLsx2Html ();

      log.dbg (3, "init", "loading agent mensaka for listix");

      String [] bb = org.gastona.commonGastona.getReducedArguments (aa);
      myMensaka4listix = new mensaka4listix (unitListix, unitJavaj, unitData, bb);
   }

   public static void showAboutGastona (String [] aa)
   {
//(o) Android_TODO message box
////      javax.swing.JOptionPane.showMessageDialog (
////            null,
////            "Gastona v" + gastonaVersion.getVersion () + "\nBuilt on " + gastonaVersion.getBuildDate () + "\nCopyright (c) 2007,2008,2009,2010\nAlejandro Xalabarder Aulet\nwww.gastona.org",
////            "About",
////            javax.swing.JOptionPane.INFORMATION_MESSAGE);
   }

   private void loadMetadata ()
   {
      // try to load #data# <TableMetadataDicc> and copy its contents to singleMetaDataServer
      //
      if (unitData != null)
      {
         Eva meta = unitData.getEva ("TableMetadataDicc");
         if (log.isDebugging(3))
         {
            if (meta == null)
            {
               log.dbg (3, "init", "there is no metadata dicctionary for tables");
            }
            else
            {
               log.dbg (3, "init", "metadata dicctionary for tables " + meta.rows () + " field(s) loaded");
               for (int ii = 0; ii < meta.rows (); ii ++)
                  log.dbg (3, "init", "[" + ii + "] " + meta.get(ii).toString ());
            }
         }
         utilMetadata.addMetaData (unitData.getEva ("TableMetadataDicc"));
      }
   }
}
