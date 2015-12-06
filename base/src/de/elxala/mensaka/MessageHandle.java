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

package de.elxala.mensaka;

/**	======== de.elxala.mensaka.MessageHandle ==========================================
	@author Alejandro Xalabarder 11.07.2004 02:33

	06.08.2004 21:57: nuevo package de.elxala.mensaka
*/

/**
   Handle to identify the message. At the moment it just contains an index to be used directly by
   Mensaka methods but in the future (when removing messages with no suscribers might be allowed)
   it can be used to check if the handle is still valid.

   Using handles to send a message is not a must since it is still possible to use the method
   Mensaka.sendMessage (String), but it is convenient for three reasons: it is suposed to be faster
   (not need to search the message as string), produces a cleaner code (not repeating
   string constants that may produce typo errors) and might be better "protocolled": when a controller
   use MessageHandle it calls the method Mensaka.declare, thus at this moment the source and the
   message it might send is known and it is not needed to wait for the message to get this information.

   Another important reason is that any system (e.g. listix or a general controller) might send messages just using a string.


   .......

   27.05.2007 11:31

      information set and required by Mensaka

            - mskMessageText
                  message of the text needed to assign and reassign if needed the mskMenssageIndex

            - mskMenssageIndex
                  index assignable only by Mensaka!
                  if != -1 then there is a valid message in the intern list of messages of Mensaka
                  NOTE: mensaka might or might not assign a valid message index for "lost messages"
                        (messages with no listeners at the moment of sending the message)
                        It is not right to assume that after Mensaka.declare we obtain a valid message index

            - mskEraseEpoch
                  Mensaka has an increasing counter to reflect the time of deletions of its internal list
                  of messages. At the moment of assigning a msgMensakaIndex to a handle Mensaka also stamps
                  this internal counter into eraseEpoch of the handle. This way it is possible for mensaka
                  to delete messages of its list without informing all handles about it but invalidating
                  them, that is

                     if (handle.eraseEpoch != currentEraseEpoch)
                        // handle.mskMenssageIndex is invalid! and has to be assigned again using the mskMessageText
                     else
                        // handle.mskMenssageIndex is valid and might be used

*/
public class MessageHandle
{
   // only relevant for mensaka!
   //
   protected String mskMessageText = null;
   protected int    mskMenssageIndex = -1;
   protected int    mskEraseEpoch = -1;

   protected Object sourceObject = null;

   protected int     loggLevelMsgLost = de.elxala.zServices.logServer.LOG_ERROR;
   protected int     triesLeft = -1;

   /**
      Allow default constructor, the handle is not yet valid and it has to
      be filled by calling create method (Mensaka.declare also makes this)
   */
   public MessageHandle ()
   {
   }

   /**
      initilize the handle and declares the message 'msg' for the owner of the handle

      @param objOwner object that might emit the message, usually the owner of the handle
      @param msg      the Mensaka message to be send
   */
   public MessageHandle (Object objOwner, String msg)
   {
      create (objOwner, msg);
   }

   public void create (Object objOwner, String msg)
   {
      sourceObject = objOwner;

      mskMessageText = msg;
      Mensaka.validateHandle (this);
   }

   /**
      This Method permits setting the "message lost" policy for this message

     @param desist if set to true the message will produce just one "message lost" event
                   and after that Mensaka will not attempt to send it again.
                   Note that the default value for this policy is false and it is usually the
                   most convenient policy. Mainly for two reasons: it allows components to be
                   instanciated later and use the message then and, event if the message has really
                   no listeners, the "message lost" log could be a useful trace.
   */
   public void setDesistAfterMsgLost (boolean desist)
   {
      triesLeft = (desist) ? 1: -1;
   }

   /**
      This Method permits setting the "message lost" log level for this message

     @param loggLevelMsgLost per default a message lost event will be treated as error, if the component
                             decides that it shouldn't produce an error, it can assign the exact level of
                             warning or debug for this event (se constants in de.elxala.zServices.logServer)
   */
   public void setMsgLostLogLevel (int errorLevel4MsgLost)
   {
      //System.out.println ("==> setMsgLostLogLevel (" + errorLevel4MsgLost + ") for \"" + mskMessageText + "\"");
      loggLevelMsgLost = errorLevel4MsgLost;
   }

   public void invalidate ()
   {
      mskMenssageIndex = -1;
   }

   /**
      Mensaka would use this method to inform the handle that the message has ben lost
   */
   protected void youAreLost ()
   {
      if (triesLeft > 0) triesLeft --;
   }

   /**
      Mensaka would use this funtion to know if the message has "desist" (see setMsgLostPolicy)
   */
   protected boolean IdoDesist ()
   {
      return triesLeft == 0;
   }

//   public boolean isValid ()
//   {
//      return (mskMessageText >= 0);
//   }

//   public void set (int indx)
//   {
//      index = indx;
//   }
//
//   public int get ()
//   {
//      return index;
//   }
//
//   public Object getCreator ()
//   {
//      return owner;
//   }
};
