/*
de.elxala.parse.xml
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

package de.elxala.parse.xml;

import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.utilEscapeStr;

import de.elxala.mensaka.*;
//import javaj.widgets.basics.*;
import de.elxala.zServices.*;

import java.util.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;

/**
*/
public class saXmelon2
         extends DefaultHandler
         implements ContentHandler, EntityResolver ////loca////, Locator
{
   // sax parser reader
   //
   private XMLReader xmlReader = null;

   // gastona logger
   //
   private static logger log = new logger (null, "de.elxala.parse.xml.saXmelon2", null);

   // xml path control
   //
   private static final int cNODE=0;            // tag name
   private static final int cNAMESPACE=1;       // ...
   private static final int cLOCALNAME=2;       // ...
   private static final int cCOUNTER=3;         // unique (per xml file) path counter
   private static final int cPARENT_COUNTER=4;  // the path counter of first parent with stored data (0 if no parent)
   private static final int cHASDATA=5;         // to control whether the element has stored data or not
   private static final int cHASATTRIB=6;       // to control whether the element has stored attributes or not
//?nochut?   private static final int cPATIDINDX=6;       // to know the pathID (adding MIN_PAT_ID) of each level

   private static final long MIN_FILE_ID  = 1000;
   private static final long MIN_PAT_ID   = 100;
   private static final long TAGID_FOR_FREETEXT = 0;
   private static final int PARENT_COUNTER_ROOT = 0;
   private static final long INITIAL_DATA_COUNTER = 1;

   protected static final char DATA_PLACE_ATTRIBUTE = 'A'; // as attribute      : <a myAtt="data"> ... </a>
   protected static final char DATA_PLACE_VALUEATT  = 'V'; // as "value" attribute  : <a myAtt="xxx">data</a>
   protected static final char DATA_PLACE_FREETEXT  = 'X'; // unstructured text : <a> data <b> ... </b> data </a>
   protected static final char DATA_PLACE_TAGVALUE  = 'D'; // normal data       : <a> data </a>

   public sqlSolver cliDB = null;

   class cacheableStruct
   {
      public long fileID = 0;
      public String dbName = null;
      public String tabPrefix = "xmelon";
      public List patIDList = null;      // to store path+namespace ..
      public List tagIDList = null;      // to store tag original names ...
   }

   class perFileStruc
   {
      public Eva currentPath = new Eva(); // table (pathNode, namespace, localName, counter, hasData)
      public long dataCounter = INITIAL_DATA_COUNTER;  // incremted with each data store
      public long pathCounter = 1; // 0 means root, also as parent
      public String strData = "";
      public boolean lastWasClosingTag = false; // initial value has no meaning and will be not used
   }

   private perFileStruc perFile = null;
   private cacheableStruct cached = new cacheableStruct ();

   /**
   */
   public saXmelon2 ()
   {
      initOnce ();
   }

   public InputSource resolveEntity (String publicId, String systemId)
   {
      log.dbg (5, "ignoring [" + publicId + "] [" + systemId + "]");
      //return new InputSource(new java.io.ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
      return new InputSource(new java.io.ByteArrayInputStream(" ".getBytes()));
      //return null;
   }

   private void initOnce ()
   {
      if (xmlReader != null) return;

      log.dbg (2, "initOnce", "getting XML reader");
      // prepare it as xml reader
      try
      {
         xmlReader = ( (SAXParserFactory.newInstance ()).newSAXParser () ).getXMLReader();
         xmlReader.setContentHandler (this);
         xmlReader.setEntityResolver (this);

///loca/////         setDocumentLocator(loco);
      }
      catch (Exception e)
      {
         log.severe ("initOnce",  "getting a new xmlReader: " + e);
         // e.printStackTrace ();
      }
   }

   public void clearCache ()
   {
      cached = new cacheableStruct ();
      log.dbg (2, "clearCache", "cache cleared");
   }

   private boolean checkMisprog (boolean sayAgain, String varName)
   {
      if (sayAgain)
         log.severe ("out",  varName + " is null during operation, is saXmelon2 misprogrammed?");
      return sayAgain;
   }


   private void out (String str)
   {
      if (checkMisprog (cliDB == null, "cliDB")) return;
      cliDB.writeScript (str + "\n");
   }

///loca/////      private Locator loco = new LocatorImpl(this);

///loca/////   public int getColumnNumber()
///loca/////   {
///loca/////      return loco.getColumnNumber();
///loca/////   }
///loca/////
///loca/////   public int getLineNumber()
///loca/////   {
///loca/////      return loco.getLineNumber();
///loca/////   }
///loca/////
///loca/////   public String getPublicId()
///loca/////   {
///loca/////      return loco.getPublicId();
///loca/////   }
///loca/////
///loca/////   public String getSystemId()
///loca/////   {
///loca/////      return loco.getSystemId();
///loca/////   }

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

         //NOTE: adding tagAsColumnName will produce to much rendundance
         //cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + cached.tabPrefix + "_tagDef (tagID int, tagStr, tagAsColName);");
         //
         //xmelon visor
         //
         //   table: xmelon_tagDef (tagID, tagStr)
         //   table: xmelon_pathDef (patID, parentPatID, pathStr, lastNode, level)
         //   table: xmelon_files (fileID, timeParse, fullPath)
         //   table: xmelon_data (fileID, dataCnt, patCnt, parentPatCnt, patID, tagID, dataPlace, value)
         //
         String tTAG  = cached.tabPrefix + "_tagDef";
         String tPATH = cached.tabPrefix + "_pathDef";
         String tFILE = cached.tabPrefix + "_files";
         String tDATA = cached.tabPrefix + "_data";
         String tLOG  = cached.tabPrefix + "_log";

         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tTAG  + " (tagID int, tagStr, UNIQUE(tagID));");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tPATH + " (patID int, parentPatID int, pathStr, lastNode, level, UNIQUE(patID));");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tFILE + " (fileID int, timeParse, fullPath, UNIQUE(fileID));");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tDATA + " (fileID int, dataCnt int, patCnt int, parentPatCnt int, patID int, tagID int, dataPlace, value, UNIQUE(fileID, dataCnt));");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + tLOG  + " (fileID int, logMessage);");

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
         cliDB.writeScript (dbMore.getSQL_CreateTableConnections ());
         cliDB.writeScript (dbMore.getSQL_InsertConnection("parentPath", tPATH, "parentPatID",   tPATH,  "patID"));
         cliDB.writeScript (dbMore.getSQL_InsertConnection("file"      , tDATA, "fileID",        tFILE,  "fileID"));
         cliDB.writeScript (dbMore.getSQL_InsertConnection("path"      , tDATA, "patID",         tPATH,  "patID"));
         cliDB.writeScript (dbMore.getSQL_InsertConnection("tag"       , tDATA, "tagID",         tTAG ,  "tagID"));
         //NO! es N->N !
         // cliDB.writeScript (dbMore.getSQL_InsertConnection("parent"    , tDATA, "parentPatCnt",  tDATA,  "patCnt"));

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


   public void parseFile (String fileToParse, String dbName, String tablePrefix)
   {
      parseFile (fileToParse, dbName, tablePrefix, false);
   }

   /**
      Parses the xml file 'fileToParse' and places the results in an xmelon
      schema into the database dbName using 'tablePrefix' for naming the tables.
   */
   public void parseFile (String fileToParse, String dbName, String tablePrefix, boolean keepCache)
   {
      perFile = new perFileStruc ();

      cliDB = new sqlSolver ();
      initialScript (dbName, tablePrefix);
      processOneFile (fileToParse);
      cliDB = null;
      if (!keepCache)
         cached = new cacheableStruct ();
   }

   private void processOneFile (String fileName)
   {
      if (checkMisprog (cliDB == null, "cliDB")) return;
      if (checkMisprog (cached == null, "cached")) return;

      cliDB.openScript ();
      out ("INSERT INTO " + cached.tabPrefix + "_files VALUES (" + cached.fileID + ", '" + cliDB.escapeString (DateFormat.getTodayStr ()) + "', '" + fileName + "');");

      log.dbg (2, "processOneFile", "start parsing [" + fileName + "]");
      try
      {
         // System.out.println ("Processing .. [" + fileName + "]");
         xmlReader.parse (new org.xml.sax.InputSource (fileName));
      }
      catch (Exception e)
      {
         log.err ("processOneFile", "file to parse [" + fileName + "] " + "" + e);
         //e.printStackTrace ();
      }

      // run the script even if there is some parse error
      // this way we can know the last succsessful record
      cliDB.closeScript ();
      log.dbg (2, "processOneFile", "storing in DB");
      cliDB.runSQL (cached.dbName);
      log.dbg (2, "processOneFile", "finished");
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
   protected long getPathTypeIdentifier ()
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
            out ("INSERT OR IGNORE INTO " + cached.tabPrefix + "_pathDef VALUES (0, 0, '', '', 0);");
         }

         long parentIndx = getPathParentIndex (pathStr);

         log.dbg (2, "getPathTypeIdentifier a insert con parentIndx = " + parentIndx);
         out ("INSERT INTO " + cached.tabPrefix + "_pathDef VALUES ("
                  + (MIN_PAT_ID + indx) + ", "
                  + (parentIndx != -1 ? (MIN_PAT_ID + parentIndx) : 0) + ", '"
                  + utilEscapeStr.escapeStr(pathStr) + "', '"
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
                  + utilEscapeStr.escapeStr(tagOriginal) + "'"    // tagStr
//                  + naming.toNameISO_9660Joliet (tagOriginal) + "'"  // tagAsColName
                  + ");");
      }

      return MIN_PAT_ID + indx;
   }

   /**
      Get - from the current path - the first node counter having
      data (that is referenced somewhere in table xmelon_data)

      used to find the parent pathCounter
   */
   private int getPathCounterParent ()
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

      Usually no data is spected in following situations

         <some>
            FREE TEXT
            <more> normal data </more>
            FREE TEXT
         </some>
   */
   protected void storeTextDataIfAny ()
   {
      if (perFile.strData.length () == 0) return; // no data, ok

      outData (getPathTypeIdentifier(), null, DATA_PLACE_FREETEXT, perFile.strData);
      perFile.strData = "";
   }

   private void outData (long pathTyId, String tagName, char dataPlace, String value)
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

   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o
   // IMPLEMENTATION Interface ContentHandler
   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o

   public void startElement (String namespace, String localname,  String type, Attributes attributes) throws SAXException
   {
      perFile.lastWasClosingTag = false;

      // If any data found, store it as "free text"
      // e.g.  <some> FREE TEXT <more> ...
      storeTextDataIfAny ();

      if (checkMisprog (perFile == null, "perFile")) return;
      if (checkMisprog (cached == null, "cached")) return;

      EvaLine ele = new EvaLine();

      ele.setValue (type, cNODE);
      ele.setValue (namespace, cNAMESPACE);
      ele.setValue (localname, cLOCALNAME);
      ele.setValue ("" + (perFile.pathCounter ++), cCOUNTER);
      ele.setValue ("" + getPathCounterParent (), cPARENT_COUNTER);
      ele.setValue ("0", cHASDATA); // we don't know yet
      ele.setValue ("0", cHASATTRIB); // we don't know yet

      // push element stack
      perFile.currentPath.addLine (ele);

      long pathTyId = -1;

      // collect attribute names
      //
      for (int ii = 0; ii < attributes.getLength (); ii ++)
      {
         if (pathTyId == -1)
            pathTyId = getPathTypeIdentifier();

         perFile.currentPath.setValue ("1" , perFile.currentPath.rows ()-1, cHASATTRIB);
         outData (pathTyId, attributes.getQName (ii), DATA_PLACE_ATTRIBUTE, attributes.getValue (ii));
      }
   }

   public void endElement (String namespace, String localname, String type) throws SAXException
   {
      if (checkMisprog (perFile == null, "perFile")) return;
      if (checkMisprog (cached == null, "cached")) return;

      //check if text data
      if (perFile.lastWasClosingTag)
      {
         log.dbg (2, "endElement", "lastWasClosingTag");
         // If any data found, store it as "free text"
         // e.g.  </more> FREE TEXT </some>
         storeTextDataIfAny ();
         perFile.currentPath.removeLine (perFile.currentPath.rows () - 1);
      }
      else
      {
         log.dbg (2, "endElement", "not lastWasClosingTag");

         // decide if store the element in the current path or in the previous path
         //

         int lastIndx = perFile.currentPath.rows () - 1;
         boolean hasAtt  = perFile.currentPath.getValue(lastIndx, cHASATTRIB).equals("1");
         boolean hasData = perFile.currentPath.getValue(lastIndx, cHASDATA).equals("1");
         boolean hasValue = perFile.strData.length () > 0;
         boolean stored = false;

         // if hasAtt is true and we end the tag with data
         // then are two possible cases
         //
         //    hasData

         if (hasValue && hasAtt)
         {
            log.dbg (2, "endElement", "data [" + perFile.strData + "] stored as attribute \"value\" (V)");
            outData (getPathTypeIdentifier(), type + "_value", DATA_PLACE_VALUEATT, perFile.strData);
            stored = true;

            //Note : if aditionally hasData were true, we would have following case
            //          <tagXY at1="aaa">
            //             <field>bbbb</field>
            //             data!!???
            //          </tagXY>
            // We store it as "attribute value", it could be considered as free text as well ...
         }

         // remove this path
         perFile.currentPath.removeLine (perFile.currentPath.rows () - 1);

         // only save data if there is any data at all!
         if (hasValue && !stored)
         {
            log.dbg (2, "endElement", "data [" + perFile.strData + "] AS normal tag (T)");
            outData (getPathTypeIdentifier(), type, DATA_PLACE_TAGVALUE, perFile.strData);
         }
      }

      perFile.lastWasClosingTag = true;
      perFile.strData = "";
   }

   public void characters (char[] ch, int start, int len)
   {
      perFile.strData += (new String (ch, start, len)).trim ();
   }


   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o
   // MAIN CALL
   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o

   public static void main (String [] aa)
   {
      if (aa.length < 1)
      {
         System.out.println ("saXmelon2 Syntax: dbName xmlfile ...");
         return;
      }

      if ((new java.io.File("sessionLog")).exists ())
      {
         System.setProperty (org.gastona.gastonaCtes.PROP_SESSION_LOG_DIR, ".\\sessionLog");
         logServer.configure (".\\sessionLog\\", "xmelon", "v1.0");
      }


      // aa = new String [] {"data\\test_01.xml"};
      String outputDBName = aa[0];

      //Check is a valid sqlite3 db! (or not exists)
      TextFile dbFile = new TextFile ();
      if (dbFile.fopen(outputDBName, "rb"))
      {
         byte [] magic = new byte[16];

         int leidos = dbFile.readBytes(magic);
         boolean bad = (leidos > 0 && leidos != 16);
         if (bad || !(new String (magic)).equals("SQLite format 3\0"))
         {
            System.out.println ("First parameter has to be either a non existing file or a sqlite3 database file!");
            System.out.println ("[" + outputDBName + "] seems to belong to another kind of file.");
            System.out.println ("Take care and walk along the shadow! (leidos = " + leidos + " composasa [" + new String (magic) + "]");
            System.exit (1);
         }
      }

      saXmelon2 saxan = new saXmelon2 ();
      for (int ii = 1; ii < aa.length; ii ++)
      {
         System.out.println ("parsing " + aa[ii] + "...");
         saxan.parseFile (aa[ii], outputDBName, "xmelon");
      }

      System.out.println ("well done!");
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