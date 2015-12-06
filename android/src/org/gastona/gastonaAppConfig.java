/*
gastona for Android
Copyright (C) 2013 Wakeupthecat UG, Alejandro Xalabarder Aulet

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

import android.content.Context;
import android.view.View;

import java.io.File;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import org.gastona.cmds.*;

public class gastonaAppConfig
{
   public static final String getAppName ()
   {
      return "gastona";
   }

   public static final String getAppPackageString ()
   {
      return "org.gastona";
   }

   public static final String getAppFlexActorClassName ()
   {
      return "gastonaFlexActor";
   }

   public static final String getAppMainActorClassName ()
   {
      return "gastonaMainActor";
   }
   
   public static EvaUnit [] getAppHardcodedGastonaTrio (String gastFileName)
   {
      EvaUnit [] euRet = null;
      //if (gastFileName.equalsIgnoreCase("scriptHardCoded1.gast"))
      //{
      //   euRet = new EvaUnit [3];
      //   euRet[0] = scriptHardCoded1.getlistix ();
      //   euRet[1] = scriptHardCoded1.getjavaj ();
      //   euRet[2] = scriptHardCoded1.getdata ();
      //}

      return euRet;
   }
   
   public static View loadMainAppScript (Context who, String mntSdcard)
   {
      // determine application directories acording with placement of autoStart.gast
      File fstart = new File (mntSdcard + "/gastona/autoStart.gast");
      if (fstart.exists ())
      {
         androidFileUtil.setApplicationDir (fstart.getParent ());
         androidFileUtil.setApplicationCacheDir (fstart.getParent () + "/cache");
      }
      else
      {
         fstart = new File (mntSdcard + "/Android/data/org.gastona/files/autoStart.gast");
         androidFileUtil.setApplicationDir      (mntSdcard + "/Android/data/org.gastona/files");
         androidFileUtil.setApplicationCacheDir (mntSdcard + "/Android/data/org.gastona/cache");
      }
      
      // look for the start, if not unzip the demo
      //
      if (!fstart.exists ())
      {
         CmdMsgBox.alerta (CmdMsgBox.TOAST_MESSAGE, "", "Installing demo ...");
         javaLoad.unZipResourceZip ("initial_demo", fileUtil.getApplicationDir ());
      }
      if (!fstart.exists ())
      {
         CmdMsgBox.alerta (CmdMsgBox.WARNING_MESSAGE,
                            "Gastona cannot start",
                            "could not extract demo into " + fileUtil.getApplicationDir (),
                            new String [] {"Accept"}, new String [] {"javaj doExit"});
         return null;
      }

      //log.dbg (2, "info", "starting script [" + fstart.getAbsolutePath () + "]");
      
      return gastonaFlexActor.loadFrame (who, fstart.getAbsolutePath ());
   }   
}

