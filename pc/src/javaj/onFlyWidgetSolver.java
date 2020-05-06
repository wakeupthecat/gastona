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

import java.awt.*;
import javax.swing.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import de.elxala.langutil.*;
import de.elxala.zServices.*;

import javaj.widgets.*;
import javaj.widgets.basics.*;
import javaj.widgets.tree.*;
import javaj.widgets.panels.*;
import javaj.widgets.graphics.*;

public class onFlyWidgetSolver implements widgetSolver_able
{
   //private Map registeredZWidgets = new TreeMap();

   private static logger log = new logger (null, "javaj.onFlyWidgetSolver", null);


   // fictitious class just to get the member classes compiled
   // when compiling gastona.java
   public class mention
   {
      public xyPanel o1=null;
      public xyRadio o2=null;
      public xySplit o3=null;
      public xJTabbedPane o4=null;
      public JMenuBar o5=null;
      public JToolBar o6=null;
   }

   /**
      === Implementation of widgetSolver_able

      Looks if the zwidget alredy exists and return it from the list of
      registered zwidgets and if it is not yet registered then makes an instance
      of it, register it and return the component.

      @arg  layoutModel  layout Model where all needed layouts (<layout of xxx>) are to be found
      @arg  basicName    basic name of the widget
      @arg  prefixName   prefix for the widget
   */
   public Component getNativeWidget (javajEBS layoutModel, String basicName, String prefixName, boolean createContainers)
   {
      // form the name (prefix + " " + name) or (name) if prefix is ""
      String theName = ((prefixName.length() > 0) ? (prefixName + " "): "") + basicName;

      log.dbg (2, "getNativeWidget", "layoutModel \"" + layoutModel + "\" basicName \"" + basicName + "\" prefixName \"" + prefixName + "\""+ "\" createContainers \"" + createContainers + "\"");

      // NOTE: createContainers is false only when examining the widgets for the first time
      //
      if (createContainers) // only allow masking if really laying out
      {
         //is it masked ? (layout or component)
         //
         //  We made a loop to find the final masker layout since we could have the scenario
         //        <layoutMasks>
         //            lay1, lay2
         //            lay2, lay3
         //
         //  where the final masker layout of lay1 is lay3
         //  but at the same time avoid recursive masking (print out an error in that case)
         //

         //not a java.awt.List !!
         java.util.List llevo = new Vector ();

         do
         {
            String masked = layoutModel.getMaskedLayout(theName);
            if (masked == null) break; // it is not masked ...

            log.dbg (2, "getNativeWidget", "masked layout \"" + masked + "\" for layout or widget \"" + theName + "\"");

            //check recursive layout
            if (llevo.contains (theName))
            {
               log.err ("getNativeWidget", "found recursive layout while masking \"" + theName + "\"");
               break;
            }

            //add to list to control recursivity
            llevo.add (theName);

            // take the names for the masker (the layout who masks)
            // SUPONEMOS old prefix "" !!!
            //(o) TODO_javaj_layouting Either fix masking to support "component prefix" or eliminate "component prefix" concept
            basicName = masked;
            theName = masked;
         } while (true);
      }

//      Component co = globalJavaj.getWidgetByName (theName);
//      if (co == null)
//      {
//         // create it
//         co = solveZWidget (layoutModel, basicName, prefixName, theName, createContainers);
//      }
//      else log.dbg (2, "getNativeWidget", "found getWidgetByName \"" + theName + "\"");

      return solveZWidget (layoutModel, basicName, prefixName, theName, createContainers);
   }

