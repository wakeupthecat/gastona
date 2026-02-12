/*
package de.elxala.langutil
(c) Copyright 2005-2021 Alejandro Xalabarder Aulet

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


/*
   //(o) javaj_Catalog_source

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       RADIO
   <groupInfo>  special containers
   <javaClass>  javaj.widgets.panels.xyRadio
   <importance> 8
   <desc>       //Panel for grouping radio buttons

   <help>
      //
      // NOTE: the new widget zRadioButtonTable is a better approach to handle radio buttons
      //
      //  This panel is a special panel to group radio buttons (see zWidget zJRadio)
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       RADIO, X | Y, Title
      //
      //       radioButton1, radioButton2, ...
      //
      //  The first row starts with "RADIO", then the orientation
      //  which can be X or H for horizontal and Y or V for vertical and the third
      //  column, if given, creates a border with a title.
      //
      //  The second and last row is to specified all radio buttons that form the group.
      //
      //  Usually layouts does not send messages, RADIO layout send a message (message = name of the
      //  layout) on every click on a radio button contained on it a message. Note that the radio
      //  button send an own message as well, depending on your application you can choose the most
      //  convenient message to react to on a selection.
      //


   <examples>
      gastSample

      radio layout example
      radio layout example2

   <radio layout example>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout RADIO"
      //
      //   <layout of F>
      //
      //      RADIO, Y, Options
      //
      //      rOption1, rAnotherOption, rNone
      //

   <radio layout example2>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout RADIO 2"
      //
      //   <layout of F>
      //     PANEL, Y
      //
      //     radioG, oConsole
      //
      //   <layout of radioG>
      //      RADIO, X, Options
      //
      //      rPop, rRock, rClassic, rSmoothJazz, rMute
      //
      //#listix#
      //
      //   <-- radioG>
      //       //The radio @<radioG selected> has been selected (Mute is @<rMute selected>)
      //       //
      //
      //   <-- rRock>
      //       //Now is Rock selected
      //       //
      //

#**FIN_EVA#

*/

package javaj.widgets.panels;


import java.awt.Component;
import javax.swing.*;
import javax.swing.border.*;

import javaj.widgets.basics.*;
import javaj.widgets.zRadioButton;

import de.elxala.Eva.*;
import de.elxala.mensaka.*;

/**
   20.02.2009 22:38

   at the moment there are 2 communication methods implemented (communication xyRadio with its radio buttons)

   comm-radio method 1
      Through mensaka, xyPanel subscribes himself to actions of all buttons
      + lesser intrusive : does not affect at all code of zRadioButton
      + message actions of single buttons might be ignored with no error because now they have listener
      - it is sensible on changes in order of notifications (xyPanel must be notified first!)
      - this intern communication will be logged mixed with application messages

   comm-radio method 2
      Using a special communication, interface IRadioManager
      + it is guaranteed that xyPanel receives first the radio button action
      - intrusive, changes in zRadioButton has to be performed
      - not much type Safe (casting Object to zRadioButton)
      - OO like, not trivial to understand


   follow the messages "//comm-radio method 1" and "//comm-radio method 2" to switch the
   communication mechanism

*/
public class xyRadio extends JPanel implements setParameters_able,
                                               MensakaTarget,        //comm-radio method 1
                                               IRadioManager         //comm-radio method 2


{
   private static final int RX_RADIOBUTTONS = 0;

   private MessageHandle HMSG_Action = null;

   public xyRadio ()
   {
      super ();
      // setBorder (BorderFactory.createBevelBorder (BevelBorder.RAISED));

      setBorder(new EmptyBorder(2, 2, 2, 2));
      //System.out.println ("CONSTRUCTED " + getName());
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   private ButtonGroup radioGroup = new ButtonGroup ();

   public void setParameters (CParameterArray params)
   {
      String p1 = params.getValueAt (1, "X").toUpperCase();
      String p2 = params.getValueAt (2, null);

      setLayout (new BoxLayout(this, (p1.equals("X") || p1.equals("H")) ? BoxLayout.X_AXIS: BoxLayout.Y_AXIS));
      if (p2 != null)
         setBorder (BorderFactory.createTitledBorder (p2));
   }

   public Component add (Component co)
   {
      Component ret = null;

      // NOTE on 20.02.2009 19:13
      // acording to the java documentation the ButtonGroup admit any AbstractButton
      // but actually the functinality implemented is only suitable for radioButtons
      // and maybe for toggle buttons (at this moment we don't have this)
      // We will admit just radio buttons
      //

      //System.out.println ("ADD TO RADIO PANEL " + getName() + " the componnet " + co.getName ());
      if (co instanceof zRadioButton)
      {
         //comm-radio method 1
         Mensaka.subscribe (this, RX_RADIOBUTTONS + getComponents().length, co.getName ());

         //comm-radio method 2
         //((zRadioButton)co).setRadioManager (this);

         ret = super.add (co);
         radioGroup.add ((AbstractButton) co);
      }
      else
      {
         //RADIO FUTURA! add components that are not radio buttons
         //  ret = super.add (co);
         widgetLogger.log().err ("xyRadio::add", "cannot add " + co.getName () + " on layout " + getName () + ", because it is not an zRadioButton");
      }

      return ret;
   }


   private void signalAction (EvaUnit data, String radioName)
   {
      if (HMSG_Action == null)
      {
         HMSG_Action = new MessageHandle (this, getName ());
      }

      Eva eva = data.getSomeHowEva (getName () + " selected");
      eva.clear ();
      eva.setValue (radioName, 0, 0);

      // send the message
      Mensaka.sendPacket (HMSG_Action, data);
   }

   //comm-radio method 1
   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      int indx = (mappedID - RX_RADIOBUTTONS);

      widgetLogger.log().dbg (2, "xyRadio::takePacket", "received message from button " + indx);

      if (indx < 0 || indx >= getComponents().length)
      {
         widgetLogger.log().severe ("xyRadio::takePacket", "message wrong (mappedID = " + mappedID + ", size of components = " + getComponents().length);
         return true;
      }

      // received selection of a radiobutton (index = indx)
      // update the data on the rest of radiobuttons
      //
      zRadioButton it = null;

      //System.out.println ("será este ? (me pregunto) " + getComponents() [indx]);
      for (int ii = 0; ii < getComponents().length; ii ++)
      {
         if (ii == indx) it = (zRadioButton) getComponents() [ii];

         // setting the selected to 0 into the ebs (eva model of the zwidget)
         ((zRadioButton) getComponents() [ii]).getHelper ().ebs ().setChecked (ii == indx);
      }
      if (it == null)
            widgetLogger.log().severe ("xyRadio::takePacket", "message radiobutton with index " + indx + " not found within " + getComponents().length + " elements");
      else signalAction (it.getHelper ().ebs ().getData (), it.getName ());
      return true;
   }

   //comm-radio method 2
   public void checkedToTrue (Component me)
   {
      widgetLogger.log().dbg (2, "xyRadio::checkedToTrue", "received message from button " + me.getName ());

      //System.out.println ("será este ? (me pregunto) " + getComponents() [indx]);
      for (int ii = 0; ii < getComponents().length; ii ++)
      {
         boolean isMe = (me != (Component) getComponents() [ii]);
         // setting the selected to 0 into the ebs (eva model of the zwidget)
         ((zRadioButton) getComponents() [ii]).getHelper ().ebs ().setChecked (isMe);
      }
   }
}
