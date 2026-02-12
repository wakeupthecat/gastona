/*
package de.elxala.zWidgets
Copyright (C) 2005-2017 Alejandro Xalabarder Aulet

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

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

import de.elxala.langutil.*;
import de.elxala.Eva.*;
import de.elxala.mensaka.*;

import javaj.widgets.kits.dndFileTransHandler;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;

/*
   //(o) WelcomeGastona_source_javaj_widgets (m) zImage

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zImage
   <groupInfo>  misc
   <javaClass>  javaj.widgets.zImage
   <importance> 4
   <desc>       //An image (rapid prefix 'm')

   <help>

      //
      // To show an image of format GIF, JPEG, or PNG from a file  (see javax.swing.ImageIcon)
      // The file path might be a file, a resource found in the current class path or an Url.
      //

   <prefix> m

   <attributes>
      name           , in_out, possibleValues             , desc
                     , in    , file name                  , //File name of the image
      visible        , in    , 0 / 1                      , //Value 0 to make the widget not visible
      enabled        , in    , 0 / 1                      , //Value 0 to disable the widget

      droppedFiles   , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped files. Note that this attribute has to exist in order to enable drag & dropping files into this Component.
      droppedDirs    , out   , (Eva table)                , //Eva table (pathFile,fileName,extension,fullPath,date,size) containing all dropped directories. Note that this attribute has to exist in order to enable drag & dropping files into this Component.

   <messages>

      msg, in_out, desc

      data!       , in  , update data
      control!    , in  , update control
                  , out , Image has been clicked
      droppedFiles, out , If files drag & drop enabled this message indicates that the user has dropped files (see attribute 'droppedFiles')
      droppedDirs , out , If directories drag & drop is enabled this message indicates that the user has dropped directories (see attribute 'droppedDirs')

   <examples>
      gastSample

      hello Image
      image viewer

   <hello Image>
      //#javaj#
      //
      //    <frames> F, Hello zImage
      //
      //    <layout of F>
      //          PANEL, X
      //          mImage
      //
      //#data#
      //
      //    <mImage> 'javaj/img/miDesto.png
      //

   <image viewer>
      //#javaj#
      //
      //    <frames> F, Sample Image viewer
      //
      //    <layout of F>
      //          EVA, 10, 10, 6, 6
      //
      //          ---, 300   , X
      //             , bDir  , mImage
      //           X , aTree , +
      //
      //#data#
      //
      //    <bDir> 'Image Directory
      //    <bDir DIALOG> DIR
      //
      //#listix#
      //
      //    <main0>
      //       SET VAR, aTree separator, @<:sys file.separator>
      //
      //    <-- bDir>
      //       SCAN, ADDFILES,, @<bDir chosen>, +, png, +, gif, +, jpeg, +, jpg
      //       -->, aTree data!, sqlSelect, //SELECT fullPath FROM scan_all;
      //
      //    <-- aTree>
      //       -->, mImage data!,, @<aTree selectedPath>
      //


#**FIN_EVA#

*/

/**
*/
public class zImage extends JPanel implements MouseListener, MensakaTarget
{
   protected basicAparato helper = null;
   protected dndFileTransHandler dndHandler = null;

   protected Color backColor = Color.GRAY; //new JButton().getBackground (); // new JButton().getBackgroundColor ();

   // own data
   protected Icon theImage = null;
   protected float alpha = 0.f;

   public zImage ()
   {
      // default constructor to allow instantiation using <javaClass of...>
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   // ------
   public zImage (String map_name)
   {
      init (map_name);

      addMouseListener (this);
      addKeyListener (javaj.widgets.gestures.keyboard.getListener ());
   }

   public void setName (String map_name)
   {
      init (map_name);
   }

   public void init (String map_name)
   {
      super.setName (map_name);

      helper = new basicAparato ((MensakaTarget) this, new widgetEBS (map_name, null, null));

      // ??
      //setLayout (new BorderLayout());
      setBackground(new JButton().getBackground ());
   }

   private void readAlpha ()
   {
      alpha = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("alpha", "1"));
      if (alpha == 1.f)
         alpha = (float) stdlib.atof (helper.ebs ().getSimpleDataAttribute ("opacity", "1"));

      String backC = helper.ebs ().getSimpleDataAttribute ("backcolor");
      if (backC != null)
      {
         uniColor uco = new uniColor ();
         uco.parseColor (backC);
         backColor = uco.getNativeColor ();
      }
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);

            theImage = javaLoad.getSomeHowImageIcon (helper.ebs ().getText ());
            readAlpha ();
            //paintImmediately (new Rectangle (0, 0, getSize().width, getSize().height));
            paintImmediately (getVisibleRect());
            // repaint ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);

            setEnabled (helper.ebs ().getEnabled ());
            readAlpha ();

            //(o) TODO_REVIEW visibility issue
            // avoid setVisible (false) when the component is not visible (for the first time ?)
            boolean visible = helper.ebs ().getVisible ();
            if (visible && isShowing ())
               setVisible  (visible);

            if (dndHandler == null)
            {
               if (helper.ebs ().isDroppable ())
               {
                  // made it "dropable capable"

                  // drag & drop ability
                  //
                  /**
                      Make the zWidget to drag'n'drop of files or directories capable
                      Note that the zWidget is not subscribed to the drag'n'drop message itself ("%name% droppedFiles" or ..droppedDirs")
                      therefore it will not take any action on this event. It is a task of a controller to
                      examine, accept, process and insert into the widget the files if desired and convenient

                      Note that at this point the control for the zWidget (helper.ebs().getControl ())
                      is null and we have to update it into the handler when it chanhges
                  */
                  dndHandler = new dndFileTransHandler (
                                 helper.ebs().getControl (),
                                 helper.ebs().evaName (""),
                                 dndFileTransHandler.arrALL_FIELDS
                                 );
                  setTransferHandler (dndHandler);
               }
            }
            else
            {
               // every time the control changes set it to the drag&drop handler
               dndHandler.setCommunicationLine (helper.ebs().getControl ());
            }
            break;

         default:
            return false;
      }

      return true;
   }

   public Dimension getPreferredSize ()
   {
      return (theImage != null) ?
               new Dimension (theImage.getIconWidth(), theImage.getIconHeight()) :
               new Dimension (10, 10);
   }

   public void mousePressed (MouseEvent e)
   {
      // helper.signalAction ();
   }

   public void mouseReleased (MouseEvent e)
   {
      // helper.signalAction ();
   }

   public void mouseClicked (MouseEvent e)
   {
      helper.signalAction ();
   }

   public void mouseEntered (MouseEvent e)
   {
      // helper.signalAction ();
   }

   public void mouseExited (MouseEvent e)
   {
      // helper.signalAction ();
   }

   public void paint(Graphics g)
   {
      Dimension d = getSize();
      if (d.width <= 0 || d.height <= 0) return; // >>>> return

      if (backColor != null)
      {
         g.setColor (backColor);
         g.fillRect (0, 0, d.width, d.height);
      }
      else
         g.clearRect (0, 0, d.width, d.height);

      if (theImage == null) return; // No image yet!


      // ??? Image image = icon.getImage().getScaledInstance(width, height,  Image.SCALE_SMOOTH);

      int left  = (d.width - theImage.getIconWidth()) / 2;
      int right = (d.height - theImage.getIconHeight()) / 2;

      if (alpha < 1.f)
      {
         Graphics2D g2d = (Graphics2D) g;
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
         theImage.paintIcon(this, g2d, left, right);
      }
      else
         theImage.paintIcon(this, g, left, right);
   }
}
