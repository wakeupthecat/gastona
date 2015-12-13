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

import de.elxala.zServices.*;

import java.util.*;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import de.elxala.parse.xmelonSchema;

/**
*/
public class saXmelon
         extends DefaultHandler
         implements ContentHandler, EntityResolver ////loca////, Locator
{
   private static logger log = new logger (null, "de.elxala.parse.xml.saXmelon", null);

   // sax parser reader
   //
   private XMLReader xmlReader = null;
   private xmelonSchema xemi = new xmelonSchema ();

   /**
   */
   public saXmelon ()
   {
      initOnce ();
   }

   /// ignore rootTag list : these tags and all its descendants will be ignored
   public List optRootTagIgnoreList = new Vector ();

   /// ignore subTag list : these tags will be transparent (the descendant elements will be treated)
   public List optTransparentTagList = new Vector ();

   /// the tags will be replaced in the database
   public Map optTagAliases = new TreeMap ();

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

   public void parseFile (String fileToParse, String dbName, String tablePrefix)
   {
      parseFile (fileToParse, dbName, tablePrefix, false);
   }

   /**
      Parses the xml file 'fileToParse' and places the results in an xmelon
      schema into the database dbName using 'tablePrefix' for naming the tables.
   */
   public void parseFile (String fileToParse,
                          String dbName,
                          String tablePrefix,
                          boolean keepCache)
   {
      processOneFile (dbName, fileToParse, tablePrefix, keepCache);
   }

   public void clearCache ()
   {
      xemi.clearCache ();
   }

   private void processOneFile (String dbName, String fileName, String tablePrefix, boolean keepCache)
   {
      TextFile textF = xemi.openDBforFile (dbName, fileName, tablePrefix);
      if (textF == null) return;
      try
      {
         xmlReader.parse (new org.xml.sax.InputSource (textF.getAsInputStream ()));
      }
      catch (Exception e)
      {
         log.err ("processOneFile", "file to parse [" + fileName + "] " + "" + e);
         //e.printStackTrace ();
      }

      // run the script even if there is some parse error
      // this way we can know the last succsessful record
      xemi.closeDB ();
      if (!keepCache)
         xemi.clearCache ();
   }

   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o
   // IMPLEMENTATION Interface ContentHandler
   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o

   public void startElement (String namespace, String localname,  String type, Attributes attributes) throws SAXException
   {
      xemi.perFile.lastWasClosingTag = false;

      // If any data found, store it as "free text"
      // e.g.  <some> FREE TEXT <more> ...
      xemi.storeTextDataIfAny ();

      if (xemi.checkMisprog (xemi.perFile == null, "perFile")) return;
      if (xemi.checkMisprog (xemi.cached == null, "cached")) return;

      // resolve alias
      //
      String alias = (String) optTagAliases.get (type);
      if (alias != null)
         type = alias;

      if (optTransparentTagList.contains (type))
      {
         log.dbg (2, "startElement", "transparent tag " + type + " opened");
      }
      else
      {
         // add new element path (new layer or xtable)
         //
         EvaLine ele = new EvaLine ();

         ele.setValue (type, xemi.cNODE);
         ele.setValue (namespace, xemi.cNAMESPACE);
         ele.setValue (localname, xemi.cLOCALNAME);
         ele.setValue ("" + (xemi.perFile.pathCounter ++), xemi.cCOUNTER);
         ele.setValue ("" + xemi.getPathCounterParent (), xemi.cPARENT_COUNTER);
         ele.setValue ("0", xemi.cHASDATA); // we don't know yet
         ele.setValue ("0", xemi.cHASATTRIB); // we don't know yet

         // push element stack
         xemi.perFile.currentPath.addLine (ele);
      }

      if (optRootTagIgnoreList.contains (type))
      {
         log.dbg (2, "startElement", "root tag " + type + " will be ignored");
         xemi.perFile.ignoringContent = true;
         return;
      }

      long pathTyId = -1;

      // collect attribute names
      //
      for (int ii = 0; ii < attributes.getLength (); ii ++)
      {
         if (pathTyId == -1)
            pathTyId = xemi.getPathTypeIdentifier();

         xemi.perFile.currentPath.setValue ("1" , xemi.perFile.currentPath.rows ()-1, xemi.cHASATTRIB);
         xemi.outData (pathTyId, attributes.getQName (ii), xemi.DATA_PLACE_ATTRIBUTE, attributes.getValue (ii));
      }
   }

   public void endElement (String namespace, String localname, String type) throws SAXException
   {
      if (xemi.checkMisprog (xemi.perFile == null, "perFile")) return;
      if (xemi.checkMisprog (xemi.cached == null, "cached")) return;

      if (optTransparentTagList.contains (type))
      {
         log.dbg (2, "endElement", "transparent tag " + type + " closed");
         return;
      }

      String alias = (String) optTagAliases.get (type);
      if (alias != null)
         type = alias;

      //System.out.println (" END ELEMENT [" + type + "] data [" + perFile.strData + "]" );

      if (optRootTagIgnoreList.contains (type))
      {
         log.dbg (2, "endElement", "ignoring tag root " + type + " closed");
         xemi.perFile.ignoringContent = false;
      }

      //check if text data
      if (xemi.perFile.lastWasClosingTag)
      {
         log.dbg (2, "endElement", "lastWasClosingTag");
         // If any data found, store it as "free text"
         // e.g.  </more> FREE TEXT </some>
         xemi.storeTextDataIfAny ();
         xemi.perFile.currentPath.removeLine (xemi.perFile.currentPath.rows () - 1);
      }
      else
      {
         log.dbg (2, "endElement", "not lastWasClosingTag");

         // decide if store the element in the current path or in the previous path
         //

         int lastIndx = xemi.perFile.currentPath.rows () - 1;
         boolean hasAtt  = xemi.perFile.currentPath.getValue(lastIndx, xemi.cHASATTRIB).equals("1");
         boolean hasData = xemi.perFile.currentPath.getValue(lastIndx, xemi.cHASDATA).equals("1");
         boolean hasValue = xemi.perFile.strData.length () > 0;

         // if hasAtt is true and we end the tag with data
         // then are two possible cases
         //
         //    hasData
         if (hasValue && hasAtt)
         {
            // the tag has attributes so we need a field name for the data (xxx_value)
            //
            log.dbg (2, "endElement", "data [" + xemi.perFile.strData + "] stored as attribute \"value\" (V)");
            xemi.outData (xemi.getPathTypeIdentifier(), type + "_value", xemi.DATA_PLACE_VALUEATT, xemi.perFile.strData);

            //Note : if aditionally hasData were true, we would have following case
            //          <tagXY at1="aaa">
            //             <field>bbbb</field>
            //             data!!???
            //          </tagXY>
            // We store it as "attribute value", it could be considered as free text as well ...
         }

         // remove this path
         xemi.perFile.currentPath.removeLine (xemi.perFile.currentPath.rows () - 1);

         // only save data if there is any data at all!
         if (hasValue && !hasAtt)
         {
            log.dbg (2, "endElement", "data [" + xemi.perFile.strData + "] AS normal tag (T)");
            xemi.outData (xemi.getPathTypeIdentifier(), type, xemi.DATA_PLACE_TAGVALUE, xemi.perFile.strData);
         }
      }

      xemi.perFile.lastWasClosingTag = true;
      xemi.perFile.strData = "";
   }

   public void characters (char[] ch, int start, int len)
   {
      if (!xemi.perFile.ignoringContent)
         xemi.perFile.strData += (new String (ch, start, len)).trim ();
   }


   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o
   // MAIN CALL
   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o

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
            System.out.println ("Take care and walk by the shadow (on a heavy sun)! (leidos = " + leidos + " composasa [" + new String (magic) + "]");
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