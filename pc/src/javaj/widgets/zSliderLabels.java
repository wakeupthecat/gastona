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
import de.elxala.Eva.layout.*;
import de.elxala.langutil.*;

import javaj.widgets.basics.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (d) zSliderLabels

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zSliderLabels
   <groupInfo>  button
   <javaClass>  javaj.widgets.zSliderLabels
   <importance> 3
   <desc>       //Slider with labels for values (rapid prefix 'd')

   <help>
      //
      // Slider where the following values might be configured
      //   - current value of the slider (default 0)
      //   - minimum value (default 0)
      //   - maximum value (default 100)
      //   - increment value (default 1)

   <prefix> d

   <attributes>
      name             , in_out, possibleValues , desc

                       , in    , text           , //Current value
      visible          , in    , 0 / 1          , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1          , //Value 0 to disable the widget
      rangeConfig      , in    , (Eva list)     , //List of 4 numbers (min, max, inc, default) configuring minimum, maximum, incremnt and default values
      orientation      , in    , X / Y          , //Orientation 'X' or 'H' for horizontal, 'Y' or 'V' for vertical



   <messages>

      msg, in_out, desc

      data!,  in    , update data
      control!,  in    , update control
        ,  out   , the slider has been changed

   <examples>
      gastSample

      hello zSliderLabels
      slider ranges
      slider oriented

   <hello zSliderLabels>
      //#javaj#
      //
      //    <frames> F, Hello zSliderLabels
      //
      //    <layout of F>
      //          PANEL, X
      //          dSlider
      //
      //#listix#
      //    <-- dSlider>
      //       //You have changed the slider to the value @<dSlider>!
      //       //
      //


   <slider ranges>

      //#javaj#
      //
      //   <frames> F, Slider example
      //
      //   <layout of F>
      //      EVA, 10, 10, 6, 6
      //
      //      , A
      //      , dNormal
      //      , dFino
      //      , dPreciso
      //
      //#data#
      //
      //   <dFino> 0
      //   <dFino rangeConfig> -1000, 1000, 1, 0
      //
      //   <dPreciso> 0
      //   <dPreciso rangeConfig> -2, 5, .001, 0
      //
      //#listix#
      //
      //   <-- dNormal>   //Selected from normal @<dNormal>
      //                  //
      //   <-- dFino>     //Selected from fino @<dFino>
      //                  //
      //   <-- dPreciso>  //Selected from preciso @<dPreciso>
      //                  //
      //

   <slider oriented>

      //#javaj#
      //
      //   <frames> F, Slider example
      //
      //   <layout of F>
      //      EVA, 10, 10, 6, 6
      //
      //      ,   X   ,  X  , X
      //    X , dOne  , dTwo, dThree
      //      ,       ,     , kVertical
      //
      //#data#
      //
      //   <kVertical selected> 1
      //
      //   <dOne orientation> V
      //   <dTwo orientation> V
      //   <dThree orientation> V
      //
      //#listix#
      //
      //    <-- kVertical>
      //          IN CASE, @<kVertical selected>
      //             , 1, -->, dThree control!, orientation, Y
      //             , 0, -->, dThree control!, orientation, X
      //

#**FIN_EVA#

*/



/**

      componente: dMiSlider
      data      : <dMiSlider>     value
                  <dMiSlider rangeConfig>  minValue [0], maxValue [100], increment [1], defaultValue [0]

*/
public class zSliderLabels extends JPanel implements ChangeListener, MensakaTarget
{

   // real components of this zwidget
   //
   private JSlider slider   = null;
   private JLabel labelMin  = new JLabel ("0");
   private JLabel labelMax  = new JLabel ("100");
   private JLabel labelCurr = new JLabel ("0");

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

   public zSliderLabels ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zSliderLabels (String map_name)
   {
      build (map_name);
   }

   public void setName (String map_name)
   {
      build (map_name);
   }

   private void build (String map_name)
   {
      super.setName (map_name);

      helper = new basicAparato (this, new widgetEBS (map_name, null, null));
      slider = new JSlider ();

      // needed to have a typical UI slider in windows (from Windows 7 the default slider is quite stupid)
      slider.setUI(new javax.swing.plaf.basic.BasicSliderUI (slider));

      evaInfoLayX = prepareEvaLayout (true);
      evaInfoLayY = prepareEvaLayout (false);
      currentLayout = evaInfoLayX;
      theLay = new EvaLayout (currentLayout);

      setLayout (theLay);

      add ("slider",   slider);
      add ("lMin",     labelMin);
      add ("lCurrent", labelCurr);
      add ("lMax",     labelMax);

      slider.addChangeListener(this);
   }

   public Dimension getPreferredSize ()
   {
//      if (theLay == null)// y yo que se!
//         return super.getPreferredSize ();

      Dimension di = theLay.preferredLayoutSize(null);
      //System.out.println ("getPreferredSize () [" + getName () + "] yo doy " + di.width + ", " + di.height);

      return di;
   }

   public Dimension getMinimumSize ()
   {
//      if (theLay == null)// y yo que se!
//         return super.getMinimumSize ();

      Dimension di = theLay.minimumLayoutSize(null);
      //System.out.println ("getMinimumSize () [" + getName () + "] yo doy " + di.width + ", " + di.height);

      return di;
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

   public void stateChanged(ChangeEvent e)
   {
      if (slider.getValueIsAdjusting() || doNotNotify) return;

      int intval = slider.getValue ();

      valValue = minValue + incValue * intval;
      if (redonder != 1.)
      {
         valValue = ((int) (valValue * redonder)) / redonder;
      }

      widgetLogger.log().dbg (2, "zSliderLabels::stateChanged", "intval " + intval + " value " + valValue);

      labelCurr.setText ("" + valValue);

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

            setEnabled (helper.ebs ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebs ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);
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
         slider.setOrientation (iOrientation);
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
         slider.setValue ((int) ((valValue - minValue) / incValue));
         widgetLogger.log().dbg (2, "zSliderLabels::mySetValue", "incValue " + incValue + " value " + value + " valValue " + valValue + " finalValue " + slider.getValue ());

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

         slider.setMinimum (0);
         slider.setMaximum (npasos);

         //setMajorTickSpacing ((int) (range / 5));
         //setMinorTickSpacing ((int) (range / 20));
         slider.setMajorTickSpacing (10);
         slider.setMinorTickSpacing (1);
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
