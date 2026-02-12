/*
package javaj.widgets.graphics;
Copyright (C) 2011 Alejandro Xalabarder Aulet

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

import de.elxala.langutil.androidSysUtil;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.math.space.*;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;

import android.graphics.*;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.MotionEvent;
import android.view.WindowManager;

import android.os.Build;



public class z2DCampos extends View
                             implements izWidget
                                        , MensakaTarget
                                        , multiFingerTouchDetector.interested
{
   private basicAparato helper = null;

   private multiFingerTouchDetector pulpoDetector = null;

   private int COLOR_GRID_LINES = Color.rgb (255, 126, 0);
   private boolean ONCE_ANTIALIASING = true;

   private int PAPER_BACKGROUND_COLOR = Color.BLACK;

   private vect3f campoFondo = new vect3f (-0.f, 0.f, 0.f);

   public z2DCampos (Context co, String name)
   {
      super(co);
      // movement guys (handlers)
      //pulpoDetector = new multiFingerTouchDetector (co, this);
      pulpoDetector = new multiFingerTouchDetector (this);
      setName (name);
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (name, null, null));
   }

   //-i- interface iWidget ---------------------------------------
   //
   public int getDefaultHeight () { return getHeight (); }
   public int getDefaultWidth () { return getWidth (); }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String pname)
   {
      mName = pname;
   }

   private float scaleX = 1.f;
   private float scaleY = 1.f;

   public void setScale(float scalex, float scaley)
   {
      scaleX = scalex;
      scaleY = scaley;

      postInvalidate ();
   }

   protected void onDraw(Canvas canvas)
   {
      // NOTE: DO NOT TAKE width and height from canvas, I don't know the meaning but it is does not
      //       match with the real shown canvas width and height !!!!
      //
      int tela_dx = getWidth ();
      int tela_dy = getHeight ();

      //System.out.println ("mi propiamente width heit " +  getWidth () + ", " + getHeight ());
      //System.out.println ("el canvases    width heit " +  canvas.getWidth () + ", " + canvas.getHeight ());

      if (tela_dx <= 0 || tela_dy <= 0) return; // >>>> return

      Paint paint = new Paint();

      float stkWidth = paint.getStrokeWidth ();

      //paint background
      paint.setColor  (PAPER_BACKGROUND_COLOR);
      canvas.drawRect (new Rect (0, 0, tela_dx, tela_dy), paint);

      if (ONCE_ANTIALIASING)
      {
         paint.setAntiAlias(true);
         ONCE_ANTIALIASING = false;
      }
      paint.setColor  (COLOR_GRID_LINES);

      //canvas.drawLine (0,  0, 100, 100, paint);

      //if (pulpoDetector.getActiveFingersCount () > 0)
      if (pulpoDetector.gestureInProgress ())
      {

         // calcula tamanyo letras en pixels
         //
         //    --> Paint object
         //    <-- incx
         //    <-- incy
         //
         Rect charBound = new Rect ();
         paint.getTextBounds ("X", 0, 1, charBound);

         int incx = charBound.width ();
         int incy = charBound.height ();
         int posx = (int) (incx * .5);
         int posy = (int) (incy * 1.5);
         //

         boolean isSimulator = Build.PRODUCT.equals ("sdk");

         int N_CARGAS = (isSimulator ? 1:0) + pulpoDetector.getActiveFingersCount ();
         int N_SECTORES = 16;
         int N_PIXELS_DESPLAZA_CARGA = 6;
         int N_MOVER_CARGA_PRUEBA = 500 / N_PIXELS_DESPLAZA_CARGA;

         vect3f [] posCargas = new vect3f [N_CARGAS];
         float [] valCargas = new float[N_CARGAS];

         if (posCargas.length > 0)
         {
            posCargas[0] = new vect3f (100.f, 100.f, 0.f);
            valCargas[0] = -1.f;
         }
         for (int ii = isSimulator ? 1: 0; ii < N_CARGAS; ii ++)
         {
            posCargas[ii] = pulpoDetector.getFinger (ii - (isSimulator ? 1: 0)).pNow;
            valCargas[ii] = 1.f;
         }
         if (ATRACCION && N_CARGAS > 1)
            valCargas[1] = -1.f;

         vect3f posLinea = new vect3f ();
         vect3f vF = new vect3f ();
         vect3f vFtotal = new vect3f ();

         //paint.setColor  (Color.GREEN);
         paint.setColor  (Color.YELLOW);
         for (int ii = 0; ii < N_CARGAS; ii ++)
         {
            float qtest = valCargas[ii] > 0 ? 1.f : -1.f; // carga de prueba que se aleje de la original
            float radio = qtest * valCargas[ii] * 10.f;

            vect3f pCenter = posCargas[ii];
            if (pCenter == null) continue;

            canvas.drawCircle (pCenter.x, pCenter.y, radio, paint);

            android.util.Log.d ("ss", "carga x y (" + posCargas[ii].x + ", " + posCargas[ii].y + ")");

            // tirar li'neas de fuerza
            for (int tet = 0; tet < N_SECTORES; tet ++)
            {
               posLinea.set ((float) (pCenter.x + radio * Math.cos (tet * 2 * Math.PI / N_SECTORES)),
                             (float) (pCenter.y + radio * Math.sin (tet * 2 * Math.PI / N_SECTORES)),
                             0.f);

               //android.util.Log.d ("ss", "posLinea x y (" + posLinea.x + ", " + posLinea.y + ")");
               int veces = 0;
               do
               {
                  vFtotal.set (campoFondo);
                  for (int cc = 0; cc < N_CARGAS; cc ++)
                  {
                     if (posCargas[cc] == null) continue;
                     vF.set (posLinea.x, posLinea.y, 0.f);
                     vF.minus (posCargas[cc]);

                     float distance = vF.norm ();
                     //vF.mult (1.f / distance); // normalize to module 1

                     //android.util.Log.d ("ss", "vF x y (" + vF.x + ", " + vF.y + ") distance " + distance);
                     if (distance == 0.f) distance = 0.0001f;

                     float Fmod = valCargas[cc] * qtest / (distance*distance);
                     vF.mult (Fmod / distance); // divided by distance to normalize the original vector!

                     vFtotal.plus (vF);
                  }

                  //android.util.Log.d ("ss", "vFtotal x y (" + vFtotal.x + ", " + vFtotal.y + ")");

                  vFtotal.normalize (); // take module for the color ?
                  vFtotal.mult (N_PIXELS_DESPLAZA_CARGA);

                  canvas.drawLine (posLinea.x, posLinea.y, posLinea.x + vFtotal.x, posLinea.y + vFtotal.y, paint);
                  posLinea.plus (vFtotal);
               }
               //while (veces++ < N_MOVER_CARGA_PRUEBA && veces < 14);
               while (veces++ < N_MOVER_CARGA_PRUEBA);
            }
         }
      }
   }

   private boolean ATRACCION = false;

   public boolean onTouchEvent(MotionEvent event)
   {
      return pulpoDetector.onTouchEvent(new uniMotion (event));
   }

   // ==========================================================
   // implementing multiTouchDetector.interested
   //
   public void onFingerDown    (multiFingerTouchDetector detector, int fingerIndx)
   {
      postInvalidate ();
   }

   public void onFingerUp      (multiFingerTouchDetector detector, int fingerIndx)
   {
      postInvalidate ();
   }

   public void onMovement      (multiFingerTouchDetector detector)
   {
      postInvalidate ();
   }

   public void onGestureEnd    (multiFingerTouchDetector detector, boolean cancel)
   {
      postInvalidate ();
   }

   public String printPar (vect3f vect)
   {
      if (vect != null)
         return "(" + vect.x + ", " + vect.y + ")";
      return "(null ..)";
   }

   //-i- interface MensakaTarget
   //
   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            if (widgetLogger.updateContainerWarning (
                  "data", "z2DCampos",
                  helper.ebs().getName (),
                  helper.ebs().getData (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (euData, null, pars);
            Eva cteField = helper.ebs ().getDataAttribute ("cteField");

            if (cteField != null)
            {
               campoFondo.set (
                  (float) stdlib.atof (cteField.getValue (0,0)),
                  (float) stdlib.atof (cteField.getValue (0,1)),
                  (float) stdlib.atof (cteField.getValue (0,2))
                  );
            }

            // to implement images two images are needed (as in RadioButton)
            //    do it individually or for all check boxes ?
            //    does it worth the effort?
            //
            //   if (helper.ebs().getImageFile () != currentIconName)
            //   {
            //      currentIconName = helper.ebs().getImageFile ();
            //      ImageIcon ima = javaLoad.getSomeHowImageIcon (currentIconName);
            //      setIcon (ima);
            //   }
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            if (widgetLogger.updateContainerWarning (
                  "control", "z2DCampos",
                  helper.ebs().getName (),
                  helper.ebs().getControl (),
                  euData)
               )
               return true;

            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setSelected (helper.ebs ().isChecked ());
            setEnabled (helper.ebs ().getEnabled ());
            setVisibility (helper.ebs ().getVisible () ? android.view.View.VISIBLE: android.view.View.INVISIBLE);
            break;

         default:
            return false;
      }

      return true;
   }
   //-i-
}
