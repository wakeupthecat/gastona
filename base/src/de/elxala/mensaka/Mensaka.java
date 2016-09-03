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

/**   ======== de.elxala.langutil.MensakaPaket ==========================================
   @author Alejandro Xalabarder 25.02.2003 16:39

*/

import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.Eva.*;
import de.elxala.zServices.logger;
import java.util.*;
import javaj.widgets.basics.*;
import javaj.widgets.basics.zWidgetUtil; //(o) NOTES_java_javac if not explicit imported then it is not compiled (???!!)

public class Mensaka
{
   // Note: all methods in Mensaka are static! we cannot provide a this pointer to logger
   protected static logger log = new logger (new Mensaka(), "mensaka", null);
   protected static MensakaLogFlow flowLogger = new MensakaLogFlow();

   protected static boolean UNSUBSCRIBE_USED = false; // for a temporary work around
   protected static final String BOTTLE_ANONYMUS = "(bottle)"; // message in a bottle = anonymus

   // struct ...
   private static class list_Targets
   {
      /// vector of subscriptors of a particular message
      List /* vector<MensakaTarget> */      l_objSubscritos = new Vector ();

      /// each subscriptor wants to receive the message with a particular int (mappedID)
      List /* vector<int [1]> */       l_mappedID = new Vector ();
   };

   // NOTE : both lists have to grow on parallel

   static protected List /* Vector<String>       */ vec_msgText = new Vector ();
   static protected List /* Vector<list_Targets> */ vec_targets = new Vector ();

   static public void destroyCommunications ()
   {
      vec_msgText = new Vector ();
      vec_targets = new Vector ();
   }

   /**
      registers a new message and returns its index. It asumes that the message
      it is not alredy registered (does not check for it).
   */
   private static int registerNewMessage (String sMsg)
   {
      // check vector consistency
      if (vec_msgText.size() != vec_targets.size())
      {
         log.fatal ("registerNewMessage", "inconsistent message vectors!");
      }

      vec_msgText.add (sMsg);
      vec_targets.add (new list_Targets ());

      flowLogger.registerNewMessage (vec_msgText.size ()-1, sMsg);

      return vec_targets.size () - 1;
   }

   // anonymus send packet without packet
   //
   static public int sendPacket (String msgId)
   {
      MessageHandle hndMSG = new MessageHandle (BOTTLE_ANONYMUS, msgId);
      return sendPacket (hndMSG, null);
   }

   /**
      Sends the message 'msgId' with data 'pk' to all the subscribers of this message.
      Everybody (every method in every class) can send such a packet, even no subscribers for this
      message are needed. In case there are no subscribers for this message at that moment then
      the message is lost (not obviously!) and the function returns 0.

      @return the number of subscribers for this message THAT HAVE RETURN true in its method takePacket

      Example:
         Mensaka.sendPacket ("hello world packettery", null);
    */
   public static int sendPacket (String msgId, EvaUnit pk)
   {
      return sendPacket (msgId, pk, null);
   }

   public static int sendPacket (String msgId, EvaUnit pk, String [] arg)
   {
      MessageHandle hndMSG = new MessageHandle (BOTTLE_ANONYMUS, msgId);
      return sendPacket (hndMSG, pk, arg);
   }


   /**
      Returns true if the message related with the handle 'handle' has, at least, one subscriber.
      This fucntion is intented to avoid filling the data into EvaUnit for
      that processes that are going to send a packet and are not sure if any
      other process is going to listen them. (e.g. DEBUG_MSG)

      Example:

         if (Mensaka.hasSubscribers (handle))
         {
            // prepar pData ...
            Mensaka.sendPacket (handle, pData);
         }


      @param handle of the message to check

      return true if, at this time, the checked message has at least one subscriber to it

   */
   public static boolean hasSubscribers (MessageHandle handle)
   {
      validateHandle (handle);

      if (! isValidHandle (handle)) return false;

      list_Targets targs = (list_Targets) vec_targets.get(handle.mskMenssageIndex);
      return targs.l_objSubscritos.size () > 0;
   }

   /**
      for flowLogger
   */
   protected static int getNumberOfSubscribers(MessageHandle handle)
   {
      validateHandle (handle);

      if (! isValidHandle (handle)) return 0;

      list_Targets targs = (list_Targets) vec_targets.get(handle.mskMenssageIndex);
      return targs.l_objSubscritos.size ();
   }

