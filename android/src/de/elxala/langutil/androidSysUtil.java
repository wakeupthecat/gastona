/*
package de.elxala.langutil
(c) Copyright 2010 Alejandro Xalabarder Aulet

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

/**   ======== de.elxala.langutil.androidSysUtil ==========================================
   @author Alejandro Xalabarder 19.09.2010 13:45

*/

import android.util.*;
import android.view.WindowManager;
import android.app.Activity;
import android.content.res.Resources;
import android.content.Context;
import java.io.*;

/**
*/
public class androidSysUtil
{
   private static WindowManager androidWinMan = null;
   private static Activity androidMainActivity = null;

   private static Activity androidCurrentActivity = null;

   public static void setCurrentActivity(Activity esa)
   {
      androidCurrentActivity = esa;
   }

   public static Activity getCurrentActivity()
   {
      return androidCurrentActivity;
   }

   // 12.12.2010 17:46 Android question : what returns getApplicationContext ?
   //                  it does not seem to mean the context of the activity but the main context
   // public static Context getCurrentContext()
   // {
   //    if (androidCurrentActivity != null)
   //       return androidCurrentActivity.getApplicationContext();
   //    return null;
   // }

   public static void setMainActivity(Activity esa)
   {
      androidMainActivity = esa;
   }

   public static Activity getMainActivity()
   {
      return androidMainActivity;
   }

   // 12.12.2010 17:46 Android question : what returns getApplicationContext ?
   //                  it does not seem to mean the context of the activity but the main context
   public static Context getMainAppContext()
   {
      if (androidMainActivity != null)
         return androidMainActivity.getApplicationContext();
      return null;
   }


   public static void setWindowManager(WindowManager awm)
   {
      androidWinMan = awm;
   }

   public static WindowManager getWindowManager()
   {
      return androidWinMan;
   }


   /**
      e.g. "drawable/mi_desto.png" ??
      creo que 
         "drawable/mi_desto"
   */
   public static int getResourceId (String subpath)
   {
      return getMainActivity().getApplicationContext().getResources().getIdentifier (subpath, null, "org.gastona");
   }

   /**
      creo que "mi_desto", "drawable"
   */
   public static int getResourceId (String fileName, String parentPath)
   {
      return getMainActivity().getApplicationContext().getResources().getIdentifier (fileName, parentPath, "org.gastona");
   }
   
   public static InputStream openAssetFile (String fileName)
   {
      InputStream is = null;
      
      try {
         is = getMainActivity().getAssets().open (fileName);
      }      
      catch (Exception e) {}
      
      return is;
   }

   public static Resources getResources()
   {
      return getMainActivity().getApplicationContext().getResources();
   }

   private static boolean hasMetrics = false;
   private static int winDX = 100;
   private static int winDY = 100;
   private static int stdCharX = 9;
   private static int stdCharY = 36;

   public static int getWidthChars (float factor)
   {
      getMetric ();
      return (int) (factor * stdCharX);
   }

   public static int getHeightChars (float factor)
   {
      getMetric ();
      return (int) (factor * stdCharY);
   }

   public static int getWindowDX ()
   {
      getMetric ();
      return winDX;
   }

   public static int getWindowDY ()
   {
      getMetric ();
      return winDY;
   }

   private static void getMetric ()
   {
      if (hasMetrics) return;
      android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
      androidSysUtil.getWindowManager().getDefaultDisplay().getMetrics(metrics);
      winDX = metrics.widthPixels;
      winDY = metrics.heightPixels;
   }
}