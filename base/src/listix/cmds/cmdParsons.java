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
   <groupInfo>  system_process
   <javaClass>  listix.cmds.cmdParsons
   <importance> 4
   <desc>       //Parse a text file and produce records into a database table with the result

   <help>
      //
      // This command is a parser that stores the results as records into a database table. Basically
      // you give it a text file and the rules to find a single record (pattern), the rest is automatically done.
      // The direction "text file" to database is indicated in the first option FILE2DB, which is right
      // now the only way to use PARSONS.
      //
      // For example, to parse in a simple way a file containing a comma separated table (or comma
      // separated values CSV) we could write
      //
      //    PARSONS, FILE2DB, myFile.csv, myDB.db, myResults
      //           , PATTERN, id, name, desc, //(.*),(.*),(.*)
      //
      // this commad will fill the table myResults_parsons of the database myDB.db with the records
      // found in the source file myFile.csv. Where 'id', 'name' and 'desc' are the column names
      // that the table sample_parsons will have and "(.*),(.*),(.*)" is the pattern to parse (see
      // "Regular expressions in Parsons" close below).
      //
      // The option PATTERN is basically a list of column names and a group regular expression pattern
      // (explained below) containing as many groups (expression enclosed in parentesis) as column are
      // given.
      //
      // Multiple patterns can be given in a single PARSONS call, this can be used when the record
      // information is not placed in the same line in the file to be parsed.
      //
      // Syntax of the option PATTERN
      // ------------------------------------------------
      //
      //       PATTERN, colum1, colum2, ..., columN, regular expression pattern containing n groups
      //       ..
      //
      // "columnX" can be a column name for the result table or one of these modifiers
      //
      //    modifier  meaning for the following columns to the modifier
      //    --------  ----------------------------------------------------
      //    :keep     columns afected are not mandatory and the value once set is kept, that is
      //              a new record does not reset the value. This modifier is useful for parsing
      //              headers or values that appear only on change.
      //
      //    :optional columns afected are not mandatory, therefore a record can be concluded
      //              without having found a match of it.
      //
      //
      // Regular expresions in PARSONS
      // ------------------------------------------------
      //
      // The key to get something parsed is the ability (or possibility) of finding the right "pattern".
      // For that PARSONS use standard java regular expressions (see java.util.regex.Pattern documentation).
      //
      // A regular expression can describe things like, beginning of line, one or more blanks, containg
      // or not containing this or that character etc. Unfortunatelly the regular expressions are not
      // preciselly easy to read but they are a proved powerful way of finding matches. To illustrate
      // this briefly, let us see an example. A text containing id, name and telephone in this way
      //
      //       id=9012 name=my first name tel= 111xx1111
      //       id=1334 name=alea tel= 222xx222
      //       ...
      //
      // can be parsed using the pattern "id=(.*) name=(.*) tel=(.*)" where the parenthesis is for what
      // is called "Groups and capturing". PARSONS expect simple groups, one for each field to capture.
      // The point and asterisc '.*' is quite often used and it means : any character (.) one or more
      // times (*). The whole PARSONS command looks like
      //
      //   PARSONS, FILE2DB, text.txt, myDB.db, sample
      //          , PATTERN, ID, NAME, TEL, //id=(.*) name=(.*) tel=(.*)
      //
      // This command will parse the file text.txt and fill the database myDB.db with following records
      //
      //       table sample_files
      //
      //           fileID  timeParse            fullPath
      //           ------  -------------------  --------------------------
      //           1001    2010-03-14 22:17:45  "path"\text.txt
      //
      //       table sample_parsons
      //
      //           fileID  lineNr  lastLineNr  ID   NAME            DESC
      //           ------  ------  ----------  ---- --------------  -------------
      //            1001    3        3         9012  my first name  111xx1111
      //            1001    4        4         1334  alea           222xx222
      //
      // Since every file parsed is recorded and get automatically an id, it is possible to parse more
      // files against the same table or parse other tables all within the same database. The range of
      // lines where the record is found is also saved allowing things like openning the file and go to
      // the line etc.
      //
      // Note also that the connection between the two tables (see command DBMORE) is also automatically
      // done.


   <aliases>
      alias
      PARSE LINES
      LINE PARSER

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //parse the file fileSource and set the result on database dbName on tables 'tablePrefix'_files and 'tablePrefix'_parsons

   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , FILE2DB      ,             , //
         1   , sourceFile   ,             , //Name of text file to be parsed
         1   , targetDbName , (default db), //Database name where to put the result of parsing
         1   , tablePrefix  , parsons     , //Optional prefix for the result tables ('prefix'_files and 'prefix'_parsons)
         1   , recordPatternRef,          , //Eva name where the Pattern is to be found (pattern given in an Eva variable)

         2   , FILE2DB      ,             , //
         2   , sourceFile   ,             , //Name of text file to be parsed
         2   , targetDbName , (default db), //Database name where to put the result of parsing
         2   , tablePrefix  , parsons     , //Optional prefix for the result tables ('prefix'_files and 'prefix'_parsons)
         2   , field1       ,             , //Field names to be found in the given recordPattern (e.g. userName, Telephone)
         2   , ...          ,             , //
         2   , recordPattern,             , //Java Pattern (see java.util.regex.Pattern) containing as much groups as fields defined (e.g. "user:(.*) tel:(.*)")

