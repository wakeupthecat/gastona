/*
library listix (www.listix.org)
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

/*
   //(o) WelcomeGastona_source_listix_command RESUTIL

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       RESUTIL
   <groupInfo>  system_files
   <javaClass>  listix.cmds.cmdResourceUtil
   <importance> 3
   <desc>       //Utilities for resources, urls and micro-tools

   <help>
      //
      // Utilities for checking and copying resources and the named Gastona micro-tools.
      // Meaning by resource that file that can be found within the current java class path,
      // typically read only, for example a file found into a jar file.
      // A Gastona micro-tool is a resource file that will be extracted automatically
      // when needed acording with some rules defined in the resource file
      // META-GASTONA/muTools/muToolsManifest.eva.
      //
      // Gastona has at least one micro-tool which is sqlite (sqlite.exe or sqlite.bin for linux) and
      // it will be extracted onto a temporary directory when required internally either by Listix
      // commands or by Javaj zWidgets related with database operations.
      //

   <aliases>
      alias
      RESOURCE
      RES


<!      RESUTIL, EXISTS, urlName
<!      RESUTIL, EXISTS URL, urlName
<!      RESUTIL, MINI TOOL, miniToolName, tool sqlite
<!      RESUTIL, COPY       , URLname, basepath
<!               , NEWNAME    , borica.gif
<!               , AUTOSUBPATH, 1
<!               , ALGORITHM  , 1
<!      RESUTIL, STREAM     , URLname, basepath
<!               , MB LIMIT   , (100)
<!               , MAX MB FILE, (5)
<!               , BASENAME   , (STREAM)
<!               , EXTENSION  , (mp3)

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5       , //Checks the existence of a resource file within the java class path. Return 1 if found 0 if not found
         2   ,    5      , //Checks the existence of a resource file as url. Return 1 if found 0 if not found
         3   ,    5      , //Return the fullpath of the executable program related with the micro tool 'microToolName'. If the micro tool is not yet installed then it will be previously installed
         4   ,    5      , //Assigns a call path for the microtool, as a result the micro tool is suposed to be installed and therefore the auto installation of the micro tool will not take place when the micro tool is invoqued
         5   ,    5      , //Copies an existent resource file or url into the directory 'basepath'
         6   ,    5      , //Copies an stream url into files
         7   ,    5      , //Moves a file to another path

   <syntaxParams>
      synIndx, name     , defVal        , desc

         1   , EXISTS   ,               , //
         1   , resourceName,            , //Resource name, in general a class, image or any file to be found in the current java class path

         2   , EXISTS URL,              , //
         2   , urlName  ,               , //URL name

         3   , MICRO TOOL,              , //
         3   , microToolName,           , //Logic name of the micro-tool (for example: sqlite)
         3   , varTarget,               , //If given, name of the Eva variable to store the result, if not given the result will be printed out

         4   , SET MICRO TOOL,          , //
         4   , microToolName,           , //Logic name of the micro-tool (for example: sqlite, ruby etc)
         4   , binaryPath,              , //Either full path or name of the binary for the micro tool in case the OS can resolve its location

         5   , COPY    ,                , //
         5   , resourceName,            , //Resource name, in general a class, image or any file to be found in the current java class path
         5   , basepath   ,             , //?File name target or directory name if the option AUTOSUBPATH?NEWNAME is given

         6   , COPY URL  ,              , //
         6   , resourceName,            , //URL name to be copied
         6   , toDirName  ,             , //Directory name where the file is to be copied

         7   , MOVE    ,                , //
         7   , sourceName ,             , //Path of the file or resource to be moved
         7   , targetName ,             , //?File name target or directory name if the option AUTOSUBPATH?NEWNAME is given

   <examples>
      gastSample

      example resource

   <example resource>

      //#javaj#
      //
      //   <frames> F, "example listix RESOURCE"
      //
      //   <layout of F>
      //
      //   EVA, 10, 10, 5, 5
      //   ---,           ,    X      ,
      //      , lResource , eResource , bCheckResource
      //      , lMicroTool, eMicroTool, bCheckMicroTool
      //      , lUrl      , eUrl      , bCheckUrl
      //    X , oConsola  , -         , -
      //
      //#data#
      //
      //    <eResource>  javaj/img/ok.png
      //    <eMicroTool> sqlite
      //    <eUrl>       http://www.gastona.org/index.html
      //
      //    <bCheckResource> Check
      //    <bCheckMicroTool> Get muTool
      //    <bCheckUrl> Check
      //
      //#listix#
      //
      //   <-- bCheckResource>
      //       //Check resource "@<eResource>" =
      //       RESOURCE, EXISTS, @<eResource>
      //       //
      //       //
      //
      //   <-- bCheckMicroTool>
      //       //Micro tool "@<eMicroTool>" = [
      //       RESOURCE, MICRO TOOL, @<eMicroTool>
      //       //]
      //       //
      //
      //   <-- bCheckUrl>
      //       //Check Url "@<eUrl>" =
      //       RESOURCE, EXISTS URL, @<eUrl>
      //       //
      //       //
      //

#**FIN_EVA#
*/
package listix.cmds;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.javaLoad;
import de.elxala.langutil.filedir.urlUtil;
import de.elxala.langutil.filedir.fileUtil;
import de.elxala.zServices.*;

