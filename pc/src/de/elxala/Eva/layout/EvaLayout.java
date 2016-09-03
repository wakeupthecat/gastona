/*
package de.elxala.langutil
(c) Copyright 2006 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_javaj_layout EVALAYOUT

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       EVALAYOUT
   <groupInfo>
   <javaClass>  de.elxala.Eva.layout.EvaLayout
   <importance> 10
   <desc>       //A fexible grid layout

   <help>
      //
      //  Eva layout is the most powerful layout used by Javaj. The components are laid out in a grid
      //  and it is possible to define very accurately which cells of the grid occupy each one, which
      //  size has to have and if they are expandable or not.
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       EVALAYOUT, marginX, marginY, gapX, gapY
      //
      //       --grid--   , header col1, ...  , header colN
      //       header row1,  cell 1 1  , ...  , cell 1 N
      //       ...        ,  ...       , ...  , ...
      //       header rowM,  cell M 1  , ...  , cell M N
      //
      //  The first row starts with "EVALAYOUT" or simply "EVA", then optionally the following values
      //  in pixels for the whole grid
      //
      //      marginX  : horizontal margins for both left and right sides
      //      marginY  : vertical margins for both top and bottom areas
      //      gapX     : horizontal space between columns
      //      gapY     : vertical space between rows
      //
      //  The rest of rows defines a grid with arbitrary N columns and M rows, the minimum for both M
      //  and N is 1. To define a grid M x N we will need M+1 rows and a maximum of N+1 columns, this
      //  is because the header types given in rows and columns. These header values may be one of:
      //
      //       header
      //       value    meaning
      //      -------   --------
      //         A      Adapt (default). The row or column will adapt its size to the bigest default
      //                size of all components that start in this row or column.
      //         X      Expand. The row or column is expandable, when the the frame is resized all
      //                expandable row or columns will share the remaining space equally.
      //      a number  A fix size in pixels for the row or column
      //
      //  Note that the very first header ("--grid--") acctually does not correspond with a row or a
      //  column of the grid and therefore it is ignored by EvaLayout.
      //
      //  Finally the cells are used to place and expand the components. If we want the component to
      //  ocupy just one cell then we place it in that cell. If we want the component to ocupy more
      //  cells then we place the component on the left-top cell of the rectangle of cells to be
      //  ocupped, we fill the rest of top cells with "-" to the right and the rest of left cells with
      //  the symbol "+" to the bottom, the rest of cells (neither top or left cells) might be left in
      //  blank.
      //
      //   Example:
      //
      //         let's say we want to lay-out the following form
      //
      //         ----------------------------------------------
      //         |   label1  | field     ------->   | button1 |
      //         ----------------------------------------------
      //         |   label2  | text      ------->   | button2 |
      //         ------------|   |                  |----------
      //                     |   |                  | button3 |
      //                     |   V                  |----------
      //                     |                      |
      //                     ------------------------
      //
      //         representing this as grid of cells can be done at least in these two ways
      //
      //
      //                 Adapt     Adapt   Expand  Adapt                   Adapt     Expand   Adapt
      //               -------------------------------------             -----------------------------
      //        Adapt  | label1  | field |   -   | button1 |      Adapt  | label1  | field | button1 |
      //               |---------|-------|-------|---------|             |---------|-------|---------|
      //        Adapt  | label2  | text  |   -   | button2 |      Adapt  | label2  | text  | button2 |
      //               |---------|------ |-------|---------|             |---------|------ |---------|
      //        Adapt  |         |   +   |       | button3 |      Adapt  |         |   +   | button3 |
      //               |---------|------ |-------|---------|             |---------|------ |---------|
      //        Expand |         |   +   |       |         |      Expand |         |   +   |         |
      //               -------------------------------------             -----------------------------
      //
      //         the implementation of the second one as Evalayout would be
      //
      //             <layout of myFirstLayout>
      //
      //                EVALAYOUT
      //
      //               grid,    A     ,    X    ,    A
      //                  A, label1   , field   ,  button1
      //                  A, label2   , text    ,  button2
      //                  A,          ,   +     ,  button3
      //                  X,          ,   +     ,
      //
      //    NOTE: While columns and rows of type expandable or fixed size might be empty of components,
      //          this does not make sense for columns and rows of type adaptable (A), since in this
      //          case there is nothing to adapt. These columns or rows has to be avoided because
      //          might produce undefined results.
      //
<...>
      //    NOTE: EvaLayout is also available for C++ development with Windows Api and MFC,
      //         see as reference the article http://www.codeproject.com/KB/dialog/EvaLayout.aspx
      //

   <examples>
      gastSample

      eva layout example1
      eva layout example2
      eva layout example3
      eva layout complet
      eva layout centering
      eva layout percenting


   <eva layout example1>
      //#gastona#
      //
      //   <!PAINT LAYOUT>
      //
      //#javaj#
      //
      //   <frames>
      //      F, "example layout EVALAYOUT"
      //
      //   <layout of F>
      //
      //      EVALAYOUT, 10, 10, 5, 5
      //
      //      grid,          ,    X    ,
      //          , lLabel1  , eField1 ,  bButton1
      //          , lLabel2  , xText1  ,  bButton2
      //          ,          ,   +     ,  bButton3
      //        X ,          ,   +     ,

   <eva layout example2>
      //#gastona#
      //
      //   <!PAINT LAYOUT>
      //
      //#javaj#
      //
      //   <frames>
      //      F, "example 2 layout EVALAYOUT"
      //
      //   <layout of F>
      //
      //      EVA, 10, 10, 5, 5
      //
      //      --- ,    75    ,    X    ,    A       ,
      //          , bButton  , xMemo   ,    -       ,
      //          , bBoton   ,   +     ,            ,
      //          , bKnopf   ,   +     ,            ,
      //        X ,          ,   +     ,            ,
      //          , eCamp    ,   -     , bBotó maco ,


   <eva layout example3>
      //#gastona#
      //
      //   <!PAINT LAYOUT>
      //
      //#javaj#
      //
      //   <frames>
      //      F, "example 2 bis layout EVALAYOUT"
      //
      //   <layout of F>
      //      EVALAYOUT, 15, 15, 5, 5
      //
      //             ,          ,   X     ,
      //           50, bBoton1  , -       , -
      //             , bBoton4  , eField  , bBoton2
      //           X ,  +       , xText   ,  +
      //           50, bBoton3  ,     -   ,  +

   <eva layout complet>
      //#gastona#
      //
      //   <!PAINT LAYOUT>
      //
      //#javaj#
      //
      //   <frames>
      //      F, "example complex layout"
      //
      //   <layout of F>
      //     EVALAYOUT, 15, 15, 5, 5
      //
      //      ---,     80    ,    X     , 110
      //         , lLabel1   , eEdit1   , -
      //         , lLabel2   , cCombo   , lLabel3
      //         , lLabel4   , xMemo    , iLista
      //         , bBoton1   ,    +     , +
      //         , bBoton2   ,    +     , +
      //       X ,           ,    +     , +
      //         , layOwner  ,    -     , +
      //         ,           ,          , bBoton4
      //
      //   <layout of layOwner>
      //      PANEL, Y, Owner Info
      //
      //      LayFields
      //
      //   <layout of LayFields>
      //     EVALAYOUT, 5, 5, 5, 5
      //
      //      ---,         ,    X
      //         , lName   , eName
      //         , lPhone  , ePhone
      //

   <eva layout centering>
      //#gastona#
      //
      //   <PAINT LAYOUT>
      //
      //#javaj#
      //
      //    <frames>
      //       Fmain, Centering with EvaLayout demo
      //
      //    <layout of Fmain>
      //       EVA
      //
      //         , X,    A     , X
      //       X ,
      //       A ,  , bCentered
      //       X ,

   <eva layout percenting>
      //#gastona#
      //
      //   <!PAINT LAYOUT>
      //
      //#javaj#
      //
      //    <frames>
      //       Fmain, Percenting with EvaLayout demo, 300, 300
      //
      //    <layout of Fmain>
      //       EVA, 10, 10, 7, 7
      //
      //         , X    , X   , X     , X
      //       X , b11  , b13 , -     , -
      //       X , b22  , -   , b23   , -
      //       X ,  +   ,     , +     ,
      //       X , b12  , -   , +     ,
      //
      //#data#
      //
      //    <b11> 25% x 25%
      //    <b13> 75% horizontally 25% vertically
      //    <b22> fifty-fifty
      //    <b12> 50% x 25% y
      //    <b23> half and 3/4


#**FIN_EVA#

*/

