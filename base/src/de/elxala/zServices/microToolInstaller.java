/*
package de.elxala.langutil
(c) Copyright 2005..2019 Alejandro Xalabarder Aulet

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

package de.elxala.zServices;


import java.io.File;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.mensaka.*;

/**
<pre>

   microToolInstaller


   20.07.2014 support Raspberry PI by including arm architecture checking
              deprecate old getExeSqlite and do an exception only in case of Mac OX


   31.08.2013 Going back to the old approach since a instalation of Ubuntu DOES NOT INCLUDE sqlite3 by default!!!
                Also the microtool concept has to be imposed, we want to pack ruby, lua, perl and python for linux as well
                then these languages would be really part of gastona "out of the box" (at least gastona PC!)

   14.10.2009 --OLD OLD OLD---- NOTE ABOUT OBTAINING CURRENT sqlite muTool !
   -------------------------------------------------------------------------------------------

   Due to problems with long temporary paths (e.g. in some windows XP anb Mac OS x), the feature
   microToolInstaller is not really used in the only microTool at the moment of gastona.jar (sqlite)
   See method microToolInstaller.getExeSqlite ()

      - For linux and Mac OSx the tool is expected as default installed on /usr/bin/sqlite3
        (no sqlite.bin jared anymore)
      - For windows it will be installed always and for all instances on %TEMP%\sqlite3.exe
        from the "jared" META-GASTONA/muTools/sqlite3.exe

     NOTE: this new approach obliges in Windows to check always the existence of sqlite3.exe
           before any call. For intance:
               - an application install it
               - another app found it (do not install it) and use it
               - the first app is closed and REMOVES the sqlite3.exe !!
               - the second want to use it again (whithout checking it) and FAILS!

   -------------------------------------------------------------------------------------------

   We call in this context "intern tool" to such external programs in binary form that a
   java application wraps in its own jar file to be deployed/installed and used if it is
   called during the run of the application (see at the end of the file pros and cons of this approach).

   For that purpose microToolInstaller offers following static methods for the applications

      - existsInternTool (String toolLogicName)

         returns true if the internTool is available. Note that the same program might find
         the tool available if running under Windows while not available if running under Linux
         or viceversa.

      - getExeToolPath (String toolLogicName)

         returns the full path of the binary (exe etc) file representing the tool
         the simple call to this method causes the installation of the micro tool if it were not
         already installed

      - setCacheDirectory (String path)

         an application might choose to set this directory using this method
         if an application do it:
            then all the micro tools will be installed (when needed)
            onto the directory <cachedirectory>/muTools and this directory will be kept
         if an application don't do it
            all the micro tools will be installed (when needed) onto the
            directory <temp>/muToolsXXXXX/
            where XXXXX will be different (at least not existent) for each java VM
            if the application has a successfully exit this directory should be deleted automatically

   Preparing a jar containing intern tools
   ---------------------------------------

      1) pack the files in the jar file of the application
      -----------------------------------------------------
         pack the tool and all the files it needs in a single directory under the
         path META-GASTONA/muTools/win if the tool is windows specific or under
         META-GASTONA/muTools/linux if the tool is for linux

         the chosen name for the directory will become the "module" name

         for example if we have a tool called myTool for both windoes and linux
         we could copy the needed files under

            META-GASTONA/muTools/winOS/myTool    for a windows specific tool and
            META-GASTONA/muTools/linuxOS/myTool  for a linux specific tool
            META-GASTONA/muTools/allOS/myTool    for a platform independent tool


       2) declare this tool in a line in the file META-GASTONA/muTools/muToolsManifest.eva
       ---------------------------------------------------------------------------------

         for example:
            #data#

               <tableTools>
                  logicName , modul  , winOS    , linuxOS   , linuxOS64, allOS

                  tool1     , myTool , tool1.exe, tool1.bin ,
                  tool2     , myTool , tool2.exe,           ,



       3) declare all the files for each module also in in the file META-GASTONA/muTools/muToolsManifest.eva
       ---------------------------------------------------------------------------------

         for example:
            #data#

                <module myTool winOS>
                  fileName
                  tool1.exe
                  tool2.exe

                <module myTool linuxOS>
                  fileName
                  tool1.bin

                <module myTool allOS>
                  fileName
                  petamas/mydbhelpful.db


*/
public class microToolInstaller
{
   private static String MUTOOLS_MANIFEST = "META-GASTONA/muTools/muToolsManifest.eva";
   private static String MUTOOLS_DIR = "muToolRemovableDir";

   private static Eva     installedTools   = new Eva ("* installedTools");
   private static Eva     installedModules = new Eva ("* installedModules");
   private static EvaUnit IInfoMain = EvaFile.loadEvaUnit (MUTOOLS_MANIFEST, "data");

