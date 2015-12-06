/*
packages de.elxala
Copyright (C) 2005 Alejandro Xalabarder Aulet

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

import java.util.List;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;   // WindowListener

import javax.swing.*;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import javaj.widgets.*;

/*
   define la estructura (Eva based) de una aplicacio'n javaj
   p.e.

      #javaj#

      <sysDefaultFonts>
            Arial,   10, 0, *
            Verdana, 11, 3, Button.font, ToggleButton.font
            Verdana, 11, 0, Label.font, ..
            ...

      <LookAndFeel>
         win, com.sun.java.swing.plaf.windows.WindowsLookAndFeel
         met, javax.swing.plaf.metal.MetalLookAndFeel
         mot, com.sun.java.swing.plaf.motif.MotifLookAndFeel


      <sysDefaultImages>
         javaj/img/leaf.png   , Tree.leafIcon
         javaj/img/folder.png , Tree.closedIcon, Tree.rootIcon, Tree.secondRootIcon
         javaj/img/folder.png , Tree.openIcon
         javaj/img/node1.png  , Tree.collapsedIcon
         javaj/img/node2.png  , Tree.expandedIcon


      <extra_libraries>
      <controllers>

      <frames>
         pral, ,                  // se supone que lo carga desde esta misma evaunit
         spy,  spyDialog.javaj,   // se supone que lo carga de esta nueva configuracio'n

      <layout of pral>

         EVALAYOUT,
            zzz,   X    ,   A
               , tula
              X, panelXY,  Plistas
               , PBajos,     +


*/


/**

*/
public class javajEBS
{
   // this message is sent by javaj after instanciating all it components
   // sending the EvaUnit data (#data#). All widgets listen to this message
   // and if they don't already have a base context then use the data sent
   // as base context taking data and control from it.
   // This mechanism permit more instances of javaj working with independent contextes
   //
   public static final String msgCONTEXT_BASE        = "javaj onceContextData&Control";

   public static final String msgFRAMES_MOUNTED      = "javaj frames_mounted";
   public static final String msgSHOW_FRAMES         = "javaj show_frames";
   public static final String msgFRAMES_VISIBLE      = "javaj frames_visible";

   public static final String msgEXIT_JAVAJ_DOWN          = "javaj doExit";
   public static final String msgEXIT_JAVAJ_DOWN_CONTINUE = "javaj doExitYes";

   public static final String msgEXIT_JAVAJ_QUESTION = "javaj exit?";
   public static final String msgEXIT_JAVAJ          = "javaj exit";
   public static final String msgEXIT_JAVAJ_BIS      = "javaj exit_now";
   public static final String msgEXIT_JAVAJ_DONE     = "javaj exit_done";
   public static final String msgEXIT_JAVAJ_EXIT     = "javaj exit_exit";

   public static final String unitDEFAULT_DATA          = "data";

   public static final int LAY_UNKNOWN    = 100;

   // elxala layouts
   public static final int LAY_EVA_LAYOUT  = 101;
   public static final int LAY_EVA_MOSAIC  = 102;
   public static final int LAY_EVA_MOSAICX = 103;
   public static final int LAY_PAK         = 104;   // ?? not yet implemented
   public static final int LAY_SWITCH_LAYOUT = 105;

   // application layouts
   public static final int LAY_MENU        = 110;
   public static final int LAY_TOOLBAR     = 111;
   public static final int LAY_TABBED_PANE = 112;
   public static final int LAY_RADIO       = 113;
   public static final int LAY_SPLIT       = 114;

   // custom
   public static final int LAY_CONTAINER_ADD = 120;
   public static final int LAY_PANEL         = 121;

   //

   public static final int WID_UNKNOWN = 0;
   public static final int WID_PRIMITIVE = 1;
   public static final int WID_EXPLICIT = 2;
   public static final int WID_PANEL = 3;


   private String hauptDateiName = "";
   private EvaUnit mainEUData = null;

   /*
      [ ] Como se define el "mundo" de javaj ? un fichero eva con unidades ? varios ficheros ?

      debemos permitir que carge el mismo las unidades de Eva del fichero puesto
      que sin este dato (fichero) no se pueden cargar los frames (otras unidades dentro del mismo fichero)
   */
   public javajEBS ()
   {
   }

