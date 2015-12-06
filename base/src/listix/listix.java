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



package listix;

import java.util.*;
import java.util.regex.*;  // for Matcher, Pattern
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.graph.*;
import de.elxala.Eva.*;
import de.elxala.db.sqlite.*;
import de.elxala.zServices.logger;

import listix.table.*;
import listix.cmds.*;

/**
   see
*/
public class listix
{
   public static final String LISTIX_DEFAULT_DATE_FORMAT = "dd.MM.yyyy HH:mm";

   private logger log      = new logger (this, "listix", null);          // log intern just for listix
   public  logger log_ext  = new logger (this, "listix_command", null);  // log for commands

   //(o) todo_listix_logging logging listix flow as records when this is implemented in logServer
   //
   public static final int FLOWLEVEL_1 = 2;
   public static final int FLOWLEVEL_2 = 3;
   public static final int FLOWLEVEL_3 = 4;

   //
   public  logger log_flow = new logger (this, "listix_flow", new String [] { "value", "formatStackDepth" });
   //(o) listix_logs logging listix flow
   //
   //
   // Different listix flow records
   //
   //    At debug level 2 : start of listix formats and incidences
   //
   //       context  msg        meaning
   //       -------  -------    ---------------------------------------------------
   //       flow     var        start format which is a special variable or table column
   //       flow     format     start format which is an Eva variable (listix format)
   //       flow     formatEnd  finish a format of stack level 0
   //
   //       info     "leaving format because NOCYCLIC found!"
   //       info     "special format @"
   //       info     "format NOT found"
   //
   //    At debug level 3 : opening files and starting commands and subcommands
   //
   //       context  msg        meaning
   //       -------  -------    ---------------------------------------------------
   //       open     Capture    open as capture (special programming feature)
   //       open     StdOut     open as standard output (print out on the console)
   //       open     File(x)    open as file where x may be 'w' = open for write or 'a' = open for append
   //       flow     subCmd     start a subcommand (some commands accept sub-commands)
   //       flow     cmd        start a command
   //
   //    At debug level 4 : tracing all the prints
   //
   //       context  msg        meaning
   //       -------  -------    ---------------------------------------------------
   //       print    Capture     text printed out in Capture mode
   //       print    StdOut      text printed out on the console
   //       print    File        text printed out in a file
   //       print    Var         text printed out in an Eva variable
   //

   // makedir
   public static boolean MAKE_TARGET_DIRS = true;

   // if CAPTURE_FILES is true then all the files will be write in memory in a
   // static EvaUnit 'eunitCapturedFiles' which have as many Eva's as files,
   // the Eva name is the filename and the contents the text
   //
   public static EvaUnit eunitCapturedFiles = new EvaUnit("generatedFiles");
   public static boolean CAPTURE_FILES = false;

   public Eva evaCurrentCaptureFile = null;

   private TextFile globFile = null;
   private boolean  openByMe = false;

   private Eva globTargetEva = null;
   private StringBuffer globTargetStrBuffer = null;
   private boolean usingTargetStrBuffer = false;

   private EvaUnit globData = null;
   private EvaUnit globFormats = null;

   private String theNewLineString = TextFile.NEWLINE_NATIVE;

   private tableCursorStack tablon = null;


   // this variable is exclusively thought to find parameters in calls like LISTIX or GENERATE
   // or the parameters of the main application which are passed to listix main procedures (formats)
   //
   // It stores the stack depth at the moment of creating this instance
   // or such level that cannot be underpassed to search parameters
   // (see setStackDepthZero4Parameters)
   private int stackDepthZero = 0;
   private int stackAtBeginning = 0;

   private commander comino = new commander ();

   /**
      Note: be sure call destroy() method (or restoreStack()) specially if created with an already
      initialized tableCursorStack
   */
   public listix (EvaUnit lsxFormats, EvaUnit lsxData, tableCursorStack tableTable)
   {
      init (lsxFormats, lsxData, tableTable, TextFile.NEWLINE_NATIVE, null);
   }

   /**
      Note: be sure call destroy() method (or restoreStack()) specially if created with an already
      initialized tableCursorStack
   */
   public listix (EvaUnit lsxFormats, EvaUnit lsxData, tableCursorStack tableTable, String [] arguments)
   {
      init (lsxFormats, lsxData, tableTable, TextFile.NEWLINE_NATIVE, arguments);
   }

