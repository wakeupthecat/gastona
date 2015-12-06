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
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import javaj.widgets.graphics.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javaj.widgets.basics.*;
import de.elxala.zServices.*;

/**
*/
public class z2DGraphicScenes extends SvgDrafterView implements MensakaTarget
{
   private static final Color black = new Color(0, 0, 0);
   private final int SCALE = 11;
   private final int OFFSET = 13;

   private basicAparato helper = null;
   private BufferedImage bimg;

   private static MessageHandle TX_GEST_START    = new MessageHandle ();
   private static MessageHandle TX_GEST_CONTINUE = new MessageHandle ();

   //private Color backColor = new JButton().getBackground (); // new JButton().getBackgroundColor ();

   // own data
   //private Drawable theDrawable = null;

   public z2DGraphicScenes ()
   {
      init ("?");
   }

   public z2DGraphicScenes (String map_name)
   {
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

      Mensaka.suscribe (this, SCALE, map_name + " scale");   // simply scale
      Mensaka.suscribe (this, SCALE, map_name + " scaleX");  // to be deprecated
      Mensaka.suscribe (this, SCALE, map_name + " scaleY");  // to be deprecated
      Mensaka.suscribe (this, OFFSET, map_name + " offset");    // simply offset
      Mensaka.suscribe (this, OFFSET, map_name + " offsetX");   // to be deprecated
      Mensaka.suscribe (this, OFFSET, map_name + " offsetY");   // to be deprecated


      TX_GEST_START = new MessageHandle ();
      TX_GEST_CONTINUE = new MessageHandle ();
      Mensaka.declare (this, TX_GEST_START   , map_name + " stroke start"   , logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_GEST_CONTINUE, map_name + " stroke continue", logServer.LOG_DEBUG_0);
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

   /*

      modo messaging

      <-- name stroke start>
         @<name stroke x0Screen>
         @<name stroke y0Screen>
         @<name stroke x0>
         @<name stroke y0>

      <-- name stroke continue>
         @<name stroke dxScreen>
         @<name stroke dyScreen>
         @<name stroke dx>
         @<name stroke dy>

      <-- name stroke end>
         @<name stroke x1Screen>
         @<name stroke y1Screen>
         @<name stroke x1>
         @<name stroke y1>
   */
   private int DATA = de.elxala.Eva.abstractTable.baseEBS.DATA;


   public void messageGestureStart (float xScreen, float yScreen, float x0, float y0)
   {
      helper.ebs ().setSimpleAttribute (DATA, "stroke x0Screen", xScreen + "");
      helper.ebs ().setSimpleAttribute (DATA, "stroke y0Screen", yScreen + "");
      helper.ebs ().setSimpleAttribute (DATA, "stroke x0", x0 + "");
      helper.ebs ().setSimpleAttribute (DATA, "stroke y0", y0 + "");
      Mensaka.sendPacket (TX_GEST_START, helper.ebs ().getData ());
   }

   public void messageGestureContinue (float dxScreen, float dyScreen, float dx, float dy)
   {
      helper.ebs ().setSimpleAttribute (DATA, "stroke dxScreen", dxScreen + "");
      helper.ebs ().setSimpleAttribute (DATA, "stroke dyScreen", dyScreen + "");
      helper.ebs ().setSimpleAttribute (DATA, "stroke dx", dx  + "");
      helper.ebs ().setSimpleAttribute (DATA, "stroke dy", dy + "");
      Mensaka.sendPacket (TX_GEST_CONTINUE, helper.ebs ().getData ());
   }

   public boolean takePacket (int mappedID, EvaUnit euData)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setNameDataAndControl (null, euData, null);
            paintAll ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setNameDataAndControl (null, null, euData);
            setEnabled (helper.ebs ().getEnabled ());
            setVisible (helper.ebs ().getVisible ());

            String sval = "";

            sval = helper.ebs ().getSimpleDataAttribute ("gestic");
            if (sval != null)
               setGestureMode (stdlib.atoi (sval));

            sval = helper.ebs ().getSimpleDataAttribute ("autofit");
            if (sval != null)
            {
               SCREEN_AUTOFIT_X = SCREEN_AUTOFIT_Y = sval.length () > 0 && sval.charAt (0) == '1';
               SCREEN_AUTOFIT_Y = sval.length () > 1 && sval.charAt (1) == '1';
            }

            String svalX = helper.ebs ().getSimpleDataAttribute ("scaleX");
            String svalY = helper.ebs ().getSimpleDataAttribute ("scaleY");
            float scale_x = (svalX != null) ? (float) stdlib.atof (svalX): getScaleX ();
            float scale_y = (svalY != null) ? (float) stdlib.atof (svalY): getScaleY ();

            svalX = helper.ebs ().getSimpleDataAttribute ("offsetX");
            svalY = helper.ebs ().getSimpleDataAttribute ("offsetY");
            float offset_x = (svalX != null) ? (float) stdlib.atof (svalX): getOffsetX ();
            float offset_y = (svalY != null) ? (float) stdlib.atof (svalY): getOffsetY ();

            setScale (scale_x, scale_y, false);
            setOffset (offset_x, offset_y, false);

            render ();

            setEnabled (helper.ebs ().getEnabled ());
            if (isShowing ())
               setVisible (helper.ebs ().getVisible ());
            break;

         case SCALE:
            float sX = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("scaleX"));
            float sY = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("scaleY"));
            setScale (sX, sY);
            break;

         case OFFSET:
            float oX = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("offsetX"));
            float oY = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("offsetY"));
            setOffset (oX, oY);
            break;


         default:
            return false;
      }

      return true;
   }

   private EvaLine mCurrentEvalin = new EvaLine ();

   public void paintAll ()
   {
      clearGraphic ();

      // load svg if given
      //String imageFileName = fileUtil.getFilePathFromVagueFilename (helper.ebs ().getText ());

      String imageFileName = helper.ebs ().getText ();
      if (imageFileName != null && imageFileName.length () > 0)
      {
         //setGestureMode (MODE_GESTURE_DEFAULT);
         widgetLogger.log ().dbg (2, "loading svg file " + imageFileName);
         laEscena.addObject ("DEFAULTsvgfile");
         setSGVFile (imageFileName);
      }

      // paint attribute graphic
      loadGraphicData ("FONDOgraphic", helper.ebs ().getDataAttribute ("graphic"), "1111", null);

      // paint scene
      paintSceneData ();
      paintImmediately (0, 0, 3000, 3000);
   }

   public void paintSceneData ()
   {
      Eva scene = helper.ebs ().getDataAttribute ("scene");
      if (scene == null) return;

      //setGestureMode (MODE_GESTURE_OBJECTS);
      //   graphic name, posx, posy, scalex, scaley

      widgetLogger.log ().dbg (2, "paintSceneData", "have " + scene.rows () + " graphics");
      for (int ii = 0; ii < scene.rows (); ii ++)
      {
         mCurrentEvalin = scene.get(ii);
         String graphname = scene.getValue (ii, 0);
         String basicMov  = scene.getValue (ii, 1);

         widgetLogger.log ().dbg (2, "paintSceneData", "loading graph for [" + graphname + "]");

         float posx = numAt(2);
         float posy = numAt(3);
         float scalex = numAt(4);
         float scaley = numAt(5);

         loadGraphicData (graphname, helper.ebs ().getDataAttribute ("graphic " + graphname), basicMov, new uniRect (posx, posy, scalex, scaley));
      }
   }

   public void loadGraphicData (String objectName, Eva gDat, String basicMov, uniRect posScala)
   {
      if (gDat == null) return;

      laEscena.addObject (objectName, basicMov, posScala);

      widgetLogger.log ().dbg (2, "paintGraphicData", "have graphic data of rows " + gDat.rows ());
      for (int ii = 0; ii < gDat.rows (); ii ++)
      {
         mCurrentEvalin = gDat.get(ii);
         String orden = gDat.getValue (ii, 0).toLowerCase ();
         String style = gDat.getValue (ii, 1);
         String data  = gDat.getValue (ii, 2);
         if (orden.equals ("path"))      processSvgPath (data, style, false, false);
         if (orden.equals ("polygon"))   processSvgPath (data, style, true, true);
         if (orden.equals ("polyline"))  processSvgPath (data, style, true, false);
         if (orden.equals ("rect"))      processSvgRect (numAt(2), numAt(3), numAt(4), numAt(5), numAt(6), numAt(7), style);
         if (orden.equals ("circle"))    processSvgCircle (numAt(2), numAt(3), numAt(4), style);
         if (orden.equals ("oval"))      processSvgOval (numAt(2), numAt(3), numAt(4), numAt(5), style);
         if (orden.equals ("ellipse"))   processSvgEllipse (numAt(2), numAt(3), numAt(4), numAt(5), style);
         if (orden.equals ("text"))      processSvgText (textAt(6), numAt(2), numAt(3));
      }
   }

   private String textAt (int col)
   {
      Cadena ctext = new Cadena (mCurrentEvalin.getValue (col));
      ctext.replaceMeOnce (new String [][] { { "\\\\", "\\" }, { "\\r", "\n" }, { "\\n", "\n" }});
      return ctext.o_str;
   }

   private float numAt (int col)
   {
      return (float) stdlib.atof (mCurrentEvalin.getValue (col));
   }

   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

//      if (bimg == null || bimg.getWidth() != d.width || bimg.getHeight() != d.height)
//      {
//         bimg = getGraphicsConfiguration().createCompatibleImage(d.width, d.height);
//
//         //if (miro != null) miro.reset(d.width, d.height);
//      }

      //uniCanvas ges2 = new uniCanvas (bimg.createGraphics());

      uniCanvas ges2 = new uniCanvas ((Graphics2D) g);
      ges2.getG ().setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      //ges2.getG ().setBackground    (getBackground ());
      ges2.getG ().setBackground    (Color.GRAY);
      ges2.getG ().clearRect        (0, 0, d.width, d.height);


      // NOTE: scale and translation IS DONE IN Scene.render2D !!
      ////      ges2.getG ().translate (getOffsetX(), getOffsetY());
      ////      ges2.getG ().scale (getScaleX(), getScaleY());

      render2D (ges2);

//      ges2.getG().setColor (Color.ORANGE);
//      ges2.getG().drawLine (0, 0, 100, 100);

//      ges2.getG ().dispose();
//      g.drawImage(bimg, 0, 0, this);
   }
}
