/*
library listix (www.listix.org)
Copyright (C) 2022-2026 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command UNIQUEFILESTO

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       UFSTO
   <groupInfo>  system_files
   <javaClass>  listix.cmds.cmdUniqueFileContentStorage
   <importance> 4
   <desc>       //Creates and handles a table for storage of unique file contents based on its hash

   <help>
      //
      // It handles a table for unique file contents (UF) based on the content hash, by default md5 hash, in a database table
      // named as uniqFileContentSTO, abbreviated UFSTO table.
      //
      // Each record in this table has a unique file content(*), that is if two files has identical content even if they have
      // different names or dates will occupy just a single entry
      //
      //      uniqueFileSTO
      //              (
      //              fileHash,           hash of the content of the file
      //              size,               size of the file
      //              compSize,           size of the compressed file if its contentBlob is actually compressed, otherwise 0
      //              f0Name,             name of the file when storing the content (first file with this content)
      //              f0Extension,        extension of file when storing the content (first file with this content)
      //              f0Date,             date file when storing the content (first file with this content)
      //              storeDate,          date when the file was stored
      //              status,             set to 0 when the file is added, other values or meanings has to be handle by the application
      //              contentBlob         content of the file, either compressed (sizeComp <> 0) or not compressed
      //              )
      // 
      // (*) two files are considered with the same content if their hash and size are the same
      //     note that this can result in a false identical content in case of a hash collision which is very improbable
      //     but still possible
      //
      // auto compress mode
      // This mode compress follow these rules
      //
      //   - it does not try to compress files with extensions given in AUTO-NO-COMP option which
      //     has the default value "zip,jar,gz,rar,7z,jpeg,jpg,png,mpg,mpeg,mp3,mp4,vob"
      //   - it will not try to compress files which size is less than AUTO-COMP MINSIZE in bytes which has the
      //     default value of 1000 (almost a 1Kbyte)
      //   - by definition a file with size 0 is not compressed in any case
      //
      // the compression algorithm is the native gz from java (slightly less efficient than zip and specially 7z)
      // and it cannot be changed.
      //
      // A special table reflects the result of the last STORE operation
      //
      //      uniqueFileSTO_STORE_RESULT
      //              (
      //              Name,               name given    
      //              contentHash,        hash calculated or retrieved
      //              isNew               if the hash was new one
      //              )
      //
      // Also two variables are updated with the last file or content requested  by STORE operations:
      //
      //    <UFILESTO_OUT_hash>
      //    <UFILESTO_OUT_new>
      //

   <aliases>
      alias

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Store a file content, if not already stored, into the UFSTO table
         2   ,    4      , //Store file contents from a folder, if not already stored, into the UFSTO table
         3   ,    4      , //Extracts a file content from UFSTO table


   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , db           , (default db), //Database name where to put the result of parsing
         1   , STORE FILE   ,             , //
         1   , physicalFile ,             , //Filename of the file to be included if not yet in the UFSTO table
         1   , outVar4FileHash,           , //(DEPRECATED! USE variable <UFILESTO_OUT_hash> instead) Optional output variable name for calculated fileHash

         2   , db           , (default db), //Database name where to put the result of parsing
         2   , STORE DIR    ,             , //
         2   , folderPath   ,             , //Path of the folder to be scanned for files to save in the UFSTO table
         2   , includeExtensions, (all)   , //Comma separated list of file extensions to be saved from the folder (default all files)
         2   , excludeExtensions, (all)   , //Comma separated list of file extensions not to be saved from the folder (by default none is excluded)

         3   , db           , (default db), //Database name where to put the result of parsing
         3   , EXTRACT      ,             , //
         3   , contentHash  ,             , //contentHash of the file to be extracted
         3   , targetFileName,            , //file name including path of the target file of the extraction
         3   , datetime     , (now)       , //Date time given in format yyyy-MM-dd HH:mm:ss or long (seconds from 1970-01-01)
         3   , overwrite    ,  1          , //Value 1 (default) allows overwritten the file if it previously exists


      <! next syntaxes
      <!          UNIQ FILE, EXISTS HASH , db, hashvalue
      <!                   , COMP ALGO, [md5]|sha1...
      <!
      <!          UNIQ FILE, EXISTS FILE, db, physicalFileName
      <!                   , COMP ALGO, [md5]|sha1...
      <!
      <!          UNIQ FILE, COMPARE  , db, (PHYSICALFILE|HASH), value
      <!                   , COMP ALGO, [md5]|sha1...


   <options>
      synIndx, optionName  , parameters, defVal, desc
         x   , COMP ALGO, md5|sha1|    , md5, //Hashing algorithm to use in the UFSTO table, note that different algorithms will be handled in different tables
         1   , DOCOMPRESS  , auto|no|yes|-1|0|1, auto, //Define if the file content has to be compressed (1) or not compressed (0) or auto compress mode (-1 and default) if this is decided automatially
         1   , AUTO-NO-COMP EXTENSIONS, comma sep extensions, "zip,rar,gz,7z,jar,jpeg,png", //List of separated extensions of the files which content don't has to be compressed in the auto compress mode
         1   , AUTO-COMP MINSIZE, size in bytes, 1000, //Minimum size of the file to be candidate for compression in the auto compress mode

         2   , INCLUDE EXTENSIONS, comma sep extensions, , //same meaning as parameter includeExtensions
         2   , EXCLUDE EXTENSIONS, comma sep extensions, , //same meaning as parameter excludeExtensions
         2   , DOCOMPRESS  , -1 auto|0:no|1:yes, -1, //Define if the file content has to be compressed (1) or not compressed (0) or auto compress mode (-1 and default) if this is decided automatially
         2   , AUTO-NO-COMP EXTENSIONS, comma sep extensions, "zip,rar,gz,7z,jar,jpeg,png", //List of separated extensions of the files which content don't has to be compressed in the auto compress mode
         2   , AUTO-COMP MINSIZE, size in bytes, 1000, //Minimum size of the file to be candidate for compression in the auto compress mode

   <! NO because of SQLinjection danger
   <!      1&2   , EXTRA COLUMNS, string ,        , //Comma separated column names to be added to the standard schema of UDSTO table
   <!      1&2   , EXTRA INITIAL VALUES, string,  , //Comma separated values, if necessary within quotes, for the extraColumns in the initial record


   <examples>
      gastSample


#**FIN EVA#
*/

