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

package javaj.widgets.table.util;

import javax.swing.table.*;
import de.elxala.langutil.*;
import de.elxala.Eva.*;
import javaj.widgets.table.*;

import de.elxala.zServices.*;

/**
   util metadata ------------------------------

   //
   //   ===================================
   //       METADATA
   //
   //    basically need the column size of a field
   //    in order to resize the column in the table
   //
   //    Procedure:
   //
   //       A table has to procure this information
   //       asking for that to a supposed service
   //       "_service FieldsMetaData"
   //       this service receives as input a unit with
   //       the eva "metadatafields". For example
   //       <metadatafields>
   //             id
   //             name
   //             date
   //
   //       then the service should answer something like
   //
   //       <metadatafields>
   //             id   , 10, 10
   //             name , 40, 30
   //             date , 20, 20
   //
   //       where the first length is the length of the filed as for editing it etc.
   //       and the second (if specified) the short length for a column for this field
   //       in a report or in a table
*/
public class utilMetadata
{
   private static logger log = new logger (null, "javaj.widgets.table.util.utilMetadata", null);

   private static final float FACTOR_CHAR = 1.3f;
   private static final int   MIN_WIDTH_COLUMN_0 = (int) (4 * FACTOR_CHAR);

   private static final int   PIXELS_FOR_A_CHAR = 6; // should be Font dependent ...
   private static final int   COL_META_SHORTLEN = 1;
   private static final int   COL_META_STAT_N   = 2;
   private static final int   COL_META_STAT_EX  = 3;
   private static final int   COL_META_ISNUMERIC = 4;

   private static Eva  eMetadata = new Eva ("metadata");

   public static void addMetaData (Eva meta)
   {
      if (meta == null) return;
      log.dbg (2, "addMetaData", "received eva with " + meta.rows () + " elements");
      for (int ii = 0; ii < meta.rows (); ii ++)
      {
         // copy the row if new
         if (eMetadata.rowOf (meta.getValue (ii, 0)) == -1)
         {
            log.dbg (2, "addMetaData", "add columnn element [" + meta.getValue (ii, 0) + "]");
            eMetadata.addLine (meta.get(ii));
         }
      }
   }

   public static void anotateUserShortLenCampo (String columnName, int width)
   {
      int indx = eMetadata.rowOf (columnName);

      // don't have it in metadata ? add it
      if (indx == -1)
      {
         eMetadata.addRow (columnName);
         indx = eMetadata.rowOf (columnName);
      }

      // store the with
      //System.out.println ("utilMetadata::anotate " + width + " for " + columnName);
      log.dbg (2, "anotateUserShortLenCampo", "anotate user width of " + width + " for column [" + columnName + "]");
      eMetadata.setValue ("" + width, indx, COL_META_SHORTLEN );
   }

