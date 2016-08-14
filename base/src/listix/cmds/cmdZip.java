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
   //(o) WelcomeGastona_source_listix_command ZIP

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       ZIP
   <groupInfo>  system_files
   <javaClass>  listix.cmds.cmdZip
   <importance> 5
   <desc>       //Zip utilities for compressing and extracting files in zip, jar or gzip formats

   <help>
      //
      // Utility commands to compress or extract files in formats zip, gzip or jar.
      //


   <aliases>
      alias
      SIP


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //make a zip (jar) file of an entire directory
         2   ,    2      , //unzips a file (zip or jar)
         3   ,    2      , //retrives the entries contents in a zip (jar) file
         4   ,    2      , //make a gzip file
         5   ,    2      , //un-gzips a file

<! NOT IMPLEMENTED!!!!
<!         6   ,    2      , //make a zip (jar) file from a list of files
<!         7   ,    2      , //make a jar file with manifest from an entire directory


   <syntaxParams>
      synIndx, name         , defVal , desc
         1   , ZIP DIR      ,        , //
         1   , zipFileName  ,        , //Filename for the desired zip or jar file
         1   , dirPath      ,        , //Directory to be zipped

         2   , UNZIP        ,        , //
         2   , zipFileName  ,        , //Filename of the zip file to be extracted
         2   , fileToExtract,        , //File to extract, if not given then extract all
         2   , ...          ,        , //

         3   , GET ENTRIES  ,        , //
         3   , zipFileName  ,        , //Filename of the zip or jar file
         3   , contentVariable,      , //Name of the Eva table that will be generated with the list of all files in the zip file using the column 'fileName'

         4   , GZIP         ,        , //
         4   , fileToGZip   ,        , //Filename of the file to be g-zipped
         4   , gzipFileName ,  ""    , //If given, filename for the desired gzip file, if not given the name will be fileToGZip ended with ".gz"

         5   , UNGZIP        ,        , //
         5   , gzipFileName  ,        , //Filename of the gzip file to be extracted
         5   , targetFileName, ""    ,  //If given, filename for the desired extracted file, if not given and gzipFileName ends with ".gz" the extracted filename will be gzipFileName minus ".gz"

         6   , ZIP FILES    ,        , //
         6   , zipFileName  ,        , //Filename for the desired zip or jar file
         6   , listFiles    ,        , //Name of the Eva table containing a list of all files to be zipped, this are to be found in the column named 'fileName'

<! NOT IMPLEMENTED!!!!
<!         7   , JAR DIR      ,        , //
<!         7   , jarFileName  ,        , //Filename for the desired zip or jar file
<!         7   , dirPath      ,        , //Directory to be zipped
<!         7   , manifestClass,        , //Class name for the jar manifest (e.g. gastona.gastona)

