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
   <name>       zCheckBoxTable
   <groupInfo>  button
   <javaClass>  javaj.widgets.zCheckBoxTable
   <prefix> kg
   <importance> 7
   <desc>       //Group of several check boxes group in one widget (rapid prefix 'kg')

   <help>
      //
      // This widget manages a complete check box group and it is a good alternative to use zCheckBox
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
      LABELVALUE.selected, out, String                    , //Returns 0 or 1 depending on if the check box identified by the label LABELVALUE is selected or not
      LABELVALUE.COLUMNONOFF, out, String                 , //Returns the value of the column named COLUMNONOFOn or COLUMNONOFOff depending on if the check box identified by the label LABELVALUE is selected or not

   <messages>

      msg, in_out, desc

      data!   ,  in    , update data
      control!,  in    , update control
      select! ,  in    , //select records. Example "MSG, widget select!, #, 2, 4" select the records 2 and 4 (0 based) and "MSG, widget select!, name, Barcelona" selects the first record which name is "Barcelona"
              ,  out   , a check box in the group has been changed


   <examples>
      gastSample

      checkBoxTable sample

   <checkBoxTable sample>
      //#javaj#
      //
      //    <frames> F, Hello zCheckBoxTable
      //
      //    <layout of F>
      //       PANEL, Y
      //       kgChecks, oConsole
      //
      //#data#
      //
      //    <kgChecks orientation> X
      //    <kgChecks>
      //        label , value1 , value0  , selected, desc1      , desc0
      //        Opt1  , 161    ,   -11   , 1       , is on!     , is off!
      //        Opt2  , 102    ,   -22   , 0       , encendida  , apagada
      //        Opt3  , 113    ,   -33   , 1       , an         , aus
      //        Opt4  , 144    ,   -44   , 0       , set la vie , je ne sais pas
      //
      //#listix#
      //
      //    <main> LSX, show
      //
      //    <-- kgChecks> LSX, show
      //
      //    <show>
      //       MSG, oConsole clear
      //       LOOP, VAR, kgChecks
      //           ,, //item @<label> is @<selected>, now value @<valueOnOff>, desc @<descOnOff>
      //       //
      //       //current data
      //       DUMP, data,, kgChecks
      //
      //    <show selected>
      //       CHECK, VAR, kgChecks subTableSelection
      //       LOOP, VAR, kgChecks subTableSelection
      //           ,, //item @<label> is @<selected>, now value @<value1> (@<value0>), desc @<desc1> (@<desc0>)
      //       //
      //
      //    <valueOnOff>   VALUE OF, value@<selected>
      //    <descOnOff>    VALUE OF, desc@<selected>



#**FIN_EVA#

*/


/**

   //(o) TODO/javaj/zCheckBoxTable implement individual selections as in sample

    campo    , significado

    id       , id para reconocer el check box, si no se dá se toma label, si tampoco automaticamente k1, k2 etc
    label    , lo que se ve (no se puede cambiar facilmente ...), si no existe se toma id, si tampoco k1, k2 etc
    selected , valor inicial + actualizarse con cada seleccion/des-seleccion

    NO! se puede implementar facilmente con (ver abajo)
    xxxxOn   , si el check esta activo se inserta en la variable <grupo id.xxxx>
               por ejemplo si seleccionamos la Opt3 se cargará el valor
                     <kgChecks Opt3.desc> //ist an

   label, desc0   , desc1, selected

   juan , meSiento, noMeSientoLasPiernas, 0
   pedro, estoyOn , estoyOff            , 1
*/
public class zCheckBoxTable extends JPanel implements MensakaTarget
{
   private tableAparato helper = null;
   private boolean SILENT_SELECTION = false;

   private kgButton [] arrChecks = null;

   class kgButton extends JCheckBox implements ActionListener
   {
      public kgButton (int index, String label)
      {
         mIndx = index;
         setText (label);
         addActionListener (this);
      }

      public void actionPerformed(ActionEvent ev)
      {
         selectCheckIndex (mIndx, isSelected ());
         helper.updateSelectedInTable ();
         if (!SILENT_SELECTION)
            helper.signalAction ();
      }

      // public String label;
      public int mIndx = 0;
   }

