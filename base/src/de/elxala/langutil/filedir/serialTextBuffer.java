/*
java package de.elxala.Eva (see EvaFormat.PDF)
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

import java.util.*;
import java.lang.StringBuffer;
import de.elxala.zServices.*;

/**
   class serialString
   @author Alejandro Xalabarder Aulet
   @date   2010

        A serialTextBuffer is an array of Strings that is virtually not limited since, if required, a 
        file would be used transparently. It can only be read through the method getNextLine ().

   The idea is to have a general propose text buffer that for small texts works totally in memory
   and that can also handle arbitrary big texts.

      Example of use:

      serialTextBuffer endlessStream = new serialTextBuffer ();

      // writing something on the (i.e. a script or a HTTP response etc)
      endlessStream.writeln ("line 1");
      endlessStream.writeln ("line 2");

      // readind the content
      endlessStream.rewind ();   // not strictly needed
      while (endlessStream.getNextLine ())
         System.out.println (endlessStream.getLastReadLine());

*/
public class serialTextBuffer
{
   private static logger log = new logger (null, "de.elxala.langutil.filedir.serialTextBuffer", null);

   private static final int MAX_LINES_KEEP = 3000;

   private static final int STATE_IDDLE = 0;
   private static final int STATE_WRITING = 1;
   private static final int STATE_READING = 2;

   protected int currentState = STATE_IDDLE;

   //memory
   protected List     strArr = new Vector ();
   protected int      nextLine2read = 0;
   protected String   lastReadLine = null;
   protected int      lastIndxRead = 0; // only to read as binary file!
   
   protected StringBuffer   lastWriteLineWithNoReturn = new StringBuffer ();
   protected String   lastWantedNewLineString = TextFile.RETURN_STR; // by defect is this, but if writeNewLine(arg) is called then it will take this value

   //file
   protected String   tmpFileName = null;
   protected TextFile txtFile = new TextFile ();

   public void clear ()
   {
      freeFile ();
      strArr = new Vector ();
      nextLine2read = 0;
      lastReadLine = null;
      lastIndxRead = 0;
      currentState = STATE_IDDLE;
   }

   public void write (String str)
   {
      writeString (str);
   }

   public void writeln (String str)
   {
      writeString (str);
      writeln ();
   }

   public void writeln ()
   {
      addLine2Array (lastWriteLineWithNoReturn.toString ());
      lastWriteLineWithNoReturn = new StringBuffer ();
   }
   
   protected void addLine2Array (String str)
   {
      log.dbg (2, "addLine2Array", "new line size " + str.length ());
      strArr.add (str);
      if (strArr.size () > MAX_LINES_KEEP)
      {
         log.dbg (2, "addLine2Array", "save into disk " + strArr.size () + " lines");
         vuelca();

         //ensure the the buffer is emptied!
         strArr = new Vector ();
      }
   }

   public void writeNewLine (final String newLineStr)
   {
      lastWantedNewLineString = newLineStr;
      writeln ();
   }

   public void writeNewLine ()
   {
      writeln ();
   }

   //(o) devnote_algorithms_reading manually RT LF
   
   //(o) TODO_writing 13 and 10 separately REVIEW! (see note in urlUtil)
   //    about habdling (13+10) when writing them separately (not in the same string)
   //
   public void writeString (String str)
   {
      if (currentState == STATE_READING)
      {
         log.severe("writeln", "attempt to write while reading was active, this is not allowed in serialTextBuffer.");
         return;
      }
      currentState = STATE_WRITING;
      
      // add line and separate them if either [13 10] or [13] or [10] are present
      //
      int indxDone = 0;
      int totLen = str.length ();
      do 
      {
         int pos13 = str.indexOf (TextFile.NEWLINE_RT13, indxDone);
         int pos10 = str.indexOf (TextFile.NEWLINE_LF10, indxDone);
         if (pos13 == -1 && pos10 == -1) break; // NO more return(s) in line
         int first = (pos10 != -1 && (pos13 == -1 || pos10 < pos13)) ? pos10: pos13;
         if (first == -1) 
         {
            // IMPOSIBLE!
            log.severe ("writeln", "met impossible condition : pos10 " + pos10 + ", pos13 " + pos13);
            break;
         }
         // return detectd, but first write remaining if not empty
         //
         if (lastWriteLineWithNoReturn.length () > 0)
         {
            addLine2Array (lastWriteLineWithNoReturn.toString ());
            lastWriteLineWithNoReturn = new StringBuffer ();
         }

         // write until return
         //
         addLine2Array (str.substring (indxDone, first));
         indxDone = first + ((pos13 != -1 && (pos13 + 1) == pos10) ? 2:1);

      } while (indxDone < totLen);

      log.dbg (2, "writeln", "append str from indx " + indxDone);
      lastWriteLineWithNoReturn.append (str.substring(indxDone));
   }

   public void rewind ()
   {
      // in this state the object can be read again
      currentState = STATE_WRITING;
      nextLine2read = 0;
      lastReadLine = null;
      lastIndxRead = 0;
      txtFile.fclose (); // if it was open for read then close it now
   }

   public boolean getNextLine ()
   {
      lastReadLine = getNextLine2 ();
      lastIndxRead = 0;
      return lastReadLine != null;
   }

   public String getLastReadLine ()
   {
      return lastReadLine;
   }
   