package de.elxala.Eva.layout;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration; // to traverse the HashTable ...
import java.awt.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

import de.elxala.zServices.*;


/**
   @author    Alejandro Xalabarder
   @date      11.04.2006 22:32


   Example:
   <pre>

   import java.awt.*;
   import javax.swing.*;
   import de.elxala.Eva.*;
   import de.elxala.Eva.layout.*;

   public class sampleEvaLayout
   {
      public static void main (String [] aa)
      {
         JFrame frame = new JFrame ("sampleEvaLayout");
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

         Container pane = frame.getContentPane ();
         Eva lay = new Eva();

         // set margins
         lay.addLine (new EvaLine ("EvaLayout, 15, 15, 5, 5"));

         // set grid
         lay.addLine (new EvaLine ("RxC,        ,  100  ,  X ,"));
         lay.addLine (new EvaLine ("   , lname  , eName ,    ,"));
         lay.addLine (new EvaLine ("   , lobserv, xObs  , -  ,"));
         lay.addLine (new EvaLine ("  X,        ,   +   ,    ,"));

         pane.setLayout (new EvaLayout(lay));

         pane.add ("lname",   new JLabel("Name"));
         pane.add ("lobserv", new JLabel("Notes"));
         pane.add ("eName",   new JTextField());
         pane.add ("xObs",    new JTextPane());

         frame.pack();
         frame.setSize (new Dimension (300, 200));
         frame.show();
      }
   }

   </pre>
*/
public class EvaLayout implements LayoutManager2
{
   private static logger log = new logger (null, "de.elxala.Eva.EvaLayout", null);

