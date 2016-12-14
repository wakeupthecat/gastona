/*
gastona for Android
Copyright (C) 2013 Alejandro Xalabarder Aulet

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

package org.gastona;

import android.content.Context;
import android.view.View;

import java.io.File;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import org.gastona.cmds.*;

public class gastonaAppConfig
{
   public static final String getAppName ()
   {
      return "gastona";
   }

   public static final String getAppPackageString ()
   {
      return "org.gastona";
   }

   public static final String getAppFlexActorClassName ()
   {
      return "gastonaFlexActor";
   }

   public static final String getAppMainActorClassName ()
   {
      return "gastonaMainActor";
   }

   public static View loadMainAppScript (Context who)
   {
      View lamainView = gastonaFlexActor.loadFrame (who, "autoStart.gast");

      if (lamainView == null)
      {
         CmdMsgBox.alerta (CmdMsgBox.WARNING_MESSAGE,
                            "Gastona cannot start",
                            "autoStart.gast not found!",
                            new String [] {"Accept"}, new String [] {"javaj doExit"});
         return null;
      }

      return lamainView;
   }
}
