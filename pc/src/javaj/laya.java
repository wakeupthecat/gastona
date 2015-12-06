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

import java.awt.Container;
import java.awt.Component;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import java.awt.event.*;   // WindowAdapter

import de.elxala.Eva.*;
import de.elxala.Eva.layout.*;
import javaj.widgets.kits.*;
import javaj.widgets.panels.*;

import de.elxala.zServices.*;

/**
   This class is responsible of laying out components
   and containers
*/
public class laya
{
   private static logger log = new logger (null, "javaj.laya", null);
   private static layComposCyclicControl ciclon = new layComposCyclicControl ();

   public static void main (String [] aa)
   {
      if (aa.length < 2)
      {
         System.out.println ("Syntax: javajFilename nameOfFrame");
         return;
      }

      EvaUnit eu = EvaFile.loadEvaUnit (aa[0], "javaj");
      if (eu == null)
      {
         System.err.println ("Not found either the file " + aa[0] + " or the EvaUnit javaj inside!");
         return;
      }

      zFrame pater = buildzFrame (eu, aa[1]);

      pater.addWindowListener (new WindowAdapter() { public void windowClosing (WindowEvent e)
      {
         System.exit (0);
      }});

      pater.show ();
   }

   public static zFrame buildzFrame (EvaUnit javajLayouts, String name)
   {
      return buildzFrame (javajLayouts, name, "");
   }

   /**
      builds a zFrame using a onFlyWidgetSolver
   */
   public static zFrame buildzFrame (EvaUnit javajLayouts, String name, String prefix)
   {
      zFrame poli = new zFrame(name);
      poli.setBackground ((new javax.swing.JButton ("")).getBackground ());

      buildLayout (new onFlyWidgetSolver (),
                   poli.getContentPane (),
                   new javajEBS (javajLayouts),
                   name,
                   prefix
                   );

      poli.pack ();
      return poli;
   }

   public static void buildLayout (
         widgetSolver_able quincalla,     // provide the native widgets
         Container    pana,               // where to build the layout
         javajEBS     layoutModel,        // define the layout and all inner layouts needed
         String       layoutName          // the layout name
         )
   {
      buildLayout (quincalla, pana, layoutModel, layoutName, "");
   }

   public static void buildLayout (
         widgetSolver_able quincalla,     // provide the native widgets
         Container    pana,               // where to build the layout
         javajEBS     layoutModel,        // define the layout and all inner layouts needed
         String       layoutName,         // the layout name
         String       prefix4Widgets      // the prefix for all widget names
         )
   {
      Component co = quincalla.getNativeWidget (layoutModel, layoutName, prefix4Widgets, true);
      pana.add (co);
   }

