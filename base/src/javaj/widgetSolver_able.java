/*
packages de.elxala
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

package javaj;

import java.awt.Component;

public interface widgetSolver_able
{
   /**
      Has to return a component

      @arg  layoutModel  layout Model where all needed layouts (<layout of xxx>) are to be found
      @arg  basicName    basic name of the widget
      @arg  prefixName   prefix for the widget
      @arg  createContainers if true the containers and layouts will be created and the widgets laid out on them
                             if false just the widgets will be instanciated and registered
   */
   public Component getNativeWidget (javajEBS layoutModel, String basicName, String prefixName, boolean createContainers);
}