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
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

package javaj.widgets.graphics;

import java.awt.*;

public interface Renderable
{
   // z2DPanel will call this method to infom about his name (and therefore the render can send messages to him)
   //
   public void z2DPanelBaseIs (String map_name);

   // z2DPanel will call this method on its paint method
   //
   public void render (int w, int h, Graphics2D g2);
   public void render (int w, int h, Graphics g);

   // z2DPanel will call this method when size has changed (panel resized)
   //
   public void reset  (int width, int height);

   // button: 1=izquierdo, 2=centro, 3=derecho
   public void cuadroMouse (int button, int x0, int y0, int x1, int y1);
}
