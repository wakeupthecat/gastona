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

import org.xml.sax.Attributes;

import de.elxala.zServices.*;
import de.elxala.parse.svg.*;
import de.elxala.langutil.*;
import de.elxala.Eva.*;
import javaj.widgets.graphics.*;

/**
   Scene capable of loading its containts from svg files as well as from Eva variables

*/
public class graphicObjectLoader extends objectGraph implements svgSaxListener
{
   private static logger log = new logger (null, "javaj.widgets.graphics.graphicObjectLoader", null);
   private svgSaxReader svgReader = new svgSaxReader (this);

   // svg/xml parsers
   //
   private static svgLikePathParser2uniPath  mPathParser = new svgLikePathParser2uniPath ();

   public void loadObjectFromSvg (String fileName)
   {
      if (fileName == null || fileName.length () == 0) return;

      log.dbg (2, "loading " + fileName);

      // give a name to the graphic object like an url
      super.name = "file://" + fileName;
      super.movil.setBasicMovement ("1111");
      super.setCurrentPosAndScale (null);

      svgReader.parseFile (fileName); // will trigger processSvg... functions
      log.dbg (2, "loaded");
   }

   // returns the scale (x and y) that makes the graphic object
   // fit in an area of size telaX x telaY without loosing the proportions
   // the correspondent offsets are given by the functions getOffsetXtoFit and getOffsetYtoFit
   //
   public float getScaleToFit (int telaX, int telaY)
   {
      float scaX = (originalBounds.width () > 0) ? (float) telaX / (float) originalBounds.width (): 1.0f;
      float scaY = (originalBounds.height () > 0) ? (float) telaY / (float) originalBounds.height (): 1.0f;

      return (scaX * originalBounds.height() > telaY) ? scaY: scaX;
   }

   public float getOffsetXtoFit ()
   {
      return (float) -originalBounds.left ();
   }

   public float getOffsetYtoFit ()
   {
      return (float) -originalBounds.top ();
   }

   private static strEncoder encoder4Text = null;
   private static strEncoder getTextEncoder ()
   {
      if (encoder4Text == null)
      {
         // ctext.replaceMeOnce (new String [][] { { "\\\\", "\\" }, { "\\r", "\n" }, { "\\n", "\n" }});
         // seguramente con el sistema nuevo no se puede mapear \n como dos cosas \\n y \\r !!
         //
         encoder4Text = new strEncoder ("~");
         encoder4Text.addStrPairs  (new String [] {
               "\\",   "\\\\",   // A \ character.
               "\n",   "\\n"     // A \r character.
            });
      }
      return encoder4Text;
   }


   //(o) javaj_widgets_2Dscene allowed geometries
   //
   public void loadObjectFromEva (String objectName, Eva evaShapes, Eva pressShapes, String basicMov, offsetAndScale posScala)
   {
      if (evaShapes == null) return;

      super.name = objectName;
      super.movil.setBasicMovement (basicMov);
      super.setCurrentPosAndScale (posScala);

      // press graphic object if any
      graphicObjectLoader pressOb = (pressShapes != null) ? new graphicObjectLoader (): null;
      if (pressOb != null)
      {
         pressOb.loadObjectFromEva ("", // name not important
                                    pressShapes,     // shapes for the press object
                                    null,       // IMPORTANT: IT MUST BE NULL !!! if not we have recursive calls
                                    "",         // basic move not important, it has to follow the one from the master object
                                    null);      // posScala not important, it has to follow the one from the master object
      }
      super.clickCtrl.setClickObj (pressOb);

      log.dbg (2, "loadObjectFromEva", "have graphic data of rows " + evaShapes.rows ());
      for (int ii = 0; ii < evaShapes.rows (); ii ++)
      {
         EvaLine eline = evaShapes.get(ii);
         String orden = evaShapes.getValue (ii, 0).toLowerCase ();
         String style = evaShapes.getValue (ii, 1);
         String data  = evaShapes.getValue (ii, 2);
         if (orden.equals ("svgfile"))   svgReader.parseFile (data); // will trigger processSvg... functions
         if (orden.equals ("defstyle") ||
             orden.equals ("style"))  styleGlobalContainer.addOrChangeStyle (style, data);
         if (orden.equals ("path"))      processSvgPath (data, style, false, false);
         if (orden.equals ("polygon"))   processSvgPath (data, style, true, true);
         if (orden.equals ("polyline"))  processSvgPath (data, style, true, false);
         if (orden.equals ("rect"))      processSvgRect    (eline.getFloat(2), eline.getFloat(3), eline.getFloat(4), eline.getFloat(5), eline.getFloat(6), eline.getFloat(7), style);
         if (orden.equals ("circle"))    processSvgCircle  (eline.getFloat(2), eline.getFloat(3), eline.getFloat(4), style);
         if (orden.equals ("oval"))      processSvgOval    (eline.getFloat(2), eline.getFloat(3), eline.getFloat(4), eline.getFloat(5), style);
         if (orden.equals ("ellipse"))   processSvgEllipse (eline.getFloat(2), eline.getFloat(3), eline.getFloat(4), eline.getFloat(5), style);
         if (orden.equals ("text"))
         {
            // text, style, x, y, enmarcado, ?, texto
            String str = getTextEncoder().decode (eline.getValue (6));
            processSvgText    (str, eline.getFloat(2), eline.getFloat(3), eline.getFloat(4) == 1.f, style);
         }
      }
   }

