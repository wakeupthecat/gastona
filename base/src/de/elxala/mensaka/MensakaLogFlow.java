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
   @author Alejandro Xalabarder
   @data   05.04.2009 12:41

	05.04.2009 12:41: Solución (final ?) para message logging
	                  Esta clase deberá eliminar las clases "restos"
	                     MensakaProtokoller.java, Protokoll.java, Protokol2dot.java, protokolServer.java

*/

import java.util.List;
import java.util.Vector;
import de.elxala.Eva.*;
import de.elxala.Eva.abstractTable.*;
import de.elxala.zServices.logger;
import javaj.widgets.basics.*;

/**

   mensaka_sess_agents

      agentId, componentName, objectToStr

   mensaka_sess_Msgs

      msgId, msgText

   mensaka_sess_declaredMsgs

      msgId, agentId, msgText

   mensaka_sess_suscribedMsg

      msgId, agentId


   mensaka_flow_lostMsgs

      agentId, msgText

   mensaka_flow_sentMsgs

      agentTx, agentRx, startEnd, msgId


*/
public class MensakaLogFlow
{
   protected static final int MAX_AGENT_SIZE = 1000;
   protected static final int FLOWLEVEL_1 = 2;
   protected static final int FLOWLEVEL_2 = 3;
   protected static final int FLOWLEVEL_3 = 4;


   protected static String [][] ownConnections = new String [][]
         {
            new String [] { "agent", "mensaka_sess_declaredMsgs", "agentId", "mensaka_sess_agents", "agentId" },
            new String [] { "agent", "mensaka_sess_suscribedMsg", "agentId", "mensaka_sess_agents", "agentId" },
            new String [] { "agent", "mensaka_flow_lostMsgs"    , "agentId", "mensaka_sess_agents", "agentId" },
            new String [] { "agentTx", "mensaka_flow_sentMsgs"    , "agentTx", "mensaka_sess_agents", "agentId" },
            new String [] { "agentRx", "mensaka_flow_sentMsgs"    , "agentRx", "mensaka_sess_agents", "agentId" },
            new String [] { "msg"  , "mensaka_sess_declaredMsgs", "msgId"  , "mensaka_sess_msgs", "msgId"     },
            new String [] { "msg"  , "mensaka_sess_suscribedMsg", "msgId"  , "mensaka_sess_msgs", "msgId"     },
            new String [] { "msg"  , "mensaka_flow_lostMsgs"    , "msgId"  , "mensaka_sess_msgs", "msgId"     },
            new String [] { "msg"  , "mensaka_flow_sentMsgs"    , "msgId"  , "mensaka_sess_msgs", "msgId"     }
         };

   protected logger log_sess_agents = new logger (this, "mensaka_sess_agents", new String [] { "agentId", "componentName", "objectToStr" });
   protected logger log_sess_msgs = new logger   (this, "mensaka_sess_msgs"  , new String [] { "msgId", "msgText" });
   protected logger log_sess_declaredMsgs = new logger (this, "mensaka_sess_declaredMsgs", new String [] { "msgId", "agentId", "msgText" });
   protected logger log_sess_suscribedMsg = new logger (this, "mensaka_sess_suscribedMsg", new String [] { "msgId", "agentId" });

   protected logger log_flow_lostMsgs = new logger (this, "mensaka_flow_lostMsgs", new String [] { "agentId", "msgText" });
   protected logger log_flow_sentMsgs = new logger (this, "mensaka_flow_sentMsgs", new String [] { "agentTx", "agentRx", "beginEnd", "msgId" }, ownConnections);

   // connections
   //
   //    agent, mensaka_sess_declaredMsgs, agentId, mensaka_sess_agents, agentId
   //    agent, mensaka_sess_suscribedMsg, agentId, mensaka_sess_agents, agentId
   //    agent, mensaka_flow_lostMsgs, agentId, mensaka_sess_agents, agentId
   //    agent, mensaka_flow_sentMsgs, agentTx, mensaka_sess_agents, agentId
   //    agent, mensaka_flow_sentMsgs, agentRx, mensaka_sess_agents, agentId
   //    msg  , mensaka_sess_declaredMsgs, msgId, mensaka_sess_msgs, msgId
   //    msg  , mensaka_sess_suscribedMsg, msgId, mensaka_sess_msgs, msgId
   //    msg  , mensaka_flow_lostMsgs, msgId, mensaka_sess_msgs, msgId
   //    msg  , mensaka_flow_sentMsgs, msgId, mensaka_sess_msgs, msgId
   //
   // connections simplifyied
   //
   //    agent,       ,        , mensaka_sess_agents, agentId
   //    agent,       , agentTx, mensaka_sess_agents, agentId
   //    agent,       , agentRx, mensaka_sess_agents, agentId
   //    msg  ,       ,        , mensaka_sess_msgs  , msgId
   //


   //protected logger log_flow = new logger (this, "mensaka_flow", new String [] { "firstId", "field1", "field2" });

   private List /* vector<Object> */ l_mskAgents = new Vector ();

//   static public void setActive (boolean on)
//   {
//      active = false;
//   }
//
//   static public boolean isActive ()
//   {
//      return active;
//   }
//
   private boolean errorInformed = false;

