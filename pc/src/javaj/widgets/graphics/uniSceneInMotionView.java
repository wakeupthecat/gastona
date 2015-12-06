/*
package javaj.widgets.graphics;
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
package javaj.widgets.graphics;

//import android.content.Context;
//import android.graphics.*;
//import android.view.View;
//import android.view.MotionEvent;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import javax.swing.JPanel;
import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.math.space.vect3f;

import javaj.widgets.gestures.*;
import javaj.widgets.basics.*;
import javaj.widgets.graphics.*;
import javaj.widgets.graphics.objects.*;


/**
   Native view able to handle native motion events (mouse)

 */
public class uniSceneInMotionView extends JPanel implements MouseListener, MouseMotionListener
{
   private static logger log = new logger (null, "javaj.widgets.graphics.uniSceneInMotionView", null);

   protected ISceneInMotion laEscena2 = null;

   public uniSceneInMotionView ()
   {
      addMouseListener (this);
      addMouseMotionListener(this);
   }

   public void assignNewScene (ISceneInMotion theScene)
   {
      laEscena2 = theScene;
   }

   public void render ()
   {
      invalidate ();
      //paintImmediately (new Rectangle (0, 0, 3000, 3000));
      paintImmediately (getVisibleRect());
   }

   private boolean leftButton = true;

   public void mousePressed (MouseEvent event)
   {
      if (laEscena2 == null) return;
      //log.dbg (2, "onTouchEvent", "event action " + event.getAction() " pointer count " + event.getPointerCount());

      leftButton = event.getButton() == MouseEvent.BUTTON1;

      uniMotion uEvent = new uniMotion (event, uniMotion.FIRST_POINTER_DOWN);
      laEscena2.onUniMotion (uEvent);
      render ();
   }

   public void mouseMoved(MouseEvent event)
   {
   }

   public void mouseDragged(MouseEvent event)
   {
      if (laEscena2 == null) return;

      uniMotion uEvent = new uniMotion (event, uniMotion.POINTER_MOVE);
      laEscena2.onUniMotion (uEvent);
      render ();
   }

   public void mouseReleased (MouseEvent event)
   {
      if (laEscena2 == null) return;

      uniMotion uEvent = new uniMotion (event, uniMotion.LAST_POINTER_UP);
      laEscena2.onUniMotion (uEvent);
      render ();
   }

   public void mouseClicked (MouseEvent e)
   {
   }

   public void mouseEntered (MouseEvent e)
   {
   }

   public void mouseExited (MouseEvent e)
   {
   }
}
