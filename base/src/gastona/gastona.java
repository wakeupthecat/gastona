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

package gastona;

/*
   //(o) WelcomeGastona_source_javaj_variables Fusion

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    gastona_variables
   <name>       fusion
   <groupInfo>  fusion
   <javaClass>  de.elxala.langutil.graph.sysLookAndFeel
   <importance> 4
   <desc>       //Variables of unit #gastona#

   <help>
      //
      //  The most important units in a Gastona application are #javaj# and #listix# together with
      //  #data#, since all the functionality is performed by these two components. The job of Gastona
      //  is starting Javaj and Listix with their configurations and data. There are few things to
      //  configure in #gastona# unit
      //
      //  Variable <PAINT LAYOUT>
      //  -----------------------
      //
      //    Only the presence of this variable make the components of all visible frames to paint its
      //    limits using a red line. This can be useful during the layout design. In some samples this
      //    variable is commented out (<!PAINT LAYOUT>), it is enough to remove the "!" symbol to
      //    activate this feature.
      //
      //  Variable <fusion>
      //  -----------------------
      //
      //    It is possible to have a Gastona script divided in several files, using this varible
      //    Gastona load these files and merge the units together to form the final Gastona script.
      //
      //       Syntax:
      //
      //          <fusion>
      //             fileToLoad, (mergeType)
      //             ...
      //
      //    where "fileToLoad" is the path to the file to load given either in an absolute path or a
      //    relative path. Listix variables cannot be used here since at this point Listix is not yet
      //    active. If a relative path is given it will be loaded using the current directory and if
      //    not found there it will be search into the java class path (e.g. in the gastona.jar)
      //
      //    Once the file is found all units (#gastona#, #javaj#, #data# and #listix#) will be merged
      //    if present in the file. Merging a unit means setting all Eva variables found in the unit
      //    of the file into the unit of the final script. If an Eva variable does not exist it will
      //    be created and if it already exists then the rows of the new variable will be added at
      //    the end of the existing variable (mergeType A = Append, which is the default). This behaviour
      //    of merging variables can be changed giving one of the following 'mergeType'
      //
      //       mergeType         Meaning
      //       -------------     -----------------------------
      //       A (default)       Append: Contents of the loaded variable will be appended to the end of the variable
      //       R                 Replace: The entire variable will be replaced with the new one
      //       T                 Table Append: The variable except the first line (column names) will appended
      //
      //    NOTE: This mechanism is not recursive, variables <fusion> found in loaded files will
      //          have no effect!

   <examples>
      gastSample

      loading EmergencyEditor
      loading EmergencyEditor as sencodary frame

   <loading EmergencyEditor>

      //#gastona#
      //
      //   <fusion>
      //      META-GASTONA/utilApp/editor/EmergencyEditor.gasti
      //
      //#listix#
      //
      //   <main0>
      //       SET DATA!, bStdEE.bSave, enabled, 0
      //

   <loading EmergencyEditor as sencodary frame>

      //#gastona#
      //
      //   <fusion>
      //      META-GASTONA/utilApp/editor/EmergencyEditor.gasti
      //
      //#javaj#
      //
      //   <frames>  Fmain, "Goto line"
      //
      //   <layout of Fmain>
      //       PANEL, X
      //
      //       bGoto line, eLine
      //
      //#data#
      //
      //    <eLine> 4
      //
      //#listix#
      //
      //    <main0>
      //       SETVAR, t1, @<:lsx tmp txt>
      //       GEN, @<t1>, sampleText
      //
      //    <-- bGoto line>
      //       LISTIX, run StdEE.edit(file line), @<t1>, @<eLine>
      //
      //    <sampleText>
      //       //This is a sample text
      //       //to test the EmergencyEditor and its call
      //       //"run StdEE.edit(file line)"
      //       //<<< LINE 4
      //       //end of sample text

#**FIN_EVA#

*/

import javaj.widgets.table.util.utilMetadata;

import org.gastona.gastonaCtes;

import java.io.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.mensaka.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;
import de.elxala.langutil.filedir.fileUtil;