   //19.10.2010 20:31
   // Note : Limits introduced in change (bildID) 11088 on 2010-09-20 01:47:25
   //        named "FIX problema en layout (LOW LEVEL BUG 2!)"
   //        But today the problem (without limits) cannot be reproduced! (?)
   // Limits set as aproximation, these can be reviewed and changed
   //
   private static int MAX_SIZE_DX = 10000;
   private static int MAX_SIZE_DY = 10000;

   protected static final int HEADER_EXPAND   = 10;
   protected static final int HEADER_ORIGINAL = 11;
   protected static final int HEADER_NUMERIC  = 12;

   protected static final String EXPAND_HORIZONTAL = "-";
   protected static final String EXPAND_VERTICAL = "+";

   // variables for pre-calculated layout
   //
   private boolean isPrecalculated = false;

   protected int Hmargin;
   protected int Vmargin;
   protected int Hgap;
   protected int Vgap;

   private int mnCols = -1;   // note : this is a cached value and might be call before precalculation!
   private int mnRows = -1;   // note : this is a cached value and might be call before precalculation!
   private int fijoH = 0;
   private int fijoV = 0;

   public int [] HdimMin  = new int [0];
   public int [] HdimPref = new int [0];  // for preferred instead of minimum
   public int [] Hpos = new int [0];
   public int [] VdimMin  = new int [0];
   public int [] VdimPref = new int [0];  // for preferred instead of minimum
   public int [] Vpos = new int [0];

   private Eva lay = null;
   private Hashtable componentHTable = new Hashtable();

   protected class widgetInfo
   {
      public widgetInfo (String nam, Component com)
      {
         name = nam;
         comp = com;
      }

      public String  name;      // name of the component in the layout array
      public Component comp;    // component info
      public boolean isLaidOut; // if the component has been found in the layout array
                                // and therefore if indxPos is a valid calculated field

      // place in the layout array mesured in array indices
      public int posCol0;   // first column that the widget occupies
      public int posRow0;   // first row
      public int posCol1;   // last column
      public int posRow1;   // last row
   }
   Vector columnsReparto = new Vector ();
   Vector rowsReparto = new Vector ();

   public EvaLayout()
   {
      this(new Eva());
   }

   public EvaLayout(Eva layarray)
   {
      log.dbg (2, "EvaLayout", "create EvaLayout " + layarray.getName ());
      lay = layarray;
   }

   /**
      Switches to another layout : note that the components used in this new layout
      has to exists (added to the layout using add method)
   */
   public void switchLayout(Eva layarray)
   {
      log.dbg (2, "switchLayout", "switch new layout info " + layarray.getName ());
      lay = layarray;
      invalidatePreCalc ();
   }

   private int headType (String str)
   {
      if (str.length () == 0 ||
          str.equalsIgnoreCase ("a")) return HEADER_ORIGINAL;
      if (str.equalsIgnoreCase ("x")) return HEADER_EXPAND;

      return HEADER_NUMERIC; // it should
   }

