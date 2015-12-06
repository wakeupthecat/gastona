/*
package javaj.widgets.graphics;
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

package javaj.widgets.graphics;

import java.io.File;

import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.math.space.vect3f;
import de.elxala.math.space.curve.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;
import java.util.*;

/**

*/
public class cebollaClockInMotion // solo idea ... public cebollaInMotion
                implements  ISceneInMotion,
                            //zoomTouchDetector.interested,
                            multiFingerTouchDetector.interested,
                            MensakaTarget
{

   // ************************************
   // title   : centralObjects
   // purpose : common variables
   //

   protected static logger log = new logger (null, "javaj.widgets.graphics.cebollaClockInMotion", null);

   public static final int RX_FRAMES_MOUNTED = 0;

   //public static final int MODO_NOSE = 0;
   public static final int MODO_TRAZA = 1;   // refe
   public static final int MODO_MODELA = 5;
   public static final int MODO_ELIMINA_PTOS = 6;

   public cebollaClockInMotion ()
   {
      Mensaka.subscribe (this, RX_FRAMES_MOUNTED, javaj.javajEBSbase.msgFRAMES_MOUNTED);
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      if (mappedID == RX_FRAMES_MOUNTED)
      {
         initPathGestor ();
      }
      return true;
   }

   // ************************************
   // title   : pathgestor
   // purpose :
   //

    // !!! ERROR(4) in de.elxala.langutil.filedir.fileUtil : ensureDirsForFile : cannot make dirs of path [/mnt/sdcard/omeureloxo]

   public final String getRELOS_DATA_DIR () { return "/mnt/sdcard/omeureloxo/data"; };
   public final String getCurrentRelosFileName () { return getRELOS_DATA_DIR () + "/currentRelos.rel"; };

   public static final int PATH_BACKGROUND = 0;
   public static final int PATH_ESFERA = 1;
   public static final int PATH_AGUJA_HORAS = 2;
   public static final int PATH_AGUJA_MINUTOS = 3;
   public static final int PATH_AGUJA_SEGUNDOS = 4;
   public static final int PATH_EJE = 5;
   public static final int PATH_MAXIMO = 5;

   public int currentFasePathIndex = PATH_ESFERA;
//   public uniPath currentFasePath = null;
   public uniPath thePath = null;


   protected relosInMotion currentRelos = null;

   protected void initPathGestor ()
   {
      currentRelos = new relosInMotion ();
      loadCurrentRelos ();
      setFasePath (PATH_ESFERA);
   }

   private void newRelos ()
   {
      initPathArray ();
      setFasePath (PATH_ESFERA);
   }

   private void initPathArray ()
   {
      currentRelos = new relosInMotion ();
      currentRelos.loadData ("currentRelos", null);
   }

   private void loadCurrentRelos ()
   {
      EvaUnit euRel = EvaFile.loadEvaUnit (getCurrentRelosFileName (), "data");
      currentRelos.loadData ("currentRelos", euRel);
   }

   private void saveCurrentRelos ()
   {
      EvaFile.saveEvaUnit (getCurrentRelosFileName (), currentRelos.getDataEbs ().getData ());
   }

   private void saveRelos ()
   {
      String filename = getRELOS_DATA_DIR () + "/" + DateFormat.getStr (new Date (), "yyyyMMdd_HH_mm_ss_S") + ".rel";
      currentRelos.saveData ();
      EvaFile.saveEvaUnit (filename, currentRelos.getDataEbs ().getData ());
      System.out.print ("salvando [" + filename + "] ... ");
      File fi = fileUtil.getNewFile (filename);
      if (fi.exists ())
            System.out.println ("ok!");
      else System.out.println ("NOT OK :(");
   }

   void setFasePath (int phasePathNr)
   {
      currentFasePathIndex = Math.min (PATH_MAXIMO, Math.max(0, phasePathNr));
      switch (currentFasePathIndex)
      {
         case PATH_BACKGROUND:      break;
         case PATH_ESFERA:          thePath = currentRelos.backgroundShape; break;
         case PATH_AGUJA_HORAS:     thePath = currentRelos.hoursShape; break;
         case PATH_AGUJA_MINUTOS:   thePath = currentRelos.minutesShape; break;
         case PATH_AGUJA_SEGUNDOS:  thePath = currentRelos.secondsShape; break;
         case PATH_EJE:             thePath = currentRelos.axisShape; break;
         default:
            break;
      }
   }
   // ************************************
   // title   : catalogo
   // purpose :
   //

   public void savedump ()
   {
      //save the data into the atribute "trazos", NOTE: all in the first row! but if print onto a file will be ok
      //
      // // Eva edata = helper.ebs ().getAttribute (helper.ebs ().DATA, true, "trazos");
      // // edata.clear ();
      // // String str = miCebolla.thePath.getEdiPaths ().toString ();
      // // edata.setValue (str);

      // // //additionally print out the content in stdout
      // // System.out.println (str);
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

   private final int MENU_PHASE = 0;
   private final int MENU_TRAZA = 1;
   private final int MENU_COLOR = 2;
   private final int MENU_MODELA1 = 3;
   private final int MENU_MODELA2 = 4;
   private final int MENU_COUNT = 5;

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
         menuColorArray[ii].parseTrazo (0, 0, colorNames[ii], "jau,1,-101,-17,-34,-6,-29,26,-15,69,15,93,-12,9,30,-16,30,-8,46,6,66,14,39,-8,14,-27,4,-26,-20,-63,-1,-64,10,16,-33");
      }

      menuIconArray[MENU_PHASE].parseTrazo (0, 0, "", "pol 40 10 -10 40 -20 -30");
      menuIconArray[MENU_TRAZA].parseTrazo (31, 41, "fc:#888888;sc:black", "jauz,-58,6,-23,-123,-58,118,86,67");
      menuIconArray[MENU_TRAZA].parseTrazo ( 5,  6, "fc:#888888;sc:black", "jauz,-11,-44,26,-37,37,-18,58,7,41,72,-7,84,-24,47,-45,12,-37,-18,-3,-40,30,-44,-15,-35");

      styleGlobalContainer.addOrChangeStyle ("editCurrentTrazo", "fc:none;sc:black;sw:2");
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

      menuIconArray[MENU_COLOR].parseTrazo (326, 287, "maderaClara",   "jau,-51,1,-46,-22,-15,-41,15,-41,43,-26,74,-2,59,12,62,24,36,29,18,43,2,54,-21,56,-40,22,-38,-1,-29,-23,-3,-52,-21,-23,-45,-10");
      menuIconArray[MENU_COLOR].parseTrazo (330, 282, "maderaClara",   "jau,-51,1,-46,-22,-15,-41,15,-41,43,-26,74,-2,59,12,62,24,36,29,18,43,2,54,-21,56,-40,22,-38,-1,-29,-23,-3,-52,-21,-23,-45,-10");
      menuIconArray[MENU_COLOR].parseTrazo (320, 245, "fillBlanco",    "jau,-25,0,7,24,25,4,8,-15,-14,-12");
      menuIconArray[MENU_COLOR].parseTrazo (279, 184, "fillGris",      "jau,-21,9,-1,14,35,1,1,-21,-15,-3");
      menuIconArray[MENU_COLOR].parseTrazo (255, 226, "fillGrisNegro", "jau,-17,15,16,11,25,-20,-15,-9,-11,6");
      menuIconArray[MENU_COLOR].parseTrazo (322, 169, "fillRosa",      "jau,-14,15,13,17,24,-17,-9,-14,-15,-1");
      menuIconArray[MENU_COLOR].parseTrazo (365, 177, "fillRojo",      "jau,-6,16,12,17,26,-18,-7,-14,-9,-9,-16,9");
      menuIconArray[MENU_COLOR].parseTrazo (426, 193, "fillNaranja",   "jau,-12,2,-6,10,-4,15,23,2,18,-12,-6,-13,-12,-3");
      menuIconArray[MENU_COLOR].parseTrazo (480, 220, "fillAmarillo",  "jau,-19,1,-9,20,9,15,26,-10,-5,-22");
      menuIconArray[MENU_COLOR].parseTrazo (494, 275, "fillVerdeObs",  "jau,-14,0,-6,21,11,4,21,0,7,-19,-8,-9,-15,5");
      menuIconArray[MENU_COLOR].parseTrazo (494, 318, "fillAzulObs",   "jau,-25,9,3,25,27,-2,12,-17,-14,-14");
      menuIconArray[MENU_COLOR].parseTrazo (449, 350, "fillLilaObs",   "jau,-22,-4,-7,18,27,13,10,-12,-8,-14");

      menuIconArray[MENU_MODELA1].parseTrazo (138, 121, "fc:+217070000", "jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81");
      menuIconArray[MENU_MODELA1].parseTrazo ( 96, 223, "fc:+217070000", "jau,-43,-81,-10,-36,9,-19,39,8,87,54");
      menuIconArray[MENU_MODELA1].parseTrazo ( 58,  82, "fc:+217070000", "jau,-50,9,-34,48,-16,29,20,19,30,-23,40,-17");
      menuIconArray[MENU_MODELA1].parseTrazo (368, 148, "fc:+217070000", "jau,26,22,14,27,1,46,-12,47,21,45,-17,102,-16,19,-17,-12,16,-25,-9,-90,-43,-64,-9,-77");
      menuIconArray[MENU_MODELA1].parseTrazo ( 96, 213, "fc:+217070000", "jau,4,52,30,34,17,73,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-3,-51,4,-120");
      menuIconArray[MENU_MODELA1].parseTrazo (388, 160, "fc:+217070000", "jau,37,14,22,51,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14");
      menuIconArray[MENU_MODELA1].parseTrazo ( 28,  83, "fc:+217070000", "jau,-22,-23,0,27");

      menuIconArray[MENU_MODELA2].parseTrazo (138, 121, "fc:+217070000", "jau,84,39,109,-20,47,23,-6,54,-22,20,-35,25,-68,29,-75,1,-54,-29,-31,-81");
      //menuIconArray[MENU_MODELA2].parseTrazo ( 96, 223, "fc:+217070000", "jau,-43,-81,-10,-36,9,-19,39,8,87,54");
      //menuIconArray[MENU_MODELA2].parseTrazo ( 58,  82, "fc:+217070000", "jau,-50,9,-34,48,-16,29,20,19,30,-23,40,-17");
      menuIconArray[MENU_MODELA2].parseTrazo (368, 148, "fc:+217070000", "jau,26,22,14,27,1,46,-12,47,21,45,-17,102,-16,19,-17,-12,16,-25,-9,-90,-43,-64,-9,-77");
      //menuIconArray[MENU_MODELA2].parseTrazo ( 96, 213, "fc:+217070000", "jau,4,52,30,34,17,73,0,54,-12,28,10,4,18,-1,-2,-18,11,-18,-3,-51,4,-120");
      //menuIconArray[MENU_MODELA2].parseTrazo (388, 160, "fc:+217070000", "jau,37,14,22,51,8,86,-10,0,-3,27,-12,-28,5,38,-11,-2,-6,-37,5,-80,-11,-45,-19,-14");
      menuIconArray[MENU_MODELA2].parseTrazo ( 28,  83, "fc:+217070000", "jau,-22,-23,0,27");

      menuAreas = new uniRect [2];
   }

   // Menu1:
   //    Nuevo: graba el presente y empieza un nuevo reloj
   //    Phase: fondo, centro, aguja hora, aguja minuto, aguja segundo, FIN
   //    Trazo: linea sola, linea y relleno, relleno solo
   //    Dibujar / Modificar puntos
   //    Color: paleta(s) de relleno
   //    Editar Trazo: seleccionar area, atras, adelante

   // Menu2:

   public void renderMenu (uniCanvas canvas)
   {
      initRenderMenu ();

      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      currentMenuOrientationTop = tela_dx < tela_dy;

      uniPaint pai = new uniPaint ();
      pai.setColor  (EDIT_POINT_COLOR);

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

      // System.out.println ("comprende touch " + touchpos + " whereas rect " + menuAreas[1]);

      return -1;
   }

   public boolean menuTouched (vect3f touchpos)
   {
      int indxArea = indxMenuAreaTouched (touchpos);
      //System.out.println ("arial is " + indxArea);
      if (indxArea == -1) return false;

      if (indxArea == MENUAREA_PRAL)
      {
         int longdim = (currentMenuOrientationTop) ? (int) touchpos.x: (int) touchpos.y;

         int nmenu = (longdim - MENUDIM_MARGIN) / (MENUDIM_SQUARE_SIZE + MENUDIM_GAP);
         int pixx = (longdim - MENUDIM_MARGIN) % (MENUDIM_SQUARE_SIZE + MENUDIM_GAP);
         if (pixx <= MENUDIM_SQUARE_SIZE)
         {
            //System.out.println ("me trigas un menal " + nmenu);
            actionMenu (nmenu);
            return true;
         }
      }
      if (indxArea == MENUAREA_COLOR)
      {
         //System.out.println ("campeoncilloarial is " + indxArea);
         int longdim = (currentMenuOrientationTop) ? (int) touchpos.x: (int) touchpos.y;

         int nmenu = (longdim - MENUCOLDIM_MARGIN) / (MENUCOLDIM_SQUARE_SIZE + MENUCOLDIM_GAP);
         int pixx = (longdim - MENUCOLDIM_MARGIN) % (MENUCOLDIM_SQUARE_SIZE + MENUCOLDIM_GAP);
         if (pixx <= MENUCOLDIM_SQUARE_SIZE)
         {
            //System.out.println ("me trigas un poc " + nmenu);
            actionColor (nmenu);
            return true;
         }
      }
      //System.out.println ("NO me trigas NA!");
      return false;
   }

   protected void actionMenu (int nMenu)
   {
      //System.out.println ("arjoonns menus " + nMenu);
      switch (nMenu)
      {
         case MENU_PHASE:
               // Mensaka.sendPacket ("reloxo::save");
               // Mensaka.sendPacket ("reloxo::new");
               saveRelos ();
               newRelos ();
               break;
         case MENU_TRAZA:
               setCurrentTrazoAndMode (-1, cebollaInMotion.MODO_TRAZA);
               break;
         case MENU_MODELA1:
               incrementCurrentTrazo (-1);
               setMode (MODO_MODELA);
               break;
         case MENU_MODELA2:
               incrementCurrentTrazo (+1);
               setMode (MODO_MODELA);
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

   protected uniColor EDIT_POINT_COLOR = new uniColor (0, 0, 0);
   protected uniColor DELETE_POINT_COLOR = new uniColor (255, 0, 0);

   protected offsetAndScale etherPortView = new offsetAndScale ();

   public void renderUniCanvas (uniCanvas canvas, uniColor backgroundColor)
   {
      int tela_dx = canvas.getDx ();
      int tela_dy = canvas.getDy ();

      //clear background if any
      if (backgroundColor != null)
         canvas.fillRect (new uniRect (0, 0, tela_dx, tela_dy), backgroundColor);

      //System.out.println ("apply offset " + offsetX + ", " + offsetY + " scale " + scaleX + ", " + scaleY);
      canvas.scale (etherPortView.scaleX, etherPortView.scaleY);
      canvas.translate (- etherPortView.offsetX, - etherPortView.offsetY);

      if (thePath != null)
      {
         thePath.paintYou (canvas);

         if (showEditPoints ())
            renderCurrentEditTrazo (canvas);
      }
      renderMenu (canvas);
   }


   public void renderCurrentEditTrazo (uniCanvas canvas)
   {
      if (! showEditPoints ()) return;

      if (VER_ARREGLOS)
      {
         polyAutoCasteljauPPT.arreglo = 0.0f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+200200255"));

         polyAutoCasteljauPPT.arreglo = .25f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+150150255"));

         polyAutoCasteljauPPT.arreglo = 0.5f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+100100255"));

         polyAutoCasteljauPPT.arreglo = 0.75f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+050050255"));

         polyAutoCasteljauPPT.arreglo = 0.999f;
         canvas.drawTrazoPath (thePath, currentEditTrazo, styleGlobalContainer.getStyleObjectByName ("sw:2;sc:+000000255"));

         polyAutoCasteljauPPT.arreglo = -1.f;
      }
      styleObject editCurrent = styleGlobalContainer.getStyleObjectByName ("editCurrentTrazo");
      canvas.drawTrazoPath (thePath, currentEditTrazo, editCurrent);

      // dibujar puntos de current path
      //
      ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      if (et != null)
      {
         uniPaint pai = new uniPaint ();
         //uniUtil.printLater ("punteando trazo " + currentEditTrazo);
         // // float xx = et.posX;
         // // float yy = et.posY;
         pai.setColor  (modoActual == MODO_ELIMINA_PTOS ? DELETE_POINT_COLOR: EDIT_POINT_COLOR);
         //pai.setStrokeWidth (6.f);
         // // canvas.drawRect (new uniRect (true, xx, yy, EDIT_POINT_RECT, EDIT_POINT_RECT), pai);
         for (int pp = 0; pp < et.getPairsCount (); pp ++)
         {
            int PIN_LENGTH = 24;
            vect3f [] pino = new vect3f [2];
            et.getAbsolutePointAndPinDir (pp, pino);
            if (pino[0] == null || pino[1] == null) continue;

            //System.out.println ("  rectangulito a " + xx + ", " + yy);
            canvas.drawRect (new uniRect (true, pino[0].x, pino[0].y, EDIT_POINT_RECT, EDIT_POINT_RECT), pai);
            // pino[1].mult (PIN_LENGTH);
            // System.out.println ("  rectangulito desde " + pino[0].x + ", " + pino[0].y + " apartado " + pino[1].x  +", "+ pino[1].y+", "+ pino[1].z);
            // canvas.drawLine ((int) pino[0].x ,
                             // (int) pino[0].y ,
                             // (int) (pino[0].x + pino[1].x),
                             // (int) (pino[0].y + pino[1].y),
                             // pai);
            // canvas.drawRect (new uniRect (true, pino[0].x + pino[1].x, pino[0].y + pino[1].y, EDIT_POINT_RECT, EDIT_POINT_RECT), pai);
         }
      }
   }


   protected boolean showEditPoints ()
   {
      return currentEditTrazo != -1 && (modoActual == MODO_MODELA || modoActual == MODO_ELIMINA_PTOS);
   }

   // gestures detectors/helpers
   //
   protected multiFingerTouchDetector pulpoDetector = new multiFingerTouchDetector (this);


   // ================================================================
   // Movement management
   //

//!   public boolean onTouchEvent(MotionEvent event)
   public boolean onUniMotion (uniMotion uniEvent)
   {
      return pulpoDetector.onTouchEvent(uniEvent);
   }

   private vect3f fingerDownInMenu = null;

   // ==========================================================
   // implementing multiTouchDetector.interested
   //
   public void onFingerDown (multiFingerTouchDetector detector, int fingerIndx)
   {
      if (pulpoDetector.getActiveFingersCount () == 1)
      {
         fingerDownInMenu = pulpoDetector.getFinger (0).getLastPosition ();
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
         fingerDownInMenu = pulpoDetector.getFinger (0).getLastPosition ();
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
   protected float lastDrawnX = 0;
   protected float lastDrawnY = 0;


   protected void processFingers ()
   {
      if (fingerDownInMenu != null) return;

      int nowFingers = pulpoDetector.getActiveFingersCount ();

      switch (modoActual)
      {
         case MODO_TRAZA:
            //System.out.println ("TRAZA champion!");
            processDrawingShapes ();
            break;
         case MODO_ELIMINA_PTOS:
            //System.out.println ("ELIMINAPTOS champion!");
         case MODO_MODELA:
            //System.out.println ("MODELLA champion!");
            modelaCurrent ();
            break;
         default: break;
       }
   }

   public static final int TRAZA_EDITA = 2;
   public static final int TRAZA_REDUCE_EDITA = 3;
   public static final int TRAZA_REDUCE_TRAZA = 4;

   protected int trazaBehavior = TRAZA_REDUCE_TRAZA;

   public void setTrazaBehavior (int kind)
   {
      trazaBehavior = kind;
   }

   public int getMIN_DISTANCE_TWO_PTS ()
   {
      // poner mas grande si se quiere evitar "lineas-tiembla",
      // pero la reducción es más óptima si se mantiene en 1
      // tanto para trazos de gran superficie como para los de poca superficie
      return 1;
   }

   private float REDAZA_QUALATA = 1.f;

   private float dierma (float ponto)
   {
      //return ponto;
      //return REDAZA_QUALATA * (int) (0.5f + ponto / REDAZA_QUALATA) + REDAZA_QUALATA / 2.f;
      return (float) (Math.random () * REDAZA_QUALATA + ponto);
      //return REDAZA_QUALATA * (int) (ponto / REDAZA_QUALATA) ;
	}


   protected void processDrawingShapes ()
   {
      // only finger 0 is taken into account!
      //
      //System.out.println ("processEditingChains");
      fingerTouch fing = pulpoDetector.getFinger (0);

      if (fing == null) return;

      //desnaturaliza!
      //
//      if (REDAZA_QUALATA > 1)
//      {
//         fing.pNow = new vect3f (dierma (fing.pNow.x), dierma (fing.pNow.y));
//         fing.pIni = new vect3f (dierma (fing.pIni.x), dierma (fing.pIni.y));
//      }


      if (fing.isPressing ())
      {
         vect3f vec = fing.pNow != null ? fing.pNow: fing.pIni;
         //desnaturaliza!
         //
         if (REDAZA_QUALATA > 1)
         {
            vec = new vect3f (dierma (vec.x), dierma (vec.y));
         }
         if (currentEditTrazo != -1)
         {
            if (Math.abs(vec.x - lastDrawnX) > getMIN_DISTANCE_TWO_PTS () ||
                Math.abs(vec.y - lastDrawnY) > getMIN_DISTANCE_TWO_PTS ())
            {
               lastDrawnX = vec.x;
               lastDrawnY = vec.y;
               //System.out.println ("==TRAZO  anadimos " + vec.x + ", " + vec.y);
               thePath.getEdiPaths ().autoCasteljauPoint (vec.x, vec.y);
               //thePath.getEdiPaths ().lineTo (vec.x, vec.y);
            }
         }
         else
         {
            // esto crea un nuevo path en "thePath.getEdiPaths ()"
            currentEditTrazo = thePath.getEdiPaths ().startTrazoAt (vec.x, vec.y);
            lastDrawnX = vec.x;
            lastDrawnY = vec.y;
            //System.out.println ("==TRAZO " + currentEditTrazo + " punto move= " +  vec.x + ", " + vec.y);
         }
      }
      if (fing.isFinished ())
      {
         switch (trazaBehavior)
         {
            case TRAZA_EDITA:                 // traza y edita ... (primer modo implementado)
               modoActual = MODO_MODELA;
               break;
            case TRAZA_REDUCE_TRAZA:    // traza, reduce y inicia otra traza
               reduceCurrent ();
               setCurrentTrazoAndMode (-1, cebollaInMotion.MODO_TRAZA);
               break;
            case TRAZA_REDUCE_EDITA:    // traza, reduce y edita
               reduceCurrent ();
               modoActual = MODO_MODELA;
               break;
            default:
               break;
         }
         //System.out.println ("==TRAZO " + currentEditTrazo + " finished, MODO_MODELA chaSys.size () = " + thePath.getEdiPaths ().getTrazo (currentEditTrazo).getPairsCount ());
         //System.out.println ("==TRAZO " + currentEditTrazo + " dump");
         //System.out.println (thePath.getEdiPaths ().toString (currentEditTrazo));
      }
   }

   protected final int FINGER_ATTACH_WHOLE_TRAZO = -2;

   public int getPIXEL_TOLERANCE_FINGER ()
   {
      return 150;
   }

   protected void modelaCurrent ()
   {
      int nFing = pulpoDetector.getActiveFingersCount ();
      ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      int nPtos = 0;
      if (et != null)
         nPtos = et.getPairsCount ();

      if (nFing == 0 || nPtos == 0) return;
      if (nFing == 1 && pulpoDetector.getFinger (0).doubleTap ())
      {
         currentEditTrazo = -1;
         modoActual = MODO_TRAZA;
         //System.out.println ("==TRAZO  -1 MODO_TRAZA");
         return;
      }

      //System.out.println ("==TRAZO " + currentEditTrazo + " modela " + nPtos + " con " + nFing + " dedos");

      float [] fingerInfluenza = new float [nPtos];
      vect3f [] novaPos = new vect3f [nPtos];

      List newPoints = new Vector ();
      boolean modeDelete = modoActual == MODO_ELIMINA_PTOS;

      //System.out.println ("==ANTES DE MODELA " + currentEditTrazo + " dump");
      //System.out.println (thePath.getEdiPaths ().toString (currentEditTrazo));

      for (int ff = 0; ff < nFing; ff ++)
      {
         // process FINGER ff
         //
         fingerTouch fing = pulpoDetector.getFinger (ff);
         vect3f attachedPoint = (vect3f) fing.getAttachedObject ();
         if (attachedPoint == null || modeDelete)
         {
            // Finger not attached to a specific point yet
            // try to find closest point to the finger
            //
            uniRect fingSquare = new uniRect (true, fing.getLastPosition ().x, fing.getLastPosition ().y, getPIXEL_TOLERANCE_FINGER (), getPIXEL_TOLERANCE_FINGER ());

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
                  float dist = pto.distance2 (fing.getLastPosition ());
                  if (minDist < 0.f || dist < minDist)
                  {
                     minDist = dist;
                     ptoCandidato = pto;
                  }
               }
            }


            if (ptoCandidato != null)
            {
               if (modeDelete)
               {
                  //System.out.println ("  dedo " + ff + " deletes the point with index "  + pp);
                  et.removePair ((int) ptoCandidato.z);
                  thePath.getEdiPaths ().setContentChanged ();
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
               // still not attached ? then move the whole trazo, let's reserve the index -1 to the very first point
               fing.setAttachedObject (new vect3f (et.getPosX(), et.getPosY(), FINGER_ATTACH_WHOLE_TRAZO));
            }
         }
         if (modeDelete) continue;

         attachedPoint = (vect3f) fing.getAttachedObject ();
         if (attachedPoint == null) break; // should never happen...
         if ((int) attachedPoint.z == FINGER_ATTACH_WHOLE_TRAZO)
         {
            // FINGER ff is not attached, move the whole trazo
            //
            //System.out.println ("  dedo " + ff + " moves the whole trazo in dx, dy "  + fing.getDx () + ", " + fing.getDy ());
            //et.removePair(indx);
            et.setPosX (attachedPoint.x + (float) fing.getDx ());
            et.setPosY (attachedPoint.y + (float) fing.getDy ());
         }
         else if (attachedPoint.z >= 0)
         {
            // FINGER ff is attached to some point of the trazo, let's move the point to follow the finger
            //
            int indx = (int) attachedPoint.z;
            //System.out.println ("  dedo " + ff + " change punto " + indx + " in dx, dy "  + fing.getDx () + ", " + fing.getDy ());
            //et.removePair(indx);
            et.changePairAbs(indx, attachedPoint.x + (float) fing.getDx (), attachedPoint.y + (float) fing.getDy ());
         }
         // else ?

         thePath.getEdiPaths ().setContentChanged ();
     }

     //System.out.println ("==DESPUES DE MODELA " + currentEditTrazo + " dump");
     //System.out.println (thePath.getEdiPaths ().toString (currentEditTrazo));
   }

   protected int modoActual = MODO_TRAZA;
   protected int currentEditTrazo = -1;
   protected String defaultStyle = "";

   public void clear ()
   {
      thePath = new uniPath ();
      modoActual = MODO_TRAZA;
      currentEditTrazo = -1;
      setCurrentStyle ("sw:2");
   }

   public void setCurrentStyle (String strstyle)
   {
      if (modoActual != MODO_MODELA || currentEditTrazo == -1)
      {
         defaultStyle = strstyle;
         //System.out.println ("STYLE 1set default style " + strstyle );
         return;
      }

      ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      if (et != null)
      {
         //System.out.println ("STYLE 2set style " + strstyle + " to current " + currentEditTrazo);
         et.style = strstyle;
         thePath.getEdiPaths ().setContentChanged ();
      }
      else
      {
         defaultStyle = strstyle;
         //System.out.println ("STYLE 3set default style " + strstyle );
      }
   }

   public void setCurrentTrazoAndMode (int indx, int modo)
   {
      currentEditTrazo = indx;
      modoActual = modo;
   }

   public void setMode (int modo)
   {
      modoActual = modo;
   }

   public boolean incrementCurrentTrazo (int inc)
   {
      int maxIndx = thePath.getEdiPaths ().getTrazosSize ()-1;
      if (maxIndx < 0) return false;

      int nindx = currentEditTrazo + inc;
      if (nindx >= 0 && nindx <= maxIndx)
      {
         currentEditTrazo = nindx;
      }
      else currentEditTrazo = (inc > 0) ? 0: maxIndx;
      return true;
   }


   public static int REDUCTION_TOLERANCE = 5;

   public void reduceCurrent ()
   {
      ediTrazo current = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      if (current == null) return;

      ediTrazo redu = reduceCurrent (REDUCTION_TOLERANCE);
      if (redu == null) return;

      //System.out.println ("STYLE reduceCurrent, me pongo el estilo " + redu.style);
      redu.style = defaultStyle;
      current.set (redu);
   }

   // return an ediTrazo with the reduced current drawing form or null if no current form
   //
   public ediTrazo reduceCurrent (float tolerance)
   {
      ediTrazo et = thePath.getEdiPaths ().getTrazo (currentEditTrazo);
      int nPtos = (et != null) ? et.getPairsCount (): 0;
      if (nPtos == 0) return null;

      pointReduction predo = new pointReduction (tolerance);

//uniUtil.printLater ("createReducedCurrent x y " + et.posX + ", " + et.posY + " + ptos " + nPtos);
      for (int ii = 0; ii < nPtos; ii ++)
         predo.addPoint (et.getPointX(ii), et.getPointY(ii));

      List redu = predo.reducePoints ();
      ediTrazo editredo = new ediTrazo (et.getPosX(), et.getPosY(), et.trazoForm);
      //uniUtil.printLater ("reducidos a " + redu.size ());

      if (et.getPointX(0) == 0.f && et.getPointY(0) == 0.f)
      {
      }
      else
      {
         System.err.println ("ERROR! unexpected first value on reduction reduceCurrent");
      }

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
      ediTrazo redu = reduceCurrent (tolerance);
      if (redu == null) return;
      //System.out.println ("STYLE reduceCurrent tolerance, me pongo el estilo " + redu.style);
      redu.style = defaultStyle;
      thePath.getEdiPaths ().arrEdiTrazos.add (redu);
      thePath.getEdiPaths ().setContentChanged ();
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