<!         3   , INIT MULTI    ,             , //
<!         3   , targetFileBase,             , //default "parsons" will generate parsons_files.txt parsons_parsons.txt

<!         4   , MULTI        ,             , //
<!         4   , sourceFile   ,             , //Name of text file to be parsed
<!         4   , recordPatternRef,          , //Eva name where the Pattern is to be found (pattern given in an Eva variable)

   <options>
      synIndx, optionName, parameters, defVal, desc

         1   , PATTERN        ,  "field1, .., regExp", , //Field names followed by a group regular expression pattern, used to parse contents
         1   , START LINE     ,  lineNr    , 1     , //Line number for start parsing
         1   , END LINE       ,  lineNr    , -1    , //Line number for end parsing (-1 = to the end of file)
         1   , LIMIT LINES    ,  number    , -1    , //Maximum of lines to be parsed (-1 = no limit)
         1   , LIMIT RECORDS  ,  number    , -1    , //Maximum of records to be parsed (-1 = no limit)
         1   , CLEAN          ,  0 / 1     , 0     , //Clear tables if exist before parsing
         1   , *TEXT FIELD     ,  field,previousField,pattEnd, , //Por ejemplo "  , TEXT, restoPartida, opening, //^$"  (se inicia después de "opening" y finaliza con linea en blanco

         2   , PATTERN        ,  "field1, .., regExp", , //Field names followed by a group regular expression pattern, used to parse contents
         2   , START LINE     ,  lineNr    , 1     , //Line number for start parsing
         2   , END LINE       ,  lineNr    , -1    , //Line number for end parsing (-1 = to the end of file)
         2   , LIMIT LINES    ,  number    , -1    , //Maximum of lines to be parsed (-1 = no limit)
         2   , LIMIT RECORDS  ,  number    , -1    , //Maximum of records to be parsed (-1 = no limit)
         2   , *TEXT FIELD     ,  field,previousField,pattEnd, , //Por ejemplo "  , TEXT, restoPartida, opening, //^$"  (se inicia después de "opening" y finaliza con linea en blanco

   <examples>
      gastSample
      parsing and showing result
      Windows DOS help
      Windows NET help

   <parsing and showing result>
      //#javaj#
      //
      //   <frames>
      //      Fmain, "listix command PARSONS example", 400, 300
      //
      //   <layout of Fmain>  EVA, 10, 10, 5, 5
      //
      //   ---, X
      //      , bGo
      //    X , oConsola
      //    X , tResult
      //
      //#listix#
      //
      //   <main0>
      //      SETVAR, tmptxt, @<:lsx tmp>
      //
      //   <-- bGo>
      //      //Generating sample file with text to be parsed ...
      //      //
      //      GEN, @<tmptxt>, sampleFile
      //      //Parsing file ...
      //      //
      //      PARSONS, FILE2DB, @<tmptxt>,, test
      //             , PATTERN, id, name, tel, //id:(.*) name:(.*) tel:(.*)
      //      //Done.
      //      //A look into the tables ...
      //      GEN,, lookAtDB (db), listix, META-GASTONA/utilApp/std/lookAtDB.lsx
      //         , PARAMS, ""
      //      //
      //      -->, tResult, sqlSelect, //SELECT * FROM test_parsons ORDER BY id+0;
      //
      //   <sampleFile>
      //      // sample file for demo of PARSONS listix command
      //      //
      //      //    id:71  name:Julian tel:11199029991
      //      //    id:102  name:Evarist tel:111818189292
      //      //    id:43  name:Loretta tel:11118287723
      //      //
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
      //   <sysDefaultFonts> Courier, 12, 0, TextArea
      //
      //#data#
      //
      //   <sCommands sqlSelect>
      //      //SELECT cmdName,Description
      //      //    FROM dosCmds_parsons
      //      //    WHERE cmdName<>'DISKPART' AND cmdName<>'CHKNTFS' AND cmdName<>'SC';
      //
      //#listix#
      //
      //   <main0>
      //      @<exit if linux>
      //      SETVAR, tmp  , @<:lsx tmp text>
      //      SETVAR, xHelp fileName, @<tmp>
      //      CALL, //CMD /C help > "@<tmp>"
      //      PARSONS, FILE2DB, @<tmp>,, dosCmds
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


   <Windows NET help>
      //
      // <! Note: this sample actually does not use PARSONS, is just demonstrates how
      // <!       to do something similar to "Windows DOS help" sample without PARSONS
      //
      //
      //#javaj#
      //
      //   <frames> fNETHelp, NET commands help, 700, 500
      //
      //   <layout of fNETHelp>
      //      EVA, 10, 10, 5, 5
      //
      //         ,          ,  X
      //       X , tCommands, xHelp
      //
      //   <sysDefaultFonts>  Courier, 12, 0, TextArea
      //
      //#data#
      //
      //
      //   <tCommands>
      //      name
      //
      //      NAMES
      //      SERVICES
      //      SYNTAX
      //      ""
      //      ACCOUNTS
      //      COMPUTER
      //      CONFIG
      //      CONFIG SERVER
      //      CONFIG WORKSTATION
      //      CONTINUE
      //      FILE
      //      GROUP
      //      HELP
      //      HELPMSG
      //      LOCALGROUP
      //      NAME
      //      PAUSE
      //      PRINT
      //      SEND
      //      SESSION
      //      SHARE
      //      START
      //      STATISTICS
      //      STOP
      //      TIME
      //      USE
      //      USER
      //      VIEW
      //
      //#listix#
      //
      //   <main0>
      //      @<exit if linux>
      //      SETVAR, tmp  , @<:lsx tmp text>
      //      SETVAR, xHelp fileName, @<tmp>
      //
      //   <exit if linux>
      //      CHECK, LINUX
      //
      //      BOX, I, This is a Windows specific sample
      //      MSG, javaj doExit
      //
      //   <-- tCommands>
      //      -->, eCommand,, @<tCommands selected.name>
      //      @<help for command>
      //
      //   <-- eCommand>
      //      @<help for command>
      //
      //   <help for command>
      //      CALL, //CMD /C net help @<eCommand> > "@<tmp>"
      //      MSG, xHelp load

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
      Scans a file and places the result either in an Eva variable or into a table of a sqlite database


      Example 1:
      ----------------------------
      FILE TO SCAN---------------------------------
         Filename: _5223032.orf
         Timestamp: Tue May 22 18:45:41 2007
         Camera: OLYMPUS E-500
         ISO speed: 100
         Shutter: 1/30.0 sec
         Aperture: f/3.5
         Focal length: 14.0 mm
         Filter pattern: RGGBRGGBRGGBRGGB
         Daylight multipliers: 1.838447 0.946109 1.111843
         Camera multipliers: 2.164062 1.000000 1.265625 0.000000
      -----------------------------------------------------------

      <patternFieldMap>

         fileName   , //FileName: (.*)
         time       , //Timestamp: (.*)
         Camera     , //Camera: (.*)
         ISO        , //ISO speed: (.*)

         shutter, units, //Shutter: (.*) (.*)

         aperture   , //Aperture: (.*)
         focalLength, //Focal length: (.*) mm
         filterPatt , //Filter pattern: (.*)

         dayMultR, dayMultG, dayMultB,           //Daylight multipliers: (.*) (.*) (.*)
         camMultR, camMultG, camMultB, camMultM, //Camera multipliers: (.*) (.*) (.*) (.*)

      RESULT EVA ---------------------------------------

      <resultEva>

         fileName, time, camera, ISO, shutter, units, aperture, focalLength, filterPatt, dayMultR, dayMultG, dayMultB, camMultR, camMultG, camMultB, camMultM
         _5223032.orf, "Tue May 22 18:45:41 2007", "OLYMPUS E-500", 100, "1/30.0", "sec", "f/3.5", "14.0", "RGGBRGGBRGGBRGGB", 1.838447, 0.946109, 1.111843, 2.164062, 1.000000, 1.265625, 0.000000
      --------------------------------------------------


      Example 2:
      ----------------------------
      FILE TO SCAN---------------------------------

         AREA=Chorradas
         1-Ergocios TABO 2-Arkancios TABO 3-Jumehelos TABO soy yo happy
         1-Akoma TABO 2-Orhancio TABO 3-Cohorcio TABO se acaba

         AREA=Finalmente

         1-Suso TABO 2-Sisi TABO 3-Erkomar TABO finalmentes
      -----------------------------------------------------------

      <fixNumber> 4430

      <patternFieldMap>
         *fileName, fileName
         *line,     scanLine
         fixNumber,      //
         area,           //^AREA=(.*)
         c1, c2, c3, c4, //^1-(.*) TABO 2-(.*) TABO 3-(.*) TABO (.*)


      -- special fields starting with *
         fileName: file name as given in command
         fullPath: fullpath filename (canonical)
         lineNumber: current line number of file (for text files)
         byteNumber: current byte number of file (for binary files)

      -- fields not produced by scan
         simply write its name and as pattern just // (empty string)



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
   private static MessageHandle LIGHT_MSG_START     = null; // new MessageHandle (); // "_lib parsons_start"
   private static MessageHandle LIGHT_MSG_PROGRESS  = null; // new MessageHandle (); // "_lib parsons_progresss"
   private static MessageHandle LIGHT_MSG_END       = null; // new MessageHandle (); // "_lib parsons_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   private static boolean initOnce_msgHandles ()
   {
      if (LIGHT_MSG_START == null)
      {
         LIGHT_MSG_START     = new MessageHandle ();
         LIGHT_MSG_PROGRESS  = new MessageHandle ();
         LIGHT_MSG_END       = new MessageHandle ();

         // this messages are not mandatory to be suscribed, the are provided just as internal information of parser command
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

   private String oper        = null;
   private String fileSource  = null;
   private String dbName      = null;
   private String tablePrefix = null;

   private int startLine = 1;
   private int endLine   = -1;
   private int limitLines = -1;
   private int limitRecords = -1;

   private aLineParsons parsons = null;

   private long fileID = -1;

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      oper        = cmd.getArg(0);
      fileSource  = cmd.getArg(1);
      dbName      = cmd.getArg(2);
      tablePrefix = cmd.getArg(3);

      if (dbName.length () == 0)
         dbName = cmd.getListix ().getDefaultDBName ();

      String  customFile = cmd.takeOptionString(new String [] { "FROMFILE", "FILE" }, "" );

      startLine = stdlib.atoi (cmd.takeOptionString(new String [] {"STARTLINE", "START", "BEGIN", "BEGINLINE" }, "1"));
      endLine   = stdlib.atoi (cmd.takeOptionString(new String [] {"ENDLINE", "END" }, "-1"));
      limitLines = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITLINES", "MAXLINES", "LINES" }, "-1"));
      limitRecords = stdlib.atoi (cmd.takeOptionString(new String [] {"LIMITRECORDS", "MAXRECORDS", "RECORDS" }, "-1"));

      boolean clean = (1 == stdlib.atoi (cmd.takeOptionString(new String [] {"CLEAN", "CLEAR" }, "-1")));

      if (cmd.getLog().isDebugging (2))
      {
         String plusStr = "";
         if (startLine != 1) plusStr     += " start line = " + startLine;
         if (endLine != -1) plusStr      += " end line = " + endLine;
         if (limitLines != -1) plusStr   += " limit lines = " + limitLines;
         if (limitRecords != -1) plusStr += " limit records = " + limitRecords;
         cmd.getLog().dbg (2, "PARSONS", "execute with : oper [" + oper + "] fileSource [" + fileSource + "] dbName [" +  dbName + "]" + plusStr);
      }

      // load option PATTERN
      //
      Eva evaWithPattern = new Eva ();
      String [] optPar = null;
      while ((optPar = cmd.takeOptionParameters (new String [] { "PATT", "PATTERN"}, true)) != null)
      {
         // solve all values
         for (int ii = 0; ii < optPar.length; ii ++)
            optPar[ii] = that.solveStrAsString (optPar[ii]);

         // add it to eva
         evaWithPattern.addLine (new EvaLine (optPar));
      }

      int nParams = commands.cols (indxComm);

      if (cmd.getArgSize () < 5)
      {
         // syntax : FILE2DB .... and pattern via options
         //          "PARSONS", "FILE2DB", "fileSource", "dbName", [ "tablePrefix" ]
         //
         parsons = new aLineParsons(evaWithPattern);
      }
      else if (cmd.getArgSize () >= 5)
      {
         if (!oldFashion (cmd)) return 1;
      }
      else
      {
         cmd.getLog().err ("PARSONS", "too few parameters (just " + commands.cols (indxComm) + ")");
         return 1;
      }

      if (parsons.init ())
         doParseFile (cmd.getListix (), clean);
      else
         cmd.getLog().err ("PARSONS", "some error in pattern, no parse performed");

      cmd.checkRemainingOptions (true);
      return 1;
   }

   private boolean oldFashion (listixCmdStruct cmd)
   {
      cmd.getLog().err ("PARSONS", "The syntax used in command PARSONS is DEPRECATED, use the option PATTERN instead!");
      if (cmd.getArgSize () == 5)
      {
         // syntax : FILE2DB ...., "recordPatternVar"
         //          "PARSONS", "FILE2DB", "fileSource", "dbName", "tablePrefix", "recordPatternVar"
         //
         String evaref = cmd.getArg(4);
         Eva evaWithPattern = cmd.getListix ().getVarEva (evaref);
         if (evaWithPattern == null)
         {
            cmd.getLog().err ("PARSONS", "expected eva variable [" + evaref + "] with parsons pattern but not found!");
            return false;
         }

         // solve all patterns
         int fieldNo = 0;
         for (int ii = 0; ii < evaWithPattern.rows (); ii ++)
         {
            int last = evaWithPattern.get(ii).cols () - 1;
            String solved = cmd.getListix ().solveStrAsString (evaWithPattern.getValue(ii, last));
            evaWithPattern.setValue (solved, ii, last);
            cmd.getLog().dbg (2, "PARSONS", "pattern fields [" + fieldNo + " to " + (fieldNo + last) + "] = [" + solved + "]");
         }

         if (evaWithPattern == null)
         {
            cmd.getLog().err ("PARSONS", "patternRef \"" + evaref + "\" not found!");
//            evaWithPattern = new Eva ("");
//            evaWithPattern.addLine (new EvaLine (new String [] {"all", "(.*)" }));
         }
         else if (evaWithPattern.rows () == 0)
         {
            cmd.getLog().err ("PARSONS", "patternRef \"" + evaref + "\" contain no patterns!");
         }
         else
         {
            cmd.getLog().dbg (2, "PARSONS", "pattern given in eva variable [" + evaWithPattern + "]");
            parsons = new aLineParsons(evaWithPattern);
         }
      }
      else if (cmd.getArgSize () > 5)
      {
         // syntax : FILE2DB ...., field1, field2, ..., "recordPattern"
         //          "PARSONS", "FILE2DB", "fileSource", "dbName", "tablePrefix", "field1, field2, ...", "recordPattern" } )); break;

         cmd.getLog().dbg (2, "PARSONS", "pattern given in parameters");
//    0  PARSONS,
//    1  FILE2DB,
//    2  sample2Parse.txt,
//    3  @<db>,
//    4  test1,
//    5  id,
//    6  name,
//    7  tel,
//    8 //id:(.*) name:(.*) tel:(.*)

         String [] fields = new String [cmd.getArgSize () - 4 - 1]; // - (FILE2DB + filesource + dbName + tablePrefic) - (recordPattern)

         for (int ii = 4; ii < cmd.getArgSize () - 1; ii ++)
         {
            fields [ii - 4] = cmd.getArg(ii);
            cmd.getLog().dbg (2, "PARSONS", "pattern field [" + (ii-4) + "] = [" + fields [ii-4] + "]");
         }
         String patt = cmd.getArg (cmd.getArgSize () - 1);
         cmd.getLog().dbg (2, "PARSONS", "pattern = [" + patt + "]");

         // construct parsons
         //
         // public void addFieldsPatternMap (String pattern, String [] fields)
         parsons = new aLineParsons();
         parsons.addFieldsPatternMap (patt, fields);
      }

      return true;
   }

   private void doParseFile (listix that, boolean clean)
   {
      if (fileSource == null || fileSource.length () == 0)
      {
         that.log ().err ("PARSONS", "File to scan not found! [" + fileSource + "]");
         return;
      }

      //      startLine    = stdlib.atoi (cmd.takeOptionString("STARTLINE", "1"));
      //      endLine      = stdlib.atoi (cmd.takeOptionString("ENDLINE", "-1"));
      //      limitLines   = stdlib.atoi (cmd.takeOptionString("LIMITLINES", "-1"));
      //      limitRecords = stdlib.atoi (cmd.takeOptionString("LIMITRECORDS", "-1"));


      //System.out.println ("working with file" + fileSource);
      TextFile fix = new TextFile ();
      if (!fix.fopen (fileSource, "r"))
      {
         that.log ().err ("PARSONS", "File to scan not found! [" + fileSource + "]");
         return;
      }

      // file is open, start to parse!
      //

      // open DB
      sqlSolver myDB = new sqlSolver ();

      String fieldsCommas = "";
      for (int ii = 0; ii < parsons.getCurrentRecord().length; ii ++)
      {
         fieldsCommas += ((ii > 0) ? ", ":"") + parsons.getCurrentRecord()[ii].name;
      }

      String taFiles   = tablePrefix + "_files";
      String taParsons = tablePrefix + "_parsons";
      String taAll     = tablePrefix + "_all";

      //(o) listix_sql_schemas PARSONS FILE2DB schema creation

      // ensure tables
      myDB.openScript ();
      if (clean)
      {
         myDB.writeScript ("DROP TABLE IF EXISTS " + taFiles + ";");
         myDB.writeScript ("DROP TABLE IF EXISTS " + taParsons + ";");
         myDB.writeScript ("DROP TABLE IF EXISTS " + taParsons + ";");
      }
      myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + taFiles + " (fileID, timeParse, fullPath, UNIQUE(fileID));");
      myDB.writeScript ("CREATE TABLE IF NOT EXISTS " + taParsons + " (fileID, lineNr, lastLineNr, " + fieldsCommas + ");");
      myDB.writeScript ("CREATE INDEX IF NOT EXISTS " + taParsons + "_indx ON " + taParsons + " (fileID, lineNr);");

      // o-o  Add dbMore connections info
      myDB.writeScript (dbMore.getSQL_CreateTableConnections ());
      myDB.writeScript (dbMore.getSQL_InsertConnection("file", taParsons, "fileID", taFiles, "fileID"));

      //12.10.2008 16:48
      // ALTHOUGH it is described in SQLITE DOCUMENTATION the option "CREATE VIEW IF NOT EXISTS" DOES NOT WORK !!!!!
      //
      //myDB.writeScript ("CREATE VIEW IF NOT EXISTS ..
      myDB.writeScript ("DROP VIEW IF EXISTS " + taAll + ";");
      myDB.writeScript ("CREATE VIEW " + taAll + " AS SELECT * FROM " + taParsons + " LEFT JOIN " + taFiles + " USING (fileID) ;");

      myDB.closeScript ();
      myDB.runSQL (dbName);

      fileID = sqlUtil.getNextID(dbName,  tablePrefix + "_files", "fileID", 1000);
      myDB.openScript ();
      myDB.writeScript ("INSERT INTO " + taFiles + " VALUES (" + fileID + ", '" + myDB.escapeString (DateFormat.getTodayStr ()) + "', '" + fileSource + "');");
      myDB.closeScript ();
      myDB.runSQL (dbName);

      int nLine = 1;
      int nLinesRead = 0;
      int nRecords = 0;

      myDB.openScript ();

      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_START, null); // ... )))

      String lineStr = "";
      int firstLineNrOfRecord = -1;
      while (fix.readLine ())
      {
         // control optional limits
         //
         if (nLine < startLine) { nLine++; continue; };
         if (endLine != -1 && nLine > endLine)   break;
         if (limitLines != -1 && nLinesRead >= limitLines) break;
         if (limitRecords != -1 && nRecords >= limitRecords) break;

         lineStr = fix.TheLine ();
         while (lineStr.length() > 0)
         {
            int indxCol = parsons.scan (lineStr);

            //detect first line of record
            //
            if (firstLineNrOfRecord == -1 && indxCol > 0)
               firstLineNrOfRecord = nLine;

            if (parsons.recordComplete ())
            {
               nRecords ++;
               that.log ().dbg (5, "PARSONS", "new record at line " + nLine);
               String camphos = "";
               for (int jj = 0; jj < parsons.getCurrentRecord().length; jj ++)
               {
                  that.log ().dbg (5, "PARSONS", parsons.getCurrentRecord()[jj].name + " = [" + parsons.getCurrentRecord()[jj].value + "]");
                  camphos += ((jj==0)? "": ",") + "'" + myDB.escapeString (parsons.getCurrentRecord()[jj].value) + "'";
               }

               String insLine = "INSERT INTO " + tablePrefix + "_parsons VALUES (" + fileID + ", " + firstLineNrOfRecord + ", " + nLine + ", " + camphos + ");";
               myDB.writeScript (insLine);

               // reset first line
               firstLineNrOfRecord = -1;
               that.log ().dbg (6, "PARSONS", insLine);
            }
            if (indxCol > 0)
            {
               lineStr = lineStr.substring (indxCol);
            }
            else lineStr = "";
         }
         nLine ++;
         nLinesRead ++;
         if (sendMessages && (nLine % 500) == 0) Mensaka.sendPacket (LIGHT_MSG_PROGRESS, null); // ... )))
      }
      fix.fclose ();

      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_END, null); // ... )))

      // Close DB
      myDB.closeScript ();
      myDB.runSQL (dbName);
   }
}

