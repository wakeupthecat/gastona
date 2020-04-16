/*
Copyright (C) 2020 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

function coordinateIntervalNorm (namo)
{
   // getNormalInterval (37)        // 50
   // getNormalInterval (3211)      // 2000
   // getNormalInterval (0.37)      // 0.5
   // getNormalInterval (0.037)     // 0.05

   if (namo > 1)
   {
      var diggi = [0, 1, 2, 2, 5, 5, 5, 5, 10, 10];
      var strnum = "" + Math.round(namo);
      var szeros = Array(strnum.length).join("0");
      var str = "" + Math.round (strnum.substr(0, 1) + "." + strnum.substr(1, 1));
      return diggi[str.charAt(0)] + szeros;
   }
   else
   {
      var diggi = ["00", "01", "02", "02", "05", "05", "05", "05", "1", "1"];
      var strnum = "" + namo;
      if (strnum.charAt (0) === '0') strnum = strnum.substr(1);
      if (strnum.charAt (0) !== '.') return 1; // ? give error!
      var lenn = strnum.length;
      for (var mm = 1; mm < lenn-1 && strnum.charAt (mm) === '0'; mm ++) ;
      var str = "" + Math.round (strnum.substr(mm, 1) + "." + strnum.substr(mm+1, 1));
      var salo = "." + Array(mm+1).join("0") + diggi[str.charAt(0)];

      return (+(salo)) * 100.;
   }
}
