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

package de.elxala.Eva.layout;

import java.util.Vector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import javaj.widgets.basics.izWidget;

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
         frame.setSize (new Dimensio (300, 200));
         frame.show();
      }
   }

   </pre>
*/

// ????? @RemoteView
public class EvaLayout extends ViewGroup
{
   public void detacha()
   {
      detachAllViewsFromParent();
   }
    public EvaLayout(Context context)
    {
        super(context);
        switchLayout(new Eva ());
    }

    public EvaLayout(Context context, Eva layoutInfo)
    {
        super(context);
        switchLayout(layoutInfo);
    }

    public EvaLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        switchLayout(new Eva ());
    }

    public EvaLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        switchLayout(new Eva ());
    }


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new EvaLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p)
    {
        return p instanceof EvaLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p)
    {
        return new LayoutParams(p);
    }


   public void addView(View child, String name)
   {
      //!!! Lástima! no funciona, nos vemos obligados a usar LayoutParams(String) ....
      //LayoutParams lp = new LayoutParams((LayoutParams) child.getLayoutParams(), name);

      //funciona
      //LayoutParams lp = new LayoutParams(name);

      //NOTE: if the component implements izWidget we can set the default width and height by
      //      using getDefaultWidth and getDefaultHeight
      //      Not doing that at this moment causes a wrong dimension of the component forever (!?)
      //      (at least, right now I don't know how to handle this)
      // That's why it is important that the component NOT only implements the methods getDefaultWidth and Height
      // BUT ALSO declares that implements izWidget! I forgot this twice and in both cases wasted time in finding
      // the problem, this is the reason for this warning

      if (! (child instanceof izWidget))
      {
         log.warn ("addView", "a component is not izWidget [" + child + "]");
      }

      LayoutParams lp = (child instanceof izWidget) ? new LayoutParams(name, ((izWidget)child).getDefaultWidth(), ((izWidget)child).getDefaultHeight()): new LayoutParams(name);
      addView(child, lp);
   }

   @Override
   public void addView(View child, int index)
   {
      log.err ("addView", "Improper method EvaLayout.addView used for index " + index + " !");
   }

   @Override
   public void addView(View child, int width, int height)
   {
      log.err ("addView", "Improper method EvaLayout.addView used width height!");
   }

   @Override
   public void addView(View child, ViewGroup.LayoutParams params)
   {
      super.addView (child, params);
      isPrecalculated = false;
   }

   @Override
   public void addView(View child, int index, ViewGroup.LayoutParams params)
   {
      super.addView (child, index, params);
      isPrecalculated = false;
   }

   @Override
   public void addView(View child)
   {
      log.err ("addView", "Improper method EvaLayout.addView used!");
   }

