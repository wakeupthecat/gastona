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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.Eva.layout.*;

import javaj.widgets.basics.*;
import javaj.widgets.table.*;
import javaj.widgets.panels.*;
import javaj.widgets.table.util.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (s) zAsiste

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zAsiste
   <groupInfo>  table
   <javaClass>  javaj.widgets.zAsiste
   <prefix>  s
   <importance> 8
   <desc>       //A database table with search, filter and sorting capabilities (rapid prefix 's')

   <help>

      //
      // An "Asiste Tabla" (assist table) is a database table or view that incorporates
      // the capabilities of searching, sorting and filtering for each column or a selection
      // of them. An edit field is placed over each column, the user can enter the filter or sort
      // criteria on it and when pressing enter a new query is performed and presented.
      //
      // We show the possible values for seach, order and filter with some examples
      //
      //    value of
      //    editfield   search result
      //    ----------- ---------------------------------------------
      //    casa        all records where this field contains "casa"
      //    casa mia    all records where this field contains "casa" and "mia" in any order
      //
      //    >B          order by this field and records greater or equal "B"
      //    <Z          reverse order by this field and records less or equal "Z"
      //    ]B          order by this field and records greater or equal "B" and less than "C"
      //    [B          reverse order by this field and records less or equal "B" and greater than "A"
      //
      //    )44         order by this field numerically and records biger or equal 44
      //    (44         reverse order by this field numerically and records lower or equal 44
      //
      //    '>          records containing ">" (e.g. searching <>[]()' characters at begining of text)
      //
      // The combination of more criteria is solved with AND operation, that means all records has to
      // comply all criterias.
      //
      // Widget characteristics: "Common", "Table select" and "Table from database".
      //

   <attributes>
     name             , in_out, possibleValues             , desc

     visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
     enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
     var              , in    , Eva name                   , //Reference to variable containing the data for this widget. If this atribute is given it will mask the data contained in the attribute "" (usually the data)

     visibleColumns   , in    , (Eva line)                 , //list of visible columns
     selected.COLNAME , out   , (String)                   , //contents of the column with name COLNAME of last selected row of the table
     subTableSelection, out   , (Eva table)                , //a table formed with the selected rows

     dbName           , in    , (file name)                , //If specified then it is the database name from which the data will be retrieved (default is the global temporary database)
     sqlSelect        , in    , (sqlite select or pragma)  , //If specified SQL query where the data will be retrieved (see dbName)
     sqlExtraFilter   , inout , (part of sqlite select)    , //Filter to be applied to the query

   <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control
                  , out , a row from the table has been selected (see also selected.COLNAME and subTableSelection attributes)
      2           , out , a row from the table has been double clicked (see also selected.COLNAME attribute)