   //(o) javaj_widgets hardcoded primitives widgets
   //
   // try to instanciate a component by name
   //
   // Primitive componentes (zWidgets) :
   //
   private Component getPrimitiveComponent (String baseName, String fullName)
   {
      // take name and len (in case component e)
      //
      String sRest = (baseName.length () > 1) ? baseName.substring (1, baseName.length ()): "";

      // calculate "embedded" length for text fields (e20Edit or e04Year)
      int iMayLen = (baseName.length () >= 3) ? stdlib.atoi (baseName.substring (1, 2)): 10;

      Component theComp = null;

      char firstLetter  = (baseName.length () > 0) ? baseName.charAt (0): '\n'; // something impossible
      char secondLetter = (baseName.length () > 1) ? baseName.charAt (1): '\n'; // something impossible

      //(o) DOC/javaj/zwidgets specific zWidgets for PC

      // z widgets
      //
      switch (firstLetter)
      {
         case 'a': theComp = new xyScrollPane   (new zTree (fullName)); break;
         case 'b': theComp = new zButton       (fullName, sRest); break;
         case 'c': theComp = new zComboBox     (fullName); break;
//         case 'd': theComp = new zSlider       (fullName); break;
         case 'd': theComp = new zSliderLabels (fullName); break;
         case 'e': theComp = new zEditField    (fullName, iMayLen, iMayLen > 0);  break;
         case 'f': theComp = new xyScrollPane   (new zMiniEditor (fullName)); break;
         case 'g': theComp = new zLeds         (fullName); break;
         case 't':
               switch (secondLetter)
               {
                  case 'r': /* tr */ theComp = new zRadioButtonTable (fullName); break;
                  //case 'k': /* tk */ theComp = new zCheckBoxTable (fullName);  break;
                  default : theComp = new xyScrollPane (new zTable (fullName)); break;
               }
               break;
         case 's': theComp = new zAsisteTabla  (fullName); break;
         case 'i': theComp = new xyScrollPane   (new zList (fullName)); break;
         case 'h': theComp = new zRadioButtonTable (fullName); break;
         case 'r':
               switch (secondLetter)
               {
                  case 'g': /* rg */
                  case 't': /* rt */ theComp = new zRadioButtonTable (fullName); break;
                  default : theComp = new zRadioButton  (fullName, sRest); break;
               }
               break;
         case 'k':
               switch (secondLetter)
               {
                  case 'g': /* rg */
                  case 't': /* rt */ theComp = new zCheckBoxTable (fullName); break;
                  default : theComp = new zCheckBox     (fullName, sRest); break;
               }
               break;
         case 'l': theComp = new zLabel        (fullName, sRest); break;
         case 'm': theComp = new xyScrollPane   (new zImage (fullName)); break;
         case 'x': theComp = new xyScrollPane   (new zTextArea (fullName)); break;

         case '2':
               if (miscUtil.startsWithIgnoreCase (fullName, "2D"))
               {
                  if (miscUtil.startsWithIgnoreCase (fullName, "2Db"))             // before "2Dbasic"
                     theComp = new zBasicGraphic2D (fullName);
                  else if (miscUtil.startsWithIgnoreCase (fullName, "2Ds"))        // before "2Dsvg"
                     theComp = new z2DGraphicScenes (fullName);
                  else if (miscUtil.startsWithIgnoreCase (fullName, "2De"))        // cebolla
                     theComp = new  z2DCebolla (fullName);
                  else if (miscUtil.startsWithIgnoreCase (fullName, "2Dj"))        // z2DMiRelos
                     theComp = new  z2DMiRelos (fullName);
                  else if (miscUtil.startsWithIgnoreCase (fullName, "2Dp"))        // z2DEditRelos
                     theComp = new  z2DEditRelos (fullName);
                  else if (miscUtil.startsWithIgnoreCase (fullName, "2Dmath"))        // z2DMathFunc
                     theComp = new  z2DMathFunc ();
                  //else if (miscUtil.startsWithIgnoreCase (fullName, "2Dcampos"))        // z2DCampos
                  //   theComp = new  z2DCampos (fullName);
                  else theComp = new JButton ( "?" + fullName);
               }
               else theComp = new JButton ( "?" + fullName);
               break;
         case 'o':
               int tipo = zConsole.STD_BOTH;
               switch (secondLetter)
               {
                  case 's': /* os */
                  case 'o': /* oo */
                  case '1': /* o1 */
                     theComp = new xyScrollPane (new zConsole (fullName, zConsole.STD_OUTPUT));
                     break;
                  case 'e': /* oe */
                  case '2': /* o2 */
                     theComp = new xyScrollPane (new zConsole (fullName, zConsole.STD_ERROR));
                     break;
                  default :
                     theComp = new xyScrollPane (new zConsole (fullName, zConsole.STD_BOTH));
                     break;
               }
               break;
         //(o) javaj_special como poner un JScroll en un widget primitivo (chapuza prevista!)
         // lo mismo que z2DPanelBase pero con scrolls !!
         // case '4': theComp = new xyScrollPane   (new z2DPanelBase  (fullName, null)); break;

         default:
            log.err ("getPrimitiveComponent", "widget not found for component [" + baseName + "/" + fullName + "]");
            theComp = new JButton ( "?" + fullName); break;
      }

      if (theComp != null)
         log.dbg (2, "getPrimitiveComponent", "new primitive component [" + baseName + "/" + fullName + "] = " + theComp.getClass().toString ());
      else
         log.err ("getPrimitiveComponent", "could not create any component for [" + baseName + "/" + fullName + "]");
      return theComp;
   }

