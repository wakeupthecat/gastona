/*
packages de.elxala
Copyright (C) 2020  Alejandro Xalabarder Aulet

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

package de.elxala.langutil.streams;

import de.elxala.zServices.logger;
import java.io.*;
import java.net.URL;
import de.elxala.langutil.filedir.*;

/**
   class streamURLRipper.java
   @author Alejandro Xalabarder Aulet
   @date   2020


*/
public class streamURLRipper extends Thread
{
   protected static logger log ()
   {
      return streamPass.log ();
   }

   public static final int REC_STATUS_IDLE   = 0;
   public static final int REC_STATUS_READY  = 1;
   public static final int REC_STATUS_REC    = 2;
   public static final int REC_STATUS_DOSTOP = 3;
   public static final int REC_STATUS_CONTINUOUS = 4;

   protected int recStatus = REC_STATUS_IDLE;

   protected String URLName = null;
   protected String fileToWrite = null;

   public void stopGlobal ()
   {
      if (recStatus > REC_STATUS_IDLE)
         recStatus = REC_STATUS_DOSTOP;
   }

   public streamURLRipper (String urlName, String fullPathTarget)
   {
      URLName = urlName;
      fileToWrite = fullPathTarget;
   }

   public boolean changeTarget (String fullPathTarget)
   {
      fileToWrite = fullPathTarget;
      recStatus = REC_STATUS_CONTINUOUS;
      return true;
   }

   public void run ()
   {
      recStatus = REC_STATUS_IDLE;

      try
      {
         URL urla = new URL (URLName);    // it cannot return null, or ?
         InputStream is = urla.openStream ();

         if (is == null)
         {
            // should not happen!
            log ().severe ("streamURLRipper", "cannot open urlName [" + URLName + "]!");
            return;
         }

         //LOOP CONTINUOUS SAVE
         do {
            if (! fileUtil.ensureDirsForFile (fileToWrite))
            {
               log ().err ("streamURLRipper", "cannot create directories for target file [" + fileToWrite + "]");
               return;
            }

            TextFile tfOutput = new TextFile ();
            if (!tfOutput.fopen (fileToWrite, "wb"))
            {
               log ().err ("streamURLRipper", "target file [" + fileToWrite + "] could not be opened for write");
               return;
            }

            log ().dbg (2, "streamURLRipper", "copying [" + URLName + "] on [" + fileToWrite + "] ...");

            // recording ...
            recStatus = REC_STATUS_REC;
            int len = 0;
            byte [] buff = new byte [1024];
            while(recStatus == REC_STATUS_REC && (len = is.read(buff)) != -1)
               tfOutput.writeBytes (buff, len);

            // end recording ...

            tfOutput.fclose ();
            log ().dbg (2, "streamURLRipper", "closing file");
         }
         while (recStatus == REC_STATUS_CONTINUOUS);

         // close url stream
         is.close ();
         recStatus = REC_STATUS_IDLE;
      }
      catch (Exception e)
      {
         log ().severe ("streamURLRipper", "Exception reading from " + URLName + " or writing to " + fileToWrite + "! " + e);
         return ;
      }
   }
}
