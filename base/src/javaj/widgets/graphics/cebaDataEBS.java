/*
package javaj.widgets.graphics;
Copyright (C) 2013-2022 Alejandro Xalabarder Aulet

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
package javaj.widgets.graphics;

import de.elxala.zServices.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import javaj.widgets.graphics.objects.*;
import java.util.*;
import de.elxala.Eva.*;
import de.elxala.Eva.abstractTable.baseEBS;

/**
   2022-03-19 13:20:03 get from relosInMotion.java
*/
public class cebaDataEBS
{
   private static logger log = new logger (null, "javaj.widgets.graphics.cebaData", null);

   protected baseEBS ebs = new baseEBS ("noname", new EvaUnit (), null);

   public uniPath mainLayerShape = null;

   public cebaDataEBS (String varname)
   {
      loadData (varname, null);
   }

   public void loadData (String name, EvaUnit euCeba)
   {
      mainLayerShape = new uniPath ();
      ebs = new baseEBS (name, (euCeba != null ? euCeba: new EvaUnit ("data")), null);
      mainLayerShape.getEdiPaths ().parseTrassosFromEva (ebs.getEnsureDataAttribute ("mainLayerShape"));
   }

   public baseEBS getDataEbs ()
   {
      return ebs;
   }

   public uniPath getUniPath ()
   {
      return mainLayerShape;
   }

   public editablePaths getEdiPaths ()
   {
      return getUniPath ().getEdiPaths ();
   }

   private String uniPathContentsOrEmpty (uniPath upa)
   {
      if (upa == null) return "";
      return upa.getEdiPaths ().toString ();
   }

   // save data from editables shapes to baseEBS object (text form)
   //
   public void saveData ()
   {
      ebs = new baseEBS (ebs.getName (), new EvaUnit ("data"), null);
      if (mainLayerShape != null)
         mainLayerShape.getEdiPaths ().dumpIntoEva (ebs.getEnsureDataAttribute ("mainLayerShape"));
   }

   public void saveRm3 (String filename)
   {
      saveData ();

      Eva evaTrassos = getDataEbs ().getDataAttribute ("mainLayerShape");
      if (evaTrassos == null)
      {
         log.dbg (4, "saveRm3", "no mainLayerShape");
         return;
      }

      if (filename == null || filename.length () == 0)
         filename = DateFormat.getStr (new Date (), "yyyyMMdd_HH_mm_ss_S") + ".rm3";
      System.out.print ("saving in [" + filename + "] ... ");

      TextFile fitx = new TextFile ();
      if (!fitx.fopen (filename, "w"))
      {
         System.err.println ("Cannot create file [" + filename + "]!");
         return;
      }

      for (int rr = 0; rr < evaTrassos.rows (); rr ++)
      {
         for (int cc = 0; cc < evaTrassos.cols (rr); cc ++)
            fitx.writeString (evaTrassos.getValue (rr, cc) + ", ");

         fitx.writeLine ("");
      }
      fitx.fclose ();
      log.dbg (4, "saveRm3", "done");
   }

   public void loadRm3 (String filename)
   {
      TextFile fitx = new TextFile ();
      if (!fitx.fopen (filename, "rt"))
      {
         log.err ("loadRm3", "Cannot open for read file [" + filename + "]!");
         return;
      }
      log.dbg (4, "loadRm3", "loading [" + filename + "] ... ");

      Eva evaTrassos = new Eva (ebs.getName () + " mainLayerShape");

      // main challenge:
      //     z, 173, 53, "style etc", jau,3,40, ...
      // has to be converted into eva
      //     z, 173, 53, "style etc", //jau,3,40, ...

      while (fitx.readLine ())
      {
         // string parsing based on EvaLine.java
         // we parse cells and if first is "z" we get the 4th cell as one string
         //
         boolean firstZ = false;
         int indxCol = 0;
         stringCursor strcu = new stringCursor (fitx.TheLine ());

         List vcells = new Vector ();
         while (!strcu.ended ())
         {
            String quepo = EvaLine.getNextToken (strcu, ',', true);
            if (firstZ && indxCol == 4)
            {
               vcells.add (quepo + "," + strcu.strPoint ());
               log.dbg (9, "loadRm3", "velcevu encontr [" + strcu.strPoint () + "] ... ");
               break;
            }
            if (quepo != null)
            {
               if (indxCol == 0)
                  firstZ = quepo.equalsIgnoreCase("z");

               log.dbg (9, "loadRm3", "adosific [" + quepo + "] ... ");
               vcells.add (quepo);
               indxCol ++;
            }
         }

         // make the string array and add it
         //
         String [] arr_cols = new String[vcells.size ()];
         for (int ii = 0; ii < vcells.size (); ii ++)
            arr_cols[ii] = (String) vcells.get(ii);

         evaTrassos.addLine (arr_cols);
      }
      fitx.fclose ();

      EvaUnit euData = new EvaUnit ("data");
      euData.add (evaTrassos);

      log.dbg (9, "loadRm3", "pastos [" + euData + "] ... ");

      loadData (ebs.getName (), euData);
   }
}
