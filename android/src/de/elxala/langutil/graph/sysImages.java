/*
package de.elxala.langutil
(c) Copyright 2009 Alejandro Xalabarder Aulet

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

package de.elxala.langutil.graph;

import javax.swing.*;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;

public class sysImages
{
   // another log client of ...sysDefaults
   private static logger log ()
   {
      return sysDefaults.log;
   }

   /**
       set default images for leaf and folders for all trees using UIManager

            Example of eva with values supported by zTree of javaj

            <sysDefaultImages>
               javaj/img/leaf.png    ,  Tree.leafIcon
               javaj/img/folder.png  ,  Tree.closedIcon
               javaj/img/folder.png  ,  Tree.openIcon
               javaj/img/node1.png   ,  Tree.collapsedIcon
               javaj/img/node2.png   ,  Tree.expandedIcon
               javaj/img/folder.png  ,  Tree.rootIcon
               javaj/img/folder.png  ,  Tree.secondRootIcon

   */
   public static void setDefaultImages (Eva defaultImages)
   {
      javaj.globalJavaj.ensureDefRes_javajUI_tree ();

      for (int ii = 0; ii < defaultImages.rows (); ii ++)
      {
         String resName = defaultImages.getValue (ii, 0); // e.g. "javaj/img/leaf.png"
         for (int cc = 1; cc < defaultImages.cols(ii); cc ++)
         {
            String key = defaultImages.getValue (ii, cc); // e.g. "Tree.leafIcon"
            utilSys.objectSacPut (key, javaLoad.getSomeHowDrawable (resName, false));
         }
      }

      // other Tree properties not used at the moment
      //
      //Tree.drawHorizontalLines Boolean true  If true nodes have a horizontal connecting them to the leading edge of their parent.
      //Tree.drawVerticalLines Boolean true  If true a vertical line is drawn down from expanded nodes.
      //Tree.leftChildIndent Integer 0  This plus Tree.rightChildIndent account for the total space, along the y axis, to offset nodes from their parent.
      //Tree.rightChildIndent Integer 0  This plus Tree.leftChildIndent account for the total space, along the y axis, to offset nodes from their parent.
      //Tree.rowHeight Integer -1  Row height for the Tree.
   }
}
