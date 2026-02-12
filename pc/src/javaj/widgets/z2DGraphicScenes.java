/*
package de.elxala.zWidgets
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

package javaj.widgets;

import java.awt.*;
import java.awt.image.BufferedImage;

import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import de.elxala.langutil.graph.uniUtilImage;

import javaj.widgets.basics.*;
import de.elxala.zServices.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (s) z2DGraphicScenes

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       z2DGraphicScenes
   <groupInfo>  misc
   <javaClass>  javaj.widgets.z2DGraphicScenes
   <prefix>  2Ds
   <importance> 4
   <desc>       //A canvas to draw 2D geometries with zoom and movement capabilities

   <help>

      //
      // A canvas where you can place a scene composed by objects. Basically in the attribute "scene"
      // you define the objects of the scene, the order will be the z-order (behind ... top). For instance
      //
      //    <2DsMyPaint scene>
      //       background
      //       stick
      //       ball
      //
      // then for each object an attribute "graphic NAME" will define its final aspect
      //
      //    <2DsMyPaint graphic background>
      //       rect, "fill:blue", 0, 0, 200, 400
      //
      //    <2DsMyPaint graphic stick>
      //       path, "fill:brown", "M 200 60 c 10 40 15 43 22 18 34 7 z"
      //
      //    <2DsMyPaint graphic ball>
      //       circle, "fill:green", 130, 100, 50
      //
      // the attribute "graphic NAME" admits multiple lines and following geometries are supported
      //
      //    geometrie      parameters
      //    -----------    ---------------------------------------------
      //    path           style, path data
      //    rect           style, left, top, width, height, radius x, radius y
      //    polygon        style, points
      //    polyline       style, points
      //    circle         style, center x, center y, radius
      //    ellipse        style, center x, center y, radius x, radius y
      //    oval           style, left, top, width, height
      //    text           style, left, right, width, ?, text
      //    svgfile        name of svg file (not all that svg defines is supported but the geometries path, rect, etc)
      //
      // where "style" and "path data" are very similar as defined in SVG 1.1 with slight variations
      //
      //
      // NOTE: This widget has been added lately and its specification, although useful, still has to improved and
      //       in these changes might loose compatibility with the defined here!
      //

   <attributes>
     name             , in_out, possibleValues            , desc

     visible          , in    , 0 / 1                     , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                     , //Value 0 to disable the widget
     var              , in    , Eva name                  , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

     gestic          , in    , number                     , //Determines which zoom and translations are allowed, for instance 24 allow translation of the objects and 25 zoom as well. (ZOOM_MULTITOUCH 1, ZOOM_X 2, ZOOM_Y 4, TRANS_X 8, TRANS_Y 16, MESSAGES 32)
     autofit         , in    , 1 / 0                      , //If 1 the objects are atomatically resized to fit all them in the whole area

     scaleX          , inout , number                     , //Scale in X axis
     scaleY          , inout , number                     , //Scale in Y axis
     offsetX         , inout , number                     , //Offset in X axis
     offsetY         , inout , number                     , //Offset in Y axis

     scene           , in    , table                      , //table of two columns (no header) : name of the object, movements allowed as 0/1 up,down,left,right for example 1101 means all movements except left.
     graphic NAME    , in    , table                      , //table of geometries defining the object with name NAME, possible geometries are path, rect, circle, oval, ellipse, polygon, polyline, text and svgfile

   <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control

      scale       , in  , to change the scales using the attributes scaleX and scaleY
      offset      , in  , to change the offets using the attributes offsetX and offsetY

   <examples>
     gastSample

     scene noche
     grid and curve

   <scene noche>
      //#javaj#
      //
      //    <frames> f1,
      //
      //    <layout of f1>
      //       EVA, 4, 4, 3, 3
      //          , X
      //        X , 2DsMyPaint
      //       100, oConso
      //
      //#listix#
      //
      //    <-- graphicPress fledy>
      //        //you clicked on fledy ...
      //        //
      //
      //#data#
      //
      //    <2DsMyPaint scene>
      //       noche
      //       luna
      //       fledy
      //
      //    <2DsMyPaint graphic noche>
      //       rect, "fill:#440053", 0, 0, 600, 400
      //
      //    <2DsMyPaint graphic luna>
      //       circle, "fc:#C2C2C2", 260, 170, 110
      //
      //    <2DsMyPaint graphic fledy>
      //       path, "fc:black",  "M  190  237 j   55 -5  48 -31  61  37  -45  3 -18  27 -31 -5 -21  20 -17  23 -27 -21 -50 -4 -23 -17 -30  8  37 -48 z"
      //       path, "fc:black",  "M  194  240 j  -10 -18 -1 -16  11  7  23  5  26 -18  1  22 -5  15"
      //
      //    <2DsMyPaint graphicPress fledy>
      //       path, "fc:red",  "M  190  237 j   55 -5  48 -31  61  37  -45  3 -18  27 -31 -5 -21  20 -17  23 -27 -21 -50 -4 -23 -17 -30  8  37 -48 z"
      //       path, "fc:red",  "M  194  240 j  -10 -18 -1 -16  11  7  23  5  26 -18  1  22 -5  15"
      //       text, "font-family:Tahoma;fc:;fs:22", 180, 150, 0, 0, Cuñaaaao!
      //

   <grid and curve>
      //#javaj#
      //
      //   <frames> f1,
      //   <layout of f1>
      //      EVA, 4, 4, 3, 3
      //         , X
      //      X , 2DsMyPaint
      //
      //#listix#
      //
      //   <main>
      //      LSX, crea grid(x0 x1 incx y0 y1 incy var), -50, 50, 10, -40, 40, 10, "2DsMyPaint graphic grid"
      //      MSG, 2DsMyPaint data!
      //
      //   <crea grid(x0 x1 incx y0 y1 incy var)>
      //      VAR=, @<p7>, ""
      //      LOOP, FOR, x, @<p1>, @<p2>, @<p3>
      //         ,, VAR+, @<p7>, path, "sw:0.1", "M @<x> @<p4> L @<x> @<p5>"
      //      LOOP, FOR, y, @<p4>, @<p5>, @<p6>
      //         ,, VAR+, @<p7>, path, "sw:0.1", "M @<p1> @<y> L @<p2> @<y>"
      //      VAR+, @<p7>, text, "font-family:Tahoma;fc:;fs:2", 0, 0, 0, 0, ""
      //      LOOP, FOR, x, @<p1>, @<p2>, @<p3>
      //         ,, VAR+, @<p7>, text, "font-family:Tahoma;fs:2", @<x+.3>, 0, 0, 0, @<x>
      //      LOOP, FOR, y, @<p4>, @<p5>, @<p6>
      //         ,, VAR+, @<p7>, text, "font-family:Tahoma;fs:2", .3, @<y>, 0, 0, @<invy>
      //
      //   <x+.3> =, x+.3
      //
      //   <invy> =, -@<y>
      //
      //#data#
      //
      //   <2DsMyPaint scene>
      //      grid
      //      dabajo
      //
      //   <2DsMyPaint graphic dabajo>
      //      path, "fc:none;sw:.4;sc:yellow",      "M  -40 0 L -30 -40 -10 0 40 0"
      //      path, "fc:none;sw:.4;sc:+250143090",  "M  -40 0 C -30 -40 -10 0 40 0"
      //      path, "fc:none;sw:.4;sc:green",       "M  -40 0 J -30 -40 -10 0 40 0"
      //

#**FIN_EVA#

*/



