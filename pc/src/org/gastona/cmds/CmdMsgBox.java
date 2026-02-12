/*
library de.elxala
Copyright (C) 2005-2022 Alejandro Xalabarder Aulet

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
   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       MSGBOX
   <groupInfo>  lang_comm
   <javaClass>  listix.cmds.CmdMsgBox
   <importance> 3
   <desc>       //Opens a message box


   <help>       //Opens a message box


   <aliases>
      alias
      BOX
      MESSAGE BOX

   <syntaxHeader>
      synIndx, groupInfo, importance, desc
         1   ,   gui    , 2      , //Opens a messageBox and displays the given message of the given type

   <syntaxParams>
      synIndx, name       , defVal      , desc
         1   , typeOrIcon ,             , //Type of message, either (x) for Error message, (!) for warning message, (i) for information message
         1   , message    ,             , //Message to show
         1   , title      ,             , //Title of the box

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample
      simple message

   <simple message>
      //
      //#listix#
      //
      //   <main>
      //       BOX, (i), //This is an information message
      //       BOX, (!), //This is a warning message
      //       BOX, (x), //This is an error message
      //

#**FIN_EVA#
*/

package org.gastona.cmds;

import listix.*;
import listix.cmds.commandable;

import de.elxala.Eva.*;
import de.elxala.mensaka.*;

/**
   to allow sending mensaka messages from listix !

*/
public class CmdMsgBox implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "MSGBOX",
          "BOX",
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

      String type    = cmd.getArg(0);
      String message = cmd.getArg(1);
      String title   = cmd.getArg(2);

      int msgType = javax.swing.JOptionPane.INFORMATION_MESSAGE;

      char chaType = type.length () > 0 ? type.charAt(0): 'i';
      // admit (i) [!] etc
      if (type.length () >= 3)
           chaType = type.charAt(1);

      if ("eExX".indexOf (chaType) != -1)
         msgType = javax.swing.JOptionPane.ERROR_MESSAGE;
      else if ("wW!".indexOf (chaType) != -1)
         msgType = javax.swing.JOptionPane.WARNING_MESSAGE;
      else if ("iImM".indexOf (chaType) != -1)
         msgType = javax.swing.JOptionPane.INFORMATION_MESSAGE;

      if (title.equals(""))
      {
         // check if <AppName> is given
         Eva appN = that.getVarEva ("AppName");
         if (appN != null)
              title = appN.getValue ();
         else title = "ups! one message box";
      }

      javax.swing.JOptionPane.showMessageDialog (
            null,
            message,
            title,
            msgType);

      if (javaj.javaj36.lastMainFrame != null)
      {
         // show always the pop-up regardless of application focus etc ...
         // it is possible that the main app does not have the focus at the time of
         // showing the message. For instance command comes via some communication (UDP, HTTP ..) or timeout etc..
         // Without these lines the pop-up will be shown only after the app gaining focus
         // for instance by clicking on the frame.
         // If the message is shown on the top or hidden by other windows is another matter (not guaranteed)
         //
         javaj.javaj36.lastMainFrame.requestFocus ();
         javaj.javaj36.lastMainFrame.requestFocusInWindow ();
      }

      return 1;
   }
}
