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

import java.util.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import de.elxala.math.space.*;
//import de.elxala.math.*;

import javaj.widgets.basics.*;
import de.elxala.zServices.*;


/*
   //(o) WelcomeGastona_source_javaj_widgets (s) z2DMiRelos

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       z2DMiRelos
   <groupInfo>  misc
   <javaClass>  javaj.widgets.z2DMiRelos
   <prefix>  2Dj
   <importance> 4
   <desc>       //A canvas to draw own painted clocks

   <help>

      //
      // Experimental!
      //

   <attributes>
     name             , in_out, possibleValues            , desc

     visible          , in    , 0 / 1                     , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                     , //Value 0 to disable the widget
     var              , in    , Eva name                  , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

     backGroundShape  , in    , Eva                       , //Contains the trazos for the background of the clock if any
     minutesHandShape , in    , Eva                       , //Contains the trazos for the minutes hand of the clock if any
     hoursHandShape   , in    , Eva                       , //Contains the trazos for the hour hand of the clock if any
     secondsHandShape , in    , Eva                       , //Contains the trazos for the second hand of the clock if any
     axisShape        , in    , Eva                       , //Contains the trazos for the axis of the clock if any

     axisPosition     , in    , set of coordinates        , //x,y center of axis
     referenceTime    , in    , time string               , //hh:mm:ss of the clock when stopped, the default value is 03:00:00
     referencePositions, in   , set of coordinates        , //alternative way to set references. If given it has to contain 4 coordinates: x,y center of axis, x,y end of hours hand, x,y end of minutes hand, x, y end of seconds hand

  <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control

      run     , in  , sync the clock and start updating it
      stop    , in  , stops the clock
      toHtml5 , in  , exports the clock to html5

   <examples>
     gastSample

     mirelos sample

   <mirelos sample>
      //#javaj#
      //
      //    <frames> main,
      //
      //    <layout of main>
      //       EVA, 4, 4, 3, 3
      //          , X
      //        X , 2DjMiRelos
      //
      //#data#
      //
      //    <2DjMiRelos backGroundShape>
      //      z ,80, 50, "sc:none", //jau, 480, 480
      //      z ,332, 119, "fc:#E68A00", //jauz,96,34,58,115,-30,123,-113,70,-119,-58,-32,-117,36,-98
      //
      //    <2DjMiRelos hoursShape>
      //       z ,316, 269, "fc:#E6E600", //jau,65,-10,1,-13,39,23,-17,25,-13,-12,-54,16,-13,0,-9,-27
      //
      //    <2DjMiRelos minutesShape>
      //       z ,309, 296, "fc:#E6E600", //jau,39,0,2,-13,-11,-119,26,1,-32,-36,-38,27,23,15,-5,122
      //
      //    <2DjMiRelos secondsShape>
      //       z ,330, 280, "", //jau,10,-13,-31,-52,25,-23,-11,-39,10,-26,-4,-40,-10,26,26,0,-16,-26
      //
      //    <2DjMiRelos axisShape>
      //       z ,322, 280, "fc:#CC3300", //jau,13,-9,-4,13
      //
      //    <2DjMiRelos axisPosition>
      //		    322, 280
      //	  
      //    <2DjMiRelos referenceTime> 03:00:00
      
#**FIN EVA#

*/


/**
*/
public class z2DMiRelos extends uniSceneInMotionView implements MensakaTarget
{
   private static final Color black = new Color(0, 0, 0);
   private final int RUN = 16;
   private final int STOP = 17;
   private final int EXPORT_HTML5 = 18;

   private basicAparato helper = null;
   private Icon backgroundImage = null;

   private relosInMotion miRelos = new relosInMotion ();

   private Timer tictac = null;
   private miTimer theTicTac = new miTimer ();

   public z2DMiRelos ()
   {
      assignNewScene (miRelos);
      init ("?");
   }

   public z2DMiRelos (String map_name)
   {
      assignNewScene (miRelos);
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

      Mensaka.subscribe (this, RUN, map_name + " run");
      Mensaka.subscribe (this, STOP, map_name + " stop");
      Mensaka.subscribe (this, EXPORT_HTML5, map_name + " toHtml5");
   }

   class miTimer
   {
      public int periodMilli = 500;

      public void start ()
      {
         TimerTask tasca = new TimerTask ()
         {
            public void run ()
            {
               render ();
            }
         };

         tictac = new Timer ();
         tictac.scheduleAtFixedRate (tasca, 0, periodMilli);
      }

      public void stop ()
      {
         if (tictac != null)
            tictac.cancel ();
      }
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
            loadAllData ();
            theTicTac.start ();
            render ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setEnabled (helper.ebs ().getEnabled ());

            boolean visible = helper.ebs ().getVisible ();
            if (visible || isShowing ())
               setVisible  (visible);

            render ();
            break;

         case RUN:
            theTicTac.start ();
            break;

         case STOP:
            theTicTac.stop ();
            break;

         case EXPORT_HTML5:
            exportHtml5 (getPar1Str (pars, "export" + getName () + ".html5"));
            break;

         default:
            return false;
      }

      return true;
   }

   public void loadAllData ()
   {
      // load background image if any
      String backgImgFile = helper.ebs ().getSimpleDataAttribute ("image");
      if (backgImgFile != null && backgImgFile.length () > 0)
           backgroundImage = javaLoad.getSomeHowImageIcon (backgImgFile);
      else backgroundImage = null;

      miRelos.loadData (helper.ebs ().getName (), helper.ebs ().getData ());
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

         // background image or background color
         uniColor backCol = null;
         if (backgroundImage != null)
         {
            int left  = (d.width - backgroundImage.getIconWidth()) / 2;
            int right = (d.height - backgroundImage.getIconHeight()) / 2;

            backgroundImage.paintIcon(this, g, left, right);
         }
         else backCol = new uniColor(uniColor.LGRAY);

         miRelos.renderUniCanvas (ges2, backCol);
         paintingNow = false;
     }
   }

   public void exportHtml5 (String fileName)
   {
   }
}
