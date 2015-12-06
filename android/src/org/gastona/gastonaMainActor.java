/*
gastona for Android
Copyright (C) 2011 Alejandro Xalabarder Aulet

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

import android.util.Log;

import java.io.File;
import android.os.Environment;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.view.View;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

import org.gastona.cmds.CmdMsgBox;


public class gastonaMainActor extends Activity implements MensakaTarget
{
   private static logger log = new logger (null, gastonaAppConfig.getAppMainActorClassName (), null);

   private MessageHandle TX_FRAMES_ARE_MOUNTED = new MessageHandle ();
   private MessageHandle TX_SHOW_FRAMES        = new MessageHandle ();
   private MessageHandle TX_FRAMES_ARE_VISIBLE = new MessageHandle ();

   private static final int DOEXIT = 10;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      androidSysUtil.setCurrentActivity (this);

      logDirDetectionAndTemp.detectLogDir ();
      logDirDetectionAndTemp.detectErrorLogDir ();

      androidFileUtil.setAndroidFileDir (getApplicationContext().getFilesDir ().getAbsolutePath());
      androidFileUtil.setAndroidCacheDir (getApplicationContext().getCacheDir ().getAbsolutePath());

// NO FUNCIONA EN tablet ASUS !!! ???
//      {
//         String fileName = "/mnt/gastona/forceErrors/" + DateFormat.getStr (new java.util.Date (), "yyyyMMdd_HHmmss");
//         logServer.configureErrorLog (fileName);
//      }


      log.dbg (2, "info", "ANDROID_ID [" + android.provider.Settings.Secure.ANDROID_ID + "]");
      log.dbg (2, "info", "app files dir [" + androidFileUtil.getAndroidFileDir () + "]");
      log.dbg (2, "info", "app cache dir [" + androidFileUtil.getAndroidCacheDir () + "]");

      log.dbg (2, "info", "Build.BOARD   [" + Build.BOARD   + "]");
      log.dbg (2, "info", "Build.DEVICE  [" + Build.DEVICE  + "]");
      log.dbg (2, "info", "Build.PRODUCT [" + Build.PRODUCT + "]");

      if (Build.PRODUCT.equals ("sdk"))
      {
         log.dbg (2, "Device simulator!");
      }
      else
      {
         log.dbg (2, "Not sdk device");
      }

      String mntSdcard = Environment.getExternalStorageDirectory().getPath();

      androidSysUtil.setWindowManager (getWindowManager ());
      androidSysUtil.setMainActivity (this);

      logDirDetectionAndTemp.onceAssignTempDir ();

      Mensaka.subscribe (this, DOEXIT, "javaj doExit");
      Mensaka.subscribe (this, DOEXIT, "javaj doBack");

      // NOTE: this messages are not mandatory to be subscribed, the are provided for finer control of initialization
      Mensaka.declare (this, TX_FRAMES_ARE_MOUNTED, javaj.javajEBSbase.msgFRAMES_MOUNTED, logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_SHOW_FRAMES       , javaj.javajEBSbase.msgSHOW_FRAMES, logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_FRAMES_ARE_VISIBLE, javaj.javajEBSbase.msgFRAMES_VISIBLE, logServer.LOG_DEBUG_0);

      // check if having sdcard
      //
      boolean mounted = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
      if (! mounted)
      {
         CmdMsgBox.alerta (CmdMsgBox.WARNING_MESSAGE,
                            gastonaAppConfig.getAppName () + " will terminate",
                            "No external storage (" + mntSdcard + ") is mounted",
                            new String [] {"Accept"}, new String [] {"javaj doExit"});
         return;
      }

      View la1 = gastonaAppConfig.loadMainAppScript (this, mntSdcard);
      if (la1 == null)
      {
         CmdMsgBox.alerta (CmdMsgBox.WARNING_MESSAGE,
                            gastonaAppConfig.getAppName () + " will terminate",
                            "Could not find main script!",
                            new String [] {"Accept"}, new String [] {"javaj doExit"});
         return;
      }

      // evitar exception ?!
      android.view.ViewGroup vigu = (android.view.ViewGroup) la1.getParent();
      if (vigu != null) 
      {
         System.out.println ("TEST:: Hemos evitado una desgracia!");
         vigu.removeView(la1);
      }

      setContentView(la1);

      // message to permit controllers arrange the widgets
      Mensaka.sendPacket (TX_FRAMES_ARE_MOUNTED, null);

      // message to make frames visible
      Mensaka.sendPacket (TX_SHOW_FRAMES, null);

      // message to let it known that frames are visible
      Mensaka.sendPacket (TX_FRAMES_ARE_VISIBLE, null);
   }


//   @Override
//   protected Dialog onCreateDialog(int id)
//   {
//   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case DOEXIT:
            log.dbg (2, "takePacket", "javaj doExit received");
            androidSysUtil.getCurrentActivity().finish ();
            break;

         default:
            return false;
      }

      return true;
   }
   
   
   private static String msg = "GASTONAL";

   protected void onStart()
   {
      super.onStart ();

      androidSysUtil.setCurrentActivity (this);
      log.dbg (0, "gastona onStart!");
      Log.d(msg, "gastona onStart");
   }

   protected void onRestart()
   {
      super.onRestart ();
      log.dbg (0, "gastona onRestart!");
      Log.d(msg, "GASTONA onRestart");
   }

   protected void onResume()
   {
      super.onResume ();
      log.dbg (0, "gastona onResume!");
      Log.d(msg, "GASTONA onResume");
   }

   protected void onPause()
   {
      super.onPause ();
      log.dbg (0, "gastona onPause!");
      Log.d(msg, "GASTONA onPause");
   }

   protected void onStop()
   {
      super.onStop ();
      // finish ();
      log.dbg (0, "gastona onStop!");
      Log.d(msg, "GASTONA onStop");
   }

   protected void onDestroy()
   {
      super.onDestroy ();
      log.dbg (0, "gastona onDestroy!");
      Log.d(msg, "GASTONA onDestroy");

      //javaj36.finalizeJavaj ();
      Mensaka.sendPacket ("javaj exit", null);
      log.dbg (0, "gastona", "This is the end my friend.");
      Mensaka.destroyCommunications ();
      utilSys.destroySac ();
      javaj.globalJavaj.destroyStatic ();
      androidFileUtil.destroy ();
      logServer.resetStatic ();
      listix.cmds.cmdMicoHTTPServer.shutDownAllMicoServers ();
   }
}
