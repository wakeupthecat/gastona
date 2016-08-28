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

import android.widget.*;
import android.view.View;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;

import javaj.widgets.basics.*;
import de.elxala.zServices.*;

/**
*/
public class z2DGraphicScenes extends uniSceneInMotionView implements MensakaTarget, izWidget
{
   private final int SCALE = 11;
   private final int OFFSET = 13;
   private final int SAVEPNGSET = 14;
   private final int SAVEPNG = 15;
   private final int DUMP = 16;

   private basicAparato helper = null;

   private sceneInMotion miEscena = new sceneInMotion ();
   //private Color backColor = new JButton().getBackground (); // new JButton().getBackgroundColor ();

   // own data
   //private Drawable theDrawable = null;

   public z2DGraphicScenes (Context co)
   {
      super (co);
      assignNewScene (miEscena);
   }

   // ------
   public z2DGraphicScenes (Context co, String map_name)
   {
      super (co);
      assignNewScene (miEscena);
      init (map_name);
      setName (map_name);
   }

   public void init (String map_name)
   {
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

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String map_name)
   {
      mName = map_name;
      init (map_name);
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
            if (widgetLogger.updateContainerWarning (
                  "data", "zImage",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);
            tryAttackWidget ();
            paintAll ();
            render ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "zImage",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);

            if (helper.ebs().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

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
            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
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
         }
      }
   }

   private void tryAttackWidget ()
   {
      if (helper.ebs().hasAll ())
      {
         String sval = "";

         sval = helper.ebs ().getSimpleDataAttribute ("gestic");
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
      }
   }

   public void paintAll ()
   {
      miEscena.laEscena.clearGraphic ();

      // load svg if given
      String imageFileName = fileUtil.resolveCurrentDirFileName (helper.ebs ().getText ());
      if (imageFileName != null && imageFileName.length () > 0)
      {
         //setGestureMode (MODE_GESTURE_DEFAULT);
         widgetLogger.log ().dbg (2, "loading svg file " + imageFileName);

         graphicObjectLoader obLo = new graphicObjectLoader ();
         obLo.loadObjectFromSvg (imageFileName);
         miEscena.laEscena.addObject (obLo);
      }

      // paint attribute graphic
      graphicObjectLoader obLo = new graphicObjectLoader ();
      obLo.loadObjectFromEva ("FONDOgraphic",
                              helper.ebs ().getDataAttribute ("graphic"),
                              helper.ebs ().getDataAttribute ("graphicPress"),
                              "1111",
                              null);
      miEscena.laEscena.addObject (obLo);

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
         EvaLine el = scene.get(ii);
         String graphname = scene.getValue (ii, 0);
         String basicMov  = scene.getValue (ii, 1);

         widgetLogger.log ().dbg (2, "paintSceneData", "loading graph for [" + graphname + "]");

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

         // support for trazos and trazosPress (editable paths)
         //
         if (helper.ebs ().getDataAttribute ("trazos " + graphname) != null)
         {
            widgetLogger.log ().dbg (2, "paintSceneData", "found trazos attribute for [" + graphname + "]");
            graphicObjectLoader obLo = new graphicObjectLoader ();

            obLo.loadObjectFromEvaTrazos (graphname,
                                          helper.ebs ().getDataAttribute ("trazos " + graphname),
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
      widgetLogger.log ().err ("saveCurrentPNG", "saveCurrentPNG is not implemented in android");
      //Dimension d = getSize();
      //String baseName = helper.ebs ().getSimpleDataAttribute ("pngName");
      //if (baseName == null || baseName.length () == 0) baseName = "generatedPNG_";
      //savePNG (baseName + d.width + "x" + d.height + ".png", d.width, d.height);
   }

   protected void savePNGs ()
   {
      // de http://developer.android.com/guide/practices/screens_support.html
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
      widgetLogger.log ().err ("savePNG", "savePNG is not implemented in android");

      //BufferedImage bimg = getGraphicsConfiguration().createCompatibleImage(dx, dy);
      //uniCanvas ges2 = new uniCanvas (bimg.createGraphics(), 0, 0, dx, dy);
      //
      //ges2.getG ().setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      ////ges2.getG ().setBackground    (getBackground ());
      ////ges2.getG ().setBackground    (Color.GRAY);
      //ges2.getG ().clearRect        (0, 0, dx, dy);
      //miEscena.setAutoFit (true, true);
      //miEscena.renderUniCanvas (ges2, null); //(o) TODO TOTEST! set color transparent ???
      //uniUtilImage.saveBufferedImageTofile (bimg, name, "png");
   }
}
