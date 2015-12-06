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

import javax.swing.UIManager;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JTextArea;

import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.mensaka.*;

import javaj.widgets.text.*;
import javaj.widgets.basics.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (o) zConsole

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zConsole
   <groupInfo>  text
   <javaClass>  javaj.widgets.zConsole
   <importance> 6
   <desc>       //An output console for standard output and standard error streams (rapid prefix 'o')

   <help>

      //
      // Output console for standard output and/or standard error streams. These streams are used by
      // a lot of programs specially command line programs. In Gastona, listix use the standard output
      // stream as default file for its generation (see listix command GENERATE). Also intern debug
      // and error messages of Gastona use these streams, therefore it is a good idea to include
      // somewhere a console in your gastona application during the development to track possible
      // errors.
      //

   <prefix> o

   <attributes>
      name             , in_out, possibleValues             , desc

      visible          , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the widget
<!      data             , out   , (Eva list)                 , //Text printed out into the console related with the last "traffic" message
      fileName         , in    , file name                  , //File to be used in message "save"

   <messages>

      msg, in_out, desc

<!      data!       , in   , update data
<!      control!       , in   , update control
      save     , in   , //saves the contents to the file given in the attribute "fileName"
      clear    , in   , clears the contents
      autoScroll, in   , restore autoScroll mode. Autoscroll is automatically disabled when the user click on the console in any line but the last one.
      plugStd  , in   , plug the standard output (System.out)
      plugErr  , in   , plug the standard error  (System.err)
      unplugStd, in   , unplug the standard output (System.out)
      unplugErr, in   , unplug the standard error  (System.err)
<!      traffic  , out  , Informs that a new data (attribute data) has been printed out on this console (see also attribute data)


   <examples>
      gastSample

<!      data4Tester
      hello zConsole
<!      zConsole advanced
      all consoles

   <data4Tester>

   <hello zConsole>
      //#javaj#
      //
      //    <frames> oConsola, Hello oConsola
      //
      //#listix#
      //
      //    <main>
      //      //This is "hello zConsole" gastona script!
      //      //
      //

   <all consoles>
      //#javaj#
      //
      //    <frames> F, Hello oConsola
      //
      //    <layout of F>
      //          EVA, 10, 10, 5, 5
      //          --,    A   , X
      //            , lConsole for standard output (o1Consola)
      //          X , o1Consola, -
      //            , lConsole for standard errors (o2Consola)
      //          X , o2Consola, -
      //            , lConsole for both (oConsola)
      //          X , oConsola, -
      //          X ,   +
      //            , mandos  , -
      //
      //   <layout of mandos>
      //      EVA, 5, 5, 7, 7
      //
      //         ,                ,                , X,
      //         , kstdOut plugged, kstdErr plugged,  , bPrint Hello, bProduce an Error, bCall Dir, bClear Consoles
      //
      //#data#
      //
      //    <kstdOut plugged selected> 1
      //    <kstdErr plugged selected> 1
      //
      //#listix#
      //
      //    <-- bPrint Hello>
      //       //HELLO CONSOLE!
      //       //
      //
      //    <-- bCall Dir>
      //       CHECK, LINUX, CALL, //CMD /C dir
      //       CALL, ls -la
      //
      //    <-- bProduce an Error>
      //       PLEASE WORK,,
      //
      //    <-- bClear Consoles>
      //       MSG, o1Consola clear
      //       MSG, o2Consola clear
      //       MSG, oConsola clear
      //
      //    <-- kstdOut plugged>
      //       IN CASE, @<kstdOut plugged selected>
      //              , 1, MSG, oConsola plugStd
      //              , 0, MSG, oConsola unplugStd
      //
      //    <-- kstdErr plugged>
      //      IN CASE, @<kstdErr plugged selected>
      //             , 1, MSG, oConsola plugErr
      //             , 0, MSG, oConsola unplugErr

#**FIN_EVA#

*/

/**
   zConsole : zWidget representing a console for output

   @see zWidgets
   @see javaHelper.gast
*/
public class zConsole extends JTextArea implements MensakaTarget
{
   private arrayStream  unStream    = new arrayStream ();
   private PrintStream  myStreamOut = new PrintStream (unStream, true);

   private arrayStreamErr  unStreamErr = new arrayStreamErr ();
   private PrintStream     myStreamErr = new PrintStream (unStreamErr, true);

   private static PrintStream  oldOut = System.out;
   private static PrintStream  oldErr = System.err;

   private final Font fonto = new Font ("monospaced", 0, 12);

   private final int AUTOSCROLL = 10;
   private final int CLEAR = 11;
   private final int SAVE = 12;
   private final int PLUG_ERR = 13;
   private final int PLUG_STD = 14;
   private final int UNPLUG_ERR = 15;
   private final int UNPLUG_STD = 16;

   private MessageHandle TX_TRAFFIC = new MessageHandle ();
   private MessageHandle TX_ERROR_DETECTED = new MessageHandle ();

   private int capturedTypeOutput = STD_BOTH;

   public static final int STD_NONE   = 0;
   public static final int STD_OUTPUT = 1;
   public static final int STD_ERROR  = 2;
   public static final int STD_BOTH   = STD_OUTPUT | STD_ERROR;

   public zConsole()
   {
      init (STD_BOTH);
   }

   public zConsole(String name)
   {
      super.setName (name);
      init (STD_BOTH);
   }

