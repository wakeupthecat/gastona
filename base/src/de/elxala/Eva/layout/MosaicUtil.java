/*
package de.elxala.langutil
(c) Copyright 2006 Alejandro Xalabarder Aulet

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
   //(o) WelcomeGastona_source_javaj_layout EVAMOSAIC

   ========================================================================================
   ================ documentation for WelcomeGastona.gast =================================
   ========================================================================================

#gastonaDoc#

   <docType>    javaj_layout
   <name>       EVAMOSAIC
   <groupInfo>
   <javaClass>  de.elxala.Eva.layout.MosaicUtil
   <importance> 3
   <desc>       //Variant of Evalayout oriented to database forms

   <help>
      //
      //  This is a variant of the Evalayout, and a little bit experimental layout, to cover a very
      //  common kind of forms typically used in database applications. Of course it can be used
      //  directly but it is specially thought to be made through code generation.
      //
      //  Syntax:
      //
      //    <layout of NAME>
      //       EVAMOSAIC(X), marginX, marginY, gapX, gapY
      //
      //       header row1,  cell 1 1, size , cell 1 2, size, ...
      //       ...        ,  ...            , ...
      //       header rowM,  cell M 1, size , ...
      //
      //  The grid differs from Evalayout that it has not header columns and the rows are specified,
      //  after the header, always with pairs component name / size.
      //
      //  The different between EVAMOSAIC and EVAMOSAICX is that the second one expands horizontally
      //  the contained components.


   <examples>
      gastSample

      evaMosaic layout example

   <evaMosaic layout example>
      //#gastona#
      //
      //   <!PAINT LAYOUT> 1
      //
      //#javaj#
      //
      //   <frames>
      //      F, "example layout EVAMOSAIC(X)"
      //
      //   <layout of F>
      //
      //     EVAMOSAICX, 10, 11, 4, 5
      //
      //     A, ID  , 100, DATE, 200, TYPE,100
      //     A, NAME, 200, CITY, 200
      //     X, NOTES, 400
      //
      //
      //	<layout of ID>
      //		PANEL, X, ID
      //		eID
      //
      //	<layout of NAME>
      //		PANEL, X, Name
      //		eName
      //
      //	<layout of CITY>
      //		PANEL, X, City
      //		eCity
      //
      //	<layout of DATE>
      //		PANEL, X, Date
      //		eDate
      //
      //	<layout of TYPE>
      //		PANEL, X, Type
      //		cType
      //
      //	<layout of NOTES>
      //		PANEL, X, Notes
      //		xNotes


#**FIN_EVA#

*/


package de.elxala.Eva.layout;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration; // to traverse the HashTable ...
import java.awt.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;

/**
   @author    Alejandro Xalabarder
   @date      11.04.2006 22:32


*/
public class MosaicUtil
{

   /*
      converts a MOSAIC layout into a EvaLayout
      (see algorithm at the end of the file)


      INPUT:
         <ristras>
                 MOSAIC,10,11,4,5

                 A, eID ,100, eName, 300, eDate, 200, eType,100
                 X, eObserv, 600
                 A, eplus, 60, cAttrib, 60, eFin, 400

      OUTPUT:
         <ristras 2EvaLayout>
                 EVA,10,11,4,5

                 xxx, 60      , 40     , 20    , 280  , 120   , 80 , 100
                  A , eID     , -      , eName , -    , eDate , -  , eType
                  X , eObserv , -      , -     , -    , -     , -  , ""
                  A , eplus   , cAttrib, -    , eFin  , -     , "" , ""


   */
   public static Eva convertMosaicToEvaLayout (Eva mosaic, boolean extended)
   {
      Eva resultEva = new Eva (mosaic.getName () + " 2EvaLayout");

      // copy header
      EvaLine lin = new EvaLine ();
      lin.set (mosaic.get (0));

      resultEva.addLine (lin);
      resultEva.setValue ("EVA", 0, 0);
      resultEva.setValue ("xxx", 1, 0);

      // algorithm
      //


      // prepar mosaicLeft from the input mosaic

      Eva mosaicLeft = new Eva ("");

      for (int ii = 1; ii < mosaic.rows (); ii ++)
      {
         EvaLine eli = new EvaLine ();
         // copies the line
         eli.set (mosaic.get (ii));
         //removes the first element
         eli.removeElements (1);

         // copies the line exept the first element
         mosaicLeft.addLine (eli);

         // copy the row type to the result eva
         resultEva.setValue (mosaic.getValue (ii, 0), ii + 1, 0);
      }

      // loop
      //
      while (true)
      {
         // search the minimal column size
         //
         int minColRow  = -1;
         int minColSize = 0;
         for (int ii = 0; ii < mosaicLeft.rows (); ii ++)
         {
            if (mosaicLeft.get(ii).cols () <= 1) continue; // row consumed

            int width = stdlib.atoi (mosaicLeft.getValue (ii, 1));
            if (minColSize == 0 || minColSize > width)
            {
               minColSize = width;
               minColRow = ii;
            }
         }
         if (minColSize == 0 || minColRow == -1) break; // end of process ------>


         // found a colsize, we made a new column in the EvaLayout
         // and, for each row, try to add the new elements or extend it if already added
         // (name is "" if the element has been already added)

         // add a new column in the EvaLayout
         resultEva.addCol ("" + minColSize, 1);
         if (extended)
            resultEva.addCol ("X", 1);

         for (int ii = 0; ii < mosaicLeft.rows (); ii ++)
         {
            // add element or extend it

            //
            String compo = mosaicLeft.getValue (ii, 0);

            // incorporate the element or simply extend it
            //
            resultEva.addCol (compo, ii + 2);
            if (extended)
               resultEva.addCol ("-", ii + 2);

            // erase name of widget incorporated
            if (compo.length () > 0 && !compo.equals("-"))
               mosaicLeft.setValue ("-", ii, 0);

            // substract size of the treated element
            //
            int width = stdlib.atoi (mosaicLeft.getValue (ii, 1));
            if (width - minColSize <= 0)
            {
               // element in this row is finished, remove it (two columns)
               //
               mosaicLeft.get(ii).removeElements (2);
            }
            else
            {
               // substract minColSize
               mosaicLeft.setValue ("" + (width - minColSize), ii, 1);
            }
         }
     } // end loop


     return resultEva;
   }
}