   // Static method to load in a uniPath a graphic given in trassos format
   //
   public static void loadUniPathFromEvaTrassos (uniPath uPath, Eva evaTrassos)
   {
      if (evaTrassos == null) return;

      svgLikePathParser2uniPath pParser = new svgLikePathParser2uniPath ();

      log.dbg (2, "loadUniPathFromEvaTrassos", "have graphic data of rows " + evaTrassos.rows ());
      for (int ii = 0; ii < evaTrassos.rows (); ii ++)
      {
         EvaLine eline = evaTrassos.get(ii);
         String orden = evaTrassos.getValue (ii, 0).toLowerCase ();
         String xval = evaTrassos.getValue (ii, 1);
         String yval = evaTrassos.getValue (ii, 2);
         String style = evaTrassos.getValue (ii, 3);
         String data = evaTrassos.getValue (ii, 4);

         if (orden.equals ("z")) // "z" de trass
         {
            int indxTrass = uPath.getEdiPaths ().startTrassAt ((float) stdlib.atof (xval), (float) stdlib.atof (yval));
            pParser.parsePathStringOnUnipath (uPath, data);
            uPath.getEdiPaths ().setStyleToTrass (indxTrass, style);
         }
         else if (orden.equals ("defstyle"))
         {
            styleGlobalContainer.addOrChangeStyle (evaTrassos.getValue (ii, 1), evaTrassos.getValue (ii, 2));
         }
         else
         {
            log.err ("loadUniPathFromEvaTrassos", "orden \"" + orden + "\" no reconocida todavia!");
         }
      }
   }

   public void loadObjectFromEvaTrassos (String objectName, Eva evaTrassos)
   {
      loadObjectFromEvaTrassos (objectName, evaTrassos, null, "111", null);
   }

   public void loadObjectFromEvaTrassos (String objectName, Eva evaTrassos, Eva pressTrassos, String basicMov, offsetAndScale posScala)
   {
      if (evaTrassos == null) return;

      super.name = objectName;
      super.movil.setBasicMovement (basicMov);
      super.setCurrentPosAndScale (posScala);

      graphicObjectLoader pressOb = (pressTrassos != null) ? new graphicObjectLoader (): null;
      if (pressOb != null)
      {
         pressOb.loadObjectFromEva ("", // name not important
                                    pressTrassos,     // shapes for the press object
                                    null,       // IMPORTANT: IT MUST BE NULL !!! if not we have recursive calls
                                    "",         // basic move not important, it has to follow the one from the master object
                                    null);      // posScala not important, it has to follow the one from the master object
      }
      super.clickCtrl.setClickObj (pressOb);

      uniPath miUPath = new uniPath ();
      loadUniPathFromEvaTrassos (miUPath, evaTrassos);
      super.addElement (miUPath);
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

      uniPath apath = mPathParser.parsePath (pathData, polygonOrPolyline ? 'M': 'm');
      if (closePath)
         apath.getEdiPaths ().close ();

      fillAndPaintPath (apath, pathStyle);
   }

   public void processSvgRect (float x, float y, float dx, float dy, float rx, float ry, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgRect", "x y dx dy (" + x + ", " + y + " / " + dx + ", " + dy + ") style [" + pathStyle + "]");

      uniPath apath = new uniPath ();
      apath.getEdiPaths ().addRoundRect (new uniRect (false, x, y, dx, dy), rx, ry);
      fillAndPaintPath (apath, pathStyle);
   }

   public void processSvgCircle  (float xcenter, float ycenter, float radio, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgCircle", "cx cy radius (" + xcenter + ", " + ycenter + " / " + radio + ") style [" + pathStyle + "]");
      //uniUtil.printLater ("processSvgCircle...cx cy radius (" + xcenter + ", " + ycenter + " / " + radio + ") style [" + pathStyle + "]");

      uniPath apath = new uniPath ();
      apath.getEdiPaths ().addCircle (xcenter, ycenter, radio);
      fillAndPaintPath (apath, pathStyle);
   }

   public void processSvgOval  (float left, float top, float width, float height, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgOval", "left top width height (" + left + ", " + top + " / " + width + ", " + height + ") style [" + pathStyle + "]");
      //uniUtil.printLater ("processSvgOval...left top width height (" + left + ", " + top + " / " + width + ", " + height + ") style [" + pathStyle + "]");

      uniPath apath = new uniPath ();
      apath.getEdiPaths ().addOval (new uniRect (false, left, top, width, height));
      fillAndPaintPath (apath, pathStyle);
   }

   public void processSvgEllipse (float cx, float cy, float rx, float ry, String pathStyle)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgEllipse", "cx cy rx ry (" + cx + ", " + cy + " / " + rx + ", " + ry + ") style [" + pathStyle + "]");
      //uniUtil.printLater ("processSvgEllipse...cx cy rx ry (" + cx + ", " + cy + " / " + rx + ", " + ry + ") style [" + pathStyle + "]");

      uniPath apath = new uniPath ();
      apath.getEdiPaths ().addOval  (new uniRect (true, cx, cy, rx, ry));
      fillAndPaintPath (apath, pathStyle);
   }

   private void fillAndPaintPath (uniPath nowPath, String pathStyle)
   {
      nowPath.setStyle (pathStyle);
      super.addElement (nowPath);
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

   public void processSvgText (String strData, float x, float y, boolean withRectangle, String style)
   {
      if (log.isDebugging (4))
         log.dbg (4, "processSvgText", "text  [" + strData + "] x " + x +  " y " + y);

      super.addElement (new textElement (strData, x, y, style, withRectangle));
   }
}
