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

import de.elxala.zServices.*;

/**
*/
public class movable
{
   private static logger log = new logger (null, "javaj.widgets.graphics.objects.movable", null);

   // DESPLAZAMIENTO
   public uniRect maxDesp = new uniRect (10f, 10f, 10f, 10f);
   public float currentDesplacementX = 0.f;
   public float currentDesplacementY = 0.f;
   //       [left right top bottom]
   //       todas direcciones [1 1 1 1]
   //       ...
   //
   // limites de movimiento
   //       maxdesp [999 999 .5 .5]

   public void setBasicMovement (String basicMov)
   {
      if (basicMov == null || basicMov.length () != 4) return;

      // Note: we want the user to give left, right, top, down
      //       but uniRect express it in different order!
      float maxL = basicMov.charAt(0) == '0' ? 0f: 10f; // left
      float maxR = basicMov.charAt(1) == '0' ? 0f: 10f; // right (!)
      float maxT = basicMov.charAt(2) == '0' ? 0f: 10f; // top   (!)
      float maxB = basicMov.charAt(3) == '0' ? 0f: 10f; // bottom
      maxDesp.set (maxL, maxT, maxR, maxB); //Note: here it is left, top, right, bottom!
   }

   public boolean canMove ()
   {
      return canMove (true, true, true, true);
   }

   public boolean canMove (boolean toLeft, boolean toRight, boolean toUp, boolean toDown)
   {
      return (toLeft && maxDesp.left() > 0f) ||
             (toRight && maxDesp.right() > 0f) ||
             (toUp && maxDesp.top() > 0f) ||
             (toDown && maxDesp.bottom() > 0f);
   }

   public boolean move (float xFactor, float yFactor)
   {
      boolean treat = false;
      if (xFactor != 0f && (maxDesp.left() > 0f || maxDesp.right() > 0f))
      {
         currentDesplacementX = Math.max(-maxDesp.left(),  xFactor);
         currentDesplacementX = Math.min( maxDesp.right(), currentDesplacementX);
         treat = true;
      }
      if (yFactor != 0f && (maxDesp.top() > 0f || maxDesp.bottom() > 0f))
      {
         currentDesplacementY = Math.max(-maxDesp.top(),  yFactor);
         currentDesplacementY = Math.min( maxDesp.bottom(), currentDesplacementY);
         treat = true;
      }
      log.dbg (2, "move", "currentDesplacement XY " + currentDesplacementX + ", " + currentDesplacementY);
      return treat;
   }
}