   public static int sendPacket (MessageHandle hand, EvaUnit pk)
   {
      return sendPacket (hand, pk, new String [0]);
   }

   /**
      to send a message using a valid message handle
   */
   public static int sendPacket (MessageHandle hand, EvaUnit pk, String [] parameters)
   {
      validateHandle (hand);
      if (! isValidHandle (hand))
      {
         // some memory problem ? it should not happen
         if (hand != null)
            log.fatal ("sendPacket", "handle of message [" + hand.mskMessageText + "] comming from [" + hand.sourceObject.toString () + "] could not be validated!");
         return 0;
      }

      if (hand.IdoDesist ())
      {
         // the message has desist to be emitted (see MessageHandle ::setDesistAfterMsgLost msg lost policy)
         // DO NOT REPOR THIS WITH log !
         return 0;
      }

      // get the list of subscribers
      //
      list_Targets targs = (list_Targets) vec_targets.get (hand.mskMenssageIndex);

      if (targs.l_objSubscritos.size () == 0)
      {
         // MESSAGE PACKET LOST!
         flowLogger.packetLost (hand, pk);

         // check msg lost policy
         hand.youAreLost ();
         if (hand.IdoDesist ())
         {
            // the message has desist to be emitted (see MessageHandle ::setDesistAfterMsgLost msg lost policy)
            // we will report this just once!
            log.dbg (0, "the message [" + hand.mskMessageText + "] will not be sent anymore!");
         }
         return 0;
      }

      MensakaTarget obj;
      int mappo = 0;
      int count = 0;

      flowLogger.startMessage (hand, pk);

      //Important! get the size here to prevent recursion risk
      //           01.11.2010 21:17 It happened when calling gastona.gastona from a gast script
      int nsubscribed = targs.l_objSubscritos.size ();

      for (int ii = 0; ii < nsubscribed; ii ++)
      {
         obj = (MensakaTarget) targs.l_objSubscritos.get (ii);
         if (obj == null) continue; // allow subscriptors null, due to unsubscribe behaviour

         int [] matmap = (int []) targs.l_mappedID.get(ii);
         if (matmap == null || matmap.length == 0)
         {
            final String failText = "Fail mapping of subscribers (mensaka intern!) for message [" + hand.mskMessageText + "]";
            if (UNSUBSCRIBE_USED)
                 log.warn ("sendPacket", failText);
            else log.fatal ("sendPacket", failText);
            continue;
         }

         mappo = matmap[0];

         flowLogger.logMessageEntry (hand, obj, pk);

         //(o) TODO_listix implement stack message ?
         // PUSH stackMessages (hand + obj)  .... msg:"javaj allez" target:"zBoticario"
         lastMsgHandle = hand;
         lastMsgTarget = obj;

         // physically send the message! call to takePacket
         if (obj.takePacket (mappo, pk, parameters)) count ++;

         //(o) TODO_listix implement stack message ?
         // POP stackMessages ()

         flowLogger.logMessageExit (hand, obj, pk);
      }

      if (nsubscribed != targs.l_objSubscritos.size ())
      {
         log.warn ("sendPacket", "Notifications the same message (\"" + hand.mskMessageText + "\") while dispatching it!");
      }

      flowLogger.endMessage (hand, pk);

      return count;
   }

   /**
      Declares a message (see declare (Object theSource, MessageHandle handle, String msgToDeclare))
      And aditionally sets the log level for "packet lost" event for this message

      @param theSource    source object (usually this) that will send the declared message
      @param handle       handle to the message to be used on further sendPacket calls
      @param msgToDeclare the message to be declared
      @param logLevelWhenMessageLost set the log level for the trace of lost messages (or packets) for this message

   */
   public static void declare (Object theSource, MessageHandle handle, String msgToDeclare, int logLevelWhenMessageLost)
   {
      declare (theSource, handle, msgToDeclare);
      handle.setMsgLostLogLevel (logLevelWhenMessageLost);
   }

