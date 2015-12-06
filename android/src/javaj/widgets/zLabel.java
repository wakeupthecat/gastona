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

import android.widget.*;
import android.os.Handler;
import android.os.Message;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.graph.sysFonts;

import javaj.*;
import javaj.widgets.basics.*;

import android.content.Context;

/**
   zLabel : zWidget representing a GUI label

   @see zWidgets
   @see javaHelper.gast

*/
public class zLabel extends TextView implements MensakaTarget, izWidget
{
   private basicAparato helper = null;

   private int fondo = 17170447; //android.R.color.darker_gray

   public zLabel (Context co)
   {
      super (co);
      setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
      setBackgroundColor(fondo);

      // default constructor to allow instantiation using <javaClass of...>
   }

   public zLabel (Context co, String map_name, String slabel)
   {
      super (co);
      setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
      setBackgroundColor(fondo);
      build  (map_name, slabel);
   }

   private void build (String map_name, String slabel)
   {
      super.setText (slabel);
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));
   }

   protected static final int HANDLER_OP_SETTEXT   = 0;
   protected static final int HANDLER_OP_ENABLE = 1;
   protected static final int HANDLER_OP_DISABLE = 2;
   protected static final int HANDLER_OP_VISIBLE = 3;
   protected static final int HANDLER_OP_INVISIBLE = 4;
   protected static final int HANDLER_OP_SETBACKCOLOR   = 5;

   protected final Handler UIhandler = new Handler() {
      public void handleMessage(Message msg)
      {
         int op = msg.getData().getInt("operation");
         String value = msg.getData().getString("value");
         
         switch (op)
         {
            case HANDLER_OP_SETTEXT:   setText  (value); break;
            case HANDLER_OP_VISIBLE:   setVisibility (android.view.View.VISIBLE); break;
            case HANDLER_OP_INVISIBLE: setVisibility (android.view.View.INVISIBLE); break;
            case HANDLER_OP_ENABLE:    setEnabled (true); break;
            case HANDLER_OP_DISABLE:   setEnabled (false); break;
            case HANDLER_OP_SETBACKCOLOR:   setBackgroundColor (stdlib.atoi (value)); break;
            default: break;
         }
      }
   };

   protected void attakWidget (int op, String value)
   {
      utilHandler.sendToWidget (UIhandler, op, value);
   }

   protected void attakWidget (int op)
   {
      attakWidget (op, "");
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
      build (map_name, map_name.substring(1));
   }

   //-i- interface MensakaTarget ---------------------------------------
   //
   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zLabel",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);
            attakWidget (HANDLER_OP_SETTEXT, helper.decideLabel (getText ().toString ()));
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "zLabel",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);
            if (helper.ebs ().getEnabled ())
                 attakWidget (HANDLER_OP_ENABLE);
            else attakWidget (HANDLER_OP_DISABLE);
            
            String bCol = helper.ebs ().getSimpleAttribute (helper.ebs ().CONTROL, "backColor", null);
            if (bCol != null && bCol.length () > 0)
               attakWidget (HANDLER_OP_SETBACKCOLOR, bCol);

            if (helper.ebs ().getVisible ())
                 attakWidget (HANDLER_OP_VISIBLE);
            else attakWidget (HANDLER_OP_INVISIBLE);
            break;

         default:
            return false;
      }

      return true;
   }
}