   public zCheckBoxTable ()
   {
      // default constructor to allow instantiation using <javaClass of...>
      super ();
      // setBorder (BorderFactory.createBevelBorder (BevelBorder.RAISED));
      setBorder(new EmptyBorder(2, 2, 2, 2));
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public zCheckBoxTable (String map_name)
   {
      super ();
      build (map_name);
      setBorder(new EmptyBorder(2, 2, 2, 2));
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
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
            if (visible && isShowing ())
               setVisible  (visible);
            break;

         case widgetConsts.RX_SELECT_DATA:
            //selecting elements
            //  do it silently (not producing a real selection)
            helper.doSelect (pars);
            SILENT_SELECTION = true;

            // clear all!
            for (int indx = 0; indx < arrChecks.length; indx ++)
               arrChecks[indx].setSelected (false);
            int [] arrsel = helper.getSelectedIndices ();
            for (int ii = 0; ii < arrsel.length; ii ++)
            {
               int indx = arrsel[ii];
               if (arrChecks != null && indx >= 0 && indx < arrChecks.length)
                  arrChecks[indx].setSelected (true);
            }
            SILENT_SELECTION = false;
            helper.updateSelectedInTable ();
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

         tableEvaDataEBS tab = helper.getRealTableObject ();
         if (tab == null)
         {
            helper.log.err ("tryAttackWidget", "Noncuentra datar de [" + getName () + "]!" + helper.ebsTable ().toString ());
            return;
         }
         int nCheks = tab.getTotalRecords ();

         arrChecks = new kgButton [nCheks];

         int [] arrSel = new int [nCheks];

         // do not select just the first one
         //
         // int selecIndx = (arr != null && arr.length > 0) ? arr[0]: -1;
         // selectCheckIndex (selecIndx, true);

         for (int ii = 0; ii < nCheks; ii ++)
         {
            boolean existId    = tab.getColumnIndex ("id") >= 0;
            boolean existLab   = tab.getColumnIndex ("label") >= 0;
            boolean existSel   = tab.getColumnIndex ("selected") >= 0;
            String strLabel    = tab.getValue ("label", ii);
            String strId       = tab.getValue ("id", ii);
            String strSelected = tab.getValue ("selected", ii);
            arrChecks[ii] = new kgButton(ii, existLab ? strLabel: existId ? strId: "k" + (ii + 1));

            boolean selbytable = (existSel && "1".equals (strSelected));

             // 1st) mechanism <widget selectedIndices>  2nd) selected by table
            if (helper.isIndexSelected (ii) || selbytable)
            {
               arrChecks[ii].setSelected (true);
               arrSel[ii] = ii;
            }
            else arrSel[ii] = -1;

            super.add (arrChecks[ii]);
         }
         // set all variables and subTableSelection for the selected items
         //
         helper.storeAllSelectedIndices (arrSel);
         helper.updateSelectedInTable ();

         String strOrient = helper.ebsTable ().getSimpleDataAttribute ("orientation");
         String strTitle  = helper.ebsTable ().getSimpleDataAttribute ("titledBorder");

         boolean horizontal = (strOrient != null) && (strOrient.equalsIgnoreCase("X") || strOrient.equalsIgnoreCase("H"));
         setLayout (new BoxLayout(this, (horizontal) ? BoxLayout.X_AXIS: BoxLayout.Y_AXIS));
         if (strTitle != null)
            setBorder (BorderFactory.createTitledBorder (strTitle));
      }
   }

   public void selectCheckIndex (int indx, boolean selOn)
   {
      if (helper == null) return;
      if (!helper.ebsTable().hasAll ())
      {
          helper.log.err ("selectCheckIndex", "selectCheckIndex but no data or control!");
          return;
      }

      helper.log.dbg (2, "selectCheckIndex", "button index = " + indx + " on = " + selOn);

      int nsel = 0;
      for (int ii = 0; ii < arrChecks.length; ii ++)
         nsel += (arrChecks[ii].isSelected ()) ? 1:0;

      int [] newSelIndx = new int [nsel];
      nsel = 0;
      for (int ii = 0; ii < arrChecks.length; ii ++)
      {
         if (arrChecks[ii].isSelected ())
            newSelIndx[nsel++] = ii;
      }

      helper.storeAllSelectedIndices (newSelIndx);
   }
}
