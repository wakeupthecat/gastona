/*
package de.elxala.langutil
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

package de.elxala.langutil;

/**   ======== de.elxala.langutil.utilSys ==========================================
   @author Alejandro Xalabarder 04.07.2009 13:04

*/

import java.io.*;
import de.elxala.langutil.filedir.*;
import java.util.*;

/**
*/
public class utilSys
{
   public static final String dirSeparator = File.separator;
   public static final boolean isSysUnix = dirSeparator.equals ("/");


   private static Map objectMap = new TreeMap ();

   public static void objectSacPut (String idString, Object obj)
   {
      objectMap.put (idString, obj);
   }

   public static Object objectSacGet (String idString)
   {
      return objectMap.get (idString);
   }

   public static int objectSacGetInt (String idString, int defaultValue)
   {
      Object obj = objectMap.get (idString);
      if (obj == null) return defaultValue;
      int [] iarr = (int[]) obj;
      return iarr.length > 0 ? iarr[0]: defaultValue;
   }

   public static void destroySac ()
   {
      objectMap = new TreeMap ();
   }
}
