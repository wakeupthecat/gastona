/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005-2026  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.Eva;
import java.util.Vector;
import java.util.List;

import de.elxala.langutil.*;

/**
   class EvaLine
   @author Alejandro Xalabarder Aulet
   @date   2001

   This class describes and handles the called structure EvaLine (see EvaFormat.PDF)

   An EvaLine object is basically an array of strings and can be constructed from a single string
   (comma separated strings).

   <pre>
   Example:

         EvaLine elin = new EvaLine (" Peter, 444 yyy zz, Stuttgart  ");

         System.out.println ("# of columns = " + elin.cols ());
         System.out.println ("value of col 1 = [" + elin.getValue (1) + "]");
         System.out.println ("EvaLine toString : [" + elin + "]");
   </pre>


   02.02.2005 12:00 EVA Text File Specification v1.0
*/
public class EvaLine implements java.io.Serializable
{
   protected final static char ATOM_ENVOLVER = 34;  // "
   protected final static char ATOM_SEPARATOR = 44;    // ,

   private static final String START_LITERAL_1 = "'";
   private static final String START_LITERAL_2 = "//";
   private static final char START_LITERAL_1_CH = '\'';
   private static final char START_LITERAL_2_CH = '/';

   private String [] arr_Column = null;

   /**
      Constructs a EvaLine object from a String array
   */
   public EvaLine (String [] arrData)
   {
      if (arrData == null || arrData.length == 0)
      {
         // evaline contains always 1 element! (see specification)
         arr_Column = new String [] {""};
         return;
      }
      arr_Column = arrData;
   }

   public EvaLine ()
   {
      arr_Column = new String [] {""};
   }

   /**
      Constructs a EvaLine object from another EvaLine object
   */
   public EvaLine (EvaLine eLin)
   {
      set (eLin);
   }

   /**
      Constructs a EvaLine object from String 'sLin' where the columns are comma separated
      Example:

      EvaLine elin = new EvaLin ("Peter, 444 yyy zz, Stuttgart");
   */
   public EvaLine (String sLin)
   {
      parseLine (sLin);
   }


   public void clear ()
   {
      arr_Column = new String [] {""};
   }

   public void copy (EvaLine eline2)
   {
      arr_Column = new String [] {""};
      if (eline2 == null) return;

      arr_Column = new String [eline2.arr_Column.length];
      for (int ii = 0; ii < arr_Column.length; ii ++)
      {
         arr_Column[ii] = eline2.arr_Column[ii];
      }
   }

   /**
      return the EvaLin as String array
   */
   public String [] getColumnArray ()
   {
      return arr_Column;
   }

   /**
      Load the line from a String array
   */
   public void set (String [] aLin)
   {
      arr_Column = aLin;
   }

   /**
      Copies a EvaLin
   */
   public void set (EvaLine eLin)
   {
      arr_Column = new String [eLin.arr_Column.length];

      for (int ii = 0; ii < arr_Column.length; ii ++)
         arr_Column[ii] = eLin.arr_Column[ii];
   }

   /**
      sets the value 'sVal' at column 'ncol' if the column already exists

      returns true is the set is done
   */
   public boolean set (String sVal, int ncol)
   {
      if (ncol < cols () && ncol >= 0) {
         arr_Column [ncol] = sVal;
         return true;
      }
      else return false;
   }

   /**
      Sets the value on the column specified. If the line has no
      such column then it will be resized!.

      @param sVal Value to set
      @param ncol Number of the column to set the value
   */
   public void setValue (String sVal, int ncol)
   {
      if (ncol < 0) return;

      // inserting columns
      if (ncol >= cols ())
      {
         String[] newLine = new String [ncol+1];
         for (int ii = 0; ii < ncol+1; ii++)
            newLine[ii] = (ii < cols ()) ? arr_Column [ii]: "";
         arr_Column = newLine;
      }
      arr_Column [ncol] = (sVal != null) ? sVal: "";
   }

   /**
      Returns the String at position 'ncol' or null if the column does not exist
   */
   public String get (int ncol)
   {
      if (ncol < cols () && ncol >= 0)
         return arr_Column [ncol];
      else return null;
   }

   /**
      Returns the String at position 'ncol' or "" (empty string) if the column does not exist
      NOTE: it returns ALWAYS a String
   */
   public String getValue (int ncol)
   {
      if (ncol < cols () && ncol >= 0)
         return arr_Column [ncol];
      else return "";
   }

   /**
      Returns the double at position 'ncol' or 0.f if the column does not exist or has no numeric value
   */
   public float getFloat (int ncol)
   {
      return (float) stdlib.atof (getValue (ncol));
   }

   /**
      Returns the double at position 'ncol' or 0.f if the column does not exist or has no numeric value
   */
   public double getDouble (int ncol)
   {
      return stdlib.atof (getValue (ncol));
   }

   /**
      Returns the int at position 'ncol' or 0 if the column does not exist or has no numeric value
   */
   public int getInt (int ncol)
   {
      return stdlib.atoi (getValue (ncol));
   }

   /**
      removes 'nelem' elements from offset 'offset'
   */
   public void removeElements (int nelem, int offset)
   {
      if (offset >= cols () || offset < 0) return;
      if (nelem + offset > cols ())
         nelem = cols () - offset;

      String [] nline = new String [cols () - nelem];

      // copy until offset-1
      for (int cc = 0; cc < offset; cc ++)
         nline[cc] = getValue (cc);

      // copy from offset + nelements
      for (int cc = offset + nelem; cc < cols (); cc ++)
         nline[cc-nelem] = getValue (cc);

      set (nline);
   }