public class cmdResourceUtil implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
            "RESUTIL",
            "RESOURCE",
            "RES",
         };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command has.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts

   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String subCmd = cmd.getArg(0);

      if (cmd.meantConstantString (subCmd, new String [] { "EXISTS" } ))
      {
         //  RESUTIL, EXISTS, resourceName
         //
         String resName = cmd.getArg(1);
         that.printTextLsx (javaLoad.existsResource (resName) ? "1": "0");
      }
      else if (cmd.meantConstantString (subCmd, new String [] { "EXISTSURL", "URLEXISTS" } ))
      {
         //  RESUTIL, EXISTS URL, urlName
         //
         that.printTextLsx (urlUtil.urlExists (cmd.getArg(1)) ? "1": "0");
      }
      else if (cmd.meantConstantString (subCmd, new String [] { "MINITOOL", "MICROTOOL", "MICRO", "MUTOOL" } ))
      {
         // RESUTIL, MINI TOOL, miniToolName, tool sqlite
         //
         String miniToolName = cmd.getArg(1);
         String evaName = cmd.getArg(2);

         if (evaName.length () > 0)
         {
            Eva theVar = that.getSomeHowVarEva (evaName);
            theVar.clear ();
            theVar.setValue (microToolInstaller.getExeToolPath (miniToolName), 0, 0);
         }
         else
            that.printTextLsx (microToolInstaller.getExeToolPath (miniToolName));

      }
      else if (cmd.meantConstantString (subCmd, new String [] { "SETMICROTOOL", "SETMICROTOOLPATH", "SETTOOLPATH", "SETTOOL", "SETMUTOOL" , "SETMUTOOLPATH" } ))
      {
         String miniToolName = cmd.getArg(1);
         String newpath = cmd.getArg(2);

         microToolInstaller.assignPath4MuTool (miniToolName, newpath);
      }
      else if (cmd.meantConstantString (subCmd, new String [] { "COPY", "COPIA" } ))
      {
         String resourceName = cmd.getArg(1);
         String toFileName = cmd.getArg(2);

         if (!fileUtil.copyFile (resourceName, toFileName))
            that.log ().err ("RESUTIL", "Copy from [" + resourceName + "] to [" + toFileName + "] failed!");
      }
      else if (cmd.meantConstantString (subCmd, new String [] { "MOVE", "MUEVE", "MOU" } ))
      {
         String resourceName = cmd.getArg(1);
         String toFileName = cmd.getArg(2);

         if (!fileUtil.moveFile (resourceName, toFileName))
            that.log ().err ("RESUTIL", "Move from [" + resourceName + "] to [" + toFileName + "] failed!");
      }
      else if (cmd.meantConstantString (subCmd, new String [] { "COPYURL", "URLCOPY" } ))
      {
         String resourceName = cmd.getArg(1);
         String toFileName = cmd.getArg(2);

         if (!urlUtil.copyUrl (resourceName, null, toFileName))
         {
            that.log ().err ("RESUTIL", "URL copy from [" + resourceName + "] to [" + toFileName + "] failed!");
         }
      }

      cmd.checkRemainingOptions ();
      return 1;
   }