   private Component solveZWidget (javajEBS layoutModel, String baseName, String prefixName, String fullName, boolean createContainers)
   {
      String [] params = new String [10];

      boolean isAPanel = false;
      int  kindOfWidget = layoutModel.kindOfWidget (baseName, params);

      // do we already have it ?
      Component compos = globalJavaj.getWidgetByName (fullName);
      boolean estaba = (compos != null);

      if (estaba)
      {
         if (kindOfWidget != javajEBS.WID_PANEL)
         {
            log.dbg (2, "solveZWidget", "found widgetByName \"" + fullName + "\"");
            return compos;
         }
      }

      switch (kindOfWidget)
      {
         case javajEBS.WID_UNKNOWN:
            break;

         case javajEBS.WID_PRIMITIVE:
            compos = getPrimitiveComponent (baseName, fullName);
            break;

         case javajEBS.WID_EXPLICIT:
            compos = (Component) javaLoad.javaInstanciator (params[0]);
            if (compos == null)
            {
               // que hasemos ??
               // Error is already prompted in javaLoad, thus simply satisfy somehow the code
               compos = new JButton ("((" + baseName + "))");
               log.warn ("solveZWidget", "new button as default for [" + baseName + "]");
            }
            compos.setName (baseName);
            log.dbg (2, "solveZWidget", "new component, explicit [" + baseName + "/" + baseName + "] = " + compos);

            if (compos instanceof setParameters_able)
            {
               ((setParameters_able) compos).setParameters (params);
            }

            //(o) TODO_INSTANCIANDO CLASES VIA <javaClass of ..
            //       QUITAR EL else
            //       Nuevo Formato
            //           <javaClass of WIDGETNAME>  javaContainerClass, SCROLL, parameter(*), ...
            //
            //       - El widget recibirá TODOS los parametros incluyendo su clase etc.
            //       - Arreglar el tema de nulls en params y limitación de 10 haciendo que
            //         kindOfWidget admita una evaLine retornable o asi
            //
            else if (params[1] != null && params[1].equals ("1"))
            {
               log.err ("solveZWidget", "new component, scroll for explicit widget [" + fullName + "]");
               compos = new xyScrollPane (compos);
            }
            break;

         case javajEBS.WID_PANEL:

            if (! createContainers) break;

            // provide the appropiate container
            if (estaba)
            {
               log.dbg (2, "solveZWidget", "found panel \"" + fullName + "\"");
               if (compos instanceof java.awt.Container)
               {
                  //log.dbg (2, "solveZWidget", "container " + fullName + " removeAll");
                  ((java.awt.Container) compos).removeAll ();
               }
            }
            else
            switch (javajEBS.getLayoutKind (params[0]))
            {
               case javajEBS.LAY_EVA_MOSAIC:
               case javajEBS.LAY_EVA_MOSAICX:
               case javajEBS.LAY_SWITCH_LAYOUT:
               case javajEBS.LAY_EVA_LAYOUT:
                  compos = new JPanel ();
                  break;

               case javajEBS.LAY_CONTAINER_ADD:
                  if (params[1].length () != 0)
                  {
                     // se especifica una clase (un panel)
                     compos = (Component) javaLoad.javaInstanciator (params[1]);
                  }
                  break;

               case javajEBS.LAY_PANEL:
                  compos = new xyPanel ();
                  compos.setName (fullName);
                  break;

               case javajEBS.LAY_RADIO:
                  compos = new xyRadio ();
                  compos.setName (fullName);
                  break;

               case javajEBS.LAY_SPLIT:
                  compos = new xySplit ();
                  compos.setName (fullName);
                  break;

               case javajEBS.LAY_TABBED_PANE:
                  compos = new xJTabbedPane ();
                  compos.setName (fullName);
                  break;

               case javajEBS.LAY_MENU:
                  // no se especifica expli'citamente JMenuBar pero lo e's
                  compos = new JMenuBar ();
                  break;

               case javajEBS.LAY_TOOLBAR:
                  // no se especifica expli'citamente JToolBar pero lo e's
                  compos = new JToolBar ();
                  break;

               default:
                  // ni se especifica ni na'
                  log.err ("solveZWidget", "Layout/container [" + params[0] + "] not recognized!");
                  compos = new JPanel ();
            }

            // set the fullName if not already set (note that primitive widgets might have already done it)
            if (compos != null)
            {
               // set parameters if widget is setParameters_able
               if (compos instanceof setParameters_able)
               {
                  ((setParameters_able) compos).setParameters (params);
               }
            }

            // RECURSIVE !.. (because getNativeWidget may be called by laya)
            laya.recBuildLayout (this, (Container) compos, layoutModel, baseName, prefixName);
            isAPanel = true;
            break;

         default:
            break;
      }

      // register it
      if (!estaba && compos != null)
      {
         log.dbg (2, "solveZWidget", "register component \"" + fullName + "\"");
         globalJavaj.registerWidgetByName (fullName, compos);
         if (log.isDebugging (3))
         {
            log.dbg (3, "solveZWidget", "sizes for " + (isAPanel ? " a panel ": fullName) +
                        " minimum   (" + compos.getMinimumSize().getWidth () + ", " + compos.getMinimumSize().getHeight () + ")" +
                        " preferred (" + compos.getPreferredSize().getWidth () + ", " + compos.getPreferredSize().getHeight () + ")" +
                        " maximum   (" + compos.getMaximumSize().getWidth () + ", " + compos.getMaximumSize().getHeight () + ")"
                    );
         }

         if (compos != null && compos.getName () != null && compos.getName ().length () == 0)
         {
            compos.setName (fullName);
         }
      }

      return compos;
   }
}