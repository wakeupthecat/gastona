/*
library de.elxala and associated applications
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

import javax.swing.*;
import java.awt.*;

import de.elxala.langutil.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import javaj.widgets.basics.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (g) zLeds

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zLeds
   <groupInfo>  misc
   <javaClass>  javaj.widgets.zLeds
   <importance> 3
   <desc>       //A set of up to three lights (rapid prefix 'g')

   <help>
      //
      // A set of up to three lights or leds of rectangular form that can be used as indicators of
      // any activity. Each led can be set to 4 possible colors throug messages.
      //
      // At present the number of leds per default is three and it can only be changed using the
      // <javaClass of ...> instanciation mechanism. For example:
      //
      //    <layout of myLedsSet>  javaj.widgets.zLeds, 2
      //
      //    where 'myLedsSet' is supossed to be the widget name
      //
      // Signaling Gastona intern processing
      // -----------------------------------
      //
      // This widget might easily reflect some processing states. For example there are some time
      // consuming processes like sqlite queries, scaning files or parsing text that are signalized
      // using Mensaka messages. We can catch these messages and convert them to our widget messages
      // using the javaj variable <MessageToMessage>. For example
      //
      //   <MessageToMessage>
      //
      //      _lib db.sqlite.callStart  , gLus L12
      //      _lib db.sqlite.callEnd    , gLus L11
      //      _lib db.sqlite.callError  , gLus L13
      //
      //      ledMsg scanFiles_start    , gLus L22
      //      ledMsg scanFiles_end      , gLus L21
      //
      //      ledMsg parsing_start      , gLus L32
      //      ledMsg parsing_end        , gLus L31
      //

   <prefix> g

   <attributes>
     name             , in_out, possibleValues             , desc

     visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                      , //Value 0 to disable the widget


   <messages>

      msg, in_out, desc

      data!    , in    , update data
      control! , in    , update control
      L10      , in    , set the light 1 with color 0
      L11      , in    , set the light 1 with color 1
      L12      , in    , set the light 1 with color 2
      L13      , in    , set the light 1 with color 3
      L20      , in    , set the light 2 with color 0
      L21      , in    , set the light 2 with color 1
      L22      , in    , set the light 2 with color 2
      L23      , in    , set the light 2 with color 3
      L30      , in    , set the light 3 with color 0
      L31      , in    , set the light 3 with color 1
      L32      , in    , set the light 3 with color 2
      L33      , in    , set the light 3 with color 3



   <examples>
     gastSample

     hello zLeds

   <hello zLeds>
      //   #listix#
      //       <-- iAll>
      //          MSK, gLights @<iAll selected.msg>
      //
      //   #data#
      //       <iAll>
      //          msg
      //          L10
      //          L11
      //          L12
      //          L13
      //          L20
      //          L21
      //          L22
      //          L23
      //          L30
      //          L31
      //          L32
      //          L33
      //
      //   #javaj#
      //
      //       <frames> F, Hello zLeds, 200, 200
      //
      //       <layout of F>
      //             PANEL, X
      //             iAll, gLights

#**FIN_EVA#

*/

/**
*/
public class zLeds extends JPanel implements MensakaTarget, setParameters_able
{
   private basicAparato helper = null;

   private static final int MAP_00 = 100;

   // private model
   //
   private static final int MAX_LUCES = 9;
   private static final int MAX_COLORS = 10;
   private int NLUCES = 3;
   private int NCOLORS = 4;

   private int [] status = new int[NLUCES];


   private Color [] colores = new Color [MAX_COLORS];
   private Color colorFondo = new Color (154, 188, 160);   // verde caqui

   public zLeds ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zLeds (String map_name)
   {
      build (map_name, NLUCES);
   }

   public void setName (String map_name)
   {
      build (map_name, NLUCES);
   }

