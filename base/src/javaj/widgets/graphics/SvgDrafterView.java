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
package javaj.widgets.graphics;

import javaj.widgets.basics.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import de.elxala.zServices.*;
import de.elxala.parse.xml.*;
import de.elxala.parse.svg.*;
import de.elxala.langutil.*;

import javaj.widgets.graphics.objects.*;

/**
 */
public abstract class SvgDrafterView extends flexiPathView implements svgSaxListener
{
   private static logger log = new logger (null, "javaj.widgets.graphics.SvgDrafterView", null);
   private svgSaxReader svgReader = new svgSaxReader (this);

   // svg/xml parsers
   //
   private svgPath2GraphicsPath    svgPathParser = new svgPath2GraphicsPath ();
   private svgStyle2GraphicsPaint  svgStyler     = new svgStyle2GraphicsPaint ();

   private uniPath currPath  = new uniPath ();
   private uniPaint currPaint = new uniPaint ();
   private uniPaint currFill = new uniPaint ();

   public abstract void messageGestureStart (float xScreen, float yScreen, float x, float y);
   public abstract void messageGestureContinue (float dxScreen, float dyScreen, float dx, float dy);

   public void setSGVFile (String fileName)
   {
      loadSVG (fileName);
      resetpos ();
   }

   public void clearGraphic ()
   {
      laEscena.clearGraphic ();
      currPath  = new uniPath ();
      currPaint = new uniPaint ();
      currFill = new uniPaint ();
   }

   public void loadSVG(String fileName)
   {
      if (fileName == null || fileName.length () == 0) return;

      log.dbg (2, "loading " + fileName);

      // give a name to the graphic object like an url
      laEscena.addObject ("file://" + fileName);

      svgReader.parseFile (fileName); // will trigger processSvg... functions
      log.dbg (2, "loaded");
   }

   public void processSvgSetDimension (String swidth, String sheight)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgSetDimension", "dimension (" + swidth + ", " + sheight + ")");

      // What to do ? the meaning of this svg element is unclear for me
      // graphWidth = (float) stdlib.atof (swidth);
      // graphHeight = (float) stdlib.atof (sheight);
   }

   public void processSvgPath (String pathData, String pathStyle, boolean polygonOrPolyline, boolean closePath)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgPath", "path  [" + pathData + "]  style [" + pathStyle + "]");

      currPath = svgPathParser.parsePath (pathData, polygonOrPolyline ? 'M': 'm');
      if (closePath)
         currPath.close ();

      fillAndPaintPath (currPath, pathStyle);
   }

   public void processSvgRect (float x, float y, float dx, float dy, float rx, float ry, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgRect", "x y dx dy (" + x + ", " + y + " / " + dx + ", " + dy + ") style [" + pathStyle + "]");

      currPath = new uniPath ();
      currPath.addRoundRect (new uniRect (x, y, x+dx, y+dy), rx, ry);
      fillAndPaintPath (currPath, pathStyle);
   }

   public void processSvgCircle  (float xcenter, float ycenter, float radio, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgCircle", "cx cy radius (" + xcenter + ", " + ycenter + " / " + radio + ") style [" + pathStyle + "]");

      currPath = new uniPath ();
      currPath.addCircle (xcenter, ycenter, radio);
      fillAndPaintPath (currPath, pathStyle);
   }

   public void processSvgOval  (float left, float top, float width, float height, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgOval", "left top width height (" + left + ", " + top + " / " + width + ", " + height + ") style [" + pathStyle + "]");

      currPath = new uniPath ();
      currPath.addOval (new uniRect (left, top, left+width, top+height));
      fillAndPaintPath (currPath, pathStyle);
   }

   public void processSvgEllipse (float cx, float cy, float rx, float ry, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgEllipse", "cx cy rx ry (" + cx + ", " + cy + " / " + rx + ", " + ry + ") style [" + pathStyle + "]");

      currPath = new uniPath ();
      currPath.addOval  (new uniRect (cx-rx/2, cy-ry/2, cx+rx/2, cy+ry/2));
      fillAndPaintPath (currPath, pathStyle);
   }

   private void fillAndPaintPath (uniPath nowPath, String pathStyle)
   {
      currPaint.reset ();
      currPaint.setColorRGB  (uniColor.PAPER_INK_COLOR);
      currPaint.setStyle  (uniPaint.STYLE_STROKE); // FILL or FILL_AND_STROKE
      currPaint.setAntiAlias(true);

      currFill.reset ();
      currFill.setColorRGB  (uniColor.LGRAY);
      currFill.setStyle  (uniPaint.STYLE_FILL);
      currFill.setAntiAlias(true);

      svgStyler.parseStyle (pathStyle, currFill, currPaint);
 
      // provisional ... TODO: hacerlo más general translate + scale, rotate y/o transform matrix
      float rotate = svgStyler.parseTransformationFromStyle (pathStyle);
      //System.out.println ("ME ROTAS QUILLO " + rotate + "[" + pathStyle + "]");

      uniPath ppath = new uniPath (nowPath);
      if (svgStyler.hasFillInfo ())
         laEscena.addElement (new pathElement (ppath, new uniPaint (currFill), rotate));

      if (svgStyler.hasStrokeInfo ())
         laEscena.addElement (new pathElement (ppath, new uniPaint (currPaint), rotate));
   }

   public void processSvgText (String strData, Attributes textAttributes)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgText", "text  [" + strData + "]");

//////      int x = stdlib.atoi (textAttributes.getValue ("x"));
//////      int y = stdlib.atoi (textAttributes.getValue ("y"));
//////
//////      currPaint.reset ();
//////      currPaint.setColor  (Color.BLACK);
//////
//////      laEscena.addElement (new textElement (strData, x, y, new uniPath (currPaint)));
   }

   public void processSvgText (String strData, float x, float y)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgText", "text  [" + strData + "] x " + x +  " y " + y);

      currPaint.reset ();
      currPaint.setColorRGB  (uniColor.PAPER_INK_COLOR);
      laEscena.addElement (new textElement (strData, x, y, new uniPaint (currPaint)));
   }
}
