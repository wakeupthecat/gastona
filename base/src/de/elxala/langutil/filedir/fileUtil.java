/*
java packages for gastona
Copyright (C) 2005-2026  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.langutil.filedir;

/**   ======== de.elxala.langutil.filedir ==========================================
   Alejandro Xalabarder
*/

import java.util.*;
import java.io.*;
import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import java.util.zip.*;

/**
   class fileUtil
   @author Alejandro Xalabarder Aulet
   @date   2006

   Utilities with files and paths
*/
public class fileUtil
{
   protected static logger log = new logger (null, "de.elxala.langutil.filedir.fileUtil", null);
   public static final String  DIR_SEP = "" + File.separatorChar;

   public static final int BYTE_BUFFER_SIZE = 1024;
   public static final int DEFAULT_PART_LIMIT_SIZE = 20*1024*1024;
   public static final int MINIMUM_DEFINED_PART_SIZE = BYTE_BUFFER_SIZE;

   public static boolean endsWithSeparator (String path)
   {
      return path.endsWith("/") || path.endsWith("\\");
   }

   /**
      resolve /./ and /../ in path without using canonical path in order not to convert
      symbolic links to its pointed directory, that is, if sdcard is a symbolic link

         /sdcard -> /mnt/sdcard

         and we have

         /sdcard/mydir/../other

         I want to obtain

         /sdcard/other

         and not

         /mnt/sdcard/other

      some cases to test

         ./
         ./aa
         /.
         /aaa/.
         /aaa/./bbb

         ../aa
         /..
         aaa/..
         aaa/../b
         aaa/.b/../.c

   */
   public static String resolveRelativePointsInPath (String path)
   {
      int ava = 0;

      // get rid from "//" and "\\" strings
      //
      if (path.indexOf ("//") != -1 || path.indexOf ("\\\\") != -1)
      {
         Cadena str = new Cadena (path);
         str.replaceMe ("//", "/");
         str.replaceMe ("\\\\", "\\");
         path = str.getStr ();
      }

      while (ava < path.length ())
      {
         if (path.equals(".")) return ".";
         if (path.equals("/.")) return "/";
         if (path.equals("./")) return ".";
         if (path.equals("\\.")) return "\\";
         if (path.equals(".\\")) return ".";

         if (path.charAt(ava) != '.')
         {
            ava = indxNextSlash (path, ava);
            continue;
         }

         if (ava+1 >= path.length () || path.charAt(ava+1) == '\\' || path.charAt(ava+1) == '/')
         {
            // single point "/."
            if (!path.equals("."))
               path = cutFromTo (path, ava, ava+2);

            //System.out.println ("single point /. [" + path + "]");
            continue;
         }

         // either /.. or not interesting
         if (path.charAt(ava+1) == '.'
             && ava > 1 // "../" and "/.." cannot be resolved !
             && (path.length() == ava+2 || (path.charAt(ava+2) == '\\' || path.charAt(ava+2) == '/'))
            )
         {
            // doble point "/.."
            int pos0 = indxPreviousSlash (path, ava-2);
            //System.out.println ("double point /.. cut [" + path + "] from " + (pos0+1) + " to " + (ava+2));
            path = cutFromTo (path, (pos0 < 0 ? 0: pos0+2), ava+3);
            ava = pos0;
            //System.out.println ("double point /.. [" + path + "]");
         }

         // look for previous /
         ava= indxNextSlash (path, ava);
      }

      //avoid ending with /
      if (path.length () > 1 && (path.charAt(path.length ()-1) == '/' || path.charAt(path.length ()-1) == '\\'))
         path = path.substring (0, path.length ()-1);
      return path;
   }

   public static String getPathOsSeparator (String path)
   {
      return  File.separator.equals ("/") ? getPathLinuxSeparator (path): getPathWinSeparator (path);
   }

   public static String getPathWinSeparator (String path)
   {
      return path.replace ('/', '\\');
   }

   public static String getPathLinuxSeparator (String path)
   {
      return  path.replace ('\\', '/');
   }


   private static int indxNextSlash (String path, int pos)
   {
      while (pos < path.length () && pos >= 0 &&
             path.charAt(pos) != '\\' &&
             path.charAt(pos) != '/')
         pos ++;
      return pos + 1;
   }

   private static int indxPreviousSlash (String path, int pos)
   {
      while (pos > 0 &&
             path.charAt(pos) != '\\' &&
             path.charAt(pos) != '/')
         pos --;
      return pos - 1;
   }

   private static String cutFromTo (String base, int from, int to)
   {
      from = Math.max (0, from);
      to   = Math.min (base.length (), to);
      //System.out.println ("CUR from " + from + " [" + base + "] to " + to);
      return base.substring(0, from) + base.substring(to);
   }


