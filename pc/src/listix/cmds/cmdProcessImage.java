/*
Copyright (C) 2019-2026 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command PROCESS IMAGE

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       PROCESS IMAGE
   <groupInfo>  data_graph
   <javaClass>  listix.cmds.cmdProcessImage
   <importance> 3
   <desc>       //Replaces a bmp with a png

   <help>
      //
      //  Operations on an image, replacing the original one. Currently two operations are possible:
      //       - Convert a BMP to PNG
      //       - Crop an image
      //

   <aliases>
      alias
      PROC IMG


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    2      , //Process an image, values for options can be given as formulas, for example "DIM / 2" where DIM is a variable

   <syntaxParams>
      synIndx, name          , defVal, desc
         1   , 2PNG          ,     , //
         1   , srcFileName   ,     , //Bitmap source file name
         1   , tgtFileName   ,     , //Bitmap target file name
         2   , CROP          ,     , //
         2   , srcFileName   ,     , //Source image file name (any known extension)
         2   , tgtFileName   ,     , //Target image file name (any known extension)
         2   , offsetX       ,     , //Offset x to crop (remove from left offsetX pixels)
         2   , offsetY       ,     , //Offset y to crop (remove from top offsetY pixels)
         2   , finalWidth    ,     , //If greater than 0 specify the maximal width of the final image
         2   , finalHeight   ,     , //If greater than 0 specify the maximal height of the final image

   <options>
      synIndx, optionName  , parameters     , defVal    , desc
        1   , KEEP BACKUP , 0/1            , 0         , //Set to 1 if want to keep a backup of the origial image
        1   , CROP          , "x,y,dx,dy", //Crops the image
        1   , RESIZE        , "dx,dy", //Resizes the image
        1   , SCALE         , "sx,sy", //Scales the image
        1   , ADJUST        , "d1,d2,type", // two final dimensions to reach, type can be 0 = chose orientation, or 1 = x,y

   <examples>
      gastSample

      cropAndRenameFiles
      screenDemo

   <cropAndRenameFiles>
      // NOTE: This example does not actually call the command PROCESS IMAGE,
      //       simply remove the start "//" in order to activate it
      //
      //#javaj#
      //
      //   <frames> oConsol
      //
      //#data#
      //
      //   <OUTDIR> sortida
      //
      //   <PREFIX> NovaSerie-
      //
      //   <IMGNUM> 10001
      //
      //#listix#
      //
      //   <main>
      //      LOOP, FILES, ., png
      //          ,, //processing @<fileName> ... (commented out!)
      //          ,, //PROCESS IMAGE, CROP, @<fullPath>, @<OUTDIR>/@<PREFIX>@<TOCANOM>.png, 0, 50, 0, 400
      //      //
      //      //fi!
      //
      //   <TOCANOM>
      //      STRCONV, SUBSTR, @<IMGNUM>, 2, 4
      //      NUM=, IMGNUM, IMGNUM + 1

   <screenDemo>
      //#javaj#
      //
      //   <frames> main, ScreenDemo v0.1, 650
      //
      //   <lay main>
      //      EVA, 5, 5, 3, 3
      //
      //         , X   , X
      //         , lCrop, lResize
      //       X , mCrop, mResize
      //         , lScale, -
      //       X , mScale, -
      //
      //#data#
      //
      //   <lCrop>   //Crop 200, 160, 300, 200
      //   <lResize> //Resize 200, 300
      //   <lScale>  //Scale 0.3, (0.3)
      //
      //#listix#
      //
      //   <main0>
      //
      //      VAR=, tmpSCREEN, @<:lsx tmp png>
      //      VAR=, tmpCROP,   @<:lsx tmp png>
      //      VAR=, tmpRESIZE, @<:lsx tmp png>
      //      VAR=, tmpSCALA,  @<:lsx tmp png>
      //
      //      SCREEN, @<tmpSCREEN>, png
      //
      //      PROCESS IMAGE, 2PNG, @<tmpSCREEN>, @<tmpCROP>.png
      //		             , CROP, 200, 160, 300, 200
      //
      //      PROCESS IMAGE, 2PNG, @<tmpSCREEN>, @<tmpRESIZE>.png
      //                   , RESIZE, 200, 300
      //
      //      PROCESS IMAGE, 2PNG, @<tmpSCREEN>, @<tmpSCALA>.png
      //                   , SCALE, 0.3
      //
      //      -->, mCrop data!,, @<tmpCROP>.png
      //      -->, mResize data!,, @<tmpRESIZE>.png
      //      -->, mScale data!,, @<tmpSCALA>.png
      //


#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.streams.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

import java.io.File;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

/**
   Listix command PROCESS IMAGE

*/
public class cmdProcessImage implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "PROCESS IMAGE",
          "PROCESS IMG",
          "PROC IMG",
       };
   }

   listixCmdStruct cmd = null;

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      // listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);
      cmd = new listixCmdStruct (that, commandEva, indxComm);

      // pass parameters (solved)
      //
      String opt = cmd.getArg(0);
      String srcFileName  = cmd.getArg(1);
      String tgtFileName  = cmd.getArg(2);
      // if (!cmd.checkParamSize (1, 1)) return 1;

      // boolean optKeepBackup = "1".equals (cmd.takeOptionString(new String [] { "KEEPOLD", "KEEPBACKUP", "BACKUP" }, "0" ));

      boolean opt2png = cmd.meantConstantString (opt, new String [] { "PNG", "2PNG", "BMP2PNG" });
      boolean optCrop = cmd.meantConstantString (opt, new String [] { "CROP", "CUT" });

      // Read the src image
      //
      BufferedImage destimg = openImage (srcFileName);
      if (destimg == null)
      {
         cmd.getLog().err ("PROCESSIMAGE", "Cannot load image [" + srcFileName + "]!");
      }
      else
      {
         String [] opts = cmd.getRemainingOptionNames ();

         // do the crop of own CROP syntax .... to be deprecated ?
         if (optCrop)
         {
            //                      0        1           2           3        4           5         6
            //      PROCESSIMAGE,  CROP,  srcFileName, tgtFileName, offsetX, offsetY, finalWidth, finalHeight
            destimg = crop (destimg,
                             calcFormulas.calcFormula (that, cmd.getArg(3)),
                             calcFormulas.calcFormula (that, cmd.getArg(4)),
                             calcFormulas.calcFormula (that, cmd.getArg(5)),
                             calcFormulas.calcFormula (that, cmd.getArg(6))
                            );
         }

         String [] optPars = null;
         for (int oo = 0; oo < opts.length; oo ++)
         {
            optPars = cmd.takeOptionParameters (new String [] { "CROP"});
            if (optPars != null && optPars.length > 0)
            {
                destimg = crop (destimg,
                                calcFormulas.calcFormula (that, optPars[0]),
                                calcFormulas.calcFormula (that, optPars.length > 1 ? optPars[1]: "0"),
                                calcFormulas.calcFormula (that, optPars.length > 2 ? optPars[2]: "0"),
                                calcFormulas.calcFormula (that, optPars.length > 3 ? optPars[3]: "0")
                              );
            }

            optPars = cmd.takeOptionParameters (new String [] { "RESIZE"});
            if (optPars != null && optPars.length > 0)
            {
               destimg = resize (destimg,
                                 calcFormulas.calcFormula (that, optPars[0]),
                                 optPars.length > 1 ? calcFormulas.calcFormula (that, optPars[1]): destimg.getHeight ()
                                );
            }

            optPars = cmd.takeOptionParameters (new String [] { "ADJUST", "NORMALIZE", "NORM" });
            if (optPars != null && optPars.length > 0)
            {
               destimg = adjust (destimg,
                                 calcFormulas.calcFormula (that, optPars[0]),
                                 calcFormulas.calcFormula (that, optPars.length > 1 ? optPars[1]: optPars[0]),
                                 calcFormulas.calcFormula (that, optPars.length > 2 ? optPars[2]: "0")
                                );
            }

            optPars = cmd.takeOptionParameters (new String [] { "SCALE"});
            if (optPars != null && optPars.length > 0)
            {
               double scaleX = calcFormulas.calcFormula (that, optPars[0]);
               double scaleY = optPars.length > 1 ? calcFormulas.calcFormula (that, optPars[1]): scaleX;

               destimg = resize (destimg, (int) (scaleX * destimg.getWidth ()),
                                          (int) (scaleY * destimg.getHeight ())
                                         );
            }
         }
      }

      // finally save the image to file
      if (tgtFileName.length () == 0)
         tgtFileName = srcFileName + ".png";
      fileUtil.ensureDirsForFile (tgtFileName);
      File tgtfile = fileUtil.getNewFile(tgtFileName);
      try {
         ImageIO.write(destimg, "png", tgtfile);
      } catch (Exception error) {
         cmd.getLog().err ("PROCESSIMAGE", "Cannot write target image [" + tgtfile.getName () + "]!");
      }
      cmd.checkRemainingOptions ();
      return 1;
   }

   public BufferedImage adjust (BufferedImage img, int d1, int d2, int orientXY)
   {
      int orx = img.getWidth ();
      int ory = img.getHeight ();
      boolean land = orx > ory;  // origin is landscape
      boolean dand = d1 > d2;    // target is landscape

      cmd.getLog().dbg (0, "adjust", "orx ory land " + orx + ", " + ory + ", " + land);
      cmd.getLog().dbg (0, "adjust", "orx ory dand " + d1 + ", " + d2 + ", " + dand);

      // choose the target dx, dy from the origin form
      int tgx = (land ^ dand) ? d2: d1;
      int tgy = (land ^ dand) ? d1: d2;

      boolean originWider = (double) orx / (double) ory > (double) tgx / (double) tgy;

      int passx = originWider ? (int) (tgy * (double) orx / (double) ory): tgx;
      int passy = originWider ? tgy: (int) (tgx * (double) ory / (double) orx);


// logMSG(listix_command): adjust : orx ory land 480, 610, false
//
// logMSG(listix_command): adjust : orx ory dand 512, 768, false
//
// logMSG(listix_command): adjust : originWider false resize to passx passy 512, 650


      cmd.getLog().dbg (0, "adjust", "originWider " + originWider + " resize to passx passy " + passx + ", " + passy);

      BufferedImage pass = resize (img, passx, passy);

      if (passx > tgx)
      {
         cmd.getLog().dbg (0, "adjust", "wanna crap " + ((passx - tgx) / 2) + ", 0, " + tgx + ", " + tgy);
         return crop (pass, (passx - tgx) / 2, 0, tgx, tgy);
      }
      else
      {
         cmd.getLog().dbg (0, "adjust", "wanna crap 0, " + ((passy - tgy) / 2) + ", " + tgx + ", " + tgy);
         return crop (pass, 0, (passy - tgy) / 2, tgx, tgy);
      }
   }

    public BufferedImage adjust (BufferedImage img, double d1, double d2, double orientXY)
    {
        return adjust (img, (int) d1, (int) d2, (int) orientXY);
    }

    public BufferedImage crop (BufferedImage img, int x0, int y0, int dx, int dy)
    {
        if (x0 < 0 || y0 < 0)
        {
            cmd.getLog().err ("crop", "error cannot crop x0, y0 " + x0 + ", " + y0);
            return img;
        }

        return img.getSubimage (x0 > 0 ? x0: 0,
                              y0 > 0 ? y0: 0,
                              dx > 0 ? dx: img.getWidth (),
                              dy > 0 ? dy: img.getHeight ()
                             );
    }

    public BufferedImage crop (BufferedImage img, double x0, double y0, double dx, double dy)
    {
        return crop (img,  (int) x0, (int) y0, (int) dx, (int) dy);
    }

   public BufferedImage resize (BufferedImage img, int newW, int newH)
   {
      Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
      BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

      Graphics2D g2d = dimg.createGraphics();
      g2d.drawImage(tmp, 0, 0, null);
      g2d.dispose();

      return dimg;
   }

   public BufferedImage resize (BufferedImage img, double newW, double newH)
   {
       return resize (img, (int) newW, (int) newH);
   }

   private BufferedImage openImage (String srcFileName)
   {
      BufferedImage image;
      File imgFile = new File2 (srcFileName);

      try {
         image = ImageIO.read(imgFile);
      } catch (Exception error) {
         return null;
      }
      return image;
   }
}