   //(o) DEPRECATE_listix newLineString artifact it is no more useful (remove all and constructor)
   public listix (EvaUnit lsxFormats, EvaUnit lsxData, tableCursorStack tableTable, String newLineString)
   {
      init (lsxFormats, lsxData, tableTable, newLineString, null);
   }

   /**
      Note: be sure call destroy() method (or restoreStack()) specially if created with an already
      initialized tableCursorStack
   */
   public void init (EvaUnit lsxFormats, EvaUnit lsxData, tableCursorStack tableTable, String newLineString, String [] params)
   {
      theNewLineString = newLineString;
      globData = lsxData;
      globFormats = lsxFormats;

      tablon = (tableTable == null) ? new tableCursorStack (): tableTable;

      // fetch the stack depth (for @<:listix paramCounter>)
      stackAtBeginning = stackDepthZero = tablon.getDepth();
      if (params != null && params.length > 0)
      {
         log.dbg (2, "init", "params length " + params.length);
         tablon.pushTableCursor(new tableCursor(new tableAccessParams (params)));
      }

      // removed on 28.02.2009 11:31
      ////(o) sql cache!!
      //roSqlPool.setAllowCache (true);
   }


   /**
      Function for log pruposes
      Note that it does not return the stack of this instance but the last used stack!
   */
   public static String [] getLastFormatStack ()
   {
      return cyclicControl.getLastFormatStack ();
   }

   public void destroy ()
   {
      restoreStack ();
   }

   public void restoreStack ()
   {
      while (tablon.getDepth() > stackAtBeginning)
         tablon.popTableCursor ();
   }

   public static String getVersion ()
   {
      return "0.36.090612";
   }

   public int getStackDepthZero4Parameters ()
   {
      return stackDepthZero;
   }

   /**
      A user of this function should do something like

         int oldStackLevel = oListix.getStackDepthZero4Parameters();
         oListix.setStackDepthZero4Parameters (oListix.getTableCursorStack ().getDepth());
         .... do something
         oListix.setStackDepthZero4Parameters (oldStackLevel);

      Note that this cannot be performed automatically by this class (listix)
      A bad use of this method might produce that the primitive variable @<:listix paramCount> returns a wrong value!
   */
   public void setStackDepthZero4Parameters (int newValue)
   {
      //trace who is doing this ?
      stackDepthZero = newValue;
   }

   /// return the logger object for the listix commands
   ///
   public logger log ()
   {
      return log_ext;
   }

   private String badVariableValue (String varName)
   {
      //Note : the symbol '?' is used because it is an invalid file name character

      //(o) TOSEE_listix Be more strict with bad variable names "?((xx))"
      //          Bad variable names already produce an error, but still it is not a fatal error.
      //          Either made it fatal error or prevnt from calling commands like CALL, GENERATE
      //          or LAUNCH etc if a badVariableValue occurs at least once.
      return "?((" + varName + "))";
   }


   /**
      Set the new line string for this listix object 'nlStr', except if this is null
      in which case the call has no effect.
   */
   public void setNewLineString (String nlStr)
   {
      if (nlStr != null)
         theNewLineString = nlStr;
   }

   public void addInternCommand (commandable cmdToAdd)
   {
      comino.loadCommandable (cmdToAdd);
   }

   public EvaUnit getGlobalData ()
   {
      return globData;
   }

   public EvaUnit getGlobalFormats ()
   {
      return globFormats;
   }

   public TextFile getGlobalFile ()
   {
      return globFile;
   }

//   public commander getCommander ()
//   {
//      return comino;
//   }

   public String getDefaultDBName ()
   {
      String defDB = sqlUtil.getGlobalDefaultDB ();
      if (defDB == null || defDB.equals(""))
      {
         defDB = fileUtil.createTemporal ("lsx", ".db");
         log.dbg (2, "createDefaultTempDBName", "create defaultDB " + defDB);
         sqlUtil.setGlobalDefaultDB (defDB);
      }

      return defDB;
   }

   public String createTempFile (String extension)
   {
      String sExt = (extension != null && extension.length() > 0) ? ("." + extension): ".tmp";
      return fileUtil.createTemporal ("lsxTmpFile", sExt);
   }