//// ? lo necesito ??
////     /**
////     * Returns a set of layout parameters with a width of
////     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
////     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
////     * and with the coordinates (0, 0).
////     */
////    @Override
////    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
////        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
////    }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
   {
      //log.dbg (2, "onMeasure", "widthMeasureSpec = " + MeasureSpec.toString(widthMeasureSpec));
      //log.dbg (2, "onMeasure", "heightMeasureSpec = " + MeasureSpec.toString(heightMeasureSpec));

      int specWidth =  MeasureSpec.getSize(widthMeasureSpec);
      int specHeight =  MeasureSpec.getSize(heightMeasureSpec);

      //log.dbg (2, "onMeasure", "widthMeasureSpec, height = " + widthMeasureSpec + ", " + heightMeasureSpec);

      int maxHeight = 0;
      int maxWidth = 0;

      // Find out how big everyone wants to be
      measureChildren(widthMeasureSpec, heightMeasureSpec);

      Dimensio mini = minimumLayoutSize(0, 0, specWidth, specHeight);
      Dimensio maxi = preferredLayoutSize(0, 0, specWidth, specHeight);

      // Check against minimum height and width
      maxHeight = Math.max(maxi.height, mini.height);
      maxWidth = Math.max(maxi.width, mini.width);

      setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
   }

   @Override
   protected void onLayout(boolean changed, int posX0, int posY0, int posX1, int posY1)
   {
      layoutContainer(posX0, posY0, posX1, posY1);
   }

   /**
   * Per-child layout information associated with AbsoluteLayout.
   * See
   * {@link android.R.styleable#AbsoluteLayout_Layout Absolute Layout Attributes}
   * for a list of all child view attributes that this class supports.
   */
   public static class LayoutParams extends ViewGroup.LayoutParams
   {
      public String  name;      // name of the component in the layout array
      public boolean isLaidOut = false; // if the component has been found in the layout array
                                // and therefore if indxPos is a valid calculated field

      // place in the layout array mesured in array indices
      public int posCol0 = 0;   // first column that the widget occupies
      public int posRow0 = 0;   // first row
      public int posCol1 = 0;   // last column
      public int posRow1 = 0;   // last row

      /**
      * Creates a new set of layout parameters with the specified name
      *
      * @param compName component name
      */
      public LayoutParams(String compName)
      {
         //---- super(140, 16);   // width height
         super(WRAP_CONTENT, WRAP_CONTENT);
         //super(MATCH_PARENT, MATCH_PARENT); // solo se consigue (en 1280x800) que los botones no tengan label!?
         name = compName;
      }

      public LayoutParams(String compName, int width, int height)
      {
         //super(width, height);   //
         super(WRAP_CONTENT, WRAP_CONTENT);
         //super(MATCH_PARENT, MATCH_PARENT); // solo se consigue (en 1280x800) que los botones no tengan label!?
         name = compName;
      }

      /**
      * ? implement this ? (to be xml capable)
      */
      public LayoutParams(Context c, AttributeSet attrs)
      {
         super(c, attrs);
      }

      public LayoutParams(int width, int height)
      {
         //super(width, height);
         super(WRAP_CONTENT, WRAP_CONTENT);
         //super(MATCH_PARENT, MATCH_PARENT); // solo se consigue (en 1280x800) que los botones no tengan label!?

         name = "";
      }

      /**
      * {@inheritDoc}
      */
      public LayoutParams(ViewGroup.LayoutParams source, String compName)
      {
         super(source);
         name = compName;
      }

      public LayoutParams(ViewGroup.LayoutParams source)
      {
         super(source);
      }

//      @Override
//      public String debug(String output)
//      {
//         return output + "EvaLayout.LayoutParams={name=" + name + "]";
//      }
   }

   public class Dimensio
   {
      public int width = 0;
      public int height = 0;

      public Dimensio (int w, int h)
      {
         width = w;
         height = h;
      }
   }


   private static logger log = new logger (null, "de.elxala.Eva.EvaLayout", null);

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

   Vector columnsReparto = new Vector ();
   Vector rowsReparto = new Vector ();

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

   private boolean checkLayoutInfo ()
   {
      return lay.getValue().toUpperCase().startsWith ("EVA");
   }

   private void precalculateAll ()
   {
      if (isPrecalculated) return;
      if (!checkLayoutInfo ())
      {
         log.err("checkLayoutInfo", "layout [" + lay.getName () + "] is not of type EVA");
         // do not return! some variables has to be initialized anyway
      }

      log.dbg (8, "precalculateAll", "layout " + lay.getName () + " perform precalculation.");

      Hmargin = Math.max (0, stdlib.atoi (lay.getValue (0,1)));
      Vmargin = Math.max (0, stdlib.atoi (lay.getValue (0,2)));
      Hgap    = Math.max (0, stdlib.atoi (lay.getValue (0,3)));
      Vgap    = Math.max (0, stdlib.atoi (lay.getValue (0,4)));

      log.dbg (8, "precalculateAll", nColumns() + " columns x " + nRows() + " rows");
      log.dbg (8, "precalculateAll", "margins xm=" + Hmargin + ", ym=" + Vmargin  + ", yg=" + Hgap  + ", yg=" + Vgap);

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
      int count = getChildCount();
      for (int i = 0; i < count; i++)
      {
         View child = getChildAt(i);
         // if (child.getVisibility() != GONE)
         EvaLayout.LayoutParams lp = (EvaLayout.LayoutParams) child.getLayoutParams();
         lp.isLaidOut = false;
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
            log.dbg (8, "precalculateAll", "Adaption... VdimPref[rr] = " + VdimPref[rr]);
         }
         else if (typ == HEADER_EXPAND)
         {
            rowsReparto.add (new int [] { rr });    // compute later
            log.dbg (8, "precalculateAll", "Expand... VdimPref[rr] = " + VdimPref[rr]);
         }
         else
         {
            // indicated size
            VdimPref[rr] = VdimMin[rr] = stdlib.atoi(heaRow);
            log.dbg (8, "precalculateAll", "Explicit... VdimPref[rr] = " + VdimPref[rr]);
         }

         Vpos[rr] = fijoV + gap;
         fijoV += VdimPref[rr];
         fijoV += gap;
      }
      fijoV += Vmargin;
      log.dbg (8, "precalculateAll", "fijoV = " + fijoV + " Vmargin = " + Vmargin + " Vgap = " + Vgap);

      //DEBUG ....
      if (log.isDebugging (8))
      {
         String vertical = "Vertical array (posY/prefHeight/minHeight)";
         for (int rr = 0; rr < Vpos.length; rr++)
            vertical += "  " + rr + ") " + Vpos[rr] + "/" + VdimPref[rr] + "/" + VdimMin[rr];

         log.dbg (8, "precalculateAll", vertical);
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
      log.dbg (8, "precalculateAll", "fijoH = " + fijoH);

      //DEBUG ....
      if (log.isDebugging (2))
      {
         String horizontal = "Horizontal array (posX/prefWidth/minWidth)";
         for (int cc = 0; cc < Hpos.length; cc++)
            horizontal += "  " + cc + ") " + Hpos[cc] + "/" + HdimPref[cc] + "/" + HdimMin[cc];

         log.dbg (8, "precalculateAll", horizontal);
      }

      // finding all components in the layout array
      for (int cc = 0; cc < nColumns(); cc ++)
      {
         for (int rr = 0; rr < nRows(); rr ++)
         {
            String name = widgetAt(rr, cc);

            LayoutParams wid = theComponent (name);
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
               log.dbg (8, "precalculateAll", wid.name + " leftTop (" + wid.posCol0 + ", "  + wid.posRow0 + ") rightBottom (" + wid.posCol1 + ", "  + wid.posRow1 + ")");
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

   public int getDefaultHeight ()
   {
      precalculateAll ();
      int suma = 0;

      for (int row=0; row < nRows(); row ++)
      {
         suma += minHeightOfRow (row, false);
      }
      return suma + Vmargin + Vmargin + Vgap * (nRows() - 1);
   }

   public int getDefaultWidth ()
   {
      precalculateAll ();
      int suma = 0;

      for (int col=0; col < nColumns(); col ++)
      {
         suma += minWidthOfColumn (col, false);
      }
      return suma + Hmargin + Hmargin + Hgap * (nColumns() - 1);
   }

   /**
    * Calculates the preferred size dimensions for the specified
    * panel given the components in the specified parent container.
    * @param parent the component to be laid out
    */
   public Dimensio preferredLayoutSize(int posX0, int posY0, int posX1, int posY1)
   {
       ///*EXPERIMENT!!!*/invalidatePreCalc ();
       Dimensio di = getLayoutSize(posX0, posY0, posX1, posY1, true);
       log.dbg (9, "preferredLayoutSize", lay.getName() + " preferredLayoutSize (" + di.width + ", " + di.height + ")");

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
   public Dimensio minimumLayoutSize(int posX0, int posY0, int posX1, int posY1)
   {
       Dimensio di = getLayoutSize(posX0, posY0, posX1, posY1, false);
       log.dbg (9, "minimumLayoutSize", lay.getName() + " minimumLayoutSize (" + di.width + ", " + di.height + ")");
       return di;
   }

   /**
    *calculating layout size (minimum or preferred).
    */
   protected Dimensio getLayoutSize(int posX0, int posY0, int posX1, int posY1, boolean isPreferred)
   {
      log.dbg (9, "getLayoutSize", lay.getName());
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
      for (int ii = 0; ii < getChildCount(); ii ++)
      {
         View vi = getChildAt(ii);
         LayoutParams lp = (EvaLayout.LayoutParams) vi.getLayoutParams();
         boolean someExpan = false;
         if (! lp.isLaidOut) continue;


//         log.dbg (2, "getLayoutSize",  lp.name + " mea (" + vi.getMeasuredWidth() + ", " + vi.getMeasuredHeight() + ")");
//         log.dbg (2, "getLayoutSize",  lp.name + " haz (" + vi.getWidth() + ", " + vi.getHeight() + ")");
//
//         int csizWidth  = (isPreferred) ? vi.getMeasuredWidth(): vi.getWidth();
//         int csizHeight = (isPreferred) ? vi.getMeasuredHeight(): vi.getHeight();

         int csizWidth  = (vi instanceof izWidget) ? ((izWidget)vi).getDefaultWidth(): 300;
         int csizHeight = (vi instanceof izWidget) ? ((izWidget)vi).getDefaultHeight(): 400;

         log.dbg (9, "getLayoutSize",  lp.name + " dim (" + csizWidth + ", " + csizHeight + ")");

         // some column expandable ?
         //
         someExpan = false;
         for (int cc = lp.posCol0; cc <= lp.posCol1; cc ++)
            if (headType(columnHeader(cc)) == HEADER_EXPAND)
            {
               someExpan = true;
               break;
            }

         if (someExpan)
         {
            // sum of all columns that this component occupy
            int sum = 0;
            for (int cc = lp.posCol0; cc <= lp.posCol1; cc ++)
               sum += (Hdim[cc] + extraCol[cc]);

            // distribute it in all columns to be salomonic
            int resto = csizWidth - sum;
            if (resto > 0)
            {
               if (lp.posCol0 == lp.posCol1)
               {
                  // System.err.println ("Resto X " + resto + " de " +  lp.name + " en la " + lp.posCol0 + " veniendo de csiz.width " + csiz.width + " y sum " + sum + " que repahartimos en " + (1 + lp.posCol1 - lp.posCol0) + " parates tenahamos una estra de " + extraCol[lp.posCol0]);
               }
               for (int cc = lp.posCol0; cc <= lp.posCol1; cc ++)
                  extraCol[cc] = resto / (1 + lp.posCol1 - lp.posCol0);
            }
         }


         // some row expandable ?
         //
         someExpan = false;
         for (int rr = lp.posRow0; rr <= lp.posRow1; rr ++)
            if (headType(rowHeader(rr)) == HEADER_EXPAND)
            {
               someExpan = true;
               break;
            }

         if (someExpan)
         {
            // sum of all height (rows) that this component occupy
            int sum = 0;
            for (int rr = lp.posRow0; rr <= lp.posRow1; rr ++)
               sum += (Vdim[rr] + extraRow[rr]);

            // distribute it in all columns to be salomonic
            int resto = csizHeight - sum;
            if (resto > 0)
            {
               for (int rr = lp.posRow0; rr <= lp.posRow1; rr ++)
                  extraRow[rr] = resto / (1 + lp.posRow1 - lp.posRow0);
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

      // Insets insets = (parent != null) ? parent.getInsets(): new Insets(0,0,0,0);
      tot_width  += Hgap * (nColumns() - 1) + 2 * Hmargin;
      tot_height += Vgap * (nRows() - 1)    + 2 * Vmargin;

      log.dbg (9, "getLayoutSize",  "returning tot_width " + tot_width + ", tot_height " + tot_height);
      // System.out.println ("getLayoutSize pref=" + isPreferred + " nos sale (" + tot_width + ", " + tot_height + ")");
      return new Dimensio (tot_width > 0 ? tot_width: 100, tot_height > 0 ? tot_height: 100);
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

   private LayoutParams theComponent(String cellName)
   {
      if (cellName.length() == 0 || cellName.equals(EXPAND_HORIZONTAL) || cellName.equals(EXPAND_VERTICAL)) return null;

      EvaLayout.LayoutParams lp;
      for (int ii = 0; ii < getChildCount(); ii ++)
      {
         lp = (EvaLayout.LayoutParams) getChildAt(ii).getLayoutParams();
         if (lp.name.equals (cellName)) return lp;
      }
      log.severe ("theComponent", "Component " + cellName + " not found in the container laying out " + lay.getName () + "!");
      return null;
   }

   private int minWidthOfColumn (int ncol, boolean preferred)
   {
      // el componente ma's ancho de la columna
      int maxwidth = 0;

      for (int ii = 0; ii < getChildCount(); ii ++)
      {
         View vi = getChildAt(ii);
         LayoutParams lp = (EvaLayout.LayoutParams) vi.getLayoutParams();

         // buscar al pavo en la columna ncol
         int row = 0;
         for (row = 0; row < nRows(); row ++)
         {
            String nam = widgetAt(row, ncol);
            if (nam.equals (lp.name))
               break;
         }
         if (row >= nRows()) continue;
         if (widgetAt (row, ncol+1).equals(EXPAND_HORIZONTAL)) continue;   // widget occupies more columns so even do not compute it

         int width = (vi instanceof izWidget) ? ((izWidget)vi).getDefaultWidth(): 300;
         maxwidth = Math.max (maxwidth, width);
      }
      return maxwidth > 200 ? 200 : maxwidth;
   }

   private int minHeightOfRow (int nrow, boolean preferred)
   {
      // el componente ma's alto de la columna
      int maxheight = 0;

      for (int ii = 0; ii < getChildCount(); ii ++)
      {
         View vi = getChildAt(ii);
         LayoutParams lp = (EvaLayout.LayoutParams) vi.getLayoutParams();

         // buscar al pavo en la row nrow
         int col = 0;
         for (col = 0; col < nColumns(); col ++)
         {
            String nam = widgetAt(nrow, col);
            if (nam.equals (lp.name))
               break;
         }
         if (col >= nColumns()) continue;
         if (widgetAt (nrow+1, col).equals(EXPAND_VERTICAL)) continue;   // widget occupies more rows so do not compute it

         int height = (vi instanceof izWidget) ? ((izWidget)vi).getDefaultHeight(): 400;
         maxheight = Math.max (maxheight, height);
      }
      return maxheight > 200 ? 200 : maxheight;
   }

   /**
    * Lays out the container in the specified container.
    * @param parent the component which needs to be laid out
    */
   public void layoutContainer(int posX0, int posY0, int posX1, int posY1)
   {
      //isPrecalculated = false;
      log.dbg (9, "layoutContainer", lay.getName ());

      precalculateAll ();

      //synchronized (parent.getTreeLock())
      {
         //if (log.isDebugging(4))
         //   log.dbg (4, "layoutContainer", "insets left right =" + insets.left + ", " + insets.right + " top bottom " + insets.top + ", " + insets.bottom);

         // Total parent dimensions
         int parDx = posX1 - posX0;
         int parDy = posY1 - posY0;

         log.dbg (9, "layoutContainer", "parent size =" + parDx + ", " + parDy);

         int repartH = parDx - fijoH;
         int repartV = parDy - fijoV;

         int [] HextraPos = new int [HdimPref.length];
         int [] VextraPos = new int [VdimPref.length];

         log.dbg (9, "layoutContainer", "repartH=" + repartH + " repartV=" + repartV);

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

         for (int co = 0; co < getChildCount(); co ++)
         {
            View vi = getChildAt(co);
            LayoutParams lp = (EvaLayout.LayoutParams) vi.getLayoutParams();

            log.dbg (9, "layoutContainer", "element [" + lp.name + "]");

            if (! lp.isLaidOut) continue;

            // System.out.println ("   indices " + lp.posCol0 + " (" + Hpos[lp.posCol0] + " extras " + HextraPos[lp.posCol0] + ")");

            int x = Hpos[lp.posCol0] + HextraPos[lp.posCol0];
            int y = Vpos[lp.posRow0] + VextraPos[lp.posRow0];
            int dx = 0;
            int dy = 0;

            for (int mm = lp.posCol0; mm <= lp.posCol1; mm ++)
            {
               if (mm != lp.posCol0) dx += Hgap;
               dx += HdimPref[mm];
            }
            for (int mm = lp.posRow0; mm <= lp.posRow1; mm ++)
            {
               if (mm != lp.posRow0) dy += Vgap;
               dy += VdimPref[mm];
            }

            if (x < 0 || y < 0 || dx < 0 || dy < 0)
            {
               //Disable this warning because it happens very often when minimizing the window etc
               //log.warn ("layoutContainer", "component not laid out! [" + lp.name + "] (" + x + ", " + y + ") (" + dx + ", " + dy + ")");
               continue;
            }

            //vi.setWidth (dx);
            //vi.setHeight (dy);
            setMeasuredDimension(dx, dy);
            vi.layout(x, y, x+dx, y+dy);
            log.dbg (9, "layoutContainer", "vi.name [" + lp.name + "] (" + x + ", " + y + ") (" + dx + ", " + dy + ")");
         }
       } // end synchronized
   }

////   // LayoutManager2 /////////////////////////////////////////////////////////
////
////   /**
////    * Returns the maximum size of this component.
////    */
////   public Dimensio maximumLayoutSize(Container target)
////   {
////       return new Dimensio(Integer.MAX_VALUE, Integer.MAX_VALUE);
////   }
////
////   /**
////    * Returns the alignment along the x axis.  This specifies how
////    * the component would like to be aligned relative to other
////    * components.  The value should be a number between 0 and 1
////    * where 0 represents alignment along the origin, 1 is aligned
////    * the furthest away from the origin, 0.5 is centered, etc.
////    */
////   public float getLayoutAlignmentX(Container target)
////   {
////       return 0.5f;
////   }
////
////   /**
////    * Returns the alignment along the y axis.  This specifies how
////    * the component would like to be aligned relative to other
////    * components.  The value should be a number between 0 and 1
////    * where 0 represents alignment along the origin, 1 is aligned
////    * the furthest away from the origin, 0.5 is centered, etc.
////    */
////   public float getLayoutAlignmentY(Container target)
////   {
////       return 0.5f;
////   }
////
////   /**
////    * Invalidates the layout, indicating that if the layout manager
////    * has cached information it should be discarded.
////    */
////   public void invalidateLayout(Container target)
////   {
////      invalidatePreCalc();
////   }

   public void invalidatePreCalc()
   {
       isPrecalculated = false;
   }
}
