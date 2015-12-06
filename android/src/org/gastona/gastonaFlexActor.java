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
   private static logger log = new logger (null, gastonaAppConfig.getAppFlexActorClassName (), null);
   public static final String NAME4UIFRAME = "javaj.frame";
   public static final String NAME4UIFRAMETITLE = "javaj.frameTitle";
   public static final String NAME4UIGASTONA = "gastona.object";

   private static View mNoView = null; // in case the layout cannot be loaded
   private View mBasuraView = null;    // to allow removing the attached view!!

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

   public static View loadFrame (Context co, String [] allParams)
   {
      String fileName = org.gastona.commonGastona.getGastFileNameAndProcessArgs (allParams);

      if (fileName == null || fileName.length () == 0)
      {
         log.err ("loadFrame", "invalid fileName!");
         return getNoView (co);
      }

//!//       //--!-- if (javajST.existFrame (layoutName))
//!//       {
//!//          View fr = (View) utilSys.objectSacGet (NAME4UIFRAME + "." + fileName);
//!//          if (fr != null)
//!//          {
//!//             log.dbg (2, "loadFrame", "frame [" + fileName + "] was already loaded");
//!// 
//!//             //(o) TODO reestructurar esto, por ahora para no perder el titulo en caso de frame cargado...
//!//             String title = (String) utilSys.objectSacGet (NAME4UIFRAMETITLE + "." + fileName);
//!//             if (co instanceof Activity)
//!//             {
//!//                ((Activity) co).setTitle (title);
//!// //NO SE PUEDE POR : Cannot make a static reference to the non-static method requestWindowFeature(int)
//!// //               if (title.length () == 0)
//!// //                    requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
//!// //               else ((Activity) co).setTitle (title);
//!//             }
//!// 
//!//             // retrieve gastona object for the activity
//!//             gastona gastPtr = (gastona) utilSys.objectSacGet (NAME4UIGASTONA + "." + fileName);
//!//             if (gastPtr != null && gastPtr.myMensaka4listix != null)
//!//             {
//!//                String [] reduzParam = new String [0];
//!//                if (allParams != null && allParams.length > 1)
//!//                {
//!//                   reduzParam = new String [allParams.length-1];
//!//                   for (int ii = 0; ii < reduzParam.length; ii ++)
//!//                      reduzParam [ii] = allParams[ii+1];
//!//                }
//!//                //(o) Android_startup calling main and main0 by reload
//!//                gastPtr.myMensaka4listix.runListixFormat("main", reduzParam);
//!//             }
//!//             return fr;
//!//          }
//!//       }

//      else
//      {
//         javajST.addFrame (layoutName);
//      }

      String frameTitle = "";

      log.dbg (2, "loadFrame", fileName);
      EvaUnit eu = null;

      EvaUnit [] trio = gastonaAppConfig.getAppHardcodedGastonaTrio (fileName);
      if (trio != null)
         eu = trio[1];
      else
         eu = EvaFile.loadEvaUnit (fileName, "javaj");

      if (eu == null || eu.size () == 0)
      {
         log.err ("loadFrame", "file [" + fileName + "] or javaj unit not found in file !");
         return getNoView (co);
      }

      String mainFrameName = "main";
      Eva eframes = eu.getEva ("frames");
      if (eframes != null)
      {
         //(o) Android_TODO assumed only one frame!
         if (eframes.rows () != 1)
            log.warn ("loadFrame", eframes.rows () + " frames while only one is expected!");

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

      //(o) Android_javaj_layoutof "layout of" is now optional
      //
      Eva mainlay = eu.getEva (mainFrameName);
      if (mainlay == null)
         mainlay = eu.getEva ("layout of " + mainFrameName);
      if (mainlay == null)
      {
         log.err ("loadFrame", "layout of " + mainFrameName + " not found!");
         return getNoView (co);
      }
      log.dbg (2, "loadFrame", fileName + " loaded!");


      View ela = laying.laya_EvaLayout (co, null, null, mainlay);

      // poblaDatos
      //EvaUnit rasa = EvaFile.loadEvaUnit (fileName, "data");

      gastona.main (allParams);
      if (gastona.lastGastona != null)
      {
         Mensaka.sendPacket (javaj.javajEBS.msgCONTEXT_BASE, gastona.lastGastona.unitData);
         gastona.lastGastona.myMensaka4listix.runListixFormat("main"); // note that gastona.main already set the parameters!
      }

      utilSys.objectSacPut (NAME4UIFRAME + "." + fileName, ela);
      utilSys.objectSacPut (NAME4UIFRAMETITLE + "." + fileName, frameTitle);
      utilSys.objectSacPut (NAME4UIGASTONA + "." + fileName, gastona.lastGastona);

      return ela;
   }

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      androidSysUtil.setCurrentActivity (this);

      mBasuraView = new zButton (this, "bEnd", "bEnd"); // replace it with an even more dummy View

      // analyze parameters from LaunchGastona
      //   LAUNCH GASTONA, filegast, parametros, ...
      String [] params = getIntent().getStringArrayExtra(CmdLaunchGastona.EXTRA_VALUE_NAME);
      if (params == null)
         log.dbg (2, "onCreate", "No parameters from " + CmdLaunchGastona.EXTRA_VALUE_NAME);
      else
      {
         log.dbg (2, "onCreate", "gast file [" + fileUtil.resolveCurrentDirFileName (params[0]) + "]");
         View la1 = loadFrame (this, params);
         // evitar exception ?!
         android.view.ViewGroup vigu = (android.view.ViewGroup) la1.getParent();
         if (vigu != null) 
         {
            System.out.println ("TEST:: Hemos evitado una desgracia en FlexActor!");
            vigu.removeView(la1);
         }
         
         setContentView(la1);
      }
   }


   protected void onStart()
   {
      super.onStart ();
      androidSysUtil.setCurrentActivity (this);
      log.dbg (0, "onStart");
   }

   protected void onRestart()
   {
      super.onRestart ();
      log.dbg (0, "onRestart");
   }

   protected void onResume()
   {
      super.onResume ();
      log.dbg (0, "onResume");
   }

   protected void onPause()
   {
      super.onPause ();
      log.dbg (0, "onPause");
   }

   protected void onStop()
   {
      super.onStop ();
      finish ();
      log.dbg (0, "onStop");
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
      log.dbg (0, "onDestroy");
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