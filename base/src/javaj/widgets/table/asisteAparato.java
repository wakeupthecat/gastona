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

package javaj.widgets.table;

import javaj.widgets.table.util.*;

import de.elxala.db.sqlite.*;
import de.elxala.mensaka.*;


/**
*/
public class asisteAparato extends tableAparato
{
   private MessageHandle HMSG_ExtraFilterChanged = null;

   public asisteAparato (MensakaTarget objWidget, tableEBS pDataAndControl)
   {
      super (objWidget, pDataAndControl);

      HMSG_ExtraFilterChanged = new MessageHandle (objWidget, pDataAndControl.evaName ("sqlExtaFilter"));
   }

   public void setAsisteColumns (String [] columNames)
   {
      ebsTable ().setAsisteColumns (columNames);

      if (myDBViewTableModel == null)
      {
         log.err ("asisteAparato.setAsisteColumns", "Wrong asisteAparato formed! Probably no database or select query given.");
         return;
      }
      myDBViewTableModel.setExtraFilter ("");
   }

   private static final String LOG_CONTEXT = "asisteAparato::updateAcordingAsisteCampos";
   public void updateAcordingAsisteCampos (String [] asisteValues)
   {
      if (myDBViewTableModel == null)
      {
         log.err (LOG_CONTEXT, "Wrong asisteAparato formed! Probably no database or select query given.");
         return;
      }

      String strDB  = ebsTable().getSqliteDatabaseName ();
      String strSQL = ebsTable().getSqliteSelectQuery ();

      // get the campo (field) Names
      String [] eNames = ebsTable().getAsisteColumns ();
      if (eNames == null)
      {
         log.err (LOG_CONTEXT, "there are no AsisteCampos, there is nothing to be updated!");
         return;
      }

      //(o) EXEPERIMENTAL_asiste Asiste Group
      //
      //       The problems are:
      //          - It produces a different layout (should be place elsewhere)
      //          - Replaces the original sql (it should be stored elsewhere)
      //          - ...
      boolean TEST_WITH_GROUPS = false;

      if (TEST_WITH_GROUPS)
      {
         String sqlGroup = utilAsiste.buildSQLGroupBY (eNames, asisteValues, strSQL);
         if (sqlGroup != null)
         {
            log.dbg (2, LOG_CONTEXT, "sql group [" + sqlGroup + "]");
            myDBViewTableModel.setExtraFilter ("");
            myDBViewTableModel.setSelectQuery (strDB, sqlGroup);

            Mensaka.sendPacket (HMSG_ExtraFilterChanged, ebsTable().getData ());
            return;
         }
      }

      // PREPAR ...
      // SELECT xxxxxx FROM (strSQL) WHERE xxx ORDER BY

      String strFilter = utilAsiste.buildSQLFilterString (eNames, asisteValues);

      log.dbg (2, LOG_CONTEXT, "sql filter [" + strFilter + "]");

      myDBViewTableModel.setExtraFilter (strFilter);
      myDBViewTableModel.setSelectQuery (strDB, strSQL);

      Mensaka.sendPacket (HMSG_ExtraFilterChanged, ebsTable().getData ());
      //updateMetaData ();
      // Mensaka.sendPacket (UPDATE_MODEL);
   }
}
