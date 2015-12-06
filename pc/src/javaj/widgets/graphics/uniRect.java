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
public class uniRect extends Rectangle2D.Float
{
   public uniRect ()
   {
      super (0f, 0f, 0f, 0f);
   }

   public uniRect (Rectangle2D rec)
   {
      setRect (rec);
   }

   public uniRect (float left, float top, float right, float bottom)
   {
      //super (float x, float y, float w, float h);
      super (left, top, right-left, bottom-top);
   }

   public uniRect (boolean isCenter, float x, float y, float width, float height)
   {
      super (x - (isCenter ? width/2.f: 0.f), y - (isCenter ? height/2.f: 0.f), width, height);
   }

   public void set (Rectangle2D rec)
   {
      setRect (rec);
   }

   public void set (uniRect rec)
   {
      setRect (rec.left (), rec.top (), rec.width (), rec.height ());
   }

   public void set (float left, float top, float right, float bottom)
   {
      //super (float x, float y, float w, float h);
      setRect (left, top, right-left, bottom-top);
   }

   public void union (uniRect rec)
   {
      if (rec.width == 0 || rec.height == 0) return;
      if (width == 0 || height == 0)
         setRect (rec);
      else
         setRect (createUnion (rec));
   }

   // already exist method contains ?
   // but it has a strange insideness definition, do it ourselves
   public boolean pointInside (float px, float py)
   {
      return !(px < left () || px > right () || py < top () || py > bottom ());
   }

   public float left ()    { return x;   }
   public float right ()   { return x + width;  }
   public float top ()     { return y; }
   public float bottom ()  { return y + height; }
   public float width ()   { return width; }
   public float height ()  { return height;  }

   public float x () { return left (); }
   public float y () { return top (); }
   public float dx () { return width (); }
   public float dy () { return height (); }
   
   public float centerX ()  { return x + width / 2.f;  }
   public float centerY ()  { return y + height / 2.f;  }

   public void setLeft (float v)    { width += Math.abs(v - x); x = v;   }
   public void setRight (float v)   { width = v - x;  }
   public void setTop (float v)     { height += Math.abs(v - y); y = v; }
   public void setBottom (float v)  { height = v - y; }

   public void setWidth (float v)   { width = v; }
   public void setHeight (float v)  { height = v;  }
}
