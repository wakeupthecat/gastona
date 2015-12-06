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

/**
*/
public class saXmelon
         extends DefaultHandler
         implements ContentHandler ////loca////, Locator
{
   // sax parser reader
   //
   private XMLReader xmlReader = null;

   // gastona logger
   //
   private logger log = new logger (this, "de.elxala.parse.xml.saXmelon", null);

   // xml path control
   //
   private static final int cNODE=0;
   private static final int cNAMESPACE=1;
   private static final int cLOCALNAME=2;
   private static final int cCOUNTER=3;

   public sqlSolver cliDB = null;

   class perFileStruc
   {
      public Eva currentPath = new Eva();
      public int pathCounter = 0;
      public String strData = "";
   }

   private static final long MIN_FILE_ID = 1000;
   private static final long MIN_PAT_ID = 100;

   class cacheableStruct
   {
      public long fileID = 0;
      public String dbName = null;
      public String tabPrefix = "xmelon";
      public List patIDList = null;      // to store path+namespace ..
      public int loadedIDs = 0;
   }

   private perFileStruc perFile = null;
   private cacheableStruct cached = new cacheableStruct ();

   /**
   */
   public saXmelon ()
   {
      initOnce ();
   }

   private void initOnce ()
   {
      if (xmlReader != null) return;

      // prepare it as xml reader
      try
      {
         xmlReader = ( (SAXParserFactory.newInstance ()).newSAXParser () ).getXMLReader();
         xmlReader.setContentHandler (this);
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
   }

   private boolean checkMisprog (boolean sayAgain, String varName)
   {
      if (sayAgain)
         log.severe ("out",  varName + " is null during operation, is saXmelon misprogrammed?");
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
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + cached.tabPrefix + "_pathDef (patID int, pathStr);");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + cached.tabPrefix + "_files (fileID int, timeParse, fullPath);");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + cached.tabPrefix + "_data  (fileID int, patID int, patCnt int, AttOrData, nameNorm, nameOri, value);");
         cliDB.writeScript ("CREATE TABLE IF NOT EXISTS " + cached.tabPrefix + "_log   (fileID int, logMessage);");
         cliDB.closeScript ();
         cliDB.runSQL (cached.dbName);

         // reset patIDList cache
         cached.patIDList = null;

         // get last fileID
         cached.fileID = sqlUtil.getNextID(cached.dbName, cached.tabPrefix + "_files", "fileID", MIN_FILE_ID);
      }
      else cached.fileID ++;

      if (cached.patIDList != null) return;

      // ok, need the list if given

      // get though script to avoid header
//      cliDB.openScript ();
//      cliDB.writeScript (".headers off;SELECT pathStr FROM " + cached.tabPrefix + "_pathDef ORDER BY patID;");
//      cliDB.closeScript ();
      cached.patIDList = cliDB.getSQL (dbName, ".headers off\nSELECT pathStr FROM " + cached.tabPrefix + "_pathDef ORDER BY patID;");
      cached.loadedIDs = cached.patIDList.size ();
      log.dbg (2, "initialScript", "obtained pathDef list of " + cached.loadedIDs + " elements");
   }

