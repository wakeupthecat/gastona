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

import java.awt.event.*;
import javax.swing.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;
import javaj.widgets.panels.*;


/*
   //(o) WelcomeGastona_source_javaj_widgets (r) zRadioButton

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zRadioButton
   <groupInfo>  button
   <javaClass>  javaj.widgets.zRadioButton
   <prefix> r
   <importance> 2
   <desc>       //A typical radio button (rapid prefix 'r')

   <help>
      //
      // NOTE: the new widget zRadioButtonTable is a better approach to handle radio buttons
      //
      // This widget represents a single radio button, note though that an isolated radio button
      // make no sense, it has to be grouped with at least another radio button. This is done through
      // the special javaj layout RADIO. Radio buttons listen and send its own messages, the RADIO
      // layout send also a message for the group.
      //
      // Note : If not given in #data# the widgets takes its contents from its own name. This is only a
      //        facility for the development and serves only as default label. Do not confuse widget's
      //        name with its contents, among other things widget names cannot be changed.
      //
      // Widget characteristics: "Common".
      //

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , text                       , //Caption text of the radio button
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget

   <messages>

      msg, in_out, desc

      data!   ,  in    , update data
      control!,  in    , update control
              ,  out   , button has been selected

   <examples>
      gastSample

      hello zRadioButton

   <hello zRadioButton>
      //#javaj#
      //
      //    <frames> F, Hello zRadioButton
      //
      //    <layout of F>
      //       PANEL, X
      //       Fradio, oConsole
      //
      //    <layout of Fradio>
      //          RADIO, Y
      //          rOpt1, rOpt2, rOpt3, rOpt4
      //
      //#listix#
      //
      //    <-- Fradio>
      //       //selected radiobutton @<Fradio selected> (option 1 state @<rOpt1 selected>)
      //       //
      //
      //    <-- rOpt4>
      //       //Option 4 has been pressed!
      //       //



#**FIN_EVA#

*/


/**
   @see a zJCheckBox
*/
public class zRadioButton extends JRadioButton implements ActionListener, MensakaTarget
{
   private basicAparato helper = null;

   public zRadioButton ()
   {
      // default constructor to allow instantiation using <javaClass of...>
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public zRadioButton (String map_name, String slabel, ImageIcon pimage)
   {
      super (slabel, pimage);

      //System.out.println ("la imagen de " + map_name + (pimage == null ? " es": " no parece") + " nula!");

      build (map_name, slabel);
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public zRadioButton (String map_name, String slabel)
   {
      super (slabel);
      build (map_name, slabel);
   }

   public void setName (String map_name)
   {
      build (map_name, map_name);
   }

   private void build (String map_name, String slabel)
   {
      super.setText (slabel);
      super.setName (map_name);

      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      addActionListener (this); // yo mismo
   }


   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            setText (helper.decideLabel (getText ()));
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);

            setSelected (helper.ebs ().isChecked ());
            setEnabled (helper.ebs ().getEnabled ());

            // AltChar or mnemonic
            char c = helper.ebs().getAltChar ();
            if (c != 0)
               setMnemonic(c);

            if (myManager != null && helper.ebs ().isChecked ())
               myManager.checkedToTrue(this);

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

   public void actionPerformed(ActionEvent ev)
   {
      if (myManager != null && isSelected ())
           myManager.checkedToTrue(this);
      else helper.ebs().setChecked (isSelected ());

      if (isSelected())
         helper.signalAction ();
   }

   private IRadioManager myManager = null;

   public void setRadioManager (IRadioManager man)
   {
      myManager = man;
   }

   /// this method is needed for the panel xyRadio to access the data of the radio button
   public basicAparato getHelper ()
   {
      return helper;
   }
}
