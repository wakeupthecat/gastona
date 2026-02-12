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

import android.widget.*;
import android.view.View;
import android.content.Context;
import android.view.View.OnClickListener;

import de.elxala.langutil.androidSysUtil.*;
import de.elxala.Eva.abstractTable.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.graph.*;
import de.elxala.langutil.*;

import javaj.widgets.basics.*;
import javaj.widgets.table.*;

/**
*/
public class zRadioButtons extends RadioGroup implements MensakaTarget, izWidget, RadioGroup.OnCheckedChangeListener
{
   private tableAparato helper = null;

   // to find the checked radio button faster
   //
   private int radioIDs [] = null;

   public zRadioButtons (Context co)
   {
      super (co);

      //NOTA: esto de LayoutParams parece que no hace nada de nada, era solo por probar
      this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
   }

   public zRadioButtons (Context co, String map_name)
   {
      super (co);
      setName (map_name);

      //NOTA: esto de LayoutParams parece que no hace nada de nada, era solo por probar
      this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
   }

   //-i- interface iWidget
   //
   public int getDefaultHeight ()
   {
      int nrows = 1;
      if (helper != null && helper.ebsTable().hasData ())
      {
         tableEvaDataEBS tab = helper.getRealTableObject ();
         nrows = tab == null ? 4: tab.getTotalRecords ();

         // /*----*/helper.log.warn ("zRadioButtons", "ME ESIGEN EL NUMERAL " + nrows);
      }
      return nrows * androidSysUtil.getHeightChars(2f);
   }

   public int getDefaultWidth () { return androidSysUtil.getWidthChars (100); }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String map_name)
   {
      mName = map_name;
      construct (map_name);
   }

   //-i-

   private void construct (String map_name)
   {
      helper = new tableAparato (this, new tableEBS (map_name, null, null));
      setOnCheckedChangeListener   (this);// yo mismo
   }


   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (!widgetLogger.updateContainerWarning ("data", "zRadioButtons",
                                             helper.ebsTable().getName (),
                                             helper.ebsTable().getData (),
                                             euData))
            {
               helper.setDataControlAndModel (euData, null, pars);
               tryAttackWidget ();
            }
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (!widgetLogger.updateContainerWarning ("control", "zRadioButtons",
                                             helper.ebsTable().getName (),
                                             helper.ebsTable().getControl (),
                                             euData))
            {
               helper.setDataControlAndModel (null, euData, pars);
               tryAttackWidget ();

               //?? helper.storeAllSelectedIndices ();
               //?? setSelected (helper.ebs ().isChecked ());
               //?? if (myManager != null && helper.ebs ().isChecked ())
               //??    myManager.checkedToTrue(this);
               setEnabled (helper.ebsTable ().getEnabled ());
               setVisibility (helper.ebsTable ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
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
         removeAllViews();

         tableEvaDataEBS tab = helper.getRealTableObject ();
         if (tab == null)
         {
            helper.log.err ("tryAttackWidget", "Noncuentra datar de [" + getName () + "]!" + helper.ebsTable ().toString ());
            return;
         }
         int nradios = tab.getTotalRecords ();

         radioIDs = new int [nradios];
         for (int ii = 0; ii < nradios; ii ++)
         {
            RadioButton radio = new RadioButton(getContext ());

            // ACHTUNG! hier getId () gibt's -1 zurueck !!! ??? w i e  a u c h  i m m e r!
            //radioIDs [ii] = radio.getId ();

            radio.setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
            radio.setText (tab.getValue ("label", ii));
            //!!!!! radio.setOnClickListener(this);

            //NOTA: esto de LayoutParams parece que no hace nada de nada, era solo por probar
            LinearLayout.LayoutParams lapa = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);

            addView (radio, ii, lapa);

            radioIDs [ii] = radio.getId ();
         }
      }
   }

   public void onCheckedChanged (RadioGroup group, int checkedId)
   {
      if (helper == null || radioIDs == null) return;
      if (helper.ebsTable().getData () == null)
      {
          helper.log.err ("onClick", "onClick but no data!");
          return;
      }

      int indxSelected = -1;
      if (checkedId != -1)
         for (int ii = 0; ii < radioIDs.length; ii ++)
            if (checkedId == radioIDs[ii])
            {
               indxSelected = ii;
               break;
            }

      helper.log.dbg (2, "onCheckedChanged", "checkedId = " + checkedId + " selectedIndx = " + indxSelected);
      selectRadioButton (indxSelected);
   }

   public void selectRadioButton (int indx)
   {
      if (indx == -1)
         helper.storeAllSelectedIndices (new int [] {});
      else
         if (helper.storeAllSelectedIndices (new int [] { indx }))
         {
            //(o) javaj_widgets_signals_zList Signal RowSelected (alias Action)
            //
            helper.signalAction ();
         }
   }
}
