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
import de.elxala.zServices.logger;

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

      <arbol separator>  "/"

      <arbol dbName>     aristothala.db
      <arbol sqlSelect>  //SELECT * FROM myTree ;

      <arbol baseImagen> images/miDesto/Node

      <arbol shortPath>  1
      <arbol rootTitle>   Broot

      <arbol expanded>
         indx, path

      <arbol selected>
         indx, path

      <arbol>
         patho, more, icon, desc

         jol/hander/peich        , anymore, 101, esto es un bonito node
         jol/hander/woko         , anymore, 111, esto es un bonito node
         jol/hander/makander     , anymore, 110, esto es un bonito node
         jol/ponder/arhoi        , anymore, 101, esto es un bonito node
         jol/ponder/cander/more  , anymore, 001, esto es un bonito node
         jol/ponder/cander/biti  , anymore, 111, esto es un bonito node



*/
public class utilTree
{
   private static logger log = new logger (new utilTree(), "javaj.widgets.tree.utilTree", null);

   public static treeInternStruct sierra (treeEBS model)
   {
      String sqlQuery  = model.getSqlSelect ();
      treeInternStruct Etarget = null;

      if (sqlQuery.equals (""))
      {
         Etarget = sierraFromEva (model);
      }
      else
      {
         Etarget = sierraFromDB (model);
      }
      return Etarget;
   }

   private static treeInternStruct sierraFromEva (treeEBS model)
   {
      Eva    evaTable  = model.mustGetEvaData ();
      String separ     = model.getSeparator ();
      String dataColumn = model.getDataColumn ();

      Eva Eorigen = evaTable;
      treeInternStruct Etarget = new treeInternStruct ("TIS(tree intern structure)");

      int colIndx = Eorigen.colOf (dataColumn);
      if (colIndx == -1)
          colIndx = 0;

      int iconIndx = Eorigen.colOf ("icon");

      String [] old = null;
      for (int ii = 1; ii < Eorigen.rows (); ii ++)
      {
         old = cutAndAddPath (Eorigen.getValue (ii, colIndx), Eorigen.getValue (ii, iconIndx), separ, Etarget, old);
      }
//      System.out.println ("HE ASSERRAO");
//      System.out.println (Etarget.eva);
//      System.out.println ("---------------HE ASSERRAO");
      return Etarget;
   }

   public static treeInternStruct sierraFromDB (treeEBS model)
   {
      //  "PATH TO TREE", "evaTarget", "separ", "SQL", "database", "fieldName", "sqlSelectQuery"

      //String databaseName = model.getDbName ();
      String dataColumn    = model.getDataColumn ();
      //String sqlSelect    = model.getSqlSelect ();
      String separ        = model.getSeparator ();
      treeInternStruct Etarget = new treeInternStruct ("TIS(tree intern structure)");

      de.elxala.db.sqlite.tableROSelect roSel = new de.elxala.db.sqlite.tableROSelect (model);
      // alternatively ...
      // tableROSelect roSel = listix.table.roSqlPool.get (model);

      int colIndx = roSel.getColumnIndex (dataColumn);
      if (colIndx == -1)
          colIndx = 0;

      int iconIndx = roSel.getColumnIndex ("icon");

      String [] old = null;
      String iconStr = "";
      for (int ii = 0; ii < roSel.getRecordCount (); ii ++)
      {
         iconStr = (iconIndx == -1) ? "": roSel.getValue (ii, iconIndx);
         old = cutAndAddPath (roSel.getValue (ii, colIndx), iconStr, separ, Etarget, old);
      }
      return Etarget;
   }


   /**
      cut the 'fullPath' acording to the separator 'separ' and places
      the nodes in the zTree way into the target eva 'target'
      returns the current node array to be used as previous record for the next
      cut

      from:
         base/folder/file1
         base/folder/file2
         base/folder/sub1/file3
         base/end

      to:
         base  , folder  , file1
               ,         , file2
               ,         , sub1   , file3
               , end

       detecting problems like

         base/subdir
         base/subdir/file1

       Error: previous node to "base/folder/file1" was a folder!

       or

         base/fo1/file1
         base/fo2/sub1/file2
         base/fo3/sub1/file3

         base  , fo1 , file1
               , fo2 , sub1   , file2
               , fo3 , sub1   , file3

   */
   private static String [] cutAndAddPath (String fullPath, String iconStr, String separ, treeInternStruct target, String [] prevRecord)
   {
      String [] arr = de.elxala.langutil.Cadena.simpleToArray (fullPath, separ);
      int rowAdd = target.eva.rows ();

      // add icon info
      target.eva.setValue (iconStr, rowAdd, 0);

      boolean stillIdentic = (prevRecord != null);

      for (int cc = 0; cc < arr.length; cc ++)
      {
         // check if item is identical to previous record
         boolean identicPrev = false;
         //OLD////if (prevRecord != null && cc < prevRecord.length)
         if (stillIdentic)
         {
            if (cc < prevRecord.length)
            {
               stillIdentic &= prevRecord[cc].equals (arr[cc]);
            }
            else
            {
               if (stillIdentic)
                  log.err ("cutAndAddPath", "Error: previous node to \"" + fullPath + "\" was a folder!");
            }
         }

         //(o) javaj_tree_issues "first separator problem"
         //07.08.2010 12:34 "first separator problem"
         //FIX:
         // Había problemas cuando se empieza por separador y se mezclan
         // paths absolutos con relativos (p.e. al hacer un ps -ef en linux)
         //
         //    NODO
         //    retonto
         //    sarporo/moleculo/achis
         //    sarporo/moleculo/orkos
         //    /usr/sbin/syslogd
         //    /usr/sbin/aloyo
         //    /esomos/aluyo/chavalin
         //    /esomos/aluyo/estapos
         //
         // el /usr/sbin... se reemplaza por { "", usr, sbin, ... } lo que crea la confusión
         // con el path anterior y se toma como hijo de este sin serlo
         //
         // Como en la estructura eva para los nodos NO tenemos contemplado el nodo vacío ("")
         // tenemos que ponerle un nombre, y le ponemos el nombre separador
         //
         String contents = (cc==0 && fullPath.startsWith(separ)) ? separ: arr[cc];

         target.eva.setValue (stillIdentic ? "": contents, rowAdd, cc + 1);
      }

      return arr;
   }
}