   public void setGlobalData (EvaUnit lsxData)
   {
      globData = lsxData;
   }

   public void setGlobalFormats (EvaUnit lsxFormats)
   {
      globFormats = lsxFormats;
   }

   public tableCursorStack getTableCursorStack ()
   {
      return tablon;
   }

   public boolean openTargetFile (String fileName)
   {
      return openTargetFile (fileName, false);
   }

   public boolean openTargetFile (String fileName, boolean append)
   {
      if (CAPTURE_FILES)
      {
         log_flow.dbg (FLOWLEVEL_2, "open", "Capture", new String [] { fileName, "" + ciclon.depth () });
         evaCurrentCaptureFile = eunitCapturedFiles.getSomeHowEva (fileName);
         //(o) TOSEE_listix should implement append inf case Capture files ? (old feature CAPTURE_FILES!)
         //
         //    Strictly the right thing is to reset the Eva if append is false
         //
         // if (! append) evaCurrentCaptureFile.clear ();
         //
         //    The question is in which case it would be needed ?
         //    Note that if we write more times on the same file due to a mistake
         //    this is going to be seen if we add per default
         //
         return true;
      }

      if (fileName.equals("") || fileName.equals("con") || fileName.equals("stdout"))
      {
         log_flow.dbg (FLOWLEVEL_2, "open", "StdOut", new String [] { fileName, "" + ciclon.depth () });
         if (globFile != null) globFile.fclose ();
         globFile = null;
         return true;
      }

      if (MAKE_TARGET_DIRS)
      {
         fileUtil.ensureDirsForFile (fileName);
      }
      openByMe = true;

      log_flow.dbg (FLOWLEVEL_2, "open", "File" + (append ? "a)": "w)"), new String [] { fileName, "" + ciclon.depth () });
      globFile = new TextFile ();
      return globFile.fopen (fileName, (append) ? "a": "w");
   }

   public void assignTargetFile (TextFile openedFile)
   {
      globFile = openedFile;
      openByMe = false;
   }

   public void closeTargetFile ()
   {
      if (openByMe && ! CAPTURE_FILES && globFile != null)
      {
         globFile.fclose ();
      }
   }

   public void setTargetEva (Eva evaTarget)
   {
      globTargetEva = evaTarget;
      if (usingTargetStrBuffer && globTargetStrBuffer != null)
         log.err ("setTargetEva", "globTargetStrBuffer is not null, cannot change listix target!");
   }

   public Eva getTargetEva ()
   {
      return globTargetEva;
   }

   public void endTargetEva ()
   {
      globTargetEva = null;
   }

   public boolean checkGlobs ()
   {
      if (globData != null && globFormats != null) return true;
      if (globData == null)
      {
         log.fatal ("checkGlobs", "data is null!");
      }
      if (globFormats != null)
      {
         log.fatal ("checkGlobs", "formats is null!");
      }
      return false;
   }

   /**
      returns an Eva variable with the given name, first searching into
      data unit and if not found in formats unit. If finally it is not
      found returns null
   */
   public Eva getVarEva (String name)
   {
      if (! checkGlobs ()) return null;

      // look if it is in data
      //
      Eva ret = globData.getEva (name);
      if (ret == null)
      {
         // try with formats
         //
         ret = globFormats.getEva (name);
      }
      return ret;
   }


   /**
      special method to get an eva value for a read operation acording to the listix criterium
      that is : first primitive, second table column value, else real eva variable.
      If 'name' is a read only value (listix primitive or table column value) a new eva is created
      and the value is placed there, otherwise the eva returned is the same as the function 'getVarEva' would return.
   */
   public Eva getReadVarEva (String name)
   {
      if (! checkGlobs ()) return null;

      Eva [] evaArr = new Eva [1];
      if (formatIsValue (name, evaArr))
         return evaArr[0];

      return getVarEva (name);
   }

   /**
      returns an Eva variable with the given name, first searching into
      data unit and if not found in formats unit. If finally it is not
      found creates a new Eva variable with that name in data unit
   */
   public Eva getSomeHowVarEva (String name)
   {
      Eva eva = getVarEva (name);
      if (eva == null)
      {
         eva = globData.getSomeHowEva (name);
      }
      return eva;
   }

