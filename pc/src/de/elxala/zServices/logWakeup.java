/*
packages de.elxala
(c) Copyright 2012 Alejandro Xalabarder Aulet

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

package de.elxala.zServices;

/**
   TODO:

   [] deprecar, comprobar que no se usan
         public static final int LOG_START = 20;
         public static final int LOG_STEP  = 21;
         public static final int LOG_END   = 30;

         // start index of custom logs
         public static final int LOG_CUSTOM_FIRST = 100;

   [] Implementar custom logs en javaj y mensaka, por ejemplo...

      CREATE TABLE logCustom_javaj_flow     (msgCounter, milliStamp, level, context, message);
      CREATE TABLE logCustom_mensaka_agents (msgCounter, milliStamp, level, agIndex, agName, agObjString);
      CREATE TABLE logCustom_mensaka_msgs   (msgCounter, milliStamp, level, agIndex, type(subscribe/declare/send/received/sendEnd), msgindx);

*/

import javax.swing.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Image;
import javax.swing.text.*;

import java.io.File;
import java.util.List;
import java.util.Vector;
import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.mensaka.*;


/**
   @date 04.03.2012 12:41
   @name logWakeup
   @author Alejandro Xalabarder

   @brief
   Popup dialog that, if activated, awakes when a log message is received

   Notes:
         - a module or any java class has to use an instance of logger (de.elxala.zServices.logger) to produce log

*/
public class logWakeup
{
   public static final int ERRORS = 0;
   public static final int WARNINGS = 1;
   public static final int MESSAGES = 2;

   private static int msgAccepted = ERRORS;

   private static JFrame myframe = null;
   private static JTextArea textArea = null;

   private static String nameTitle = "Gastona Log Wakeup!";

   public logWakeup ()
   {
   }

   public logWakeup (int msgType)
   {
      activate (msgType);
   }

   public void activate (int msgType)
   {
      msgAccepted = msgType;
      buildFrame (nameTitle);
      logNativePrinter.wakeupDiag = this;
   }

   public void deactivate ()
   {
      msgAccepted = -1;
      //myframe = null;
      logNativePrinter.wakeupDiag = null;
   }

   public static boolean accept (int msgType)
   {
      return (myframe != null) && msgType <= msgAccepted;
   }

   public static boolean isShowing ()
   {
      return (myframe != null) && msgAccepted != -1 && myframe.isShowing();
   }

   public static void message (String context, String msg2print)
   {
      if (myframe == null) return;
      myframe.show ();
      
      textArea.append (":: " + context + "\n");
      //textArea.setForeground (new java.awt.Color (0, 254, 0));
      //textArea.setBackground (java.awt.Color.GRAY);
      textArea.append (msg2print + "\n");
   }

   public static void warning (String context, String msg2print)
   {
      if (myframe == null) return;
      myframe.show ();
      textArea.append (":: " + context + "\n");
      textArea.append (msg2print + "\n");
   }

   public static void error (String context, String msg2print)
   {
      if (myframe == null) return;
      myframe.show ();
      //textArea.setForeground (new java.awt.Color (254, 34, 12));
      textArea.append (":: " + context + "\n");
      textArea.append (msg2print + "\n");
   }

   public static void buildFrame (String title)
   {
      javaj.globalJavaj.ensureDefRes_javajUI_text ();
      myframe = new JFrame (title);
      //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//(o) TODO add Icon Image for logWakeup
//      Image ima = javajST.getImageApp ();
//      if (ima != null)
//         myframe.setIconImage (ima);


      java.awt.Container pane = myframe.getContentPane ();
      Eva lay = new Eva();

      // set margins
      lay.addLine (new EvaLine ("EvaLayout, 15, 15, 5, 5"));

      // set grid
      lay.addLine (new EvaLine ("   ,   600, X "));
      lay.addLine (new EvaLine (" X , xObs , -"));
      lay.addLine (new EvaLine ("300, +    , -"));
      lay.addLine (new EvaLine ("   , bClear, bSalida, -"));

      pane.setLayout (new de.elxala.Eva.layout.EvaLayout(lay));


      // text content
      textArea = new JTextArea(15, 60);
      JScrollPane scrollPane = new JScrollPane(textArea);
      //textArea.setEditable(false);
      textArea.setFont (new java.awt.Font ("Consolas", 0, 12));
//      textArea.setBackground (java.awt.Color.BLACK);
      textArea.setBackground ((Color) UIManager.get ("javajUI.text.consoleErrBackColor"));
      textArea.setForeground ((Color) UIManager.get ("javajUI.text.consoleErrForeColor"));

//      textArea.setText (msg2print);
//      textArea.setCaretPosition (0);

      // button
      //
      JButton bSalida = new JButton ("Close");
      bSalida.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            myframe.hide ();
         }
      });

      JButton bClear = new JButton ("Clear");
      bClear.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            textArea.setText ("");
         }
      });

      // add them to the panel
      //
      pane.add ("xObs",    scrollPane);
      pane.add ("bSalida", bSalida);
      pane.add ("bClear",  bClear);

      myframe.pack();

      // center the frame
      java.awt.Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
      int x = (dim.width - myframe.getWidth ())/2;
      int y = (dim.height - myframe.getHeight ())/2;
      myframe.setLocation(new java.awt.Point (x, y));

      //frame.show();
   }
}
