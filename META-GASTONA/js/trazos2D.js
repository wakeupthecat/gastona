/*
Copyright (C) 2015 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

function trazoShape (c2d, form, px, py, fillSty, strkSty, closep, arrp)
{
   c2d.beginPath();
   c2d.moveTo(px, py);
   for (var ii = 0; ii < arrp.length; /**/)
   {
      if (form === "pol") {
         c2d.lineTo (arrp[ii], arrp[ii+1]);
         ii += 2;
      }
      else if (form == "qua") {
         c2d.quadraticCurveTo (arrp[ii], arrp[ii+1], arrp[ii+2], arrp[ii+3]);
         ii += 4;
      }
      else if (form == "cub" || form == "bez") {
         c2d.bezierCurveTo (arrp[ii], arrp[ii+1], arrp[ii+2], arrp[ii+3], arrp[ii+4], arrp[ii+5]);
         ii += 6;
      }
      else
      {
         console.log ("ERROR: unknow form " + form + " calling trazoShape!");
         break; // error!
      }
   }
   if (closep)
      c2d.closePath();

   if (fillSty) c2d.fillStyle = fillSty;
   if (strkSty) c2d.strokeStyle = strkSty;
   if (fillSty) c2d.fill ();
   if (strkSty) c2d.stroke ();
}