//
//            import java.util.regex.*;
//
//            class testCapturing
//            {
//
//               public static void main (String [] aa)
//               {
//                   //CharSequence inputStr = "abbabcd";
//                   //String patternStr = "(a(b*))+(c*)";
//
//                   CharSequence inputStr = "1-Ergocios TABO 2-Arkancios TABO 3-Jumehelos TABO soy yo happy";
//                   String patternStr = "1-(.*) TABO 2-(.*) TABO 3-(.*) TABO (.*)";
//
//
//                   Pattern pattern = Pattern.compile(patternStr);
//                   Matcher matcher = pattern.matcher(inputStr);
//                   boolean matchFound = matcher.find();
//
//                   if (matchFound)
//                   {
//                       System.out.println ("match found!");
//                       for (int ii = 0; ii <= matcher.groupCount(); ii++)
//                       {
//                           String groupStr = matcher.group(ii);
//
//                           int groupStart = matcher.start(ii);
//                           int groupEnd = matcher.end(ii);
//
//                           System.out.println (ii + ") " + "\"" + inputStr.subSequence(groupStart, groupEnd) + "\"");
//                       }
//                   }
//               }
//            }
//
//




// 20.03.2008 17:32
//
//            [*] Propuesta de comandos PARSONS
//
//               1) FILE2DB: grabar resultado de parse en un fichero
//
//
//                  <database name>   app
//                  <table name>      app
//                  <fileID>          file scanno
//                  <lineNr>          file scanno
//                  <fileds>          parser
//
//
//                  <recordPattern>   parser
//
//                  parser has to detect start of record!
//
//                  COMMAND EXAMPLE
//
//                     PARSONS, FILE2DB, fileSource, dbName, tablePrefix, recordPatternRef
//                     PARSONS, FILE2DB, fileSource, dbName, tablePrefix, field1, field2, ..., recordPattern
//
//                     generara' o trabajara' con las tablas :
//
//                     tablePrefix_files:   fileID, fileDate, parseDate, fullPath
//                     tablePrefix_records: fileID, lineNr, filed1, field2 ...
//
//
//
//
//               2) LINE2VAR: grabar resultado de parse en variables eva
//
//
//                  <variable BaseName>  app
//
//
//                  parser does not have to detect start of record!
//                  just one record
//
//
//
//                  COMMAND EXAMPLE
//
//                     PARSONS, LINE2VAR, refLineSource, evaPrefix, recordPatternRef
//                     PARSONS, LINE2VAR, refLineSource, evaPrefix, field1, field2, ..., recordPattern
//
//
//                     PARSONS, LINE2EVAS, "juan, to, three", "textNambers_", 1, 2, 3, //(.*), (.*), (.*)
//
//                     generara'
//
//                        <textNambers_1> juan
//                        <textNambers_2> two
//                        <textNambers_3> three
//
//               3) LINES2EVATABLE: grabar resultado de parse en tabla eva
//
//                  parser has to detect start of record!
//
//
//                  COMMAND EXAMPLE
//
//                     PARSONS, LINES2EVA, refLineSource, evaName, recordPatternRef
//                     PARSONS, LINES2EVA, refLineSource, evaName, field1, field2, ..., recordPattern
//
//
//                     PARSONS, LINES2EVATABLE, "juan, to, three", "textNambers_", 1, 2, 3, //(.*), (.*), (.*)
//
//                     generara'
//
//                        <evaName>
//                           1,       2,       3
//                           juan  , two   , three
//
//
//               4) INSTALL and RUN: instala uno o varios parsons que actuara'n paralelamente sobre un mismo fichero
//
//
//                  COMMAND EXAMPLE
//
//                     PARSONS, INSTALL, refLineSource, tablePostfix, recordPatternRef
//                     PARSONS, INSTALL, refLineSource, tablePostfix, field1, field2, ..., recordPattern
//
//
//                     PARSONS, RUN, dbName, tablePrefix, sql select condition
//
//
//
//
//
//                     PARSONS, INSTALL, TraceClientAspect, scope, text, //- T (.*) [-M-] (.*)
//                     PARSONS, INSTALL, ConsolosAspect,    scope, text, //- C (.*) [-M-] (.*)
//                     PARSONS, RUN, arga.DB, parseao, //(extension = 'hbsi')
//
//                     1) los ficheros deben existir en la tabla (o view) @<tablePrefix>_files
//                     2) hace un //SELECT fileID, fullFileName FROM @<tablePrefix>_files WHERE extension = 'hbsi';
//                     3) uno por uno todos los ficheros son leidos y pasados por los parsers
//                        generando registros en las tablas
//
//
//                              parseao_TraceClientAspect (fileID, lineNr, columnNr, scope, text)
//                              parseao_ConsolosAspect    (fileID, lineNr, columnNr, scope, text)
//
//
//
