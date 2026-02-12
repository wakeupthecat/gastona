/*
library listix (www.listix.org)
Copyright (C) 2005 to 2012 Alejandro Xalabarder Aulet

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
*/
public class commander extends commanderBase
{
   public commander ()
   {
      super ();
   }

   public void load_specific_commands ()
   {
      //(o) DOC/listix/commands specific commands only for PC

      // experimental feature!
      // currently binaries are only included via microtool mechanism for windows platform
      loadCommandable (new cmdRuby ());
      loadCommandable (new cmdLua ()); 
      loadCommandable (new cmdPerl ());
      loadCommandable (new cmdPython ());
      
      loadCommandable (new cmdAudio2Buffer ());
      loadCommandable (new cmdScreenshot ());
      loadCommandable (new cmdMidi ());      

      loadCommandable (new cmdReplaceBmp ());  // only for Windows
      loadCommandable (new cmdProcessImage ());  // only for Windows
   }
}
