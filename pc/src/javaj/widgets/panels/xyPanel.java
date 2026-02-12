/*
package de.elxala.langutil
(c) Copyright 2005-2021 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_javaj_layout PANEL

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       PANEL
   <groupInfo>
   <javaClass>  javaj.widgets.panels.xyPanel
   <importance> 4
   <desc>       //Simple horizontal or vertical panel

   <help>
      //
      //  Simple horizontal or vertical layout.
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       PANEL, X | Y, Title
      //
      //       component, component, ...
      //
      //  The first row starts with "PANEL", then the orientation
      //  which can be X or H for horizontal and Y or V for vertical and the third
      //  column, if given, creates a border with a title.
      //
      //  The second and last row is to specified all components to be laied out.


   <examples>
      gastSample

      panel layout example
      panel layout example2

   <panel layout example>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout PANEL"
      //
      //   <layout of F>
      //
      //      PANEL, X
      //
      //      bBoton1, bBoton2, eText
      //

   <panel layout example2>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout PANEL 2"
      //
      //   <layout of F>
      //      PANEL, X
      //
      //      vertical1, vertical2
      //
      //   <layout of vertical1>
      //      PANEL, Y, List and table
      //
      //      iList, tTable
      //
      //   <layout of vertical2>
      //      PANEL, Y, Text and button
      //
      //      xText, bBoton
      //

#**FIN_EVA#

*/

package javaj.widgets.panels;


import java.awt.Component;
import javax.swing.*;
import javax.swing.border.*;

import javaj.widgets.basics.*;

public class xyPanel extends JPanel implements setParameters_able
{
   public xyPanel ()
   {
      super ();
      setBorder(new EmptyBorder(2, 2, 2, 2));
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public void setParameters (CParameterArray params)
   {
      String p1 = params.getValueAt (1, "X").toUpperCase();
      String p2 = params.getValueAt (2, null);

      if      (p1.equals("X") || p1.equals("H")) setLayout (new BoxLayout(this, BoxLayout.X_AXIS));
      else if (p1.equals("Y") || p1.equals("V")) setLayout (new BoxLayout(this, BoxLayout.Y_AXIS));

      if (p2 != null)
         setBorder (BorderFactory.createTitledBorder (p2)); // allow title ""
   }

   public Component add (Component co)
   {
      Component ret = super.add (co);

      return ret;
   }
}
