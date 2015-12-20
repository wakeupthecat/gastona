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
package javaj.widgets.graphics.objects;

import java.util.*;
import de.elxala.langutil.*;
import de.elxala.math.space.*;
import javaj.widgets.graphics.*;

/**
   to build editable Paths

*/
public class ediTrazo
{
   public static final int FORM_POLYGONE = 0;
   public static final int FORM_RECTANGLE = 1;
   public static final int FORM_OVAL = 2;
   public static final int FORM_ARC = 3;
   public static final int FORM_PATH_CUBIC = 4;
   public static final int FORM_PATH_QUAD = 5;
   public static final int FORM_PATH_AUTOCASTELJAU = 6;  // cuadratic with automatic calculated control points

   // preparation ...
   // public static final int TRANS_MOVE_PUSH = 100;
   // public static final int TRANS_MOVE_POP = 101;
   // public static final int TRANS_SCALE_PUSH = 102;
   // public static final int TRANS_SCALE_POP = 103;
   // public static final int TRANS_ROTATE_PUSH = 104;
   // public static final int TRANS_ROTATE_POP = 105;
   // public static final int TRANS_MATRIX_PUSH = 106;
   // public static final int TRANS_MATRIX_POP = 107;

   //
   //    ---- formato orientativo aunque no definitivo!
   //    oper, x, y, style, data
   //
   //    ---- TRAZO
   //    z, x0, y0, style, data points
   //
   //    ---- DEFINICION DE ESTILO GLOBAL
   //    defstyle, name, style
   //
   //    ---- DEFINICION DE FIGURA, UNA FORMA DE AGRUPAR Y TAMPIEN PARA REPETIR FIGURAS
   //    deffigure, name
   //       trazos y defstyle
   //       NOTA! defstyle sigue siendo global (actuará despues de finalizada la figura) pero un convenio
   //             puede sustituir la falta de scope: los estilos de las figuras, si son solo locales, se pueden
   //             nombrar s1, s2, etc ... de esta manera se asegura que cada figura usa solo los suyos
   //    enddeffigure
   //
   //    ---- REFERENCIA UNA FIGURA
   //    f, x0, y0, figurename, sx, sy, rotationangle, cxrot, cyrot
   //
   //    ---- operaciones translate scale y rotacion para grupos
   //
   //    push translate, x, y
   //    pop translate
   //
   //    push scale, x, y
   //    pop scale
   //
   //    push rotation, x, y, grad
   //    pop rotation
   //

   public int trazoForm = FORM_POLYGONE; // "path", "oval", ...

   // **** not used, though for being able of own bounds calculation
   //private int pointSetSize = 1;

   private float posX = 0.f;
   private float posY = 0.f;
   // public float scaleX = 1.f;
   // public float scaleY = 1.f;
   // public float azimut = 0.f;

   public boolean isTrazoClosed = false;
   public String style = "";
   private List points = new Vector ();

   public ediTrazo ()
   {
   }

   public ediTrazo (float x, float y, int form)
   {
      posX = x;
      posY = y;
      setTrazoForm (form);
      computeBounds4Point (x, y);
   }

   public void set (ediTrazo otro)
   {
      //this = otro;
      trazoForm = otro.trazoForm;
      posX = otro.posX;
      posY = otro.posY;
      isTrazoClosed = otro.isTrazoClosed;
      style = otro.style;
      // not deep but shallow copy!
      points = new Vector (otro.points.size ());
      for (int ii = 0; ii < otro.points.size (); ii ++)
      {
         //de.elxala.langutil.uniUtil.printLater ("editTrazo::set add in ii " + ii + "[" + points + "]");
         points.add (otro.points.get (ii));
      }
   }

   public void setTrazoForm (int tform)
   {
      trazoForm = tform;
   }

   public int getValuesCount ()
   {
      return points.size ();
   }

   public int getPairsCount ()
   {
      return points.size () / 2;
   }

   private uniRect recBounds = null;

   public uniRect getBounds ()
   {
      return (recBounds == null) ? new uniRect (): recBounds;
   }

