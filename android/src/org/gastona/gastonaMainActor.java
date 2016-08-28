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
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.view.View;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

import org.gastona.cmds.*;

//
//    Two (wanted) ways of starting gastona
//
//    From launcher
//
//
//
//
//
//

public class gastonaMainActor extends Activity implements MensakaTarget
{
   private static logger log = new logger (null, "gastonaMainActor", null);

   private MessageHandle TX_FRAMES_ARE_MOUNTED = new MessageHandle ();
   private MessageHandle TX_SHOW_FRAMES        = new MessageHandle ();
   private MessageHandle TX_FRAMES_ARE_VISIBLE = new MessageHandle ();

   private static final int DOEXIT = 10;

   private static int MAINID_COUNT = 0;
   private int MAINID = MAINID_COUNT ++;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      final String DONDE = "mainActor@onCreate";

      androidSysUtil.setCurrentActivity (this);
      androidSysUtil.setWindowManager (getWindowManager ());
      androidSysUtil.setMainActivity (this);

      logDirDetectionAndTemp.detectLogDir ();

// NO FUNCIONA EN tablet ASUS !!! ???
//      {
//         String fileName = "/mnt/gastona/forceErrors/" + DateFormat.getStr (new java.util.Date (), "yyyyMMdd_HHmmss");
//         logServer.configureErrorLog (fileName);
//      }

      log.dbg (2, "info", "ANDROID_ID [" + android.provider.Settings.Secure.ANDROID_ID + "]");
      log.dbg (2, "info", "android app files dir [" + androidFileUtil.getAndroidFileDir () + "]");
      log.dbg (2, "info", "android app cache dir [" + androidFileUtil.getAndroidCacheDir () + "]");

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

      // check if having sdcard
      //
      if (! androidFileUtil.isExternalStorageMounted ())
      {
         CmdMsgBox.alerta (CmdMsgBox.WARNING_MESSAGE,
                            gastonaAppConfig.getAppName () + " will terminate",
                            "No external storage (" + androidFileUtil.getExternalStoragePath () + ") is mounted",
                            new String [] {"Accept"}, new String [] {"javaj doExit"});
         return;
      }

      logDirDetectionAndTemp.onceAssignTempDir ();

      Mensaka.subscribe (this, DOEXIT, "javaj doExit");
      Mensaka.subscribe (this, DOEXIT, "javaj doBack");

      // NOTE: this messages are not mandatory to be subscribed, the are provided for finer control of initialization
      Mensaka.declare (this, TX_FRAMES_ARE_MOUNTED, javaj.javajEBSbase.msgFRAMES_MOUNTED, logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_SHOW_FRAMES       , javaj.javajEBSbase.msgSHOW_FRAMES, logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_FRAMES_ARE_VISIBLE, javaj.javajEBSbase.msgFRAMES_VISIBLE, logServer.LOG_DEBUG_0);

      // Add stuff to allow lauching the mainActor with parameters (e.g. gast file)
      //
      String [] params = getIntent().getStringArrayExtra(CmdLaunchGastona.EXTRA_VALUE_NAME);

      boolean b_pa = params != null;
      boolean b_pa0 = b_pa && params.length > 0;

      log.dbg (2, DONDE, "params extra " + (b_pa ? (b_pa0 ? ("[0] = \"" + params[0] + "\""): "but size 0!"): " is null!") );

      boolean b_in = getIntent() != null;
      boolean b_da = b_in && getIntent().getData () != null;
      boolean b_pat = b_da && getIntent().getData ().getPath () != null;

      log.dbg (2, DONDE, "getIntent " +
                  (b_in ?
                        (b_da ?
                              ("data : " + getIntent().getData ().toString ())
                               : " but no data!"
                        )
                        : "is null!"
                  )
              );

      View la1 = null;
      if (b_pa0)
      {
         log.dbg (2, DONDE, "gast file [" + fileUtil.resolveCurrentDirFileName (params[0]) + "]");
         la1 = gastonaFlexActor.loadFrame (this, params);
         //log.dbg (2, "onCreate", "gast file [" + fileUtil.resolveCurrentDirFileName (name) + "]");
         //la1 = gastonaFlexActor.loadFrame (this, new String [] { name });
      }
      else if (b_pat)
      {
         String pathName  = getIntent().getData ().getPath ().toString ();
         log.dbg (2, DONDE, "gast file [" + fileUtil.resolveCurrentDirFileName (pathName) + "]");
         la1 = gastonaFlexActor.loadFrame (this, new String [] { pathName });
      }
      else
      {
         log.dbg (2, "onCreate", "starting gastona main script");
         la1 = gastonaAppConfig.loadMainAppScript (this);
      }
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
         log.dbg (2, "onCreate", "TEST:: Hemos evitado una desgracia!");
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
            log.dbg (2, "mainActor@takePacket", "javaj doExit received finishing current activity");
            androidSysUtil.getCurrentActivity().finish ();
            break;

         default:
            return false;
      }

      return true;
   }


   // private static String msg = "GASTONAL";

   protected void debugStamp (String posi)
   {
      log.dbg (2, "mainActor@" + posi, logServer.elapsedMillis () + " id = " + MAINID);
   }

   protected void onStart()
   {
      super.onStart ();

      androidSysUtil.setCurrentActivity (this);
      debugStamp ("onStart");
   }

   protected void onRestart()
   {
      super.onRestart ();
      debugStamp ("onRestart");
   }

   protected void onResume()
   {
      super.onResume ();
      debugStamp ("onResume");
   }

   protected void onPause()
   {
      super.onPause ();
      debugStamp ("onStart");
   }

   protected void onStop()
   {
      super.onStop ();
      debugStamp ("onStop");

      // it would exit/hide every time we press back  
      //finish ();
   }

   protected void onDestroy()
   {
      super.onDestroy ();

      //(o) ISSUE_Launching Gastona separately
      //
      //    all these destruction stuff can be removed when we get gastona
      //    being launched as separate instance as it is done when opening a gast from
      //    a file explorer.
      //    right now it does not work
      //        Intent, VIEW, file://myfilepath
      //    WHY ??

      //javaj36.finalizeJavaj ();
      Mensaka.sendPacket ("javaj exit", null);
      Mensaka.destroyCommunications ();
      utilSys.destroySac ();
      javaj.globalJavaj.destroyStatic ();
      androidFileUtil.destroy ();
      logServer.resetStatic ();
      listix.cmds.cmdMicoHTTPServer.shutDownAllMicoServers ();
      debugStamp ("onDestroy");
   }
}
