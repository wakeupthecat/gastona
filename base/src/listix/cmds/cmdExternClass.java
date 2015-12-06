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
   //(o) WelcomeGastona_source_listix_command EXTERN CLASS

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       EXTERNAL CLASS
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdExternClass
   <importance> 3
   <desc>       //Adding external listix commands and java class paths

   <help>
      //
      // To load java libraries (either jar files or paths) or to load a custom Listix command.
      //
      // Note1: calling "EXTERNAL, PATH (or JAR)" has the same effect as doing it through the Javaj's
      //        variable <external_libraries>
      // Note2: Before a new Listix commnad is added ("EXTERNAL, COMMAND") all the classes that the
      //        command needs has to be found in java class path
      //
      // Examples:
      //          <! loading needed libraries (java class paths)
      //          EXTERNAL, PATH, ".", otherClasses.jar
      //
      //          <! loading new listix custom commnands
      //          EXTERNAL, COMMAND, com.mydomain.listixCmds.myCommand1
      //          EXTERNAL, COMMAND, com.mydomain.listixCmds.myCommand2
      //
      //          <! calling a new listix custom commnand
      //          MY CMD, par1, par2
      //                , opt1, ...

   <aliases>
      alias
      EXTERNAL
      EXTERN
      IMPORT
      INCLUDE


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    3      , //Adds a new path or jar file to the java classpath, usually in order to find EXTERN commands (see EXTERN, CMD)
         2   ,    3      , //Add a new command to listix given by its class name

   <syntaxParams>
      synIndx, name             , defVal    , desc
         1   , JAR / PATH       ,           , //(use either JAR or PATH, the meaning is the same for both)
         1   , jarOrPath        ,           , //Jar file or path to be included into the java classpath
         1   , ...              ,           , //Further jar files or paths might be given in separate columns
         2   , LISTIX COMMAND / LSX CMD,    , //
         2   , commandClass     ,           , //Complete name of java class that implements the new command to be add to the listix script (Note that this class must satisfy the the interface listix.comandable)
         2   , ...              ,           , //Further commandClass are possible

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

#** fin eva#

*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

public class cmdExternClass implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "EXTERNALCLASS",
          "EXTERNALPATH",
          "EXTERNAL",
          "EXTERN",
          "EXTERNO",
          "IMPORT",
          "IMPORTPATH",
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

      // JAR or CMD ?
      String what2Load = cmd.getArg (0);

      if (cmd.meantConstantString (what2Load, new String [] { "JAR", "PATH" }))
      {
         // JAR
         String classPath = "";
         for (int pp = 2; pp < commandEva.cols (indxComm); pp ++)
         {
            classPath = that.solveStrAsString (commandEva.getValue (indxComm, pp));
            if (classPath.length () > 0)
            {
               javaLoad.addClassPath (classPath);
            }
         }
      }
      else if (cmd.meantConstantString (what2Load, new String [] { "LISTIXCOMMAND", "LSXCMD", "COMMAND", "CMD" }))
      {
         // CMD
         for (int pp = 2; pp < commandEva.cols (indxComm); pp ++)
         {
            // command name
            String cmdClass = that.solveStrAsString (commandEva.getValue (indxComm, pp));

            // instanciate it!
            commandable comando = (commandable) javaLoad.javaInstanciator (cmdClass);
            if (comando != null)
            {
               that.addInternCommand (comando);
            }
            else
            {
               that.log().err ("EXTERNALCLASS", "The extern listix command [" + cmdClass + "] could not be loaded!");
               return 1;
            }
         }
      }
      else
      {
         // nor JAR nor CMD ?
         that.log().err ("EXTERNALCLASS", "Wrong option [" + what2Load + "] only JAR/PATH or LISTIX COMMAND/LSX CMD are possible!");
         return 1;
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
