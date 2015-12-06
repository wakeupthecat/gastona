/*
Copyright (C) 2014 Alejandro Xalabarder Aulet, Wakeupthecat UG

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
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.streams.*;
import de.elxala.langutil.filedir.*;
import de.elxala.zServices.*;

/**
*/
public class callCaptureInpOut
{

   /**
      extract inline script and options FILEIN, FILEOUT, FILEERR
      and execute the binary
   */
   public static boolean callCapture (String cmdName, String strProcess, String firstInlineScript, listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      // that.log().dbg (4, "CALLCAP", "inline script");

      Eva inlineScript = new Eva ("inline_stdin");
      int indxPassOpt = cmdLoopTable.indxSkipingOptions(commandEva, indxComm, inlineScript);

      String fileIn = cmd.takeOptionString(new String [] { "FILEIN", "FILEINPUT" }, "" );
      String fileOut = cmd.takeOptionString(new String [] { "FILEOUT", "FILEOUTPUT" }, "" );
      String fileErr = cmd.takeOptionString(new String [] { "FILEERR", "FILEERROR" }, "" );

      serialTextBuffer theInputScript = new serialTextBuffer ();
      abstractStreamTextReader [] outputs = new abstractStreamTextReader [2];

      theInputScript.writeln (firstInlineScript);
      for (int ii = 0; ii < inlineScript.rows (); ii ++)
         theInputScript.writeln (that.solveStrAsString (inlineScript.getValue (ii, 0)));

      //call CALLCAP
      //
      if (!processExecutor.executeProcess (new String [] { strProcess }, fileIn, fileOut, fileErr, theInputScript, outputs))
      {
         that.log().err (cmdName, "finished with errors!");
      }

      //send the output if any to the listix target
      //
      abstractStreamTextReader theOut = (outputs != null && outputs.length > 0 && outputs[0] != null) ? outputs[0] : null;
      
      // if we have collected output and the output does not go to a file 
      // print it out onto the listix target
      //
      if (theOut != null && fileOut.length() == 0)
         for (int ii = 0; ii < theOut.countLines (); ii ++)
         {
            that.log().dbg (2, cmdName, "output [" + theOut.getLine(ii) + "]");

            if (ii > 0) that.newLineOnTarget ();
            that.printTextLsx (theOut.getLine(ii));
         }

      cmd.checkRemainingOptions (true);
      return true;
   }
}
