/*
java packages for gastona
Copyright (C) 2005-2020  Alejandro Xalabarder Aulet

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


   /**
   */
   public static String getExtension (String path)
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

      if (prefix.length() == 0)
      {
         // avoid java.lang.IllegalArgumentException: Prefix string too short
         prefix = "tmp";
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

   public static boolean copyFile (String fromFullPath, String toFullPath)
   {
      return copyFile (fromFullPath, toFullPath, false);
   }

   // copy source file into target path
   //    the source file will be search in
   //      - the file system
   //      - as java resource (e.g. typically within the jar)
   //      - as url
   // in this order
   //
   public static boolean copyFile (String fromFullPath, String toFullPath, boolean targetIsTemporary)
   {
      log.dbg (4, "copyFile", "from \"" + fromFullPath + "\" to \"" + toFullPath + "\"");

      // TODO: check if fromFullPath and toFullPath refer to the same actual file and return false if so

      // open for read
      //
      TextFile srcFile = new TextFile ();
      if (! srcFile.fopen (fromFullPath, "rb"))
      {
         log.err ("copyFile", "Source file \"" + fromFullPath + "\" not found!");
         return false;
      }

      // ensure target directories
      //
      fileUtil.mkdirsForFile ("", toFullPath, targetIsTemporary);

      // open for write
      //
      TextFile vuelca = new TextFile ();
      if (! vuelca.fopen (toFullPath, "wb"))
      {
         log.err ("copyFile", "target file \"" + toFullPath + "\" could not be opened!");
         return false;
      }

      // loop to copy the file
      //
      int nn = 0;
      boolean ok = true; // a file of length 0 could also be copied
      
      byte [] puffer = new byte [1024];
      do
      {
         nn = srcFile.readBytes (puffer);
         if (nn > 0)
            ok = vuelca.writeBytes (puffer, nn);
      }
      while (ok && nn > 0 && ! srcFile.feof ());

      vuelca.fclose ();
      srcFile.fclose ();

      log.dbg (4, "copyFile", "done");
      return ok;
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
