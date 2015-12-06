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

package javaj.widgets;

import java.awt.Dimension;

import javaj.widgets.basics.*;
import javaj.widgets.tree.*;
import javaj.widgets.kits.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javax.swing.JTree;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;

import de.elxala.langutil.*;
//import de.elxala.langutil.filedir.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (a) zTree

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zTree
   <groupInfo>  misc
   <javaClass>  javaj.widgets.zTree
   <importance> 7
   <desc>       //A general tree of entries (rapid prefix 'a')

   <help>
      //
      // A general tree of entries. The data is given in a table where each row represents an entry.
      // One column of the table, by default the first one, contains the entry path using a separator
      // (by default '/'). Aditionally a second column with name 'icon' might be used to assign
      // differents icons to the entries.
      //
      // Example of data for a tree:
      //
      //       <aMyTree>
      //            node                , icon
      //
      //            root/folder/file1   , normal
      //            root/folder/file2   , big
      //            root/a/b/c/file3    , small
      //
      // Tree from a database:
      // ----------------------
      //
      // The data might be retrieved from a database using the attributes 'dbName' and 'sqlSelect'.
      // This is actually a very easy and flexible way of building trees. For example if we had a
      // master table of customers with fields 'country', 'city', 'name' etc we could write something
      // like this
      //
      //       <aTree sqlSelect>
      //           //SELECT country || "/" || city || "/" || name AS node, type AS icon ORDER BY node
      //
      // to have a tree with folder levels 'country', 'city' and customer name.
      //
      // There are two things to have in mind with the zTree widget:
      //
      //    - The entries should be sorted in order to avoid duplicated folders
      //    - The amount of entries of a tree should not be huge. This is due to performance and
      //      memory reasons but also a reasonable sized tree is more user friendly.
      //
      // Using SQL this is not a problem at all, even more it facilitates a lot the development of
      // trees with filters (e.g. "... WHERE price+0 > 200 and price+0 < 2000"), which can be very
      // helpful for the user.
      //
      // Tree selected entry:
      // ----------------------
      //
      // A single selected path is returned in the attribute 'selectedPath' and multiple selection in
      // 'selectedMultiPath'. Note that it differs from the selection of other widgets that accept
      // tables as data, here not all the columns are offered separately. Again using SQL this can be
      // handled with few effort, following the previous example:
      //
      //    <nodeField> //country || "/" || city || "/" || name AS node
      //    <-- aTree>
      //       LOOP, SQL, @<db>, //SELECT id, etc, @<nodeField> WHERE node = '@<aTree selectedPath>';
      //       @<use id etc selected>
      //
      // where <use id etc selected> is supposed to be a format that works with the retrieved values
      // of the selected path.
      //

   <prefix> a

   <attributes>
      name             , in_out, possibleValues          , desc

                       , in  , (Eva table)               , //Table with the data for the tree at the first column
      var              , in  , (Eva name)                , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)
      dbName           , in  , (file name)               , //Database name from which the data will be retrieved (default is the variable <widgets defaultDB> if given)
      sqlSelect        , in  , (sqlite select or pragma) , //If specified then the data will be retrieved from it using the database specified in dbName
      sqlExtraFilter   , in  ,                           , //Filter that is added to the query

      visible          , in  , 0 / 1                     , //Value 0 to make the widget not visible (default is 1)
      enabled          , in  , 0 / 1                     , //Value 0 to disable the widget (default is 1)
      separator        , in  , (Character)               , //Character to be used as node or path separator
      dataColumn       , in  , (column name)             , //If given, column name of the data that contains the tree path info, otherwise tree path info is taken from the first column.
      baseIcons        , in  , (string)                  , //Prefix string, usually a path, for the icons of all nodes with not empty "icon" column (icon name = 'baseIcons' + 'icon' + 'endIcons')
      endIcons         , in  , (string default is '.png'), //Postfix for the icon name of all nodes with not empty "icon" column (icon name = 'baseIcons' + 'icon' + 'endIcons')
      disableIcons     , in  , 0 / 1                     , //if 1 disable custom Icons (column 'icon') for this tree, it might be more performant for big trees.
      folderIcons      , in  , (list of strings)         , //Specify custom icons for folders, not individually but by its level in the path. Example "general, root0, root1". Note here also applies the rules of baseIcons and endIcons

      shortPath        , in  , 1 | 0                     , //if 1 (true) the nodes with just one child are grouped using the nodeSeparator thus reducing nodes
      rootTitle        , in  , (String)                  , //String to be used as root node. Note that this node will not appear in the attribute 'selectedPath'

      selectedPath     , out , (String)                  , //The selected path given as one string of the nodes separated by the 'separator'
      selectedMultiPath, out , (Eva table)               , //Table with a column "path" with the selected tree entries for single or multi-selection

      selectedNodes    , out , (Eva array)               , //Array used internally for multiselection
      expandedNodes    , out , (Eva array)               , //List of nodes that are expanded

      droppedFiles     , out , (Eva table)               , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped files. Note that this attribute has to exist in order to enable drag & dropping files into this Component.
      droppedDirs      , out , (Eva table)               , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped directories. Note that this attribute has to exist in order to enable drag & dropping files into this Component.

   <messages>

      msg, in_out, desc

      data!       ,  in    , update data
      control!    ,  in    , update control
                  ,  out   , a leaf node has been selected (e.g. clicked)
       2          ,  out   , a leaf node has been double clicked
      parent      ,  out   , a parent node has been selected (e.g. clicked)
      droppedFiles,  out   , If files drag & drop is enabled this message indicates that the user has dropped files (see attribute 'droppedFiles')
      droppedDirs ,  out   , If directories drag & drop is enabled this message indicates that the user has dropped directories (see attribute 'droppedDirs')


   <examples>
      gastSample

