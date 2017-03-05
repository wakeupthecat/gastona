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
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package de.elxala.math.space;

/*
   //(o) elxala_math class for vector 3D double
 Alejandro Xalabarder  1999    (versiones al final del fichero)

   Funciones matema'ticas ba'sicas para ca'lculo de vectores (3D)
*/

public class vect3
{
   public static final double HALF_PI = Math.PI / 2;


   public static final vect3 i = new vect3(1., 0., 0.);
   public static final vect3 j = new vect3(0., 1., 0.);
   public static final vect3 k = new vect3(0., 0., 1.);

   public double x, y, z;

   public vect3 ()
   {
      x = 0.;
      y = 0.;
      z = 0.;
   }

   public vect3 (double px, double py, double pz)
   {
      x = px;
      y = py;
      z = pz;
   }

   public vect3 (boolean espherics, double r, double teta, double phi)
   {
      if ( ! espherics)
      {
         x = r;
         y = teta;
         z = phi;
         return;
      }
      double rsinPhi = r * Math.sin(phi);
      x = rsinPhi * Math.cos (teta);
      y = rsinPhi * Math.sin (teta);
      z = r * Math.cos(phi);
   }

   // for 2d
   public vect3 (double px, double py)
   {
      x = px;
      y = py;
      z = 0.;
   }

   // for 2d
   public vect3 (boolean polar, double r, double teta)
   {
      if ( ! polar)
      {
         x = r;
         y = teta;
         return;
      }
      x = r * Math.cos (teta);
      y = r * Math.sin (teta);
   }

   public vect3 (vect3 pto1, vect3 pto2)
   {
      x = pto2.x - pto1.x;
      y = pto2.y - pto1.y;
      z = pto2.z - pto1.z;
   }

   public double norm ()
   {
      return Math.sqrt (x * x + y * y + z * z);
   }

   public void normalize ()
   {
      double norma = norm ();

      if (norma > 0.)
      {
         x /= norma;
         y /= norma;
         z /= norma;
      }
   }

   public static double distance (vect3 v1, vect3 v2)
   {
      return minus (v1, v2).norm ();
   }

   public double distance (vect3 ve)
   {
      vect3 aux = new vect3 (x, y, z);

      aux.minus (ve);
      return aux.norm ();
   }

   public static double dotProduct (vect3 v1, vect3 v2)
   {
      return v1.x * v2.x + v1.y * v2.y + v1.z * v1.z;
   }

   public double dotProduct (vect3 v1)
   {
      return v1.x * x + v1.y * y + v1.z * z;
   }

   public static double dot            (vect3 v1, vect3 v2)  { return dotProduct (v1, v2); }
   public        double dot            (vect3 v1)            { return dotProduct (v1); }
   public static double prod_scalar    (vect3 v1, vect3 v2)  { return dotProduct (v1, v2); }
   public        double prod_scalar    (vect3 v1)            { return dotProduct (v1); }

   public static vect3 crossProduct (vect3 v1, vect3 v2)
   {
      return new vect3
            (v1.y * v2.z - v1.z * v2.y,
             v1.z * v2.x - v1.x * v2.z,
             v1.x * v2.y - v1.y * v2.x);
   }

   public void crossProduct (vect3 v1)
   {
      x = y * v1.z - z * v1.y;
      y = z * v1.x - x * v1.z;
      z = x * v1.y - y * v1.x;
   }

   public static vect3 cross          (vect3 v1, vect3 v2) { return crossProduct (v1, v2); }
   public        void  cross          (vect3 v1)           { crossProduct (v1); }
   public static vect3 prod_vectorial (vect3 v1, vect3 v2) { return crossProduct (v1, v2); }
   public        void  prod_vectorial (vect3 v1)           { crossProduct (v1); }

   public double angle (vect3 v2)
   {
      double n1 = norm ();
      double n2 = v2.norm ();

       if (n1 == 0. || n2 == 0.)
            return (double) HALF_PI;
       else
            //return acos((x * v2.x + y * v2.y + z * v2.z) / (n1 * n2));
            return (double) Math.acos(dotProduct (v2) / (n1 * n2));
   }

   public double phi ()
   {
      return (double) Math.atan2 (Math.sqrt (x * x + y * y), z);
   }

   public double theta ()
   {
      return (double) Math.atan2 (y, x);
   }

   public double radio ()
   {
      return norm ();
   }

   public static vect3 plus (vect3 v1, vect3 v2)
   {
      return new vect3 (v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
   }

   public static vect3 neg (vect3 v1)
   {
      return new vect3 (-v1.x, -v1.y, -v1.z);
   }

   public void plus (vect3 v1)
   {
      x += v1.x;
      y += v1.y;
      z += v1.z;
   }

   public static vect3 minus (vect3 v1, vect3 v2)
   {
      return new vect3 (v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
   }

   public void minus (vect3 v1)
   {
      x -= v1.x;
      y -= v1.y;
      z -= v1.z;
   }

   public static vect3 mult (vect3 v1, double escalar)
   {
      return new vect3 (v1.x * escalar, v1.y * escalar, v1.z * escalar);
   }

   public void mult (double escalar)
   {
      x *= escalar;
      y *= escalar;
      z *= escalar;
   }

   public static vect3 div (vect3 v1, double escalar)
   {
      // user should check escalar != 0.
      return new vect3 (v1.x / escalar, v1.y / escalar, v1.z / escalar);
   }

   public void div (double escalar)
   {
      // user should check escalar != 0.
      x /= escalar;
      y /= escalar;
      z /= escalar;
   }

   public boolean equals (vect3 v)
   {
      return ((x == v.x) &&
              (y == v.y) &&
              (z == v.z));
   }
}
