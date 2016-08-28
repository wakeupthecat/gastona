/*
packages de.elxala
(c) Copyright 2011 Alejandro Xalabarder Aulet

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
   @date 10.04.2008 21:54
   @name logger
   @author Alejandro Xalabarder

   @brief
   Provides logging service


   Notes:
         - a module or any java class has to use an instance of logger (de.elxala.zServices.logger) to produce log

*/
public class logNativePrinter
{
   protected static logWakeup wakeupDiag = null;

   public static void message (String context, String msg2print)
   {
      if (wakeupDiag != null && wakeupDiag.accept (logWakeup.MESSAGES))
         wakeupDiag.message (context, msg2print);
      else
         System.out.println ("\nlogMSG(" + context + "): " + msg2print);
   }

   public static void warning (String context, String msg2print)
   {
      if (wakeupDiag != null && wakeupDiag.accept (logWakeup.WARNINGS))
         wakeupDiag.warning (context, msg2print);
      else
         System.out.println ("\nlogWARN(" + context + "): " + msg2print);
   }

   public static void error (String context, String msg2print)
   {
      if (wakeupDiag != null && wakeupDiag.accept (logWakeup.ERRORS))
         wakeupDiag.error (context, msg2print);
      else
         System.out.println ("\nlogERR(" + context + "): " + msg2print);
   }

   public static void errorBox (String msg2print, String title)
   {
      errorBoxEvo (msg2print, title);
   }

   public static void errorBoxEvo (String msg2print, String title)
   {
      JFrame frame = new JFrame (title);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      java.awt.Container pane = frame.getContentPane ();
      Eva lay = new Eva();

      // set margins
      lay.addLine (new EvaLine ("EvaLayout, 15, 15, 5, 5"));

      // set grid
      lay.addLine (new EvaLine ("   ,   400, X "));
      lay.addLine (new EvaLine (" X , xObs , -"));
      lay.addLine (new EvaLine ("300, +    , -"));
      lay.addLine (new EvaLine ("   , bSalida, -"));

      pane.setLayout (new de.elxala.Eva.layout.EvaLayout(lay));


      // text content
      JTextArea textArea = new JTextArea(5, 20);
      JScrollPane scrollPane = new JScrollPane(textArea);
      textArea.setEditable(false);
      textArea.setText (msg2print);
      textArea.setCaretPosition (0);

      // button
      //
      JButton bSalida = new JButton ("EXIT");
      bSalida.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
            if (wakeupDiag != null && !wakeupDiag.isShowing ())
               System.exit (1);
        }
      });

      // add them to the panel
      //
      pane.add ("xObs",    scrollPane);
      pane.add ("bSalida", bSalida);

      frame.pack();

      // center the frame
      java.awt.Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
      int x = (dim.width-frame.getWidth ())/2;
      int y = (dim.height-frame.getHeight ())/2;
      frame.setLocation(new java.awt.Point (x, y));

      frame.show();
   }

   public static void errorBoxClasic (String msg2print, String title)
   {
      javax.swing.JOptionPane.showMessageDialog (
            null,
            msg2print,
            title,
            javax.swing.JOptionPane.ERROR_MESSAGE);
      if (wakeupDiag != null && !wakeupDiag.isShowing ())
         System.exit (1);
   }
}
