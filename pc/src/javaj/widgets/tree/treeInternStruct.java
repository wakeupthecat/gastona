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
      <TIS(internal)>
         img/node, konoto , ramo, branca, hoja
         img/node,        ,     ,       , hojita
         img/node,        ,     ,       , fruto seco
         img/node,        ,     , bronca, suhoja
         img/node,        ,     ,       , su fruto seco
         img/node,        , rima, parte , palabra
         img/node,        ,     ,       , verbo
         img/node, tronco2, ram , pieza
         img/node,        ,     , peca
         img/node, trinc  , sub1, sub2, finalmente


   ******* PREPARATION 17.04.2008 19:00
      or, also posible, including parent directories

      <TIS(internal)>
         img/node, konoto
         img/node,        , ramo
         img/node,        ,     , branca
         img/node,        ,     ,       , hoja
         img/node,        ,     ,       , hojita
         img/node,        ,     ,       , fruto seco
         ...


   or better would be ...

      <TIS(internal)>

            0      1     2     ..   nodesNr  nodesNr+1  nodesNr+2
         nodesNr, node, node, ...., lastNode, image, keyData

   ******* PREPARATION 17.04.2008 19:00


*/

public class treeInternStruct // extends Eva
{
   public Eva eva;
   private static final int IMG_OFFSET = 1;   // offset due to image information at column 0

   public treeInternStruct(String name)
   {
      eva = new Eva (name);
   }

   public int countSubNodes (int row)
   {
      int cc = eva.cols (row);
      return (cc == 0) ? 0: cc - IMG_OFFSET;
   }

   public boolean isLeaf (int row, int col)
   {
      return (col == countSubNodes (row) - 1);

// ************ PREPARATION 17.04.2008 19:58
      // it is node if it is in the last column AND the next node has (EITHER less columns or the column equivalent is not "")
      // example

      // row=R col=3 ("branca") is not a leaf because has a child ("hoja")
      //                     0         1      2      3      4
      //      row=R)      img/node,        ,     , branca
      //      row=R+1)    img/node,        ,     ,       , hoja

      // row=R col=3 ("branca") is a leaf because has the next element is a 'brother'
      //                     0         1      2      3      4
      //      row=R)      img/node,        ,     , branca
      //      row=R+1)    img/node,        ,     , hoja

      // row=R col=3 ("branca") is a leaf because has the next element is at a level with less columns
      //                     0         1      2      3      4
      //      row=R)      img/node,        ,     , branca
      //      row=R+1)    img/node,        , hoja

//      int colsMe   = countSubNodes (row);
//      int colsNext = countSubNodes (row+1);
//
//      System.out.println ("isLeaf (" + row + ", " + col + ") \"" + eva.getValue (row, col) + "\"");
//      System.out.println ("    colsMe = " + colsMe + " colsNext = " + colsNext);
//
//      if (col+IMG_OFFSET < colsMe) return false;
//      System.out.println ("    maybe1");
//      if (colsNext < colsMe) return false;
//      System.out.println ("    maybe2");
//      if (eva.getValue (row+1, col+IMG_OFFSET).length () == 0) return false;
//      System.out.println ("    yes! if leaf");
//
//      return true;
// ************ PREPARATION 17.04.2008 19:58
   }

   public int countRows ()
   {
      return eva.rows ();
   }

   public String getSubNode (int row, int col)
   {
      // permitir llamada con -1!
      if (col < 0)
      {
         return "";
      }
      return eva.getValue (row, col + 1);
   }

   public String getIconName (int row)
   {
      return eva.getValue (row, 0);
   }
}
