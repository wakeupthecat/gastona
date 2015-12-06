/*
packages de.elxala
Copyright (C) 2005 Alejandro Xalabarder Aulet

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

package javaj.widgets.graphics;

import de.elxala.math.space.*;

/**
   @author Alejandro Xalabarder
   @date   2011
   @lastupdate   07.07.2011 22:45
*/
class scaledRectD
{
   private uniRectD recReal  = new uniRectD (0., 0., 1000., 1000.);
   private uniRectD recOnStartGesture  = new uniRectD (0., 0., 1000., 1000.);
   private double scaleX = 1.;
   private double scaleY = 1.;

   private double scaleXOnStartGesture = 1.;
   private double scaleYOnStartGesture = 1.;

   private int pixels_dx = 100;
   private int pixels_dy = 100;

   private boolean YPOSUP = false;

   public scaledRectD (boolean yPositiveUp)
   {
      YPOSUP = yPositiveUp;
      if (YPOSUP)
         recReal  = new uniRectD (-6., 2.5, 6., -2.5);
   }

   public scaledRectD (boolean yPositiveUp, int dxPixels, int dyPixels, uniRectD rec)
   {
      pixels_dx = dxPixels;
      pixels_dy = dyPixels;

      recReal = new uniRectD (rec);
      calcScale ();
   }

   public void setRectangleReal (uniRectD rec)
   {
      recReal = new uniRectD(rec);
      calcScale ();
   }

   public void setRectangleReal (double left, double top, double right, double bottom)
   {
      recReal = new uniRectD(left, top, right, bottom);
      calcScale ();
   }

   public void setSizeInPixels (int dx, int dy)
   {
      pixels_dx = dx;
      pixels_dy = dy;
      calcScale ();
   }

   protected void calcScale ()
   {
      scaleX = pixels_dx == 1 ? 1.: dx() / (pixels_dx-1);
      scaleY = pixels_dy == 1 ? 1.: dy() / (pixels_dy-1);
   }

   public double minX ()  { return recReal.left ();  }
   public double maxX ()  { return recReal.right ();  }
   public double minY ()  { return YPOSUP ? recReal.top (): recReal.bottom ();  }
   public double maxY ()  { return YPOSUP ? recReal.bottom (): recReal.top (); }

   public double dx ()  { return recReal.width ();  }
   public double dy ()  { return recReal.height (); }

   public double scaleX ()   { return scaleX; }
   public double scaleY ()   { return scaleX; }

   public int xReal2pixel (double xx)
   {
      return (int) ((xx - recReal.left ()) / scaleX);
   }

   /**
      y in pixels is always "y positive downwards"
      the same as the coordinates given by mouse and touch events
   */
   public int yReal2pixel (double yy)
   {
      double incY = YPOSUP ? (recReal.top () - yy): (yy - recReal.top ());
      return (int) (incY / scaleY);
   }


   public double xPixel2real (int pixelsx)
   {
      return recReal.left () + (double) pixelsx * scaleX;
   }

   /**
      y in pixels is always "y positive downwards"
      the same as the coordinates given by mouse and touch events
   */
   public double yPixel2real (int pixelsy)
   {
      if (YPOSUP)
         return recReal.top () - (double) pixelsy * scaleY;
      else
         return recReal.bottom ()  + (double) pixelsy * scaleY;
   }

   public void setReference4Gesture ()
   {
      //android.util.Log.d ("soom", "   setReference4Gesture minX maxX minY maxY " + minX + ", " + maxX + ", " + minY + ", " + maxY);
      recOnStartGesture = new uniRectD (recReal);

      scaleXOnStartGesture = scaleX;
      scaleYOnStartGesture = scaleY;
   }

   public void relativeTranslation(vect3f p1, vect3f p2)
   {
      double desplazaX = (p1.x - p2.x) * scaleXOnStartGesture;
      double desplazaY = (p2.y - p1.y) * scaleYOnStartGesture;

      setRectangleReal
            (
            recReal.left() + desplazaX,
            recReal.top() + desplazaY,
            recReal.right() + desplazaX,
            recReal.bottom() + desplazaY
            );
   }

////////   /**
////////      We give to rectangles in pixels, the first one is the reference and the second one the
////////      desired result of the reference (tipically by a pinch or spread multi-touch)
////////      Optionally we can sqare the rectagles in order to have no distorsion
////////   */
////////   public void zoomRectangular (Rect rRef, Rect rDest, boolean square)
////////   {
////////      // add 0.01 to avoid raise conditions (factor 0 or infinite)
////////      double facZoomX = (rRef.right - rRef.left + .01) / (rDest.right - rDest.left + .01);
////////      double facZoomY = (rRef.top - rRef.bottom + .01) / (rDest.top - rDest.bottom + .01);
////////
////////      android.util.Log.d ("soom", "facZoomX Y " + facZoomX + ", " + facZoomY);
////////
////////      if (square)
////////      {
////////         facZoomX = Math.max (facZoomX, facZoomY);
////////         facZoomY = facZoomX;
////////      }
////////
////////      // center in pixels of rectangle ref
////////      //int cx = rRef.right + (int) ((rRef.right - rRef.left) / 2);
////////      //int cy = rRef.bottom + (int) ((rRef.top - rRef.bottom) / 2);
////////      int cx = (int) ((rRef.right - rRef.left) / 2);
////////      int cy = (int) ((rRef.top - rRef.bottom) / 2);
////////
////////      android.util.Log.d ("soom", "   cx cy " + cx + ", " + cy);
////////
////////      // the Yreference and Xref has to be the same after scaling
////////      double Xref = refMinX - cx * refScaleX;
////////      double Yref = refMinY - cy * refScaleY;
////////
////////      android.util.Log.d ("soom", "   Xref Yref " + Xref + ", " + Yref);
////////
////////      double minX1 = Xref + cx * facZoomX * refScaleX;
////////      double minY1 = Yref + cy * facZoomY * refScaleY;
////////
////////      double maxX1 = minX1 + (refMaxX - refMinX) * facZoomX;
////////      double maxY1 = minY1 + (refMaxY - refMinY) * facZoomY;
////////
////////      zoom (minX1, maxX1, minY1, maxY1);
////////   }

   public void changeAspect (double incWidth, double incHeight)
   {
      double incX = (incWidth - 1.) * recReal.width () * .5;
      double incY = (incHeight - 1.) * recReal.height () * .5;

      setRectangleReal
            (
            recReal.left() - incX,
            recReal.top() + (YPOSUP ? 1:-1) * incY,
            recReal.right() + incX,
            recReal.bottom() - (YPOSUP ? 1:-1) * incY
            );
   }

   /**
      sets the X/Y aspect ratio to 1.
   */
   public void squareAspect ()
   {
      //             A * a
      //            -------- = 1
      //             B * b
      //
      //             a/b  = B/A = r
      //
      //             B/A = (1+e)/(1-e)
      //
      //            r - 1 = (r + 1) * e
      //
      //            e = (r-1)/(r+1)

//      System.err.println ("rangoX " + rangX () + " en " + marco_dx + " pisels me a rraza " + xppix);
//      System.err.println ("rangoY " + rangY () + " en " + marco_dy + " pisels me a rraza " + yppix);
//      System.err.println ("a racion r eche " + yppix/xppix);

      double r = scaleY / scaleX; // r = 1/(A*B)

      if (r == 1.) return; // it is ok!

      double e = (r-1.) / (r+1.);

      changeAspect (1.+e, 1.-e);
   }

}
