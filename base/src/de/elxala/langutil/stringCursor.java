/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2017  Alejandro Xalabarder Aulet

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

package de.elxala.langutil;

/*
*/
public class stringCursor
{
   public String strval = ""; // should be final ?
   public int indx = 0;

   public stringCursor (String str)
   {
      strval = str;
   }

   public String str () { return strval; }
   public void inc () { indx ++; }
   public void inc (int n) { indx += n; }
   public void incEnd () { indx = strval.length (); }

   public boolean ended ()  { return indx >= strval.length (); }
   public boolean endIn (int steps) { return indx+steps >= strval.length (); }

   public char charPoint () { return charPoint (0); }
   public char charPoint (int plus) { return indx+plus < 0 || indx+plus >= strval.length () ? 0: strval.charAt (indx+plus); }

   public String strPoint ()
   {
      return strPoint (0);
   }

   public String strPoint (int plus)
   {
      if (indx+plus < 0) return strval;
      if (indx+plus >= strval.length ()) return "";
      return strval.substring (indx+plus);
   }
}
