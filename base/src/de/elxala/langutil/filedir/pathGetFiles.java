/*
package de.elxala.langutil
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

package de.elxala.langutil.filedir;

/**   ======== de.elxala.langutil.pathGetFiles ==========================================
   @author Alejandro Xalabarder 10.10.2004 23:25

*/

import java.io.File;
import java.util.*;

import de.elxala.langutil.*;
import de.elxala.zServices.logger;


// read all filenames that matches extensions etc.
//
public class pathGetFiles
{
   private static int NenN = 36;

   public static final int FOR_FILES = 0;
   public static final int FOR_DIRECTORIES = 1;

   public static logger logStatic = new logger (null, "de.elxala.langutil.filedir.pathGetFiles", null);
   public logger log = logStatic;

   // get root path
   //
   private String RootPathString = "";
   fileMultiFilter theFiltrus = new fileMultiFilter ();
   private List   lDirs = new Vector();
   private boolean isRecursive = true;
   private boolean scanForFiles = true;

   private File [] farray = null;    // array of entries in the current layer (tree depth)
//   private File farrayParent = null; // needed to detect symbolic links in linux

   private int  indxToca = 0;
   private boolean EndOfScan = false;

   public pathGetFiles ()
   {
   }

   public pathGetFiles (int whatToScan)
   {
      scanForFiles = whatToScan == FOR_FILES;
   }

   public void initScan (String basePath, String [] extensionsToScan)
   {
      initScan (basePath, extensionsToScan, true);
   }

   public void initScan (String basePath, String [] extensionsToScan, boolean recursive)
   {
      log.dbg(2, "initScan1", "basePath \"" + basePath + "\" " + (extensionsToScan != null ? extensionsToScan.length: 0) + " extensions");
      fileMultiFilter fil = new fileMultiFilter ();
      for (int ii = 0; ii < extensionsToScan.length; ii ++)
      {
         log.dbg(2, "initScan1", "extension " + ii + " \"" + extensionsToScan[ii] + "\"");
         fil.addCriteria ("+", extensionsToScan[ii]);
      }

      initScan (basePath, "", recursive, fil);
   }

   /**
      rootPath: root path to be scanned, this path will not appear in the column parentPath
      subPath:  optional subpath, if given only the subpath relative to rootPath will be scanned (the name of the subpath will appear in column parentPath)
      recursive: if the scan has to be recursive
      filter : filter applied to files and directories

   */
   public void initScan (String rootPath, String subPath, boolean recursive, fileMultiFilter filter)
   {
      log.dbg(2, "initScan", "rootPath \"" + rootPath + "\" subpath \"" + subPath + "\" recursive " + recursive);
      // initialize state
      //
      RootPathString = rootPath;
      isRecursive = recursive;
      theFiltrus = filter;

      lDirs = new Vector();
      farray = new File [0];

      indxToca = 0;
      EndOfScan = false;

      // get root path to be removed on each file if removeRootPath is true
      //
      if (rootPath.length () > 0)
      {
         File rootFile = new File (rootPath);
         try { RootPathString = rootFile.getCanonicalPath (); } catch (Exception e)
         {
            log.err("initScan", "exception getting canonical path for rootPath \"" + rootPath + "\"\n" + e);
         };
         subPath = "/" + subPath;
      }

      // first directory
      //
      log.dbg(2, "initScan", "adding first directory \"" + RootPathString + subPath + "\"");
      lDirs.add (RootPathString + subPath);
   }

