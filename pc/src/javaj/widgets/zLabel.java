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

import javax.swing.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (l) zLabel

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zLabel
   <groupInfo>  text
   <javaClass>  javaj.widgets.zLabel
   <prefix> l
   <importance> 8
   <desc>       //A typical label (rapid prefix 'l')

   <help>
      //
      // A zLabel is a typical label
      //
      // Note : If not given in #data# the label takes its contents from its own name. This is only a
      //        facility for the development and serves only as default label. Do not confuse label's
      //        name with its contents, among other things widget names cannot be changed.
      //
      // Widget characteristics: "Common".
      //


   <attributes>
     name             , in_out, possibleValues             , desc

                      , in    , text                       , //Data for the label, that is the text to be displayed
     var              , in    , Eva name                   , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)
     visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                      , //Value 0 to disable the widget


   <messages>

      msg, in_out, desc

      data!   ,  in , update data
      control!,  in , update control

   <examples>
     gastSample

     hello zLabel

   <hello zLabel>
      //#javaj#
      //
      //    <frames> F, Hello zLabel
      //
      //    <layout of F>
      //       PANEL, Y
      //       lALabel, lAnother label
      //
      //#data#
      //
      //    <lALabel>   //This is the data of the label
      //

#**FIN_EVA#

*/

/**
   zLabel : zWidget representing a GUI label

   @see zWidgets
   @see javaHelper.gast

*/
public class zLabel extends JLabel implements MensakaTarget
{
   private basicAparato helper = null;

   public zLabel ()
   {
      // default constructor to allow instantiation using <javaClass of...>
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public zLabel (String map_name, String slabel)
   {
      build  (map_name, slabel);
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
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
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            setText (helper.decideLabel (getText ()));
            paintImmediately (getVisibleRect());
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);
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
}