   public int readBytes (byte[] cbuf)
   {
      int nread = 0;
      if (lastReadLine == null) getNextLine ();
      if (lastReadLine == null) return -1; // this is feof !
      while (lastReadLine != null && nread < cbuf.length)
      {
         byte[] laliby = lastReadLine.getBytes();
         while (nread < cbuf.length && lastIndxRead < laliby.length)
            cbuf[nread++] = laliby[lastIndxRead++];

         // require new line ?
         if (lastIndxRead >= laliby.length)
         {
            getNextLine ();
            // add return line feed if not eof !!
            if (lastReadLine != null)
               lastReadLine = lastWantedNewLineString + lastReadLine;
         }
      }
      return nread;
   }
   
   public String toTruncatedString (int maxBytes)
   {
      // can be very expensive if the text is huge!!

      rewind ();
      StringBuffer sbuff = new StringBuffer ();
      while (maxBytes > 0 && getNextLine ())
      {
         maxBytes -= getLastReadLine ().length ();
         sbuff.append ((sbuff.length () == 0 ? "": lastWantedNewLineString) + getLastReadLine ());
      }

      return sbuff.toString ();
   }

   public String toString ()
   {
      // can be very expensive if the text is huge!!

      rewind ();
      StringBuffer sbuff = new StringBuffer ();
      while (getNextLine ())
      {
         sbuff.append (getLastReadLine ());
      }

      return sbuff.toString ();
   }

   private String getNextLine2 ()
   {
      if (currentState == STATE_IDDLE)
      {
         clear ();
         return null;
      }

      if (currentState == STATE_WRITING)
      {
         currentState = STATE_READING;
         if (tmpFileName != null && !txtFile.fopen (tmpFileName, "r", false))
         {
            log.err("getNextLine", "open to read [" + tmpFileName + "] failed");
            clear ();
            return null;
         }
         currentState = STATE_READING;
      }

      // reading from file ?
      if (tmpFileName != null)
      {
         if (txtFile.readLine ())
         {
            return txtFile.TheLine ();
         }
         else
         {
            // end of file reached
            freeFile ();
            //TODO!!!! return null;
            // WHY freeFile ???? what happens if rewind!!!
            //2014.01.19 investigated (tested) without success :(
            //           if we replace it with rewind it does not work!
         }
      }

      // read from memory
      if (nextLine2read > strArr.size ()-1)
      {
         if (nextLine2read == strArr.size () && lastWriteLineWithNoReturn.length () > 0)
         {
            nextLine2read ++;
            return lastWriteLineWithNoReturn.toString ();
         }
         else
         {
            //rewind ();
            return null;
         }
      }

      // return it from memory
      return (String) strArr.get (nextLine2read ++);
   }
   
   public boolean writeContentIntoOpenedFile (TextFile fil2)
   {
      if (tmpFileName != null)
      {
         TextFile intfil = new TextFile ();
         if (!intfil.fopen (tmpFileName, "r", false))
         {
            log.err("getNextLine", "open to read [" + tmpFileName + "] failed");
            return false;
         }
         while (intfil.readLine ())
            fil2.writeLine (intfil.TheLine ());

      }
      
      for (int ii = 0; ii < strArr.size (); ii ++)
      {
         fil2.writeLine ((String) strArr.get (ii));
      }
      return true;
   }

   private void vuelca ()
   {
      if (!txtFile.fopen (getFileName (), "a", false))
      {
         log.err("vuelca", "open to append [" + getFileName () + "] failed");
         return;
      }

      for (int ii = 0; ii < strArr.size (); ii ++)
      {
         txtFile.writeString ((String) strArr.get (ii));
         txtFile.writeNewLine (lastWantedNewLineString);
      }


// NOTA: si hacemos esto aqui => un read h�brido (parte grabado en fichero, parte en memoria) no lo va ha hacer bien
//       cuando el vulca se ha hecho con un trozo de l�nea sin retorno
//
//        // Note: it is ok to write now the "no return" line and reset it
//        //       because next time it has to be reset here.
//       txtFile.write (lastWriteLineWithNoReturn);
//       lastWriteLineWithNoReturn = "";

      txtFile.fclose ();
   }

   private String getFileName ()
   {
      if (tmpFileName != null) return tmpFileName;

      //!!! from recycle !
      tmpFileName = fileUtil.createTemporal ();
      return tmpFileName;
   }

   private void freeFile ()
   {
      txtFile.fclose ();
      if (tmpFileName != null)
      {
         //!!! recycle (tmpFileName);
         tmpFileName = null;
      }
   }

   public static void main (String [] aa)
   {
      if (aa.length != 1)
      {
         System.out.println ("Syntax : fileName\n");
         return;
      }

      String fileName = aa[0];

      TextFile fi = new TextFile ();
      if (!fi.fopen (fileName, "r", false))
      {
         System.out.println ("file [" + fileName + "] could not be opened!");
         return;
      }

      serialTextBuffer se = new serialTextBuffer();
      int nl = 0;
      while (fi.readLine ())
      {
         nl ++;
         se.writeln (fi.TheLine ());
      }

      // already read, now write it
      //

      fileName = fileName + ".COPY4TEST";
      System.out.println ("leidas " + nl + " lines, writing on [" + fileName + "] ...");

      if (!fi.fopen (fileName, "w", false))
      {
         System.out.println ("file [" + aa[0] + "] could not be opened!");
         return;
      }

      while (se.getNextLine ())
         fi.writeLine (se.getLastReadLine ());

      fi.fclose ();
      System.out.println ("done.");
   }
}
