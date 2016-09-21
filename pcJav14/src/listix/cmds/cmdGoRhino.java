/*
library listix (www.listix.org)
Copyright (C) 2016 Alejandro Xalabarder Aulet

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

import org.mozilla.javascript.*;
import de.elxala.zServices.*;

public class cmdGoRhino implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
         "GORHINO",
         "RHINO",
         "RINO",
         "GORRINO",
         "JS",
         "JAVASCRIPT",
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
      cmd.getLog().severe ("GORHINO", "GoRhino cannot be executed, gastona.jar binary DOES NOT CONTAIN Rhino!");
      javax.swing.JOptionPane.showMessageDialog (
            null,
            "Gastona v" + org.gastona.gastonaVersion.getVersion () + 
            "\nBuilt on " + org.gastona.gastonaVersion.getBuildDate () + 
            "\nThis version is java 1.4 compat. does not contain Rhino!\nCommand GoRhino not implemented :(",
            "GoRhino not Implemented in binary",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
      
      return 1;
   }
}
