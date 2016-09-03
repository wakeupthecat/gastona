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

import java.util.*;

/**
   class Eva
   @author Alejandro Xalabarder Aulet
   @date   2001

   This class describes and handles the called structure Eva (see EvaFormat.PDF)

   A Eva object is basically a named list of String arrays (EvaLine objects), and it is
   thought to be contained in a EvaUnit (set of Eva's).
   Implements serialitation (toString) and deserialitation through "EvaUnit EvaFile.loadEvaUnit (..)"

   <pre>
   Example:

         Eva eva = new Eva ("agenda");

         eva.setValue ("Peter",  0, 0);
         eva.setValue ("Silvia", 1, 0);
         eva.setValue ("Eva",    2, 0);

         eva.setValue ("4xx yyy zz", 0, 1);
         eva.setValue ("6xx yyy zz", 1, 1);
         eva.setValue ("7xx yyy zz", 2, 1);

         // ... or
         eva.addLine (new EvaLine ("Peter,  444 yyy zz"));
         eva.addLine (new EvaLine ("Silvia, 666 yyy zz"));
         eva.addLine (new EvaLine ("Eva,    777 yyy zz"));

         System.out.println ("Eva name is [" + eva.getName () + "]");
         System.out.println ("# of rows = " + eva.rows ());
         System.out.println ("# of columns in row 1 = " + eva.cols (1));
         System.out.println ("value of row 1, col0 = [" + eva.getValue (1, 0) + "]");
         System.out.println ("Eva toString :\n" + eva);
   </pre>


   02.02.2005 12:00 EVA Text File Specification v1.0
*/
public class Eva implements java.io.Serializable
{
   public static final String RETURN_STR = System.getProperty("line.separator", "\n");

   public static final char MERGE_REPLACE   = 'R';   // the eva is replaced (if it already exist)
   public static final char MERGE_ADD       = 'A';   // the rows of the plusEva are added at the end
   public static final char MERGE_ADD_TABLE = 'T';   // the rows of the plusEva are added at the end except the first one which is the header with column names

   public String Nombre;
   public List   lis_EvaLin; // List < EvaLine >

   /**
      Constructs a Eva object with empty name of size 0 rows
   */
   public Eva ()
   {
      create ("");
   }

   /**
      Constructs a Eva object with name 'nom' of size 0 rows
   */
   public Eva (String nom)
   {
      create (nom);
   }

   /**
      Constructs an Eva object with the name 'name', size 1 row x 1 column and value of cell 0, 0 'value'.
      see also create (String name, String value)
      <pre>
      Example:

      Eva obj = new Eva ("myEva", "one,two,three");
      </pre>
      will create a Eva with one row and one(!) column containing the value "one,two,three"
   */
   public Eva (String name, String value)
   {
      create (name, value);
   }


   /**
      Constructs an Eva object with the name 'name', and sets the data from values

      <pre>
      Example:

      Eva obj = new Eva ("myEva", new String [][] { { "first row" }, { "one" , "two", "three"} });

      </pre>
      will create an Eva with one row and three columns in row 0 ([one] [two] and [three])
   */
   public Eva (String name, String [][] values)
   {
      create (name, values);
   }


   /**
      returns the name of the Eva
   */
   public String getName ()
   {
      return Nombre;
   }

   /**
      sets the name of the Eva
   */
   public void setName (String name)
   {
      Nombre = name;
   }

   /**
      Clears the contents of the Eva object (just the rows but the name is not cleared)
   */
   public void clear ()
   {
      init ();
   }

   /**
      Creates (clearing the old content) an Eva object with the name 'name'
   */
   public void create (String name)
   {
      Nombre = name;
      init ();
   }

   /**
      Creates (clearing the old content) an Eva object with the name 'name', size 1 row x 1 column and value of cell 0, 0 'value'.
   */
   public void create (String name, String value)
   {
      Nombre = name;
      init ();
      setValueVar (value);
   }