   public javajEBS (EvaUnit euData)
   {
      mainEUData = euData;
   }

   private String [] getEvaAsArray (String name)
   {
      Eva peva = mainEUData.getEva (name);
      if (peva == null) return new String [0];

      return peva.getAsArray ();
   }

   /**
      returns the value or null
   */
   public String getVar (String evaName)
   {
      Eva peva = mainEUData.getEva (evaName);
      if (peva == null) return null;

      return peva.getValue ();
   }

   public Eva getEva (String evaName)
   {
      if (mainEUData == null) return null;
      return mainEUData.getEva (evaName);
   }


   private Image mImageApp = null;

   // remember DO NOT try to load *.ico images but bmp, gif, png, jpg etc ..
   //
   public Image getImageApp ()
   {
      if (mImageApp != null)
         return mImageApp;

      ImageIcon imai = javaLoad.getSomeHowImageIcon (getVar("imageApp"));
      mImageApp = (imai == null) ? null: imai.getImage ();
      return mImageApp;
   }

   public List getLayoutNames ()
   {
      if (mainEUData == null) return new Vector ();

      List names = new Vector ();

      for (int ii = 0; ii < mainEUData.size(); ii ++)
      {
         String eName = mainEUData.getEva(ii).getName ();
         if (eName.startsWith ("layout of "))
            names.add (eName.substring ("layout of ".length ()));
      }
      return names;
   }

   /**
      set a layout mask for a specific layout with name layoutName
      up to here when trying to layout layoutName layoutMask will be actually laid out
      to remove the mask, simply call again setMaskedLayout with layoutMask null or ""
   */
   public void setMaskedLayout (String layoutName, String layoutMask)
   {
      if (mainEUData == null) return;

      // NOTE: we create the eva layoutMasks on this EBS but right now this is not strictly necessary
      //       any other private unit could be used
      //(o) TOSEE_javaj Exporting layoutMasks. Maybe store this eva under #data# (e.g. <:javaj layoutMasks>)
      //                In this case probably this method should be moved to javaj
      Eva ctrl = mainEUData.getSomeHowEva ("layoutMasks");

      int row = ctrl.rowOf (layoutName);
      if (row > -1)
      {
         // the mask already exists
         if (layoutMask == null || layoutMask.length () == 0)
         {
            // mask want to be removed
            ctrl.removeLine (row);
            //System.out.println ("resultado de borrar " + layoutName);
            //System.out.println ("" + ctrl);
            return;
         }
         //System.out.println ("MASKO MASKO " + layoutName + " con (" + layoutMask + ")");

         // change the mask
         ctrl.setValue (layoutMask, row, 1);
      }
      else
      {
         if (layoutMask == null || layoutMask.length () == 0)
         {
            // not need to create nothing!
         }
         else
         {
            // it does not yet exists, do create it
            ctrl.addLine (new EvaLine (new String [] { layoutName, layoutMask } ));
         }
      }
      //System.out.println ("====== after setMaskedLayout " + ctrl);
   }

   public String [] getFrames ()
   {
      if (mainEUData == null) return new String [0]; // or null ?

      //(o) TOSEE_javaj Exporting layoutMasks. Maybe store this eva under #data# (e.g. <:javaj layoutMasks>)
      //                In this case probably this method should be moved to javaj
      Eva ctrl = mainEUData.getSomeHowEva ("frames");
      String [] arr = new String [ctrl.rows ()];
      for (int ii = 0; ii < arr.length; ii ++)
         arr[ii] = ctrl.getValue (ii, 0);

      return arr;
   }

   public boolean existFrame (String layoutName)
   {
      if (mainEUData == null) return false;

      Eva ctrl = mainEUData.getSomeHowEva ("frames");
      return ctrl.rowOf (layoutName) != -1;
   }

   public boolean isMainFrame (String layoutName)
   {
      if (mainEUData == null) return false;

      // main frame is the first one in the list of frames
      Eva ctrl = mainEUData.getSomeHowEva ("frames");
      return ctrl.getValue (0, 0).equals (layoutName);
   }

