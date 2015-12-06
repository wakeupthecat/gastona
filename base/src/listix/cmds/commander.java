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

package listix.cmds;

import listix.*;
import java.util.*;
import de.elxala.Eva.*;

/**
   holding and management of the listix commands. An instance of the class
   allows to add commands (*) and call them from a listix context (treatCommand)

   holds a list of listix commands (interface commandable) and maps all
   possible command names of the commnad for future search.

   (*)
   Note :
   The function loadCommandable load commands only once because it uses internally a static
   list of commnads, as consequence there is no way to remove the commands.

*/
public class commander
{
   private static Map mapCommands    = null;
   private static Vector vecCommands = null;
   // private int NPrimitiveCommands = 0;

   public commander ()
   {
      cuasi_static_constructor();
   }

   private void cuasi_static_constructor()
   {
      if (mapCommands != null) return;

      // this has to be done only once!
      //
      mapCommands = new TreeMap ();
      vecCommands = new Vector ();

      loadCommandable (new cmdGenerate ());

      loadCommandable (new cmdLoopTable ());
      loadCommandable (new cmdRunLoopTable ());

      loadCommandable (new cmdDatabase ());

      loadCommandable (new cmdInCase ());
      loadCommandable (new cmdInCaseNumeric ());
      loadCommandable (new cmdCheck ());

      loadCommandable (new cmdValueOf ());
      loadCommandable (new cmdSetVariable ());
      loadCommandable (new cmdSetNumeric ());
      loadCommandable (new cmdSetProperty ());
      loadCommandable (new cmdAddToVariable ());
      loadCommandable (new cmdAddNumericToVariable ());
      loadCommandable (new cmdInfile ());
      loadCommandable (new cmdFormula ());

      loadCommandable (new cmdFileSystemInfo ());  // to be deprecated, use SCAN instead
      loadCommandable (new cmdScanFiles ());
      loadCommandable (new cmdExternClass ());
      loadCommandable (new cmdCall ());
      loadCommandable (new cmdLaunch ());
      loadCommandable (new cmdJavaMain ());

      loadCommandable (new cmdStrconvert ());

      loadCommandable (new cmdDump ());
      loadCommandable (new cmdLoadUnit ());

      loadCommandable (new cmdParsons ());
      loadCommandable (new cmdSleep ());
      loadCommandable (new cmdZip ());
      loadCommandable (new cmdListix ());
      loadCommandable (new cmdResourceUtil ());
      loadCommandable (new cmdDBMore ());
      loadCommandable (new cmdXmelon ());
      loadCommandable (new cmdParserEVA ());

      loadCommandable (new cmdAudio2Buffer ());

      // NPrimitiveCommands = mapCommands.size ();
   }

   public void loadCommandable (commandable com)
   {
      if (vecCommands.contains (com) || null != mapCommands.get(comparableCommandName (com.getNames()[0])))
      {
         // do not add it again!
         // it is not an error, it can happens since
         // vecCommands is internally static
         return;
      }

      // add it to the list of commands
      vecCommands.add (com);

      // map all names
      String [] allNames = com.getNames ();
      for (int ii = 0; ii < allNames.length; ii ++)
      {
         String cmdName = comparableCommandName (allNames[ii]);
         if (mapCommands.get (cmdName) != null)
            System.err.println ("ERROR! in listix.cmds.commander: try to map the existing name [" + allNames[ii] + "] to a new listix command!");
         else
            mapCommands.put (cmdName, com);
      }
   }

   /**
      normalize the command names : upper case and with no blanks
   */
   private static String comparableCommandName (String commandRawName)
   {
      String comm = commandRawName.toUpperCase ();

      // ... or com.replace (" ", "")
      int indx = 0;
      while ((indx = comm.indexOf (' ')) != -1)
         comm = comm.substring (0, indx) + comm.substring (indx + 1);

      return comm;
   }

   /**
      normalize the command names and or constant parameters using upper case and removing all blanks
      Note that it migth be used for listix commands but also for recognize other
      options or arguments
   */
   public static boolean meantCommand (String relaxedComando, String [] possibilities)
   {
      String comm = comparableCommandName (relaxedComando);

      for (int pp = 0; pp < possibilities.length; pp ++)
      {
         if (comm.equalsIgnoreCase (possibilities[pp]))
            return true;
      }

      return false;
   }


   /**
      Tries to execute a listix command that is supposed to be at row 'rrow' in the eva 'commnads'.
      If the commnad is found within the loaded commands then it is executed.
      Returns the number of lines that the command had or 1 if the command was not recognized.
   */
   public int treatCommand (listix that, Eva commands, int rrow)
   {
      int pasos = 1;

      String theCommand = commands.getValue (rrow, 0).toUpperCase ();

      // if there is command and it is not "" (void command) ...
      //
      if (rrow < commands.rows () && theCommand.length () > 0)
      {
         String comm = comparableCommandName (theCommand);

         commandable coma = (commandable) mapCommands.get (comm);

         if (coma != null)
         {
            pasos = coma.execute (that, commands, rrow);
         }
         else
         {
            that.log().err ("comander", "Command not recognized! : " + comm + " in eva " + commands.getName () + " row " + rrow);
            return 1;
         }
      }
      return pasos;
   }
}
