/*
package de.elxala.langutil
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

/*
*/

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import de.elxala.Eva.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;

/*

Example1:
============================================ test1.java

import de.elxala.langutil.graph.*;
import java.awt.image.*;
import java.awt.*;


public class test1
{
   static public void main (String [] arg)
   {
      uniUtilImage.saveBufferedImageTofile (myCreateImage (), "test1.png", "png");
   }

    public static BufferedImage myCreateImage()
    {
        int width = 100;
        int height = 100;

        // Create a buffered image in which to draw
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Create a graphics contents on the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // Draw graphics
        g2d.setColor(Color.red);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.black);
        g2d.fillOval(0, 0, width, height);

        // Graphics context no longer needed so dispose it
        g2d.dispose();

        return bufferedImage;
    }
}

Example2: with claraDiagram
============================================ test2.java

import de.elxala.langutil.graph.*;
import java.awt.image.*;
import java.io.*;
import java.awt.*;

import de.elxala.novaClara.*;
import de.elxala.Eva.*;


public class test2
{
   static public void main (String [] aa)
   {
      // Create an image to save
      uniUtilImage.saveBufferedImageTofile (myClaraDiagram(), "argonmaio.png", "png");
   }

   public static BufferedImage myClaraDiagram()
   {
      ClaraDiagramDrawer dali = new ClaraDiagramDrawer ();
      dali.setModel   (EvaFile.loadEvaUnit ("diagramExample.eva", "modelo"));
      dali.setControl (EvaFile.loadEvaUnit ("diagramExample.eva", "control"));

      dali.setFont ("monspaced", 11);
      dali.resetCalulatedDraw ();

      Dimension dima = dali.tellPreferredSize ();
      BufferedImage bufferedImage = new BufferedImage((int) dima.getWidth (), (int) dima.getHeight (), BufferedImage.TYPE_INT_RGB);

      dali.render (dima, new Dimension (0, 0), (Graphics2D) bufferedImage.getGraphics ());

      return bufferedImage;
   }
}
*/

public class uniUtilImage
{

   //(o) TODO_uniUtilImage implement "void saveIconTofile (Icon, filename, format) "
   //
//  public static void main (String[] args) throws IOException
//  {
//      // if(args.length == 0)
//      // {
//         // System.out.println("Converts an image into a png file");
//         // System.out.println("Usage: imageFile");
//         // return;
//      // }
//      // private Icon theImage = null;
//      // theImage = javaLoad.getSomeHowImageIcon (args[0]);
//
//      // // but we need a BufferedImage and not an Image ...
//      // // .. so create a BufferedImage from a Image (the way I found!)
//      // // .. Note : maybe there is another way, or the best solution is to make BMPLoader to return a BufferedImage etc..
//      // //
//      // dummyImageObserver pipo = new dummyImageObserver ();
//      // BufferedImage buim = new  BufferedImage((int) theImage.getWidth (pipo), (int) theImage.getHeight (pipo), BufferedImage.TYPE_INT_RGB);
//      // buim.createGraphics().drawImage (theImage, null, null);
//      // //
//      // save (args[0] + ".png", "png", buim);
//  }
//	public static boolean saveIconTofile (BufferedImage ima, String fileName, String formatName)
//	{
//	}


	public static boolean saveBufferedImageTofile (BufferedImage ima, String fileName, String formatName)
	{
	   boolean ok = true;

       // Write generated image to a file
       try
       {
           // Save as PNG
           File file = fileUtil.getNewFile(fileName);
           ImageIO.write(ima, formatName, file);
       }
       catch (IOException e)
       {
         ok = false;
       }
		return ok;
	}

   public static BufferedImage graffitiToBufferedImage (Eva evaGraffiti, String grafFormat)
   {
      return graffitiToBufferedImage (evaGraffiti, grafFormat, 20, 20, null);
   }

   public static BufferedImage graffitiToBufferedImage (Eva evaGraffiti, String grafFormat, int IMG_SX, int IMG_SY, Color backColor)
   {
      graphicObjectLoader oblo = new graphicObjectLoader ();

      if (grafFormat != null && grafFormat.equalsIgnoreCase ("trazos"))
           oblo.loadObjectFromEvaTrazos ("graffititemp", evaGraffiti, null, "111", new offsetAndScale ());
      else oblo.loadObjectFromEva       ("graffititemp", evaGraffiti, null, "111", new offsetAndScale ());

      String imSize = System.getProperty (org.gastona.gastonaCtes.PROP_GASTONA_DEFAULT_GRAFFITTI_SIZE, null);
      if (imSize != null)
         IMG_SX = IMG_SY = de.elxala.langutil.stdlib.atoi (imSize);

      Dimension dima = new Dimension (IMG_SX, IMG_SY);
      BufferedImage bufferedImage = new BufferedImage((int) dima.getWidth (), (int) dima.getHeight (), BufferedImage.TYPE_INT_ARGB);

      Graphics2D ga2 = (Graphics2D) bufferedImage.getGraphics ();

      if (backColor == null)
           ga2.setComposite(AlphaComposite.Clear); // transparent backgound !! (together with BufferedImage.TYPE_INT_ARGB)
      else ga2.setColor (backColor);

      ga2.fillRect (0, 0, IMG_SX, IMG_SY);

      uniCanvas uca = new uniCanvas ((Graphics2D) bufferedImage.getGraphics (), 0, 0, IMG_SX, IMG_SY);

      float scala = oblo.getScaleToFit (IMG_SX, IMG_SY);
      uca.scale (scala, scala);
      uca.translate (oblo.getOffsetXtoFit (), oblo.getOffsetYtoFit ());

      uca.getG ().setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      oblo.paintYou (uca);

      return bufferedImage;
   }
}
