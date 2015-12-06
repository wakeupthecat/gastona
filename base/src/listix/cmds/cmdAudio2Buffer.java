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
   //(o) WelcomeGastona_source_listix_command AUDIO2BUFFER

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       AUDIO2BUFFER
   <groupInfo>  media
   <javaClass>  listix.cmds.cmdAudio2Buffer
   <importance> 4
   <desc>       //Povide access to Audio stuff

   <help>
      //
      //
      //    AUDIO2BUFFER, RECORD START
      //


   <aliases>
      alias
      AUDIO2

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Start recording

   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , START RECORD ,             , //
         2   , PAUSE RECORD ,             , //
         3   , RESUME RECORD ,             , //
         4   , STOP RECORD ,             , //
         5   , START PLAY ,             , //
         6   , PAUSE PLAY ,             , //
         7   , RESUME PLAY ,             , //
         8   , STOP PLAY ,             , //

   <options>
      synIndx, optionName, parameters, defVal, desc

   <examples>
      gastSample

      #**FIN EVA#
*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;

import org.gastona.media.audioRec2Buffer;

/**
*/
public class cmdAudio2Buffer implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "AUDIO2BUFFER",
          "AUDIO2",
      };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String oper    = cmd.getArg(0);
      String msgName = cmd.getArg(1);

      //... String  customFile = cmd.takeOptionString(new String [] { "FROMFILE", "FILE" }, "" );

      //... startLine = stdlib.atoi (cmd.takeOptionString(new String [] {"STARTLINE", "START", "BEGIN", "BEGINLINE" }, "1"));

      cmd.getLog().dbg (2, "AUDIO2BUFFER", "operation [" + oper + "]");

      if (oper.equalsIgnoreCase ("START RECORD"))       audioRec2Buffer.recStart ();
      else if (oper.equalsIgnoreCase ("PAUSE RECORD"))  audioRec2Buffer.recPause ();
      else if (oper.equalsIgnoreCase ("RESUME RECORD")) audioRec2Buffer.recResume ();
      else if (oper.equalsIgnoreCase ("STOP RECORD"))   audioRec2Buffer.recStop ();
      else if (oper.equalsIgnoreCase ("START PLAY"))    audioRec2Buffer.playStart ();
      else if (oper.equalsIgnoreCase ("PAUSE PLAY"))    audioRec2Buffer.playPause ();
      else if (oper.equalsIgnoreCase ("RESUME PLAY"))   audioRec2Buffer.playResume ();
      else if (oper.equalsIgnoreCase ("STOP PLAY"))     audioRec2Buffer.playStop ();

      cmd.checkRemainingOptions (true);
      return 1;
   }

}
