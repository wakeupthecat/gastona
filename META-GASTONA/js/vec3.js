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


function anyToXYZ (xany, y, z)
{
    if (xany instanceof vec3)
        return xany

    if (xany instanceof Array)
        return { x: xany[0]||0,
                 y: xany[1]||0,
                 z: xany[1]||0,
        }

    return { x: xany||0,
             y: y||0,
             z: z||0,
    }
}

function vec3(xov, y, z)
{
    var pt = anyToXYZ (xov, y, z)
    this.x = pt.x;
    this.y = pt.y;
    this.z = pt.z;
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
        return this
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

    plus: function(xov, y, z) {
        var pt = anyToXYZ (xov, y, z)
        this.x += pt.x;
        this.y += pt.y;
        this.z += pt.z;
        return this
    },

    add: function(xov, y, z) {
        return this.plus (xov, y, z);
    },

    minus: function(xov, y, z) {
        var pt = anyToXYZ (xov, y, z)
        this.x -= pt.x;
        this.y -= pt.y;
        this.z -= pt.z;
        return this
    },

    substract: function(xov, y, z) {
        return this.minus (xov, y, z);
    },

    mult: function(esc) {
        this.x *= esc;
        this.y *= esc;
        this.z *= esc;
        return this
    },

    div: function(esc) {
        if (!esc || esc === 0) esc = 1e-20;
        this.x /= esc;
        this.y /= esc;
        this.z /= esc;
        return this
    },

    dot: function(xov, y, z) {
        var pt = anyToXYZ (xov, y, z)
        return this.x * pt.x + this.y * pt.y + this.z * pt.z;
    },

    cross: function(xov, y, z) {
        var pt = anyToXYZ (xov, y, z)
        return new vec3(
            this.y * pt.z - this.z * pt.y,
            this.z * pt.x - this.x * pt.z,
            this.x * pt.y - this.y * pt.x
        );
    },

    norm: function() {
        return Math.sqrt(this.dot(this));
    },

    normalize: function() {
        this.div (this.norm ());
        return this
    },

    angle: function(a) {
        var r1 = this.norm ();
        if (r1 === 0) return Math.PI / 2.; // or 0 ?
        var r2 = a.norm ();
        if (r2 === 0) return Math.PI / 2.; // or 0 ?
        return Math.acos(this.dot(a) / (r1 * r2));
    },

    getSphericCoord: function () {
        return new vec3 (this.norm (),
                         Math.atan2 (this.y, this.x),
                         Math.atan2 (Math.sqrt (this.x * this.x + this.y * this.y), this.z)
                        )
    },

    clone: function() {
        return new vec3(this.x, this.y, this.z);
    },

    set: function(xov, y, z) {
        var pt = anyToXYZ (xov, y, z)
        this.x = pt.x;
        this.y = pt.y;
        this.z = pt.z;
        return this
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
   return vec3Minus (v2, v1);
}

function vec3Minus (v1, v2)
{
   var vsol = v1.clone ();
   vsol.minus (v2);
   return vsol;
}

function vec3Plus (v1, v2)
{
   var vsol = v1.clone ();
   vsol.plus (v2);
   return vsol;
}

function vec3Add (v1, v2)
{
   return vec3Plus (v1, v2);
}

function vec3FromArray (arr, indx, dim)
{
   var vsol = new vec3 ();
   vsol.fromArray (arr, indx, dim);
   return vsol;
}

function vec3FromSpherical(roa, theta, phi)
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
    var pt = anyToXYZ (roa, theta, phi)

    var r = pt.x;
    var sinTheta = Math.sin (pt.y);
    var cosTheta = Math.cos (pt.y);
    var sinPhi   = Math.sin (pt.z);
    var cosPhi   = Math.cos (pt.z);

   return new vec3 (r * sinPhi * cosTheta, r * sinPhi * sinTheta, r * cosPhi)
}

function vec3polar(radi, angle)
{
   return vec3spherical(radi, angle, Math.PI / 2.);
}

// returns a new unitary vector "i" in x direction
//
function vec3i() {
    return new vec3(1,0,0)
}

// returns a new unitary vector "j" in y direction
//
function vec3j() {
    return new vec3(0,1,0)
}

// returns a new unitary vector "k" in z direction
//
function vec3k() {
    return new vec3(0,0,1)
}

//  returns a new vector orthogonal to v1 and v2
//
function vec3Orthogonal(v1, v2) {
    return v1.cross(v2)
}

//  returns a vector from v1 with component x, y rotated 90 degrees
//
function vec3_2Drotate90deg(v1, sign) {
    // v1 x +k = rotation +90 degrees (clockwise)
    // v1 x -k = rotation -90 degrees (anti-clockwise)
    var zv = vec3k ()
    if (sign && sign == -1)
        zv.mult (-1)

    return v1.cross(zv)
}