//   protected void vuelcaPathIDs ()
//   {
//      if (checkMisprog (cliDB == null, "cliDB")) return;
//      if (checkMisprog (cached == null, "cached")) return;
//   }

   public void parseFile (String fileToParse, String dbName, String tablePrefix)
   {
      perFile = new perFileStruc ();

      cliDB = new sqlSolver ();
      initialScript (dbName, tablePrefix);
      processOneFile (fileToParse);
      cliDB = null;
   }

   private void processOneFile (String fileName)
   {
      if (checkMisprog (cliDB == null, "cliDB")) return;
      if (checkMisprog (cached == null, "cached")) return;

      cliDB.openScript ();
      out ("INSERT INTO " + cached.tabPrefix + "_files VALUES (" + cached.fileID + ", '" + cliDB.escapeString (DateFormat.getTodayStr ()) + "', '" + fileName + "');");

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
//      vuelcaPathIDs ();
      cliDB.closeScript ();
      cliDB.runSQL (cached.dbName);
   }

   /**
      NOTE: Only call this function if a record is going to be stored
   */
   protected long getPathTypeIdentifier ()
   {
      if (checkMisprog (perFile == null, "perFile")) return -1;
      if (checkMisprog (cached == null, "cached")) return -1;

      String pathStr = "";
      for (int ii = 0; ii < perFile.currentPath.rows (); ii ++)
      {
         pathStr += "/" + perFile.currentPath.getValue(ii, cNODE);
      }
      long indx = cached.patIDList.indexOf (pathStr);
      if (indx == -1)
      {
         cached.patIDList.add (pathStr);
         indx = cached.patIDList.size () - 1;
         out ("INSERT INTO " + cached.tabPrefix + "_pathDef VALUES ("
                  + (MIN_PAT_ID + indx) + ", '"
                  + utilEscapeStr.escapeStr(pathStr) + "');");
      }

      return MIN_PAT_ID + indx;
   }

   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o
   // IMPLEMENTATION Interface ContentHandler
   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o

   public void startElement (String namespace, String localname,  String type, Attributes attributes) throws SAXException
   {
      if (checkMisprog (perFile == null, "perFile")) return;
      if (checkMisprog (cached == null, "cached")) return;

      EvaLine ele = new EvaLine();

      ele.setValue (type, cNODE);
      ele.setValue (namespace, cNAMESPACE);
      ele.setValue (localname, cLOCALNAME);
      ele.setValue ("" + (perFile.pathCounter ++), cCOUNTER);

      // push element stack
      perFile.currentPath.addLine (ele);

      long pathTyId = -1;

      // collect attribute names
      //
      for (int ii = 0; ii < attributes.getLength (); ii ++)
      {
         if (pathTyId == -1)
            pathTyId = getPathTypeIdentifier();

         out ("INSERT INTO " + cached.tabPrefix + "_data VALUES ("
                 + cached.fileID + ", "
                 + pathTyId + ", "
                 + (perFile.pathCounter - 1) + ", "
                 + "'A'" + ", '"
                 + naming.toNameISO_9660Joliet (attributes.getQName (ii)) + "', '"
                 + utilEscapeStr.escapeStr(attributes.getQName (ii)) + "', '"
                 + utilEscapeStr.escapeStr(attributes.getValue (ii)) + "');");
      }
   }

   public void endElement (String namespace, String localname, String type) throws SAXException
   {
      if (checkMisprog (perFile == null, "perFile")) return;
      if (checkMisprog (cached == null, "cached")) return;

      //TODO! check if namespace y localname is ok
      //
      // pop element stack
      perFile.currentPath.removeLine (perFile.currentPath.rows ()-1);

      if (perFile.strData.length () == 0) return;
      if (perFile.currentPath.rows () == 0)
      {
         out ("INSERT INTO " + cached.tabPrefix + "_log VALUES (" + cached.fileID + ", 'unexpected data [" + utilEscapeStr.escapeStr(perFile.strData) + "] on root [" + utilEscapeStr.escapeStr(type) + "]');");
         perFile.strData = "";
         return;
      }

      // ++++ record the data
      //
      //(o) EN REALIDAD INTERESA EL ULTIMO PATH CON DATOS !!!!
      int pathCntParent = stdlib.atoi (perFile.currentPath.getValue (perFile.currentPath.rows ()-1, cCOUNTER));
      pathCntParent = -1; // esta mal!!!


      long pathTyId = getPathTypeIdentifier();
      out ("INSERT INTO " + cached.tabPrefix + "_data VALUES ("
               + cached.fileID + ", "
               + pathTyId + ", "
               + pathCntParent + ", "
               + "'D'" + ", '"
               + naming.toNameISO_9660Joliet (type) + "', '"
               + utilEscapeStr.escapeStr(type) + "', '"
               + utilEscapeStr.escapeStr(perFile.strData) + "');");
///loca/////      String loca = "[" + getSystemId() + "/" + getPublicId() + ":" + getLineNumber() + ":" + getColumnNumber()+ "]";
///loca/////      outFitx.writeString ("INSERT INTO dadar VALUES (" + pathID + ", '" + type + "', '" + eData.getValue(0,0) + "'); " + loca + "\n");
      perFile.strData = "";
   }

   public void characters (char[] ch, int start, int len)
   {
//      if (strData == null) strData = ""
//      {
//         out ("INSERT INTO " + cached.tabPrefix + "_log VALUES (" + fileID + ", 'date over data [" + strData + "]');");
//      }

      perFile.strData += (new String (ch, start, len)).trim ();
   }


   public static void main (String [] aa)
   {
      if (aa.length < 1)
      {
         System.out.println ("saXmelon Syntax: dbName xmlfile ...");
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
            System.out.println ("Take care and walk by the shadow! (leidos = " + leidos + " composasa [" + new String (magic) + "]");
            System.exit (1);
         }
      }

      saXmelon saxan = new saXmelon ();
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