<! (see  //(o) TODO_zWidgets_zTable drag & drop on table.)
<!      droppedFiles, out , If files drag & drop enabled this message indicates that the user has dropped files (see attribute 'droppedFiles')
<!      droppedDirs , out, If directories drag & drop is enabled this message indicates that the user has dropped directories (see attribute 'droppedDirs')


   <examples>
     gastSample

     assist basic example
     assist sample gastonaDoc
     assist sample gastonaDoc plus export

   <assist basic example>
      //#javaj#
      //
      //    <frames> F, Hello zAsisteTabla
      //
      //    <layout of F>
      //          PANEL, Y
      //          sAsiste, lSelectInfo
      //
      //#data#
      //
      //    <sAsiste sqlSelect> //SELECT * FROM someData
      //
      //#listix#
      //
      //    <main0>
      //       DATABASE,,CREATETABLE, someData
      //
      //    <someData>
      //          id , name,     city           , age
      //          718, Ricardo,  San Francisco  , 12
      //          321, Abelardo, Betanzos       , 45
      //          112, Joseba  , Bilbo          , 21
      //
      //    <-- sAsiste>
      //       -->, lSelectInfo data!,, //Last selected @<sAsiste selected.name> from @<sAsiste selected.city> is @<sAsiste selected.age> years old
      //
      //


   <assist sample gastonaDoc>
      //#javaj#
      //
      //   <frames> sAsiste, zAsiste example, 500, 300
      //
      //#listix#
      //
      //   <main0>
      //      @<copy gastona doc db>
      //      @<update asiste>
      //
      //   <copy gastona doc db>
      //      VAR=, tmp, @<:lsx tmp>
      //      RESUTIL, COPY, META-GASTONA/WelcomeGastona/genDB/gastonaDoc.db, @<tmp>
      //
      //   <update asiste>
      //      VAR=, sAsiste dbName, @<tmp>
      //      -->, sAsiste data!, sqlSelect, //SELECT name, desc FROM tGastItemDoc

   <assist sample gastonaDoc plus export>
      //#gastona#
      //
      //   <fusion>
      //       META-GASTONA/utilApp/std/ExportBasicDialog.gasti
      //
      //#javaj#
      //
      //   <frames> fMain, zAsiste example, 500, 300
      //
      //   <layout of fMain>
      //       EVA, 10, 10, 10, 10
      //
      //          ,  X
      //         X, sAsiste
      //          , layExportBasic
      //
      //#listix#
      //
      //   <main0>
      //      @<copy gastona doc db>
      //      @<update asiste>
      //
      //   <copy gastona doc db>
      //      VAR=, tmp, @<:lsx tmp>
      //      RESUTIL, COPY, META-GASTONA/WelcomeGastona/genDB/gastonaDoc.db, @<tmp>
      //
      //   <update asiste>
      //      VAR=, sAsiste dbName, @<tmp>
      //      -->, sAsiste data!, sqlSelect, //SELECT name, desc FROM tGastItemDoc
      //      LISTIX, ExportBasic set (gridName dbName), sAsiste, @<tmp>
      //



#**FIN_EVA#

*/


/**
   JPanel nativo que contiene:
        - una tabla (en un scrollPane)
        - una serie de campos asiste encima de las columnas correspondientes de la tabla
        - controla el modelo de la tabla via tableHand helper

   realiza:
         - posiciona los campos asiste encima de las columnas
           siguiendo los cambios de ancho y posicio'n
         - informa sobre cambios de ancho de una columna (mensaje standard)

   uso:
         setCamposAsiste (String [] columnName)


*/

/**
   zAsisteTabla : zWidget representing a GUI database table with search capabilities and more

   @see zWidgets
   @see javaHelper.gast

*/
public class zAsisteTabla extends JPanel
        implements MensakaTarget
                   , TableColumnModelListener
                   , ActionListener
                   , AdjustmentListener
{
   // real swing gui components of this Asiste panel
   //
   private JTable        theTable      = new JTable(2, 3);           // table
   private xyScrollPane  scrol         = new xyScrollPane (theTable);    // scroll pane
   private JTextField [] arrCampoAprox = new JTextField [0];         // text fields of asiste

   private boolean arrCampoAproxValid = false;  // for initialization

   private AbstractTableModel currentSwingModel = null;

   private asisteAparato helper = null;

   // intern EvaLayout manager used for the layout of the Asiste panel
   // note that this layout is independent from any javaj layouting and therefore
   // cannot be configured, it is here hardcoded.
   //
   private EvaLayout gaston = new EvaLayout();

   private int PositioningCallsCounter = 0;
   private boolean DoNotPositionNow = false;

   public zAsisteTabla ()
   {
      // default constructor to allow instantiation using <javaClass of...>
   }

//(o) TOREVIEW_changesLaptop No se d donde viene o porque este cambio, encontrado en copias de portatil
/*
 ??
   String constNMame = "";

   public zAsisteTabla (String map_name)
   {
     constNMame = map_name;
     SwingUtilities.invokeLater(puesBuild);
//      build (map_name);
   }

   Runnable puesBuild = new Runnable()
   {
      public void run()
      {
         build (constNMame);
      }
   };
*/
   public zAsisteTabla (String map_name)
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
      helper = new asisteAparato (this, new tableEBS (map_name, null, null));

      theTable.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

      // listening to selection and mouse double click
      //
      theTable.getSelectionModel ().addListSelectionListener (new meOigo());
      theTable.addMouseListener (new list2ClickListener (helper));
      theTable.getColumnModel ().addColumnModelListener (this);

      // decorate selection
      //
      theTable.setSelectionForeground (helper.ebsTable ().getSelFontColor ().getAwtColor ());
      theTable.setSelectionBackground (helper.ebsTable ().getSelNormalColor ().getAwtColor ());

      scrol.getHorizontalScrollBar().addAdjustmentListener (this);

      // theTable.setFocusTraversalKeysEnabled (false);
   }

   public Dimension getPreferredSize ()
   {
      return gaston.preferredLayoutSize(null);
   }

   public Dimension getMinimumSize ()
   {
      return gaston.minimumLayoutSize(null);
   }

   public JTable getJTable ()
   {
      return theTable;
   }

   public JTextField [] getJTextFieldArray ()
   {
      return arrCampoAprox;
   }

   /**
         <lay4asist>
            EvaLayout
               zzz, 5, 10, 5, 10, ..., X
                 A,  , asist0, , asist1, ...
                 X, scroll, -, -, - ...
   */
   private Eva prepareEvaLayout ()
   {
      // Container pane = this.getContentPane();
      Eva lay = new Eva("4asist");

      lay.setValueVar ("EvaLayout");
      lay.setValue ("zzz", 1, 0);
      lay.setValue ("A",  2, 0);
      lay.setValue ("X",   3, 0);

      for (int cc = 0; cc < arrCampoAprox.length; cc ++)
      {
         lay.setValue ("5",          1, 1 + 2 * cc);
         lay.setValue ("0",          1, 2 + 2 * cc);
         lay.setValue ("asist" + cc, 2, 2 + 2 * cc);

         lay.setValue ("-", 3, 1 + 2 * cc);
         lay.setValue ("-", 3, 2 + 2 * cc);
      }
      lay.setValue ("X", 1, 2 * arrCampoAprox.length + 1);
      lay.setValue ("-", 3, 2 * arrCampoAprox.length + 1);
      lay.setValue ("scroll", 3, 1);

      //System.out.println (lay);
      return lay;
   }

   private void fillPane()
   {
      gaston = new EvaLayout(prepareEvaLayout ());
      setLayout (gaston);

      while (getComponentCount() > 0)
         remove (0);

      // add to
      for (int cc = 0; cc < arrCampoAprox.length; cc ++)
         add ("asist" + cc, arrCampoAprox[cc]);

      add ("scroll", scrol);
   }

   private void setCamposAsiste (String [] columnName)
   {
      arrCampoAproxValid = false;
      arrCampoAprox = new JTextField [columnName.length];
      for (int ii = 0; ii < arrCampoAprox.length; ii ++)
      {
         arrCampoAprox[ii] = new JTextField(); // z
         arrCampoAprox[ii].setName(columnName[ii]);
         arrCampoAprox[ii].addActionListener (this);
      }
      arrCampoAproxValid = true;
   }

   public void setAsisteColumns (String [] columnName)
   {
      setCamposAsiste (columnName);
      fillPane();
      positioning ();
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

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebsTable ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);

            // set enable / disable
            //   Note: while setting visibility to the panel works it does not with setEnabled
            boolean isEnabled = helper.ebsTable ().getEnabled ();

            // go through all components in panel (but not hirarchically ..)
            Component [] allComp = getComponents ();
            for (int ii = 0; ii < allComp.length; ii ++)
               allComp[ii].setEnabled (isEnabled);

            // the table is contained in the scroll
            theTable.setEnabled (isEnabled);


            // set selected rows
            //
            int [] indices = helper.getSelectedIndices ();

            theTable.getSelectionModel ().setValueIsAdjusting(true);
            theTable.getSelectionModel ().clearSelection();
            for (int ii = 0; ii < indices.length; ii ++)
            {
               theTable.getSelectionModel ().addSelectionInterval(indices[ii], indices[ii]);
            }
            theTable.getSelectionModel ().setValueIsAdjusting(false);
            //

            break;

         default:
            return false;
      }

      return true;
   }

   private void tryAttackWidget ()
   {
      if ( ! helper.ebsTable().hasAll ()) return;

      // if we set the same object as model
      // JTable doesn't reset its data to the given model
      // but some data is cached. So we need this
      theTable.setModel (new dummySwingTableModel ());

      // Set the swing model into the native JTable
      //
      currentSwingModel = helper.getSwingTableModel ();
      theTable.setModel (currentSwingModel);

      // Reset selection
      //
      theTable.getSelectionModel ().clearSelection ();

      //(o) javaj_widgets Table columns width depending on the Font size
      //    Note if "getGraphics().getFontMetrics()...." were not enough fast
      //    we should use a member variable but the cuestion is when to update it
      //    since in constructor is not possible etc...
      //
      //
      //    int pixelsPerChar = getGraphics().getFontMetrics().stringWidth("W");
      //    utilMetadata.resizeTableColumns (helper, getColumnModel(), pixelsPerChar);
      //
      utilMetadata.resizeTableColumns (helper, theTable.getColumnModel());

      setCamposAsiste (helper.ebsTable ().getAsisteColumns ());
      fillPane();
      positioning ();
      
      if (isShowing ())
      {
         updateUI ();   // JTable::updateUI
      }
   }


   public void adjustmentValueChanged (AdjustmentEvent e)
   {
      positioning ();
   }

   //Tells listeners that a column was added to the model.
   public void columnAdded(TableColumnModelEvent e)
   {
      positioning();
   }

   // Tells listeners that a column was moved due to a margin change.
   public void columnMarginChanged(ChangeEvent e)
   {
      //(o) TODO_javaj_widgets IDEA to save changed column sizes from user
      //allowIt = true;
      positioning();
      //(o) TODO_javaj_widgets IDEA to save changed column sizes from user
      //allowIt = false;
   }

   //Tells listeners that a column was repositioned.
   public void columnMoved(TableColumnModelEvent e)
   {
      // continous event
      //System.out.println ("columnMoved");
   }

   //Tells listeners that a column was removed from the model.
   public void columnRemoved(TableColumnModelEvent e)
   {
      //System.out.println ("columnRemoved");
      positioning();
   }

   //Tells listeners that the selection model of the TableColumnModel changed.
   public void columnSelectionChanged(ListSelectionEvent e)
   {
      positioning();
   }

   public void actionPerformed(java.awt.event.ActionEvent ev)
   {
      //collect the edit fields but look for the right column (the user could have moved the columns)
      //
      String [] arrAsiste = new String [arrCampoAprox.length];
      int indx = 0;
      for (int ii = 0; ii < theTable.getColumnCount (); ii ++)
      {
         String colName = (String) theTable.getColumnName(ii);

         // we don't know the right order of the columns and where is the virtualCountColumn (if any)
         // so look for it by name
         for (int cc = 0; cc < arrCampoAprox.length; cc ++)
         {
            if (colName.equals(arrCampoAprox[cc].getName()))
            {
               //System.out.println ("tu te refieres a [" + colName + "] o que con un valuado de [" + arrCampoAprox[ii].getText() + "]");
               arrAsiste[cc] = arrCampoAprox[indx ++].getText();
            }
         }
      }

      helper.updateAcordingAsisteCampos (arrAsiste);

      // reseting the scroll shift !!!
      scrol.getVerticalScrollBar().getModel().setValue(0);

      if (currentSwingModel != null)
      {
         // needed mainly to update the "virtual counter coulumn" column 0
         // which column name change with the data
         //
         theTable.setModel (new dummySwingTableModel ());
         theTable.setModel (currentSwingModel);
      }

      utilMetadata.resizeTableColumns (helper, theTable.getColumnModel());
      theTable.repaint ();
   }

   //(o) TODO_javaj_widgets_zAsisteTabla IDEA to save changed column sizes from user
   //private boolean allowIt = false;

   private /*synchronized*/ void realPositioning ()
   {
      /* protect, at least here, from concurring before arrCampoAprox is ready
         because of the realPositioning is called from another thread
         NOTE1: it prevents from a couple of exceptions to be throw, but not completely!!, see try catch below
         NOTE2: making the method "synchronized" does not help AT ALL!
      */
      if (false == arrCampoAproxValid) return;

      int xPos = -scrol.getHorizontalScrollBar ().getValue ();
      //System.out.println ("pues hombre xPos me vale " + xPos);
      int colWidth = 0;
      int hueco = 0;
      int DY_FIELDS = 40;

      Eva lay = gaston.getEva ();
      int NAS = 0;

      for (int ii = 0; ii < theTable.getColumnCount (); ii ++)
      {
         //(o) TODO_javaj_widgets_zAsisteTabla review this try-catch, made to avoid a sporadic exception
         //       - the "for" loop takes care of theTable.getColumnCount, why then getColumnName throughs an exception ?
         //       - this error cannot be sistematically reproduced
         //       - if the exception occurs, in this case, it does not matter at all!
         //
         //java.lang.ArrayIndexOutOfBoundsException: 0 >= 0
         //        at java.util.Vector.elementAt(Vector.java:431)
         //        at javax.swing.JTable.DefaultTableColumnModel.getColumn(DefaultTableColumnModel.java:277)
         //        at javax.swing.JTable.convertColumnIndexToModel(JTable.java:1680)
         //        at javax.swing.JTable.getColumnName(JTable.java:1740)
         //        at javaj.widgets.zAsisteTabla.realPositioning(zAsisteTabla.java:366)
         //        at javaj.widgets.zAsisteTabla.access$100(zAsisteTabla.java:60)
         //        at javaj.widgets.zAsisteTabla$1.run(zAsisteTabla.java:396)
         //        ...
         try
         {
            colWidth = theTable.getColumnModel().getColumn (ii).getWidth ();
            int visibleWidth =  Math.min (colWidth, colWidth + xPos);

            // is in arrCampoAprox ?

            String colName = "";

            colName = (String) theTable.getColumnName(ii);

            //(o) TODO_javaj_widgets_zAsisteTabla IDEA to save changed column sizes from user
            //if (allowIt)
            //   utilMetadata.anotateUserShortLenCampo (colName, colWidth);
            boolean find = false;
            for (int cc = 0; cc < arrCampoAprox.length; cc ++)
            {
               if (colName.equals(arrCampoAprox[cc].getName()))
               {
                  lay.setValue ("" + hueco, 1, 1 + 2*NAS); // gap distance
                  if (visibleWidth > 0)
                       lay.setValue ("" + visibleWidth,  1, 2 + 2*NAS);
                  else lay.setValue ("" + 0,  1, 2 + 2*NAS);

                  NAS ++;
                  hueco = 0;
                  find = true;
                  break; // column found
               }
            }
            if (!find && visibleWidth > 0) hueco += visibleWidth;
            xPos += colWidth;
         }
         catch (Exception e)
         {
             // not really very interested in ..
             return;
         }
      }
      //System.out.println ("VERLAY " + lay);

      gaston.invalidatePreCalc ();
      
      if (isShowing ()) 
      {
         repaint ();
         updateUI ();
      }
   }

   Runnable puesPositioning = new Runnable()
   {
      public void run()
      {
         PositioningCallsCounter --;
         if (PositioningCallsCounter == 0)
              realPositioning();
         //else Mensaka.sendPacket ("+++ save one positioning!");
      }
   };

   // this call increments a counter and sets a later invocation
   // who will decrement the same counter and just execute the real
   // method on the last one (counter == 0)
   //
   private void positioning ()
   {
      if (! DoNotPositionNow)
      {
         PositioningCallsCounter ++;
         SwingUtilities.invokeLater(puesPositioning);
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
         if (helper.ebsTable().getData () == null)
         {
            helper.log.err ("valueChanged", "value changed but no data!");
            return;
         }

         if (helper.storeAllSelectedIndices (theTable.getSelectedRows ()))
         {
            theTable.setSelectionBackground (helper.ebsTable ().getSelNormalColor ().getAwtColor ());
         }
         else
         {
            theTable.setSelectionBackground (helper.ebsTable ().getSelOverflowColor ().getAwtColor ());
         }

         //(o) widget_signals_zTable Signal RowSelected (alias Action)
         //
         helper.signalAction ();
      }
   }
}
