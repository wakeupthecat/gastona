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

package listix.table;

import listix.*;

/**

*/
public class tableAccessFabrik
{
   public static tableAccessBase create (listixCmdStruct cmdData)
   {
      String typeTable   = cmdData.getArg(0);

      tableAccessBase xTabla = null;

      if (typeTable.equalsIgnoreCase ("EVA"))         xTabla = new tableAccessEva ();
      else if (typeTable.equalsIgnoreCase ("SQL"))    xTabla = new tableAccessSql ();
      else if (typeTable.equalsIgnoreCase ("COLUMNS")) xTabla = new tableColumns ();
      else if (typeTable.equalsIgnoreCase ("FILES"))  xTabla = new tableAccessPathFiles ();
      else if (typeTable.equalsIgnoreCase ("DIRS"))   xTabla = new tableAccessPathDirs ();
      else if (typeTable.equalsIgnoreCase ("FOR"))    xTabla = new tableAccessFor ();
      else if (typeTable.equalsIgnoreCase ("TEXT FILE")) xTabla = new tableAccessTextFile ();
      else if (typeTable.equalsIgnoreCase ("TEXTFILE")) xTabla = new tableAccessTextFile ();
      else
      {
         cmdData.getLog ().err ("tableAccessFabrik", "LOOP type [" + typeTable + "] not supported!");
         return null;
      }

      xTabla.setCommand (cmdData);
      xTabla.rewind ();
      return xTabla;
   }
}
