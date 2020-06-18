/*
library de.elxala
Copyright (C) 2005-2020 Alejandro Xalabarder Aulet

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

import java.util.Date;
import de.elxala.langutil.DateFormat;

import android.os.Environment;
import java.io.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

/**
*/
public class logDirDetectionAndTemp
{
   public static String PROP_SESSION_LOG_DIR = "gastona.sessionLog.dir";

   public static String sessionLogDirSlash = null;

   public static void onceAssignTempDir ()
   {
      boolean mounted = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
      //boolean removeable = Environment.isExternalStorageRemovable();
      boolean removeable = false;
      if (mounted && !removeable)
      {
         System.setProperty ("java.io.tmpdir", Environment.getExternalStorageDirectory() + "/tmp/gastonaTMP");
      }
      else
      {
         System.setProperty ("java.io.tmpdir", androidFileUtil.getAndroidCacheDir () + "/gastonaTMP");
      }
   }

   public static void detectLogDir ()
   {
      //(o) gastona_traces enable or not the several traces
      //
      String strSessDir = System.getProperty (PROP_SESSION_LOG_DIR, "");
      if (strSessDir == null || strSessDir.length () == 0)
      {
         // detection of directory "gastonaLog" or "sessionLog"
         //
         strSessDir = "gastonaLog";
         File checkFile = new File2 (strSessDir);
         if (!checkFile.exists () || !checkFile.isDirectory())
         {
            strSessDir = "sessionLog";
            checkFile = new File2 (strSessDir);
            if (!checkFile.exists () || !checkFile.isDirectory())
            {
               // no log at all !
               System.setProperty (PROP_SESSION_LOG_DIR, "");
               return;
            }
         }
      }

      setSessionLogDir (strSessDir);
   }

   protected static void setSessionLogDir (String dirName)
   {
      File sessDir = new File2 (dirName);
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
         System.setProperty (PROP_SESSION_LOG_DIR, dirName);
      }
      catch (Exception e)
      {
         System.err.println ("Gastona: fatal error while accesing session log directory!");
         e.printStackTrace ();
      }

      logServer.configure (sessionLogDirSlash, "gastona", gastonaVersion.getVersion () + " built on " + gastonaVersion.getBuildDate ());
   }
}
