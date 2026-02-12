/*
package de.elxala
(c) Copyright 2005-2023 Alejandro Xalabarder Aulet

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

package de.elxala.parse.parsons;

public class parsonsColumn
{
   protected boolean specialCol = false;
   protected String name = null;
   protected String value = null;

   public boolean isRegular ()  { return !specialCol; }
   public boolean isSpecial ()  { return specialCol;  }

   public String getName  ()          { return name; }
   public String getValue ()          { return value; }
   public void setValue (String val)  { value = val;  }

   public parsonsColumn (String fieldName)
   {
      name = fieldName;
      specialCol = name.length () == 0 || "*#:-/.".indexOf (name.charAt (0)) != -1;
   }
}
