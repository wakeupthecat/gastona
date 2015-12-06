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

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;
import de.elxala.langutil.graph.sysFonts;
import de.elxala.langutil.*;
import android.widget.*;
import android.content.Context;

/**
   zCheckBox : zWidget representing a GUI check box

   @see zWidgets
   @see javaHelper.gast

*/
public class zCheckBox extends CheckBox
                       implements MensakaTarget,
                                  CompoundButton.OnCheckedChangeListener,
                                  izWidget
{
   private basicAparato helper = null;

   //private String currentIconName = null;

   public zCheckBox (Context co)
   {
      // default constructor to allow instantiation using <javaClass of...>
      super (co);
      setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
   }

   public zCheckBox (Context co, String map_name, String slabel)
   {
      super (co);
      setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
      setText (slabel);
      setName (map_name);
   }

   private void build (String map_name)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));
      setOnCheckedChangeListener (this);
   }

   //-i- interface iWidget
   //
   public int getDefaultHeight () { return androidSysUtil.getHeightChars(1.2f); }
   public int getDefaultWidth () { return androidSysUtil.getWidthChars (3 + getText().length ()); }

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
   //-i-

   //-i- interface CompoundButton.OnCheckedChangeListener
   //
   public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
   {
      widgetLogger.log ().dbg (2, "zCheckBox", mName + " is checked " + isChecked);

      //NOTE!!! different as JCheckBox ! here isSelected has the old state!!!
      // WRONG! .... helper.ebs().setChecked (isSelected ());

      helper.ebs().setChecked (isChecked);
      helper.signalAction ();
   }
   //-i-

   //-i- interface MensakaTarget
   //
   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zCheckBox",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);
            setText (helper.decideLabel (getText ().toString ()));

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
            if (widgetLogger.updateContainerWarning (
                  "control", "zCheckBox",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);

            setSelected (helper.ebs ().isChecked ());
            setEnabled (helper.ebs ().getEnabled ());
            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            break;

         default:
            return false;
      }

      return true;
   }
   //-i-
}
