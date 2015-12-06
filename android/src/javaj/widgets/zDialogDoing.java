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

import android.widget.Button;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.langutil.graph.sysFonts;

import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.kits.*;
import android.content.Context;
import android.view.View;
import android.graphics.drawable.*;
import android.graphics.Canvas;

import android.app.ProgressDialog;

/**
   zDialogDoing : javaj Android dialog cancelable progress zWidget representing a GUI button
*/
public class zDialogDoing implements MensakaTarget
{
   public ProgressDialog theDialog = null;

   public static final int RX_SET_DOING = 10;
   public static final int RX_END_DOING = 11;
   
   public zDialogDoing ()
   {
      Mensaka.subscribe (this, RX_SET_DOING, ":gastona javaj DOING");
      Mensaka.subscribe (this, RX_END_DOING, ":gastona javaj STOP DOING");
   }

   // new Thread(new Runnable() {  
   //    @Override
   //   public void run() {
   //         // TODO Auto-generated method stub
   //         try
   //         {
   //               Thread.sleep(5000);
   //         }catch(Exception e){}
   //         myPd_ring.dismiss();
   //   }

   // public zButton (Context co, String map_name, String slabel)
   // {
      // super (co);
      // setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
      // setText (slabel);
      // setName (map_name);
   // }

   /*
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
   */

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      System.out.println ("OSTIASSSS QUE PASAL!!!!");
      switch (mappedID)
      {
         case RX_SET_DOING:
            {
               System.out.println ("ME SET DOING A OSTIAS!");
               // MSG, javaj.doing, message, title
               if (theDialog != null) theDialog.dismiss ();
               theDialog = null;
               
               String [] doingPar = pars;
               String message = doingPar.length > 0 ? doingPar[0]: "doing ...";
               String title = doingPar.length > 1 ? doingPar[1]: "";
               theDialog = ProgressDialog.show(androidSysUtil.getMainAppContext (), title, message, true);
               theDialog.setCancelable (true);
            }
            break;
            
         case RX_END_DOING:
            {
               System.out.println ("ME SET STOP DOING A OSTIAS!");
               if (theDialog != null) theDialog.dismiss ();
               theDialog = null;
            }
            break;

         default:
            System.out.println ("QUE SETO ?");
            return false;
      }

      return true;
   }
}
