/*
library listix (www.listix.org)
Copyright (C) 2005-2022 Alejandro Xalabarder Aulet

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

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.parse.*;
import de.elxala.parse.parsons.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.dbMore.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end

/**
   Scans (source) either :
         a file
         a text contained in a variable
      or a string

   and can place the result(s) (target) in

      a db table
      a variable as a table
      a variable as a single value

   the scan of one source can collect results for more targets (parsons agents)
   so, for instance a single pass through a file might generate different tables.

*/
public class cmdParsonsEstructurel implements commandable
{
   //(o) TODO_elxala_mensaka_util make util class LightMessage
   //             Example
   //
   //       private static int chivato = new LightMessage("scanFiles");
   //       ...
   //       chivato.sendStart ();      // here "ledMsg scanFiles_start" will be sent
   //       chivato.sendProgress ();   // here "ledMsg scanFiles_progress1" or "ledMsg scanFiles_progress2" will be sent
   //       chivato.sendError ();      // here "ledMsg scanFiles_error" will be sent
   //       chivato.sendEnd ();        // here "ledMsg scanFiles_end" will be sent

   //private static MessageHandle TX_FATALERROR    = new MessageHandle (); // "_lib scanFiles_error"
   private static MessageHandle LIGHT_MSG_START     = null; // new MessageHandle (); // "ledMsg parsing_start"
   private static MessageHandle LIGHT_MSG_PROGRESS  = null; // new MessageHandle (); // "ledMsg parsing_progresss"
   private static MessageHandle LIGHT_MSG_END       = null; // new MessageHandle (); // "ledMsg parsing_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   protected String PARSONS_FILES_TABLENAME = "parsons_files";


   private static boolean initOnce_msgHandles ()
   {
      if (LIGHT_MSG_START == null)
      {
         LIGHT_MSG_START     = new MessageHandle ();
         LIGHT_MSG_PROGRESS  = new MessageHandle ();
         LIGHT_MSG_END       = new MessageHandle ();

         // this messages are not mandatory to be subscribed, the are provided just as internal information of parser command
         Mensaka.declare (null, LIGHT_MSG_START    , "ledMsg parsing_start"      , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_PROGRESS , "ledMsg parsing_progresss"  , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_END      , "ledMsg parsing_end"        , logServer.LOG_DEBUG_0);
      }
      return true;
   }


   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "PARSONS",
          "PARSE FILE",
          "PARSE",
       };
   }

   private String dbName      = null;

   private int startLine = 1;
   private int endLine   = -1;
   private int limitLines = -1;
   private int limitRecords = -1;
   private int limitNFiles = -1;
   private scanTextSources parsingSource = null;

   static final String [] AGENT_HEAD = new String [] { "TABLE", "DBTABLE", "VARTABLE", "EVATABLE", "VAR", "EVA" };
   static final String [] AGENT_PATTERN = new String [] { "", "PATT", "PATTERN" };
   static final String [] AGENT_OPTCOLPATT = new String [] { "OPT", "OPTCOL", "OPTCOLPATT", "OPTCOLPATTERN" };

   parsonsAgentSet agents = new parsonsAgentSet ();

