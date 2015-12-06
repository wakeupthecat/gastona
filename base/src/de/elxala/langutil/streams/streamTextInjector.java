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

import java.util.List;
import java.io.*;
import java.io.OutputStream;
import de.elxala.zServices.*;


public class streamTextInjector extends Thread
{
   protected static logger log ()
   {
      return streamPass.log ();
   }

   private OutputStream oStream = null;

   private String [] arrStr = null;
   private List listStr = null;
   private String lineBreak = System.getProperty("line.separator", "\n");

   private boolean finished = false;

   public streamTextInjector (OutputStream outstr, String[] strArray, String lineBreakChars)
   {
      oStream = outstr;
      arrStr = strArray;
      if (lineBreakChars != null)
         lineBreak = lineBreakChars;
   }

   public streamTextInjector (OutputStream outstr, List strList, String lineBreakChars)
   {
      oStream = outstr;
      listStr = strList;
      if (lineBreakChars != null)
         lineBreak = lineBreakChars;
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
         if (arrStr != null)
         {
            for (int ii=0; ii < arrStr.length; ii ++)
            {
               oStream.write (arrStr[ii].getBytes ());
               oStream.write (lineBreak.getBytes ());
            }
         }
         if (listStr != null)
         {
            for (int ii=0; ii < listStr.size (); ii ++)
            {
               oStream.write (((String) listStr.get(ii)).getBytes ());
               oStream.write (lineBreak.getBytes ());
            }
         }
         oStream.close ();
         finished = true;
      }
      catch (Exception e)
      {
         log().severe ("streamTextIntector::run", "exception " + e);
      }
   }
}