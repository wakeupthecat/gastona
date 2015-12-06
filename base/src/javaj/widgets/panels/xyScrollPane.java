/*
package de.elxala.langutil
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

package javaj.widgets.panels;

import javax.swing.*;
import java.awt.*;

/**
   Esta clase soluciona el poblema de los preferred size "por la cara" demasiado grandes del JScroll (400 x 400)
*/
public class xyScrollPane extends JScrollPane
{
   private Component ico = null;

   public xyScrollPane (Component co)
   {
      super(co);
      ico = co;
      setPreferredSize (
         new Dimension (
               (int) (ico.getPreferredSize ().getWidth () * 1.2),
               (int) (ico.getPreferredSize ().getHeight () * 1.2)
               ));
   }

   public Component getComponent ()
   {
      return ico;
   }

   //(o) javaj_widgets_TODO preferredSize en un JScroll. Este mecanismo es seguramente muy caro!
   //                       getPreferredSize se llama muchas veces
   public Dimension getPreferredSize ()
   {
      return new Dimension (
            Math.max (100, (int) (ico.getPreferredSize ().getWidth () * 1.2)),
            Math.max (100, (int) (ico.getPreferredSize ().getHeight () * 1.2))
            );
   }
}