<! NOT IMPLEMENTED!!!!
<!         8   , JAR FILES    ,        , //
<!         8   , jarFileName  ,        , //Jar file name to be generated
<!         8   , listFiles    ,        , //Name of the Eva table containing a list of all files to be zipped, this are to be found in the column named 'fileName'
<!         8   , manifestClass,        , //Class name for the jar manifest (e.g. gastona.gastona)

   <options>
      synIndx, optionName, parameters, defVal, desc

      1      , STORE     ,  "1/0"     , "0", //Specify if the entries should be just stored with no compression
      6      , STORE     ,  "1/0"     , "0", //Specify if the entries should be just stored with no compression
      2      , BASE PATH ,  "filePath", "", //Specifies the base path where the files will be extracted
      5      , BASE PATH ,  "filePath", "", //Specifies the base path where the file will be extracted
      6      , BASE PATH ,  "filepath", "", //Specifies a base path for the source files specified in the parameter 'listFiles'. Note that this basepath will not appear in the generated zip file

      4      , SET FILE DATE, "1/0"     , "1", //Set the file date to the gz file, thus it can be recovered when ungzipping it
      5      , SET DATE     , "gz, now or yyyy-MM-dd HH:mm" , "gz", //To set the date time to the decompressed file. Default 'gz': same date-time as the gz file, 'now': current date-time or specify date-time


   <examples>
      gastSample
      zip viewer demo

   <zip viewer demo>
      //#javaj#
      //
      //   <frames> F, Zip viewer demo, 400
      //
      //   <layout of F>
      //      EVA, 10, 10, 7, 7
      //
      //         ,          , X       ,
      //         , lZipFile , eZipFile, bPickFile
      //       X , vistas   , -       , -
      //
      //   <layout of vistas>
      //      TABBED,
      //
      //      Asiste table , sAsiste
      //      Tree         , aTree
      //
      //#data#
      //
      //   <lZipFile> Zip or jar file
      //
      //   <bPickFile DIALOG> FILE
      //
      //   <eZipFile> gastona.jar
      //
      //#listix#
      //
      //   <-- bPickFile>
      //      -->, eZipFile data!,, @<bPickFile chosen>
      //      @<get Zip Entries>
      //
      //   <-- eZipFile>
      //      @<get Zip Entries>
      //
      //   <get Zip Entries>
      //      ZIP, GET ENTRIES, @<eZipFile>, tabEntries
      //      DATABASE,, CREATE TABLE, tabEntries
      //      -->, aTree data!, sqlSelect, //SELECT * FROM tabEntries ORDER BY fileName;
      //      -->, sAsiste data!, sqlSelect, //SELECT * FROM tabEntries ORDER BY fileName;
      //

#**FIN_EVA#
*/

package listix.cmds;

import java.util.List;
import java.util.Vector;
import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.stdlib;
import de.elxala.zServices.logger;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.DateFormat;
import java.util.zip.*;
import java.util.jar.*;
import java.io.*;
import java.net.URL;

