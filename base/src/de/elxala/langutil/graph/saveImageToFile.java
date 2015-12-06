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
   @author Alejandro Xalabarder
   @date   19.01.2005 19:53

   Facility to encode a image into a png file

   Example of use (see also de/elxala/pp/langutil/graph/pp_saveImageToPNG.java):

      // -----------------------------------
      String PNG_FILE = "example.PNG";

		Image imagin = aVisibleComponent.createImage (100, 100);

		drawSomeImage (imagin.getGraphics ());
   	if (savePNGtoFile (PNG_FILE, imagin))
   	{
   	   System.out.println ("the image has been saved on " + PNG_FILE);
   	}
      // -----------------------------------
*/

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;


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
      saveImageToFile.save ("test1.png", "png", myCreateImage ());
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
      saveImageToFile.save ("argonmaio.png", "png", myClaraDiagram());
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

public class saveImageToFile
{
////////   public static void main (String[] args) throws IOException
////////   {
////////      if(args.length == 0)
////////      {
////////         System.out.println("Converts an image into a png file");
////////         System.out.println("Usage: imageFile");
////////         return;
////////      }
////////      private Icon theImage = null;
////////      theImage = javaLoad.getSomeHowImageIcon (helper.ebs ().getText ());
////////      theImage.write ()
////////
////////   }

	public static boolean save (String fileName, String formatName, BufferedImage ima)
	{
	   boolean ok = true;

       // Write generated image to a file
       try
       {
           // Save as PNG
           File file = new File(fileName);
           ImageIO.write(ima, formatName, file);
       }
       catch (IOException e)
       {
         ok = false;
       }
		return ok;
	}
}
