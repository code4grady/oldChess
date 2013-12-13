/**************************************************
 * dom-drag.js
 * 09.25.2001
 * www.youngpup.net
 **************************************************
 * 10.28.2001 - fixed minor bug where events
 * sometimes fired off the handle, not the root.
 **************************************************/
var Drag = {
	obj : null,
	init : function(o)
	{
		o.onmousedown = Drag.start;
		if (isNaN(parseInt(o.style.left))) o.style.left = "0px";
		if (isNaN(parseInt(o.style.top ))) o.style.top  = "0px";
	},

	start : function(e)
	{
		e = Drag.fixE(e);
    		
		var o = Drag.obj = this;
		var y = parseInt(o.style.top);
		var x = parseInt(o.style.left);
				
		var bpnt = new boardPoint(x,y);
		var bpix = getBoardPositionIndexFromLocation(bpnt);
		moveStartEvaluate(boardPosArray[bpix]);
		
		o = Drag.obj;
	    o.lastMouseX = e.clientX;
		o.lastMouseY = e.clientY;
        o.style.zIndex = "100";
        reduceZindexes(o.id);

		document.onmousemove = Drag.drag;
		document.onmouseup = Drag.end;

		return false;
	},

	drag : function(e)
	{
		e = Drag.fixE(e);
		var o = Drag.obj;

		var ey	= e.clientY;
		var ex	= e.clientX;

		var y = parseInt(o.style.top);
		var x = parseInt(o.style.left);
		
		var nx = x + (ex - o.lastMouseX);
		var ny = y + (ey - o.lastMouseY);
		
		Drag.obj.style["left"] = nx + "px";
		Drag.obj.style["top"]  = ny + "px";
		
		Drag.obj.lastMouseX	= ex;
		Drag.obj.lastMouseY	= ey;

		return false;
	},

	end : function()
	{
		var oLeft = parseInt(Drag.obj.style["left"]);
		var oTop = parseInt(Drag.obj.style["top"]);
		var constrainedPoint = constrainToGrid(oLeft, oTop);
		var gridX = constrainedPoint.left;
		var gridY = constrainedPoint.top;

		Drag.obj.style["left"] = gridX;
		Drag.obj.style["top"]  = gridY;
		
		document.onmousemove = null;
		document.onmouseup   = null;
		
		Drag.obj = null;
		
		
		var bpnt = new boardPoint(gridX,gridY);
		var bpix = getBoardPositionIndexFromLocation(bpnt);
		moveEndEvaluate(boardPosArray[bpix]);
		
	},

	fixE : function(e)
	{
		if (typeof e == 'undefined') e = window.event;
		if (typeof e.layerX == 'undefined') e.layerX = e.offsetX;
		if (typeof e.layerY == 'undefined') e.layerY = e.offsetY;
		return e;
	}
	
};