   /**
      Creates (clearing the old content) an Eva object with the name 'name', and sets
      the first row (0) with 'firstEvaLine'.

      <pre>
      Example:

      Eva obj = new Eva ();
      obj.create ("myEva", new EvaLine ("one,two,three"));
      </pre>
      will create a Eva with one row and three columns in row 0 ([one] [two] and [three])
    */
   public void create (String name, EvaLine firstEvaLine)
   {
      Nombre = name;
      init ();
      addLine (firstEvaLine);
   }

   /**
      Creates (clearing the old content) an Eva object with the name 'name', and sets
      all rows according to the given String [][]

      <pre>
      Example:

      Eva obj = new Eva ();
      obj.create ("myEva", new String [][] { { "one", "two", "three" }, {"second", "and last" } };
      </pre>
      will create a Eva with one two rows, the first one of three columsn and the second one two
    */
   public void create (String name, String [][] values)
   {
      Nombre = name;
      setValues (values);
   }

   /**
      Creates (clearing the old content) an Eva object with the name 'name', size 1 row x 1 column and value of cell 0, 0 'value'.
   */
   private void init ()
   {
      lis_EvaLin = new Vector ();
   }

   /**
      Adds a new EvaLine (row) to the Eva object
      @param newRow the new EvaLine to add
   */
   public void addLine (EvaLine newRow)
   {
      lis_EvaLin.add (newRow);
   }

   /**
      Inserts a new row at the position 'indx' with the value 'newRow'. If the position 'indx' is not
      in the ranges of current rows (0 .. rows () -1), then 'newRow' is simply added at the end
   */
   public void addLine (int indx, EvaLine newRow)
   {
      if (indx >= 0 && indx < rows ())
           lis_EvaLin.add (indx, newRow);
      else lis_EvaLin.add (newRow);
   }

   /**
      Removes the row with position 'indx'
      returns true the operation could be performed
   */
   public boolean removeLine (int indx)
   {
      if (indx >= 0 && indx < rows ())
      {
         lis_EvaLin.remove (indx);
         return true;
      }
      return false;
   }

   public void merge (Eva plusEva)
   {
      merge (plusEva, MERGE_ADD, false);
   }

   /**
      merges the Eva 'plusEva' following the policies 'mergePolicy' and 'copy'

      @mergePolicy :
            MERGE_REPLACE     'R'   the eva is replaced (if it already exist)
            MERGE_ADD         'A'   the rows of the plusEva are added at the end
            MERGE_ADD_TABLE   'T'   the rows of the plusEva are added at the end except the first one which is the header with column names

      @copy:
         make a copy instead of just reference the source (whole Eva or just EvaLines)

   */
   public void merge (Eva plusEva, char mergePolicy, boolean copy)
   {
      if (plusEva == null) return;

      if (mergePolicy == MERGE_REPLACE)
         clear ();

      int startRow = (rows() > 0 && mergePolicy == MERGE_ADD_TABLE) ? 1: 0;

      for (int rr = startRow; rr < plusEva.rows (); rr ++)
      {
         EvaLine eLin = plusEva.get(rr);
         addLine (copy ?  new EvaLine (eLin): eLin);
      }
   }


   /**
      Search through the contents of column 0 a value equales to 'value' and returns its index (0 .. rows ()-1)
      if 'value' were not found then returns -1
      (Note: same as rowOf (String))
   */
   public int indexOf (String value)
   {
      return rowOf (value, 0, true);
   }

   /**
   */
   public int rowOf (String value)
   {
      return rowOf (value, false);
   }

   /**
      Search through the contents of column 0 a value equales to 'value' and returns its row (0 .. rows ()-1)
      if 'value' were not found then returns -1
      (Note: same as indexOf (String))
   */
   public int rowOf (String value, boolean ignoreCase)
   {
      return rowOf (value, 0, ignoreCase);
   }

   public int rowOf (String value, int col)
   {
      return rowOf (value, col, true);
   }