   private static logger log = new logger(null, "de.elxala.zServices.microToolInstaller", null);

   private static boolean temporalPolicy = false; // CHANGED on 2016.11.20 due to antivirus checks on each extraction (e.g. about 6 seconds to check sqlite each time!)
   private static String baseDir = null;

   private static boolean checkHavingInfo ()
   {
      if (IInfoMain != null) return true;
      log.err ("checkHavingInfo", "file " + MUTOOLS_MANIFEST + " not found or unit #data# not found inside!");

      return false;
   }

   /**
      returns true if the tool is declared as internal tool
      in the file "ToolInstallerV2.eva", evaunit "intern tools" and eva
      "win ToolList" or "linux ToolList" depending on the platform.
   */
   public static boolean existsInternTool (String toolLogicName)
   {
      if (! checkHavingInfo ()) return false;

      Eva table = IInfoMain.getEva("tableTools"); // in reality binaryName
      if (table == null)
      {
         return false;
      }

      int indx = table.rowOf (toolLogicName, table.colOf("logicName"), false);
      return indx > 0;  // indx 0 would be column header "logicName" which is not allowed!
   }

   //2016.11.20 New finalFullPath
   //   Motivation1: the target directory name does not have to be OS dependent, e.g. for windows always "win" etc no danger of conflict!
   //   Motivation2: antiviruses now wants to check first execution of a binary extracted from a jar (i.e. in a temporary file)
   //                this breaks seamly the philosophy of microtool so the only solution is to
   //                create a removable directory in a temporary directory (i.e. tmpdir/gastonaTMP/muTools656) but don't remove it 
   //                actively on exit so the next execution the binaries will be find and the antivirus already know them.
   //                NOTE: this approach is only for microTools and the number of these directories would only increase with 
   //                      new micro-tooling versions (rare)
   //   
   private static String finalFullPath (String moduleName, String binaryName)
   {
      String full = baseDir + "/" + moduleName + "/" + binaryName;
      return full.replace ('/', File.separatorChar);
   }
   
   //2016.11.20 Old finalFullPath
   //
   //private static String finalFullPath (String osString, String moduleName, String binaryName)
   //{
   //   String full = baseDir + osString + "/" + moduleName + "/" + binaryName;
   //   return full.replace ('/', File.separatorChar);
   //}

   //
   // this method does not look up the intern tool configuration but
   // directly assigns the full path where the tool can be found
   // toolPath empty or null removes the the tool full path
   //
   public static void assignPath4MuTool (String toolLogicName, String toolPath)
   {
      boolean unInstall = toolPath == null || toolPath.length () == 0;
      int indx = installedTools.rowOf (toolLogicName);
      if (indx != -1)
      {
         if (unInstall)
              installedTools.removeLine (indx);
         else installedTools.setValue (toolPath, indx, 1);
      }
      else
      {
         if (!unInstall)
            installedTools.addLine (new EvaLine (new String [] { toolLogicName , toolPath } ));
      }
      if (unInstall)
           log.dbg (5, "assignPath4MuTool", "micro tool \"" + toolLogicName + "\" assumed to be installed into path \"" + toolPath + "\"");
      else log.dbg (5, "assignPath4MuTool", "micro tool \"" + toolLogicName + "\" removed from list");
   }

