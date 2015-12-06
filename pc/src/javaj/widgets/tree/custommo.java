/*
library de.elxala
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

package javaj.widgets.tree;

// Imports
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

import de.elxala.langutil.graph.sysDefaults;
import de.elxala.langutil.*;
import javaj.widgets.basics.widgetLogger;

/**
   TreeCell renderer for zJTree
*/
public class custommo extends     JLabel
                      implements  TreeCellRenderer
{
   private boolean isSelected;
   private static DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

   public custommo()
   {
      //(o) javaj_widgets_zTree customize default icons
   }

   public Component getTreeCellRendererComponent
         (
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus
         )
   {
      if (value instanceof treeEvaNodo)
      {
         // ok ..
      }
      else
      {
         widgetLogger.log().severe ("tree.customo::getTreeCellRendererComponent", "the node object is not a treeEvaNodo!");
         return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      }

      treeEvaNodo eNodo = (treeEvaNodo) value;
      isSelected = selected;

      //(o) TODO_javaj_widgets_zTree use some limited pool/cache of icons
      //

      String iconName = leaf ? eNodo.getIconName4Leaf(): eNodo.getIconName4Folder();
      if (iconName.length () > 0)
      {
         //System.out.println ("REZABO [" + iconName + "]");
         setIcon (javaLoad.getSomeHowImageIcon (iconName));
      }
      else
      {
         //System.out.println ("REZABO NASDA!");
         if (leaf)
         {
            setIcon ((Icon) UIManager.get ("Tree.leafIcon"));
         }
         else
         {
            // un poco de ma's variedad con carpetitas curiosas ...
            //
            if (eNodo.isRootNode () && UIManager.get ("Tree.rootIcon") != null)
            {
               setIcon ((Icon) UIManager.get ("Tree.rootIcon"));
            }
            else if (eNodo.isSecondRootNode () && UIManager.get ("Tree.secondRootIcon") != null)
            {
               setIcon ((Icon) UIManager.get ("Tree.secondRootIcon"));
            }
            else
            {
               // es un vulgar folder! para ti' no tenemos personalisasion campeo'n!
               setIcon ((Icon) UIManager.get ((expanded) ? "Tree.closedIcon" : "Tree.openIcon"));
               //setIcon (javaLoad.getSomeHowImageIcon ("javaj/img/folder.png"));
            }
         }
      }

      // Set the correct foreground color
      if (!isSelected)
      {
         setBackground (Color.white);
         setForeground (Color.black);
      }
      else
      {
         setBackground (Color.orange); // has no influence but it has to be set! (?)
         setForeground (Color.black);
      }
      setText (value.toString () + "                                                            ");

      return this;
   }

   // Hack to paint the background!. Usually a JLabel should
   // paint its own background, but due to an apparent bug or
   // limitation in the TreeCellRenderer, we need paint method here
   public void paint (Graphics g)
   {
      // Set the correct background color
      //g.setColor (isSelected ? SystemColor.textHighlight : Color.white);
      //g.setColor (isSelected ? Color.lightGray : Color.white);
      g.setColor (isSelected ? Color.orange : Color.white);

      // Draw a rectangle in the background of the cell
      g.fillRect( 0, 0, getWidth() - 1, getHeight() - 1 );

      super.paint( g );
   }
}
