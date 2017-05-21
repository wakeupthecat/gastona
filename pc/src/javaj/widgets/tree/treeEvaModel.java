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

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import de.elxala.langutil.*;
import de.elxala.Eva.*;

/*
   Esta versio'n carga un arbol de una eva
   p.e.

      <arbol nodeSeparator> "/"
      <arbol>
         konoto , ramo, branca, hoja
                ,     ,       , hojita
                ,     ,       , fruto seco
                ,     , bronca, suhoja
                ,     ,       , su fruto seco
                , rima, parte , palabra
                ,     ,       , verbo
         tronco2, ram , pieza
                ,     , peca
         trinc  , sub1, sub2, finalmente

      <arbol currentExpanded>
            knoto,   ramo, bronca
            tronco2, ram



   V II

   ............. DATA

      ----old--------

      <arbol separator>  "/"
      <arbol baseIcons> images/miDesto/Node/
      <arbol defaultImage> nodoNormal.png
      <arbol rootTitle>   Broot
      <arbol>
         patho, more, icon, desc

         jol/hander/peich        , anymore, 101, esto es un bonito node
         jol/hander/woko         , anymore, 111, esto es un bonito node
         jol/hander/makander     , anymore, 110, esto es un bonito node
         jol/ponder/arhoi        , anymore, 101, esto es un bonito node
         jol/ponder/cander/more  , anymore, 001, esto es un bonito node
         jol/ponder/cander/biti  , anymore, 111, esto es un bonito node



   ............. CONTROL


      <arbol shortPath>  1

      <arbol expanded>
         indx, path

      <arbol selected>
         indx, path




*/
public class treeEvaModel implements javax.swing.tree.TreeModel
{
   private treeEBS ebsModel = null;
   private treeInternStruct nodesInEva = null;

   public treeEvaModel (treeEBS ebsData)
   {
      setEBSModel (ebsData);
   }

   public void setEBSModel (treeEBS ebsData)
   {
      ebsModel = ebsData;
      nodesInEva = utilTree.sierra (ebsModel);
//      System.out.println ("=================================");
//      System.out.println (nodesInEva.eva);
//      System.out.println ("=================================");
   }

   // this is needed becase getRoot() is called more than once by the JTree
   // why should we create each time a new object ?
   private treeEvaNodo rootNodeCached = null;

   public Object getRoot()
   {
      if (rootNodeCached == null)
         rootNodeCached = new treeEvaNodo (nodesInEva, ebsModel);

      return rootNodeCached;
   }

   public boolean isLeaf(Object aNode)
   {
      //System.out.println ("is leaf " + note.fullPath() + " ?es decir " + note.nRow + ", " + note.nCol);
      treeEvaNodo note = (treeEvaNodo) aNode;

      return nodesInEva.isLeaf (note.nRow, note.nCol);
   }

   /**
      returns the next row from currRow where the next child is found
      or -1 if not anymore chidren are found.
   */
   private int nextChildRow (int currRow, int parentCol)
   {
      int plusRow = 1;

      while (currRow + plusRow < nodesInEva.countRows ())
      {
         String herman = (parentCol < 0) ? "": nodesInEva.getSubNode (currRow + plusRow, parentCol);
         String filloa = nodesInEva.getSubNode (currRow + plusRow, parentCol + 1);

//System.out.println ("herman = (" + herman + ") filloa = (" + filloa + ")");

         // topamos con un hermano
         if (!herman.equals ("")) return -1;

         // hijo si no ""
         if (!filloa.equals(""))
         {
            return currRow + plusRow;
         }

         plusRow ++;

//System.out.println ("plusRow = " + plusRow);
      }

      return -1;
   }


   private int getChildCount(int row, int col)
   {
      // contar hijos
      int currRow = row;
      int fillos = 0;

      do
      {
         fillos ++;
         currRow = nextChildRow (currRow, col);
      }
      while (currRow != -1);

      //System.out.println ("me hasen " + fillos);
      return fillos;
   }

   public int getChildCount(Object parent)
   {
      if (isLeaf (parent))
         return 0;

      treeEvaNodo note = (treeEvaNodo) parent;

      //System.out.println ("voy a contar los hijos de " + note.fullPath() + " es decir " + note.nRow + ", " + note.nCol);

      return getChildCount(note.nRow, note.nCol);
   }

   public Object getChild(Object parent, int index)
   {
      if (isLeaf (parent))
         return null;

      treeEvaNodo note = (treeEvaNodo) parent;

      //System.out.println ("getChild numero " + index + " de " + note.fullPath() + " es decir " + note.nRow + ", " + note.nCol);

      // look for the next child
      //
      int currRow = note.nRow;
      int pasaIndx = index;
      while (pasaIndx-- > 0)
         currRow = nextChildRow (currRow, note.nCol);

      if (currRow == -1) return null; // ??

//      Eva eData = ebsModel.mustGetEvaData ();   // to handle data with a shorter name

      int columnLeaf  = nodesInEva.countSubNodes (currRow) - 1;
      int columnChild = note.nCol + 1;

      // if it is a leaf or short path is not enabled then return simply the node
      //
      if (columnChild == columnLeaf || ! ebsModel.getIsShortPath ())
      {
         return new treeEvaNodo (nodesInEva, ebsModel, currRow, columnChild);
      }

      // short path is enabled thus determine the long node name (e.g. "root\justOneDir")
      // if any

      String longName = "";
      String SEPAR = ebsModel.getSeparator ();

      // basically while number_of_hild == 1
      //
      while (columnChild != columnLeaf && getChildCount (currRow, columnChild) == 1)
      {
         //(o) javaj_tree_issues "first separator problem"
         // avoid concat separator if given as element (see first separator problem)
         String nodeStr = nodesInEva.getSubNode (currRow, columnChild);
         if (nodeStr.equals (SEPAR))
              longName += SEPAR;
         else longName += SEPAR + nodeStr;
         columnChild ++;
      }

      if (columnChild != columnLeaf)
      {
         longName += SEPAR + nodesInEva.getSubNode (currRow, columnChild);
      }
      else columnChild --;
      longName = longName.substring (SEPAR.length());

      // System.out.println ("Hemos ahorrado un de nombre [" + longName + "] creamos columnChild " + columnChild);
      return new treeEvaNodo (nodesInEva, ebsModel, currRow, columnChild, longName);
   }