   //(o) listix_core_primitiveVariables listix::valPrimitive where the variable @<:xxx> are solved
   //
   private String valPrimitive (String name)
   {
      if (name.length () == 0 || (name.charAt (0) != ':' && ! name.equals("@")) )
         return null;

      //(o) TODO_listix improve the scan of primitive with a regular expresion

      String lowName = name.toLowerCase ();
      int salto = 0;

      if (lowName.startsWith (":listix"))    salto = ":listix".length ();
      else if (lowName.startsWith (":lsx"))  salto = ":lsx".length ();
      if (salto > 0)
      {
         lowName = lowName.substring (salto + 1);  // is lower case
         name    = name.substring (salto + 1);     // is any case

         if (lowName.equals ("row"))   return "" + tablon.getCurrentDataRow ();
         if (lowName.equals ("rows"))  return "" + tablon.getCurrentDataRows ();

         if (lowName.equals ("firstrow")) return "" + ((tablon.getCurrentDataRow () == 0) ? "1": "0");
         if (lowName.equals ("lastrow"))  return "" + ((tablon.getCurrentDataRow () == tablon.getCurrentDataRows () -1) ? "1": "0");

         //(o) todo_listix [ ] anyadir var primitiva ":listix ROW+" ":listix ROW-" ?
         //
         //          NOTE : These functions are dangerous (very easy to create endless loops)
         //                 and they does not seem to be very useful
         //
         // //ESTA SE PODRIA ANYADIR SIN PROBLEMAS
         //
         // if (lsxFormat.equalsIgnoreCase (":listix ROW+"))
         // {
         //    tablon.increment_RUNTABLE ();
         //    return "";
         // }
         //
         // //ESTA ES MAS COMPLICADO POR LA CORRECTA IMPLEMENTACION decrement_RUNTABLE EN TODOS
         //
         // if (lsxFormat.equalsIgnoreCase (":listix ROW-"))
         // {
         //    tablon.decrement_RUNTABLE ();
         //    return "";
         // }

         //(o) todo_listix anyadir las primitivas <:listix cols> y <:listix colName x>
         //
         // if (lowName.equals ("cols"))  return "" + tablon.getCurrentDataCols ();
         // if (lowName.startsWith ("colname")) ... return "" + tablon.getCurrentDataColName (x);

         //12.06.2009 20:22
         //eliminar (No cambiar)
         //   <:lsx tmpFile> por <:lsx tmp1>
         //y cambiar
         //   <:lsx newTmpFile> <:lsx newTempFile> por <:lsx tmp extension>

         //return default db
         //
         if (lowName.equals ("tmpdb") || lowName.equals ("tmpdbname") || lowName.equals ("defaulttmpdb"))
            return getDefaultDBName ();

         //DEPRECATED temporal files from 12.06.2009 20:31
         //
         if (lowName.equals ("tmpfile") || lowName.equals ("newtmpfile") || lowName.equals ("newtempfile"))
         {
            log.err ("valPrimitive", "variable <:lsx " + lowName + "> is DEPRECATED! use <:lsx tmp extension> instead!");
            return createTempFile (null);
         }

         //create temporal file
         //
         if (lowName.equals ("tmp") || lowName.startsWith ("tmp "))
         {
            // create a tmp file
            String ext = name.substring ("tmp".length ());
            if (ext.length () > 0) ext = ext.substring (1); // remove " "
            return createTempFile (ext);
         }

         //return date
         //
         if (lowName.startsWith ("date"))
         {
            // the command can be ...date> or ...date xDateFormat>
            String pattern = name.substring ("date".length ());
            if (pattern.length () == 0)
            {
               // no pattern, then use our default one
               pattern = LISTIX_DEFAULT_DATE_FORMAT;
            }
            else if (pattern.equals("0"))
            {
                pattern = "yyyy-MM-dd";
            }
            else if (pattern.equals("1"))
            {
                pattern = "yyyy-MM-dd HH:mm:ss";
            }
            else if (pattern.equals("2"))
            {
                pattern = "yyyy-MM-dd HH:mm:ss S";
            }
            else
            {
               // another pattern! remove the initial " "
               pattern = pattern.substring (1);
            }
            return "" + DateFormat.getStr (new Date (), pattern);
         }

         if (lowName.equals ("paramcount") || lowName.equals ("argcount"))
         {
            // @<:listix paramCounter>
            //    returns the number of parameters of the last called listix format using
            //    the command GENERATE or LISTIX (if given..)
            //    Note that this is always right but due to previous LOOP's it is not guaranteed at all
            //    that the parameters themselfs are accessible (@<p1> etc)
            //
            //(o) TODO_listix It is possible to build a primitive @<:listix param y> to guarantee the access to the parameters always
            int mira = tablon.getDepth () - 1;

            while (mira >= stackDepthZero)
            {
               tableCursor toca = (tableCursor) tablon.getTableStack().get(mira);
               if (toca.data () instanceof tableAccessParams)
               {
                  return "" + ((tableAccessParams) toca.data ()).getParamCount ();
               }
               //System.out.println ("\neta no era " + toca.data ().getName ());
               mira --;
            }
            return "0";
         }

         if (lowName.equals("screenx"))
         {
            return "" + de.elxala.langutil.graph.sysMetrics.getScreenPixelWidth();
         }

         if (lowName.equals("screeny"))
         {
            return "" + de.elxala.langutil.graph.sysMetrics.getScreenPixelHeight();
         }

         try {
            if (lowName.equals("host nameupper")) return "" + java.net.InetAddress.getLocalHost().getHostName().toUpperCase ();
            if (lowName.equals("host name")) return "" + java.net.InetAddress.getLocalHost().getHostName();
            if (lowName.equals("host ip")) return "" + java.net.InetAddress.getLocalHost().getHostAddress();
         }
         catch (Exception e) { return ""; }

                  // false :listix command
         //
         log.err ("valPrimitive", "unknown \":listix\" primitive [" + name + "] it not equal to lowName (" + lowName + ")");
         return badVariableValue (name);
      }

      if (lowName.startsWith (":sys ") || lowName.startsWith (":prop "))
      {
         String propName = name.substring (5);
         return System.getProperty (propName, "");
      }
      return null;
   }


