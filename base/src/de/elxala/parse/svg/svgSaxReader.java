/*
de.elxala.parse.xml
Copyright (C) 2011 Alejandro Xalabarder Aulet

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

package de.elxala.parse.svg;

import javax.xml.parsers.*;
import de.elxala.langutil.stdlib;
import de.elxala.langutil.filedir.TextFile;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import de.elxala.zServices.*;

public class svgSaxReader extends DefaultHandler
                          implements ContentHandler, EntityResolver
{
   private static logger log = new logger (null, "de.elxala.parse.svg.svgSaxReader", null);

   // sax parser reader
   //
   private XMLReader xmlReader = null;

   // to store the current path
   //
   private String [] currentPathArr = new String [20];
   private int currentPathLen = 0;

   // listener that will process all svg tags send by the reader
   //
   private svgSaxListener theSvgProcesor = null;

   public svgSaxReader (svgSaxListener svgProcesor)
   {
      theSvgProcesor = svgProcesor;
      initOnce ();
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
      }
      catch (Exception e)
      {
         log.severe ("initOnce", "error getting XML reader");
      }
   }

   private void clear ()
   {
      strData = "";
      groupStyle = "";
      lastAttText = null;
      currentPathLen = 0;
   }

   private String currentPathStr ()
   {
      String ss = "";

      for (int ii = 0; ii < currentPathLen; ii ++)
         ss += "/" + currentPathArr[ii];
      return ss;
   }

   private void pushCurrentPath (String tag)
   {
      currentPathArr[currentPathLen ++] = tag;
   }

   private boolean popCurrentPath ()
   {
      if (currentPathLen <= 0) return false;
      currentPathLen --;
      return true;
   }

   public InputSource resolveEntity (String publicId, String systemId)
   {
      log.dbg (4, "resolveEntity", "ignoring [" + publicId + "] [" + systemId + "]");

      //return new InputSource(new java.io.ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
      return new InputSource(new java.io.ByteArrayInputStream(" ".getBytes()));
   }

   public void parseFile (String fileToParse)
   {
      clear ();
      processOneFile (fileToParse);
   }

   private void processOneFile (String fileName)
   {
      TextFile tfile = new TextFile ();
      java.io.InputStream elis = null;

      if (tfile.fopen (fileName, "rb"))
         elis = tfile.getAsInputStream ();         
      else
      {
         log.err ("processOneFile", "file cannot be opened [" + fileName + "] ... ");
         return;
      }
      
      try
      {
         log.dbg (2, "processOneFile", "Processing [" + fileName + "] ...");
         //xmlReader.parse (new org.xml.sax.InputSource (new java.io.FileInputStream (fileName)));
         xmlReader.parse (new org.xml.sax.InputSource (elis));         
      }
      catch (java.io.FileNotFoundException nfound)
      {
         log.err ("processOneFile", "file not found [" + fileName + "] ... ");
      }
      catch (Exception e)
      {
         log.severe ("processOneFile", "exception parsing [" + fileName + "] ... " + e);
      }
   }

   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o
   // IMPLEMENTATION Interface ContentHandler
   // o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o-o

   private String strData = "";
   private String groupStyle = "";
   private Attributes lastAttText = null;

   private static final String styleAttNames [] = new String [] { "fill", "fill-opacity", "fill-rule", "stroke", "stroke-width", "stroke-opacity", "rotate" };

   private String concat (String s1, String s2)
   {
      return concat (s1, s2, ";");
   }

   private String concat (String s1, String s2, String joinStr)
   {
      if (s1 == null) s1 = "";
      if (s2 == null) s2 = "";
      return s1 + (s1.length () > 0 && s2.length () > 0 ? joinStr: "") + s2;
   }

   private String buildStyle (Attributes att)
   {
      String strStyle = "";
      String attVal = "";

      for (int ii = 0; ii < styleAttNames.length; ii ++)
      {
         attVal = att.getValue(styleAttNames[ii]);
         if (attVal != null)
            strStyle = concat (styleAttNames[ii] + ":" + attVal, strStyle);
      }

      return concat (groupStyle, concat (strStyle, att.getValue("style")));
   }

   public void startElement (String namespace, String localname, String tagName, Attributes attributes) throws SAXException
   {
//android.util.Log.d ("svgSaxReader", "startElement: tagName [" + tagName + "]");
      log.dbg (4, "startElement", "start tag [" + tagName + "] ...");
      pushCurrentPath (tagName);
      String nowPath = currentPathStr ();

      // can be "/svg/g/path" or "/svg/path"
      //
      if (nowPath.equals ("/svg"))
      {
         theSvgProcesor.processSvgSetDimension (attributes.getValue("width"), attributes.getValue("height"));
      }
      if (tagName.equals ("g"))
      {
         groupStyle = "";
         groupStyle = buildStyle (attributes);
      }
      if (tagName.equals ("path"))
      {
         String data = attributes.getValue("d");
         if (data != null)
            theSvgProcesor.processSvgPath (data, buildStyle (attributes), false, false);
      }
      else if (tagName.equals ("polygon") || tagName.equals ("polyline"))
      {
         String data = attributes.getValue("points");
         if (data != null)
            theSvgProcesor.processSvgPath (data, buildStyle (attributes), true, tagName.equals ("polygon"));
      }
      else if (tagName.equals ("text") || tagName.equals ("tspan"))
      {
         lastAttText = new AttributesImpl (attributes);
      }
      else if (tagName.equals ("rect"))
      {
         float x = (float) stdlib.atof (attributes.getValue("x"));
         float y = (float) stdlib.atof (attributes.getValue("y"));
         float dx = (float) stdlib.atof (attributes.getValue("width"));
         float dy = (float) stdlib.atof (attributes.getValue("height"));
         float rx = (float) stdlib.atof (attributes.getValue("rx"));
         float ry = (float) stdlib.atof (attributes.getValue("ry"));
         theSvgProcesor.processSvgRect (x, y, dx, dy, rx, ry, buildStyle (attributes));
      }
      else if (tagName.equals ("circle"))
      {
         float cx = (float) stdlib.atof (attributes.getValue("cx"));
         float cy = (float) stdlib.atof (attributes.getValue("cy"));
         float rad = (float) stdlib.atof (attributes.getValue("r"));
         theSvgProcesor.processSvgCircle (cx, cy, rad, buildStyle (attributes));
      }
      else if (tagName.equals ("ellipse"))
      {
         float cx = (float) stdlib.atof (attributes.getValue("cx"));
         float cy = (float) stdlib.atof (attributes.getValue("cy"));
         float rx = (float) stdlib.atof (attributes.getValue("rx"));
         float ry = (float) stdlib.atof (attributes.getValue("ry"));
         theSvgProcesor.processSvgEllipse (cx, cy, rx, ry, buildStyle (attributes));
      }
   }

   public void endElement (String namespace, String localname, String tagName) throws SAXException
   {
      log.dbg (4, "endElement", "end tag [" + tagName + "] ...");
      if (tagName.equals ("text") || tagName.equals ("tspan"))
      {
         theSvgProcesor.processSvgText (strData, lastAttText);
         strData = "";
      }
      popCurrentPath ();
   }

   public void characters (char[] ch, int start, int len)
   {
      log.dbg (4, "characters", "reading " + len + " data characters");
      strData += (new String (ch, start, len)).trim ();
   }
}

