/*
packages javaj
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

package javaj;

import de.elxala.mensaka.*;
import de.elxala.Eva.*;

import javaj.widgets.basics.*;

/**
   Simple repeater of messages


   javaj use it directly :
         if the eva MessageToMessage exists in javaj evaunit then
         an instance of msgRepeater is performed and the eva used as translation message

         optionally might be instanciated as controller giving it the parameters
         eva name, eva unit, eva file. For example

         <controllers>
            javaj.msgRepeater, catala, translations, config/lang/messages
            javaj.msgRepeater, rus,    translations, config/lang/messages
*/
public class msgRepeater implements MensakaTarget, setParameters_able
{
   private MessageHandle [] jergaVector = new MessageHandle [0];

   public boolean takePacket (int mappedID, EvaUnit euData, String [] pars)
   {
      // ))) o---o )))
      //
      // send re-mapped message
      //
      if (mappedID >= 0 &&
          mappedID < jergaVector.length &&
          jergaVector[mappedID] != null)
         Mensaka.sendPacket (jergaVector[mappedID], euData, pars);

      return true;
   }

   /**
      parameters: translation Eva, EvaUnit name, EvaFile name
   */
   public void setParameters (String [] param)
   {
      if (param.length < 3) return;
      EvaUnit eu = EvaFile.loadEvaUnit (param[2], param[1]);

      if (eu == null) return;

      loadMessageToRepeat (eu.getEva (param[0]));
   }

   public void loadMessageToRepeat (Eva mapaStr)
   {
      jergaVector = new MessageHandle [mapaStr.rows ()];

      for (int ii = 0; ii < mapaStr.rows (); ii ++)
      {
         String fromMsg = mapaStr.getValue (ii, 0);
         String toMsg   = mapaStr.getValue (ii, 1);
         jergaVector[ii] = null;

         // subscribe to fromMsg
         Mensaka.subscribe (this, ii, fromMsg);
         if (toMsg.length () == 0) continue; // just receive fromMsg and do not send anything

         // handle to send toMsg
         jergaVector[ii] = new MessageHandle ();
         Mensaka.declare (this, jergaVector[ii], toMsg);
      }
   }
}
