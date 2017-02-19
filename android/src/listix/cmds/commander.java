/*
library listix (www.listix.org)
Copyright (C) 2005-2016 Alejandro Xalabarder Aulet

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

/**
   holding and management of the listix commands. An instance of the class
   allows to add commands (*) and call them from a listix context (treatCommand)

   holds a list of listix commands (interface commandable) and maps all
   possible command names of the commnad for future search.

   (*)
   Note :
   The function loadCommandable load commands only once because it uses internally a static
   list of commnads, as consequence there is no way to remove the commands.

*/
public class commander extends commanderBase
{
   public commander ()
   {
      super ();
   }

   public void load_specific_commands ()
   {
      //(o) DOC/listix/commands specific commands only for android
      
      loadCommandable (new cmdIntent ());

      //(o) TODO/android/review command GPS
      // loadCommandable (new cmdGPS ());
   }
}
