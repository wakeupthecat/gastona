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

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.math.space.*;

/**
*/

   //
   //    <:PIZ Frame>
   //        -14.1, 14.1               minX, maxX
   //         -2.6, 2.6                minY, maxY
   //         300, 600                 dx, dy
   //           m, 10e-2,  -14, 1, 0
   //        watt, 10e-6,  -2.5, .2, 0
   //       polar, 0, 100                    coord. polares  tmin=0 tmax=100
   //
   //       unitsX, minRayX, incX, nFirstLargaX
   //       unitsY, minRayY, incY, nFirstLargaY

/**
   @author Alejandro Xalabarder
   @date   2005
   @lastupdate   10.11.2005 22:15

   Class specialized in drawing coordinate systems, it handles the needed
   data structure too.

*/
class mathFunctionFrame
{
   public static final String COORSYS_CARTESIAN   = "cartesianas";
   public static final String COORSYS_POLAR       = "polares";
   public static final String COORSYS_PARAMETRICS = "parametricas";

   //public static int MAX_PTOS_X = 1300;   // ojo! si ponemos "final" habra' que compilar TODAS las clases que lo usan para que se enteren!!!
   public static int MAX_PRECI1_X = 300;
   public static int MAX_PRECI2_X = 700;
   public static int MAX_PRECI3_X = 2000;
   public static int DESIRED_PRECI = 2;   // 1: Draft 2:Normal 3:Extrem
// public static int MAX_PTOS_X = 6;

   public double minX = -6., maxX = +6., minY = -2.5, maxY = 2.5;
   public double scaleX=1., scaleY=1.;

   public int marco_dx = 100, marco_dy = 100;
   public double tMin = -10., tMax = +10.;
   public String CoordSys = COORSYS_CARTESIAN;

   public String unitsX = "", unitsY = "", unitsT = "";   // p.e. "m"
   public String sExpoX = "", sExpoY = "", sExpoT = "";   // p.e. "10e-2"

   public double rangX ()
   {
      return maxX - minX;
   }

   public double rangY ()
   {
      return maxY - minY;
   }

   public int toPixelX (double xx)
   {
      return (int) ((xx - minX) / scaleX);
   }

   public int toPixelY (double yy)
   {
      return (int) ((maxY - yy) / scaleY);
   }


   public double toRealX (int pixelsx)
   {
      return minX + (double) pixelsx * scaleX;
   }

   public double toRealY (int pixelsy)
   {
      return maxY - (double) pixelsy * scaleY;
   }

   private void recalculaEscalas ()
   {
      if ((maxX - minX) == 0. )  maxX = minX + 0.1;
      if ((maxY - minY) == 0. )  maxY = minY + 0.1;
      if ((maxX - minX) == 0. || (maxY - minY) == 0.)
      {
         System.err.println ("CrasoError (169);");
         return;
      }
      if ((marco_dx-2) == 0 || (marco_dy-2) == 0)
      {
         System.err.println ("CrasoError (172);");
         return;
      }

      scaleX = (double) ((maxX - minX) / (marco_dx-2));
      scaleY = (double) ((maxY - minY) / (marco_dy-2));
   }

   public void zoom (double miX, double maX, double miY, double maY)
   {
      //android.util.Log.d ("soom", "   minX maxX minY maxY " + miX + ", " + maX + ", " + miY + ", " + maY);

      minX = miX;
      maxX = maX;
      minY = miY;
      maxY = maY;
      recalculaEscalas ();
   }

   private double refMinX = 0., refMaxX = 0., refMinY = 1., refMaxY = 1.;
   private double refScaleX = 1., refScaleY = 1.;

   public void setReference4Gesture ()
   {
      //android.util.Log.d ("soom", "   setReference4Gesture minX maxX minY maxY " + minX + ", " + maxX + ", " + minY + ", " + maxY);
      refMinX = minX;
      refMaxX = maxX;
      refMinY = minY;
      refMaxY = maxY;

      refScaleX = scaleX;
      refScaleY = scaleY;
   }