   /**
      Search through the contents of column 'col' a value equals to 'value' and returns its row (0 .. rows ()-1)
      if 'value' were not found then returns -1
   */
   public int rowOf (String value, int col, boolean ignoreCase)
   {
      for (int rr = 0; rr < rows (); rr ++)
      {
         boolean igual = (ignoreCase) ? value.equalsIgnoreCase (getValue (rr, col)):
                                        value.equals  (getValue (rr, col));
         if (igual)
            return rr;
      }
      return -1;
   }

   public int colOf (String value)
   {
      return colOf (value, true);
   }

   /**
      Search through the contents of row 0 a value equals to 'value' and returns its column (0 .. cols(0)-1)
      if 'value' were not found then returns -1
   */
   public int colOf (String value, boolean ignoreCase)
   {
      return colOf (value, 0, ignoreCase);
   }

   public int colOf (String value, int row)
   {
      return colOf (value, row, true);
   }

   /**
      Search through the contents of row 'row' a value equals to 'value' and returns its column (0 .. cols(row)-1)
      if 'value' were not found then returns -1
   */
   public int colOf (String value, int row, boolean ignoreCase)
   {
      for (int cc = 0; cc < cols(row); cc ++)
      {
         boolean igual = (ignoreCase) ? value.equalsIgnoreCase (getValue (row, cc)):
                                        value.equals  (getValue (row, cc));
         if (igual)
            return cc;
      }
      return -1;
   }

   /**
      returns the contents (as EvaLine) of row 'nRow'. In case 'nRow' is an invalid row then returns null
   */
   public EvaLine get (int nLin)
   {
      if (nLin < rows () && nLin >= 0)
         return (EvaLine) lis_EvaLin.get (nLin);
      else return null;
   }

   /**
      returns the size in rows
   */
   public int rows ()
   {
      return lis_EvaLin.size ();
   }

   /**
      Returns the maximum number of columns of all the rows
      (Note: the function calculates this on every call, and this might be not performant!)
   */
   public int maxCols ()
   {
      int maxx = 0;
      if (rows () > 0)
      {
         for (int rr = 0; rr < rows (); rr ++)
            if (cols(rr) > maxx)
                maxx = cols(rr);
      }
      return maxx;
   }

   /**
      Returns the minimum number of columns of all the rows
      (Note: the function calculates this on every call, and this might be not performant!)
    */
    public int minCols ()
    {
        int minn = 0;
        if (rows () > 0)
        {
         minn = cols(0);
         for (int rr = 0; rr < rows (); rr ++)
                if (cols(rr) < minn)
                    minn = cols(rr);
        }
        return minn;
    }

    // NOTA : quitar acentos por el p problema con gcj "error: malformed UTF-8 character." de los c
    // public boolean esCuadrada ()
    // {
    //     // requiere mucho caalculo pero tambieen la funcioon es de dudoso uso ja
    //     return  (rows () == maxCols ()) &&  (rows () == minCols ()); ka
    // }

    /**
      Returns the number of columns in the row 'nrow'. If the row does not exist then returns 0
   */
   public int cols (int nrow)
    {
      if (rows () > nrow && nrow >= 0)
           return ((EvaLine) lis_EvaLin.get (nrow)).cols ();
      else return 0;
   }

   /**
      Serialitation of the object in Eva format for text file (see EvaFormat.PDF)
   */
   public String toString ()
   {
      String ret = RETURN_STR;
      StringBuffer ss = new StringBuffer (ret + "   <" + Nombre + ">" + ret);

      for (int ii = 0; ii < rows (); ii ++)
      {
         ss.append ("      ");
         ss.append (((EvaLine) lis_EvaLin.get (ii)).toString ());
         ss.append (ret);
      }

      return ss.toString ();
   }

   /**
      returns the contents of the row 'nrow' as String [] (every column as index of the array)
      if the row 'nrow' does not exist then returns null
   */
   public String [] getStrArray (int nrow)
   {
      if (nrow >= 0 && nrow < rows ())
         return (String []) ((EvaLine) lis_EvaLin.get (nrow)).getColumnArray ();
      return null;
   }