   /**
      concatenate two paths with system directory separator
      Note that p1 might end with the separator or not and note also that this cannot be checked or unified!
      (maybe ending all the directories with separator but it does not look well!)
      For instance
            C:       have the meaning of current directory in unit C:
            C:\      is the root directory of C:
            C:\dir   is the directory "dir" of C:
   */
   public static String concatPaths (String p1, String p2)
   {
      if (p2 == null || p2.length() == 0)
         return p1;

      if (p1 == null || p1.length() == 0)
         return p2;

      return p1 + (endsWithSeparator (p1) ? "": DIR_SEP) + p2;
   }


   public static String getParent (String fullpath)
   {
      // it doesn't come from a filtered extension so we have to calculate it
      int pu = fullpath.length ();
      String parent = "";
      while (--pu >= 0)
      {
         if (fullpath.charAt(pu) == '/' || fullpath.charAt(pu) == '\\')
         {
            parent = fullpath.substring (0, pu);
            break;
         }
      }
      return parent;
   }

   public static String getJustNameAndExtension (String fullpath)
   {
      // it doesn't come from a filtered extension so we have to calculate it
      int pu = fullpath.length ();
      String name = fullpath;
      while (--pu >= 0)
      {
         if (fullpath.charAt(pu) == '/' || fullpath.charAt(pu) == '\\')
         {
            name = fullpath.substring (pu+1);
            break;
         }
      }
      return name;
   }

   public static String getLastExtension (String path)
   {
      // it doesn't come from a filtered extension so we have to calculate it
      int pu = path.length ();
      String extension = "";
      while (--pu >= 0)
      {
         if (path.charAt(pu) == '.')
         {
            extension = path.substring (pu+1);
            break;
         }
         else if (path.charAt(pu) == '/' || path.charAt(pu) == '\\') break;
      }
      return extension;
   }

   //aliases from getLastExtension
   public static String getShortExtension (String fullpath)  {  return getLastExtension (fullpath); }
   public static String getExtension      (String fullpath)  {  return getLastExtension (fullpath); }

   // if the name have more dots take the first one as start of extension
   //  e.g.
   //    getFullExtension ("myLog.csv.db")
   // returns "csv.db"
   //
   public static String getFullExtension (String fullpath)
   {
      String nameAndExt = getJustNameAndExtension (fullpath);
      int firstDot = nameAndExt.indexOf (".");
      return (firstDot >= 0) ? nameAndExt.substring (firstDot + 1): "";
   }

   //aliases from getFullExtension
   public static String getLongExtension (String fullpath)  {  return getFullExtension (fullpath); }

   // returns the File structure for the path
   public static File getFileStruct (String fullpath)
   {
      // Note: File2 handles file names given with quotes "xxxx"
      return new File2 (fullpath);
   }

   static public String getApplicationDir ()
   {
      return System.getProperty ("user.dir");
   }

   public static String createTemporal ()
   {
      return createTemporal ("tmp", "tmp");
   }

   public static String createTemporal (String prefix)
   {
      return createTemporal (prefix, "tmp");
   }

   public static String getTemporalDirInitial ()
   {
      return uniFileUtil.getTemporalDirInitial ();
   }

   public static String getTemporalDirApp ()
   {
      return uniFileUtil.getTemporalDirApp ();
   }

   public static String createTemporal (String prefix, String sufix)
   {
      return createTemporal (prefix, sufix, getTemporalDirApp (), true);
   }

   public static String createTemporal (String prefix, String sufix, String tempdir)
   {
      return createTemporal (prefix, sufix, tempdir, true);
   }

   public static String createTemporal (String prefix, String sufix, String tempdir, boolean tryToRemoveItOnExit)
   {
      File uniqFileTmp;

      if (prefix.length() < 3)
      {
         // avoid java.lang.IllegalArgumentException: Prefix string too short
         prefix = "tmp" + prefix;
      }

      try
      {
         // Asegurar directorio temporal (debido a "gastonaTemp")
         // Seguramente es necesario hacelo asi, aunque podri'a ser suficiente
         // hacerlo solo la primera vez que se cree un fichero temporar "delete on exit"
         File tmpDir = new File2 (tempdir);
         tmpDir.mkdirs ();

         uniqFileTmp = File.createTempFile(prefix, sufix, new File2 (tempdir));
         log.dbg (2, "createTemporal", "temp file to create \"" + uniqFileTmp + "\"");
         if (tryToRemoveItOnExit)
         {
            uniFileUtil.deleteTmpFileOnExit (uniqFileTmp);
         }
         try
         {
            return uniqFileTmp.getCanonicalPath ();
         }
         catch (Exception e)
         {
            log.fatal ("createTemporal", "canonical path for \"" + uniqFileTmp + "\") " + e, e.getStackTrace());
         }
      }
      catch (Exception e)
      {
         log.fatal ("createTemporal", "exception with prefix \"" + prefix + "\" sufix \"" + sufix + "\" tempdir \"" + tempdir + "\" " + e, e.getStackTrace());
      }
      return null;
   }

   public static String createTempDir (String prefix, boolean doDestroyItOnExit)
   {
      return createTempDir (prefix, getTemporalDirApp (), doDestroyItOnExit);
   }

