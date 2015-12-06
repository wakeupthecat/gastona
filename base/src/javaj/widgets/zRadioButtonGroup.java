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

import de.elxala.Eva.abstractTable.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.graph.*;
import de.elxala.langutil.*;

import javaj.widgets.basics.*;
import javaj.widgets.table.*;

import java.awt.event.*;
import javax.swing.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;
import javaj.widgets.panels.*;


import java.awt.Component;
import javax.swing.*;
import javax.swing.border.*;

import javaj.widgets.basics.*;
import javaj.widgets.zRadioButton;

import de.elxala.Eva.*;
import de.elxala.mensaka.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (rg) zRadioButtonGroup

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zRadioButtonGroup
   <groupInfo>  button
   <javaClass>  javaj.widgets.zRadioButtonGroup
   <prefix> rg
   <importance> 7
   <desc>       //Radio group all in one widget (rapid prefix 'rg')

   <help>
      //
      // This widget manages a complete radio button group and it is a good alternative to use zRadioButton
      // and the layout RADIO. The labels as well as any other convenient data associated with the radio
      // buttons of the group are given in a table.
      //
      // Widget characteristics: "Common".
      //

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , text                       , //Caption text of the radio button
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      orientation      , in    , X / Y                      , //Horizontal or vertical (default) orientation
      titledBorder     , in    , text                       , //If given a titled border will be placed

   <messages>

      msg, in_out, desc

      data!   ,  in    , update data
      control!,  in    , update control
      select! ,  in    , parameters : #, index
              ,  out   , a radio button has been selected


   <examples>
      gastSample

      <!data4Tester
      hello zRadioButtonGroup

   <data4Tester>
      //#data#
      //
      //    <rgName>
      //       label
      //       First button
      //       Second button
      //

   <hello zRadioButtonGroup>
      //#javaj#
      //
      //    <frames> F, Hello zRadioButtonGroup
      //
      //    <layout of F>
      //       PANEL, X
      //       rgRadio, oConsole
      //
      //#data#
      //
      //    <rgRadio>
      //        label , value
      //        Opt1  , 61
      //        Opt2  , 29
      //        Opt3  , 18
      //        Opt4  , 44
      //
      //#listix#
      //
      //    <-- rgRadio>
      //       //selected radiobutton @<rgRadio selected.label> with associated value @<rgRadio selected.value>
      //       //
      //



#**FIN_EVA#

*/


/**
   Notes: 07.06.2011 21:51

      There are two possible aproaches for the implementation
      1) use xyRadio (radio group panel) and zRadioButton
         then we have redundancy of messages that could be useful

      2) not use them but the native ButtonGroup and JRadioButton
         thus duplicating somehow the functionality from xyRadio and zRadioButton

      Here we implement the 2 aproach
*/
public class zRadioButtonGroup extends JPanel implements MensakaTarget
{
   private ButtonGroup radioGroup = new ButtonGroup ();

   private tableAparato helper = null;
   private boolean SILENT_SELECTION = false;

   private raButton [] arrRadios = null;

   class raButton extends JRadioButton implements ActionListener
   {
      public raButton (int index, String label)
      {
         mIndx = index;
         setText (label);
         addActionListener (this);
      }

      public void actionPerformed(ActionEvent ev)
      {
         if (isSelected ())
         {
            selectRadioIndex (mIndx);
            helper.signalAction ();
         }
      }

      // public String label;
      public int mIndx = 0;
   }

   public zRadioButtonGroup ()
   {
      // default constructor to allow instantiation using <javaClass of...>
      super ();
      // setBorder (BorderFactory.createBevelBorder (BevelBorder.RAISED));
      setBorder(new EmptyBorder(2, 2, 2, 2));
   }

   public zRadioButtonGroup (String map_name)
   {
      super ();
      build (map_name);
      setBorder(new EmptyBorder(2, 2, 2, 2));
   }

   private void build (String map_name)
   {
      super.setName (map_name);
      helper = new tableAparato ((MensakaTarget) this, new tableEBS (map_name, null, null));
   }


   public boolean takePacket (int mappedID, EvaUnit euData)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.setNameDataAndControl (null, euData, null);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.setNameDataAndControl (null, null, euData);
            if (helper.ebsTable().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            // set the selected indices
            //setSelectedIndices (helper.getSelectedIndices ());


            //?? helper.storeAllSelectedIndices ();
            //?? setSelected (helper.ebsTable ().isChecked ());
            //?? if (myManager != null && helper.ebsTable ().isChecked ())
            //??    myManager.checkedToTrue(this);
            setEnabled (helper.ebsTable ().getEnabled ());
            if (isShowing ())
               setVisible (helper.ebsTable ().getVisible ());
            break;

         case widgetConsts.RX_SELECT_DATA:
            //selecting elements
            //  do it silently (not producing a real selection)
            helper.doSelect (Mensaka.getCurrentPacketParameters ());
            SILENT_SELECTION = true;
            int [] arrsel = helper.getSelectedIndices ();
            if (arrsel.length > 0 && arrRadios != null && arrsel[0] < arrRadios.length)
               arrRadios[arrsel[0]].setSelected (true);
            SILENT_SELECTION = false;
            break;

         default:
            return false;
      }

      return true;
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsTable().hasAll ())
      {
         removeAll();
         radioGroup = new ButtonGroup ();

         tableEvaDataEBS tab = helper.getRealTableObject ();
         if (tab == null)
         {
            helper.log.err ("tryAttackWidget", "Noncuentra datar de [" + getName () + "]!" + helper.ebsTable ().toString ());
            return;
         }
         int nradios = tab.getTotalRecords ();

         arrRadios = new raButton [nradios];

         int [] arr = helper.getSelectedIndices ();
         int selecIndx = (arr != null && arr.length > 0) ? arr[0]: -1;
         selectRadioIndex (selecIndx);

         for (int ii = 0; ii < nradios; ii ++)
         {
            arrRadios[ii] = new raButton(ii, tab.getValue ("label", ii));
            if (ii == selecIndx) arrRadios[ii].setSelected (true);
            super.add (arrRadios[ii]);
            radioGroup.add ((AbstractButton) arrRadios[ii]);
         }

         String strOrient = helper.ebsTable ().getSimpleDataAttribute ("orientation");
         String strTitle  = helper.ebsTable ().getSimpleDataAttribute ("titledBorder");

         boolean horizontal = (strOrient  != null) && (strOrient .equalsIgnoreCase("X") || strOrient .equalsIgnoreCase("H"));
         setLayout (new BoxLayout(this, (horizontal) ? BoxLayout.X_AXIS: BoxLayout.Y_AXIS));
         if (strTitle != null)
            setBorder (BorderFactory.createTitledBorder (strTitle));
      }
   }

   public void selectRadioIndex (int indx)
   {
      if (helper == null) return;
      if (!helper.ebsTable().hasAll ())
      {
          helper.log.err ("selectRadioIndex", "selectRadioIndex but no data or control!");
          return;
      }

      helper.log.dbg (2, "selectRadioIndex", "button index = " + indx);
      if (indx == -1)
         helper.storeAllSelectedIndices (new int [] {});
      else
         helper.storeAllSelectedIndices (new int [] { indx });
   }
}
