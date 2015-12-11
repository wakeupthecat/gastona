/*
de.elxala.parse.xml
Copyright (C) 2014 Alejandro Xalabarder Aulet

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

package de.elxala.parse;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.utilEscapeStr;

import de.elxala.zServices.*;

import java.util.*;

import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;

/**
   xmelonSchema keeps its original name - it was designed to store xml content - but
   is a general schema that can also store JSON for example.
   
*/
public class xmelonSchema
{
   // gastona logger
   //
   private static logger log = new logger (null, "de.elxala.parse.xmelonSchema", null);

   // xml path control
   //
   public static final int cNODE=0;            // tag name
   public static final int cNAMESPACE=1;       // ...
   public static final int cLOCALNAME=2;       // ...
   public static final int cCOUNTER=3;         // unique (per xml file) path counter
   public static final int cPARENT_COUNTER=4;  // the path counter of first parent with stored data (0 if no parent)
   public static final int cHASDATA=5;         // to control whether the element has stored data or not
   public static final int cHASATTRIB=6;       // to control whether the element has stored attributes or not
//?nochut?   private static final int cPATIDINDX=6;       // to know the pathID (adding MIN_PAT_ID) of each level

   private static final long MIN_FILE_ID  = 1000;
   private static final long MIN_PAT_ID   = 100;
   private static final long TAGID_FOR_FREETEXT = 0;
   private static final int PARENT_COUNTER_ROOT = 0;
   private static final long INITIAL_DATA_COUNTER = 1;

   public static final char DATA_PLACE_ATTRIBUTE = 'A'; // as attribute      : <a myAtt="data"> ... </a>
   public static final char DATA_PLACE_VALUEATT  = 'V'; // as "value" attribute  : <a myAtt="xxx">data</a>
   public static final char DATA_PLACE_FREETEXT  = 'X'; // unstructured text : <a> data <b> ... </b> data </a>
   public static final char DATA_PLACE_TAGVALUE  = 'D'; // normal data       : <a> data </a>

   public sqlSolver cliDB = null;

   public class cacheableStruct
   {
      public long fileID = 0;
      public String dbName = null;
      public String tabPrefix = "xmelon";
      public List patIDList = null;      // to store path+namespace ..
      public List tagIDList = null;      // to store tag original names ...
   }

   public class perFileStruc
   {
      public Eva currentPath = new Eva(); // table (pathNode, namespace, localName, counter, hasData)
      public long dataCounter = INITIAL_DATA_COUNTER;  // incremted with each data store
      public long pathCounter = 1; // 0 means root, also as parent
      public String strData = "";
      public boolean lastWasClosingTag = false; // initial value has no meaning and will be not used
      public boolean ignoringContent = false;
   }

   public perFileStruc perFile = null;
   public cacheableStruct cached = new cacheableStruct ();

   public void clearCache ()
   {
      cached = new cacheableStruct ();
      log.dbg (2, "clearCache", "cache cleared");
   }

   public boolean checkMisprog (boolean sayAgain, String varName)
   {
      if (sayAgain)
         log.severe ("out",  varName + " is null during operation, is xmelonSchema misprogrammed?");
      return sayAgain;
   }


   public void out (String str)
   {
      if (checkMisprog (cliDB == null, "cliDB")) return;
      cliDB.writeScript (str + "\n");
   }

