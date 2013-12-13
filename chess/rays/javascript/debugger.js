var RAYSDEBUGGER = null;
function DEBUGWRITE(debugstring)
{
	try
	{
		if(!RAYSDEBUGGER)
		{
			RAYSDEBUGGER = window.open("", "DEBUGGER", "width=250,height=200,resizable",false);
			RAYSDEBUGGER.document.open();
			RAYSDEBUGGER.document.write("<HTML>");
			RAYSDEBUGGER.document.write("<HEAD>");
			RAYSDEBUGGER.document.write("<STYLE>");
			RAYSDEBUGGER.document.write(".btn   {border:1px solid #999999;font-size:8pt;font-family:Verdana;}");
			RAYSDEBUGGER.document.write("BODY   {background-color:#FFFFDD; margin:0px;}");
			RAYSDEBUGGER.document.write("TD     {font-size:8pt;font-family:Verdana;}");
			RAYSDEBUGGER.document.write("</STYLE>");
			RAYSDEBUGGER.document.write("<SCR" + "IPT language='JavaScript'>");
			RAYSDEBUGGER.document.write("function showMessage(msg)");
			RAYSDEBUGGER.document.write("{");
			RAYSDEBUGGER.document.write("if(!document.getElementById('RUN').checked)");
			RAYSDEBUGGER.document.write("return;");
			RAYSDEBUGGER.document.write("var content = '';");
			RAYSDEBUGGER.document.write("if(document.getElementById('APPEND').checked)");
			RAYSDEBUGGER.document.write("{");
			RAYSDEBUGGER.document.write("content = document.getElementById('DEBUGTEXT').value + msg;");
			RAYSDEBUGGER.document.write("}");
			RAYSDEBUGGER.document.write("else");
			RAYSDEBUGGER.document.write("{");
			RAYSDEBUGGER.document.write("content = msg;");
			RAYSDEBUGGER.document.write("}");
			RAYSDEBUGGER.document.write("document.getElementById('DEBUGTEXT').value = content + '\\n';");
			RAYSDEBUGGER.document.write("}");
			RAYSDEBUGGER.document.write("</SCR" + "IPT>");
			RAYSDEBUGGER.document.write("<TITLE>Debug Display Window</TITLE>");
			RAYSDEBUGGER.document.write("</HEAD>");
			RAYSDEBUGGER.document.write("<BODY>");
			RAYSDEBUGGER.document.write("<TABLE HEIGHT='100%' WIDTH='100%' CELLPADDING='0' CELLSPACING='0' BORDER='0'>");
			RAYSDEBUGGER.document.write("<TR>");
			RAYSDEBUGGER.document.write("<TD COLSPAN='6'>");
			RAYSDEBUGGER.document.write("<TEXTAREA ID='DEBUGTEXT' style='height:100%;width:100%;font-family:Courier New;font-size:8pt;'>");
			RAYSDEBUGGER.document.write("</TEXTAREA>");
			RAYSDEBUGGER.document.write("</TD>");
			RAYSDEBUGGER.document.write("</TR>");
			RAYSDEBUGGER.document.write("<TR style='height:24px' BGCOLOR='#DDDDDD'>");
			RAYSDEBUGGER.document.write("<TD WIDTH='1%'>&nbsp;&nbsp;<INPUT ");
			RAYSDEBUGGER.document.write("class='btn' TYPE='BUTTON' VALUE=' Clear ' onclick='document.getElementById(\"DEBUGTEXT\").value=\"\";'></TD>");
			RAYSDEBUGGER.document.write("<TD WIDTH='1%'>&nbsp;&nbsp;<INPUT CHECKED TYPE='CHECKBOX' ID='RUN'></TD>");
			RAYSDEBUGGER.document.write("<TD>On</TD>");
			RAYSDEBUGGER.document.write("<TD WIDTH='1%'>&nbsp;&nbsp;<INPUT CHECKED TYPE='CHECKBOX' ID='APPEND'></TD>");
			RAYSDEBUGGER.document.write("<TD>Append</TD>");
			RAYSDEBUGGER.document.write("<TD WIDTH='200px'>&nbsp;</TD>");
			RAYSDEBUGGER.document.write("</TR>");
			RAYSDEBUGGER.document.write("</TABLE>");
			RAYSDEBUGGER.document.write("</BODY>");
			RAYSDEBUGGER.document.write("</HTML>");
			RAYSDEBUGGER.document.close();
		}
		RAYSDEBUGGER.showMessage(debugstring);
	}
	catch(e)
	{
		alert("IE SUCKS...");
		RAYSDEBUGGER = null;
	}
}

var PROPERTYWINDOW = null;
function SHOWPROPS(obj)
{
	try
	{
		if(!PROPERTYWINDOW)
		{
			PROPERTYWINDOW = window.open("", "DEBUGGER", "width=500,height=200,resizable",false);
			PROPERTYWINDOW.document.open();
			PROPERTYWINDOW.document.write("<HTML><HEAD><TITLE>Property Display Window</TITLE>");
			PROPERTYWINDOW.document.write("</HEAD><BODY style=\"margin:0px;\">");
			PROPERTYWINDOW.document.write("<TABLE HEIGHT='100%' WIDTH='100%' CELLPADDING='0' CELLSPACING='0' BORDER='0'>");
			PROPERTYWINDOW.document.write("<TR><TD><TEXTAREA ID='PROPERTIES'"); 
			PROPERTYWINDOW.document.write("style='height:100%;width:100%;font-family:Courier New;font-size:8pt;'>");
			PROPERTYWINDOW.document.write("</TEXTAREA></TD></TR></TABLE></BODY></HTML>");
			PROPERTYWINDOW.document.close();
		}
		var z = 0;
		var s = "";
		var zs;
		for (var m in obj)
		{
			++z;
			zs = 4 - (""+z).length;
			switch(zs)
			{
			case 3:
				s += "000" + z + ": ";
				break;
			case 2:
				s += "00"  + z + ": ";
				break;
			case 1:
				s += "0"   + z + ": ";
				break;
			default:
				s += ""    + z + ": ";
				break;
			}
			try
			{
				s += m + " = " + obj[m] + "\n";
			}
			catch(e)
			{
				s += m + " = <ex -- " + m + " -- ex>\n";
			}
		}
		if(s.length > 0)
		{
			PROPERTYWINDOW.document.getElementById("PROPERTIES").value = s;		
		}
		else
		{
			PROPERTYWINDOW.document.getElementById("PROPERTIES").value = "Object ID: " + 
				obj.id + "\nObject Length: " + obj.length + "\nObject Name: " + 
				obj.name + "\nNo properties found for\n" + obj;
		}
	}
	catch(e)
	{
		PROPERTYWINDOW = null; // The child window was probably closed.
	}
}

