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
import java.util.List;
import java.util.Vector;
import de.elxala.zServices.*;
import de.elxala.langutil.*;

/**
   class TextFile
   @author Alejandro Xalabarder Aulet
   @date   2015

   Utilities with files and paths
*/
public class androidFileUtil
{
   private static List listFileDelOnExit = new Vector ();
   private static boolean temporalChecked = false;

   protected static String statAndroidPersistDir = null;
   protected static String statAndroidCacheDir = null;


   public static void deleteTmpFileOnExit (File fi)
   {
      fi.deleteOnExit ();
   }

   private static boolean checkString (String str, String info)
   {
      if (str != null) return true;
      //android.util.Log.e ("gastona fileUtil", "String " + info + " not assigned at the begining of main gastona!");
      fileUtil.log.severe ("checkString", "Constant string for " + info + " is not assigned on starting main gastona!");
      return false;
   }

   public static void setAndroidFileDir (String persistDir)
   {
      statAndroidPersistDir = persistDir;
   }

   public static void setAndroidCacheDir (String cacheDir)
   {
      statAndroidCacheDir = cacheDir;
   }

   public static String getAndroidFileDir ()
   {
      if (!checkString (statAndroidPersistDir, "Android file dir")) return "";
      return statAndroidPersistDir;
   }

   public static String getAndroidCacheDir ()
   {
      if (!checkString (statAndroidCacheDir, "Android cache dir")) return "";
      return statAndroidCacheDir;
   }


   public static String statPersistDir = null;
   public static String statCacheDir = null;
   public static long nTemporal = 0;

   static public void setApplicationDir (String persistDir)
   {
      statPersistDir = persistDir;
   }

   static public void setApplicationCacheDir (String cacheDir)
   {
      statCacheDir = cacheDir;
   }

   static public String getApplicationDir ()
   {
      if (!checkString (statPersistDir, "Application dir")) return "";
      return statPersistDir;
   }

   static public String getApplicationCacheDir ()
   {
      if (!checkString (statCacheDir, "Cache dir")) return "";
      return statCacheDir;
   }

   public static void destroy ()
   {
      fileUtil.log.dbg (2, "destroy", "deleting " + listFileDelOnExit.size () + " entries");
      for (int ii =0; ii < listFileDelOnExit.size (); ii ++)
      {
         File fi = (File) listFileDelOnExit.get (ii);
         fileUtil.log.dbg (4, "destroy", "deleting " + fi.getName ());
         if (!fi.exists ())
         {
            fileUtil.log.dbg (4, "destroy", "it does not exist!");
            continue;
         }

         if (fi.isDirectory ())
         {
            File [] arrfi = fi.listFiles ();
            fileUtil.log.dbg (4, "destroy", "deleting directory " + fi.getName () + " with " + arrfi.length + " entries");

            for (int ss =0; ss < arrfi.length; ss ++)
               if (arrfi[ss].exists ())
                  arrfi[ss].delete ();
         }
         fi.delete ();
      }
      listFileDelOnExit = new Vector ();
      temporalChecked = false;
   }
}
