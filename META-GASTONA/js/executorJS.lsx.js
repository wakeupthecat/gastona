<!DOCTYPE html>
<html>
<script type="text/javascript">
   var outStr = "";
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
      outStr = outStr + str + "\n";
   }


   onload = function () {

@<USER CODE>

          xmlhttp.open ("POST","JSresponse",true);
          xmlhttp.send (outStr);
      }
</script>

<body>
</body>
</html>
