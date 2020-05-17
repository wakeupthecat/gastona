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

package javaj;

import java.util.*;

/**
   Cyclic control for layout composition (just track the layout name)
*/
public class layComposCyclicControl
{
   public Vector pilaRecursion = new Vector ();
   public String cyclusErrorMsg = "";

   public layComposCyclicControl()
   {
   }

   public int depth ()
   {
      return pilaRecursion.size ();
   }

   public boolean pushClean (String item)
   {
      if (pilaRecursion.contains (item) || pilaRecursion.size () > 20)
      {
         cyclusErrorMsg = "";
         for (int yy = 0; yy < pilaRecursion.size (); yy ++)
         {
            cyclusErrorMsg += (yy == 0) ? "": " -> ";
            cyclusErrorMsg += "[" + (String) pilaRecursion.get (yy) + "]";
         }
         cyclusErrorMsg += " -> [" + item + "]";
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
}

