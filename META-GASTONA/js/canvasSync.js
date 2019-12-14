/*
Copyright (C) 2015-2109 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.
*/

// =============== function canvasSync (gcontext)
//    Synchronize a rendering sequence in presence of asynchronous image loading
//    Images have to be declared first using "declareImage"
//    Then add one or more rendering commands using "addRender"
//    Finally call "renderAll"
//
//    see  https://jsfiddle.net/alejandro_xalabarder/yr9kqn48/
//
// Example:
//
//    var canvas = document.getElementById ("canvas");
//    var imatges = canvasSync (canvas.getContext("2d"));
//    imatges.declareImage ("painting.png");
//    imatges.addRender (function () {
//                this.ctx.rect (0, 0, 50, 50);
//                this.ctx.stroke ();
//                this.renderImage ("painting.png", 10, 10, 50, 50);
//                this.ctx.rect (25, 25, 50, 50);
//                this.ctx.stroke ();
//                });
//    imatges.renderAll ();
//
function canvasSync (gcontext)
{
   var ctx = gcontext;
   var imageBag = { };
   var declaredImages = [];
   var renderArray = [];

   var jomateix = {
            reset : reset,
            declareImage : declareImage,
            renderImage : renderImage,
            addRender: addRender,
            renderAll: renderAll,
            ctx: ctx
         };

   return jomateix;

   function reset ()
   {
      imageBag = {};
      declaredImages = [];
      renderArray = [];
   }

   function declareImage (iname)
   {
      var image = new Image();
      image.onload = function() { imageBag [iname] = image; };
      image.onerror = function() { imageBag [iname] = -1; };
      image.src = iname;
      declaredImages.push (iname);
   }

   // remember drawImage syntaxes : 3, 5 or 9 parameters!
   //
   //    context.drawImage(img,x,y);
   //    context.drawImage(img,x,y,width,height);
   //    context.drawImage(img,sx,sy,swidth,sheight,x,y,width,height);
   //
   function renderImage (iname, sx, sy, swidth, sheight, x, y, width, height)
   {
      if (declaredImages.indexOf (iname) == -1) {
         console.log ("Error: call renderImage with unknow image \"" + iname + "\"");
         return;
      }

      if (! imageBag[iname]) {
         console.log ("Error: call renderImage \"" + iname + "\"" + " out of addRender, renderAll contexts" );
         return;
      }

      if (imageBag[iname] && imageBag[iname] !== -1)
      {
         ctx.closePath ();

         if (height)
            ctx.drawImage(imageBag[iname], sx, sy, swidth, sheight, x, y, width, height);
         else if (sheight)
            ctx.drawImage(imageBag[iname], sx, sy, swidth, sheight);
         else
            ctx.drawImage(imageBag[iname], sx||0, sy||0);

         ctx.beginPath ();
      }
   }

   function allImagesAreLoaded ()
   {
      for (var ii = 0; ii < declaredImages.length; ii ++)
         if (!imageBag[declaredImages[ii]])
            return false;
      return true;
   }

   function addRender (paintFunct)
   {
      renderArray.push (paintFunct);
   }

   function renderAll ()
   {
      if (! allImagesAreLoaded ())
      {
         setTimeout (renderAll, 100);
         return;
      }

      // now all images are loaded, do the the whole rendering
      //
      ctx.beginPath ();
      for (var rr = 0; rr < renderArray.length; rr ++)
         renderArray[rr].call (jomateix);
      ctx.closePath ();
   }
}
