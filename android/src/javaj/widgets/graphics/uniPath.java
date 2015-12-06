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
package javaj.widgets.graphics;

import android.graphics.Path;
import android.graphics.RectF;

import de.elxala.math.space.*;   // vect3f
import de.elxala.math.space.curve.*;
import javaj.widgets.graphics.objects.*;

/**
   PathEsperant follow the philosophy of Android's Path concept (all shapes are a paths)
   therefore for the implementation in java "PC" we encapsulate all possible
   shapes

*/
public class uniPath implements paintableDimensionableElement
{
   public editablePaths mEdiPaths = new editablePaths ();

   private String styleLogicName = "";
   private Path nativePath = new Path ();

   private int lastBuildCounter = -1;

   public editablePaths getEdiPaths ()
   {
      return mEdiPaths;
   }

   //paintableDimensionableElement
   public void paintYou (uniCanvas here)
   {
      here.drawPath (this);
   }

   public void fitIntoCanvasArea (uniCanvas here, uniRect area)
   {
      here.fitPathInArea (this, area);
   }

   //paintableDimensionableElement
   public void getBounds (uniRect bounds)
   {
      bounds.set (mEdiPaths.getBounds ());
   }

   public uniRect getBounds ()
   {
      return mEdiPaths.getBounds ();
   }

   //paintableDimensionableElement
   public void unionBounds (uniRect bounds)
   {
      bounds.union (mEdiPaths.getBounds ());
   }

   public void parseTrazo (float x, float y, String strStyle, String strTrazo)
   {
      mEdiPaths.parseTrazo (x, y, strStyle, strTrazo);
   }

   public Path getNativePath ()
   {
      buildNativePath ();
      return nativePath;
   }

   public void setStyle (String styleString)
   {
      styleLogicName = styleString;
      getEdiPaths ().setStyle2all (styleString);
   }

   public String getStyle ()
   {
      return styleLogicName;
   }

   public int getTrazosCount ()
   {
      return mEdiPaths.getTrazosSize ();
   }

   public Path getNativePathFromTrazo (int indx)
   {
      Path thePath = new Path();
      buildNativePath (thePath,  indx);
      return thePath;
   }

   public String getStyleFromTrazo (int indx)
   {
      return mEdiPaths.getTrazo (indx).style;
   }


   public void buildNativePath ()
   {
      if (mEdiPaths.getChangeCounter () == lastBuildCounter) return;
      System.out.println ("buildNativePath after " + (mEdiPaths.getChangeCounter () - lastBuildCounter) + " changes" );
      lastBuildCounter = mEdiPaths.getChangeCounter ();
      nativePath = new Path();

      for (int ii = 0; ii < mEdiPaths.getTrazosSize (); ii ++)
      {
         buildNativePath (nativePath,  ii);
      }
      lastBuildCounter = mEdiPaths.getChangeCounter ();
   }