   // NOTE: call computeBounds4Point always BEFORE actual insertion of the point in arrEdiTrazos !
   private void computeBounds4Point (float x, float y)
   {
      //System.out.println ("compato x " + x + "y " + y + " absolut " + (x+lastX) + ", " + (y+lastX) );
      if (recBounds == null)
         recBounds = new uniRect (x, y, x, y);
      else
      {
         if (x < recBounds.left ())   recBounds.setLeft  (x);
         if (x > recBounds.right ())  recBounds.setRight (x);
         if (y < recBounds.top ())    recBounds.setTop   (y);
         if (y > recBounds.bottom ()) recBounds.setBottom(y);
      }
      //System.out.println ("recBounds x0 " + recBounds.left () + " y0 " + recBounds.top() + " " + recBounds );
   }

   public void computeBounds (uniRect rec, boolean bol)
   {
      rec.set (getBounds ());
   }

   public float getPosX ()
   {
      return posX;
   }

   public float getPosY ()
   {
      return posY;
   }

   public void setPosX (float px)
   {
      posX = px;
   }

   public void setPosY (float py)
   {
      posY = py;
   }

   public float getValueAt (int indx)
   {
      if (indx >= 0 && indx < points.size ())
         return ((float []) points.get(indx))[0];
      return 0.f;
   }

   public float getPointX (int indx)
   {
      //if (scaleX != 1.f)
      //   return scaleX * getValueAt (indx + indx);
      return getValueAt (indx + indx);
   }

   public float getPointY (int indx)
   {
      //if (scaleY != 1.f)
      //   return scaleY * getValueAt (indx + indx + 1);
      return getValueAt (indx + indx + 1);
   }

   public float getPointAbsX (int indx)
   {
      return posX + getPointX (indx);
   }

   public float getPointAbsY (int indx)
   {
      return posY + getPointY (indx);
   }

   public vect3f getPairAt (int indx)
   {
      return new vect3f (getPointX (indx), getPointY (indx));
   }

   public vect3f getPairAbsoluteAt (int indx)
   {
      return new vect3f (getPointAbsX (indx), getPointAbsY (indx));
   }

   // get the coordinates for the point and its pin direction which has to be conveniently separated
   public void getAbsolutePointAndPinDir (int indx, vect3f [] pointAndPin)
   {
      if (pointAndPin.length < 2)
      {
         System.err.println ("ERROR: Wrong call to getAbsolutePointAndPin, pointAndPin size has to be >= 2!");
         return;
      }
      if (points.size () < 2)
         return;

      vect3f pto = new vect3f (getPointAbsX (indx), getPointAbsY (indx));
      vect3f delta1 = null;

      if (indx > 0 && indx < points.size ()-1)
      {
         // not the first one nor the last one
         vect3f prev = new vect3f (getPointAbsX (indx-1), getPointAbsY (indx-1));
         vect3f next = new vect3f (getPointAbsX (indx+1), getPointAbsY (indx+1));

         delta1 = vect3f.plus (vect3f.minus (prev, pto), vect3f.minus (next, pto));
         //float nor = delta1.norm ();
         //delta1.normalize (nor < 1.f ? -1: (-1 + 1.f / nor));
         delta1.normalize (-1);
      }
      else
      {
         vect3f orig = indx == 0 ? pto: new vect3f (getPointAbsX (indx-1), getPointAbsY (indx-1));
         vect3f dest = indx == 0 ? new vect3f (getPointAbsX (1), getPointAbsY (1)): pto;

         delta1 = vect3f.prod_vectorial (vect3f.minus (dest, orig), vect3f.k);
         delta1.normalize ();
      }
      pointAndPin[0] = pto;
      pointAndPin[1] = delta1;
   }

   public void close (boolean isClosed)
   {
      isTrazoClosed = isClosed;
   }

   public boolean isClosed ()
   {
      return isTrazoClosed;
   }

   public boolean isEmpty ()
   {
      return points.size () == 0;
   }

   public boolean isForm (int form)
   {
      if (isEmpty ())
         setTrazoForm (form);
      return trazoForm == form;
   }

   public void addValue (float pt)
   {
      //de.elxala.langutil.uniUtil.printLater ("editTrazo::addValue valu " + pt + " points size " + points.size () + " [" + points + "]");
      points.add (new float[] { pt });
   }

   public void insertValueAt (int indx, float pt)
   {
      if (indx < 0) indx = 0;
      if (indx > points.size ()) indx = points.size ();

      //de.elxala.langutil.uniUtil.printLater ("editTrazo::insertValueAt valu " + pt + " index = " + indx + " points size " + points.size () + " [" + points + "]");
      points.add (indx,  new float[] { pt });
   }

