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
import de.elxala.langutil.filedir.*;


/**
   @author Alejandro Xalabarder
   @date   30.12.2009

   @brief thread that reads and saves a stream as text file until the end of the stream

   See also abstractStreamTextReader
*/
public class streamReader2TextFile extends abstractStreamTextReader
{
   private int countLn = 0;
   private String mFileName = null;
   private TextFile fileTxt = new TextFile ();

   public streamReader2TextFile (InputStream ins, String fileName)
   {
      super (ins);
      mFileName = fileName;
   }

   public int countLines ()
   {
      return countLn;
   }

   public List getAsList ()
   {
      log().severe ("streamReader2TextFile", "misprogrammed, method getAsList is not implemented for this stream");
      return new Vector ();
   }

   public String getLine (int nline)
   {
      log().severe ("streamReader2TextFile", "misprogrammed, method getLine is not implemented for this stream");
      return "";
   }

   public void addLine (String strline)
   {
      countLn ++;
      if (mFileName != null)
      {
         fileTxt.fopen (mFileName, "a");
         fileTxt.writeLine (strline);
         fileTxt.fclose ();
      }
   }

   public void run ()
   {
      countLn = 0;
      super.run ();
   }
}