   /**
      Declares that the object 'theSource' will send (during its life) Mensaka messages of type 'msgToDeclare'.
      This function has two proposes : the first one is, by using it more information about sources of messages is obtained
      "on the fly" or while then program is running (see Protokol), and second it takes a handle for the message
      which is a better way to send messages (using Mensaka.sendPacket (Mensaka Handle...) instead of Mensaka.sendPacket (String ...))

      @param theSource    source object (usually this) that will send the declared message
      @param handle       handle to the message to be used on further sendPacket calls
      @param msgToDeclare the message to be declared

   */
   public static void declare (Object theSource, MessageHandle handle, String msgToDeclare)
   {
      if (handle == null)
      {
         log.err ("declare", "Message handle null declaring message [" + msgToDeclare + "]");
         return;
      }

      handle.create (theSource, msgToDeclare);
      validateHandle (handle);

      if (! isValidHandle (handle))
      {
         // the message is not there! then add new message and new list of subscribers
         // we will save time each time the message is sent because it has a valid handle.
         // that does not mean that the message couldn't be lost (no subscribers)!
         //
         handle.mskMenssageIndex = registerNewMessage (handle.mskMessageText);
      }
   }

   static private int currentEraseEpoch = 0;

   private static boolean isValidHandle (MessageHandle handle)
   {
      return (handle != null &&
              handle.mskMenssageIndex >= 0 &&
              handle.mskMenssageIndex < vec_msgText.size () &&
              handle.mskEraseEpoch == currentEraseEpoch
              );
   }

   /**
      It does not create the message if not found!
      //(o) TODO_Mensaka Why "it does not create ..." ? review all calls to validate handle
                         For instance de.elxala.db.sqlite.clientCaller send lost packets on staticInit
   */
   public static void validateHandle (MessageHandle handle)
   {
      if (handle == null)
      {
         log.err ("validateHandle", "Message handle null");
         return;
      }

      if (isValidHandle (handle))
      {
         // handle is valid!
         return;
      }

      // search for the message into the list
      handle.mskMenssageIndex = vec_msgText.indexOf (handle.mskMessageText);

      // if not already registered do it now
      if (handle.mskMenssageIndex == -1)
         handle.mskMenssageIndex = registerNewMessage (handle.mskMessageText);

      // validity stamp
      handle.mskEraseEpoch = currentEraseEpoch;
   }

   /**
      Subscribes an object of a class (that implements MensakaTarget) to receive a specific
      message 'msgId'

      @param theTarget the class that implements MensakaTarget and want to receive the message 'msgID'
      @param mappID a internal (to 'theTarget') identifier of type int that will identifies the message 'msgID'
      @param msgID the message that 'theTarget' wants to receive up to this moment

      Example:

         .. body of a class X that implements MensakaTarget

         int MSG_BYE_BYE = 55;

         Mensaka.subscribe (this, MSG_BYE_BYE, "adios");

         // implementation od MensakaTarget
         public boolean takePacket (int mappedMsg, EvaUnit pk) {
            switch (mappedMsg) {
               case MSG_BYE_BYE:
                  System.out.println ("ciao");
                  return true;
               default: break;
            }
            return false;
         }

    */
   public static void subscribe (MensakaTarget theTarget, int mappId, String msgId)
   {
      String ello = msgId; //(o) mensaka_MessageCase //.toLowerCase ();

      log.dbg (2, "subscribe", "message [" + msgId + "] with mappId " + mappId);

      // get the message indx
      int msgIndx = vec_msgText.indexOf (ello);
      if (msgIndx != -1)
      {
         // get the list of targets of this message
         log.dbg (2, "subscribe", "message alredy registered with msgIndex " + msgIndx);
      }
      else
      {
         // the message is not there! then add new message and new list of subscribers
         msgIndx = registerNewMessage (ello);

         log.dbg (2, "subscribe", "message is new, get the msgIndex " + msgIndx);
      }

      list_Targets laListaTargets = (list_Targets) vec_targets.get(msgIndx);

      boolean addIt = false;

      // get the indx of the target in the list of targets
      int tarindx = laListaTargets.l_objSubscritos.indexOf(theTarget);
      if (tarindx == -1)
      {
         // it was not subscribed ==> subscribirlo
         addIt = true;
      }
      else
      {
         // ya estaba subscrito pero si el mappId es diferente lo subscribimos
         // puede ser u'til para robots que mapean mensajes pues el target siempre sera' el
         // robot pero puede que el mismo mensaje tenga varios destinatarios
         //
         int eraMap = ((int []) laListaTargets.l_mappedID.get(tarindx)) [0];
         addIt = (mappId != eraMap);
      }

      if (addIt)
      {
         log.dbg (2, "subscribe", "message added");
         laListaTargets.l_objSubscritos.add (theTarget);
         laListaTargets.l_mappedID.add (new int [] { mappId });

         flowLogger.subscribedMessage (theTarget, msgIndx);
      }
      else log.dbg (2, "subscribe", "message was alredy subscribed with the same mappId!");

      return;
   }