   public String getMainFrame ()
   {
      if (mainEUData == null) return "";

      // main frame is the first one in the list of frames
      Eva ctrl = mainEUData.getSomeHowEva ("frames");
      return ctrl.getValue (0, 0);
   }

   public void addFrame (String layoutName)
   {
      if (mainEUData == null) return;

      if (existFrame(layoutName)) return;

      Eva ctrl = mainEUData.getSomeHowEva ("frames");
      ctrl.addLine (new EvaLine (layoutName));
   }

   /**
      returns the masked layout name of layoutName if any
      if not found as masked layout then returns null
   */
   public String getMaskedLayout (String layoutName)
   {
      if (mainEUData == null) return null;

      Eva ctrl = getEva ("layoutMasks");
      //System.out.println ("====== getMaskedLayout " + ctrl);
      if (ctrl == null) return null;

      int row = ctrl.rowOf (layoutName);
      if (row != -1)
         return ctrl.getValue (row, 1);

      return null;
   }

   public String [] getExtraLibraries ()
   {
      return getEvaAsArray ("extra_libraries");
   }

   public Eva getControllers ()
   {
      Eva ctrl = getEva ("controllers");
      return (ctrl == null) ? new Eva (): ctrl;
   }

   public Eva getMessageMapper ()
   {
      Eva ctrl = getEva ("MessageToMessage");
      return (ctrl == null) ? new Eva (): ctrl;
   }

   public int getFramesCount ()
   {
      Eva ef = getEva("frames");

      return (ef != null) ? ef.rows (): 0;
   }

   private String getValueOfFrame (int row, int col)
   {
      Eva ef = getEva("frames");

      return (ef != null) ? ef.getValue(row, col): "";
   }

   public String getIdOfFrame (int indx)
   {
      return getValueOfFrame(indx, 0);
   }

   public String getDefaultTitleOfFrame (int indx)
   {
      return getValueOfFrame(indx, 1);
   }

   public String getPrefixOfFrame (int indx)
   {
      return getValueOfFrame(indx, 4);
   }

   /**
      returns the initial dimension of the frame at index 'indx' if specified (width and height > 0)
      otherwise returns null
   */
   public Dimension getInitialSizeOfFrame (int indx)
   {
      int pW = stdlib.atoi (getValueOfFrame(indx, 2));
      int pH = stdlib.atoi (getValueOfFrame(indx, 3));

      if (pW != 0 && pH != 0)
         return new Dimension (pW, pH);

      return null;
   }

   /**
      returns the initial dimension of the frame at index 'indx' using currentDim as default
   */
   public Dimension getInitialSizeOfFrame (int indx, Dimension currentDim)
   {
      int pW = stdlib.atoi (getValueOfFrame(indx, 2));
      int pH = stdlib.atoi (getValueOfFrame(indx, 3));

      return new Dimension (pW != 0 ? pW: (int) currentDim.getWidth (),
                            pH != 0 ? pH: (int) currentDim.getHeight ());
   }

   public Eva getLayout (String frameName)
   {
      return mainEUData.getEva ("layout of " + frameName);
   }

   public Eva getSysFontsEva ()
   {
      return mainEUData.getEva ("sysDefaultFonts");
   }

   public Eva getSysImagesEva ()
   {
      return mainEUData.getEva ("sysDefaultImages");
   }


   public boolean isFrameDecorated ()
   {
      Eva lookFeel = mainEUData.getEva ("LookAndFeel");
      if (lookFeel == null) return false;

      return lookFeel.getValue (0, 3).equals("1");
   }

   // removed on 23.08.2009 22:29
   //   /**
   //      returns an image (Icon) if found for the widget, otherwise returns null
   //   */
   //   public ImageIcon imageOfWidget (String name)
   //   {
   //      Eva images = mainEUData.getEva ("images");
   //      if (images == null) return null;
   //
   //      int row = images.rowOf (name);
   //      if (row == -1) return null;
   //
   //      //System.out.println ("LA IMAGEN row es " + row + " el file es \"" + images.getValue(row, 1) + "\"");
   //
   //      return /*ImageIcon */ javaLoad.getSomeHowImageIcon (images.getValue(row, 1));
   //   }


