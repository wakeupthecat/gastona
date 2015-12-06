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
import javaj.widgets.graphics.objects.*;
import javaj.widgets.kits.*;
import android.content.Context;
import android.view.View;
import android.graphics.drawable.*;
import android.graphics.Canvas;

/**
   zButton : javaj zWidget representing a GUI button
*/
public class zButton extends Button implements MensakaTarget, izWidget
{
   private basicAparato helper = null;
   private String currentIconName = null;

   private Drawable dra = null;
   private graphicObjectLoader elGrafitti = null;
   private graphicObjectLoader elGrafittiPress = null;
   private float calcScaleX = 1.f;
   private float calcScaleY = 1.f;
   private float calcOffsetX = 0.f;
   private float calcOffsetY = 0.f;

   public zButton (Context co)
   {
      super (co);
      setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
   }

   public zButton (Context co, String map_name, String slabel)
   {
      super (co);
      setTextSize (sysFonts.getStandardTextSizeInScaledPixels ());
      setText (slabel);
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
      construct (map_name);
   }

   public int getDefaultHeight ()
   {
      // this is for left or right images! for vertical (up or down) would be the summ!
      return Math.max (androidSysUtil.getHeightChars(1.4f),
                       dra == null ? 0: dra.getIntrinsicHeight());
   }

   public int getDefaultWidth ()
   {
      // this is for left or right images! for vertical (up or down) would be Math.max!
      return androidSysUtil.getWidthChars (3 + getText().length ())  + (dra == null ? 0: dra.getIntrinsicWidth());
   }