   private void build (String map_name, int nLights)
   {
      // DO NOT call here setName but super.setName!!!
      super.setName (map_name);
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      colores [0] = getBackground (); //new Color (126, 82, 68); // Amarillo (marron) oscuro
      colores [1] = new Color (128, 128, 128); // gris oscuro
      colores [2] = new Color (254, 249, 210); // cf_AmarilloClaro
      colores [3] = new Color (242,  71,  24); // Rojo

      // bimg = getGraphicsConfiguration().createCompatibleImage(d.width, d.height);

//      status [0] = 1;
//      status [1] = 2;
//      status [2] = 3;

      subscribeLights (0, NLUCES-1);
   }

   // example
   //
   //   javaj.widgets.zLed, 4, --, -, 000000000, 254249210, -
   //
   public void setParameters (String [] param)
   {
      // parameters: thisclass NumberOfLights ShapeAndOrientation color1RRRGGGBBB color2RRRGGGBBB ...
      // shape can be -- -| o- o|

      int nParamsReal = param.length;
      while (nParamsReal > 0 && param[nParamsReal-1] == null) nParamsReal --;

      int ipar = 1;
      if (nParamsReal < 2) return;

      NLUCES = Math.min (MAX_LUCES, stdlib.atoi (param[ipar]));
      status = new int[NLUCES];

      if (++ipar >= nParamsReal) return;
      // ignore shape & orientation

      // colors given as RRRGGGBBB for instance 128, 34, 7 would be represented as 128034007

      NCOLORS = Math.min (MAX_COLORS, nParamsReal - 3);

      // set the colors
      //
      // either rrrgggbbb  then RGB color
      // or     background then background color
      // or     any other  then not set this color (take the default)
      //
      while (++ipar < nParamsReal && (ipar-3) < NCOLORS)
      {
         String cRRRGGGBBB = param[ipar];
         Color col = null;

         if (cRRRGGGBBB.equalsIgnoreCase("background"))
            col = getBackground ();
         else if (cRRRGGGBBB.length () == 9)
         {
            int rrr = stdlib.atoi (cRRRGGGBBB.substring (0, 3));
            int ggg = stdlib.atoi (cRRRGGGBBB.substring (3, 6));
            int bbb = stdlib.atoi (cRRRGGGBBB.substring (6, 9));

            col = new Color (rrr, ggg, bbb);
         }
         else
            col = colores [ipar-3];

         colores [ipar-3] = (col != null) ? col: getBackground ();
      }
      subscribeLights (3, NLUCES - 1);
   }

   private void subscribeLights (int desde, int hasta)
   {
      for (int luz = desde; luz <= hasta; luz ++)
         for (int colo = 0; colo < NCOLORS; colo ++)
            Mensaka.subscribe (this, MAP_00 + luz * 10 + colo, helper.ebs ().evaName ("L" + (luz+1) + colo));

      // "L10", "L11", "L12", "L13"
      // "L20", "L21", "L22", "L23"
      // "L30", "L31", "L32", "L33"
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      if (mappedID == widgetConsts.RX_UPDATE_DATA)
      {
         helper.ebs ().setDataControlAttributes (euData, null, pars);
      }
      if (mappedID == widgetConsts.RX_UPDATE_CONTROL)
      {
         helper.ebs ().setDataControlAttributes (null, euData, pars);
         setEnabled (helper.ebs ().getEnabled ());

         //(o) TODO_REVIEW visibility issue
         // avoid setVisible (false) when the component is not visible (for the first time ?)
         boolean visible = helper.ebs ().getVisible ();
         if (visible && isShowing ())
            setVisible  (visible);
         return true;
      }

      if (mappedID < MAP_00) return false;

      int nluz   = (mappedID - MAP_00) / 10;
      int ncolor = (mappedID - MAP_00) % 10;

      if (nluz < status.length)
         status[nluz] = ncolor;
      // else err ("try to use light " + nluz + " but have only " + status.length + " lights!");

      //paintImmediately (new Rectangle (0, 0, marcoX, marcoY));
      paintImmediately (getVisibleRect());

      return true;
   }