   /**
      checks if the 'lsxFormat' format is either
         a primitive value (e.g. <:listix date>)
         or a previous record field of the current table stack (e.g. <:- name>)
         or a record field of the current table stack
      if it is one of them then returns in 'retEva' its value and returns true
      if not then returns false.

      'lsxFormat' name of the format to determine
      'retEva'    has to be dimensioned with size 1, returns in index 0 the contents of the format as Eva
   */
   public boolean formatIsValue (String lsxFormat, Eva [] retEva)
   {
      String valor = valPrimitive (lsxFormat);

      if (valor != null)
      {
         retEva[0] = new Eva ("variable " + lsxFormat); // any name ...
         retEva[0].setValue (valor);
         return true;
      }

      // ... a last record field ?
      //
      boolean oldValue = false;
      if (lsxFormat.startsWith (":-"))
      {
         lsxFormat = lsxFormat.substring (2);
         oldValue = true;
      }

      // ... a normal record field of the current table data ?
      //
      // look if it is a field
      //
      String deTabla = tablon.findValueColumn (lsxFormat, oldValue);
      if (deTabla != null)
      {
         retEva[0] = new Eva ("field " + lsxFormat); // any name ...
         retEva[0].setValue (deTabla);
         return true;
      }

      return false;
   }

   /**
      'lsxFormat' name of the format to determine
      'retEva'    has to be dimensioned with size 1, returns in index 0 the contents of the format as Eva
   */
   public boolean formatIsListixFormat (String lsxFormat, Eva [] retEva)
   {
      retEva[0] = getVarEva (lsxFormat);

      // strange ... not found
      if (retEva[0] == null)
      {
         log.err ("formatIsListixFormat", "the eva variable [" + lsxFormat + "] not found!");
         return false;
      }
      return true;
   }

   private cyclicControl ciclon = new cyclicControl ();

   /**
      Solves the given lsx format 'lsxFormat'

      @returns a new unamed Eva variable with the solved listix format

      Example:
         Eva eva = solveLsxFormatAsEva (":listix date");
         System.out.println  (eva);

      output:
         <>
            '31.08.2008 11:30

   */
   public Eva solveLsxFormatAsEva (String lsxFormat)
   {
      Eva eraEva = globTargetEva;
      Eva result = new Eva ();

      globTargetEva = result;
      printLsxFormat (lsxFormat);
      globTargetEva = eraEva;

      return result;
   }

