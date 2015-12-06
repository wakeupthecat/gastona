/*
package de.elxala.zWidgets
Copyright (C) 2005, 2009 Alejandro Xalabarder Aulet

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

package javaj.widgets.panels;

import javaj.widgets.basics.*;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import de.elxala.langutil.*;
import de.elxala.mensaka.*;
import de.elxala.Eva.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import de.elxala.zServices.logger;

import javaj.*;

/*
   //(o) javaj_Catalog_source

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_widget
   <name>       zFrame
   <groupInfo>  containers
   <javaClass>  javaj.widgets.panels.zFrame
   <importance> 10
   <desc>       //A java frame, that is an application window or dialog.

   <help>
      //
      // Represents a window for the application or for a dialog. It is a special widget since it
      // is at the same time a widget and a layout. It only can be instanciated by javaj though the
      // javaj variable <frames>. Using the attributes of zFrame you can change the title, visibility,
      // position and size of the window.
      //
      // It is not possible to have just a frame without any contents, not with Javaj. While this is
      // not a serious problem (I hope) it can be very useful. An application with just a frame is
      // interpreted by Javaj as it where a frame with just one widget and the frame's name is at
      // the same time the widget name. In the practice this name ambiguity actually cannot cause
      // any missbehaviour and it is very useful for building very easy frames. For example
      //
      //       <frames> oConsole, "Frame with just a console"
      //
      // It is at the same time a frame called "oConsole" that contains only one widget called also
      // "oConsole" which is a console (because it starts with "o").
      //
      // This trick is used widely in the samples of this documentation.
      //
      // Note: while testing be careful and do not set the main frame to invisible, if you cannot set
      // it again to visible (you don't have any GUI to do that) then the java application could only
      // be closed through a Task manager or a system kill command (if any).
      //

   <importance> 3

   <attributes>
      name             , in_out, possibleValues             , desc

      title            , in    , text                       , Title of the frame
<!//(o) TODO_widgets zFrame maximized to be implemented in zFrame
<!      maximized        , in/out, 0 / 1                      , Value 0 not maximized, 1 maximized

      visible          , in    , 0 / 1                      , //Value 0 to make the frame not visible
      enabled          , in    , 0 / 1                      , //Value 0 to disable the Frame
      posX             , inout , number                     , //Position x of the frame (top-left corner)
      posY             , inout , number                     , //Position y of the frame (top-left corner)
      sizeX            , inout , number                     , //Width of the frame
      sizeY            , inout , number                     , //Height of the frame

   <messages>

      msg, in_out, desc

      data!     ,  in    , update data
      control!  ,  in    , update control
      show      ,  in    , Shows the frame
      hide      ,  in    , Hides the frame
      toggleVisible,  in , Toggles visibility (show or hide)


   <examples>
      gastSample

      just a Frame example
      a simple Frame example
      more Frames example
      my close frame example

   <just a Frame example>
      //#javaj#
      //
      //    <frames>
      //       Fmain, A frame with no contents?
      //
      //    <layout of Fmain>
      //       PANEL, X
      //       lLabel
      //
      //#data#
      //    <lLabel> " "
      //


   <a simple Frame example>
      //#javaj#
      //
      //    <frames>
      //       Fmain, This is a test Frame
      //
      //    <layout of Fmain>
      //       PANEL, X
      //       lLabel, bResetPos
      //
      //#data#
      //
      //    <bResetPos> "Reset position and size"
      //
      //#listix#
      //
      //    <-- bResetPos>
      //       SET NUM, Fmain posX, 10
      //       SET NUM, Fmain posY, 10
      //       SET NUM, Fmain sizeX, 200
      //       SET NUM, Fmain sizeY, 100
      //       MSG, Fmain control!
      //


   <more Frames example>
      //#javaj#
      //
      //   <frames>
      //      Fmain , More frames example
      //      frame2, A second frame
      //
      //   <layout of Fmain>
      //      EVA, 10, 10, 4, 4
      //
      //         , A          , X
      //         , bShowFrame2,
      //         , bHideFrame2,
      //         , bGetPosFrame2, lPosFrame2
      //
      //   <layout of frame2>
      //      EVA, 10, 10, 4, 4
      //
      //         , A
      //         , bCloseMe,
      //
      //#listix#
      //
      //   <-- bShowFrame2>   MESSAGE, frame2 show
      //   <-- bHideFrame2>   MESSAGE, frame2 hide
      //   <-- bCloseMe>      MESSAGE, frame2 hide
      //
      //   <-- bGetPosFrame2>
      //       MSG, frame2 tellPosAndSize
      //       -->, lPosFrame2 data!,, //top-left (@<frame2 posX>, @<frame2 posY>) size (@<frame2 sizeX>, @<frame2 sizeY>)
      //
      //

   <my close frame example>
      //#javaj#
      //
      //   <frames>
      //       Fmain , My close frame example
      //       frame2, A tu vera!
      //
      //   <layout of Fmain>
      //         EVA, 10, 10, 4, 4
      //
      //         , A          , X
      //         , bShowFrame2,
      //         , bHideFrame2,
      //
      //   <layout of frame2>
      //         EVA, 10, 10, 4, 4
      //
      //         , A
      //         , bCloseMe,
      //
      //#listix#
      //
      //   <-- bShowFrame2>
      //       MSG, Fmain tellPosAndSize
      //       SET NUM, frame2 posX, @<Fmain posX> + @<Fmain sizeX>
      //       SET NUM, frame2 posY, @<Fmain posY>
      //       SET NUM, frame2 sizeY, @<Fmain sizeY>
      //       MESSAGE, frame2 control!
      //       MESSAGE, frame2 show
      //
      //   <-- bHideFrame2>   MESSAGE, frame2 hide
      //   <-- bCloseMe>      MESSAGE, frame2 hide

#**FIN_EVA#

*/


