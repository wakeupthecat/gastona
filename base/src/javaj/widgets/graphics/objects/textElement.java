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

import javaj.widgets.graphics.*;

public class textElement implements paintableDimensionableElement
{
   public String mText;
   public float x;
   public float y;
   public boolean isAbotonado; // has a round rectangle
   public String mStyleStr;

   // 09.11.2011 00:00
   // Note that the bounds cannot be calculated without canvas and paint !!!!
   // since java needs the canvas for the font metrics and android use the paint for it ?!?!?!?!
   // (such a stupid dependency!)
   private uniRect theBounds;

   public textElement (String text, float posx, float posy, String pStyleStr, boolean abotonadoAspect)
   {
      mText = text;
      x = posx;
      y = posy;
      mStyleStr = pStyleStr;
      isAbotonado = abotonadoAspect;
      theBounds = new uniRect (posx, posy, 10.f * text.length (), 10.f); // aprox
   }

   protected uniPaint getPaintStroke (uniCanvas here)
   {
      styleObject styleObj = here.getStyleObject (mStyleStr);
      return styleObj.hasStroke () ? styleObj.getStrokePaint (): new uniPaint ();
   }

   public void paintYou (uniCanvas here)
   {
      // GET SOME PAINT!

      float marg = 0.f;
      float hText = here.getTextHeight (getPaintStroke (here));
      float wText = here.getTextWidth (mText, getPaintStroke (here));

      if (isAbotonado)
      {
         marg = hText / 2.f;

         uniPath recPath = new uniPath ();
         recPath.getEdiPaths ().addRoundRect (new uniRect (false, x, y, wText + marg + marg, hText + marg + marg), 4, 4);
         recPath.setStyle (mStyleStr);
         here.drawPath (recPath);
      }

      theBounds.set (x, y, wText + marg + marg, hText + marg + marg);
      here.drawText (mText, x + marg, y + marg + hText, getPaintStroke (here));
   }

   public void getBounds (uniRect rbounds)
   {
      rbounds.set (theBounds);
   }

   public void unionBounds (uniRect bounds)
   {
      uniRect thisrect = new uniRect ();
      getBounds (thisrect);
      bounds.union (thisrect);
   }
}
