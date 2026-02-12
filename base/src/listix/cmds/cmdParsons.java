/*
library listix (www.listix.org)
Copyright (C) 2005-2026 Alejandro Xalabarder Aulet

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

/*
   //(o) WelcomeGastona_source_listix_command PARSONS

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       PARSONS
   <groupInfo>  data_parsers
   <javaClass>  listix.cmds.cmdParsons
   <importance> 4
   <desc>       //Parse a text file and produce records into a database table with the result

   <help>
      //The command PARSONS basically helps to see a text as structured data in form of a table or several tables
      //
      //For example a text in a file mysource.txt containing lines like
      //
      //        year: 1961, name: Patrice Lumumba, land: DR Congo
      //        year: 1972, name: Victor Jara, land: Chile
      //        ...
      //
      //can, using the PARSONS command
      //
      //        PARSONS, FILE, mysource.txt
      //               , TABLE, people
      //               , PATT , year, name, land, // year: (.*), name: (.*), land: (.*)
      //
      //generate a table with the schema
      //
      //        table people (..., year, name, land)
      //
      //List of principal features:
      //
      //      - source text: admits text in form of file, url, content of a variable or a single string
      //      - target table: result can be placed in sqlite database table, a variable or a string
      //      - patterns: patterns are a list of column names followed by a regular expression with capturing groups (parenthesis)
      //      - records multi-line: each output record might be in a single line or splited among several lines
      //      - agents: a text can be parsed at the same time for different tables
      //      - batch: all files of an entire folder can be processed recursively in a single call
      //
      //Schematically the relation between sources, targets and agents can be depicted as
      //
      //       --- Sources ---                                             --- Targets ---
      //
      //                                                ----------           -----------
      //        (folder)                                | Agent  |     ------| DB table|
      //       -----------                              ----------    /      -----------
      //       |txt file |------\                            |       /
      //       -----------       \                           v      /        -----------
      //                          \                     --->[ ]--->o---------|variable |
      //       -----------         \                   /            \        -----------
      //       |variable |----------o-----> Parsons ->o -- ..        \
      //       -----------         /                   \              \      -----------
      //                          /                     \              ------| value   |
      //       -----------       /                       ..                  -----------
      //       | string  |------/
      //       -----------
      //
      //
      //--- Writing patterns for an Agent
      //
      //The PARSON option PATTERN has two parts:
      //          - a list of column names that we want to capture
      //          - a regular expression with the capture groups (content between parenthesis)
      //
      //We give a PATTERN or a list of PATTERNs after starting an Agent (for example after option TABLE)
      //
      //Only if the columns to find are in differnt lines, for example
      //
      //
      //
      //              , TABLE, tablename
      //              ,      , year, // year: ([^,]*),
      //              ,      , name, // name: ([^,]*),
      //              ,      , land, // land: ([^,]*)
      //
      //starts an Agent for filling the table 'tablename'
      //
      //For writting good PARSON PATTERNS knowledge of regular expression rules is very important
      //
      //
      //
      //   PARSONS, EVA, xSource
      //          , CLEAR, 1
      //          , TABLE, tableSalida
      //          ,      , year, // year: ([^,]*),
      //          ,      , name, // name: ([^,]*),
      //          ,      , land, // land: ([^,]*)
      //
      //       orderId: 727   date: 2023/01/10 15:22              orderId   dateTime               total  clientNr   clientName
      //       total: 102                                         -------   ----------------   ---------  --------   ---------------------
      //       client: 2291 COMAXA, SA                              727     2023/01/10 15:22         102     2291    COMAXA, SA
      //                                                            730     2023/01/12 10:04        ....
      //       orderId: 730   date: 2023/01/12 10:04                ...     ...
      //       ...
      //
      //And here a PARSONS command that could be used for this example
      //
      //          PARSONS, FILE, orders2023.txt
      //                 , TABLE, orders
      //                 ,      , orderId, dateTime,    //orderId: (\d*)   date: (.*)
      //                 ,      , total,                //total: (\d*)
      //                 ,      , clientNr, clientName, //client: (\d*) (.*)
      //
      //
      //-- PARSONS Syntaxes
      //
      //Actually there one syntax for each possible source : FOLDER, FILE, VAR, STR
      //but except for the parameters all have practically the same options that configure the parsons AGENTS
      //
      //In general we have the syntax structure
      //
      //    PARSONS, FOLDER|FILE|VAR|STR, ...
      //           , global options
      //           , agent 1 ...
      //           , agent 2 ...
      //           , ...
      //
      //where "souce type, source name" can be :
      //     "FILE, fileName"         for parsing a file
      //     "EVAVAR, variableName"   for parsing a text given in an Eva variable
      //     "STRING, stringToParse"  for parsing directly a string
      //
      //"target database" is the database name to use if some DBTABLE agent is defined
      //
      //The agents defines what to match and how to store the results, the are three possible types:
      //
      //    DBTABLE  : stores the matched groups as records in the given database table
      //    EVATABLE : stores the matched groups as records in the given eva variable
      //    VAR      : stores the matched groups while parsing in the given variables (only stores last value!)
      //
      //table agents has to define in the next lines a parsons pattern which basically defines the column names of the
      //table plus the regular expression pattern to be matched. PATTERN is the default option so the word 'PATTERN'
      //can be omited if desired, the syntax for the option pattern is
      //
      //    , PATTERN, colum1, colum2, ..., columN, regular expression pattern containing n groups
      //
      //For example
      //
      //    PARSONS, ...
      //           , DBTABLE, agenda,
      //           , PATTERN, id, name, telephone, //id:(.*) name:(.*) tel:(.*)
      //
      //will create a table containing the columns "id", "name" and "telephone" and will fill it
      //with the records found.
      //
      //The VAR agent just stores the current value of a matching record. This can be useful for unique records
      //but also for capturing values that are not repeated on each record, typically header contents. In order
      //to do that we need to use the CONST feature of DBTABLE agent.
      //
      //--- CONST content in DBTABLE agent (advanced parsons)
      //The DBTABLE agent can define aditionally a set of columns and its contents that will not come directly
      //from its regular expression matching but from constant strings or eva variables including the ones
      //generated by parsons VAR agents of the same PARSONS command. Among other possibilities it allows
      //the inclusion in a generated table of some header information, for example for parsing the text
      //
      //      orderNr:11012 date:2014.01.01 ...
      //         prodId:18892  units:4
      //         prodId:71782  units:7
      //      orderNr:11042 date:2014.01.03 ...
      //         prodId:11224  units:1
      //
      //we can use two DBTABLE agents, one for the orders and the second for the detail lines
      //and "capture" the important link information between these two tables which is the orderNr
      //
      //    PARSONS, FILE   , orders.txt
      //           , VAR    , orderID, //orderNr:(\d*)
      //           , DBTABLE, orders
      //           ,        , orderID, date, rest, //orderNr:(.*) date:(.*) (.*)
      //           , DBTABLE, orderlines, CONST, orderID, @<orderID>
      //           ,        , prodID, units,       //prodId:(.*) units:(.*)
      //
      //so table orderlines get field orderID through the extra VAR agent for orderNr item.
      //(Note the regular expressions are naiv, not robust enough, for simplicity)
      //
      //
      //--- Regular expressions in PARSONS
      //
      //The key to get something parsed is the ability (or possibility) of finding the right "pattern".
      //For that PARSONS use standard java regular expressions (see java.util.regex.Pattern documentation).
      //
      //A regular expression can describe things like, beginning of line, one or more blanks, containg
      //or not containing this or that character etc. Unfortunately the regular expressions are not
      //preciselly easy to read but they are a proved powerful way of finding matches. To illustrate
      //this briefly, let us see an example. A text containing id, name and telephone in this way
      //
      //       id=9012 name=my first name tel= 111xx1111
      //       id=1334 name=alea tel= 222xx222
      //       ...
      //
      //can be parsed using the pattern "id=(.*) name=(.*) tel=(.*)" where the parenthesis is for what
      //is called "Groups and capturing". PARSONS expect simple groups, one for each field to capture.
      //The point and asterisc '.*' is quite often used and it means : any character (.) one or more
      //times (*). The whole PARSONS command looks like
      //
      //   PARSONS, FILE, text.txt, myDB.db
      //          , TABLE  , tableBook
      //          , PATTERN, ID, NAME, TEL, //id=(.*) name=(.*) tel=(.*)
      //
      //This command will parse the file text.txt and fill the database myDB.db with following records
      //
      //       table parsons_files
      //
      //           fileID  timeParse            fullPath
      //           ------  -------------------  --------------------------
      //           1001    2010-03-14 22:17:45  "path"\text.txt
      //
      //       table tableBook
      //
      //           fileID  lineNr  lastLineNr  ID   NAME            DESC
      //           ------  ------  ----------  ---- --------------  -------------
      //            1001    3        3         9012  my first name  111xx1111
      //            1001    4        4         1334  alea           222xx222
      //
      //Since every file parsed is recorded and get automatically an id, it is possible to parse more
      //files against the same table or parse other tables all within the same database. The range of
      //lines where the record is found is also saved allowing things like openning the file and go to
      //the line etc.
      //
      //Note also that the connection between the two tables (see DEEPDB command) is also automatically
      //done.


   <aliases>
      alias
      PARSER
      PARSE LINES
      LINE PARSER

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //parse the file fileSource
         2   ,    4      , //parse the text given in the variable evaVarName
         3   ,    4      , //parse the given stringContent
         4   ,    4      , //parse the files in rootFolder of given extensions

   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , FILE         ,             , //
         1   , sourceFile   ,             , //Name of text file to be parsed
         1   , targetDbName , (default db), //Database name where to put the result of parsing if some agent DBTABLE is given

         2   , EVAVAR       ,             , //
         2   , evaVarName   ,             , //Name of the eva variable containing the text file to be parsed
         2   , targetDbName , (default db), //Database name where to put the result of parsing if some agent DBTABLE is given

         3   , STR          ,             , //
         3   , stringContent,             , //Content to be parsed as string
         3   , targetDbName , (default db), //Database name where to put the result of parsing if some agent DBTABLE is given

         4   , FOLDER       ,             , //
         4   , rootFolder   ,             , //root folder for files to parse
         4   , targetDbName , (default db), //Database name where to put the result of parsing if some agent TABLE is given
         4   , extensions   ,             , //comma separated extensions to be parsed
         4   , recursive    , 1           , //Value 1 (default) to parse folders recursively, 0 otherwise


   <options>
      synIndx, optionName, parameters, defVal, desc

         x   , IGNORE PATTERN ,  "regExp"            ,,  //Records matching the given pattern will be ignored
         x   , VAR            ,  "varName1, varName2, .., regExp"  , ,//Parse agent that place the results direct in variables. The variables will contain just the last value parsed. This variables are especially useful for expressions that are not repeated (e.g. header information) and can be used in constValues of other DBTABLE agents, thus alloing this information to be stored on each record.
         x   , BASE PATTERN   ,  "field1, .., regExp", , //If given it acts as the common part of all database tables given in the current parsons command. When using BASE, the whole record (base + specific for each table) has to be found in a single line.
         x   , CLEAN          ,  0 / 1     , 0     , //Clear tables if exist before parsing
         x   , LIMIT LINES    ,  number    , -1    , //Maximum of lines to be parsed (default -1 = no limit)
         x   , LIMIT RECORDS  ,  number    , -1    , //Maximum of records to be parsed (default -1 = no limit)
         x   , TRIM FIELDS    ,  0 / 1     , 1     , //Parsed fields or columns will be trimmed from leading and trailing spaces
         x   , START LINE     ,  lineNr    , 1     , //Line number for start parsing
         x   , END LINE       ,  lineNr    , -1    , //Line number for end parsing (default -1 = to the end of file)
         4   , LIMIT FILES    , number    , -1     , //Maximum number of files to be parsed (default -1 = no limit)

         x   , DBTABLE        ,  "tableName, [CONST, constColumns, constValues]"  , ,//Start of a parse agent (pattern(s) has to follow) that will store results as records in the given tableName of the database given as command parameter. Optionally a set of constants and its values might be given, constColumns is a string with all fields comma separated (might be enclosed in quotes) and constValues a SQL expression of the values also comma separated if more than one field is given.
         x   , EVATABLE       ,  "tableName, evaVarName"  , ,//Start of a parse agent (pattern(s) has to follow) that will save its records in the given eva variable used as a table

         x   , PATTERN        ,  "field1, .., regExp", , //Field or column names followed by a capturing group regular expression pattern, used to match results. Several PATTERN lines can be used if the record is to be found in different lines or simply to divide a long pattern.
         x   , OPTCOLPATTERN  ,  "field1, .., regExp", , //Field or column names followed by a capturing group regular expression pattern, used to match results. Several PATTERN lines can be used if the record is to be found in different lines or simply to divide a long pattern.


<!  Idea para capturar textos usando varias líneas
<!
<!         x   , *TEXT FIELD     ,  "field,previousField,pattEnd", , //Por ejemplo "  , TEXT, restoPartida, opening, //^$"  (se inicia después de "opening" y finaliza con linea en blanco

   <examples>
      gastSample
      parsing a text and showing the result
      parsons interactive
      Windows DOS help

   <parsing a text and showing the result>
      //#javaj#
      //
      //   <frames>
      //      Fmain, "listix command PARSONS example", 400, 300
      //
      //   <layout of Fmain>  EVA, 10, 10, 5, 5
      //
      //   ---, X
      //    X , oConsola
      //    X , tResult
      //
      //#listix#
      //
      //   <main>
      //      PARSONS, EVA       , sampleText
      //             , EVATABLE  , tResult
      //             , PATTERN, id, name, tel, //id:(.*) name:(.*) tel:(.*)
      //      -->, tResult data!
      //
      //   <sampleText>
      //      // sample text for demo of PARSONS listix command
      //      //
      //      //    id:71  name:Julian tel:11199029991
      //      //    id:102  name:Evarist tel:111818189292
      //      //    id:43  name:Loretta tel:11118287723
      //      //
      //


   <parsons interactive>
      //#javaj#
      //
      //   <frames>
      //      Fmain, "PARSONS interactive", 600, 500
      //
      //   <layout of Fmain>  EVA, 10, 10, 5, 5
      //
      //   ---, X
      //      , lText to be parsed
      //    X , xSource
      //      , lListix command
      //    X , xLsxCommand
      //      , bGo
      //      , lContents of tableSalida
      //    X , tResult
      //
      //   <sysDefaultFonts>
      //      Tahoma, 12, 0, *
      //      Consolas, 14, 0, TextArea
      //
      //#data#
      //
      //	<xSource>
      //      // sample file for demo of PARSONS listix command
      //      //
      //      // agendaId:5 descrip:trabajo
      //      //    id:71  name:Julian tel:11199029991
      //      //    id:102  name:Evarist tel:111818189292
      //      //
      //      // agendaId:9 descrip:personal
      //      //    id:43  name:Loretta tel:11118287723
      //      //
      //
      //	<xLsxCommand>
      //      //PARSONS, EVA, xSource
      //      //       , CLEAR, 1
      //      //       , VAR  , agendaId, desc, //agendaId:(.*) descrip:(.*)
      //      //       , TABLE, tableSalida, CONST, "agId,agTitle", //@<agendaId>, '@<desc>'
      //      //       ,      , id, name, tel, //id:(.*) name:(.*) tel:(.*)
      //
      //#listix#
      //
      //   <-- bGo>
      //      STRCONV, TEXT-EVA, xLsxCommand, lsxCommandToExecute
      //      LSX, lsxCommandToExecute
      //
      //      -->, tResult data!, sqlSelect, //SELECT * FROM tableSalida
      //

   <Windows DOS help>
      //#javaj#
      //
      //   <frames> fDOSHelp, DOS commands help sample, 700, 500
      //
      //   <layout of fDOSHelp>
      //      EVA, 10, 10, 5, 5
      //
      //         , X
      //       X , sCommands
      //       X , xHelp
      //
      //   <sysDefaultFonts> Consolas, 12, 0, TextArea
      //
      //#data#
      //
      //   <sCommands sqlSelect>
      //      //SELECT cmdName,Description
      //      //    FROM dosCmds
      //      //    WHERE cmdName<>'DISKPART' AND cmdName<>'CHKNTFS' AND cmdName<>'SC';
      //
      //#listix#
      //
      //   <main0>
      //      @<exit if linux>
      //      SETVAR, tmp  , @<:lsx tmp text>
      //      SETVAR, xHelp fileName, @<tmp>
      //      CALL, //CMD /C help > "@<tmp>"
      //      PARSONS, FILE, @<tmp>
      //             , DBTABLE, dosCmds
      //             , PATTERN, cmdName, Description, //^(\w\w*)  \s*(\w.*)
      //
      //   <-- sCommands>
      //      CALL, //CMD /C help @<sCommands selected.cmdName> > "@<tmp>"
      //      MSG, xHelp load
      //
      //   <exit if linux>
      //      CHECK, LINUX
      //      BOX, I, This is a Windows specific sample
      //      MSG, javaj doExit

      #**FIN EVA#
*/

