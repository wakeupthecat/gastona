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
import de.elxala.zServices.*;
import de.elxala.langutil.filedir.*;


public class streamFileInjector extends Thread
{
   protected static logger log ()
   {
      return streamPass.log ();
   }

   private OutputStream oStream = null;

   private String mFileName = null;
   private TextFile fileBin = new TextFile ();

   private boolean finished = false;

   public streamFileInjector (OutputStream outstr, String fileName)
   {
      oStream = outstr;
      mFileName = fileName;
   }

   public boolean hasFinished ()
   {
      return finished;
   }

   public void run ()
   {
      finished = false;
      try
      {
         if (!fileBin.fopen (mFileName, "rb"))
         {
            log().err ("streamFileInjector::run", "cannot open for read the file [" + mFileName + "]");
         }
         else
         {
            byte [] buff = new byte[400];
            int quan = 0;
            do
            {
               quan = fileBin.readBytes(buff);
               if (quan == -1) break;
               oStream.write (buff, 0, quan);
            } while (quan >= 0);
         }
         oStream.close ();
         finished = true;
      }
      catch (Exception e)
      {
         log().severe ("streamFileInjector::run", "exception " + e);
      }
   }
}