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


/*
   //(o) WelcomeGastona_source_javaj_widgets (k) zCheckBox

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zCheckBox
   <groupInfo>  button
   <javaClass>  javaj.widgets.zCheckBox
   <importance> 8
   <desc>       //A typical check box (rapid prefix k)

   <help>
      //
      // Typical checkbox. The attribute 'selected' indicates the state of the checkbox.
      //
      // Note : If not given in #data# the widgets takes its label from its own name. This is only a
      //        facility for the development and serves only as default label. Do not confuse widget's
      //        name with its contents, among other things widget names cannot be changed.
      //
      // Widget characteristics: "Common".
      //

   <prefix>  k


   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , (String)                   , //Caption of the checkbox
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      selected         , inout , 0 | 1                      , //Value 0 not checked, 1 checked

   <messages>

      msg     , in_out , desc

              ,  out   , check box has been pressed
      data!   ,  in    , update data
      control!,  in    , update control


   <examples>
      gastSample

      <!data4Tester
      hello zCheckBox

   <data4Tester>
      //#data#
      //
      //    <kName>  Caption of the checkbox
      //    <kName selected> 1
      //

   <hello zCheckBox>
      //#javaj#
      //
      //    <frames> F, Hello zCheckBox
      //
      //    <layout of F>
      //          EVA, 30, 30, 10, 10
      //          , A
      //          , lState
      //          , kCheck
      //
      //#data#
      //
      //    <kCheck> "Check or uncheck"
      //
      //#listix#
      //
      //    <-- kCheck>
      //       IN CASE,  @<kCheck selected>
      //              ,   1, SET VAR, lState, "Checked!"
      //              ,   0, SET VAR, lState, "NOT checked!"
      //       MSG, lState data!
      //

#**FIN_EVA#

*/

/**
   zCheckBox : zWidget representing a GUI check box

   @see zWidgets
   @see javaHelper.gast

*/
public class zCheckBox extends JCheckBox implements ActionListener, MensakaTarget
{
   private basicAparato helper = null;

   //private String currentIconName = null;

   public zCheckBox ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zCheckBox (String map_name, String slabel)
   {
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

   public boolean takePacket (int mappedID, EvaUnit euData)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setNameDataAndControl (null, euData, null);

            setText (helper.decideLabel (getText ()));

            // to implement images two images are needed (as in RadioButton)
            //    do it individually or for all check boxes ?
            //    does it worth the effort?
            //
            //   if (helper.ebs().getImageFile () != currentIconName)
            //   {
            //      currentIconName = helper.ebs().getImageFile ();
            //      ImageIcon ima = javaLoad.getSomeHowImageIcon (currentIconName);
            //      setIcon (ima);
            //   }
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setNameDataAndControl (null, null, euData);

            // AltChar or mnemonic
            char c = helper.ebs().getAltChar ();
            if (c != 0)
               setMnemonic(c);

            setSelected (helper.ebs ().isChecked ());
            setEnabled (helper.ebs ().getEnabled ());
            if (isShowing ())
               setVisible (helper.ebs ().getVisible ());
            break;

         default:
            return false;
      }

      return true;
   }

   public void actionPerformed(ActionEvent ev)
   {
      helper.ebs().setChecked (isSelected ());
      helper.signalAction ();
   }
}
