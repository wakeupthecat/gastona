/*
package javaj.widgets.graphics;
Copyright (C) 2011-2022 Alejandro Xalabarder Aulet

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

import android.graphics.RectF;
import android.graphics.Rect;

/**
   13.06.2011 01:02

   Trying to unify "identic" types between java's (Sun and Android)
   if this does not work properly or is too overload
   then use RectF for android and Rectangle2D for PC
   in all classes related
*/
public class uniRect
{
   public RectF theRect;

   public uniRect ()
   {
      theRect = new RectF(0f, 0f, 0f, 0f);
   }

   public uniRect (uniRect rec)
   {
      theRect = new RectF(rec.theRect);
   }

   public uniRect (Rect rec)
   {
      theRect = new RectF(rec);
   }

   public uniRect (RectF rec)
   {
      theRect = new RectF(rec);
   }

   public uniRect (float left, float top, float right, float bottom)
   {
      theRect = new RectF(left, top, right, bottom);
   }

   public uniRect (boolean isCenter, float x, float y, float width, float height)
   {
      if (isCenter)
           theRect = new RectF (x-width/2.f, y-height/2.f, x+width/2.f, y+height/2.f);
      else theRect = new RectF (x, y, x+width, y+height);
   }

   public Rect getRect ()
   {
      return new Rect((int) theRect.left, (int) theRect.top, (int) theRect.right, (int) theRect.bottom);
   }

   public void set (float left, float top, float right, float bottom)
   {
      theRect = new RectF(left, top, right, bottom);
   }

   public void set (uniRect rec)
   {
      theRect = new RectF(rec.left (), rec.top (), rec.right (), rec.bottom ());
   }

   public void union (uniRect rec)
   {
      theRect.union (rec.theRect);
   }

   public boolean contains(float x, float y)
   {
      return theRect.contains (x, y);
   }

   // already exist method contains ?
   // but it has a strange insideness definition, do it ourselves
   public boolean isPointInside (float px, float py)
   {
      return !(px < left () || px > right () || py < top () || py > bottom ());
   }

   // alias for comp.
   public boolean pointInside (float px, float py)
   {
      return isPointInside (px, py);
   }

   // returns true if this rectangle is entirely inside rec2
   public boolean isInsideRect (uniRect rec2)
   {
      return isPointInside (rec2.left (), rec2.top ()) && pointInside (rec2.left (), rec2.top ());
   }

   // returns true if rectangle rec2 is entirely inside this one
   public boolean containsRect (uniRect rec2)
   {
      // NOTE:  these two are the same
      //
      //        myrec.containsRect (rec2)
      //        rec2.isInsideRect (myrec)
      //
      return rec2.isInsideRect (this);
   }

   // returns true if this rectangle and rec2 intersect in some way
   //
   public boolean intersectRect (uniRect rec2)
   {
      return !isOutsideRect (rec2);
   }

   // returns true this and rec2 does not intersect at all
   public boolean isOutsideRect (uniRect rec2)
   {
      // NOTE:  these two are the same
      //
      //        myrec.isOutsideRect (rec2)
      //        rec2.isOutsideRect (myrec)
      //
      return rec2.left () >      right () ||
                  left () > rec2.right () ||
             rec2.bottom () >      top () ||
             rec2.bottom () > rec2.top ()
             ;
   }

   public float left ()    { return theRect.left;   }
   public float right ()   { return theRect.right;  }
   public float top ()     { return theRect.top; }
   public float bottom ()  { return theRect.bottom; }
   public float width ()   { return right () - left (); }
   public float height ()  { return bottom () - top (); }

   public float x () { return left (); }
   public float y () { return top (); }
   public float dx () { return width (); }
   public float dy () { return height (); }

   public float centerX ()  { return left() + width() / 2.f;  }
   public float centerY ()  { return top() + height() / 2.f;  }

   public void setLeft(float val)   {  theRect.left = val; }
   public void setRight(float val)   {  theRect.right = val; }
   public void setTop(float val)   {  theRect.top = val; }
   public void setBottom(float val)   {  theRect.bottom = val; }

   public String toString()
   {
      return "(" + left() + ", " + top() + ") (" + width() + ", " + height () + ")  left= " + left() + " right= " + right() + " top=  "+ top() + " bottom= " + bottom();
   }

}