<!      data4Tester
      hello zTree
      icons tree
      icon explorer

   <data4Tester>
      //#data#
      //
      //    <aName>
      //        nodeInfo
      //        root/dir1/File1
      //        root/dir1/File2
      //        root/dir2/File3
      //        root/dir2/dir3/LastFile
      //

   <hello zTree>
      //#javaj#
      //
      //    <frames> F, Hello aTree
      //
      //    <layout of F>
      //          EVA, 5, 5, 3, 3
      //          --, X
      //          X , aTree,
      //          A , lYouSelected
      //          A , eSelected
      //
      //#listix#
      //
      //    <-- aTree>         -->, eSelected data!,, the entry @<aTree selectedPath>
      //    <-- aTree parent>  -->, eSelected data!,, the folder @<aTree selectedPath>
      //
      //#data#
      //
      //    <aTree>
      //        name
      //        base/dir1/File1
      //        base/dir1/File2
      //        base/dir2/File3
      //        base/dir2/dir3/LastFile
      //        second/dir2/Other
      //

   <icons tree>
      //#javaj#
      //
      //    <frames> aTree, Example Icons tree
      //
      //#data#
      //
      //    <aTree baseIcons> javaj/img/
      //    <aTree>
      //         name         ,  icon
      //
      //         decide/Accept,  ok
      //         decide/Cancel,  cancel
      //         make/Write   ,  write
      //         make/Save    ,  floppy
      //         make/Encript ,  key
      //         make/Explore ,  explorer
      //         object/Book  ,  book
      //         object/Drive ,  drive
      //
      //#listix#
      //
      //    <-- aTree>
      //       //You have selected @<aTree selectedPath>
      //       //
      //

   <icon explorer>
      //#javaj#
      //
      //   <frames> F, Icon explorer
      //
      //   <layout of F>
      //       EVA, 5, 5, 3, 3
      //        --, X
      //          , lDirectory
      //          , eDir
      //        X , aTree,
      //          , lYouSelected
      //          , eSelected
      //
      //#data#
      //
      //   <eDir>
      //
      //   <aTree endIcons>
      //   <aTree rootTitle> Images found
      //
      //#listix#
      //
      //   <main0>
      //     @<assign separator>
      //
      //   <-- eDir>
      //       SCAN, ADD FILES,, @<eDir>, +, png, +, gif, +, jpeg, +, jpg
      //       -->, aTree data!, sqlSelect, //SELECT fullPath AS icon FROM scan_all ORDER BY icon;
      //
      //   <-- aTree>
      //        -->, eSelected data!,, @<aTree selectedPath>
      //
      //   <assign separator>
      //      CHECK, LINUX, SETVAR, aTree separator, \

#**FIN_EVA#

*/


