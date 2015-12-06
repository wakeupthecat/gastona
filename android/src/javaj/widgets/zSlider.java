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

import javaj.widgets.basics.*;
import javaj.widgets.text.*;

/**
*/
public class zSlider extends SeekBar
                     implements MensakaTarget, izWidget, SeekBar.OnSeekBarChangeListener
{
   private basicAparato helper = null;

   private double redonder = 100.;
   private boolean doNotNotify = false;

   private double minValue = 0.;
   private double maxValue = 0.;
   private double incValue = 0.;
   private double valValue = 0.;

   public zSlider (Context co, String map_name)
   {
      super (co);
      // constructor (map_name, 0, 0, 100, 50);
      setName (map_name);
   }

   private void build (String map_name)
   {
      helper = new basicAparato (this, new widgetEBS (map_name, null, null));
      setOnSeekBarChangeListener  (this);
      //-!- addChangeListener(this);
   }

   //-i- interface iWidget ---------------------------------------
   //
   public int getDefaultHeight () { return androidSysUtil.getHeightChars(.8f); }
   public int getDefaultWidth () { return androidSysUtil.getWidthChars (6); }

   private String mName = "";

   public String getName ()
      {
      return mName;
      }

   public void setName (String map_name)
   {
      mName = map_name;
      build (map_name);
   }


   //-i- interface OnSeekBarChangeListener--------------------------------------
   //

   // Notification that the progress level has changed.
   //
   public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser)
   {
      stateChanged ();
   }

   //Notification that the user has started a touch gesture.
   public void onStartTrackingTouch (SeekBar seekBar)
   {
   }

   //Notification that the user has finished a touch gesture.
   public void onStopTrackingTouch (SeekBar seekBar)
   {
   }

   public void stateChanged()
   {
      if (doNotNotify) return;

      int intval = getProgress ();

      valValue = minValue + incValue * intval;
      if (redonder != 1.)
      {
         valValue = ((int) (valValue * redonder)) / redonder;
      }

      widgetLogger.log().dbg (2, "zSlider::stateChanged", "intval " + intval + " value " + valValue);

      helper.ebs().setText ("" + valValue);
      helper.signalAction ();
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);

            valValue = stdlib.atof (helper.ebs().getText ());
            if (helper.ebs().hasAll ())
               mySetValue (valValue);
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);

            updateControl ();
            if (helper.ebs().firstTimeWithAll ())
               mySetValue (valValue);

            setEnabled (helper.ebs ().getEnabled ());
            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
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

      // orientation not possible in android
      //      // orientation
      //      //
      //      String orientation = helper.ebs().getSimpleAttribute (helper.ebs().CONTROL, "orientation", "X");
      //      int iori = (orientation.equalsIgnoreCase("X") || orientation.equalsIgnoreCase("H")) ? 0: 1;
      //      setOrientation (iori);
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
         setProgress ((int) ((valValue - minValue) / incValue));
         widgetLogger.log().dbg (2, "zSlider::mySetValue", "incValue " + incValue + " value " + value + " finalValue " + getProgress ());
      }
   }

   private void setRanges (double min, double max, double inc, double defaultVal)
   {
      double range = max - min;

      if (range > 0. && inc > 0.)
      {
         if (inc > range) inc = range / 10.; // inc cannot be greater! take some lower value

         int npasos = (int) (range / inc);
         if (npasos > 2000) npasos = 2000;   // make no sense to have more inc than pixels

         minValue = min;
         maxValue = max;
         incValue = inc;

         // setting the configuration should not trigger any action
         //
         doNotNotify = true;

         setMax (npasos);

//-!-         setMajorTickSpacing (10);
//-!-         setMinorTickSpacing (1);
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
