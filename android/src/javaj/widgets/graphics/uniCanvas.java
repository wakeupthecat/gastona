/*
package javaj.widgets.graphics;
Copyright (C) 2011 Alejandro Xalabarder Aulet

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
package javaj.widgets.graphics;

import android.graphics.*;
import javaj.widgets.graphics.objects.*;
import javaj.widgets.graphics.*;

/**
*/
public class uniCanvas
{
   public Canvas ese = null;

   public int x0 = 0;
   public int y0 = 0;
   public int dx = 100;
   public int dy = 100;

   public uniCanvas (Canvas obj, int left, int top, int width, int height)
   {
      ese = obj;

      x0 = left;
      y0 = top;
      dx = width;
      dy = height;
   }

   public int getX0 ()  { return x0; }
   public int getY0 ()  { return y0; }
   public int getDx ()  { return dx; }
   public int getDy ()  { return dy; }

   public Canvas getG ()
   { return ese; }

   public int getTextHeight (uniPaint pai)
   {
      Paint.FontMetrics fome = pai.getFontMetrics ();
      int H = (int) (fome.top - fome.bottom);
      return H;
   }

   public int getTextWidth (String str, uniPaint pai)
   {
      uniRect bounds = new uniRect ();
      pai.getTextBounds (str, 0, str.length (), bounds.getRect ());
      return (int) bounds.width ();
   }

   public void drawLine (int x0, int y0, int x1, int y1, uniPaint pai)
   {
      ese.drawLine (x0, y0, x1, y1, pai);
   }
   
   public void fillRect (uniRect rec, uniColor ucol)
   {
      Paint pa = new Paint ();
      pa.setStrokeWidth (0);  // tricky filling a rectangle in android
      pa.setColor (ucol.getNativeColor ());
      
      ese.drawRect ((int) rec.left (),  (int) rec.top (),    (int) rec.right (), (int) rec.bottom (), pa);
   }

   public void drawRect (uniRect rec, uniPaint pai)
   {
      ese.drawLine (rec.left (), rec.top (), rec.right (), rec.top (), pai);
      ese.drawLine (rec.left (), rec.bottom (), rec.right (), rec.bottom (), pai);
      ese.drawLine (rec.left (), rec.top (), rec.left (), rec.bottom (), pai);
      ese.drawLine (rec.right (), rec.top (), rec.right (), rec.bottom (), pai);
   }

   public void drawText (String text, float x, float y, uniPaint pai)
   {
      ese.drawText (text, x, y, pai);
   }

   public void drawTrazoPath (uniPath pat, int indx, styleObject styleObj)
   {
      if (styleObj == null)
         styleObj = styleGlobalContainer.getStyleObjectByName (pat.getStyleFromTrazo (indx));

      Path elpath = pat.getNativePathFromTrazo (indx);
      if (styleObj.hasFill ())
      {
         ese.drawPath (elpath, styleObj.getFillPaint ());
      }
      if (styleObj.hasStroke ())
      {
         ese.drawPath (elpath, styleObj.getStrokePaint ());
      }
   }
   
   public void drawPath (uniPath pat)
   {
      for (int ii = 0; ii < pat.getTrazosCount (); ii ++)
      {
         drawTrazoPath (pat, ii, null);
      }
   }

   public void fitPathInArea (uniPath pat, uniRect area)
   {
      offsetAndScale skalo = new offsetAndScale ();
      skalo.autoZoom (pat.getBounds (), area);

      // System.out.println ("area   " + area);
      // System.out.println ("bondos " + pat.getBounds ());
      // System.out.println ("skalo  " + skalo);
      
      scale (skalo.scaleX, skalo.scaleY);
      translate (skalo.offsetX, skalo.offsetY);
      for (int ii = 0; ii < pat.getTrazosCount (); ii ++)
      {
         drawTrazoPath (pat, ii, null);
      }
      translate (- skalo.offsetX, - skalo.offsetY);
      scale (1f / skalo.scaleX, 1f / skalo.scaleY);
   }

   public void rotate (float degrees, float cx, float cy)
   {
      ese.rotate (degrees, cx, cy);
   }

   public void scale (float scaleX, float scaleY)
   {
      ese.scale (scaleX, scaleY);
   }

   public void translate (float offsetX, float offsetY)
   {
      ese.translate (offsetX, offsetY);
   }
}
