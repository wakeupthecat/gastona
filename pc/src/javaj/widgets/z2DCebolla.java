/*
package de.elxala.zWidgets
Copyright (C) 2011-2015 Alejandro Xalabarder Aulet

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
   <name>       z2DCebolla
   <groupInfo>  misc
   <javaClass>  javaj.widgets.z2DCebolla
   <prefix>  2De
   <importance> 4
   <desc>       //A canvas to draw 2D geometries

   <help>

      //
      // Experimental!
      //

   <attributes>
     name             , in_out, possibleValues            , desc

     visible          , in    , 0 / 1                     , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                     , //Value 0 to disable the widget
     var              , in    , Eva name                  , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

     MIN_DISTANCE_TWO_PTS   , in , number                 , //Parmeter for stroking
     EDIT_POINT_RECT        , in , number                 , //Parameter for visualizing editing points

  <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control

   <examples>
     gastSample

     cebollasample
     trazaCaballo
     trazaToBrowserDemo

   <cebollasample>
      //#javaj#
      //
      //    <frames> f1,
      //
      //    <layout of f1>
      //       EVA, 4, 4, 3, 3
      //          , X
      //        X , 2DeCoso
      //          , mandos
      //       100, oConso
      //
      //    <layout of mandos>
      //       EVA, 4, 4, 3, 3
      //          ,          ,         ,        ,           , X,
      //          , bNewTrazo, bEdit -, bEdit +, bDel Points,  ,  bDump
      //          , lTolerancia, eTole, bReduce, rgTintas, -, bSave
      //
      //#listix#
      //
      //   <main>
      //      MSG, 2DeCoso setStyle, tinta1
      //
      //   <-- rgTintas>
      //       MSG, 2DeCoso setStyle, @<rgTintas selected.value>
      //
      //
      //    <-- bNewTrazo>     MSG, 2DeCoso newTrazo
      //    <-- bEdit ->       MSG, 2DeCoso incCurrentTrazo, -1
      //    <-- bEdit +>       MSG, 2DeCoso incCurrentTrazo, 1
      //    <-- bDel Points>   MSG, 2DeCoso modeDelPoints
      //    <-- bReduce>
      //       -->, 2DeCoso control!, REDUCTION_TOLERANCE, @<eTole>
      //       MSG, 2DeCoso reducePoints, @<eTole>
      //
      //    <-- bDump>
      //          MSG, oConso clear
      //          MSG, 2DeCoso dump
      //
      //#data#
      //
      //   <rgTintas orientation> X
      //   <rgTintas>
      //        label , value, selected
      //        notinta , notinta
      //        tinta1  , tinta1   , 1
      //        tinta2  , tinta2   ,
      //        tinta3  , tinta3   ,
      //
      //    <eTole> 3
      //    <2DeCoso REDUCTION_TOLERANCE> 3
      //    <2DeCoso MIN_DISTANCE_TWO_PTS> 5
      //    <2DeCoso EDIT_POINT_RECT> 6
      //    <2DeCoso trazos>
      //      defstyle, editCurrentTrazo, "fc:none;sc:yellow;sw:2"
      //      defstyle, tinta1, "fc:+188188188;sw:1"
      //      defstyle, tinta2, "fc:+123123123;sw:1"
      //      defstyle, tinta3, "fc:+067067067;sw:1"
      //      defstyle, notinta, "fc:none;sw:1"


   <trazaCaballo>
      //#javaj#
      //
      //    <frames> f1, Traza caballo, 800, 700
      //
      //    <layout of f1>
      //       EVA, 4, 4, 3, 3
      //          , X
      //        X , 2DeCoso
      //          , mandos
      //       100, oConso
      //
      //    <layout of mandos>
      //       EVA, 4, 4, 3, 3
      //          ,          ,         ,        ,           , X,
      //          , bNewTrazo, bEdit -, bEdit +, bDel Points, , bSave, bLoad, bDump
      //          , lTolerancia, eTole, bReduce,
      //
      //#listix#
      //
      //    <-- bNewTrazo>     MSG, 2DeCoso newTrazo
      //    <-- bEdit ->       MSG, 2DeCoso incCurrentTrazo, -1
      //    <-- bEdit +>       MSG, 2DeCoso incCurrentTrazo, 1
      //    <-- bDel Points>   MSG, 2DeCoso modeDelPoints
      //    <-- bReduce>       MSG, 2DeCoso reducePoints, @<eTole>
      //    <-- bDump>
      //          MSG, oConso clear
      //          MSG, 2DeCoso dump
      //    <-- bSave>
      //       MSG, 2DeCoso flushTrazos
      //       DUMP, data, :mem garda, 2DeCoso trazos
      //    <-- bLoad>
      //       VAR=, 2DeCoso trazos, ""
      //       LOAD, data, :mem garda
      //       MSG, 2DeCoso data!
      //
      //#data#
      //
      //    <eTole> 12
      //    <2DeCoso MIN_DISTANCE_TWO_PTS> 12
      //    <2DeCoso EDIT_POINT_RECT> 6
      //    <2DeCoso trazos>
		//       defstyle, pel, "fc:+255127039"
 		//       defstyle, pelo, "fc:+234234234"
      //
      //       z ,238, 121, "pel", //jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81
      //       z ,196, 223, "pel", //jau,-43,-81,-10,-36,9,-19,39,8,64,37
      //       z ,155, 84, "pel", //jau,-47,7,-34,48,-16,29,20,19,36,-29,40,-15
      //       z ,468, 148, "pel", //jau,26,22,14,27,1,46,-5,49,12,56,-12,73,-7,33,-25,0,13,-32,-10,-93,-45,-57,-16,-49
      //       z ,196, 213, "pel", //jau,4,52,29,42,18,65,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-4,-85,5,-86
      //       z ,473, 152, "pelo", //jau,51,14,23,59,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14
      //       z ,128, 83, "pel", //jau,-22,-23,3,31
      //       z ,123, 83, "pelo", //jau,44,-3,65,19,28,27,30,25,-20,19,6,13,-15,-3,-7,-26,-13,7,-17,-24,-28,-28,-21,4,-23,-20,-31,-9

   <trazaToBrowserDemo>
      //#data#
      //
      //   <laData>
      //      // var vdata = {
      //      //      "Caballo graffiti": [
      //      //    [ "defstyle", "piel", "#ff7f27" ],
      //      //    [ "defstyle", "pelo", "#eaeaea"],
      //      //    [ "z" ,238, 121, "piel",  "jau",84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81 ],
      //      //    [ "z" ,196, 223, "piel",  "jau",-43,-81,-10,-36,9,-19,39,8,64,37 ],
      //      //    [ "z" ,155, 84,  "piel",  "jau",-47,7,-34,48,-16,29,20,19,36,-29,40,-15 ],
      //      //    [ "z" ,468, 148, "piel",  "jau",26,22,14,27,1,46,-5,49,12,56,-12,73,-7,33,-25,0,13,-32,-10,-93,-45,-57,-16,-49 ],
      //      //    [ "z" ,196, 213, "piel",  "jau",4,52,29,42,18,65,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-4,-85,5,-86 ],
      //      //    [ "z" ,473, 152, "pelo",  "jau",51,14,23,59,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14 ],
      //      //    [ "z" ,128, 83,  "piel",  "jau",-22,-23,3,31 ],
      //      //    [ "z" ,123, 83,  "pelo",  "jau",44,-3,65,19,28,27,30,25,-20,19,6,13,-15,-3,-7,-26,-13,7,-17,-24,-28,-28,-21,4,-23,-20,-31,-9 ],
      //      //    ],
      //      // };
      //      //
      //
      //#listix#
      //
      //   <main>
      //      VAR=, fi, @<:lsx tmp html>
      //      GEN, @<fi>, caballos
      //      BROWSER, @<fi>
      //      <! Note: on exit the temporary html will be deleted!
      //      SLEEP, 1000
      //
      //   <caballos>
      //      //<html>
      //      //<body>
      //      //      <canvas width="400" height="400" id="Caballo"></canvas>
      //      //      <svg width="400" height="400" id="Caballo" shape-rendering="geometricPrecision"></svg>
      //      //<script>
      //      //
      //      //@<:infile META-GASTONA/js/trazos2D-min.js>
      //      //
      //      // @<laData>
      //      //
      //      //   window.onload = function(e)
      //      //   {
      //      //      trazos2D ().renderAllGraffitis (vdata);
      //      //   };
      //      //
      //      //</script>
      //      //</body>
      //      //</html>
     

#**FIN EVA#

*/