package listix.cmds;

import java.util.*;
import java.io.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.utilEscapeStr;
import de.elxala.parse.csv.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end
import de.elxala.math.hash.*;
import java.util.regex.*;  // for Matcher, Pattern
import java.util.zip.*;

/**
*/
public class cmdUniqueFileSTO implements commandable
{
   //private static MessageHandle TX_FATALERROR    = new MessageHandle (); // "_lib scanFiles_error"
   private static MessageHandle LIGHT_MSG_START     = null; // new MessageHandle (); // "ledMsg parsons_start"
   private static MessageHandle LIGHT_MSG_PROGRESS  = null; // new MessageHandle (); // "ledMsg parsons_progresss"
   private static MessageHandle LIGHT_MSG_END       = null; // new MessageHandle (); // "ledMsg parsons_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   public static final String UFSTO_TABLE_BASENAME = "uniqueFileSTO_";
   public static final String AUTO_NO_COMP_EXTENSIONS = "zip,jar,gz,rar,7z,jpeg,jpg,png,mpg,mpeg,mp3,mp4,vob";
   public static final String AUTO_COMP_MINSIZE = "1000";

   protected String optColumnHeaderStr      = null;
   protected String optDoCompress           = null;
   protected int    optAutoCompMinSize      = 1000;
   protected String optAutoNoCompExtensions = null;
   protected String optDirIncludeExtensions = null;
   protected String optDirExcludeExtensions = null;

   public static final int UNDEF = 0;
   public static final int AUTO = 1;
   public static final int COMPRESS = 2;
   public static final int DONT_COMPRESS = 3;

   protected String  UFSTO_tableName = "";
   protected int     UFSTO_compressMode = UNDEF;

   private static boolean initOnce_msgHandles ()
   {
      if (LIGHT_MSG_START == null)
      {
         LIGHT_MSG_START     = new MessageHandle ();
         LIGHT_MSG_PROGRESS  = new MessageHandle ();
         LIGHT_MSG_END       = new MessageHandle ();

         //(o) TODO_parsons Unify ledMsg parsing_start etc for PARSONS and XMELON

         //NOTE! the message should be the same as in parsons, this signal should be general
         //      for parsing activity

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
          "UFSTO",
          "UFISTO",
          "UFILESTO",
          "FILESTO",
          "FILESTORAGE",
          "FISTO",
          "UNIQUEFILESTO",
          "UNIQUEFILESTORAGE",
       };
   }