import java.util.List;
import java.util.Vector;
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
   private static final String UNIT_LISTIX        = "listix";
   private static final String UNIT_GASTONA       = "gastona";
   private static final String UNIT_JAVAJ         = "javaj";
   private static final String UNIT_DATA          = "data";

   private static String sessionLogDirSlash = null;

   protected logger log = new logger (this, "gastona", null);

   private EvaUnit unitGastona = null;
   private EvaUnit unitListix  = null;
   private EvaUnit unitJavaj   = null;
   private EvaUnit unitData    = null;

   public static PrintStream  originalOutStream = System.out;
   public static PrintStream  originalErrStream = System.err;

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
      detectLogDir ();

      //(o) TODO_gastona_META to be configured in META-GASTONA/gastonaConfig.eva  <tempPolicy>
      //Setting gastona's temporary directory
      {
         String oldTmpDir = fileUtil.getTemporalDirBase ();
         // NOTE that it wouldn't be enough to make dirs now to ensure
         // the existence of the temporary directory. For example:
         // A gastona application start - without creating any temporary file -
         // another gastona application, both create the temp dir but then
         // the first application is closed and REMOVES the temp dir, after
         // that the second app tries to use it BUT THE TEMP DIR DOES NOT EXIST ANYMORE!!

         // current tmp should be valid and exist! do not check it, it is a system issue!
         fileUtil.mkdirs (oldTmpDir, "gastonaTMP", true, false);
         String newTempDir = fileUtil.concatPaths(oldTmpDir, "gastonaTMP");
         System.setProperty ("java.io.tmpdir", newTempDir);
      }

      try
      {
         new gastona(par);
      }
      catch (Exception e)
      {
         //e.printStackTrace (originalErrStream);
         ensurePrintStackTraceException (e);

         logger mylog = new logger (null, "gastonaError", null);
         mylog.fatal ("main",
                      "a not handled fatal error has ocurred initializing gastona, or in listix main or main0!\n" + e,
                      e.getStackTrace());
         System.exit (1);
      }
   }


   private boolean loadUnits (String fileName)
   {
      log.dbg (2, "loadUnits", "loading units");

      unitGastona = EvaFile.loadEvaUnit (fileName, UNIT_GASTONA);

      // given <sessionLog> ?
      if (unitGastona != null && unitGastona.getEva ("sessionLog") != null)
      {
         String dir = unitGastona.getEva ("sessionLog").getValue ();
         log.dbg (2, "loadUnits", "sessionLogDir given [" + dir + "] changing log directory to it.");
         setSessionLogDir (dir);
      }

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

      Eva atontao = unitGastona.getEva ("AUTODETECT_NUMERIC");
      if (atontao != null)
      {
         swingTableModelAdapter.INTENTA_COLUMNAS_NUMERICAS_ATONT = true;
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

   private static void detectLogDir ()
   {
      //(o) gastona_traces enable or not the several traces
      //
      String strSessDir = System.getProperty (gastonaCtes.PROP_SESSION_LOG_DIR, "");
      if (strSessDir == null || strSessDir.length () == 0)
      {
         // detection of directory "gastonaLog" or "sessionLog"
         //
         strSessDir = "gastonaLog";
         File checkFile = new File (strSessDir);
         if (!checkFile.exists () || !checkFile.isDirectory())
         {
            strSessDir = "sessionLog";
            checkFile = new File (strSessDir);
            if (!checkFile.exists () || !checkFile.isDirectory())
            {
               // no log at all !
               System.setProperty (gastonaCtes.PROP_SESSION_LOG_DIR, "");
               return;
            }
         }
      }

      setSessionLogDir (strSessDir);
   }

   protected static void setSessionLogDir (String dirName)
   {
      File sessDir = new File (dirName);
      if (!sessDir.exists ()) sessDir.mkdirs ();
      if (!sessDir.exists ())
      {
         System.err.println ("Gastona: fatal error trying to create session log directory [" + dirName + "]!");
         return;
      }

      try
      {
         dirName = sessDir.getCanonicalPath();
         sessionLogDirSlash = dirName + "" + File.separatorChar;
         System.setProperty (gastonaCtes.PROP_SESSION_LOG_DIR, dirName);
      }
      catch (Exception e)
      {
         System.err.println ("Gastona: fatal error while accesing session log directory!");
         e.printStackTrace ();
      }

      logServer.configure (sessionLogDirSlash, "gastona", gastonaVersion.getVersion () + " built on " + gastonaVersion.getBuildDate ());
   }

   private static int nGastonaInstances = 0;

   public gastona (String [] aa)
   {
      nGastonaInstances  ++;
      log.dbg (2, "version", gastonaVersion.getVersion () + " built on " + gastonaVersion.getBuildDate ());

      if (nGastonaInstances > 1)
      {
         log.warn ("init", "Gastona instance #" + nGastonaInstances + ". Note that multiple instances of Gastona is not supported at the moment (just for experimental pruposes).");
         // allow it anyway
      }

      //(o) gast_INIT_FindMainGast
      // search .gast file
      //
      String fileName = searchGastonaApplication (aa);
      if (fileName == null)
      {
         log.err ("init", "not found gast file at all!");
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
         File fi = new File (dirOfGastFile);
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
         EvaFile.saveEvaUnit (log.getLogDirectory () + gastonaCtes.NAME_LINKED_GAST, unitListix);
         EvaFile.saveEvaUnit (log.getLogDirectory () + gastonaCtes.NAME_LINKED_GAST, unitData);
         EvaFile.saveEvaUnit (log.getLogDirectory () + gastonaCtes.NAME_LINKED_GAST, unitJavaj);
         log.dbg (2, "init", log.getLogDirectory () + gastonaCtes.NAME_LINKED_GAST + " saved");

//         //try to clean sqlite log file
//         File fi = new File (log.getLogDirectory () + de.elxala.db.sqlite.clientCaller.SQLITE_LOG_FILE_NAME);
//         if (fi.exists ())
//            fi.delete ();

         //try to copy log helper (viewer)
         microToolInstaller.copyFileFromJar (null, "META-GASTONA/utilApp/logAnalysis/logViewer.gast", sessionLogDirSlash + "logViewer.gast");
      }

      loadMetadata ();

      // ensure the service lsx2Html
      log.dbg (3, "init", "loading agent for messages listix to html");
      new servMsgLsx2Html ();

      log.dbg (3, "init", "loading agent mensaka for listix");

      // reduce parameters (arguments) for listix
      String [] bb = new String [(aa.length > 0) ? aa.length - 1: 0];
      for (int ii = 1; ii < aa.length; ii ++)
      {
         //System.out.println ("GASTONA COPIO " + (ii-1) + " [" + aa[ii] + "]");
         bb[ii-1] = aa[ii];
      }
      //System.out.println ("PARAMETERS TO LISTIX = " + bb.length);

      new mensaka4listix (unitListix, unitJavaj, unitData, bb);
   }

   /**
      search of gastona application policy:

         1) the first parameter is taken as application.gast file
         2) the autoStart.gast file is found in the current directory
         3) the autoStart.gast file is found in jar file (in the root directory)
         4) start "META-GASTONA/WelcomeGastona/WelcomeGastona.gast"
         5) show copyright & version pop-up
   */
   private String searchGastonaApplication (String [] aa)
   {
      if (aa.length > 0)
      {
         log.dbg (2, "searchingApp", "given in parameter [" + aa[0] + "]");

         //(o) gastona_init collect command line parameters
         //
         System.setProperty("gastona.argumentCount", "" + (aa.length-1));
         for (int ii = 1; ii < aa.length; ii ++)
            System.setProperty("gastona.argument" + ii, "" + aa[ii]);

         return aa[0];
      }
      System.setProperty("gastona.argumentCount", "0");

      // first look for "autoStart.gast" in current directory
      // Note :
      //       This kind of start permit a easy way to start a gastona application
      //       without having to associate the extension .gast to "java -jar gastona.jar"
      //       or in linux start the script with #!java -jar gastona.har
      //
      File normalFile = new File ("autoStart.gast");
      if (normalFile.exists ())
      {
         log.dbg (2, "searchingApp", "found autoStart.gast in current directory");
         return "autoStart.gast";
      }

      TextFile appfile = new TextFile ();

      // then look for "autoStart.gast" in current directory or in classpath
      // Note :
      //       This kind of start permit a easy way to customize a gastona.jar
      //       by simply insert in the root path of gastona.jar a gast script (autoStart.gast)
      //       and all classes needed
      //
      if (appfile.fopen ("autoStart.gast", "r"))
      {
         appfile.fclose ();
         log.dbg (2, "searchingApp", "found somewhere autoStart.gast");
         return "autoStart.gast";
      }

      String welcomeFile = "META-GASTONA/WelcomeGastona/WelcomeGastona.gast";
      // then look for "META-GASTONA/WelcomeGastona/WelcomeGastona.gast"
      // Note :
      //       This is the welcome gastona script
      //
      if (appfile.fopen (welcomeFile, "r"))
      {
         appfile.fclose ();
         log.dbg (2, "searchingApp", "found " + welcomeFile);
         return welcomeFile;
      }

      log.dbg (2, "searchingApp", "application not found, show copyright");
      showAboutGastona (null);

      return null;
   }

   public static void showAboutGastona (String [] aa)
   {
      javax.swing.JOptionPane.showMessageDialog (
            null,
            "Gastona v" + gastonaVersion.getVersion () + "\nBuilt on " + gastonaVersion.getBuildDate () + "\nCopyright (c) 2007,2008,2009,2010,2011\nAlejandro Xalabarder Aulet\nwww.gastona.org",
            "About",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
