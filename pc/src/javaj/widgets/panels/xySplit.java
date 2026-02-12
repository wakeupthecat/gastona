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
   //(o) javaj_Catalog_source

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       SPLIT
   <groupInfo>
   <javaClass>  javaj.widgets.panels.xySplit
   <importance> 3
   <desc>       //Horizontal or vertical split

   <help>
      //
      //  Horizontal or vertical Split pane (for details about split pane see java JSplitPane)
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       SPLIT, X | Y
      //
      //       component1, component2
      //
      //  The first row starts with "SPLIT", then the orientation
      //  which can be X or H for horizontal and Y or V for vertical.
      //
      //  The second row is to specify the two components to split.


   <examples>
      gastSample

      split layout example
      split layout example2

   <split layout example>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout SPLIT"
      //
      //   <layout of F>
      //
      //      SPLIT, X
      //
      //      oConsole, xText
      //

   <split layout example2>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout SPLIT 2"
      //
      //   <layout of F>
      //
      //      SPLIT, X
      //
      //      xText, rightPanel
      //
      //   <layout of rightPanel>
      //
      //      SPLIT, Y
      //
      //      tTable, oConsole
      //

#**FIN_EVA#

*/

package javaj.widgets.panels;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.*;

import javaj.widgets.basics.*;

public class xySplit extends JSplitPane implements setParameters_able
{
   public xySplit ()
   {
      super ();
      // setBorder (BorderFactory.createBevelBorder (BevelBorder.RAISED));

      setBorder(new EmptyBorder(2, 2, 2, 2));
      setOneTouchExpandable (true);
      setMinimumSize (new Dimension(0,0));
      setContinuousLayout (true);
   }

   public void removeAll ()
   {
      //NOTE : if we use Container::removeAll the slider will be not visible after adding
      //       the components (java bug ?), so do not removeAll simply call again add etc..
//      super.removeAll ();
//      firstComp = true;
   }

   public void setParameters (CParameterArray params)
   {
      String p1 = params.getValueAt (1, "X");
      String p2 = params.getValueAt (2, null);

      if (p1.equalsIgnoreCase("X") || p1.equalsIgnoreCase("H"))
         setOrientation (HORIZONTAL_SPLIT);
      else if (p1.equalsIgnoreCase("Y") || p1.equalsIgnoreCase("V"))
         setOrientation (VERTICAL_SPLIT);

      if (p2 != null)
         setBorder (BorderFactory.createTitledBorder (p2));
   }

   private boolean firstComp = true;

   public Component add (Component co)
   {
      // co.setContinuousLayout (true);
      switch (getOrientation ())
      {
         case HORIZONTAL_SPLIT:
            if (firstComp)
                 setLeftComponent  (co);
            else setRightComponent (co);
            break;
         case VERTICAL_SPLIT:
            if (firstComp)
                 setTopComponent  (co);
            else setBottomComponent (co);
            break;
         default:
            break;
      }

      firstComp = !firstComp;
      return co;
   }
}
