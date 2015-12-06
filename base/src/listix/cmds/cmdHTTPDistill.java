/*
library listix (www.listix.org)
Copyright (C) 2012 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command HTTPDISTILL

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       HTTP DISTILL
   <groupInfo>  internet
   <javaClass>  listix.cmds.cmdHTTDistill
   <importance> 5
   <desc>       //To analyze an HTTP response

   <help>
      //
      // Extracts message status, header and body from a http response
      //


   <aliases>
      alias
      HTTP DISTIL
      HTTP DISTILL
      HTTP DISTIL RESPONSE
      HTTP DISTILL RESPONSE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    5      , //Returns the first line, that is the message status (e.g. "HTTP/1.1 200 OK")
         2   ,    5      , //Returns the status code (e.g. "200")
         3   ,    5      , //Returns the header as a text or, if specified, the value of a given field
         5   ,    5      , //Returns the body of the response, decoding chunked body if needed

   <syntaxParams>
      synIndx, name           , defVal    , desc
         1   , HTTPresponseFile,          , //File containing the HTTP response
         1   , STATUS MSG     ,           , //
         2   , HTTPresponseFile,          , //File containing the HTTP response
         2   , STATUS CODE    ,           , //
         3   , HTTPresponseFile,          , //File containing the HTTP response
         3   , HEADER          ,          , //
         3   , fieldName       ,          , //Field name from header to get the value (e.g. "Content-Type")
         3   , defaultValue    ,          , //Default value if the field is not found in the header
         4   , HTTPresponseFile,          , //File containing the HTTP response
         4   , BODY            ,          , //

   <options>
      synIndx, optionName            , parameters, defVal, desc

   <examples>
      gastSample

      Http DISTIL example

   <Http DISTIL example>
      //#javaj#
      //
      //   <frames>
      //       oConsole, "Http DISTIL example", 200, 300
      //
      //#listix#
      //
      //   <main>
      //       VAR=, fres1, ":(mem) response1"
      //       VAR=, fres2, ":(mem) response2"
      //       GEN, @<fres1>, htt response data1
      //       GEN, @<fres2>, htt response data chunked
      //       //==== STATUS response 1:[
      //       HTTP DISTILL, @<fres1>, STATUS MSG
      //       //]
      //       //==== BODY response 1:[
      //       HTTP DISTILL, @<fres1>, BODY
      //       //]
      //       //==== HEADER response 1:[
      //       HTTP DISTILL, @<fres1>, HEADER
      //       //]
      //       //==== STATUS CODE response 2:[
      //       HTTP DISTILL, @<fres2>, STATUS CODE
      //       //]
      //       //==== HEADER Conten-Type response 2:[
      //       HTTP DISTILL, @<fres2>, HEADER, "Content-Type"
      //       //]
      //       //==== HEADER MiVar response 2:[
      //       HTTP DISTILL, @<fres2>, HEADER, "MiVar", "?"
      //       //]
      //       //==== BODY response 2:[
      //       HTTP DISTILL, @<fres2>, BODY
      //       //]
      //
      //   <htt response data1>
      //      //HTTP/1.1 200 OK
      //      //Date: Thu, 27 Dec 2012 21:06:13 GMT
      //      //Server: Apache
      //      //Content-Type: application/json
      //      //Content-Language: es
      //      //
      //      //This data is not chunked
      //      //second line
      //      //third etc
      //      //end of message
      //      //
      //
      //   <htt response data chunked>
      //      //HTTP/1.1 200 OK
      //      //Date: Thu, 27 Dec 2012 21:06:13 GMT
      //      //Server: Apache
      //      //Transfer-Encoding: chunked
      //      //Content-Type: application/json
      //      //Content-Language: es
      //      //
      //      //A
      //      //1234567890
      //      //10
      //      //long text
      //      //abcde
      //      //3
      //      //fin
      //      //
      //

#**FIN_EVA#

*/

package listix.cmds;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.*;
import java.util.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;

import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.langutil.*;


