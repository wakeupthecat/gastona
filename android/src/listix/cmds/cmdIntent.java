/*
library listix (www.listix.org)
Copyright (C) 2011 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_listix_command BROWSER

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       INTENT
   <groupInfo>  system_run
   <javaClass>  listix.cmds.cmdIntent
   <importance> 6
   <desc>       //To call an android intent

   <gastonaSecure> 0

   <help>
      //

   <aliases>
      alias

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    6      , //Opens the intent

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , action       ,       , //possible actions: VIEW, DIAL, SENDTO, EDIT by the moment
         1   , url          ,       , //Url to open the action

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample
      launching an intent

   <launching an intent>

      //#javaj#
      //
      //   <frames> main, "listix command INTENT example"
      //
      //   <layout of main>
      //
      //   EVA, 10, 10, 5, 5
      //   ---,  X
      //      ,  bDial
      //
      //#data#
      //
      //#listix#
      //
      //   <-- bDial>  ITENT, DIAL, 0034
      //

#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import java.util.*;
import java.io.File;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;

import android.net.*;
import android.content.Intent;
import android.app.Activity;
import android.webkit.WebView;

public class cmdIntent implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "INTENT",
          "ACTION",
       };
   }

   
   //public static void doEmail (String [] listTO, String [] listCC, String subject, String body, List filesList)
   public static void doEmail (listixCmdStruct cmd)
   {
      String [] listTO = cmd.takeOptionParameters ("TO");
      String [] listCC = cmd.takeOptionParameters ("CC");
      String subject = cmd.takeOptionString ("SUBJECT");
      String body = cmd.takeOptionString ("BODY");
      String [] optAttach = cmd.takeOptionParameters ("ATTACH");
      List filesList = new Vector ();
      while (optAttach != null)
      {
         for (int ii = 0; ii < optAttach.length; ii ++)
            filesList.add (optAttach[ii]);
         optAttach = cmd.takeOptionParameters ("ATTACH");
      }
         
      try
      {
         Intent procura = new Intent (Intent.ACTION_SEND_MULTIPLE);
         procura.setType("text/plain");
         procura.putExtra (Intent.EXTRA_EMAIL, listTO);
         if (listCC != null)
            procura.putExtra (Intent.EXTRA_CC,    listCC);
         if (subject != null)
            procura.putExtra (Intent.EXTRA_SUBJECT, subject);
         if (body != null)
            procura.putExtra (Intent.EXTRA_TEXT   , body);

         if (filesList != null && filesList.size () > 0)
         {
            ArrayList<Uri> arrUri = new ArrayList<Uri> ();
            for (int ii = 0; ii < filesList.size (); ii ++)
               arrUri.add (Uri.fromFile(new File((String) filesList.get (ii))));
            procura.putParcelableArrayListExtra (Intent.EXTRA_STREAM, arrUri);
         }
         
         androidSysUtil.getMainActivity ().startActivity(Intent.createChooser(procura, "Send mail..."));
         //androidSysUtil.getMainActivity ().startActivity(procura);
      }
      catch (Exception e)
      {
         cmd.getLog().err ("INTENT", "exception launching intent SEND_MULTIPLE [" + e + "]");
      }      
   }
   
   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commandEva, indxComm);

      String pAction = cmd.getArg (0); // !!! not to uppercase !!! thus allowing for example "android.intent.action.ALL_APPS"
      String laurl = cmd.getArg (1);
      String mime = cmd.getArg (2);

      String sAction = null;
      if (pAction.equalsIgnoreCase ("VIEW")) sAction = Intent.ACTION_VIEW;
      if (pAction.equalsIgnoreCase ("DIAL")) sAction = Intent.ACTION_DIAL;
      if (pAction.equalsIgnoreCase ("EDIT")) sAction = Intent.ACTION_EDIT;
      if (pAction.equalsIgnoreCase ("INSERT")) sAction = Intent.ACTION_INSERT;
      if (pAction.equalsIgnoreCase ("SENDTO")) sAction = Intent.ACTION_SENDTO;
      if (pAction.equalsIgnoreCase ("EMAIL") || 
          pAction.equalsIgnoreCase ("SEND_MULTIPLE") ||
          pAction.equalsIgnoreCase ("EMILIO")) 
      {
         doEmail (cmd);
         cmd.checkRemainingOptions (true);
         return 1;
      }

      if (sAction == null)
      {
         cmd.getLog().err ("INTENT", "jaaarls! intent [" + pAction + "] raahraal [" + Intent.ACTION_VIEW + "]");
         cmd.getLog().err ("INTENT", "jaaarls! all apps [" + Intent.ACTION_ALL_APPS + "]");

         sAction = pAction;
      }

      try
      {
         Intent intent = new Intent(sAction);
         Uri lauri = Uri.parse(laurl);
         if (mime != null && mime.length () > 0)
              intent.setDataAndType (lauri, mime);
         else intent.setData (lauri);
         androidSysUtil.getMainActivity ().startActivity(intent);
      }
      catch (Exception e)
      {
         cmd.getLog().err ("INTENT", "exception launching intent [" + e + "]");
      }

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
/*

Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","abc@gmail.com", null));
emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
startActivity(Intent.createChooser(emailIntent, "Send email..."));
----------------------

Intent send = new Intent(Intent.ACTION_SENDTO);
String uriText = "mailto:" + Uri.encode("email@gmail.com") + 
          "?subject=" + Uri.encode("the subject") + 
          "&body=" + Uri.encode("the body of the message");
Uri uri = Uri.parse(uriText);

send.setData(uri);
startActivity(Intent.createChooser(send, "Send mail..."));


-----------------------
whith attachment 

public static void email(Context context, String emailTo, String emailCC, String subject, String emailText, List<String> filePaths)
{
    //need to "send multiple" to get more than one attachment
    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
    emailIntent.setType("text/plain");
    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,   new String[]{emailTo});
    emailIntent.putExtra(android.content.Intent.EXTRA_CC,      new String[]{emailCC});
    //has to be an ArrayList
    ArrayList<Uri> uris = new ArrayList<Uri>();
    //convert from paths to Android friendly Parcelable Uri's
    for (String file : filePaths)
        uris.add (Uri.fromFile(new File(file)));

    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
}

-----------------------
whith attachment 



Intent intent = new Intent(Intent.ACTION_SEND);
intent.setType("text/plain");
intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"email@servo.com"});
intent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
intent.putExtra(Intent.EXTRA_TEXT, "body text");
Uri uri = Uri.parse("file://" + fileName);
intent.putExtra(Intent.EXTRA_STREAM, uri);
startActivity(Intent.createChooser(intent, "Send email"));


-----------------------
whith attachment 


Bitmap screenshot = Bitmap.createBitmap(_rootView.getWidth(), _rootView.getHeight(), Bitmap.Config.RGB_565);
_rootView.draw(new Canvas(screenshot));

String path = Images.Media.insertImage(getContentResolver(), screenshot, "title", null);
Uri screenshotUri = Uri.parse(path);

final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
emailIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
emailIntent.setType("image/png");

startActivity(Intent.createChooser(emailIntent, "Send email using"));

-----------------------
whith attachment 

          Intent sendIntent = new Intent(Intent.ACTION_SEND);
          	sendIntent.setType("image/jpeg");
        	sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Photo");
        	sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://sdcard/dcim/Camera/filename.jpg"));
        	sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the photo");
        	startActivity(Intent.createChooser(sendIntent, "Email:"));
*/