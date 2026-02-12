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
   Wrapper for sqlSelect for goRhino (javascript by Rhino)
 */
public class goSqlSelect extends ScriptableObject
{
   private static final long serialVersionUID = 1578910199001811828L;
   private sqlSelect theObj = new sqlSelect ();

   /// The zero-parameter constructor.
   public goSqlSelect()
   {
   }

   /// The Java method defining the JavaScript File constructor.
   //
   @JSConstructor
   public static Scriptable goSqlSelect (Context cx, Object[] aa, Function ctorObj, boolean inNewExpr)
   {
      goSqlSelect result = new goSqlSelect ();
      if (aa.length > 0 && aa[0] != Context.getUndefinedValue())
      {
         String dbFile = Context.toString (aa[0]);
         String selectSql = aa.length > 1 ? Context.toString (aa[1]): null;
         String extraFilterSql = aa.length > 2 ? Context.toString (aa[2]): null;

         result.setSelectQuery (dbFile, selectSql, extraFilterSql);
      }
      return result;
   }


    /// Returns the name of this JavaScript class, "goSqlSelect".
    //
   @Override
   public String getClassName ()
   {
      return "goSqlSelect";
   }

   @JSFunction
   public int getRecordCount ()
   {
      return theObj.getRecordCount ();
   }

   @JSFunction
   public int getColumnCount ()
   {
      return theObj.getColumnCount ();
   }

   @JSFunction
   public int getColumnIndex (String columnName)
   {
      return theObj.getColumnIndex (columnName);
   }

   @JSFunction
   public String getColumnName (int col)
   {
      return theObj.getColumnName (col);
   }

   @JSFunction
   public void setSelectQuery (String databaseFile, String sqlSelect, String extraFilter)
   {
      theObj.setSelectQuery (databaseFile, sqlSelect, extraFilter);
   }

   @JSFunction
   public String getValue (int row, int col)
   {
      return theObj.getValue (row, col);
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

   //@TODO
   // introduced 2019.09.23 but actually it does not work, javascript can call it but no object is returned
   // based on goFile readLines (), does this function works ?
   //
   @JSFunction
   public Object getColumnNames ()
   {
      String [] columns = new String [getColumnCount ()];
      for (int ii = 0; ii < columns.length; ii++)
         columns[ii] = getColumnName (ii);

      return Context.getCurrentContext().newObject (ScriptableObject.getTopLevelScope(this), "Array", columns);
   }

   //@TODO
   // introduced 2019.09.23 but actually it does not work, javascript can call it but no object is returned
   // based on goFile readLines (), does this function works ?
   //
   @JSFunction
   public Object getRecord (int row)
   {
      ScriptableObject desc = new NativeObject();
      // ScriptRuntime.setBuiltinProtoAndParent(desc, ScriptableObject.getTopLevelScope(this), TopLevel.Builtins.Object);

      desc.defineProperty(getColumnName(0), getValue (row, 0), ScriptableObject.DONTENUM);
      return desc;
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
   * js> defineClass("goSqlSelect");
   * js> o = {};
   * [object Object]
   * js> o.__proto__ = goSqlSelect.prototype;
   * [object goSqlSelect]
   * js> o.write("hi");
   * js: called on incompatible object
   * </pre>
   * The runtime will take care of such checks when non-static Java methods
   * are defined as JavaScript functions.
   */
   private static goSqlSelect checkInstance (Scriptable obj)
   {
      if (obj == null || !(obj instanceof goSqlSelect))
      {
         throw Context.reportRuntimeError("called on incompatible object");
      }
      return (goSqlSelect) obj;
   }
}


/*

#javaj#

   <frames> oSal

#listix#

   <content>
      id, name
      1282, emilia
      3243, samba pati

   <rewrite>
     'var fix = new goSqlSelect ("", "SELECT * FROM content");
     'var fo = new goFile ();
     '
     'if (fo.fopen (":mem out", "w")) {
     '   for (var ii = 0; ii < 2; ii ++) {
     '       fo.writeLine ("te comento: " + fix.getValue (ii, 0) + ") " + fix.getValue (ii, 1));
     '   }
     '   fo.fclose ();
     '}
     '"fin"

   <main>
      DB,, CREATE TABLE,  content
      '
      goRhino, @<rewrite>
      '
      '
      LOOP, TEXT FILE, :mem out
          ,, @<value>
*/