/*
library de.elxala
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

package org.gastona.cmds;

import android.widget.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.*;
import android.app.AlertDialog;
import android.widget.Toast;



import listix.*;
import listix.cmds.commandable;

import de.elxala.langutil.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

/**
   to allow sending mensaka messages from listix !

*/
public class CmdMsgBox implements commandable
{
   public static final int UNKNOWN_MESSAGE = 0;
   public static final int INFORMATION_MESSAGE = 1;
   public static final int WARNING_MESSAGE = 2;
   public static final int ERROR_MESSAGE = 3;
   public static final int TOAST_MESSAGE = 4;
   public static final int QUESTION_MESSAGE = 5;
   public static final int SELECT_MESSAGE = 6;
   public static final int LOADING_MESSAGE = 7;

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "MSGBOX",
          "BOX",
       };
   }

   public static void createToastada (final String msg)
   {
       androidSysUtil.getCurrentActivity().runOnUiThread(new Runnable() 
       {
           public void run() 
           {
               //Note: Maybe it could be used org.gastona.gastonaFlexActor.lastActivity here
               //      instead of the root Activity, but since the Toast seems to work as it is
               //      there is no reason to change it
               Toast.makeText(androidSysUtil.getMainActivity (), msg, Toast.LENGTH_SHORT).show();
           }
       });
   }

   public static void showAlertBox (final AlertDialog alert)
   {
       androidSysUtil.getCurrentActivity().runOnUiThread(new Runnable() 
       {
           public void run() 
           {
               alert.show ();
           }
       });
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      String type    = cmd.getArg(0);
      String message = cmd.getArg(1);
      String title   = cmd.getArg(2);

      int msgType = INFORMATION_MESSAGE;

      String [] buttonLabels   = new String [] { "Accept", "", "" };
      String [] buttonMessages = new String [] { "", "", "" };

      if (type.length() > 0)
         switch (type.charAt(0))
         {
            case 'E': msgType = ERROR_MESSAGE; break;
            case 'W': msgType = WARNING_MESSAGE; break;
            case 'I': msgType = INFORMATION_MESSAGE; break;
            case 'Q': msgType = QUESTION_MESSAGE; break;
            case 'T': msgType = TOAST_MESSAGE; break;
            case 'S': msgType = SELECT_MESSAGE; break;
            case 'L': msgType = LOADING_MESSAGE; break;
            default:
               break;
         }

      if (msgType == TOAST_MESSAGE)
      {
         createToastada (message);
         return 1;
      }

      if (msgType == QUESTION_MESSAGE)
      {
         buttonLabels   = cmd.takeOptionParameters ("LABELS");
         buttonMessages = cmd.takeOptionParameters ("MESSAGES");
      }

      if (msgType == SELECT_MESSAGE)
      {
         String tableName = cmd.getArg(1);
         if (tableName.length () == 0)
         {
            that.log().err ("BOX", "No table specified at BOX SELECT!");
            return 1;
         }

         Eva evaVals = cmd.getListix ().getVarEva (tableName);
         if (evaVals == null)
         {
            that.log().err ("BOX", "Table " + tableName + " for the BOX SELECT not found!");
            return 1;
         }
         if (evaVals.rows () < 2)
         {
            that.log().err ("BOX", "The table " + tableName + " for the BOX SELECT has no elements!");
            return 1;
         }

         lastListix = cmd.getListix ();
         lastSelectTable = evaVals;
         removeSelectItem ();

         that.log().dbg (2, "BOX", "SELECT with table " + tableName);
         CharSequence[] items = new CharSequence[evaVals.rows()-1];
         for (int ii = 0; ii < items.length; ii ++)
         {
            items[ii] = evaVals.getValue(ii+1, 0);
         }

         AlertDialog.Builder builder = new AlertDialog.Builder(androidSysUtil.getCurrentActivity());
         builder.setTitle(title);
         builder.setItems(items, new DialogInterface.OnClickListener()
         {
             public void onClick(DialogInterface dialog, int item)
             {
               selectItem (item);
             }
         });
         showAlertBox (builder.create());
         return 1;
      }

      if (msgType == LOADING_MESSAGE)
      {
         // ---- it does not work --------------
         that.log().dbg (2, "BOX", "LOADING ....");
         android.app.ProgressDialog dialog = android.app.ProgressDialog.show(androidSysUtil.getCurrentActivity(), "", "Loading. Please wait...", true);
         dialog.show ();
         try
         {
            Thread.currentThread().sleep (2000);
         }
         catch (Exception e) {}
         dialog.hide ();
         return 1;
         // ---- it does not work --------------
      }

      alerta (msgType, title, message, buttonLabels, buttonMessages);
      return 1;
   }

   public static void alerta (int type, String title, String text)
   {
      alerta (type, title, text, new String [] { "Accept" }, new String [0]);
   }

   public static void alerta (int type, String title, String text, final String [] arrLabels, final String [] arrMessages)
   {
      String sIcon = null;
      switch (type)
      {
         case ERROR_MESSAGE:       sIcon = "error.png"; break;
         case WARNING_MESSAGE:     sIcon = "warning.png"; break;
         case INFORMATION_MESSAGE: sIcon = "information.png"; break;
         case QUESTION_MESSAGE:    sIcon = "question.png"; break;
         case TOAST_MESSAGE:
            {
               createToastada (text);
               // Toast.makeText(androidSysUtil.getMainActivity (), text, Toast.LENGTH_SHORT).show();
               return;
            }
         default:
            javaj.widgets.basics.widgetLogger.log().severe ("cmdMsgBox::alerta", "This type of message (" + type + ") is not supported in this method!");
            break;
      }

      AlertDialog aldi = new AlertDialog.Builder (androidSysUtil.getCurrentActivity()).create ();
      if (sIcon != null)
         aldi.setIcon (javaLoad.getSomeHowDrawable (sIcon, true));

      DialogInterface.OnClickListener liso =
               new DialogInterface.OnClickListener ()
               {
                  public void onClick(DialogInterface dialog, int which)
                  {
                     if (arrMessages != null)
                     {
                        if (which == AlertDialog.BUTTON_POSITIVE && arrMessages.length > 0 && arrMessages [0].length () > 0) Mensaka.sendPacket (arrMessages [0], null);
                        if (which == AlertDialog.BUTTON_NEGATIVE && arrMessages.length > 1 && arrMessages [1].length () > 0) Mensaka.sendPacket (arrMessages [1], null);
                        if (which == AlertDialog.BUTTON_NEUTRAL  && arrMessages.length > 2 && arrMessages [2].length () > 0) Mensaka.sendPacket (arrMessages [2], null);
                     }
                  }
               };

      aldi.setTitle(title);
      aldi.setMessage(text);
      if (arrLabels == null)
         aldi.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", liso);
      else
      {
         if (arrLabels.length > 0 && arrLabels[0] != null && arrLabels[0].length () > 0) aldi.setButton(AlertDialog.BUTTON_POSITIVE, arrLabels[0], liso);
         if (arrLabels.length > 1 && arrLabels[1] != null && arrLabels[1].length () > 0) aldi.setButton(AlertDialog.BUTTON_NEGATIVE, arrLabels[1], liso);
         if (arrLabels.length > 2 && arrLabels[2] != null && arrLabels[2].length () > 0) aldi.setButton(AlertDialog.BUTTON_NEUTRAL , arrLabels[2], liso);
      }

      showAlertBox (aldi);
      // aldi.show ();
   }

   private Eva lastSelectTable = null;
   private listix lastListix = null;

   protected void removeSelectItem ()
   {
      if (lastSelectTable == null) return;
      if (lastListix == null) return;

      for (int cc = 0; cc < lastSelectTable.cols (0); cc ++)
      {
         String nameSelected = lastSelectTable.getName () + " selected." + lastSelectTable.getValue(0, cc);
         lastListix.getGlobalData ().remove (nameSelected);
      }
   }

   protected void selectItem (int indx)
   {
      if (lastSelectTable == null) return;
      if (lastListix == null) return;

      for (int cc = 0; cc < lastSelectTable.cols (0); cc ++)
      {
         Eva va = lastListix.getSomeHowVarEva (lastSelectTable.getName () + " selected." + lastSelectTable.getValue(0, cc));
         va.setValueVar (lastSelectTable.getValue(indx + 1, cc));
      }
      Mensaka.sendPacket (lastSelectTable.getName (), null);
   }
}
