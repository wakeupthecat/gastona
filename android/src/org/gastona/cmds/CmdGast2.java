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

package org.gastona.cmds;

import android.content.Intent;
import listix.*;
import listix.cmds.*;
import listix.cmds.commandable;

import de.elxala.Eva.*;
import de.elxala.langutil.*;

/**
      LAUNCH GASTONA, filegast, parametros, ...

*/
public class CmdGast2 implements commandable
{
   public static final String EXTRA_VALUE_NAME = "gastonaFlex.LaunchGastonaParameters";

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "GAST2",
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

      // copy parameters
      String [] aa = new String [cmd.getArgSize ()];
      for (int ii = 0; ii < cmd.getArgSize (); ii ++)
         aa[ii] = cmd.getArg (ii);

      cmd.getLog().dbg (2, "GAST2", "passed parameters " + aa.length);

      Intent sekunda = new Intent(androidSysUtil.getMainActivity (), org.gastona.gastonaMainActor.class);
      sekunda.setClassName("org.gastona", "org.gastona.gastonaMainActor");
      sekunda.putExtra (EXTRA_VALUE_NAME, aa);
      androidSysUtil.getMainActivity ().startActivity (sekunda);

      return 1;
   }
}