   public void addPairRel (float px, float py)
   {
      computeBounds4Point (px + posX, py + posY);
      if (isFormPointArray () && points.size () == 0)
      {
         addValue (0.f);
         addValue (0.f);
      }
      addValue (px);
      addValue (py);
   }

   public void addPairAbs (float px, float py)
   {
      addPairRel (px - posX, py - posY);
   }

   public void changePairAbs (int indx, float px, float py)
   {
      if (indx >= 0 && indx+indx+1 < points.size ())
      {
         ((float []) points.get(indx+indx))[0] = px - posX;
         ((float []) points.get(indx+indx+1))[0] = py - posY;
         computeBounds4Point (getPointAbsX(indx), getPointAbsY(indx)); // en realidad se ha de calcular de nuevo!
      }
   }

   public void changePairRel (int indx, float dx, float dy)
   {
      if (indx >= 0 && indx+indx+1 < points.size ())
      {
         ((float []) points.get(indx+indx))[0] += dx;
         ((float []) points.get(indx+indx+1))[0] += dy;
         computeBounds4Point (getPointAbsX(indx), getPointAbsY(indx)); // en realidad se ha de calcular de nuevo!
      }
   }

   public void insertPair (int indx, float px, float py)
   {
      if (isFormPointArray () && points.size () == 0)
      {
         addValue (0.f);
         addValue (0.f);
      }
      insertValueAt (indx+indx, py); // first insert y at pos of x
      insertValueAt (indx+indx, px); // then x
   }

   public void removePair (int indx)
   {
      if (indx < 0) return;
      if (indx+indx < points.size ()) points.remove (indx+indx); // remove X
      if (indx+indx < points.size ()) points.remove (indx+indx); // remove Y at same index as X, since the X is not anymore there
   }


   public boolean parseForm (String strTrazo)
   {
      int indx = 0;
      int setpoint = 0;
      isTrazoClosed = false;

      // NOTE: the points are saved as relative to previous point!
      //  we want to have them in memory relative to initial point, so we have to recalculate them
      //

      if (strTrazo.startsWith ("pol"))
      {
         setTrazoForm (FORM_POLYGONE);
         isTrazoClosed = strTrazo.startsWith ("polz");
         setpoint = 1;
      }
      else
      if (strTrazo.startsWith ("rec"))
      {
         setTrazoForm (FORM_RECTANGLE);
      }
      else
      if (strTrazo.startsWith ("ova") || strTrazo.startsWith ("cir"))
      {
         setTrazoForm (FORM_OVAL);
      }
      else
      if (strTrazo.startsWith ("arc"))
      {
         setTrazoForm (FORM_ARC);
         isTrazoClosed = strTrazo.startsWith ("arcz");
      }
      else
      if (strTrazo.startsWith ("cub"))
      {
         setTrazoForm (FORM_PATH_CUBIC);
         isTrazoClosed = strTrazo.startsWith ("cubz");
         setpoint = 3;
      }
      else
      if (strTrazo.startsWith ("qua"))
      {
         setTrazoForm (FORM_PATH_QUAD);
         isTrazoClosed = strTrazo.startsWith ("quaz");
         setpoint = 2;
      }
      else
      if (strTrazo.startsWith ("jau"))
      {
         setTrazoForm (FORM_PATH_AUTOCASTELJAU);
         isTrazoClosed = strTrazo.startsWith ("jauz");
         setpoint = 1;
      }
      else
      {
         return false;
      }
      indx = 3 + (isTrazoClosed ? 1: 0);

      points = new Vector ();
      String num = "";
      float xref = posX;
      float yref = posY;
      float lastReadX = 0.f;
      boolean hasReadX = false;
      int nValues = 0;
      float [] firstFourValues = new float [4]; // for : arc, ova, cir, rec

      while (indx <= strTrazo.length ()) // NOTE "<=" to process the last value!
      {
         char charat = indx < strTrazo.length () ? strTrazo.charAt(indx): ' ';
         indx ++;
         if (charat != ' ' && charat != ',')
         {
             //read character
             num += charat;
         }
         else
         {
            // new number!
            if (num.length () == 0) continue;
            float newNumber = (float) stdlib.atof (num);
            num = "";

            //
            if (setpoint > 0)
            {
               // wait for x, y
               if (!hasReadX)
                  lastReadX = newNumber;
               else
               {
                  addPairAbs (xref + lastReadX, yref + newNumber);
                  if (getPairsCount () % setpoint == 0)
                  {
                     xref += lastReadX;
                     yref += newNumber;
                  }
               }
               hasReadX = !hasReadX;
            }
            else
            {
               if (nValues < 4)
                  firstFourValues[nValues ++] = newNumber;
               // else error!
            }
         }
      }
      switch (trazoForm)
      {
         case FORM_ARC:
         case FORM_RECTANGLE:
            if (nValues >= 2) addPairRel (firstFourValues[0], firstFourValues[1]);
            if (nValues >= 3) addValue (firstFourValues[2]);
            if (nValues >= 4) addValue (firstFourValues[3]);
            break;
         case FORM_OVAL:
            if (nValues >= 2) addPairRel (firstFourValues[0], firstFourValues[1]);
            break;
         default:
            break;
      }

      return true;
   }