//NN  private aLineParsons[] agentParsons = null;  // number of agents
//NN  private String[]       agentTables = null;   // name of table of each agent (SAME SIZE than agentParsons!)

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   //@Override
   public int execute (listix that, Eva commands, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      boolean explicitDBgiven = cmd.getArg(2).length() > 0;

      dbName = cmd.getListix().resolveDBName (cmd.getArg(2));

      // String  folderSyntaxExtensions = cmd.getArg (3);
      // boolean folderSyntaxRecursive = cmd.getArg (4).length() == 0 ? true: (cmd.getArg (4).charAt(0) == 1);

      parsingSource = new scanTextSources ("parsedFiles");
      // check source
      //
      if (!parsingSource.openContentSource (cmd, "PARSONS"))
         return 1;

      startLine    = stdlib.atoi (cmd.takeOptionString(new String [] {"STARTLINE", "START", "BEGIN", "BEGINLINE" }, "1"));
      endLine      = stdlib.atoi (cmd.takeOptionString(new String [] {"ENDLINE", "END" }, "-1"));
      limitLines   = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITLINES", "MAXLINES", "LINES" }, "-1"));
      limitRecords = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITRECORDS", "MAXRECORDS", "RECORDS" }, "-1"));
      limitNFiles  = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITFILES", "MAXFILES", "FILES" }, "-1"));

      boolean clean = (1 == stdlib.atoi (cmd.takeOptionString(new String [] {"CLEAN", "CLEAR" }, "-1")));

      if (cmd.getLog().isDebugging (2))
      {
         String plusStr = "";
         if (startLine != 1) plusStr     += " start line = " + startLine;
         if (endLine != -1) plusStr      += " end line = " + endLine;
         if (limitLines != -1) plusStr   += " limit lines = " + limitLines;
         if (limitRecords != -1) plusStr += " limit records = " + limitRecords;
         if (limitNFiles != -1) plusStr  += " limit files = " + limitNFiles;
         cmd.getLog().dbg (2, "PARSONS", "execute with : " +
                              "oper [" + parsingSource.getOperName () +
                              "] sourceName [" + parsingSource.getSourceName () +
                              "] dbName [" + dbName + "]" + plusStr);
      }

      if (limitNFiles == 0 || limitLines == 0 || limitRecords == 0)
      {
         cmd.getLog().dbg (2, "PARSONS", "nothing to do, limits " + limitNFiles + "/" + limitLines + "/" + limitRecords + " in parsons command");
         return 1;
      }

      // ============ load master pattern agent if any
      //
      // collect all common pattern lines
      //
      Eva evaCommonPattern = new Eva ();
      String [] masterPatOpt = null;
      while ((masterPatOpt = cmd.takeOptionParameters (new String [] { "COMMONPATTERN", "COMMON", "BASEPATTERN", "BASE" }, true)) != null)
      {
         evaCommonPattern.addLine (new EvaLine (masterPatOpt));
      }

      agents = new parsonsAgentSet ();
      agents.commonAgent.parsons = new aLineParsons (evaCommonPattern);

      // load option ANTIPATTERN or IGNORE PATTERN
      //
      Eva evaAntiPattern = new Eva ();
      String [] antiPatOpt = null;
      while ((antiPatOpt = cmd.takeOptionParameters (new String [] { "ANTIPATTERN", "IGNOREPATTERN", "IGNORE"}, true)) != null)
         evaAntiPattern.addLine (new EvaLine (antiPatOpt));

      //NOTE! we use the same commonAgent object for antipatterns (ignore lines)
      //      but this functionality has NOTHING to do with the common or base Agent
      //      if it might be confuse then rewrite the methods and use another object for ignoring lines!
      //!//commonAgent.parsons.setAntiPatternList (evaAntiPattern);
      agents.commonAgent.parsons.setAntiPatternList (evaAntiPattern);

      // ============ load all parsons agents and its patterns
      //

      // System.out.println ("NAgents = "+ Nagents);
      int currAgent = 0;
      String [] remOpt = cmd.getRemainingOptionNames ();

      Eva evaWithPattern = new Eva ();
      Eva evaWithOptColPattern = new Eva ();

      for (int indx = 0; indx < remOpt.length; indx ++)
      {
         String optStr = remOpt[indx];

         //static final String [] AGENT_PATTERN = new String [] { "", "PATT", "PATTERN" };

         int agType = (optStr.equals ("TABLE") || optStr.equals ("DBTABLE")) ? parsonsAgent.DB_TABLE :
                      (optStr.equals ("VARTABLE") || optStr.equals ("EVATABLE")) ? parsonsAgent.EVA_TABLE :
                      (optStr.equals ("VAR") || optStr.equals ("EVA")) ? parsonsAgent.SINGLE_EVA_VALUE : -1;

         //System.out.println ("optStr " + optStr + " is type " + agType);

         if (agType >= parsonsAgent.DB_TABLE)
         {
            // NOTE:
            //     in case of TABLE or VARTABLE solving variables is not desired!
            //         options might contain values to be assigned, for example
            //             ...
            //             , TABLE, tableSalida, CONST, "agId,agTitle", //@<agendaId>, '@<desc>'
            //      if we solve now @<agendaId> and @<desc> all records will have the same value which is not the goal
            //      since these variables usually change during the parsing (see example)
            //
            //    on the other side for agent type VAR we want to solve since we don't have values but the pattern
            //    for example
            //             ...
            //             , VAR, v1, v2, //@<someBegin> (@<pattv1)..(\d*) etc
            //
            String [] agentOptPar =
                    cmd.takeOptionParameters (new String [] { optStr },
                                              (agType == parsonsAgent.SINGLE_EVA_VALUE)); // solve to true if SINGLE VAR since pattern is there!

            if (evaWithPattern.rows () > 0)
            {
               agents.setPatternToLastAgent (evaWithPattern, evaAntiPattern);
               evaWithPattern = new Eva ();
            }
            agents.addAgent (agType, agentOptPar);
         }
         else if (cmd.meantConstantString (optStr, AGENT_PATTERN))
         {
            // collect pattern
            //
            String [] optPar = cmd.takeOptionParameters (AGENT_PATTERN, true);
            if (optPar != null)
            {
               // accumulate pattern
               evaWithPattern.addLine (new EvaLine (optPar));
               //System.out.println ("add pattern for agent of length " + optPar.length);
            }
         }
         else if (cmd.meantConstantString (optStr, AGENT_OPTCOLPATT))
         {
            // collect optional column pattern
            //
            String [] optPar = cmd.takeOptionParameters (AGENT_OPTCOLPATT, true);
            if (optPar != null)
            {
               // accumulate pattern
               evaWithOptColPattern.addLine (new EvaLine (optPar));
               //System.out.println ("add pattern for agent of length " + optPar.length);
            }
         }
         else cmd.getLog().severe ("PARSONS", "unknown option " + indx + "[" + optStr + "] in parsons command! ");
      }

      //
      if (evaWithPattern.rows () > 0 || evaWithOptColPattern.rows () > 0)
      {
         // assign last agent
         //System.out.println ("assign LAST pattern to last agent");
         agents.setPatternsToLastAgent (evaWithPattern, evaWithOptColPattern, evaAntiPattern);
      }

      if (cmd.getArgSize () < 2)
      {
         cmd.getLog().err ("PARSONS", "too few parameters (just " + commands.cols (indxComm) + ")");
         return 1;
      }

      if (!agents.startAgents (cmd.getLog()))
      {
         cmd.getLog().err ("PARSONS", "some error in patterns, no parse performed");
         return 1;
      }

      // check if there is any DBTABLE agent
      //
      boolean someDBTable = false;
      for (int tt = 0; tt < agents.size (); tt ++)
         if (agents.getAgentAt (tt).isTypeDB_TABLE ())
         {
            someDBTable = true;
            break;
         }
      if (explicitDBgiven && !someDBTable)
      {
         cmd.getLog().err ("PARSONS", "Explicit database name is given in command but no DBTABLE agent found, database will not be used.");
      }

      doParse (cmd.getListix (), someDBTable, clean);
      cmd.checkRemainingOptions ();
      return 1;
   }

   private void doParse (listix that, boolean someDBTable, boolean clean)
   {
      //      startLine    = stdlib.atoi (cmd.takeOptionString("STARTLINE", "1"));
      //      endLine      = stdlib.atoi (cmd.takeOptionString("ENDLINE", "-1"));
      //      limitLines   = stdlib.atoi (cmd.takeOptionString("LIMITLINES", "-1"));
      //      limitRecords = stdlib.atoi (cmd.takeOptionString("LIMITRECORDS", "-1"));

      // open DB
      sqlSolver myDB = new sqlSolver ();

      long fileID = (someDBTable) ? prepareDBTables (myDB, clean): -1;
      prepareVariables (that.getGlobalData (), clean);

      boolean multiLinePossible = !agents.commonAgent.hasPatterns ();
      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_START, null); // ... )))

      if (someDBTable)
         myDB.openScript ();

      // fetch the first file in case sourceFolder ...
      parsingSource.openNextSrcFile (myDB, fileID);

      int nFiles = 0;

      do // loop for the case sourceFolder ...
      {
         // here we parse one file (or string or eva-var)
         //

         int nLine = 0;
         int nLinesRead = 0;
         int nRecords = 0;

         String lineStr = "";
         int nLinesIgnored = 0;

         while ((lineStr = parsingSource.getNextLineContentSource ()) != null)
         {
            nLine ++;
            // control optional limits
            //
            if (nLine < startLine) { nLine++; continue; };
            if (endLine != -1 && nLine > endLine)   break;
            if (limitLines != -1 && nLinesRead >= limitLines) break;
            if (limitRecords != -1 && nRecords >= limitRecords) break;

            // Note: all agents would ignore it as well
            if (agents.commonAgent.parsons.ignoreLine (lineStr))
            {
               nLinesIgnored ++;
            }
            else
            {
               // strategy master -> slaves:
               //
               //  first check masther then give rest of line to slaves
               //  can be inefficient in some cases, but should work always
               //
               String remLineStr = lineStr;
               boolean hasRemainingPart = false;

               do
               {
                  hasRemainingPart = false;
                  if (agents.commonAgent.hasPatterns ())
                  {
                     agents.commonAgent.parseLine (remLineStr, nLine);
                     if (!agents.commonAgent.hasRecordCompleted ())
                     {
                        that.log ().dbg (2, "PARSONS", "doParseFile no common part found, go to next line");
                        break;
                     }
                     if (agents.commonAgent.hasRemainingLine ())
                     {
                        that.log ().dbg (2, "PARSONS", "doParseFile common part found");
                        remLineStr = agents.commonAgent.getRemainingLine ();
                        agents.commonAgent.consumeRemainingLine ();
                     }
                     else
                     {
                        that.log ().dbg (2, "PARSONS", "doParseFile common part found but no remaining line!");
                        remLineStr = "";
                     }
                  }

                  //System.out.println ("MATEROSO lineNr " + nLine + " [" + remLineStr + "]");
                  for (int ii = 0; ii < agents.size (); ii ++)
                  {
                     parsonsAgent age = agents.getAgentAt (ii);
                     age.parseLine (remLineStr, nLine);
                     if (age.hasRecordCompleted ())
                     {
                        //System.out.println ("arrAgents  ii " + ii + " [" + remLineStr + "]");
                        // write record
                        writeRecordOfAgent (ii, that, myDB, fileID);
                        nRecords ++;
                        if (multiLinePossible && !age.isTypeSINGLE_EVA_VALUE() && age.hasRemainingLine ())
                        {
                           // NOTE: Variable agents do not consume the line! they just pick their stuff
                           // other agents are allowed to look for multiple records in the same line
                           //
                           remLineStr = age.getRemainingLine ();
                           hasRemainingPart = true;
                           age.consumeRemainingLine ();
                           that.log ().dbg (2, "PARSONS", "doParseFile remaining line [" + remLineStr + "]");
                           //System.out.println ("doParseFile remaining line [" + remLineStr + "]");
                        }
                        //NOT! else remLineStr = "";

                        age.consumeRecord ();
                        agents.commonAgent.consumeRecord ();

                        // agents VAR even if they complete the record does not stop the rest
                        // of the agents to look at the line.
                        // but agent tables, once they consume the line, do
                        //(o) TODO_parsons clarify policy of ownership of records, currently
                        //                a table agent that completes its record owns the record regardless of table name!
                        //                a variable agent - at the begining - gets its data but let the line to be parsed by
                        //                the rest of agents.
                        if (! age.isTypeSINGLE_EVA_VALUE())
                           break;
                     }
                  }
               }
               while (multiLinePossible && hasRemainingPart && remLineStr.length () > 0);
            }

            nLinesRead ++;
            if (sendMessages && (nLine % 500) == 0) Mensaka.sendPacket (LIGHT_MSG_PROGRESS, null); // ... )))
         }
         parsingSource.closeContentSource ();
         that.log ().dbg (2, "PARSONS", "finished parsing file " + nLinesRead + " lines read, " + nLinesIgnored + " lines ignored");

         nFiles ++;
         if (limitNFiles > 0 && nFiles >= limitNFiles) break;

      } while (parsingSource.openNextSrcFile (myDB, ++ fileID));

      if (nFiles > 1)
      {
         that.log ().dbg (2, "PARSONS", "finished parsing " + nFiles + " files");
      }

      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_END, null); // ... )))
      if (someDBTable)
      {
         // Close DB
         myDB.closeScript ();
         myDB.runSQL (dbName);
      }
   }

   private long prepareDBTables (sqlSolver myDB, boolean clean)
   {
      // first check if any agent wants to write on DB
      //
      {
         boolean needDB = false;
         for (int tt = 0; tt < agents.size (); tt ++)
         {
            needDB = true;
            if (agents.getAgentAt (tt).isTypeDB_TABLE ()) break;
            needDB = false;
         }
         if (! needDB) return -1;
      }

      //(o) listix_sql_schemas PARSONS schema creation

      long fileIDret = -1;

      // ensure table for files
      //
      myDB.openScript ();
      if (clean)
         myDB.writeScript ("DROP TABLE IF EXISTS " + PARSONS_FILES_TABLENAME + ";");

      myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + PARSONS_FILES_TABLENAME + " (fileID int, timeParse text, fullPath text, UNIQUE(fileID));");
      myDB.closeScript ();
      myDB.runSQL (dbName);

      // get the fileID
      //
      fileIDret = sqlUtil.getNextID(dbName, PARSONS_FILES_TABLENAME, "fileID", 1000);

      String sourceStr = parsingSource.getSourceNoFolder (myDB);
      // in case sourceFolder the value here has to return null !
      // since we don't want to insert here the file entry but later

      myDB.openScript ();
      if (sourceStr != null)
      {
         myDB.writeScript ("INSERT INTO " + PARSONS_FILES_TABLENAME + " VALUES (" +
                                   fileIDret + ", '" +
                                   myDB.escapeString (DateFormat.getTodayStr ()) + "', '" +
                                   myDB.escapeString (sourceStr) + "');");
      }

      // ensure all tables and variables
      for (int tt = 0; tt < agents.size (); tt ++)
      {
         parsonsAgent age = agents.getAgentAt (tt);
         if (! age.isTypeDB_TABLE ()) continue;

         String taParsons = age.tableName;
         String taAll     = age.tableName + "_all";
         String masFields = agents.commonAgent.getColumnNamesCS ();
         String fieldsCommas =
                 masFields +
                 (masFields.length() > 0 ? " text, ":"") +
                 age.getColumnNamesCS ();

         if (clean)
            myDB.writeScript ("DROP TABLE IF EXISTS " + taParsons + ";");

         myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + taParsons + " (fileID int, lineNr int, lastLineNr int, " + fieldsCommas + ");");
         myDB.writeScript ("CREATE INDEX IF NOT EXISTS " + taParsons + "_indx ON " + taParsons + " (fileID, lineNr);");

         // o-o  Add dbMore connections info
         myDB.writeScript (deepSqlUtil.getSQL_CreateTableConnections ());
         myDB.writeScript (deepSqlUtil.getSQL_InsertConnection("file", taParsons, "fileID", PARSONS_FILES_TABLENAME, "fileID"));

         //12.10.2008 16:48
         // ALTHOUGH it is described in SQLITE DOCUMENTATION the option "CREATE VIEW IF NOT EXISTS" DOES NOT WORK !!!!!
         //
         //myDB.writeScript ("CREATE VIEW IF NOT EXISTS ..
         myDB.writeScript ("DROP VIEW IF EXISTS " + taAll + ";");
         myDB.writeScript ("CREATE VIEW " + taAll + " AS SELECT * FROM " + taParsons + " LEFT JOIN " + PARSONS_FILES_TABLENAME + " USING (fileID) ;");
      }
      myDB.closeScript ();
      myDB.runSQL (dbName);

      return fileIDret;
   }


   protected void prepareVariables (EvaUnit euData, boolean clean)
   {
      // ensure all variables
      for (int tt = 0; tt < agents.size (); tt ++)
      {
         parsonsAgent age = agents.getAgentAt (tt);
         if (age.isTypeEVA_TABLE ())
         {
            Eva var = euData.getSomeHowEva (age.tableName);
            if (clean)
               var.clear ();

            // set the column names
            var.setValue ("fileID"    , 0, 0);
            var.setValue ("lineNr"    , 0, 1);
            var.setValue ("lastLineNr", 0, 2);
            for (int ii = 0; ii < age.getColumnCount (); ii ++)
               var.setValue (age.getColumnName (ii), 0, 3 + ii);
         }
         else
         if (age.isTypeSINGLE_EVA_VALUE ())
         {
            for (int ii = 0; ii < age.getColumnCount (); ii ++)
            {
               Eva var = euData.getSomeHowEva (age.getColumnName (ii));
               if (clean)
                  var.clear ();
            }
         }
      }
   }


   private void writeRecordOfAgent (int agentIndx, listix that, sqlSolver myDB, long fileID)
   {
      parsonsAgent agent = agents.getAgentAt (agentIndx);
      String masterValues = agents.commonAgent.getValuesCS (that);
      String valoresCS = masterValues + (masterValues.length() > 0 ? ", ":"") + agent.getValuesCS (that);

      if (!agents.commonAgent.checkAllValues ())
      {
         that.log ().err ("PARSONS", "not all values filled in record at line " + agent.getFirstAndLastLines () + " for common part");
         // continue anyway
      }
      if (!agent.checkAllValues ())
      {
         that.log ().err ("PARSONS", "not all values filled in record at line " + agent.getFirstAndLastLines () + " for parsons agent " + agentIndx);
         // continue anyway
      }

      that.log ().dbg (5, "PARSONS", "new record at line " + agent.getFirstAndLastLines () + " for parsons agent " + agentIndx + "[" + valoresCS + "]");

      if (agent.isTypeEVA_TABLE ())
      {
         Eva var = that.getGlobalData ().getSomeHowEva (agent.tableName);
         // new line number
         int lineNr = var.rows ();

         var.setValue (fileID + "", lineNr, 0);
         var.setValue (agent.getFirstLine () + "", lineNr, 1);
         var.setValue (agent.getLastLine  () + "", lineNr, 2);

         // set all column values
         for (int ii = 0; ii < agent.getColumnCount (); ii ++)
            var.setValue (agent.getColumnValue (ii), lineNr, 3 + ii);
      }
      else
      if (agent.isTypeSINGLE_EVA_VALUE ())
      {
         for (int ii = 0; ii < agent.getColumnCount (); ii ++)
         {
            Eva var = that.getGlobalData ().getSomeHowEva (agent.getColumnName (ii));
            var.setValue (agent.getColumnValue (ii), 0, 0);
         }
      }
      else if (agent.isTypeDB_TABLE ())
      {
         String insLine = "INSERT INTO " + agent.tableName + " VALUES (" + fileID + ", " + agent.getFirstAndLastLines () + ", " + valoresCS + ");";
         myDB.writeScript (insLine);
         // reset first line
         that.log ().dbg (6, "PARSONS", insLine);
      }

      //(o) TODO_parsons clarify policy of ownership of records (see break at the end of main loop of doParse)
      //         Note that due to a break in doParse a table agent that completes its record owns
      //         the record regardless of table name!

      //
      // ==== RULE TO AVOID CONFLICTS PARSING AGAINST THE SAME TABLE ====
      // the first agent that completes the record wins!
      // the rest of agents completing the same table has to reset its records
      // except the variables that cannot collide with tables
      if (!agent.isTypeSINGLE_EVA_VALUE ())
      {
         for (int p2 = 0; p2 < agents.size (); p2 ++)
         {
            parsonsAgent candi = agents.getAgentAt (p2);
            if (p2 != agentIndx &&
                !candi.isTypeSINGLE_EVA_VALUE () &&
                candi.tableName.equals (agent.tableName))
               candi.parsons.resetRecord ();
         }
      }
   }
}
