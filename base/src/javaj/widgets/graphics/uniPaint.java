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

import java.awt.*;

/**
*/
public class uniPaint // implements java.awt.Paint
{
   public static final int STYLE_STROKE = 0;
   public static final int STYLE_FILL = 1;

   public int mStyle = STYLE_STROKE;
   public int mColorRBG = 0;
   public int mAlfa = 255;
   public BasicStroke mPenStroke = new BasicStroke(.3f);
   public float mTextSize = 11.f;

   public Color mColorColor = null;

   public uniPaint ()
   {
   }

   public uniPaint (uniPaint cop)
   {
      mStyle     = cop.mStyle;
      mColorRBG  = cop.mColorRBG;
      mAlfa      = cop.mAlfa;
      mPenStroke = cop.mPenStroke;
   }

   public void reset ()
   {
      mStyle = STYLE_STROKE;
      mColorRBG = 0;
      mAlfa = 255;
      mColorColor = null;
   }

   public void setStyle (int style)
   {
      mStyle = (style == STYLE_STROKE) ? STYLE_STROKE: STYLE_FILL;
   }

   public boolean isStroke ()
   {
      return mStyle == STYLE_STROKE;
   }

   public boolean isFill ()
   {
      return mStyle == STYLE_FILL;
   }

   public void setAlpha (int alpha)
   {
      mAlfa = alpha > 255 ? 255: (alpha < 0 ? 0: alpha);
      mColorColor = null;
   }

   public void setColor (uniColor color)
   {
      mColorRBG = color.toRGB ();
      mColorColor = null;
   }

   public void setColorRGB (int color)
   {
      mColorRBG = color;
      mColorColor = null;
   }

   public int getColorRBG ()
   {
      return mColorRBG;
   }

   public void setColorAndAlfa (int colorAndAlfa)
   {
      mAlfa = colorAndAlfa / 255 / 255 / 255;
      mColorRBG = colorAndAlfa -  mAlfa * 255 * 255 * 255;
      mColorColor = null;
   }

   public int getColorAndAlfa ()
   {
      return mColorRBG + 255 * 255 * 255 * mAlfa;
   }

   public void setAntiAlias (boolean sino)
   {
   }

   public void setStrokeWidth (float sw)
   {
      mPenStroke = new BasicStroke(sw);
   }

   public void setTextSize (float sw)
   {
      mTextSize = sw;
   }

   public float getTextSize ()
   {
      return mTextSize;
   }


   // PC specific
   //
   public Stroke getStroke ()
   {
      return mPenStroke;
   }

   public Color getColorColor ()
   {
      if (mColorColor == null)
      {
         //mColorColor = new Color (getColorAndAlfa (), (mAlfa > 0));
         mColorColor = new Color (getColorRBG ());
      }
      return mColorColor;
   }
}
