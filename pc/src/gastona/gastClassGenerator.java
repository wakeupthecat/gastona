/*
library de.elxala
Copyright (C) 2005 to 2012 Alejandro Xalabarder Aulet

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

package gastona;

//import org.gastona.*;
import de.elxala.Eva.*;
import de.elxala.langutil.filedir.*;
import de.elxala.langutil.*;
import de.elxala.mensaka.*;
import de.elxala.zServices.*;


/**
*/
public class gastClassGenerator
{
   public static void generateGastClass (String gastName, EvaUnit gastU, EvaUnit gastJ, EvaUnit gastD, EvaUnit gastL)
   {
      TextFile fi = new TextFile ();
      if (!fi.fopen (gastName + "GastClass.java", "w"))
      {
         System.err.println ("Cannot create file [" + gastName + "GastClass.java]!");
         return;
      }
      
      fi.writeLine ("//package com.wakeupthecat.gastClasses;");
      fi.writeLine ("");
      fi.writeLine ("import de.elxala.Eva.*;");
      fi.writeLine ("");
      fi.writeLine ("public class " + gastName + "GastClass");
      fi.writeLine ("{");
      writeLoader (fi, "gastona", gastU);
      writeLoader (fi, "javaj", gastJ);
      writeLoader (fi, "guix", gastJ);
      writeLoader (fi, "data", gastD);
      writeLoader (fi, "listix", gastL);
      
      fi.writeLine ("   public static void dump ()");
      fi.writeLine ("   {");
      fi.writeLine ("      System.out.println (\"#gastona#\");");
      fi.writeLine ("      System.out.println (getgastona ());");
      fi.writeLine ("      System.out.println (\"#javaj#\");");
      fi.writeLine ("      System.out.println (getjavaj ());");
      fi.writeLine ("      System.out.println (\"#data#\");");
      fi.writeLine ("      System.out.println (getdata ());");
      fi.writeLine ("      System.out.println (\"#listix#\");");
      fi.writeLine ("      System.out.println (getlistix ());");
      fi.writeLine ("   }");
      fi.writeLine ("");      
      fi.writeLine ("   public static void main (String [] aa)");
      fi.writeLine ("   {");
      fi.writeLine ("      new org.gastona.mensaka4listix (getlistix (), getjavaj (), getdata (), aa);");
      fi.writeLine ("   }");
      fi.writeLine ("}");
      fi.writeLine ("");
      fi.fclose ();      
   }
   
   private static void writeLoader (TextFile fi, String unitName, EvaUnit eunit)
   {
      fi.writeLine ("   public static EvaUnit get" + unitName + " ()");
      fi.writeLine ("   {");
      fi.writeLine ("      Eva eva = null;");
      fi.writeLine ("      String oneLine = null;");
      fi.writeLine ("      EvaUnit eu = new EvaUnit(\"" + unitName + "\");");
      for (int ii = 0; ii < eunit.size (); ii ++)
      {
         Eva eva = eunit.getEva(ii);
         fi.writeLine ("      eva = new Eva(\"" + eva.getName () + "\");");
         for (int jj = 0; jj < eva.rows (); jj ++)
         {
            //String oneLine = eva.get(jj).toString ();
            Cadena str = new Cadena (eva.get(jj).toString ());
            str.replaceMe ("\"", "\\\"");
            fi.writeLine ("      eva.addLine (new EvaLine (\"" + str.o_str + "\"));");
         }
         fi.writeLine ("      eu.add (eva);");
      }
      fi.writeLine ("      return eu;");
      fi.writeLine ("   }");
      fi.writeLine ("");
   }
}

