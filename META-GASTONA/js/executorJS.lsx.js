<!DOCTYPE html>
<html>
<script type="text/javascript">
   var outArr = [] ;
   // impostor console
   var console = { log: function (s) { out (s); } };
   // impostor alert
   function alert (s) { out ("ALERT! \"" + s + "\"") }

   // AJAX stuff
   var xmlhttp;
   if (window.XMLHttpRequest)
      xmlhttp = new XMLHttpRequest();
   else if (window.ActiveXObject)
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   else
      alert("Your browser does not support XMLHTTP!");

   xmlhttp.onreadystatechange = function ()
   {
      if (xmlhttp.readyState == 4)
         close ();
   }

   function out (str)
   {
      outArr.push (str);
   }

   // convert strings, arrays, objects, functions into a readable string
   //
   function o2str (ele, maxstack, indent)
   {
      if (maxstack === undefined) maxstack = 4;
      if (!indent) indent = "";

      function recurr (el) { return o2str (el, maxstack-1, indent + "   "); }

      if (maxstack == 0) return "** too deep!";
      var sa = "", ii;
      if (ele instanceof Array)
      {
         for (ii in ele)
            sa = sa + recurr (ele[ii]) + ", ";
         return "[" + sa + "]";
      }
      if (typeof ele === "object")
      {
        for (ii in ele)
           sa = sa + indent + "   " + ii + " : " + recurr (ele[ii]) + ",\n";
        return "\n" + indent + "{\n" + sa + indent + "}";
      }
      if (typeof ele === "string")
         return "\"" + ele + "\"";

      return "" + ele;
   }

   // trim for IE8
   if (typeof String.prototype.trim !== 'function') {
      String.prototype.trim = function() {
         return this.replace(/^\s+|\s+$/g, '');
      }
   }

   // for all
   //
   if (typeof String.prototype.trimTail !== 'function') {
      String.prototype.trimTail = function() {
         return this.replace(/\s+$/g, '');
      }
   }

   if (typeof String.prototype.left !== 'function') {
      String.prototype.left = function (len) {
         return this.substring (0, len);
      }
   }
   if (typeof String.prototype.right !== 'function') {
      String.prototype.right = function (len) {
         return this.substring (this.length - len);
      }
   }

   // char.repeat (n) but checking to avoid javascript error
   //
   function rechar (cha, times)
   {
      if (times <= 0) return '';
      return cha.repeat (times);
   }


   onload = function () {

@<USER CODE>

          xmlhttp.open ("POST","JSresponse",true);
          xmlhttp.send (outArr.join ("\n"));
      }
</script>

<body>
</body>
</html>
