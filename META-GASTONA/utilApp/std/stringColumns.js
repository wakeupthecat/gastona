// ------------- stringColumns.js
//    Utility to format a text given in different columns
//    with a fixed column width with a maximum
//
// There are two possible calls
//
//    getStringColumns
//       returns an array with the text formatted
//
//    outListixPrint
//       print out on the fly using listix.printTextLsx goRhino function
//
// both function accept the same parameters
//
//    getStringColumns (txttable, maxcol, s_start, s_gap, s_end)
//    outListixPrint   (txttable, maxcol, s_start, s_gap, s_end)
//
// where
//    txttable : an array of rows each consisting of an array of strings as many as columns
//    maxcol   : maximum number of characters per column
//    s_start  : string for the start of each row plus additional rows as needed
//    s_gap    : string that separate each column
//    s_end    : string for ending each row plus additional rows as needed
//
// Example:
//
//    var texte = [[ "This is a text somehow too long to be fit in a single row", "second column", "and the third one"],
//                 [ "second Row", "again here a long text to see the behavior", "last but not least line with some text to be formatted" ],
//                ];
//
//       var aa = getStringColumns (texte, 20, "[ ", " | ", " ]");
//
//       for (var rr in aa)
//            out (aa[rr]);
//
//   and the output would be:
//
//      [ This is a text someh | second column        | and the third one    ]
//      [ ow too long to be    |                      |                      ]
//      [  fit in a single row |                      |                      ]
//      [ second Row           | again here a long te | last but not least l ]
//      [                      | xt to see the behavi | ine with some text   ]
//      [                      | or                   |  to be formatted     ]
//

function outArray (sortida, str)
{
   sortida.push (str);
}

function outListixPrint (sortida, str)
{
   listix.printTextLsx (str);
   listix.newLineOnTarget ();
}

function getStringColumns (txttable, maxcol, s_start, s_gap, s_end)
{
   return stringColumns (txttable, maxcol, s_start, s_gap, s_end, outArray);
}

function printStringColumns (txttable, maxcol, s_start, s_gap, s_end)
{
   return stringColumns (txttable, maxcol, s_start, s_gap, s_end, outListixPrint);
}

function stringColumns (txttable, maxcol, s_start, s_gap, s_end, outfunc)
{
   outfunc = outfunc||outArray;

   // repeat character,
   // note that string.repeat () is not supported by all browsers!
   function repchar (cha, times)
   {
      var str = "";
      for (var ii = 0; ii < times; ii ++)
         str += cha;
      return str;
   }

   // define how short may be a word in order to not to be separated
   // it depends on the column width
   function shortLen (forWidth)
   {
      return forWidth < 10 ? 0: Math.min (forWidth * .15, 12);
   }


   var wcols = [];

   // compute col widths
   for (var rr in txttable)
      for (var cc in txttable[rr])
      {
         var was = (wcols[cc]||{width:0}).width;
         wcols[cc] = { ptread: 0,
                       width: Math.min(maxcol, Math.max(txttable[rr][cc].length, was))
                     };
      }

   var salida = [];

   // print all rows
   for (var rr in txttable)
   {
      var queda = true;
      for (var cc in txttable[rr])
         wcols[cc].ptread = 0;

      while (queda)
      {
         var linea = [];
         queda = false;
         for (var cc in txttable[rr])
         {
            var line = txttable[rr][cc];
            var lentotake = wcols[cc].width;
            var powi = wcols[cc].ptread + wcols[cc].width - 1;

            // check if last word is cut and try to set it in the next line
            //
            if (line.length > wcols[cc].width && line.substr(powi).match(/[0-9,a-z,A-Z]/))
            {
               var bla1 = line.lastIndexOf (' ', powi); // position of first blank before last word
               var bla2 = line.indexOf (' ', powi + 1); // position of first blank after last word
               if (bla2 > 0 && (bla2 - bla1 - 1) <= shortLen (wcols[cc].width))
               {
                  // set it to next line
                  lentotake -= (powi - bla1 + 1);
               }
            }

            var tros = txttable[rr][cc].substr(wcols[cc].ptread, lentotake);
            wcols[cc].ptread += lentotake;

            queda |= line.length > wcols[cc].ptread;

            tros = tros + repchar(" ", wcols[cc].width - tros.length);
            linea.push (tros);
         }
         outfunc (salida, (s_start||"") + linea.join (s_gap||"") + (s_end||""));
      }
   }

   return salida;
}
