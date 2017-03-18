/*
package de.elxala.math.space.curve;
(c) Copyright 2006 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package de.elxala.math.space.curve;

import de.elxala.math.space.*;   // vect3f

/**
 *
 */

public class polyAutoCasteljau implements IpolyAutoCasteljau
{
   public curveWithAdjustPoint PP = null;

   public boolean isClosed = false;

   public polyAutoCasteljau (curveWithAdjustPoint curve)
   {
      PP = curve;
      calculations ();
   }

   public polyAutoCasteljau (vect3f [] thePoints)
   {
      PP = new curveWithAdjustPoint (thePoints);
      calculations ();
   }

   public void create (vect3f [] thePoints)
   {
      PP = new curveWithAdjustPoint (thePoints);
      calculations ();
   }

   public void setCloseCurve (boolean closeIt)
   {
      isClosed = closeIt;
      calculations ();
   }

   public vect3f [] points ()
   {
      return PP.points;
   }

   public vect3f [][] adjusts ()
   {
      return PP.adjust;
   }

   public vect3f  getPoint  (int indx)
   {
      return PP.getPoint (indx);
   }

   public int curvesCount ()
   {
      return PP.curvesCount ();
   }

   public boolean getVectorsCurve (int ncurv, vect3f [] ini_j1_j2_fin)
   {
      if (ncurv < 0 || ncurv >= PP.curvesCount ()) return false;

      if (ini_j1_j2_fin.length < 4)
      {
         System.out.println ("ERROR polyAutoCasteljau::getVectorsCurve ! parameter 2 has to be a created vect3f[4]!");
         return false;
      }

      // puntos inicial y final
      //
      ini_j1_j2_fin[0] = PP.getPoint (ncurv);
      ini_j1_j2_fin[3] = PP.getPoint (ncurv+1);

      // puntos de ajuste
      //
      if (ncurv == 0 && !isClosed)
           ini_j1_j2_fin[1] = getJust1Point (ncurv + 1);
      else ini_j1_j2_fin[1] = getJust2Point (ncurv);

      if (ncurv == PP.curvesCount () - 1 && !isClosed)
           ini_j1_j2_fin[2] = getJust2Point (ncurv);
      else ini_j1_j2_fin[2] = getJust1Point (ncurv + 1);

      return true;
   }

   public int size ()
   {
      return PP.size ();
   }

   public boolean ptoValido (int indx)
   {
      return PP.ptoValido (indx);
   }

   public void removePoint (int indx)
   {
      PP.removePoint (indx);
      calculations ();
   }

   public void tryInsertPoint (int indxPafter, float x, float y)
   {
      PP.tryInsertPoint (indxPafter, x, y);
      calculations ();
   }

   public void calculations ()
   {
   }

   public vect3f[] internGetControlPoints (vect3f p1, vect3f p2, vect3f p3)
   {
      System.out.println  ("ERROR: bad polyAutoCasteljau!");
      return new vect3f [2];
   }

   public void getJustifPoints (int cc, vect3f [] justif)
   {
      vect3f[] ptos = internGetControlPoints(points ()[cc-1], points ()[cc], points ()[cc+1]);

      justif[0] = ptos[0];
      justif[1] = ptos[1];
   }

   private vect3f [] arr = new vect3f[2];

   public vect3f getJust1Point (int cc)
   {
      getJustifPoints (cc, arr);
      return arr[0];
   }

   public vect3f getJust2Point (int cc)
   {
      getJustifPoints (cc, arr);
      return arr[1];
   }
}