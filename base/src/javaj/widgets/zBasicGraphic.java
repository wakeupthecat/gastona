/*
package de.elxala.zWidgets
Copyright (C) 2005 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package javaj.widgets.graphics;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class zBasicGraphic extends JPanel implements MouseListener
{
   private static final Color black = new Color(0, 0, 0);
//   private static final Color white = new Color(240, 240, 255);
//   private static final Color red = new Color(149, 43, 42);
//   private static final Color blue = new Color(94, 105, 176);
//   private static final Color yellow = new Color(255, 255, 140);


   private BufferedImage bimg;
   private Renderable miro = null;

   public zBasicGraphic (String map_name)
   {
      init (map_name, null);
   }

   public zBasicGraphic (String map_name, Renderable renderer)
   {
      init (map_name, renderer);
   }

   public void init (String map_name, Renderable renderer)
   {
      addMouseListener (this);

      // contact with render
      miro = renderer;

      // set default things
      setPreferredSize (new Dimension (500, 400));
      setBackground    (black);
      setLayout        (new BorderLayout());

      // set own name (now could come messages triggered by miro)
      this.setName (map_name);
   }


   public void setName (String map_name)
   {
      super.setName (map_name);

      if (miro != null)
      {
         miro.z2DPanelBaseIs (getName ());
      }
   }

   int origenX=0, origenY=0;

   boolean presado = false;   // truco para evitar el doble mouseRelease!

   public void mousePressed (MouseEvent e)
   {
      origenX = e.getX ();
      origenY = e.getY ();
      presado = true;

      // System.err.println ("Mouse pressed; # of clicks: " + e.getClickCount() + " eleventh " + e);
   }

   public void mouseReleased (MouseEvent e)
   {
      if (!presado) return; // este es doble !!
      presado = false;

      // System.err.println ("Mouse released; # of clicks: " + e.getClickCount() + " eleventh " + e);

      int x = e.getX ();
      int y = e.getY ();

      if (miro != null && (x != origenX || y != origenY))
         miro.cuadroMouse (e.getButton (), origenX, origenY, x, y);
   }

   public void mouseClicked (MouseEvent e)
   {
      //System.err.println ("mouse evento " + e);
   }

   public void mouseEntered (MouseEvent e)
   {
      // System.err.println ("mouseEntered " + e);
   }

   public void mouseExited (MouseEvent e)
   {
      // System.err.println ("mouseExited " + e);
   }


   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      g.fillRect (0, 0, d.width, d.height);
      g.setColor (Color.WHITE);

      if (miro != null)
      {
         miro.render (d.width, d.height, g);
      }
      else
      {
         g.drawLine (0, 0, 100, 100);
      }
   }
}