   public static String createTempDir(String prefix, String dirBase, boolean doDestroyItOnExit)
   {
      if (dirBase == null)
         dirBase = getTemporalDirApp ();

      //(o) ensure_mkdirs!
      String tempDir = createTemporal(prefix, "tmpDir", dirBase, true);
      if (tempDir == null)
      {
         // for some reason it did not create a unique file!
         return null;
      }

      //delete the file and do mkdirs to create it as directory using the same name!
      //
      File tDir = new File2 (tempDir);
      tDir.delete();
      if (!tDir.mkdirs())
      {
         log.fatal ("createTempDir", "cannot make dir of path [" + tempDir + "]");
         return null;
      }

      // Now we are sure that this directory has been created new
      // so its contain can be removed with no danger for other directories
      //
      if (doDestroyItOnExit)
      {
         //safe try delete
         uniFileUtil.deleteTmpFileOnExit (tDir);

         //agressive delete
         destroyDirOnExit (tDir);
      }

      return tempDir;
   }

   public static boolean existSystemFile (String fullPath)
   {
      return existSystemPath (fullPath, true, false);
   }

   public static boolean existSystemDir (String fullPath)
   {
      return existSystemPath (fullPath, false, true);
   }

   public static boolean existSystemPath (String fullPath)
   {
      return existSystemPath (fullPath, true, true);
   }

   public static boolean existSystemPath (String fullPath, boolean asFile, boolean asDir)
   {
      File path = new File2 (fullPath);
      return
            path.exists () &&
            ((path.isDirectory () && asDir) ||
             (path.isFile () && asFile))
            ;
   }

   public static boolean ensureDirsForFile (String fullFilePath)
   {
      if (TextFile.isMemoryFile (fullFilePath))
      {
         return true;
      }
      // ensure directory
      File dirFile = new File2 (fullFilePath);
      // System.out.println ("1 dirfile = " + dirFile);

      dirFile = dirFile.getParentFile();
      // System.out.println ("2 dirfile = " + dirFile);

      if (dirFile != null && !dirFile.exists ())
      {
         dirFile.mkdirs ();
         if (! dirFile.exists ())
         {
            log.err ("ensureDirsForFile", "cannot make dirs of path [" + dirFile.getPath () + "]");
            return false;
         }
      }
      return true;
   }


   /**
      Attempt to create the directories needed for the file 'existingBasePath' + 'restOfFullFilePath'
      making them NOT deletables on exit (see mkdirs (String, String, boolean, boolean))
   */
   public static void mkdirsForFile (String existingBasePath, String restOfFullFilePath)
   {
      mkdirs (existingBasePath, restOfFullFilePath, false, true);
   }

   /**
      Attempt to create the directories needed for the file 'existingBasePath' + 'restOfFullFilePath'
      making them deletables on exit depending on 'deleteOnExit' (see mkdirs (String, String, boolean, boolean))
      NOTE: if 'deleteOnExit' is true also the final file will be deletable on exit (even if not yet created)
   */
   public static void mkdirsForFile (String existingBasePath, String restOfFullFilePath, boolean deleteOnExit)
   {
      mkdirs (existingBasePath, restOfFullFilePath, deleteOnExit, true);
   }

   /**
      Attempt to create the directories of the path 'existingBasePath' + 'restOfFullPath'
      making them NOT deletables on exit (see mkdirs (String, String, boolean))
   */
   public static void mkdirs (String existingBasePath, String restOfFullFilePath)
   {
      mkdirs (existingBasePath, restOfFullFilePath, false, false);
   }

   /**
      Attempt to create the directories needed for the path 'existingBasePath' + 'restOfFullFilePath'
      making them deletables on exit depending on 'deleteOnExit' (see mkdirs (String, String, boolean, boolean))
   */
   public static void mkdirs (String existingBasePath, String restOfFullFilePath, boolean deleteOnExit)
   {
      mkdirs (existingBasePath, restOfFullFilePath, deleteOnExit, false);
   }