/**
*/
public class z2DGraphicScenes extends uniSceneInMotionView implements MensakaTarget
{
   private static final Color black = new Color(0, 0, 0);
   private final int SCALE = 11;
   private final int OFFSET = 13;
   private final int SAVEPNGSET = 14;
   private final int SAVEPNG = 15;
   private final int DUMP = 16;

   private basicAparato helper = null;

   //private Color backColor = new JButton().getBackground (); // new JButton().getBackgroundColor ();

   // own data
   //private Drawable theDrawable = null;

   private sceneInMotion miEscena = new sceneInMotion ();

   public z2DGraphicScenes ()
   {
      assignNewScene (miEscena);
      init ("?");
   }

   public z2DGraphicScenes (String map_name)
   {
      assignNewScene (miEscena);
      init (map_name);
   }

   public void init (String map_name)
   {
      this.setName (map_name);

      // set default things
      setPreferredSize (new Dimension (500, 400));
      setBackground    (black);
      setLayout        (new BorderLayout());


      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      Mensaka.subscribe (this, SCALE, map_name + " scale");   // simply scale
      Mensaka.subscribe (this, SCALE, map_name + " scaleX");  // to be deprecated
      Mensaka.subscribe (this, SCALE, map_name + " scaleY");  // to be deprecated
      Mensaka.subscribe (this, OFFSET, map_name + " offset");    // simply offset
      Mensaka.subscribe (this, OFFSET, map_name + " offsetX");   // to be deprecated
      Mensaka.subscribe (this, OFFSET, map_name + " offsetY");   // to be deprecated
      Mensaka.subscribe (this, SAVEPNGSET, map_name + " savePNGset");
      Mensaka.subscribe (this, SAVEPNG, map_name + " savePNG");

      Mensaka.subscribe (this, DUMP, map_name + " dump");
   }

