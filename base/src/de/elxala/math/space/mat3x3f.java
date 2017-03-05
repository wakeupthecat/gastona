/*
java package de.elxala.math
Copyright (C) 2012  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.math.space;

/**
   a mat3x3f is a matrix formed by 3 vect3f

   (v1.x, v1.y, v1.z)
   (v2.x, v2.y, v2.z)
   (v3.x, v3.y, v3.z)

*/
public class mat3x3f
{
   //  constructor (per default creates identity 3x3 matrix)
   //  Note : we use the normal vectors i, j, k (vect3_i etc..) and assign them
   //         to rows (should be columns), but the result for the identity matrix 3x3 is the same
   public vect3f v1 = vect3f.i;  // 1 0 0
   public vect3f v2 = vect3f.j;  // 0 1 0
   public vect3f v3 = vect3f.k;  // 0 0 1

   public mat3x3f ()
   {
   }
   
   public mat3x3f (mat3x3f cop)
   {
      set (cop);
   }

   public mat3x3f (vect3f vec1, vect3f vec2, vect3f vec3)
   {
      set (vec1, vec2, vec3);
   }

   public void set (mat3x3f cop)
   {
      if (cop == null) return;
      v1 = cop.v1;
      v2 = cop.v2;
      v3 = cop.v3;
   }

   // set (per default creates identity 3x3 matrix)
   public void set (vect3f vv1, vect3f vv2, vect3f vv3)
   {
      v1 = new vect3f (vv1);
      v2 = new vect3f (vv2);
      v3 = new vect3f (vv3);
   }

   // creates a rotation matrix in the x axis by given radians
   //
   public void createRotationX (float radians)
   {
      float co = (float) Math.cos (radians);
      float si = (float) Math.sin (radians);
      set (new vect3f ( 1.f,  0.f,  0.f),
           new vect3f ( 0.f,   co,   si),
           new vect3f ( 0.f,  -si,   co));
   }

   // creates a rotation matrix in the y axis by given radians
   //
   public void createRotationY (float radians)
   {
      float co = (float) Math.cos (radians);
      float si = (float) Math.sin (radians);
      set (new vect3f (  co,  0.f,  -si),
           new vect3f ( 0.f,  1.f,  0.f),
           new vect3f (  si,  0.f,   co));
   }

   // creates a rotation matrix in the z axis by given radians
   //
   public void createRotationZ (float radians)
   {
      float co = (float) Math.cos (radians);
      float si = (float) Math.sin (radians);
      set (new vect3f (   co,   si,  0.f),
           new vect3f (  -si,   co,  0.f),
           new vect3f (  0.f,  0.f,  1.f));
   }


   
   
   /**
      returns the vector i (first column)
   */
   public vect3f i ()
   {
      return new vect3f (v1.x, v2.x, v3.x);
   }

   /**
      returns the vector j (second column)
   */
   public vect3f j ()
   {
      return new vect3f (v1.y, v2.y, v3.y);
   }

   /**
      returns the vector k (third column)
   */
   public vect3f k ()
   {
      return new vect3f (v1.z, v2.z, v3.z);
   }

   /**
      transpose a mat3x3f
   */
   public static void transpose (mat3x3f resul)
   {
      resul.set (resul.i (), resul.j (), resul.k ());
   }

   /**
      transpose itself
   */
   public void transpose ()
   {
      transpose (this);
   }

   /**
      mat3x3f + mat3x3f

     (v1.x, v1.y, v1.z)   (v1.x, v1.y, v1.z)
     (v2.x, v2.y, v2.z) + (v2.x, v2.y, v2.z) = ...
     (v3.x, v3.y, v3.z)   (v3.x, v3.y, v3.z)

   */
   public static mat3x3f plus (mat3x3f mat1, mat3x3f mat2)
   {
      return new mat3x3f (
            vect3f.plus (mat1.v1, mat2.v1), 
            vect3f.plus (mat1.v2, mat2.v2), 
            vect3f.plus (mat1.v3, mat2.v3));
   }

