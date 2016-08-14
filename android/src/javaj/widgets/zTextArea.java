/*
package de.elxala.zWidgets
Copyright (C) 2016 Alejandro Xalabarder Aulet

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

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.graph.sysFonts;

import javaj.widgets.basics.*;
import javaj.widgets.text.*;

import javaj.*;
import android.os.Handler;
import android.os.Message;

/**
*/
public class zTextArea extends EditText
                  implements MensakaTarget, izWidget
{
   private textAparato helper = null;

   public zTextArea (Context co)
   {
      super(co);
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zTextArea (Context co, String map_name)
   {
      super(co);
      setName (map_name);
   }

   private void build (String map_name)
   {
      setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
      setHorizontallyScrolling(true);

      helper = new textAparato (this, new smallTextEBS (map_name, null, null));

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
   public int getDefaultHeight () { return androidSysUtil.getHeightChars(5.0f); }
   public int getDefaultWidth () { return androidSysUtil.getWidthChars (30); }

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

   // (see note in zConsole Handler)
   //
   protected final Handler UIhandler = new Handler() {
      public void handleMessage(Message msg)
      {
         int op = msg.getData().getInt("operation");
         String value = msg.getData().getString("value");

         switch (op)
         {
            case HANDLER_OP_SETTEXT:   setText (value); break;
            case HANDLER_OP_APPEND:    append  (value); break;
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
            if (!widgetLogger.updateContainerWarning ("data", "zTextArea",
                                             helper.ebsText().getName (),
                                             helper.ebsText().getData (),
                                             euData))
            {
               helper.ebsText ().setDataControlAttributes (euData, null, pars);
               tryAttackWidget ();
            }
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (!widgetLogger.updateContainerWarning ("control", "zTextArea",
                                             helper.ebsText().getName (),
                                             helper.ebsText().getControl (),
                                             euData))
            {
               helper.ebsText ().setDataControlAttributes (null, euData, pars);
               if (helper.ebsText().firstTimeHavingDataAndControl ())
               {
                  tryAttackWidget ();
               }

               updateControl ();
            }
            break;

         case widgetConsts.RX_REVERT_DATA:
            if (helper.getIsDirty () && helper.ebsText().hasData ())
            {
               helper.ebsText ().setText (getText ().toString ());
               changeDirty (false);
            }
            break;

         case textAparato.RX_COMMNAD_LOAD:
            commandLoad (pars);
            break;

         case textAparato.RX_COMMNAD_SAVE:
            //if (helper.getIsDirty ())
            {
               helper.ebsText ().setText (getText ().toString ());
               changeDirty (false);
            }
            commandSave (pars);
            break;

         case textAparato.RX_COMMNAD_CLEAR:
            helper.ebsText ().setText ("");
            attakWidget (HANDLER_OP_SETTEXT, "");
            changeDirty (false);
            break;

         // El problema para incorporar estos comandos es el posible diferente estado
         // entre el modelo : helper.ebsText () que se guarda en la eva y
         // el texto del widget

         case textAparato.RX_COMMNAD_INSERTTEXT:
            {
               StringBuffer str = new StringBuffer ();
               for (int ii = 0; ii < pars.length; ii ++)
                  str.append (pars[ii]);
               helper.ebsText ().setText (getText ().toString () + str.toString ());
               attakWidget (HANDLER_OP_SETTEXT, helper.ebsText ().getText ());
            }
            break;

         case textAparato.RX_COMMNAD_NEWLINE:
            {
               StringBuffer str = new StringBuffer ();
               for (int ii = 0; ii < pars.length; ii ++)
                  str.append ("\n" + pars[ii]);

               helper.ebsText ().setText (getText ().toString () + str.toString ());
               attakWidget (HANDLER_OP_SETTEXT, helper.ebsText ().getText ());
            }
            break;

         default:
            return false;
      }

      return true;
   }
   //-i- -------------------------------------------------------


   private void updateControl ()
   {
      helper.updateControl (this);
      boolean wrap = helper.ebsText ().getIsWrapLines ();
      setHorizontallyScrolling (!wrap);
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsText().hasAll ())
      {
         attakWidget (HANDLER_OP_SETTEXT, helper.ebsText ().getText ());

         changeDirty (false);
      }
   }

   private void commandLoad (String [] params)
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ())
      {
         helper.log.err ("zTextArea.commandLoad", "widget has no data or control. Message [" + helper.ebsText().evaName (helper.ebsText().sMSG_LOAD) + "] ignored");
         return;
      }

      // DETECT fileName in parameters
      if (params != null && params.length > 0)
      {
         String fName = params[0];
         if (params.length > 1)
            helper.log.warn ("zTextArea.commandLoad", "too much arguments only first \"" +  fName + "\" will be used");
         helper.ebsText ().setFileName (fName);
      }

      helper.ebsText ().setText ("");
      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null)
      {
         helper.log.err ("zTextArea.commandLoad", "widget " +  helper.ebsText().getName() + " has no attribute fileName. Message load ignored");
         return;
      }
      if (fileName.length () == 0) return;

      String [] lines = TextFile.readFile (fileName);
      if (lines == null)
      {
         // the file does not exist
         //(o) TODO_widgets_text use a new control variable "fileStatus" or "error" to indicate the error
         //helper.ebsText ().err ("reading from file " + fileName);
         return;
      }

      StringBuffer alltext = new StringBuffer ();
      for (int ii = 0; ii < lines.length; ii ++)
      {
         alltext.append (lines[ii] + "\n");
      }
      helper.ebsText ().setText (alltext.toString ());
      tryAttackWidget ();
   }

   private void commandSave (String [] params)
   {
      // has data and control ?
      if (! helper.ebsText().hasAll ())
      {
         helper.log.err ("zTextArea.commandSave", "widget has no data or control. Message [" + helper.ebsText().evaName (helper.ebsText().sMSG_SAVE) + "] ignored");
         return;
      }

      // DETECT fileName in parameters
      if (params != null && params.length > 0)
      {
         String fName = params[0];
         if (params.length > 1)
            helper.log.warn ("zTextArea.commandSave", "too much arguments only first \"" +  fName + "\" will be used");
         helper.ebsText ().setFileName (fName);
      }

      // has fileName ?
      String fileName = helper.ebsText ().getFileName ();
      if (fileName == null || fileName.length () == 0)
      {
         helper.log.err ("zTextArea.commandSave", "widget " +  helper.ebsText().getName() + " has " +
                         ((fileName == null) ? "no attribute fileName": ("no valid fileName [" + fileName + "]")) +
                         ". Message save ignored");
         return;
      }

      boolean ok = TextFile.writeFile (fileName, helper.ebsText ().getText ());

      helper.log.dbg (2, "zTextArea.commandSave", "writing into the file " + fileName);
      if (! ok)
      {
         helper.log.err ("zTextArea.commandSave", "error writing into the file " + fileName);
      }
   }

   // =============== implementing DocumentListener
   //

   private void changeDirty (boolean dirty)
   {
      //(o) Android text background sin cambio de color (sino quita los marcos del texto)

      if (helper.setIsDirty (dirty))
      {
      //   setBackgroundColor ((int) helper.ebsText ().getBackgroundColor ());
      }
   }
}
