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

/*
   //(o) WelcomeGastona_source_javaj_widgets (t) zTable

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zTable
   <groupInfo>  table
   <javaClass>  javaj.widgets.zTable
   <importance> 7
   <desc>       //A table from memory (Eva) or database (rapid prefix 't')

   <help>
      //
      // Table which data can be given as a Table in memory (Eva table) or can be retrieved from a
      // database using a sql Select query (see "Table select" and "Table from database" widget
      // characteristics).
      //
      // In this widget the select query might be given without limits since an automatic mechanism
      // called "windowing" takes care of retrieving the data in small blocks. For example you can
      // query a database table containing millions of registers just with "SELECT * FROM theTable;".
      //
      // For facilitating the data exploration to the user it is recommended to use zAsiste instead
      // of zTable.
      //
      // Widget characteristics: "Common", "Table select" and "Table from database".
      //

   <prefix> t

   <attributes>
      name             , in_out, possibleValues           , desc

                       , in    , Eva table                , //Contents of the table or cache in case of db table
      var              , in    , Eva name                 , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

      visible          , in    , 0 / 1                    , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                    , //Value 0 to disable the widget

<! //(o) TODO_zWidgets_zTable visibility in tables of the virtual column record count
<!     hideCountColumn , in    , 0 / 1                      , //If 1 the virtual column (record count) will be hidden
<!    NOTE: hideCountColumn is partially implemented
<!             javaj\widgets\table\tableEBS.java ::hasVirtualRecordCountColumn ()
<!             javaj\widgets\table\ util\swingTableModelAdapter.java  parameter of constructor withVirtualCounterColumn
<!          but if set to false (hide true) then the auto-resize of the columns does not match!
<!          It has to be found the cause and fix it before document it as attribute

      visibleColumns   , in    , Eva line                 , //list of visible columns
      dbName           , in    , file name                , //If specified then it is the database name from which the data will be retrieved (default is the global temporary database)
      sqlSelect        , in    , sqlite select or pragma  , //If specified then the data will be retrieved from a database (see dbName)
      sqlExtraFilter   , in    , part of sqlite select    , //Filter that is added to the query
      subTableSelection, out   , Eva table                , //a table formed with the selected rows
      selected.COLNAME  , out   , String                   , //Contents of the column with name COLNAME of first selected row of the table. There is one variable select.COLNAME for each column in the table (inclusive the not visible columns)

<! (see  //(o) TODO_zWidgets_zTable drag & drop on table.)
<!     droppedFiles     , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped files. Note that this attribute has to exist in order to enable drag & dropping files into this Component.
<!     droppedDirs      , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped directories. Note that this attribute has to exist in order to enable drag & dropping files into this Component.

   <messages>

      msg      , in_out, desc
      data!    , in    , update data
      control! , in    , update control
      select!  , in    , //select records. The first parameter is the column name or # or #1 to select by index, Example "MSG, widget select!, #, 2, 4" select the records 2 and 4 (0 based) and "MSG, widget select!, name, Barcelona" selects the first record which name is "Barcelona"
         , out   , a row or more rows has been selected
      2  , out   , a row from the table has been double clicked (see also selected.COLNAME attribute)

<! (see  //(o) TODO_zWidgets_zTable drag & drop on table.)
<!      droppedFiles, out , If files drag & drop enabled this message indicates that the user has dropped files (see attribute 'droppedFiles')
<!      droppedDirs , out, If directories drag & drop is enabled this message indicates that the user has dropped directories (see attribute 'droppedDirs')


   <examples>
      gastSample
      hello zTable
      hello zTable select
      hello zTable select2
      zTable database
<! (see  //(o) TODO_zWidgets_zTable drag & drop on table.)
<!      table drop files
      tabla de multiplicar

   <hello zTable>
      //#javaj#
      //
      //    <frames> tTabla, Hello zTable
      //
      //#data#
      //
      //    <tTabla>
      //       id, name, telephone
      //
      //       12, Michael,  5563
      //       56, Evariste, 1811
      //      231, Alea,     2112
      //      108, Carl,     3222
      //

   <hello zTable select>
      //#javaj#
      //
      //    <frames> tTabla, Hello zTable select
      //
      //#data#
      //
      //    <tTabla>
      //       id, name, telephone
      //
      //       12, Michael,  5563
      //       56, Evariste, 1811
      //      231, Alea,     2112
      //      108, Carl,     3222
      //
      //#listix#
      //
      //    <-- tTabla>
      //       //
      //       //
      //       //First selected "@<tTabla selected.name>"
      //       //All selected row(s) :
      //       //
      //       LOOP, EVA, tTabla subTableSelection
      //          ,, //    @<name> (id = @<id>)

   <hello zTable select2>
      //#javaj#
      //
      //    <frames> F, Hello zTable select 2  , 400
      //
      //    <layout of F>
      //          EVA, 5, 5, 3, 3
      //
      //             , X   , X
      //             , lMain table, -
      //           X , tTabla, -
      //             , lSelected elements table, -
      //           X , tTablaSel, -
      //             , bPre sel1, bPre sel2
      //
      //#data#
      //
      //    <tTabla>
      //       id, name, telephone
      //
      //       12, Michael,  5563
      //       56, Evariste, 1811
      //      231, Alea,     2112
      //      108, Carl,     3222
      //
      //    <tTablaSel var> tTabla subTableSelection
      //
      //#listix#
      //
      //    <-- tTabla>
      //       @<update table selection>
      //
      //    <-- bPre sel1>
      //       MSG, tTabla select!, #1, 1, 3
      //       @<update table selection>
      //
      //    <-- bPre sel2>
      //       MSG, tTabla select!, "name", Evariste, Alea, Carl
      //       @<update table selection>
      //
      //    <update table selection>
      //       MSG, tTablaSel data!



   <zTable database>
      //#javaj#
      //
      //    <frames> F, zTable database demo
      //
      //    <layout of F>
      //          EVA, 10, 10, 4, 4
      //          - , X
      //          A , lSelected
      //          X , tTabla
      //
      //#data#
      //
      //    <tTabla sqlSelect> //SELECT * FROM someData;
      //
      //    <someData>
      //          id , name,     city           , age
      //          718, Ricardo,  San Francisco  , 12
      //          321, Abelardo, Betanzos       , 45
      //          112, Joseba  , Bilbo          , 21
      //
      //#listix#
      //
      //    <main0>
      //       DATABASE,,CREATETABLE, someData
      //
      //    <-- tTabla>
      //       -->, lSelected data!,, @<tTabla selected.name> lives in @<tTabla selected.city>
      //

<! (see  //(o) TODO_zWidgets_zTable drag & drop on table.)
<!   <table drop files>
<!      //#javaj#
<!      //
<!      //    <frames> tTable, Drop files onto the table
<!      //
<!      //#data#
<!      //
<!      //    <tTable droppedFiles>
<!      //
<!      //#listix#
<!      //
<!      //    <-- tTable droppedFiles>
<!      //       VAR=, tTable, Path, Name, Extension, Date, Size
<!      //       LOOP, EVA, tTable droppedFiles
<!      //           ,, VAR+, tTable, @<pathFile>, @<fileName>, @<extension>, @<date>, @<size>
<!      //

   <tabla de multiplicar>
      //#javaj#
      //
      //   <frames>
      //      F, Tablas de multiplicar
      //
      //   <layout of F>
      //      EVA, 10, 10, 4, 4
      //
      //      ---,   A
      //         , digits
      //        X, tMultTable
      //
      //   <layout of digits>
      //      EVA, 4, 4, 2, 2
      //
      //      ---,
      //         , b1, b2, b3,
      //         , b4, b5, b6,
      //         , b7, b8, b9
      //
      //#listix#
      //
      //   <main0>  listix, calculaTabla, 1
      //
      //   <-- b1>  listix, calculaTabla, 1
      //   <-- b2>  listix, calculaTabla, 2
      //   <-- b3>  listix, calculaTabla, 3
      //   <-- b4>  listix, calculaTabla, 4
      //   <-- b5>  listix, calculaTabla, 5
      //   <-- b6>  listix, calculaTabla, 6
      //   <-- b7>  listix, calculaTabla, 7
      //   <-- b8>  listix, calculaTabla, 8
      //   <-- b9>  listix, calculaTabla, 9
      //
      //   <calculaTabla>
      //
      //      SET VAR, tMultTable, @<p1> x ..., "", " "
      //      LOOP, FOR, ii, 1, 9
      //          ,, @<setvalue>
      //      MSG, tMultTable data!
      //
      //   <setvalue>    ADD TO VAR, tMultTable, "@<p1> x @<ii>", "=", @<calculaVal>
      //   <calculaVal>  =, p1 * ii
      //


#**FIN_EVA#

*/

