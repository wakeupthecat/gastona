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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import de.elxala.Eva.*;
import de.elxala.Eva.layout.*;
import de.elxala.langutil.*;
import javaj.widgets.*;
import javaj.widgets.graphics.*;

import de.elxala.zServices.*;

/**
   This class is responsible of laying out components
   and containers
*/
public class laying
{
   private static logger log = new logger (null, "javaj.laying", null);
   private static layComposCyclicControl ciclon = new layComposCyclicControl ();

//   public static void main (String [] aa)
//   {
//      if (aa.length < 2)
//      {
//         System.out.println ("Syntax: javajFilename nameOfFrame");
//         return;
//      }
//
//      EvaUnit eu = EvaFile.loadEvaUnit (aa[0], "javaj");
//      if (eu == null)
//      {
//         System.err.println ("Not found either the file " + aa[0] + " or the EvaUnit javaj inside!");
//         return;
//      }
//
//      zFrame pater = buildzFrame (eu, aa[1]);
//
//      pater.addWindowListener (new WindowAdapter() { public void windowClosing (WindowEvent e)
//      {
//         System.exit (0);
//      }});
//
//      pater.show ();
//   }
//
//   public static zFrame buildzFrame (EvaUnit javajLayouts, String name)
//   {
//      return buildzFrame (javajLayouts, name, "");
//   }
//
//   /**
//      builds a zFrame using a onFlyWidgetSolver
//   */
//   public static zFrame buildzFrame (EvaUnit javajLayouts, String name, String prefix)
//   {
//      zFrame poli = new zFrame(name);
//      poli.setBackground ((new javax.swing.JButton ("")).getBackground ());
//
//      buildLayout (new onFlyWidgetSolver (),
//                   poli.getContentPane (),
//                   new javajEBS (javajLayouts),
//                   name,
//                   prefix
//                   );
//
//      poli.pack ();
//      return poli;
//   }
//
//   public static void buildLayout (
//         widgetSolver_able quincalla,     // provide the native widgets
//         Container    pana,               // where to build the layout
//         javajEBS     layoutModel,        // define the layout and all inner layouts needed
//         String       layoutName          // the layout name
//         )
//   {
//      buildLayout (quincalla, pana, layoutModel, layoutName, "");
//   }
//
//   public static void buildLayout (
//         widgetSolver_able quincalla,     // provide the native widgets
//         Container    pana,               // where to build the layout
//         javajEBS     layoutModel,        // define the layout and all inner layouts needed
//         String       layoutName,         // the layout name
//         String       prefix4Widgets      // the prefix for all widget names
//         )
//   {
//      Component co = quincalla.getNativeWidget (layoutModel, layoutName, prefix4Widgets, true);
//      pana.add (co);
//   }
//
//   /**
//      builds the layout acording to the javaj rules in the container
//      'pana'. Note that this is not a layout manager (like EvaLayout) but a
//      layout builder. The tasks of the builder are filling the container using
//      the method add and creating layout managers like MenuBar, ToolBar and EvaLayout.
//      Since the layouts in javaj are, or might be, composites, that is containers that
//      might contain other containers, the method buildLayout can be be recursively called,
//      not directly but through the widgetSolver_able 'quincalla'.
//
//      @arg  quincalla      object with the method "Component getNativeWidget (String prefix4Widgets, String name, javajEBS layoutModel))"
//      @arg  pana           container to fill
//      @arg  layoutModel    layout Model where all needed layouts (<layout of xxx>) has to be found
//      @arg  layoutName     layout name (xxxx of <layout of xxx>)
//      @arg  prefix4Widgets prefix for all widget names in this layout (for all widgets in the composite)
//   */
//   protected static void recBuildLayout (
//         widgetSolver_able quincalla,     // provider of native widgets
//         Container    pana,               // where to build the layout
//         javajEBS     layoutModel,        // define the layout and all inner layouts needed
//         String       layoutName,         // the layout name
//         String       prefix4Widgets      // the prefix for all widget names
//         )
//   {
//
//      //09.03.2009 21:37
//      // NOTE: this method is very similar to loadWidgetsOfLayout, changes here should be made in recBuildLayout as well!
//      //       (both have the know-how of getting the widgets of a layout)
//      //
//      Eva evalay = layoutModel.getLayout (layoutName);
//      if (evalay == null)
//      {
//         log.severe ("recBuildLayout", "layout not found [" + layoutName + "]");
//         return;
//      }
//
//      if (! ciclon.pushClean (layoutName))
//      {
//         //seems to be cyclic
//
//         //Normal handling
//         log.fatal ("recBuildLayout", "layout [" + layoutName + "] is cyclic ! (" + ciclon.cyclusMsg + ")");
//         //writeStringOnTarget("((cyclic!))");
//         return;
//      }
//
//      int widgetKind = layoutModel.getLayoutKind (evalay);
//      if (log.isDebugging (2))
//         log.dbg (2, "recBuildLayout", "layoutName [" + layoutName + "] prefix [" + prefix4Widgets + "] kind = " + widgetKind);
//
//      switch (widgetKind)
//      {
//         case javajEBS.LAY_SWITCH_LAYOUT:
//            recBuildLayout (quincalla, pana, layoutModel, evalay.getValue (0, 1), prefix4Widgets);
//            break;
//
//         case javajEBS.LAY_EVA_LAYOUT:
//            laya_EvaLayout (pana, prefix4Widgets, layoutModel, quincalla, evalay);
//            break;
//
//         case javajEBS.LAY_EVA_MOSAICX:
//         case javajEBS.LAY_EVA_MOSAIC:
//            Eva toEva = MosaicUtil.convertMosaicToEvaLayout (evalay, widgetKind == javajEBS.LAY_EVA_MOSAICX);
//            laya_EvaLayout (pana, prefix4Widgets, layoutModel, quincalla, toEva);
//            break;
//
//         case javajEBS.LAY_MENU:
//            if (pana != null)
//               fabrikMenuBar.setContent ((JMenuBar) pana, evalay, prefix4Widgets);
//            break;
//
//         case javajEBS.LAY_TOOLBAR:
//            if (pana != null)
//               fabrikToolBar.setContent ((JToolBar) pana, evalay, prefix4Widgets);
//            break;
//
//         case javajEBS.LAY_TABBED_PANE:
//            //   TABBED
//            //
//            //    label1, component1
//            //    label2, component2
//            //    ...   , ...
//            //
//            for (int ii = 1; ii < evalay.rows (); ii ++)
//            {
//               String label = evalay.getValue (ii, 0);
//               String name  = evalay.getValue (ii, 1);
//
//               if (name.length () > 0)
//               {
//                  Component co = quincalla.getNativeWidget (layoutModel, name, prefix4Widgets, pana != null);
//                  if (pana != null)
//                  {
//                     pana.add (label, co);
//                  }
//               }
//               else
//                  log.err ("recBuildLayout", "Tabbed pane component not given in row " + ii + " (label = [" + label + "])");
//            }
//            break;
//
//         case javajEBS.LAY_PANEL:
//         case javajEBS.LAY_RADIO:
//         case javajEBS.LAY_SPLIT:
//         case javajEBS.LAY_CONTAINER_ADD:
//            // all the widgets to add in the row 1
//            //
//            //   PANEL or RADIO or SPLIT or ADD, (its parameters ...)
//            //
//            //    component1, component2
//            //
//            //(o) javaj_format Possibly relax here the format and more rows
//            //                 getting simple all components found
//            //
//            for (int ii = 0; ii < evalay.cols (1); ii ++)
//            {
//               String name  = evalay.getValue (1, ii);
//               Component co = quincalla.getNativeWidget (layoutModel, name, prefix4Widgets, pana != null);
//               if (pana != null)
//                  pana.add (co);
//            }
//            break;
//
//         case javajEBS.LAY_PAK:
//            break;
//
//         default:
//            break;
//      }
//
//      ciclon.pop ();
//   }
//
//   /**
//      instanciate all widgets in the given layout
//
//      @arg  quincalla      object with the method "Component getNativeWidget (String prefix4Widgets, String name, javajEBS layoutModel))"
//      @arg  layoutModel    layout Model where all needed layouts (<layout of xxx>) has to be found
//      @arg  layoutName     layout name (xxxx of <layout of xxx>)
//   */
//   public static void loadWidgetsOfLayout (
//         widgetSolver_able quincalla,     // provider of native widgets
//         javajEBS     layoutModel,        // define the layout and all inner layouts needed
//         String       layoutName          // the layout name
//         )
//   {
//      //09.03.2009 21:37
//      // NOTE: this method is very similar to recBuildLayout, changes here should be made in recBuildLayout as well!
//      //       (both have the know-how of getting the widgets of a layout)
//      //
//      Eva evalay = layoutModel.getLayout (layoutName);
//      if (evalay == null)
//      {
//         log.severe ("loadWidgetsOfLayout", "layout not found [" + layoutName + "]");
//         return;
//      }
//
//      if (! ciclon.pushClean (layoutName))
//      {
//         //seems to be cyclic
//         log.fatal ("loadWidgetsOfLayout", "layout [" + layoutName + "] is cyclic ! (" + ciclon.cyclusMsg + ")");
//         return;
//      }
//
//      int widgetKind = layoutModel.getLayoutKind (evalay);
//      if (log.isDebugging (2))
//         log.dbg (2, "loadWidgetsOfLayout", "layoutName [" + layoutName + "] kind = " + widgetKind);
//
//      switch (widgetKind)
//      {
//         case javajEBS.LAY_SWITCH_LAYOUT:
//            break;
//
//         case javajEBS.LAY_EVA_LAYOUT:
//            laya_EvaLayout (null, "", layoutModel, quincalla, evalay);
//            break;
//
//         case javajEBS.LAY_EVA_MOSAICX:
//         case javajEBS.LAY_EVA_MOSAIC:
//            Eva toEva = MosaicUtil.convertMosaicToEvaLayout (evalay, widgetKind == javajEBS.LAY_EVA_MOSAICX);
//            laya_EvaLayout (null, "", layoutModel, quincalla, toEva);
//            break;
//
//         case javajEBS.LAY_MENU:
//         case javajEBS.LAY_TOOLBAR:
//            break;
//
//         case javajEBS.LAY_TABBED_PANE:
//            //   TABBED
//            //
//            //    label1, component1
//            //    label2, component2
//            //    ...   , ...
//            //
//            for (int ii = 1; ii < evalay.rows (); ii ++)
//            {
//               String label = evalay.getValue (ii, 0);
//               String name  = evalay.getValue (ii, 1);
//
//               if (name.length () > 0)
//                  quincalla.getNativeWidget (layoutModel, name, "", false);
//               else
//                  log.err ("loadWidgetsOfLayout", "Tabbed pane component not given in row " + ii + " (label = [" + label + "])");
//            }
//            break;
//
//         case javajEBS.LAY_PANEL:
//         case javajEBS.LAY_RADIO:
//         case javajEBS.LAY_SPLIT:
//         case javajEBS.LAY_CONTAINER_ADD:
//            // all the widgets to add in the row 1
//            //
//            //   PANEL or RADIO or SPLIT or ADD, (its parameters ...)
//            //
//            //    component1, component2
//            //
//            //(o) javaj_format Possibly relax here the format and more rows
//            //                 getting simple all components found
//            //
//            for (int ii = 0; ii < evalay.cols (1); ii ++)
//            {
//               String name  = evalay.getValue (1, ii);
//               Component co = quincalla.getNativeWidget (layoutModel, name, "", false);
//            }
//            break;
//
//         case javajEBS.LAY_PAK:
//            break;
//
//         default:
//            break;
//      }
//      ciclon.pop ();
//   }


