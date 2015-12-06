/*
package de.elxala.langutil
(c) Copyright 2005  Alejandro Xalabarder Aulet

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

package de.elxala.langutil.filedir;

import java.io.*;
import java.nio.channels.*;
import java.nio.*;

public class mappedFile
{
   private boolean isMapped = false;
   public  long    sizeOfFile = 0;
   private FileChannel.MapMode  iMode = FileChannel.MapMode.READ_ONLY;
   public  MappedByteBuffer puffer = null;
   //public  ByteBuffer puffer = null;

   public mappedFile (String name, String mode)
   {
      mapFile (name, mode, 0);
   }

   public mappedFile (String name, String mode, int offset)
   {
      mapFile (name, mode, offset);
   }

   public boolean mapFile (String name, String mode, int offset)
   {
      // System.out.println ("TENTO BRIR " + name);

      iMode = FileChannel.MapMode.READ_ONLY;
      if (mode.equals ("w") || mode.equals ("rw"))
         iMode = FileChannel.MapMode.READ_WRITE;

      try
      {
         FileChannel chan = (new RandomAccessFile (name, mode)).getChannel ();
         sizeOfFile = chan.size ();
         puffer = chan.map (iMode, offset, sizeOfFile - offset);

         // System.out.println ("a puffer de iMode " + iMode + " con osset " + offset + " y sizeOfile " + sizeOfFile);
         // if (puffer == null)
         //     System.err.println ("puffer is null");
         // else System.err.println ("VA BENE CAMPEON!");

         chan.close ();
      }
      catch (Exception e)
      {
         System.err.println (e);
         // System.err.println ("Exception!");
         return false;
      }
      isMapped = true;
      return isMapped;
   }

   public boolean isOkMapped ()
   {
      return isMapped;
   }

   public void close ()
   {
      if (iMode == FileChannel.MapMode.READ_WRITE)
         puffer.force ();
      puffer = null;
   }
}

