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
   //(o) WelcomeGastona_source_javaj_widgets (rg) zRadioButtonTable

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zRadioButtonTable
   <groupInfo>  button
   <javaClass>  javaj.widgets.zRadioButtonTable
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
      var              , in    , Eva name                   , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)
      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
      orientation      , in    , X / Y                      , //Horizontal or vertical (default) orientation
      titledBorder     , in    , text                       , //If given a titled border will be placed

<!?      visibleColumns   , in    , Eva line                 , //list of visible columns
      dbName           , in    , file name                , //If specified then it is the database name from which the data will be retrieved (default is the global temporary database)
      sqlSelect        , in    , sqlite select or pragma  , //If specified then the data will be retrieved from a database (see dbName)
      sqlExtraFilter   , in    , part of sqlite select    , //Filter that is added to the query
      subTableSelection, out   , Eva table                , //a table formed with the selected rows
      selected.COLNAME  , out   , String                   , //Contents of the column with name COLNAME of first selected row of the table. There is one variable select.COLNAME for each column in the table (inclusive the not visible columns)


   <messages>

      msg, in_out, desc

      data!   ,  in    , update data
      control!,  in    , update control
      select! ,  in    , //select records. Example "MSG, widget select!, #, 2, 4" select the records 2 and 4 (0 based) and "MSG, widget select!, name, Barcelona" selects the first record which name is "Barcelona"
              ,  out   , a radio button has been selected


   <examples>
      gastSample

      hello zRadioButtonTable

   <hello zRadioButtonTable>
      //#javaj#
      //
      //    <frames> F, Hello zRadioButtonTable
      //
      //    <layout of F>
      //       PANEL, X
      //       rgRadio, oConsole
      //
      //#data#
      //
      //    <rgRadio>
      //        label , value, selected
      //        Opt1  , 61   ,
      //        Opt2  , 29   ,
      //        Opt3  , 18   , 1
      //        Opt4  , 44   ,
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

      There are two possible approaches for the implementation
      1) use xyRadio (radio group panel) and zRadioButton
         then we have redundancy of messages that could be useful

      2) not use them but the native ButtonGroup and JRadioButton
         thus duplicating somehow the functionality from xyRadio and zRadioButton

      Here we implement the 2 approach
*/
public class zRadioButtonTable extends JPanel implements MensakaTarget
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
            helper.updateSelectedInTable ();
            if (!SILENT_SELECTION)
            helper.signalAction ();
         }
      }

      // public String label;
      public int mIndx = 0;
   }

   public zRadioButtonTable ()
   {
      // default constructor to allow instantiation using <javaClass of...>
      super ();
      // setBorder (BorderFactory.createBevelBorder (BevelBorder.RAISED));
      setBorder(new EmptyBorder(2, 2, 2, 2));
   }

   public zRadioButtonTable (String map_name)
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


   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.setDataControlAndModel (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.setDataControlAndModel (null, euData, pars);
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

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebsTable ().getVisible ();
            if (visible || isShowing ())
               setVisible  (visible);
            break;

         case widgetConsts.RX_SELECT_DATA:
            if (!helper.ebsTable().hasAll ())
                helper.log.err ("selectRadioIndex", "selectRadioIndex but no data or control!");
            else
            {
               //selecting elements
               //  do it silently (not producing a real selection)
               helper.doSelect (pars);
               SILENT_SELECTION = true;
               int [] arrsel = helper.getSelectedIndices ();
               int indx = arrsel.length > 0 ? arrsel[0]: -1;
               if (arrRadios != null && indx >= 0 && indx < arrRadios.length)
                  arrRadios[indx].setSelected (true);
               SILENT_SELECTION = false;
               helper.updateSelectedInTable ();
            }
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
         int selectIndx = -1;

         for (int ii = 0; ii < nradios; ii ++)
         {
            boolean existId    = tab.getColumnIndex ("id") >= 0;
            boolean existLab   = tab.getColumnIndex ("label") >= 0;
            boolean existSel   = tab.getColumnIndex ("selected") >= 0;
            String strLabel    = tab.getValue ("label", ii);
            String strId       = tab.getValue ("id", ii);
            String strSelected = tab.getValue ("selected", ii);

            arrRadios[ii] = new raButton(ii, existLab ? strLabel: existId ? strId: "r" + (ii + 1));

             // 1st) mechanism <widget selectedIndices>  2nd) selected by table
            boolean selbytable = (existSel && "1".equals (strSelected));
            if (helper.isIndexSelected (ii) || selbytable)
            {
               selectIndx = ii;
            }

            super.add (arrRadios[ii]);
            radioGroup.add ((AbstractButton) arrRadios[ii]);
         }
         if (selectIndx != -1)
            arrRadios[selectIndx].setSelected (true);
         selectRadioIndex (selectIndx);
         helper.updateSelectedInTable ();

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
