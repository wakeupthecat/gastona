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

import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import de.elxala.mensaka.*;
import javaj.*;
import javaj.widgets.*;
import listix.*;
import de.elxala.Eva.*;
import de.elxala.Eva.layout.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

import org.gastona.cmds.*;
import org.gastona.*;

/**
*/

public class gastonaFlexActor extends Activity
{
   private static logger log = new logger (null, "gastonaFlexActor", null);
   public static final String NAME4UIFRAME = "javaj.frame";
   public static final String NAME4UIFRAMETITLE = "javaj.frameTitle";
   public static final String NAME4UIGASTONA = "gastona.object";

   private static View mNoView = null; // in case the layout cannot be loaded
   private View mBasuraView = null;    // to allow removing the attached view!!

   private static int FLEXID_COUNT = 0;

   private int FLEXID = FLEXID_COUNT ++;
   private String FLEXIDName = "?";

   private static View getNoView (Context co)
   {
      if (mNoView == null)
         mNoView = new zButton (co, "bEnd", "End");

      return mNoView;
   }

   public static View loadFrame (Context co, String fileName)
   {
      return loadFrame (co, new String [] { fileName });
   }

   protected static void unsubscribeAllViewsIn (ViewGroup root)
   {
      if (root == null) return;
      for (int ii = 0; ii < root.getChildCount(); ii ++)
      {
         View lav = root.getChildAt (ii);

         if (lav instanceof MensakaTarget)
            Mensaka.unsubscribe ((MensakaTarget) lav);

         if (lav instanceof ViewGroup)
            unsubscribeAllViewsIn((ViewGroup) lav);
      }
   }

   public static View loadFrame (Context co, String [] allParams)
   {
      final String DONDE = "flexActor@loadFrame";
      String fileName = org.gastona.commonGastona.getGastFileNameAndProcessArgs (allParams);

      if (fileName == null || fileName.length () == 0)
      {
         log.err (DONDE, "invalid fileName!");
         return getNoView (co);
      }

      View view2return = null;

      // POLICY : pass the dutchie View !
      //
      log.dbg (0, DONDE, "GASTRECYCLER checking in map of " +  utilSys.getSacSize () + " a recycling of : " + fileName);
      view2return = (View) utilSys.objectSacGet (NAME4UIFRAME + "." + fileName);
      if (view2return != null)
      {
         android.view.ViewGroup vigru = (android.view.ViewGroup) view2return.getParent ();
         if (vigru != null)
         {
            log.dbg (0, DONDE, "GASTRECYCLER recycle view form : " + fileName);
            vigru.removeView (view2return);
         }
         else
         {
            log.dbg (2, DONDE, "cannot find parent group recycling view form : " + fileName);
         }
         unsubscribeAllViewsIn (vigru);
         view2return = null;

         gastona gastPtr = (gastona) utilSys.objectSacGet (NAME4UIGASTONA + "." + fileName);
         Mensaka.unsubscribe (gastPtr.myMensaka4listix);
         gastPtr.myMensaka4listix = null;
         gastPtr = null;
         utilSys.objectSacPut (NAME4UIGASTONA + "." + fileName, null);

         // como si no hubiera pasado nada ...
      }

//.//
//.//         //--!-- if (javajST.existFrame (layoutName))
//.//         {
//.//            view2return = (View) utilSys.objectSacGet (NAME4UIFRAME + "." + fileName);
//.//            if (view2return != null)
//.//            {
//.//               log.dbg (2, "loadFrame", "frame [" + fileName + "] was already loaded");
//.//
//.//               //(o) TODO reestructurar esto, por ahora para no perder el titulo en caso de frame cargado...
//.//               String title = (String) utilSys.objectSacGet (NAME4UIFRAMETITLE + "." + fileName);
//.//               if (co instanceof Activity)
//.//               {
//.//                  ((Activity) co).setTitle (title);
//.//   //NO SE PUEDE POR : Cannot make a static reference to the non-static method requestWindowFeature(int)
//.//   //               if (title.length () == 0)
//.//   //                    requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
//.//   //               else ((Activity) co).setTitle (title);
//.//               }
//.//
//.//               //(o) RELOAD_CHANGE  we want the gastona script to be loaded again!
//.//               //                   pros: if we generate same gast name with different content, test from programMe ... etc
//.//               //                   cons: lose of data if activity was simply recalled (?)
//.//               //
//.//               // retrieve gastona object for the activity
//.//               //// gastona gastPtr = (gastona) utilSys.objectSacGet (NAME4UIGASTONA + "." + fileName);
//.//               //// if (gastPtr != null && gastPtr.myMensaka4listix != null)
//.//               //// {
//.//               ////    String [] reduzParam = new String [0];
//.//               ////    if (allParams != null && allParams.length > 1)
//.//               ////    {
//.//               ////       reduzParam = new String [allParams.length-1];
//.//               ////       for (int ii = 0; ii < reduzParam.length; ii ++)
//.//               ////          reduzParam [ii] = allParams[ii+1];
//.//               ////    }
//.//               ////    //(o) Android_startup calling main and main0 by reload
//.//               ////    gastPtr.myMensaka4listix.runListixFormat("main", reduzParam);
//.//               //// }
//.//               //// return view2return;
//.//
//.//               gastona gastPtr = (gastona) utilSys.objectSacGet (NAME4UIGASTONA + "." + fileName);
//.//               gastPtr.myMensaka4listix = null;
//.//               gastPtr = null;
//.//               utilSys.objectSacPut (NAME4UIGASTONA + "." + fileName, null);
//.//            }
//.//         }
//.//   //      else
//.//   //      {
//.//   //         javajST.addFrame (layoutName);
//.//   //      }

      String frameTitle = "";

      log.dbg (2, DONDE, "load " + fileName);
      gastona.main (allParams);

      EvaUnit eu = gastona.lastGastona.unitJavaj;
      if (eu == null || eu.size () == 0)
      {
         log.err (DONDE, "file [" + fileName + "] or javaj unit not found in file !");
         return getNoView (co);
      }

      String mainFrameName = "main";
      Eva eframes = eu.getEva ("frames");
      if (eframes != null)
      {
         //(o) Android_TODO assumed only one frame!
         if (eframes.rows () != 1)
            log.warn (DONDE, eframes.rows () + " frames while only one is expected!");

         mainFrameName = eframes.getValue (0, 0);
         frameTitle = eframes.getValue (0, 1);
         if (co instanceof Activity)
         {
            ((Activity) co).setTitle (frameTitle);
//NO SE PUEDE POR : Cannot make a static reference to the non-static method requestWindowFeature(int)
//            if (frameTitle.length () == 0)
//                 requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
//            else ((Activity) co).setTitle (frameTitle);
         }
      }

      //(o) TODO/android/javaj remove "optional" "layout of" it simply promotes writing not compatible scripts
      //(o) Android_javaj_layoutof "layout of" is now optional
      //
      Eva mainlay = eu.getEva (mainFrameName);
      if (mainlay == null)
         mainlay = eu.getEva ("layout of " + mainFrameName);
      if (mainlay == null)
      {
         log.err (DONDE, "layout of " + mainFrameName + " not found!");
         return getNoView (co);
      }

      if (view2return == null)
         view2return = laying.laya_EvaLayout (co, null, null, mainlay);

      // poblaDatos
      //EvaUnit rasa = EvaFile.loadEvaUnit (fileName, "data");

      if (gastona.lastGastona != null)
      {
         log.dbg (2, DONDE, "running main of : " + fileName);
         Mensaka.sendPacket (javaj.javajEBS.msgCONTEXT_BASE, gastona.lastGastona.unitData);
         gastona.lastGastona.myMensaka4listix.runListixFormat("main"); // note that gastona.main already set the parameters!
      }

      // A direct or memory file gast is just tracked once
      // nested GAST calls in the same instance is not supported
      //
      //    by giving a common name we ensure that the next time this gast is called
      //    the old one get cleared ("pass the dutchie" policy)
      //
      if (fileName.startsWith (":utf"))
         fileName = ":utf";

      utilSys.objectSacPut (NAME4UIFRAME + "." + fileName, view2return);
      utilSys.objectSacPut (NAME4UIFRAMETITLE + "." + fileName, frameTitle);
      utilSys.objectSacPut (NAME4UIGASTONA + "." + fileName, gastona.lastGastona);
      log.dbg (0, DONDE, "GASTRECYCLER store view and gastona logic for : " + fileName);

      return view2return;
   }

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      /*
      // &&&&
      {
         Eva eva = new Eva (logServer.EVACONF_LOG_LEVELS_BY_CLIENT);

         eva.setValue ("clientName", 0, 0);
         eva.setValue ("maxLogLevel", 0, 1);

         eva.setValue ("listix_flow", 1, 0);
         eva.setValue ("12", 1, 1);

         eva.setValue ("gastonaMainActor", 2, 0);
         eva.setValue ("19", 2, 1);

         eva.setValue ("gastonaFlexActor", 3, 0);
         eva.setValue ("19", 3, 1);

         logServer.setUDPDebugPort (0); // will set the default one
         logServer.configure (10, eva);
      }
      // &&&&
	  */

