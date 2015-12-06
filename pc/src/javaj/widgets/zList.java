/*
package javaj.widgets
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

import javax.swing.JList;
import java.awt.Component;

import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;
import javax.swing.event.*;

import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javaj.widgets.table.util.*;
import javaj.widgets.basics.*;
import javaj.widgets.table.*;
import javaj.widgets.kits.dndFileTransHandler;


/*
   //(o) WelcomeGastona_source_javaj_widgets (i) zList

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zList
   <groupInfo>  table
   <javaClass>  javaj.widgets.zList
   <importance> 9
   <desc>       //A list that supports memory tables or db queries (rapid prefix 'i')

   <help>
      //
      // List which data can be given as a Table in memory (Eva table) or can be retrieved from a
      // database using a sql Select query (see zComboBox and "Table select" and "Table from database"
      // widget characteristics).
      //
      // Widget characteristics: "Common", "Table select", "Table from database" and "Files droppable".
      //

   <prefix> i

   <attributes>
     name             , in_out, possibleValues             , desc

                      , in    , Eva table                  , //Data for the list, that an Eva table with column names at the first row
     var              , in    , Eva name                   , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

     visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
     visibleColumns   , in    , (Eva line)                 , //list of visible columns

     subTableSelection, out   , (Eva table)                , //a table formed with the selected rows
     selected.COLNAME , out   , (String)                   , //contents of the column with name COLNAME of last selected row of the table

     dbName           , in    , (file name)                , //If specified then it is the database name from which the data will be retrieved (default is the global temporary database)
     sqlSelect        , in    , (sqlite select or pragma)  , //If specified then the data will be retrieved from a database (see dbName)
     sqlExtraFilter   , in    , (part of sqlite select)    , //Filter that is added to the query

     droppedFiles     , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped files. Note that this attribute has to exist in order to enable drag & dropping files into this Component.
     droppedDirs      , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped directories. Note that this attribute has to exist in order to enable drag & dropping files into this Component.

   <messages>

      msg, in_out, desc

      data!   ,  in    , update data
      control!,  in    , update control
              ,  out   , an element of the list has been selected (see also selected.COLNAME and subTableSelection attributes)
      2       ,  out   , an element of the list has been double clicked (see also selected.COLNAME attribute)
      droppedFiles, out , If files drag & drop enabled this message indicates that the user has dropped files (see attribute 'droppedFiles')
      droppedDirs , out, If directories drag & drop is enabled this message indicates that the user has dropped directories (see attribute 'droppedDirs')


   <examples>
     gastSample

     <!data4Tester
     hello zList
     list of listix commands

   <data4Tester>
      //#data#
      //
      //    <iName visibleColumns>  name
      //
      //    <iName>
      //       id, name, telephone
      //
      //       12, Michael,  5563
      //       56, Evariste, 1811
      //      231, Alea,     2112
      //      108, Simago,   3222
      //

   <hello zList>
      //#javaj#
      //
      //    <frames> iLista, Hello zSmallList
      //
      //#data#
      //
      //    <iLista visibleColumns>  name
      //
      //    <iLista>
      //       id, name, telephone
      //
      //       12, Michael,  5563
      //       56, Evariste, 1811
      //      231, Alea,     2112
      //      108, Simago,   3222
      //
      //#listix#
      //
      //    <-- iLista>
      //       //Last selected @<iLista selected.name>
      //       //
      //

   <list of listix commands>
      //#javaj#
      //
      //    <frames> F, Simple list of Listix Commands, 400, 500
      //
      //    <layout of F>
      //      EVA, 10, 10, 6, 6
      //
      //      ---, X
      //       X , iLista,
      //         , lDescription
      //
      //#data#
      //
      //    <iLista visibleColumns> name
      //
      //#listix#
      //
      //   <main0>
      //      SET PROP, gastona.defaultDB, @<:lsx tmp>
      //      RESUTIL, COPY, META-GASTONA/WelcomeGastona/genDB/gastonaDoc.db, @<:sys gastona.defaultDB>
      //      -->, iLista data!, sqlSelect, //SELECT name, desc FROM tGastItemDoc WHERE structType == 'listix_command' ORDER BY name
      //
      //   <-- iLista>
      //     -->, lDescription data!,, @<iLista selected.desc>


#**FIN_EVA#

*/
import javaj.widgets.panels.*;