package javaj.widgets;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.Dimension;

import de.elxala.langutil.*;
import de.elxala.mensaka.*;

import javaj.widgets.basics.*;
import javaj.widgets.table.*;
import javaj.widgets.table.util.*;
import javax.swing.event.ListSelectionListener;
import de.elxala.Eva.*;
//import javaj.widgets.kits.dndFileTransHandler;

/**
   Handles the native zJTable in a javaj way but is assisted in all
   by a helper of type zJTableEBS and uses zSelectionListener as well

   26.02.2006 21:16  Make it more idiot, now he has no idea about the TableModel he receives
                     he just pass it to JTable

*/
public class zTable extends JTable
                    implements MensakaTarget
                 //<<onColumChange>>            ,   TableColumnModelListener
{
   private tableAparato helper = null;

   private AbstractTableModel currentSwingModel = null;
//   private dndFileTransHandler dndHandler = null;

   public zTable ()
   {
      super (2, 3);  // por poner algo ...
      // default constructor to allow instantiation using <javaClass of...>
   }

//(o) TOREVIEW_changesLaptop No se d donde viene o porque este cambio, encontrado en copias de portatil
/*
  String constNMame = "";


   public zTable (String map_name)
   {
      super (2, 3);  // por poner algo ...
      constNMame = map_name;

      build (constNMame);
      // listening to selection and mouse double click
      //
//      getSelectionModel ().addListSelectionListener (new meOigo());
//      addMouseListener (new list2ClickListener (helper));

      // decorate selection
      //
//      setSelectionForeground (helper.ebsTable ().getSelFontColor ().getAwtColor ());
//      setSelectionBackground (helper.ebsTable ().getSelNormalColor ().getAwtColor ());

//      SwingUtilities.invokeLater(puesBuild);
   }

   // needed to avoid following exception
   //     java.lang.NullPointerException
   //         at javax.swing.border.EmptyBorder.<init>(EmptyBorder.java:54)
   Runnable puesBuild = new Runnable()
   {
      public void run()
      {
         System.out.println ("me llaman el cabron de manchester!");
         build (constNMame);
         //setVisible (true);
      }
   };
*/
   public zTable (String map_name)
   {
      super (2, 3);  // por poner algo ...
      build (map_name);
   }

   public void setName (String map_name)
   {
      build (map_name);
   }

   public void build (String map_name)
   {
      //(o) javaj_widgets NOTA: si damos un preferred size parece que
      //                        posteriormente NO se puede redimensionar!! p.m. java
      //                        (va envuelto en un JScroll con setPreferred * 1.2 ver onFlyWidgetSolver)
      //setPreferredSize(new Dimension (200, 160));

      super.setName (map_name);
      helper = new tableAparato (this, new tableEBS (map_name, null, null));

      // adjust JTable
      //
      setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

      // listening to selection and mouse double click
      //
      getSelectionModel ().addListSelectionListener (new meOigo());
      addMouseListener (new list2ClickListener (helper));

      // decorate selection
      //
      setSelectionForeground (helper.ebsTable ().getSelFontColor ().getAwtColor ());
      setSelectionBackground (helper.ebsTable ().getSelNormalColor ().getAwtColor ());
      //<<onColumChange>> getColumnModel ().addColumnModelListener (this);
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
            if (visible && isShowing ())
               setVisible  (visible);

            reflectSelectedIndexesOnWidget ();
            //

             //(o) TODO_zWidgets_zTable drag & drop on table. Behaviour of dropping into JTable not ok.
             //    If we enable this only will be possible to drop into a table row (uninteresting)
             //    and unlike JList IS NOT POSSIBLE TO drop onto an empty table (unacceptable)
//            if (dndHandler == null)
//            {
//               if (helper.ebsTable ().isDroppable ())
//               {
//                  // made it "dropable capable"
//
//                  // drag & drop ability
//                  //
//                  /**
//                      Make the zWidget to drag'n'drop of files or directories capable
//                      Note that the zWidget is not subscribed to the drag'n'drop message itself ("%name% droppedFiles" or ..droppedDirs")
//                      therefore it will not take any action on this event. It is a task of a controller to
//                      examine, accept, process and insert into the widget the files if desired and convenient
//
//                      Note that at this point the control for the zWidget (helper.ebs().getControl ())
//                      is null and we have to update it into the handler when it chanhges
//                  */
//                  dndHandler = new dndFileTransHandler (
//                                 helper.ebsTable().getControl (),
//                                 helper.ebsTable().evaName (""),
//                                 dndFileTransHandler.arrALL_FIELDS
//                                 );
//                  setTransferHandler (dndHandler);
//               }
//            }
//            else
//            {
//               // every time the control changes set it to the drag&drop handler
//               dndHandler.setCommunicationLine (helper.ebsTable().getControl ());
//            }
            break;

         case widgetConsts.RX_SELECT_DATA:
            helper.doSelect (pars);
            reflectSelectedIndexesOnWidget ();
            break;

         default:
            helper.log.severe ("takePacket", "message mappedID " + mappedID + " not handled!");
            return false;
      }

      return true;
   }

   private void tryAttackWidget ()
   {
      if (helper.ebsTable().hasAll ())
      {
         updateData ();
      }
   }

   private void reflectSelectedIndexesOnWidget ()
   {
      //selecting elements
      //  do it silently (not producing a real selection)
      //
      int [] indices = helper.getSelectedIndices ();

      getSelectionModel ().setValueIsAdjusting(true);
      getSelectionModel ().clearSelection();
      for (int ii = 0; ii < indices.length; ii ++)
      {
         getSelectionModel ().addSelectionInterval(indices[ii], indices[ii]);
      }
      getSelectionModel ().setValueIsAdjusting(false);
   }


   private void updateData ()
   {
      if (!helper.ebsTable().hasAll ()) return;

//=================== TIENE QUE FUNCIONAR!
//      if (currentSwingModel != null)
//      {
//         // If the table was not null means that some table
//         // has been shown to the user, then store his column size preferences!
//         // much better than listening to TableColumnModelListener
//         transmitUserColumnWidths ();
//      }
//=================== TIENE QUE FUNCIONAR!

      // if we set the same object as model
      // JTable doesn't reset its data to the given model
      // but some data is cached. So we need this
      setModel (new dummySwingTableModel ());

      // Set the swing model into the native JTable
      //
      currentSwingModel = helper.getSwingTableModel ();
      setModel (currentSwingModel);

      if (helper.log.isDebugging (2))
         helper.log.dbg (2, "updateData", "model with " + currentSwingModel.getColumnCount() + " columns and " + currentSwingModel.getRowCount() + " rows");

      // in theory "this" as TableModelListener should implement "tableChanged(TableModelEvent e)"
      // and I'm not sure if it does.
      // Anyway this notification is for changes in cells of table that at the moment we do not allow
      currentSwingModel.addTableModelListener (this);

      // Reset selection
      //
      getSelectionModel ().clearSelection ();

      //(o) javaj_widgets Table columns width depending on the Font size
      //    Note if "getGraphics().getFontMetrics()...." were not fast enough
      //    we should use a member variable but the cuestion is when to update it
      //    because in constructor is not possible etc...
      //
      //
      //    int pixelsPerChar = getGraphics().getFontMetrics().stringWidth("W");
      //    utilMetadata.resizeTableColumns (helper, getColumnModel(), pixelsPerChar);
      //
      utilMetadata.resizeTableColumns (helper, getColumnModel());

      helper.log.dbg (2, "updateData", "call updateUI()");
      updateUI ();   // JTable::updateUI
   }

   private void transmitUserColumnWidths ()
   {
      for (int ii = 0; ii < getColumnCount (); ii ++)
      {
         TableColumn col = getColumnModel().getColumn (ii);
         String colName = helper.ebsTable().getColumnName (ii);

         widgetLogger.log().dbg (2, "tTable::transmitUserColumnWidths", "colName " + colName + " width " + col.getWidth ());
         helper.anotateUserShortLenCampo (ii, col.getWidth ());
      }
   }

   /**
      Intern class to listen to selection. This seems to be needed in JTable
      because if the JTable implements ListSelectionListener directly it makes
      strange selections and the method is called twice (why?)
   */
   class meOigo implements ListSelectionListener
   {
      public void valueChanged (ListSelectionEvent e)
      {
         if (e.getValueIsAdjusting ()) return;
         if (helper == null) return;

         helper.log.dbg (2, "valueChanged", "");
         if (helper.ebsTable().getData () == null)
         {
            helper.log.err ("valueChanged", "value changed but no data!");
            return;
         }

         if (helper.storeAllSelectedIndices (getSelectedRows ()))
         {
            helper.log.dbg (2, "valueChanged", "normal selection color");
            setSelectionBackground (helper.ebsTable ().getSelNormalColor ().getAwtColor ());
         }
         else
         {
            helper.log.dbg (2, "valueChanged", "selection overflow color");
            setSelectionBackground (helper.ebsTable ().getSelOverflowColor ().getAwtColor ());
         }

         //(o) widget_signals_zTable Signal RowSelected (alias Action)
         //
         helper.log.dbg (2, "valueChanged", "signal action");
         helper.signalAction ();
      }
   }
}