   /**
      Solves the given string 'str' and return the result as an Eva variable

      @returns a new unamed Eva variable with the solved string

      Example:
         Eva eva = solveStrAsEva ("today is @<:listix date> and current name is \"@<name>\");
         System.out.println  (eva);

         <>
            'today is 31.08.2008 11:30 and current name is "braulio"

   */
   public Eva solveStrAsEva (String str)
   {
      Eva eraEva = globTargetEva;
      Eva result = new Eva ();

      globTargetEva = result;
      printTextLsx (str);
      globTargetEva = eraEva;

      return result;
   }

   public String solveStrAsString (String str)
   {
      // returns the solved espression as a text, that is a String
      // that might contain return and line feed characters
      return solveStrAsEva (str).getAsText ();
   }

   public String solveStrAsStringFast (String str)
   {
      globTargetStrBuffer = new StringBuffer ();
      usingTargetStrBuffer = true;
      printTextLsx (str);
      usingTargetStrBuffer = false;
      return globTargetStrBuffer.toString ();
   }

   // prints a listix format with name 'lsxFormat'
   //
   public boolean printLsxFormat (String lsxFormat)
   {
      if (globData == null || globFormats == null)
      {
         log.err ("printLsxFormat", "lack of source units : " + ((globData == null) ?  "data is null!":"") + ((globFormats == null) ?  "formats is null!":""));
         return false;
      }
      if (! ciclon.pushClean (lsxFormat, "" + tablon.getDepth ()))
      {
         //seems to be cyclic
         //begin EXPERIMENT!
         Eva [] retFormat = new Eva[1];
         if (formatIsListixFormat (lsxFormat, retFormat))
         {
            if (retFormat[0].getValue (0,0).equals ("NOCYCLIC"))
            {
               //(o) todo_listix_cyclic Review this! is NOCYCLIC a command ? isn't has to be checked ??
               //

               //ups! the guy knew that it could happen, and he wants simply avoid it
               log_flow.dbg (FLOWLEVEL_1, "info", "leaving format because NOCYCLIC found!", new String [] { lsxFormat, "" + ciclon.depth () });
               return true;
            }
         }
         //end EXPERIMENT!


         //Normal handling
         log.fatal ("printLsxFormat", "format <" + lsxFormat + "> is cyclic ! (" + ciclon.cyclusMsg + ")");
         //writeStringOnTarget("((cyclic!))");
         return false;
      }

      boolean ok = true;

      // get the format as eva
      //
      Eva [] retFormat = new Eva[1];

      // if .. special not overwritable eva <@> is always "@"
      if (lsxFormat.equals("@"))
      {
         log_flow.dbg (FLOWLEVEL_1, "info", "special format @", new String [] { lsxFormat, "" + ciclon.depth () });
         printTextLsx ("@");
      }

      // else if .. a primitive value (<:listix ..> or a record field or a previous record field from a table
      else if (formatIsValue (lsxFormat, retFormat))
      {
         log_flow.dbg (FLOWLEVEL_1, "flow", "var", new String [] { lsxFormat, "" + ciclon.depth () });
         printTextLsx (retFormat[0].getValue ());
      }
      // else if .. a normal eva variable
      else if (formatIsListixFormat (lsxFormat, retFormat))
      {
         log_flow.dbg (FLOWLEVEL_1, "flow", "format", new String [] { lsxFormat, "" + ciclon.depth () });
         doFormat (retFormat[0]);
      }
      else
      {
         log_flow.dbg (FLOWLEVEL_1, "info", "format NOT found", new String [] { lsxFormat, "" + ciclon.depth () });
         ok = false;
         log.err ("solveLsxFormatAsEva", "[" + lsxFormat + "] not found. No replacement done!");
         writeStringOnTarget (badVariableValue (lsxFormat));
      }

      ciclon.pop ();

      if (ciclon.depth() == 0)
         log_flow.dbg (FLOWLEVEL_1, "flow", "formatEnd", new String [] { lsxFormat, "" + ciclon.depth () });

      return ok;
   }