   /**
      builds the layout acording to the javaj rules in the container
      'pana'. Note that this is not a layout manager (like EvaLayout) but a
      layout builder. The tasks of the builder are filling the container using
      the method add and creating layout managers like MenuBar, ToolBar and EvaLayout.
      Since the layouts in javaj are, or might be, composites, that is containers that
      might contain other containers, the method buildLayout can be be recursively called,
      not directly but through the widgetSolver_able 'quincalla'.

      @arg  quincalla      object with the method "Component getNativeWidget (String prefix4Widgets, String name, javajEBS layoutModel))"
      @arg  pana           container to fill
      @arg  layoutModel    layout Model where all needed layouts (<layout of xxx>) has to be found
      @arg  layoutName     layout name (xxxx of <layout of xxx>)
      @arg  prefix4Widgets prefix for all widget names in this layout (for all widgets in the composite)
   */
   protected static void recBuildLayout (
         widgetSolver_able quincalla,     // provider of native widgets
         Container    pana,               // where to build the layout
         javajEBS     layoutModel,        // define the layout and all inner layouts needed
         String       layoutName,         // the layout name
         String       prefix4Widgets      // the prefix for all widget names
         )
   {

      //09.03.2009 21:37
      // NOTE: this method is very similar to loadWidgetsOfLayout, changes here should be made in recBuildLayout as well!
      //       (both have the know-how of getting the widgets of a layout)
      //
      Eva evalay = layoutModel.getLayout (layoutName);
      if (evalay == null)
      {
         log.severe ("recBuildLayout", "layout not found [" + layoutName + "]");
         return;
      }

      if (! ciclon.pushClean (layoutName))
      {
         //seems to be cyclic

         //Normal handling
         log.fatal ("recBuildLayout", "layout [" + layoutName + "] is cyclic ! (" + ciclon.cyclusMsg + ")");
         //writeStringOnTarget("((cyclic!))");
         return;
      }

      int widgetKind = layoutModel.getLayoutKind (evalay);
      if (log.isDebugging (2))
         log.dbg (2, "recBuildLayout", "layoutName [" + layoutName + "] prefix [" + prefix4Widgets + "] kind = " + widgetKind);

      switch (widgetKind)
      {
         case javajEBS.LAY_SWITCH_LAYOUT:
            recBuildLayout (quincalla, pana, layoutModel, evalay.getValue (0, 1), prefix4Widgets);
            break;

         case javajEBS.LAY_EVA_LAYOUT:
            laya_EvaLayout (pana, prefix4Widgets, layoutModel, quincalla, evalay);
            break;

         case javajEBS.LAY_EVA_MOSAICX:
         case javajEBS.LAY_EVA_MOSAIC:
            Eva toEva = MosaicUtil.convertMosaicToEvaLayout (evalay, widgetKind == javajEBS.LAY_EVA_MOSAICX);
            laya_EvaLayout (pana, prefix4Widgets, layoutModel, quincalla, toEva);
            break;

         case javajEBS.LAY_MENU:
            if (pana != null)
               fabrikMenuBar.setContent ((JMenuBar) pana, evalay, prefix4Widgets);
            break;

         case javajEBS.LAY_TOOLBAR:
            if (pana != null)
               fabrikToolBar.setContent ((JToolBar) pana, evalay, prefix4Widgets);
            break;

         case javajEBS.LAY_TABBED_PANE:
            //   TABBED
            //
            //    label1, component1
            //    label2, component2
            //    ...   , ...
            //
            for (int ii = 1; ii < evalay.rows (); ii ++)
            {
               String label = evalay.getValue (ii, 0);
               String name  = evalay.getValue (ii, 1);

               if (name.length () > 0)
               {
                  Component co = quincalla.getNativeWidget (layoutModel, name, prefix4Widgets, pana != null);
                  if (pana != null)
                  {
                     pana.add (label, co);
                  }
               }
               else
                  log.err ("recBuildLayout", "Tabbed pane component not given in row " + ii + " (label = [" + label + "])");
            }
            break;

         case javajEBS.LAY_PANEL:
         case javajEBS.LAY_RADIO:
         case javajEBS.LAY_SPLIT:
         case javajEBS.LAY_CONTAINER_ADD:
            // all the widgets to add in the row 1
            //
            //   PANEL or RADIO or SPLIT or ADD, (its parameters ...)
            //
            //    component1, component2
            //
            //(o) javaj_format Possibly relax here the format and more rows
            //                 getting simple all components found
            //
            for (int ii = 0; ii < evalay.cols (1); ii ++)
            {
               String name  = evalay.getValue (1, ii);
               Component co = quincalla.getNativeWidget (layoutModel, name, prefix4Widgets, pana != null);
               if (pana != null)
                  pana.add (co);
            }
            break;

         case javajEBS.LAY_PAK:
            break;

         default:
            break;
      }

      ciclon.pop ();
   }

