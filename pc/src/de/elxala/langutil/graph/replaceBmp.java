/*
package de.elxala.langutil
(c) Copyright 2007-2013 Alejandro Xalabarder Aulet

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

package de.elxala.langutil.graph;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import javax.imageio.*;
import javax.imageio.stream.*;

import java.awt.image.*;
import de.elxala.zServices.*;
import de.elxala.langutil.filedir.*;

/**
      19.08.2007 21:25
      09.06.2013


*/
public class replaceBmp
{
   private static logger log = new logger (null, "de.elxala.langutil.graph.replaceBmp", null);

   public static boolean replaceBMPwithPNG (String ficher)
   {
      return convertBMPtoPNG (ficher, false);
   }

   public static boolean convertBMPtoPNG (String ficher)
   {
      return convertBMPtoPNG (ficher, true);
   }

   public static boolean convertBMPtoPNG (String ficher, boolean keepOld)
   {
      String fixOut = (ficher.length () >= 4) ?
                           ficher.substring (0, ficher.length () - 4):
                           ficher;
      fixOut += ".png";

      FileInputStream inStream = null;
      Image theImage = null;
      try
      {
         inStream = new FileInputStream (ficher);
         if (inStream != null)
            theImage = BMPLoader.read (inStream);
      }
      catch (Exception e) 
      {
         log.err ("convertBMPtoPNG", "exception reading file [" + ficher + "] " + e);
         return false;
      }
      
      if (inStream == null || theImage == null)
      {
         log.err ("convertBMPtoPNG", "error loading bmp from [" + ficher + "]");
         return false;
      }

       log.dbg (2, "convertBMPtoPNG", "pasando a png [" + ficher + "] ...");

      // but we need a BufferedImage and not an Image ...
      // .. so create a BufferedImage from a Image (the way I found!)
      // .. Note : maybe there is another way, or the best solution is to make BMPLoader to return a BufferedImage etc..
      //
      dummyImageObserver pipo = new dummyImageObserver ();
      BufferedImage buim = new  BufferedImage((int) theImage.getWidth (pipo), (int) theImage.getHeight (pipo), BufferedImage.TYPE_INT_RGB);
      buim.createGraphics().drawImage (theImage, null, null);
      //

      uniUtilImage.saveBufferedImageTofile (buim, fixOut, "png");

      if (!keepOld)
      {
         File fix = fileUtil.getNewFile (ficher);
         fix.delete ();
      }

      return true;
   }

   public static void replaceBMPwithPNG (String [] filearr)
   {
      convertBMPtoPNG (filearr, false);
   }
   
   public static void convertBMPtoPNG (String [] filearr)
   {
      convertBMPtoPNG (filearr, true);
   }
   
   public static int convertBMPtoPNG (String [] filearr, boolean keepOld)
   {
      return recConvertBMPtoPNG ("", filearr, keepOld);
   }

   private static int recConvertBMPtoPNG (String baseDir, String [] filearr, boolean keepOld)
   {
      int count = 0;
      for (int ii = 0; ii < filearr.length; ii ++)
      {
         String fileX =  (baseDir.length () > 0 ? baseDir+"/": "") + filearr[ii];
         //System.out.println ("count " + count + " porosesu " + fileX);
         File fileFi = fileUtil.getNewFile(fileX);
         if (fileFi.isDirectory ())
         {
            //System.out.println ("     guaita es un carpetonci!");
            count += recConvertBMPtoPNG (fileX, fileFi.list (), keepOld);
         }
         else if (fileFi.isFile ())
         {
            //System.out.println ("     guaita is a file!");
            if (fileX.length () > 4 && (fileX.endsWith (".bmp") || fileX.endsWith (".BMP")))
               if (convertBMPtoPNG (fileX, keepOld)) 
                  count ++;
         }
      }
      return count;
   }
}
