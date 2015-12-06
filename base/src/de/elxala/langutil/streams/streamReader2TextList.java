/*
package de.elxala.langutil
(c) Copyright 2009 Alejandro Xalabarder Aulet

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

package de.elxala.langutil.streams;

import java.io.*;
import java.util.*;
import de.elxala.zServices.*;

/**
   @author Alejandro Xalabarder
   @date   30.12.2009

   @brief thread that reads and stores a stream as text lines until the end of the stream

   See also abstractStreamTextReader
*/
public class streamReader2TextList extends abstractStreamTextReader
{
   //NOTE: This stream is planned to be used by windowing (e.g. sqlite)
   //      an unlimited StreamTextBuffer should be implemented using a temporary file or like
//!!   private static int LIMIT_BUFFERED_LINES = 3000;
//NOTE 01.01.2010 15:53
// No se puede dejar de leer el stream! eso bloquearía el proceso!
// si se quiere implementar este límite debe simplemente dejar de anyadir lineas en textBuffer
// pero continuar la lectura hasta el final!

   private List textBuffer = null;

   public streamReader2TextList (InputStream ins)
   {
      super (ins);
      textBuffer = new Vector();
   }

   public List getAsList ()
   {
      return textBuffer == null ? new Vector (): textBuffer;
   }

   public int countLines ()
   {
      return textBuffer == null ? 0 : textBuffer.size ();
   }

   public String getLine (int nline)
   {
      if (textBuffer == null || nline < 0 || nline > textBuffer.size ()) return "";
      return (String) textBuffer.get (nline);
   }

   public void addLine (String strline)
   {
      textBuffer.add (strline);
   }

   public void run ()
   {
      textBuffer = new Vector();
      super.run ();
   }
}