public class cmdZip implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "ZIP",
            "SIP",
         };
   }

   // to keep the current logger
   private logger theLog = new logger (null, "dummylogger", null);

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      theLog = that.log();

      String oper        = cmd.getArg(0);
      String zipFileName = cmd.getArg(1);
      String secondParam = cmd.getArg(2);

      // get options
      String basePath  = cmd.takeOptionString ("BASEPATH");
      boolean onlyStore = "1".equals (cmd.takeOptionString ("STORE"));

      if (cmd.meantConstantString (oper, new String [] { "ZIP", "ZIPDIR" } ))
      {
         zipDir (zipFileName, onlyStore, secondParam, basePath);
      }
      else if (cmd.meantConstantString (oper, new String [] { "ZIPFILES" } ))
      {
         Eva listOfFiles = that.getVarEva (secondParam);
         if (listOfFiles == null)
         {
            theLog.err ("ZIP", "ZIP Eva table (listOfFiles) <" + secondParam + "> not found!");
            return 1;
         }
         zip (zipFileName, onlyStore, listOfFiles, basePath);
      }
      else if (cmd.meantConstantString (oper, new String [] { "UNZIP" } ))
      {
         // check if extrac all or just a part of them
         List onlyThese = new Vector ();
         for (int ii = 2; ii < cmd.getArgSize (); ii ++)
            onlyThese.add (cmd.getArg(ii));

         unzip (zipFileName, onlyThese.size () == 0 ? null: onlyThese, basePath);
      }
      else if (cmd.meantConstantString (oper, new String [] { "GETENTRIES","GET", "ENTRIES" } ))
      {
         if (secondParam.length () == 0)
         {
            theLog.err ("ZIP", "ENTRIES requires the parameter contentFiles (an Eva table)!");
            return 1;
         }

         // get or create the eva target for the list of entries
         Eva target = that.getSomeHowVarEva (secondParam);

         zipEntries (zipFileName, target);
      }
      else if (cmd.meantConstantString (oper, new String [] { "GZIP" } ))
      {
         boolean setDateTimeFromOrigin = "1".equals (cmd.takeOptionString ("SETFILEDATE", "1"));

         // NOTE for GZIP zipFileName is the 1st parameter = file to zip!
         gzip (zipFileName, secondParam, setDateTimeFromOrigin);
      }
      else if (cmd.meantConstantString (oper, new String [] { "UNGZIP" } ))
      {
         String dateTimeOpt = cmd.takeOptionString ("SETDATE", "gz");
         ungzip (zipFileName, secondParam, dateTimeOpt);
      }
      else
      {
         theLog.err ("ZIP", "Unknown subcommand [" + oper  + "]!");
      }

      cmd.checkRemainingOptions ();
      return 1;
   }

   private byte[] theBuffer = null;

   private void resetBuffer ()
   {
      theBuffer = null;
   }

   private byte[] getBuffer ()
   {
      if (theBuffer == null)
         theBuffer = new byte[8192];  // (8Kb)
      return theBuffer;
   }

   private String toPathZip (String path)
   {
      return path.replace ('\\', '/');
   }

   /**
      Zip files method
   */
   private void zipDir (String ziFile, boolean onlyStore, String dirPath, String basePath /* NOT USED .. reserved */)
   {
      // Zip entire directory
      //
      boolean ok = true;

      pathGetFiles moto = new pathGetFiles();
      moto.initScan (dirPath, new String[0] /*extensions to scan*/);

      theLog.dbg (2, "ZIP", "zipDir [" + dirPath + "] on [" + ziFile + "]");

      try
      {
         // open zip stream as deflated archive mode
         ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(ziFile));

         // THIS DOES NOT WORK FOR STOTED METHOD !!
         // AND DEFLATED IS ALREADY DEFAULT
         //zipStream.setMethod(onlyStore ? ZipOutputStream.STORED: ZipOutputStream.DEFLATED);
         if (onlyStore)
            zipStream.setLevel(0);


         // Loop whole directory
         List cosas = null;
         //4SAVE DIR ENTRIES resetLastDirEntry ();
         do
         {
            cosas = moto.scanN (100);
            for (int jj = 0; jj < cosas.size (); jj++)
            {
               String [] record = (String []) cosas.get (jj);

               // form sub path from dirPath + file name only
               String entrySingle = fileUtil.concatPaths (record[0], record[1]);
               String entryFull   = fileUtil.concatPaths (dirPath, entrySingle);

               theLog.dbg (11, "ZIP", "zipDir entrySingle [" + record[0] + "]+[" + record[1] + "]=[" + entrySingle + "] entryFull [" + entryFull + "]");

               //theLog.dbg (2, "ZIP", "zip making entry [" + entrySingle + "]");
               if (! zipOneEntry (zipStream, entrySingle, entryFull, onlyStore))
               {
                  ok = false;
                  theLog.severe ("ZIP", "zip writing on target file [" + ziFile + "]");
               }
            }
         }
         while (cosas.size () > 0);

         zipStream.close();
      }
      catch(IOException e)
      {
         ok = false;
         theLog.severe ("ZIP", "zip on writing on target file [" + ziFile + "]" + e);
      }
      resetBuffer ();

      theLog.dbg (4, "ZIP", "zip " + ((ok) ? "well done.": "ended with errors!"));
   }

   /**
      Zip files method
   */
   private void zip (String ziFile, boolean onlyStore, Eva listOfFiles, String basePath)
   {
      // Zip file
      //
      boolean ok = true;

      int colFileName = listOfFiles.colOf ("fileName");
      if (colFileName == -1)
      {
         theLog.err ("ZIP", "zip [" + ziFile + "] listOfFiles does not contain column \"fileName\", nothing to do here");
         return;
      }

      theLog.dbg (2, "ZIP", "zip " + listOfFiles.rows() + " file(s) from [" + basePath + "]" + " on [" + ziFile + "] only store = " + onlyStore);

      try
      {
         // open zip stream as deflated archive mode
         ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(ziFile));

         // THIS DOES NOT WORK FOR STOTED METHOD !!
         // AND DEFLATED IS ALREADY THE DEFAULT ONE
         //zipStream.setMethod(onlyStore ? ZipOutputStream.STORED: ZipOutputStream.DEFLATED);
         if (onlyStore)
            zipStream.setLevel(0);

         //4SAVE DIR ENTRIES resetLastDirEntry ();
         // Loop all files
         for (int ii = 1; ii < listOfFiles.rows (); ii ++)
         {
            String entrySingle = listOfFiles.getValue (ii, colFileName);
            String entryFull   = fileUtil.concatPaths (basePath, entrySingle);

            theLog.dbg (11, "ZIP", "zipDir entrySingle [" + entrySingle + "] entryFull [" + entryFull + "]");

            //theLog.dbg (2, "ZIP", "zip making entry [" + entrySingle + "]");
            if (! zipOneEntry (zipStream, entrySingle, entryFull, onlyStore))
            {
               ok = false;
               theLog.severe ("ZIP", "zip writing on target file [" + ziFile + "]");
            }
         }
         zipStream.close();
      }
      catch(IOException e)
      {
         ok = false;
         theLog.severe ("ZIP", "zip on writing on target file [" + ziFile + "]" + e);
      }
      resetBuffer ();

      theLog.dbg (4, "ZIP", "zip " + ((ok) ? "well done.": "ended with errors!"));
   }


   //4SAVE DIR ENTRIES private String lastDirEntry = null;
   //4SAVE DIR ENTRIES private void resetLastDirEntry () { lastDirEntry = null; }

   /**
      Better idea to zip directory entries (instead of the one commented with "4SAVE DIR ENTRIES"
      This code ensure that
         - directories are created before its child files (it seems jar make it in this way)
         - possible to give an unsorted list of entries

      ... code
         private List dirEntryAlready = null;
         private List dirEntryChain = null;

         private void resetLastDirEntry(boolean activate)
         {
            dirEntryAlready = activate ? new Vector(): null;
            dirEntryChain = activate ? new Vector(): null;
         }
         ...
         private boolean zipOneEntry (...
         {
            if (dirEntryAlready != null)
            {
               //dir entries are activated
               dirEntryChain = makeEntryChain (entrySingle);
               // this makes from a given path i.e. gastona\javaj\javaj36.class
               // a list with the pending directories to create first and then the file etry
               //        0: gastona
               //        1: gastona/javaj
               //        2: gastona/javaj/javaj36.class
               // Note, for example, that if the directory gastona were already created the
               // list would be formed just by the elements 1 and 2
            }

            do
            {
               String zipEntryName = (dirEntryChain == null) ? entrySingle: (String) dirEntryChain.get(0);

               // same code as now ... using zipEntryName instead of entrySingle
               // NOTE! in order to stamp time in directory entries an extra code is needed! form fullDirPath...
               //

               if (dirEntryChain != null && dirEntryChain.size () > 0)
                  dirEntryChain.erase (0);
            } while (dirEntryChain != null && dirEntryChain.size () > 0)
         }

   */


   private boolean zipOneEntry (ZipOutputStream outStream, String entrySingle, String entryFull, boolean onlyStore)
   {
      try
      {
         //TODO FEEDBACK ---> entrySingle from entryFull

         //NOTE: FOR JAR FILES IT IS MANDATORY USING / INSTEAD OF \ !!! (TO AVOID THE ERROR MESSAGE "Invalid or corrupt jar file!");

         ZipEntry entry = new ZipEntry(entrySingle.replace('\\', '/'));
         File fil = new File (entryFull);
         long filesTime = fil.lastModified ();
         entry.setTime (filesTime);

         //(o) TOSEE_listix_cmds ZIP, onlyStote not used here
         //                         keep the parameter for future use (e.g STORE EXTENSIONS, jpeg, jpg, rar, zip, ... etc)
//         if (onlyStore)
//            entry.setLevel (0);

         FileInputStream in = new FileInputStream(entryFull);

         //4SAVE DIR ENTRIES // add directory entry if needed
         //4SAVE DIR ENTRIES //
         //4SAVE DIR ENTRIES String entryDir = fileUtil.getParent(entrySingle);
         //4SAVE DIR ENTRIES if (lastDirEntry == null || ! entryDir.equals (lastDirEntry))
         //4SAVE DIR ENTRIES {
         //4SAVE DIR ENTRIES    lastDirEntry = entryDir;
         //4SAVE DIR ENTRIES    if (entryDir.length () > 0)
         //4SAVE DIR ENTRIES    {
         //4SAVE DIR ENTRIES       theLog.dbg (2, "ZIP", "adding directory entry [" + lastDirEntry + "]");
         //4SAVE DIR ENTRIES       outStream.putNextEntry(new ZipEntry(lastDirEntry + "/"));
         //4SAVE DIR ENTRIES    }
         //4SAVE DIR ENTRIES }

         // add entry
         theLog.dbg (2, "ZIP", "adding entry [" + entrySingle + "]");
         outStream.putNextEntry(entry);

         // write data
         int len = 0;
         byte [] buff = getBuffer ();
         while((len = in.read(buff)) != -1)
            outStream.write(buff, 0, len);

         outStream.closeEntry();
         in.close();
      }
      catch(Exception e)
      {
         theLog.err ("ZIP", "zip writing on zip file: " + e);
         return false;
      }
      resetBuffer ();

      return true;
   }


   /**
      Zip extract entries method
   */
   private void zipEntries (String ziFile, Eva listOfFiles)
   {
      boolean ok = true;

      theLog.dbg (2, "ZIP", "zip entries [" + ziFile + "]");

      listOfFiles.clear ();
      listOfFiles.addLine (new EvaLine ("fileName"));

      try
      {
         // open zip file
         ZipInputStream in = new ZipInputStream(new FileInputStream(ziFile));

         // red all entries
         ZipEntry entry;
         while ((entry = in.getNextEntry()) != null)
         {
            if (! entry.isDirectory ())
            {
               theLog.dbg (2, "ZIP", "add entry [" + entry.getName() + "]");
               listOfFiles.addLine (new EvaLine (entry.getName()));
            }
         }
         in.close();
      }
      catch(Exception e)
      {
         ok = false;
         theLog.severe ("ZIP", "zipEntries [" + ziFile + "]" + e);
      }
      resetBuffer ();

      theLog.dbg (4, "ZIP", "zipEntries " + ((ok) ? "well done.": "ended with errors!"));
   }

   private String tryBetterFileName (String fileName)
   {
      // About the characters to replace in the name:
      // Note that we only call tryBetterFileName if there is some problem
      // creating the target file or the directories, it should not be the
      // usual case.
      //
      for (int ii = 0; ii < fileName.length (); ii ++)
      {
         char ica = fileName.charAt (ii);
         if (" /\\.-+'!_~@€$§ß()".indexOf (ica) != -1) continue;
         if ((ica >= '0' && ica <= '9') ||
             (ica >= 'A' && ica <= 'Z') ||
             (ica >= 'a' && ica <= 'z')) continue;
         fileName = fileName.replace (ica, '_');
      }
      return fileName;
   }


   /**
      unzip method
   */
   private void unzip (String ziFile, List onlyThese, String basePath)
   {
      boolean ok = true;
      byte [] buff = getBuffer ();
      boolean targetIsOnMemory = TextFile.isMemoryFile (basePath);

      theLog.dbg (2, "ZIP", "unzip [" + ziFile + "] on [" + basePath + "]");

      try
      {
         InputStream is = null;

         File asfile = new File (ziFile);
         if (asfile.exists ())
         {
            is = new FileInputStream(ziFile);
         }
         else
         {
            // try url
            is = new URL(ziFile).openStream();
         }
         if (is == null)
         {
            theLog.err ("ZIP", "unzip cannot open [" + ziFile + "] as stream");
            return;
         }

         // open zip stream
         ZipInputStream in = new ZipInputStream(is);

         // read all entries of the tip file
         //
         ZipEntry entry;
         while ((entry = in.getNextEntry()) != null)
         {
            String targetFileName = targetIsOnMemory ? basePath: fileUtil.concatPaths (basePath, entry.getName());

            if (entry.isDirectory () && !targetIsOnMemory)
            {
               //just create directory
               theLog.dbg (2, "ZIP", "create directory [" + targetFileName + "]");
               if (!fileUtil.ensureDirsForFile (targetFileName + "dum"))
               {
                  theLog.dbg (0, "ZIP", "has to change path name [" + targetFileName + "]");
                  targetFileName = tryBetterFileName(targetFileName);
                  if (!fileUtil.ensureDirsForFile (targetFileName + "dum"))
                  {
                     theLog.warn ("ZIP", "could not create directories [" + targetFileName + "]");
                     while (in.read(buff) != -1);
                     continue;
                  }
               }

               // set date
               File fi = new File (targetFileName);
               fi.setLastModified (entry.getTime ());
               continue;
            }

            if (onlyThese != null && ! onlyThese.contains (entry.getName()))
            {
               while (in.read(buff) != -1);
               continue;
            }

            //TODO FEEDBACK --> entry.getName ();
//            if (entry.getMethod() == ZipEntry.DEFLATED)
//               stdout.println("  Inflating: "+entry.getName());
//            else
//               stdout.println(" Extracting: "+entry.getName());

            if (!fileUtil.ensureDirsForFile (targetFileName))
            {
               theLog.dbg (0, "ZIP", "has to change path name [" + targetFileName + "]");
               targetFileName = tryBetterFileName(targetFileName);
            }

            if (fileUtil.ensureDirsForFile (targetFileName))
            {
               theLog.dbg (2, "ZIP", "unzipping [" + entry.getName() + "] on [" + targetFileName + "]");

               TextFile tfOutput = new TextFile ();
               if (!tfOutput.fopen (targetFileName, "wb"))
               {
                  theLog.err ("ZIP", "target file [" + targetFileName + "] could not be opened for write");
                  return;
               }
               boolean memoryFile = tfOutput.isMemoryFile ();

               int len = 0;
               while ((len = in.read(buff)) != -1)
                  tfOutput.writeBytes (buff, len);
               tfOutput.fclose();

               if (!memoryFile)
               {
                  // set date
                  File fi = new File (targetFileName);
                  fi.setLastModified (entry.getTime ());
               }
            }
            else
            {
               theLog.err ("ZIP", "cannot create directories for target file [" + targetFileName + "]");
               ok = false;
               // target directory could not be created, symply read the file
               while (in.read(buff) != -1);
            }
         }
         in.close();
      }
      catch(Exception e)
      {
         ok = false;
         theLog.severe ("ZIP", "unzip [" + ziFile + "] " + e);
      }
      resetBuffer ();

      theLog.dbg (4, "ZIP", "unzip " + ((ok) ? "well done.": "ended with errors!"));
   }


   /**
      gzip method
   */
   private boolean gzip (String fileToZip, String targetZip, boolean setDateTimeFromFile)
   {
      if (targetZip.length () == 0)
         targetZip = fileToZip + ".gz";

      TextFile tfinput = new TextFile ();
      if (!tfinput.fopen (fileToZip, "rb"))
      {
         theLog.err ("ZIP", "gzip source file [" + fileToZip + "] could not be opened for read");
         return false;
      }

      // OPEN GZ FILE TO WRITE
      //
      GZIPOutputStream zipout;
      try
      {
         FileOutputStream out = new FileOutputStream (targetZip);
         zipout = new GZIPOutputStream (out);
      }
      catch (IOException e)
      {
         theLog.severe ("ZIP", "gzip target file [" + targetZip + "] could not be opened" + e);
         return false;
      }

      boolean ok = true;
      theLog.dbg (2, "ZIP", "gzip [" + fileToZip + "] on [" + targetZip + "]");

      // COMPRESS THE FILE
      //
      try
      {
         InputStream in = tfinput.getAsInputStream ();
         if (in != null)
         {
            int len;
            byte [] buff = getBuffer ();
            while ((len = in.read (buff)) != -1)
            {
               zipout.write (buff, 0, len);
               //Feedback ...
               //System.out.print (".");
            }
            in.close ();
         }
      }
      catch (IOException e)
      {
         ok = false;
         theLog.severe ("ZIP", "gzip, the file [" + fileToZip + "] could not be compressed on [" + targetZip + "]" + e);
         // do not return !! we have to close zipout!!
      }

      /*
         ==== CLOSE
      */
      try { zipout.close (); }
      catch (IOException e)
      {
         ok = false;
         theLog.severe ("ZIP", "gzip target file [" + targetZip + "] could not be closed" + e);
      }
      //resetBuffer ();

      if (setDateTimeFromFile)
      {
         File fiori  = new File (fileToZip);
         File figzip = new File (targetZip);
         figzip.setLastModified (fiori.lastModified ());
      }

      theLog.dbg (4, "ZIP", "gzip " + ((ok) ? "well done.": "ended with errors!"));
      return ok;
   }


   /**
      ungzip method
   */
   private boolean ungzip (String oriZip, String targetFile, String dateTimeOption)
   {
      // form targetFile name if needed
      //
      if (targetFile.length () == 0 && oriZip.endsWith(".gz"))
      {
         targetFile = oriZip.substring (oriZip.length () - 3);
      }

      // have a targetFile ?
      //
      if (targetFile.length () == 0)
      {
         theLog.err ("ZIP", "ungzip, target file not specified and gzip file [" + oriZip + "] does not end with .gz, no decompression performed!");
         return false;
      }

      if (! fileUtil.ensureDirsForFile (targetFile))
      {
         theLog.err ("ZIP", "cannot create directories for target file [" + targetFile + "]");
         return false;
      }

      TextFile tfOutput = new TextFile ();
      if (!tfOutput.fopen (targetFile, "wb"))
      {
         theLog.err ("ZIP", "target file [" + targetFile + "] could not be opened for write");
         return false;
      }

      // Open gz file to read
      //
      GZIPInputStream zipin;
      try
      {
         zipin = new GZIPInputStream (new FileInputStream (oriZip));
      }
      catch (IOException e)
      {
         theLog.severe ("ZIP", "ungzip, source file [" + oriZip + "] could not be opened" + e);
         return false;
      }

      boolean ok = true;
      theLog.dbg (2, "ZIP", "ungzip [" + oriZip + "] on [" + targetFile + "]");

      // Uncompress the file
      //
      try
      {
         //!!! FileOutputStream out = new FileOutputStream (targetFile);

         int len;
         byte [] buff = getBuffer ();
         while ((len = zipin.read (buff)) != -1)
         {
            //!!! out.write (buff, 0, len);
            tfOutput.writeBytes (buff, len);
            //(o) TODO_listix_cmd add Zip feedback ... System.out.print (".");
         }
         tfOutput.fclose ();
      }
      catch (IOException e)
      {
         ok = false;
         theLog.severe ("ZIP", "ungzip, source file [" + oriZip + "] could not be decompressed" + e);
         // no return !! we have to close zipout!!
      }

      /*
         ==== CLOSE
      */
      try { zipin.close (); }
      catch (IOException e)
      {
         ok = false;
         theLog.severe ("ZIP", "ungzip, source file [" + oriZip + "] could not be closed" + e);
      }

      if (! dateTimeOption.equalsIgnoreCase ("now"))
      {
         if (dateTimeOption.equalsIgnoreCase ("gz") || dateTimeOption.equalsIgnoreCase ("gzip"))
         {
            File figzip = new File (oriZip);
            File fiori  = new File (targetFile);
            fiori.setLastModified (figzip.lastModified ());
         }
         else
         {
            File fiori  = new File (targetFile);
            fiori.setLastModified (DateFormat.getAsLong (dateTimeOption));
         }
      }

      theLog.dbg (4, "ZIP", "ungzip " + ((ok) ? "well done.": "ended with errors!"));
      //resetBuffer ();
      return ok;
   }
}
