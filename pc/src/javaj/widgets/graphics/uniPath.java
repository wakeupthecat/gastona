/*
package javaj.widgets.graphics;
Copyright (C) 2011-2022 Alejandro Xalabarder Aulet

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

import java.awt.*;
import java.awt.geom.*;

import de.elxala.math.space.*;   // vect3f
import de.elxala.math.space.curve.*;
import de.elxala.langutil.uniUtil;
import javaj.widgets.graphics.objects.*;

/**
   uniPath follow the philosophy of Android's Path concept (all shapes are a paths)
   therefore for the implementation in java "PC" we encapsulate all possible
   shapes

*/
public class uniPath implements paintableDimensionableElement
{
   public GeneralPath objGeneralPath = new GeneralPath(GeneralPath.WIND_NON_ZERO);
   public RoundRectangle2D.Float objRect2D = null;
   public Ellipse2D.Float objEllipse2D = null;
   public Arc2D.Float objArc2D = null;

   public editablePaths mEdiPaths = new editablePaths ();

   private String styleLogicName = "";

   private int lastBuildCounter = -1;

   public editablePaths getEdiPaths ()
   {
      return mEdiPaths;
   }

   //paintableDimensionableElement
   public void paintYou (uniCanvas here)
   {
      //here.assignStyleMap (mEdiPaths.getStyleMap ());
      here.drawPath (this);
   }