   // utility for commands (e.g. cmdListix) and other communication mechanisms
   // calls a format with parameters
   //
   public boolean printLsxFormat (String lsxFormat, String [] parameters)
   {
      //09.01.2011 23:16
      //Nota!:
      //    Metodo sacado de cmdListix, se podría reescribir y hacer privada setStackDepth4Parameters
      //    etc.
      //Pregunta:
      //    Como es que cmdGenerate no usa esta función cuando pasa parámetros ??? (porque siempre
      //    utiliza un nuevo listix ?)
      //

      // store the key stack level
      //
      int oldStackLevel = getStackDepthZero4Parameters();
      setStackDepthZero4Parameters (getTableCursorStack ().getDepth());

      // push the parameters in the stack
      //
      boolean beenPushed = false;
      if (parameters != null && parameters.length > 0)
      {
         log.dbg (2, "printLsxFormat", "params length " + parameters.length);
         getTableCursorStack ().pushTableCursor(new tableCursor(new tableAccessParams (parameters)));
         beenPushed = true;
      }

      // call the listix format
      //
      boolean reto = printLsxFormat (lsxFormat);

      // restore the key stack level and pop the param table if needed
      //
      setStackDepthZero4Parameters (oldStackLevel);

      if (beenPushed)
         getTableCursorStack ().end_RUNTABLE ();

      return reto;
   }

   // to facilitate other commands the execution of subcommands (i.e. command "IN CASE")
   //
   public void executeSingleCommand (Eva evaCommand)
   {
      log_flow.dbg (FLOWLEVEL_2, "flow", "subCmd", new String [] { evaCommand.getValue (0, 0), "" + ciclon.depth () });
      comino.treatCommand (this, evaCommand, 0);
      //log_flow.dbg (FLOWLEVEL_2, "flow", "subCmdEnd", new String [] { evaCommand.getValue (0, 0), "" + ciclon.depth () });
   }


   // prints a listix format previously loaded in an Eva variable
   //
   public void doFormat (Eva eFormat)
   {
      boolean needReturn = false;
      int rr = 0;
      while (rr < eFormat.rows ())
      {
         // is it a text ? or a command
         //
         if (eFormat.cols (rr) > 1)
         {
            log_flow.dbg (FLOWLEVEL_2, "flow", "cmd", new String [] { eFormat.getValue (rr, 0), "" + ciclon.depth () });
            rr += comino.treatCommand (this, eFormat, rr);
            //log_flow.dbg (FLOWLEVEL_2, "flow", "cmdEnd", new String [] { eFormat.getValue (rr, 0), "" + ciclon.depth () });

            needReturn = false;   // with the break wouldn't be necessary but ...
            //break;
         }
         else
         {
            if (needReturn)
            {
               // some line of the format was printed out inmediatly before thus
               // a return-line feed is needed.
               newLineOnTarget ();
            }

            printTextLsx (eFormat.getValue (rr, 0));
            rr ++;
            needReturn = true;
         }
      }
   }

//   <main>
//
//      OSYS, MAKEDIR, out/dsp
//      OSYS, LAUNCH, DIR
//      OSYS, DEL, DIR
//
//      GENERATE FILE, filename, formatLsx, formatLsxEvaUnit, formatLsxFile, dataEvaUnit, dataFile
//      GEN, filename, formatLsx, formatLsxEvaUnit, formatLsxFile, dataEvaUnit, dataFile
//
//      out/@<name>.dot, diagram, formats, SM_dotDiagram.listix
//      out/dev/@<name>.dev,  devTemplate, formats, devProy.listix
//

   public void printTextLsx (String text)
   {
      String line = text;
      int [] ini_fin = new int [4];       // contain start pos and end pos (+1)

      while (found_VARIABLE_EVA (line, ini_fin))
      {
         String prevStr  = line.substring (0, ini_fin[0]);
         String variable = line.substring (ini_fin[2], ini_fin[3]);
         String restStr  = line.substring (ini_fin[1]);

         writeStringOnTarget (prevStr);
         printLsxFormat (variable);
         line = restStr;
      }

      // print the rest
      if (line.length () > 0)
         writeStringOnTarget (line);
   }

   public int countLsxFormatWhileText (Eva eFormat, int fromRow)
   {
      int kuenta = 0;
      for (int rr = fromRow; rr < eFormat.rows (); rr ++)
      {
         if (eFormat.cols (rr) > 1) break;
         kuenta ++;
      }
      return kuenta;
   }