/**
   zFrame : zWidget representing a java frame (javax.swing.JFrame)

   @see zWidgets
   @see documentation for Gastona at the end of this file

*/
public class zFrame extends JFrame implements MensakaTarget, ComponentListener
{
   private frameAparato helper = null;
   private logger log_widgetPos = new logger (this, "javaj_widgetPos", new String [] { "paintCount", "name", "posX", "posY", "dx", "dy" });


   // to store visible attribute while not having control
   private boolean isVisibleStored = true;

   // to know if real visibility is allowed (wait for once message javajEBS.msgSHOW_FRAMES)
   private boolean migthShow = false;
   private int nPaints = 0;

   public zFrame (String map_name)
   {
      // super (slabel);
      setName (map_name);

      addComponentListener (this);

      helper = new frameAparato ((MensakaTarget) this, new frameEBS (map_name, null, null));
   }

   public void setVisible (boolean janein)
   {
      if (helper.ebsFrame ().hasControl ())
      {
         //System.out.println (getName() + " setVisible " + janein + " in control");
         helper.ebsFrame ().setVisible (janein);
      }

      if (migthShow)
      {
         super.setVisible (janein);
      }
      else
      {
         //System.out.println (getName() + " setVisible " + janein + " stored");
         isVisibleStored = janein;
      }
   }

   //TEST MORE CONTROLLING
//   public void show ()
//   {
//      System.out.println ("win HEY SOMEONE ZOWS ME!! ME IS " + getName());
//      super.show ();
//   }
//
//   public void hide ()
//   {
//      System.out.println ("win HEY SOMEONE JAIS ME, WAS ZOLLS!! ME IS " + getName());
//      super.hide ();
//   }
//
//   public void setVisible(boolean b)
//   {
//      System.out.println ("ARKANKASTOS! " + b + " on " + getName());
//      super.setVisible (b);
//   }


   public boolean admitRecomendedSize ()
   {
      if (helper.ebsFrame ().hasControl ())
      {
         // if not getSize is set it admits recomendedSize
         int [] num = new int[1];
         return ! helper.ebsFrame ().getSizeX(num);
      }
      return true; // ?
   }

   public void componentHidden  (ComponentEvent e)  { }
   public void componentShown   (ComponentEvent e)  { }

   public void componentMoved   (ComponentEvent e)
   {
      if (helper.ebsFrame ().hasControl ())
         helper.signalPosition (getX (), getY ());

      if (globalJavaj.getPaintingLayout ()) repaint ();
   }

   public void componentResized (ComponentEvent e)
   {
      //it seems that secondary frames receive this before control / data etc ...
      if (helper.ebsFrame ().hasControl ())
         helper.signalResize (getWidth (), getHeight ());
   }

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      switch (mappedID)
      {
         case widgetConsts.RX_UPDATE_DATA:
            helper.ebs ().setDataControlAttributes (euData, null, pars);
            tryAttackWidget ();
            break;

         case widgetConsts.RX_UPDATE_CONTROL:
            helper.ebs ().setDataControlAttributes (null, euData, pars);
            if (helper.ebs ().firstTimeHavingDataAndControl ())
            {
               //System.out.println (getName () + " firstTimeHavingDataAndControl !");
               tryAttackWidget ();
            }
            updatePosition ();
            setEnabled (helper.ebs ().getEnabled ());
            setVisible (helper.ebs ().getVisible ());
            break;

         case frameAparato.RX_SHOW_FRAMES:
            //System.out.println (getName () + " trigger to show!");
            migthShow = true;
            super.setVisible (helper.ebsFrame ().getVisible ());
            break;

         case frameAparato.RX_WILD_UPDATE:
            SwingUtilities.updateComponentTreeUI (this);
            validate ();
            break;

         case frameAparato.RX_SHOW:
            setVisible (true);
            break;

         case frameAparato.RX_HIDE:
            setVisible (false);
            break;

         case frameAparato.RX_TOGGLE_VISIBLE:
            setVisible ( ! isVisible ());
            break;

         case frameAparato.RX_TELL_POSANDSIZE:
            if (helper.ebsFrame ().hasControl ())
            {
               helper.ebsFrame ().setPos (getX (), getY ());
               helper.ebsFrame ().setSize (getWidth (), getHeight ());
            }
            break;

         default:
            return false;
      }

