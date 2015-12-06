var pograma = function () {
/*
#layouts#

   <main>
      Evalayout, 30, 30, 20, 20
         --- , X          , X2
           X , xMarketesIn, dMarketesOut
           X , xHtmlOut   , +

#data#

   <xMarketesIn>
      //#h Cabecera
      //-- Titulo
      // Hola! escribe algo en marketes!
      //

#**#

*/
}.toString ();

   var luix = jGastona (evaFileStr2obj (pograma));
   document.getElementById('xMarketesIn').onkeyup = maka;

   function adapta ()
   {
      luix.doLayout(window.innerWidth-15, window.innerHeight-15);

      //document.getElementById('xMarketesIn').addEventListener ("keyup", maka );
      document.getElementById('xMarketesIn').onkeyup = maka;
      maka ();
   }

   window.addEventListener("load", adapta);
   window.addEventListener("resize", adapta);

   function mensaka (na)
   {
      luix.mensaka (na)
   }

   maka ();

   function escapeHtml(str)
   {
      return String(str).replace(/&/g, '&amp;')
                        .replace(/"/g, '&quot;')
                        .replace(/'/g, '&#39;')
                        .replace(/</g, '&lt;')
                        .replace(/>/g, '&gt;');
   }

   function maka ()
   {
      var str = document.getElementById('xMarketesIn').value;
      var productoHtml = marketes(str);

      //var sal = "<pre>";
      var sal = "";

      sal += escapeHtml ("<html><head>") + "\n";
      sal += escapeHtml ("   <!-- <link rel='stylesheet' type='text/css' href='REMEMBER INCLUDE YOUR STYLE.css'> -->") + "\n";
      sal += escapeHtml ("<style>") + "\n";
      sal += escapeHtml ("   <!--  or paste it here -->") + "\n";
      sal += escapeHtml ("</style>") + "\n";
      sal += escapeHtml ("</head><body>") + "\n";
      sal += escapeHtml (productoHtml) + "\n";
      sal += escapeHtml ("</body></html>") + "\n";
      //sal += "</pre>";

      document.getElementById("xHtmlOut").innerHTML = sal;

      document.getElementById('dMarketesOut').innerHTML = productoHtml;
   }