   private void precalculateAll ()
   {
      if (isPrecalculated) return;
      log.dbg (2, "precalculateAll", "layout " + lay.getName () + " perform precalculation.");

      Hmargin = Math.max (0, stdlib.atoi (lay.getValue (0,1)));
      Vmargin = Math.max (0, stdlib.atoi (lay.getValue (0,2)));
      Hgap    = Math.max (0, stdlib.atoi (lay.getValue (0,3)));
      Vgap    = Math.max (0, stdlib.atoi (lay.getValue (0,4)));

      log.dbg (4, "precalculateAll", nColumns() + " columns x " + nRows() + " rows");
      log.dbg (4, "precalculateAll", "margins xm=" + Hmargin + ", ym=" + Vmargin  + ", yg=" + Hgap  + ", yg=" + Vgap);

      mnCols = -1;   // reset cached number of cols
      mnRows = -1;   // reset cached number of rows

      HdimMin  = new int [nColumns()];
      HdimPref = new int [nColumns()];
      Hpos = new int [nColumns()];
      VdimMin  = new int [nRows()];
      VdimPref = new int [nRows()];
      Vpos = new int [nRows()];

      columnsReparto = new Vector ();
      rowsReparto = new Vector ();

      // for all components ...
      Enumeration enu = componentHTable.keys();
      while (enu.hasMoreElements())
      {
         String key = (String) enu.nextElement();
         ((widgetInfo) componentHTable.get(key)).isLaidOut = false;
      }

      // compute Vdim (Note: it might be precalculated if needed)
      fijoV = Vmargin;
      for (int rr = 0; rr < nRows(); rr ++)
      {
         String heaRow = rowHeader(rr);
         int typ = headType(heaRow);
         int gap = (rr == 0) ? 0: Vgap;

         if (typ == HEADER_ORIGINAL)
         {
            // maximum-minimum of the row
            VdimPref[rr] = minHeightOfRow(rr, true);
            VdimMin[rr]  = minHeightOfRow(rr, false);
            log.dbg (2, "precalculateAll", "Adaption... VdimPref[rr] = " + VdimPref[rr]);
         }
         else if (typ == HEADER_EXPAND)
         {
            rowsReparto.add (new int [] { rr });    // compute later
            log.dbg (2, "precalculateAll", "Expand... VdimPref[rr] = " + VdimPref[rr]);
         }
         else
         {
            // indicated size
            VdimPref[rr] = VdimMin[rr] = stdlib.atoi(heaRow);
            log.dbg (2, "precalculateAll", "Explicit... VdimPref[rr] = " + VdimPref[rr]);
         }

         Vpos[rr] = fijoV + gap;
         fijoV += VdimPref[rr];
         fijoV += gap;
      }
      fijoV += Vmargin;
      log.dbg (2, "precalculateAll", "fijoV = " + fijoV + " Vmargin = " + Vmargin + " Vgap = " + Vgap);

      //DEBUG ....
      if (log.isDebugging (2))
      {
         String vertical = "Vertical array (posY/prefHeight/minHeight)";
         for (int rr = 0; rr < Vpos.length; rr++)
            vertical += "  " + rr + ") " + Vpos[rr] + "/" + VdimPref[rr] + "/" + VdimMin[rr];

         log.dbg (2, "precalculateAll", vertical);
      }

      // compute Hdim (Note: it might be precalculated if needed)
      fijoH = Hmargin;
      for (int cc = 0; cc < nColumns(); cc ++)
      {
         String heaCol = columnHeader(cc);
         int typ = headType(heaCol);
         int gap = (cc == 0) ? 0: Hgap;

         if (typ == HEADER_ORIGINAL)
         {
            // maximum-minimum of the column
            HdimPref[cc] = minWidthOfColumn(cc, true);
            HdimMin[cc]  = minWidthOfColumn(cc, false);
         }
         else if (typ == HEADER_EXPAND)
            columnsReparto.add (new int [] { cc });  // compute later
         else
            HdimPref[cc] = HdimMin[cc] = stdlib.atoi(heaCol);          // indicated size

         Hpos[cc] = fijoH + gap;
         fijoH += HdimPref[cc];
         fijoH += gap;
      }
      fijoH += Hmargin;
      log.dbg (2, "precalculateAll", "fijoH = " + fijoH);

      //DEBUG ....
      if (log.isDebugging (2))
      {
         String horizontal = "Horizontal array (posX/prefWidth/minWidth)";
         for (int cc = 0; cc < Hpos.length; cc++)
            horizontal += "  " + cc + ") " + Hpos[cc] + "/" + HdimPref[cc] + "/" + HdimMin[cc];

         log.dbg (2, "precalculateAll", horizontal);
      }

      // finding all components in the layout array
      for (int cc = 0; cc < nColumns(); cc ++)
      {
         for (int rr = 0; rr < nRows(); rr ++)
         {
            String name = widgetAt(rr, cc);

            widgetInfo wid = theComponent (name);
            if (wid == null) continue;

            // set position x,y
            wid.posCol0 = cc;
            wid.posRow0 = rr;

            // set position x2,y2
            int ava = cc;
            while (ava+1 < nColumns() && widgetAt(rr, ava+1).equals (EXPAND_HORIZONTAL)) ava ++;
            wid.posCol1 = ava;

            ava = rr;
            while (ava+1 < nRows() && widgetAt(ava+1, cc).equals (EXPAND_VERTICAL)) ava ++;
            wid.posRow1 = ava;
            wid.isLaidOut = true;

            //DEBUG ....
            if (log.isDebugging (2))
            {
               log.dbg (2, "precalculateAll", wid.name + " leftTop (" + wid.posCol0 + ", "  + wid.posRow0 + ") rightBottom (" + wid.posCol1 + ", "  + wid.posRow1 + ")");
            }
         }
      }

      isPrecalculated = true;
   }