   private static View scrollWrap (Context co, View view)
   {
      return view;

      //(o) Android_TODO Solve the problem with ScrollView
      //       ScrollView has the problem that does not fill the parent!
      //       The text view occupies only the area needed for the data (text)
      //       and it grows as needed (in consoles is noticeable due to the background color)
      //       Several things has been tried with no results!
      //       Since an editable text is scrolleable per-se dont need at this point
      //       ScrollView

//      ScrollView scroller = new ScrollView(co);
//      scroller.setFillViewport (true); // <--- no effect !!! :(
////      scroller.addView(view);
//      scroller.addView(view,
//                       new ScrollView.LayoutParams(
//                              ScrollView.LayoutParams.MATCH_PARENT,   // <--- no effect !!! :(
//                              ScrollView.LayoutParams.MATCH_PARENT)); // <--- no effect !!! :(
//      return scroller;
   }

   /**
      Note : if called with pana == null then we are not interested in create panels and layouts but
             simply in instanciating all widgets (not containers)
   */
   public static View laya_EvaLayout (
         Context      co,
         javajEBS     layoutModel,
         widgetSolver_able quincalla,
         Eva          evalay
         )
  {
      EvaLayout ela = new EvaLayout (co, evalay);

      String [] widNames = ela.getWidgets ();
      for (int ii = 0; ii < widNames.length; ii ++)
      {
         String name = widNames[ii];
         if (name.length () == 0) continue;

         //...
         //Component compos = quincalla.getNativeWidget (layoutModel, widgets[ww]);
         //...

         View compos = null;
         switch (name.charAt (0))
         {
            case 'k': compos = new zCheckBox (co, name, name.substring(1));   break;
            case 'i': compos = new zList (co, name);                          break;
            case 'b': compos = new zButton (co, name, name.substring(1));     break;
            case 'e': compos = new zEditField (co, name);                     break;
            case 'l': compos = new zLabel (co, name, name.substring(1));      break;
            case 'x': compos = scrollWrap (co, new zTextArea (co, name));     break;
            case 's': compos = new zSliderLabels (co, name);                  break;
            case 'd': compos = new zSlider (co, name);                        break;
            case 'm': compos = new zImage (co, name);                         break;
            case 'r': compos = new zRadioButtons (co, name);                  break;
            case 'c': compos = new zComboBox (co, name);                      break;

            case '2':
                  if (miscUtil.startsWithIgnoreCase (name, "2D"))
                  {
                     if (miscUtil.startsWithIgnoreCase (name, "2Db")) compos = new zBasicGraphic2D (co, name);
                     if (miscUtil.startsWithIgnoreCase (name, "2Ds")) compos = new z2DGraphicScenes (co, name);
                     if (miscUtil.startsWithIgnoreCase (name, "2De")) compos = new z2DCebolla (co, name);
                     if (miscUtil.startsWithIgnoreCase (name, "2Dj")) compos = new z2DMiRelos (co, name);
                     if (miscUtil.startsWithIgnoreCase (name, "2Dp")) compos = new z2DEditRelos (co, name);                     
                     if (miscUtil.startsWithIgnoreCase (name, "2Dmath")) compos = new z2DMathFunc (co, name);
                     if (miscUtil.startsWithIgnoreCase (name, "2Dcampos")) compos = new z2DCampos (co, name);
                  }
                  break;

            case 'o':
               {
                  // consoles
                  //    o1 = simple output
                  //    o2 = only errors
                  //    else = both
                  int consoletype = zConsole.STD_BOTH;
                  if (name.length () > 1 && name.charAt (1) == '1') consoletype = zConsole.STD_OUTPUT;
                  if (name.length () > 1 && name.charAt (1) == '2') consoletype = zConsole.STD_ERROR;

                  compos = scrollWrap (co, new zConsole (co, name, consoletype));
                  break;
               }
            default:
               compos = new zButton (co, name, name);
               break;
         }
         if (compos != null)
         {
            ela.addView (compos, name);
         }
      }

      return ela;
   }
}