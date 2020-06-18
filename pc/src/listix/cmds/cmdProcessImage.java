/*
Copyright (C) 2019-2020 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command JAVA STATIC

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
         1   ,    2      , //Process an image

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
        1    , KEEP BACKUP , 0/1            , 0         , //Set to 1 if want to keep a backup of the origial image

   <examples>
      gastSample

      cropAndRenameFiles

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

      // pass parameters (solved)
      //
      String opt = cmd.getArg(0);
      String srcFileName  = cmd.getArg(1);
      String tgtFileName  = cmd.getArg(2);
      // if (!cmd.checkParamSize (1, 1)) return 1;

      // boolean optKeepBackup = "1".equals (cmd.takeOptionString(new String [] { "KEEPOLD", "KEEPBACKUP", "BACKUP" }, "0" ));

      boolean opt2png = cmd.meantConstantString (opt, new String [] { "2PNG", "BMP2PNG" });
      boolean optCrop = cmd.meantConstantString (opt, new String [] { "CROP", "CUT" });

      if (opt2png)
      {
         cmd.getLog().err ("PROCESSIMAGE", "Syntax [" + opt + "] not implemented yet!");
         // de.elxala.langutil.graph.replaceBmp.convertBMPtoPNG (new String [] { file }, optKeepOld);
      }

      if (optCrop)
      {
         //                      0        1           2           3        4           5         6
         //      PROCESSIMAGE,  CROP,  srcFileName, tgtFileName, offsetX, offsetY, finalWidth, finalHeight

         int offsetX = stdlib.atoi (cmd.getArg(3));
         int offsetY = stdlib.atoi (cmd.getArg(4));
         int finalWidth = stdlib.atoi (cmd.getArg(5));
         int finalHeight = stdlib.atoi (cmd.getArg(6));


         BufferedImage image = openImage (srcFileName);
         if (image == null)
         {
            cmd.getLog().err ("PROCESSIMAGE", "Cannot load image [" + srcFileName + "]!");
         }
         else
         {
            BufferedImage destimg = image.getSubimage(offsetX, offsetY,
                                    finalWidth > 0 ? finalWidth: image.getWidth (),
                                    finalHeight > 0 ? finalHeight: image.getHeight ());

            if (tgtFileName.length () == 0)
               tgtFileName = srcFileName + ".png";
            fileUtil.ensureDirsForFile (tgtFileName);
            File tgtfile = fileUtil.getNewFile(tgtFileName);
            try {
               ImageIO.write(destimg, "png", tgtfile);
            } catch (Exception error) {
               cmd.getLog().err ("PROCESSIMAGE", "Cannot write target image [" + tgtfile.getName () + "]!");
            }
         }
      }

      cmd.checkRemainingOptions ();
      return 1;
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
