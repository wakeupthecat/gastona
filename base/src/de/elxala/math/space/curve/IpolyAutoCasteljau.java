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

public interface IpolyAutoCasteljau
{
   //public IpolyAutoCasteljau (curveWithAdjustPoint curve);
   public void    create         (vect3f [] thePoints);

   public vect3f  getPoint        (int indx);
   public int     curvesCount     ();
   public boolean getVectorsCurve (int nCurve, vect3f [] j1_j2_pfinal);
   public vect3f  getJust1Point   (int indx);
   public vect3f  getJust2Point   (int indx);

   public int     size           ();
   public void    calculations   ();

   public boolean ptoValido      (int indx);
   public void    removePoint    (int indx);
   public void    tryInsertPoint (int indxInsertion, float x, float y);
}
