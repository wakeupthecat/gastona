   // Object to generate deep connected SQL SELECT queries
   //
   // The function deepTable returns an object which has two methods
   //    addConnection : to add named connections between tables
   //                    tables owing connections are now deep tables
   //                    where not only its columns can be queried but also
   //                    the columns from its connections.
   //    deepSelect    : resolves a deep SELECT 
   //
   // Example:
   //
   //   Suppose we have these two tables to be joined
   //
   //       tabSongs   (songId, title, artId);
   //       tabArtists (id, name, countryId);
   //
   //   following code 
   //
   //       var deep = deepTable ();
   //       deep.addConnection ( "artist, tabSongs, artId, tabArtists, id");
   //       var SQL = deep.deepSelect ("tabSongs", "artId, artist->name, title", true)
   //
   //   where the deepSelect pretends to mimic an hypothetical DEEP SQL
   //
   //       DEEP_SELECT artId, artist->name, title FROM DEEP_tabSongs;
   //
   //   will generate the SQL string from the new "deep" tabSongs
   //
   //       SELECT
   //          tabSongs.artId AS artId,
   //          artist.name AS artist_name,
   //          tabSongs.title AS title
   //       FROM
   //          tabSongs,
   //          tabArtists AS artist
   //       WHERE
   //          tabSongs.artId == artist.id
   //
   function deepTable ()
   {
      var conns = {};

      // trim for IE8
      if (typeof String.prototype.trim !== 'function') {
         String.prototype.trim = function() {
            return this.replace(/^\s+|\s+$/g, '');
         }
      }

      return {
         addConnection: addConnection,
         deepSelect: deepSelect,
      }

      // Add a connection or a part of a connection
      // Connection can be given in any of these three types: string, array, object
      //
      function addConnection (connection)
      {
         if (!connection) return;

         if (typeof connection === "string")
         {
            // example  "customer, tSales, customerId, tCompanies, id"
            connection = connection.split (",");
         }

         if (connection instanceof Array)
         {
            if (connection.length < 3) return;
            connection = { name     : connection[0].trim(),
                           srcTable : connection[1].trim(),
                           srcKey   : connection[2].trim(),
                           tgtTable : (connection[3]||"").trim(),
                           tgtKey   : (connection[4]||"").trim()
                         };
         }

         // finnally connection is an object
         if (!connection["name"] || !connection["srcTable"]) return;

         // index it by "table connection_name"
         conns[connection["srcTable"] + " " + connection["name"]] = connection;
      }

      function deepSelect (baseTable, deepColumns, lineBreaks)
      {
         var fromList = [ baseTable ];
         var whereList = [];
         var selectList = [];

         function addUnique (arr, elem)
         {
            if (arr.indexOf (elem) === -1)
               arr.push (elem);
         }

         function deepColumn (deepCol)
         {
            var arro = deepCol.split ("->");
            if (arro.length === 1) {
               // no "->" found try with space
               arro = deepCol.trim ().split (/\s* \s*/); // like split(" ") but avoiding empty elements
            }

            var nowBase = baseTable;
            var aliasTable = baseTable;

            function shortAlias (alias)
            {
               return (alias.length === baseTable.length) ? baseTable: alias.substring (baseTable.length + 1);
            }

            for (var connIndx = 0; connIndx < arro.length - 1; connIndx ++)
            {
               var lacon = conns [nowBase + " " + arro[connIndx].trim ()];
               if (!lacon) {
                  console.log ("Error : deepSelect, connection " + nowBase + " " + arro[connIndx].trim () + " not found!");
                  continue;
               }
               var aliasTableCon = aliasTable + "_" + lacon.name;

               // add element to FROM array
               //
               addUnique (fromList, lacon.tgtTable + " AS " + shortAlias (aliasTableCon));

               var connCond = [];
               if (typeof lacon.srcKey === "string")
               {
                  // only one-to-one key
                  connCond.push (shortAlias(aliasTable) + "." + lacon.srcKey + " == " + shortAlias(aliasTableCon) + "." + lacon.tgtKey);
               }
               else
               {
                  // handle n-to-n key
                  for (cc in lacon.srcKey)
                     connCond.push (shortAlias(aliasTable) + "." + lacon.srcKey[cc] + " == " + shortAlias(aliasTableCon) + "." + lacon.tgtKey[cc]);
               }

               // add element to WHERE array
               //
               addUnique (whereList, connCond.join (" AND "));

               aliasTable = aliasTableCon;
               nowBase = lacon.tgtTable;
            }

            var finalColumnName = arro[arro.length - 1].trim ();
            selectList.push (shortAlias (aliasTable) + "." + finalColumnName + " AS " + shortAlias (aliasTable + "_" + finalColumnName));
         }

         // example deepColumns as string : "  id , customer->name, customer country iso3  "
         // example deepColumns as array  : ["id" , "customer->name", "customer country iso3" ]

         var colArray = (typeof deepColumns === "string") ? deepColumns.split(","): deepColumns;

         for (var dc in colArray)
            deepColumn (colArray[dc].trim ());
         
         var SEP1 = lineBreaks ? "\n": " ";
         var SEP3 = lineBreaks ? "\n   ": " ";

         return "SELECT" + SEP3 + selectList.join ("," + SEP3) + SEP1 + "FROM" + SEP3 + fromList.join ("," + SEP3) + SEP1 + "WHERE" + SEP3 + whereList.join (" AND" + SEP3);
      }
   }
   
   // ANOTHER EXAMPLE:
   //
   //   var deeta = deepTable ();
   //
   //   deeta.addConnection ( "customer, tSales     , customerId , tCompanies    , id    ");
   //   deeta.addConnection ( "product , tSales     , productId  , tProducts     , prodID");
   //   deeta.addConnection ( "provider, tProducts  , providerID , tCompanies    , id    ");
   //   deeta.addConnection ( "country , tCompanies , countryId  , tISOCountries , isoA2 ");
   //
   //   out (deeta.deepSelect ("tSales", "date, product->name");
   //   out (deeta.deepSelect ("tSales", ["id", "date",
   //                                     "customer -> name",
   //                                     "customer country name",
   //                                     "product->name",
   //                                     "quantity",
   //                                     "product provider name",
   //                                     "product->provider->country->name" ));
   //
   //   the first deepSelect outputs
   //
   //      SELECT
   //         tSales.date AS date,
   //         product.name AS product_name
   //      FROM
   //         tSales,
   //         tProducts AS product
   //      WHERE
   //         tSales.productId == product.prodID
   //
   //   the second one a much longer query ...
   //
   


//---- Without using shortAlias
//
//SELECT
//   tabSongs.artId AS tabSongs_artId,
//   tabSongs_artist.name AS tabSongs_artist_name,
//   tabSongs.title AS tabSongs_title
//FROM
//   tabSongs,
//   tabArtists AS tabSongs_artist
//WHERE
//   tabSongs.artId == tabSongs_artist.id


  
//---- Using shortAlias
//SELECT
//   tabSongs.artId AS artId
//   , artist.name AS artist_name
//   , tabSongs.title AS title
//FROM
//   tabSongs
//   , tabArtists AS artist
//WHERE
//   tabSongs.artId == artist.id
//
   