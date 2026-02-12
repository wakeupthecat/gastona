/*
gastona
Copyright (C) 2015-2021 Alejandro Xalabarder Aulet

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

package org.gastona;

import org.gastona.*;

import java.io.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.mensaka.*;
import de.elxala.langutil.*;
import de.elxala.zServices.*;
import de.elxala.langutil.filedir.fileUtil;
import de.elxala.db.*;

import java.util.*;
import java.io.File;
import de.elxala.zServices.*;


public class commonGastona
{
   public static logger log = new logger (null, "gastona", null);

   public static final String MAINGAST_MEMORYFILE = ":mem _mainGast_";
   public static final String UTF8_PARAM = ":utf8:";
   public static final String UTF8_PARAM2 = ":utf-8:";

   // examines for pattern
   //      :utf8:xxxxxxxx par1 par2
   // if so the memory file ":mem _mainGast_" is created with the decoded utf-8 data and this memory file name is returned
   //
   public static String getGastFileNameAndProcessArgs (String [] aa)
   {
      //(o) gast_INIT_FindMainGast
      // search .gast file
      //
      if (aa != null && aa.length > 0)
      {
         int indxStart = miscUtil.startsWithIgnoreCase (aa[0], UTF8_PARAM) ? UTF8_PARAM.length ():
                         miscUtil.startsWithIgnoreCase (aa[0], UTF8_PARAM2) ? UTF8_PARAM2.length (): 0;
         if (indxStart > 0)
         {
            TextFile memFile = new TextFile ();
            if (memFile.fopen (MAINGAST_MEMORYFILE, "w"))
               memFile.writeString (utilEscapeStr.desEscapeStr (aa[0].substring (indxStart), "UTF-8"));
            memFile.fclose ();
            aa[0] = MAINGAST_MEMORYFILE;
         }
      }

      return searchGastonaApplication (aa);
   }

   // examines for
   //      :mem nnnnnn
   // if so the contents of the memory file ":mem nnnnnn" is encoded into utf-8 ("xxxxx") and
   // the string ":utf8:xxxxxx" is returned
   // that is what gastona main can process
   //
   public static String getGastConformFileName (String fileName)
   {
      if (! TextFile.isMemoryFile (fileName))
         return fileName;

      // is a memory file => encode de contents in utf-8
      // and add the prefix ":utf8:"

      TextFile fmem = new TextFile ();
      String sal = UTF8_PARAM;
      if (fmem.fopen (fileName, "r"))
           sal += utilEscapeStr.escapeStr (fmem.readAllIntoStringBuffer ().toString (), "UTF-8");
      fmem.fclose ();
      return sal;
   }

   public static String [] getReducedArguments (String [] aa)
   {
      if (aa.length < 2) return new String [] {};

      // reduce parameters (arguments) for listix
      String [] bb = new String [aa.length - 1];

      for (int ii = 1; ii < aa.length; ii ++)
      {
         bb[ii-1] = aa[ii];
      }
      return bb;
   }

   /**
      search of gastona application policy:

         1) the first parameter is taken as application.gast file
         2) the autoStart.gast file is found in the current directory
         3) the autoStart.gast file is found in jar file (in the root directory)
         4) start "META-GASTONA/WelcomeGastona/WelcomeGastona.gast"
         5) show copyright & version pop-up
   */
   public static String searchGastonaApplication (String [] aa)
   {
      if (aa != null && aa.length > 0)
      {
         log.dbg (2, "searchingApp", "given in parameter [" + aa[0] + "]");

         //(o) gastona_init collect command line parameters
         //
         System.setProperty("gastona.argumentCount", "" + (aa.length-1));
         for (int ii = 1; ii < aa.length; ii ++)
            System.setProperty("gastona.argument" + ii, "" + aa[ii]);

         return aa[0];
      }
      System.setProperty("gastona.argumentCount", "0");

      // first look for "autoStart.gast" in current directory
      // Note :
      //       This kind of start permit a easy way to start a gastona application
      //       without having to associate the extension .gast to "java -jar gastona.jar"
      //       or in linux start the script with #!java -jar gastona.har
      //
      File normalFile = fileUtil.doubleCheckFile ("autoStart.gast");
      if (normalFile.exists ())
      {
         log.dbg (2, "searchingApp", "found autoStart.gast in current directory");
         return "autoStart.gast";
      }

      TextFile appfile = new TextFile ();

      // then look for "autoStart.gast" in current directory or in classpath
      // Note :
      //       This kind of start permit a easy way to customize a gastona.jar
      //       by simply insert in the root path of gastona.jar a gast script (autoStart.gast)
      //       and all classes needed
      //
      if (appfile.fopen ("autoStart.gast", "r"))
      {
         appfile.fclose ();
         log.dbg (2, "searchingApp", "found somewhere autoStart.gast");
         return "autoStart.gast";
      }

      String welcomeFile = "META-GASTONA/WelcomeGastona/WelcomeGastona.gast";
      // then look for "META-GASTONA/WelcomeGastona/WelcomeGastona.gast"
      // Note :
      //       This is the welcome gastona script
      //
      if (appfile.fopen (welcomeFile, "r"))
      {
         appfile.fclose ();
         log.dbg (2, "searchingApp", "found " + welcomeFile);
         return welcomeFile;
      }
      return null;
   }
}