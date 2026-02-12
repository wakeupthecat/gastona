/*
packages de.elxala
Copyright (C) 2005-2020  Alejandro Xalabarder Aulet

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

/**   ======== de.elxala.langutil.urlUtil ==========================================
   Alejandro Xalabarder

   07.04.2009 21:11
*/

import de.elxala.zServices.logger;
import java.io.*;
import java.net.URL;
import de.elxala.langutil.streams.*;


/**
   class urlUtil
   @author Alejandro Xalabarder Aulet
   @date   2009

   Class to facilitate urls

*/
public class urlUtil
{
   private static logger logStatic = new logger (null, "de.elxala.langutil.filedir.urlUtil", null);

   public static boolean urlExists (String urlName)
   {
      try
      {
         URL urla = new URL (urlName);    // it cannot return null, or ?
         InputStream is = (urla != null) ? urla.openStream (): null;

         if (is != null)
         {
            is.close ();
            return true;
         }
      }
      catch (java.net.UnknownHostException e) {}
      catch (java.net.MalformedURLException e) {}
      catch (java.io.FileNotFoundException e) {}
      catch (Exception  e)
      {
         //another exception ? => error
         logStatic.err ("existsUrl", "Exception checking URL \"" + urlName + "\" : " + e);
      }
      return false;
   }

   protected static streamURLRipper rippo = null;
   protected static String useURLstreamSource = null;

   public static boolean streamURLStart (String urlName, String targetFileName)
   {
      if (urlName != null && urlName.length () > 0)
      {
         useURLstreamSource = urlName;
      }

      if (!urlExists (useURLstreamSource))
      {
         // error already logged
         return false;
      }

      rippo = new streamURLRipper  (useURLstreamSource, targetFileName);
      if (rippo == null)
         return false;

      rippo.start ();
      return true;
   }

   public static boolean streamURLContinue (String targetFileName)
   {
      return (rippo != null) ? rippo.changeTarget (targetFileName): streamURLStart (null, targetFileName);
   }

   public static void streamURLStop ()
   {
      if (rippo != null)
         rippo.stopGlobal ();

      rippo = null;
   }

   public static boolean copyUrl (String urlName, String dirTarget, String fullPathTarget)
   {
      if (!(dirTarget == null ^ fullPathTarget == null))
      {
         //bad use
         logStatic.severe ("copyUrlResource", "Bad use! either dirTarget or fullPathTarget has to be provided but not both, dirTarget["+dirTarget+"] fullPathTarget["+fullPathTarget+"]");
         return false;
      }

      if (!urlExists (urlName))
      {
         // already logged error
         return false;
      }

      String fileToWrite = fullPathTarget; // by the moment ..

      try
      {
//logStatic.dbg (2, "copyUrlResource", "PHASO !");
         URL urla = new URL (urlName);    // it cannot return null, or ?
         InputStream is = urla.openStream ();

         if (is == null)
         {
            // should not happen!
            logStatic.severe ("copyUrlResource", "cannot open urlName [" + urlName + "]!");
            return false;
         }

//logStatic.dbg (2, "copyUrlResource", "PHASO2 ! fileToWrite ["  + fileToWrite + "]");
         if (fileToWrite == null)
            fileToWrite = dirTarget + urla.getFile ();

         // NEW CODE (from 2015.02.01 23:39)
         //
         if (! fileUtil.ensureDirsForFile (fileToWrite))
         {
            logStatic.err ("copyUrlResource", "cannot create directories for target file [" + fileToWrite + "]");
            return false;
         }

         TextFile tfOutput = new TextFile ();
         if (!tfOutput.fopen (fileToWrite, "wb"))
         {
            logStatic.err ("copyUrlResource", "target file [" + fileToWrite + "] could not be opened for write");
            return false;
         }

         logStatic.dbg (2, "copyUrlResource", "copying [" + urlName + "] on [" + fileToWrite + "] ...");
         int charo = -1;
         int kanto = 0;


         //(o) TODO_writing text files since we read in blocks of 1024 is quite probable that return line feed pairs (13+10)
         //    are packed together and then serialTextBuffer.writeString handles them correctly
         //    but if we write one by one two new lines are added.
         //    REVIEW this method using new byte [1];
         //
         int len = 0;
         byte [] buff = new byte [1024];
         while((len = is.read(buff)) != -1)
            tfOutput.writeBytes (buff, len);

         //         NOTA : No parece que vaya más rápido con un buffer
         //
         //         byte [] torrent = new byte[1024000];
         //
         //         do
         //         {
         //            tantos = is.read (torrent);
         //            if (tantos > 0)
         //            {
         //               roso.write (torrent, 0, tantos);
         //               kanto += tantos;
         //            }
         //         } while (tantos > 0);


         // System.out.println ("Me he cargado " + kanto + " caracteres! ellos se hallan en [" + urla.getFile () + "] seguramente");
         tfOutput.fclose ();
         is.close ();
      }
      catch (Exception e)
      {
         logStatic.severe ("copyUrlResource", "Exception reading from " + urlName + " or writing to " + fileToWrite + "! " + e);
         return false;
      }
      return true;
   }
}
