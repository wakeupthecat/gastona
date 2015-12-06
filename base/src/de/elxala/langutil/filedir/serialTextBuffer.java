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
import de.elxala.zServices.*;

/**
   class serialString
   @author Alejandro Xalabarder Aulet
   @date   2010

        A serialTextBuffer is an array of Strings that is virtually not limited since it would use
   transparently a file if required. It can only be read through the method getNextLine ().

   The idea is to have a general proupose text buffer that for small texts works totally in memory
   and that can also handle arbitrary big texts.
*/
public class serialTextBuffer
{
   private static logger log = new logger (null, "de.elxala.langutil.filedir.serialTextBuffer", null);

   private static final int MAX_LINES_KEEP = 3000;

   private static final int STATE_IDDLE = 0;
   private static final int STATE_WRITING = 1;
   private static final int STATE_READING = 2;

   protected int currentState = STATE_IDDLE;

   //memory
   protected List     strArr = new Vector ();
   protected int      nextLine2read = 0;
   protected String   lastReadLine = null;

   //file
   protected String   tmpFileName = null;
   protected TextFile txtFile = new TextFile ();

   public void clear ()
   {
      freeFile ();
      strArr = new Vector ();
      nextLine2read = 0;
      lastReadLine = null;
      currentState = STATE_IDDLE;
   }

   public void writeln (String str)
   {
      if (currentState == STATE_READING)
      {
         log.severe("writeln", "attempt to write while reading was active, this is not allowed in serialTextBuffer.");
         return;
      }
      currentState = STATE_WRITING;

      strArr.add (str);
      if (strArr.size () > MAX_LINES_KEEP)
      {
         vuelca();

         //ensure the the buffer is emptied!
         strArr = new Vector ();
      }
   }

   public void rewind ()
   {
      // in this state the object can be read again
      currentState = STATE_WRITING;
      nextLine2read = 0;
      lastReadLine = null;
   }

   public boolean getNextLine ()
   {
      lastReadLine = getNextLine2 ();
      return lastReadLine != null;
   }

   public String getLastReadLine ()
   {
      return lastReadLine;
   }

   public String toString ()
   {
      // can be very expensive if the text is huge!!

      rewind ();
      StringBuffer sbuff = new StringBuffer ();
      while (getNextLine ())
      {
         sbuff.append (getLastReadLine ());
      }

      return sbuff.toString ();
   }

   private String getNextLine2 ()
   {
      if (currentState == STATE_IDDLE)
      {
         clear ();
         return null;
      }

      if (currentState == STATE_WRITING)
      {
         currentState = STATE_READING;
         if (tmpFileName != null && !txtFile.fopen (tmpFileName, "r"))
         {
            log.err("getNextLine", "open to read [" + tmpFileName + "] failed");
            clear ();
            return null;
         }
         currentState = STATE_READING;
      }

      // reading from file ?
      if (tmpFileName != null)
      {
         if (txtFile.readLine ())
            return txtFile.TheLine ();

         // end of file reached
         freeFile ();
      }

      // read from memory
      if (nextLine2read > strArr.size ()-1)
      {
         rewind ();
         return null;
      }

      // return it from memory
      return (String) strArr.get (nextLine2read ++);
   }

   private void vuelca ()
   {
      if (!txtFile.fopen (getFileName (), "a"))
      {
         log.err("vuelca", "open to append [" + getFileName () + "] failed");
         return;
      }

      for (int ii = 0; ii < strArr.size (); ii ++)
         txtFile.writeLine ((String) strArr.get (ii));
      txtFile.fclose ();
   }

   private String getFileName ()
   {
      if (tmpFileName != null) return tmpFileName;

      //!!! from recycle !
      tmpFileName = fileUtil.createTemporal ();
      return tmpFileName;
   }

   private void freeFile ()
   {
      txtFile.fclose ();
      if (tmpFileName != null)
      {
         //!!! recycle (tmpFileName);
         tmpFileName = null;
      }
   }

   public static void main (String [] aa)
   {
      if (aa.length != 1)
      {
         System.out.println ("Syntax : fileName\n");
         return;
      }

      String fileName = aa[0];

      TextFile fi = new TextFile ();
      if (!fi.fopen (fileName, "r"))
      {
         System.out.println ("file [" + fileName + "] could not be opened!");
         return;
      }

      serialTextBuffer se = new serialTextBuffer();
      int nl = 0;
      while (fi.readLine ())
      {
         nl ++;
         se.writeln (fi.TheLine ());
      }

      // already read, now write it
      //

      fileName = fileName + ".COPY4TEST";
      System.out.println ("leidas " + nl + " lines, writing on [" + fileName + "] ...");

      if (!fi.fopen (fileName, "w"))
      {
         System.out.println ("file [" + aa[0] + "] could not be opened!");
         return;
      }

      while (se.getNextLine ())
         fi.writeLine (se.getLastReadLine ());

      fi.fclose ();
      System.out.println ("done.");
   }
}
