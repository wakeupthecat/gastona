/*
package javaj.widgets
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

package javaj.widgets.table.util;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import javaj.widgets.table.*;

/*
    To detect double click on lists
*/
public class list2ClickListener implements MouseListener
{
   private tableAparato helper;

   public list2ClickListener (tableAparato tableHelper)
   {
      helper = tableHelper;
   }

   // implementation of interface MouseListener
   public void mouseClicked(MouseEvent e)
   {
      if (e.getClickCount() == 2)
      {
         helper.signalDoubleAction ();
      }
   }

   //public void mousePressed(MouseEvent e) {}
   public void mouseReleased(MouseEvent e) {}
   public void mouseEntered(MouseEvent e)  {}
   public void mouseExited(MouseEvent e)   {}
   public void mousePressed (MouseEvent e) {}
}
