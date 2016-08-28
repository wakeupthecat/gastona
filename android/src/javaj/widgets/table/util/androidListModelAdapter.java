/*
library de.elxala
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

package javaj.widgets.table.util;

import de.elxala.langutil.*;
import de.elxala.Eva.abstractTable.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import javaj.*;
import javaj.widgets.table.*;
import javaj.widgets.basics.widgetLogger;
import de.elxala.langutil.graph.sysFonts;

import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.graphics.drawable.*;

/*
   list model adapter, will provide the data to the native android widget ListView
*/
public class androidListModelAdapter implements ListAdapter
{
   private tableEvaDataEBS    tabletaReal;     // this extract really - in case of DB - the data from sql
   private tableWidgetBaseEBS tabletaVisible;  // this is needed because of visible columns

   private String [] visibleColArray = null;

   /**
      The model needs a real object capable of extracting records on demand (in case table is DB)
      but also it has to present the data acording with visibility of the columns. The real table
      is implemented with tableROSelect (which is a tableEvaDataEBS) but this class
      knows NOTHING about visible columns since this is a feature only interesting for a GUI component
      and it is implemented in javaj.widgets.table package. Therefore we need both tables. Of course
      not two independent tables but both has to share the same EBS!

      This is how it works: when information is needed about recordCount or values of registers
      the real table is used, and this updates the EBS at this point the virtual table is capable
      to acces the updated data through EBS.

   */
   public androidListModelAdapter (tableEvaDataEBS tableReal, tableWidgetBaseEBS tableVisible)
   {
      tabletaReal    = tableReal;
      tabletaVisible = tableVisible;

      visibleColArray = tabletaVisible.getVisibleColumns ();
   }


   // =======================================================
   // Implementation of android.widget.ListAdapter (derived from Adapter)
   // =======================================================
   public boolean areAllItemsEnabled()
   {
      return true;
   }

   public boolean isEnabled(int position)
   {
      return true;
   }

   // =======================================================
   // Implementation of android.widget.Adapter
   // =======================================================
   public int getCount()
   {
      // see note in swingListModelAdapter in the same method
      int size = tabletaReal.getRecordCount();

      //widgetLogger.log ().dbg (4, "androidListModelAdapter::getCount", "me askean count " + size);
      return size < 0 ? 0: size;
   }

   public Object getItem(int row)
   {
      // ensures that the data for row 'row' is present if not it is retrieved on demand
      // note the column index doesn't matter now
      //
      tabletaReal.getValue (row, 0);

      // Now the data of row 'row' is in EBS

      String value = "";

      for (int ii = 0; ii < visibleColArray.length; ii ++)
      {
         int realCol = tabletaVisible.getColumnIndex (visibleColArray[ii]);
         value += ((ii > 0) ? ", ": "") + tabletaVisible.getValue (row, realCol);
      }

      //widgetLogger.log ().dbg (4, "androidListModelAdapter::getItem", "me askean " + row + "[" + value + "]");
      return value;
   }

   public String getItemTextDetail(int row)
   {
      int indxCol = tabletaVisible.getColumnIndex ("textDetail");
      if (indxCol < 0) return null;

      return tabletaReal.getValue (row, indxCol);
   }

   public Drawable getDrawableByRow(int row)
   {
      int iconCol = tabletaVisible.getColumnIndex ("icon");
      if (iconCol < 0) return null;

      String pngName = tabletaReal.getValue (row, iconCol);
      if (pngName.length () == 0) return null;

      long start = System.currentTimeMillis ();
      Drawable bida = javaLoad.getSomeHowDrawable (pngName, true);
      long millis = System.currentTimeMillis () - start;
      if (bida != null)
      {
         widgetLogger.log().dbg (2, "getDrawableByRow", "loading icon " + pngName + " took " + millis + " ms");
      }
      else
      {
         widgetLogger.log().dbg (2, "getDrawableByRow", "loading icon " + pngName + " (not found) " + millis + " ms");
      }

      return bida;
   }

   public long getItemId (int position)
   {
      return position; // pa que la quiere .. ?
   }

   @Override
   public int getItemViewType (int position)
   {
      return 0; // a ver si le gusta ...
   }

   @Override
   public int getViewTypeCount ()
   {
       return 1;
   }

