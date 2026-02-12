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
public class editablePaths
{
   private ediTrass currentET = null;;
   private int changesCounter = 0;

   protected styleSet headStyles = new styleSet ();
   protected List arrEdiTrassos = new Vector (); // Vector<ediTrass> ~ (Trass = Stroke)

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

   public styleSet getStyleMap ()
   {
      return headStyles;
   }

   public uniRect getBounds ()
   {
      if (recBounds == null)
      {
         recBounds = new uniRect ();
         for (int ii = 0; ii < arrEdiTrassos.size (); ii ++)
            recBounds.union (((ediTrass)arrEdiTrassos.get (ii)).getBounds ());
      }
      return recBounds;
   }

   // returns last trass inciated with different add methods
   // otherwise null
   public ediTrass getCurrentTrass ()
   {
      return currentET;
   }

   // assing intern currentET by index
   // this can affect editablePaths object
   // use with knowledge!
   public void setCurrentTrassByIndex (int trassIndx)
   {
      currentET = trassIndx < 0 || trassIndx >= arrEdiTrassos.size () ? null:
                  (ediTrass) arrEdiTrassos.get(trassIndx);
   }

   public boolean trassVisibleInRect (int trassIndx, uniRect rec2)
   {
      if (trassIndx < 0 || trassIndx >= arrEdiTrassos.size ())
         return false;
      return ((ediTrass) arrEdiTrassos.get(trassIndx)).getBounds ().intersectRect (rec2);
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

   public int getTrassosSize ()
   {
      return arrEdiTrassos.size ();
   }

   // returns the last trass index or -1
   public int getLastTrassIndx ()
   {
      return arrEdiTrassos.size () - 1;
   }

   public void addTrass (ediTrass trass)
   {
      arrEdiTrassos.add (trass);
   }

   public ediTrass getTrass (int indx)
   {
      if (indx < 0 || indx >= arrEdiTrassos.size ())
         return null;
      return ((ediTrass) arrEdiTrassos.get(indx));
   }

   // public void setAzimut2all (float azimut)
   // {
      // changesCounter ++;
      // for (int ii = 0; ii < arrEdiTrassos.size (); ii ++)
         // ((ediTrass) arrEdiTrassos.get(ii)).azimut = azimut;
   // }

   public void setStyle2all (String style)
   {
      changesCounter ++;
      for (int ii = 0; ii < arrEdiTrassos.size (); ii ++)
         ((ediTrass) arrEdiTrassos.get(ii)).style = style;
   }

   public void setStyleToTrass (int indxTrass, String style)
   {
      if (indxTrass >= 0 && indxTrass < getTrassosSize ())
         ((ediTrass) arrEdiTrassos.get(indxTrass)).style = style;
   }

   // starts a new Trass (path) at point x, y
   // returns the Trass index of the new element
   //
   public int startTrassAt (float x, float y)
   {
      moveTo (x, y);
      return getLastTrassIndx();
   }


   // No se puede este approach, porque parsePathStringOnUnipath espera un uniPath
   // y nosotros solo somos parte de un unipath! (editablePaths)
   //
   //private svgLikePathParser2uniPath  pathParser = new svgLikePathParser2uniPath ();
   // public void parseTrassFlexiformat (float x, float y, String strStyle, String strTrass)
   // {
      // int indxTrass = startTrassAt ((float) stdlib.atof (xval), (float) stdlib.atof (yval));
      // pathParser.parsePathStringOnUnipath (uPath, data);
      // uPath.getEdiPaths ().setStyleToTrass (indxTrass, style);
   // }

   // public void parseTrassosFromEva (EvaUnit evaTrassos)
   // {
      // if (evaTrassos == null) return;
   // }

   public static String trassosToJavaScript (Eva evaTrassos, EvaUnit euTrassosLib, boolean optim, int telaX, int telaY, boolean center)
   {
      editablePaths este = new editablePaths ();

      este.parseTrassosFromEva (evaTrassos, euTrassosLib);
      float scale = 0.9f * este.getScaleToFit (telaX, telaY);

      String scaleAndOffset = "";
      if (scale != 1.0f)
         scaleAndOffset += "c2d.scale (" + scale + ", " + scale + ");\n";

      float dispX = + scale * 0.1f * este.getBounds ().width ()  + este.getOffsetXtoFit ();
      float dispY = + scale * 0.1f * este.getBounds ().height () + este.getOffsetYtoFit ();
      if (center)
         scaleAndOffset += "c2d.translate (" + dispX + ", " + dispY + ");\n";
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
   public void parseTrassosFromEva (Eva evaTrassos)
   {
      parseTrassosFromEva (evaTrassos, null);
   }

   public void parseTrassosFromEva (Eva evaTrassos, EvaUnit euTrassosLib)
   {
      if (evaTrassos == null) return;

      //log.dbg (2, "parseTrassosFromEva", "have trassos data of rows " + evaTrassos.rows ());
      for (int ii = 0; ii < evaTrassos.rows (); ii ++)
      {
         EvaLine eline = evaTrassos.get(ii);
         String orden = evaTrassos.getValue (ii, 0).toLowerCase ();

         currentET = null;

         if (orden.equals ("z")) // "z" de trass
         {
            String xval = evaTrassos.getValue (ii, 1);
            String yval = evaTrassos.getValue (ii, 2);
            String style = evaTrassos.getValue (ii, 3);
            String data = evaTrassos.getValue (ii, 4);

            parseTrass ((float) stdlib.atof (xval), (float) stdlib.atof (yval), style, data);
         }
         else if (orden.equals ("defstyle"))
         {
            headStyles.addStyle (evaTrassos.getValue (ii, 1), evaTrassos.getValue (ii, 2));
         }
         else if (orden.equals ("ref"))
         {
            //     ref, varname, [*planned?* posx, posy, scalex, scaley, rotation ]
            //

            // simple composition for "trassos" by passing a EvaUnit as "trassos library" together with the main Eva
            // no relative variation: offset, resize or rotation supported yet
            //

            if (euTrassosLib != null && simpleRecursiveCtrl < 20)
            {
               simpleRecursiveCtrl ++;
               parseTrassosFromEva (euTrassosLib.getEva (evaTrassos.getValue (ii, 1)), euTrassosLib);
               simpleRecursiveCtrl --;
            }
            else
            {
               //log.err ("parseTrassosFromEva", "reference " + evaTrassos.getValue (ii, 1) + " too deep!");
            }
         }
         else
         {
            //log.err ("parseTrassosFromEva", "orden \"" + orden + "\" no reconocida todavia!");
         }
      }
   }

   private static int simpleRecursiveCtrl = 0;

   //
   //   por ejemplo:
   //   parseTrass (138, 121, "fc:+217070000", "jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81");
   //
   public void parseTrass (float x, float y, String strStyle, String strTrass)
   {
      int indxTrass = startTrassAt (x, y);
      getTrass (indxTrass).parseForm (strTrass);
      getTrass (indxTrass).style = strStyle;
   }

   public void requiredTrass (float x, float y, int forma)
   {
      if (currentET == null || !currentET.isForm (forma))
      {
         // either no currentET yet or currentET was of not void and for another type
         currentET = new ediTrass (x, y, forma);
         addTrass (currentET);
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

      requiredTrass (x, y, ediTrass.FORM_POLYGONE);
      lastX = x;
      lastY = y;
   }

   public void rMoveTo (float x, float y)
   {
      changesCounter ++;
      lastX += x;
      lastY += y;
      requiredTrass (lastX, lastY, ediTrass.FORM_POLYGONE);
   }

   public void lineTo (float x, float y)
   {
      changesCounter ++;
      requiredTrass (lastX, lastY, ediTrass.FORM_POLYGONE);
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
      requiredTrass (lastX, lastY, ediTrass.FORM_PATH_CUBIC);
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
      requiredTrass (lastX, lastY, ediTrass.FORM_PATH_QUAD);
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
      requiredTrass (lastX, lastY, ediTrass.FORM_PATH_AUTOCASTELJAU);
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
      //if (!currentET.isEmpty () || !currentET.isForm (ediTrass.FORM_RECTANGLE))

      requiredTrass (rec.left (), rec.top(), ediTrass.FORM_RECTANGLE);
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
      requiredTrass (cx - radius, cy - radius, ediTrass.FORM_OVAL);
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
      requiredTrass (oval.left (), oval.top(), ediTrass.FORM_OVAL);
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
      requiredTrass (oval.left (), oval.top(), ediTrass.FORM_ARC);

      currentET.addPairRel (oval.width (), oval.height ());
      currentET.addValue (startAngle);
      currentET.addValue (sweepAngle);
   }

   public void rArcTo (float dx, float dy, float startAngle, float sweepAngle)
   {
      addArc (new uniRect (false, lastX, lastY, dx, dy), startAngle, sweepAngle);
   }

   public String toString (int indx)
   {
      if (indx < 0 || indx >= arrEdiTrassos.size ())
         return null;
      return ((ediTrass) arrEdiTrassos.get(indx)).toString ();
   }

   public String toString ()
   {
      int ii = 0;
      String str = null;
      StringBuffer s = new StringBuffer ();

      s.append (headStyles.toString ());
      s.append ("\n");

      while ((str = toString (ii++)) != null)
         s.append (str + "\n");

      return s.toString ();
   }

   public void dumpIntoEva (Eva eva)
   {
      headStyles.dumpIntoEva (eva);
      for (int ii = 0; ii < arrEdiTrassos.size (); ii ++)
         eva.addLine (new EvaLine (((ediTrass) arrEdiTrassos.get(ii)).toStringArray ()));
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

   public StringBuffer buildJavaScriptCode (int trassIndx, boolean optimized)
   {
      return ediTrass2JScanvas.buildJavaScriptCode (getTrass (trassIndx), headStyles, optimized);
   }
}
