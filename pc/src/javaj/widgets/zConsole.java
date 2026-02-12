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
import de.elxala.langutil.*;
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
      save     , in   , //saves the contents to the file given either as parameter or given in the attribute "fileName"
      clear    , in   , clears the contents
      autoScroll, in   , restore autoScroll mode. Autoscroll is automatically disabled when the user click on the console in any line but the last one.
      plugStd  , in   , plug the standard output (System.out)
      plugErr  , in   , plug the standard error  (System.err)
      unplugStd, in   , unplug the standard output (System.out)
      unplugErr, in   , unplug the standard error  (System.err)
<!      traffic  , out  , Informs that a new data (attribute data) has been printed out on this console (see also attribute data)


   <examples>
      gastSample

      hello zConsole
      all consoles
      change zConsole Colors


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

   <change zConsole Colors>
      //#javaj#
      //
      //   <frames> main
      //
      //   <layout of main>
      //      EVA, 10, 10, 5, 5
      //
      //         ,            , X
      //         , lBackground, eBackground
      //         , lForeground, eForeground
      //         , bGo        , bInvert
      //       X , oConsole, -
      //
      //
      //#data#
      //
      //   <eBackground> +025022087
      //   <eForeground> +155101222
      //
      //#listix#
      //
      //   <setMisColores>
      //      //
      //      //background @<eBackground>
      //      //foreground @<eForeground>
      //      //
      //      -->, oConsole control!, backColor, @<eBackground>, foreColor, @<eForeground>
      //
      //   <main>	           LSX, setMisColores
      //   <-- bGo>	        LSX, setMisColores
      //   <-- eBackground>  LSX, setMisColores
      //   <-- eForeground>  LSX, setMisColores
      //
      //   <-- bInvert>
      //      VAR=, temp, @<eBackground>
      //      -->, eBackground data!,, @<eForeground>
      //      -->, eForeground data!,, @<temp>
      //      LSX, setMisColores




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

   private final Font fonto = new Font ("Consolas", 0, 12);

   private final int AUTOSCROLL = 10;
   private final int CLEAR = 11;
   private final int SAVE = 12;
   private final int PLUG_ERR = 13;
   private final int PLUG_STD = 14;
   private final int UNPLUG_ERR = 15;
   private final int UNPLUG_STD = 16;
   private final int GOTO_LINE = 17;

   private MessageHandle TX_TRAFFIC = new MessageHandle ();
   private MessageHandle TX_CONSOLE_ERROR_DETECTED = new MessageHandle ();

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

   private void setColors ()
   {
      setColors (null, null);
   }

   private void setColors (Color bkCol, Color fgCol)
   {
      if (helper.ebs ().hasAll ())
      {
         // try to read attributes with colors
         //
         String strCol = helper.ebs ().getBackColorAttribute ();
         if (strCol != null && strCol.length () > 0)
         {
            int [] col = miscUtil.parsedColorRGB (strCol);
            if (col != null && col.length == 4)
               bkCol = (col[3] == -1) ? new Color (col[0], col[1], col[2]):
                                        new Color (col[0], col[1], col[2], col[3]);
         }

         strCol = helper.ebs ().getForeColorAttribute ();
         if (strCol != null && strCol.length () > 0)
         {
            int [] col = miscUtil.parsedColorRGB (strCol);
            if (col != null && col.length == 4)
               fgCol = (col[3] == -1) ? new Color (col[0], col[1], col[2]):
                                        new Color (col[0], col[1], col[2], col[3]);
         }
      }

      if (bkCol != null) setBackground (bkCol);
      if (fgCol != null) setForeground (fgCol);
   }

   public void init(int typeOutput)
   {
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());

      setDoubleBuffered(false);
      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (getName (), null, null));
      javaj.globalJavaj.ensureDefRes_javajUI_text ();

      capturedTypeOutput = typeOutput;
      setEditable (false);

      switch (typeOutput)
      {
         case STD_OUTPUT:
            setColors ((Color) UIManager.get ("javajUI.text.consoleOutBackColor"),
                       (Color) UIManager.get ("javajUI.text.consoleOutForeColor"));
            break;

         case STD_ERROR:
            setColors ((Color) UIManager.get ("javajUI.text.consoleErrBackColor"),
                       (Color) UIManager.get ("javajUI.text.consoleErrForeColor"));
            break;

         default:
            setColors ((Color) UIManager.get ("javajUI.text.consoleBothBackColor"),
                       (Color) UIManager.get ("javajUI.text.consoleBothForeColor"));
            break;
      }

      setFont (fonto);

      String msg = getName();

      Mensaka.subscribe (this, CLEAR,      msg + " clear");
      Mensaka.subscribe (this, SAVE,       msg + " save");
      Mensaka.subscribe (this, AUTOSCROLL, msg + " autoScroll");
      Mensaka.subscribe (this, PLUG_ERR,   msg + " plugErr");
      Mensaka.subscribe (this, PLUG_STD,   msg + " plugStd");
      Mensaka.subscribe (this, UNPLUG_ERR, msg + " unplugErr");
      Mensaka.subscribe (this, UNPLUG_STD, msg + " unplugStd");
      Mensaka.subscribe (this, GOTO_LINE , msg + " gotoLine");

      Mensaka.declare (this, TX_TRAFFIC,  msg + " traffic", de.elxala.zServices.logServer.LOG_DEBUG_0);
      Mensaka.declare (this, TX_CONSOLE_ERROR_DETECTED,  "CONSOLE_ERROR_DETECTED", de.elxala.zServices.logServer.LOG_DEBUG_0);

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
         case GOTO_LINE:
            goToLine (pars);
            break;

         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);
            setEnabled (helper.ebs ().getEnabled ());

            setColors ();

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebs ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);
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
               Mensaka.sendPacket (TX_CONSOLE_ERROR_DETECTED, null); // Notify that an error has occurred
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
      if (Mensaka.hasSubscribers (TX_TRAFFIC))
      {
         if (eTraffic == null)
         {
            eTraffic = euTraffic.getSomeHowEva ("data");
         }
         eTraffic.setValueVar (elStream.toString ());
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

      if (str != null)
      {
         append (str);
         // other way ...
         // append (new String (b, off, len));
         sizeOfText += str.length ();
      }

      elStream.reset ();

      if (autoscroll)
      {
         autoscroll ();
      }
   }

   private Rectangle rec2update = new Rectangle ();

   public /* synchronized */ void autoscroll ()
   {
      Dimension di = getSize ();
      Point loc = getLocation ();
      setCaretPosition (sizeOfText);

      // rec2update = new Rectangle (loc.x, loc.y, di.width, di.height);

      if (this.isShowing ())
      {
         // 2014.08.10 21:28
         // invokeLater seems to fix the problem
         //
         //  java.lang.NullPointerException
         //  at javax.swing.BufferStrategyPaintManager.flushAccumulatedRegion(Unknown Source)
         //  at javax.swing.BufferStrategyPaintManager.endPaint(Unknown Source)
         //  at javax.swing.RepaintManager.endPaint(Unknown Source)
         //  at javax.swing.JComponent._paintImmediately(Unknown Source)
         //  at javax.swing.JComponent.paintImmediately(Unknown Source)
         //  at javax.swing.JComponent.paintImmediately(Unknown Source)
         //  at javaj.widgets.zConsole.autoscroll(zConsole.java:410)
         //  at javaj.widgets.zConsole.writeLocal(zConsole.java:398)
         //  at javaj.widgets.zConsole$arrayStream.write(zConsole.java:328)
         //  at java.io.PrintStream.write(Unknown Source)
         // ...
         // it occured quite often with the sample udpClientServer
         //    seems to be important: the console was created small (default size)
         //    at some point a thread (from udp socket ?) uses sinchronized listix and
         //    prints out the string in the console that tries to paint immediately
         //    but it is not in the GUI thread!
         //
         // if (Looper.myLooper() == Looper.getMainLooper())
         // {
            // // I am the UI Thread, so it is safe
            // paintImmediately (rec2update);
         // }
         // else
         {
            javax.swing.SwingUtilities.invokeLater(new Runnable() { public void run()
               {
                  //setDoubleBuffered(false);
                  paintImmediately (getVisibleRect());
                  //setDoubleBuffered(true);
               }});
         }
      }
   }

   public void clear()
   {
      setText("");
      sizeOfText = 0;

      //checked, following code does not prodce an uncontrolled recursion
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

      boolean ok = TextFile.writeFile (fileName, getText ());
      if (ok)
           widgetLogger.log ().dbg (2, "zConsole", "save, contents saved into file " + fileName);
      else widgetLogger.log ().err ("zConsole", "save, error writing into file " + fileName);
   }

   private void goToLine (String [] lineArr)
   {
      int lineNr = -1;
      if (lineArr != null)
         lineNr = lineArr.length > 0 ? stdlib.atoi (lineArr[0]): -1;
      //else
      //   lineNr = helper.ebsText ().hasLineToGo () ? helper.ebsText ().getLineToGo (): -1;

      requestFocus();
      requestFocusInWindow();

      if (lineNr == -1)
      {
         // want to go to end of file
         widgetLogger.log ().dbg (2, "zConsole::goToLine", "lineNr == -1 (end of the file)");
         setCaretPosition (getText().length ());
      }
      else
      {
         widgetLogger.log ().dbg (2, "zConsole::goToLine", "go to the lineNr == " + lineNr);

         // GO TO LINE lineNr
         //
         int llevoLines = 1;
         int posCaret = 0;
         int maxCaret = 0;
         while (llevoLines < lineNr)
         {
            //(o) TOSEE_javaj_widgets TEXT CARET Why it does not work with char 13 ?????? (I would say, it did time ago)
            maxCaret = posCaret;
            posCaret = 1 + getText().indexOf(10, posCaret);
            if (posCaret == 0) break;
            llevoLines ++;
         }

         //helper.log.warn ("zConsole.goToLine", "pos careto es " + posCaret + " llevo lines se resume en " + llevoLines);

         if (posCaret >= 0)
         {
            if (maxCaret > posCaret)
               setCaretPosition (getText().length ());
            else
               setCaretPosition (posCaret);
         }
      }
   }
}