   /**
      Attempt to create the directories needed for the path 'existingBasePath' + 'restOfFullPath'
      but only the part contained in 'restOfFullFilePath'.

      'existingBasePath' should exist and might not be deletable on exit.
      if 'lastOneIsFile' is true the last part of 'restOfFullPath' is a file, this will not be created
      but it might be deletable on exit

      if 'deleteOnExit' is true, the created directories and the last file if any will be deletables on exit

      The method take cares of the lack or excess of path separators between 'existingBasePath' and 'restOfFullFilePath'
      thus these might be given in many ways.

      (Paths and files will be made deletable on exit if 'deleteOnExit' is true)

      Examples:
         fileUtil.mkdirs (".", "subdir1/subdir2/file.txt"    , true, true);    // works fine
         fileUtil.mkdirs ("./", "\\subdir1\\subdir2/file.txt", true, true);    // works too

         fileUtil.mkdirs ("", "A:/java/app/temp/subdir", true, false);

   */
   public static void mkdirs (String existingBasePath, String restOfFullPath, boolean deleteOnExit, boolean lastOneIsFile)
   {
//System.out.println ("mkdirs ([" + existingBasePath + "], [" + restOfFullPath + "], onExit[" + deleteOnExit + "], isFile[" + lastOneIsFile + "])");
      // ensure dirbase ends with /
      String dirBase = existingBasePath;
      if (dirBase.length () == 0 && utilSys.isSysUnix)
         dirBase += "/";

      // separate rest of fullpath and create directories in order
      java.util.List dirList = de.elxala.langutil.Cadena.simpleToList (restOfFullPath, "/\\");
      String subPath = "";
      for (int ii = 0; ii < dirList.size (); ii ++)
      {
         subPath += (String) dirList.get (ii);
//System.out.println ("mkdirs ii " + ii + " subpath [" + subPath + "]");
         if (subPath.length () > 0)
         {
            File fi = new File2 (concatPaths (dirBase, subPath));
            if (!lastOneIsFile || ii < dirList.size ()-1)
            {
//System.out.println ("mkdirs mkdir");
               fi.mkdir ();
               subPath += "/";
            }

            if (deleteOnExit)
               uniFileUtil.deleteTmpFileOnExit (fi);
         }
      }
   }

    public static File getNewFile (String vagueFileName)
    {
        return doubleCheckFile (resolveCurrentDirFileName (vagueFileName));
    }

    public static File2 getNewFile2 (String vagueFileName)
    {
        return new File2(doubleCheckFile (resolveCurrentDirFileName (vagueFileName)).getAbsolutePath ());
    }

    // this method is thought for Android Apps getFilePathFromVagueFilename
    public static String resolveCurrentDirFileName (String vagueFileName)
    {
        return uniFileUtil.resolveCurrentDirFileName (vagueFileName);
    }

    public static File doubleCheckFile (String pathName)
    {
        File pathFi = new File2 (pathName.length () == 0 ? ".": pathName);
        if (!pathFi.exists ())
        {
            // Second try
            // This workaround is needed sometimes usually when current directory has been changed, no idea why this happens!
            //
            pathFi = new File2 (pathFi.getAbsolutePath ());
            if (pathFi.exists ())
                log.warn ("Workaround for false 'file not exists' required for [" + pathName + "] [" + pathFi.getPath () + "]");
        }
        return pathFi;
   }

   public static File getRootDirectoryOf (String referenceFile, String rootMark)
   {
      File reference = doubleCheckFile (referenceFile);
      if (! reference.exists ()) return null;

      String cannon = ".";
      try { cannon = reference.getCanonicalPath(); } catch (Exception e) {}
      return getRootDirectoryOf (new File2 (cannon), rootMark);
   }

   private static File getRootDirectoryOf (File referenceFile, String rootMark)
   {
      File tryfile = doubleCheckFile (referenceFile + "/" + rootMark);
      if (tryfile.exists ())
         return tryfile.getParentFile ();

      File parent = referenceFile.getParentFile();
      if (parent == null)
         return null;

      return getRootDirectoryOf (parent, rootMark);
   }

   //
   // ensure remove temporary dir and its contains
   // use with care!
   // must be ensure that the directory is new created before
   // puting into the list destroyableTmpDirs
   //
   //  destroyFileOrDirRecursively removes all inside !
   //
   private static final boolean ACTIVATE_AGRESSIVE_DESTROY_TMP_DIR = false;
   // NOTE about when to use ACTIVATE_AGRESSIVE_DESTROY_TMP_DIR
   //
   // currently not used because
   //    we have control about every single temporary file and directory being created
   //    so applying File::deleteOnExit to each one is enough to ensure its deletion in normal scenarios
   //    (unless they are in use by other application!)
   //
   // should be used if
   //    When replacing sqlite by dbsql (sqlite + BerkeleyDB), since when we create a new temporary db
   //    actually BDB is creating more files inside the directory xxxx-journal
   //    Want to use the construction
   //          @<myTempDir>/myUniquefile1.txt
   //    so the user is creating her unique files and File::deleteOnExit cannot be applied on them
   //

   private static List destroyableTmpDirs = new Vector ();

   private static void destroyDirOnExit (File fi)
   {
      log.dbg (2, "destroyDirOnExit", "directory " + fi.getAbsolutePath () + " will be destroyed on exit the current script");
      destroyableTmpDirs.add (fi);
   }

   private static void destroyFileOrDirRecursively (File dir)
   {
      if (dir.isDirectory ())
      {
         File [] arrfi = dir.listFiles ();
         log.dbg (4, "destroyFileOrDirRecursively", "deleting directory " + dir.getAbsolutePath () + " with " + arrfi.length + " entries");

         for (int ff =0; ff < arrfi.length; ff ++)
            if (arrfi[ff].exists ())
               destroyFileOrDirRecursively (arrfi[ff]);
      }
      if (ACTIVATE_AGRESSIVE_DESTROY_TMP_DIR)
      {
         log.dbg (2, "destroyFileOrDirRecursively", "delete [" + dir.getAbsolutePath () + "]!");
         dir.delete ();
      }
      else log.dbg (0, "destroyFileOrDirRecursively", "would delete [" + dir.getAbsolutePath () + "]!");
   }

