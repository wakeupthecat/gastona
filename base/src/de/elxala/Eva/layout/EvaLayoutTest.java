/*
package de.elxala.langutil
(c) Copyright 2006 Alejandro Xalabarder Aulet

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

package de.elxala.Eva.layout;

import java.awt.*;
import javax.swing.*;
import de.elxala.Eva.*;
import de.elxala.Eva.layout.*;

/**
   @author    Alejandro Xalabarder
   @date      11.04.2006 22:32

*/
public class EvaLayoutTest extends JPanel
{
   private Color backColor = Color.WHITE;
   private Color lineColor = Color.RED;
   private EvaLayout gaston = new EvaLayout();

   public EvaLayoutTest(Eva evalay)
   {
      setEvaLayoutInfo (evalay);
   }

   public Dimension getPreferredSize ()
   {
      return gaston.preferredLayoutSize(null);
   }

   public Dimension getMinimumSize ()
   {
      return gaston.minimumLayoutSize(null);
   }

   public void setEvaLayoutInfo (Eva evalay)
   {
      removeAll ();
      gaston = new EvaLayout(evalay);
      setLayout (gaston);

      String [] widget = gaston.getWidgets();
      for (int ii = 0; ii < widget.length; ii ++)
      {
         add(widget[ii], new JButton(widget[ii]));
      }
      updateUI ();
   }


   public void paint (Graphics gra)
   {
      super.paint (gra);

      gra.setColor (lineColor);
      setBackground (backColor);

      // compute MAXY
      int MAXY = gaston.Vmargin;
      for (int ii = 0; ii < gaston.VdimPref.length; ii ++)
      {
         if (ii > 0)
            MAXY += gaston.Vgap;
         MAXY += gaston.VdimPref[ii];
      }
      MAXY += gaston.Vmargin;

      // draw horizontal lines and compute MAXX
      int x = gaston.Hmargin;
      gra.drawLine (x, 0, x, MAXY);
      for (int ii = 0; ii < gaston.HdimPref.length; ii ++)
      {
         if (ii > 0)
         {
            x += gaston.Hgap;
            gra.drawLine (x, 0, x, MAXY);
         }

         x += gaston.HdimPref[ii];
         gra.drawLine (x, 0, x, MAXY);
      }
      int MAXX = x + gaston.Hmargin;

      // draw vertical lines
      int y = gaston.Vmargin;
      gra.drawLine (0, y, MAXX, y);
      for (int ii = 0; ii < gaston.VdimPref.length; ii ++)
      {
         if (ii > 0)
         {
            y += gaston.Vgap;
            gra.drawLine (0, y, MAXX, y);
         }
         y += gaston.VdimPref[ii];
         gra.drawLine (0, y, MAXX, y);
      }
   }

   private static Color letterColor (String color)
   {
      if (color.length() < 1) return Color.BLACK;

      char col = color.toUpperCase().charAt(0);
      switch (col)
      {
         case 'R': return Color.RED;
         case 'G': return Color.GREEN;
         case 'B': return Color.BLUE;
         case 'O': return Color.ORANGE;
         case 'Y': return Color.YELLOW;
         case 'M': return Color.MAGENTA;
         case 'C': return Color.CYAN;
         case 'D': return Color.DARK_GRAY;
         case 'W': return Color.WHITE;
         case 'P': return Color.PINK;
         default: return Color.BLACK;
      }
   }

   public void launchFrame ()
   {
      JFrame frame = new JFrame("EvaLayoutTest for \"" + gaston.getEva().getName() + "\"");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      frame.setContentPane (this);

      //Display the window.
      frame.pack();
      frame.setSize (getPreferredSize ());
      frame.setVisible(true);
   }

   public void launchFrame (Color BackgroundColor, Color LineColor)
   {
      if (BackgroundColor != null)
         backColor = BackgroundColor;
      if (LineColor != null)
         lineColor = LineColor;
      launchFrame ();
   }

   public static void main(String[] aa)
   {
      System.out.println ("arkonsos! " + de.elxala.langutil.jsys.getCallerButNotMe ());
      if (aa.length < 3)
      {
         System.out.println ("EvaLayoutTest v0.0.1");
         System.out.println ("(c) 2006 Alejandro Xalabarder");
         System.out.println ("Parameters of the syntax call:");
         System.out.println ("   fileName unitName layoutName [ BackgroundColor [ LineColor ] ]");
         System.out.println ("");
         System.out.println ("Example: ");
         System.out.println (" Create a test file with following contents");
         System.out.println (" and then run EvaLayoutTest with parameters \"test.eva\" \"data\" \"mylayout\"");
         System.out.println ("---------- file test.eva -------------------");
         System.out.println ("#data#");
         System.out.println ("");
         System.out.println ("   <mylayout>");
         System.out.println ("       EvaLayout, 15, 15, 7, 5");
         System.out.println ("");
         System.out.println ("       ---,  A     ,  100  ,  X ,");
         System.out.println ("        A , lname  , eName ,    ,");
         System.out.println ("        A , lobserv, xObs  , -  ,");
         System.out.println ("        X ,        ,   +   ,    ,");
         System.out.println ("#**FIN_EVA#");
         System.out.println ("---------- ------------ -------------------");
         return;
      }

      String fileName = aa[0];
      String unitName = aa[1];
      String evaName = aa[2];
      Color backgroudC = (aa.length > 3) ? letterColor (aa[3]): null;
      Color linesC     = (aa.length > 4) ? letterColor (aa[4]): null;

      EvaUnit uni = EvaFile.loadEvaUnit (fileName, unitName);
      if (uni == null)
      {
         System.err.println ("ERROR in EvaLayoutTest call : either the file " + fileName + " or the unit " + unitName + " inside it are not found!");
         return;
      }
      Eva lay = uni.getEva(evaName);
      if (lay == null)
      {
         System.err.println ("ERROR in EvaLayoutTest call : the eva " + evaName + " is not found in unit " + unitName + " of the file " + fileName + "!");
         return;
      }

      EvaLayoutTest layoa = new EvaLayoutTest (lay);

      //System.err.println ("estos son mis colores " + backgroudC + ", " + linesC);

      layoa.launchFrame (backgroudC, linesC);

      //createAndShowGUI(evaName, lay);
   }
}