   private boolean areThereListOfFiles ()
   {
//      if (farray == null)
//      {
//         log.dbg(4, "areThereListOfFiles", "end of scan, farray == null");
//         EndOfScan = true;
//         return false;
//      }

      if (farray != null && indxToca < farray.length) return true;
      if (lDirs == null || lDirs.size () == 0)
      {
         log.dbg(4, "areThereListOfFiles", "end of scan, lDirs == null or size 0");
         EndOfScan = true;
         return false;
      }
      indxToca = 0;
      farray = new File [0];

      File currDir = new File ((String) lDirs.get (0)); // get directory to process
      log.dbg(2, "areThereListOfFiles", "check directory \"" + lDirs.get (0) + "\"");

      if (! currDir.exists ())
      {
         log.warn("areThereListOfFiles", "the directory \"" + lDirs.get (0) + "\" does not exist!");
         lDirs.remove (0); // remove processed directory
         return false;  // strange ...
      }

      if (! currDir.isDirectory ())
      {
         log.warn("areThereListOfFiles", "the path \"" + lDirs.get (0) + "\" is not a directory!");
         lDirs.remove (0); // remove processed directory
         return false;  // strange ...
      }

      farray = currDir.listFiles ();
//      farrayParent = currDir;
      lDirs.remove (0); // remove processed directory
      if (farray == null)
      {
         // Special folders like "System ...Information"
         // System.err.println ("Como curiosidad te dire que " + currDir.getName () + " no tiene nada de nada");
         log.dbg (2, "areThereListOfFiles", "No contents found in directory! (system directory?)");
         return false;
      }

      log.dbg (2, "areThereListOfFiles", farray.length + " items found");
      return true;
   }


   //    example : List comps = pathGetFiles.scan ("../example", { "h", "hpp"} );
   //
   //int scan (String basePath, String [] extensionsToScan, List internDirList, List filesList)
   public List scanN (int N)
   {
      List lComponents = new Vector ();

      log.dbg (2, "scanN", "N = " + N);
      while (! EndOfScan )
      {
         // reached N !
         if (lComponents.size () >= N) break;

         // ensure list of files !
         if (! areThereListOfFiles ()) continue;

         // no new files ? then end of process
//         if (indxToca >= farray.length) break;

         while (indxToca < farray.length && lComponents.size () < N)
         {
            File aFile = farray[indxToca ++];
            log.dbg (2, "scanN", "element " + indxToca + " [" + aFile + "]");

            //Detect directories that are symbolic links in linux to avoid redundant entries (and scanning)
            //These will be treated as simple files
            boolean isSymbolicLink = false;
            if (stdlib.isOSLinux ())
            {
               File realFile = aFile;
               //canonicalFile has to solve the link if any
               try { realFile = aFile.getCanonicalFile (); } catch (Exception e) {}

               isSymbolicLink = realFile.compareTo (aFile) != 0; // note: (compareTo == 0) means that both are equal!
               if (isSymbolicLink)
               {
                  log.dbg (2, "scanN", "it is a unix symbolic link! file [" + aFile.getPath() + "] realFile [" + realFile.getPath () + "]");
                  //log.err ("scanN", "it is a unix symbolic link! file [" + aFile.getPath() + "] realFile [" + realFile.getPath () + "]");
               }
            }

            //if (aFile.isDirectory () && !isSymbolicLink)
            //Allow symbolic link directories (01.01.2011 18:08)
            if (aFile.isDirectory ())
            {
               if (isRecursive && theFiltrus.accept (true, aFile.getAbsolutePath (), ""))
               {
                  log.dbg (2, "scanN", "is directory add it to the dir list");
                  lDirs.add (aFile.getAbsolutePath ()); // add to directories to process
               }
               else log.dbg (2, "scanN", "is directory discard it");

               if (scanForFiles)
                  continue;
            }

            // scanning for directories and it is a file => not interested
            if (!scanForFiles && aFile.isFile ()) continue;

            if (! aFile.isFile () && !isSymbolicLink && scanForFiles)
            {
               log.err ("scanN", "NOT A DIR, NOT A FILE! WHAT IS THIS !" + aFile);
               // Do not ignore it!, print out an error message but
               // save it as a normal file
            }

            if (!theFiltrus.accept (aFile)) continue;  // >>>>

            // match one extension => add to files
            String name = aFile.getPath ();

            String extension = fileUtil.getExtension (name);

            String nameSolo = aFile.getName ();
            String dirSolo  = "";

            int iniDir = 0;
            int finDir = name.length () - nameSolo.length ();

            if (RootPathString.length() > 0)
            {
               iniDir = RootPathString.length() + (fileUtil.endsWithSeparator(RootPathString) ? 0: 1);
            }

            // add one for the separator ?

            if (iniDir < 0 || finDir-iniDir < 0 || finDir > name.length() || iniDir >= name.length())
            {
               log.err ("scanN", "strange path [" + name + "] cut (" + iniDir + ", " + finDir + ") not possible!");
            }
            else
            {
               dirSolo  = name.substring (iniDir, finDir);
            }

            if (fileUtil.endsWithSeparator (dirSolo))
               dirSolo = dirSolo.substring(0, dirSolo.length()-1);

            String dateStr  = DateFormat.getStr (new Date(aFile.lastModified ()));

            log.dbg (2, "scanN", "add file dir[" + dirSolo + "] name[" + nameSolo + "] extension[" +  extension + "] date[" + dateStr + "] size[" + aFile.length () + "]");
            lComponents.add (buildRecord (dirSolo, nameSolo, extension, dateStr, "" + aFile.length ()));
         }
      }
      log.dbg (2, "scanN", "return " + lComponents.size () + " components");
      return lComponents;
   }


