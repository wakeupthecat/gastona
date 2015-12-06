/*
package javaj.widgets
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


package de.elxala.Eva.abstractTable;

import de.elxala.Eva.*;
import de.elxala.zServices.*;

// NOTA 29.06.2008 13:53: quitar acentos por el p problema con gcj "error: malformed UTF-8 character." de los c

/*
   //(o) TODO_elxala_eva  this class should be in de.elxala.Eva instead of abstractTable

   base class that relates a name (a String) with a data and a control (EvaUnits)


   EBS class diagram
   ----------------------------------------------------------------------------------



                   baseEBS(*e)
           -----------------------------------
            ^                             ^
            |                             |
            |                             |
      widgetEBS(*w)                      tableEvaDataEBS(*e)
   ----------------------          ------------------------------
            ^                          ^                 ^
            |                          |                 |
   used by almost all            tableEvaDB(*e)   absTableWindowingEBS(*e)
   zWidgets except those                           -----------------------------
   which data is table based                             ^                 ^
                                                         |                 |
                                                         |                 |
                                                   tableROSelect(*s)   tableWidgetBaseEBS(*t)
                                                                       ---------------------
                                                                           ^
                                                                           |
                                                                       tableEBS(*t)



            from package ...
      (*e) de.elxala.Eva.abstractTable
      (*w) javaj.widgets.basics
      (*t) javaj.widgets.table
      (*s) de.elxala.db.sqlite

*/
public class baseEBS
{
   protected static logger log = new logger (null, "de.elxala.Eva.baseEBS", null);

   // Note that this attribute is used transparently by this base class
   // in theory nobody should be interested in usign it
   protected static final String sATTR_VAR  = "var";

   public static final int DATA = 0;
   public static final int CONTROL = 1;

   public static boolean STACK_TRACE_ON_ERROR = true;

   protected String theName = "";
   protected EvaUnit [] EUContainers = new EvaUnit[] { null, null };

   private EvaUnit EvaUnitFalsa = new EvaUnit ("$invalid$");
   private Eva     EvaFalsa     = new Eva     ("$invalid$");

   private boolean firstTimeWithDataAndControl = false;

//`??????????   protected logger log = logStatic;


//   public baseEBS ()
//   {
//      System.out.println ("ERRONEO CONCETO! se crea una baseEBS anonimatica!");
//   }

   public baseEBS (String name, EvaUnit pData, EvaUnit pControl)
   {
//      System.out.println ("ha nacido una baseEBS [" + name + "]!");
      setNameDataAndControl (name, pData, pControl);
   }

   // NOTE (C++) : this is NOT a copy constructor!
   public baseEBS (baseEBS ebs)
   {
      setNameDataAndControl (ebs);
   }

   public String  getName    ()   { return theName; }
   public EvaUnit getData    ()   { return EUContainers[DATA]; }
   public EvaUnit getControl ()   { return EUContainers[CONTROL]; }

   public boolean hasName    ()   { return getName () != null; }
   public boolean hasData    ()   { return getData () != null; }
   public boolean hasControl ()   { return getControl () != null; }
   public boolean hasAll ()       { return hasName() && hasData() && hasControl(); }
   public boolean firstTimeWithAll () { return hasAll () && firstTimeHavingDataAndControl (); }

   public boolean firstTimeHavingDataAndControl ()
   {
      return firstTimeWithDataAndControl;
   }

   /**
      Forms the eva name of an attribute 'attName' that will be used in data or control
   */
   public String evaName(String attName)
   {
      return theName + ((attName.length () == 0) ? "": " ") + attName;
   }

   public String evaName(String attName, boolean asReference)
   {
      return theName + ((attName.length () == 0) ? "": " ") + attName + (asReference ? " " + sATTR_VAR: "");
   }

   /**
   */
   public void setNameDataAndControl (baseEBS ebs)
   {
      setNameDataAndControl (ebs.getName (), ebs.getData (), ebs.getControl ());
   }

