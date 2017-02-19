/*
package de.elxala.zWidgets
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.*;
import java.awt.event.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;
import javaj.widgets.table.*;
import javaj.widgets.table.util.*;


/*
   //(o) WelcomeGastona_source_javaj_widgets (c) zComboBox

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zComboBox
   <groupInfo>  table
   <javaClass>  javaj.widgets.zComboBox
   <importance> 7
   <desc>       //A combo box list with database capabilities (rapid prefix 'c')

   <help>

      //
      // Combo box has, like list and tables, the Table data model. This is a convenient way of
      // representing an item since we can represent more "dimensions" of each selection facilitating
      // the implementation of the logic. For example we might present to the user the options "Less",
      // "More" and "No limit" having these values the logic meaning for us of 10, 50 and 9000.
      // Then we simply build the data as the table
      //
      //       <cMyCombox>
      //          text        , limit
      //          "Less"      , 10
      //          "More"      , 50
      //          "No limit"  , 9000
      //
      // and when the user chooses the item "More" we have the limit onto the variable
      // @<cMyCombox selected.limit> (attribute selected.COLUMN).
      //
      // And talking about limitations, note that although the data can be retrieved from a database,
      // in this widget big selections are limited to about 1000 elements for some technical reasons.
      // In this widget "automatic database windowing" cannot be used as it is in zTable or zAsiste
      // widgets. So please use zTable or better zAsiste for big table results. Furthermore for a
      // combo box a huge list would not make much sense, specially for the user!
      //
      // Widget characteristics: "Common", "Table select" and "Table from database".
      //


   <prefix> c

   <attributes>
      name             , in_out, possibleValues             , desc

                       , in    , (Eva Table)              , //Contents of items
      visible          , in    , 0 / 1                    , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                    , //Value 0 to disable the widget
      var              , in    , Eva name                 , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

      visibleColumns   , in    , (Eva list)               , //Comma separated list of column names
      subTableSelection, out   , Eva table                , //a table formed with the selected rows
      selected.COLNAME , out   , String                   , //Contents of the column with name COLNAME of first selected row of the table. There is one variable select.COLNAME for each column in the table (inclusive the not visible columns)

      dbName           , in    , file name                , //If specified then it is the database name from which the data will be retrieved (default is the global temporary database)
      sqlSelect        , in    , sqlite select or pragma  , //If specified then the data will be retrieved from a database (see dbName)
      sqlExtraFilter   , in    , part of sqlite select    , //Filter that is added to the query

   <messages>

      msg, in_out, desc

              ,  out   , an Item has been selected
      data!   ,  in    , update data
      control!,  in    , update control


   <examples>
      gastSample

   <hello zComboBox>
      //#javaj#
      //
      //    <frames> F, Hello zComboBox
      //
      //    <layout of F>
      //          PANEL, X
      //          cCombo
      //
      //#data#
      //
      //    <cCombo visibleColumns> name
      //    <cCombo>
      //       name      , value
      //       Option A  , 1
      //       Option B  , 2
      //       No Option , 0
      //
      //#listix#
      //
      //    <-- cCombo>
      //       //You have chosen the item @<cCombo selected.name> (value @<cCombo selected.value>)
      //       //
      //

#**FIN_EVA#

*/

//
//   NOTE:
//         working with JComboBox is more complicated as with JList, we are forced to
//         use the ComboBoxModel mechanism because JComboBox doesn't have the method
//         setListData (Object [])
//


/**
   zComboBox : zWidget representing a GUI combo box

   @see zWidgets
   @see javaHelper.gast
*/
public class zComboBox extends JComboBox implements MensakaTarget
{
   private tableAparato helper = null;
   private boolean SILENT_SELECTION = false;

   public zComboBox ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zComboBox (String map_name)
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

      helper = new tableAparato (this, new tableEBS (map_name, null, null));

      //(o) javaj_widgets_combo pasando del intento de hacer el combo box editable
      setEditable (false);
      addActionListener (new actiona ()); // inner class
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

            setEnabled (helper.ebsTable ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebsTable ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);

            break;

         case widgetConsts.RX_SELECT_DATA:
            //selecting elements
            //  do it silently (not producing a real selection)
            helper.doSelect (pars);
            SILENT_SELECTION = true;
            trySelectIndex ();
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
         // shortest way to set the new model
         String [] arr = helper.getSwingListModel ().getAllRowsUpTo1024 ();

         JComboBox memo = new JComboBox (arr);
         setModel (memo.getModel ());

         trySelectIndex ();
      }
   }

   private void trySelectIndex ()
   {
      // set selection
      int [] indices = helper.getSelectedIndices ();
      if (indices.length > 0 && indices[0] < getItemCount ())
         setSelectedIndex (indices[0]);
      else
         //NOTE: Combo has always an index selected, take it from the combo component
         helper.storeAllSelectedIndices (new int [] { getSelectedIndex () });
   }

   private class actiona implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         if (SILENT_SELECTION) return;

         JComboBox cb = (JComboBox) e.getSource();

         // System.out.println ("(String) cb.getSelectedItem () " + (String) cb.getSelectedItem ());
         // System.out.println ("(String) cb.getSelectedIndex () " + cb.getSelectedIndex ());
         helper.storeAllSelectedIndices (new int [] { cb.getSelectedIndex () });
         helper.signalAction ();
      }
   }
}


