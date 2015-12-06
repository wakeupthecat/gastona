/*
packages de.elxala
Copyright (C) 2005  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.langutil.filedir;

// NOTA 29.06.2008 13:53: quitar acentos por el p problema con gcj "error: malformed UTF-8 character." de los c

/**   ======== de.elxala.langutil.TextFile ==========================================
   Alejandro Xalabarder

   14.04.2008 22:44     nuevo log con logger, quitar Causa y getCausa
   17.10.2004 14:42     anyadir open mode "a" y "w!" (escritura segura de cada string cierra y abre con "a" cada vez)
   11.07.2004 18:29     modificar funcio'n TheLine (robustez)
   06.06.2004 15:24     anyadir String createTemporal (...)
   31.08.2002 20:14     nuevo me'todo esta'tico "public static String [] readFile (String Nombre)"
   27.02.2002 22:57     nuevo me'todo esta'tico "public static boolean readFile (String Nombre, Cadena contenido)"
   02.11.2001 16:32     pequenyez
   17.08.2001 01:30     created
*/

import java.io.*;
import java.util.List;
import java.util.Vector;

import java.io.InputStream;   // for files from jar file (resources)
import java.net.URL;
// import java.net.URI;
import de.elxala.langutil.*;
import de.elxala.zServices.logger;

/**
   class TextFile
   @author Alejandro Xalabarder Aulet
   @date   2001

   Class to facilitate the job with text files.

   <pre>
   Example:

      TextFile wri = new TextFile ();
      if (wri.fopen ("TEST.TXT", "w"))
      {
         System.out.println ("writing file ...");
         wri.writeLine ("first line");
         wri.writeLine ("end of file");
         wri.fclose ();

         System.out.println ("reading file ...");
         TextFile rea = new TextFile ();
         if (rea.fopen ("TEST.TXT", "r"))
         {
            while (rea.readLine ())
            {
               System.out.println ("[" + rea.TheLine () + "]");
            }
            rea.fclose ();
         }
      }
   </pre>

   02.02.2005 12:00 EVA Text File Specification v1.0
*/
public class TextFile
{
   public static final String RETURN_STR = System.getProperty("line.separator", "\n");

   public static final String NEWLINE_NATIVE = System.getProperty("line.separator", "\n");
   public static final String NEWLINE_RTLF   = new String (new byte [] {13, 10});
   public static final String NEWLINE_LF     = new String (new byte [] {10});

//(o) elxala_TextFile preparation for opneAs parameter (file system --> java resource --> url)
//    (maybe has no interest at all to make it parametrizable, the default behaviour should be good enough)
   public static final int TRY_AS_FILESYSTEMS = 1;
   public static final int TRY_AS_JAVARESOURCES = 2;
   public static final int TRY_AS_URL = 4;

   // NOTE: This class is a special one regard to logger because logServer uses TextFiles to protocol
   //       all clients, therefore the special logStatic handling and the special "TextFile (logger specialLogger)" constructor
   public static logger logStatic = null;
   public logger log = null;

   private FileReader      m_fr;
   private BufferedReader  m_br;
   private FileWriter      m_fw;
   private BufferedWriter  m_bw;

   private FileOutputStream m_fos = null;
   private FileInputStream  m_fis = null;

   private InputStream     m_stream = null;
   private static final int MAX_STREAM_BUFFER = 2000;
   private byte [] m_streamBuffer = null;
   private int m_streamBufferSize = 0;
   private int m_streamIndxRead = 0;
   private boolean trece = false;

   private String   m_Line;
   private boolean  m_feof = true;
   private String   m_FileName = "";
   private boolean  m_ModeSure = false;


   public TextFile ()
   {
      initStatic ();
      log = logStatic;
   }

   public TextFile (logger specialLogger)
   {
      log = specialLogger;
   }

   void initStatic ()
   {
      if (logStatic == null)
      {
         logStatic = new logger (null, "de.elxala.langutil.filedir.TextFile", null);
      }
   }

   public void assignLogger (logger specialLogger)
   {
      log = specialLogger;
   }


   public String getFileName ()
   {
      return m_FileName;
   }

   public FileWriter getFileWriter ()
   {
      return m_fw;
   }

   public BufferedWriter getBufferedWriter ()
   {
      return m_bw;
   }

//(o) elxala_TextFile preparation for opneAs parameter (file system --> java resource --> url)
//   public boolean fopen (final String nom, final String modo, int tryOpenAs)