   public void relativeTranslation(vect3f p1, vect3f p2)
   {
      //android.util.Log.d ("soom", "   translation ...");

      // add 0.01 to avoid raise conditions (factor 0 or infinite)
      double desplazaX = (p1.x - p2.x) * refScaleX;
      double desplazaY = (p2.y - p1.y) * refScaleY;

      zoom (refMinX + desplazaX, refMaxX + desplazaX,
            refMinY + desplazaY, refMaxY + desplazaY);
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

   public void set_dx_dy (int dx, int dy)
   {
      marco_dx = dx;
      marco_dy = dy;
      recalculaEscalas ();
   }


   public void changeAspect (double incWidth, double incHeight)
   {
      double incX = (incWidth - 1.) * rangX () * .5;
      minX -= incX;
      maxX += incX;

      double incY = (incHeight - 1.) * rangY () * .5;
      minY -= incY;
      maxY += incY;
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

      double xppix = rangX () / marco_dx;
      double yppix = rangY () / marco_dy;

//      System.err.println ("rangoX " + rangX () + " en " + marco_dx + " pisels me a rraza " + xppix);
//      System.err.println ("rangoY " + rangY () + " en " + marco_dy + " pisels me a rraza " + yppix);
//      System.err.println ("a racion r eche " + yppix/xppix);

      double r = yppix / xppix; // r = 1/(A*B)

      if (r == 1.) return; // it is ok!

      double e = (r-1.) / (r+1.);

      changeAspect (1.+e, 1.-e);
   }



   // =====================================
   // =====================================
   // =====================================

   private static final int MUCHOSx = 100;
   private static final int MUCHOSy = 60;
   private static final int POCOSx  = 15;
   private static final int POCOSy  = 10;

   private static double fact_log10 = 1 / Math.log(10);

   private double log10 (double x)
   {
      return Math.log (x) * fact_log10;
   }


   private double resto (double a, double d)
   {
      double redo = Math.floor (d * .00001 + a / d);
      double modo = redo * d;
      return Math.abs (a - modo);
   }

   private int expon (double num)
   {
      double logmargen = log10 (Math.abs (num));
      int logint, sig=1;

      if (logmargen < 0)
      {
         logint = 2 - (int) logmargen;
         sig = -1;
      }
      else logint = (int) logmargen;

      logint /= 3.;
      logint *= 3.;
      return (logint * sig);
   }

   private double round (double x, int dec)
   {
      double centos = Math.pow (10., dec);
      double plus = .5 / centos;
      if (x < 0.) plus *= -1;

      int capa = (int) ((x + plus) * centos);

      return capa / centos;
   }

   // private static int maxtamray = 1 + CarPixY / 2;
   private static final int maxtamray = 12;
   private static final int mitadMaxtamray = maxtamray/2;

   /**
      main method of the class that draws a coordinate
      either horizontal or vertical
   */
   public void drawCoordenada (uniCanvas can, uniPath pai, boolean Horiz)
   {
      double maxo = (Horiz) ? maxX: maxY;
      double mino = (Horiz) ? minX: minY;

      int H2 = can.getTextHeight (pai) / 2;

      //  Escribir unidades con exponente
      //
      String sexpo = "";
      int iexpo = expon (maxo - mino);

      if (iexpo >= 2 || iexpo < -1)
      {
         sexpo = " x10" + ((iexpo > 0) ? "+" : " ") + iexpo;
      }
      if (Horiz)
           sExpoX = sexpo;
      else sExpoY = sexpo;

      //  calc_razon
      //
      double maxim = maxo - mino;
      double paso = Math.pow (10., (int) log10 (maxim) - 1);
      if (paso == 0.)
      {
         System.err.println ("CrasoError (498);");
         return;
      }

      while (maxim / paso > ((Horiz) ? MUCHOSx: MUCHOSy)) paso *= 10.;
      if    (maxim / paso < ((Horiz) ? POCOSx : POCOSy))  paso /= 5.;

      double tok = ((int) ((mino + paso) / paso)) * paso;
      //
      //

      if (tok < mino) tok = mino;

      boolean  cada_5  = ((maxo - mino) / (10. * paso) < 3.);
      boolean  cada_10 = ((maxo - mino) / ( 5. * paso) < 2.);

      while (tok < maxo)
      {
         int quinto = (resto (tok,  5. * paso) < (.9 * paso)) ? 1: 0;
         int decimo = (resto (tok, 10. * paso) < (.9 * paso)) ? 1: 0;

         int xpix, ypix;
         int tamray = 2 * quinto + (decimo + 1) * mitadMaxtamray;
         if (Horiz)
         {
            xpix = toPixelX (tok);
            ypix = toPixelY (0) + tamray / 2;
            if (ypix < 0 || ypix > marco_dy)
               ypix = marco_dy;
         }
         else
         {
            xpix = toPixelX (0) - tamray / 2;
            ypix = toPixelY (tok);
            if (xpix < 0 || xpix > marco_dx)
               xpix = 0;
         }

         if (Horiz)
              can.drawLine (xpix, ypix, xpix         , ypix - tamray, pai);
         else can.drawLine (xpix, ypix, xpix + tamray, ypix         , pai);

         //  escribir numerito
         String aus = "";

         if (decimo == 1 || cada_10 || (cada_5 && quinto == 1))
         {
            if (iexpo > 1 || iexpo < -1)
               aus = "" + round (tok / Math.pow (10., iexpo), 2); // redondear a 2

            if (Math.abs (iexpo) < 2)
            {
               if (maxo - mino > 3)
                    aus = "" + round(tok,0);   //  %4.0f
               else aus = "" + round(tok,2);   //  %5.2f
            }
            while (aus.length () > 0 && aus.charAt(0) == ' ') aus = aus.substring (1);
            int x, y;
            if (Horiz)
            {
               //calculate text's width
               int W2 = can.getTextWidth (aus, pai) / 2;

               x = xpix - W2;
               y = ypix - maxtamray - 4;

               // si no cabe ... nada
               if (x + W2 > marco_dx) x = -1;
            }
            else
            {
               x = xpix + maxtamray + 4;
               y = ypix + H2;

               // si no cabe ... nada
               if (y + H2 > marco_dy) y = -1;
            }
            if (x >= 0 && y >= 0 && !aus.equals ("0.0"))
               can.drawText (aus, x, y, pai);

            // tira_linea (pg, tok, Horiz, (tok==0) ? SOLID_LINE :DOTTED_LINE);
        }
        tok += paso;
     }
   }


   // =====================================
   // =====================================
   // =====================================

   /**
      deserialize data structure
   */
   public void fromEva (Eva eva)
   {
      if (eva.rows () < 4) return;

      minX = stdlib.atof (eva.getValue (0,0));
      maxX = stdlib.atof (eva.getValue (0,1));
      unitsX = eva.getValue (0, 2);

      minY = stdlib.atof (eva.getValue (1,0));
      maxY = stdlib.atof (eva.getValue (1,1));
      unitsY = eva.getValue (1, 2);

      tMin = stdlib.atof (eva.getValue (2,0));
      tMax = stdlib.atof (eva.getValue (2,1));
      unitsT = eva.getValue (2, 2);
      if (tMin == tMax && tMax == 0.f)
      {
         tMin = -100.;
         tMax = 100.;
      }

      marco_dx = stdlib.atoi (eva.getValue (3,0));
      marco_dy = stdlib.atoi (eva.getValue (3,1));

      CoordSys = eva.getValue (4,0);
      if (CoordSys.trim ().length () == 0)
         CoordSys = COORSYS_CARTESIAN;
   }

   /**
      serialize data structure
   */
   public void toEva (Eva eva)
   {
      eva.clear ();
      eva.addLine (new EvaLine (minX + ", " + maxX + ", " + unitsX));
      eva.addLine (new EvaLine (minY + ", " + maxY + ", " + unitsY));
      eva.addLine (new EvaLine (tMin + ", " + tMax + ", " + unitsT));
      eva.addLine (new EvaLine (marco_dx + ", " + marco_dy));

      eva.addLine (new EvaLine (CoordSys));
   }
}
