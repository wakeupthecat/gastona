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
   <name>       MESSAGE
   <groupInfo>  lang_comm
   <javaClass>  listix.cmds.CmdCallMensaka
   <importance> 5
   <desc>       //Sending messages to widgets and other listeners


   <help>
      //
      // Sends a Mensaka message. Mensaka is the message mechanism that is used Gastona and it is the
      // base of the communication with widgets (Javaj zWidgets). A Mensaka message consits of a text
      // which is the key of the message and a packet of data (an EvaUnit object). In Gastona
      // applications this packet is always the unit #data#, so usually we will refer to a message
      // just as the text associated to it.
      //
      // Widgtets send messages and need messages to update its data or change its states, but it is
      // also possible to send a message to other components like Javaj (e.g. "javaj doExit") or in
      // general any java object or controller. Even it is posible to send a message to Listix from
      // Listix, while for small application this is really not needed it can be helpful to build a
      // logic of own messages in complex applications.
      //
      // Listix can listen to messages through the special format <-- messageText>, then when the
      // message is sent the format is executed.
      //

   <aliases>
      alias
      MENSAKA
      MSG
      MSK
      SEND


   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Sends the given message 'messageContent' using mensaka mechanism using as packet the current listix data

<! DEPRECATED 06.06.2010 12:46
<!         2   ,    3      , //Sends the given message 'messageContent' using mensaka mechanism using as packet 'evaUnit' from the file 'evaFileName'

   <syntaxParams>
      synIndx, name           , defVal      , desc
         1   , messageContent ,             , //Mensaka message to be sent

<! DEPRECATED 06.06.2010 12:46
<!         1   , data | listix  , data        ,//Packet to be sent with the message. Either the current listix data (default) or the current listix formats

<!         2   , messageContent ,             , //Mensaka message to be sent
<!         2   , LOAD           ,             , //

<! DEPRECATED 06.06.2010 12:46
<!         2   , evaUnit        ,             , //EvaUnit to be sent as packet of the message
<!         2   , evaFileName    ,             , //File name containing 'evaUnit'

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample
      simple message
      messages sample

   <simple message>
      //#javaj#
      //
      //    <frames> oConsole, "Simple message sample"
      //
      //#listix#
      //
      //    <main0>
      //       //sending "message in a bottle" message...
      //       MESSAGE, message in a bottle
      //
      //    <-- message in a bottle>
      //       //I am a rock!

   <messages sample>
      //#javaj#
      //
      //   <frames> Fmain, Message example, 350
      //
      //   <layout of Fmain>
      //       EVA, 10, 10, 5, 5
      //
      //     ---,         ,    X
      //        , bSend   , eMsgText
      //        , bExit
      //      X , oConsola, -
      //
      //#listix#
      //
      //   <main0>
      //       //Enter one message INI or FIN and press Send button
      //       //
      //
      //   <-- bSend>	 MESSAGE, @<eMsgText>
      //   <-- bExit>   MESSAGE, javaj doExit
      //
      //   <-- eMsgText>
      //       //you enter "@<eMsgText>", now press Send
      //       //
      //
      //   <-- INI>
      //       //Received the message "INI", starting something ...
      //       //
      //
      //   <-- FIN>
      //       //Received the message "FIN", closing sample ...
      //       //
      //       WAIT, 2000
      //       MESSAGE, javaj doExit


#**FIN_EVA#
*/


package gastona.cmds;

import listix.*;
import listix.cmds.commandable;

import de.elxala.Eva.*;
import de.elxala.mensaka.*;

/**
   to allow sending mensaka messages from listix !


*/
public class CmdCallMensaka implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "MSK",
          "MSG",
          "MENSAKA",
          "MESSAGE",
          "SEND"
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

      //if (!cmd.checkParamSize (1, 1)) return 1;

      String message  = cmd.getArg(0);
      String [] param = new String [cmd.getArgSize ()-1];

      for (int ii = 0; ii < param.length; ii ++)
         param[ii] = cmd.getArg(1 + ii);

      that.log().dbg (2, "MESSAGE", "send message [" + message + "] parSize " + param.length );
      Mensaka.sendPacket (message, that.getGlobalData (), param);

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
