/*
package javaj.widgets
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

package javaj.widgets;

import javaj.widgets.table.*;
import javaj.widgets.basics.widgetLogger;

public class comboMando extends basicTableMando
{
   public comboMando (Object objCtroller, tableEBS pDataAndControl)
   {
      super (objCtroller, pDataAndControl);
   }

   public void setAsisteColumns (String [] columNames)
   {
      // make no sense for lists
      widgetLogger.log().severe ("comboMando::setAsisteColumns", "function not supported in comboBox [\"" + ebsTable().getName() + "\"]");
   }
}
