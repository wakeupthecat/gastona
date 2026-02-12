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

import java.util.*;
import java.lang.StringBuffer;

// NOTA 29.06.2008 13:53: quitar acentos por el p problema con gcj "error: malformed UTF-8 character." de los c

/**
   class EvaUnit
   @author Alejandro Xalabarder Aulet
   @date   2001

   EvaUnit is a named container for Eva objects. It can be loaded/saved from/to a text file (see EvaFile)
   and also the contents may be altered as desired (clear, add, remove). Provides access to the contained Eva's
   by its names (Eva's names) and also acces to the Strings inside the Evas.

   <pre>
   Example:

         // create the EvaUnit
         //
         EvaUnit euni = new EvaUnit ("myUnit");

         // create (on the fly) a couple of evas and take the references
         //
         Eva eva1 = euni.getSomeHowEva ("myEva");
         Eva eva2 = euni.getSomeHowEva ("otra");

         // add data to the eva1
         //
         eva1.addLine (new EvaLine ("one, two, three"));
         eva1.addLine (new EvaLine ("eins, zwei, drei"));
         eva1.addLine (new EvaLine ("uno, dos, tres"));

         // print out some values
         //
         System.out.println ("# evas = " + euni.size ());
         System.out.println ("value of eva myEva row 2, column 2 = [" + euni.getValue ("myEva", 2, 2) + "]");
         System.out.println ("EvaUnit toString:\n" + euni);

         // save it to file
         //
         EvaFile.saveEvaUnit ("myFile.eva", euni);
   </pre>


   18.03.2007 14:56 EVA ::merge
   02.02.2005 12:00 EVA Text File Specification v1.0
*/
public class EvaUnit implements java.io.Serializable
{
   private String Nom = "";
   public List lis_Evas = null; // List < Eva > needed as public for special utilities

   public EvaUnit (String nom)
   {
      clear (nom);
   }

   public EvaUnit ()
   {
      clear ("");
   }

   // This set the EvaUnit object as a reference of the passed EvaUnit "master"
   // all changes to the reference will be reflected in the original
   // except the name that will be simply copied
   //
   public void setAsReferenceOf (EvaUnit master)
   {
      Nom = master.getName ();
      lis_Evas = master.lis_Evas;
   }

   // Copies "source" EvaUnit contents to this one
   //
   public void copyEvaUnit (EvaUnit cop)
   {
      Nom = cop.getName ();

      lis_Evas = new Vector ();
      for (int ii = 0; ii < cop.lis_Evas.size (); ii ++)
      {
         Eva eva = new Eva ();
         eva.copyEva ((Eva) cop.lis_Evas.get (ii));
         lis_Evas.add (eva);
      }
   }


   /**
      returns the name of the EvaUnit
   */
   public String getName ()
   {
      return Nom;
   }

   /**
      sets the name 'name' to the EvaUnit
   */
   public void setName (String name)
   {
      Nom = name;
   }

   /**
      return the number of Eva's in the unit
   */
   public int size ()
   {
      return lis_Evas.size ();
   }

   /**
      clears the contents and set a new name 'nom'
   */
   public void clear (String nom)
   {
      Nom = nom;
      lis_Evas = new Vector ();
   }

   /**
      clears the contents (the name is kept)
   */
   public void clear ()
   {
      clear (Nom);
   }

   /**
      Add a new Eva 'nova' checking if the name is alredy in use

      returns true if the Eva could be added
   */
   public boolean add (Eva nova)
   {
      if (nova == null || indxEva(nova.getName ()) != -1) return false;
      lis_Evas.add (nova);
      return true;
   }

   /**
      tries to delete an Eva with name 'evaName' from the unit

      return true if the Eva was there and now is removed
   */
   public boolean remove (String Nom)
   {
      int indx = indxEva(Nom);
      if (indx == -1) return false;
      lis_Evas.remove (indx);
      return true;
   }

   /**
      tries to delete an Eva at index 'indx' from the unit

      returns true if the Eva was there and now is removed
   */
   public boolean remove (int indx)
   {
      if (indx >= 0 && indx < size ())
      {
         lis_Evas.remove (indx);
         return true;
      }
      return false;
   }


   public int merge (EvaUnit plusUnit)
   {
      return merge (plusUnit, Eva.MERGE_ADD);
   }

   public int merge (EvaUnit plusUnit, char mergePolicy)
   {
      return merge (plusUnit, mergePolicy, true, false);
   }

   public int merge (EvaUnit plusUnit, char mergePolicy, boolean insertIfNotExists)
   {
      return merge (plusUnit, mergePolicy, insertIfNotExists, false);
   }