   /**
      returns the whole contents as a String, concatenating all the columns in a row and
      adding a new line character (\n) on every row
   */
   public String getAsText ()
   {
      String ret = System.getProperty("line.separator", "\n");
      StringBuffer ss = new StringBuffer ();

      for (int rr = 0; rr < rows (); rr ++)
      {
         if (rr != 0)
            ss.append (ret);
         for (int cc = 0; cc < cols (rr); cc ++)
            ss.append (getValue (rr, cc));
      }
      return ss.toString ();
   }

   /**
      returns the whole contents as a String [], concatenating all the columns in a row and
      every row in a index of the array
   */
   public String [] getAsArray ()
   {
      String [] array = new String [rows ()];

      for (int rr = 0; rr < rows (); rr ++)
      {
         String ss = "";
         for (int cc = 0; cc < cols (rr); cc ++)
            ss += getValue (rr, cc);

            array[rr] = ss;
      }
      return array;
   }

   /**
      returns the value at position row 0 column 0, if the value even does not exist (rows () == 0)
      then returns an empty string ("")
   */
   public String getValue ()
   {
      return getValue(0,0);
   }

   /**
      returns the value at position row 'nrow' column 0, if the value even does not exist
      then returns an empty string ("")
   */
   public String getValue (int nrow)
   {
      return getValue (nrow,0);
   }

   /**
      returns the value at position row 'nrow' column 'ncol', if the value even does not exist
      then returns an empty string ("")
   */
   public String getValue (int nrow, int ncol)
   {
      if (nrow >= 0 && nrow < rows ())
           return ((EvaLine) lis_EvaLin.get (nrow)).getValue (ncol);
      else return "";
   }

   public boolean setLineAtRow (int row, EvaLine line)
   {
      if (row >= 0 && row < rows ())
      {
         lis_EvaLin.set (row, line);
         return true;
      }
      return false;
   }

   /**
      Creates (clearing the old content) an Eva object with the name 'name', and sets
      all rows according to the given String [][]

      <pre>
      Example:

      Eva obj = new Eva ();
      obj.create ("myEva", new String [][] { { "one", "two", "three" }, {"second", "and last" } };
      </pre>
      will create a Eva with one two rows, the first one of three columsn and the second one two
    */
   public void setValues (String [][] values)
   {
      init ();
      for (int ii = 0; ii < values.length; ii ++)
         lis_EvaLin.add (new EvaLine (values [ii]));
   }

   /**
      sets the value 'sVal' at the position row='nrow' column='ncol'.
      IMPORTANT! if the position does not exist it will try to acomodate it by redimensioning the Eva !
   */
   public void setValue (String sVal, int nrow, int ncol)
   {
      if (nrow < 0 || ncol < 0) return;

      // redimensionar ... (?! puede ser peligroso...)
      while (nrow >= rows ())
         lis_EvaLin.add (new EvaLine(new String [ncol >= 0 ? ncol: 0]));

      EvaLine elin = (EvaLine) lis_EvaLin.get (nrow);
      if (elin == null) return;
      elin.setValue (sVal, ncol);
   }

   /**
      sets the value 'sVal' at the position row='nrow' column=0.
      NOTE: the row is previously cleared, so if the row did contain more columns these will be deleted!
      IMPORTANT! if the row does not exist it will try to acomodate it by redimensioning the Eva !
   */
   public void setValueRow (String sVal, int row)
   {
      EvaLine elin = (EvaLine) lis_EvaLin.get (row);

      if (elin == null) return;
      elin.clear ();
      elin.setValue (sVal, 0);
   }

   /**
      sets the variable to the single value 'sVal' (at  position 0,0)
   */
   public void setValueVar (String sVal)
   {
      clear ();
      setValue (sVal, 0, 0);
   }

   /**
      adds a new row with only one column and value 'sVal'
   */
   public void addRow (String sVal)
   {
      setValue (sVal, rows (), 0);
   }

   /**
      adds a new column in row=0 with value 'sVal'
   */
   public void addCol (String sVal)
   {
      addCol (sVal, 0);
   }

   /**
      adds a new column in row=nrow with value 'sVal'
   */
   public void addCol (String sVal, int row)
   {
      setValue (sVal, row, cols(row));
   }
}

