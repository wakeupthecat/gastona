/*
package de.elxala.math.space.curve;
(c) Copyright 2013 Alejandro Xalabarder Aulet

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

import java.util.*;
import de.elxala.math.space.*;
import de.elxala.math.*;
import de.elxala.langutil.*;
import de.elxala.zServices.logger;

public class pointReduction
{
   private static logger log = new logger (null, "elxala.math.space.curve.pointReduction", null);
   private float tolerance2 = 10.f; // square of tolerance in order to be compared with a square distance
   private List arrPoints = new Vector (); // vector<vect3f>

   private static final float POINT_STATE_UNKNOWN = 0.f;
   private static final float POINT_STATE_SELECTED = 1.f;
   private static final float POINT_STATE_DISCARDED = 2.f;
   
   public pointReduction ()
   {
   }

   public pointReduction (float ptolerance)
   {
      setTolerance (ptolerance);
   }

   public void setTolerance (float ptolerance)
   {
      tolerance2 = ptolerance * ptolerance;
   }

   public void clear ()
   {
      arrPoints = new Vector ();
   }
   
   public void addPoints (float [] xyArr)
   {
      for (int ii = 0; ii+1 < xyArr.length; ii += 2)
         addPoint (new vect3f(xyArr[ii], xyArr[ii+1]));
   }

   public void addPoint (float x, float y)
   {
      addPoint (new vect3f(x, y));
   }

   public void addPoint (vect3f pto)
   {
      vect3f p = new vect3f (pto);
      p.z = POINT_STATE_UNKNOWN;
      arrPoints.add (p);
   }
   
   public int getSize ()
   {
      return arrPoints.size ();
   }

   public int totalDiscN = 0;
   public double totalDiscEx = 0.f;
   public double totalDiscEx2 = 0.f;

   public List reducePoints ()
   {
      List sal = new Vector ();
      if (arrPoints.size () == 0) return sal;

      // select first and last
      vecAt (0).z = POINT_STATE_SELECTED;
      vecAt (arrPoints.size ()-1).z = POINT_STATE_SELECTED;
      
      totalDiscEx = 0.f;
      totalDiscEx2 = 0.f;

      while (findStraight ())
      {
         float maxDist = 0.f;
         int nnMaxDist = -1;

         // for the statistic of distances to straight of discarded points
         int n = 0;
         double Ex = 0.f;
         double Ex2 = 0.f;

         if (log.isDebugging (6))
            log.dbg (6, "reducePoints", "findStraight " +  indxX0 + " <--> " + indxX1);
         
         for (int nn = indxX0+1; nn < indxX1; nn ++)
         {
            vect3f evalp = vecAt (nn);
            vect3f closestp = closestPointToSegment (vecAt (indxX0), vecAt (indxX1), evalp);

            float d2 = (closestp.x - evalp.x) * (closestp.x - evalp.x) + (closestp.y - evalp.y) * (closestp.y - evalp.y);
            n ++;
            Ex += Math.sqrt(d2);
            Ex2 += d2;
            //System.out.println ("\n at indx " + nn + " evalpto " + evalp + " closestpt" + closestp + " distance = " + Math.sqrt(d2));

            if (d2 > tolerance2 && d2 > maxDist)
            {
               maxDist = d2;
               nnMaxDist = nn;
            }
         }
         if (nnMaxDist >= 0)
         {
            // found distance > tolerance, select the maximum
            //
            vecAt (nnMaxDist).z = POINT_STATE_SELECTED;
         }
         else
         {
            // not found distance > tolerance, all to do not transmit
            //
            if (n > 0) // must!
            {
               if (log.isDebugging (6))
                  log.dbg (6, "reducePoints", "discard in interval [" +  indxX0 + " - " + indxX1 + "] " + n + " values, distMean = " + 
                               utilMath.round ((float) (Ex/n), 4) + 
                               " sigma " + utilMath.round ((float) Math.sqrt(((Ex2)-(Ex*Ex/n))/n), 4));
               totalDiscEx += Ex;
               totalDiscEx2 += Ex2;
               totalDiscN += n;
            }
            for (int nn = indxX0+1; nn < indxX1; nn ++)
               vecAt (nn).z = POINT_STATE_DISCARDED;
         }
      }

      // copy all reduced points
      //
      for (int nn = 0; nn < arrPoints.size (); nn ++)
      {
         if (vecAt(nn).z == POINT_STATE_SELECTED)
         {
            sal.add (new vect3f(vecAt(nn).x, vecAt(nn).y, nn));
         }
      }

      if (totalDiscN > 0)
      {
         if (log.isDebugging (6))
            log.dbg (6, "reducePoints", "discard TOTAL [0 - " + arrPoints.size() + "]: " + totalDiscN  + 
                        " values, distMean = " + utilMath.round ((float) totalDiscEx/totalDiscN, 4) + 
                        ", sigma " + utilMath.round ((float) Math.sqrt(((totalDiscEx2)-(totalDiscEx*totalDiscEx/totalDiscN))/totalDiscN), 4));
      }
      if (log.isDebugging (2))
      {
         int removed = sal.size () - arrPoints.size ();
         log.dbg (2, "reducePoints", "tolerance2 = " + tolerance2 + " removed " + removed + " points from " + arrPoints.size () + "(" + ((int) (removed * 100.f / arrPoints.size ())) + " %)");
      }


      return sal;
   }
   
   protected int indxX0;
   protected int indxX1;

   protected vect3f closestPointToSegment (vect3f p0, vect3f p1, vect3f p2)
   {
      if (p0.x == p1.x && p0.y == p1.y) // p0 == p1
         return new vect3f (p0);

      //
      //      (y1-y0)(y2-y0) + (x1-x0)(x2-x0)
      // t = --------------------------------
      //         (x1-x0)^2 + (y1-y0)^2
      //
      // equation of the strait line
      //
      // x = x0 + (x1-x0) t
      // y = y0 + (y1-y0) t
      //
      // (in parametrics) r = p0 + (p1-p0) t
      //
      float t = ((p1.y - p0.y) * (p2.y - p0.y) + (p1.x - p0.x) * (p2.x - p0.x)) /
                 ((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y));


      // make sure that return point is on the vector;
      //    t < 0 means the point is placed before p0
      //    t > 1 means the point is placed after p1
      //    t = 0 the point is exactly p0
      //    t = 1 the point is exactly p1
      t = (t > 1.f) ? 1.f : t;
      t = (t < 0.f) ? 0.f : t;

      return new vect3f (p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t);
   }
   
   // base of the reduction algorithm
   // find a segment where are points to be evaluated
   // when there is no segment then the algorithm is finished
   //
   //    for example if arrPoints (U=unknown S=Selected D=Discarded)
   //
   //       0  1  2  3  4  5  6  7  8  9
   //       S  D  D  S  U  U  S  U  U  S
   //
   //    then findStraight returns true and indxX0 = 3 and indxX1 = 6
   //
   protected boolean findStraight ()
   {
      if (arrPoints.size () == 0)
         return false;

      indxX0 = -1;
      indxX1 = -1;

      for (int nn = 0; nn < arrPoints.size (); nn ++)
      {
         vect3f evap = vecAt(nn);
         if (indxX0 == -1)
         {
            if (evap.z == POINT_STATE_UNKNOWN)
               indxX0 = nn - 1;
         }
         else if (evap.z != POINT_STATE_UNKNOWN)
         {
            indxX1 = nn;
            break;
         }
      }

      return indxX0 != -1 && (indxX1 - indxX0) > 1;
   }

   vect3f vecAt (int indx)
   {
      return (vect3f) arrPoints.get(indx);
   }

   public static void main (String [] aa)
   {
      pointReduction argo = new pointReduction ();

      argo.setTolerance (2.0f);
      //argo.clear ();
      argo.addPoint (0, 0);
      argo.addPoint (1, 3);
      argo.addPoint (2, 2);
      argo.addPoint (3, 7);
      argo.addPoint (4, 6);
      argo.addPoint (5, 4);
      List redu = argo.reducePoints ();

      for (int ii = 0; ii < redu.size (); ii ++)
      {
         vect3f repo = (vect3f) redu.get(ii);
         System.out.println ("\n" + repo.z + ") " + repo.x + ", " + repo.y);
      }
   }
}