/**
*/
public class z2DCebolla extends uniSceneInMotionView implements MensakaTarget
{
   private static final Color black = new Color(0, 0, 0);
   private final int DUMP = 16;
   private final int EDIT_TRAZO = 17;
   private final int INC_TRAZO = 18;
   private final int ADD_POINTS_TRAZO = 19;
   private final int NEW_TRAZO = 20;
   private final int DEL_POINTS = 21;
   private final int REDUCE_POINTS = 22;
   private final int SET_STYLE = 23;
   private final int CLEAR = 24;
   private final int FLUSH = 25;
   private final int FLUSH_TRAZO = 26;
   private final int FLUSH_JS = 27;

   private basicAparato helper = null;
   private Icon backgroundImage = null;

   private cebollaInMotion miCebolla = new cebollaInMotion ();

   public z2DCebolla ()
   {
      assignNewScene (miCebolla);
      init ("?");
   }

   public z2DCebolla (String map_name)
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

      Mensaka.subscribe (this, FLUSH, map_name + " flush");
      Mensaka.subscribe (this, FLUSH_JS, map_name + " flushTrazosJs");
      Mensaka.subscribe (this, FLUSH_TRAZO, map_name + " flushTrazos");
      Mensaka.subscribe (this, DUMP, map_name + " dump");
      Mensaka.subscribe (this, INC_TRAZO, map_name + " incCurrentTrazo");
      Mensaka.subscribe (this, EDIT_TRAZO, map_name + " editTrazo");
      Mensaka.subscribe (this, ADD_POINTS_TRAZO, map_name + " addPointsTrazo");
      Mensaka.subscribe (this, NEW_TRAZO, map_name + " newTrazo");
      Mensaka.subscribe (this, DEL_POINTS, map_name + " modeDelPoints");
      Mensaka.subscribe (this, REDUCE_POINTS, map_name + " reducePoints");
      Mensaka.subscribe (this, SET_STYLE, map_name + " setStyle");
      Mensaka.subscribe (this, CLEAR, map_name + " clear");
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
            render ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);

            setEnabled (helper.ebs ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)

            String vale = helper.ebs ().getSimpleDataAttribute ("MIN_DISTANCE_TWO_PTS");
            if (vale != null) miCebolla.MIN_DISTANCE_TWO_PTS = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("EDIT_POINT_RECT");
            if (vale != null) miCebolla.EDIT_POINT_RECT  = stdlib.atoi (vale);

            vale = helper.ebs ().getSimpleDataAttribute ("REDUCTION_TOLERANCE");
            if (vale != null) miCebolla.REDUCTION_TOLERANCE  = stdlib.atoi (vale);

            boolean visible = helper.ebs ().getVisible ();
            if (visible || isShowing ())
               setVisible  (visible);

            render ();
            break;

         case EDIT_TRAZO:
            miCebolla.setCurrentTrazoAndMode (getPar1Int (pars, 0), cebollaInMotion.MODO_MODELA);
            render ();
            break;

         case INC_TRAZO:
            miCebolla.incrementCurrentTrazo (getPar1Int (pars, 1));
            miCebolla.setMode (cebollaInMotion.MODO_MODELA);
            render ();
            break;

         case ADD_POINTS_TRAZO:
            miCebolla.setCurrentTrazoAndMode (getPar1Int (pars, 0), cebollaInMotion.MODO_TRAZA);
            render ();
            break;

         case NEW_TRAZO:
            miCebolla.setCurrentTrazoAndMode (-1, cebollaInMotion.MODO_TRAZA);
            render ();
            break;

         case DEL_POINTS:
            miCebolla.setMode (cebollaInMotion.MODO_ELIMINA_PTOS);
            render ();
            break;

         case REDUCE_POINTS:
            miCebolla.createReducedCurrent (getPar1Int (pars, 0));
            render ();
            break;

         case SET_STYLE:
            miCebolla.setCurrentStyle (getPar1Str (pars, ""));
            render ();
            break;

         case CLEAR:
            miCebolla.clear ();
            render ();
            break;

         case FLUSH_TRAZO:
            flushTrazo (false);
            break;

         case FLUSH_JS:
            flushJavaScriptCode (false);
            break;

         case FLUSH:
            flushTrazo (false);
            flushJavaScriptCode (false);
            break;

         case DUMP:
            flushTrazo (true);
            flushJavaScriptCode (true);
            break;

         default:
            return false;
      }

      return true;
   }


   public void flushTrazo (boolean printout)
   {
      Eva edata = helper.ebs ().getAttribute (helper.ebs ().DATA, true, "trazos");
      edata.clear ();
      miCebolla.thePath.getEdiPaths ().dumpIntoEva  (edata);
      if (printout)
         System.out.println (edata);
   }

   public void flushJavaScriptCode (boolean printout)
   {
      //save the data into the atribute "trazos", NOTE: all in the first row! but if print onto a file will be ok
      //
      Eva edata = helper.ebs ().getAttribute (helper.ebs ().DATA, true, "trazosJS");
      String str = miCebolla.thePath.getEdiPaths ().toJavaScriptCode ();
      edata.setValueVar (str);
      if (printout)
         System.out.println (edata);
   }

   public void loadAllData ()
   {
      // load background image if any
      String backgImgFile = helper.ebs ().getSimpleDataAttribute ("image");
      if (backgImgFile != null && backgImgFile.length () > 0)
           backgroundImage = javaLoad.getSomeHowImageIcon (backgImgFile);
      else backgroundImage = null;

      // load paths
      miCebolla.thePath = new uniPath ();
      graphicObjectLoader oba = new graphicObjectLoader ();
      oba.loadUniPathFromEvaTrazos (miCebolla.thePath, helper.ebs ().getDataAttribute ("trazos"));
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