   public int printLsxFormatWhileText (Eva eFormat, int fromRow)
   {
      int kuenta = 0;
      for (int rr = fromRow; rr < eFormat.rows (); rr ++)
      {
         if (eFormat.cols (rr) > 1) break;

         if (kuenta != 0)
            newLineOnTarget ();

         printTextLsx (eFormat.getValue (rr, 0));
         kuenta ++;
      }
      return kuenta;
   }


   private static final Pattern PATT_LSX_VARIABLE_EVA = Pattern.compile ("@<[^>]*>"); //  @<algo> donde algo= lo que sea menos >
   // private static final Pattern PATT_LSX_VARIABLE_EVA = Pattern.compile ("<<.*>>"); // (?)<algo> donde "algo" es lo que sea menos '<' o '>'

   /**
      help to find a listix eva variable
   */
   private static boolean found_VARIABLE_EVA (String where, int [] ini_fin)
   {
      //(o) javaTemas_Patterns&Matchers truco para detectar algunos casos con "no caracter" delante
      // we add " " at the begining to avoid the case "@(algo) ..." which not has the "no" @ first !

      Matcher esta = PATT_LSX_VARIABLE_EVA.matcher (where);
      if ( ! esta.find ()) return false;

      //       0123456789
      //       @<jalka>

      ini_fin[0] = esta.start ();
      ini_fin[1] = esta.end ();

      ini_fin[2] = ini_fin[0] + 2;   // substract "@<"
      ini_fin[3] = ini_fin[1] - 1;   // substract ">"

      return true;
   }

   // writing on target (either an eva or a file)
   //
   public void writeStringOnTarget (String str)
   {
      int iMark = 0;  //String dbgMark = "?";

      if (usingTargetStrBuffer && globTargetStrBuffer != null)
      {
         iMark = 4; // dbgMark = "Var"; // Eva
         globTargetStrBuffer.append (str);
      }
      else if (globTargetEva == null)
      {
         if (CAPTURE_FILES)
         {
            if (evaCurrentCaptureFile != null)
            {
               iMark = 1; // dbgMark = "Cap"; // capture

               int currRow = evaCurrentCaptureFile.rows() - 1;
               currRow = (currRow < 0) ? 0: currRow;
               String line = evaCurrentCaptureFile.getValue(currRow);
               line += str;
               evaCurrentCaptureFile.setValue(line, currRow);
            }
            else
            {
               log.severe ("writeStringOnTarget", "CAPTURE_FILES is TRUE but cannot write the output on target");
               return;
            }
         }
         else if (globFile == null)
         {
            // output to the standard strout
            iMark = 2; // dbgMark = "Out"; // stdout
            System.out.print (str);
         }
         else if (globFile.feof ())
         {
            log.err ("writeStringOnTarget", "Attempt to write on file but no file specified or it has been closed!");
            return;
         }
         else
         {
            iMark = 3; // dbgMark = "File"; // file
            globFile.writeString (str);
         }
      }
      else
      {
         int row = Math.max(0, globTargetEva.rows () - 1);

         iMark = 4; // dbgMark = "Var"; // Eva
         globTargetEva.setValue (globTargetEva.getValue (row) + str, row, 0);
         // System.err.println ("Escrabo :" + str + ":" + globTargetEva + "##...");
      }

      if (log_flow.isDebugging (FLOWLEVEL_3))
      {
         String dbgMark = (iMark == 0) ? "?": (iMark == 1) ? "Cap": (iMark == 2) ? "Out" : (iMark == 3) ? "File": "Var";
         log_flow.dbg (FLOWLEVEL_3, "print", dbgMark, new String [] { str, "" + ciclon.depth () });
      }
   }

   public void newLineOnTarget ()
   {
      if (usingTargetStrBuffer && globTargetStrBuffer != null)
      {
         globTargetStrBuffer.append (theNewLineString);
      }
      else if (globTargetEva == null)
      {
         if (CAPTURE_FILES)
         {
            if (evaCurrentCaptureFile != null)
                 evaCurrentCaptureFile.addRow ("");
            else log.severe ("newLineOnTarget", "CAPTURE_FILES is TRUE but cannot write the output on target");
         }
         else writeStringOnTarget (theNewLineString);
      }
      else globTargetEva.setValue ("", globTargetEva.rows (), 0);
   }
}

/*
*/