/**
*/
public class zList extends JList implements MensakaTarget
{
   private tableAparato helper = null;
   private dndFileTransHandler dndHandler = null;
   private boolean SILENT_SELECTION = false;

   //try scroll, doesnot work!
   //   private xyScrollPane  scroll = null;

   public zList ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

   public zList (String map_name)
   {
      build (map_name);
   }

   public void setName (String map_name)
   {
      build (map_name);
   }

   public void build (String map_name)
   {
      super.setName (map_name);
      helper = new tableAparato (this, new tableEBS (map_name, null, null));

      // listening to selection and mouse double click
      //
      getSelectionModel ().addListSelectionListener (new meOigo());
      addMouseListener (new list2ClickListener (helper));

      // decorate selection
      //
      setSelectionForeground (helper.ebsTable ().getSelFontColor ().getAwtColor ());
      setSelectionBackground (helper.ebsTable ().getSelNormalColor ().getAwtColor ());

      //try scroll, doesnot work!
      //if (scroll == null)
      //   scroll = new xyScrollPane (this);
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.setDataControlAndModel (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.setDataControlAndModel (null, euData, pars);
            if (helper.ebsTable().firstTimeHavingDataAndControl ())
            {
               tryAttackWidget ();
            }

            setEnabled (helper.ebsTable ().getEnabled ());

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebsTable ().getVisible ();
            if (visible || isShowing ())
               setVisible  (visible);

            // set the selected indices
            setSelectedIndices (helper.getSelectedIndices ());

            if (dndHandler == null)
            {
               if (helper.ebsTable ().isDroppable ())
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
                                 helper.ebsTable().getControl (),
                                 helper.ebsTable().evaName (""),
                                 dndFileTransHandler.arrALL_FIELDS
                                 );
                  setTransferHandler (dndHandler);
               }
            }
            else
            {
               // every time the control changes set it to the drag&drop handler
               dndHandler.setCommunicationLine (helper.ebsTable().getControl ());
            }
            break;

         case widgetConsts.RX_SELECT_DATA:
            //selecting elements
            //  do it silently (not producing a real selection)
            helper.doSelect (pars);
            SILENT_SELECTION = true;
            setSelectedIndices (helper.getSelectedIndices ());
            SILENT_SELECTION = false;
            break;


         default:
            return false;
      }

      return true;
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsTable().hasAll ())
      {
   		getSelectionModel ().clearSelection ();

         setModel (new dummySwingListModel());
         setModel (helper.getSwingListModel ());
      }
   }

   /**
      Note: JList could implement itself ListSelectionListener
            but we do it in the same way as we do in zTable and zAsiste
   */
   class meOigo implements ListSelectionListener
   {
      public void valueChanged (ListSelectionEvent e)
      {
         if (e.getValueIsAdjusting () || SILENT_SELECTION) return;

         if (helper == null) return;
         if (helper.ebsTable().getData () == null)
         {
            helper.log.err ("valueChanged", "value changed but no data!");
            return;
         }

         if (helper.storeAllSelectedIndices (getSelectedIndices ()))
         {
            setSelectionBackground (helper.ebsTable ().getSelNormalColor ().getAwtColor ());
         }
         else
         {
            setSelectionBackground (helper.ebsTable ().getSelOverflowColor ().getAwtColor ());
         }

         //(o) javaj_widgets_signals_zList Signal RowSelected (alias Action)
         //
         helper.signalAction ();
      }
   }
}
