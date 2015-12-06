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

import javaj.widgets.graphics.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javaj.widgets.basics.*;
import de.elxala.zServices.*;


public class zAproGraphic2D extends SvgDrafterView implements MensakaTarget
{
   private static final Color black = new Color(0, 0, 0);
   private final int SCALE = 11;
   private final int OFFSET = 13;

   private basicAparato helper = null;
   private BufferedImage bimg;

   private static MessageHandle TX_GEST_START    = new MessageHandle ();
   private static MessageHandle TX_GEST_CONTINUE = new MessageHandle ();

   public  void messageGestureStart (float xScreen, float yScreen, float x, float y)
   {
   }
   public  void messageGestureContinue (float dxScreen, float dyScreen, float dx, float dy)
   {
   }


   public zAproGraphic2D (String map_name)
   {
      init (map_name, null);
   }

   public zAproGraphic2D (String map_name, Renderable renderer)
   {
      init (map_name, renderer);
   }

   public void init (String map_name, Renderable renderer)
   {
      this.setName (map_name);
      addMouseListener (this);

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
               SCREEN_AUTOFIT_X = sval.length () > 0 && sval.charAt (1) == '1';
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
         if (orden.equals ("text"))      processSvgText (data, numAt(3), numAt(4));
      }
   }

   private float numAt (int col)
   {
      return (float) stdlib.atof (mCurrentEvalin.getValue (col));
   }


   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      if (bimg == null || bimg.getWidth() != d.width || bimg.getHeight() != d.height)
      {
         bimg = getGraphicsConfiguration().createCompatibleImage(d.width, d.height);

         //if (miro != null) miro.reset(d.width, d.height);
      }

      uniCanvas ges2 = new uniCanvas (bimg.createGraphics());
      ges2.getG ().setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      ges2.getG ().setBackground    (getBackground ());
      ges2.getG ().clearRect        (0, 0, d.width, d.height);


      render2D (ges2);

      ges2.getG ().dispose();
      g.drawImage(bimg, 0, 0, this);


//      Dimension d = getSize();
//      if (d.width <= 0 || d.height <= 0) return; // >>>> return
//
//      if (bimg == null || bimg.getWidth() != d.width || bimg.getHeight() != d.height)
//      {
//         bimg = getGraphicsConfiguration().createCompatibleImage(d.width, d.height);
//      }
//
//      Graphics2D g2 = bimg.createGraphics();
//      g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//      g2.setBackground    (getBackground ());
//      g2.clearRect        (0, 0, d.width, d.height);
//
//      g2.draw (new Line2D.Float (new Point2D.Double (0., 0.), new Point2D.Double (100., 100.)));
//
//
//      g2.dispose();
//      g.drawImage(bimg, 0, 0, this);
   }
}




/*
   //(o) javaj_Catalog_source

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#data#


   <table_widgets>
   <!   prefix, javaName       , groupInfo,  importance, helpEvaName, desc, helpText
            2 , zAproGraphic2D, graphics ,    3       , help 2,  //A basic graphisc 2D

   <help 2>

      //A basic graphisc 2D


   <table_widgets_attributes>
    <! prefix  , name,         in_out  , possibleValues                      , desc

      2        ,visible          , in  , 0 | 1                               , //Value 0 to make the widget not visible
      2        ,enabled          , in  , 0 | 1                               , //Value 0 to disable the widget



   <table_widgets_messages>

   <! prefix  , msg, in_out, desc
      2       ,          , out   , button has been pressed
      2       , data!       , in    , update data
      2       , control!       , in    , update control


   <table_widgets_examples>
     <! prefix    , sampleEvaName       , desc, sampleText
         2        , 2 zAproGraphic2D   , //Data for widget tester
         2        , hello zButton       , //A basic use of a zAproGraphic2D

   <2 testData>
      //
      //

   <hello zAproGraphic2D>
      //#listix#
      //
      //#data#
      //
      //#javaj#
      //
      //    <frames> F, Hello zAproGraphic2D
      //
      //    <layout of F>
      //          PANEL, X
      //          2Panel2D
      //

#**FIN_EVA#

*/