   private void buildNativePath (Path gepa, int trazoIndx)
   {
      ediTrazo et = mEdiPaths.getTrazo (trazoIndx);
      if (et == null || et.isEmpty ()) return;

      try
      {
         switch (et.trazoForm)
         {
            case ediTrazo.FORM_OVAL:
            {
               //assertion (et.getValuesCount () == 2, "Trazo oval wrong size " + et.getValuesCount () + " it should have only 2")
               //assertion (mEdiPaths.getTrazosSize ()-1 == ii, "Trazo OVAL not last one ?")
               gepa.addOval (new RectF (et.getPosX(), et.getPosY(), et.getPosX() + et.getPointX(0), et.getPosY() + et.getPointY(0)), Path.Direction.CW /* any dir! */);
            }
            break;
            case ediTrazo.FORM_ARC:
            {
               //assertion (et.getValuesCount () >= 2, "Trazo arc less size " + et.getValuesCount () + " it should have at least size 2")
               //assertion (et.getValuesCount () <= 4, "Trazo arc too much size " + et.getValuesCount () + " it should have at maximum 4")
               //assertion (mEdiPaths.getTrazosSize ()-1 == ii, "Trazo ARC not last one ?")
               gepa.addArc (new RectF (et.getPosX(), et.getPosY(), et.getPosX() + et.getPointX(0), et.getPosY() + et.getPointY(0)), et.getValueAt (2), et.getValueAt (3));
            }
            break;
            case ediTrazo.FORM_RECTANGLE:
            {
               //assertion (et.getValuesCount () >= 2, "Trazo rec less size " + et.getValuesCount () + " it should have at least size 2")
               //assertion (et.getValuesCount () <= 4, "Trazo rec too much size " + et.getValuesCount () + " it should have at maximum 4")
               //assertion (mEdiPaths.getTrazosSize ()-1 == ii, "Trazo REC not last one ?")
               gepa.addRoundRect (new RectF (et.getPosX(), et.getPosY(), et.getPosX() + et.getPointX(0), et.getPosY() + et.getPointY(0)),
                                       et.getPointX(1), //radius x
                                       et.getPointY(1), //radius y
                                       Path.Direction.CW /* any dir! */);
            }
            break;
            case ediTrazo.FORM_POLYGONE:
            {
               gepa.moveTo (et.getPointAbsX(0), et.getPointAbsY(0));
               for (int pp = 1; pp < et.getPairsCount (); pp ++)
               {
                  gepa.lineTo (et.getPointAbsX(pp), et.getPointAbsY(pp));
               }

               if (et.isClosed ())
                  gepa.close ();
            }
            break;
            case ediTrazo.FORM_PATH_CUBIC:
            {
               gepa.moveTo (et.getPointAbsX(0), et.getPointAbsY(0));
               for (int pp = 1; pp < et.getPairsCount (); pp += 3)
               {
                  gepa.cubicTo (et.getPointAbsX(pp)  , et.getPointAbsY(pp),     //(cx1, cy1,
                                et.getPointAbsX(pp+1), et.getPointAbsY(pp+1),   // cx2, cy2,
                                et.getPointAbsX(pp+2), et.getPointAbsY(pp+2));  // x, y);
               }

               if (et.isClosed ())
                  gepa.close ();
            }
            break;

            case ediTrazo.FORM_PATH_QUAD:
            {
               gepa.moveTo (et.getPointAbsX(0), et.getPointAbsY(0));
               for (int pp = 1; pp < et.getPairsCount (); pp += 2)
               {
                  gepa.quadTo (et.getPointAbsX(pp)  , et.getPointAbsY(pp),
                               et.getPointAbsX(pp+1), et.getPointAbsY(pp+1));
               }
               if (et.isClosed ())
                  gepa.close ();
            }
            break;

            case ediTrazo.FORM_PATH_AUTOCASTELJAU:
            {
               //for (int vv = 0; (vv+1) < et.getPairsCount (); vv ++)
               //   System.out.println (vv + ") " + et.getPointX(vv) + ", " + et.getPointY(vv));
               float lx = et.getPointAbsX(0);
               float ly = et.getPointAbsY(0);
               gepa.moveTo (lx, ly);
               //assertion (et.getValuesCount () >= 2, "Trazo rec less size " + et.getValuesCount () + " it should have at least size 2")
               if (et.getPairsCount () < 3)
               {
                  gepa.lineTo (lx + et.getPointX(1), ly + et.getPointY(1));
               }
               else
               {
                  vect3f [] ctrlP = null;
                  vect3f prevCtrPt = null;
                  vect3f lastCtrlPt = null;
                  float lastXabs = et.getPointAbsX(et.getPairsCount ()-1);
                  float lastYabs = et.getPointAbsY(et.getPairsCount ()-1);

                  vect3f p0 = new vect3f (lastXabs, lastYabs);
                  vect3f p1 = new vect3f (lx, ly);
                  vect3f p2 = new vect3f (lx + et.getPointX(1), ly + et.getPointY(1));
                  if (et.isClosed ())
                  {
                     ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);
                     prevCtrPt = new vect3f (ctrlP[1]);
                     lastCtrlPt = new vect3f (ctrlP[0]);
                  }
                  for (int dd = 1; (dd+1) < et.getPairsCount (); dd ++)
                  {
                     p0.set (lx, ly);
                     p1.set (et.getPointAbsX(dd)  ,  et.getPointAbsY(dd));
                     p2.set (et.getPointAbsX(dd+1),  et.getPointAbsY(dd+1));
                     ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);
                     if (prevCtrPt == null) prevCtrPt = new vect3f (ctrlP[0]);
                     gepa.cubicTo (prevCtrPt.x, prevCtrPt.y, ctrlP[0].x, ctrlP[0].y, p1.x, p1.y);
                     prevCtrPt.set(ctrlP[1]);

                     lx =  et.getPointAbsX(dd);
                     ly =  et.getPointAbsY(dd);
                  }
                  if (prevCtrPt != null) // has to be true!
                  {
                     if (et.isClosed ())
                     {
                        p0.set (lx, ly);
                        p1.set (lastXabs, lastYabs);
                        p2.set (et.getPointAbsX(0), et.getPointAbsY(0));
                        ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);

                        gepa.cubicTo (prevCtrPt.x, prevCtrPt.y, ctrlP[0].x, ctrlP[0].y, lastXabs, lastYabs);
                        gepa.cubicTo (ctrlP[1].x, ctrlP[1].y, lastCtrlPt.x, lastCtrlPt.y, et.getPointAbsX(0), et.getPointAbsY(0));
                     }
                     else
                     {
                        gepa.cubicTo (prevCtrPt.x, prevCtrPt.y, prevCtrPt.x, prevCtrPt.y, lastXabs, lastYabs);
                     }
                  }
               }
            }
            break;
         }
      }
      catch (Exception e) {}
   }
}