   public static void unsubscribe (String senderName, MensakaTarget theTarget)
   {
      // get indx of the message
      int indx1 = vec_msgText.indexOf (senderName); //(o) mensaka_MessageCase //.toLowerCase ());
      if (indx1 == -1) return;

      // get the targets list
      list_Targets targets = (list_Targets) vec_targets.get(indx1);
      // get indx of the target from the targets list
      int indx2 = targets.l_objSubscritos.indexOf(theTarget);
      if (indx2 != -1)
      {
         // DO NOT REMOVE IT NOW !! (if the list is in use by nested calls can cause problems to next subscribers)
         targets.l_objSubscritos.set(indx2, null);
         targets.l_mappedID.set(indx2, null);
         UNSUBSCRIBE_USED = true;
//         // remove target and map associated
//         targets.l_objSubscritos.remove (indx2);
//         targets.l_mappedID.remove (indx2);
//
//         // if there are no more targets subscribed then remove the message and the list (void) itself as well
//         if (targets.l_objSubscritos.size () == 0)
//         {
//            vec_msgText.remove (indx1);
//            vec_targets.remove(indx1);
//         }
      }
      else {
         // this target was not subscribed!
      }
   }


   public static void unsubscribe (MensakaTarget theTarget)
   {

      for (int ii = 0; ii < vec_targets.size (); ii ++)
      {
         list_Targets targets = (list_Targets) vec_targets.get(ii);
         int indx2 = targets.l_objSubscritos.indexOf(theTarget);
         if (indx2 != -1)
         {
            // DO NOT REMOVE IT NOW !! (if the list is in use by nested calls can cause problems to next subscribers)
            targets.l_objSubscritos.set(indx2, null);
            targets.l_mappedID.set(indx2, null);
            UNSUBSCRIBE_USED = true;
   //         // remove target and map associated
   //         targets.l_objSubscritos.remove (indx2);
   //         targets.l_mappedID.remove (indx2);
   //
   //         // if there are no more targets subscribed then remove the message and the list (void) itself as well
   //         if (targets.l_objSubscritos.size () == 0)
   //         {
   //            vec_msgText.remove (indx1);
   //            vec_targets.remove(indx1);
   //         }
         }
      }
   }

   private static MessageHandle lastMsgHandle = null;
   private static MensakaTarget lastMsgTarget = null;

   /**
      Function for log pruposes

      //(o) TODO_mensaka debug message stack: implement (if possible at low cost)


   */
   public static String [] getLastMessageStack ()
   {
      if (lastMsgHandle == null || lastMsgTarget == null) return new String [0];

      String [] laPila = new String [1];

      String nameTarget =
            lastMsgTarget.getClass().toString () + " " +
            zWidgetUtil.getNameIfzWidget (lastMsgTarget);

      laPila[0] = "msg: [" + lastMsgHandle.mskMessageText + "] target: [" + nameTarget + "]";
      return laPila;
   }

   public static void dumpMensaka (String filename)
   {
      TextFile fix = new TextFile ();
      if ( ! fix.fopen (filename, "w")) return;

      for (int mm = 0; mm < vec_msgText.size(); mm ++)
      {
         // Formato: target, mensaje
         //
         // no hay manera de conocer la source del mensaje puesto que sendPacket
         // no sabe que objeto le ha llamado habra' que interpretar pues la primera
         // parte del mensaje como la fuente del mismo
         // aunque esta convencio'n no tiene porque seguirse siempre.
         //
         List targ = ((list_Targets) vec_targets.get (mm)).l_objSubscritos;

         for (int tt = 0; tt < targ.size (); tt ++)
         {
            String targName = "";

            Object obj = (Object) targ.get(tt);
            targName = obj.getClass ().getName () + "\t";

            if (zWidgetUtil.isObjectAzWidget (obj))
            {
               String compName = zWidgetUtil.getNameIfzWidget (obj);
               targName += (compName != null && compName.length () > 0) ? compName: "(unamed Component)";
            }
            else targName += "(no widget)";

            fix.writeLine (targName + "\t" + (String) vec_msgText.get(mm));
         }
      }

      fix.fclose ();
   }
}