   protected int exitExecute (listixCmdStruct cmd)
   {
      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_END, null); // ... )))
      cmd.checkRemainingOptions ();
      return 1;
   }

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

      String dbName = cmd.getListix ().resolveDBName (cmd.getArg(0));
      String oper   = cmd.getArg(1);

      boolean operStoreFile = cmd.meantConstantString (oper, new String [] { "FILE", "STOREFILE", "STORE" });
      boolean operStoreDir  = cmd.meantConstantString (oper, new String [] { "DIR", "FOLDER", "STOREDIR", "STOREFOLDER" });
      boolean operExtract   = cmd.meantConstantString (oper, new String [] { "EXTRACT", "EXTRACTFILE" });

      if (!operStoreFile && !operStoreDir && !operExtract)
      {
         cmd.getLog().err ("UFSTO", "wrong syntax ["+ oper +"] not recognized or implemented yet, available are STORE FILE, STORE FOLDER and EXTRACT FILE");
         return 1;
      }

      // algorithm : common option to all syntaxes
      String algorithm = cmd.takeOptionString(new String [] {"COMPRESSION ALGORITHM", "COMP ALGORITHM", "COMPALGO" }, "md5");
      UFSTO_tableName  = UFSTO_TABLE_BASENAME + algorithm;

      cmd.getLog().dbg (2, "UFSTO", "execute with : oper [" + oper + "] dbName [" +  dbName + "] tableName [" + UFSTO_tableName + "]");

      if (sendMessages)
         Mensaka.sendPacket (LIGHT_MSG_START, null); // ... )))

      if (operExtract)
      {
         //          0   1        2         3               4         5
         //  UFSTO, db, EXTRACT, fileHash, targetFileName, datetime, overwrite

         String fileHash = cmd.getArg(2);
         String targetFile = cmd.getArg(3);
         String datetime = cmd.getArg(4);
         boolean overwrite = stdlib.atoi (cmd.getArg(5)) == 1;

         if (!overwrite)
         {
            File fitx = new File (targetFile);
            if (fitx.exists ())
            {
               cmd.getLog().dbg (2, "UFSTO", "EXTRACT file but [" + targetFile + "] exists, don't overwrite");
               return exitExecute (cmd);
            }
         }

         String tmpTarget = fileUtil.createTemporal ("fiSTO", "gz");
         String SQL = "SELECT (compSize > 0) AS isCompressed, " +
                        " CASE WHEN compSize > 0 THEN " + 
                        " writefile ('" + fileUtil.getPathLinuxSeparator (tmpTarget) + "', contentBlob) "  +
                        " ELSE " +
                        " writefile ('" + fileUtil.getPathLinuxSeparator (targetFile) + "', contentBlob) "  +
                        " END " +
                        " FROM " +
                        UFSTO_tableName +
                        " WHERE fileHash = '" + fileHash + "'";

         // sqlSolver cliDB = new sqlSolver ();
         // cliDB.runSQL (dbName, SQL + ";");
         tableROSelect taRO = new tableROSelect (dbName, SQL);
         
         if (taRO.getValue ("isCompressed", 0).equals ("1"))
         {
            cmd.getLog().dbg (4, "UFSTO", "ungzipping [" + tmpTarget + "]");
            cmdZip.ungzip (cmd.getLog(), tmpTarget, targetFile);
         }

         if (datetime.length () > 0)
            cmdTouch.touchFile (targetFile, datetime);

         return exitExecute (cmd);
      }

      // common options to store file and store dir syntaxes
      //
      optDoCompress           = (cmd.takeOptionString(new String [] {"DOCOMPRESS", "COMPRESS" }, "auto")).toLowerCase ();
      optAutoCompMinSize      = stdlib.atoi (cmd.takeOptionString(new String [] {"AUTO-COMPMINSIZE", "MINSIZE" }, AUTO_COMP_MINSIZE));
      optAutoNoCompExtensions = cmd.takeOptionString(new String [] {"AUTO-NO-COMPEXTENSIONS", "NO-COMPEXTENSIONS" }, AUTO_NO_COMP_EXTENSIONS);

      // NO, because of SQLinjection danger
      // optExtraColumns         = cmd.takeOptionString(new String [] {"EXTRA COLUMNS", "EXTRA FIELDS" }, null);
      // optExtraInitialValues   = cmd.takeOptionString(new String [] {"EXTRA INITIAL VALUES", "INITAL VALUES" }, null);

      UFSTO_compressMode = UNDEF; // actually set to AUTO in takeOptionString
      if (optDoCompress.equals ("auto") || optDoCompress.equals ("-1"))
         UFSTO_compressMode = AUTO;
      else if (optDoCompress.equals ("yes") || optDoCompress.equals ("y") || optDoCompress.equals ("1"))
         UFSTO_compressMode = COMPRESS;
      else if (optDoCompress.equals ("no") || optDoCompress.equals ("n") || optDoCompress.equals ("0"))
         UFSTO_compressMode = DONT_COMPRESS;
      else
      {
         cmd.getLog().err ("UFSTO", "wrong compression mode ["+ optDoCompress +"] value has to be one of auto,yes,y,no,n,-1,1 or 0");
         return exitExecute (cmd);
      }

      if (operStoreDir)
      {
         cmd.getLog().err ("UFSTO", "STORE FOLDER not yet implemented .... !");
         // optDirIncludeExtensions = cmd.takeOptionString(new String [] {"INCLUDEEXTENSIONS", "+EXTENSIONS", "ADDEXTENSIONS" }, "");
         // optDirExcludeExtensions = cmd.takeOptionString(new String [] {"EXCLUDEEXTENSIONS", "-EXTENSIONS", "IGNOREEXTENSIONS" }, "");
         // ...
         return exitExecute (cmd);
      }

      if (operStoreFile)
      {
         //          0   1           2
         //  UFSTO, db, STORE FILE, physicalFile, outVarHash

         String sourceFile = cmd.getArg(2);

         // ------ DEPRECATED parameter!
         // make it compatible for a while
         //
         String outVar4FileHash = cmd.getArg(3);
         if (outVar4FileHash.length () > 0)
         {
            Eva evaTarget = that.getSomeHowVarEva (outVar4FileHash);
            cmd.getLog().warn ("UFSTO", "outVar4FileHash parameter has been DEPRECATED! use the variable UFILESTO_OUT_hash instead");

            // WORKAROUND! place the result into an Eva 
            evaTarget.clear ();
            evaTarget.setValue ("@<UFILESTO_OUT_hash>", 0, 0);
         }
         // ----- END OF DEPRECATED


         if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_START, null); // ... )))

         // 1) ------ Calculate file hash
         //
         File fitx = new File2 (sourceFile);
         String fileHash = "";
         if (fitx.exists () && fitx.isFile ())
            fileHash = hashos.hash(algorithm, sourceFile, -1);

         {
            Eva evaOut = that.getSomeHowVarEva ("UFILESTO_OUT_hash");
            evaOut.clear ();
            evaOut.setValue (fileHash, 0, 0);
         }

         if (fileHash == "")
         {
            cmd.getLog().err ("UFSTO", "could not calculate hash for [" + sourceFile + "] maybe file does not exists!");
            return exitExecute (cmd);
         }

         // 2) ------ Check if file has been already stored
         //
         tableROSelect taRO = new tableROSelect (dbName,
                                                 "SELECT fileHash FROM " + UFSTO_tableName +
                                                 " WHERE fileHash = '" + fileHash + "'"
                                                 );

         if (taRO.getRecordCount () == 1)
         {
            cmd.getLog().dbg (2, "UFSTO", "contents for [" + sourceFile + "] with hash [" + fileHash + " ] was already stored");
            return exitExecute (cmd);
         }
         if (taRO.getRecordCount () != 0)
         {
            cmd.getLog().err ("UFSTO", "error retrieving contents with hash [" + fileHash + "] for file [" + sourceFile + "] with result # " + taRO.getRecordCount ());
            return exitExecute (cmd);
         }

         // 3) ------ Check if proceed with file compression
         //
         boolean doCompress = UFSTO_compressMode == COMPRESS;
         long compressSize = 0;
         String extension = fileUtil.getFullExtension (sourceFile);
         String file2Read = sourceFile;
         if (UFSTO_compressMode == AUTO)
         {
            if (fitx.length () >= optAutoCompMinSize)
            {
               doCompress = ("," + optAutoNoCompExtensions + ",").indexOf ("," + extension + ",") == -1;
            }
         }
         if (doCompress)
         {
            file2Read = fileUtil.createTemporal ("fiSTO", "gz");
            cmdZip.gzip (cmd.getLog(), sourceFile, file2Read, false);
            
            File fitxComp = new File2 (file2Read);
            compressSize = fitxComp.length ();
         }

         // 4) ------ Store the file in the table
         //
         String dateStr   = DateFormat.getStr (new Date(fitx.lastModified ()));
         String storeDate = DateFormat.getStr (new Date());

         String DEFAULT_SCHEMA = "fileHash, size, compSize, f0Name, f0Extension, f0Date, storeDate, status, contentBlob";

         sqlSolver cliDB = new sqlSolver ();
         cliDB.runSQL (dbName,
                      "CREATE TABLE IF NOT EXISTS " + UFSTO_tableName + " (" + DEFAULT_SCHEMA + ");"

                      // NO, because of SQLinjection danger
                      // + (optExtraColumns ? ", " + optExtraColumns: "")
                      // + " ) ;"

                      + "INSERT INTO " + UFSTO_tableName + " (" + DEFAULT_SCHEMA + ") "
                      + " VALUES ("
                      +    "'" + fileHash + "', "
                      +    fitx.length () + ", "
                      +    compressSize + ", "
                      +    "'" + cliDB.escapeString (fitx.getName ()) + "', "
                      +    "'" + extension + "', "
                      +    "'" + dateStr + "', "
                      +    "'" + storeDate + "', "
                      +    0 + ", "
                      +    "readfile('" + fileUtil.getPathLinuxSeparator (file2Read) + "')"

                      // NO, because of SQLinjection danger
                      //  + (optExtraValues ? ", " + optExtraValues: "")

                      + ");"
                      );
                      
         if (compressSize > 0)
         {
            File fi = new File2 (file2Read);
            fi.delete ();
         }
         return exitExecute (cmd);
      }

      return exitExecute (cmd);
   }

   // protected void parseCSVFile (listix that, String csvFileName, String dbName, String tablePrefix)
   // {
   // }
}

