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

public class pathElement implements paintableDimensionableElement
{
   private float rotationDegrees = 0.f;
   private uniPath mPath;
   private uniPaint mPaint;

   public pathElement (uniPath pat, uniPaint pai, float rotationDeg)
   {
      mPath = pat;
      mPaint = pai;
      rotationDegrees = rotationDeg;
   }

   public uniPath getPath () { return mPath; }
   public uniPaint getPaint () { return mPaint; }
   public float getRotationDegrees () { return rotationDegrees; }

   public void paintYou (uniCanvas here)
   {
      uniRect rec = null;
      if (rotationDegrees != 0.f)
      {
         rec = new uniRect();
         getPath ().computeBounds (rec, true);
         here.rotate (rotationDegrees, rec.centerX (), rec.centerY ());
      }

      here.drawPath (mPath, mPaint);
      if (rec != null)
         here.rotate (-rotationDegrees, rec.centerX (), rec.centerY ());
   }

   public void getBounds (uniRect rbounds)
   {
      mPath.computeBounds (rbounds, true);
   }

   public void unionBounds (uniRect bounds)
   {
      uniRect thisrect = new uniRect ();
      mPath.computeBounds (bounds, true);
      bounds.union (thisrect);
   }
}
