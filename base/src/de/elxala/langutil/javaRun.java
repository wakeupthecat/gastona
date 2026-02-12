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

package de.elxala.langutil;

import java.io.*;
import de.elxala.langutil.streams.*;
import de.elxala.zServices.*;

/**
   @brief class to run/execute an aplication from java without the problem of using solely Runtime.exec

   @author Alejandro Xalabarder
   @date   05.08.2004

   The problem with executing just Runtime.exec and then call waitFor is that if the
   program executed writes output on stderr or stdout there is absolute no handling of
   these streams and the process could be blocked or just not been executed.

   reference article "When Runtime.exec () won't" by Michael C.Daconta
   (https://www.infoworld.com/article/2071275/when-runtime-exec---won-t.html)

   Example of use: see main
*/
public class javaRun
{
   private static logger log = new logger (null, "de.elxala.langutil.javaRun", null);

   /**
      execute and DON'T wait for finalization

      @param command String with command to be launched

      Example :

         boolean done = javaRun.launch ("cmd.exe /C dir");

      @returns true if it could be launched with no errors
   */
   public static boolean launch (String commandSingle)
   {
      return (-1 != gExecute (null, commandSingle, false));
   }

   /**
      execute and DON'T wait for finalization

      @param command String [] with command to be lauched

      Example :

         boolean done = javaRun.launch (new String [] { "cmd.exe", "/C", "dir" });

      @returns true if it could be launched with no errors
   */
   public static boolean launch (String [] commandArray)
   {
      return (-1 != gExecute (commandArray, null, false));
   }


   /**
      execute and wait for finalization

      @param command String with command to be launched

      Example :

         int ret = javaRun.execute ("cmd.exe /C dir");

   */
   public static int execute (String commandSingle)
   {
      return gExecute (null, commandSingle, true);
   }

   public static int executePreShell (String preShell, String commandSingle, boolean wait4termination)
   {
      return gExecutePreShell (preShell, null, commandSingle, wait4termination, false);
   }

   /**
   */
   public static int execute (String commandSingle, boolean silent)
   {
      return gExecute (null, commandSingle, true, silent);
   }

   /**
      execute and wait for finalization

      @param command String [] with command to be launched

      Example :

         int ret = javaRun.execute (new String [] { "cmd.exe", "/C", "dir" });
   */
   public static int execute (String [] commandArray)
   {
      return gExecute (commandArray, null, true);
   }

   public static int executePreShell (String preShell, String [] commandArray, boolean wait4termination)
   {
      return gExecutePreShell (preShell, commandArray, null, wait4termination, false);
   }

   private static int gExecute (String [] commandArray, String commandSingle, boolean wait4Termination)
   {
      return gExecute (commandArray, commandSingle, wait4Termination, false);
   }

   private static int gExecute (String [] commandArray, String commandSingle, boolean wait4Termination, boolean silent)
   {
      return gExecutePreShell (null, commandArray, commandSingle, wait4Termination, false);
   }

   /**
      global execute admits two kinds of command but only one will be accepted (the other one has to be null)
   */
   private static int gExecutePreShell (String preShell, String [] commandArray, String commandSingle, boolean wait4Termination, boolean silent)
   {
      int retorno = 0;

      log.dbg (2, "gExecute", ((commandSingle != null) ? "comandSingle": "commandArray") + " wait4Termination " + wait4Termination + " silent " + silent);
      try
      {
         Process process = null;

         if (commandSingle != null)
         {
            //(o) JAVA EXEC !!!!
            if (preShell != null && preShell.length () > 0)
            {
               log.dbg (2, "gExecute", "comandSingle preShell [" + preShell + "] command [" + commandSingle + "]");
               process = Runtime.getRuntime().exec (preShell); // e.g. preShell = "su" for linux
               OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
               osw.write(commandSingle);
               osw.flush();
               osw.close();
            }
            else 
            {
               log.dbg (2, "gExecute", "comandSingle [" + commandSingle + "]");
               process = Runtime.getRuntime().exec (commandSingle); //clean env?   , new String [0]);
           }
         }
         else if (commandArray != null)
         {
            if (log.isDebugging (2))
            {
               String arrStr = "";
               for (int ii = 0; ii < commandArray.length; ii ++)
                  arrStr += "{" + commandArray[ii] + "} ";
               log.dbg (2, "gExecute", "commandArray [" + arrStr + "]");
            }
            //(o) JAVA EXEC !!!!
            if (preShell != null && preShell.length () > 0)
            {
               log.dbg (2, "gExecute", "executing using preShell [" + preShell + "]");
               process = Runtime.getRuntime().exec (preShell); // e.g. preShell = "su" for linux
               OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());  
               for (int ii = 0; ii < commandArray.length; ii ++)
                  osw.write(commandArray[ii]);
               osw.flush();
               osw.close();
            }
            else process = Runtime.getRuntime().exec (commandArray); //clean env?   , new String [0]);
         }
         else
         {
            log.severe ("gExecute", "bad call to gExecute, no command given!");
            return -1;
         }

         if (process == null)
         {
            log.severe ("gExecute", "bad call to gExecute !!");
            return -1;
         }

         streamPass passErr = new streamPass (process.getErrorStream (), (silent) ? null: System.err);  // proc's error stream is "stderr"
         streamPass passOut = new streamPass (process.getInputStream (), (silent) ? null: System.out);  // proc's input stream is "stdout" !!

         passErr.start ();
         passOut.start ();

         if (wait4Termination)
         {
            retorno = process.waitFor();
         }
      }
      catch (Exception e)
      {
         log.err ("gExecute", "exception " + e);
         retorno = -1;
      }

      log.dbg (2, "gExecute", "process returns " + retorno);
      return retorno;
   }

   public static void main (String [] args)
   {
      System.out.println ("this is a simple test of the class javaRun");

      // NOTE: javac.exe with no parameters writes on stderr
      //
      System.out.println ("=========== execute javac.exe");
      execute ("javac.exe");

      // NOTE: java.exe with no parameters writes on stdout
      //
      System.out.println ("=========== execute java.exe");
      execute ("java.exe");

      System.out.println ("=========== execute cmd.exe /C dir");
      execute (new String [] { "cmd.exe", "/C", "dir" });

      System.out.println ("=========== launch cmd.exe /C dir");
      launch ("cmd.exe /C dir");

      System.out.println ("=========== done.");
   }
}
