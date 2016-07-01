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
import android.os.Environment;

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

   public static boolean isExternalStorageMounted ()
   {
      return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
   }

   public static String getExternalStoragePath ()
   {
      return Environment.getExternalStorageDirectory().getPath();
   }

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


   // Directory for files only visible for the App (e.g. gastona). It will be destroyed with
   // when uninstalling the App.
   //
   public static String getAndroidFileDir ()
   {
      // Note: this is probably the internal storage directory (?)
      //       in android there is also such a directory from external storage
      //       given by getExternalFilesDir and getExternalFilesDirs
      return androidSysUtil.getMainAppContext().getFilesDir ().getAbsolutePath();
   }

   // Directory for cache files only visible for the App (e.g. gastona). It will be destroyed with
   // when uninstalling the App
   //
   public static String getAndroidCacheDir ()
   {
      // Note: see note in getAndroidFileDir
      return androidSysUtil.getMainAppContext().getCacheDir ().getAbsolutePath();
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
