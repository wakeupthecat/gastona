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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

import javaj.widgets.basics.*;

/**
*/
public class zSlider extends JSlider implements ChangeListener, MensakaTarget
{
   private basicAparato helper = null;

   private double redonder = 100.;
   private boolean doNotNotify = false;

   private double minValue = 0.;
   private double maxValue = 0.;
   private double incValue = 0.;
   private double valValue = 0.;

   public zSlider (String map_name)
   {
      // constructor (map_name, 0, 0, 100, 50);
      constructor (map_name);
   }

   private void constructor (String map_name)
   {
      setName (map_name);

      helper = new basicAparato (this, new widgetEBS (map_name, null, null));

      addChangeListener(this);
   }

   public void stateChanged(ChangeEvent e)
   {
      if (getValueIsAdjusting() || doNotNotify) return;

      int intval = super.getValue ();

      valValue = minValue + incValue * intval;
      if (redonder != 1.)
      {
         valValue = ((int) (valValue * redonder)) / redonder;
      }

      widgetLogger.log().dbg (2, "zSlider::stateChanged", "intval " + intval + " value " + valValue);

      helper.ebs().setText ("" + valValue);
      helper.signalAction ();
   }

   public boolean takePacket (int mappedID, EvaUnit euData)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs().setNameDataAndControl (null, euData, null);

            valValue = stdlib.atof (helper.ebs().getText ());
            if (helper.ebs().hasAll ())
               mySetValue (valValue);
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs().setNameDataAndControl (null, null, euData);

            updateControl ();
            if (helper.ebs().firstTimeWithAll ())
               mySetValue (valValue);

            setEnabled (helper.ebs ().getEnabled ());
            if (isShowing ())
               setVisible (helper.ebs ().getVisible ());
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
      String orientation = helper.ebs().getSimpleAttribute (helper.ebs().CONTROL, "orientation", "X");
      int iori = (orientation.equalsIgnoreCase("X") || orientation.equalsIgnoreCase("H")) ? 0: 1;
      setOrientation (iori);
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
         super.setValue ((int) ((valValue - minValue) / incValue));
         widgetLogger.log().dbg (2, "zSlider::mySetValue", "incValue " + incValue + " value " + value + " finalValue " + super.getValue ());
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

         setMinimum (0);
         setMaximum (npasos);

         //setMajorTickSpacing ((int) (range / 5));
         //setMinorTickSpacing ((int) (range / 20));
         setMajorTickSpacing (10);
         setMinorTickSpacing (1);
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



/*
   //(o) javaj_Catalog_source

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#data#


   <table_widgets>
   <!   prefix, javaName     , groupInfo,  importance, helpEvaName, desc, helpText
            ! , zSliderASECAS, button   ,    3       , help !,  //A slider deprecated


   <help !>

      //A zSlider is deprecated zSliderLabels is used insetead



   <table_widgets_attributes>
   <! prefix  , name,         in_out  , possibleValues                      , desc


   <table_widgets_messages>
   <! prefix  , msg, in_out, desc

   <table_widgets_examples>
   <! prefix    , sampleEvaName,   desc, sampleText


#** FIN EVA#

*/