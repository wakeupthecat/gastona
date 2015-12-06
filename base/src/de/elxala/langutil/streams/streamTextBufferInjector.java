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

import java.io.OutputStream;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;


public class streamTextBufferInjector extends Thread
{
   protected static logger log ()
   {
      return streamPass.log ();
   }

   private OutputStream oStream = null;

   private serialTextBuffer serialText = null;
   private String lineBreak = System.getProperty("line.separator", "\n");

   private boolean finished = false;

   public streamTextBufferInjector (OutputStream outstr, serialTextBuffer serTextBuff, String lineBreakChars)
   {
      oStream = outstr;
      serialText = serTextBuff;
      if (lineBreakChars != null)
         lineBreak = lineBreakChars;
   }


   public boolean hasFinished ()
   {
      return finished;
   }

   public void run ()
   {
      String str = null;
      finished = false;

      try
      {
         serialText.rewind ();
         while (serialText.getNextLine ())
         {
            oStream.write (serialText.getLastReadLine ().getBytes ());
            oStream.write (lineBreak.getBytes ());
         } while (str != null); // or .. while (true)

         oStream.close ();
         finished = true;
      }
      catch (Exception e)
      {
         log().severe ("streamTextIntector::run", "exception " + e);
      }
   }
}