   public int getIndexOfChild(Object parent, Object child)
   {
      treeEvaNodo note = (treeEvaNodo) parent;
      treeEvaNodo hij  = (treeEvaNodo) child;

      //.... if (!ebsModel.hasAll ()) return -1;
      // to make a shorter name
      //Eva eData = ebsModel.mustGetEvaData ();

      if (note.nRow > hij.nRow)
         return -1;  // ??

      return hij.nRow - note.nRow;
   }

   public void expandeme (JTree ami)
   {
      Eva evaToExpand = ebsModel.getExpandedNodes (true);

      // expandir los nodos expandidos
      if (evaToExpand.rows () == 0) return;

      // la eva expanded esta formada por filas de una sola columna en donde
      // cada fila contiene el "co'mico" formato de TreePath::toString ()
      //
      // ejemplo:
      //
      // <currentExpanded>
      //    "[\]"
      //    "[\, Mortadelo]"
      //    "[\, Mortadelo, linux]"
      //

      // algoritmo de expansion usando JTree::Row puesto que no se puede usar TreeNode (EvaTreeModel NO GUARDA LOS OBJETOS!!)
      //
      boolean algo = false;
      int quedan = evaToExpand.rows ();
      do
      {
         algo = false;
         int desde = 0;
         int hasta = ami.getRowCount ();

         for (int rr = desde; rr < hasta; rr ++)
         {
            if (ami.isExpanded (rr)) // lo que ya esta expandido asi' se queda ...
               continue;

            String que = ami.getPathForRow (rr).toString ();
            int indx = evaToExpand.indexOf (que);
            if (indx != -1)
            {
               algo = true;
               ami.expandRow (rr);
               quedan --;
               break;
            }
         }
      } while (quedan > 0 && algo);
   }

   //(o) javaj_tree_issues EvaTreeModel el problema de los métodos estáticos
   //
   //     "Necesitamos" métodos estáticos porque queremos poder variar el modelo
   //     pero SIN conocer el modelo propiamente !!
   //
   //    Los controladores poseen el modelo real (la EvaUnit) pero no el objeto
   //    EvaTreeModel que puede haberse creado autom�ticamente por algún zWidget
   //    (s�lo nos comunicamos con EvaUnit's !!)
   //
   private static void addToExpanded (treeEBS pEBSModel, String strTreePathWay)
   {
      Eva evaToExpand = pEBSModel.getExpandedNodes (true);
      int indx = evaToExpand.indexOf (strTreePathWay);
      if (indx == -1)
         evaToExpand.addLine (new EvaLine ( new String [] { strTreePathWay } ));
   }

//   public static void addNodo (EvaUnit dataOfModel, String fullpath)
//   {
//      addNodo (dataOfModel, fullpath, false);
//   }
//
//   public static void addNodo (EvaUnit dataOfModel, String fullpath, boolean expanded)
//   {
//      // como es un me'todo esta'tico no conoce variables miembro como nodeSeparator ...
//      //
//      String nodeSepar = DEFAULT_NODE_SEPARATOR;
//      {
//         Eva aux = dataOfModel.getEva (EVA_NODE_SEPARATOR);
//         if (aux != null)
//            nodeSepar = aux.getValue ();
//      }
//
//      addNodo (dataOfModel, fullpath, expanded, nodeSepar);
//   }
//
//   public static void addNodo (EvaUnit dataOfModel, String fullpath, boolean expanded, String separator)
//   {
//      String [] arrPath = de.elxala.Eva.EvaLine.simpleStringSeparator2Array (fullpath, separator);
//
//      String tripat = "" + separator;
//      String knote = "" + separator;
//      for (int ii = 0; ii < arrPath.length; ii ++)
//      {
//         if (expanded)
//         {
//            addToExpanded (dataOfModel, "[" + tripat + "]");
//            if (ii != arrPath.length -1)
//               tripat += ", ";
//            tripat += arrPath[ii];
//         }
//
//         Eva ev = dataOfModel.getSomeHowEva (knote);
//
//         // anyadirlo si no estaba
//         int indx = ev.indexOf (arrPath[ii]);
//         if (indx == -1)
//         {
//            ev.addLine (new EvaLine (new String [] { arrPath[ii] }));
//         }
//
//         if (ii != 0)
//            knote += separator;
//         knote += arrPath[ii];
//      }
//   }

   // servicios que hay que tener como modelo ...
   //
   public void addTreeModelListener(TreeModelListener listener)
   {
   }

   public void removeTreeModelListener(TreeModelListener listener)
   {
   }

   public void valueForPathChanged (javax.swing.tree.TreePath tp, java.lang.Object o)
   {
   }
}

