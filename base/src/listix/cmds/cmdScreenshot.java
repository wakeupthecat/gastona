/*
library listix (www.listix.org)
Copyright (C) 2014-2020 Alejandro Xalabarder Aulet

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

/*
   //(o) WelcomeGastona_source_listix_command DUMP UNIT

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       SCREENSHOT
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdScreenshot
   <importance> 2
   <desc>       //Makes a screenshot


   <help>
      //
      // Makes a screenshot
      //

   <aliases>
      alias
      SCREEN
      SNAPSHOT
      SNAP

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Makes a screenshot

   <syntaxParams>
      synIndx, name             , defVal  , desc
         1   , fileNameBase     ,         , //Filename without extension, it may contain directory path and also the extension as well. If it is ended with "-" or "+" then a timestamp will be added automatically and at the end the extension
         1   , formatName       , jpg     , //An informal name of the format (jpg, png, .. same parameter as in javax.imageio.ImageIO::write)
         1   , xpos             ,         , //Left x position of the screen in pixels, if starts with "%" then it is a percentage
         1   , ypos             ,         , //Top y position of the screen in pixels, if starts with "%" then it is a percentage
         1   , dx               ,         , //Width in pixels of the snapshot, if starts with "%" then it is a percentage
         1   , dy               ,         , //Height in pixels of the snapshot, if starts with "%" then it is a percentage

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      screenshot example

   <screenshot example>
      //#javaj#
      //
      //   <frames> mPantalla, "Center of the screen"
      //
      //#listix#
      //
      //    <main0>
      //       VAR=, tmppng, @<:lsx tmp png>
      //       SCREEN, @<tmppng>, png, %35, %35, %30, %30
      //       -->, mPantalla data!,, @<tmppng>
      //
      //


#**FIN_EVA#
*/

package listix.cmds;

import java.io.*;
import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.zServices.*;

public class cmdScreenshot implements commandable
{
   private logger log = null;

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "SCREENSHOT",
            "SCREEN",
            "SNAPSHOT",
            "SNAP",
         };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      int nPar = commandEva.cols(indxComm);

      String fileName = cmd.getArg(0);
      String formatName  = cmd.getArg(1);
      String x0 = cmd.getArg(2);
      String y0 = cmd.getArg(3);
      String dx = cmd.getArg(4);
      String dy = cmd.getArg(5);

      log = that.log();
      snappa (fileName, formatName, x0, y0, dx, dy);

      cmd.checkRemainingOptions ();
      return 1;
   }

   void out (String msg)
   {
      if (log != null)
         log.dbg (2, "SCREENSHOT", msg);
      else
         System.out.println (msg);
   }

   void err (String msg)
   {
      if (log != null)
         log.err ("SCREENSHOT", msg);
      else
         System.err.println (msg);
   }

   protected void snappa (String namebase, String fileType, String posX, String posY, String dimX, String dimY)
   {
      // Solving file
      //
      String fileName = namebase;

      if (namebase.endsWith ("-") || namebase.endsWith ("+"))
      {
         DateFormat hoxe = new DateFormat("yyyyyMMdd_hhmmss_S");
         fileName += hoxe.get();
      }
      if (! fileName.endsWith ("." + fileType))
         fileName += ("." + fileType);

      File filo = fileUtil.getNewFile (fileName);
      if (filo == null)
      {
         err ("bad fileName [" + filo.getPath () + "]");
         return;
      }

      // make dirs
      String parent = filo.getParent ();
      if (parent != null && parent.length () > 0)
      {
         File destDir = new File2 (filo.getParent ());
         destDir.mkdirs ();
      }

      // Solving dimensions
      //
      Dimension screenSize = null;
      try
      {
          screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      }
      catch (Exception e)
      {
         err ("getting the screen properties " + e);
         return;
      }

      int maxX = (int)screenSize.getWidth();
      int maxY = (int)screenSize.getHeight();
      int oX = posX.startsWith ("%") ? (int) (0.01 * maxX * stdlib.atoi (posX.substring(1))): stdlib.atoi (posX);
      int oY = posY.startsWith ("%") ? (int) (0.01 * maxY * stdlib.atoi (posY.substring(1))): stdlib.atoi (posY);
      if (oX < 0) oX = 0;
      if (oY < 0) oY = 0;
      int dX = dimX.startsWith ("%") ? (int) (0.01 * maxX * stdlib.atoi (dimX.substring(1))): stdlib.atoi (dimX);
      int dY = dimY.startsWith ("%") ? (int) (0.01 * maxY * stdlib.atoi (dimY.substring(1))): stdlib.atoi (dimY);
      if (dX == 0) dX = maxX - oX;
      if (dY == 0) dY = maxY - oY;
      if (oX >= maxX || oY >= maxY || dX < 0 || dY < 0)
      {
         err ("Wrong dimensions, no file generated!");
         return;
      }

      try
      {
         Rectangle screenRect = new Rectangle(oX, oY, dX, dY);

         out ("saving screen " + screenRect + " on " + filo.getName ());

         Robot robot = new Robot();
         BufferedImage image = robot.createScreenCapture(screenRect);
         ImageIO.write(image, fileType, filo);
      }
      catch(Exception e)
      {
         err ("Exception getting or saving screenshot " + e);
      }
    }
}
