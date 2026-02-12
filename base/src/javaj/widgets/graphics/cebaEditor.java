/*
package javaj.widgets.graphics;
Copyright (C) 2013-2026 Alejandro Xalabarder Aulet

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

package javaj.widgets.graphics;

import java.io.File;

import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.math.space.vect3f;
import de.elxala.math.space.curve.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;
import java.util.*;

/**
   2022-04-09  fix editing points with some zoom (scale != 1). Add MODUS_ADD_PUNTS
   2022-04-07  fix pan
   2022-03-19  cebaEditor from cebollaClockInMotion
               remove all related with clock or reloxo

*/
public class cebaEditor
                implements  ISceneInMotion,
                            multiFingerTouchDetector.interested
                            // NOTE: twoFingerTouchDetector.interested IS NOT NEEDED since default zoom and pan is ok
{
   // ************************************
   // title   : centralObjects
   // purpose : common variables
   //

   protected static logger log = new logger (null, "javaj.widgets.graphics.cebaEditor", null);

   //public static final int MODO_NOSE = 0;
   public static final int MODUS_TRASS         = 1;   // refe
   public static final int MODUS_ZOOM_AND_PAN  = 2;

   public static final int FIRST_SHOW_PUNTS_MODUS = 5;
   public static final int MODUS_MODELA_PUNTS  = FIRST_SHOW_PUNTS_MODUS;
   public static final int MODUS_ELIMINA_PUNTS = FIRST_SHOW_PUNTS_MODUS + 1;
   public static final int MODUS_ADD_PUNTS     = FIRST_SHOW_PUNTS_MODUS + 2;
   public static final int LAST_SHOW_PUNTS_MODUS = FIRST_SHOW_PUNTS_MODUS + 2;

   public float NPUNTS_TOLERANCE_DEFAULT = 1.5f;
   public float NPUNTS_TOLERANCE = NPUNTS_TOLERANCE_DEFAULT;

   public int NPUNTS_TOLERANCE_FACTOR_DEFAULT = 25;
   public int NPUNTS_TOLERANCE_FACTOR = NPUNTS_TOLERANCE_FACTOR_DEFAULT;

   protected String myNAME = null;

   protected cebaDataEBS currentCeba = null;
   protected uniRect currentVisibleBox = new uniRect ();

   // gestures detectors/helpers
   //
   private twoFingerTouchDetector zoomDetector = null;
   private multiFingerTouchDetector pulpoDetector = null;

   MessageHandle MSGH_loadedRm3         = new MessageHandle ("javaj.widgets.graphics.cebaEditor", "cebaEditor )) loadedRm3");
   MessageHandle MSGH_beforeClearCanvas = new MessageHandle ("javaj.widgets.graphics.cebaEditor", "cebaEditor )) beforeClearCanvas");

   public cebaEditor (String varname)
   {
      myNAME = varname;
      initPathArray ();
      zoomDetector  = new twoFingerTouchDetector (null);
      pulpoDetector = new multiFingerTouchDetector (this);
   }

   // ************************************
   // title   : pathgestor
   // purpose :
   //

   public void initPathArray ()
   {
      currentCeba = new cebaDataEBS (myNAME);
      currentCeba.loadData (myNAME, null);
   }

   public void loadRm3 (String filename)
   {
      clear ();
      currentCeba.loadRm3 (filename);
      Mensaka.sendPacket (MSGH_loadedRm3, null, new String [] { filename });
   }

   public void saveRm3 (String filename)
   {
      currentCeba.saveRm3 (filename);
   }

   public void keyboardEvent (boolean isPress, int keycode, String keyname)
   {
      // react only to release since hold key is repeating the event
      //
      if (isPress) return;

      // ARROW_LEFT  = 37;
      // ARROW_UP    = 38;
      // ARROW_RIGHT = 39;
      // ARROW_DOWN  = 40;
      //
      // ESC    = 27;
      // INS    = 155;
      // DEL    = 127;
      // POS1   = 36;
      // POSEND = 35;
      //
      // PAGE_UP   = 33;
      // PAGE_DOWN = 34;

      switch (keycode)
      {
         case keyboard.F1: actionMenu (MENU_TRASS); break;
         case keyboard.F2: actionMenu (MENU_ZOOM_AND_PAN); break;

         case keyboard.F5: actionMenu (MENU_MODELA1); break;
         case keyboard.F6: actionMenu (MENU_MODELA2); break;

         case keyboard.F8:    // it seems that sometimes F8 is consumed by other component (textArea ??) and get lost
         case keyboard.F9:    // fallback for F8
            actionMenu (MENU_MODELA_DELPUNTS);
            break;

         case keyboard.PAGE_UP: zoomOut (); break;
         case keyboard.PAGE_DOWN: zoomIn (); break;
         default:
            break;
      }
   }

   // ************************************
   // title   : renderMenu
   // purpose : renders the current menu
   //

   private boolean currentMenuOrientationTop = true;

   private uniPath [] menuIconArray = null;
   private uniPath [] menuColorArray = null;
   private uniRect [] menuAreas = null;
   private int menuColorActiv = 0; // 1: top 2: left 3: bottom 4: right

   private final int MENU_NEW          = 0;
   private final int MENU_ZOOM_AND_PAN = 1;
   private final int MENU_TRASS        = 2;
   private final int MENU_COLOR        = 3;
   private final int MENU_MODELA1      = 4;
   private final int MENU_MODELA2      = 5;
   private final int MENU_MODELA_ADDPUNTS      = 6;
   private final int MENU_MODELA_DELPUNTS      = 7;
   private final int MENU_COUNT        = 1 + MENU_MODELA_DELPUNTS;

   private final int MENUCOLOR_COUNT = 11;

   private final int MENUAREA_PRAL = 0;
   private final int MENUAREA_COLOR = 1;

   //private final int MENUDIM_SQUARE_SIZE = 80;
   private final int MENUDIM_SQUARE_SIZE = 60;
   private final int MENUDIM_MARGIN = 10;
   private final int MENUDIM_GAP = 7;

   private int MENUCOLDIM_SQUARE_SIZE = 40;
   private final int MENUCOLDIM_MARGIN = 6;
   private final int MENUCOLDIM_GAP = 4;

   private uniColor COLOR_BACKMENU = null;
   private String [] colorNames = null;

   private void initRenderMenu ()
   {
      // prevent to be called twice
      // note that renderMenu can call the method automatically before any other initialization from us
      if (menuIconArray != null) return;

      COLOR_BACKMENU = new uniColor (253, 242, 183);
      colorNames = new String []
                           {
                              "fillNone", "fillBlanco", "fillGris", "fillGrisNegro", "fillAmarillo", "fillNaranja", "fillRosa", "fillRojo", "fillLilaObs", "fillAzulObs", "fillVerdeObs"
                           };

      menuIconArray = new uniPath [MENU_COUNT];
      for (int ii = 0; ii < menuIconArray.length; ii ++)
      {
         menuIconArray[ii] = new uniPath ();
      }

      menuColorArray = new uniPath [colorNames.length];
      for (int ii = 0; ii < menuColorArray.length; ii ++)
      {
         menuColorArray[ii] = new uniPath ();
         menuColorArray[ii].parseTrass (0, 0, colorNames[ii], "jau,1,-101,-17,-34,-6,-29,26,-15,69,15,93,-12,9,30,-16,30,-8,46,6,66,14,39,-8,14,-27,4,-26,-20,-63,-1,-64,10,16,-33");
      }

      menuIconArray[MENU_NEW].parseTrass (0, 0, "", "pol 40 10 -10 40 -20 -30");

      menuIconArray[MENU_ZOOM_AND_PAN].parseTrass (31, 41, "fc:#888888;sc:green", "jauz,-58,6,-23,-123,-58,118,86,67");
      menuIconArray[MENU_ZOOM_AND_PAN].parseTrass ( 5,  6, "fc:#888888;sc:green", "jauz,-11,-44,26,-37,37,-18,58,7,41,72,-7,84,-24,47,-45,12,-37,-18,-3,-40,30,-44,-15,-35");

      menuIconArray[MENU_TRASS].parseTrass (31, 41, "fc:#888888;sc:black", "jauz,-58,6,-23,-123,-58,118,86,67");
      menuIconArray[MENU_TRASS].parseTrass ( 5,  6, "fc:#888888;sc:black", "jauz,-11,-44,26,-37,37,-18,58,7,41,72,-7,84,-24,47,-45,12,-37,-18,-3,-40,30,-44,-15,-35");

      styleGlobalContainer.addOrChangeStyle ("editCurrentTrass", "fc:none;sc:black;sw:2");
      styleGlobalContainer.addOrChangeStyle ("maderaClara", "fc:+238209178;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillNone", "sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillBlanco", "fc:+255255255;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillNaranja", "fc:+246147034;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillAmarillo", "fc:+253241028;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillVerdeObs", "fc:+000165081;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillAzulObs" , "fc:+006114185;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillLilaObs" , "fc:+146040141;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillRojo"   , "fc:+237028037;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillRosa"    , "fc:+246173205;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillGrisNegro", "fc:+086086086;sw:2");
      styleGlobalContainer.addOrChangeStyle ("fillGris"     , "fc:+165165165;sw:2");

      menuIconArray[MENU_COLOR].parseTrass (326, 287, "maderaClara",   "jau,-51,1,-46,-22,-15,-41,15,-41,43,-26,74,-2,59,12,62,24,36,29,18,43,2,54,-21,56,-40,22,-38,-1,-29,-23,-3,-52,-21,-23,-45,-10");
      menuIconArray[MENU_COLOR].parseTrass (330, 282, "maderaClara",   "jau,-51,1,-46,-22,-15,-41,15,-41,43,-26,74,-2,59,12,62,24,36,29,18,43,2,54,-21,56,-40,22,-38,-1,-29,-23,-3,-52,-21,-23,-45,-10");
      menuIconArray[MENU_COLOR].parseTrass (320, 245, "fillBlanco",    "jau,-25,0,7,24,25,4,8,-15,-14,-12");
      menuIconArray[MENU_COLOR].parseTrass (279, 184, "fillGris",      "jau,-21,9,-1,14,35,1,1,-21,-15,-3");
      menuIconArray[MENU_COLOR].parseTrass (255, 226, "fillGrisNegro", "jau,-17,15,16,11,25,-20,-15,-9,-11,6");
      menuIconArray[MENU_COLOR].parseTrass (322, 169, "fillRosa",      "jau,-14,15,13,17,24,-17,-9,-14,-15,-1");
      menuIconArray[MENU_COLOR].parseTrass (365, 177, "fillRojo",      "jau,-6,16,12,17,26,-18,-7,-14,-9,-9,-16,9");
      menuIconArray[MENU_COLOR].parseTrass (426, 193, "fillNaranja",   "jau,-12,2,-6,10,-4,15,23,2,18,-12,-6,-13,-12,-3");
      menuIconArray[MENU_COLOR].parseTrass (480, 220, "fillAmarillo",  "jau,-19,1,-9,20,9,15,26,-10,-5,-22");
      menuIconArray[MENU_COLOR].parseTrass (494, 275, "fillVerdeObs",  "jau,-14,0,-6,21,11,4,21,0,7,-19,-8,-9,-15,5");
      menuIconArray[MENU_COLOR].parseTrass (494, 318, "fillAzulObs",   "jau,-25,9,3,25,27,-2,12,-17,-14,-14");
      menuIconArray[MENU_COLOR].parseTrass (449, 350, "fillLilaObs",   "jau,-22,-4,-7,18,27,13,10,-12,-8,-14");

      menuIconArray[MENU_MODELA1].parseTrass (138, 121, "fc:+217070000", "jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81");
      menuIconArray[MENU_MODELA1].parseTrass ( 96, 223, "fc:+217070000", "jau,-43,-81,-10,-36,9,-19,39,8,87,54");
      menuIconArray[MENU_MODELA1].parseTrass (368, 148, "fc:+217070000", "jau,26,22,14,27,1,46,-12,47,21,45,-17,102,-16,19,-17,-12,16,-25,-9,-90,-43,-64,-9,-77");
      menuIconArray[MENU_MODELA1].parseTrass ( 96, 213, "fc:+217070000", "jau,4,52,30,34,17,73,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-3,-51,4,-120");
      menuIconArray[MENU_MODELA1].parseTrass (388, 160, "fc:+217070000", "jau,37,14,22,51,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14");
      menuIconArray[MENU_MODELA1].parseTrass ( 28,  83, "fc:+217070000", "jau,-22,-23,0,27");

      // cap
      menuIconArray[MENU_MODELA2].parseTrass ( 58,  82, "fc:+217070000", "jau,-50,9,-34,48,-16,29,20,19,30,-23,40,-17");
      menuIconArray[MENU_MODELA2].parseTrass ( 28,  83, "fc:+217070000", "jau,-22,-23,0,27");

      // noseque
      menuIconArray[MENU_MODELA_ADDPUNTS].parseTrass (388, 160, "fc:+217070000", "jau,4,52,30,34,17,73,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-3,-51,4,-120");

      // tail
      menuIconArray[MENU_MODELA_DELPUNTS].parseTrass (388, 160, "fc:+217070000", "jau,37,14,22,51,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14");

      menuAreas = new uniRect [2];
   }

   // Menu1:
   //    Nuevo: graba el presente y empieza un nuevo reloj
   //    Trass: linea sola, linea y relleno, relleno solo
   //    Color: paleta(s) de relleno
   //    Editar Trass: seleccionar area, atras, adelante

   // Menu2:

   public void renderMenu (uniCanvas canvas)
   {
      initRenderMenu ();

      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      currentMenuOrientationTop = tela_dx < tela_dy;

      uniPaint pai = new uniPaint ();
      pai.setColor  (POINT_COLOR_EDIT);

      int incx = currentMenuOrientationTop ? MENUDIM_SQUARE_SIZE + MENUDIM_GAP: 0;
      int incy = currentMenuOrientationTop ? 0: MENUDIM_SQUARE_SIZE + MENUDIM_GAP;

      int shortdim = MENUDIM_MARGIN + MENUDIM_MARGIN + MENUDIM_SQUARE_SIZE;
      int longdim = MENUDIM_MARGIN + MENUDIM_MARGIN + MENU_COUNT * (MENUDIM_SQUARE_SIZE + MENUDIM_GAP) - MENUDIM_GAP;

      menuAreas[MENUAREA_PRAL] = new uniRect (0, 0,
                                              currentMenuOrientationTop ? longdim: shortdim,
                                              currentMenuOrientationTop ? shortdim: longdim);

      canvas.fillRect (menuAreas[MENUAREA_PRAL], COLOR_BACKMENU);

      //canvas.translate (10, 10);
      for (int ii = 0; ii < menuIconArray.length; ii ++)
      {
         menuIconArray[ii].fitIntoCanvasArea (canvas,
                       new uniRect (false,
                                    MENUDIM_MARGIN + incx * ii,
                                    MENUDIM_MARGIN + incy * ii,
                                    MENUDIM_SQUARE_SIZE,
                                    MENUDIM_SQUARE_SIZE));
         // menuIconArray[ii].paintYou (canvas);
         // canvas.translate (currentMenuOrientationTop ? 60: 0, currentMenuOrientationTop ? 0: 60);
      }

      if (menuColorActiv > 0 && menuColorArray.length > 0)
      {
         MENUCOLDIM_SQUARE_SIZE = ((currentMenuOrientationTop ? tela_dx: tela_dy) - 2 * MENUCOLDIM_MARGIN - MENUCOLDIM_GAP * (menuColorArray.length - 1)) / menuColorArray.length;

         int posx = currentMenuOrientationTop ? 0: shortdim;
         int posy = currentMenuOrientationTop ? shortdim: 0;
         int dx   = currentMenuOrientationTop ? tela_dx: 2 * MENUCOLDIM_MARGIN + MENUCOLDIM_SQUARE_SIZE;
         int dy   = currentMenuOrientationTop ? 2 * MENUCOLDIM_MARGIN + MENUCOLDIM_SQUARE_SIZE: tela_dx;
         menuAreas[MENUAREA_COLOR] = new uniRect (false, posx, posy, dx, dy);

         canvas.fillRect (menuAreas[MENUAREA_COLOR], new uniColor(255, 200, 200));

         incx = currentMenuOrientationTop ? MENUCOLDIM_SQUARE_SIZE + MENUCOLDIM_GAP: 0;
         incy = currentMenuOrientationTop ? 0: MENUCOLDIM_SQUARE_SIZE + MENUCOLDIM_GAP;

         for (int ii = 0; ii < menuColorArray.length; ii ++)
         {
            menuColorArray[ii].fitIntoCanvasArea (canvas,
                          new uniRect (false,
                                       posx + MENUCOLDIM_MARGIN + incx * ii,
                                       posy + MENUCOLDIM_MARGIN + incy * ii,
                                       MENUCOLDIM_SQUARE_SIZE,
                                       MENUCOLDIM_SQUARE_SIZE));
         }
      }
   }

   public boolean menuAreaTouched (vect3f touchpos)
   {
      return indxMenuAreaTouched (touchpos) != -1;
   }

   protected int indxMenuAreaTouched (vect3f touchpos)
   {
      for (int rr = 0; rr < menuAreas.length; rr ++)
      {
         if (menuAreas[rr] == null) continue;
         if (menuAreas[rr].pointInside (touchpos.x, touchpos.y)) return rr;
      }
      return -1;
   }

   public boolean menuTouched (vect3f touchpos)
   {
      int indxArea = indxMenuAreaTouched (touchpos);
      if (indxArea == -1) return false;

      if (indxArea == MENUAREA_PRAL)
      {
         int longdim = (currentMenuOrientationTop) ? (int) touchpos.x: (int) touchpos.y;

         int nmenu = (longdim - MENUDIM_MARGIN) / (MENUDIM_SQUARE_SIZE + MENUDIM_GAP);
         int pixx = (longdim - MENUDIM_MARGIN) % (MENUDIM_SQUARE_SIZE + MENUDIM_GAP);
         if (pixx <= MENUDIM_SQUARE_SIZE)
         {
            actionMenu (nmenu);
            return true;
         }
      }
      if (indxArea == MENUAREA_COLOR)
      {
         int longdim = (currentMenuOrientationTop) ? (int) touchpos.x: (int) touchpos.y;

         int nmenu = (longdim - MENUCOLDIM_MARGIN) / (MENUCOLDIM_SQUARE_SIZE + MENUCOLDIM_GAP);
         int pixx = (longdim - MENUCOLDIM_MARGIN) % (MENUCOLDIM_SQUARE_SIZE + MENUCOLDIM_GAP);
         if (pixx <= MENUCOLDIM_SQUARE_SIZE)
         {
            actionColor (nmenu);
            return true;
         }
      }
      return false;
   }

   protected void actionMenu (int nMenu)
   {
      switch (nMenu)
      {
         case MENU_NEW:
               Mensaka.sendPacket (MSGH_beforeClearCanvas, null);
               clear ();
               break;
         case MENU_ZOOM_AND_PAN:
               setCurrentTrassAndMode (-1, MODUS_ZOOM_AND_PAN);
               break;
         case MENU_TRASS:
               setCurrentTrassAndMode (-1, MODUS_TRASS);
               break;
         case MENU_MODELA1:
               // or incrementCurrentTrass (-1);
               incrementCurrentVisibleTrass (-1);
               setMode (MODUS_MODELA_PUNTS);
               break;
         case MENU_MODELA2:
               // or incrementCurrentTrass (+1);
               incrementCurrentVisibleTrass (+1);
               setMode (MODUS_MODELA_PUNTS);
               break;
         case MENU_MODELA_ADDPUNTS:
               toggleAddPoints ();
               break;
         case MENU_MODELA_DELPUNTS:
               toggleDeletePoints ();
               break;
         case MENU_COLOR:
               menuColorActiv = (menuColorActiv + 1) % 2;
               break;

          default:
            break;
      }
   }

   protected void actionColor (int nColor)
   {
      if (nColor >= 0 && nColor < colorNames.length)
      {
         setCurrentStyle (styleGlobalContainer.getStyleStringByName (colorNames[nColor]));
         //setCurrentStyle (colorNames[nColor]);
      }
   }

   public static int EDIT_POINT_RECT = 5;
   public boolean VER_ARREGLOS = false;

   protected uniColor POINT_COLOR_EDIT   = new uniColor (  0, 255,   0);
   protected uniColor POINT_COLOR_DELETE = new uniColor (255,   0,   0);
   protected uniColor POINT_COLOR_ZEREA  = new uniColor (255, 127,  39);
   protected uniColor POINT_COLOR_APPEND = new uniColor (  0,   0, 255);

   public void renderUniCanvas (uniCanvas canvas, uniColor backgroundColor)
   {
      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      zoomDetector.setUnScaleBox (0, 0, tela_dx, tela_dy, currentVisibleBox);

      //clear background if any
      if (backgroundColor != null)
         canvas.fillRect (new uniRect (0, 0, tela_dx, tela_dy), backgroundColor);

      canvas.scale (getScaleX (), getScaleY ());
      canvas.translate (- getOffsetX (), - getOffsetY ());

      if (currentCeba.getUniPath () != null)
      {
         currentCeba.getUniPath ().paintYou (canvas);

         if (showEditPoints ())
            renderCurrentEditTrass (canvas);
      }

      renderMenu (canvas);
   }


   public void renderCurrentEditTrass (uniCanvas canvas)
   {
      if (! showEditPoints ()) return;

      // 2022-04-07 20:17:12 these "arreglos" to be deprecated ...
      //
      if (VER_ARREGLOS)
      {
         polyAutoCasteljauPPT.arreglo = 0.0f;
         canvas.drawTrassPath (currentCeba.getUniPath (), currentEditTrass, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+200200255"));

         polyAutoCasteljauPPT.arreglo = .25f;
         canvas.drawTrassPath (currentCeba.getUniPath (), currentEditTrass, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+150150255"));

         polyAutoCasteljauPPT.arreglo = 0.5f;
         canvas.drawTrassPath (currentCeba.getUniPath (), currentEditTrass, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+100100255"));

         polyAutoCasteljauPPT.arreglo = 0.75f;
         canvas.drawTrassPath (currentCeba.getUniPath (), currentEditTrass, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+050050255"));

         polyAutoCasteljauPPT.arreglo = 0.999f;
         canvas.drawTrassPath (currentCeba.getUniPath (), currentEditTrass, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+000000255"));

         polyAutoCasteljauPPT.arreglo = -1.f;
      }

      styleObject editCurrent = styleGlobalContainer.getStyleObjectByName ("editCurrentTrass");
      canvas.drawTrassPath (currentCeba.getUniPath (), currentEditTrass, editCurrent);

      // dibujar puntos de current path
      //
      ediTrass et = currentCeba.getEdiPaths ().getTrass (currentEditTrass);
      if (et != null)
      {
         float size = EDIT_POINT_RECT / getScaleX ();
         for (int pp = 0; pp < et.getPairsCount (); pp ++)
         {
            int PIN_LENGTH = 24;
            vect3f [] pino = new vect3f [2];
            et.getAbsolutePointAndPinDir (pp, pino);
            if (pino[0] == null || pino[1] == null) continue;

            // try to show always the same size
            canvas.fillRect (new uniRect (true, pino[0].x, pino[0].y, size, size), editionPointColor ());
         }
      }
   }

   protected uniColor editionPointColor ()
   {
      switch (modoActual)
      {
         case MODUS_ELIMINA_PUNTS:  return POINT_COLOR_DELETE;
         case MODUS_ADD_PUNTS:      return POINT_COLOR_APPEND;
      }
      return POINT_COLOR_EDIT;
   }

   protected boolean showEditPoints ()
   {
      return currentEditTrass != -1 &&
             (modoActual >= FIRST_SHOW_PUNTS_MODUS && modoActual <= LAST_SHOW_PUNTS_MODUS);
   }

   // ================================================================
   // Movement management
   //
   private vect3f fingerDownInMenu = null;

   public boolean onUniMotion (uniMotion uniEvent)
   {
      if (modoActual == MODUS_ZOOM_AND_PAN)
         return  zoomDetector.onTouchEvent(uniEvent);
      return pulpoDetector.onTouchEvent(uniEvent);
   }

   public float getScaleX () { return zoomDetector.nowScaleX; };
   public float getScaleY () { return zoomDetector.nowScaleY; };
   public float getOffsetX () { return zoomDetector.nowOffsetX + zoomDetector.nowDisplacedX; };
   public float getOffsetY () { return zoomDetector.nowOffsetY + zoomDetector.nowDisplacedY; };

   public void setOffset(float offX, float offY)
   {
      zoomDetector.nowOffsetX = offX;
      zoomDetector.nowOffsetY = offY;
      log.dbg (2, "setOffset", " set offset to " + offX + ", " + offY);
   }

   public void setScale(float scalex, float scaley)
   {
      zoomDetector.nowScaleX = scalex;
      zoomDetector.nowScaleY = scaley;
      log.dbg (2, "setScale", " set scale to " + scalex + ", " + scaley);
   }

   public void zoom(float multscale)
   {
      zoomDetector.nowScaleX *= multscale;
      zoomDetector.nowScaleY *= multscale;
   }

   public void zoomIn ()
   {
      zoom (1.5f);
   }

   public void zoomOut ()
   {
      zoom (1f/1.5f);
   }

   // ==========================================================
   // implementing twoFingerTouchDetector.interested
   //
   // NOTE : this is not needed because twoFingerTouchDetector itself handles fine zoom and pan per the default
   /*
   public boolean onGestureStart    (twoFingerTouchDetector detector)
   public boolean onGestureContinue (twoFingerTouchDetector detector)
   public void    onGestureEnd      (twoFingerTouchDetector detector, boolean cancel)
   */


   // ==========================================================
   // implementing multiTouchDetector.interested
   //
   public void onFingerDown (multiFingerTouchDetector detector, int fingerIndx)
   {
      if (pulpoDetector.getActiveFingersCount () == 1)
      {
         fingerDownInMenu = zoomDetector.unScalePoint (pulpoDetector.getFinger (0).getLastPosition ());
         if (!menuAreaTouched (fingerDownInMenu))
            fingerDownInMenu = null;
      }
      processFingers ();
   }

   public void onFingerUp (multiFingerTouchDetector detector, int fingerIndx)
   {
      if (fingerDownInMenu != null && pulpoDetector.getActiveFingersCount () == 0)
      {
         boolean wasmenu = menuTouched (fingerDownInMenu);
         fingerDownInMenu = null;
         if (wasmenu) return;
      }
      processFingers ();
   }

   public void onMovement (multiFingerTouchDetector detector)
   {
      if (fingerDownInMenu != null && pulpoDetector.getActiveFingersCount () > 0)
      {
         fingerDownInMenu = zoomDetector.unScalePoint (pulpoDetector.getFinger (0).getLastPosition ());
      }
      processFingers ();
   }

   public void onGestureEnd (multiFingerTouchDetector detector, boolean cancel)
   {
      processFingers ();
   }

   // ==========================================================
   // funciones que actuan sobre los paths editables
   //
   //       private void processFingers ()
   //       private void processDrawingShapes ()
   //       private void modelaCurrent ()
   //

   protected void processFingers ()
   {
      if (fingerDownInMenu != null) return;

      int nowFingers = pulpoDetector.getActiveFingersCount ();

      switch (modoActual)
      {
         case MODUS_TRASS:
            processDrawingShapes ();
            break;
         case MODUS_ELIMINA_PUNTS:
         case MODUS_MODELA_PUNTS:
            modelaCurrent ();
            break;
         case MODUS_ADD_PUNTS:
            addPunt2Current ();
            break;
         default: break;
       }
   }

   protected void processDrawingShapes ()
   {
      // only finger 0 is taken into account!
      //
      fingerTouch fing = pulpoDetector.getFinger (0);

      if (fing == null) return;
      if (fing.isPressing ())
      {
         vect3f vec = zoomDetector.unScalePoint (fing.getLastPosition ());

         if (currentEditTrass != -1)
         {
            currentCeba.getEdiPaths ().autoCasteljauPoint (vec.x, vec.y);
            log.dbg (6, "new point at trass #" + currentEditTrass + " (" +  vec.x + ", " + vec.y + ")");
         }
         else
         {
            // esto crea un nuevo path en " currentCeba.getEdiPaths ()"
            currentEditTrass = currentCeba.getEdiPaths ().startTrassAt (vec.x, vec.y);
            log.dbg (3, "new trass #" + currentEditTrass + " starting at (" +  vec.x + ", " + vec.y + ")");
         }
      }
      if (fing.isFinished ())
      {
         reduceCurrent ();
         setCurrentTrassAndMode (-1, cebollaInMotion.MODO_TRASS);
         log.dbg (3, "new trass #" + currentEditTrass + " reduced");
      }
   }

   protected final int FINGER_ATTACH_WHOLE_TRASS = -2;

   public float getPIXEL_TOLERANCE_FINGER (float scale)
   {
      return 150.f / scale;
   }

   protected void modelaCurrent ()
   {
      int nFing = pulpoDetector.getActiveFingersCount ();
      ediTrass et = currentCeba.getEdiPaths ().getTrass (currentEditTrass);
      int nPtos = 0;
      if (et != null)
         nPtos = et.getPairsCount ();

      if (nFing == 0 || nPtos == 0) return;

      //System.out.println ("==TRASS " + currentEditTrass + " modela " + nPtos + " con " + nFing + " dedos");

      float [] fingerInfluenza = new float [nPtos];
      vect3f [] novaPos = new vect3f [nPtos];

      List newPoints = new Vector ();
      boolean modeDelete = modoActual == MODUS_ELIMINA_PUNTS;

      //System.out.println ("==ANTES DE MODELA " + currentEditTrass + " dump");
      //System.out.println (currentCeba.getEdiPaths ().toString (currentEditTrass));

      for (int ff = 0; ff < nFing; ff ++)
      {
         // process FINGER ff
         //
         fingerTouch fing = pulpoDetector.getFinger (ff);
         vect3f conpos = zoomDetector.unScalePoint (fing.getLastPosition ());
         float conposDx = zoomDetector.unScaleCoordinateX (fing.getDx ());
         float conposDy = zoomDetector.unScaleCoordinateY (fing.getDy ());

         // !!! attached Point must be a reference so it can be modified
         //
         vect3f attachedPoint = (vect3f) fing.getAttachedObject ();

         log.dbg (0, "modelaCurrent", "finger at " + conpos.x + ", " + conpos.y +
                  " attached at " + (attachedPoint == null ? "...":
                  attachedPoint.x + ", " + attachedPoint.y + " [" + attachedPoint.z + "]")
                 );

         if (attachedPoint == null || modeDelete)
         {
            // Finger not attached to a specific point yet
            // try to find closest point to the finger
            //
            uniRect fingSquare = new uniRect (true,
                                              conpos.x,
                                              conpos.y,
                                              getPIXEL_TOLERANCE_FINGER (zoomDetector.nowScaleX),
                                              getPIXEL_TOLERANCE_FINGER (zoomDetector.nowScaleY));

            // find the closest point being in the square "fingSquare"
            //
            float minDist = -1.f;
            vect3f ptoCandidato = null;
            for (int pp = 0; pp < et.getPairsCount (); pp ++)
            {
               vect3f pto = et.getPairAbsoluteAt (pp);
               pto.z = pp; // we store in z the point index!
               if (fingSquare.contains (pto.x, pto.y))
               {
                  float dist = pto.distance2 (conpos);
                  if (minDist < 0.f || dist < minDist)
                  {
                     minDist = dist;
                     ptoCandidato = pto;
                     log.dbg (3, "modelaCurrent", "finger " + conpos.x + ", " + conpos.y +
                              " attached candidate dist " + dist + " indx [" + pto.z + "]" + pto.x + ", " + pto.y
                             );
                  }
               }
            }

            boolean modeInsertAt = false; // *** by now disabled

            if (ptoCandidato != null)
            {
               if (modeInsertAt)
               {
                  //System.out.println ("  dedo " + ff + " deletes the point with index "  + pp);
                  et.addDuplicated ((int) ptoCandidato.z);
                  fing.setAttachedObject (ptoCandidato);
                  currentCeba.getEdiPaths ().setContentChanged ();
               }
               else if (modeDelete)
               {
                  //System.out.println ("  dedo " + ff + " deletes the point with index "  + pp);
                  et.removePair ((int) ptoCandidato.z);
                  currentCeba.getEdiPaths ().setContentChanged ();
               }
               else
               {
                  // attach the point to the finger for this and further movements
                  fing.setAttachedObject (ptoCandidato);
               }
            }

            if (modeDelete) break;
            attachedPoint = (vect3f) fing.getAttachedObject ();
            if (attachedPoint == null)
            {
               // still not attached ? then move the whole trass, let's reserve the index -1 to the very first point
               fing.setAttachedObject (new vect3f (et.getPosX(), et.getPosY(), FINGER_ATTACH_WHOLE_TRASS));
            }
         }
         if (modeDelete) continue;

         attachedPoint = (vect3f) fing.getAttachedObject ();
         if (attachedPoint == null) break; // should never happen...
         if ((int) attachedPoint.z == FINGER_ATTACH_WHOLE_TRASS)
         {
            // FINGER ff is not attached, move the whole trass
            //
            et.setPosX (attachedPoint.x + conposDx);
            et.setPosY (attachedPoint.y + conposDy);
            log.dbg (6, "modelaCurrent", "displace current trass #" + currentEditTrass + " (" + conposDx + ", " + conposDy + ")");
         }
         else if (attachedPoint.z >= 0)
         {
            // FINGER ff is attached to some point of the trass, let's move the point to follow the finger
            //
            int indx = (int) attachedPoint.z;
            et.changePairAbs(indx, attachedPoint.x + conposDx, attachedPoint.y + conposDy);
            log.dbg (6, "modelaCurrent", "displace point #" + indx + " (" + conposDx + ", " + conposDy + ")");
         }
         // else ?

         currentCeba.getEdiPaths ().setContentChanged ();
     }

     //System.out.println ("==DESPUES DE MODELA " + currentEditTrass + " dump");
     //System.out.println (currentCeba.getEdiPaths ().toString (currentEditTrass));
   }

   protected int lastPuntMovingIndx = -1;

   protected void addPunt2Current ()
   {
      // only add with first finger, handle more fingers would make difficult to handle correct point order
      //
      fingerTouch fing = pulpoDetector.getFinger (0);
      if (fing == null) return;

      editablePaths edPaths = currentCeba.getEdiPaths (); // shorter name

      // unscaled position
      vect3f conpos = zoomDetector.unScalePoint (fing.getLastPosition ());

      edPaths.setCurrentTrassByIndex (currentEditTrass);   // do it always just to be sure... (more robust)
      ediTrass cutra = edPaths.getCurrentTrass ();
      if (fing.isFinished () || cutra == null)
      {
         edPaths.requiredTrass ((float) conpos.x, (float) conpos.y, ediTrass.FORM_PATH_AUTOCASTELJAU);
         currentEditTrass = edPaths.getLastTrassIndx();
         log.dbg (4, "addPunt2Current", "start new trass #" + currentEditTrass + " at (" + conpos.x + ", " + conpos.y + ")");
         lastPuntMovingIndx = -1;
         return;
      }

      if (fing.isPressing ())
      {
         if (lastPuntMovingIndx == -1)
         {
            // create a new point at the end of the current trass
            // Note that we ensure working on the correct trass after calling setCurrentTrassByIndex
            edPaths.autoCasteljauPoint (conpos.x, conpos.y);
            lastPuntMovingIndx = cutra.getPairsCount ()-1;
            log.dbg (4, "addPunt2Current", "add new point in trass #" + currentEditTrass + " at (" + conpos.x + ", " + conpos.y + ")");
         }
         else
         {
            cutra.changePairAbs (lastPuntMovingIndx, conpos.x, conpos.y);
            log.dbg (6, "addPunt2Current", "displacing new point in trass #" + currentEditTrass + " at (" + conpos.x + ", " + conpos.y + ")");
         }
         edPaths.setContentChanged ();
      }
   }

   protected int modoActual = MODUS_TRASS;
   protected int currentEditTrass = -1;
   protected String defaultStyle = "";

   public void clear ()
   {
      currentCeba.loadData (myNAME, null);
      modoActual = MODUS_TRASS;
      currentEditTrass = -1;
      setCurrentStyle ("fc:none");
   }

   public void setCurrentStyle (String strstyle)
   {
      if (modoActual != MODUS_MODELA_PUNTS || currentEditTrass == -1)
      {
         defaultStyle = strstyle;
         //System.out.println ("STYLE 1set default style " + strstyle );
         return;
      }

      ediTrass et = currentCeba.getEdiPaths ().getTrass (currentEditTrass);
      if (et != null)
      {
         //System.out.println ("STYLE 2set style " + strstyle + " to current " + currentEditTrass);
         et.style = strstyle;
         currentCeba.getEdiPaths ().setContentChanged ();
      }
      else
      {
         defaultStyle = strstyle;
         //System.out.println ("STYLE 3set default style " + strstyle );
      }
   }

   public void setCurrentTrassAndMode (int indx, int modo)
   {
      currentEditTrass = indx;
      modoActual = modo;
   }

   public void setMode (int modo)
   {
      log.dbg (0, "setMode", "ALGUN BORDE SETA modoActual " + modoActual + " JOOOL...");
      modoActual = modo;
   }

   public void toggleDeletePoints ()
   {
      // implement explicitly a cyclic toggle
      switch (modoActual)
      {
         case MODUS_ELIMINA_PUNTS:  modoActual = MODUS_ADD_PUNTS; break;
         case MODUS_ADD_PUNTS:      modoActual = MODUS_MODELA_PUNTS; break;
         case MODUS_MODELA_PUNTS:   modoActual = MODUS_ELIMINA_PUNTS; break;
      }

      log.dbg (0, "toggleDeletePoints", "SETAMOS modoActual " + modoActual + " AHAHA HAHA me peto!!");
   }

   //@review if really needed
   public void toggleAddPoints ()
   {
      // change to MODUS_ADD_PUNTS or next MODUS_ADD_PUNTS
      //
      modoActual = modoActual != MODUS_ADD_PUNTS ? MODUS_ADD_PUNTS: MODUS_MODELA_PUNTS;
   }

   public boolean incrementCurrentTrass (int inc)
   {
      int maxIndx = currentCeba.getEdiPaths ().getTrassosSize ()-1;
      if (maxIndx < 0) return false;

      int nindx = currentEditTrass + inc;
      if (nindx >= 0 && nindx <= maxIndx)
      {
         currentEditTrass = nindx;
      }
      else currentEditTrass = (inc > 0) ? 0: maxIndx;
      return true;
   }

   public boolean incrementCurrentVisibleTrass (int inc)
   {
      int maxIndx = currentCeba.getEdiPaths ().getTrassosSize ()-1;
      if (maxIndx < 0) return false;

      if (currentEditTrass < 0 || currentEditTrass > maxIndx)
         currentEditTrass = 0;
      int saqueIndx = currentEditTrass;
      do
      {
         // obtain a valid current index
         //
         currentEditTrass += inc;
         if (currentEditTrass < 0 || currentEditTrass > maxIndx)
            currentEditTrass = (inc > 0) ? 0: maxIndx;

         // if this is visible then return true
         if (currentCeba.getEdiPaths ().trassVisibleInRect (currentEditTrass, currentVisibleBox)) return true;
      }
      while (saqueIndx != currentEditTrass);
      return false;
   }

   public void reduceCurrent ()
   {
      ediTrass current = currentCeba.getEdiPaths ().getTrass (currentEditTrass);
      if (current == null) return;

      ediTrass redu = reduceCurrentTrass ();
      if (redu == null) return;

      //System.out.println ("STYLE reduceCurrent, me pongo el estilo " + redu.style);
      redu.style = defaultStyle;
      current.set (redu);
   }

   // return an ediTrass with the reduced current drawing form or null if no current form
   //
   protected ediTrass reduceCurrentTrass ()
   {
      ediTrass et = currentCeba.getEdiPaths ().getTrass (currentEditTrass);
      int nPtos = (et != null) ? et.getPairsCount (): 0;
      if (nPtos <= 10) return null;

      // two strategies:
      //   a) START: tole = 0.5 ITER: npts > 20 ? increment tole *= 1.5 GOTO ITER  END:
      //   b) START: tole = trassBox min side / 20
      //   c) higher density lower tolerance

      //   d) fix tolerance!!

      uniRect box = et.getBounds ();

      float minSide = Math.min (box.dx (), box.dy ());
      float ptsDensity = nPtos / (box.dx () * box.dy ());

      //float tole = Math.max (NPUNTS_TOLERANCE_FACTOR / ptsDensity, 0.4f);
      //log.dbg (0, "reduceCurrentTrass", "initial pts: " + nPtos + " ptsDensity: " + ptsDensity + " (NTF: " + NPUNTS_TOLERANCE_FACTOR + ") tolerance: " + tole);

      List redu = null;

      float tole = Math.max (minSide / NPUNTS_TOLERANCE_FACTOR, 0.4f);

      tole = NPUNTS_TOLERANCE;

      log.dbg (0, "reduceCurrentTrass", "initial pts: " + nPtos + " minSide: " + minSide + " (NTF: " + NPUNTS_TOLERANCE_FACTOR + ") tolerance: " + tole);
      do {
         pointReduction predo = new pointReduction (tole);
         for (int ii = 0; ii < nPtos; ii ++)
            predo.addPoint (et.getPointX(ii), et.getPointY(ii));

         redu = predo.reducePoints ();
         if (redu.size () < 10 && nPtos / redu.size () > 10)
         {
            tole /= 2f;
            log.dbg (0, "reduceCurrentTrass", "too less points nPtos: " + nPtos + " redu " + redu.size () +  " adjust tolerance to " + tole);
         }
         else break;
      } while (tole > .4f);

      ediTrass editredo = new ediTrass (et.getPosX(), et.getPosY(), et.trassForm);
      if (et.getPointX(0) == 0.f && et.getPointY(0) == 0.f)
      {
      }
      else
      {
         System.err.println ("ERROR! unexpected first value on reduction reduceCurrent");
      }

      log.dbg (0, "reduceCurrentTrass", "trass of " + nPtos + " get reduced to " + redu.size () + " points");
      for (int ii = 1; ii < redu.size (); ii ++)
      {
         vect3f repo = (vect3f) redu.get(ii);
         editredo.addPairRel (repo.x, repo.y);
      }
      return editredo;
   }

   // reducedCurrent (float tolerance)
   public void createReducedCurrent (float tolerance)
   {
      ediTrass redu = reduceCurrentTrass ();
      if (redu == null) return;
      //System.out.println ("STYLE reduceCurrent tolerance, me pongo el estilo " + redu.style);
      redu.style = defaultStyle;
      currentCeba.getEdiPaths ().addTrass (redu);
      currentCeba.getEdiPaths ().setContentChanged ();
   }

   // ================================================================
   // Attachability management
   //
   protected void onWindowVisibilityChanged (int visibility)
   {
      log.dbg (4, "onWindowVisibilityChanged", "" + visibility);
   }

   protected void onDetachedFromWindow ()
   {
      log.dbg (4, "onDetachedFromWindow", "detached");
   }
}
