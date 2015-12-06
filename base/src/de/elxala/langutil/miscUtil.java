/*
package de.elxala.langutil
(c) Copyright 2015 Alejandro Xalabarder Aulet

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


/**
import java.io.*;
import de.elxala.langutil.filedir.*;
import java.util.*;
*/
public class miscUtil
{
   //
   // tries to parse a color in one of the following formats
   //
   //    #rrGGbb or #AArrGGbb         where the values are given in Hexadecimal
   //    +rrrGGGbbb or #AAArrrGGGbbb  where the values are given in Decimal
   //
   // (*) chosen AA at the beginning of the strings to parse to be Android compatible! :(
   //
   // if the color is found an integer array of 4 element containing
   //     0 red    (0..255)
   //     1 green  (0..255)
   //     2 blue   (0..255)
   //     3 alfa   (0..255) or -1 if not given
   //
   // otherwise returns null
   //
   // (*) chosen AA at the beginning of the strings to parse to be Android compatible! :(
   //
   public static int [] parsedColorRGB (String str)
   {
      int [] resp = { 0, 0, 0, -1 };
      if (str.charAt(0) == '#')
      {
         // either #rrGGbb or #AArrGGbb
         if (str.length () < 7) return null;
         boolean alfa = str.length () >= 9;
         int Off = alfa ? 2: 0;

         resp[0] = Integer.parseInt (str.substring (Off + 1, Off + 1 + 2), 16);
         resp[1] = Integer.parseInt (str.substring (Off + 3, Off + 3 + 2), 16);
         resp[2] = Integer.parseInt (str.substring (Off + 5, Off + 5 + 2), 16);

         if (alfa)
            resp[3] = Integer.parseInt(str.substring(1, 1 + 2), 16);
         return resp;
      }

      if (str.charAt(0) == '+')
      {
         // either +rrrGGGbbb or +AAArrrGGGbbb
         if (str.length () < 10) return null;
         boolean alfa = str.length () >= 13;
         int Off = alfa ? 3: 0;

         resp[0] = Integer.parseInt (str.substring (Off + 1, Off + 1 + 3));
         resp[1] = Integer.parseInt (str.substring (Off + 4, Off + 4 + 3));
         resp[2] = Integer.parseInt (str.substring (Off + 7, Off + 7 + 3));

         if (alfa)
            resp[3] = Integer.parseInt(str.substring (1, 1 + 3));
         return resp;
      }
      return null;
   }

   public static boolean startsWithIgnoreCase (String strToEval, String startStr)
   {
      if (strToEval.length () < startStr.length ())
         return false;
      return strToEval.substring(0, startStr.length ()).equalsIgnoreCase (startStr);
   }
}
