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
         
         //(o) DOC/javaj/zwidgets specific zWidgets for Android

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
            //case 'w': compos = scrollWrap (co, new zWebkit (co, name));       break;
            case 'w': compos = new zWebkit (co, name);                        break;

            case '2':
                  if (miscUtil.startsWithIgnoreCase (name, "2D"))
                  {
                     if (miscUtil.startsWithIgnoreCase (name, "2Db")) compos = new zBasicGraphic2D (co, name);
                      // if (miscUtil.startsWithIgnoreCase (name, "2Ds")) compos = new z2DGraphicScenes (co, name);
                      // if (miscUtil.startsWithIgnoreCase (name, "2De")) compos = new z2DCebolla (co, name);
                      // if (miscUtil.startsWithIgnoreCase (name, "2Dj")) compos = new z2DMiRelos (co, name);
                      // if (miscUtil.startsWithIgnoreCase (name, "2Dp")) compos = new z2DEditRelos (co, name);
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