   private String [] buildRecord (String dirSolo, String nameSolo, String extension, String dateStr, String size)
   {
      String fullSubPath    = fileUtil.concatPaths (dirSolo, nameSolo);
      String fullPath       = fileUtil.concatPaths (RootPathString, fullSubPath);
      String fullParentPath = fileUtil.concatPaths (RootPathString, dirSolo);

      return new String [] {
            dirSolo, nameSolo, extension, dateStr, size,
            fileUtil.resolveRelativePointsInPath (fullPath),
            fileUtil.resolveRelativePointsInPath (fullParentPath),
            fileUtil.resolveRelativePointsInPath (fullSubPath)
             };
   }

   public static String [] getRecordColumns ()
   {
      return new String [] { "parentPath", "fileName", "extension", "date", "size", "fullPath", "fullParentPath", "fullSubPath" };
   }


   public static void main (String [] aa)
   {
      if (aa.length < 1)
      {
         System.out.println ("Syntax: pathGetFiles rootDir subDir [opFilter str] ...");
         System.out.println ("");
         System.out.println ("where opFilter can be:");
         System.out.println ("");
         System.out.println ("   +   include the extension 'str'");
         System.out.println ("   -   exclude the extension 'str'");
         System.out.println ("   +D  include the directory that contains 'str'");
         System.out.println ("   -D  exclude the directory that contains 'str'");
         System.out.println ("   +F  include the file that contains 'str'");
         System.out.println ("   -F  exclude the file that contains 'str'");
         return;
      }

      //for (int cc = 0; cc < aa.length; cc ++)
      //{
      //   System.out.println ("aa[" + cc + "] [" + aa[cc] + "]");
      //}

      String rootPath = aa[0];
      String subpath  = aa.length > 1 ? aa[1]: ".";

      fileMultiFilter macro = new fileMultiFilter ();

      // add all criteria
      for (int cc = 2; cc < aa.length; cc += 2)
      {
         if (cc+1 >= aa.length)
         {
            System.err.println ("filter not completed analyzing \"" + aa[cc] + "\"");
            return;
         }
         macro.addCriteria (aa[cc], aa[cc + 1]);
      }

      pathGetFiles moto = new pathGetFiles();

      Crono rolex = new Crono();
      rolex.start();

      moto.initScan (rootPath, subpath, true, macro);

      int kantos = 0;
      List cosas = null;
      do
      {
         cosas = moto.scanN (NenN);
         kantos += cosas.size ();
         for (int jj = 0; jj < cosas.size (); jj++)
         {
            //String elo = (String) cosas.get (jj);
            System.out.println (((String []) cosas.get (jj))[0] + "\\" + ((String []) cosas.get (jj))[1]);
         }
      }
      while (cosas.size () > 0);

      rolex.stop ();

      System.out.println ();
      //System.out.println (kantos + " files");
      System.out.println (kantos + " files scanned in " + rolex.elapsedMillis ()/1000 + " s");
   }
}