   protected int nColumns ()
   {
//      OLD
//      return Math.max(0, lay.cols(1) - 1);
      if (mnCols != -1) return mnCols;

      // has to be calculated,
      // the maximum n of cols of header or rows
      //
      for (int ii = 1; ii < lay.rows (); ii ++)
         if (mnCols < Math.max(0, lay.cols(ii) - 1))
            mnCols = Math.max(0, lay.cols(ii) - 1);

      mnCols = Math.max(0, mnCols);
      return mnCols;
   }

   protected int nRows ()
   {
//      OLD
//      return Math.max(0, lay.rows() - 2);

      if (mnRows != -1) return mnRows;

      mnRows = Math.max(0, lay.rows() - 2);
      return mnRows;
   }

   public Eva getEva ()
   {
      return lay;
   }

   public String [] getWidgets ()
   {
      java.util.Vector vecWidg = new java.util.Vector ();

      for (int rr = 0; rr < nRows(); rr ++)
         for (int cc = 0; cc < nColumns(); cc ++)
         {
            String name = widgetAt (rr, cc);
            if (name.length() > 0 && !name.equals(EXPAND_HORIZONTAL) && !name.equals(EXPAND_VERTICAL))
               vecWidg.add (name);
         }

      // pasarlo a array
      String [] arrWidg = new String [vecWidg.size ()];
      for (int ii = 0; ii < arrWidg.length; ii ++)
         arrWidg[ii] = (String) vecWidg.get (ii);

      return arrWidg;
   }


   /**
    * Adds the specified component with the specified name
    */
   public void addLayoutComponent(String name, Component comp)
   {
      log.dbg (2, "addLayoutComponent", name + " compName (" + comp.getName () + ")");
      componentHTable.put(name, new widgetInfo (name, comp));
      isPrecalculated = false;
   }

   /**
    * Removes the specified component from the layout.
    * @param comp the component to be removed
    */
   public void removeLayoutComponent(Component comp)
   {
       // componentHTable.remove(comp);
   }

   /**
    * Calculates the preferred size dimensions for the specified
    * panel given the components in the specified parent container.
    * @param parent the component to be laid out
    */
   public Dimension preferredLayoutSize(Container parent)
   {
       ///*EXPERIMENT!!!*/invalidatePreCalc ();
       Dimension di = getLayoutSize(parent, true);
       log.dbg (2, "preferredLayoutSize", lay.getName() + " preferredLayoutSize (" + di.width + ", " + di.height + ")");

       //(o) TODO_javaj_layingOut Problem: preferredLayoutSize
       //19.04.2009 19:50 Problem: preferredLayoutSize is called when pack and after that no more
       //                 layouts data dependent like slider (don't know if horizontal or vertical) have problems
       //                 Note: this is not just a problem of EvaLayout but of all layout managers
       //log.severe ("SEVERINIO!");

       return di;
   }

   /**
    * Calculates the minimum size dimensions for the specified
    * panel given the components in the specified parent container.
    * @param parent the component to be laid out
    */
   public Dimension minimumLayoutSize(Container parent)
   {
       Dimension di = getLayoutSize(parent, false);
       log.dbg (2, "minimumLayoutSize", lay.getName() + " minimumLayoutSize (" + di.width + ", " + di.height + ")");
       return di;
   }

