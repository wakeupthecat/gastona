/*
package javaj.widgets.graphics;
Copyright (C) 2011-2026 Alejandro Xalabarder Aulet

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
package javaj.widgets.graphics.objects;

//import org.xml.sax.Attributes;

import de.elxala.zServices.*;
import de.elxala.parse.svg.*;
import de.elxala.langutil.*;
import javaj.widgets.graphics.*;

/**
   A style object is built with a string containing the style in human readable
   that is parsed giving two uniPaint objects as product. Two access these two uniPaint objects
   just use the methods from the base class:

      boolean  hasFill ()
      uniPaint getFillPaint ()
      boolean  hasStroke ()
      uniPaint getStrokePaint ()

 */
public class styleObject extends svgLikeStyleParser2uniPaint
{
   private static logger log = new logger (null, "javaj.widgets.graphics.styleObject", null);

   protected uniPaint strokePen = null;
   protected uniPaint fillPen = null;
   protected String styleString = "";

   public styleObject (String pStyleString)
   {
      styleString = pStyleString;
      if (pStyleString != null)
         doParse ();
   }

   public boolean hasStroke ()
   {
      return strokePen != null;
   }

   public boolean hasFill ()
   {
      return fillPen != null;
   }

   public uniPaint getStrokePaint ()
   {
      return strokePen;
   }

   public uniPaint getFillPaint ()
   {
      return fillPen;
   }

   public void setNewStyle (String pStyleString)
   {
      strokePen = null;
      fillPen = null;
      styleString = pStyleString;
      if (pStyleString != null)
         doParse ();
   }

   public String getStyleString ()
   {
      return styleString;
   }

   public String toString ()
   {
      return getStyleString ();
   }

   // private void ensureParse ()
   // {
      // if (strParsed) return;
      // //
      // // it is not parsed because it was constructed without styleString
      // // therefore
   // }

   private void doParse ()
   {
      //strParsed = true;
      strokePen = new uniPaint ();
      strokePen.setColorRGB  (uniColor.PAPER_INK_COLOR);
      strokePen.setStyle  (uniPaint.STYLE_STROKE); // FILL or FILL_AND_STROKE
      strokePen.setAntiAlias(true);

      fillPen = new uniPaint ();
      fillPen.setColorRGB  (uniColor.LGRAY);
      fillPen.setStyle  (uniPaint.STYLE_FILL);
      fillPen.setAntiAlias(true);

      // from svgLikeStyleParser2uniPaint
      super.parseStyle (styleString, fillPen, strokePen);
      if (!hasFillInfo ()) fillPen = null;
      if (!hasStrokeInfo ()) strokePen = null;
   }
}
