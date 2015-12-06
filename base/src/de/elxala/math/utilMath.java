/*
java package de.elxala.math
Copyright (C) 2012  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.math;

public class utilMath
{
   public static final float PI = (float) Math.PI;
   public static final float HALF_PI = (float) (Math.PI / 2.);


   public static float toRadianf (float degrees)
   {
      return degrees * PI / 180.f;
   }
   
   public static float toDegreef (float radians)
   {
      return radians * 180.f / PI;
   }
   
   public static final float LITTLE_VALUE = 0.0000000001f;
   
   public static boolean quasi_equal (float x, float t) 
   {
      return (float) Math.abs (x-t) < LITTLE_VALUE;
   }
   
   public static float clipf (float num, float mini, float maxi)
   {
      if (num >= mini && num <= maxi)
         return num;
      return (num < mini) ? mini: maxi;
   }

   public static float q_asinf (float x)
   {
      // Arcoseno evitando x=1
      
       if (quasi_equal(x, 1f))
           return x * HALF_PI;
       else
           return (float) Math.atan(x / Math.sqrt(1 - x * x));
   }

   public static float q_atan2f (float y, float x)
   {
      // Arcotangente: retorna valor entre 0 y 2 Pi
      
       float aux = 0.f;
       if (quasi_equal(x, 0.f))
            aux = HALF_PI + ((y < 0) ? PI: 0.f);
       else
            aux = (float) Math.atan (y / x) + ((x < 0) ? PI: 0.f);

      if (aux < 0)
         aux += 2.f * PI;
         
      return aux;
   }

   // Redondeo con n° decimales
   public static float redondeof (float num, int ndec)
   {
       double fact = Math.pow (10, ndec);
       return (float) ((double) Math.round (num * fact) / fact);
   }
}