   /**
    *calculating layout size (minimum or preferred).
    */
   protected Dimension getLayoutSize(Container parent, boolean isPreferred)
   {
      log.dbg (2, "getLayoutSize", lay.getName());
      precalculateAll ();

      // In precalculateAll the methods minWidthOfColumn and minHeightOfRow
      // does not evaluate expandable components since these might use other columns.
      // But in order to calculate the minimum or preferred total size we need this information.
      // In these cases we have to calculate following : if the sum of the sizes of the
      // columns that the component occupies is less that the minimum/preferred size of
      // the component then we add the difference to the total width
      // We evaluate this ONLY for those components that could be expanded!
      // for example
      //
      //    NO COMPONENT HAS TO          COMPONENTS comp1 and comp3
      //    BE RECALCULED                HAS TO BE RECALCULED
      //    ---------------------        ------------------------
      //    grid,   160  ,   A           grid,   160  ,   X
      //      A ,  comp1 ,   -             A ,  comp1 ,   -
      //      A ,  comp2 , comp3           X ,  comp2 , comp3
      //

      int [] extraCol = new int [nColumns ()];
      int [] extraRow = new int [nRows ()];

      int [] Hdim = isPreferred ? HdimPref: HdimMin;
      int [] Vdim = isPreferred ? VdimPref: VdimMin;

      //System.err.println ("PARLANT DE " + lay.getName () + " !!!");

      // for all components ...
      Enumeration enu = componentHTable.keys();
      while (enu.hasMoreElements())
      {
         boolean someExpan = false;
         String key = (String) enu.nextElement();
         widgetInfo wi = (widgetInfo) componentHTable.get(key);
         if (! wi.isLaidOut) continue;

         Dimension csiz = (isPreferred) ? wi.comp.getPreferredSize(): wi.comp.getMinimumSize();

         log.dbg (2, "getLayoutSize",  wi.name + " dim (" + csiz.width + ", " + csiz.height + ")");

         // some column expandable ?
         //
         someExpan = false;
         for (int cc = wi.posCol0; cc <= wi.posCol1; cc ++)
            if (headType(columnHeader(cc)) == HEADER_EXPAND)
            {
               someExpan = true;
               break;
            }

         if (someExpan)
         {
            // sum of all columns that this component occupy
            int sum = 0;
            for (int cc = wi.posCol0; cc <= wi.posCol1; cc ++)
               sum += (Hdim[cc] + extraCol[cc]);

            // distribute it in all columns to be salomonic
            int resto = csiz.width - sum;
            if (resto > 0)
            {
               if (wi.posCol0 == wi.posCol1)
               {
                  // System.err.println ("Resto X " + resto + " de " +  wi.name + " en la " + wi.posCol0 + " veniendo de csiz.width " + csiz.width + " y sum " + sum + " que repahartimos en " + (1 + wi.posCol1 - wi.posCol0) + " parates tenahamos una estra de " + extraCol[wi.posCol0]);
               }
               for (int cc = wi.posCol0; cc <= wi.posCol1; cc ++)
                  extraCol[cc] = resto / (1 + wi.posCol1 - wi.posCol0);
            }
         }


         // some row expandable ?
         //
         someExpan = false;
         for (int rr = wi.posRow0; rr <= wi.posRow1; rr ++)
            if (headType(rowHeader(rr)) == HEADER_EXPAND)
            {
               someExpan = true;
               break;
            }

         if (someExpan)
         {
            // sum of all height (rows) that this component occupy
            int sum = 0;
            for (int rr = wi.posRow0; rr <= wi.posRow1; rr ++)
               sum += (Vdim[rr] + extraRow[rr]);

            // distribute it in all columns to be salomonic
            int resto = csiz.height - sum;
            if (resto > 0)
            {
               for (int rr = wi.posRow0; rr <= wi.posRow1; rr ++)
                  extraRow[rr] = resto / (1 + wi.posRow1 - wi.posRow0);
            }
         }
      }

      int tot_width = 0;
      for (int cc = 0; cc < nColumns(); cc ++)
      {
         tot_width += (Hdim[cc] + extraCol[cc]);
      }

      int tot_height = 0;
      for (int rr = 0; rr < nRows(); rr ++)
      {
         tot_height += Vdim[rr] + extraRow[rr];
      }

      Insets insets = (parent != null) ? parent.getInsets(): new Insets(0,0,0,0);
      tot_width  += Hgap * (nColumns() - 1) + insets.left + insets.right + 2 * Hmargin;
      tot_height += Vgap * (nRows() - 1)    + insets.top + insets.bottom + 2 * Vmargin;

      log.dbg (2, "getLayoutSize",  "returning tot_width " + tot_width + ", tot_height " + tot_height);
      // System.out.println ("getLayoutSize pref=" + isPreferred + " nos sale (" + tot_width + ", " + tot_height + ")");
      return new Dimension (tot_width, tot_height);
   }

