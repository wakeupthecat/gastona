/*
packages de.elxala
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

/**   ======== de.elxala.langutil.urlUtil ==========================================
   Alejandro Xalabarder

   07.04.2009 21:11
*/

import java.io.*;
import java.util.List;
import java.util.Vector;

import java.io.InputStream;   // for files from jar file (resources)
import java.net.URL;
// import java.net.URI;
import de.elxala.langutil.*;
import de.elxala.zServices.logger;

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
      catch (Exception  e)
      {
         //another exception ? => error
         logStatic.err ("existsUrl", "Exception checking URL \"" + urlName + "\" : " + e);
      }
      return false;
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
logStatic.dbg (2, "copyUrlResource", "PHASO !");
         URL urla = new URL (urlName);    // it cannot return null, or ?
         InputStream is = urla.openStream ();

         if (is == null)
         {
            // should not happen!
            logStatic.severe ("copyUrlResource", "cannot open urlName [" + urlName + "]!");
            return false;
         }

logStatic.dbg (2, "copyUrlResource", "PHASO2 ! fileToWrite ["  + fileToWrite + "]");
         if (fileToWrite == null)
            fileToWrite = dirTarget + urla.getFile ();

logStatic.dbg (2, "copyUrlResource", "PHASO2.1 ! fileToWrite ["  + fileToWrite + "]");
         // make dir if necessary
         File elfile = new File (fileToWrite);
         File eldir  = elfile.getParentFile ();
         if (eldir != null && !eldir.exists ())
         {
            logStatic.dbg (2, "copyUrlResource", "creating directories " + eldir);
            eldir.mkdirs ();
         }
////         String tupare = elfile.getParent();
////logStatic.dbg (2, "copyUrlResource", "PHASO2.2 ! elfile ["  + elfile + "]");
////logStatic.dbg (2, "copyUrlResource", "PHASO2.22 ! elfile ["  + elfile + "] elfileParent " + elfile.getParent());
////         if (tupare != null)
////         {
////            File eldir  = new File (tupare);
////logStatic.dbg (2, "copyUrlResource", "PHASO2.3 ! eldir ["  + eldir + "]");
////            if (!eldir.exists ())
////            {
////               logStatic.dbg (2, "copyUrlResource", "creating directories " + eldir);
////               eldir.mkdirs ();
////            }
////         }
logStatic.dbg (2, "copyUrlResource", "PHASO3 !");

         logStatic.dbg (2, "copyUrlResource", "copying [" + urlName + "] on [" + fileToWrite + "] ...");
         FileOutputStream roso = new FileOutputStream (fileToWrite);

         int charo = -1;
         int kanto = 0;

         do
         {
            charo = is.read ();
            if (charo != -1)
            {
               roso.write (charo);
               kanto ++;
            }
         } while (charo != -1);

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
         roso.close ();
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
