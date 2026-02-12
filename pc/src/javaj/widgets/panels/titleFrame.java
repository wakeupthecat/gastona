/*
package de.elxala.langutil
(c) Copyright 2021 Alejandro Xalabarder Aulet

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


/*
   Intern panel to be used in zwidgets ?nName
      for instance
         enDescription
      will generate a edit box framed with a panel with title "Description"
*/

package javaj.widgets.panels;

import java.awt.Component;
import javax.swing.*;
import javax.swing.border.*;

import javaj.widgets.basics.*;

public class titleFrame extends JPanel
{
   public titleFrame ()
   {
      //super ();
      setLayout (new BoxLayout(this, BoxLayout.X_AXIS));
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public titleFrame (Component co, String title)
   {
      setLayout (new BoxLayout(this, BoxLayout.X_AXIS));
      setTitle (title);
      super.add (co);
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public void setTitle (String title)
   {
      setBorder (BorderFactory.createTitledBorder (title));
   }
}