   /**
      merges the EvaUnit 'plusUnit' following the policies 'insertIfNotExists' and 'mergePolicy'


      @mergePolicy :
            MERGE_ADD         'A'   the rows of the plusEva are added at the end
            MERGE_REPLACE     'R'   the eva is replaced (if it already exist)
            MERGE_ADD_TABLE   'T'   the rows of the plusEva are added at the end except the first one which is the header with column names
            MERGE_NEW_VARS    'N'   only the new variables are added, the existent one remains unchanged

      @insertIfNotExists:
         if the Eva to merge does not exists then it is created

   */
   public int merge (EvaUnit plusUnit, char mergePolicy, boolean insertIfNotExists, boolean copy)
   {
      int nMerged = 0;

      if (plusUnit == null) return 0;

      // add new variables is default for all policies
      // MERGE_NEW_VARS simply specify that if not new then it cannot be merged
      boolean canMergeExistent = mergePolicy != Eva.MERGE_NEW_VARS;

      for (int ii = 0; ii < plusUnit.size (); ii ++)
      {
         Eva eSource = plusUnit.getEva(ii);
         String eName = eSource.getName ();
         Eva eHere = getEva (eName);
         boolean exists = eHere != null;

         if (exists && canMergeExistent)
         {
            eHere.merge (eSource, mergePolicy, copy);
            nMerged ++;
         }
         if (!exists && insertIfNotExists)
         {
            // it does not exists ... then insert it
            //
            Eva e2add = eSource;
            if (copy)
            {
               e2add = new Eva (eSource.Nom);
               e2add.merge (eSource, mergePolicy, true);
            }

            add (e2add);
            nMerged ++;
         }
      }

      return nMerged;
   }


   /**
   *  Retorna l'index de l'eva amb el nom "name" si existeix
   *  en cas contrari retorna -1
   */
   public int indxEva (String name)
   {
      if (name == null) return -1;

      for (int ii = 0; ii < size (); ii ++)
         if (name.equalsIgnoreCase ( ((Eva) lis_Evas.get (ii)).Nom) )
            return ii;
      return -1;
   }

   /**
   *  Serializa (so'lo salida)
   */
   public String toString ()
   {
      String ret = Eva.RETURN_STR;
      StringBuffer ss = new StringBuffer ("#" + Nom + "#" + ret);

      for (int ii = 0; ii < size (); ii ++)
      {
         ss.append (((Eva) lis_Evas.get (ii)).toString ());
      }

      return ss.toString ();
   }

   /**
      Returns the Eva named "nameEva" or null if it does not exist
   */
   public Eva getEva (String nameEva)
   {
      return getEva (indxEva (nameEva));
   }

   /**
      Returns the Eva by index (0..size ()-1) or null if it does not exist
   */
   public Eva getEva (int indx)
   {
      if (indx >= 0 && indx < size ())
         return (Eva) lis_Evas.get (indx);
      return null;
   }

   /**
      Returns a List of String with the names of all the Eva's in the unit
   */
   public List getEvasNames ()
   {
      List lalista = new Vector ();

      for (int ii = 0; ii < lis_Evas.size (); ii ++)
      {
         lalista.add (((Eva) lis_Evas.get (ii)).getName ());
      }

      return lalista;
   }

   /**
      Returns the String found in the Eva named 'nameEva' at row 'nrow and column 'ncol'
      If such Eva or the String at that position does not exists returns "".
      Note: this method ALWAYS return a string (never null)
   */
   public String getValue (String nameEva, int nrow, int ncol)
   {
      int indx = indxEva (nameEva);
      if (indx != -1)
         return ((Eva) lis_Evas.get (indx)).getValue (nrow,ncol);
      return "";
   }

   /**
      Returns the String found in the Eva named 'nameEva' at row 'nrow' and column 0
      If such Eva or the String at that position does not exists returns "".
      Note: this method ALWAYS return a string (never null)
   */
   public String getValue (String nameEva, int nrow)
   {
      return getValue (nameEva, nrow, 0);
   }

   /**
      Returns the String found in the Eva named 'nameEva' at row 0 and column 0
      If such Eva or the String at that position does not exists returns "".
      Note: this method ALWAYS return a string (never null)
   */
   public String getValue (String nameEva)
   {
      return getValue (nameEva, 0, 0);
   }

   /**
      gets the eva named 'evaName' if exists in the EvaUnit if not then a new Eva with this name
      will be created.

      Note : this method returns an Eva object ALWAYS! no null value might be return
   */
   public Eva getSomeHowEva (String evaName)
   {
      Eva ella = getEva (evaName);

      // ensure existency
      if (ella == null)
      {
         ella = new Eva (evaName);
         add (ella);
      }
      return ella;
   }

   /**
      Convenient method for setting a value in a Eva with just one element (row=0, col=0).
      This method ensures that the eva with name 'evaName' will exist and the value 'value' will be
      set at position row=0 column=0.

      Same as : getSomeHowEva (evaName).setValue (value)
   */
   public boolean setValueVar (String evaName, String value)
   {
      Eva ella = getSomeHowEva (evaName);

      ella.setValueVar (value);

      return true;
   }
}