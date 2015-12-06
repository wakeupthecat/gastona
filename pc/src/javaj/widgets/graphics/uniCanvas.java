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

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.font.GlyphVector;
import javaj.widgets.graphics.objects.*;

import java.awt.RenderingHints;

/**
*/
public class uniCanvas
{
   public Graphics2D ese = null;

   public int x0 = 0;
   public int y0 = 0;
   public int dx = 100;
   public int dy = 100;

   public uniCanvas (java.awt.Graphics2D obj, int left, int top, int width, int height)
   {
      ese = obj;
      ese.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      x0 = left;
      y0 = top;
      dx = width;
      dy = height;
   }

   public int getX0 ()  { return x0; }
   public int getY0 ()  { return y0; }
   public int getDx ()  { return dx; }
   public int getDy ()  { return dy; }

   public Graphics2D getG ()
   {
      return ese;
   }

   public int getTextHeight (uniPaint pai)
   {
      // we don't use pai but for android is needed!

      return ese.getFontMetrics ().getHeight ();

      //Paint.FontMetrics fome = pai.getFontMetrics ();
      //int H = (int) (fome.top - fome.bottom);

   }

   public int getTextWidth (String str, uniPaint pai)
   {
      // we don't use pai but for android is needed!
      return ese.getFontMetrics ().stringWidth (str);

      //android
      //uniRect bounds = new uniRect ();
      //pai.getTextBounds (aus, 0, aus.length (), bounds);
      //int W = bounds.width ();
   }

   public void drawLine (int x0, int y0, int x1, int y1, uniPaint pai)
   {
      ese.setColor (pai.getColorColor ());
      ese.drawLine (x0, y0, x1, y1);
   }

   public void fillRect (uniRect rec, uniColor ucol)
   {
      ese.setColor (ucol.getNativeColor ());
      ese.fillRect ((int) rec.left (),  (int) rec.top (),    (int) rec.width (), (int) rec.height ());
   }
   
   public void drawRect (uniRect rec, uniPaint pai)
   {
      ese.setColor (pai.getColorColor ());
      ese.drawLine ((int) rec.left (),  (int) rec.top (),    (int) rec.right (), (int) rec.top ());
      ese.drawLine ((int) rec.left (),  (int) rec.bottom (), (int) rec.right (), (int) rec.bottom ());
      ese.drawLine ((int) rec.left (),  (int) rec.top (),    (int) rec.left (),  (int) rec.bottom ());
      ese.drawLine ((int) rec.right (), (int) rec.top (),    (int) rec.right (), (int) rec.bottom ());
   }

   public void drawText (String text, float x, float y, uniPaint pai)
   {
      //System.out.println ("drawo     [" + text + "] en " + x + ", " + y);
      //System.out.println ("font info [" + pai.getTextFontFamily () + "] type " + pai.getTextFontType () + " size " + (int) pai.getTextSize ());

      Font fontana = new Font (pai.getTextFontFamily (), pai.getTextFontType (), (int) pai.getTextSize ());
      ese.setFont (fontana);
      ese.setColor (pai.getColorColor ());
      ese.drawString (text, x, y);


      //ver articulo : http://docstore.mik.ua/orelly/java-ent/jfc/ch04_09.htm
      //             de Java Foundation Classes in a Nutshell
      //Graphics2D g;
      //      GlyphVector msg = fontana.createGlyphVector(ese.getFontRenderContext(), text);
      //      ese.drawGlyphVector(msg, x, y);
   }

   public void drawTrazoPath (uniPath pat, int indx, styleObject styleObj)
   {
      //de.elxala.langutil.uniUtil.printLater ("drawPath " + ii + "  form  [" + pat.getEdiPaths().getTrazo(ii).trazoForm + "]  style [" + pat.getStyleFromTrazo (ii) + "]");
      if (styleObj == null)
         styleObj = styleGlobalContainer.getStyleObjectByName (pat.getStyleFromTrazo (indx));
      Shape elpath = pat.getNativePathFromTrazo (indx);
      if (styleObj.hasFill ())
      {
         ese.setColor (styleObj.getFillPaint ().getColorColor ());
         ese.fill (elpath);
      }
      if (styleObj.hasStroke ())
      {
         ese.setColor (styleObj.getStrokePaint ().getColorColor ());
         ese.setStroke (styleObj.getStrokePaint ().getStroke ());
         ese.draw (elpath);
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
      ese.rotate (degrees * 2 * Math.PI / 360.f, cx, cy);
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
