/*
package de.elxala.zWidgets
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

package javaj.widgets;

import android.widget.TextView;
import android.widget.SeekBar;
import android.content.Context;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.Eva.layout.*;
import de.elxala.langutil.graph.sysFonts;

import javaj.widgets.basics.*;

/**

      componente: dMiSlider
      data      : <dMiSlider>     value
                  <dMiSlider rangeConfig>  minValue [0], maxValue [100], increment [1], defaultValue [0]

*/
public class zSliderLabels extends EvaLayout
      implements MensakaTarget,
                 izWidget
{

   // real components of this zwidget
   //
   private SeekBar slider   = null;
   private TextView labelMin  = null;
   private TextView labelMax  = null;
   private TextView labelCurr = null;

   // layout used in this zwidget
   private Eva evaInfoLayX = new Eva();
   private Eva evaInfoLayY = new Eva();
   private Eva currentLayout = null;
   private EvaLayout theLay = null;


   // Aparato helper
   private basicAparato helper = null;

   private double redonder = 100.;
   private boolean doNotNotify = false;

   private double minValue = 0.;
   private double maxValue = 0.;
   private double incValue = 0.;
   private double valValue = 0.;

   public zSliderLabels (Context co, String map_name)
   {
      super (co);
      setName (map_name);
      build (co, map_name);
   }

   private void build (Context co, String map_name)
   {
      helper = new basicAparato (this, new widgetEBS (map_name, null, null));
      slider = new SeekBar (co);

      labelMin  = new TextView (co);
      labelMin.setText("0");
      labelMin.setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());

      labelMax  = new TextView (co);
      labelMax.setText("100");
      labelMax.setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());

      labelCurr = new TextView (co);
      labelCurr.setText("0");
      labelCurr.setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());


      evaInfoLayX = prepareEvaLayout (true);
      evaInfoLayY = prepareEvaLayout (false);
      currentLayout = evaInfoLayX;
      switchLayout (currentLayout);

      addView (slider,   "slider");
      addView (labelMin, "lMin");
      addView (labelCurr, "lCurrent");
      addView (labelMax  , "lMax");

      //slider.addChangeListener(this);
   }


   //-i- interface iWidget ---------------------------------------
   //
   public int getDefaultHeight () { return super.getDefaultHeight (); }
   public int getDefaultWidth () { return super.getDefaultWidth (); }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String map_name)
   {
      mName = map_name;
      //build (map_name);
   }

   /**
      This component uses an EvaLayout for the slider and the labels

      <name layout>
            EvaLayout
               zzz, A, X, A
                 A, slider, -, -
                 A, lMin, lCurrent, lMax
   */
   private Eva prepareEvaLayout (boolean orientX)
   {
      // Container pane = this.getContentPane();
      Eva lay = new Eva("4sliderLabels");
      if (orientX)
      {
         lay.setValueVar ("EVA");
         lay.addLine (new EvaLine ("zzz,   A   , X, A, X,  A   "));
         lay.addLine (new EvaLine ("   , slider, -, -, -, -    "));
         lay.addLine (new EvaLine ("   , lMin, ,lCurrent, ,lMax"));
      }
      else
      {
         lay.setValueVar ("EVA");
         lay.addLine (new EvaLine ("yyy,        ,         "));
         lay.addLine (new EvaLine ("   , slider , lMax    "));
         lay.addLine (new EvaLine ("  X,   +    ,         "));
         lay.addLine (new EvaLine ("   ,   +    ,lCurrent "));
         lay.addLine (new EvaLine ("  X,   +    ,         "));
         lay.addLine (new EvaLine ("   ,   +    , lMin    "));
      }

      return lay;
   }

   public void stateChanged(/* //-!- ChangeEvent e */)
   {
      if (doNotNotify) return;

      int intval = 0;
//-!-      int intval = slider.getValue ();

      valValue = minValue + incValue * intval;
      if (redonder != 1.)
      {
         valValue = ((int) (valValue * redonder)) / redonder;
      }

//-!-      widgetLogger.log().dbg (2, "zSliderLabels::stateChanged", "intval " + intval + " value " + valValue);

      labelCurr.setText ("" + valValue);

      helper.ebs().setText ("" + valValue);
      helper.signalAction ();
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zSliderLabels",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);

            valValue = stdlib.atof (helper.ebs().getText ());
            if (helper.ebs().hasAll ())
               mySetValue (valValue);
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "zSliderLabels",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);