   /**
      instanciate all widgets in the given layout

      @arg  quincalla      object with the method "Component getNativeWidget (String prefix4Widgets, String name, javajEBS layoutModel))"
      @arg  layoutModel    layout Model where all needed layouts (<layout of xxx>) has to be found
      @arg  layoutName     layout name (xxxx of <layout of xxx>)
   */
   public static void loadWidgetsOfLayout (
         widgetSolver_able quincalla,     // provider of native widgets
         javajEBS     layoutModel,        // define the layout and all inner layouts needed
         String       layoutName          // the layout name
         )
   {
      //09.03.2009 21:37
      // NOTE: this method is very similar to recBuildLayout, changes here should be made in recBuildLayout as well!
      //       (both have the know-how of getting the widgets of a layout)
      //
      Eva evalay = layoutModel.getLayout (layoutName);
      if (evalay == null)
      {
         log.severe ("loadWidgetsOfLayout", "layout not found [" + layoutName + "]");
         return;
      }

      if (! ciclon.pushClean (layoutName))
      {
         //seems to be cyclic
         log.fatal ("loadWidgetsOfLayout", "layout [" + layoutName + "] is cyclic ! (" + ciclon.cyclusMsg + ")");
         return;
      }

      int widgetKind = layoutModel.getLayoutKind (evalay);
      if (log.isDebugging (2))
         log.dbg (2, "loadWidgetsOfLayout", "layoutName [" + layoutName + "] kind = " + widgetKind);

      switch (widgetKind)
      {
         case javajEBS.LAY_SWITCH_LAYOUT:
            break;

         case javajEBS.LAY_EVA_LAYOUT:
            laya_EvaLayout (null, "", layoutModel, quincalla, evalay);
            break;

         case javajEBS.LAY_EVA_MOSAICX:
         case javajEBS.LAY_EVA_MOSAIC:
            Eva toEva = MosaicUtil.convertMosaicToEvaLayout (evalay, widgetKind == javajEBS.LAY_EVA_MOSAICX);
            laya_EvaLayout (null, "", layoutModel, quincalla, toEva);
            break;

         case javajEBS.LAY_MENU:
         case javajEBS.LAY_TOOLBAR:
            break;

         case javajEBS.LAY_TABBED_PANE:
            //   TABBED
            //
            //    label1, component1
            //    label2, component2
            //    ...   , ...
            //
            for (int ii = 1; ii < evalay.rows (); ii ++)
            {
               String label = evalay.getValue (ii, 0);
               String name  = evalay.getValue (ii, 1);

               if (name.length () > 0)
                  quincalla.getNativeWidget (layoutModel, name, "", false);
               else
                  log.err ("loadWidgetsOfLayout", "Tabbed pane component not given in row " + ii + " (label = [" + label + "])");
            }
            break;

         case javajEBS.LAY_PANEL:
         case javajEBS.LAY_RADIO:
         case javajEBS.LAY_SPLIT:
         case javajEBS.LAY_CONTAINER_ADD:
            // all the widgets to add in the row 1
            //
            //   PANEL or RADIO or SPLIT or ADD, (its parameters ...)
            //
            //    component1, component2
            //
            //(o) javaj_format Possibly relax here the format and more rows
            //                 getting simple all components found
            //
            for (int ii = 0; ii < evalay.cols (1); ii ++)
            {
               String name  = evalay.getValue (1, ii);
               Component co = quincalla.getNativeWidget (layoutModel, name, "", false);
            }
            break;

         case javajEBS.LAY_PAK:
            break;

         default:
            break;
      }
      ciclon.pop ();
   }

   /**
      Note : if called with pana == null then we are not interested in create panels and layouts but
             simply in instanciating all widgets (not containers)
   */
   private static void laya_EvaLayout (
         Container    pana,
         String       prefix4Widgets,
         javajEBS     layoutModel,
         widgetSolver_able quincalla,
         Eva          evalay
         )
  {
      EvaLayout ela = new EvaLayout(evalay);
//      if (ela != null && collectEvaLayouts)
//      {
//         evaLayoutsArray.add (ela);
//      }
      if (pana != null)
      {
         pana.setLayout (ela);
      }

      String [] widgets = ela.getWidgets ();

      for (int ww = 0; ww < widgets.length; ww ++)
      {
         log.dbg (2, "laya_EvaLayout", "component [" + widgets[ww] + "] prefix [" + prefix4Widgets + "]");
         Component compos = quincalla.getNativeWidget (layoutModel, widgets[ww], prefix4Widgets, pana != null);
         if (compos != null && pana != null)
         {
            pana.add (compos, widgets[ww]);
         }
      }
   }

//   private static String getSelectedLayout (javajEBS layoutModel)
//   {
//System.err.println ("okey mano, me requieres " + switchName + " comprendo...");
//      Eva switchLay = layoutModel.getEva (switchName);
//      if (switchLay == null)
//      {
//         System.err.println ("laya FALLA switch layout " + switchLay + " not found!");
//         return null;
//      }
//
//      // now look if any <nameOfSwitchLayout current> is given
//      int indx = -1;
//      Eva selectLay = layoutModel.getEva (switchName +  " current");
//      if (selectLay != null)
//      {
//System.err.println ("bien bien ayo " + switchName +  " current con un porcentual de " + selectLay.getValue ());
//         indx = switchLay.rowOf (selectLay.getValue ());
//      }
//      if (indx == -1)
//      {
//         if (selectLay != null)
//         {
//            System.err.println ("laya FALLA switch layout " + switchLay + " to " + selectLay.getValue () + " but this is not found!");
//         }
//         else
//         {
//            // this is normal <nameOfSwitchLayout current> does not have to be present
//         }
//
//         String defaultLay = switchLay.getValue (1);
//System.err.println ("goso en pos, defectuamos " + defaultLay);
//         return (defaultLay.length () > 0) ? defaultLay: null;
//      }
//
//System.err.println ("tenemos indx " + indx);
//System.err.println ("retornamons " + switchLay.getValue (indx, 0));
//
//      return switchLay.getValue (indx, 0);
//   }
}