package listix.cmds;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
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
public class cmdParsons implements commandable
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
   private boolean trimFields = true;

   // static final String [] AGENT_HEAD = new String [] { "TABLE", "DBTABLE", "VARTABLE", "EVATABLE", "VAR", "EVA", "OPTVAR" };
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

      String oper = cmd.getArg (0);
      String srcName = cmd.getArg (1);
      boolean explicitDBgiven = cmd.getArg(2).length() > 0;

      dbName = cmd.getListix().resolveDBName (cmd.getArg(2));

      // String  folderSyntaxExtensions = cmd.getArg (3);
      // boolean folderSyntaxRecursive = cmd.getArg (4).length() == 0 ? true: (cmd.getArg (4).charAt(0) == 1);

      // check source
      //
      if (!openContentSource (cmd, oper, srcName))
         return 1;

      startLine = stdlib.atoi (cmd.takeOptionString(new String [] {"STARTLINE", "START", "BEGIN", "BEGINLINE" }, "1"));
      endLine   = stdlib.atoi (cmd.takeOptionString(new String [] {"ENDLINE", "END" }, "-1"));
      limitLines = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITLINES", "MAXLINES", "LINES" }, "-1"));
      limitRecords = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITRECORDS", "MAXRECORDS", "RECORDS" }, "-1"));
      limitNFiles = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITFILES", "MAXFILES", "FILES" }, "-1"));
      trimFields = (1 == stdlib.atoi (cmd.takeOptionString(new String [] {"TRIMFIELDS", "TRIM" }, "1")));

      boolean clean = (1 == stdlib.atoi (cmd.takeOptionString(new String [] {"CLEAN", "CLEAR" }, "-1")));

      if (cmd.getLog().isDebugging (2))
      {
         String plusStr = "";
         if (startLine != 1) plusStr     += " start line = " + startLine;
         if (endLine != -1) plusStr      += " end line = " + endLine;
         if (limitLines != -1) plusStr   += " limit lines = " + limitLines;
         if (limitRecords != -1) plusStr += " limit records = " + limitRecords;
         if (limitNFiles != -1) plusStr  += " limit files = " + limitNFiles;
         cmd.getLog().dbg (2, "PARSONS", "execute with : oper [" + oper + "] sourceName [" + srcName + "] dbName [" +  dbName + "]" + plusStr);
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
            //     in case of TABLE or VARTABLE solving variables is not desired specially for the fourth
            //     parameter if given since it is expressed in variables that has to be solved
            //     in the parsing loop and not at the time of reading the option. For example
            //             ...
            //             , TABLE, tableSalida, CONST, "agId,agTitle", //@<agendaId>, '@<desc>'
            //     variables @<agendaId> and @<desc> change  during the parsing.
            //     Therefore we apply solving for all TABLE option parameters, specially important for parameter 0 (table name) and fourth (column values)
            //
            //    on the other side for agent type VAR we want to solve all parameters since we they may contain
            //    part of the pattern, for example
            //             ...
            //             , VAR, v1, v2, //@<someBegin> (@<pattv1>)..(\d*) etc
            //
            String [] agentOptPar =
                    cmd.takeOptionParameters (new String [] { optStr },
                                              true,
                                              (agType == parsonsAgent.SINGLE_EVA_VALUE) ? -1: 1);

            if (evaWithPattern.rows () > 0 || evaWithOptColPattern.rows () > 0)
            {
               agents.setPatternsToLastAgent (evaWithPattern, evaWithOptColPattern, evaAntiPattern);
               evaWithPattern = new Eva ();
               evaWithOptColPattern = new Eva ();
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

      if (!agents.areAllAgentsValid (cmd.getLog()))
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

   private TextFile             sourceFitxer = null;
   private Eva                  sourceEvavar = null;
   private tableAccessPathFiles sourceFolder = null;
   private int                  sourceCurrentLine = -1;

   private boolean openContentSource (listixCmdStruct cmd, String oper, String scrName)
   {
      sourceFitxer = null;
      sourceEvavar = null;
      sourceFolder = null;
      if (cmd.meantConstantString (oper, new String [] { "FILE", "FILE2DB" }))
      {
         sourceFitxer = new TextFile ();
         if (!sourceFitxer.fopen (scrName, "r"))
         {
            cmd.getLog().err ("PARSONS", "File to scan cannot be opened! [" + scrName + "]");
            sourceFitxer = null;
            sourceCurrentLine = -1;
            return false;
         }
      }
      else if (cmd.meantConstantString (oper, new String [] { "VAR", "EVAVAR", "EVA" }))
      {
         sourceEvavar = cmd.getListix ().getVarEva (scrName);
         if (sourceEvavar == null)
         {
            cmd.getLog().err ("PARSONS", "Variable to scan cannot be found! [" + scrName + "]");
            sourceCurrentLine = -1;
            return false;
         }
      }
      else if (cmd.meantConstantString (oper, new String [] { "FOLDER", "DIR", "FOLDER2DB", "DIR2DB" }))
      {
         // PARSONS, FOLDER, root, dbTarget, extensions, recursive
         //
         String  rootDir = cmd.getArg (1); // same as srcName
         // .............. cmd.getArg (2); // is dbName
         String  extensions = cmd.getArg (3);
         boolean recursive = cmd.getArg (4).length() == 0 ? true: (cmd.getArg (4).charAt(0) == 1);

         sourceFolder = new tableAccessPathFiles ();
         sourceFolder.setCommand (cmd, rootDir, extensions, recursive);
      }
      else if (cmd.meantConstantString (oper, new String [] { "STR", "STRING", "TEXT" }))
      {
         sourceEvavar = new Eva ("intern.string"); // name does not matter!
         sourceEvavar.setValue (scrName, 0, 0);
      }
      sourceCurrentLine = 0;
      return true;
   }

   private boolean openNextSrcFile (listix lsx, sqlSolver myDB, long fileId)
   {
      if (sourceFolder == null) return false;
      if (sourceFolder.EOT ()) return false;

      // get file and from file scanner
      String scrName = sourceFolder.getValue (sourceFolder.currRow, sourceFolder.colOf ("fullPath"));
      sourceFolder.incrementRow ();

      //public static String [] getRecordColumns ()
      //{
      //   return new String [] { "parentPath", "fileName", "extension", "date", "size", "fullPath", "fullParentPath", "fullSubPath" };
      //}

      // fopen this file
      //
      if (sourceFitxer != null)
      {
         sourceFitxer.fclose ();
      }
      sourceFitxer = new TextFile ();
      if (!sourceFitxer.fopen (scrName, "r"))
      {
         lsx.log ().err ("PARSONS", "File to scan cannot be opened! [" + scrName + "]");
         sourceFitxer = null;
         sourceCurrentLine = -1;
         return false;
      }

      // insert it into files table
      //
      myDB.writeScript ("INSERT INTO " + PARSONS_FILES_TABLENAME + " VALUES (" +
                                fileId + ", '" +
                                myDB.escapeString (DateFormat.getTodayStr ()) + "', '" +
                                myDB.escapeString (scrName) + "');");
      return true;
   }

   private String getNextLineContentSource ()
   {
      if (sourceFitxer != null)
      {
         if (sourceFitxer.readLine ())
            return sourceFitxer.TheLine ();
         return null;
      }
      if (sourceEvavar != null)
      {
         if (sourceCurrentLine < sourceEvavar.rows ())
            return sourceEvavar.getValue (sourceCurrentLine ++, 0);
         return null;
      }
      return null;
   }

   private void closeContentSource ()
   {
      if (sourceFitxer != null)
      {
         sourceFitxer.fclose ();
      }
   }

    private void doParse (listix that, boolean someDBTable, boolean clean)
    {
        //      startLine    = stdlib.atoi (cmd.takeOptionString("STARTLINE", "1"));
        //      endLine      = stdlib.atoi (cmd.takeOptionString("ENDLINE", "-1"));
        //      limitLines   = stdlib.atoi (cmd.takeOptionString("LIMITLINES", "-1"));
        //      limitRecords = stdlib.atoi (cmd.takeOptionString("LIMITRECORDS", "-1"));

        // open DB
        sqlSolver myDB = new sqlSolver ();

        long fileID = (someDBTable) ? prepareDBTables (that, myDB, clean): -1;

        prepareVariables (that.getGlobalData (), clean);

        boolean multiLinePossible = !agents.commonAgent.hasPatterns ();
        if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_START, null); // ... )))

        if (someDBTable)
            myDB.openScript ();

        // fetch the first file in case sourceFolder ...
        openNextSrcFile (that, myDB, fileID);

        int nFiles = 0;

        do // loop for the case sourceFolder ...
        {
            // here we parse one file (or string or eva-var)
            //

            int nLine = 0;
            int nLinesRead = 0;
            int nRecords = 0;

            // on each new file we have to reset all the agents (clean incomplete records)
            // included the common pattern
            agents.clearRecords ();

            String lineStr = "";
            int nLinesIgnored = 0;

            while ((lineStr = getNextLineContentSource ()) != null)
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
                                {
                                    // ==== RULE TO AVOID CONFLICTS PARSING AGAINST THE SAME TABLE ====
                                    // the first agent that completes the record wins!
                                    // the rest of agents completing the same table has to reset its records
                                    // except the variables that cannot collide with tables
                                    for (int p2 = 0; p2 < agents.size (); p2 ++)
                                    {
                                        parsonsAgent candi = agents.getAgentAt (p2);
                                        if (!candi.isTypeSINGLE_EVA_VALUE () && candi.tableName.equals (age.tableName))
                                            candi.parsons.resetRecord ();
                                    }
                                    // @TODO we have already cleared the agent records that needed it, now break or not break, this is the question..
                                    // ...to be tested with agents of different tables and remaining line interesting for some other agent...
                                    break;
                                }
                            }
                        }
                    } while (multiLinePossible && hasRemainingPart && remLineStr.length () > 0);
                }

                nLinesRead ++;
                if (sendMessages && (nLine % 500) == 0) Mensaka.sendPacket (LIGHT_MSG_PROGRESS, null); // ... )))
            }
            closeContentSource ();
            that.log ().dbg (2, "PARSONS", "finished parsing file " + nLinesRead + " lines read, " + nLinesIgnored + " lines ignored");

            nFiles ++;
            if (limitNFiles > 0 && nFiles >= limitNFiles) break;

        } while (openNextSrcFile (that, myDB, ++ fileID));

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

   private long prepareDBTables (listix that, sqlSolver myDB, boolean clean)
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

      String sourceStr = sourceFitxer != null ? sourceFitxer.getFileName ():
                         sourceEvavar != null ? (":var " + myDB.escapeString (sourceEvavar.getName ())):
                         sourceFolder != null ? null: "?source";
                         // in case sourceFolder the value here has to return null !
                         // since we don't want to insert here the file entry but later

      myDB.openScript ();
      if (sourceStr != null)
      {
         String insQL = "INSERT INTO " + PARSONS_FILES_TABLENAME + " VALUES (" +
                       fileIDret + ", '" +
                       myDB.escapeString (DateFormat.getTodayStr ()) + "', '" +
                       myDB.escapeString (sourceStr) + "');";
         myDB.writeScript (insQL);
         that.log ().dbg (6, "PARSONS", insQL);
      }

      // ensure all tables and variables
      for (int tt = 0; tt < agents.size (); tt ++)
      {
         parsonsAgent age = agents.getAgentAt (tt);
         if (! age.isTypeDB_TABLE ()) continue;

         String insSQL = "";
         String taParsons = age.tableName;
         String taAll     = age.tableName + "_all";
         String masFields = agents.commonAgent.getColumnNamesCS ();

        for (int ia = 0; ia < agents.commonAgent.getColumnCount (); ia ++)
        {
            parsonsColumn col = agents.commonAgent.getColumn (ia);
        }


         String fieldsCommas =
                 masFields +
                 (masFields.length() > 0 ? " text, ":"") +
                 age.getColumnNamesCS ();

         if (clean)
            myDB.writeScript ("DROP TABLE IF EXISTS " + taParsons + ";");

         myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + taParsons + " (fileID int, lineNr int, lastLineNr int, " + fieldsCommas + ");");
         insSQL = "CREATE INDEX IF NOT EXISTS " + taParsons + "_indx ON " + taParsons + " (fileID, lineNr);";
         myDB.writeScript (insSQL);
         that.log ().dbg (6, "PARSONS", insSQL);

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
            Eva evar = euData.getSomeHowEva (age.tableName);
            if (clean)
               evar.clear ();

            // set the column names
            evar.setValue ("fileID"    , 0, 0);
            evar.setValue ("lineNr"    , 0, 1);
            evar.setValue ("lastLineNr", 0, 2);
            int colNr = 0;
            for (int ii = 0; ii < age.getColumnCount (); ii ++)
            {
               parsonsColumn col = age.getColumn (ii);
               if (col.isRegular ())
               {
                  evar.setValue (col.getName (), 0, 3 + colNr);
                  colNr ++;
               }
            }
         }
         else
         if (age.isTypeSINGLE_EVA_VALUE ())
         {
            for (int ii = 0; ii < age.getColumnCount (); ii ++)
            {
               parsonsColumn col = age.getColumn (ii);
               if (col.isRegular ())
               {
                  Eva evar = euData.getSomeHowEva (col.getName ());
                  if (clean)
                     evar.clear ();
               }
            }
         }
      }
   }


   private void writeRecordOfAgent (int agentIndx, listix that, sqlSolver myDB, long fileID)
   {
      parsonsAgent agent = agents.getAgentAt (agentIndx);
      String masterValues = agents.commonAgent.getValuesCS (that, trimFields);
      String valoresCS = (masterValues.length() > 0 ? masterValues + ", ":"") +
                         agent.getValuesCS (that, trimFields);

      if (!agents.commonAgent.checkAllValues ())
      {
         that.log ().err ("PARSONS", "value for [" +  agents.commonAgent.firstUnfilledColumn () + "] not filled in record between lines " + agent.getFirstAndLastLines () + " for common part");
         // continue anyway
      }
      if (!agent.checkAllValues ())
      {
         that.log ().err ("PARSONS", "value for [" +  agent.firstUnfilledColumn () + "] not filled in record between lines " + agent.getFirstAndLastLines () + " for parsons agent " + agentIndx);
         // continue anyway
      }

      that.log ().dbg (5, "PARSONS", "new record at line " + agent.getFirstAndLastLines () + " for parsons agent " + agentIndx + "[" + valoresCS + "]");

      if (agent.isTypeEVA_TABLE ())
      {
         Eva evar = that.getGlobalData ().getSomeHowEva (agent.tableName);
         // new line number
         int lineNr = evar.rows ();

         evar.setValue (fileID + "", lineNr, 0);
         evar.setValue (agent.getFirstLine () + "", lineNr, 1);
         evar.setValue (agent.getLastLine  () + "", lineNr, 2);

         // set all column values
         int colNr = 0;
         for (int ii = 0; ii < agent.getColumnCount (); ii ++)
         {
            parsonsColumn col = agent.getColumn (ii);
            if (col.isRegular ())
            {
               evar.setValue (col.getValue (), lineNr, 3 + colNr);
               colNr ++;
            }
         }
      }
      else if (agent.isTypeSINGLE_EVA_VALUE ())
      {
         for (int ii = 0; ii < agent.getColumnCount (); ii ++)
         {
            parsonsColumn col = agent.getColumn (ii);
            if (col.isRegular ())
            {
               Eva evar = that.getGlobalData ().getSomeHowEva (col.getName ());
               evar.setValue (col.getValue (), 0, 0);
            }
         }
      }
      else if (agent.isTypeDB_TABLE ())
      {
         String insLine = "INSERT INTO " + agent.tableName + " VALUES (" + fileID + ", " + agent.getFirstAndLastLines () + ", " + valoresCS + ");";
         myDB.writeScript (insLine);
         // reset first line
         that.log ().dbg (6, "PARSONS", insLine);
      }
   }
}

// alternative diagram
//
//    --- Sources ---                                      --- Targets ---
//
//   -----------                          ----------           -----------
//   |txt file |------\                   | Agent  |     ------| DB table|
//   -----------       \                  ----------    /      -----------
//                      \                      |       /
//   -----------         \                     v      /        -----------
//   |variable |----------o-----> Parsons --->[ ]--->o---------|variable |
//   -----------         /                            \        -----------
//                      /                              \
//   -----------       /                                \      -----------
//   | string  |------/                                  ------| value   |
//   -----------                                               -----------
//
//