   /**
   *  fopen abre un fichero secuencial al viejo estilo C
   *  si ya habia un fichero abierto (en la clase) lo cierra antes.
   *  NOTA:
   *     acepta los modos
   *        "r" (que es en realidad "rt")
   *        "w" (que es en realidad "w+t")
   *        "a" (que es en realidad "at")
   *        "w!" (lo abre como "w" pero en cada escritura cierra y abre el fichero con "a")
   */
   public boolean fopen (final String nom, final String modo)
   {
      boolean bRet = true; // by the moment
      boolean forRead = modo.equals ("r") || modo.equals ("rb");

      fclose ();
      m_feof = false;      // by the moment

      m_FileName = nom;

      try
      {
         if (modo.equals ("r"))
         {
            m_fr = new FileReader (nom);
            m_br = new BufferedReader (m_fr, 8192);
         }
         else if (modo.equals ("rb"))
         {
            m_fis = new FileInputStream (nom);
         }
         else if (modo.equals ("w") || modo.equals ("w!"))
         {
            m_fw = new FileWriter (nom);
            m_bw = new BufferedWriter (m_fw);

            m_ModeSure = modo.equals ("w!");
         }
         else if (modo.equals ("wb"))
         {
            m_fos = new FileOutputStream (nom);
         }
         else if (modo.equals ("a"))
         {
            m_fw = new FileWriter (nom, true);
            m_bw = new BufferedWriter (m_fw);
         }
         else
         {
            m_feof = true;
            bRet = false;
            log.err ("fopen", "file [" +  nom  + "] wrong mode \"" + modo + "\"! (possible modes are \"r\" or \"rb\" for read, \"w\", \"w!\" or \"wb\" for write and \"a\" for append)");
         }
      }
      catch (java.io.FileNotFoundException e)
      {
         bRet = false;
         if (forRead)
         // not a bug, just first attempt failed. TextFile accepts files in own jar or other classpath location
         log.dbg (5, "fopen", "file [" + m_FileName + "] not found in normal path, now try to open it as java resource (or url)");
         else
            log.dbg (5, "fopen", "error opening file [" + m_FileName + "] for output. " + e);

      }
      catch (Exception e)
      {
         m_feof = true;
         bRet = false;

         // AFINAR Exceptions aqui' ! permitir file not found etc
         log.err ("fopen", "file [" + m_FileName + "] causes an exception " + e.toString ());
      }

      if (!forRead) return bRet;
      if (bRet)
      {
         log.dbg (5, "fopen", "file [" + m_FileName + "] opened from file system");
         return true;
      }

      // 2nd chance, search in resources (e.g. files in jar file)
      //
      URL aurl = javaLoad.getResource (nom);
      if (aurl == null)
      {
         log.dbg (5, "fopen", "file [" + m_FileName + "] not found also as java resource");
//+++log.err ("fopen", "file [" + m_FileName + "] is NO java resource ");
      }
      else
      {
         log.dbg (5, "fopen", "file [" + m_FileName + "] opened as java resource");
//+++log.err ("fopen", "file [" + m_FileName + "] SIIIN java resource ");
      }

      // 3nd chance!, search as pure URL
      //
      if (aurl == null)
      {
         try { aurl = new URL (nom); }
         catch (Exception e)
         {
            log.dbg (5, "fopen", "url exception " + e.toString ());
//+++log.err ("fopen", "file [" + m_FileName + "] causes an url exception " + e.toString ());
         }
         if (aurl == null)
         {
            log.dbg (5, "fopen", "file [" + m_FileName + "] not found also as url");
//+++log.err ("fopen", "file [" + m_FileName + "] NOL urls machos");
            return false;
         }
         else
         {
//+++log.err ("fopen", "file [" + m_FileName + "] SIIIIL urls cander");
            log.dbg (5, "fopen", "file [" + m_FileName + "] opened as url");
         }
      }

      m_feof = false;      // by the moment
      m_FileName = nom;
      bRet = true;

      try
      {
         m_stream = aurl.openStream ();
         m_streamBuffer = new byte [MAX_STREAM_BUFFER + 1]; // +1 just for "C historic reasons"
         m_streamIndxRead = 0;
         m_streamBufferSize = 0;

//+++log.err ("fopen", "file [" + m_FileName + "] tenems un url distint de null " + (m_stream != null));
      }
      catch (Exception e)
      {
         m_feof = true;
         bRet = false;
         log.err ("fopen", "exception while trying to open [" + nom + "] as stream! " + e.toString ());
      }

//+++log.err ("fopen", "RETOTO [" + m_FileName + "] " + bRet);
      return bRet;
   }

