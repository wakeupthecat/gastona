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
package javaj.widgets.graphics.objects;

import javaj.widgets.graphics.*;

public class textElement implements paintableDimensionableElement
{
   public String mText;
   public float x;
   public float y;
   private uniPaint mPaint;

   public textElement (String text, float posx, float posy, uniPaint pai)
   {
      mText = text;
      x = posx;
      y = posy;
      mPaint = pai;
   }

   public uniPaint getPaint () { return mPaint; }
   public void paintYou (uniCanvas here)
   {
      here.drawText (mText, x, y, mPaint);
   }

   public void getBounds (uniRect rbounds)
   {
      //rbounds.set (x, y, 10, 10);
      rbounds.set (0f, 0f, 0f, 0f);
   }

   public void unionBounds (uniRect bounds)
   {
      uniRect thisrect = new uniRect ();
      //thisrect.set (x, y, 10, 10);
      thisrect = new uniRect (bounds);
      bounds.union (thisrect);
   }
}
