/*
Copyright (C) 2015-2019 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

////////////////////
// vec3
////////////////////


function vec3(x, y, z) {
  this.x = x || 0;
  this.y = y || 0;
  this.z = z || 0;
}

vec3.prototype = {

   toString: function () {
      return this.x + ", " + this.y + ", " + this.z;
   },

   fromArray: function(arr, indx, dim) {
      dim = dim||3;
      var base = (indx||0)*dim;
      this.set (arr[base], arr[1+base], (dim > 2 ? arr[2+base]: 0));
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

   minus: function(v2) {
      this.x -= v2.x;
      this.y -= v2.y;
      this.z -= v2.z;
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

function vec3FromTo (v1, v2) {
  var v3 = v2.clone ();
  v3.minus (v1);
  return v3;
}

function vec3FromArray (arr, indx, dim) {
  var v3 = new vec3 ();
  v3.fromArray (arr, indx, dim);
  return v3;
}