   static class loHolder // so called ViewHolder, not a view!
   {
      public TextView tTitle = null;
      public TextView tDetail = null;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent)
   {
      // reuse views
      // 9.4 View holder pattern
      // http://www.vogella.com/tutorials/AndroidListView/article.html
      //
      boolean hasDetail = getItemTextDetail(position) != null;

      if (convertView == null)
      {
         float txtSize = sysFonts.getStandardTextSizeInScaledPixels ();

         // create the holder
         loHolder holdi = new loHolder ();

         holdi.tTitle = new TextView (androidSysUtil.getCurrentActivity ());
         holdi.tTitle.setTextSize (txtSize);
         holdi.tTitle.setPadding (3, 3, 3, 3);
         //holdi.tTitle.setText ((String) getItem(position));
         //holdi.tTitle.setCompoundDrawablesWithIntrinsicBounds (getDrawableByRow(position), null, null, null);

         if (hasDetail)
         {
            holdi.tDetail = new TextView (androidSysUtil.getCurrentActivity ());
            holdi.tDetail.setTextSize (txtSize);
            holdi.tDetail.setPadding (3, 3, 3, 3);
            // holdi.tDetail.setText ((String) getItemTextDetail(position));
         }
         if (! hasDetail || tabletaVisible.getColumnIndex ("icon") >= 0)
         {
            // change size and aspect of title
            holdi.tTitle.setTextSize (1.3f * txtSize);
            //tTitle.setTypeface (android.graphics.Typeface.BOLD);
         }

         // creating and setting the layout
         LinearLayout lila = new LinearLayout(androidSysUtil.getCurrentActivity ());
         lila.setOrientation (LinearLayout.VERTICAL);

         //(o) issue/android/ListView/ugly effects scrolling
         //     also not complete selection in short entries
         //     giving these layout params does not help!, no difference with or without this
         //
         // lila.setLayoutParams (new android.widget.AbsListView.LayoutParams(android.widget.AbsListView.LayoutParams.FILL_PARENT, android.widget.AbsListView.LayoutParams.WRAP_CONTENT, 1));
         // holdi.tTitle.setLayoutParams (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

         lila.addView (holdi.tTitle);
         if (hasDetail) {

            //(o) issue/android/ListView/ugly effects scrolling
            // holdi.tDetail.setLayoutParams (new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            lila.addView (holdi.tDetail);
         }

         convertView = lila;

         // store the new holder
         convertView.setTag (holdi);
      }

      // get the holder
      loHolder holdi = (loHolder) convertView.getTag ();

      holdi.tTitle.setText ((String) getItem(position));
      holdi.tTitle.setCompoundDrawablesWithIntrinsicBounds (getDrawableByRow(position), null, null, null);

      if (holdi.tDetail != null)
      {
         String detailStr = getItemTextDetail(position);
         if (detailStr == null)  // actually not possible...
            detailStr = "";

         holdi.tDetail.setText (detailStr);
      }

      // try to fix visual issue with selecting items in the list (specially short ones)
      // with the suggestion in http://stackoverflow.com/a/5258168/573554
      // but it does not work!
      //convertView.setOnClickListener (
      //            new android.view.View.OnClickListener() {
      //               @Override
      //                  public void onClick(View view) {
      //                  }
      //            });
      return convertView;

////// INTENTO CON EVALAYOUT ....
////      Eva layamo = new Eva ();
////      layamo.addLine (new EvaLine ("EVALAYOUT, 3, 3, 2, 2"));
////      layamo.addLine (new EvaLine ("   ,     , X"));
////      layamo.addLine (new EvaLine ("   , mIma, xText1"));
////      layamo.addLine (new EvaLine (" 20,   + , xText2"));
////
////      View laeya = laying.laya_EvaLayout (androidSysUtil.getCurrentContext (), null, null, layamo);
////
////      EvaUnit euDada = new EvaUnit ();
////      Eva eva = new Eva ("xText1");
////      eva.addLine (new EvaLine (new String [] { (String) getItem(position) }));
////      euDada.add (eva);
////
////      eva = new Eva ("xText2");
////      eva.addLine (new EvaLine (new String [] { "un poc mas de roll mach\nla pos es "  + position }));
////      euDada.add (eva);
////
////      eva = new Eva ("mIma");
////      eva.addLine (new EvaLine (new String [] { "/data/data/org.gastona/files/clipboard.png" }));
////      euDada.add (eva);
////
////      Mensaka.sendPacket (javaj.javajEBS.msgCONTEXT_BASE, euDada);
////      return laeya;
////// INTENTO CON EVALAYOUT ....

   }

   public boolean hasStableIds()
   {
      return false;
   }

   public boolean isEmpty()
   {
      return getCount() == 0;
   }

   @Override
   public void registerDataSetObserver(android.database.DataSetObserver observer)
   {
      // hecho!
   }

   @Override
   public void unregisterDataSetObserver(android.database.DataSetObserver observer)
   {
      // o que pena, te vas!
   }
   // =======================================================


   /*
      Alternative for using ListModel using setListData ()

      ==========================================
      To get all elements in an array for lists (not abused!)
      It is the easiest way to implement a JList and JComboBox
      if not it is needed ListModel
      ==========================================
   */
   private static final int MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS = 1024;

   public String [] getAllRows () // (boolean header, boolean onlyVisible)
   {
      int totSize = getCount ();
      if (totSize > MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS)
      {
         widgetLogger.log().fatal ("swingListModelAdapter::getAllRows", "small List or ComboBox exceeded, the result might not be complete!");
         totSize = MAX_ALL_ELEMENTS_FOR_LISTS_AND_COMBOS;
      }

      String [] allE = new String [totSize];
      for (int ii = 0; ii < totSize; ii ++)
      {
         allE[ii] = (String) getItem(ii);
      }

      return allE;
   }
}