   public static void destroyAllTmpDirDestroyables ()
   {
      log.dbg (2, "destroyAllTmpDirDestroyables", "deleting " + destroyableTmpDirs.size () + " entries");

      // doing it in reverse order is better so we delete from the most recent to the oldest object
      //
      for (int ii = destroyableTmpDirs.size ()-1; ii >= 0; ii --)
      {
         File fi = (File) destroyableTmpDirs.get (ii);
         log.dbg (4, "destroyAllTmpDirDestroyables", "deleting " + fi.getName ());
         if (!fi.exists ())
         {
            log.dbg (4, "destroyAllTmpDirDestroyables", "it does not exist!");
            continue;
         }

         destroyFileOrDirRecursively (fi); // or file
      }
      destroyableTmpDirs = new Vector ();
   }

   public static boolean looksLikeUrl (String fileName)
   {
      return fileName.indexOf ("://") >= 0;
   }

   public static boolean moveFile (String fromFullPath, String toFullPath)
   {
      log.dbg (4, "moveFile", "from \"" + fromFullPath + "\" to \"" + toFullPath + "\"");

      if (copyFile (fromFullPath, toFullPath, false))
      {
         File src = new File2 (fromFullPath);
         File tgt = new File2 (toFullPath);

         if (src != null && src.exists() && src.isFile() &&
             tgt != null && tgt.exists() && tgt.isFile() &&
             (tgt.compareTo (src) != 0) &&     // not referencing the same path
             tgt.length () == src.length ()      // file length are identical
            )
            return src.delete ();

         log.err ("moveFile", "could not remove source file successfully " +
                              "comp:" + tgt.compareTo (src) +
                              " srcLen: " + src.length () +
                              " tgtLen: " + tgt.length ());
         // log.err ("moveFile", "could not remove source file successfully");
      }
      else
      {
         log.err ("moveFile", "could not copy successfully \"" + fromFullPath + "\" to \"" + toFullPath + "\"");
      }
      return false;
   }


   public static boolean copyFile (String zipFileSource, String fromParh, String toFullPath)
   {
      return copyFileGeneral (zipFileSource, fromParh, toFullPath, 1, -1, false);
   }

   public static boolean copyFileFromZip (String zipFileSource, String fromParh, String toFullPath, boolean targetIsTemporary)
   {
      return copyFileGeneral (zipFileSource, fromParh, toFullPath, 1, -1, targetIsTemporary);
   }

   public static boolean copyFile (String fromFullPath, String toFullPath)
   {
      return copyFileGeneral (null, fromFullPath, toFullPath, 1, -1, false);
   }

   public static boolean copyFile (String fromFullPath, String toFullPath, boolean targetIsTemporary)
   {
      return copyFileGeneral (null, fromFullPath, toFullPath, 1, -1, targetIsTemporary);
   }

   // copy source file into target path
   // if specified fromZipFile then the source will be seek in that zip/jar file
   //
   // otherwise the file may come from:
   //     - memory file (e.g. ":mem xxx")
   //     - file system file
   //     - classpath (e.g. from own gastona.jar)
   //     - url (e.g. https://www.blah.com/myfile)
   //
   public static boolean copyFileGeneral (String fromZipFile, String fromPath, String toPath, long startByte, long lengthBytes, boolean targetIsTemporary)
   {
      log.dbg (4, "copyFileGeneral", "from zip \"" + fromZipFile + "\" fromPath \"" + fromPath + "\" toPath \"" + toPath + "\" start-length (" + startByte + ", " + lengthBytes + ")");

      // TODO: check if fromPath and toPath refer to the same actual file and return false if so

      long offsetByte = startByte > 0 ? startByte - 1: 0;

      someFile    srcFile = null;
      InputStream zipInpStream = null;

      try // needed for zip stuff
      {
         // ----- Source file
         //

         if (fromZipFile != null && fromZipFile.length () > 0)
         {
            // source comes from a zip file
            //
            ZipFile zipf = null;
            zipf = new ZipFile(fromZipFile);
            ZipEntry zie = zipf.getEntry (fromPath);
            if (zie == null || zie.isDirectory ())
            {
               log.err ("copyFileGeneral", "Source file \"" + fromPath + "\" not found in zip file \"" + fromZipFile + "\"!");
               return false;
            }
            zipInpStream = zipf.getInputStream (zie);
            // zie.getSize ()
         }
         else
         {
            // other source (file system, memory file, classpath, url etc)
            //
            srcFile = new someFile ();
            if (! srcFile.fopen (fromPath, "rb"))
            {
               log.err ("copyFileGeneral", "Source file \"" + fromPath + "\" not found!");
               return false;
            }
         }

         // ----- Target file
         //

         // ensure directories
         fileUtil.mkdirsForFile ("", toPath, targetIsTemporary);

         TextFile vuelca = new TextFile ();
         if (! vuelca.fopen (toPath, "wb"))
         {
            log.err ("copyFileGeneral", "target file \"" + toPath + "\" could not be opened!");
            return false;
         }

         // ----- Loop to copy the file
         //

         long bytesWritten = 0;
         int nn = 0;
         boolean ok = true; // a file of length 0 could also be copied

         byte [] puffer = new byte [BYTE_BUFFER_SIZE];
         do
         {
            nn = srcFile != null ?
                   srcFile.readBytes (puffer):
                   zipInpStream.read (puffer);
            if (nn > 0)
            {
               // handle offsetByte (startByte)
               //
               int skip = offsetByte >= nn ? nn: (int) offsetByte;
               offsetByte = offsetByte >= nn ? offsetByte - nn: 0;

               if (skip < nn)
               {
                  // handle lengthBytes
                  //
                  int towrite = nn - skip;
                  if (lengthBytes != -1 && bytesWritten + towrite > lengthBytes)
                  {
                     towrite = (int) (lengthBytes - bytesWritten);
                  }

                  if (towrite > 0)
                  {
                     // finally write the bytes in target
                     bytesWritten += towrite;
                     ok = vuelca.writeBytes (puffer, skip, towrite);
                  }
               }
            }

            if (srcFile != null && srcFile.feof ()) break;
         }
         while (ok && nn > 0);

         vuelca.fclose ();
         if (srcFile != null)
            srcFile.fclose ();
         if (zipInpStream != null)
            zipInpStream.close ();

         if (false == ok)
         {
            log.err ("copyFileGeneral", "terminated with some error during writing");
            return false;
         }
      }
      catch (Exception e)
      {
         log.err ("copyFileGeneral", "exception from zip \"" + fromZipFile + "\" fromPath \"" + fromPath + "\" toPath \"" + toPath + "\"" + e);
         return false;
      }

      log.dbg (4, "copyFileGeneral", "done");
      return true;
   }

