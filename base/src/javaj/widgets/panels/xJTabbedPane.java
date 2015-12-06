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

/*
   //(o) javaj_Catalog_source

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       TABBED
   <groupInfo>  special containers
   <javaClass>  javaj.widgets.panels.xJTabbedPane
   <importance> 7
   <desc>       //Tabbed pane layout

   <help>
      //
      //  Creates a tabbed pane with as much tabs as components specified
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       TABBED
      //
      //       Tab label 1, component1
      //       Tab label 2, component2
      //       ...        , ...
      //
      //  The first row is "TABBED". The second and next rows until the end specify for each component
      //  the label and the component itself.


   <examples>
      gastSample

      tabbed layout example

   <tabbed layout example>
      //#javaj#
      //
      //   <frames>
      //      F, "example layout TABBED"
      //
      //   <layout of F>
      //
      //      TABBED
      //
      //      A text  , xText
      //      A table , tTable
      //      Other   , combi
      //
      //   <layout of combi>
      //       EVA, 5, 5, 4, 4
      //
      //       ,       , X
      //       , lLabel, eEditF, bButton
      //     X , xText2, -     , -

#**FIN_EVA#

*/

package javaj.widgets.panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import javaj.widgets.basics.*;


//
//
public class xJTabbedPane extends JTabbedPane
{
   // public JTabbedPane tabPane = new JTabbedPane ();

   public xJTabbedPane ()
   {
      setBorder(new EmptyBorder(2, 2, 2, 2));
      setFont (new Font ("Arial", 0, 9));
      // add (tabPane);
   }

   public Component add (Component ob)
   {
      addTab ("title" + getTabCount (), ob);
      return ob;
   }

   public Component add (String title, Component ob)
   {
      addTab (title, ob);
      return ob;
   }
}