   /**
   *  feof devuelve false si el fichero esta abierto para lectura y todavi'a
   *  no se ha alcanzado el final del fichero. En otro caso retorna true.
   */
   public boolean feof ()
   {
      return m_feof;
   }

   /**
   */
   public boolean isFromJarOrResources()
   {
      return (m_stream != null);
   }

   public boolean isStream()
   {
      return (m_stream != null);
   }

   private int readACharFromJar ()
   {
      if (m_streamIndxRead < m_streamBufferSize)
         return m_streamBuffer[m_streamIndxRead++];

      m_streamIndxRead = 0;
      m_streamBufferSize = -1;
      try
      {
         m_streamBufferSize = m_stream.read (m_streamBuffer, 0, MAX_STREAM_BUFFER);
      }
      catch (Exception e)
      {
         log.err ("readACharFromJar", "file [" + m_FileName + "] causes an exception " + e.toString ());
      }

      if (m_streamBufferSize < 1)
         return m_streamBufferSize; // either -1 (end of stream) or 0 (not yet ... wait)

      return m_streamBuffer[m_streamIndxRead++];
   }

   private boolean readLineFromJar ()
   {
      // read from a FileStream
      m_Line = "";

      int charo = readACharFromJar ();
      if (charo < 0)
      {
         m_feof = true;
         return false;
      }

      while (charo >= 0)
      {
         if (charo == 13) break;
         if (charo == 10 && !trece) break;

         if (charo != 10)
         {
            m_Line += (char) charo;
         }
         trece = false;

         charo = readACharFromJar ();
      }
      trece = (charo == 13);

      m_feof = (charo == -1);
      return m_Line.length () > 0 || !m_feof;
   }

   /**
   *  lee la siguiente li'nea del fichero y retorna true si hay e'xito
   */
   public boolean readLine ()
   {
      if (isStream ())
      {
         return readLineFromJar ();
      }

      if (feof ())
         return false;

      if (m_br == null)
      {
         log.err ("readLine", "file [" + m_FileName + "] was not open for reading in text mode (\"r\")!");
         return false;
      }
      try
      {
         m_Line = m_br.readLine ();
      }
      catch (Exception e)
      {
         log.err ("readLine", "file [" + m_FileName + "] causes an exception " + e.toString ());
         return false;
      }
      m_feof = (m_Line == null);
      return !m_feof;
   }


   public int readBytes (byte[] cbuf)
   {
      int quant = 0;

      if (m_stream != null)
      {
         try
         {
            quant = m_stream.read (cbuf);
         }
         catch (Exception e)
         {
            log.err ("readBytes", "exception reading the file [" +  m_FileName  + "] as url " + e.toString ());
            return quant;
         }

         m_feof = (quant < 1);
         return quant;
      }

      if (feof ())
         return quant;

      if (m_fis == null)
      {
         log.err ("readBytes", "file [" + m_FileName + "] was not open for reading in binary mode (\"rb\")!");
         return quant;
      }

      try
      {
         quant = m_fis.read (cbuf);
      }
      catch (Exception e)
      {
         log.err ("readBytes", "exception reading the file [" +  m_FileName  + "] " + e.toString ());
         return quant;
      }

      m_feof = (quant < 1);
      return quant;
   }


   /**
   *  escribe una nueva linea (con retorno de carro!)
   */
   public boolean writeLine (final String line)
   {
      return writeString (line + RETURN_STR);
   }

   /**
   *  escribe una nueva linea (sin retorno de carro!)
   */
   public boolean writeString (final String line)
   {
      if (m_bw == null)
      {
         log.err ("writeString", "file [" + m_FileName + "] not open for write!");
         return false;
      }

      try
      {
         m_bw.write (line, 0, line.length ());
         if (m_ModeSure)
            fopen (m_FileName, "a");   // close and open again as "a" append
         return true;
      }
      catch (Exception e)
      {
         log.err ("writeString", "Exception writing into the file [" + m_FileName + "] " + e.toString ());
         return false;
      }
   }

