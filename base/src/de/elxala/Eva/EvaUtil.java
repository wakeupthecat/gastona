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

/**
   Clase EvaUtil
   12.03.2003 por Alejandro Xalabarder

   13.03.2003 00:11
   16.05.2004 23:08  nuevo me'todo static int bigReplace : facilita el reemplazar tomando como mapa directamente una EvaUnit
   26.10.2007 22:18  ne method getEvaNames

*/

import java.util.List;
import java.util.Vector;
import de.elxala.langutil.*;
import java.util.regex.*;  // Pattern and Matcher


public class EvaUtil
{
   /**
      returns a list of strings with all eva names found in the EvaUnit eu
      given in the list 'evaNames'. If 'useRegularExpresion' is true then the
      names found in the parameter 'evaNames' are treated as Regular Expresions.
   */
   public static List getEvaNames (EvaUnit eu, List evaNames, boolean useRegularExpresion)
   {
      List lReto = new Vector ();
      List lEvasInUnit = eu.getEvasNames ();

      for (int ii = 0; ii < evaNames.size (); ii ++)
      {
         String givName = (String) evaNames.get (ii);

         if (! useRegularExpresion )
         {
            // just has to match exactly
            if (!lReto.contains (givName) && lEvasInUnit.contains (givName))
               lReto.add (givName);
         }
         else
         {
            // Using regular expressions!
            Pattern pat = Pattern.compile (givName);

            //System.out.println ("prismero " + ii + " [" + givName + "] compilatto!");

            for (int ee = 0; ee < lEvasInUnit.size (); ee ++)
            {
               String ename = (String) lEvasInUnit.get (ee);

//System.out.println ("misro " + ee + " [" + ename + "]");

               if (!lReto.contains (ename) && pat.matcher (ename).find ())
               {
//System.out.println ("   MACHA MACHO!");
                  lReto.add (ename);
               }
            }
         }
      }
      return lReto;
   }
}
