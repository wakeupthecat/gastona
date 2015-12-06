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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import javaj.widgets.basics.izWidget;

import de.elxala.langutil.*;
import de.elxala.Eva.abstractTable.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;
import javaj.*;
import javaj.widgets.table.*;
import javaj.widgets.basics.widgetLogger;
import de.elxala.langutil.graph.sysFonts;

import android.content.Context;
import android.view.*;
import android.widget.*;
import android.graphics.drawable.*;

/**
*/
public class androidSpinnerModelAdapter extends androidListModelAdapter implements SpinnerAdapter
{
   //private Context mico = null;
   //public androidSpinnerModelAdapter (Context tucontext, tableEvaDataEBS tableReal, tableWidgetBaseEBS tableVisible)
   public androidSpinnerModelAdapter (tableEvaDataEBS tableReal, tableWidgetBaseEBS tableVisible)
   {
      super (tableReal, tableVisible);
      //mico = tucontext;
   }

   public View getDropDownView(int position, View convertView, ViewGroup parent)
   {
      return getView(position, convertView, parent);
//      TextView bu = new TextView (mico);
//      bu.setText ("\\/");
//      //return new TextView (getContext ());
//      return bu;
   }
}