   //
   public static boolean splitFile (String fromFullPath, String toFullPathBase, int numberOfParts)
   {
      return splitFile (fromFullPath, toFullPathBase, numberOfParts, DEFAULT_PART_LIMIT_SIZE);
   }

   // note :
   //   windows assign bizarre file types to the extensions
   //
   //    xxx.123    OpenOffice.org XML 1.0 Table
   //    xxx.386    Virtual driver
   //    xxx.669    Audio File (VLC)
   //
   public static boolean splitFile (String fromFullPath, String toFullPathBase, int numberOfParts, long partLimitBytes)
   {
      if (toFullPathBase == null || toFullPathBase.length () == 0)
         toFullPathBase = fromFullPath;

      // open for read
      //
      TextFile srcFile = new TextFile ();
      if (! srcFile.fopen (fromFullPath, "rb"))
      {
         log.err ("splitFile", "Source file \"" + fromFullPath + "\" not found!");
         return false;
      }

      if (numberOfParts > 0)
      {
         // try to find out the length of the file
         File fitxer = new File (fromFullPath);
         if (fitxer != null && fitxer.exists () && fitxer.isFile ())
         {
            partLimitBytes = fitxer.length () / numberOfParts;
         }
      }

      // minimum partLimit is to ensure that in one read involve maximum two parts
      //
      if (partLimitBytes <= 0)
         partLimitBytes = DEFAULT_PART_LIMIT_SIZE;
      else if (partLimitBytes < MINIMUM_DEFINED_PART_SIZE)
         partLimitBytes = MINIMUM_DEFINED_PART_SIZE;

      log.dbg (4, "splitFile", "from \"" + fromFullPath + "\" to \"" + toFullPathBase + "\" in parts with max size " + partLimitBytes + " bytes");

      String partEnd = "";
      int partNo = 0;
      long partBytesDone = -1; // to start opening a partNo file

      TextFile vuelca = new TextFile ();

      int nRead = 0;
      int nWritten = 0; // usually same as nRead except when we are between two target parts

      boolean ok = true; // a file of length 0 could also be copied
      byte [] puffer = new byte [MINIMUM_DEFINED_PART_SIZE];

      do
      {
         // ensure the proper partNo file is opened
         //
         if (partBytesDone < 0 || partBytesDone >= partLimitBytes)
         {
            if (partBytesDone < 0)
            {
               // ensure target directories
               //
               fileUtil.mkdirsForFile ("", toFullPathBase, false);
            }

            vuelca.fclose ();
            partNo ++;
            partBytesDone = 0;

            // e.g. ".017" (.999 continues with .1000 etc)
            partEnd = "." + (partNo < 100 ? "0":"") + (partNo < 10 ? "0":"") + partNo;

            if (! vuelca.fopen (toFullPathBase + partEnd, "wb"))
            {
               log.err ("splitFile", "target file \"" + toFullPathBase + partEnd + "\" could not be opened!");
               return false;
            }

            // nRead and nWritten are the values of the last read/write
            // of the current "puffer"
            //
            if (nRead > 0 && nWritten < nRead)
            {
               // remaining read bytes
               // now it is ensured that is less than MINIMUM_DEFINED_PART_SIZE
               partBytesDone = nRead - nWritten;
               vuelca.writeBytes (puffer, nWritten, (int) partBytesDone);
            }
         }

         // write until partNo limit
         //
         nRead = srcFile.readBytes (puffer);
         nWritten = nRead; // by now ...

         if (nRead > 0)
         {
            // write below the limit if necessary
            if (partBytesDone + nRead > partLimitBytes)
               nWritten = (int) (partLimitBytes - partBytesDone);

            ok = vuelca.writeBytes (puffer, nWritten);
            partBytesDone += nWritten;
         }
      }
      while (ok && nRead > 0 && (false == srcFile.feof () || nRead != nWritten));

      vuelca.fclose ();
      srcFile.fclose ();

      log.dbg (4, "splitFile", "done");
      return ok;
   }