   /**
      mat3x3f - mat3x3f

     (v1.x, v1.y, v1.z)   (v1.x, v1.y, v1.z)
     (v2.x, v2.y, v2.z) - (v2.x, v2.y, v2.z) = ...
     (v3.x, v3.y, v3.z)   (v3.x, v3.y, v3.z)

   */
   public static mat3x3f minus (mat3x3f mat1, mat3x3f mat2)
   {
      return new mat3x3f (
            vect3f.minus (mat1.v1, mat2.v1), 
            vect3f.minus (mat1.v2, mat2.v2), 
            vect3f.minus (mat1.v3, mat2.v3));
   }

   /**
      mat3x3f * escalar

      (v1.x, v1.y, v1.z)
      (v2.x, v2.y, v2.z) x A or A x (..) = ...
      (v3.x, v3.y, v3.z)

   */
   public static mat3x3f product (mat3x3f mat, float escalar)
   {
      return new mat3x3f (
            vect3f.mult (mat.v1, escalar), 
            vect3f.mult (mat.v2, escalar), 
            vect3f.mult (mat.v3, escalar));
   }

   /**
      (this mat3x3f) * escalar
   */
   public mat3x3f product (float escalar)
   {
      return product (this, escalar);
   }

   /**
      mat3x3f * vect3f

      (v1.x, v1.y, v1.z)   (vv.x)
      (v2.x, v2.y, v2.z) x (vv.y) = ...
      (v3.x, v3.y, v3.z)   (vv.z)

   */
   public static vect3f product (mat3x3f mat, vect3f vv)
   {
      return new vect3f (
            mat.v1.dotProduct (vv),
            mat.v2.dotProduct (vv),
            mat.v3.dotProduct (vv));
   }

   /**
      (this mat3x3f) * vect3f
   */
   public vect3f product (vect3f vv)
   {
      return product (this, vv);
   }

   /**
      vect3f * mat3x3f

                           (v1.x, v1.y, v1.z)
      (vv.x, vv.y, vv.z) x (v2.x, v2.y, v2.z) = ...
                           (v3.x, v3.y, v3.z)

   */
   static vect3f product (vect3f vv, mat3x3f mat)
   {
      return new vect3f (
                  vv.dotProduct (mat.i ()),
                  vv.dotProduct (mat.j ()),
                  vv.dotProduct (mat.k ()));
   }

   /**
      mat3x3f * mat3x3f

     (v1.x, v1.y, v1.z)   (v1.x, v1.y, v1.z)
     (v2.x, v2.y, v2.z) x (v2.x, v2.y, v2.z) = ...
     (v3.x, v3.y, v3.z)   (v3.x, v3.y, v3.z)

   */
   public static mat3x3f product (mat3x3f mat1, mat3x3f mat2)
   {
      mat3x3f mRes = new mat3x3f (mat2);
      mRes.transpose ();

      mRes.set ( new vect3f (mat1.v1.dotProduct (mRes.v1), 
                             mat1.v1.dotProduct (mRes.v2), 
                             mat1.v1.dotProduct (mRes.v3)),
                 new vect3f (mat1.v2.dotProduct (mRes.v1), 
                             mat1.v2.dotProduct (mRes.v2), 
                             mat1.v2.dotProduct (mRes.v3)),
                 new vect3f (mat1.v3.dotProduct (mRes.v1), 
                             mat1.v3.dotProduct (mRes.v2), 
                             mat1.v3.dotProduct (mRes.v3)) );

      return mRes;
   }

   /**
      (this mat3x3f) * mat3x3f
   */
   public mat3x3f product (mat3x3f mm)
   {
      return product (this, mm);
   }

   /**
      determinant
   */
   public float det ()
   {
      return v1.x * v2.y * v3.z +
             v1.y * v2.z * v3.x +
             v1.z * v2.x * v3.y -
             v1.z * v2.y * v3.x -
             v1.y * v2.x * v3.z -
             v1.x * v2.z * v3.y;
   }

   public boolean equals (mat3x3f mat)
   {
      return (v1.equals (mat.v1) &&
              v2.equals (mat.v2) &&
              v3.equals (mat.v3));
   }
   
   //===== orientation operations
   //
   
