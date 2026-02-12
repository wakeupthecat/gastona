/*
packages de.elxala
Copyright (C) 2005-2022 Alejandro Xalabarder Aulet

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

package javaj;

import java.awt.*;
import java.awt.event.*;   // WindowListener

import javax.swing.*;
import java.util.Vector;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.graph.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.*;

import de.elxala.mensaka.*;
import javaj.widgets.basics.*;
import javaj.widgets.panels.*;

public class javaj36 implements GuiBusyListener
{
   public static final String VERSION = "0.4.200223";

//   private static final int por_citarlos = javaj
   private static final utilSys menciono_utilSys = null;

   private static logger log = new logger (null, "javaj", null);

   private MessageHandle TX_FRAMES_ARE_MOUNTED = new MessageHandle ();
   private MessageHandle TX_SHOW_FRAMES        = new MessageHandle ();
   private MessageHandle TX_FRAMES_ARE_VISIBLE = new MessageHandle ();

   // special messages because finalizeJavaj must to be static
   private static onFlyWidgetSolver widgetSolver = new onFlyWidgetSolver ();

//   public static Vector listOfAllComponents  = new Vector ();
   public static Vector listOfAllControllers = new Vector ();

   private javajEBS javajST = null;
   private String nameBase = "";
   private EvaUnit euJavaj = null;
   private EvaUnit euData = null;

   private final int APPICON_NORMAL  = 0;
   private final int APPICON_BUSY    = 1;
   private final int APPICON_LOG     = 2;
   private final int APPICON_LOGBUSY = 3;
   private final int APPICONS = 4;
   private Image [] appImageArr = new Image [APPICONS];

   public static zFrame lastMainFrame = null;
   private zFrame theParentFrame = null;

   protected void assignMainFrame (zFrame mframe)
   {
      theParentFrame = mframe;
      lastMainFrame = theParentFrame;
   }

   public javaj36 (EvaUnit unitJavaj, EvaUnit unitData)
   {
      euJavaj = unitJavaj;
      euData = unitData;

      Mensaka.setGuiBusyListener (this);
   }

   // == implementation of GuiBusyListener
   //
    @Override
   public void setGuiThreadIsBusy (boolean busy)
   {
      if (mainFrame == null) return;

      boolean logDir = log.getLogDirectory () != null || logServer.hasPushLogger ();

      // APPICON_NORMAL  = 0;
      // APPICON_BUSY    = 1;
      // APPICON_LOG     = 2;
      // APPICON_LOGBUSY = 3;
      // APPICONS = 4;
      Image img = appImageArr [ (busy ? APPICON_BUSY:APPICON_NORMAL) + (logDir ? APPICON_LOG:0) ];
      if (img != null)
         mainFrame.setIconImage (img);
   }

   //
   // exit policy:
   //    every javaj instance has an ID javajInstance
   //    the one with the javajInstance equals to 0 is the master javaj instance
   //    when the master javaj is closed the rest are also closed
   //
   static int currJavajInstance = 0;
   private int javajInstance = currJavajInstance ++;

   private boolean isMasterJavaj ()
   {
      return (javajInstance == 0);
   }

   public void startPhase1 ()
   {
      if (euJavaj == null)
      {
         log.err ("startPhase1", "euJavaj is null!");
         return;
      }
      log.dbg (2, "startPhase1", "begin");


//log.err("startApplication", "go creo mi EBS");
      javajST = new javajEBS (euJavaj);

      javaj36ExitHandler.ensurePresence ();

      // NOTE: this messages are not mandatory to be subscribed, the are provided for finer control of initialization
      Mensaka.declare (this, TX_FRAMES_ARE_MOUNTED, javajST.msgFRAMES_MOUNTED, logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_SHOW_FRAMES       , javajST.msgSHOW_FRAMES, logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_FRAMES_ARE_VISIBLE, javajST.msgFRAMES_VISIBLE, logServer.LOG_DEBUG_0);

//      if (javajST.getFramesCount () == 0)
//      {
//         log.err ("startApplication", euJavaj.getName () + " does not contain frames!");
//         return;
//      }
//
      loadExtraLibraries ();
      defaultResourcesAndLookAndFeel ();
      loadAllWidgets ();
      loadControllers ();

      //---------------------------------------
      // create and layout the main frame
      assignMainFrame (showFrame (javajST.getMainFrame (), false));
      loadRestOfFrames ();
      //---------------------------------------
      log.dbg (2, "startPhase1", "end");
   }
   //

   //
   //
   public void startPhase2 ()
   {
      log.dbg (2, "startPhase2", "begin");
      // send default data found in #data# (named also once context)
      // IMPORTANT : does not matter if euData is null or not, the message
      //             is guaranteed by javaj to be sent at this point
      //
      // send all widgets its "default" data
      Mensaka.sendPacket (javajEBS.msgCONTEXT_BASE, euData);
      // Los wigets se han inicializado con la data default + data posiblemente cargada en main0 (gastona+javaj+listix)

      //(o) TODO_javaj Revisar si esto puede ser problemático o bien todo lo contrario y debería ser aplicado en otros procedimientos, no solo para theParentFrame
      //       Anyadido el 19.04.2009 19:58 por el problema con zSliderLabel (ejemplo sliders verticales)
      // let's repack widgets that now have data
      if (theParentFrame != null)
      {
         //about size of frame
         // Recomended size DO NOT override if frame has alredy resized (due to custom size)
         //
         if (theParentFrame.admitRecomendedSize ())
         {
            log.dbg (2, "startPhase2", "theParentFrame.pack ()");
            //tryGUIpack (theParentFrame);
            theParentFrame.pack ();

            Dimension dimPref = javajST.getInitialSizeOfFrame (0, theParentFrame.getSize ());
            log.dbg (2, "startPhase2", "admitRecomendedSizw true dimPref " + dimPref);
            if (dimPref != null)
               theParentFrame.setSize (dimPref);
         }
         else
         {
            // not recomended size means that we had already a sizeX sizeY in data
            // therefore we will update it now in the java JFrame because pack has change it
            // automatic one
            log.dbg (2, "startPhase2", "theParentFrame.updatePosition ()");
            theParentFrame.updatePosition ();
         }
         showFrame (javajST.getMainFrame (), true);
      }

      // message to permit controllers arrange the widgets
      Mensaka.sendPacket (TX_FRAMES_ARE_MOUNTED, euData);

      // message to make frames visible
      Mensaka.sendPacket (TX_SHOW_FRAMES, euData);

      // message to let it known that frames are visible
      Mensaka.sendPacket (TX_FRAMES_ARE_VISIBLE, euData);
      log.dbg (2, "startPhase2", "end");
   }

   public void startApplication ()
   {
      startPhase1 ();
      startPhase2 ();
   }


   private void loadExtraLibraries ()
   {
      String [] extLib = javajST.getExtraLibraries ();

      log.dbg (2, "loadExtraLibraries", extLib.length + " extra libraries found");
      for (int ee = 0; ee < extLib.length; ee ++)
      {
         // System.loadLibrary (extLib [ee]); .... esto no sirve para nada ....
         log.dbg (2, "loadExtraLibraries", "addClassPath [" + extLib [ee] + "]");
         javaLoad.addClassPath (extLib [ee]);
      }
   }

   private void loadControllers ()
   {
      // if exists <MessageToMessage> load an intern controller of type msgRepeater with it
      //
      Eva messagingTrans = javajST.getMessageMapper (); // <MessageToMessage>

      if (messagingTrans != null)
      {
         log.dbg (2, "loadControllers", messagingTrans.rows() + " MessageToMessage found");
         // not registered intern controller
         msgRepeater ceferin = new msgRepeater ();
         ceferin.loadMessageToRepeat (messagingTrans);
      }

      // load controllers from <controllers>
      //
      Eva extCtrl = javajST.getControllers ();
      log.dbg (2, "loadControllers", extCtrl.rows() + " controllers found");
      for (int ff = 0; ff < extCtrl.rows(); ff ++)
      {
         log.dbg (2, "loadControllers", "javaInstanciator [" + extCtrl.getValue (ff, 0) + "]");
         Object obj = javaLoad.javaInstanciator (extCtrl.getValue (ff, 0));
         if (obj instanceof setParameters_able)
         {
            ((setParameters_able) obj).setParameters (new CParameterArray (extCtrl.get(ff).getColumnArray ()));
         }
         listOfAllControllers.add (obj);
      }
   }

   private zFrame mainFrame = null;

   private void defaultResourcesAndLookAndFeel ()
   {
      // default fonts
      //
      if (javajST.getSysFontsEva () != null)
      {
         log.dbg (2, "defaultResourcesAndLookAndFeel", "default fonts found, setting " + javajST.getSysFontsEva ().getName () + "");
         sysDefaults.setDefaultFonts (javajST.getSysFontsEva ());
      }
      else
      {
         log.dbg (2, "defaultResourcesAndLookAndFeel", "no default fonts found, setting harcoded default fonts");
         sysDefaults.setDefaultFonts ();
      }

      // default GUI system images
      //
      if (javajST.getSysImagesEva () != null)
      {
         log.dbg (2, "defaultResourcesAndLookAndFeel", javajST.getSysImagesEva ().getName ());
         sysDefaults.setDefaultImages (javajST.getSysImagesEva ());
      }
      else
      {
         log.dbg (2, "defaultResourcesAndLookAndFeel", "no special tree images found");
         sysDefaults.setDefaultImages (new Eva ());
      }

      // default look and feel
      //
      log.dbg (2, "defaultResourcesAndLookAndFeel", "Look&Feel decorated " + javajST.isFrameDecorated ());
      JFrame.setDefaultLookAndFeelDecorated(javajST.isFrameDecorated ());

      // change look & feel ?
      //
      String newLook = javajST.newLookAndFeel ();
      if (newLook.length () > 0)
      {
         log.dbg (2, "defaultResourcesAndLookAndFeel", "set Look&Feel " + newLook);
         sysLookAndFeel.setLookAndFeel (newLook);
      }
      else if (! utilSys.isSysUnix)
      {
         log.dbg (2, "defaultResourcesAndLookAndFeel", "set default Look&Feel WINDOWS");
         sysLookAndFeel.setLookAndFeel (sysLookAndFeel.LOOK_WINDOWS);
      }
   }

   private void loadAllWidgets ()
   {
      //09.03.2009 20:44
      // instantiate all the widgets and only the widgets (not containers)
      // traversing all variables <layout of ...>
      // all the widgets must to be found in some layout.
      // This is needed to ensure that all the widgets get the data and messages at the same time
      //
      java.util.List listLayoutNames = javajST.getLayoutNames ();

      for (int ii = 0; ii < listLayoutNames.size (); ii ++)
      {
         log.dbg (2, "loadAllWidgets", "loading widgets of layout [" + (String) listLayoutNames.get (ii) + "]");
         laya.loadWidgetsOfLayout (widgetSolver, javajST, (String) listLayoutNames.get (ii));
      }
   }

   // New method for creating frames!
   public zFrame showFrame (String layoutName, boolean visible)
   {
      if (layoutName == null || layoutName.length () == 0) return null;
      if (javajST.existFrame (layoutName))
      {
         zFrame fr = (zFrame) UIManager.get ("javaj.frame." + layoutName);
         if (fr != null)
         {
             fr.setVisible (visible);
             return fr;
         }
      }
      else
      {
         javajST.addFrame (layoutName);
      }

      // new frame, create it an add it
      log.dbg (2, "showFrame", "create new frame object for " + layoutName);
      zFrame fr = new zFrame (layoutName);
      UIManager.put ("javaj.frame." + layoutName, fr);  // register it

      laya.buildLayout (widgetSolver,
                        fr.getContentPane (),
                        javajST,
                        layoutName,
                        "");              //(o) TOSEE_javaj what about frame prefix ?


      Image ima = javajST.getImageApp ();
      if (ima != null)
         fr.setIconImage (ima);

      fr.setBackground ((new JButton ("")).getBackground ());

      if (javajST.isMainFrame (layoutName))
      {
         //(o) javaj/frameIcons the main javaj main frame way
         //
         mainFrame = fr;

         appImageArr[APPICON_NORMAL]  = ima;
         appImageArr[APPICON_BUSY]    = javajST.loadIconImage ("iconAppBusy");
         appImageArr[APPICON_LOG]     = javajST.loadIconImage ("iconAppLog");
         appImageArr[APPICON_LOGBUSY] = javajST.loadIconImage ("iconAppLogBusy");

         // listener to closing main frame
         //
         if (isMasterJavaj ())
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // tells the frame to not really close the window!

         mainFrame.addWindowListener (
               new WindowAdapter() {
                     public void windowClosing (WindowEvent e)
                     {
                        if (isMasterJavaj ())
                           javaj36ExitHandler.finalizeJavaj ();
                     }});
      }

      log.dbg (2, "showFrame", "frame pack");

      fr.pack ();
      fr.setTitle (javajST.getDefaultTitleOfFrame (0));

      //about size of frame
      // Recomended size DO NOT override if frame has alredy resized (due to custom size)
      //
      if (fr.admitRecomendedSize ())
      {
         Dimension dimPref = javajST.getInitialSizeOfFrame (0, fr.getSize ());
         log.dbg (2, "showFrame", "admitRecomendedSizw true dimPref " + dimPref);
         if (dimPref != null)
            fr.setSize (dimPref);
      }
      else
      {
         // not recommended size means that we had already a sizeX sizeY in data
         // therefore we will update it now in the java JFrame because pack has change it
         // automatic one
         log.dbg (2, "showFrame", "fr.updatePosition ()");
         fr.updatePosition ();
      }

      log.dbg (2, "showFrame", "fr.setVisible (" + visible + ")");
      fr.setVisible (visible);
      return fr;
   }

   /**
      a dialog :

         - will not exit the application on windows close
         - will not load external libraries
         - will not load controllers
   */
   private void loadRestOfFrames ()
   {
      // load rest of frames
      //
      for (int ff = 1; ff < javajST.getFramesCount (); ff ++)
      {
         zFrame ofr = showFrame (javajST.getIdOfFrame (ff), false);
         ofr.setVisible (false);
      }
   }


   //public void relayout (String frameName)
      // detectar frameName! (if null => todos)
   public void relayout ()
   {
      log.dbg (2, "relayout", "relayout " + javajST.getFramesCount () + " frames");
      //System.out.println ("relayout ()");
      for (int ff = 0; ff < javajST.getFramesCount (); ff ++)
      {
         String fname = javajST.getIdOfFrame (ff);
         if (! javajST.existFrame (fname)) continue;

         zFrame fr = (zFrame) UIManager.get ("javaj.frame." + fname);
         if (fr == null)
         {
            log.dbg (2, "relayout", "frame " + fname + " not already registered, do nothing");
            continue;
         }

         // build the new layout
         //
         Container con = fr.getContentPane ();
         con.removeAll ();
         laya.buildLayout (widgetSolver,
                           con,
                           javajST,
                           fname,
                           javajST.getPrefixOfFrame (0));

         log.dbg (2, "relayout", "frame " + fname + " re-layouted");

         if (fr.isShowing ())
            fr.setVisible (true);

         fr.repaint ();
      }
   }

   /**
      new switch layout
   */
   public void maskLayout (String layoutName, String newLayout)
   {
      javajST.setMaskedLayout (layoutName, newLayout, null);
   }

   public void maskLayout (String layoutName, String newLayout, String layoutAlternative)
   {
      javajST.setMaskedLayout (layoutName, newLayout, layoutAlternative);
   }
}
