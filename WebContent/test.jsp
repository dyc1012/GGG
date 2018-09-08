<html lang="en">
<head>
	<meta charset="utf-8">
	<title>jQuery UI Dialog - ClickOutside demo</title>
	
	<style>
	body {
	font-size: 12px;
  color:#FFF;
  background:#2D2E2F;
	font-family: Menlo, Monaco, "Andale Mono", "lucida console", "Courier New", monospace;
}
.ui-widget{
  font-family: Menlo, Monaco, "Andale Mono", "lucida console", "Courier New", monospace;
  font-size:14px;
  padding:10px;
  border-width:3px;
}
.ui-dialog .ui-dialog-content{
  padding-top:20px;
}
.demo-description {
	clear: both;
	padding: 12px;
	font-size: 1.3em;
	line-height: 1.4em;
}

.ui-draggable, .ui-droppable {
	background-position: top;
}
.ui-dialog .ui-dialog-titlebar-close {
  width:auto;
}
.ui-icon{
  text-indent: 0;
  width:auto;
}
button{
  margin:20px auto;
  display:block;
  background:#CE0538;
  border:none;
  padding:10px;
  font-size:16px;
  margin:10px auto;
  border-radius:5px;
  cursor:pointer;
  width:500px;
  color:#FFF;
  font-family: Menlo, Monaco, "Andale Mono", "lucida console", "Courier New", monospace;
}
.btn-red{
  background: #CE0538;
  padding:10px;
  color:#FFF;
  text-decoration:none;
  border-radius:2px;
  display:inline-block;
  margin-top:10px;
}
.btn-red:focus,
button:focus,
button:hover{
  background:#830324;
}
.ui-button-icon-only .ui-button-text, 
.ui-button-icons-only .ui-button-text{
  text-indent:0;  
  padding: 0;
}
.ui-dialog .ui-dialog-titlebar-close{
  height: 20px;
}
	
	</style>
	
	
	<script type="text/javascript">
	// JS for demo
	$( document ).ready(function() {
	$( "#dialog1" ).dialog({
				autoOpen: false,
				show: {
					effect: "fade",
					duration: 150
				},
				hide: {
					effect: "fade",
					duration: 150
				},
				position: {
	  			my: "center",
	  			at: "center"
				},
				// Add the 2 options below to use click outside feature
				clickOutside: true, // clicking outside the dialog will close it
				clickOutsideTrigger: "#opener1"  // Element (id or class) that triggers the dialog opening 
			});
			
	    $( "#dialog2" ).dialog({
				autoOpen: false,
				show: {
					effect: "fade",
					duration: 150
				},
				hide: {
					effect: "fade",
					duration: 150
				},
				position: {
	  			my: "center",
	  			at: "center"
				},
				// Add the 2 options below to use click outside feature
				clickOutside: true, // clicking outside the dialog will close it
				clickOutsideTrigger: "#opener2"  // Element (id or class) that triggers the dialog opening
			});		

	    $( "#dialog3" ).dialog({
				autoOpen: false,
				show: {
					effect: "fade",
					duration: 150
				},
				hide: {
					effect: "fade",
					duration: 150
				},
				position: {
	  			my: "center",
	  			at: "center"
				},
				clickOutside: false // For demo purpose. Not necessary because this is the default value
			});	

			$( "#opener1" ).click(function() {
				$( "#dialog1" ).dialog( "open" );
			});
			
			$( "#opener2" ).click(function() {
				$( "#dialog2" ).dialog( "open" );
			});
			
			$( "#opener3" ).click(function() {
				$( "#dialog3" ).dialog( "open" );
			});
	});

	/* jQuery UI dialog clickoutside */

	/*
	The MIT License (MIT)

	Copyright (c) 2013 - AGENCE WEB COHERACTIO

	Permission is hereby granted, free of charge, to any person obtaining a copy of
	this software and associated documentation files (the "Software"), to deal in
	the Software without restriction, including without limitation the rights to
	use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
	the Software, and to permit persons to whom the Software is furnished to do so,
	subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
	FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
	COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
	CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
	*/

	$.widget( "ui.dialog", $.ui.dialog, {
	  options: {
	    clickOutside: false, // Determine if clicking outside the dialog shall close it
	    clickOutsideTrigger: "" // Element (id or class) that triggers the dialog opening 
	  },

	  open: function() {
	    var clickOutsideTriggerEl = $( this.options.clickOutsideTrigger );
	    var that = this;
	    
	    if (this.options.clickOutside){
	      // Add document wide click handler for the current dialog namespace
	      $(document).on( "click.ui.dialogClickOutside" + that.eventNamespace, function(event){
	        if ( $(event.target).closest($(clickOutsideTriggerEl)).length == 0 && $(event.target).closest($(that.uiDialog)).length == 0){
	          that.close();
	        }
	      });
	    }
	    
	    this._super(); // Invoke parent open method
	  },
	  
	  close: function() {
	    var that = this;
	    
	    // Remove document wide click handler for the current dialog
	    $(document).off( "click.ui.dialogClickOutside" + that.eventNamespace );
	    
	    this._super(); // Invoke parent close method 
	  },  

	});
	
	</script>
</head>
<body>
  <div class="demo-description">
  <p>Jquery UI dialogs can't be closed when you click elsewhere on the page that makes it a problem when you have several dialogs on a single page (you'd expect that opening a dialog widget will automatically close the other opened dialogs) or links or elements that keep you on the same page (e.g. form, ajax refresh, ...).</p>
    <p>Agence Web Coheractio has developed a small plugin (1 ko) enabling that important feature.</p>
  </div>

<div id="dialog1" title="Basic dialog #1">
	<strong>Dialog #1</strong>
	<p>This is an animated dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.</p>
</div>

<div id="dialog2" title="Basic dialog #2">
	<strong>Dialog #2</strong>
	<p>This is an animated dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'close' link.</p>
</div>

<div id="dialog3" title="Basic dialog #3">
	<strong>Dialog #3 : default jQuery UI dialog without click outside</strong>
	<p>This is a animated dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'close' link.</p>
</div>

<div class="button-holder">
  <button id="opener1">Open Dialog #1 <br/>With click outside active</button>
</div>
<div class="button-holder">
  <button id="opener2"><span>Open Dialog #2 <br/>With click outside active &amp; inner span button</span></button>
</div>
<div class="button-holder">
  <button id="opener3">Open Dialog #3<br/>Without click outside</button>
</div>


<div class="demo-description">
  <p>Dialogs may be animated by specifying an effect for the show and/or hide properties.  You must include the individual effects file for any effects you would like to use.</p>
  <p>The plugin can be downloaded on Github from our Github repository : https://github.com/coheractio/jQuery-UI-Dialog-ClickOutside
</div>
</body>


</html>
