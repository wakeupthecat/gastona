/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2016  Alejandro Xalabarder Aulet

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

package de.elxala.langutil.filedir;

import java.util.*;
import java.lang.StringBuffer;
import de.elxala.zServices.*;

public class memoryLines
{
   public List   linesArray = new Vector ();
   boolean       newLineAtEnd = false;
   StringBuffer  currentLine = new StringBuffer ();
   int           readingLine = 0;

   public int countLines ()
   {
      return linesArray.size () + (newLineAtEnd ? 1: 0);
   }

   public void write (String str)
   {
      if (str.length () > 0)
      {
         newLineAtEnd = false;
         currentLine.append (str);
      }
   }

   public void writeln ()
   {
      linesArray.add (currentLine.toString ());
      currentLine = new StringBuffer ();
      newLineAtEnd = true;
   }

   public void writeln (String str)
   {
      write (str);
      writeln ();
   }

   public void rewind ()
   {
      readingLine = 0;
   }

   public String readNextLine ()
   {
      if (readingLine > linesArray.size ()) return null;

      if (readingLine == linesArray.size ())
      {
         readingLine ++;
         return currentLine.length () > 0 ? currentLine.toString (): (newLineAtEnd ? "": null);
      }
      return (String) linesArray.get (readingLine ++);
   }
}