   private int X0  = 12;
   private int Y0  = 4;
   private int H   = 5;
   private int W   = 50;
   private int MAR = 2;

   private int marcoX = W + 2 * X0;
   private int marcoY = 2 * Y0 + 2 * MAR + 3 * H;

   public Dimension getMinimumSize ()
   {
      return new Dimension (marcoX, marcoY);
   }

   public Dimension getMaximumSize ()
   {
      return getMinimumSize ();
   }

   public Dimension getPreferredSize ()
   {
      return getMinimumSize ();
   }

   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      Graphics2D g2 = (Graphics2D) g;
      // g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      // System.out.println ("pinto por la cuenta que me trae!");
      int Y = Y0;

      // marco
      //

      g2.setColor (getBackground ());
      g2.fillRect (0, 0, marcoX, marcoY);
//      g2.setColor (Color.GRAY);
//      g2.drawRect (0, 0, marcoX-1, marcoY-1);
//      g2.setColor (Color.WHITE);
//      g2.drawRect (1, 1, marcoX-2, marcoY-2);

      for (int ii = 0; ii < NLUCES; ii ++)
      {
         Color col = colores [status[ii]];
         if (col != null)
            g2.setColor (colores [status[ii]]);
         g2.fillRect (X0, Y, W, H);

//         g2.setColor (Color.GRAY);
//         g2.drawRect (X0, Y, W, H);
         g2.setColor (Color.WHITE);
         g2.drawLine (X0, Y, X0+W, Y);
         g2.drawLine (X0, Y, X0, Y+H);

         g2.setColor (Color.black);
         g2.drawLine (X0+W, Y, X0+W, Y+H);
         g2.drawLine (X0, Y+H, X0+W, Y+H);

         Y += H + MAR;
      }

      g2.dispose();
   }
}

/**
      componente: misLuses
      model     :
               <shapes>
                     -, -, o
                     -, -, o

               <colors>
                     B70055, FFE712,
                     B70055, FFE712,
                     B70055, FFE712,
                     B70055, FFE712,
                     B70055, FFE712,
                     B70055, FFE712,

      control   :
                  <pral misLuses gui>  0, 1, 1     ( , enable y visible)
                                       0,
                  <pral tMiArbol gui>  0, 1, 1
                  <pral tMiArbol nodeselect>

      mensajes  : tMiArbol nodeselect ))   se ha cambiado la seleccion de nodo
                  tMiArbol control!         ((    debe actualizar el control del arbol (seleccion, visible etc)
                  tMiArbol uM         ((    debe actualizar el modelo del arbol (redibujarlo, expandirlo)







//      colores [0] = new Color (128, 128, 128); // gris oscuro
//      colores [1] = new Color (157, 139, 63);  // Amarillo (marron) oscuro
//      colores [2] = new Color (254, 249, 210); // cf_AmarilloClaro
//      colores [3] = new Color (220, 0, 0);     // cf_Rojo

      // colorFondo = new Color (154, 188, 160);   // verde caqui
//   public static final Color cf_AzulOscuro = new Color (0, 128, 128);
//   public static final Color cf_VerdeOscuro = new Color (0, 128, 64);
//   public static final Color cf_LilaOscuro = new Color (128, 0, 128);
//
//   public static final Color cf_Granate = new Color (128, 0, 64);
//
//   public static final Color  cf_GrisClaro = new Color (192, 192, 192);
//   public static final Color  cf_RosaClaro = new Color (255, 204, 204);
//   public static final Color  cf_AzulClaro = new Color (149, 213, 234);
//   public static final Color  cf_AmarilloClaro = new Color (255, 255, 174);
//   public static final Color  cf_VerdeClaro = new Color (193, 255, 193);
//
//   public static final Color  cf_Rojo = new Color (220, 0, 0);
//   public static final Color  cf_Verde = new Color (0, 220, 0);
//   public static final Color  cf_Azul = new Color (0, 0, 220);

*/





