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

import de.elxala.zServices.*;

/**
   @author Alejandro Xalabarder
   @date   2005
   @lastupdate   30.10.2011 19:47

   Class specialized in drawing coordinate systems

*/
public class mathFunctionFrame
{
   private static logger log = new logger (null, "javaj.widgets.graphics.mathFunctionFrame", null);

   public static final String COORSYS_CARTESIAN   = "cartesianas";
   public static final String COORSYS_POLAR       = "polares";
   public static final String COORSYS_PARAMETRICS = "parametricas";

   public double minX = -6., maxX = +6., minY = -2.5, maxY = 2.5;
   public double scaleX=1., scaleY=1.;

   private int marco_dx = 100, marco_dy = 100;
   private double tMin = -10., tMax = +10.;
   private String CoordSys = COORSYS_CARTESIAN;

   private String unitsX = "", unitsY = "", unitsT = "";   // p.e. "m"
   private String sExpoX = "", sExpoY = "", sExpoT = "";   // p.e. "10e-2"

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
      return (int) ((xx - minX) * scaleX);
   }

   public int toPixelY (double yy)
   {
      return (int) ((maxY - yy) * scaleY);
   }


   public double toRealX (int pixelsx)
   {
      return minX + (double) pixelsx / scaleX;
   }

   public double toRealY (int pixelsy)
   {
      return maxY - (double) pixelsy / scaleY;
   }

   private void recalculaEscalas ()
   {
      scaleX = (maxX - minX) == 0 ? 1.: (double) ((marco_dx-2) / (maxX - minX));
      scaleY = (maxY - minY) == 0 ? 1.: (double) ((marco_dy-2) / (maxY - minY));
   }

   public void setScaleByLimits (double miX, double maX, double miY, double maY)
   {
      log.dbg (2, "setScaleByLimits", "   minX maxX minY maxY " + miX + ", " + maX + ", " + miY + ", " + maY);

      minX = miX;
      maxX = maX;
      minY = miY;
      maxY = maY;
      recalculaEscalas ();
   }

   public void setScaleAndOffsets (double pscalex, double pscaley, double minimX, double maximY)
   {
      log.dbg (2, "setScaleAndOffsets", "   scalex " + pscalex + " scaley " + pscaley + "offsetX (minX) " + minimX + " offsetY (maxY) " +  maximY);

      scaleX = pscalex;
      scaleY = pscaley;
      minX = minimX;
      maxX = minimX + marco_dx / scaleX;
      minY = maximY - marco_dy / scaleY;
      maxY = maximY;
   }

   private double refMinX = 0., refMaxX = 0., refMinY = 1., refMaxY = 1.;
   private double refScaleX = 1., refScaleY = 1.;

   public void setReference4Gesture ()
   {
      log.dbg (2, "setReference4Gesture", "   minX maxX minY maxY " + minX + ", " + maxX + ", " + minY + ", " + maxY);
      refMinX = minX;
      refMaxX = maxX;
      refMinY = minY;
      refMaxY = maxY;

      refScaleX = scaleX;
      refScaleY = scaleY;
   }


   public void relativeTranslation(vect3f p1, vect3f p2)
   {
      log.dbg (2, "relativeTranslation", "   translation ...");

      // add 0.01 to avoid raise conditions (factor 0 or infinite)
      double desplazaX = (p1.x - p2.x) / refScaleX;
      double desplazaY = (p2.y - p1.y) / refScaleY;

      setScaleByLimits (refMinX + desplazaX, refMaxX + desplazaX,
                        refMinY + desplazaY, refMaxY + desplazaY);
   }

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
   public void drawCoordenada (uniCanvas can, uniPaint pai, boolean Horiz)
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
}
