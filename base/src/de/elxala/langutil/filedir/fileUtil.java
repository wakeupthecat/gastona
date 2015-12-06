/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005  Alejandro Xalabarder Aulet

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

import java.io.*;
import de.elxala.zServices.*;
import de.elxala.langutil.*;

/**
   class TextFile
   @author Alejandro Xalabarder Aulet
   @date   2006

   Utilities with files and paths
*/
public class fileUtil
{
   private static logger log = new logger (null, "de.elxala.langutil.filedir.fileUtil", null);
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
            path = cutFromTo (path, pos0+2, ava+3);
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

   public static String createTemporal ()
   {
      return createTemporal ("tmp", "tmp");
   }

   public static String createTemporal (String prefix)
   {
      return createTemporal (prefix, "tmp");
   }

   private static String tempDirBase = null;
   public static String getTemporalDirBase ()
   {
      // temporal directories with accents (e.g. spanish ...Configuración Local...)
      // DOES NOT work with sqlite !!!
      // Therefore, temp dir criteria:
      //
      //    1) if tmp exists then IT IS THE BASE TEMP DIR
      //    2) if \tmp exists then IT IS THE BASE TEMP DIR !!!
      //    3) take it from java.io.tmpdir property
      //
      //    after that ALWAYS getCanonical Path and set java.io.tmpdir to the
      //    new path
      //

      if (tempDirBase != null) return tempDirBase;
      File fi = new File ("tmp");
      if (!fi.exists () || !fi.isDirectory ())
         fi = new File ("/tmp");
      if (!fi.exists () || !fi.isDirectory ())
         fi = new File (System.getProperty("java.io.tmpdir", "."));

      try { tempDirBase = fi.getCanonicalPath (); } catch (Exception e) {}

      boolean avis = false;
      for (int ii = 0; ii < tempDirBase.length (); ii++)
         if (tempDirBase.charAt (ii) > '~') avis=true;

      if (avis)
      {
         String mess = "Temporary directory path contain strange characters\n [" + tempDirBase + "]\n applications like sqlite may not work properly.\n Creating a root or sub-directory named tmp might avoid such problems.";
         log.err ("getTemporalDirBase", mess);

         javax.swing.JOptionPane.showMessageDialog (
               null,
               mess,
               "Error",
               javax.swing.JOptionPane.ERROR_MESSAGE);
      }

      return tempDirBase;
   }

   public static String createTemporal (String prefix, String sufix)
   {
      String tempdir = System.getProperty("java.io.tmpdir", ".");
//      System.out.println ("LA COSA VA BIEN []");
      return createTemporal (prefix, sufix, tempdir, true);
   }

   public static String createTemporal (String prefix, String sufix, String tempdir)
   {
      return createTemporal (prefix, sufix, tempdir, true);
   }

   public static String createTemporal (String prefix, String sufix, String tempdir, boolean DeleteItOnExit)
   {
      File uniqFileTmp;

      if (prefix.length() == 0)
      {
         // avoid java.lang.IllegalArgumentException: Prefix string too short
         prefix = "TMP";
      }

      try
      {
         // Asegurar directorio temporar (debido a "gastonaTemp")
         // Seguramente es necesario hacelo asi, aunque podría ser sufuciente
         // hacerlo solo la primera vez que se cree un fichero temporar "delete on exit"
         File tmpDir = new File (tempdir);
         tmpDir.mkdirs ();

         uniqFileTmp = File.createTempFile(prefix, sufix, new File (tempdir));
         if (DeleteItOnExit)
         {
            uniqFileTmp.deleteOnExit ();
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

   public static String createTempDir(String prefix, String dirBase, boolean DeleteItOnExit)
   {
      if (dirBase == null)
         dirBase = System.getProperty("java.io.tmpdir", ".");

      //(o) ensure_mkdirs!
      String tempDir = createTemporal(prefix, "tmpDir", dirBase, false);  // NO SE PUEDE BORRAR AL SALIR!

      File tDir = new File (tempDir);
      tDir.delete();
      if (!tDir.mkdir())
      {
         log.fatal ("createTempDir", "cannot make dir of path [" + tempDir + "]");
      }
      if (DeleteItOnExit)
         tDir.deleteOnExit ();

      return tempDir;
   }

   public static boolean ensureDirsForFile (String fullFilePath)
   {
      // ensure directory
      File dirFile = new File (fullFilePath);
      // System.out.println ("1 dirfile = " + dirFile);
      dirFile = dirFile.getParentFile();
      // System.out.println ("2 dirfile = " + dirFile);
      if (dirFile != null && ! dirFile.exists ())
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
            File fi = new File (concatPaths (dirBase, subPath));
            if (!lastOneIsFile || ii < dirList.size ()-1)
            {
//System.out.println ("mkdirs mkdir");
               fi.mkdir ();
               subPath += "/";
            }

            if (deleteOnExit)
               fi.deleteOnExit ();
         }
      }
   }

   public static File getRootDirectoryOf (String referenceFile, String rootMark)
   {
      File reference = new File (referenceFile);
      if (! reference.exists ()) return null;

      String cannon = ".";
      try { cannon = reference.getCanonicalPath(); } catch (Exception e) {}
      return getRootDirectoryOf (new File (cannon), rootMark);
   }

   private static File getRootDirectoryOf (File referenceFile, String rootMark)
   {
      File tryfile = new File (referenceFile + "/" + rootMark);
      if (tryfile.exists ())
         return tryfile.getParentFile ();

      File parent = referenceFile.getParentFile();
      if (parent == null)
         return null;

      return getRootDirectoryOf (parent, rootMark);
   }


   public static void main (String [] aa)
   {
      //java -cp C:\Xcachito\celtabuild de.elxala.langutil.filedir.fileUtil
      if (aa.length > 0)
      {
         System.out.println (resolveRelativePointsInPath (aa[0]));
         return;
      }
      System.out.println (resolveRelativePointsInPath ("."));
      System.out.println (resolveRelativePointsInPath ("./"));
      System.out.println (resolveRelativePointsInPath ("./a"));
      System.out.println (resolveRelativePointsInPath ("./aa"));
      System.out.println (resolveRelativePointsInPath ("/."));
      System.out.println (resolveRelativePointsInPath ("/./a"));
      System.out.println (resolveRelativePointsInPath ("/./aa"));
      System.out.println (resolveRelativePointsInPath ("/a/."));
      System.out.println (resolveRelativePointsInPath ("/aa/."));
      System.out.println (resolveRelativePointsInPath ("/aa/./bb"));

      System.out.println (resolveRelativePointsInPath (".."));
      System.out.println (resolveRelativePointsInPath ("../"));
      System.out.println (resolveRelativePointsInPath ("../a"));
      System.out.println (resolveRelativePointsInPath ("../aa"));
      System.out.println (resolveRelativePointsInPath ("/.."));
      System.out.println (resolveRelativePointsInPath ("/../a"));
      System.out.println (resolveRelativePointsInPath ("/../aa"));
      System.out.println (resolveRelativePointsInPath ("/a/.."));
      System.out.println (resolveRelativePointsInPath ("/aa/.."));
      System.out.println (resolveRelativePointsInPath ("/aa/../bb"));
      System.out.println (resolveRelativePointsInPath ("/aa/.bb/../..cc/dd"));
      System.out.println (resolveRelativePointsInPath ("/o/.si/./nooooo/no2no2/../.././vale/./ok/NO/.."));
   }
}
