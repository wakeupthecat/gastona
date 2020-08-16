/*
library listix (www.listix.org)
Copyright (C) 2005-2020 Alejandro Xalabarder Aulet

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
   private static int cyclicInstanceStaticCnt = 0;

   // instance counter id
   public int    cyclicInstanceId = (++ cyclicInstanceStaticCnt);

   // recursion stacks, they have to run in parallel (same pushes, same pops)
   protected Vector pilaRecursion = new Vector ();
   protected Vector pilaRecursionStamps = new Vector ();

   public String cyclusErrorMsg = "";
   public static Vector lastUsedStack = null;
   public static long lastElapsedMilliseconds = 0;

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
      item += ":(loopDepth=" + tableState + ")";

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

      // push in both parallel stacks
      pilaRecursion.add (item);
      pilaRecursionStamps.add (new long [] { System.currentTimeMillis () });
      return true;
   }

   public void pop ()
   {
      // compute the elapsed milliseconds for the last call (cycle)
      int lastindx = pilaRecursionStamps.size () - 1;
      lastElapsedMilliseconds = System.currentTimeMillis () - ((long []) pilaRecursionStamps.get (lastindx))[0];

      // pop both parallel stacks
      pilaRecursion.remove (pilaRecursion.size () -1);
      pilaRecursionStamps.remove (pilaRecursionStamps.size () -1);
   }

   /**
      Function for log purposes
      Note that it does not return the stack of this instance but the last used stack!

      //(o) TODO_listix debug listix format stack: evaluate effects of this approach

           There are two possible solutions but both would require that the instanciator of the
           cyclicControl object would destroy it explicitly when finished with its use.
           Unless (in option 1) we destroy them automatically after reaching depth zero within a pop and create a
           new instance on demand (giving the same handle numer!)

          option 1: keeping all instances internally and return only handles

          option 2: Building a list of pointers to all instances

          Solution option 1 should be fixed better with a pool to avoid constructions and destructions very often

          Solution option 2 would not work!: we don't have any control about destructions
          and cannot get rid of the pointer automatically when reach depth 0 since we don't know if
          the object wants to be destroyed or simply is going to be used later

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