   private void construct (String map_name)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      setOnClickListener  (
            new View.OnClickListener()
            {
               public void onClick(View v)
               {
                 actionPerformed();
               }
            });
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "zButton",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);
            setText (helper.decideLabel (getText ().toString ()));
            if (helper.ebs().getImageFile () != currentIconName)
            {
               currentIconName = helper.ebs().getImageFile ();
               widgetLogger.log ().dbg (2, "loading image " + currentIconName);

               //BitmapDrawable bidra = (currentIconName.length() > 0) ? new BitmapDrawable (currentIconName): null;
               dra = javaLoad.getSomeHowDrawable (currentIconName, true);
               setCompoundDrawablesWithIntrinsicBounds (dra, null, null, null);
            }

            if (helper.ebs().getGraffiti () != null)
            {
               // Set dummy drawable to get the reference size depending on the resolution
               // and to get the label right placed

               int id = androidSysUtil.getResourceId ("drawable/emptybuttonimg"); // remember : do not include ".png"!
               if (id != 0)
               {
                  Drawable dra = androidSysUtil.getResources().getDrawable(id);
                  dra.setAlpha (0); // NOT INTERESTED REALLY IN THE PICTURE BUT JUST IN ITS PLACEMENT AND SIZE!
                  setCompoundDrawablesWithIntrinsicBounds (dra, null, null, null);
               }
               elGrafitti = null;
               elGrafittiPress = null;
            }
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "zButton",
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

   public void onDraw (Canvas ca)
   {
      super.onDraw (ca);

      // grafity
      //
      if (helper.ebs().getGraffiti () != null)
      {
         doGrafitti (new uniCanvas (ca, getLeft (), getTop (), getWidth (), getHeight ()),
                     helper.ebs().getGraffiti (),
                     helper.ebs().getGraffitiPress ()
                     );
      }
   }

   public void actionPerformed()
   {
      //(o) javaj_widgets Experimental Launch standard dialogs on button action

      String assocDialog = helper.ebs().getSimpleAttribute (helper.ebs().CONTROL, "DIALOG");
      if (assocDialog != null)
      {
         //Eva files = helper.ebs().getAttribute (helper.ebs().CONTROL, true, "chosen." + assocDialog);
         Eva files = helper.ebs().getAttribute (helper.ebs().CONTROL, true, "chosen");

         if (assocDialog.equalsIgnoreCase ("FILE"))
         {
            if ( ! fileDialog.selectFile ("", null, files, false)) return;  // nothing selected
         }
         else if (assocDialog.equalsIgnoreCase ("FILES"))
         {
            if ( ! fileDialog.selectFile ("", null, files, true)) return;  // nothing selected
         }
         else if (assocDialog.equalsIgnoreCase ("DIR"))
         {
            if ( ! fileDialog.selectDir (null, files, false)) return;  // nothing selected
         }
         else if (assocDialog.equalsIgnoreCase ("DIRS"))
         {
            if ( ! fileDialog.selectDir (null, files, true)) return;  // nothing selected
         }
         widgetLogger.log().dbg (9, "zButton", "selected files or directories : " + files);
      }

      // action of a normal button
      helper.signalAction ();
   }


   public void doGrafitti (uniCanvas uCan, Eva evaPainting, Eva evaPaintingPress)
   {
      if (elGrafitti == null)
      {
         elGrafitti = new graphicObjectLoader ();
         elGrafitti.loadObjectFromEva ("namoso", evaPainting, null /** note **/ , "111", new offsetAndScale ());
         // ** Note: we actually do not press the graphical object but the button, so we cannot use the press semantic of the object

         if (evaPaintingPress != null)
         {
            elGrafittiPress = new graphicObjectLoader ();
            elGrafittiPress.loadObjectFromEva ("namoso2", evaPaintingPress, null, "111", new offsetAndScale ());
         }

         calcScaleX = 1.f;
         calcScaleY = 1.f;
         calcOffsetX = 0.f;
         calcOffsetY = 0.f;

         int tela_dx = 16; // just fallback, it will be recalculated
         int tela_dy = 16; // just fallback

         Drawable [] arrDra = getCompoundDrawables ();
         if (arrDra != null && arrDra.length > 0 && arrDra[0] != null)
         {
            uniPaint uPai = new uniPaint ();

            int left  = getCompoundPaddingLeft() - getCompoundDrawablePadding () - arrDra[0].getIntrinsicWidth ();
            int right = getCompoundPaddingLeft() - getCompoundDrawablePadding ();
            int tops = (getHeight () - arrDra[0].getIntrinsicHeight ()) / 2;
            int boto = getHeight () - tops;

            calcOffsetX += left;
            calcOffsetY += tops;

            tela_dx = arrDra[0].getIntrinsicWidth ();
            tela_dy = arrDra[0].getIntrinsicHeight ();

//            System.out.println ("ese peazo boton " + getName () + "! (" + tela_dx + " x " + tela_dy + ")");
//            System.out.println ("ese peazo marco " + getName () + "! (" + left + ", " + right + ") (" + tops + ", " + boto + ")");

//            uniPaint upa = new uniPaint ();
//            upa.setColor (uniColor.RED);
//            uCan.drawRect (new uniRect (left-1, tops-1, right+1, boto+1), upa);
         }

         calcScaleX = calcScaleY = elGrafitti.getScaleToFit (tela_dx, tela_dy);

         //System.out.println ("ese peazo graffiti " + getName ()  + "! (" + bounder.width () + " x " + bounder.height () + ")");

         calcOffsetX /= calcScaleX;
         calcOffsetY /= calcScaleY;

         calcOffsetX += elGrafitti.getOffsetXtoFit ();
         calcOffsetY += elGrafitti.getOffsetYtoFit ();
      }

      // decide which painting depending on button state press / release
      //

      float lastScaleX = calcScaleX;
      float lastScaleY = calcScaleY;

      graphicObjectLoader obj = elGrafitti;

      if (isPressed ())
      {
         if (elGrafittiPress == null)
         {
            // default behaviour for pressed button
            lastScaleX *= 1.2f;
            lastScaleY *= 1.2f;
         }
         else
            obj = elGrafittiPress;
      }

      uCan.scale (lastScaleX, lastScaleY);
      uCan.translate (calcOffsetX, calcOffsetY);
      obj.paintYou (uCan);
   }
}
