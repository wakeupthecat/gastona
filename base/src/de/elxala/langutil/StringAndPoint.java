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

public class StringAndPoint
{
   public String strval = ""; // should be final ?
   public int pos = 0;

   public StringAndPoint (String str)
   {
      strval = str;
   }

   public String str () { return strval; }
   public void inc () { pos ++; }
   public void inc (int n) { pos += n; }
   public void incEnd () { pos = strval.length (); }

   public boolean ended ()  { return pos >= strval.length (); }
   public boolean endIn (int steps) { return pos+steps >= strval.length (); }

   public char charPoint () { return charPoint (0); }
   public char charPoint (int plus) { return pos+plus < 0 || pos+plus >= strval.length () ? 0: strval.charAt (pos+plus); }
}
