/*
package javaj.widgets
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
*/
public class zList extends ListView implements MensakaTarget, OnItemClickListener, izWidget
{
   private tableAparato helper = null;
   //try scroll, doesnot work!
   //   private xyScrollPane  scroll = null;

   public zList (Context co)
   {
      // default constructor to allow instantiation using <javaClass of...>
      super (co);
   }

   public zList (Context co, String map_name)
   {
      super (co);
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

   public int getDefaultHeight () { return androidSysUtil.getHeightChars(5.f); }
   public int getDefaultWidth () { return androidSysUtil.getWidthChars (80.f); }

   public void build (String map_name)
   {
      helper = new tableAparato (this, new tableEBS (map_name, null, null));

      setOnItemClickListener(this);

      // decorate selection
      //
      //setSelectionForeground (helper.ebsTable ().getSelFontColor ());
      //setSelectionBackground (helper.ebsTable ().getSelNormalColor ());
   }

   private int [] misSelectos = new int[0];

   public void setSelectedIndices (int [] ellos)
   {
      misSelectos = ellos;
   }

   public int [] getSelectedIndices ()
   {
      return misSelectos;
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zList",
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
                  "control", "zList",
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

            // set the selected indices
            setSelectedIndices (helper.getSelectedIndices ());

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
         setAdapter ((ListAdapter) helper.getAndroidListModel ());
      }
   }


   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
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
}
