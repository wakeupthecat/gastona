/*
packages de.elxala
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

package javaj.widgets;

import android.util.Log;
import java.io.*;
import javaj.*;
import javaj.widgets.text.*;

import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.graph.sysFonts;

import javaj.widgets.basics.*;
import javaj.widgets.text.*;
import javaj.widgets.graphics.uniColor;


/**
   zConsole : zWidget representing a console for output

   @see zWidgets
   @see javaHelper.gast
*/
public class zConsole extends EditText
                      implements MensakaTarget, izWidget
{
   private arrayStream  unStream    = new arrayStream ();
   private PrintStream  myStreamOut = new PrintStream (unStream, true);

   private arrayStreamErr  unStreamErr = new arrayStreamErr ();
   private PrintStream     myStreamErr = new PrintStream (unStreamErr, true);

   private static PrintStream  oldOut = System.out;
   private static PrintStream  oldErr = System.err;

   //!!! private final Font fonto = new Font ("monospaced", 0, 12);

   private final int AUTOSCROLL = 10;
   private final int CLEAR = 11;
   private final int SAVE = 12;
   private final int PLUG_ERR = 13;
   private final int PLUG_STD = 14;
   private final int UNPLUG_ERR = 15;
   private final int UNPLUG_STD = 16;

//24.01.2011 01:51 removed because can cause problems, and it does not seem to be very useful
//
//   private MessageHandle TX_TRAFFIC = new MessageHandle ();
   private MessageHandle TX_CONSOLE_ERROR_DETECTED = new MessageHandle ();

   private int capturedTypeOutput = STD_BOTH;

   public static final int STD_NONE   = 0;
   public static final int STD_OUTPUT = 1;
   public static final int STD_ERROR  = 2;
   public static final int STD_BOTH   = STD_OUTPUT | STD_ERROR;

   public zConsole(Context co, String name)
   {
      super(co);
      setName (name);
      init (STD_BOTH);
   }

   public zConsole(Context co, String name, int typeOutput)
   {
      super(co);
      setName (name);
      init (typeOutput);
   }

   //-i- interface iWidget ---------------------------------------
   //
   public int getDefaultHeight () { return androidSysUtil.getHeightChars(5.0f); }
   public int getDefaultWidth () { return androidSysUtil.getWidthChars (30); }

   private String mName = "";

   public String getName ()
   {
      return mName;
   }

   public void setName (String map_name)
   {
      mName = map_name;
   }

   public void init(int typeOutput)
   {
      javaj.globalJavaj.ensureDefRes_javajUI_text ();

      capturedTypeOutput = typeOutput;
      //!!! setEditable (false);

      String midFix = "Both";
      switch (typeOutput)
      {
         case STD_OUTPUT: midFix = "Out";  break;
         case STD_ERROR : midFix = "Err";  break;
         default:  break;
      }

      uniColor backCol = (uniColor) utilSys.objectSacGet ("javajUI.text.console" + midFix + "BackColor");
      uniColor foreCol = (uniColor) utilSys.objectSacGet ("javajUI.text.console" + midFix + "ForeColor");
      if (backCol != null) setBackgroundColor (backCol.toInt ());
      if (foreCol != null) setTextColor (foreCol.toInt ());

      widgetLogger.log ().dbg (4, "init", "backCol is " + backCol);
      widgetLogger.log ().dbg (4, "init", "foreCol is " + foreCol);

      setTypeface (android.graphics.Typeface.MONOSPACE);
      setTextSize (11f);

      String msg = getName();

      Mensaka.subscribe (this, CLEAR,      msg + " clear");
      Mensaka.subscribe (this, SAVE,       msg + " save");
      Mensaka.subscribe (this, AUTOSCROLL, msg + " autoScroll");
      Mensaka.subscribe (this, PLUG_ERR,   msg + " plugErr");
      Mensaka.subscribe (this, PLUG_STD,   msg + " plugStd");
      Mensaka.subscribe (this, UNPLUG_ERR, msg + " unplugErr");
      Mensaka.subscribe (this, UNPLUG_STD, msg + " unplugStd");

//      Mensaka.declare (this, TX_TRAFFIC,  msg + " traffic", de.elxala.zServices.logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_CONSOLE_ERROR_DETECTED,  "CONSOLE_ERROR_DETECTED", de.elxala.zServices.logServer.LOG_DEBUG_0);

      plug ();
   }

   protected static final int HANDLER_OP_SETTEXT   = 0;
   protected static final int HANDLER_OP_APPEND    = 1;

   protected final Handler UIhandler = new Handler() {
      public void handleMessage(Message msg)
      {
         int op = msg.getData().getInt("operation");
         String value = msg.getData().getString("value");
         
         switch (op)
         {
            case HANDLER_OP_APPEND:     append  (value); break;
            case HANDLER_OP_SETTEXT:    setText (value); break;
            default: break;
         }
         String op2 = msg.getData().getString("opera");
         if (op2 != null && op2.equals("JAL!"))
            append  (value);
      }
   };

   protected void attakWidget (int op, String value)
   {
      utilHandler.sendToWidget (UIhandler, op, value);
   }

   public boolean isEmpty ()
   {
      return sizeOfText == 0;
   }

   public int typeConsole ()
   {
      return capturedTypeOutput;
   }

   public OutputStream getOutputStream ()
   {
      return unStream;
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case AUTOSCROLL: autoscroll (); break;
         case CLEAR:      clear (); break;
         case SAVE:       save (euData, (pars != null && pars.length >= 1) ? pars[0]: null); break;
         case PLUG_ERR:   plugError (); break;
         case PLUG_STD:   plugOutput (); break;
         case UNPLUG_ERR: unplugError (); break;
         case UNPLUG_STD: unplugOutput (); break;

         default: break;
      }
      return true;
   }

   private class arrayStream extends ByteArrayOutputStream
   {
      public void write (byte[] b, int off, int len)
      {
          super.write (b, off, len); // paque ?
          writeLocal  (b, off, len, unStream);
      }
   }

   // to avoid recursive calls if the target of the message
   // produces an error ...
   static boolean canSendMessage = true;

   private class arrayStreamErr extends ByteArrayOutputStream
   {
      public void write (byte[] b, int off, int len)
      {
         super.write (b, off, len); // paque ?
         writeLocal  (b, off, len, unStreamErr);
         if (canSendMessage)
         {
            // we don't know what the target of this message will do
            // so to avoid problems...
            canSendMessage = false;
            Mensaka.sendPacket (TX_CONSOLE_ERROR_DETECTED, null); // Notify that an error has occurred
         }
      }
   }

   private int sizeOfText = 0;

   public void writeLocal (byte[] b, int off, int len, ByteArrayOutputStream elStream)
   {
      // determine if automatic scroll is needed
      boolean autoscroll = false;
//!!!       int careto = getCaretPosition ();
//!!!       if (sizeOfText == 0 || careto == sizeOfText)
//!!!       {
//!!!          autoscroll = true;
//!!!       }


      String str = elStream.toString ();

      //!//append (str);
      attakWidget (HANDLER_OP_APPEND, str);

      sizeOfText += str.length ();
      elStream.reset ();

      if (autoscroll)
         autoscroll ();
   }

   public /*synchronized */void autoscroll ()
   {
//!!!       Dimension di = getSize ();
//!!!       Point loc = getLocation ();
//!!!       setCaretPosition (sizeOfText);
//!!!       paintImmediately (new Rectangle (loc.x, loc.y, di.width, di.height));
   }

   public void clear()
   {
      //!// setText("");
      attakWidget (HANDLER_OP_SETTEXT, "");
      sizeOfText = 0;

      //checked, following code DOES NOT produce an uncontrolled recursion
      //          <-- CONSOLE_ERROR_DETECTED>
      //                -->, myConsole clear
      //                PRODUCE, ANOTHER ERROR
      canSendMessage = true;
   }

   /**
      captures the stdout and stderr to this console
   */
   public void plug ()
   {
       if ((typeConsole () & STD_ERROR) != 0) plugError ();
       if ((typeConsole () & STD_OUTPUT) != 0) plugOutput ();
   }

   public void plugOutput ()
   {
       System.out.println ("stdout redirect to zConsola " + getName ());
       oldOut = System.out;
       System.setOut (myStreamOut);
   }


   public boolean isOutPlugged ()
   {
      return (myStreamOut == System.out);
   }

   public boolean isErrPlugged ()
   {
      return (myStreamErr == System.err);
   }


   public void plugError ()
   {
       System.out.println ("stderr redirect to zConsola " + getName ());
       oldErr = System.err;
       System.setErr (myStreamErr);
   }

   /**
      restores stdout and stderr to default console
   */
   public void unplug ()
   {
      unplugError ();
      unplugOutput ();
   }

   public void unplugOutput ()
   {
      // NOTE : a single PrintStream can be both !!!
      //if (printStream == System.out) System.out.println ("es out");
      //if (printStream == System.err) System.out.println ("es err");

      if (isOutPlugged ())
      {
         System.setOut (oldOut);
         System.out.println ("stdout back to last console");
      }
   }

   public void unplugError ()
   {
      if (isErrPlugged ())
      {
         System.setErr (oldErr);

         // actually it should be printed out in System.err, but may cause problems to do this
         // (treated as error while it is not)
         System.out.println ("stderr back to last error console");
      }
   }

   private void save (EvaUnit data, String p1)
   {
      String fileName = p1;
      if (fileName == null || fileName.length() == 0)
      {
         // has data and control ?
         Eva eva = data.getEva(getName () + " fileName");
         if (eva == null)
         {
            widgetLogger.log ().err ("zConsole", "message \"save\" received but fileName not specified (<" + getName () + " fileName>)");
            return;
         }
         fileName = eva.getValue ();
         // has fileName ?
         if (fileName.length () == 0)
         {
            widgetLogger.log ().err ("zConsole", "message \"save\" received but fileName wrong specified (<" + getName () + " fileName> is empty)");
            return;
         }
      }

      boolean ok = TextFile.writeFile (fileName, getText ().toString ());
      if (ok)
           widgetLogger.log ().dbg (2, "zConsole", "save, contents saved into file " + fileName);
      else widgetLogger.log ().err ("zConsole", "save, error writing into file " + fileName);
   }
}
