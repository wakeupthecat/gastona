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

package de.elxala.langutil.filedir;

import java.util.*;
import java.lang.StringBuffer;
import de.elxala.zServices.*;
import java.io.InputStream;

/**
   class serialTextBufferInputStreamWrapper
   @author Alejandro Xalabarder Aulet
   @date   2015

        A InputStream wrapper for a serialTextBuffer.

*/
public class serialTextBufferInputStreamWrapper extends InputStream
{
   private serialTextBuffer theObj = null;
   
   public serialTextBufferInputStreamWrapper (serialTextBuffer obj)
   {
      theObj = obj;
   }

//   //Returns an estimate of the number of bytes that can be read (or skipped over) from this input stream without blocking by the next invocation of a method for this input stream.
//   public int available ()
//   {
//      return 1;
//   }

   //Closes this input stream and releases any system resources associated with the stream.
//   public void close ()
//   {
//      theObj.freeFile ();
//   }
   
   //Reads the next byte of data from the input stream.
   public int read ()
   {
      byte[] buf = new byte[1];
      
      if (read(buf) == 1)
         return buf[0];
      return -1;
   }

   public int read (byte[] buf)
   {
      return theObj.readBytes (buf);
   }
   
   public void reset ()
   {
      theObj.rewind ();
   }
}
