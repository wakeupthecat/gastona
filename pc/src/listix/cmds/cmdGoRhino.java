/*
library listix (www.listix.org)
Copyright (C) 2016-2021 Alejandro Xalabarder Aulet

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

public class cmdGoRhino extends baseCmdGoRhino
{
   // this method is thought to be called directly using JAVA STATIC listix command
   // for instance:
   //
   //       JAVA STATIC, listix.cmds.cmdGoRhino, callSingle, //"amigo".match (/go/) ? "1": "0"
   //
   // this call is much faster (e.g. 0.32 ms) than calling listix command GORHINO (e.g. 1.11 ms)
   // however Android cannot use JAVA STATIC !
   //
   public static String callSingle (String [] jsSource)
   {
      // the parameter is a String array in order to be callable from JAVA STATIC but
      // actually it only make sense to send one element
      // anyway if there are more we will call all as separate scripts
      //
      StringBuffer sal = new StringBuffer ();

      for (int ii = 0; ii < jsSource.length; ii ++)
         sal.append (callSingle (jsSource[ii], org.gastona.commonGastona.log, null ));

      return sal.toString ();
   }
}