   /**
   *  writes the contents of the entire file 'fileName' (it has to be a text file)
   */
   public void writeFileContents (String fileName)
   {
      TextFile srcFile = new TextFile ();
      if (!srcFile.fopen (fileName, "r"))
      {
         log.err ("writeFileContents", "Source file [" + fileName + "] not found or cannot be opened!");
         return;
      }

      while (srcFile.readLine ())
         writeLine (srcFile.TheLine ());

      srcFile.fclose ();
   }


   /**
   *  escribe una nueva linea (sin retorno de carro!)
   */
   public boolean writeBytes (byte [] data, int len)
   {
      if (len < 1) return false;
      if (m_fos == null)
      {
         log.err ("writeBytes", "file [" + m_FileName + "] was not open for writing in binary mode (\"wb\")");
         return false;
      }

      try
      {
         m_fos.write (data, 0, len);
      }
      catch (Exception e)
      {
         log.err ("writeBytes", "Exception writing into the file [" + m_FileName + "] " + e.toString ());
         return false;
      }
      return true;
   }


   /**
   *  retorna la u'ltima li'nea lei'da con readLine
   */
   public String TheLine ()
   {
      if (m_Line == null)
      {
         // intento de leer despues de fin de fichero ?!
         return "";
      }
      return new String (m_Line);
   }

   /**
   *  fclose cierra un fichero secuencial al viejo estilo C
   *  si ya habia un fichero abierto (en la clase) lo cierra antes.
   *  NOTA:
   *     por ahora solo acepta los modos
   *        "r" (que es en realidad "rt")
   *        "w" (que es en realidad "w+t")
   */
   public void fclose ()
   {
      try
      {
         m_feof = true;
         if (m_br != null) m_br.close ();
         if (m_fr != null) m_fr.close ();

         if (m_bw != null) m_bw.close ();
         if (m_fw != null) m_fw.close ();

         if (m_fos != null) m_fos.close ();
         if (m_fis != null) m_fis.close ();

         if (m_stream != null) m_stream.close ();
      }
      catch (Exception e)
      {
         log.err ("fclose", "Exception closing file [" + m_FileName + "] " + e.toString ());
      }

      m_br = null;
      m_fr = null;
      m_bw = null;
      m_fw = null;
      m_stream = null;
      m_fos = null;
      m_fis = null;

      m_ModeSure = false;
   }


   /**
      Lee un fichero de texto y lo retorna en la Cadena 'contenido'
      Retorna true si se ha podido leer.
   */
   public static boolean readFile (String Nombre, Cadena contenido)
   {
      TextFile fix = new TextFile ();

      if (!fix.fopen (Nombre, "r"))
      {
         logStatic.err ("readFile", "cannot read the file [" + Nombre + "] !");
         return false;
      }

      contenido.setStr ("");
      while (fix.readLine ())
      {
         contenido.o_str += fix.TheLine();
         contenido.o_str += RETURN_STR;
      }
      fix.fclose ();
      return true;
   }


   /**
      Lee un fichero de texto y lo retorna en un array de String. Si el
      fichero no ha podido leerse retorna null.
   */
   public static String [] readFile (String Nombre)
   {
      TextFile fix = new TextFile ();

      if (!fix.fopen (Nombre, "r"))
         return null;

      List lineas = new Vector ();
      while (fix.readLine ())
         lineas.add (fix.TheLine ()); // NOTE: this is "unchecked" for java 1.5 "what a pitty!" but this code has to compile with 1.1 and run as well in older JVM's
      fix.fclose ();

      String [] Contenido = new String [lineas.size ()];
      for (int ii = 0; ii < lineas.size (); ii++)
         Contenido[ii] = (String) lineas.get (ii);

      return Contenido;
   }

   /**
      Write a text file
   */
   public static boolean writeFile (String Nombre, String [] contents, boolean addReturn)
   {
      TextFile fix = new TextFile ();

      if (!fix.fopen (Nombre, "w"))
         return false;

      for (int ii = 0; ii < contents.length; ii++)
      {
         if (addReturn)
              fix.writeLine (contents[ii]);
         else fix.writeString (contents[ii]);
      }

      fix.fclose ();
      return true;
   }

   public static boolean writeFile (String Nombre, String [] contents)
   {
      return writeFile (Nombre, contents, true);
   }

   // NOTE : it can be very inefficicent if the contets is very big
   public static boolean writeFile (String Nombre, String contents)
   {
      return writeFile (Nombre, new String [] { contents }, false);
   }
}