   protected void initialScript (String dbName, String prefix)
   {
      if (checkMisprog (cliDB == null, "cliDB")) return;
      if (checkMisprog (cached == null, "cached")) return;

      //(o) listix_sql_schemas XMELON schema creation

      if (cached.dbName == null || !cached.dbName.equals (dbName) ||
          cached.tabPrefix == null || !cached.tabPrefix.equals (prefix)
          )
      {
         cached.tabPrefix = prefix;
         cached.dbName = dbName;
         log.dbg (2, "initialScript", "ensure tables creation for prefix " + cached.tabPrefix + " in database " + cached.dbName);
         cliDB.openScript ();

         //
         //   table: xmelon_files    (fileID, timeParse, fullPath)
         //   table: xmelon_tagDef   (tagID, tagStr, tagStrNormal)
         //   table: xmelon_pathDef  (patID, parentPatID, pathStr, pathStrNormal, lastNode, level)
         //   table: xmelon_data     (fileID, dataCnt, patCnt, parentPatCnt, patID, tagID, dataPlace, value)
         //
         String tTAG  = cached.tabPrefix + "_tagDef";
         String tPATH = cached.tabPrefix + "_pathDef";
         String tFILE = cached.tabPrefix + "_files";
         String tDATA = cached.tabPrefix + "_data";
         String tLOG  = cached.tabPrefix + "_log";

         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tTAG  + " (tagID int, tagStr text, tagStrNormal text, UNIQUE(tagID));");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tPATH + " (patID int, parentPatID int, pathStr text, pathStrNormal text, lastNode text, level int, UNIQUE(patID));");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tFILE + " (fileID int, timeParse text, fullPath text, UNIQUE(fileID));");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tDATA + " (fileID int, dataCnt int, patCnt int, parentPatCnt int, patID int, tagID int, dataPlace text, value text, UNIQUE(fileID, dataCnt));");

         cliDB.writeScript ("CREATE INDEX IF NOT EXISTS " + tDATA + "_indx1 ON " + tDATA + " (fileID, patCnt);");
         cliDB.writeScript ("CREATE INDEX IF NOT EXISTS " + tDATA + "_indx2 ON " + tDATA + " (fileID, parentPatCnt);");
         cliDB.writeScript ("CREATE INDEX IF NOT EXISTS " + tDATA + "_indx3 ON " + tDATA + " (patID, tagID);");  // relevant for <SQL calc_PathTag> in util.lsx
         cliDB.writeScript ("CREATE INDEX IF NOT EXISTS " + tDATA + "_indx4 ON " + tDATA + " (fileID, patID, patCnt);");

         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tLOG  + " (fileID int, logMessage text);");

         // o-o  Add dbMore connections info
         //
         //connexiones directas
         //
         //   conn, srcTab, srcKey, targTab, targKey
         //
         //   file  , xmelon_data, fileID, xmelon_files, fileID
         //   path  , xmelon_data, patID, xmelon_pathDef, patID
         //   tag   , xmelon_data, tagID, xmelon_tagDef, tagID
         //   parent, xmelon_data, parentPatCnt, xmelon_data, patCnt
         //
         cliDB.writeScript (deepSqlUtil.getSQL_CreateTableConnections ());
         cliDB.writeScript (deepSqlUtil.getSQL_InsertConnection("parentPath", tPATH, "parentPatID",   tPATH,  "patID"));
         cliDB.writeScript (deepSqlUtil.getSQL_InsertConnection("file"      , tDATA, "fileID",        tFILE,  "fileID"));
         cliDB.writeScript (deepSqlUtil.getSQL_InsertConnection("path"      , tDATA, "patID",         tPATH,  "patID"));
         cliDB.writeScript (deepSqlUtil.getSQL_InsertConnection("tag"       , tDATA, "tagID",         tTAG ,  "tagID"));
         //NO! es N->N !
         // cliDB.writeScript (deepSqlUtil.getSQL_InsertConnection("parent"    , tDATA, "parentPatCnt",  tDATA,  "patCnt"));

         cliDB.closeScript ();
         cliDB.runSQL (cached.dbName);

         // reset patIDList cache
         cached.patIDList = null;
         cached.tagIDList = null;

         // get last fileID
         cached.fileID = sqlUtil.getNextID(cached.dbName, cached.tabPrefix + "_files", "fileID", MIN_FILE_ID);
      }
      else cached.fileID ++;

      if (cached.patIDList != null) return;

      // ok, need the list if given

      // get the list id path ID's
      //
      cached.patIDList = cliDB.getSQL (dbName, ".headers off\nSELECT pathStr FROM " + cached.tabPrefix + "_pathDef ORDER BY patID;");
      log.dbg (2, "initialScript", "obtained pathDef list of " + cached.patIDList.size () + " elements");

      // get the list id tag ID's
      //
      cached.tagIDList = cliDB.getSQL (dbName, ".headers off\nSELECT tagStr FROM " + cached.tabPrefix + "_tagDef ORDER BY tagID;");
      log.dbg (2, "initialScript", "obtained tagDef list of " + cached.tagIDList.size () + " elements");
   }

   public TextFile openDBforFile (String dbName, String fileName, String tablePrefix)
   {
      perFile = new perFileStruc ();

      cliDB = new sqlSolver ();
      initialScript (dbName, tablePrefix);

      if (checkMisprog (cliDB == null, "cliDB")) return null;
      if (checkMisprog (cached == null, "cached")) return null;

      cliDB.openScript ();
      out ("INSERT INTO " + cached.tabPrefix + "_files VALUES (" + cached.fileID + ", '" + cliDB.escapeString (DateFormat.getTodayStr ()) + "', '" + fileName + "');");

      log.dbg (2, "processOneFile", "start parsing [" + fileName + "]");
      TextFile tf = new TextFile ();
      if (! tf.fopen (fileName, "rb"))  // mode "rb" to be able to get the InputStream!
      {
         log.err ("processOneFile", "file to parse [" + fileName + "] cannot be openned!");
         return null;
      }
      return tf;
   }

   public void closeDB ()
   {
      cliDB.closeScript ();
      log.dbg (2, "processOneFile", "storing in DB");
      cliDB.runSQL (cached.dbName);
      log.dbg (2, "processOneFile", "finished");
      cliDB = null;
   }


   /**
      Search the parent path from pathStr into the list of stored paths (with data)
   */
   protected long getPathParentIndex (String pathStr)
   {
      String presunto = "";
      long presuntoIndx = -1;
      for (int ii = 0; ii < cached.patIDList.size (); ii ++)
      {
         String presunto2 = (String) cached.patIDList.get (ii);
         if (presunto2.length () < pathStr.length () &&
             presunto2.length () > presunto.length () &&
             pathStr.startsWith (presunto2))
         {
            presunto = presunto2;
            presuntoIndx = ii;
         }
      }
      return presuntoIndx;
   }

   /**
      NOTE: Only call this function if a record is going to be stored

      When calling this function with the current path WE KNOW that there is
      data to be stored using this pathID, therefore we set HASDATA = 1 for this
      path
   */
   public long getPathTypeIdentifier ()
   {
      if (checkMisprog (perFile == null, "perFile")) return -1;
      if (checkMisprog (cached == null, "cached")) return -1;

      // form path and search for its index
      //
      int currentPathIndx = perFile.currentPath.rows ()-1;
      String pathStr = "";
      String lastNode = perFile.currentPath.getValue(currentPathIndx, cNODE);
      for (int ii = 0; ii <= currentPathIndx; ii ++)
      {
         pathStr += "/" + perFile.currentPath.getValue(ii, cNODE);
      }
      long indx = cached.patIDList.indexOf (pathStr);

      log.dbg (2, "getPathTypeIdentifier pathStr [" + pathStr + "] currentPathIndx = " + currentPathIndx + " index = " + indx);

      //SET has data flag
      if (currentPathIndx >= 0)
      {
         perFile.currentPath.setValue ("1" , currentPathIndx, cHASDATA);
      }

      if (indx == -1)
      {
         cached.patIDList.add (pathStr);
         indx = cached.patIDList.size () - 1;

         //(o) TOSEE_XMeLon is this record convenient ? 14.02.2010 02:46
         if (indx == 0)
         {
            // first one, insert path root (one per file)
            out ("INSERT OR IGNORE INTO " + cached.tabPrefix + "_pathDef VALUES (0, 0, '', '', '', 0);");
         }

         long parentIndx = getPathParentIndex (pathStr);

         log.dbg (2, "getPathTypeIdentifier a insert con parentIndx = " + parentIndx);
         out ("INSERT INTO " + cached.tabPrefix + "_pathDef VALUES ("
                  + (MIN_PAT_ID + indx) + ", "
                  + (parentIndx != -1 ? (MIN_PAT_ID + parentIndx) : 0) + ", '"
                  + utilEscapeStr.escapeStr(pathStr) + "', '"
                  + utilEscapeStr.escapeStr(naming.toVariableName (pathStr)) + "', '"
                  + utilEscapeStr.escapeStr(lastNode) + "', "
                  + perFile.currentPath.rows ()
                  + ");");
      }

      return MIN_PAT_ID + indx;
   }

   /**
      NOTE: Only call this function if a record is going to be stored
   */
   protected long getTagIdentifier (String tagOriginal)
   {
      if (checkMisprog (perFile == null, "perFile")) return -1;
      if (checkMisprog (cached == null, "cached")) return -1;

      long indx = cached.tagIDList.indexOf (tagOriginal);
      if (indx == -1)
      {
         cached.tagIDList.add (tagOriginal);
         indx = cached.tagIDList.size () - 1;

         // table xmelon_tagDef (tagID int, tagStr, tagAsColName)
         out ("INSERT INTO " + cached.tabPrefix + "_tagDef VALUES ("
                  + (MIN_PAT_ID + indx) + ", '"                      // tagID
                  + utilEscapeStr.escapeStr(tagOriginal) + "', '"    // tagStr
                  + naming.toVariableName (tagOriginal) + "'"  // tagStrNormal
                  + ");");
      }

      return MIN_PAT_ID + indx;
   }

   /**
      Get - from the current path - the first node counter having
      data (that is referenced somewhere in table xmelon_data)

      used to find the parent pathCounter
   */
   public int getPathCounterParent ()
   {
      // System.out.println ("CAMPEON ... MIRA LA SITUACION:" + perFile.currentPath);
      for (int ii = perFile.currentPath.rows () - 1; ii >= 0; ii --)
      {
         if (! perFile.currentPath.getValue(ii, cHASDATA).equals("1")) continue;

         // System.out.println ("QUE CAMPEON ERES! level " + fromIndex + " HASDATA [" + perFile.currentPath.getValue(fromIndex, cHASDATA) + "] Y SU COUNTER (CAMPEON) [" + perFile.currentPath.getValue (fromIndex, cCOUNTER) + "]");
         return stdlib.atoi (perFile.currentPath.getValue (ii, cCOUNTER));
      }
      return PARENT_COUNTER_ROOT;
   }

   /**
      if data is found it will be stored as "free text" (not structured data or document type)

      Usually no data is expected in following situations

         <some>
            FREE TEXT
            <more> normal data </more>
            FREE TEXT
         </some>
   */
   public void storeTextDataIfAny ()
   {
      if (perFile.strData.length () == 0) return; // no data, ok

      outData (getPathTypeIdentifier(), null, DATA_PLACE_FREETEXT, perFile.strData);
      perFile.strData = "";
   }

   public void outData (long pathTyId, String tagName, char dataPlace, String value)
   {
      // Note : to obtain pathTyId it would be enough to call getPathTypeIdentifier()
      //        but this function is expensive and doing it this way the caller can optimize it

      int level = perFile.currentPath.rows ()-1;
      int pathCounter       = stdlib.atoi (perFile.currentPath.getValue (level, cCOUNTER));
      int pathCounterParent = stdlib.atoi (perFile.currentPath.getValue (level, cPARENT_COUNTER));

///loca/////      String loca = "[" + getSystemId() + "/" + getPublicId() + ":" + getLineNumber() + ":" + getColumnNumber()+ "]";

      long tagID = (tagName == null) ? TAGID_FOR_FREETEXT: getTagIdentifier(tagName);

      if (perFile.dataCounter == INITIAL_DATA_COUNTER)
      {
         out ("INSERT INTO " + cached.tabPrefix + "_data VALUES (" + cached.fileID + ", 0, 0, 0, 0, 0, '', '');");
      }

      //_data  (fileID int, dataCnt int, patCnt int, parentPatCnt int, patID int, tagID int, dataPlace, value);
      out ("INSERT INTO " + cached.tabPrefix + "_data VALUES ("
              + cached.fileID + ", "
              + (perFile.dataCounter ++) + ", "
              + pathCounter + ", "
              + pathCounterParent + ", "
              + pathTyId + ", "
              + tagID + ", '"
              + dataPlace + "', '"
              + utilEscapeStr.escapeStr(value) + "');");
   }

}


/*
   - detect "text fuera de context" (document mode)
   - check "namespace", "localname" y type es coherente en cada start/close
   -

   pila historic paths
   pila current path


   tabla
      TABLE files ... ver otros parsers

      TABLE path types
         fileid, pathid, level, path (classpath ? localname ?)

      TABLE data
         fileid, pathid, pathCounter, dataNatur, name        , value
            1  , 1223  ,  99298     , 1        , CustomerName, Rongelio


         (dataNatur 1=attribute 2=normal tag)

      TABLE pathLocation (a lo mejor no es posible con la m de saxon)

         fileid, pathid, pathCounter, fromLine, fromColumn, toLine, toColumn
            1  , 1223  ,  99298     ,

            SI QUE SE PUEDE
            setDocumentLocator(Locator)


      TABLE incidences
         xxx

         fileid, pathid, pathCounter, quehapasao
            1  , 1223  ,  99298     , //document style!

*/