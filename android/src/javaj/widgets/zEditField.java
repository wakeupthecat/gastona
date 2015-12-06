/*
package de.elxala.zWidgets
Copyright (C) 2005-2010 Alejandro Xalabarder Aulet

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

import android.widget.EditText;
import android.content.Context;
import android.text.TextWatcher;
import android.text.Editable;

import javaj.*;
import android.os.Handler;
import android.os.Message;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

import javaj.widgets.basics.*;
import javaj.widgets.text.*;
/**
   zEditField : zWidget representing a GUI EditField

   @see zWidgets
   @see javaHelper.gast

*/
public class zEditField extends EditText
                        implements MensakaTarget, izWidget
{
   private textAparato helper = null;

   public zEditField (Context co)
   {
      // default constructor to allow instantiation using <javaClass of...>
      super (co);
   }

   public zEditField (Context co, String map_name)
   {
      super (co);
      setName (map_name);
   }

   private void build (String map_name)
   {
      this.setLines (1);
      setText ("------------------------------");
      helper = new textAparato (this, new textEBS (map_name, null, null));

      addTextChangedListener(new TextWatcher()
         {
            public void afterTextChanged(Editable s)
            {
               changeDirty (true);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
         }
         );
   }

   //-i- interface iWidget ---------------------------------------
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

   protected static final int HANDLER_OP_SETTEXT   = 0;
   protected static final int HANDLER_OP_APPEND    = 1;

   protected final Handler UIhandler = new Handler() {
      public void handleMessage(Message msg)
      {
         int op = msg.getData().getInt("operation");
         String value = msg.getData().getString("value");

         switch (op)
         {
            case HANDLER_OP_APPEND:   append  (value); break;
            case HANDLER_OP_SETTEXT:  setText (value); break;
            default: break;
         }
      }
   };

   protected void attakWidget (int op, String value)
   {
      utilHandler.sendToWidget (UIhandler, op, value);
   }

   //-i- interface MensakaTarget ---------------------------------------
   //
   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zEditField",
                  helper.ebsText().getName (),
                  helper.ebsText().getData (),
                  euData)
               )
               return true;

            helper.ebsText ().setDataControlAttributes (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "zEditField",
                  helper.ebsText().getName (),
                  helper.ebsText().getControl (),
                  euData)
               )
               return true;

            helper.ebsText ().setDataControlAttributes (null, euData, pars);
            if (helper.ebsText().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            helper.updateControl (this);
            break;

         case widgetConsts.RX_REVERT_DATA:
            if (helper.getIsDirty () && helper.ebsText().hasData ())
            {
               helper.ebsText ().setText (getText().toString());
               changeDirty (false);
            }
            break;

         default:
            return false;
      }

      return true;
   }
   //-i- -------------------------------------------------------

   private void tryAttackWidget ()
   {
      if (helper.ebsText().hasAll ())
      {
         attakWidget (HANDLER_OP_SETTEXT, helper.ebsText ().getText ());
         changeDirty (false);
      }
   }

//   public void onEnter (...)
//      {
//      helper.signalAction ();
//      }

   // =============== implementing DocumentListener
   //


   private void changeDirty (boolean dirty)
   {
      //(o) Android text background sin cambio de color (sino quita los marcos del texto)

      if (helper.setIsDirty (dirty))
      {
      //   setBackgroundColor (helper.ebsText ().getBackgroundColor ());
      }
   }
}
