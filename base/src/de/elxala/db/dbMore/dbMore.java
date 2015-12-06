/*
library de.elxala
Copyright (C) 2009 Alejandro Xalabarder Aulet

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

package de.elxala.db.dbMore;

import java.util.*;
import de.elxala.Eva.*;
import de.elxala.langutil.Cadena;
import de.elxala.zServices.*;

//import de.elxala.db.sqlite.*;

/**
   @class dbMore
   @author Alejandro Xalabarder
   @date   17.01.2010 11:37

   @brief Utilities to handle queries and query generation based on definition of table connections


*/
public class dbMore
{
   private logger log = new logger (this, "de.elxala.db.dbMore", null);

   //connection column indexes
   //
   public static final int CONN_INDX_NAME = 0;
   public static final int CONN_INDX_SOURCETABLE = 1;
   public static final int CONN_INDX_SOURCEKEY = 2;
   public static final int CONN_INDX_TARGETTABLE = 3;
   public static final int CONN_INDX_TARGETKEY = 4;


   public static String getSQL_CreateTableConnections ()
   {
      // o-o  Add Connections

      // o-o  Note : we could choose between two unique keys for the table __dbMore_connections
      //           1) fisrt one is (connName, sourceTable, sourceKey)
      //                This is semantically the unique key for a connection
      //                Note that
      //                      conn1, tab1, field1, ...
      //                      conn1, tab1, field2, ...
      //
      //                is legal since this connection is formed by more than one field or column
      //
      //                But, for example:
      //                      file, mySrcTable, fileID, myTrgTable , ....
      //                      file, mySrcTable, fileID, myTrgTable2, ....
      //
      //                is not right. Even if we want to connect the same column with two master tables
      //                this has to be expressed with a different connection name, for example
      //                      file, mySrcTable, fileID, myTrgTable , ....
      //                      file2, mySrcTable, fileID, myTrgTable2, ....
      //
      //           2) the second unique key is (connName, sourceTable, sourceKey, targetTable, targetKey)
      //                This would accept all except repeated entries which has absolute no sense
      //                Even not semantically connection entries are accepted. The adventage regarding the
      //                option 1 is that we can detect such inconsistence afterwards (e.g.
      //                SELECT connName, sourceTable, sourceKey, targetTable, count(*) as beOne FROM __dbMore_connections)
      //                while in option 1 this inconsistence will be simply ignored (due to INSERT OR IGNORE).
      //                Therefore in case 1 it can be harder to find a problem due to a bad formed
      //                connection, for instnace, the user can see that his connection is not there but
      //                he cannot know why.
      //
      //            3) other posibility is to react to the conflict with an INSERT INTO __dbMore_incidences (...) or like
      //
      return "CREATE TABLE IF NOT EXISTS __dbMore_connections (connName, sourceTable, sourceKey, targetTable, targetKey, UNIQUE (connName, sourceTable, sourceKey, targetTable, targetKey));";
    }

   // Example of use :
   //
   //    myDB.writeScript (getSQL_InsertConnection ("'root', 'scan_files', 'rootID', 'scan_roots', 'rootID'));
   //
   public static String getSQL_InsertConnection (String valuesString)
   {
      return "INSERT OR IGNORE INTO __dbMore_connections VALUES (" + valuesString + ");";
   }

   // Example of use :
   //
   //    myDB.writeScript (getSQL_InsertConnection ("root", "scan_files", "rootID", "scan_roots", "rootID"));
   //
   public static String getSQL_InsertConnection (String connName, String srcTable, String srcKey, String trgTable, String trgKey)
   {
      return "INSERT OR IGNORE INTO __dbMore_connections VALUES ('" + connName + "', '" + srcTable + "', '" + srcKey + "', '" + trgTable + "', '" + trgKey + "');";
   }

   // Example of use :
   //
   //    myDB.writeScript (getSQL_InsertConnection (new String [] { "root", "scan_files", "rootID", "scan_roots", "rootID"}));
   //
   public static String getSQL_InsertConnection (String [] conn)
   {
      if (conn.length == 5)
         return "INSERT OR IGNORE INTO __dbMore_connections VALUES ('" + conn[0] + "', '" + conn[1] + "', '" + conn[2] + "', '" + conn[3] + "', '" + conn[4] + "');";
      return "";
   }
}   