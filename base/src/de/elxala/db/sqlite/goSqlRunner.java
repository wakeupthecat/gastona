/*
packages de.elxala
Copyright (C) 2016  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.db.sqlite;


import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.io.*;
import java.util.*;

/**
   Wrapper for sqlRunner for goRhino (javascript by Rhino)
 */
public class goSqlRunner extends ScriptableObject
{
   private static final long serialVersionUID = 1578910199111811828L;
   
   private sqlRunner theObj = new sqlRunner ();

   /// The zero-parameter constructor.
   public goSqlRunner()
   {
   }
   
   /// The Java method defining the JavaScript File constructor.
   //
   @JSConstructor
   public static Scriptable goSqlRunner (Context cx, Object[] aa, Function ctorObj, boolean inNewExpr)
   {
      goSqlRunner result = new goSqlRunner ();
      return result;
   }

    /// Returns the name of this JavaScript class, "goSqlRunner".
    //
   @Override
   public String getClassName ()
   {
      return "goSqlRunner";
   }

   // NOTE:
   // CANNOT HAVE FUNCTIONS WITH SAME NAME AND DIFFERENT PARAMETERS!
   // IF SO, INVALIDATES ALL ScriptableObject.defineClass NOT ONLY FOR THIS CLASS BUT FOR ALL!
   //
   //@JSFunction
   //public void openScript ()
   //{
   //   theObj.openScript ();
   //}

   @JSFunction
   public void openScript ()
   {
      theObj.openScript ();
   }

   @JSFunction
   public void openScriptNoTransaction ()
   {
      // Note that we cannot have a single function "openScript(boolean)"
      // because the default meaning is different in the library (default means openScript(true) but in js would be false!)
      theObj.openScript (false);
   }

   @JSFunction
   public void closeScript ()
   {
      theObj.closeScript ();
   }

   @JSFunction
   public void writeScript (String statement)
   {
      theObj.writeScript (statement);
   }

   // NOTE:
   // CANNOT HAVE FUNCTIONS WITH SAME NAME AND DIFFERENT PARAMETERS!
   // IF SO, INVALIDATES ALL ScriptableObject.defineClass NOT ONLY FOR THIS CLASS BUT FOR ALL!
   //
   //@JSFunction
   //public void runSQL ()
   //{
   //   theObj.runSQL ("");
   //}
   //
   //@JSFunction
   //public void runSQL (String database)
   //{
   //   theObj.runSQL (database);
   //}
   
   
   @JSFunction
   public void runScript (String database)
   {
      theObj.runScript (database);
   }

   @JSFunction
   public void closeAndRunScript (String database)
   {
      theObj.closeScript ();
      theObj.runScript (database);
   }

   @JSFunction
   public void runSQL (String database, String sqlStatement)
   {
      theObj.runSQL (database, sqlStatement);
   }

   @JSFunction
   public String escapeString (String str)
   {
      return theObj.escapeString (str);
   }
   
   @JSFunction
   public String unEscapeString (String str)
   {
      return theObj.unEscapeString (str);
   }
   
    /**
     * Finalizer.
     *
     * Close the file when this object is collected.
     */
   @Override
   protected void finalize ()
   {
      // ?
   }
   

   //(Documentation of checkInstance from Rhino example File.java)
   /**
   * Perform the instanceof check and return the downcasted File object.
   *
   * This is necessary since methods may reside in the File.prototype
   * object and scripts can dynamically alter prototype chains. For example:
   * <pre>
   * js> defineClass("goSqlRunner");
   * js> o = {};
   * [object Object]
   * js> o.__proto__ = goSqlRunner.prototype;
   * [object goSqlRunner]
   * js> o.write("hi");
   * js: called on incompatible object
   * </pre>
   * The runtime will take care of such checks when non-static Java methods
   * are defined as JavaScript functions.
   */
   private static goSqlRunner checkInstance (Scriptable obj)
   {
      if (obj == null || !(obj instanceof goSqlRunner))
      {
         throw Context.reportRuntimeError("called on incompatible object");
      }
      return (goSqlRunner) obj;
   }
}


/*


#javaj#

   <frames> oSal

#listix#

   <jsTail>
     //var fix = new goSqlRunner ();
     //fix.runSQL ("", "CREATE TABLE tester (id, name); INSERT INTO tester VALUES (18, 'mi texto');");
     //"fin"

   <main>
      goRhino, @<jsTail>
      '
      '
      LOOP, SQL,, //SELECT * FROM tester
          ,, // @<id>: @<name>
          
*/