   /** 
      creates this normalized matrix from the vectors v1 and v2 in the current matrix
      see also createOrientationMatrix (vect3f vo1, vect3f vo2)
   */
   public void createOrientationMatrix ()
   {
      createOrientationMatrix (v1, v2);
   }

   /** 
      creates this normalized matrix from two arbitrary orientation vectors vo1 and vo2 
      that just define a plane. The first one is treat as x axis (normalized), y and z normal axis
      are calculated, y axis has to belong to the plane vo1-vo2 and z orthohonal to the plane
   */
   public void createOrientationMatrix (vect3f vo1, vect3f vo2)
   {
      vect3f xx = new vect3f(vo1);
      vect3f yy = new vect3f(vo2);
      vect3f zz = vect3f.crossProduct (xx, yy);  // zz normal al plano xx yy
      
      yy = vect3f.crossProduct (zz, xx);  // yy normal al plano xx zz

      xx.normalize ();
      yy.normalize ();
      zz.normalize ();

      set (xx, yy, zz);
   }
   
   /**
      "rotateOwn..."
      Methods for provide a rotation of the own axis (X, Y, Z) from an orientation matrix.
      
      (matrix mat3x3f with the following unitary vectors: v1 as x, v2 as y and v3 as z orientations.)
      
      Note : this kind of rotation is commutative (e.g. rot x + rot z = rot z + rot x)
      
      the argument has to be done in radians (2 PI radians = 360°), in order to give
      degrees (let's say 60°) you can do :

         rotateOwnX (mOrient, toRadian (60.));
   */


   public void rotateOwnX (float radian)
   {
      if (radian == 0.) return;
      mat3x3f rot = new mat3x3f ();
      rot.createRotationX (radian);

      // this = rot * this
      // NOTE! not the same as this * rot !!
      set (mat3x3f.product (rot, this));
   }
   
   public void rotateOwnY (float radian)
   {
      if (radian == 0.) return;
      mat3x3f rot = new mat3x3f ();
      rot.createRotationY (radian);

      // this = rot * this
      // NOTE! not the same as this * rot !!
      set (mat3x3f.product (rot, this));
   }
   
   public void rotateOwnZ (float radian)
   {
      if (radian == 0.) return;
      mat3x3f rot = new mat3x3f ();
      rot.createRotationZ (radian);

      // this = rot * this
      // NOTE! not the same as this * rot !!
      set (mat3x3f.product (rot, this));
   }

   public void rotateOwn  (vect3f radian_directions)
   {
      rotateOwnX (radian_directions.x);
      rotateOwnY (radian_directions.y);
      rotateOwnZ (radian_directions.z);
   }


   /**
      "rotateOver..."
      Methods to provide a rotation of an orientation matrix positioned (Position) into a reference space (X, Y, Z).
      
                        |      __/
               |__      |        \
              /         |_________
                       /
                      /
      
      As consequence the orientation matrix will change as well as its position
      
      Note : this kind of rotation is NOT commutative (e.g. rot x + rot z != rot z + rot x)
      
      the argument has to be done in radians (2 PI radians = 360°), in order to give
      degrees (let's say 60°) you can do :

         rotateOverX (mOrient, toRadian (60.));
   */

   /**
      Rotate 'radian' radians over the X axis a positioned ('Position') orientation unitary matrix.
      As a result a new Position and Orientation are calculated
   */
   public void rotateOverX (vect3f Position, float radian)
   {
      if (radian == 0.) return;

      mat3x3f rot = new mat3x3f ();
      rot.createRotationX (radian);

      set (product (this, rot));
      Position.set (mat3x3f.product (Position, rot));
   }
   
   public void rotateOverY (vect3f Position, float radian)
   {
      if (radian == 0.) return;

      mat3x3f rot = new mat3x3f ();
      rot.createRotationY (radian);

      set (product (this, rot));
      Position.set (mat3x3f.product (Position, rot));
   }
   
   public void rotateOverZ (vect3f Position, float radian)
   {
      if (radian == 0.) return;

      mat3x3f rot = new mat3x3f ();
      rot.createRotationZ (radian);

      set (product (this, rot));
      Position.set (mat3x3f.product (Position, rot));
   }
}
