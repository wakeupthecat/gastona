/*
package javaj.widgets.graphics;
Copyright (C) 2011 Alejandro Xalabarder Aulet

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
public class editablePaths
{
   private ediTrazo currentET = null;;
   private int changesCounter = 0;

   public List arrEdiTrazos = new Vector (); // Vector<ediTrazo> ~ (Trazo = Stroke)

   private uniRect recBounds = null;

   // for relative paints
   protected float lastX = 0.f;
   protected float lastY = 0.f;

   public editablePaths ()
   {
   }

   public int getChangeCounter()
   {
      return changesCounter;
   }

   public uniRect getBounds ()
   {
      if (recBounds == null)
      {
         recBounds = new uniRect ();
         for (int ii = 0; ii < arrEdiTrazos.size (); ii ++)
            recBounds.union (((ediTrazo)arrEdiTrazos.get (ii)).getBounds ());
      }
      return recBounds;
   }

   public void computeBounds (uniRect rec, boolean bol)
   {
      rec.set (getBounds ());
   }

   // returns the scale (x and y) that makes the graphic object
   // fit in an area of size telaX x telaY without loosing the proportions
   // the correspondent offsets are given by the functions getOffsetXtoFit and getOffsetYtoFit
   //
   public float getScaleToFit (int telaX, int telaY)
   {
      float scaX = (getBounds ().width () > 0) ? (float) telaX / (float) getBounds ().width (): 1.0f;
      float scaY = (getBounds ().height () > 0) ? (float) telaY / (float) getBounds ().height (): 1.0f;

      return (scaX * getBounds ().height() > telaY) ? scaY: scaX;
   }

   public float getOffsetXtoFit ()
   {
      return (float) -getBounds ().left ();
   }

   public float getOffsetYtoFit ()
   {
      return (float) -getBounds ().top ();
   }

   // to be used when the points has been directly accesed and changed
   public void setContentChanged ()
   {
      changesCounter ++;
   }

   public int getTrazosSize ()
   {
      return arrEdiTrazos.size ();
   }

   public ediTrazo getTrazo (int indx)
   {
      if (indx < 0 || indx >= arrEdiTrazos.size ())
         return null;
      return ((ediTrazo) arrEdiTrazos.get(indx));
   }

   // public void setAzimut2all (float azimut)
   // {
      // changesCounter ++;
      // for (int ii = 0; ii < arrEdiTrazos.size (); ii ++)
         // ((ediTrazo) arrEdiTrazos.get(ii)).azimut = azimut;
   // }

   public void setStyle2all (String style)
   {
      changesCounter ++;
      for (int ii = 0; ii < arrEdiTrazos.size (); ii ++)
         ((ediTrazo) arrEdiTrazos.get(ii)).style = style;
   }

   public void setStyleToTrazo (int indxTrazo, String style)
   {
      if (indxTrazo >= 0 && indxTrazo < getTrazosSize ())
         ((ediTrazo) arrEdiTrazos.get(indxTrazo)).style = style;
   }

   // starts a new Trazo (path) at point x, y
   // returns the Trazo index of the new element
   //
   public int startTrazoAt (float x, float y)
   {
      moveTo (x, y);
      return getTrazosSize() - 1;
   }


   // No se puede este approach, porque parsePathStringOnUnipath espera un uniPath
   // y nosotros solo somos parte de un unipath! (editablePaths)
   //
   //private svgLikePathParser2uniPath  pathParser = new svgLikePathParser2uniPath ();
   // public void parseTrazoFlexiformat (float x, float y, String strStyle, String strTrazo)
   // {
      // int indxTrazo = startTrazoAt ((float) stdlib.atof (xval), (float) stdlib.atof (yval));
      // pathParser.parsePathStringOnUnipath (uPath, data);
      // uPath.getEdiPaths ().setStyleToTrazo (indxTrazo, style);
   // }

   // public void parseTrazosFromEva (EvaUnit evaTrazos)
   // {
      // if (evaTrazos == null) return;
   // }

   public static String trazosToJavaScript (Eva evaTrazos, boolean optim, int telaX, int telaY, boolean center)
   {
      editablePaths este = new editablePaths ();

      este.parseTrazosFromEva (evaTrazos);
      float scale = este.getScaleToFit (telaX, telaY);

      String scaleAndOffset = "";
      if (scale != 1.0f)
         scaleAndOffset += "c2d.scale (" + scale + ", " + scale + ");\n";
      if (center)
         scaleAndOffset += "c2d.translate (" + este.getOffsetXtoFit () + ", " + este.getOffsetYtoFit () + ");\n";
      return scaleAndOffset + este.toJavaScriptCode (optim);
   }

   //
   //   por ejemplo:
   //      <caballo>
   //        z, 238, 121, "fc:+217070000", "jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81"
   //        z, 196, 223, "fc:+217070000", "jau,-43,-81,-10,-36,9,-19,39,8,87,54"
   //        z, 158,  82, "fc:+217070000", "jau,-50,9,-34,48,-16,29,20,19,30,-23,40,-17"
   //        z, 468, 148, "fc:+217070000", "jau,26,22,14,27,1,46,-12,47,21,45,-17,102,-16,19,-17,-12,16,-25,-9,-90,-43,-64,-9,-77"
   //        z, 196, 213, "fc:+217070000", "jau,4,52,30,34,17,73,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-3,-51,4,-120"
   //        z, 488, 160, "fc:+217070000", "jau,37,14,22,51,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14"
   //        z, 128,  83, "fc:+217070000", "jau,-22,-23,0,27"
   //
   public void parseTrazosFromEva (Eva evaTrazos)
   {
      if (evaTrazos == null) return;

      //log.dbg (2, "parseTrazosFromEva", "have trazos data of rows " + evaTrazos.rows ());
      for (int ii = 0; ii < evaTrazos.rows (); ii ++)
      {
         EvaLine eline = evaTrazos.get(ii);
         String orden = evaTrazos.getValue (ii, 0).toLowerCase ();

         if (orden.equals ("z")) // "z" de trazo
         {
            String xval = evaTrazos.getValue (ii, 1);
            String yval = evaTrazos.getValue (ii, 2);
            String style = evaTrazos.getValue (ii, 3);
            String data = evaTrazos.getValue (ii, 4);

            parseTrazo ((float) stdlib.atof (xval), (float) stdlib.atof (yval), style, data);
         }
         else if (orden.equals ("defstyle"))
         {
            styleGlobalContainer.addOrChangeStyle (evaTrazos.getValue (ii, 1), evaTrazos.getValue (ii, 2));
         }
         else
         {
            //log.err ("parseTrazosFromEva", "orden \"" + orden + "\" no reconocida todavia!");
         }
      }
   }

   //
   //   por ejemplo:
   //   parseTrazo (138, 121, "fc:+217070000", "jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81");
   //
   public void parseTrazo (float x, float y, String strStyle, String strTrazo)
   {
      int indxTrazo = startTrazoAt (x, y);
      getTrazo (indxTrazo).parseForm (strTrazo);
      getTrazo (indxTrazo).style = strStyle;
   }

   public void requiredTrazo (float x, float y, int forma)
   {
      if (currentET == null || !currentET.isForm (forma))
      {
         // either no currentET yet or currentET was of not void and for another type
         currentET = new ediTrazo (x, y, forma);
         arrEdiTrazos.add (currentET);
      }
      else if (currentET.isEmpty ())
      {
         currentET.setPosX (x);
         currentET.setPosY (y);
      }
   }

   public void moveTo (float x, float y)
   {
      changesCounter ++;

      requiredTrazo (x, y, ediTrazo.FORM_POLYGONE);
      lastX = x;
      lastY = y;
   }

   public void rMoveTo (float x, float y)
   {
      changesCounter ++;
      lastX += x;
      lastY += y;
      requiredTrazo (lastX, lastY, ediTrazo.FORM_POLYGONE);
   }

   public void lineTo (float x, float y)
   {
      changesCounter ++;
      requiredTrazo (lastX, lastY, ediTrazo.FORM_POLYGONE);
      currentET.addPairAbs (x, y);
      lastX = x;
      lastY = y;
   }

   public void rLineTo (float x, float y)
   {
      lineTo (lastX + x, lastY + y);
   }

   public void cubicTo (float cx1, float cy1, float cx2, float cy2, float x, float y)
   {
      changesCounter ++;
      requiredTrazo (lastX, lastY, ediTrazo.FORM_PATH_CUBIC);
      currentET.addPairAbs (cx1, cy1);
      currentET.addPairAbs (cx2, cy2);
      currentET.addPairAbs (x, y);
      lastX = x;
      lastY = y;
   }

   public void rCubicTo (float cx1, float cy1, float cx2, float cy2, float x, float y)
   {
      cubicTo (cx1 + lastX, cy1 + lastY, cx2 + lastX, cy2 + lastY, x + lastX, y + lastY);
   }

   public void quadTo (float cx, float cy, float x, float y)
   {
      changesCounter ++;
      requiredTrazo (lastX, lastY, ediTrazo.FORM_PATH_QUAD);
      currentET.addPairAbs (cx, cy);
      currentET.addPairAbs (x, y);
      lastX = x;
      lastY = y;
   }

   public void rQuadTo (float cx, float cy, float x, float y)
   {
      quadTo (cx + lastX, cy + lastY, x + lastX, y + lastY);
   }

   public void autoCasteljauPoint (float x, float y)
   {
      changesCounter ++;
      requiredTrazo (lastX, lastY, ediTrazo.FORM_PATH_AUTOCASTELJAU);
      currentET.addPairAbs (x, y);
      lastX = x;
      lastY = y;
   }

   public void rAutoCasteljauPoint (float x, float y)
   {
      autoCasteljauPoint (lastX + x, lastY + y);
   }

   public void close ()
   {
      changesCounter ++;
      if (currentET != null)
         currentET.close (true);
   }

   public void addRoundRect (uniRect rec, float rx, float ry)
   {
      changesCounter ++;

      // one shot element
      //if (!currentET.isEmpty () || !currentET.isForm (ediTrazo.FORM_RECTANGLE))

      requiredTrazo (rec.left (), rec.top(), ediTrazo.FORM_RECTANGLE);
      currentET.addPairRel (rec.width (), rec.height ());
      currentET.addValue (rx);
      currentET.addValue (ry);
   }

   public void rRoundRectTo (float dx, float dy, float rx, float ry)
   {
      addRoundRect (new uniRect (false, lastX, lastY, dx, dy), rx, ry);
   }

   public void addCircle (float cx, float cy, float radius)
   {
      changesCounter ++;

      // one shot element
      requiredTrazo (cx - radius, cy - radius, ediTrazo.FORM_OVAL);
      currentET.addPairRel (radius + radius, radius + radius);
   }

   public void rCircleTo (float radius)
   {
      addCircle (lastX, lastY, radius);
   }

   public void addOval (uniRect oval)
   {
      changesCounter ++;

      // one shot element
      requiredTrazo (oval.left (), oval.top(), ediTrazo.FORM_OVAL);
      currentET.addPairRel (oval.width (), oval.height ());
   }

   public void rOvalTo (float dx, float dy)
   {
      addOval (new uniRect (false, lastX, lastY, dx, dy));
   }

   public void addArc (uniRect oval, float startAngle, float sweepAngle)
   {
      changesCounter ++;

      // one shot element
      requiredTrazo (oval.left (), oval.top(), ediTrazo.FORM_ARC);

      currentET.addPairRel (oval.width (), oval.height ());
      currentET.addValue (startAngle);
      currentET.addValue (sweepAngle);
   }

   public void rArcTo (float dx, float dy, float startAngle, float sweepAngle)
   {
      addArc (new uniRect (false, lastX, lastY, dx, dy), startAngle, sweepAngle);
   }

   
   //(o) TODO_dump trazos
   
   //
   //  dump first (tambien) styleGlobalContainer
   //  
   //  collect all styles and trazos into a varible eva
   //  para poder salvarlo en fichero, db etc
   //  y convertirlo en javascript
   //
   
   
   public String toString (int indx)
   {
      if (indx < 0 || indx >= arrEdiTrazos.size ())
         return null;
      return ((ediTrazo) arrEdiTrazos.get(indx)).toString ();
   }

   public String toString ()
   {
      int ii = 0;
      String str = null;
      StringBuffer s = new StringBuffer ();

      while ((str = toString (ii++)) != null)
         s.append (str + "\n");

      return s.toString ();
   }

   public void dumpIntoEva (Eva eva)
   {
      for (int ii = 0; ii < arrEdiTrazos.size (); ii ++)
         eva.addLine (new EvaLine (((ediTrazo) arrEdiTrazos.get(ii)).toStringArray ()));
   }

   public String toJavaScriptCode ()
   {
      return toJavaScriptCode (false);
   }

   public String toJavaScriptCode (boolean optim)
   {
      int ii = 0;
      StringBuffer stra = null;
      StringBuffer s = new StringBuffer ();

      while ((stra = buildJavaScriptCode (ii++, optim)) != null)
         s.append (stra + "\n");

      return s.toString ();
   }

   private String toColorString (uniPaint up)
   {
      //c2d.fillStyle = "rgba(255, 255, 255, 0.5)";
      //c2d.globalAlfa = 0.2; for images

      int codez = up.getColorRBG ();
      String str = Integer.toHexString (codez);
      return str.length () > 6 ? str.substring (str.length ()-6): str;
   }


   // optimized uses META-GASTONA/js/trazos2D.js as function
   // while optimized generates all javascript code necesary without including anything
   //
   public StringBuffer buildJavaScriptCode (int trazoIndx, boolean optimized)
   {
      ediTrazo et = getTrazo (trazoIndx);
      if (et == null || et.isEmpty ()) return null;

      StringBuffer sb = new StringBuffer ();

      String strFill = "null";
      String strStroke = "null";
      String strDraw = "";
      String strStyle = "";

      //(o) TODO_jagui_graphic buildJavaScriptCode, aplicar estilos, falta stroke size, transparencias de fill y stroke, rotación ...
      //
      styleObject estilo = styleGlobalContainer.getStyleObjectByName (et.style);
      if (estilo.hasFill ())
      {
         strFill = "\"#" + toColorString (estilo.getFillPaint ()) + "\"";
         if (! optimized)
         {
            strStyle = "c2d.fillStyle = " + strFill + ";";
            strDraw = "c2d.fill ();";
         }
      }
      if (estilo.hasStroke ())
      {
         strStroke = "\"#" + toColorString (estilo.getStrokePaint ()) + "\"";
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

      switch (et.trazoForm)
      {
         case ediTrazo.FORM_OVAL:
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
         case ediTrazo.FORM_ARC:
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
         case ediTrazo.FORM_RECTANGLE:
         {
            sb.append ("c2d.beginPath();");
            sb.append (strStyle);
            if (estilo.hasFill ())
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
         case ediTrazo.FORM_POLYGONE:
         {
            if (optimized)
            {
               sb.append ("trazoShape (c2d, \"pol\", " +
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
         case ediTrazo.FORM_PATH_CUBIC:
         {
            if (optimized)
            {
               sb.append ("trazoShape (c2d, \"cub\", " +
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

         case ediTrazo.FORM_PATH_QUAD:
         {
            if (optimized)
            {
               sb.append ("trazoShape (c2d, \"qua\", " +
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

         case ediTrazo.FORM_PATH_AUTOCASTELJAU:
         {
            float lx = et.getPointAbsX(0);
            float ly = et.getPointAbsY(0);

            if (et.getPairsCount () < 3)
            {
               if (optimized)
               {
                  sb.append ("trazoShape (c2d, \"pol\", " +
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
                  sb.append ("trazoShape (c2d, \"cub\", " +
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