   public boolean isFormPointArray ()
   {
      switch (trazoForm)
      {
         case FORM_RECTANGLE:
         case FORM_OVAL:
         case FORM_ARC:
            return false;
         default:
            return true;
      }
   }

   //NOTE: for facilitating edition of first point we add an atificial relative 0, 0 point when loading the Trazo
   public float getFirstAbsPointX ()
   {
      return isFormPointArray () ? getPointAbsX(0): posX;
   }

   //NOTE: for facilitating edition of first point we add an atificial relative 0, 0 point when loading the Trazo
   public float getFirstAbsPointY ()
   {
      return isFormPointArray () ? getPointAbsY(0): posY;
   }


   public String toStringOnlyShape ()
   {
      StringBuffer s = new StringBuffer ();
      String addClosed = isTrazoClosed ? "z": "";
      int setpoint = 0;
      switch (trazoForm)
      {
         case FORM_RECTANGLE:     s.append ("rec");      break;
         case FORM_OVAL:          s.append ("ova");      break;
         case FORM_ARC:           s.append ("arc");      break;
         case FORM_POLYGONE:
            s.append ("pol" + addClosed);
            setpoint=1;
            break;
         case FORM_PATH_CUBIC:
            s.append ("cub" + addClosed);
            setpoint=3;
            break;
         case FORM_PATH_QUAD:
            s.append ("qua" + addClosed);
            setpoint=2;
            break;
         case FORM_PATH_AUTOCASTELJAU:
            s.append ("jau" + addClosed);
            setpoint=1;
            break;
         default:
            s.append ("?");
            break;
      }

      if (setpoint > 0)
      {
         // save points relative to previous point (originally all points relative to the first point x,y)
         //

         float lastX = getFirstAbsPointX();
         float lastY = getFirstAbsPointY();

         //System.out.println ("all points : ");
         //for (int ii = 0; ii < points.size (); ii ++)
         //   System.out.print(stdlib.removeDotZero ("" + ((float []) points.get (ii))[0]) + ", ");
         //System.out.println ("");


         //for (int ii = 0; ii < points.size (); ii ++)
         for (int ii = 2; ii < points.size (); ii ++)
         {
            float val = ((float []) points.get (ii))[0] + (ii % 2 == 0 ? (posX - lastX): (posY - lastY));
            s.append ("," + numToString (val));
            //if (((1 + ii / 2) % setpoint) == 0)
            if (((1 + (ii-2) / 2) % setpoint) == 0)
            {
               if (ii % 2 == 0)
                    lastX += val;
               else lastY += val;
            }
         }
      }
      else
         for (int ii = 0; ii < points.size (); ii ++)
            s.append ("," + numToString (getValueAt(ii)));
      return s.toString ();
   }

   public String toString ()
   {
      return toStringAll ();
   }

   public String toStringAll ()
   {
      StringBuffer s = new StringBuffer ();
      s.append ("z ,"
                + numToString (getFirstAbsPointX ()) + ", "
                + numToString (getFirstAbsPointY ()) + ", "
                // numToString (scaleX) + ", " +
                // numToString (scaleY) + ", " +
                // numToString (azimut) + ", "
                );
      s.append ("\"" + style + "\", //");
      s.append (toStringOnlyShape ());
      return s.toString ();
   }
   
   private String numToString (float number)
   {
      return stdlib.numberFix (number, 2, true);
   }

   public String [] toStringArray ()
   {
      return new String []
      {
         "z",
         numToString (getFirstAbsPointX ()),
         numToString (getFirstAbsPointY ()),
         style,
         toStringOnlyShape ()
      };
   }
}