   //    join sourceFileBase.001, sourceFileBase.002 ... into targetFile
   //
   public static boolean joinFiles (String sourceFileBase, String targetFile, boolean targetIsTemporary)
   {
      if (targetFile == null || targetFile.length () == 0)
         targetFile = sourceFileBase;

      log.dbg (4, "joinFiles", "from \"" + sourceFileBase + ".00x\" into \"" + targetFile);

      TextFile srcFile = new TextFile ();
      TextFile vuelca = new TextFile ();
      int partNo = 0;
      String partName = "";
      int nRead = 0;
      boolean writeOk = true; // a file of length 0 could also be copied
      byte [] puffer = new byte [MINIMUM_DEFINED_PART_SIZE];

      if (! vuelca.fopen (targetFile, "wb"))
      {
         log.err ("joinFiles", "target file \"" + targetFile + "\" could not be opened!");
         return false;
      }
      // ensure target directories
      //
      fileUtil.mkdirsForFile ("", targetFile, targetIsTemporary);

      do
      {
         partNo ++;
         // try to open part partNo for read

         // e.g. "base.017" (.999 continues with .1000 etc)
         //
         partName = sourceFileBase + "." + (partNo < 100 ? "0":"") + (partNo < 10 ? "0":"") + partNo;
         if (! srcFile.fopen (partName, "rb"))
         {
            if (partNo == 1)
            {
               log.err ("joinFiles", "Source file \"" + partName + "\" not found!");
               return false;
            }
            // no more parts to join
            // end of main loop
            break;
         }

         // write content on target
         do
         {
            nRead = srcFile.readBytes (puffer);
            if (nRead > 0)
            {
               writeOk = vuelca.writeBytes (puffer, nRead);
            }
         }
         while (writeOk && nRead > 0 && false == srcFile.feof ());
         srcFile.fclose ();

         if (! writeOk)
         {
            log.err ("joinFiles", "Issue writing on target \"" + targetFile + "\" !");
         }
      }
      while (true);
      vuelca.fclose ();

      log.dbg (4, "joinFiles", "done");
      return writeOk;
   }

   public static int hexchar2int (char car)
   {
      return (car >= '0' && car <= '9') ? car - '0' :
             (car >= 'A' && car <= 'F') ? 10 + car- 'A' :
             (car >= 'a' && car <= 'f') ? 10 + car - 'a' : -1;
   }

   public static String byte2hexstr (byte byteval)
   {
      int unint = byteval & 0xFF; // java +8 ... byteval.toUnsignedInt
      String sal = Integer.toHexString(unint).toUpperCase();
      return (sal.length () == 2) ? sal: "0"+sal;
   }

   public static byte[] hexStr2ByteArr (String str)
   {
      str = str.replaceAll("\\s", "");
      int len = str.length();
      if (len % 2 == 1) str = "0" + str;
      if (len < 2) return new byte [0];

      byte[] data = new byte[len / 2];
      for (int ii = 0, indx = 0; ii < len; ii += 2, indx ++)
      {
         data[indx] = (byte) (hexchar2int (str.charAt (ii)) * 16 + hexchar2int (str.charAt (ii+1)));
      }
      return data;
   }