/**
      mensajes  : tMiArbol nodeselect ))   se ha cambiado la seleccion de nodo
                  tMiArbol control!         ((    debe actualizar el control del arbol (seleccion, visible etc)
                  tMiArbol uM         ((    debe actualizar el modelo del arbol (redibujarlo, expandirlo)


*/
public class zTree extends JTree implements
      MensakaTarget,
      MouseListener,
      TreeSelectionListener,
      TreeExpansionListener
{
   private treeAparato helper = null;
   private dndFileTransHandler dndHandler = null;

   public zTree ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zTree (String map_name)
   {
      build (map_name);
   }

   public void setName (String map_name)
   {
      build (map_name);
   }

   private void build (String map_name)
   {
      staticInit ();

      super.setName (map_name);
      helper = new treeAparato (this, new treeEBS (map_name, null, null));

      addMouseListener (this); // yo mismo (interface MouseAdapter me'todo mousePressed)
      addTreeExpansionListener (this);
      addTreeSelectionListener (this);
   }


   static boolean onceDone = false;

   private void staticInit ()
   {
      if (onceDone) return;
      onceDone = true;

      this.updateUI ();
   }


// ALL THESE METHODS getMaximumSize, getMinimumSize, getPreferredSize
// DOES NOT SEEM TO WORK AT ALL!!

//   public Dimension getMaximumSize()
//   {
//      return super.getMaximumSize();
//   }
//
//   public Dimension getMinimumSize()
//   {
//      return new Dimension (400, 300);
//   }

//   public Dimension getPreferredSize()
//   {
//      if (isFixSize)
//         return new Dimension (lenfactor (largoChar), 22);
//      return super.getPreferredSize();
//   }


   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      // en este caso se reciben por separado EvaUnit de data y de contol!
      //

      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebsTree ().setDataControlAttributes (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebsTree ().setDataControlAttributes (null, euData, pars);
            if (helper.ebsTree ().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            setEnabled (helper.ebsTree ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebsTree ().getVisible ();
            if (visible || isShowing ())
               setVisible  (visible);

            if (dndHandler == null)
            {
               if (helper.ebs ().isDroppable ())
               {
                  // made it "dropable capable"

                  // drag & drop ability
                  //
                  /**
                      Make the zWidget to drag'n'drop of files or directories capable
                      Note that the zWidget is not subscribed to the drag'n'drop message itself ("%name% droppedFiles" or ..droppedDirs")
                      therefore it will not take any action on this event. It is a task of a controller to
                      examine, accept, process and insert into the widget the files if desired and convenient

                      Note that at this point the control for the zWidget (helper.ebs().getControl ())
                      is null and we have to update it into the handler when it chanhges
                  */
                  dndHandler = new dndFileTransHandler (
                                 helper.ebs().getControl (),
                                 helper.ebs().evaName (""),
                                 dndFileTransHandler.arrALL_FIELDS
                                 );
                  setTransferHandler (dndHandler);
               }
            }
            else
            {
               // each time the control changes set it to the drag&drop handler
               dndHandler.setCommunicationLine (helper.ebs().getControl ());
            }
            break;

         default:
            Mensaka.sendPacket ("DBG MensakaTarget not handled");
            return false;
      }

      return true;
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsTree ().hasAll ())
      {
         updateData ();
      }
   }

   private void updateData ()
   {
      treeEvaModel myModel = new treeEvaModel (helper.ebsTree ());
      setModel (myModel);

      // with root or not with root ?
      // make it depending on rootTitle
      //
      setRootVisible (helper.ebsTree ().getRootTitle ().length () > 0);

      // Tell the tree it is being rendered by our application
      if (helper.ebsTree ().getIsDisableIcons())
      {
         // work with default icons
      }
      else
      {
         // allow custom Icons
         setCellRenderer(new custommo());
      }

      getSelectionModel ().clearSelection ();
		updateUI ();
      //(o) javaj_widgets_tree expansio'n de nodos (so'lo una vez)
      myModel.expandeme (this);
   }

   // implementation of interface MouseListener
   public void mouseClicked(MouseEvent e)
   {
      if (!helper.ebsTree().hasControl ()) return;

      // no necesario ...
      // int selRow = getRowForLocation (e.getX(), e.getY());

      widgetLogger.log().dbg (2, "zTree::mouseClicked", "mouse click count " + e.getClickCount());

      if(e.getClickCount() == 1)
      {
         //System.out.println ("::mouseClicked MANTENEMOS = " + wasForSelection + " && " + lastSelectedWasLeaf);
         mantenemosEsperanzas = wasForSelection && lastSelectedWasLeaf;

         // DO NOT SIGNALIZE THE ACTION HERE (if SELECT_ALWAYS == false)
         //    Note that this is done in the event valueChanged which is the right place to do it for two reasons:
         //       1) it is easy to choose between signalAction and signalParentAction
         //       2) selecting nodes is also possible only using the keyboard (i.e. up/down keys, with no clicks)
         //          this is also treated in valueChanged
         if (SELECT_ALWAYS && !valueChanged && wasForSelection)
         {
            if (lastSelectedWasLeaf)
                 helper.signalAction ();
            else helper.signalParentAction ();
         }
      }
      else if(e.getClickCount() == 2)
      {
         if (mantenemosEsperanzas)
         {
            //System.out.println ("::mouseClicked Y MANTENEMOS LAS ESPERANSAS!");
            //System.out.println ("mousePressed 2   click !!");
            helper.signalDoubleAction ();
         }
         else widgetLogger.log().dbg (2, "zTree::mouseClicked", "double click not in leaf");
      }
   }

   // to detect if the click was for a selection or not (expanding nodes clicking on "+" is not a selection!)
   //    Note that the flow in case of double click on a path would be:
   //       mousePress -> valueChanged -> mouseRelease -> mouseClicked1 -> mousePress -> mouseRelease -> mouseClicked2
   //    and the flow in other case :
   //       mousePress -> other thing (i.e. treeExpanded) -> mouseRelease -> mouseClicked1 -> mousePress -> mouseRelease -> mouseClicked2
   //
   private boolean wasForSelection = false;        // inform mouseClick if the click was for a selection reason (for instance expand a node it is not)
   private boolean mantenemosEsperanzas = false;   // to detect double clicks on leaf entries
   private boolean lastSelectedWasLeaf = false;    // to know if last time valuechanged was called it was for a leaf entry
   private boolean valueChanged = false;           // to know if valuechanged is called

   // At the moment we can program the selection in two ways
   //    SELECT_ALWAYS = true  Repeated clicks on the same node will produce actions in leaf nodes and parent nodes
   //                    with one problem: clicks outside will be interpreted as made on the last node selected!
   //                    such clicks should unselect the node for example, but how to know it? (coordinates ?)
   //    SELECT_ALWAYS = false Repeated Clicks on the same node will NOT produce actions, first it has to be selected another node
   //                    (In this case the variables lastSelectedWasLeaf and valueChanged are not needed!)
   private boolean SELECT_ALWAYS = true;

   //public void mousePressed(MouseEvent e) {}
   public void mouseEntered(MouseEvent e)  { /*System.out.println ("MMMMM--entered")*/;}
   public void mouseExited(MouseEvent e)   { /*System.out.println ("MMMMM--exited") */;}
   public void mousePressed (MouseEvent e)
   {
      wasForSelection = SELECT_ALWAYS; // if not select_always reset it, else asume that it will be a selection
      valueChanged = false;
   }
   public void mouseReleased(MouseEvent e) { /*System.out.println ("MMMMM--relasado");*/ }


   public void treeExpanded (TreeExpansionEvent e)
   {
      //(o) TOSEE_javaj_widget_tree Try to send a message on expand to allow dynamic update of the tree
      //               this trivial approach does not work. The receiver of the message has to update
      //               the data and this would close the node again!
      //System.out.println ("[" + "MESOSLAYOMACHO!" + e.getPath ().toString () + "]");
      //Mensaka.sendPacket ("MESOSLAYOMACHO!" + e.getPath ().toString ());

      wasForSelection = false;   // only relevant for SELECT_ALWAYS == true
      Eva Eexpanded = helper.ebsTree ().getExpandedNodes ();

      widgetLogger.log().dbg (2, "zTree::treeExpanded", "expand  " + e.getPath ().toString ());
      int indx = Eexpanded.indexOf (e.getPath ().toString ());
      if (indx == -1)
      {
         Eexpanded.addLine (new EvaLine (new String [] { e.getPath ().toString () }));
      }
   }

   public void treeCollapsed (TreeExpansionEvent e)
   {
      wasForSelection = false;  // only relevant for SELECT_ALWAYS == true
      Eva Eexpanded = helper.ebsTree ().getExpandedNodes ();

      widgetLogger.log().dbg (2, "zTree::treeCollapsed", "collapse  " + e.getPath ().toString ());
      int indx = Eexpanded.indexOf (e.getPath ().toString ());
      if (indx != -1)
      {
         Eexpanded.removeLine (indx);
      }
   }


   /**
      from interface TreeSelectionListener

      indicates that a new selection has been done, note that this might be produced either by
      cursor movement through keyboard or by clicking using the mouse
      at this point we have NO idea about what action has caused the selection!

   */
   public void valueChanged (TreeSelectionEvent e)
   {
      if (!helper.ebsTree().hasControl ()) return;

      widgetLogger.log().dbg (4, "zTree::valueChanged", "valueChanged");
      //System.out.println ("..... wasForSelection TRUADO!!!");
      wasForSelection = true;
      valueChanged = true;

      //COLLECT MULTI NODE SELECTION
      {
         //(o) TODO_javaj_widgets_tree make a better multi-node selection table
         //                            maybe build this list only on demand (i.e. message buildSelectionTable)
         //

         // IDEA tomar los elementos seleccionados en JTree
         // IDEA ejemplo :
         //     <aMiTree selectedNodes>
         //          node         , isPath,
         //          soto_mayor   , 1     , soto, mayor
         //          soto_mayor_on, 0     , soto, mayor, on
         //

         // prepar eva data <aMyTree ESelectedMultiPath>
         //
         Eva ESelectedMultiPath = helper.ebsTree ().getAttribute (widgetEBS.CONTROL, true, "selectedMultiPath");
         ESelectedMultiPath.clear (); // clear previous selection
         ESelectedMultiPath.addLine (new EvaLine("path"));

         // prepar eva data <aMyTree selectedNodes>
         //
         Eva ETableNodes = helper.ebsTree ().getSelectedNodes (true);
         ETableNodes.clear (); // clear previous selection

   		// get the TreePath array with the nodes selected
   		//
         widgetLogger.log().dbg (4, "zTree::valueChanged", "getSelectionPaths");
   		TreePath [] pathos = getSelectionModel ().getSelectionPaths ();
   		if (pathos == null)
   		{
            widgetLogger.log().dbg (4, "zTree::valueChanged", "no paths selected");
   		   //return;
   		}
   		else
   		{
            widgetLogger.log().dbg (4, "zTree::valueChanged", "selected = " + pathos.length + " paths");
   		   for (int ii = 0; ii < pathos.length; ii++)
   		      TreePath2EvaLin (ETableNodes, ii, pathos[ii]);
      		for (int ii = 0; ii < pathos.length; ii++)
      		{
               ESelectedMultiPath.addLine (new EvaLine(new String [] { TreePath2Path (pathos[ii], helper.ebsTree ().getSeparator ()) }));
      		}
         }
      }

      //RAPID AND PRAGMATIC NODE SELECTION (JUST ONE NODE)
      {
         //
         //   <-- aBuelo>
         //         //@<aBuelo selectedPath> is a leaf
         //   <-- aBuelo parent>
         //         //@<aBuelo selectedPath> is a sub path

         // not specially interesting ...
         // Object obj = e.getNewLeadSelectionPath().getLastPathComponent();
         //

         // last node object selected
         //

         // prepar eva data <aMyTree selectedNodes>
         //
         //(o) TODO_javaj_widgets_tree add "selectedPath" and "selectedIsLeaf" to generatedEBS4Tree model!
         Eva ERapidNode = helper.ebsTree ().getAttribute (widgetEBS.CONTROL, true, "selectedPath");
         Eva EIsLeaf    = helper.ebsTree ().getAttribute (widgetEBS.CONTROL, true, "selectedIsLeaf");

         treeEvaNodo node = (treeEvaNodo) getLastSelectedPathComponent();
         if (node == null)
         {
            widgetLogger.log().dbg (2, "zTree::valueChanged", "nothing selected");
   		   ERapidNode.setValue ("", 0, 0);
            //Nothing is selected.
            //return;
         }
         else
         {
   		   ERapidNode.setValue (node.fullPath(), 0, 0);
         }

         // check if it is leaf or directory
         //

         //BUG FIXED: creating a new treeEvaModel caused a new load of nodes from DB, in any case a "sierra" procedure
         //treeEvaModel maModel = new treeEvaModel (helper.ebsTree ());
         treeEvaModel maModel = (treeEvaModel) getModel ();

         if (node == null)
         {
            widgetLogger.log().dbg (2, "zTree::valueChanged", "unselection");
            lastSelectedWasLeaf = false;
            EIsLeaf.setValueVar ("0");
            helper.signalAction ();
         }
         else if (maModel.isLeaf(node))
         {
            widgetLogger.log().dbg (2, "zTree::valueChanged", "it is a leaf");
            lastSelectedWasLeaf = true;
            EIsLeaf.setValueVar ("1");
            helper.signalAction ();
         }
         else
         {
            widgetLogger.log().dbg (2, "zTree::valueChanged", "it is not a leaf");
            lastSelectedWasLeaf = false;
            EIsLeaf.setValueVar ("0");
            helper.signalParentAction ();
         }
      }
   }

   private void TreePath2EvaLin (Eva peva, int row, TreePath patho)
   {
      if (patho == null) return;
	   for (int pp = 0; pp < patho.getPathCount (); pp ++)
	   {
	      peva.setValue (patho.getPath () [pp].toString (), row, pp);
	   }
   }

   private String TreePath2Path (TreePath patho, String separator)
   {
      if (patho == null) return "";
      String spath = "";

	   for (int pp = 1; pp < patho.getPathCount (); pp ++)
	   {
	      spath += patho.getPath () [pp].toString ();
	      if (pp+1 < patho.getPathCount ())
	         spath += separator;
	   }
	   return spath;
   }
}
