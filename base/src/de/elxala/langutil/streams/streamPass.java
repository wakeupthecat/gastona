/*
package de.elxala.langutil
(c) Copyright 2005-2022 Alejandro Xalabarder Aulet

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
import de.elxala.zServices.*;

/**
   @brief task that reads and printout (optional) a stream until end of stream (used in javaRun.java)

   @author Alejandro Xalabarder
   @date   05.08.2004

   31.03.2008 01:22 Use the methods read/write (char [] ...) instead of readLine and println.
                    this has brought a spectacular improvement in performance. Example try a
                    a "type file" as command to execute (for instance with a console de.elxala.langutil.consolas.Consola)

   Example: How to use this with Runtime.exec

         // create the process
         //
         Process proc = Runtime.getRuntime().exec ("javac");

         // create the stream passes (process "stderr" to our "stderr" and also with "stdout")
         //
         StreamPass passErr = new StreamPass (proc.getErrorStream (), System.err);  // proc's error stream is "stderr"
         StreamPass passOut = new StreamPass (proc.getInputStream (), System.out);  // proc's input stream is "stdout" !!

         // star them
         //
         passErr.start ();
         passOut.start ();

         // wait for process finalization
         //
         int exitVal = proc.waitFor();

         //NOTE: if something were stored in the streamPass it would be necessary to
         //      wait for its finalization to get the data safely
         //
         // while (!passOut.hasFinished ()) Thread.sleep (50);

   Note:
   This class was inspired (the version with readline)
   in the sample StreamGobbler.java from the article
   "When Runtime.exec () won't" by Michael C.Daconta
   https://www.infoworld.com/article/2071275/when-runtime-exec---won-t.html?page=2

*/
public class streamPass extends Thread
{
   private static logger logStatic = null;
   protected static logger log ()
   {
      if (logStatic == null)
         logStatic = new logger (null, "de.elxala.langutil.streams", null);
      return logStatic;
   }

   InputStream IS_ = null;
   OutputStream OS_ = null;

   private boolean finished = false;

   public streamPass (InputStream ins)
   {
      IS_ = ins;
      OS_ = System.out;
   }

   public streamPass (InputStream ins, OutputStream outs)
   {
      IS_ = ins;
      OS_ = outs;
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
         PrintWriter PW = (OS_ != null) ? new PrintWriter (OS_): null;
         BufferedReader BR_ = new BufferedReader (new InputStreamReader (IS_));

         char [] subafa = new char[901];
         int quants = 0;

         do
         {
            quants = BR_.read(subafa, 0, 900);
            if (quants > 0 && PW != null)
            {
               PW.write (subafa, 0, quants);
               PW.flush ();
            }
            if (quants == 0)
            {
               Thread.sleep (50);
            }
         } while (quants >= 0);

         // Note : cannot close the stream (we do not own it)
         if (PW != null) PW.flush ();
      }

      catch (IOException ioe)
      {
         log().severe ("streamPass::run", "IOException " + ioe);
         //ioe.printStackTrace ();
      }
      catch (InterruptedException ie)
      {
         log().severe ("streamPass::run", "InterruptedException " + ie);
         //ie.printStackTrace ();
      }

      finished = true;
   }
}
