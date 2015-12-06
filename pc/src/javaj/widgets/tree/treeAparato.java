/*
package de.elxala.zWidgets
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

package javaj.widgets.tree;

import javax.swing.tree.TreeModel;

import javaj.widgets.basics.*;
import de.elxala.mensaka.*;

/**
*/
public class treeAparato extends basicAparato
{
   private MessageHandle HMSG_ParentAction = null;

   public treeAparato (MensakaTarget objWidget, treeEBS pDataAndControl)
   {
      super (objWidget, pDataAndControl);
   }

   public treeEBS ebsTree ()
   {
      return (treeEBS) ebs ();
   }

   public TreeModel getSwingTreeModel ()
   {
      return new treeEvaModel (ebsTree ());
   }

   public void signalParentAction ()
   {
      // not all widgets uses this signal
      if (HMSG_ParentAction == null)
      {
         HMSG_ParentAction = new MessageHandle (myOwner, ebs().evaName ("parent"));
      }

      // send the message
      Mensaka.sendPacket (HMSG_ParentAction, ebs().getData ());
   }
}