      return true;
   }

   private void tryAttackWidget ()
   {
      if (helper.ebs().hasAll ())
      {
         // We have to distinguish if visibility is really set in control or not!
         String isVisi = helper.ebs().getSimpleAttribute (widgetEBS.CONTROL, widgetEBS.sATTR_VISIBLE);
         if (isVisi != null)
         {
            //System.out.println (getName () + " set visible to control value " + isVisi.equals("1"));
            setVisible (helper.ebs ().getVisible ()); // or (isVisi.equals("1"))
         }
         else
         {
            //System.out.println (getName () + " set visible to isVisibleStored " + isVisibleStored);
            setVisible (isVisibleStored);
         }

         //set title ?
         //
         String dataTitle = helper.ebs ().getSimpleDataAttribute (frameEBS.sATTR_TITLE);
         if (dataTitle != null)
         {
            setTitle (dataTitle);
         }
      }
   }

   public void updatePosition ()
   {
      int [] num = new int[1];

      int x0 = helper.ebsFrame ().getPosX(num) ? num[0]: getX();
      int y0 = helper.ebsFrame ().getPosY(num) ? num[0]: getY();
      int dx = helper.ebsFrame ().getSizeX(num) ? num[0]: getWidth();
      int dy = helper.ebsFrame ().getSizeY(num) ? num[0]: getHeight();

      setBounds (x0, y0, dx, dy);
   }

   private boolean modeEvaLayoutDiseny = true;


   public void paint (Graphics gra)
   {
      super.paint (gra);

      if (globalJavaj.getPaintingLayout () || log_widgetPos.isDebugging (2))
      {
         paintUsingWidgets (gra, globalJavaj.getPaintingLayout ());
      }
   }


   /*
      Method to paint the frame in design mode (with gastona when variable <PAINT LAYOUT> is present
   */
   public void paintUsingWidgets (Graphics gra, boolean doPaintRed)
   {
      //System.out.println ("modeEvaLayoutDiseny esta activat mach!");

      gra.setColor (Color.RED);
      nPaints ++;

      java.util.List allwi = globalJavaj.getListOfWidgetNames ();

      for (int ww = 0; ww < allwi.size (); ww ++)
      {
         String widName = (String) allwi.get (ww);
         Component co = (Component) globalJavaj.getWidgetByName (widName);

         if (co == null) continue;
         if (!co.isShowing ()) continue;

         Rectangle corec = co.getBounds();

         double xpos = corec.getX();
         double ypos = corec.getY();
         double xLong = -1.;
         double yLong = -1.;
         double xStart = -1.;
         double yStart = -1.;

         Container pare = co.getParent ();
         do
         {
            xpos += pare.getX();
            ypos += pare.getY();
            if (xStart >= 0)
            {
               xStart += pare.getX();
               yStart += pare.getY();
            }

            if (xLong == -1)
            {
               // only for the parent container
               //
               xLong = pare.getWidth ();
               yLong = pare.getHeight ();
               xStart = pare.getX();
               yStart = pare.getY();
            }
            pare = pare.getParent ();
         }
         while (pare != null);

         log_widgetPos.dbg (2, "pos", getName (), new String [] { (nPaints) + "", widName, (int) xpos + "" , (int) ypos + "", "" + (int) corec.getWidth(), "" + (int) corec.getHeight() });

         if (doPaintRed)
         {
            // remove last offsets (position of frame) (? sirve para algo ?)
            xpos -= getX();
            ypos -= getY();
            xStart -= getX();
            yStart -= getY();

            int pin=1;
            int pass=4;

            gra.drawLine ((int) xStart-pass, (int) ypos-pin                        , (int) (xStart+xLong)+pass, (int) ypos-pin);
            gra.drawLine ((int) xStart-pass, (int) (ypos + corec.getHeight() + pin), (int) (xStart+xLong)+pass, (int) (ypos + corec.getHeight() + pin));

            gra.drawLine ((int) xpos-pin                       , (int) yStart-pass, (int) xpos-pin                             , (int) (yStart+yLong)+pass);
            gra.drawLine ((int) (xpos + corec.getWidth() + pin), (int) yStart-pass, (int) (int) (xpos + corec.getWidth() + pin), (int) (yStart+yLong)+pass);
         }
      }
   }
}