   private String columnHeader(int ncol)
   {
      return lay.getValue(1, ncol + 1).toUpperCase ();
   }

   private String rowHeader(int nrow)
   {
      return lay.getValue(2 + nrow, 0).toUpperCase ();
   }

   private String widgetAt(int nrow, int ncol)
   {
      return lay.getValue (nrow + 2, ncol + 1);
   }

   private widgetInfo theComponent(String cellName)
   {
      if (cellName.length() == 0 || cellName.equals(EXPAND_HORIZONTAL) || cellName.equals(EXPAND_VERTICAL)) return null;

      widgetInfo wi = (widgetInfo) componentHTable.get(cellName);
      if (wi == null)
         log.severe ("theComponent", "Component " + cellName + " not found in the container laying out " + lay.getName () + "!");
      return wi;
   }

   private int minWidthOfColumn (int ncol, boolean preferred)
   {
      // el componente ma's ancho de la columna
      int maxwidth = 0;
      for (int rr = 0; rr < nRows(); rr ++)
      {
         String name = widgetAt (rr, ncol);
         widgetInfo wi = theComponent (name);
         if (wi != null)
         {
            if (widgetAt (rr, ncol+1).equals(EXPAND_HORIZONTAL)) continue;   // widget occupies more columns so do not compute it
            Dimension csiz = (preferred) ? wi.comp.getPreferredSize(): wi.comp.getMinimumSize();
            maxwidth = Math.max (maxwidth, csiz.width);
         }
      }
      //19.09.2010 Workaround
      //in some cases, specially preferred size, can be too high (8000 etc) which make calculations fail!
      return maxwidth > MAX_SIZE_DX ? MAX_SIZE_DX : maxwidth;
   }

   private int minHeightOfRow (int nrow, boolean preferred)
   {
      // el componente ma's alto de la columna
      int maxheight = 0;
      for (int cc = 0; cc < nColumns(); cc ++)
      {
         String name = widgetAt (nrow, cc);
         widgetInfo wi = theComponent (name);
         if (wi != null)
         {
            if (widgetAt (nrow+1, cc).equals(EXPAND_VERTICAL)) continue;   // widget occupies more rows so do not compute it
            Dimension csiz = (preferred) ? wi.comp.getPreferredSize(): wi.comp.getMinimumSize();
            maxheight = Math.max (maxheight, csiz.height);
         }
      }
      //19.09.2010 Workaround
      //in some cases, specially preferred size, can be too high (8000 etc) which make calculations fail!
      return maxheight > MAX_SIZE_DY ? MAX_SIZE_DY : maxheight;
   }