//            // orientation
//            boolean orientationHorizontal = true;
//            Eva ori = helper.ebs().getAttribute (helper.ebs().CONTROL, "orientation");
//            if (ori != null)
//            {
//               String orChar = ori.getValue();
//               if (orStr.equalsIgnoreCase("X") || orStr.equalsIgnoreCase("H"))
//               {
//               }
//            }


            updateControl ();
            if (helper.ebs().firstTimeWithAll ())
               mySetValue (valValue);

//-!-            setEnabled (helper.ebs ().getEnabled ());

            int viso = helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE;
            slider.setVisibility (viso);
            labelMin.setVisibility (viso);
            labelMax.setVisibility (viso);
            labelCurr.setVisibility (viso);
            break;

         default:
            return false;
      }

      return true;
   }

   private void updateControl ()
   {
      Eva range = helper.ebs().getAttribute (helper.ebs().CONTROL, "rangeConfig");
      if (range == null)
      {
         setRanges (0, 100, 1, 0);
      }
      else
      {
         setRanges (stdlib.atof (range.getValue (0, 0)),
                    stdlib.atof (range.getValue (0, 1)),
                    stdlib.atof (range.getValue (0, 2)),
                    stdlib.atof (range.getValue (0, 3)));
      }

      // orientation
      //
      String sOri = helper.ebs().getSimpleAttribute (helper.ebs().CONTROL, "orientation", "X");
      int iOrientation = (sOri.equalsIgnoreCase("X") || sOri.equalsIgnoreCase("H")) ? 0: 1;
      if (currentLayout == null || (iOrientation == 0 ^ currentLayout == evaInfoLayX))
      {
         // change of orientation
//-!-         slider.setOrientation (iOrientation);
         currentLayout = (iOrientation == 0) ? evaInfoLayX: evaInfoLayY;

         theLay.switchLayout (currentLayout);
      }
   }

   // note : the base class method JSlider.setValue CANNOT be override
   //        in order to implement our conversion (see mySetValue)
   //        because JSlider call this method with its scale which is not the right one
   //
   //   public void setValue (int value)
   //   {
   //      setValue ((double) value);
   //   }

   // note : The slider value handled by the JSlider has to be converted acording to the rage configured
   //
   private void mySetValue (double value)
   {
      if (incValue > 0.)
      {
         valValue = value;
//-!-         slider.setValue ((int) ((valValue - minValue) / incValue));
//-!-         widgetLogger.log().dbg (2, "zSliderLabels::mySetValue", "incValue " + incValue + " value " + value + " valValue " + valValue + " finalValue " + slider.getValue ());

         labelCurr.setText ("" + valValue);
      }
   }

   private void setRanges (double min, double max, double inc, double defaultVal)
   {
      double range = max - min;

      labelMin.setText ("" + min);
      labelMax.setText ("" + max);

      if (range > 0. && inc > 0.)
      {
         if (inc > range) inc = range / 10.; // inc cannot be greater! take some lower value

         int npasos = (int) (range / inc);
         //if (npasos > 2000) npasos = 2000;   // make no sense to have more inc than pixels

         minValue = min;
         maxValue = max;
         incValue = inc;

         // setting the configuration should not trigger any action
         //
         doNotNotify = true;

//-!-         slider.setMinimum (0);
//-!-         slider.setMaximum (npasos);

//-!-         slider.setMajorTickSpacing (10);
//-!-         slider.setMinorTickSpacing (1);
         // set default value only once and only if not already set!
         if (helper.ebs().hasData() && helper.ebs().getText ().equals (""))
         {
            mySetValue (defaultVal);
            helper.ebs().setText ("" + valValue);
         }

         doNotNotify = false;
      }
   }
}
