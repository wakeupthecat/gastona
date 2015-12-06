/*
package javaj.widgets
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

package javaj.widgets.text;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

/**
   make a JTextArea undo-redo capable and recognize automatically Ctrl+Z (undo) and Ctrl+Y (redo)
*/
public class undoredoTextArea extends JTextArea implements UndoableEditListener, KeyListener
{
   private UndoManager undoMgr;

   public undoredoTextArea()
   {
      getDocument().addUndoableEditListener(this);
      addKeyListener(this);
      resetUndoRedo ();
   }

   public undoredoTextArea(int sizeX, int sizeY)
   {
      super (sizeX, sizeY);

      getDocument().addUndoableEditListener(this);
      addKeyListener(this);
      resetUndoRedo ();
   }

   public void setText (String text)
   {
      resetUndoRedo ();
      super.setText (text);
   }

   /// reset undo-redo history
   public void resetUndoRedo()
   {
      undoMgr = new UndoManager();
      undoMgr.setLimit (1000);
   }

   public void destroy()
   {
      undoMgr.end();
   }

   public void undo ()
   {
      try { undoMgr.undo(); }
      catch (Exception e) {}
   }

   public void redo ()
   {
      try { undoMgr.redo(); }
      catch (Exception e) {}
   }

   /// implementing ... UndoableEditListener
   public void undoableEditHappened(UndoableEditEvent uee)
   {
      undoMgr.addEdit(uee.getEdit());
   }

   /// implementing ... KeyListener
   public void keyPressed (KeyEvent ke)
   {
      if (ke.isControlDown())
      {
         switch (ke.getKeyCode())
         {
            case KeyEvent.VK_Z: undo ();  break;   //Ctrl + Z
            case KeyEvent.VK_Y: redo ();  break;   //Ctrl + Y
            default: break;
         }
      }
   }

   public void keyReleased(KeyEvent e)
   {
   }

   public void keyTyped(KeyEvent e)
   {
   }
}
