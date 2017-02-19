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

package de.elxala.db.sqlite;

/**

   Simplest example:

      sqlSolver myDB = new sqlSolver ();
      myDB.openScript ();
      myDB.writeScript ("INSERT INTO myTable VALUES (1001, 'my first entry') ;");
      myDB.closeScript ();
      myDB.runSQL ("mydatabase.db");

      NOTE! Composing SQL queries directly with strings requires that all possible text values
            are escaped to avoid malformed queries due to some characters like ' and " etc
            and also to avoid SQL injection if the string comes from the user
   
         String mySafeText = myDB.escapeString ("Anything whith any 'char'");
         ...
         myDB.writeScript ("INSERT INTO myTable VALUES (1001, "'" + mySafeText + "') ;");
         .. or ..
         myDB.writeScript ("INSERT INTO myTable VALUES (1001, "'" + myDB.escapeString (myAnyText) + "') ;");

*/
public class sqlSolver extends sqlSolverBatch
{
}
