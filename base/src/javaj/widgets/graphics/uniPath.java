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

import java.awt.*;
import java.awt.geom.*;

/**
   PathEsperant follow the philosophy of Android's Path concept (all shapes are a paths)
   therefore for the implementation in java "PC" we encapsulate all possible
   shapes

*/
//public class uniPath implements java.awt.Shape
public class uniPath implements Shape
{
   public GeneralPath objGeneralPath = new GeneralPath(GeneralPath.WIND_NON_ZERO);
   public RoundRectangle2D.Float objRect2D = null;
   public Ellipse2D.Float objEllipse2D = null;
   public Arc2D.Float objArc2D = null;


   // for relative paints
   protected float lastX = 0.f;
   protected float lastY = 0.f;

   public uniPath ()
   {
   }

   public uniPath (uniPath cop)
   {
      lastX = cop.lastX;
      lastY = cop.lastY;
      objGeneralPath = cop.objGeneralPath;
      objRect2D    = cop.objRect2D;
      objEllipse2D = cop.objEllipse2D;
      objArc2D     = cop.objArc2D;
   }

   // only PC method (not Android)
   public Shape getShape ()
   {
      if (objRect2D != null) return objRect2D;
      if (objEllipse2D != null) return objEllipse2D;
      if (objArc2D != null) return objArc2D;
      return objGeneralPath;
   }

   //cannot extend GeneralPath since it is a final class !!
   // so we have to do the "canelo" (if we absolutely need to implement Shape !?)
   //
   public PathIterator getPathIterator(AffineTransform afi, double dob) { return objGeneralPath.getPathIterator(afi, dob); }
   public PathIterator getPathIterator(AffineTransform afi)             { return objGeneralPath.getPathIterator(afi); }
   public boolean contains(double x, double y) { return objGeneralPath.contains(x, y); }
   public boolean contains(double x, double y, double w, double h) { return objGeneralPath.contains(x, y, w, h); }
   public boolean contains(Point2D p) { return objGeneralPath.contains(p); }
   public boolean contains(Rectangle2D r) { return objGeneralPath.contains(r); }
   public Rectangle getBounds() 
   { 
      System.err.println ("DO NOT USE THIS FUNCTION!!!");
      return objGeneralPath.getBounds();
   }
   public Rectangle2D getBounds2D() { return objGeneralPath.getBounds2D(); }
   public boolean intersects(double x, double y, double w, double h) { return objGeneralPath.intersects(x, y, w, h); }
   public boolean  intersects(Rectangle2D r) { return objGeneralPath.intersects(r); }

   public void computeBounds (uniRect rec, boolean bol)
   {
      // OJO! DO NOT USE "new uniRect" BUT rec.set !!
      //
      if (objRect2D != null)
         rec.set (objRect2D.getBounds2D ());
      else if (objEllipse2D != null)
         rec.set (objEllipse2D.getBounds2D ());
      else if (objArc2D != null)
         rec.set (objArc2D.getBounds2D ());
      else rec.set (objGeneralPath.getBounds2D ());
   }

   public void moveTo (float x, float y)
   {
      objGeneralPath.moveTo (x, y);
      lastX = x;
      lastY = y;
   }

   public void rMoveTo (float x, float y)
   {
      lastX += x;
      lastY += y;
      objGeneralPath.moveTo (lastX, lastY);
   }

   public void lineTo (float x, float y)
   {
      objGeneralPath.lineTo (x, y);
      lastX = x;
      lastY = y;

      //System.out.println ("ojeto after lineto (" + x + ", " + y + ")" + ojeto + " bounds vamos " + ojeto.getBounds());
   }

   public void rLineTo (float x, float y)
   {
      lastX += x;
      lastY += y;
      objGeneralPath.lineTo (lastX, lastY);
      //System.out.println ("ojeto after rlineto (" + x + ", " + y + ")" + ojeto + " bounds vamos " + ojeto.getBounds());
   }

   public void cubicTo (float cx1, float cy1, float cx2, float cy2, float x, float y)
   {
      objGeneralPath.curveTo (cx1, cy1, cx2, cy2, x, y);
      lastX = x;
      lastY = y;
   }

   public void rCubicTo (float cx1, float cy1, float cx2, float cy2, float x, float y)
   {
      lastX += x;
      lastY += y;
      objGeneralPath.curveTo (lastX + cx1, lastY + cy1, lastX + cx2, lastY + cy2, lastX, lastY);
   }

   public void quadTo (float cx, float cy, float x, float y)
   {
      objGeneralPath.quadTo (cx, cy, x, y);
      lastX = x;
      lastY = y;
   }

   public void rQuadTo (float cx, float cy, float x, float y)
   {
      lastX += x;
      lastY += y;
      objGeneralPath.quadTo (lastX + cx, lastY + cy, lastX, lastY);
   }

   public void close ()
   {
      objGeneralPath.closePath ();
   }


   public void addRoundRect (uniRect oval, float rx, float ry)
   {
      objRect2D = new RoundRectangle2D.Float ();
      objRect2D.setRoundRect(oval.left (), oval.top(), oval.width (), oval.height (), rx, ry);
   }

   public void addCircle (float cx, float cy, float radius)
   {
      objEllipse2D = new Ellipse2D.Float (cx - radius, cy - radius, radius + radius, radius + radius);
   }

   public void addOval (uniRect oval)
   {
      objEllipse2D = new Ellipse2D.Float (oval.left (), oval.top(), oval.width (), oval.height ());
   }

   public void addArc (uniRect oval, float startAngle, float sweepAngle)
   {
      objArc2D = new Arc2D.Float (Arc2D.OPEN);

      objArc2D.setFrame (oval.left (), oval.top(), oval.width (), oval.height ());
      objArc2D.setAngleStart(startAngle);
      objArc2D.setAngleExtent(sweepAngle);

      //would not make sense
      //lastX = x2;
      //lastY = y2;
   }
}
