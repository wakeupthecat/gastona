/*
library de.elxala
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

      return 1;
   }
}




/*
//      else if (type.equalsIgnoreCase ("YN"))
//      {
//         dialog = true;
//         msgType = javax.swing.JOptionPane.YES_NO_OPTION;
//      }
//      else if (type.equalsIgnoreCase ("OC"))
//      {
//         dialog = true;
//         msgType = javax.swing.JOptionPane.OK_CANCEL_OPTION;
//      }
//      else if (type.equalsIgnoreCase ("YNA"))
//      {
//         dialog = true;
//         msgType = javax.swing.JOptionPane.YES_NO_CANCEL_OPTION;
//      }
//
//      if (dialog)
//      {
//         response = showOptionDialog(null, message, "Please choose", int optionType, int messageType, Icon icon, Object[] options, Object initialValue)
//      }
//

static int CANCEL_OPTION
          Return value from class method if CANCEL is chosen.
static int CLOSED_OPTION
          Return value from class method if user closes window without selecting anything, more than likely this should be treated as either a CANCEL_OPTION or NO_OPTION.
static int DEFAULT_OPTION
          Type used for showConfirmDialog.
static int ERROR_MESSAGE
          Used for error messages.
protected  Icon icon
          Icon used in pane.
static String ICON_PROPERTY
          Bound property name for icon.
static int INFORMATION_MESSAGE
          Used for information messages.
static String INITIAL_SELECTION_VALUE_PROPERTY
          Bound property name for initialSelectionValue.
static String INITIAL_VALUE_PROPERTY
          Bound property name for initialValue.
protected  Object initialSelectionValue
          Initial value to select in selectionValues.
protected  Object initialValue
          Value that should be initially selected in options.
static String INPUT_VALUE_PROPERTY
          Bound property name for inputValue.
protected  Object inputValue
          Value the user has input.
protected  Object message
          Message to display.
static String MESSAGE_PROPERTY
          Bound property name for message.
static String MESSAGE_TYPE_PROPERTY
          Bound property name for type.
protected  int messageType
          Message type.
static int NO_OPTION
          Return value from class method if NO is chosen.
static int OK_CANCEL_OPTION
          Type used for showConfirmDialog.
static int OK_OPTION
          Return value form class method if OK is chosen.
static String OPTION_TYPE_PROPERTY
          Bound property name for optionType.
protected  Object[] options
          Options to display to the user.
static String OPTIONS_PROPERTY
          Bound property name for option.
protected  int optionType
          Option type, one of DEFAULT_OPTION, YES_NO_OPTION, YES_NO_CANCEL_OPTION or OK_CANCEL_OPTION.
static int PLAIN_MESSAGE
          No icon is used.
static int QUESTION_MESSAGE
          Used for questions.
static String SELECTION_VALUES_PROPERTY
          Bound property name for selectionValues.
protected  Object[] selectionValues
          Array of values the user can choose from.
static Object UNINITIALIZED_VALUE
          Indicates that the user has not yet selected a value.
protected  Object value
          Currently selected value, will be a valid option, or UNINITIALIZED_VALUE or null.
static String VALUE_PROPERTY
          Bound property name for value.
static String WANTS_INPUT_PROPERTY
          Bound property name for wantsInput.
protected  boolean wantsInput
          If true, a UI widget will be provided to the user to get input.
static int WARNING_MESSAGE
          Used for warning messages.
static int YES_NO_CANCEL_OPTION
          Type used for showConfirmDialog.
static int YES_NO_OPTION
          Type used for showConfirmDialog.
static int YES_OPTION
          Return value from class method if YES is chosen.



static void showInternalMessageDialog(Component parentComponent, Object message)
          Brings up an internal confirmation dialog panel.
static void showInternalMessageDialog(Component parentComponent, Object message, String title, int messageType)
          Brings up an internal dialog panel that displays a message using a default icon determined by the messageType parameter.
static void showInternalMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon)
          Brings up an internal dialog panel displaying a message, specifying all parameters.
static int showInternalOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue)
          Brings up an internal dialog panel with a specified icon, where the initial choice is determined by the initialValue parameter and the number of choices is determined by the optionType parameter.
static void showMessageDialog(Component parentComponent, Object message)
          Brings up an information-message dialog titled "Message".
static void showMessageDialog(Component parentComponent, Object message, String title, int messageType)
          Brings up a dialog that displays a message using a default icon determined by the messageType parameter.
static void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon)
          Brings up a dialog displaying a message, specifying all parameters.
static int showOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue)
          Brings up a dialog with a specified icon, where the initial choice is determined by the initialValue parameter and the number of choices is determined by the optionType parameter.
*/