   //-i- interface iWidget
   //
   public int getDefaultHeight ()
   {
      //if (theDrawable != null) return theDrawable.getIntrinsicHeight ();
      return 100;
   }

   public int getDefaultWidth ()
   {
      //if (theDrawable != null) return theDrawable.getIntrinsicWidth ();
      return 100;
   }

   //-i-

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case DUMP:
            dump ();
            break;

         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            loadAllData ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);

            String sval = helper.ebs ().getSimpleDataAttribute ("gestic");
            if (sval != null)
               miEscena.setGestureMode (stdlib.atoi (sval));

            sval = helper.ebs ().getSimpleDataAttribute ("autofit");
            if (sval != null)
            {
               miEscena.setAutoFit (sval.length () > 0 && sval.charAt (0) == '1',
                                     sval.length () > 1 && sval.charAt (1) == '1');
            }

            String svalX = helper.ebs ().getSimpleDataAttribute ("scaleX");
            String svalY = helper.ebs ().getSimpleDataAttribute ("scaleY");
            float scale_x = (svalX != null) ? (float) stdlib.atof (svalX): miEscena.getScaleX ();
            float scale_y = (svalY != null) ? (float) stdlib.atof (svalY): miEscena.getScaleY ();

            svalX = helper.ebs ().getSimpleDataAttribute ("offsetX");
            svalY = helper.ebs ().getSimpleDataAttribute ("offsetY");
            float offset_x = (svalX != null) ? (float) stdlib.atof (svalX): miEscena.getOffsetX ();
            float offset_y = (svalY != null) ? (float) stdlib.atof (svalY): miEscena.getOffsetY ();

            miEscena.setScale (scale_x, scale_y);
            miEscena.setOffset (offset_x, offset_y);

            setEnabled (helper.ebs ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebs ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);

            render ();
            break;

         case SCALE:
            float sX = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("scaleX"));
            float sY = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("scaleY"));
            miEscena.setScale (sX, sY);
            render ();
            break;

         case OFFSET:
            float oX = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("offsetX"));
            float oY = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("offsetY"));
            miEscena.setOffset (oX, oY);
            render ();
            break;

        case SAVEPNGSET:
            savePNGs ();
            break;

        case SAVEPNG:
            saveCurrentPNG ();
            break;

         default:
            return false;
      }

      return true;
   }

   public void dump ()
   {
      //System.out.println ("la escena de size : " + miEscena.laEscena.arrEscena.size ());
      for (int ii = 0; ii < miEscena.laEscena.arrEscena.size (); ii ++)
      {
         objectGraph ob = (objectGraph) miEscena.laEscena.arrEscena.get (ii);
         //System.out.println ("los objetos de la escena " + ii + " de size : " + ob.objElements.size ());
         for (int oo = 0; oo < ob.objElements.size (); oo ++)
         {
            Object pe = ob.objElements.get (oo);
            if (pe instanceof uniPath)
            {
               String sal = ((uniPath) pe).mEdiPaths.toString ();
               Mensaka.sendPacket (getName () + " dumping", null, new String [] { sal });
               System.out.println (sal);
            }
            else if (pe instanceof textElement)
            {
                textElement te = (textElement) pe;
                String sal = "text, " + te.x + ", " + te.y + ", " + te.mStyleStr + ", //" +  te.mText;
                System.out.println (sal);
                Mensaka.sendPacket (getName () + " dumping", null, new String [] { sal });
            }
         }
      }
   }

   public void loadAllData ()
   {
      miEscena.laEscena.clearGraphic ();

      // load svg if given
      //String imageFileName = fileUtil.resolveCurrentDirFileName (helper.ebs ().getText ());

      String imageFileName = helper.ebs ().getText ();
      if (imageFileName != null && imageFileName.length () > 0)
      {
         //setGestureMode (MODE_GESTURE_DEFAULT);
         widgetLogger.log ().dbg (2, "loading svg file " + imageFileName);

         graphicObjectLoader obLo = new graphicObjectLoader ();
         obLo.loadObjectFromSvg (imageFileName);
         miEscena.laEscena.addObject (obLo);
      }

      //(o) TODO/javaj/GraphicScene support also FONDOgraphic from trassos format
      // paint attribute graphic
      graphicObjectLoader obLo = new graphicObjectLoader ();
      obLo.loadObjectFromEva ("FONDOgraphic",
                              helper.ebs ().getDataAttribute ("graphic"),
                              helper.ebs ().getDataAttribute ("graphicPress"),
                              "1111",
                              null);
      miEscena.laEscena.addObject (obLo);

      // paint scene
      loadSceneData ();
      //paintImmediately (0, 0, 3000, 3000);
      paintImmediately (getVisibleRect());
   }

   public void loadSceneData ()
   {
      Eva scene = helper.ebs ().getDataAttribute ("scene");
      if (scene == null) return;

      //setGestureMode (MODE_GESTURE_OBJECTS);
      //   graphic name, posx, posy, scalex, scaley

      widgetLogger.log ().dbg (2, "loadSceneData", "have " + scene.rows () + " graphics");
      for (int ii = 0; ii < scene.rows (); ii ++)
      {
         EvaLine el = scene.get(ii);
         String graphname = scene.getValue (ii, 0);
         String basicMov  = scene.getValue (ii, 1);

         widgetLogger.log ().dbg (2, "loadSceneData", "loading graph for [" + graphname + "]");

         float posx = numAt(el, 2);
         float posy = numAt(el, 3);
         float scalex = numAt(el, 4);
         float scaley = numAt(el, 5);
         if (scalex == .0f) scalex = 1.f;
         if (scaley == .0f) scaley = 1.f;

         // support for graphic and graphicPress (quasi svg paths)
         //
         if (helper.ebs ().getDataAttribute ("graphic " + graphname) != null)
         {
            widgetLogger.log ().dbg (2, "paintSceneData", "found graphic attribute for [" + graphname + "]");
            graphicObjectLoader obLo = new graphicObjectLoader ();
            obLo.loadObjectFromEva (graphname,
                                    helper.ebs ().getDataAttribute ("graphic " + graphname),
                                    helper.ebs ().getDataAttribute ("graphicPress " + graphname),
                                    basicMov,
                                    new offsetAndScale (posx, posy, scalex, scaley));
            miEscena.laEscena.addObject (obLo);
         }

         // support for trassos and trassosPress (editable paths)
         //
         if (helper.ebs ().getDataAttribute ("trassos " + graphname) != null)
         {
            widgetLogger.log ().dbg (2, "paintSceneData", "found trassos attribute for [" + graphname + "]");
            graphicObjectLoader obLo = new graphicObjectLoader ();

            obLo.loadObjectFromEvaTrassos (graphname,
                                          helper.ebs ().getDataAttribute ("trassos " + graphname),
                                          helper.ebs ().getDataAttribute ("graphicPress " + graphname),
                                          basicMov,
                                          new offsetAndScale (posx, posy, scalex, scaley));
            miEscena.laEscena.addObject (obLo);
         }
      }
   }

   private float numAt (EvaLine el, int col)
   {
      return (float) stdlib.atof (el.getValue (col));
   }

   protected void saveCurrentPNG ()
   {
      Dimension d = getSize();
      String baseName = helper.ebs ().getSimpleDataAttribute ("pngName");
      if (baseName == null || baseName.length () == 0) baseName = "generatedPNG_";
      savePNG (baseName + d.width + "x" + d.height + ".png", d.width, d.height);
   }

   protected void savePNGs ()
   {
      // de https://developer.android.com/guide/practices/screens_support
      // To create alternative bitmap drawables for different densities,
      // you should follow the 3:4:6:8 scaling ratio between the four
      // generalized densities. For example, if you have a bitmap drawable that's
      // 48x48 pixels for medium-density screen (the size for a launcher icon),
      // all the different sizes should be:
      //
      //    36x36 for low-density              24x24
      //    48x48 for medium-density           32x32
      //    72x72 for high-density             48x48
      //    96x96 for extra high-density       64x64
      String baseName = helper.ebs ().getSimpleDataAttribute ("pngName");
      if (baseName == null || baseName.length () == 0) baseName = "generatedPNG_";
      for (int ii = 0; ii < 4; ii ++)
      {
         int ta = (ii == 0) ? 24: (ii == 1) ? 32: (ii == 2) ? 48: 64;
         savePNG (baseName + ta + "x" + ta + ".png", ta, ta);
      }
   }

   protected void savePNG (String name, int dx, int dy)
   {
      BufferedImage bimg = getGraphicsConfiguration().createCompatibleImage(dx, dy);
      uniCanvas ges2 = new uniCanvas (bimg.createGraphics(), 0, 0, dx, dy);

      ges2.getG ().setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      //ges2.getG ().setBackground    (getBackground ());
      //ges2.getG ().setBackground    (Color.GRAY);
      ges2.getG ().clearRect        (0, 0, dx, dy);
      miEscena.setAutoFit (true, true);
      miEscena.renderUniCanvas (ges2, null); //(o) TODO TOTEST! set color transparent ???
      uniUtilImage.saveBufferedImageTofile (bimg, name, "png");
   }

   private boolean paintingNow = false;

   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      if (!paintingNow)
      {
         paintingNow = true;

         uniCanvas ges2 = new uniCanvas ((Graphics2D) g, getX(), getY(), getWidth (), getHeight ());
         ges2.getG ().setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


         // NOTE: scale and translation IS DONE IN Scene.render2D !!
         ////      ges2.getG ().translate (getOffsetX(), getOffsetY());
         ////      ges2.getG ().scale (getScaleX(), getScaleY());

         miEscena.renderUniCanvas (ges2, new uniColor(uniColor.LGRAY));
   //      ges2.getG().setColor (Color.ORANGE);
   //      ges2.getG().drawLine (0, 0, 100, 100);

   //      ges2.getG ().dispose();
   //      g.drawImage(bimg, 0, 0, this);
         paintingNow = false;
     }
   }

}


/*


#javaj#

    <frames> f1,

    <layout of f1>
       EVA, 4, 4, 3, 3
          , X
        X , 2DsMyPaint
       100, oConso
          , bStampa

#listix#

	<-- bStampa> MSG, 2DsMyPaint dump

    <-- graphicPress fledy>
        //you clicked on fledy ...
        //

#data#

    <2DsMyPaint scene>
       noche

    <2DsMyPaint graphic noche>
       rect, "fill:#440053", 0, 0, 600, 400
       circle, "fc:#C2C2C2", 260, 170, 110
       path, "fc:green",  "M  190  237 L 195 250 184 220"
       path, "fc:yellow",  "M  100  200 l 195 250 184 220"


*/