      final String DONDE = "flexActor@onCreate";

      androidSysUtil.setCurrentActivity (this);

      mBasuraView = new zButton (this, "bEnd", "bEnd"); // replace it with an even more dummy View

      // analyze parameters from LaunchGastona
      //   LAUNCH GASTONA, filegast, parametros, ...
      String [] params = getIntent().getStringArrayExtra(CmdLaunchGastona.EXTRA_VALUE_NAME);
      if (params == null || params.length == 0)
         log.dbg (2, "onCreate", "No parameters from " + CmdLaunchGastona.EXTRA_VALUE_NAME);
      else
      {
         FLEXIDName = params[0];
         log.dbg (2, DONDE, "gast file [" + fileUtil.resolveCurrentDirFileName (params[0]) + "]");
         View la1 = loadFrame (this, params);
         // evitar exception ?!
         android.view.ViewGroup vigu = (android.view.ViewGroup) la1.getParent();
         if (vigu != null)
         {
            log.dbg (2, DONDE, "TEST:: Hemos evitado una desgracia!");
            vigu.removeView(la1);
         }

         setContentView(la1);
      }
   }

   protected void debugStamp (String posi)
   {
      log.dbg (2, "flexActor@" + posi, logServer.elapsedMillis () + " id = " + FLEXID + " name = \"" + FLEXIDName + "\"");
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
      debugStamp ("onPause");
   }

   protected void onStop()
   {
      super.onStop ();
      Mensaka.sendPacket ("javaj exit", null);


      // finish ();
      debugStamp ("onStop");
   }

   protected void onDestroy()
   {
      //13.11.2010 13:26
      //    needed to avoid the exception
      //    "The specified child already has a parent. You must call removeView() on the child's parent first."
      //    when creating a new activity that reuses the View
      if (mBasuraView != null)
         setContentView(mBasuraView);

      super.onDestroy ();

      //Mensaka.unsubscribe (this);
      debugStamp ("onDestroy");
   }
}


/**
 NOTAS:

   if (use_full_screen)
   {
      getWindow().addFlags   (WindowManager.LayoutParams.FLAG_FULLSCREEN);
      getWindow().clearFlags (WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
   }
   else
   {
      getWindow().addFlags   (WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
      getWindow().clearFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN);
   }
*/