   /**
      @arg nameEBS   name of the structure or null if this has not to be changed
      @arg pData     EvaUnit representing the data or null is this has not to be changed
      @arg pControl  EvaUnit representing the control or null is this has not to be changed
   */
   public void setNameDataAndControl (String nameEBS, EvaUnit pData, EvaUnit pControl)
   {
      // Update firstTimeWithDataAndControl
      //
      if (hasData () ^ hasControl())
      {
         //possible firstTime
         if (!hasData () && pData != null)
            firstTimeWithDataAndControl = true;
         if (!hasControl () && pControl != null)
            firstTimeWithDataAndControl = true;
      }
      else firstTimeWithDataAndControl = false;

      // has to be the first one! (see function msg)
      if (nameEBS != null)
         theName = nameEBS;

      if (pControl != null)
      {
         checkContainerChange (EUContainers[CONTROL], pControl, "CONTROL");
         EUContainers[CONTROL] = pControl;
      }

      if (pData != null)
      {
         checkContainerChange (EUContainers[DATA], pData, "DATA");
         EUContainers[DATA] = pData;
      }
   }

   private void checkContainerChange (EvaUnit contOrig, EvaUnit contNew, String contType)
   {
      if (contOrig != null && contOrig != contNew)
      {
         //quasi warning!
         // check change of container, usually should not happen but it is legal
         log.dbg (0, "zWidget:" + theName, "change of " + contType + " container in widget !");
      }
      else
         log.dbg (2, "zWidget:" + theName, "update " + contType + " received");

   }

   /*
      returns the EvaUnit for the control of the EBS
      if this does not already exists then prints out an error message
      and returns a dummy EvaUnit
   */
   public EvaUnit mustGetContainer (int containerID)
   {
      if (containerID != DATA && containerID != CONTROL)
      {
         log.severe ("mustGetContainer", "wrong containerID " + containerID + ", it might be only either DATA(0) or CONTROL(1) !");
         return EvaUnitFalsa;
      }

      if (EUContainers[containerID] == null)
      {
         log.severe ("mustGetContainer", "container " + (containerID == DATA ? "DATA": "CONTROL") + " is required but it is null!");
         return EvaUnitFalsa;
      }
      return EUContainers[containerID];
   }

   /**
      get the Eva for data, it first looks if the attribute "var"
      is given and has a value, in that case the referenced eva is used
      otherwise the usual data eva is used (same name as the widget)
   */
   public Eva mustGetEvaData ()
   {
      Eva eData = getAttribute (DATA, true, "");
      if (eData == null)
      {
         // should not happen after getAttribute with force = true!
         log.fatal ("mustGetEvaData", "Could not create dada for [" + evaName ("") + "]!");
         return null;
      }

      // ensure 1 row, 1 col
      if (eData.get(0) == null)
      {
         //(o) elxala_Eva forcing 1 row and 1 column in EBS variables
         //         10.01.2010 14:37
         //         I don't know if this is the best stategy but changing this
         //         cannot be done without lots of tests
         eData.addRow ("");
      }

      return eData;

////////      // do not force a widget to have CONTROL which id needed for getDataInto()
////////      String ref = hasControl () ? getVar (): null;
////////
////////      if (ref == null || ref.length () == 0)
////////      {
////////         //log().dbg (9, "baseEBS.mustGetEvaData", "data not from another eva");
////////         return mustGetMainEvaData ();
////////      }
////////
////////      //log().dbg (9, "baseEBS.mustGetEvaData", "data from another eva [" + ref + "]");
////////      Eva eva = mustGetContainer (DATA).getEva (ref);
////////      if (eva == null)
////////      {
////////         log.err ("mustGetEvaData", "reference data (dataInto) named <" + ref + "> not found!");
////////         eva = new Eva (ref);
////////         mustGetContainer (DATA).add (eva);
////////      }
////////
////////      return eva;
   }

//NOTE: 10.01.2010 13:26 method removed because it was not used at all
//                   The idea is to get an attribute giving the final name (same name as the eva)
//                   and  not forming the evaname with (name + " " + attName)
//                   if needed, it can be implemented but better with another more clear name
//   /**
//      gets the value of a global data attribute (independent of 'attName')
//      the data must exist, if the attribute container still does not exists
//      null will be returned
//   */
//   public String getGlobalAttribute (int containerID, String attName)
//   {
//      Eva eva = mustGetContainer (containerID).getEva (attName);
//      if (eva == null) return null;
//
//      return eva.getValue (0, 0);
//   }

