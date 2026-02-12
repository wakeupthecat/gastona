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

import java.awt.Color;
import de.elxala.langutil.*;

/**
   13.06.2011 01:02
*/
public class uniColor
{
   public static final int BLACK = Color.BLACK.getRGB();
   public static final int WHITE = Color.WHITE.getRGB();
   public static final int RED = Color.RED.getRGB();
   public static final int LGRAY = Color.LIGHT_GRAY.getRGB();
   public static final int PAPER_INK_COLOR = Color.BLACK.getRGB();

   private Color esCol = new Color (0);

   public uniColor ()
   {
   }

   public uniColor (Color col)
   {
      esCol = col;
   }

   public uniColor (int rgb)
   {
      esCol = new Color(rgb);
   }

   public uniColor (int red, int green, int blue)
   {
      esCol = new Color (red, green, blue);
   }
   /*
   (from Android doc)
   Parse the color string, and return the corresponding color-int.
   If the string cannot be parsed, throws an IllegalArgumentException exception.
   Supported formats are:
   #RRGGBB #AARRGGBB 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta', 'yellow', 'lightgray', 'darkgray'

   from svg doc
   https://www.w3.org/TR/SVG/types.html#ColorKeywords

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
         esCol = new java.awt.Color(0);
         return;
      }

      if (str.startsWith ("url"))
      {
         // probably a gradient or else
         //log.dbg (2, "parseColor", "Color (" + colorStr + ") not parsed, return gray");
         esCol = new Color (232, 240, 240);  // azulito
         return;
      }

      int [] cols = miscUtil.parsedColorRGB (str);
      if (cols != null && cols.length == 4)
      {
         esCol = cols[3] == -1 ? new Color (cols[0], cols[1], cols[2]):
                                 new Color (cols[0], cols[1], cols[2], cols[3]);
      }
      else if (str.equalsIgnoreCase ("red")) esCol = Color.RED;
      else if (str.equalsIgnoreCase ("blue")) esCol = Color.BLUE;
      else if (str.equalsIgnoreCase ("green")) esCol = Color.GREEN;
      else if (str.equalsIgnoreCase ("black")) esCol = Color.BLACK;
      else if (str.equalsIgnoreCase ("white")) esCol = Color.WHITE;
      else if (str.equalsIgnoreCase ("cyan")) esCol = Color.CYAN;
      else if (str.equalsIgnoreCase ("magenta")) esCol = Color.MAGENTA;
      else if (str.equalsIgnoreCase ("yellow")) esCol = Color.YELLOW;
      else if (str.equalsIgnoreCase ("lightgray")) esCol = Color.LIGHT_GRAY;
      else if (str.equalsIgnoreCase ("darkgray")) esCol = Color.DARK_GRAY;
      //else error
   }

   public int toInt ()
   {
      return esCol.getRGB ();
   }

   public int toRGB ()
   {
      return esCol.getRGB ();
   }

   public Color getNativeColor ()
   {
      return esCol;
   }

   public Color getAwtColor ()
   {
      return esCol;
   }
}