/*

<!      RESUTIL, COPY       , URLname, basepath
<!               , NEWNAME    , borica.gif
<!               , AUTOSUBPATH, 1
<!               , ALGORITHM  , 1
<!      RESUTIL, STREAM     , URLname, basepath
<!               , MB LIMIT   , (100)
<!               , MAX MB FILE, (5)
<!               , BASENAME   , (STREAM)
<!               , EXTENSION  , (mp3)
*/

}


/**

CODIGO PARA ......


<!      RESUTIL, COPY       , URLname, basepath
<!               , NEWNAME    , borica.gif
<!               , AUTOSUBPATH, 1
<!               , ALGORITHM  , 1


      Algorithm 2 (de Lestoras.java)
      ----------------------------------------------------

<!      RESUTIL, STREAM     , URLname, basepath
<!               , MB LIMIT   , (100)
<!               , MAX MB FILE, (5)
<!               , BASENAME   , (STREAM)
<!               , EXTENSION  , (mp3)


      de gripea.java
      -------------------------


         public static void grabaUno (String dirOutput, String urlname, int limitBytes, int limitBytesFile)
         {
            String fileToWrite = "?";

            InputStream is = null;
            try
            {
               URL urla = new URL (urlname);
               is = urla.openStream ();

               if (urla == null || is == null)
               {
                  System.out.println ("No hallamos en ello un fichero! url = " + urlname);
                  return;
               }
            }
            catch (Exception e)
            {
               System.out.println (e.toString () + "\nProblems reading from " + urlname + " or writing to " + fileToWrite);
            }


            // make dir if necessary
            File eldir  = new File (dirOutput);
            if (!eldir.exists ())
            {
               System.out.println ("creating directory " + eldir);
               eldir.mkdirs ();
            }

            System.out.println ("ripping " + urlname);
            File yabasta  = new File ("BASTA");

            int total = 0;
            boolean endOfStream = false;
            do
            {
               if (yabasta.exists ())
               {
                  System.out.println ("BASTA!");
                  break;
               }

               String ahora = DateFormat.getStr (new Date (), "yyyy_MM_dd__HH_mm_ss");
               String outFileName = dirOutput + "/" + ahora + ".mp3";

               FileOutputStream roso = null;
               try
               {
                  roso = new FileOutputStream (new File (outFileName));

                  int charo = -1;
                  int kanto = 0;
                  do
                  {
                     charo = is.read ();
                     if (charo != -1)
                     {
                        roso.write (charo);
                        kanto ++;
                        total ++;
                     }

                     if (kanto % 5000 == 0 && yabasta.exists ())
                     {
                        System.out.println ("BASTA!");
                        break;
                     }
                     endOfStream = (charo == -1);
                  } while (kanto < limitBytesFile && total < limitBytes && !endOfStream);
                  System.out.println ("endOfStream = " + endOfStream + " kanto = " + kanto + " limiFile = " + limitBytesFile + " total = " + total + " limitBytes = " + limitBytes);
               }
               catch (Exception e)
               {
                  System.out.println ("" + e);
                  if (roso == null)
                     System.out.println ("Cannot open output file " + outFileName);
                  break;
               }

               try { if (roso != null) roso.close (); } catch (Exception e) {}
            } while (!endOfStream && total < limitBytes);

            try { is.close (); } catch (Exception e) {}
         }



   */

/**
   IDEA

      screen, GET WIDTH
      screen, GET HEIGHT

      screen, GET FONT LIST, evaName

      screen, CAPTURE, X, Y, DX, DY, BOCADILL
            , LIMIT MB   , 100
            , LIMIT FILES, 1
            , INTERVAL   , 1000
            , EXTENSION  , PNG

*/