   // public method related with metadata
   //
   public static int getShortLenCampo (Eva ebsTable, int fieldIndx, String nameColumn)
   {
      int MIN_COLUMN_WIDTH = 4;
      int MAX_COLUMN_WIDTH = 60;
      float CIPHER_FACTOR = 1.3f;

      int DEFAULT_WIDTH = (int) (20 * FACTOR_CHAR);
      int SHARP_WIDTH   = (int) (7 * FACTOR_CHAR);

      int ENOUGH_STAT = 20;
      int ESTIMATE_N_RECORDS = 22;

      int width = DEFAULT_WIDTH;

      log.dbg (2, "getShortLenCampo", "getShortLenCampo ebsTable " + ebsTable.getName () + ", fieldIndx " + fieldIndx + ", nameColumn [" + nameColumn + "]");
      if (eMetadata == null)
      {
         log.dbg (2, "getShortLenCampo", "return default width " + width);
         return (width);
      }

      // it is a normal field ...

      int indx = eMetadata.rowOf (nameColumn);
      if (indx == -1)
      {
         //System.out.println ("utilMetadata::getShortLenCampo add " + nameColumn + " to metadata");
         // not have it ? then add it
         log.dbg (2, "getShortLenCampo", "new column name, add it");
         eMetadata.addRow (nameColumn);
         indx = eMetadata.rows () - 1;
      }

      int fixedValue = stdlib.atoi (eMetadata.getValue (indx, COL_META_SHORTLEN));
      int statN      = stdlib.atoi (eMetadata.getValue (indx, COL_META_STAT_N));
      int statEx     = stdlib.atoi (eMetadata.getValue (indx, COL_META_STAT_EX));

      log.dbg (2, "getShortLenCampo", "fixedValue " + fixedValue + ", statN " + statN + ", statEx " + statEx);
      if (fixedValue > 0)
      {
         log.dbg (2, "getShortLenCampo", "return fixedValue " + fixedValue);
         return fixedValue;
      }

      // not found so estimate it and make statistics about it ...

      if (statN > ENOUGH_STAT)
      {
         // enough data
         log.dbg (2, "getShortLenCampo", "enough data return statistic width " + statEx / statN);
         return statEx / statN;
      }

      log.dbg (2, "getShortLenCampo", "procede with \"simple euristic estimation\" of 20 elements");


      //(o) javaj_zWidgets_mecanics Automatic resize of columns computing the first 20 records
      //
      // If no column width for is found for the field then
      // an estimation taking into account the 20 first records is used.
      // At the moment this estimation is not stored

      // IMPORTANT! DO NOT USE THE REAL TABLE
      // We are not interested in the real 20 first records but in any 20 records

      // introspeccion en los 20 primeros datos
      int minimo = Math.max (MIN_COLUMN_WIDTH, nameColumn.length ());
      boolean isNumeric = ebsTable.rows () > 1; // set to true per default
      for (int ii = 1; ii < ebsTable.rows () && ii <= ESTIMATE_N_RECORDS; ii ++)
      {
         String strVal = (String) ebsTable.getValue (ii, fieldIndx);
         int lval = strVal.length ();

         // detect if numeric (only if all numerics), if yes add a factor because ciphers has more width than alfa
         if (isNumeric && (strVal.equals("0") || stdlib.atoi (strVal) != 0))
              lval *= CIPHER_FACTOR;
         else isNumeric = false;

         if (lval > minimo)
            minimo = lval;

      }

      width = (minimo < MAX_COLUMN_WIDTH) ? minimo: MAX_COLUMN_WIDTH;
      width *= FACTOR_CHAR;

      log.dbg (2, "getShortLenCampo", "minimo = " + minimo + " estimated width (20 samples) " + width);

      //
      statN ++;
      statEx += width;

      // store the the statistic
      eMetadata.setValue ("" + statN,  indx, COL_META_STAT_N);
      eMetadata.setValue ("" + statEx, indx, COL_META_STAT_EX);
      eMetadata.setValue ("" + (isNumeric ? "1": "0"), indx, COL_META_ISNUMERIC);

      //System.out.println ("utilMetadata::getShortLenCampo return calculated mean value " + statEx / statN + " for " + nameColumn);
      log.dbg (2, "getShortLenCampo", "new statistic width = " + statEx / statN);
      return statEx / statN;
   }

   /**
      Note: better evaluate this function always after getShortLenCampo
   */
   public static boolean getIsNumeric (String nameColumn)
   {
      int indx = eMetadata.rowOf (nameColumn);
      if (indx == -1)
      {
         return false; // actually, don't know
      }

      return "1".equals (eMetadata.getValue (indx, COL_META_ISNUMERIC));
   }

   public static void resizeTableColumns (tableAparato helper, TableColumnModel colMod)
   {
      resizeTableColumns (helper, colMod, PIXELS_FOR_A_CHAR);
   }

   public static void resizeTableColumns (tableAparato helper, TableColumnModel colMod, int pixels4Char)
   {
      // resize the columns with available metadata
      //
      String name = "";
      int minusColumn = (helper.hasVirtualCountColumn () ? 1:0);

      for (int ii = 0; ii < colMod.getColumnCount (); ii ++)
      {
         int width = 0;

         if (minusColumn == 1 && ii == 0)
         {
            String srec = (String) colMod.getColumn (0).getHeaderValue ();
            width = Math.max ((int) (FACTOR_CHAR * (1+srec.length())), MIN_WIDTH_COLUMN_0); // 1+ since usually the counter need more width
         }
         else
            width = helper.getShortLenCampo (ii - minusColumn);

         colMod.getColumn (ii).setPreferredWidth (pixels4Char * width);
         colMod.getColumn (ii).setWidth (pixels4Char * width);
      }
   }
}
