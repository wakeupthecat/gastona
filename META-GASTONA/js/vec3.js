/*
Copyright (C) 2015-2020 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

////////////////////
// vec3
////////////////////


function vec3(x, y, z)
{
   // if first parameter is a vec3 !
   if (x && x.x)
   {
      this.x = x.x;
      this.y = x.y || 0;
      this.z = x.z || 0;
   }
   else
   {
  this.x = x || 0;
  this.y = y || 0;
  this.z = z || 0;
}
}

vec3.prototype =
{
   toString: function () {
      return this.x + ", " + this.y + ", " + this.z;
   },

   fromArray: function(arr, indx, dim) {
      dim = dim||3;
      var base = (indx||0)*dim;

      // +(var) ensures the result is a number even if var is a string
      //
      this.set (+(arr[base]), +(arr[1+base]), (dim > 2 ? +(arr[2+base]): 0));
   },

   setIntoArray: function(arr, indx, dim) {
      dim = dim||3;
      indx = indx||(arr.length/dim);
      var base = indx*dim;
      arr[base] = this.x;
      arr[base+1] = this.y;
      if (dim > 2)
         arr[base+2] = this.z;
   },

   plus: function(v2) {
      this.x += v2.x;
      this.y += v2.y;
      this.z += v2.z;
   },

   add: function(v2) {
      this.plus (v2);
   },

   minus: function(v2) {
      this.x -= v2.x;
      this.y -= v2.y;
      this.z -= v2.z;
   },

   substract: function(v2) {
      this.minus (v2);
   },

   mult: function(b) {
      this.x *= b;
      this.y *= b;
      this.z *= b;
   },

   div: function(b) {
      if (b === 0) b=1e-20;
      this.x /= b;
      this.y /= b;
      this.z /= b;
   },

   dot: function(v2) {
      return this.x * v2.x + this.y * v2.y + this.z * v2.z;
   },

   cross: function(v2) {
      return new vec3(
         this.y * v2.z - this.z * v2.y,
         this.z * v2.x - this.x * v2.z,
         this.x * v2.y - this.y * v2.x
      );
   },

   norm: function() {
      return Math.sqrt(this.dot(this));
   },

   normalize: function() {
      this.div (this.norm ());
   },

   angle: function(a) {
      return Math.acos(this.dot(a) / (this.norm() * a.norm()));
   },

   clone: function() {
      return new vec3(this.x, this.y, this.z);
   },

   set: function(x, y, z) {
      if (x instanceof vec3) {
         this.x = x.x;
         this.y = x.y;
         this.z = x.z;
      }
      else {
         this.x = x||0;
         this.y = y||0;
         this.z = z||0;
      }
   },
   // var v1 = new vec3(6, 0, 0);
   // var v2 = new vec3(0, 4, 0);
   //
   // var v3 = v1.cross (v2);
   //
   // v3.normalize ();
   //
   // v3.mult (7);
   //
   // var arras = [];
   //
   // v3.setIntoArray(arras, 0);
   // v1.setIntoArray(arras);
   // v2.setIntoArray(arras);
   //
   // var ss ="";
   // for (var ii in arras)
   //    ss += arras[ii] + ", ";
   //
   // ss + " v3 is " + v3.toString ();
};

function deg2rad (deg)
{
   return Math.PI * deg / 180.;
}

function rad2deg (rad)
{
   return 180. * rad / Math.PI;
}

function vec3FromTo (v1, v2)
{
   var v3 = v2.clone ();
   v3.minus (v1);
   return v3;
}

function vec3FromArray (arr, indx, dim)
{
   var v3 = new vec3 ();
   v3.fromArray (arr, indx, dim);
   return v3;
}

function vec3cartesian(ccoord)
{
   this.x = ccoord[0]|| 0;
   this.y = ccoord[1]|| 0;
   this.z = ccoord[2]|| 0;
}

function vec3spherical(scoord)
{
   // scoord given in (r, theta, phi)
   //    r     = radius or module
   //    theta = angle in radians between axis x and the projection of the vector in the plane x-y
   //    phi   = angle in radians between axis z and the vector
   //
   //NOTE: THIS IT IS NOT the ISO convention as expl. in Wikipedia
   //      but rather the convention used in mathematics and engineering
   //
   //   these two vectors v1 and v2 in the x-y plane are the same
   //        var v1 = vec3cartesian (1, 1);
   //        var v2 = vec3spherical (Math.sqrt(2), deg2rad(45));
   //

   var r = scoord[0]||0;
   var sinTheta = Math.sin (scoord[1]||0);
   var cosTheta = Math.cos (scoord[1]||0);

   // Note: only default angle is 90 degrees (no z component) but if angle is 0 it has to be computed
   var sinPhi = (scoord[2] !== undefined) ? Math.sin (scoord[2]): 1.;
   var cosPhi = (scoord[2] !== undefined) ? Math.cos (scoord[2]): 0.;

   this.x = r * sinPhi * cosTheta;
   this.y = r * sinPhi * sinTheta;
   this.z = r * cosPhi;
}

function vec3polar(radi, angle)
{
   return new vec3spherical([ radi, angle ]);
}

