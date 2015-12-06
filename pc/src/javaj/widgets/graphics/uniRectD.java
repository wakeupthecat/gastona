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

import java.awt.geom.Rectangle2D;

/**
   13.06.2011 01:02

   Trying to unify "identic" types between java's (Sun and Android)
   if this does not work properly or is too overload
   then use RectF for android and Rectangle2D for PC
   in all classes related
*/
public class uniRectD extends Rectangle2D.Double
{
   public uniRectD ()
   {
      super (0f, 0f, 0f, 0f);
   }

   public uniRectD (Rectangle2D rec)
   {
      setRect (rec);
   }

   public uniRectD (double left, double top, double right, double bottom)
   {
      //super (float x, float y, float w, float h);
      super (left, top, right-left, bottom-top);
   }

   public void set (Rectangle2D rec)
   {
      setRect (rec);
   }

   public void set (double left, double top, double right, double bottom)
   {
      //super (float x, float y, float w, float h);
      setRect (left, top, right-left, bottom-top);
   }

   public void union (uniRectD rec)
   {
      setRect (createUnion (rec));
   }


   public double left ()    { return x;   }
   public double right ()   { return x + width;  }
   public double top ()     { return y; }
   public double bottom ()  { return y + height; }
   public double width ()   { return width; }
   public double height ()  { return height;  }

   public double centerX ()  { return x + width / 2.f;  }
   public double centerY ()  { return y + height / 2.f;  }

   public void setLeft (double v)    { x = v;   }
   public void setRight (double v)   { width = v - x;  }
   public void setTop (double v)     { y = v; }
   public void setBottom (double v)  { height = v - y; }
   public void setWidth (double v)   { width = v; }
   public void setHeight (double v)  { height = v;  }
}
