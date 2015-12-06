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

import java.awt.Graphics2D;

/**
*/
public class uniCanvas
{
   public Graphics2D ese = null;


   public uniCanvas (java.awt.Graphics2D obj)
   { ese = obj; }

   public Graphics2D getG ()
   { return ese; }

   public int getTextHeight (uniPath pai)
   {
      // we don't use pai but for android is needed!

      return ese.getFontMetrics ().getHeight ();

      //Paint.FontMetrics fome = pai.getFontMetrics ();
      //int H = (int) (fome.top - fome.bottom);

   }

   public int getTextWidth (String str, uniPath pai)
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

   public void drawText (String text, float x, float y, uniPaint pai)
   {
      // System.out.println ("drawo [" + text + "] en " + x + ", " + y);
      ese.setColor (pai.getColorColor ());
      ese.drawString (text, x, y);
   }

   public void drawPath (uniPath pat, uniPaint pai)
   {
      ese.setColor (pai.getColorColor ());
      //ese.setColor (java.awt.Color.BLACK);

      if (pai.isStroke ())
      {
         //System.out.println ("Stroke! ");
         ese.setStroke (pai.getStroke ());
         ese.draw (pat.getShape ());
         //System.out.println ("ojeto pinto " + pat.ojeto + " bounds " + pat.ojeto.getBounds());
      }
      if (pai.isFill ())
      {
         //System.out.println ("Fill! ");
         ese.fill (pat.getShape ());
         //System.out.println ("ojeto relleno " + pat.ojeto + " bounds " + pat.ojeto.getBounds());
      }
   }

   public void rotate (float degrees, float cx, float cy)
   {
      //System.out.println ("TEPALLO! " + cx + ", " +cy);
      ese.rotate (degrees * 2 * Math.PI / 360.f, cx, cy);
      //ese.rotate (degrees * 2 * Math.PI / 360.f);
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
