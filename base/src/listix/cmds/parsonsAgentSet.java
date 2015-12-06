/*
library listix (www.listix.org)
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

package listix.cmds;

import java.util.*;

import de.elxala.Eva.*;
import de.elxala.parse.parsons.*;
import de.elxala.db.sqlite.sqlSolver;
import de.elxala.zServices.*;

/*
*/
public class parsonsAgentSet
{
   public parsonsAgent           commonAgent = new parsonsAgent ("c.o.m.m.o.n"); // name should not be used!
   protected List                arrAgents = new Vector (); // Vector<parsonsAgent>
   protected static parsonsAgent dummyAgent = new parsonsAgent ("dummyAgent");

   public parsonsAgentSet ()
   {
      commonAgent.parsons = new aLineParsons (new Eva ());
   }
   
   public int size ()
   {
      return arrAgents.size ();
   }
   
   public parsonsAgent getAgentAt (int indx)
   {
      return (parsonsAgent) ((arrAgents != null && 
                              arrAgents.size () > 0 && 
                              indx >= 0 && 
                              indx < arrAgents.size ()) ? arrAgents.get (indx): dummyAgent);
   }
   
   public void addAgent (int agentType, String [] params)
   {
      arrAgents.add (new parsonsAgent (params, agentType));
   }

   public void setPatternToLastAgent (Eva evaPattern, Eva evaAntiPattern)
   {
      if (arrAgents.size () == 0)
      {
         arrAgents.add (new parsonsAgent ("parsons_content"));
      }
      parsonsAgent ag = getAgentAt (arrAgents.size ()-1);
      ag.parsons = new aLineParsons (evaPattern);
      ag.parsons.setAntiPatternList (evaAntiPattern);
   }

   public boolean startAgents (logger log)
   {
      for (int pp = 0; pp < arrAgents.size (); pp ++)
      {
         aLineParsons pa = getAgentAt (pp).parsons;
         if (pa == null || ! pa.init ())
         {
            log.err ("the agent " + getAgentAt (pp).tableName + " cannot start");
            return false;
         }
      }
      return true;
   }
}