/*
package de.elxala.zWidgets
Copyright (C) 2022 Alejandro Xalabarder Aulet

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
import javax.swing.Icon;

import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javaj.widgets.basics.*;
import de.elxala.zServices.*;


/*
   //(o) WelcomeGastona_source_javaj_widgets (s) z2DGraphicScenes

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       z2DCebaEditor
   <groupInfo>  misc
   <javaClass>  javaj.widgets.z2DCebaEditor
   <prefix>  2De
   <importance> 4
   <desc>       //A canvas to draw 2D geometries related with a Ceba scene exportable and importable to a rm3 (sequence of Trassos)

   <help>
      //
      // Experimental!
      //

   <attributes>
     name             , in_out, possibleValues            , desc

     visible          , in    , 0 / 1                     , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                     , //Value 0 to disable the widget
     var              , in    , Eva name                  , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

     backcolor        , in    , gray                      , back color
     image            , in    ,                           , file name of picture to load as background
     alpha            , in    ,                           , alpha or opacity for the background image (real value between 1 and 0)

  <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control

   <examples>
     gastSample


#**FIN EVA#

*/

/**
   2022-03-19 12:29:48 from z2DEditRelos.java

*/
public class z2DCebaEditor extends uniSceneInMotionView implements MensakaTarget
{
   private static final Color black = new Color(0, 0, 0);
   private basicAparato helper = null;
   private cebaEditor laCeba = null;

   protected static final int RX_LOAD_RM3 = 1;
   protected static final int RX_SAVE_RM3 = 2;
   protected static final int RX_EDIT_DELETE_TRASS = 3;
   protected static final int RX_ZOOM   = 4;
   protected static final int RX_ZOOMIN = 5;
   protected static final int RX_ZOOMUT = 6;

   protected static final int RX_KEYBOARD_PRESS = 10;
   protected static final int RX_KEYBOARD_RELEASE = 11;

   public z2DCebaEditor ()
   {
      laCeba = new cebaEditor ("?");
      assignNewScene (laCeba);
      init ("?");
   }

   public z2DCebaEditor (String map_name)
   {
      laCeba = new cebaEditor (map_name);
      assignNewScene (laCeba);
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

      Mensaka.subscribe (this, RX_ZOOM    , helper.ebs ().evaName ("zoom"));
      Mensaka.subscribe (this, RX_ZOOMIN  , helper.ebs ().evaName ("zoomIn"));
      Mensaka.subscribe (this, RX_ZOOMUT  , helper.ebs ().evaName ("zoomOut"));
      Mensaka.subscribe (this, RX_LOAD_RM3, helper.ebs ().evaName ("loadRm3"));
      Mensaka.subscribe (this, RX_SAVE_RM3, helper.ebs ().evaName ("saveRm3"));
      Mensaka.subscribe (this, RX_EDIT_DELETE_TRASS, helper.ebs ().evaName ("deleteCurrentTrass"));

      Mensaka.subscribe (this, RX_KEYBOARD_PRESS, "javaj keypress");
      Mensaka.subscribe (this, RX_KEYBOARD_RELEASE, "javaj keyrelease");
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

   private int getPar1Int (String [] pars, int defaultValue)
   {
      return (pars == null || pars.length == 0) ? defaultValue: stdlib.atoi (pars[0]);
   }

   private String getPar1Str (String [] pars, String defaultValue)
   {
      return (pars == null || pars.length == 0) ? defaultValue: pars[0];
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_JAVAJ_FRAMES_MOUNTED:
            laCeba.initPathArray ();
            break;

         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            loadAllData ();
            render ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);

            //setEnabled (helper.ebs ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)

            //String vale = helper.ebs ().getSimpleDataAttribute ("MIN_DISTANCE_TWO_PTS");
            //if (vale != null) laCeba.setMIN_DISTANCE_TWO_PTS (stdlib.atoi (vale));

            String vale = helper.ebs ().getSimpleDataAttribute ("EDIT_POINT_RECT");
            if (vale != null) laCeba.EDIT_POINT_RECT  = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("NPUNTS_TOLERANCE_FACTOR");
            if (vale != null) laCeba.NPUNTS_TOLERANCE_FACTOR  = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("NPUNTS_TOLERANCE");
            if (vale != null) laCeba.NPUNTS_TOLERANCE = stdlib.atoi (vale);

            boolean visible = helper.ebs ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);

            render ();
            break;

         // custom positive values ...
         //
         case RX_KEYBOARD_PRESS:
         case RX_KEYBOARD_RELEASE:
            if (pars.length >= 2)
            {
               laCeba.keyboardEvent (mappedID == RX_KEYBOARD_PRESS, stdlib.atoi (pars[0]), pars[1]);
               render ();
            }
            break;

         case RX_LOAD_RM3:
            laCeba.loadRm3 (pars.length > 0 ? pars[0]: null);
            render ();
            break;

         case RX_SAVE_RM3:
            laCeba.saveRm3 (pars.length > 0 ? pars[0]: null);
            break;

         case RX_EDIT_DELETE_TRASS:
            // ?? is this what we want ?? ....
            laCeba.initPathArray ();
            render ();
            break;

         default:
            return false;
      }

      return true;
   }

   // ------------------------------------------
   // future uniBackColorAndImage ...
   //
   protected Icon backgroundImage = null;
   protected float backgroundImageAlpha = 1.f;
   protected Color backColor = Color.GRAY;

   protected void readBackColorAndImage ()
   {
      // load background image if any
      String backgImgFile = helper.ebs ().getSimpleDataAttribute ("image");
      if (backgImgFile != null && backgImgFile.length () > 0)
           backgroundImage = javaLoad.getSomeHowImageIcon (backgImgFile);
      else backgroundImage = null;

      backgroundImageAlpha = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("alpha", "1"));
      if (backgroundImageAlpha == 1.f)
         backgroundImageAlpha = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("opacity", "1"));

      String backC = helper.ebs ().getSimpleDataAttribute ("backcolor");
      if (backC != null)
      {
         uniColor uco = new uniColor ();
         uco.parseColor (backC);
         backColor = uco.getNativeColor ();
      }
   }

   protected void paintBackColorAndImage (Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      if (backColor != null)
      {
         g.setColor (backColor);
         g.fillRect (0, 0, d.width, d.height);
      }
      else
         g.clearRect (0, 0, d.width, d.height);

      if (backgroundImage != null)
      {
         int left  = (d.width - backgroundImage.getIconWidth()) / 2;
         int right = (d.height - backgroundImage.getIconHeight()) / 2;

         if (backgroundImageAlpha < 1.f)
         {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, backgroundImageAlpha));
            backgroundImage.paintIcon(this, g2d, left, right);
         }
         else
         {
            backgroundImage.paintIcon(this, g, left, right);
         }
      }
   }
   // end of future uniBackColorAndImage ...
   // ------------------------------------------

   public void loadAllData ()
   {
      readBackColorAndImage ();
   }

   private boolean paintingNow = false;

   public void paint(Graphics g)
   {
      if (!paintingNow)
      {
         paintingNow = true;

         paintBackColorAndImage (g);
         uniCanvas ges2 = new uniCanvas ((Graphics2D) g, getX(), getY(), getWidth (), getHeight ());
         laCeba.renderUniCanvas (ges2, null);

         paintingNow = false;
     }
   }
}
