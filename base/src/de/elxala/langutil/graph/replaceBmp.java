package de.elxala.langutil.graph;

import java.io.*;
import java.awt.*;
import javax.swing.*;

import javax.imageio.*;
import javax.imageio.stream.*;

import java.awt.image.*;

/**
      19.08.2007 21:25


*/
public class replaceBmp
{
   private static boolean garabaPNG (String ficher) throws Exception
   {
      String fixOut = (ficher.length () >= 4) ?
                           ficher.substring (0, ficher.length () - 4):
                           ficher;
      fixOut += ".png";

      FileInputStream in = new FileInputStream (ficher);
      Image theImage = de.elxala.langutil.graph.BMPLoader.read (in);
      if (theImage == null)
      {
         System.err.println ("(theImage == null) algo ha pasado con [" + ficher + "]");
         return false;
      }

      System.out.println ("pasando a png [" + ficher + "] ...");

      // but we need a BufferedImage and not an Image ...
      // .. so create a BufferedImage from a Image (the way I found!)
      // .. Note : maybe there is another way, or the best solution is to make BMPLoader to return a BufferedImage etc..
      //
      dummyImageObserver pipo = new dummyImageObserver ();
      BufferedImage buim = new  BufferedImage((int) theImage.getWidth (pipo), (int) theImage.getHeight (pipo), BufferedImage.TYPE_INT_RGB);
      buim.createGraphics().drawImage (theImage, null, null);
      //

      de.elxala.langutil.graph.saveImageToFile.save (fixOut, "png", buim);

      File fix = new File (ficher);
      fix.delete ();

      return true;
   }


   /*
      to test the class

      place one bitmap or more in a directory and call from it

      jav de.elxala.langutil.graph.replaceBmp .

      then the bitmaps will be converted to png's (and the bmp's removed)
   */
   public static void main (String [] aa) throws Exception
   {
      for (int ii = 0; ii < aa.length; ii ++)
      {
         File fix = new File (aa[ii]);
         if (fix.isDirectory ())
         {
            String [] bipmaps = fix.list ();
            for (int jj = 0; jj < bipmaps.length; jj ++)
            {
               if (bipmaps [jj].length () > 4 &&
                   (bipmaps [jj].endsWith (".bmp") ||
                    bipmaps [jj].endsWith (".BMP"))
                  )
                  garabaPNG (bipmaps [jj]);
            }
         }
         else if (fix.isFile ())
         {
            garabaPNG (aa[ii]);
         }
      }
   }
}
