/*
library listix (www.listix.org)
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

package listix;

import java.util.*;

public class cyclicControl
{
   public Vector pilaRecursion = new Vector ();
   public String cyclusMsg = "";
   public static Vector lastUsedStack = null;


   public cyclicControl()
   {
      lastUsedStack = pilaRecursion; // points to the last stack used
   }

   public int depth ()
   {
      return pilaRecursion.size ();
   }

   public boolean pushClean (String item, String tableState)
   {
      lastUsedStack = this.pilaRecursion;

      // anyadimos informacion acerca del estado de las tablas
      // pues es posible (legal) ir a parar a un formato anterior pero con distinta tabla
      //
      item += ":(" + tableState + ")";

      if (pilaRecursion.contains (item) || pilaRecursion.size () > 20)
      {
         int indx = pilaRecursion.indexOf (item);
         cyclusMsg = "";
//            for (int yy = indx; yy >= 0 && yy < pilaRecursion.size (); yy ++)
         for (int yy = 0; yy < pilaRecursion.size (); yy ++)
         {
            // cyclusMsg += (yy == indx) ? "": " -> ";
            cyclusMsg += (yy == 0) ? "": " -> ";
            cyclusMsg += "[" + (String) pilaRecursion.get (yy) + "]";
         }
         cyclusMsg += " -> [" + item + "]";
         return false;
      }
      //System.out.println ("[[" + pilaRecursion.size () + "]] ((" + item + "))");
      pilaRecursion.add (item);
      return true;
   }

   public void pop ()
   {
      pilaRecursion.remove (pilaRecursion.size () -1);
   }

   /**
      Function for log pruposes
      Note that it does not return the stack of this instance but the last used stack!

      //(o) TODO_listix debug listix format stack: evaluate effects of this approach

      Note that this mechanism does not guarantee that this is the stack of the current
      active listix instance (the last active when the error has been produced), but in many
      cases it will work
   */
   public static String [] getLastFormatStack ()
   {
      if (lastUsedStack == null) return new String [0];
      String [] laPila = new String [lastUsedStack.size ()];
      for (int ii = 0; ii < lastUsedStack.size (); ii ++)
         laPila[ii] = (String) lastUsedStack.get (ii);

      return laPila;
   }
}