   // EXEMPLARIKKOS
   //                            10                            20                            30                            40                            50
   //                            |                             |                             |                             |                             |
   // index: 0
   // 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10
   // 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10
   // ...
   //
   // index: 500
   // 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10
   // 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10 01 02 03 04 05 06 07 08 09 10
   // ...
   //
   // or
   //                   10                  20                  30                  40                  50
   //                   |                   |                   |                   |                   |
   // 0102030405060708091001020304050607080910010203040506070809100102030405060708091001020304050607080910
   //
   public static boolean binaryFileToHexText (String fromFullPath, String toFullPath, long startByte, long lengthBytes, boolean targetIsTemporary)
   {
      log.dbg (4, "binaryFileToHexText", "from \"" + fromFullPath + "\" to \"" + toFullPath + "\" start-length (" + startByte + ", " + lengthBytes + ")");

      long offsetByte = startByte > 0 ? startByte - 1: 0;

      // TODO: check if fromFullPath and toFullPath refer to the same actual file and return false if so

      // open for read
      //
      TextFile srcFile = new TextFile ();
      if (! srcFile.fopen (fromFullPath, "rb"))
      {
         log.err ("binaryFileToHexText", "Source file \"" + fromFullPath + "\" not found!");
         return false;
      }

      // ensure target directories
      //
      fileUtil.mkdirsForFile ("", toFullPath, targetIsTemporary);

      // open for write
      //
      TextFile vuelca = new TextFile ();
      if (! vuelca.fopen (toFullPath, "w"))
      {
         log.err ("binaryFileToHexText", "target file \"" + toFullPath + "\" could not be created!");
         return false;
      }

      String SPACES = "                           ";
      for (int pp = 0; pp < 5; pp ++)
         vuelca.writeString ((pp == 0 ? "": " ") + SPACES + ((pp+1)*10));
      vuelca.writeLine ();

      for (int pp = 0; pp < 5; pp ++)
         vuelca.writeString ((pp == 0 ? "": "  ") + SPACES + "|");
      vuelca.writeLine ();

      // loop to copy the file
      //
      long bytesWritten = 0;
      int nn = 0;
      boolean ok = true; // a file of length 0 could also be copied
      int totpassed = 0;

      byte [] puffer = new byte [50];
      do
      {
         nn = srcFile.readBytes (puffer);
         if (nn <= 0) break;

         // handle offsetByte (startByte)
         //
         int skip = offsetByte >= nn ? nn: (int) offsetByte;
         offsetByte = offsetByte >= nn ? offsetByte - nn: 0;

         int towrite = nn;
         if (skip < nn)
         {
            // handle lengthBytes
            //
            towrite = nn - skip;
            if (lengthBytes != -1 && bytesWritten + towrite > lengthBytes)
            {
               towrite = (int) (lengthBytes - bytesWritten);
            }

            if (towrite > 0)
            {
               if (totpassed % 500 == 0 || bytesWritten == 0)
               {
                  vuelca.writeLine ("");
                  vuelca.writeLine ("index: " + totpassed);
               }
               // finally write the bytes in target
               bytesWritten += towrite;

               for (int bb = skip; bb < towrite; bb ++)
                  vuelca.writeString ((bb > skip ? " ": "") + byte2hexstr (puffer[bb]));
               vuelca.writeLine ();
            }
         }
         if (towrite < 1) break;
         totpassed += nn;
      }
      while (ok && nn > 0 && ! srcFile.feof ());

      vuelca.writeLine ("final byte: " + totpassed);
      vuelca.fclose ();
      srcFile.fclose ();

      log.dbg (4, "binaryFileToHexText", "done");
      return ok;
   }

   public static boolean hexTextToBinaryFile (String fromFullPath, String toFullPath, boolean targetIsTemporary)
   {
      log.warn ("hexTextToBinaryFile", "NOT IMPLEMENTED YET!");
      return false;
   }


   // public static void main (String [] aa)
   // {
   //    //java -cp C:\Xcachito\celtabuild de.elxala.langutil.filedir.fileUtil
   //    if (aa.length > 0)
   //    {
   //       System.out.println (resolveRelativePointsInPath (aa[0]));
   //       return;
   //    }
   //    System.out.println (resolveRelativePointsInPath ("."));
   //    System.out.println (resolveRelativePointsInPath ("./"));
   //    System.out.println (resolveRelativePointsInPath ("./a"));
   //    System.out.println (resolveRelativePointsInPath ("./aa"));
   //    System.out.println (resolveRelativePointsInPath ("/."));
   //    System.out.println (resolveRelativePointsInPath ("/./a"));
   //    System.out.println (resolveRelativePointsInPath ("/./aa"));
   //    System.out.println (resolveRelativePointsInPath ("/a/."));
   //    System.out.println (resolveRelativePointsInPath ("/aa/."));
   //    System.out.println (resolveRelativePointsInPath ("/aa/./bb"));
   //
   //    System.out.println (resolveRelativePointsInPath (".."));
   //    System.out.println (resolveRelativePointsInPath ("../"));
   //    System.out.println (resolveRelativePointsInPath ("../a"));
   //    System.out.println (resolveRelativePointsInPath ("../aa"));
   //    System.out.println (resolveRelativePointsInPath ("/.."));
   //    System.out.println (resolveRelativePointsInPath ("/../a"));
   //    System.out.println (resolveRelativePointsInPath ("/../aa"));
   //    System.out.println (resolveRelativePointsInPath ("/a/.."));
   //    System.out.println (resolveRelativePointsInPath ("/aa/.."));
   //    System.out.println (resolveRelativePointsInPath ("/aa/../bb"));
   //    System.out.println (resolveRelativePointsInPath ("/aa/.bb/../..cc/dd"));
   //    System.out.println (resolveRelativePointsInPath ("/o/.si/./nooooo/no2no2/../.././vale/./ok/NO/.."));
   // }
}
