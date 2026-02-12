/*
package javaj.widgets.graphics;
Copyright (C) 2011-2022 Alejandro Xalabarder Aulet

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
package javaj.widgets.graphics.objects;

import java.util.*;
import javaj.widgets.graphics.*;
import de.elxala.math.space.*;   // vect3f
import de.elxala.math.space.curve.*;
import de.elxala.langutil.*;
import de.elxala.Eva.*;

/**
   to build editable Paths

*/
public class ediTrass2JScanvas
{

   public static String toColorString (uniPaint up)
   {
      //c2d.fillStyle = "rgba(255, 255, 255, 0.5)";
      //c2d.globalAlfa = 0.2; for images

      int codez = up.getColorRBG ();
      String str = Integer.toHexString (codez);
      return str.length () > 6 ? str.substring (str.length ()-6): str;
   }

   // optimized uses META-GASTONA/js/trassos2D.js as function
   // while not optimized generates all javascript code necessary without including anything
   //
   public static StringBuffer buildJavaScriptCode (ediTrass et, styleSet headStyles, boolean optimized)
   {
      if (et == null || et.isEmpty ()) return null;

      StringBuffer sb = new StringBuffer ();

      String strFill = "null";
      String strStroke = "null";
      String strDraw = "";
      String strStyle = "";

      //(o) TODO_jagui_graphic buildJavaScriptCode, aplicar estilos, falta stroke size, transparencias de fill y stroke, rotación ...
      //
      styleObject estil = headStyles.getStyle (et.style);
      if (estil.hasFill ())
      {
         strFill = "\"#" + toColorString (estil.getFillPaint ()) + "\"";
         if (! optimized)
         {
            strStyle = "c2d.fillStyle = " + strFill + ";";
            strDraw = "c2d.fill ();";
         }
      }
      if (estil.hasStroke ())
      {
         strStroke = "\"#" + toColorString (estil.getStrokePaint ()) + "\"";
         strStyle += "c2d.strokeStyle = " + strStroke + ";";
         strDraw += "c2d.stroke ();";
      }
      else
      {
         // duda ... siempre hay stroke ?
         strStroke = "\"#000000\"";
         strStyle += "c2d.strokeStyle = 'rgba(0,0,0,0)';";
         strDraw += "c2d.stroke ();";
      }

      switch (et.trassForm)
      {
         case ediTrass.FORM_OVAL:
         {
             // truco para pintar un oval! scale (dx, dy) -> arco centrado de radio 1!!! -> restore
             double sx = et.getPointX(0) / 2.;  // lo convertimos a radio x
             double sy = et.getPointY(0) / 2.;  // lo convertimos a radio y
             if (sx == 0.) sx = 1.;
             if (sy == 0.) sy = 1.;

             //(o) TODO_jagui_graphic falta aplicar rotación ... donde hacerlo ?
             //               idea:
             //                  save ()
             //                  translate (cx, cy)
             //                  scale (sx, sy)
             //                  rotate (-angulo);
             //                  arc (0, 0, 1, 0, 2*Math.PI, false);
             //                  restore ()
             //
             sb.append ("c2d.beginPath();");
             sb.append ("c2d.save();");
             sb.append ("c2d.scale(" + sx + ", " + sy + ");");
             sb.append ("c2d.arc(" + (et.getPosX()+sx) / sx + ", " + (et.getPosY()+sy) / sy + ", 1, 0, 2 * Math.PI, false);");
             sb.append ("c2d.restore();");
             sb.append (strStyle);
             sb.append (strDraw);

            // JAVA
            //   objEllipse2D = new Ellipse2D.Float (et.getPosX(), et.getPosY(), et.getPointX(0), et.getPointY(0));
         }
         break;
         case ediTrass.FORM_ARC:
         {
            // JAVA SCRIPT
            // JAVA
            //objArc2D = new Arc2D.Float (Arc2D.OPEN);
            //objArc2D.setFrame (et.getPosX(), et.getPosY(), et.getPointX(0), et.getPointY(0));
            //objArc2D.setAngleStart(et.getValueAt (2));  // startAngle
            //objArc2D.setAngleExtent(et.getValueAt (3)); // sweepAngle

            // ?? work ??
            sb.append ("c2d.beginPath();");
            sb.append ("c2d.arc(" + et.getPosX() + ", " + et.getPosY() + ", " + et.getPointX(0) + ", " +
                       et.getValueAt(2) + " * Math.PI / 360., " +
                       et.getValueAt(3) + " * Math.PI / 360., false);");
             sb.append (strStyle);
             sb.append (strDraw);
         }
         break;
         case ediTrass.FORM_RECTANGLE:
         {
            sb.append ("c2d.beginPath();");
            sb.append (strStyle);
            if (estil.hasFill ())
               sb.append ("c2d.fillRect(" + et.getPosX() + ", " + et.getPosY() + ", " +  et.getPointX(0) + ", " + et.getPointY(0) + ");");
            sb.append ("c2d.strokeRect(" + et.getPosX() + ", " + et.getPosY() + ", " +  et.getPointX(0) + ", " + et.getPointY(0) + ");");
            // JAVA
            // objRect2D = new RoundRectangle2D.Float ();
            // objRect2D.setRoundRect(et.getPosX(), et.getPosY(),
            //                        et.getPointX(0), //width
            //                        et.getPointY(0), //height
            //                        et.getPointX(1), //radius x
            //                        et.getPointY(1)); //radius y
         }
         break;
         case ediTrass.FORM_POLYGONE:
         {
            if (optimized)
            {
               sb.append ("trassos2D().trassShapeNoSyncCanvas (c2d, \"pol\", " +
                          et.getPointAbsX(0) + ", " + et.getPointAbsY(0) +
                          ", " + strFill + ", " + strStroke + ", " + et.isClosed () +
                          ", [");
            }
            else
            {
               sb.append ("c2d.beginPath();");
               sb.append ("c2d.moveTo(" + et.getPointAbsX(0) + ", " + et.getPointAbsY(0) + ");");
               sb.append ("var arrPtos = [");
            }
            for (int pp = 1; pp < et.getPairsCount (); pp ++)
            {
               sb.append ((pp != 1 ? ", ": "") + et.getPointAbsX(pp) + ", " + et.getPointAbsY(pp));
            }
            if (optimized)
            {
               sb.append ("]);");
            }
            else
            {
               sb.append ("];\n for (var ii = 0; ii < arrPtos.length; ii += 2) { c2d.lineTo (arrPtos[ii], arrPtos[ii+1]); }\n");
               if (et.isClosed ())
                  sb.append ("c2d.closePath();");
               sb.append (strStyle);
               sb.append (strDraw);
            }
         }
         break;
         case ediTrass.FORM_PATH_CUBIC:
         {
            if (optimized)
            {
               sb.append ("trassos2D().trassShapeNoSyncCanvas (c2d, \"cub\", " +
                          et.getPointAbsX(0) + ", " + et.getPointAbsY(0) +
                          ", " + strFill + ", " + strStroke + ", " + et.isClosed () +
                          ", [");
            }
            else
            {
               sb.append ("c2d.beginPath();");
               sb.append ("c2d.moveTo(" + et.getPointAbsX(0) + ", " + et.getPointAbsY(0) + ");");
               sb.append ("var arrPtos = [");
            }
            for (int pa = 1; pa < et.getPairsCount (); pa += 3)
            {
               sb.append ((pa != 1 ? ", ": "") + et.getPointAbsX(pa) + ", " +  et.getPointAbsY(pa) + ", " +
                                                 et.getPointAbsX(pa+1) + ", " + et.getPointAbsY(pa+1) + ", " +
                                                 et.getPointAbsX(pa+2) + ", " + et.getPointAbsY(pa+2));
            }

            if (optimized)
            {
               sb.append ("]);");
            }
            else
            {
               sb.append ("];\nfor (var ii = 0; ii < arrPtos.length; ii += 6) { c2d.bezierCurveTo (arrPtos[ii], arrPtos[ii+1], arrPtos[ii+2], arrPtos[ii+3], arrPtos[ii+4], arrPtos[ii+5]); }\n");
               if (et.isClosed ())
                  sb.append ("c2d.closePath();");
                sb.append (strStyle);
                sb.append (strDraw);
            }
         }
         break;

         case ediTrass.FORM_PATH_QUAD:
         {
            if (optimized)
            {
               sb.append ("trassos2D().trassShapeNoSyncCanvas (c2d, \"qua\", " +
                          et.getPointAbsX(0) + ", " + et.getPointAbsY(0) +
                          ", " + strFill + ", " + strStroke + ", " + et.isClosed () +
                          ", [");
            }
            else
            {
               sb.append ("c2d.beginPath();");
               sb.append ("c2d.moveTo(" + et.getPointAbsX(0) + ", " + et.getPointAbsY(0) + ");");
               sb.append ("var arrPtos = [");
            }

            for (int pp = 1; pp < et.getPairsCount (); pp += 2)
            {
               sb.append ((pp != 1 ? ", ": "") + et.getPointAbsX(pp) + ", " +  et.getPointAbsY(pp) + ", " +
                                                 et.getPointAbsX(pp+1) + ", " + et.getPointAbsY(pp+1));
            }
            if (optimized)
            {
               sb.append ("]);");
            }
            else
            {
               sb.append ("];\nfor (var ii = 0; ii < arrPtos.length; ii += 4) { c2d.quadraticCurveTo (arrPtos[ii], arrPtos[ii+1], arrPtos[ii+2], arrPtos[ii+3]); }\n");
               if (et.isClosed ())
                  sb.append ("c2d.closePath();");
                sb.append (strStyle);
                sb.append (strDraw);
            }
         }
         break;

         case ediTrass.FORM_PATH_AUTOCASTELJAU:
         {
            float lx = et.getPointAbsX(0);
            float ly = et.getPointAbsY(0);

            if (et.getPairsCount () < 3)
            {
               if (optimized)
               {
                  sb.append ("trassos2D().trassShapeNoSyncCanvas (c2d, \"pol\", " +
                             et.getPointAbsX(0) + ", " + et.getPointAbsY(0) +
                             ", " + strFill + ", " + strStroke + ", false" +
                             ", [" + (lx + et.getPointX(1)) + ", " + (ly + et.getPointY(1)) + "]);");
               }
               else
               {
                  sb.append ("c2d.beginPath();");
                  sb.append ("c2d.moveTo(" + et.getPointAbsX(0) + ", " + et.getPointAbsY(0) + ");");
                  sb.append ("c2d.lineTo(" + (lx + et.getPointX(1)) + ", " + (ly + et.getPointY(1)) + ");");
                  sb.append (strStyle);
                  sb.append (strDraw);
               }
            }
            else
            {
               vect3f [] ctrlP = null;
               vect3f prevCtrPt = null;
               vect3f lastCtrlPt = null;
               float lastXabs = et.getPointAbsX(et.getPairsCount ()-1);
               float lastYabs = et.getPointAbsY(et.getPairsCount ()-1);

               vect3f p0 = new vect3f (lastXabs, lastYabs);
               vect3f p1 = new vect3f (lx, ly);
               vect3f p2 = new vect3f (lx + et.getPointX(1), ly + et.getPointY(1));

               if (et.isClosed ())
               {
                  ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);
                  prevCtrPt = new vect3f (ctrlP[1]);
                  lastCtrlPt = new vect3f (ctrlP[0]);
               }

               if (optimized)
               {
                  sb.append ("trassos2D().trassShapeNoSyncCanvas (c2d, \"cub\", " +
                             et.getPointAbsX(0) + ", " + et.getPointAbsY(0) +
                             ", " + strFill + ", " + strStroke + ", false" +  // closing this curve is more complicated ...
                             ", [");
               }
               else
               {
                  sb.append ("c2d.beginPath();");
                  sb.append ("c2d.moveTo(" + et.getPointAbsX(0) + ", " + et.getPointAbsY(0) + ");");
                  sb.append ("var arrPtos = [");
               }

               for (int dd = 1; (dd+1) < et.getPairsCount (); dd ++)
               {
                  p0.set (lx, ly);
                  p1.set (et.getPointAbsX(dd),    et.getPointAbsY(dd));
                  p2.set (et.getPointAbsX(dd+1),  et.getPointAbsY(dd+1));
                  ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);
                  if (prevCtrPt == null) prevCtrPt = new vect3f (ctrlP[0]);

                  sb.append ((dd != 1 ? ", ": "") + prevCtrPt.x + ", " + prevCtrPt.y + ", " + ctrlP[0].x + ", " + ctrlP[0].y + ", " + p1.x + ", " + p1.y);
                  prevCtrPt.set(ctrlP[1]);

                  lx =  et.getPointAbsX(dd);
                  ly =  et.getPointAbsY(dd);
               }

               // actual closing the curve
               //
               if (et.isClosed ())
               {
                  p0.set (lx, ly);
                  p1.set (lastXabs, lastYabs);
                  p2.set (et.getPointAbsX(0), et.getPointAbsY(0));
                  ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);

                  sb.append (", " + prevCtrPt.x + ", " + prevCtrPt.y + ", " + ctrlP[0].x + ", " + ctrlP[0].y + ", " + lastXabs + ", " + lastYabs);
                  sb.append (", " + ctrlP[1].x + ", " + ctrlP[1].y + ", " + lastCtrlPt.x + ", " + lastCtrlPt.y + ", " + et.getPointAbsX(0) + ", " + et.getPointAbsY(0));
               }
               else
               {
                  sb.append (", " + prevCtrPt.x + ", " + prevCtrPt.y + ", " + prevCtrPt.x + ", " + prevCtrPt.y + ", " + lastXabs + ", " + lastYabs);
               }

               if (optimized)
               {
                  sb.append ("]);");
               }
               else
               {
                  sb.append ("];\nfor (var ii = 0; ii < arrPtos.length; ii += 6) { c2d.bezierCurveTo (arrPtos[ii], arrPtos[ii+1], arrPtos[ii+2], arrPtos[ii+3], arrPtos[ii+4], arrPtos[ii+5]); }\n");
                  sb.append (strStyle);
                  sb.append (strDraw);
               }
            }
         }
         break;
      }
      return sb;
   }

}
