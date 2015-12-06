/*
package de.elxala.math.space.curve;
(c) Copyright 2006,2011 Alejandro Xalabarder Aulet

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

package de.elxala.math.space;

/*
   //(o) elxala_math class for vector 3D float
 Alejandro Xalabarder  1999    (versiones al final del fichero)

   Funciones matema'ticas ba'sicas para ca'lculo de vectores (3D)
*/

public class vect3f
{
   public static final float HALF_PI = (float) Math.PI / 2.f;

   public static final vect3f i = new vect3f (1, 0, 0);
   public static final vect3f j = new vect3f (0, 1, 0);
   public static final vect3f k = new vect3f (0, 0, 1);

   public float x, y, z;

   public vect3f ()
   {
      x = 0;
      y = 0;
      z = 0;
   }

   public vect3f (vect3f cop)
   {
      if (cop == null) return;
      x = cop.x;
      y = cop.y;
      z = cop.z;
   }

   public vect3f (float px, float py, float pz)
   {
      x = px;
      y = py;
      z = pz;
   }

   public vect3f (boolean spherics, float r, float teta, float phi)
   {
      if (!spherics)
      {
         x = r;
         y = teta;
         z = phi;
         return;
      }
      float rsinPhi = r * (float) Math.sin(phi);
      x = rsinPhi * (float) Math.cos (teta);
      y = rsinPhi * (float) Math.sin (teta);
      z = r * (float) Math.cos(phi);
   }

   // for 2d
   public vect3f (float px, float py)
   {
      x = px;
      y = py;
      z = 0;
   }

   // for 2d
   public vect3f (boolean polar, float r, float teta)
   {
      if ( ! polar)
      {
         x = r;
         y = teta;
         return;
      }
      x = r * (float) Math.cos (teta);
      y = r * (float) Math.sin (teta);
   }

   public vect3f (vect3f pto1, vect3f pto2)
   {
      x = pto2.x - pto1.x;
      y = pto2.y - pto1.y;
      z = pto2.z - pto1.z;
   }

   public void set (vect3f vref)
   {
      x = vref.x;
      y = vref.y;
      z = vref.z;
   }

   public void set (float px, float py, float pz)
   {
      x = px;
      y = py;
      z = pz;
   }

   public void set (float px, float py)
   {
      x = px;
      y = py;
      z = 0.f;
   }

   public float norm ()
   {
      return (float) Math.sqrt (x * x + y * y + z * z);
   }

   public float norm2 ()
   {
      return (float) (x * x + y * y + z * z);
   }

   public void normalize (float distance)
   {
      float norma = norm ();

      if (norma > 0.)
      {
         if (distance == 0)
         {
            x = 0;
            y = 0;
            z = 0;
         }
         else
         {
            norma /= distance;
            x /= norma;
            y /= norma;
            z /= norma;
         }
      }
   }

   public void normalize ()
   {
      normalize (1.f);
   }

   // square of the distance
   public float distance2 (vect3f v1, vect3f v2)
   {
      return minus (v1, v2).norm2 ();
   }

   public float distance (vect3f v1, vect3f v2)
   {
      return minus (v1, v2).norm ();
   }

   // square of the distance
   public float distance2 (vect3f ve)
   {
      return distance2 (this, ve);
   }

   public float distance (vect3f ve)
   {
      return distance (this, ve);
   }

   // faster than using distance
   public static boolean inRadius (vect3f v1, vect3f v2, float radius)
   {
      if (v1 == null || v2 == null) return false;

      // if this is true, we save a calculation
      if ((v1.x - v2.x > radius) ||
          (v1.y - v2.y > radius))
         return false;

      // less than half of the side of a square inside the circle
      float insquare = 0.707f * radius;
      if ((v1.x - v2.x < insquare) &&
          (v1.y - v2.y < insquare))
         return true;

      return minus (v1, v2).norm () <= radius;
   }

   // faster than using distance
   public boolean inRadius (vect3f ve, float radius)
   {
      return inRadius (this, ve, radius);
   }

   public static float prod_scalar (vect3f v1, vect3f v2)
   {
      return v1.x * v2.x + v1.y * v2.y + v1.z * v1.z;
   }

   public float prod_scalar (vect3f v1)
   {
      return v1.x * x + v1.y * y + v1.z * z;
   }

   public static vect3f prod_vectorial (vect3f v1, vect3f v2)
   {
      return new vect3f
            (v1.y * v2.z - v1.z * v2.y,
             v1.z * v2.x - v1.x * v2.z,
             v1.x * v2.y - v1.y * v2.x);
   }

   public void prod_vectorial (vect3f v1)
   {
      x = y * v1.z - z * v1.y;
      y = z * v1.x - x * v1.z;
      z = x * v1.y - y * v1.x;
   }

   public float angleDegrees (vect3f v2)
   {
      return angle (v2) * 180.f / (float) Math.PI;
   }
   
   public float angle (vect3f v2)
   {
      float n1 = norm ();
      float n2 = v2.norm ();

       if (n1 == 0. || n2 == 0.)
            return (float) Math.PI / 2.f;
       else
            //return acos((x * v2.x + y * v2.y + z * v2.z) / (n1 * n2));
            return (float) Math.acos(prod_scalar (v2) / (n1 * n2));
   }

   public float phi ()
   {
      return (float) Math.atan2 ((float) Math.sqrt (x * x + y * y), z);
   }

   public float theta ()
   {
      return (float) Math.atan2 (y, x);
   }

   public float radio ()
   {
      return norm ();
   }

   public static vect3f plus (vect3f v1, vect3f v2)
   {
      return new vect3f (v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
   }

   public static vect3f neg (vect3f v1)
   {
      return new vect3f (-v1.x, -v1.y, -v1.z);
   }

   public void plus (vect3f v1)
   {
      x += v1.x;
      y += v1.y;
      z += v1.z;
   }

   public static vect3f minus (vect3f v1, vect3f v2)
   {
      return new vect3f (v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
   }

   public void minus (vect3f v1)
   {
      x -= v1.x;
      y -= v1.y;
      z -= v1.z;
   }

   public static vect3f mult (vect3f v1, float escalar)
   {
      return new vect3f (v1.x * escalar, v1.y * escalar, v1.z * escalar);
   }

   // facility
   public void mult (double escalar)
   {
      mult ((float) escalar);
   }
   
   public void mult (float escalar)
   {
      x *= escalar;
      y *= escalar;
      z *= escalar;
   }

   public static vect3f div (vect3f v1, float escalar)
   {
      // user should check escalar != 0.
      return new vect3f (v1.x / escalar, v1.y / escalar, v1.z / escalar);
   }

   public void div (float escalar)
   {
      // user should check escalar != 0.
      x /= escalar;
      y /= escalar;
      z /= escalar;
   }

   public boolean equals (vect3f v)
   {
      return ((x == v.x) &&
              (y == v.y) &&
              (z == v.z));
   }

   public String str ()
   {
      return toString ();
   }
   
   public String toString ()
   {
      return "(" + x + ", " + y + ", " + z + ")";
   }
}