   public zConsole(String name, int typeOutput)
   {
      super.setName (name);
      init (typeOutput);
   }

   private basicAparato helper = null;

   public void init(int typeOutput)
   {
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (getName (), null, null));
      javaj.globalJavaj.ensureDefRes_javajUI_text ();

      capturedTypeOutput = typeOutput;
      setEditable (false);

      switch (typeOutput)
      {
         case STD_OUTPUT:
            setBackground ((Color) UIManager.get ("javajUI.text.consoleOutBackColor"));
            setForeground ((Color) UIManager.get ("javajUI.text.consoleOutForeColor"));
            break;

         case STD_ERROR:
            setBackground ((Color) UIManager.get ("javajUI.text.consoleErrBackColor"));
            setForeground ((Color) UIManager.get ("javajUI.text.consoleErrForeColor"));
            break;

         default:
            setBackground ((Color) UIManager.get ("javajUI.text.consoleBothBackColor"));
            setForeground ((Color) UIManager.get ("javajUI.text.consoleBothForeColor"));
            break;
      }

      setFont (fonto);

      String msg = getName();

      Mensaka.suscribe (this, CLEAR,      msg + " clear");
      Mensaka.suscribe (this, SAVE,       msg + " save");
      Mensaka.suscribe (this, AUTOSCROLL, msg + " autoScroll");
      Mensaka.suscribe (this, PLUG_ERR,   msg + " plugErr");
      Mensaka.suscribe (this, PLUG_STD,   msg + " plugStd");
      Mensaka.suscribe (this, UNPLUG_ERR, msg + " unplugErr");
      Mensaka.suscribe (this, UNPLUG_STD, msg + " unplugStd");

      Mensaka.declare (this, TX_TRAFFIC,  msg + " traffic", de.elxala.zServices.logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_ERROR_DETECTED,  "ERROR_DETECTED", de.elxala.zServices.logServer.LOG_DEBUG_0);

      plug ();
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

   public boolean takePacket (int mappedID, EvaUnit euData)
   {
      switch (mappedID)
      {
         case AUTOSCROLL: autoscroll (); break;
         case CLEAR:      clear (); break;
         case SAVE:       save (euData); break;
         case PLUG_ERR:   plugError (); break;
         case PLUG_STD:   plugOutput (); break;
         case UNPLUG_ERR: unplugError (); break;
         case UNPLUG_STD: unplugOutput (); break;

         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setNameDataAndControl (null, euData, null);
            break;
            
         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setNameDataAndControl (null, null, euData);

            setEnabled (helper.ebs ().getEnabled ());
            if (isShowing ())
               setVisible (helper.ebs ().getVisible ());
            break;

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

            // Invoke later has to proposes:
            //   1) avoid or reduce mixing prints of the error and other possible outputs due to the message
            //   2) together used with canSendMessage reduce the number of messages per error (hopefully to just one)
            //
            javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run() {
               Mensaka.sendPacket (TX_ERROR_DETECTED, null); // Notify that an error has occurred
               canSendMessage = true;
            }});
         }
      }
   }

   private EvaUnit euTraffic = new EvaUnit(getName () + "4Traffic");
   private Eva     eTraffic = null;
   private int     sizeOfText = 0;

   public void writeLocal (byte[] b, int off, int len, ByteArrayOutputStream elStream)
   {
      if (Mensaka.hasSuscribers (TX_TRAFFIC))
      {
         if (eTraffic == null)
         {
            eTraffic = euTraffic.getSomeHowEva ("data");
         }
         eTraffic.setValue (elStream.toString ());
         Mensaka.sendPacket (TX_TRAFFIC, euTraffic);
      }

      // determine if automatic scroll is needed
      boolean autoscroll = false;
      int careto = getCaretPosition ();
      if (sizeOfText == 0 || careto == sizeOfText)
      {
         autoscroll = true;
      }

      String str = elStream.toString ();
      append (str);
      // other way ...
      // append (new String (b, off, len));
      sizeOfText += str.length ();

      elStream.reset ();

      if (autoscroll)
      {
         autoscroll ();
      }
   }

   public /*synchronized */void autoscroll ()
   {
      Dimension di = getSize ();
      Point loc = getLocation ();
      setCaretPosition (sizeOfText);
      paintImmediately (new Rectangle (loc.x, loc.y, di.width, di.height));
   }

   public void clear()
   {
      setText("");
      sizeOfText = 0;
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
       System.out.println ("stdout redirect to zConsola " + super.getName ());
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
       System.out.println ("stderr redirect to zConsola " + super.getName ());
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

   private void save (EvaUnit data)
   {
      // has data and control ?
      Eva eva = data.getEva(getName () + " fileName");
      if (eva == null)
      {
         widgetLogger.log ().err ("zConsole", "message \"save\" received but fileName not specified (<" + getName () + " fileName>)");
         return;
      }

      // has fileName ?
      String fileName = eva.getValue ();
      if (fileName.length () == 0)
      {
         widgetLogger.log ().err ("zConsole", "message \"save\" received but fileName wrong specified (<" + getName () + " fileName> is empty)");
         return;
      }

      boolean ok = TextFile.writeFile (fileName, getText ());
      if (ok)
           widgetLogger.log ().dbg (0, "zConsole", "save, contents saved into file " + fileName);
      else widgetLogger.log ().err ("zConsole", "save, error writing into file " + fileName);
   }
}
