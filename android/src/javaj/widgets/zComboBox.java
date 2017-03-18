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

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.*;
import android.widget.AdapterView.*;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.mensaka.*;

import javaj.widgets.basics.*;
import javaj.widgets.table.*;


/**
   zComboBox : zWidget representing a GUI combo box

   @see zWidgets
   @see javaHelper.gast
*/
public class zComboBox extends Spinner implements MensakaTarget, OnItemSelectedListener, izWidget
{
   private tableAparato helper = null;

   public zComboBox (Context co)
   {
      // default constructor to allow instantiation using <javaClass of...>
      super (co);
   }

   public zComboBox (Context co, String map_name)
   {
      super (co);
      //setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
      setName (map_name);
   }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String map_name)
   {
      mName = map_name;
      build (map_name);
   }

   public int getDefaultHeight () { return androidSysUtil.getHeightChars(1.f); }
   public int getDefaultWidth () { return androidSysUtil.getWidthChars (80.f); }


   private void build (String map_name)
   {
      helper = new tableAparato (this, new tableEBS (map_name, null, null));

      setOnItemSelectedListener(this);
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zComboBox",
                  helper.ebsTable().getName (),
                  helper.ebsTable().getData (),
                  euData)
               )
               return true;

            helper.setDataControlAndModel (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "zComboBox",
                  helper.ebsTable().getName (),
                  helper.ebsTable().getControl (),
                  euData)
               )
               return true;

            helper.setDataControlAndModel (null, euData, pars);
            if (helper.ebsTable().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            setEnabled (helper.ebsTable ().getEnabled ());
            setVisibility (helper.ebsTable ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            trySelectIndex ();
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
         //getSelectionModel ().clearSelection ();

         setAdapter ((SpinnerAdapter) helper.getAndroidSpinnerModel ());
         //setCellRenderer(new MyCellRenderer());
      }
      trySelectIndex ();
   }

   private int miSelecto = 0;

   public void setSelectedIndex (int indx)
   {
      miSelecto = indx;
   }

   public int getSelectedIndex ()
   {
      return miSelecto;
   }

   private void trySelectIndex ()
   {
      if (helper == null || !helper.ebsTable().hasAll ()) return;

      // set selection
      int [] indices = helper.getSelectedIndices ();
      if (indices.length > 0 && indices[0] < helper.getRealTableObject ().getTotalRecords ())
         setSelectedIndex (indices[0]);
      else
         //NOTE: Combo has always an index selected, take it from the combo component
         helper.storeAllSelectedIndices (new int [] { getSelectedIndex () });
   }

   public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
   {
      if (helper == null) return;
      if (helper.ebsTable().getData () == null)
      {
          helper.log.err ("onItemClick", "onItemClick but no data!");
          return;
      }
      if (helper.storeAllSelectedIndices (new int [] { position }))
      {
         //(o) javaj_widgets_signals_zList Signal RowSelected (alias Action)
         //
         helper.signalAction ();
      }
   }

   public void onNothingSelected(AdapterView parent)
   {
        // Do nothing.
   }
}