   public String newLookAndFeel ()
   {
      Eva lookFeel = mainEUData.getEva ("LookAndFeel");
      if (lookFeel == null) return "";

      return lookFeel.getValue (0, 1);
   }

   /*
      return
   */
   public int getLayoutKind (Eva evaLay)
   {
      if (evaLay == null)
         return LAY_UNKNOWN;

      // el tipo de layout se encuentra SIEMPRE en la posicio'n 0, 0
      //
      return getLayoutKind (evaLay.getValue (0,0));
   }

   public static int getLayoutKind (String evaLayStr)
   {
      if (evaLayStr == null || evaLayStr.length() == 0)
      {
         return LAY_UNKNOWN;
      }
      evaLayStr = evaLayStr.toUpperCase ();

      if (evaLayStr.equals ("EVALAYOUT"))    return LAY_EVA_LAYOUT;
      if (evaLayStr.equals ("EVA"))          return LAY_EVA_LAYOUT;
      if (evaLayStr.equals ("EVAMOSAIC"))    return LAY_EVA_MOSAIC;
      if (evaLayStr.equals ("EVAMOSAICX"))   return LAY_EVA_MOSAICX;
      if (evaLayStr.equals ("SWITCHLAYOUT")) return LAY_SWITCH_LAYOUT;
      if (evaLayStr.equals ("MENU"))         return LAY_MENU;
      if (evaLayStr.equals ("TOOLBAR"))      return LAY_TOOLBAR;
      if (evaLayStr.equals ("ADD"))          return LAY_CONTAINER_ADD;
      if (evaLayStr.equals ("PANEL"))        return LAY_PANEL;
      if (evaLayStr.equals ("SPLIT"))        return LAY_SPLIT;
      if (evaLayStr.equals ("RADIO"))        return LAY_RADIO;
      if (evaLayStr.equals ("TAB") ||
          evaLayStr.equals ("TABBED"))       return LAY_TABBED_PANE;
      if (evaLayStr.equals ("PAK"))          return LAY_PAK;

      return LAY_UNKNOWN;
   }

   /**
      returns the kind of widget of the widget named 'widName' and its possible parameters. The different
      kind of widgets are:

      WID_EXPLICIT
         The widget is specified by a java class name given in a eva called <javaClass of 'widName'>
         thus if this eva exists then the widget is explicit regard other considerations.

      WID_PANEL
         The widget is a panel or another widget container (composition of containers/layouts)
         If the eva <layout of 'widName'> is found then the widget is a container

      WID_PRIMITIVE
         If the widget is neither explicit or a panel then it is supposed to be a primitive
         widget, althoug this is not checked.

      'parameters': In case the widget is WID_EXPLICIT or WID_PANEL then the parameters are collected
      in the array 'parameters'. NOTE that the array parameters HAS to be allocated before the call to kindOfWidgets

      for instance if we had

         ...
         <javaClass of myWidget>
            com.mycom.mylib.aclass, 1, "title"
         ...

      then the call

         String para = new String [5];
         int kk = kindOfWidget ("myWidget", para);

         // will return kk = WID_EXPLICIT and para = [ "com.mycom.mylib.aclass", "1", "title", null, null ]


   */
   public int kindOfWidget (String widName, String [] parameters)
   {
      Eva eva = null;
      int ret = WID_PRIMITIVE;  // se supone por defecto ...

      // class specified
      //
      eva = mainEUData.getEva ("javaClass of " + widName);
      if (eva != null)
      {
         // es explicito (p.e. <javaClass of widgeto> javaj.estaClaseWidget)
         //
         ret = WID_EXPLICIT;
      }
      else
      {
         // a panel to be layouted
         //
         eva = mainEUData.getEva ("layout of " + widName);
         if (eva != null)
         {
            // es un panel  (p.e. <panel of widgeto> ADD, JTabbdPane, xTesto, botones
            //
            ret = WID_PANEL;
         }
      }

      // collect parameters
      //
      for (int ii = 0; ii < parameters.length; ii ++)
         parameters[ii] = (eva != null && ii < eva.cols(0)) ? eva.getValue(0, ii): null;

      return ret;
   }
}