   public static String getExeToolPath (String toolLogicName)
   {
      // 20.07.2014 still keep this hardcoding since it cannot be checked on MacOX with sqlite for linux (no Mac, no Mac virtual Box!)
      //            (it worked well but then not anymore...)
      if (utilSys.isOSNameMacOX () && toolLogicName.equalsIgnoreCase("sqlite"))
      {
         return "/usr/bin/sqlite3";
      }
      
      // already installed ? then return the full path
      //
      int indxCache = installedTools.rowOf (toolLogicName);
      if (indxCache != -1)
      {
         String fullpath = installedTools.getValue (indxCache, 1);
         log.dbg (5, "getExeToolPath", "micro tool \"" + toolLogicName + "\" already installed, path \"" + fullpath + "\"");
         return fullpath;
      }

      // workaround for antivirus checking each time a new temporary binary is extracted and used for the first time
      // Only for sqlite & windows: if the executable sqlite3.exe is found in the directory then use it!
      //
      if (utilSys.isOSNameWindows () && toolLogicName.equalsIgnoreCase("sqlite"))
      {
         if ((new File ("sqlite3.exe")).exists ())
         {
            log.dbg (2, "getExeToolPath", "sqlite is local!");
            return "sqlite3.exe";
         }
         else log.dbg (2, "getExeToolPath", "sqlite is NOT local!");
      }

      if (! checkHavingInfo ())
      {
         log.dbg (5, "getExeToolPath", "micro tool no configuration found, returning placebo \"" + toolLogicName + "\"");
         return toolLogicName;
      }
      log.dbg (5, "getExeToolPath", "micro tool \"" + toolLogicName + "\"");

      // look up the table
      Eva table = IInfoMain.getEva("tableTools");
      if (table == null)
      {
         log.dbg (5, "getExeToolPath", "table <tableTools> not found!");
         return "";
      }
      int indx = table.rowOf (toolLogicName, table.colOf("logicName"), false);
      if (indx < 1)
      {
         log.dbg (5, "getExeToolPath", "micro tool not configured, returning placebo \"" + toolLogicName + "\"");
         return toolLogicName;
      }

      String OSstring = utilSys.getSysString (); // either winOS, linuxOS, linux64 or linuxArm
      String nameModul =  table.getValue (indx, table.colOf ("modul"));
      String nameBinary = table.getValue (indx, table.colOf ("allOS"));

      if (nameBinary.length () > 0)
           OSstring = "allOS";
      else nameBinary = table.getValue (indx, table.colOf (OSstring));

      Eva evaModul = IInfoMain.getEva("module " + nameModul + " " + OSstring);
      if (evaModul == null)
      {
         log.err ("getExeToolPath", "micro tool " + toolLogicName + " requires module " + "<module " + nameModul + " " + OSstring + "> which is not found!");
         return "";
      }

      // is already installed the module ?
      int indxModule = installedModules.rowOf (evaModul.getName ());
      if (indxModule != -1)
      {
         // ok, then it should be easy to find ...
         String dirBase = installedModules.getValue (indxModule, 1);

         String fullPath = finalFullPath (nameModul, nameBinary);
         File este = fileUtil.doubleCheckFile (fullPath);

         if (este.exists ())
            return fullPath;

         log.err ("getExeToolPath", "micro tool " + toolLogicName + " module " + "<modul" + OSstring + " " + nameModul + "> installed ok, but file " + fullPath + " not found!");
         return "";
      }

      // decide which directory is base for muTools
      if (baseDir == null)
      {
         if (temporalPolicy)
         {
            baseDir = fileUtil.createTempDir ("muTools", null, temporalPolicy) + File.separatorChar;
         }
         else
         {
            // this directory will be created in the temporal directory (e.g. c:/tmp/gastonaTMP) but it will not 
            // be automatically deleted! It is supposed that MUTOOLS_DIR changes if some tool change
            //
            String dirname = "muToolsTMPsolo";
            Eva edir = IInfoMain.getEva (MUTOOLS_DIR);
            if (edir != null) dirname = edir.getValue ();
            baseDir = uniFileUtil.getTemporalDirApp () + File.separatorChar + dirname;
         }
      }

      log.dbg (5, "getExeToolPath", "procede to install module <" + evaModul.getName () + "> on [" + baseDir + "]");

      // full path is formed, since it was not found before in installedTools
      // we register it now, even before knowing if it can be properly installed
      // (do it below), the reason is that if it cannot be installed once why should be
      // tried more times ?
      //
      String fullPath = finalFullPath (nameModul, nameBinary);
      installedTools.addLine (new EvaLine (new String [] { toolLogicName , fullPath } ));

      // register the installation of the module
      //
      installedModules.addLine (new EvaLine (new String [] { evaModul.getName (), baseDir, (temporalPolicy ? "1": "0") } ));

      for (int ii = 1; ii < evaModul.rows (); ii ++)
      {
         String source = "META-GASTONA/muTools/" + OSstring + "/" + nameModul + "/" + evaModul.getValue (ii, 0);
         String target = finalFullPath (nameModul, evaModul.getValue (ii, 0));
         log.dbg (4, "getExeToolPath", "copying \"" + source + "\" to \"" + target + "\"");

         if (installFileFromJar (source, target, temporalPolicy))
         {
            if (utilSys.isSysUnix)
            {
               log.dbg (4, "getExeToolPath", "chmod 700 " + target);
               //(o) JAVA EXEC !!!!
               javaRun.execute ("chmod 700 " + target);
            }
         }
         else
         {
            log.fatal ("getExeToolPath", "file \"" + source + "\" could not copied to \"" + target + "\"");
            // return "";
         }
         if (temporalPolicy)
         {
            File fio = new File (target);
            fio.deleteOnExit ();
         }
      }


      //   NUEVO CONFIGURADOR 2016.11.20
      //
      //   META-GASTONA/muTools/muToolsManifest.eva
      //
      //
      //      #data#
      //
      //         <muToolRemovableDir> muToolsTMP1611
      //
      //         <tableTools>
      //            logicName , modul  , winOS        , linuxOS    , allOS
      //
      //            sqlite    , sqlite , sqlite.exe   , sqlite.bin ,
      //            dot       , graphw , dot.exe      ,
      //            neator    , graphw , neato.exe    ,
      //            gastHelpDB, gastH  ,              ,            , gastonaHelp.db
      //
      //         <module sqlite winOS>
      //            fileName
      //            sqlite.exe
      //            daskljlas.ala
      //
      //         <module sqlite linuxOS>
      //            fileName
      //            sqlite.bin
      //
      //         <moduleWin graphw allOS>
      //            fileName
      //            dot.exe
      //            aa.dll
      //            hordermore/handel.dll
      //
      //

      log.dbg (4, "getExeToolPath", "return value \"" + fullPath + "\"");
      return fullPath;
   }


