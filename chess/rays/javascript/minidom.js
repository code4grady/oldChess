//-----------------------------------------------------
// Mini Browser Independent DOM API
// Author: Ray Wilson 2-28-2006
//-----------------------------------------------------
var mozilla=document.getElementById && !document.all;
var ie=document.all;

function getElementParent(elem)
{
    return (mozilla) ? elem.parentNode : elem.parentElement;
}
function getElementChildren(elem)
{
    return (mozilla) ? elem.childNodes : elem.children;
}
function setElementAttribute(elem, attname, attvalue)
{
    if(mozilla)
    {
        elem.setAttribute(attname, attvalue);
    }
    else
    {
        elem.attributes[attname].value = attvalue;
    }
}
function getElementAttribute(elem, attname)
{
    if(mozilla)
    {
        return elem.getAttribute(attname);
    }
    else
    {
        if(elem.attributes[attname])
        {
            return elem.attributes[attname].value;
        }
        else
        {
            return "";
        }
    }
}
function getEvent(e)
{
    if(!e)
    {
        return window.event;
    }
    return e;
}
function stopEventBubble(e)
{
    if (mozilla)
    {
        e.stopPropagation();
    }
    else if (ie)
    {
        e.cancelBubble = true;
    }    
}
function getEventSource(e)
{
    return (mozilla) ? e.target : e.srcElement;
}
function parseQueryString()
{
    var queryString = new Object();
    var qs = self.location.search;
    if(qs.charAt(0) == "?")
    {
        qs = qs.substring(1);
    }
    if (qs.length == 0)
    {
        return queryString;
    }
    qs = qs.replace(/\+/g, ' ');
    var args = qs.split('&');
    for (var i=0; i<args.length; ++i) 
    {
        var value;
        var pair = args[i].split('=');
        var name = unescape(pair[0]);
        if (pair.length == 2)
        {
	        value = unescape(pair[1]);
        }
        else
        {
	        value = name;
        }
        queryString[name] = value;
    }
    return queryString;
}

