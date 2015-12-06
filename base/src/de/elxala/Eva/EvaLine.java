/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005  Alejandro Xalabarder Aulet

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
public class EvaLine
{
   private String [] arr_Column = null;

   /**
      Constructs a EvaLne object from a String array
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

      // ! this check may affect performance
      for (int ii = 0; ii < arrData.length; ii ++)
         if (arrData[ii] == null)
            arrData[ii] = "";
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
      Constructs a EvaLine object from a Cadena object (de.elxala.langutil.Cadena)
   */
   public EvaLine (Cadena sLin)
   {
      set (sLin);
   }

   /**
      Constructs a EvaLine object from String 'sLin' where the columns are comma separated
      Example:

      EvaLine elin = new EvaLin ("Peter, 444 yyy zz, Stuttgart");
   */
   public EvaLine (String sLin)
   {
      set (new Cadena (sLin));
   }


   public void clear ()
   {
      arr_Column = new String [] {""};
   }

   /**
      return the EvaLin as String array
   */
   public String [] getColumnArray ()
   {
      return arr_Column;
   }

   /**
      // NOTA : quitar acentos por el p problema con gcj "error: malformed UTF-8 character." de los c
      Carga la liinea a partir de una cadena que contiene los elementos
      separados por comas (con "" cuando conviene)
   */
   public void set (Cadena sLin)
   {
      arr_Column = sLin.toStrArray ();
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
      Returns a Cadena object where the whole EvaLine (with all its elements) is serialized
   */
   public Cadena getAsCadena ()
   {
      Cadena cad = new Cadena ();
      cad.setStrArray (arr_Column);

      return cad;
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
      return (getAsCadena ()).o_str;
   }
}