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
import javaj.widgets.gestures.*;



public class zBasicGraphic2D extends JPanel
                             implements MouseListener
                                        , zoomTouchDetector.interested
                                        , strokeDetector.interested
                                        , multiFingerTouchDetector.interested
{
   private static final Color black = new Color(0, 0, 0);
//   private static final Color white = new Color(240, 240, 255);
//   private static final Color red = new Color(149, 43, 42);
//   private static final Color blue = new Color(94, 105, 176);
//   private static final Color yellow = new Color(255, 255, 140);


   private BufferedImage bimg;

   private zoomTouchDetector zoomDetector = null;
   private strokeDetector trazaDetector = null;
   private multiFingerTouchDetector pulpoDetector = null;

   public zBasicGraphic2D (String map_name)
   {
      init (map_name);
   }

   public void init (String map_name)
   {
      addMouseListener (this);

      zoomDetector  = new zoomTouchDetector (this);
      trazaDetector = new strokeDetector (this);
      pulpoDetector = new multiFingerTouchDetector (this);

      // set default things
      setPreferredSize (new Dimension (500, 400));
      setBackground    (black);
      setLayout        (new BorderLayout());

      // set own name
      this.setName (map_name);
   }


   public void setName (String map_name)
   {
      super.setName (map_name);
   }

   public void onUniMotion (uniMotion uni)
   {
      zoomDetector.onTouchEvent(uni);
      if (!zoomDetector.gestureInProgress ())
         trazaDetector.onTouchEvent(uni);

      pulpoDetector.onTouchEvent(uni);
   }

   public void mousePressed (MouseEvent event)
   {
      //log.dbg (2, "onTouchEvent", "event action " + event.getAction() " pointer count " + event.getPointerCount());
      System.out.println ("posal");

      uniMotion uEvent = new uniMotion (event, uniMotion.FIRST_POINTER_DOWN);
      onUniMotion (uEvent);
   }

   public void mouseReleased (MouseEvent event)
   {
      uniMotion uEvent = new uniMotion (event, uniMotion.LAST_POINTER_UP);
      onUniMotion (uEvent);
   }

   public void mouseClicked (MouseEvent e)
   {
   }

   public void mouseEntered (MouseEvent e)
   {
   }

   public void mouseExited (MouseEvent e)
   {
   }


   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      if (bimg == null || bimg.getWidth() != d.width || bimg.getHeight() != d.height)
      {
         bimg = getGraphicsConfiguration().createCompatibleImage(d.width, d.height);
      }

      Graphics2D g2 = bimg.createGraphics();
      g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setBackground    (getBackground ());
      g2.clearRect        (0, 0, d.width, d.height);

      g2.draw (new Line2D.Float (new Point2D.Double (0., 0.), new Point2D.Double (100., 100.)));

      g2.dispose();
      g.drawImage(bimg, 0, 0, this);
   }


   /// implementing zoomTouchDetector.interested
   public boolean onGestureStart (zoomTouchDetector detector)
   {
      return true;
   }

   /// implementing zoomTouchDetector.interested
   public boolean onGestureContinue (zoomTouchDetector detector)
   {
      invalidate ();
      return true;
   }

   /// implementing zoomTouchDetector.interested
   public void onGestureEnd (zoomTouchDetector detector, boolean cancel)
   {
      invalidate ();
   }

   /// implementing strokeDetector.interested
   public boolean onGestureStart (strokeDetector detector)
   {
      if (zoomDetector.gestureInProgress ())
         return false;
      return true;
   }

   /// implementing strokeDetector.interested
   public boolean onGestureContinue (strokeDetector detector)
   {
      if (zoomDetector.gestureInProgress ())
         return false;

      invalidate ();
      return true;
   }

   /// implementing strokeDetector.interested
   public void onGestureEnd (strokeDetector detector, boolean cancel)
   {
      invalidate ();
   }


   public void onFingerDown    (multiFingerTouchDetector detector, int fingerIndx)
   {
      invalidate ();
   }

   public void onFingerUp      (multiFingerTouchDetector detector, int fingerIndx)
   {
      invalidate ();
   }

   public void onMovement      (multiFingerTouchDetector detector)
   {
      invalidate ();
   }

   public void onGestureEnd    (multiFingerTouchDetector detector, boolean cancel)
   {
      invalidate ();
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
            2 , zBasicGraphic2D, graphics ,    3       , help 2,  //A basic graphisc 2D

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
         2        , 2 zBasicGraphic2D   , //Data for widget tester
         2        , hello zButton       , //A basic use of a zBasicGraphic2D

   <2 testData>
      //
      //

   <hello zBasicGraphic2D>
      //#listix#
      //
      //#data#
      //
      //#javaj#
      //
      //    <frames> F, Hello zBasicGraphic2D
      //
      //    <layout of F>
      //          PANEL, X
      //          2Panel2D
      //

#**FIN_EVA#

*/