public class cmdHTTPDistill implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "HTTPDISTILL",
          "HTTPDISTIL",
          "HTTPDISTILLRESPONSE",
          "HTTPDISTILRESPONSE",
       };
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
      // ::execute - some helpful comment for all the possible syntaxes
      //
      //      comm____   , oper______    par1_____
      //      HTTP DISTILL, resultHTTP.txt, HEADER, "Conten-Type", "?"
      //

      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String fileName   = cmd.getArg(0);
      String distilPart = cmd.getArg(1);
      String headerFieldName = cmd.getArg(2);
      String defaultFieldValue = cmd.getArg(3);

      boolean headerFieldSpecified = cmd.getArgSize () > 2;

      boolean reqStatusMessage = cmd.meantConstantString (distilPart, new String [] { "STATUS", "STATUSMSG", "STATUSMESSAGE" });
      boolean reqStatusCode    = cmd.meantConstantString (distilPart, new String [] { "STATUSCODE", "CODE" });
      boolean reqHeader        = cmd.meantConstantString (distilPart, new String [] { "HEADER", "HEADERTEXT", "HEADERFIELD", "HEADERVALUE" });
      boolean reqBody          = cmd.meantConstantString (distilPart, new String [] { "BODY", "BODYTEXT" });

      //try to open the http response file
      TextFile frespons = new TextFile ();
      if (!frespons.fopen (fileName, "r"))
      {
         cmd.getLog().err ("HTTP DISTILL", "HTTP response file \"" + fileName + "\" could not be opened!");
         frespons.fclose ();
         return 1;
      }

      // for all, at least status line
      if (!frespons.readLine ())
      {
         cmd.getLog().err ("HTTP DISTILL", "Not found an HTTP status line in \"" + fileName + "\" !");
         frespons.fclose ();
         return 1;
      }
      // now frespons.TheLine contains the status line!

      if (reqStatusMessage)
      {
         cmd.getListix ().printTextLsx (frespons.TheLine ());
      }
      else if (reqStatusCode)
      {
         String msg = frespons.TheLine ();
         String code = "";
         int pp = 0;
         while (pp < msg.length () && msg.charAt (pp) != ' ') pp ++;
         pp ++; // skip the blank
         while (pp < msg.length () && msg.charAt (pp) != ' ') code = code + msg.charAt (pp ++);

         cmd.getListix ().printTextLsx (code);
      }
      else if (reqHeader)
      {
         boolean writeDefaultValue = headerFieldSpecified;
         do
         {
            if (!frespons.readLine () || frespons.TheLine ().length () == 0) break;
            if (! headerFieldSpecified)
            {
               cmd.getListix ().printTextLsx (frespons.TheLine ());
               cmd.getListix ().newLineOnTarget ();
            }
            else
            {
               // only one field
               if (frespons.TheLine ().startsWith (headerFieldName + ":"))
               {
                  int nameLen = headerFieldName.length () + 1 +
                                (frespons.TheLine ().startsWith (headerFieldName + ": ") ? 1: 0);
                  cmd.getListix ().printTextLsx (frespons.TheLine ().substring (nameLen));
                  writeDefaultValue = false;
                  break; // work done!
               }
            }
         } while (frespons.TheLine ().length () > 0);
         if (writeDefaultValue)
         {
            // header field was specified but no such field was found
            cmd.getListix ().printTextLsx (defaultFieldValue);
         }
      }
      else if (reqBody)
      {
         // detect if chunked while skiping the header
         boolean isChunked = false;
         do
         {
            if (!frespons.readLine () || frespons.TheLine ().length () == 0) break;
            if (!isChunked)
               isChunked = (frespons.TheLine ().startsWith ("Transfer-Encoding: chunked"));
         } while (frespons.TheLine ().length () > 0);

         //write body
         //  NOTE: Naif implementation of decoding chunked !!!
         //        it only works when chunked data has no new line characters!
         //        chunk size is not count at all!
         boolean isChunkSize = true;
         do
         {
            if (!frespons.readLine () || frespons.TheLine ().length () == 0) break;
            if (! isChunked)
            {
               cmd.getListix ().printTextLsx (frespons.TheLine ());
               cmd.getListix ().newLineOnTarget ();
            }
            else
            {
               if (!isChunkSize)
                  cmd.getListix ().printTextLsx (frespons.TheLine ());
               isChunkSize = ! isChunkSize;
            }
         } while (frespons.TheLine ().length () > 0);
      }
      else
      {
         cmd.getLog().err ("HTTP DISTILL", "operation " + distilPart + " not valid!");
         frespons.fclose ();
         return 1;
      }

      frespons.fclose ();
      cmd.checkRemainingOptions (true);
      return 1;
   }
}