   private int getAgentIndx (Object obj)
   {
      int indx = l_mskAgents.indexOf(obj);
      if (indx != -1) return indx;

      // new agent!

      //get names
      String objName = obj == null ? "null": obj.toString ();
      String compName = zWidgetUtil.getNameIfzWidget (obj);

      int agentID = -1;
      if (l_mskAgents.size () >= MAX_AGENT_SIZE)
      {
         if (!errorInformed)
         {
            errorInformed = true;
            log_sess_agents.err ("getAgentIndx", "agent table max size " + MAX_AGENT_SIZE + " exceeded, from this point logging messages migth be incomplete!");
         }
      }
      else
      {
         l_mskAgents.add (obj);
         agentID = l_mskAgents.size () - 1;
         log_sess_agents.dbg (FLOWLEVEL_1, "getAgentIndx", "newAgent", new String [] {""+agentID, compName, objName });
      }
      return agentID;
   }

   protected void declaredMessage (MessageHandle hand)
   {
      if (log_sess_declaredMsgs.isDebugging (FLOWLEVEL_1))
      {
         int agentID = getAgentIndx (hand.sourceObject);

         //   msk_declarations
         //
         //      TxId, "", msgId
         //
         log_sess_declaredMsgs.dbg (FLOWLEVEL_1, "declaredMessage", "declare", new String [] {""+agentID, ""+hand.mskMenssageIndex, hand.mskMessageText });
      }
   }


   protected void suscribedMessage (MensakaTarget targ, int msgIndx)
   {
      if (log_sess_suscribedMsg.isDebugging (FLOWLEVEL_1))
      {
         int agentID = getAgentIndx (targ);

         //   msk_suscriptions
         //
         //      "", RxId, msgId
         //
         log_sess_suscribedMsg.dbg (FLOWLEVEL_1, "suscribedMessage", "suscribe", new String [] {""+msgIndx, ""+agentID });
      }
   }

   protected void registerNewMessage (int indx, String sMsg)
   {
      if (log_sess_msgs.isDebugging (FLOWLEVEL_1))
      {
         //   msk_messages
         //
         //      "", "", msgId
         //
         log_sess_msgs.dbg (FLOWLEVEL_1, "registerNewMessage", "newMessges", new String [] {""+indx, sMsg });
      }
   }


   protected void packetLost (MessageHandle hand, EvaUnit pk)
   {
      // log or not according to the loggLevelMsgLost of the message sender which is usually LOG_ERROR
      // but it can be relaxed in some cases
      //
      if (log_flow_lostMsgs.isLogging (hand.loggLevelMsgLost))
      {
         int agentID = getAgentIndx (hand.sourceObject);

         //   msk_messages
         //
         //      "", "", msgId
         //
         log_flow_lostMsgs.dbg (FLOWLEVEL_1, "packetLost", "msgLost", new String [] {""+agentID, hand.mskMessageText });
      }
   }

   protected void startMessage (MessageHandle hand, EvaUnit pk)
   {
      if (log_flow_sentMsgs.isDebugging (FLOWLEVEL_1) || log_flow_lostMsgs.isDebugging (FLOWLEVEL_1))
      {
         int nsus = Mensaka.getNumberOfSuscribers (hand);
         int agentID = getAgentIndx (hand.sourceObject);

         //   msk_messages
         //
         //      "", "", msgId
         //
         if (nsus > 0)
              log_flow_sentMsgs.dbg (FLOWLEVEL_1, "sendMsg", "send to " + nsus + " suscribers", new String [] {""+agentID, "-1", "B", ""+hand.mskMenssageIndex });
         else log_flow_sentMsgs.dbg (FLOWLEVEL_1, "startMessage", "msgLost", new String [] {""+agentID, hand.mskMessageText });
      }
   }

   protected void endMessage (MessageHandle hand, EvaUnit pk)
   {
      if (log_flow_sentMsgs.isDebugging (FLOWLEVEL_1))
      {
         int nsus = Mensaka.getNumberOfSuscribers (hand);
         int agentID = getAgentIndx (hand.sourceObject);

         //   msk_messages
         //
         //      TxId, "", msgId
         //
         if (nsus > 0)
              log_flow_sentMsgs.dbg (FLOWLEVEL_1, "sendMsg", "sent to " + nsus + " suscribers", new String [] {""+agentID, "-1", "E", ""+hand.mskMenssageIndex });
         // not really interesting ...
         //else log_flow.dbg (FLOWLEVEL_1, "LostMessage", hand.mskMessageText, new String [] {""+agentID, "", "-1" });
      }
   }

   protected void logMessageEntry (MessageHandle hand, MensakaTarget dest, EvaUnit pk)
   {
      if (log_flow_sentMsgs.isDebugging (FLOWLEVEL_1))
      {
         int TxID = getAgentIndx (hand.sourceObject);
         int RxID = getAgentIndx (dest);

         //   msk_messages
         //
         //      TxId, RxId, msgId
         //
         log_flow_sentMsgs.dbg (FLOWLEVEL_1, "logMessageEntry", "deliver", new String [] {""+TxID, ""+RxID, "B", ""+hand.mskMenssageIndex });
      }
   }

   protected void logMessageExit (MessageHandle hand, MensakaTarget dest, EvaUnit pk)
   {
      if (log_flow_sentMsgs.isDebugging (FLOWLEVEL_1))
      {
         int TxID = getAgentIndx (hand.sourceObject);
         int RxID = getAgentIndx (dest);

         //   msk_messages
         //
         //      TxId, RxId, msgId
         //
         log_flow_sentMsgs.dbg (FLOWLEVEL_1, "logMessageEntry", "delivered", new String [] {""+TxID, ""+RxID, "E", ""+hand.mskMenssageIndex });
      }
   }
}
