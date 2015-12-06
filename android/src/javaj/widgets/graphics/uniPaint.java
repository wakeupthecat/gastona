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

import android.graphics.Paint;
import android.graphics.Typeface;

/**
*/
public class uniPaint extends Paint
{
   public static final int STYLE_STROKE = 0;
   public static final int STYLE_FILL = 1;

   public static final int FONT_NORMAL = Typeface.NORMAL;
   public static final int FONT_BOLD = Typeface.BOLD;
   public static final int FONT_ITALIC = Typeface.ITALIC;
   public static final int FONT_BOLD_ITALIC = Typeface.BOLD_ITALIC;

   public int mTextType = FONT_NORMAL;
   public String mTextFamily = null;   // Paint.setTypeface accept null as family

   public int mStyle = STYLE_STROKE;

   public uniPaint ()
   {
      super.setStyle (android.graphics.Paint.Style.STROKE);
      //super.setStrokeWidth (2);
   }

   public uniPaint (uniPaint cop)
   {
      super (cop);
      mTextType = cop.mTextType;
      mTextFamily = cop.mTextFamily;
      mStyle = cop.mStyle;
   }


   public void setStyle (int style)
   {
      mStyle = (style == STYLE_STROKE) ? STYLE_STROKE: STYLE_FILL;

      super.setStyle (mStyle == STYLE_STROKE ? android.graphics.Paint.Style.STROKE : android.graphics.Paint.Style.FILL);
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
      // same behaviour as in base (jar)
      super.setAlpha (alpha > 255 ? 255: (alpha < 0 ? 0: alpha));
   }

   public void setTextFontType (int fontType)
   {
      mTextType = fontType;
      super.setTypeface (Typeface.create (mTextFamily, mTextType));
   }

   public int getTextFontType ()
   {
      return mTextType;
   }

   public void setTextFontFamily (String family)
   {
      mTextFamily = family;
      setTextFontFamily (mTextFamily, FONT_NORMAL);
   }

   public void setTextFontFamily (String family, int fontType)
   {
      mTextFamily = family;
      mTextType = fontType;
      super.setTypeface (Typeface.create (mTextFamily, mTextType));
   }

   public String getTextFontFamily ()
   {
      return mTextFamily;
   }

   public void setColor (uniColor color)
   {
      super.setColor(color.toInt ());
   }

   public void setColorRGB (int colorRGB)
   {
      // we want to set JUST RGB, alpha is not expected!
      // using only setColor (int) would clear any alpha previous value,
      // see documentation android.graphics.Paint,
      // and we do not want this

      int alpha = getAlpha ();
      super.setColor(colorRGB);
      super.setAlpha(alpha);
   }
   
   public int getColorRBG ()
   {
      return super.getColor ();
   }
   
}
