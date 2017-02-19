/*
package de.elxala.zWidgets
Copyright (C) 2013 Alejandro Xalabarder Aulet

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
   <name>       z2DEditRelos
   <groupInfo>  misc
   <javaClass>  javaj.widgets.z2DEditRelos
   <prefix>  2De
   <importance> 4
   <desc>       //A canvas to draw 2D geometries related with an Relos

   <help>

      //
      // Experimental!
      //

   <attributes>
     name             , in_out, possibleValues            , desc

     visible          , in    , 0 / 1                     , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                     , //Value 0 to disable the widget
     var              , in    , Eva name                  , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)


  <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control

   <examples>
     gastSample


#**FIN EVA#

*/

/**
*/
public class z2DEditRelos extends uniSceneInMotionView implements MensakaTarget
{
   private static final Color black = new Color(0, 0, 0);
   private basicAparato helper = null;
   private Icon backgroundImage = null;

   private cebollaClockInMotion miCebolla = new cebollaClockInMotion ();

   public z2DEditRelos ()
   {
      assignNewScene (miCebolla);
      init ("?");
   }

   public z2DEditRelos (String map_name)
   {
      assignNewScene (miCebolla);
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
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            //loadAllData ();
            render ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);

            //setEnabled (helper.ebs ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            
            //String vale = helper.ebs ().getSimpleDataAttribute ("MIN_DISTANCE_TWO_PTS");
            //if (vale != null) miCebolla.setMIN_DISTANCE_TWO_PTS (stdlib.atoi (vale));

            String vale = helper.ebs ().getSimpleDataAttribute ("EDIT_POINT_RECT");
            if (vale != null) miCebolla.EDIT_POINT_RECT  = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("REDUCTION_TOLERANCE");
            if (vale != null) miCebolla.REDUCTION_TOLERANCE  = stdlib.atoi (vale);
            
            boolean visible = helper.ebs ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);

            render ();
            break;
            
         default:
            return false;
      }

      return true;
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

         // background image or background color
         uniColor backCol = null;
         if (backgroundImage != null)
         {
            int left  = (d.width - backgroundImage.getIconWidth()) / 2;
            int right = (d.height - backgroundImage.getIconHeight()) / 2;

            backgroundImage.paintIcon(this, g, left, right);
         }
         else backCol = new uniColor(uniColor.LGRAY);

         miCebolla.renderUniCanvas (ges2, backCol);
         paintingNow = false;
     }         
   }
}
