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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import javaj.widgets.basics.*;

import de.elxala.zServices.*;
import de.elxala.langutil.*;

import javaj.widgets.graphics.*;

/**
*/
public class expandable
{
   private static logger log = new logger (null, "javaj.widgets.graphics.objects.expandable", null);

   // COMPRESION EXPANSION
   public uniRect maxExp = new uniRect (1f, 1f, 1f, 1f);
   public uniRect minExp = new uniRect (1f, 1f, 1f, 1f);

   public uniRect currentExpansion = new uniRect (1f, 1f, 1f, 1f);
   // interaccion acordeon right  extend [1.2 0 ]
   // interaccion acordeon left   extend [1.2 0]
   // interaccion acordeon top    compr  [0 -0.8]
   // interaccion acordeon bottom compr  [0  0.8]
   //
   // lados del rectangulo que se pueden extender/comprimir
   //       [left right top bottom]
   //       acordeon right  [0 1 0 0]
   //       acordeon left   [1 0 0 0]
   //       acordeon top    [0 0 1 0]
   //       acordeon bottom [0 0 0 1]
   //
   // limites de compresion expansion
   //       maxexp [0 1.5 0 0]
   //       minexp [0 0.7 0 0]

   public boolean expand (float xFactor, float yFactor)
   {
      boolean treat = false;
      if (xFactor != 0f)
      {
         if (minExp.left() < 1f || maxExp.left() > 1f)
         {
            float exp = currentExpansion.left() + xFactor;
            currentExpansion.setLeft (Math.max(minExp.left(), exp));
            currentExpansion.setLeft (Math.min(maxExp.left(), currentExpansion.left()));
            treat = true;
         }
         else if (minExp.right() < 1f || maxExp.right() > 1f)
         {
            float exp = currentExpansion.right() + xFactor;
            currentExpansion.setRight (Math.max(minExp.right(), exp));
            currentExpansion.setRight (Math.min(maxExp.right(), currentExpansion.right()));
            treat = true;
         }
      }
      if (yFactor != 0f)
      {
         if (minExp.top() < 1f || maxExp.top() > 1f)
         {
            float exp = currentExpansion.top() + xFactor;
            currentExpansion.setTop (Math.max(minExp.top(), exp));
            currentExpansion.setTop (Math.min(maxExp.top(), currentExpansion.top()));
            treat = true;
         }
         else if (minExp.bottom() < 1f || maxExp.bottom() > 1f)
         {
            float exp = currentExpansion.bottom() + xFactor;
            currentExpansion.setBottom (Math.max(minExp.bottom(), exp));
            currentExpansion.setBottom (Math.min(maxExp.bottom(), currentExpansion.bottom()));
            treat = true;
         }
      }
      return treat;
   }
}
