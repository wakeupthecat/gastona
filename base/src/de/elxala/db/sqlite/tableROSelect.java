/*
library de.elxala
Copyright (C) 2005, 2017 Alejandro Xalabarder Aulet

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

package de.elxala.db.sqlite;

import de.elxala.Eva.abstractTable.*;

//
// Note:
//    in PC (taken from base) tableROSelect is implemented in Batch fashion, that is calling sqlite binary directly
//    therefore we extend directly from tableROSelectBatch
//    since in Android this is not possible we use the SQLite api (unfortunately)
//    see android/src/de/elxala/db/sqlite/tableROSelectAApi.java
//

public class tableROSelect extends tableROSelectBatch
{
   public tableROSelect ()
   {
      super ();
   }

   public tableROSelect (baseEBS ebs)
   {
      super (ebs);
   }
   
   public tableROSelect (String databaseFile, String SQLSelect)
   {
      super (databaseFile, SQLSelect);
   }
}