   public void fitIntoCanvasArea (uniCanvas here, uniRect area)
   {
      //here.assignStyleMap (mEdiPaths.getStyleMap ());
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

   public void parseTrass (float x, float y, String strStyle, String strTrass)
   {
      mEdiPaths.parseTrass (x, y, strStyle, strTrass);
   }

   public Shape getNativePath ()
   {
      buildNativePath ();
      if (objRect2D != null) return objRect2D;
      if (objEllipse2D != null) return objEllipse2D;
      if (objArc2D != null) return objArc2D;
      return objGeneralPath;
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

   public int getTrassosCount ()
   {
      return mEdiPaths.getTrassosSize ();
   }

   public Shape getNativePathFromTrass (int indx)
   {
      objArc2D = null;
      objEllipse2D = null;
      objRect2D = null;
      objGeneralPath = new GeneralPath(GeneralPath.WIND_NON_ZERO);

      buildNativePath (objGeneralPath, indx);

      if (objRect2D != null) return objRect2D;
      if (objEllipse2D != null) return objEllipse2D;
      if (objArc2D != null) return objArc2D;
      return objGeneralPath;
   }

   public String getStyleFromTrass (int indx)
   {
      return mEdiPaths.getTrass (indx).style;
   }

   public void buildNativePath ()
   {
      if (mEdiPaths.getChangeCounter () == lastBuildCounter) return;
      objArc2D = null;
      objEllipse2D = null;
      objRect2D = null;
      objGeneralPath = new GeneralPath(GeneralPath.WIND_NON_ZERO);
      for (int ii = 0; ii < mEdiPaths.getTrassosSize (); ii ++)
      {
         buildNativePath (objGeneralPath, ii);
      }
      lastBuildCounter = mEdiPaths.getChangeCounter ();
   }

   // This method accept a ediTrass <et>
   //
   //
   private void drawSingleAutoCasteljauPath (
            GeneralPath gepa,    // general path to fill with the paths being built
            ediTrass et,         // whole trass containg all sub autocasteljau segment
            int iniIndx,         // first index of <et> to start with
            int endIndx,         // last index of <et> to end with
            vect3f lastCtrlPt,   // != null if total path is closed and this is the last segment
            float closeX,        // if lastCtrlPt != null then the very first/last point x of the complete path
            float closeY         // if lastCtrlPt != null then the very first/last point y of the complete path
            )
   {
      if (endIndx <= iniIndx)
      {
         // nothing to do
         return;
      }

      vect3f p0    = new vect3f (et.getPointAbsX(iniIndx),   et.getPointAbsY(iniIndx));
      vect3f p1    = new vect3f (et.getPointAbsX(iniIndx+1), et.getPointAbsY(iniIndx+1));
      vect3f p2    = new vect3f (0.f, 0.f);
      vect3f lastP = new vect3f (et.getPointAbsX(endIndx),   et.getPointAbsY(endIndx));

      gepa.moveTo (p0.x, p0.y);
      if (1 + endIndx - iniIndx == 2)
      {
         gepa.lineTo (p1.x, p1.x);
         return;
      }
      vect3f [] ctrlP = null;
      vect3f prevCtrPt = null;

      for (int dd = iniIndx + 1; (dd+1) <= endIndx; dd ++)
      {
         p1.set (et.getPointAbsX(dd),    et.getPointAbsY(dd));
         p2.set (et.getPointAbsX(dd+1),  et.getPointAbsY(dd+1));
         ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);
         if (prevCtrPt == null)
            prevCtrPt = new vect3f (ctrlP[0]);
         gepa.curveTo (prevCtrPt.x, prevCtrPt.y, ctrlP[0].x, ctrlP[0].y, p1.x, p1.y);
         prevCtrPt.set(ctrlP[1]);

         // note that "p0" is always used as the "previous" point to dd (which is about to be incremented)
         p0.set (et.getPointAbsX(dd), et.getPointAbsY(dd));
      }
      if (prevCtrPt != null) // has to be true!
      {
         if (lastCtrlPt != null)
         {
            p1.set (lastP.x, lastP.y);
            p2.set (closeX, closeY);
            ctrlP = polyAutoCasteljauPPT.getControlPoints (p0, p1, p2);

            gepa.curveTo (prevCtrPt.x, prevCtrPt.y, ctrlP[0].x, ctrlP[0].y, lastP.x, lastP.y);
            gepa.curveTo (ctrlP[1].x, ctrlP[1].y, lastP.x, lastP.y, closeX, closeY);
         }
         else
         {
            gepa.curveTo (prevCtrPt.x, prevCtrPt.y, prevCtrPt.x, prevCtrPt.y, lastP.x, lastP.y);
         }
      }
   }

   private void buildNativePath (GeneralPath gepa, int trassIndx)
   {
      ediTrass et = mEdiPaths.getTrass (trassIndx);
      if (et == null || et.isEmpty ()) return;

      try
      {
         switch (et.trassForm)
         {
            case ediTrass.FORM_OVAL:
            {
               //assertion (et.getValuesCount () == 2, "Trass oval wrong size " + et.getValuesCount () + " it should have only 2")
               //assertion (mEdiPaths.getTrassosSize ()-1 == ii, "Trass OVAL not last one ?")
               //uniUtil.printLater ("OPS ellipse 2d " + et.posX + ", " + et.posY + ", " + et.getPointX(0) + ", " + et.getPointY(0));
               objEllipse2D = new Ellipse2D.Float (et.getPosX(), et.getPosY(), et.getPointX(0), et.getPointY(0));
            }
            break;
            case ediTrass.FORM_ARC:
            {
               //assertion (et.getValuesCount () >= 2, "Trass arc less size " + et.getValuesCount () + " it should have at least size 2")
               //assertion (et.getValuesCount () <= 4, "Trass arc too much size " + et.getValuesCount () + " it should have at maximum 4")
               //assertion (mEdiPaths.getTrassosSize ()-1 == ii, "Trass ARC not last one ?")
               objArc2D = new Arc2D.Float (Arc2D.OPEN);

               objArc2D.setFrame (et.getPosX(), et.getPosY(), et.getPointX(0), et.getPointY(0));
               objArc2D.setAngleStart(et.getValueAt (2));  // startAngle
               objArc2D.setAngleExtent(et.getValueAt (3)); // sweepAngle
            }
            break;
            case ediTrass.FORM_RECTANGLE:
            {
               //assertion (et.getValuesCount () >= 2, "Trass rec less size " + et.getValuesCount () + " it should have at least size 2")
               //assertion (et.getValuesCount () <= 4, "Trass rec too much size " + et.getValuesCount () + " it should have at maximum 4")
               //assertion (mEdiPaths.getTrassosSize ()-1 == ii, "Trass REC not last one ?")
               objRect2D = new RoundRectangle2D.Float ();
               objRect2D.setRoundRect(et.getPosX(), et.getPosY(),
                                      et.getPointX(0), //width
                                      et.getPointY(0), //height
                                      et.getPointX(1), //radius x
                                      et.getPointY(1)); //radius y
            }
            break;
            case ediTrass.FORM_POLYGONE:
            {
               gepa.moveTo (et.getPointAbsX(0), et.getPointAbsY(0));
               for (int pp = 1; pp < et.getPairsCount (); pp ++)
               {
                  gepa.lineTo (et.getPointAbsX(pp), et.getPointAbsY(pp));
               }

               if (et.isClosed ())
                  gepa.closePath ();
            }
            break;
            case ediTrass.FORM_PATH_CUBIC:
            {
               gepa.moveTo (et.getPointAbsX(0), et.getPointAbsY(0));
               for (int pp = 1; pp < et.getPairsCount (); pp += 3)
               {
                  gepa.curveTo (et.getPointAbsX(pp)  , et.getPointAbsY(pp),     //(cx1, cy1,
                                et.getPointAbsX(pp+1), et.getPointAbsY(pp+1),   // cx2, cy2,
                                et.getPointAbsX(pp+2), et.getPointAbsY(pp+2));  // x, y);
               }

               if (et.isClosed ())
                  gepa.closePath ();
            }
            break;

            case ediTrass.FORM_PATH_QUAD:
            {
               gepa.moveTo (et.getPointAbsX(0), et.getPointAbsY(0));
               for (int pp = 1; pp < et.getPairsCount (); pp += 2)
               {
                  gepa.quadTo (et.getPointAbsX(pp)  , et.getPointAbsY(pp),
                               et.getPointAbsX(pp+1), et.getPointAbsY(pp+1));
               }
               if (et.isClosed ())
                  gepa.closePath ();
            }
            break;

            //(o) graffiti/autocasteljau java canvas implementation
            //
            case ediTrass.FORM_PATH_AUTOCASTELJAU:
            {
               float x0 = et.getPointAbsX(0);
               float y0 = et.getPointAbsY(0);

               vect3f lastCtrlPt = null;

               gepa.moveTo (x0, y0);
               if (et.getPairsCount () < 3)
               {
                  gepa.lineTo (x0 + et.getPointX(1), y0 + et.getPointY(1));
               }
               else
               {
                  if (et.isClosed ())
                  {
                     // we have to calculate the very last control point now to close the final path
                     //
                     vect3f [] ctrlP = polyAutoCasteljauPPT.getControlPoints
                                         (
                                            et.getPairAbsoluteAt (et.getPairsCount ()-1),
                                            et.getPairAbsoluteAt (0),
                                            et.getPairAbsoluteAt (1)
                                         );
                     lastCtrlPt = new vect3f (ctrlP[0]);
                  }

                  // Now we divide the whole autocasteljau path in subpaths if pair clones are found
                  //
                  //  Example: <et> pairs  (0,0)  (10,7)  (20,-4)  (6,2)  (6,2)  (1,5)  (7,-8)
                  //
                  //    this will build two bezier paths from the autocasteljau segments:
                  //             (0,0)  (10,7)  (20,-4)  (6,2)
                  //             (6,2)  (1,5)  (7,-8)
                  //
                  // using this semantic of pair clones we can
                  //       - sharp shapes using one clone point
                  //           p0 p1 p2 p2 p3 p4 p5 ...
                  //           the initial bezier curve stops in p2 and a new one start from there
                  //
                  //       - straight lines and polygons concatenating clones
                  //           p0 p1 p2 p2 p3 p3 p4 p5 ...
                  //           from p2 to p3 a straight line will be drawn
                  //
                  //       - (NOPE) discontinuities (NOPE)
                  //           p0 p1 p2 p2 p2 p3 p4 p5 ...
                  //           from p2 to p3 a straight line will be drawn
                  //
                  int indxFrom = 0;
                  boolean IMPLEMENT_CLONES = true;
                  if (IMPLEMENT_CLONES)
                  {
                     for (int pp = 0; pp < et.getPairsCount (); pp ++)
                     {
                        if (et.isNextRelPairClose (pp))
                        {
                           drawSingleAutoCasteljauPath (gepa, et, indxFrom, pp, null, 0.f, 0.f);
                           indxFrom = pp + 1;
                        }
                     }
                  }
                  drawSingleAutoCasteljauPath (gepa, et, indxFrom, et.getPairsCount ()-1, lastCtrlPt, x0, y0);
               }
            }
            break;
         }
      }
      catch (Exception e) {}
   }
}
