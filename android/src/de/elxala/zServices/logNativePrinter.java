/*
packages de.elxala
(c) Copyright 2005 Alejandro Xalabarder Aulet

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

package de.elxala.zServices;

import android.util.Log;
import org.gastona.cmds.*;

public class logNativePrinter
{
   public static void message (String context, String msg2print)
   {
      System.out.println (msg2print);
      Log.d (context, msg2print);
   }

   public static void warning (String context, String msg2print)
   {
      System.out.println (msg2print);
      Log.w (context, msg2print);
   }

   public static void error (String context, String msg2print)
   {
      System.err.println (msg2print);
      Log.wtf (context, msg2print);
   }

   public static void errorBox (String msg2print, String title)
   {
      Log.wtf ("logServer", msg2print);

      CmdMsgBox.alerta (CmdMsgBox.ERROR_MESSAGE, title, msg2print, null, null);
      System.exit (1);
//      javax.swing.JOptionPane.showMessageDialog (
//            null,
//            msg2print,
//            title,
//            javax.swing.JOptionPane.ERROR_MESSAGE);
   }
}