   /**
    * Lays out the container in the specified container.
    * @param parent the component which needs to be laid out
    */
   public void layoutContainer(Container parent)
   {
      //isPrecalculated = false;
      if (log.isDebugging(4))
         log.dbg (4, "layoutContainer", lay.getName ());

      precalculateAll ();

      synchronized (parent.getTreeLock())
      {
         Insets insets = parent.getInsets();

         //if (log.isDebugging(4))
         //   log.dbg (4, "layoutContainer", "insets left right =" + insets.left + ", " + insets.right + " top bottom " + insets.top + ", " + insets.bottom);

         // Total parent dimensions
         Dimension size = parent.getSize();

         if (log.isDebugging(4))
            log.dbg (4, "layoutContainer", "parent size =" + size.width + ", " + size.height);

         int repartH = size.width  - (insets.left + insets.right) - fijoH;
         int repartV = size.height - (insets.top + insets.bottom) - fijoV;

         int [] HextraPos = new int [HdimPref.length];
         int [] VextraPos = new int [VdimPref.length];

         if (log.isDebugging(4))
            log.dbg (4, "layoutContainer", "repartH=" + repartH + " repartV=" + repartV);

         // repartir H
         if (columnsReparto.size() > 0)
         {
            repartH /= columnsReparto.size();
            for (int ii = 0; ii < columnsReparto.size(); ii ++)
            {
               int indx = ((int []) columnsReparto.get (ii))[0];
               HdimPref[indx] = repartH;
               for (int res = indx+1; res < nColumns(); res ++)
                  HextraPos[res] += repartH;
            }
         }

         // repartir V
         if (rowsReparto.size() > 0)
         {
            repartV /= rowsReparto.size();
            for (int ii = 0; ii < rowsReparto.size(); ii ++)
            {
               int indx = ((int []) rowsReparto.get (ii))[0];
               VdimPref[indx] = repartV;
               for (int res = indx+1; res < nRows(); res ++)
                  VextraPos[res] += repartV;
            }
         }

         //
         // for all components ... for (int ii = 0; ii < componentArray.size(); ii ++)
         //
         java.util.Enumeration enu = componentHTable.keys();
         while (enu.hasMoreElements())
         {
            String key = (String) enu.nextElement();
            widgetInfo wi = (widgetInfo) componentHTable.get(key);

            if (log.isDebugging(4))
               log.dbg (4, "layoutContainer", "element [" + key + "]");
            // System.out.println ("componente " + wi.name);
            if (! wi.isLaidOut) continue;

            // System.out.println ("   indices " + wi.posCol0 + " (" + Hpos[wi.posCol0] + " extras " + HextraPos[wi.posCol0] + ")");

            int x = Hpos[wi.posCol0] + HextraPos[wi.posCol0];
            int y = Vpos[wi.posRow0] + VextraPos[wi.posRow0];
            int dx = 0;
            int dy = 0;

            //if (log.isDebugging(4))
            //    log.dbg (4, "SIGUEY", "1) y = " + y + " Vpos[wi.posRow0] = " + Vpos[wi.posRow0] + " VextraPos[wi.posRow0] = " + VextraPos[wi.posRow0]);

            for (int mm = wi.posCol0; mm <= wi.posCol1; mm ++)
            {
               if (mm != wi.posCol0) dx += Hgap;
               dx += HdimPref[mm];
            }
            for (int mm = wi.posRow0; mm <= wi.posRow1; mm ++)
            {
               if (mm != wi.posRow0) dy += Vgap;
               dy += VdimPref[mm];
            }

            if (x < 0 || y < 0 || dx < 0 || dy < 0)
            {
               //Disable this warning because it happens very often when minimizing the window etc
               //log.warn ("layoutContainer", "component not laid out! [" + wi.name + "] (" + x + ", " + y + ") (" + dx + ", " + dy + ")");
               continue;
            }

            wi.comp.setBounds(x, y, dx, dy);
            if (log.isDebugging(4))
               log.dbg (4, "layoutContainer", "vi.name [" + wi.name + "] (" + x + ", " + y + ") (" + dx + ", " + dy + ")");
         }
       } // end synchronized
   }

   // LayoutManager2 /////////////////////////////////////////////////////////

   /**
    * This method make no sense in this layout, the constraints are not per component
    * but per column and rows. Since this method is called when adding components through
    * the method add(String, Component) we implement it as if constraints were the name
    */
   public void addLayoutComponent(Component comp, Object constraints)
   {
      addLayoutComponent((String) constraints, comp);
   }

   /**
    * Returns the maximum size of this component.
    */
   public Dimension maximumLayoutSize(Container target)
   {
       return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   /**
    * Returns the alignment along the x axis.  This specifies how
    * the component would like to be aligned relative to other
    * components.  The value should be a number between 0 and 1
    * where 0 represents alignment along the origin, 1 is aligned
    * the furthest away from the origin, 0.5 is centered, etc.
    */
   public float getLayoutAlignmentX(Container target)
   {
       return 0.5f;
   }

   /**
    * Returns the alignment along the y axis.  This specifies how
    * the component would like to be aligned relative to other
    * components.  The value should be a number between 0 and 1
    * where 0 represents alignment along the origin, 1 is aligned
    * the furthest away from the origin, 0.5 is centered, etc.
    */
   public float getLayoutAlignmentY(Container target)
   {
       return 0.5f;
   }

   /**
    * Invalidates the layout, indicating that if the layout manager
    * has cached information it should be discarded.
    */
   public void invalidateLayout(Container target)
   {
      invalidatePreCalc();
   }

   public void invalidatePreCalc()
   {
       isPrecalculated = false;
   }
}