/*

   El layout MOSAIC es parecido al EVALAYOUT pero

      - no tiene la cabecera de columnas
      - las filas tienen el formato : component, widthInPixels, component, widthInPixels, ...
      - la columna 0 se mantiene

   Hagamos paso a paso la siguiente conversio'n

      INPUT:
         <ristras>
                 MOSAIC,10,11,4,5

                 A, eID ,100, eName, 300, eDate, 200, eType,100
                 X, eObserv, 600
                 A, eplus, 60, cAttrib, 60, eFin, 400

      OUTPUT:
         <ristras 2EvaLayout>
                 EVA,10,11,4,5

                 xxx, 60      , 40     , 20    , 280  , 120   , 80 , 100
                  A , eID     , -      , eName , -    , eDate , -  , eType
                  X , eObserv , -      , -     , -    , -     , -  , -
                  A , eplus   , cAttrib, -    , eFin  , -     , -  , -


   ------------------ inicio

      Se prepara la Eva resultado copiando algunos datos de la eva input

            <input 2EvaLayout>

              EVA,10,11,4,5

              xxx,
               A ,
               X ,
               A ,

      Se prepara la Eva auxiliar mosaicLeft usada por el algoritmo

            <mosaicLeft>
               eID    , 100 , eName , 300, eDate, 200, eType,100
               eObserv, 600
               eplus1 , 60  , cAttrib, 60 , eFin, 400


   ------------------ loop 1

      se busca en mosaicLeft de la primera columna la que tenga el ancho ma's corto
      en este caso eplus1 de ancho 60

      se anyade una columna al resultado de ancho 60

            <input 2EvaLayout>

              EVA,10,11,4,5

              xxx, 60
               A ,
               X ,
               A ,

      se incorporan los campos (si no son nulos)


            <input 2EvaLayout>

              EVA,10,11,4,5

              xxx, 60
               A , eID
               X , eObserv
               A , eplus1

      se restan los anchos a los elementos incorporados y se borran (-) o se eliminan
      si su ancho es 0


         ANTES:
            <mosaicLeft>
               eID    , 100 , eName , 300, eDate, 200, eType,100
               eObserv, 600
               eplus1 , 60  , cAttrib, 60 , eFin, 400


         DESPUES:
            <mosaicLeft>
               -     , 40 , eName , 300, eDate, 200, eType,100
               -     , 540
               cAttrib, 60 , eFin, 400

   ------------------ loop 1

      ahora el ancho minimo es 40

            <input 2EvaLayout>

              EVA,10,11,4,5

              xxx, 60      , 40
               A , eID
               X , eObserv
               A , eplus1

      incorporar campos

            <input 2EvaLayout>

              EVA,10,11,4,5

              xxx, 60      , 40
               A , eID     , -
               X , eObserv , -
               A , eplus1  , cAttrib


      modificar mosaicLeft

            <mosaicLeft>
               eName  , 300, eDate, 200, eType,100
               -      , 500
               -      , 20 , eFin, 400


      etc
*/