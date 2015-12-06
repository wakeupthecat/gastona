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
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javaj.widgets.basics.*;


/**
*/
public class zImage extends ImageView implements MensakaTarget, izWidget
{
   private basicAparato helper = null;
   private View thisView = null;

   //private Color backColor = new JButton().getBackground (); // new JButton().getBackgroundColor ();

   // own data
   private Drawable theDrawable = null;

   public zImage (Context co)
   {
      super (co);
      thisView = this;
   }

   // ------
   public zImage (Context co, String map_name)
   {
      super (co);
      init (map_name);
      thisView = this;
   }

   public void init (String map_name)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));
   }

   //-i- interface iWidget
   //
   public int getDefaultHeight ()
   {
      if (theDrawable != null) return theDrawable.getIntrinsicHeight ();
      return 72;
   }

   public int getDefaultWidth ()
   {
      if (theDrawable != null) return theDrawable.getIntrinsicWidth ();
      return 72;
   }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String map_name)
   {
      mName = map_name;
      init (map_name);
   }
   //-i-


   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zImage",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);

           new Thread(new Runnable() {
             public void run() {

               String imageFileName = helper.ebs ().getText ();
               widgetLogger.log ().dbg (2, "loading image " + imageFileName);

               theDrawable = javaLoad.getSomeHowDrawable (imageFileName, false);
               if (theDrawable == null)
                    widgetLogger.log ().dbg (2, "could not load image");
               else widgetLogger.log ().dbg (2, "image loaded");


               thisView.post(new Runnable() {
                 public void run() {
                    setImageDrawable(theDrawable);
                    thisView.invalidate ();
                 }
               });
             }
           }).start();

            //paintImmediately (new Rectangle (0, 0, getSize().width, getSize().height));
            //repaint ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "zImage",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setEnabled (helper.ebs ().getEnabled ());
            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            break;

         default:
            return false;
      }

      return true;
   }

////   public Dimension getPreferredSize ()
////   {
////      return (theImage != null) ?
////               new Dimension (theImage.getIconWidth(), theImage.getIconHeight()) :
////               new Dimension (10, 10);
////   }

//   public void paint(Graphics g)
//   {
//      Dimension d = getSize();
//      if (d.width <= 0 || d.height <= 0) return; // >>>> return
//
//      if (backColor != null)
//      {
//         g.setColor (backColor);
//         g.fillRect (0, 0, d.width, d.height);
//      }
//      else
//         g.clearRect (0, 0, d.width, d.height);
//
//      if (theImage == null) return; // No image yet!
//
//      int left  = (d.width - theImage.getWidth()) / 2;
//      int right = (d.height - theImage.getHeight()) / 2;
//
//      theImage.paintIcon(this, g, left, right);
//   }
}
