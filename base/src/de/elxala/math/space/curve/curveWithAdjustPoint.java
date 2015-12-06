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

public class curveWithAdjustPoint
{
   public vect3f [] points = new vect3f [0];
   public vect3f [][] adjust = new vect3f [2][0];
   public int firstIndx = 0;
   public int lastIndx = -1;

   public curveWithAdjustPoint (vect3f [] thePoints)
   {
      create (thePoints);
   }

   public curveWithAdjustPoint (vect3f [] thePoints, int offset, int size)
   {
      create (thePoints, offset, size);
   }

   /**
      constructor para pruebas rápidas
   */
   public curveWithAdjustPoint (float [] thePairs)
   {
      vect3f [] apoints = new vect3f [thePairs.length / 2]; // un índice para cada punto
      // copiar puntos
      for (int ii = 0; ii+1 < thePairs.length; ii +=2)
      {
         apoints [ii/2] = new vect3f (thePairs [ii], thePairs [ii+1]);
         //System.out.println ("(" + SIS.points[ii/2].x + ", " + SIS.points[ii/2].y + ") ");
      }

      create (apoints);
   }
   

   public void create (vect3f [] thePoints)
   {
      create (thePoints, 0, thePoints.length);
   }

   public void create (vect3f [] thePoints, int size)
   {
      create (thePoints, 0, size);
   }

   public void create (vect3f [] thePoints, int offset, int size)
   {
      if (offset < 0 || size < 0  || offset+size > thePoints.length)
      {
         System.out.println ("Error creating curveWithAdjustPoint (" + thePoints.length + ", " + offset + ", " + size + ") ");
         clear ();
         return;
      }

      firstIndx = offset;
      lastIndx = firstIndx + size - 1;
      points = thePoints;  // "point" to point array

      adjust[0] = new vect3f [size ()];
      adjust[1] = new vect3f [size ()];
   }
   
   public void clear ()
   {
      points = new vect3f [0];
      adjust = new vect3f [2][0];
      firstIndx = 0;
      lastIndx = -1;
   }

   public int curvesCount ()
   {
      return size () - 1;
   }

   public int size ()
   {
      return lastIndx - firstIndx + 1;
   }

   public boolean ptoValido (int indx)
   {
      return (indx >= firstIndx && indx <= lastIndx);
   }

   public void removePoint (int indx)
   {
      if ( ! ptoValido (indx)) return;

      vect3f [] Npoints = new vect3f [size ()-1];
      vect3f [][] Nadjust = new vect3f [2][0];
      Nadjust[0] = new vect3f [size ()-1];
      Nadjust[1] = new vect3f [size ()-1];

      for (int ii = 0; ii < size (); ii ++)
      {
         int off = (ii > indx) ? -1:0;
         Npoints[ii + off] = points[ii + firstIndx];
         Nadjust[0][ii + off] = adjust[0][ii];
         Nadjust[1][ii + off] = adjust[1][ii];
      }

      points = Npoints;
      adjust = Nadjust;
      firstIndx = 0;
      lastIndx = Npoints.length - 1;
   }

   public void tryInsertPoint (int indxInsertion, float x, float y)
   {
      if ( ! ptoValido (indxInsertion)) return;

      // System.out.println ("tryInsertPoint fale...");

      System.out.println ("tryInsertPoint at index " + indxInsertion);

      System.out.println ("tryInsertPoint aplicamos el operativo");
      vect3f [] Npoints = new vect3f [size () + 1];
      vect3f [][] Nadjust = new vect3f [2][0];
      Nadjust[0] = new vect3f [size () + 1];
      Nadjust[1] = new vect3f [size () + 1];

      for (int ii = 0; ii < points.length; ii ++)
      {
         int off = (ii >= indxInsertion) ? +1 : 0;
         Npoints[ii + off] = points[ii + firstIndx];
         Nadjust[0][ii + off] = adjust[0][ii];
         Nadjust[1][ii + off] = adjust[1][ii];
      }
      Npoints[indxInsertion] = new vect3f (x, y);
      Nadjust[0][indxInsertion] = new vect3f (0,0);
      Nadjust[1][indxInsertion] = new vect3f (0,0);

      points = Npoints;
      adjust = Nadjust;
      firstIndx = 0;
      lastIndx = Npoints.length - 1;
   }

   public vect3f  getPoint  (int indx)
   {
      return points[indx + firstIndx];
   }
}