   public String getSimpleDataAttribute (String attName)
   {
      Eva eva = getAttribute(DATA, false, attName);
      if (eva == null) return null;

      return eva.getValue (0, 0);
   }

   /**
      gets the value of a simple data attribute with name 'attName'
      the data must exist, if the attribute container still does not exists
      null will be returned
   */
   public String getSimpleAttribute (int containerID, String attName)
   {
      Eva eva = getAttribute(containerID, false, attName);
      if (eva == null) return null;

      return eva.getValue (0, 0);
   }

   /**
      returns always a value for the given attribute of the containerID
      if the attribute exists then returns its value if not then the given 'defaultValue' is returned
      Anyway the container must exist.
   */
   public String getSimpleAttribute (int containerID, String attName, String defaultValue)
   {
      String val = getSimpleAttribute (containerID, attName);
      if (val == null)
         val = defaultValue;

      return val;
   }

   /**
      set the value 'valor' into a simple data attribute with name 'attName'
      the data must exist but the attribute container will be created if needed

      @arg dataLine only admited DATA or CONTROL
   */
   public void setSimpleAttribute (int containerID, String attName, String value)
   {
      mustGetContainer (containerID).getSomeHowEva (evaName (attName)).setValue (value, 0, 0);
   }

   /**
      gets the Eva variable attribute with name 'attName' from the DATA container
      the container must exist, if the Eva variable still does not exists a null is returned.
   */
   public Eva getDataAttribute (String attName)
   {
      return getAttribute (DATA, false, attName);
   }


   /**
      gets the Eva variable attribute with name 'attName' from the container 'containerID'
      the container must exist, if the Eva variable still does not exists a null is returned.
      This is the way a default value
   */
   public Eva getAttribute (int containerID, String attName)
   {
      return getAttribute (containerID, false, attName);
   }

   /**
      gets the Eva variable attribute with name 'attName' from the container 'containerID'
      the container must exist, if the Eva variable still does not exists a null is returned.
      This is the way a default value
   */
   public Eva getAttribute (int containerID, boolean force, String attName)
   {
      //first try [name + attribute + " var"] (no force!!)
      //
      Eva eva = findReferencedVariable (containerID, attName);
      if (eva != null)
         return eva;

      // not found as <name attName var> then normal search
      //
      if (force)
      {
         eva = mustGetContainer (containerID).getSomeHowEva (evaName (attName));
      }
      else eva = mustGetContainer (containerID).getEva (evaName (attName));
      return eva;
   }

   private Eva findReferencedVariable (int containerID, String attName)
   {
      Eva evaVar = mustGetContainer (containerID).getEva (evaName (attName, true));
      if (evaVar == null) return null;

      // check that it is a variable name (single string)
      if (evaVar.rows () > 1 || evaVar.cols (0) > 1)
      {
         log.err ("getAttribute", "Variable referece <" + evaName (attName, true) + "> has " + evaVar.rows () + " rows and the first one " + evaVar.cols (0) + " columns (only 1 row and 1 column is accepted)");
         //System.err.println ("baseEBS::getAttribute <" + evaName (attName + " " + ATTVAR) + "> has " + evaVar.rows () + " rows and the first one " + evaVar.cols (0) + " columns (only 1 row and 1 column is accepted)");
         return null;
      }

      // get the variable referred
      Eva eva = mustGetContainer (containerID).getEva (evaVar.getValue (0,0));
      if (eva == null)
      {
         log.err ("getAttribute", "referenced variable named <" + evaVar.getValue (0,0) + "> given in <" + evaName (attName, true) + "> not found!");

         // ---- should we ?
         // eva = new Eva (ref);
         // mustGetContainer (DATA).add (eva);
      }
      return eva;
   }
}