/*


         gzip (file2gzip, gzipFile, setDateTimeFromOrigin);
      }
      else if (cmd.meantConstantString (oper, new String [] { "UNGZIP" } ))
      {
         String dateTimeOpt = cmd.takeOptionString ("SETDATE", "gz");
         ungzip (zipFileName, secondParam, dateTimeOpt);


   only is there is no need of compression
   we can do all together, check if need to be inserted and insert it, in one single query

                  INSERT INTO uniqueFileSTO_md5
                  SELECT
                     theHash AS fileHash,
                     theSize AS size,
                     0 AS sizeComp,
                     theName AS name,
                     theDate AS date,
                     readfile(theName) AS contentBlob
                  WHERE fileHash NOT IN (SELECT fileHash FROM uniqueFileSTO_md5)
                  ;

   1) estan en la tabla ?

      <SQL_WANTED_HASHES>
         SELECT
            filename1 AS filename
            fileHash1 AS fileHash
         UNION
            filename2 AS filename
            fileHash2 AS fileHash


      <SQL_NEW_HASHES>
         SELECT * FROM (@<SQL_WANTED_HASHES>)
         WHERE fileHash NOT IN (SELECT fileHash FROM uniqueFileSTO_md5)


      // ficheros que podemos ignorar ?
      <SQL_DISCARD_HASHES>
         SELECT * FROM (@<SQL_WANTED_HASHES>)
         WHERE fileHash IN (SELECT fileHash FROM uniqueFileSTO_md5 WHERE fileHash IN (SELECT fileHash FROM (@<SQL_WANTED_HASHES>)))


   2) hay que comprimirlos  ?

         gzip them


               SELECT fileHash
               FROM uniqueFileSTO_md5
               WHERE fileHash IN (list of hashes)


INSERT INTO targetTable(field1)
SELECT field1
FROM myTable
WHERE NOT(field1 IN (SELECT field1 FROM targetTable))

*/