   /**
      removes the first 'nelem' elements
   */
   public void removeElements (int nelem)
   {
      removeElements (nelem, 0);
   }

   /**
      removes the first 'nelem' elements
   */
   public void removeColumn (int nColumn)
   {
      removeElements (1, nColumn);
   }

   /**
      Returns the size (number of columns)
   */
   public int cols ()
   {
      return arr_Column.length;
   }

   /**
      Serialize the whole line in a String (comma separated, see EvaFormat.PDF)
   */
   public String toString ()
   {
      // more effective way, the whole line is a string
      if (arr_Column.length == 1)
      {
         // we print out the escape column only if not ended with blank!
         // since these end blanks would be erroneously trimmed
         if (!arr_Column[0].endsWith (" ") && !arr_Column[0].endsWith ("\t"))
         {
            return START_LITERAL_2 + arr_Column[0];
         }
      }

      // look one per one
      StringBuffer sb = new StringBuffer ("");
      for (int ii = 0; ii < arr_Column.length; ii ++)
      {
         if (ii > 0)
            sb.append (ATOM_SEPARATOR);
         sb.append (enpaqueta (arr_Column[ii]));
      }
      return sb.toString ();
   }

   private String enpaqueta (String str)
   {
      //_DBG_ System.out.println ("[>>] Cadena::EnpaquetaMe");
      //_DBG_ System.out.println ("o_str [" + o_str + "]");

      if (str.length () == 0)
      {
         // value is ""
         return ATOM_ENVOLVER + "" + ATOM_ENVOLVER;
      }

      boolean quote = str.indexOf (ATOM_ENVOLVER) != -1 ||     //  ..."...
                      str.indexOf (ATOM_SEPARATOR) != -1 ||    //  ...,...
                      str.indexOf ("#") != -1 ||               //  ...#...
                      str.indexOf ("<") != -1 ||               //  ...<...
                      str.startsWith (START_LITERAL_1) ||      //  '......
                      str.startsWith (START_LITERAL_2) ||      //  //.....
                      str.startsWith (" ") ||                  //  s......
                      str.startsWith ("\t") ||                 //  t......
                      str.endsWith (" ") ||                    //  ......s
                      str.endsWith ("\t")                      //  ......t
                      ;

      if (quote)
         // "frase"
         return ATOM_ENVOLVER +
                str.replaceAll ("\\\"", "" + ATOM_ENVOLVER + ATOM_ENVOLVER) +
                ATOM_ENVOLVER;

      return str;
   }

   public static String getNextToken (stringCursor spo, char separator, boolean restLineLiterals)
   {
      // trim
      while (!spo.ended () && (spo.charPoint() == ' ' || spo.charPoint() == '\t')) spo.inc();
      if (spo.ended ()) return null;

      // is it a unique token ?
      //
      if (restLineLiterals)
      {
         if (spo.charPoint() == START_LITERAL_1_CH)
         {
            String str = miscUtil.substr (spo.str (), spo.indx + START_LITERAL_1.length ()); // remove literal1
            spo.incEnd ();
            return str;
         }
         if (!spo.endIn (1) && spo.charPoint() == START_LITERAL_2_CH && spo.charPoint(1) == START_LITERAL_2_CH)
         {
            String str = miscUtil.substr (spo.str (), spo.indx + START_LITERAL_2.length ()); // remove literal2
            spo.incEnd ();
            return str;
         }
      }

      // iterate until , or closing " if it starts with "
      //
      boolean envolta = spo.charPoint() == ATOM_ENVOLVER;
      int ini = envolta ? ++ spo.indx: spo.indx;
      StringBuffer cell = new StringBuffer ();

      do {
         if (envolta)
         {
           if (spo.charPoint() != ATOM_ENVOLVER) spo.inc ();
           else
               if (spo.charPoint(1) == ATOM_ENVOLVER)
               {
                 // double ""
                 // add a part including one " and continue
                 cell.append (spo.indx + 1 > ini ? miscUtil.substrBE (spo.str (), ini, spo.indx): "");
                 spo.inc (2);
                 ini = spo.indx;
               }
               else break; // close "
         }
         else
         {
            if (spo.charPoint() == separator) break;
            spo.inc ();
         }
      } while (!spo.ended ());

      // right trim if not quoted
      //
      int bak = 0;

      if (! envolta)
         while (spo.charPoint(-(bak+1)) == ' ' || spo.charPoint(-(bak+1)) == '\t') bak ++;

      if (ini < spo.indx - bak)
      {
         cell.append (miscUtil.substrBE (spo.str (), ini, spo.indx - bak - 1));
      }

      spo.inc ();
      if (envolta)
      {
         // find the next comma if any
         while (!spo.ended () && spo.charPoint () != separator) spo.inc ();
         spo.inc (); // skip the comma
      }

      return cell.toString ();
   }

   public int parseLine (String sline)
   {
      // special case ! string "" has to retorn one element (like ",")
      //
      if (sline.length () == 0)
      {
         arr_Column = new String [] { "" };
         return cols ();
      }

      stringCursor spo = new stringCursor (sline);

      List colec = new Vector ();
      while (!spo.ended ())
      {
         String quepo = getNextToken (spo, ATOM_SEPARATOR, true);
         if (quepo != null)
            colec.add (quepo);
         // System.out.println ("saco = [" + quepo + "] queda [" + queda.o_str + "]");
      }

      // make the string array
      //
      arr_Column = new String[colec.size ()];
      for (int ii = 0; ii < colec.size (); ii ++)
         arr_Column[ii] = (String) colec.get(ii);

      return cols ();
   }
}