   private static boolean fileExists (String path)
   {
      File eo = fileUtil.doubleCheckFile (path);
      return eo.exists ();
   }

   public static boolean installFileFromJar (String fromFullPath, String toFullPath, boolean deleteOnExit)
   {
      log.dbg (4, "installFileFromJar", "from \"" + fromFullPath + "\" to \"" + toFullPath + "\", deleteOnExit " + deleteOnExit);

      // System.out.println ("la cosa es que me mandan (" + fromFullPath + ", " + toFullPath + ", " + deleteOnExit + ")");
      if (fileExists (toFullPath))
      {
         log.dbg (4, "installFileFromJar", "file \"" + toFullPath + "\" alredy exists, it will not be overwritten!");
         return true;
      }
      
      return fileUtil.copyFile (fromFullPath, toFullPath, deleteOnExit);
   }

   // utility for other classes, is different from installFileFromJar in
   //    - it might overwrite the target file (installFileFromJar would not)
   //    - errors are not fatal
   //
   public static boolean copyFileFromJar (logger log, String fromFullPath, String toFullPath)
   {
      if (log != null)
         log.dbg (4, "copyFileFromJar", "from \"" + fromFullPath + "\" to \"" + toFullPath + "\"");

      return fileUtil.copyFile (fromFullPath, toFullPath);
   }
}



/*
   pros and cons of this approach
   -------------------------------

      + autocontent of the java application, easy to be intalled (just copy the jar)
        transparent for the user, robustness usability (no chance to something to be not
        properly installed)

      + robustness in the version of the tool since the application contains exactly
        the version that works for it. That means the application has a constant behaviour with
        no external dependency.

      + different applications can work with different versions of the tool without conflicts each other.

      + non intrusive applications: if the installation of the tool is configured to be temporal the
        application can be enterelly launched from a media (CD, memory stick etc) in any computer without
        need of any installation  and not leaving any file on that computer after the use of the application.

      - some files cannot be shared in some circumstances between several tools (**).

      - (12.10.2009 18:01) The implementation of muTools has a problem: It uses temporary directories
        and files. In some cases (XP, Mac OS) the temporary directory path is quite long and adding
        something like "gastonaTMP/muTools47754tmpDir/linuxOS/sqlite/sqlite.bin" can produce a too
        long path very quickly (56 char)

      (**) Sharing tools is a not very clean thing and for small tools does not worth at all
           The first goals of sharing tools are

         a) disc space saving
            this is nowadays irrelevant!

         b) improve all applications that share a tool when a new version of the tool is installed.
            Even in the case the new tool introduce just bug fixing or claim to be full compatible there
            are still the risk for an application to not work properly with the new version. Thus such an
            installation requires so much knowledge and care that becomes a hard work of maintenance.
            It doesn't worth at all, even less for small tools and makes the applications less robust because
            of its probable changing behaviour.



-----------------------------------------
CONCEPTOS

   nombre de tool:        por ejemplo "sqlite" sin exe o bin, el nombre concreto lo tiene que dar "ToolInstallUtil" (o como se llame!)

   modulo de instalacion: por ejemplo "dotUtil" puede contener varias tools (dot.exe, neato.exe etc)
                          fi'sicamente es un directorio a instalar (no recursivo por ahora)

                          para cada SO se resolvera el path (p.e. "internTools/win/" + "dotUtil")

   binaryName:            Nombre del fichero ejecutable (sin path). por ejemplo: sqlite.exe, neato.exe etc

   targetHomeSubDir:      Si esta' en blanco sera' un directorio temporal (por supuesto el nombre no importa!)
                          Si NO esta' en blanco sera' un subdirectorio a partir del home del usuario, en ese
                          caso deberi'a un nombre "suficientemente u'nico"
                          por ejemplo:
                              claraDiagram
                              elxalaSqlite
                              org.listix/sqlite
                              org.javaj/sqlite

   nombre fisico tool:    se forma a partir de targetHomeSubDir y binaryName
                          por ejemplo tmp88172sqlite/sqlite.bin
                                      tmp71722dotUtil/dot.exe
                                      etc...
                          lo debe generar ToolInstallUtil


-----------------------------------------
*/