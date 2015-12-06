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

package javaj.widgets.tree;

import de.elxala.langutil.*;
import de.elxala.Eva.*;

/*
*/
public class treeEvaNodo
{
   private treeEBS ebsModel = null;
   private treeInternStruct nodesInEva = null;

   public int nRow = -1;
   public int nCol = -1;

   public String [] arrName = new String [0];
   public String iconName = null;
   public String nodeName = "";

   /**
      This constructor is only for the root node
      rootNodeStr is the representation of the root node, typically "/" or "\\"
      or simply "" but it can be any other (e.g. "root")
   */
   public treeEvaNodo (treeInternStruct pNodesInEva, treeEBS pEBSModel)
   {
      init (pNodesInEva, pEBSModel, 0, -1);
   }

   public treeEvaNodo (treeInternStruct pNodesInEva, treeEBS pEBSModel, int row, int col)
   {
      init (pNodesInEva, pEBSModel, row, col, null);
   }

   public treeEvaNodo (treeInternStruct pNodesInEva, treeEBS pEBSModel, int row, int col, String nodeReName)
   {
      init (pNodesInEva, pEBSModel, row, col, nodeReName);
   }

   public void init (treeInternStruct pNodesInEva, treeEBS pEBSModel, int row, int col)
   {
      init (pNodesInEva, pEBSModel, row, col, null);
   }

   public void init (treeInternStruct pNodesInEva, treeEBS pEBSModel, int row, int col, String nodeReName)
   {
      ebsModel = pEBSModel;
      nodesInEva = pNodesInEva;

      //System.out.println ("QUEREO EL NOTO " + row + ", " + col);

      nRow = row;
      nCol = col;

      // is root ? then array [0]
      if (nRow == -1)
      {
         nCol = -1;
         nRow = 0;
      }

      iconName = nodesInEva.getIconName (nRow);

      arrName = new String [nCol + 1];
      if (nCol == -1) return;

      arrName [nCol] = nodesInEva.getSubNode (nRow, nCol);

      // node name
      nodeName = (nodeReName != null) ? nodeReName: arrName [nCol];

      // iterate upwards to find all parents
      //
      int rowUp = nRow;
      for (int ii = arrName.length - 2; ii >= 0; ii --)
      {
         while (rowUp > 0 && nodesInEva.getSubNode (rowUp, ii).equals (""))
            rowUp --;
         arrName [ii] = nodesInEva.getSubNode (rowUp, ii);
         //System.out.println ("   ELEMENTAL " + arrName [ii]);
      }
   }

   public boolean isRootNode ()
   {
      return nCol == -1;
   }

   public boolean isSecondRootNode ()
   {
      return nCol == 0;
   }

//NO: Que significari'a ? Nodo el cual tiene al menos una leaf, o nodo que solo tiene leafs ? poco u'til
//   public boolean isLastFolderNode ()
//   {
//      return false; // ?
//   }

   public String toString ()
   {
      return getCaption ();
   }

   public String getCaption ()
   {
      if (isRootNode ())
      {
         // return rootTile and if not exists then rootName
         if (ebsModel.getRootTitle () != null)
            return ebsModel.getRootTitle ();

         //return ebsModel.getRootPath ();
         return nodeName;
      }

      return nodeName; // arrName [nCol];
   }

   private String concatPath (int ini, int len)
   {
      //String nam = ebsModel.getRootPath ();
      String nam = "";

      for (int ii = ini; ii < len; ii ++)
      {
         if (ii != 0)
            nam += ebsModel.getSeparator ();

         //(o) javaj_tree_issues "first separator problem"
         // avoid concat separator if given as element (see first separator problem)
         if (!arrName[ii].equals (ebsModel.getSeparator ()))
            nam += arrName[ii];
      }

      return nam;
   }


   public String fullPath ()
   {
      return concatPath(0, arrName.length);
   }

   public String parentPath ()
   {
      return concatPath(0, -1 + arrName.length);
   }

   private String fullIconName (String kernName)
   {
      return (kernName.length () == 0) ? "": (ebsModel.getBaseIcons () + kernName + ebsModel.getEndIcons ());
   }

   public String getIconName4Leaf ()
   {
      return fullIconName (iconName);
   }

   public String getIconName4Folder ()
   {
      Eva folderIcons = ebsModel.getFolderIcons();
      // System.out.println ("folderIcons length " + folderIcons.cols (0));
      if (folderIcons == null || folderIcons.cols (0) == 0) return ""; // no custom icons for folders

      //       System.out.println ("nCol " + nCol + "  [" + ebsModel.getIconRoot0() + "]");

      //Decide icon name (only the kern see ::getIconName ())
      //
      String name = "";

      // <... folderIcons> general, root0, root1, root2
      // match nCol -1, 0, 1 with 1, 2, 3
      if (nCol < 2)
      {
         name = folderIcons.getValue (0, nCol + 2);
      }
      // System.out.println ("pasiamios! nCol " + nCol + " name [" + name + "]");
      if (name.length () == 0)
         name = folderIcons.getValue (0, 0); // fallback icon

      return fullIconName (name);
   }
}
