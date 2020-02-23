/*
packages de.elxala
Copyright (C) 2005 Alejandro Xalabarder Aulet

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

package javaj;

import java.awt.*;
import javax.swing.*;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.graph.uniUtilImage;
import javaj.widgets.*;
//import java.awt.image.*;

/**
*/
public class javajEBS extends javajEBSbase
{
   private Image mImageApp = null;
   private EvaUnit euDefaultIcons = null;

   public javajEBS (EvaUnit euData)
   {
      super (euData);
   }

   private Eva getEvaFromDefaultData (String name)
   {
      if (euDefaultIcons == null)
         euDefaultIcons = EvaFile.loadEvaUnit ("META-GASTONA/javaj/defaultIcons.eva", "data");
      if (euDefaultIcons == null) return null;

      return euDefaultIcons.getEva (name);
   }

   // remember DO NOT try to load *.ico images but bmp, gif, png, jpg etc ..
   //
   public Image getImageApp ()
   {
      if (mImageApp == null)
         mImageApp = loadIconImage ("iconApp");
      
         return mImageApp;
   }

   public Image loadIconImage (String imgName)
   {
      ImageIcon imai = javaLoad.getSomeHowImageIcon (getVar(imgName));
      if (imai == null)
      {
         Eva graFormat = getEva(imgName + "GraffitiFormat");
         Eva egraff = getEva(imgName + "Graffiti");
         Eva egraffSize = getEva(imgName + "GraffitiSize");

         // try to get it from META-GASTONA/utilApp/defaultDataJavaj.gast
         if (egraff == null)
         {
            egraff = getEvaFromDefaultData (imgName + "Graffiti");
            egraffSize = getEva(imgName + "GraffitiSize");
         }

         if (egraff != null)
         {
            int dx = (egraffSize != null) ? stdlib.atoi (egraffSize.getValue (0, 0)): 20;
            int dy = (egraffSize != null) ? stdlib.atoi (egraffSize.getValue (0, 1)): 20;
            dx = dx <= 0 ? 20: dx; // default 20
            dy = dy <= 0 ? dx: dy; // default square (like x)

            String gFor = graFormat == null ? "paths": graFormat.getValue ();
            imai = new ImageIcon (uniUtilImage.graffitiToBufferedImage (egraff, gFor, dx, dy, null));
         }
      }

      return (imai == null) ? null: imai.getImage ();
   }

   /**
      returns the initial dimension of the frame at index 'indx' if specified (width and height > 0)
      otherwise returns null
   */
   public Dimension getInitialSizeOfFrame (int indx)
   {
      int pW = stdlib.atoi (getValueOfFrame(indx, 2));
      int pH = stdlib.atoi (getValueOfFrame(indx, 3));

      if (pW != 0 && pH != 0)
         return new Dimension (pW, pH);

      return null;
   }

   /**
      returns the initial dimension of the frame at index 'indx' using currentDim as default
   */
   public Dimension getInitialSizeOfFrame (int indx, Dimension currentDim)
   {
      int pW = stdlib.atoi (getValueOfFrame(indx, 2));
      int pH = stdlib.atoi (getValueOfFrame(indx, 3));

      return new Dimension (pW != 0 ? pW: (int) currentDim.getWidth (),
                            pH != 0 ? pH: (int) currentDim.getHeight ());
   }

   public boolean isFrameDecorated ()
   {
      Eva lookFeel = mainEUData.getEva ("LookAndFeel");
      if (lookFeel == null) return false;

      return lookFeel.getValue (0, 2).equals("1");
   }

   public String newLookAndFeel ()
   {
      Eva lookFeel = mainEUData.getEva ("LookAndFeel");
      if (lookFeel == null) return "";

      return lookFeel.getValue (0, 1);
   }
}