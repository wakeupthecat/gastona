/*
Copyright (C) 2015,2016 Alejandro Xalabarder Aulet
License : GNU General Public License (GPL) version 3
Open source project https://github.com/wakeupthecat/gastona
*/
function evaFileObj(t){function a(t){var a,n="";for(a in t)n+="\n#"+a+"#\n",n+=e(t[a]);return n}function e(t){var a,e="";for(a in t)e+="\n   <"+a+">\n",e+=n(t[a]);return e}function n(t){var a="";for(var e in t){a+="      ";for(var n in t[e])n>0&&(a+=", "),n==t[e].length-1&&(a+="//"),a+=t[e][n];a+="\n"}return a}return"string"==typeof t&&(t=evaFileStr2obj(t)),{obj:t,toText:function(){return a(t)},toString:function(){return a(t)},evaFileObj2Text:a,evaUnitObj2Text:e,evaObj2Text:n}}function evaFile(t){alert("evaFile function name is deprecated! please use evaFileStr2obj instead"),evaFileStr2obj(t)}function evaFileUTF82obj(t){return evaFileStr2obj(decodeURIComponent(t.replace(/\+/g,"%20")))}function evaFileStr2obj(t){function a(t,a,e,n){return 0==t.indexOf(a)?(n=t.indexOf(e,1),n>0?{name:t.substr(1,n-1),rest:t.substr(n+1).trim()}:void 0):void 0}function e(t,a,e,n){if(t=t.trim(),a=t.length,0!=a){if(0==t.indexOf("'"))return[t.substr(1)];if(0==t.indexOf("//"))return[t.substr(2)];t.lastIndexOf(",")==a-1&&(t=t.substring(0,a-1)),e=t.split(",");for(n in e)e[n]=decodeURIComponent(e[n].trim());return e}}function n(t){function n(t){o&&(f[o]=p),o=void 0,p=[],t&&(i&&(c[i]=f),i=void 0,f=[])}var i,o,r,d,s,l,u,c={},f={},p=[],s=t;"string"==typeof t&&(s=t.split("\n"));for(u in s)d=s[u].trim(),r=a(d,"#","#"),r?(n(!0),i=r.name):(r=a(d,"<",">"),r&&(n(!1),o=r.name,d=r.rest,0==d.length)||i&&o&&(l=e(d),l&&p.push(l)));return n(!0),c}return"function"!=typeof String.prototype.trim&&(String.prototype.trim=function(){return this.replace(/^\s+|\s+$/g,"")}),n(t)}function EvaLayout(t,a){function e(t,a){function e(t){var a=s,e=0,n=0;return t+="",0==t.length||0==t.indexOf("A")?a=s:0==t.indexOf("X")?(a=l,e=t.length<2?1:parseInt(t.substr(1))):(a=d,n=parseInt(t)),{type:a,len:n,extraPercent:e}}function n(t,a){a=e(t||""),f.push(a),u+=a.extraPercent}function i(t,a){f[t]&&f[t].type==s&&(f[t].len=Math.max(f[t].len,a))}function o(e,n){c=t+t+a*(f.length-1);for(e in f)n=f[e],c+=n.len,u>0&&(n.extraPercent=n.extraPercent/u)}function r(t,e,n,i){for(i=0,n=n||e;(n>=e||-1==n)&&e<f.length;e++)i+=a+f[e].len+f[e].extraPercent*t;return i}var d=0,s=1,l=2,u=0,c=0,f=[];return{margin:t,gap:a,fixedSize:function(){return c},regla:f,addItem:n,setLengthOfItemAt:i,endItems:o,getLengthInRange:r,countItems:function(){return f.length}}}function n(t){return g[1][t+1]}function i(t){return g[2+t][0]}function o(){return g.length-2}function r(t){return g[2+t].length-1}function d(t,a){return t+=2,a+=1,t>=0&&t<g.length&&a>=0&&a<g[t].length?g[t][a]:void 0}function s(){if(!p){c=e(parseInt(g[0][1])||0,parseInt(g[0][3])||0),f=e(parseInt(g[0][2])||0,parseInt(g[0][4])||0);for(var a=0,s=o(),l=0;s>l;l++){a=r(l),f.addItem(i(l));for(var u=0;a>u;u++){u<c.countItems()||c.addItem(n(u));var m=d(l,u);if(m&&t.cellElementIsAnId(m)){var h=t.getLayableByName(m);if(h){h.indxPos.ileft=u,h.indxPos.itop=l;for(var y=u;a>y+1&&d(l,y+1)===t.EXPAND_HORIZONTAL;)y++;for(a>y+1&&d(l,y+1)===t.EXPAND_HORIZONTAL+t.EXPAND_HORIZONTAL&&(y=-1),h.indxPos.iright=y,y==u&&c.setLengthOfItemAt(u,h.iniRect.right-h.iniRect.left),y=l;s>y+1&&d(y+1,u)===t.EXPAND_VERTICAL;)y++;s>y+1&&d(y+1,u)===t.EXPAND_VERTICAL+t.EXPAND_VERTICAL&&(y=-1),h.indxPos.ibottom=y,y==l&&f.setLengthOfItemAt(l,h.iniRect.bottom-h.iniRect.top),h.isLaidOut=!0}}}}c.endItems(),f.endItems(),v.top=0,v.bottom=f.fixedSize(),v.left=0,v.right=c.fixedSize(),p=!0}}function l(a,e,n,i){s();for(var o=i-f.fixedSize(),r=n-c.fixedSize(),l=a+c.margin,u=0;u<c.countItems();u++){u>0&&(l+=c.getLengthInRange(r,u-1));for(var p=e+f.margin,g=0;g<f.countItems();g++){g>0&&(p+=f.getLengthInRange(o,g-1));var v=d(g,u);if(v){var m=t.getLayableByName(v);if(m)if(m.isLaidOut){var h=-c.gap,y=-f.gap;if(h+=c.getLengthInRange(r,m.indxPos.ileft,m.indxPos.iright),y+=f.getLengthInRange(o,m.indxPos.itop,m.indxPos.ibottom),0>l||0>p||0>h||0>y)continue;m.doMove(l,p,h,y),m.doShow(!0)}else m.doShow(!1)}}}}function u(a){s();for(var e=0;e<c.countItems();e++)for(var n=0;n<f.countItems();n++){var i=t.getLayableByName(d(n,e));i&&i.doShow(a)}}var c,f,p=!1,g=t.guiLayouts[a],v={left:0,right:200,top:0,bottom:20},m={ileft:0,iright:0,itop:0,ibottom:0};return g?g[0][0].match(/^eva$|^evalayout$/i)?{precalculateLayout:s,wName:a,isLaidOut:!1,iniRect:v,indxPos:m,invalidate:function(){p=!1},isWidget:!1,doMove:l,doShow:u}:void 0:void console.log('ERROR: no eva found with name "'+a+'"')}function layoutManager(t,a){function e(t,a,e){for(e in t)if(t[e]===a)return console.log("ERROR: layout element "+a+" already exists, it cannot be stacked!"),!1;return!0}function n(t){A&&A===t||i(),A=t}function i(t,a){for(a in y)t=y[a],t.isWidget?t.doShow(!1):t.invalidate()}function o(t,a){return{wName:t,isLaidOut:!1,iniRect:a||{left:0,right:200,top:0,bottom:20},indxPos:{ileft:0,iright:0,itop:0,ibottom:0},isWidget:!0,doMove:function(a,e,n,i){var o=document.getElementById(t);o?(o.style.position="absolute",o.style.left=Math.round(a)+"px",o.style.top=Math.round(e)+"px",o.style.width=Math.round(n)+"px",o.style.height=Math.round(i)+"px"):console.log("ERROR expected html element "+t+" not found!")},doShow:function(a){var e=document.getElementById(t);e&&(e.style.visibility=a?"visible":"hidden")}}}function r(t){if(""!==t){var a=y[l(t)];return a?(e(x,a.wName)&&(a.isWidget||a.precalculateLayout()),a):(console.log("ERROR: don't know how to find "+t+" or "+l(t)),o("bDummy"))}}function d(t,a){h[t]=a&&""!==a&&t!==a?a:void 0,m=!0,i()}function s(t){h[t]=void 0,m=!0,i()}function l(t){var a,e,n=[];do{if(a=h[t],!a||""===a||a===t)return t;for(e in n)if(n[e]===a)return console.log("ERROR: masks for "+t+" found circular!"),t;n.push(t),t=a}while(n.length<200);return console.log("ERROR: masks for "+t+" too deep!"),t}function u(t){return t&&t.length>0&&0!=t.indexOf(R)&&0!=t.indexOf(U)}function c(){return{EXPAND_HORIZONTAL:U,EXPAND_VERTICAL:R,guiLayouts:g,guiConfig:v,getLayableByName:r,cellElementIsAnId:u}}function f(t){if(v={},g=t.layouts,!g){g={};var a=t.javaj;if(!a)return void console.log("Error: unit layouts not found!");for(var e in a)0==e.indexOf("layout of ")?g[e.substr(10)]=a[e]:v[e]=a[e]}m=!0,h={},x=[],y={};for(var n in g){var i=EvaLayout(c(),n);i&&(y[n]=i,console.log("adding layout id ["+n+"]"))}var e,r,d,s,l,f;for(n in g){e=g[n],d=e.length;for(var r=2;d>r;r++){l=e[r].length;for(var s=1;l>s;s++)f=e[r][s],u(f)&&(y[f]||(b(f),y[f]=o(f)))}}}function p(t,a,e,i,o){t=t||"main",n(t);var d=r(t);x.push(d.wName),d.doMove(a,e,i,o),x.pop()}var g,v,m,h,y,A,b=a||function(t){console.log("adding widget id ["+t+"]")},x=[],U="-",R="+";return f(t),{guiConfig:v,loadConfig:function(a){t=a,f(t)},maskLayoutId:d,unmaskLayoutId:s,doShowLayout:function(t,a,e){p(t,0,0,a,e)},setLayout:function(t){n(t)},doLayout:function(t,a){p(A,0,0,t,a)}}}function jGastona(evaConfig,existingPlaceId){function getWindowWidth(){return window.innerWidth||document.documentElement.clientWidth||document.body.clientWidth}function getWindowHeight(){return window.innerHeight||document.documentElement.clientHeight||document.body.clientHeight}function adaptaLayout(){if(layMan){var t=getWindowWidth()-15,a=getWindowHeight()-15,e=layMan.guiConfig.layoutWindowLimits;if(e&&e[0]){var n=parseInt("0"|e[0][0]),i=parseInt("0"|e[0][1]),o=parseInt("0"|e[0][2]),r=parseInt("0"|e[0][3]);t=Math.max(t,n),a=Math.max(a,i),o>0&&(t=Math.min(t,o)),r>0&&(a=Math.min(a,r))}layMan&&layMan.doLayout(t,a)}}function loadJast(t){if(javajzWidgets={},responseAjaxUnit={},dataUnit={},listixUnit={},layMan=void 0,t){if(dataUnit=t.data||{},listixUnit=t.listix||{},!corpiny){var a="jGastonaStammHtmlElem";document.getElementById(a)||(document.body.innerHTML="<div id='"+a+"' style = 'position:relative;'></div>"),corpiny=document.getElementById(a)}for(corpiny||alert("ERROR no "+a+" no fun!");corpiny.hasChildNodes();)corpiny.removeChild(corpiny.firstChild);layMan=layoutManager(t,onAddWidget),adaptaLayout()}}function getCellEvaUnit(t,a,e,n){return t[a]?t[a][e||"0"][n||"0"]||"":""}function getDataCell(t,a,e){return getCellEvaUnit(dataUnit,t,a,e)}function setDataCell(t,a,e,n){dataUnit[t]||(dataUnit[t]=[[""]]),dataUnit[t][e||"0"]||(dataUnit[t][e||"0"]=[""]),dataUnit[t][e||"0"][n||"0"]=a||""}function setData(t,a){"string"==typeof a?setDataCell(t,a):Array.isArray(a)&&a.length>0&&Array.isArray(a[0])?dataUnit[t]=a:alert('Error: setData "'+t+'", the value is not a string nor looks like an eva variable');var e=getzWidgetByName(t);e&&updatezWidget(e)}function mensaka(t){var a=t.indexOf(" ");if(a>0){var e=t.substr(0,a),n=t.substr(a+1);if(javajzWidgets[e])return void(javajzWidgets[e][n]?javajzWidgets[e][n]():console.log("Error: widget "+e+" has no method '"+n+"'"))}var i=dataUnit["-- "+t]||listixUnit["-- "+t];return i?void executeListixFormat(i):void console.log('ignoring mensaka "'+t+'"')}function executeListixFormat(fbody){var strBody="";for(var ii in fbody)strBody+=fbody[ii]+"\n";eval(strBody)}function setVarTable_DimVal(t){var a=[["dimension","value"]];for(var e in t)a.push([t[e],getDataCell(t[e])]);dataUnit.varTable_DimVal=a}function setValueToElement(t,a){t&&("string"==typeof t.value?t.value=a:t.innerHTML=a)}function onAddWidget(t){if(t&&0!=t.length){var a,e=function(){this.innerHTML=dataUnit[t]?getDataCell(t):t.substr(1)},n=function(){setValueToElement(this,getDataCell(t))},i=function(){this.value=""},o=function(){this.style["background-image"]="url('"+getDataCell(t)+"')"},r=function(){dataUnit[t][0]=[this.value||"?"]},d=function(){dataUnit[t]=[[]];var a=this.value||"?",e=a.split("\n");for(var n in e)dataUnit[t][n]=[e[n]]},s=function(){mensaka(t)},l=dataUnit["class of "+t],u=l?l[0][0]:t;switch(u.charAt(0)){case"d":a=fabricaStandard("div",t,{"data!":e});break;case"n":a=fabricaStandard("a",t,{href:"login","data!":e});break;case"b":a=fabricaStandard("button",t,{onclick:s,"data!":e});break;case"e":a=fabricaStandard("input",t,{type:"text",onchange:r,"data!":n});break;case"f":a=fabricaStandard("input",t,{type:"file",onchange:s,"data!":i});break;case"m":a=fabricaStandard("div",t,{"data!":o});break;case"p":a=fabricaStandard("input",t,{type:"password",onchange:r,"data!":n});break;case"l":a=fabricaStandard("label",t,{"data!":e});break;case"x":var c=function(){var t,a="";for(t in dataUnit[this.id])a+=dataUnit[this.id][t]+"\n";setValueToElement(this,a)};a=fabricaStandard("textarea",t,{"data!":c,onchange:d});break;case"t":var c=function(){for(var t,a,e,n,i,o=dataUnit[this.id];this.hasChildNodes();)this.removeChild(this.firstChild);t=document.createElement("table"),t.id=this.id+"-table";for(n in o){a=document.createElement("tr");for(i in o[n])e=document.createElement("0"===n?"th":"td"),setValueToElement(e,o[n][i]),a.appendChild(e);t.appendChild(a)}this.appendChild(t)};a=fabricaSimpleTable(t,{"data!":c});break;case"c":case"r":case"k":case"i":var f=[],p=[];for(var g in dataUnit[t])"0"!==g&&(p.push(dataUnit[t][g][0]||"?"),f.push(dataUnit[t][g][1]||"?"));var v=dataUnit[t+" orientation"]||"X";"c"==t.charAt(0)&&corpiny.appendChild(fabricaSelect(t,p,f,!1)),"i"==t.charAt(0)&&corpiny.appendChild(fabricaSelect(t,p,f,!0)),"r"==t.charAt(0)&&corpiny.appendChild(fabricaGrupo("radio",v,t,p,f)),"k"==t.charAt(0)&&corpiny.appendChild(fabricaGrupo("checkbox",v,t,p,f))}a&&(javajzWidgets[t]=a,corpiny.appendChild(a),updatezWidget(a))}}function getzWidgetByName(t){return javajzWidgets[t]}function updatezWidget(t){t&&(t["data!"]?t["data!"]():alert("ERROR (updateWidget) zwidget /"+t.id+"/ with no 'data!' message"))}function str2jsVar(str){var str2="";if("string"!=typeof str)for(var ii=0;ii<str.length;ii++)str2=(0===ii?"":str2+"\n")+str[ii];else str2=str;return str2.match(/^\s*[\"\']/)?str2.substr(1):str2.match(/^\s*[\[\{]/)?eval("(function () { return "+str2+";}) ()"):function(){eval(str2)}}function fabricaStandard(t,a,e){var n=document.createElement(t);n.id=a,n.style.visibility="hidden",n.spellcheck=!1;for(var i in e)n[i]=e[i];"input"!==t||dataUnit[a]||(dataUnit[a]=[[""]]);for(var o in dataUnit)if(o.startsWith(a+" ")){var r=o.substr(a.length+1),d=dataUnit[o];n[r]=str2jsVar(d)}return n}function fabricaCombo(t,a,e){return fabricaSelect(t,a,e,!1)}function fabricaList(t,a,e){return fabricaSelect(t,a,e,!0)}function fabricaSelect(t,a,e,n){var i=document.createElement("select");n&&(i.multiple="si"),i.id=t,i.style.visibility="hidden",i.onchange=function(){mensaka(t)};for(var o in a){var r=document.createElement("option");r.value=a[o],r["data!"]=function(){},r.appendChild(document.createTextNode(e[o])),i.appendChild(r)}return i}function fabricaGrupo(t,a,e,n,i){var o=document.createElement("div");o.id=e,o.style.visibility="hidden",o.onchange=function(){mensaka(e)};for(var r in n){var d=document.createElement("input");d.type=t,d.name=e,d.value=n[r],d.label=i[r],d["data!"]=function(){},d.onchange=function(){dataUnit[e+" selected.value"]=[[this.value||"?"]],dataUnit[e+" selected.label"]=[[this.label||"?"]],dataUnit[e+"_value"]=[[this.value||"?"]]},"0"===r||"Y"!=a&&"V"!=a||o.appendChild(document.createElement("br")),o.appendChild(d),o.appendChild(document.createTextNode(i[r]))}return o}function fabricaSimpleTable(t,a){var e=document.createElement("div");e.id=t,e.widgetype="t",e.style.visibility="hidden";for(var n in a)e[n]=a[n];return e}function jaxGetHttpReq(){return window.XMLHttpRequest?new XMLHttpRequest:window.ActiveXObject?new ActiveXObject("Microsoft.XMLHTTP"):void alert("Your browser does not support AJAX!")}function startsWith(t,a){return t.slice(0,a.length)===a}function jaxDefaultResponseFunc(t,a){var e="unitAjaxResponse";responseAjaxUnit=startsWith(t,"#"+e+"#")?evaFileStr2obj(t)[e]||{}:{"ajaxRESP-rawBody":[[t]]};var n,i=1;do n=a.getResponseHeader("ajaxRESP-parameter"+i),n&&(responseAjaxUnit["ajaxRESP-parameter"+i]=[[n]]),i++;while(n)}function AJAXFormatBody(t,a,e,n){function i(t){return!n&&dataUnit[a[t]]&&1==dataUnit[a[t]].length&&1==dataUnit[a[t]][0].length?[[encodeURIComponent(dataUnit[a[t]])]]:dataUnit[a[t]]||""}var o;if(e=e||"eva",!a){a=[];for(var r in dataUnit)a.push(r)}var d="";if("propval"===e)for(o in a)d+="\n"+a[o]+":"+i(o);else if("json"===e){d="{";for(o in a)d+="'"+a[o]+"' : '"+i(o)+"', ";d+="}"}else if("eva"===e){var s=evaFileObj("#unitAjaxRequest#\n   <_jGastonaVersion> 1.0\n"),l=s.obj.unitAjaxRequest;for(o in a)l[a[o]]=i(o);d=s.toText()}else alert("ERROR: calling AJAXFormatBody with not supported format ["+e+"]");return d}function ajaxGenericPreProcessResponse(){}function AJAXgenericPost(t,a,e,n){var i=jaxGetHttpReq();if(!i)return!1;if(n||(n=jaxDefaultResponseFunc),i.onreadystatechange=function(){4==i.readyState&&200==i.status&&(ajaxGenericPreProcessResponse(i),n(i.responseText,i),mensaka("ajaxResponse "+t))},i.open("POST",t,!0),e)for(var o in e)i.setRequestHeader(o,e[o]);i.send(a)}function AJAXSendData(t,a,e,n){var i="string"==typeof a?a:AJAXFormatBody(t,a,e,n);AJAXgenericPost(t,i)}function AJAXUploadFile(t,a,e){var n=t.files[0];if(!n||""===n)return!1;var i=new FormData;i.append("filename",n),AJAXgenericPost(a,i,e)}function AJAXLoadRootJast(t){AJAXgenericPost("loadRootJast","",{"ajaxREQ-jastName":t},function(t){loadJast(evaFileStr2obj(t))})}function setContentsFromBody(t,a,e){var n="";if(e){for(var i=a.split("\n"),o=0;o<i.length;){var r=/([^:]*):(.*)/.exec(i[o++]);if(r){if(""===r[1])break;setData(r[1],r[2])}}for(var d=o;d<i.length;d++)n=n+(d!=o?"\n":"")+i[d]}else n=a;var s=document.getElementById(t);s&&setValueToElement(s,n)}function AJAXgetIdContent(t,a,e){var n="string"==typeof a?{"ajaxREQ-param":a}:a||{};n["ajaxREQ-id"]=t,AJAXgenericPost("getIdContent","",n,function(a){setContentsFromBody(t,a,e)})}function AJAXgetIdMultipleContents(t,a){AJAXgetIdContent(t,a,!0)}var dataUnit,listixUnit,corpiny,layMan,responseAjaxUnit,javajzWidgets,minWidth=-1,minHeight=-1;return corpiny=existingPlaceId,loadJast(evaConfig),document.body.onresize=function(){adaptaLayout()},{getLayoutMan:function(){return layMan},mensaka:mensaka,getIdValue:function(t){var a=document.getElementById(t);return a?a.value:getDataCell(t)},getData:getDataCell,setData:setData,setVarTable_DimVal:setVarTable_DimVal,getCellEvaUnit:getCellEvaUnit,adapta:adaptaLayout,mask:function(t,a){layMan&&(layMan.maskLayoutId(t,a),adaptaLayout())},unmask:function(t){layMan&&(layMan.unmaskLayoutId(t),adaptaLayout())},getAjaxResponse:function(){return responseAjaxUnit},AJAXPost:AJAXgenericPost,AJAXSendData:AJAXSendData,AJAXSendBody:AJAXSendData,AJAXUploadFile:AJAXUploadFile,AJAXLoadRootJast:AJAXLoadRootJast,AJAXgetIdContent:AJAXgetIdContent,AJAXgetIdMultipleContents:AJAXgetIdMultipleContents}}