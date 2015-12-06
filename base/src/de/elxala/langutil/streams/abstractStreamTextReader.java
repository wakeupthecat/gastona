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
   @date   30.12.2009 20:10

   Abstract base class for a thread that reads an input stream and separate text lines
   based on \r\n (0x0A 0x0D) or \r or \n

   Derived classes has to provided a container for the read lines which are provided by
   the base class through the abstract method "addLine (String)".

   Implementation examples may have a list of Strings, or save the read text into a file or simply
   print out the read lines.


   ------ Example print out ---------------------------------------------------

      public class readerPOut extends abstractStreamTextReader
      {
         public int contLn = 0;

         public readerPOut (InputStream ins)
         {
            super(ins);
         }

         public void addLine (String strline)
         {
            System.out.println (++ countLn + ") [" + strline + "]");
         }
      }


   ------ Example : using it with Runtime.exec --------------------------------

         // create the process
         //
         Process proc = Runtime.getRuntime().exec ("javac");

         // create the stream passes (process "stderr" to our "stderr" and also with "stdout")
         //
         readerPOut buffErr = new readerPOut (proc.getErrorStream ());
         readerPOut buffOut = new readerPOut (proc.getInputStream ());

         // star them
         //
         buffErr.start ();
         buffOut.start ();

         // wait for process finalization
         //
         int exitVal = proc.waitFor();

         // wait for reading buffers finalization
         //
         while (!buffOut.hasFinished () || !buffErr.hasFinished ())
            Thread.sleep (50);

         System.out.println ("STDOUT " + buffOut.contLn + " lines");
         System.out.println ("STDERR " + buffErr.contLn + " lines");

*/
public abstract class abstractStreamTextReader extends Thread
{
   protected static logger log ()
   {
      return streamPass.log ();
   }

   // abstract method to be implemted
   public abstract void addLine (String strline);

   // these abstract methods are not really necessary for the abstract implemetation
   // but are useful as interface for derived classes
   public abstract int countLines ();
   public abstract List getAsList ();
   public abstract String getLine (int nline);


   private static int MAX_STREAM_BUFFER = 1000;

   private InputStream INstream = null;

   private List textBuffer = null;
   private boolean lastWasReturn = false;
   private String  currentLine = "";

   private boolean finished = false;

   public abstractStreamTextReader (InputStream ins)
   {
      textBuffer = new Vector();
      lastWasReturn = false;
      currentLine = "";
      INstream = ins;
   }

   public boolean hasFinished ()
   {
      return finished;
   }

   public void run ()
   {
      finished = false;
      textBuffer = new Vector();
      lastWasReturn = false;
      currentLine = "";

      try
      {
         BufferedReader BReader = new BufferedReader (new InputStreamReader (INstream), 8192);

         char [] subafa = new char[MAX_STREAM_BUFFER+1];
         int cantos = 0;

         do
         {
            cantos = BReader.read(subafa, 0, MAX_STREAM_BUFFER);
            if (cantos > 0)
            {
               fillLine (subafa, cantos);
            }
            if (cantos == 0)
            {
               Thread.sleep (50);
            }
         } while (cantos >= 0);
      }
      catch (IOException ioe)
      {
         log().severe ("abstractStreamTextReader::run", "exception " + ioe);
         //ioe.printStackTrace ();
      }
      catch (InterruptedException ie)
      {
         log().severe ("abstractStreamTextReader::run", "exception " + ie);
         //ie.printStackTrace ();
      }

      // if is there content add the last one
      if (currentLine.length () > 0)
         addLine (currentLine);  // <<< call abstract method ("callback" addLine)

      finished = true;
   }

   //(o) devnote_algorithms_reading manually RT LF

   private void fillLine (char [] buffer, int size)
   {
      int first = 0;
      int cc = -1;

      while (++cc < size)
      {
         if (buffer[cc] == 10 && lastWasReturn)
         {
            first ++;
            continue;
         }
         if (buffer[cc] == 13 || buffer[cc] == 10)
         {
            lastWasReturn = (buffer[cc] == 13);
            currentLine += new String (buffer, first, cc-first);

            addLine (currentLine);  // <<< call abstract method ("callback" addLine)

            first = cc + 1;
            currentLine = "";
         }
      }
      currentLine += new String (buffer, first, cc-first);
   }
}
