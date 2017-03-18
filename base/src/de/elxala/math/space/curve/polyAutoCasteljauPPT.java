/*
package de.elxala.math.space.curve;
(c) Copyright 2006,2017 Alejandro Xalabarder Aulet

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

package de.elxala.math.space.curve;

import de.elxala.math.space.*;   // vect3f

/**
 *
 */
public class polyAutoCasteljauPPT extends polyAutoCasteljau
{
   static private float MISTAD = .5f;

   public polyAutoCasteljauPPT (curveWithAdjustPoint curve)
   {
      super (curve);
   }

   public polyAutoCasteljauPPT (vect3f [] thePoints)
   {
      super (thePoints);
   }

   public vect3f[] internGetControlPoints (vect3f p1, vect3f p2, vect3f p3)
   {
      return getControlPoints (p1, p2, p3);
   }

   public static float arreglo = -1.f; // either automatic (-1) or an adjust value between 0 and 1
   
   static public vect3f[] getControlPoints (vect3f p1, vect3f p2, vect3f p3)
   {
      vect3f[] ctrlPoints = new vect3f[2];
      
      vect3f v1 = new vect3f (p1, p2);
      vect3f v2 = new vect3f (p3, p2);

      vect3f dir = vect3f.minus (v1, v2);
      
      float n1 = v1.norm ();
      float n2 = v2.norm ();

      float raso = 10.f;
      float fator = (n1 + n2) * (n1 + n2);
      if (fator != 0.f)
      dir.mult (1f / fator);

      // faco1 makes the curve very curly in edges
      float faco1 = n1 > n2 ? n1 * n1: n2 * n2;

      // faco2 makes the curve quite flat
      float faco2 = n1 * n2;

      // atomatic arreglo : the more difference in distance the more faco2, if not more faco1
      float prop = n2 > 0 ? n1 / n2: 1.f;
      if (arreglo < 0.f) arreglo = (prop > 1.f ? 1.f/prop: prop);

      float faco = arreglo * faco1 + (1 - arreglo) * faco2;
      dir.mult (MISTAD * faco);
      
      ctrlPoints[0] = new vect3f (p2);
      ctrlPoints[1] = new vect3f (p2);

      ctrlPoints[0].minus (dir);
      ctrlPoints[1].plus  (dir);

      //System.out.println ("ACH minclinas " + p1 + ", " + p2 + ", " + p3 + " responso " + ctrlPoints[0] + ", " + ctrlPoints[1]);

      return ctrlPoints;
   }
}

/*
   float glanglo = v1.angleDegrees(v2);
   if (glanglo < 100.f)
   {
      if (v1.norm2 () > v2.norm2 ())
           dir.mult (MISTAD * v1.norm () * v1.norm ());
      else dir.mult (MISTAD * v2.norm () * v2.norm ());
   }
   else
     dir.mult (MISTAD * v1.norm () * v2.norm ());

z ,313, 413, "", //jau,-57,-66,83,-129,-22,-79,531,-48,-380,214,-115,-53,-72,102,105,32,44,99,-80,26,-37,-97

     
z ,68, 278, "", //jau,14,-39,6,-166,8,-13,177,-5
z ,372, 270, "", //jau,-22,-121,-1,-65,13,-5,8,13,53,113
z ,519, 274, "", //jau,-8,-40,13,-12,13,2,11,-26,-39,-22,-4,-27,13,-12,27,6,9,-13,-16,-21,-26,-2,-13,-26,19,-18,52,7,-2,-13,-41,-25
z ,591, 292, "", //jau,14,-70,31,-31,26,9,8,13,26,92,26,-3,25,-39,13,-107
z ,112, 524, "", //jau,63,-65,56,-39,109,-35,-36,31,1,40,50,50,27,11,26,-2,112,-50,29,-3,69,23,80,47,44,7,107,-40,50,-46


caballo            

z ,538, 221, "piel", //jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81
z ,496, 323, "piel", //jau,-43,-81,-10,-36,9,-19,39,8,87,54
z ,458, 182, "piel", //jau,-50,9,-34,48,-16,29,20,19,30,-23,40,-17
z ,768, 248, "piel", //jau,26,22,14,27,1,46,-12,47,21,45,-17,102,-16,19,-17,-12,16,-25,-9,-90,-43,-64,-9,-77
z ,496, 313, "piel", //jau,4,52,30,34,17,73,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-3,-51,4,-120
z ,788, 260, "piel", //jau,37,14,22,51,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14
z ,428, 183, "piel", //jau,-22,-23,0,27


z ,33, 595, "", //jau,353,-379,302,-157,13,26
z ,450, 377, "", //jau,13,-188,21,-8,125,-1,9,25,4,131,-23,11,-27,-17,-54,4,-12,96,22,18,35,-53,13,2,9,65,13,-1,36,-51,13,-1,47,82
z ,534, 424, "", //jau,-21,-138,27,-30,169,0,10,48,9,101,-22,33,-147,-2,-23,-162,159,1,9,42,-1,89,-30,15,-103,-5,-23,-107,13,-25,119,8,9,29,-2,75,-105,4,-12,-91,13,-10,79,3,9,13,-2,65,-13,8,-52,-2,-11,-13,-2,-52,14,-6,52,8,0,39

*/



