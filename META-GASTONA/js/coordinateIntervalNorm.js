/*
Copyright (C) 2020 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

function coordinateIntervalNorm (namo)
{
   // out (coordinateIntervalNorm (37))              // 50
   // out (coordinateIntervalNorm (3211))            // 2000
   // out (coordinateIntervalNorm (0.37))            // 0.5
   // out (coordinateIntervalNorm (0.037))           // 0.05
   // out (coordinateIntervalNorm (99))              // 100
   // out (coordinateIntervalNorm (.0000000000819))  // 1e-10
   // out (coordinateIntervalNorm (.000000819))      // 0.000001
   // out (coordinateIntervalNorm (.00000819))       // 0.00001
   // out (coordinateIntervalNorm (.0000819))        // 0.0001

   if (namo > 1)
   {
      var diggi = [0, 1, 2, 2, 5, 5, 5, 5, 10, 10, 10];
      var strnum = "" + Math.round(namo);
      var szeros = Array(strnum.length).join("0");
      var str = Math.round (strnum.substr(0, 1) + "." + strnum.substr(1, 1));
      return diggi[str] + szeros;
   }
   else
   {
      var diggi = ["00", "01", "02", "02", "05", "05", "05", "05", "1", "1", "1"];
      var strnum = "" + namo;
      var str = "";
      var mm = 0;
      if (strnum.charAt (0) === '0') strnum = strnum.substr(1);
      if (strnum.charAt (0) !== '.')
      {
         // expected format e.g. 8.19e-7
         var indx = strnum.indexOf ("e-");
         if (indx < 0) return 1; // error!

         str = Math.round (strnum.substr(0,3));
         mm = +(strnum.substr (indx+2));
      }
      else
      {
         for (mm = 1; mm < strnum.length-1 && strnum.charAt (mm) === '0'; mm ++) ;
         str = Math.round (strnum.substr(mm, 1) + "." + strnum.substr(mm+1, 1));
      }

      var nzeros = mm+1;
      var salo = "." + Array(nzeros + (nzeros > 2 ? -2: 0)).join("0") + diggi[str];
      return (+(salo)) * (nzeros > 2 ? 1: 100.);
   }
}
