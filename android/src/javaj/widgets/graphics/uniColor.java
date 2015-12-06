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

import android.graphics.Color;

import de.elxala.langutil.miscUtil;

/**
*/
public class uniColor
{
   public static final int LGRAY = Color.LTGRAY;
   public static final int PAPER_INK_COLOR = Color.BLACK;
   public static final int BLACK = Color.BLACK;
   public static final int WHITE = Color.WHITE;
   public static final int RED = Color.RED;

   public final int DEF_COLOR = 0;
   private int esCol = DEF_COLOR;

   public uniColor ()
   {
   }

   public uniColor (int rgb)
   {
      esCol = rgb;
   }

   public uniColor (int red, int green, int blue)
   {
      esCol = Color.rgb (red, green, blue);
   }
   /*
   (from Android doc)
   Parse the color string, and return the corresponding color-int.
   If the string cannot be parsed, throws an IllegalArgumentException exception.
   Supported formats are:
   #RRGGBB #AARRGGBB 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta', 'yellow', 'lightgray', 'darkgray'

   from svg doc
   http://www.w3.org/TR/SVG/types.html#ColorKeywords

      color    ::= "#" hexdigit hexdigit hexdigit (hexdigit hexdigit hexdigit)?
                   | "rgb(" wsp* integer comma integer comma integer wsp* ")"
                   | "rgb(" wsp* integer "%" comma integer "%" comma integer "%" wsp* ")"
                   | color-keyword
      hexdigit ::= [0-9A-Fa-f]
      comma    ::= wsp* "," wsp*



   */
   public void parseColor (String str)
   {
      if (str == null || str.length () == 0)
      {
         esCol = DEF_COLOR;
         return;
      }

      if (str.startsWith ("url"))
      {
         // probably a gradient or else
         //log.dbg (2, "parseColor", "Color (" + colorStr + ") not parsed, return gray");
         esCol = Color.rgb (232, 240, 240);  // azulito
         return;
      }

      int [] cols = miscUtil.parsedColorRGB (str);
      if (cols != null && cols.length == 4)
      {
         esCol = cols[3] == -1 ? Color.rgb (cols[0], cols[1], cols[2]): Color.argb (cols[3], cols[0], cols[1], cols[2]);
      }
      else
      {  
         // (str.charAt(0) == '#') or 'black' ... etc
         esCol = Color.parseColor (str);
      }
   }

   public int toInt ()
   {
      return esCol;
   }

   public int getNativeColor ()
   {
      return esCol;
   }

   public int toRGB ()
   {
      return esCol;
   }
}
