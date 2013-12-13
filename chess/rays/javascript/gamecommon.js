var globalGameMessage = "";
var ranks = "8,7,6,5,4,3,2,1".split(',');
var files = "a,b,c,d,e,f,g,h".split(',');

//----------------------------------------
// true = White displayed on bottom.
// false = White displayed on top.
//----------------------------------------
var globalBoardOrientation = true;

var brdPosSize = 50;
var cpImgSize = brdPosSize-1;

var wkingimg   = new Image(cpImgSize,cpImgSize);
var wqueenimg  = new Image(cpImgSize,cpImgSize);
var wbishopimg = new Image(cpImgSize,cpImgSize);
var wrookimg   = new Image(cpImgSize,cpImgSize);
var wknightimg = new Image(cpImgSize,cpImgSize);
var wpawnimg   = new Image(cpImgSize,cpImgSize);
var bkingimg   = new Image(cpImgSize,cpImgSize);
var bqueenimg  = new Image(cpImgSize,cpImgSize);
var bbishopimg = new Image(cpImgSize,cpImgSize);
var brookimg   = new Image(cpImgSize,cpImgSize);
var bknightimg = new Image(cpImgSize,cpImgSize);
var bpawnimg   = new Image(cpImgSize,cpImgSize);
var emptyimg   = new Image(cpImgSize,cpImgSize);

wkingimg.src   = "images/whiteking.gif";
wqueenimg.src  = "images/whitequeen.gif";
wbishopimg.src = "images/whitebishop.gif";
wrookimg.src   = "images/whiterook.gif";
wknightimg.src = "images/whiteknight.gif";
wpawnimg.src   = "images/whitepawn.gif";
bkingimg.src   = "images/blackking.gif";
bqueenimg.src  = "images/blackqueen.gif";
bbishopimg.src = "images/blackbishop.gif";
brookimg.src   = "images/blackrook.gif";
bknightimg.src = "images/blackknight.gif";
bpawnimg.src   = "images/blackpawn.gif";
emptyimg.src   = "images/clear1X1.gif";

var globalPositionFrom = null;
var globalPositionTo   = null;
var globalPieceAtFrom  = null;
var globalPieceAtTo    = null;
var globalHoldPreviousState   = null;

function highlightBlackMove(stc, stp)
{
    var fromPosIndex = -1;
	var toPosIndex = -1;
	if(!(stc && stp))
	{
		return;
	}
	if(stc == stp)
	{
		return;
	}
	if(stc.indexOf("|") > -1 || stp.indexOf("|") > -1)
	{
		var bCapture = false;
		carr = (stc.split("|"))[0].split(",");
		parr = (stp.split("|"))[0].split(",");
		if(carr.length == 64 && parr.length == 64)
		{
			for(var i=0; i<64 && (fromPosIndex < 0 || toPosIndex < 0); ++i)
			{
                if(fromPosIndex < 0 && parr[i].charAt(0) == "B" && carr[i].charAt(0) == "X")
				{
					fromPosIndex = i;	
				}
				if(toPosIndex < 0 && parr[i].charAt(0) == "X" && carr[i].charAt(0) == "B")
				{
					toPosIndex = i;	
				}
				if(toPosIndex < 0 && parr[i].charAt(0) == "W" && carr[i].charAt(0) == "B")
				{
					bCapture = true;
					toPosIndex = i;	
				}
			}
			if(fromPosIndex > -1 && toPosIndex > -1)
			{
				var posColor = boardPosArray[fromPosIndex].color;
				boardPosArray[fromPosIndex].positionDIV.className = (posColor == 1) ? "lightMoveMarker" : "darkMoveMarker";
				
				posColor = boardPosArray[toPosIndex].color;
				boardPosArray[toPosIndex].positionDIV.className = (bCapture) ? "capturemarker" : ((posColor == 1) ? "lightMoveMarker" : "darkMoveMarker");
			}
		}
	}
} 

function initgame()
{
	
	document.getElementById("board").style.height = ((8 * brdPosSize) + 3);
	document.getElementById("board").style.width = ((8 * brdPosSize) + 3);

	
	createBoardPositions();

	//--------------------------------------------
	// Not needed for playing against the server.
	// 
	// initBoardPositions();
	//--------------------------------------------
	
	displayGameState(null);

	document.getElementById("board").className = "boardbkgd";
}


function moveStartEvaluate(bp)
{
	globalPositionFrom = bp;
	globalPieceAtFrom = globalPositionFrom.chessPiece;
	
	debugOutput("Move from: " + globalPositionFrom.file + "" + globalPositionFrom.rank);
}

function movePieceToPosition(pc, pos)
{
	pc.chessPieceDiv.style.left = pos.positionDIV.style.left;
	pc.chessPieceDiv.style.top = pos.positionDIV.style.top;
}

function moveEndEvaluate(bp)
{
	globalPositionTo = bp;
	globalPieceAtTo = globalPositionTo.chessPiece;
	
	if(globalPieceAtTo === globalPieceAtFrom)
	{
		debugOutput(" ... No move made.");
	}
	else if(globalPieceAtTo && globalPieceAtTo.color == globalPieceAtFrom.color)
	{
		movePieceToPosition(globalPieceAtFrom, globalPositionFrom);
		debugOutput(" ... Illegal move.");
	}
	else if(globalPieceAtTo && globalPieceAtTo.color != globalPieceAtFrom.color)
	{
		debugOutput(" ... Capture: " + globalPositionTo.file + "" + globalPositionTo.rank);
		
		document.getElementById("board").removeChild(globalPositionTo.chessPiece.chessPieceDiv);
		globalPositionTo.chessPiece = globalPositionFrom.chessPiece;
		globalPositionFrom.chessPiece = null;
		
	}
	else
	{
		globalPositionTo.chessPiece = globalPositionFrom.chessPiece;
		globalPositionFrom.chessPiece = null; 	
	}
	showSerialized(serializeBoardOut(), true);
}

//-----------------------------------------------
// Reduce z-indexes of all chess pieces
// except the one with id
//-----------------------------------------------
function reduceZindexes(id)
{
	for(var i=0; i<64; ++i)
	{
		if(boardPosArray[i].chessPiece)
		{
			if(boardPosArray[i].chessPiece.chessPieceDiv.id != id)
			{
				boardPosArray[i].chessPiece.chessPieceDiv.style.zIndex = 20;
			}
		}
	}
}

function constrainToGrid(oLeft, oTop)
{
	var gridX = Math.round(oLeft/brdPosSize)*brdPosSize;
	if(gridX < 0)
		gridX = 0;
	if(gridX > 7 * brdPosSize)
		gridX = 7 * brdPosSize;
		
	var gridY = Math.round(oTop/brdPosSize)*brdPosSize;
	if(gridY < 0)
		gridY = 0;
	if(gridY > 7 * brdPosSize)
		gridY = 7 * brdPosSize;
		
	return new boardPoint(gridX, gridY);
}

function boardPoint(x,y)
{
	this.left = x;
	this.top = y;
}

function getBoardPositionUpperLeftPoint(ix)
{
	//------------------------------------------
	// Eventually will depend on orientation.
	//------------------------------------------
	var x = ix%8 * brdPosSize;
	var y = Math.floor(ix/8) * brdPosSize;
	return new boardPoint(x,y);
}

function getBoardPositionIndexFromLocation(bp)
{
	var p = Math.floor(bp.left/brdPosSize);
	var q = Math.floor(bp.top/brdPosSize);
	return q*8 + p; 		
}

function displayGameState(sGameStArr)
{
	var positionState;
    for(var i=0; i<64; ++i)
	{
		var bpPoint = getBoardPositionUpperLeftPoint(i);
		var bpDiv = boardPosArray[i].positionDIV;
		
		if(sGameStArr && sGameStArr.length == 64)
		{
			positionState = sGameStArr[i];
		}
		else
		{	
			positionState = "";
		}

		// Reset the board position DIV's class back to normal.
		if(document.getElementById(boardPosArray[i].fr))
		{
			document.getElementById(boardPosArray[i].fr).className = boardPosArray[i].holdClass;
		}
		
		if(!document.getElementById(boardPosArray[i].fr))
		{
			bpDiv.style.left = bpPoint.left;
			bpDiv.style.top = bpPoint.top;
			document.getElementById("board").appendChild(bpDiv);
		}
		
		if(boardPosArray[i].chessPiece)
		{
			cpDiv = boardPosArray[i].chessPiece.chessPieceDiv;
			if(document.getElementById(cpDiv.id))
			{
				document.getElementById("board").removeChild(cpDiv);				
			}
			cpDiv.style.left = bpPoint.left;
			cpDiv.style.top = bpPoint.top;
			cpDiv.style.zIndex = "25"; 
			document.getElementById("board").appendChild(cpDiv);
		}
	}
}

function positionColorFromFileRank(file, rank)
{
	//       01234567
	//       01010101
	var x = "abcdefgh".indexOf(file); 	
	var n = x % 2;
	var m = rank % 2;
	if(n == 0)
	{
		return (m == 0) ? 1 : 0;		
	}
	else
	{
		return (m == 0) ? 0 : 1;
	}
}

function serializeBoardOut()
{
	var addDelimiter = false;
	var sb = new StringBuffer();
	for(var i=0; i<64; ++i)
	{
		if(addDelimiter)
		{
			sb.append(",");
		}
		
		if(boardPosArray[i].chessPiece)
		{
			sb.append(boardPosArray[i].chessPiece.sToken);
		}
		else
		{
			sb.append("X");
		}
		addDelimiter = true;
		
	}
	sb.append("|");
	sb.append(document.CHESSGAMEFORM.GAMEID.value);
	return sb.toString();
}


function reportGameStateError(s)
{
	DEBUGWRITE("Game state error detected...");
	DEBUGWRITE(s);
	DEBUGWRITE("------------------------------------------");
}


//------------------------------------------
// Game state is parallel to boardPosArray.
// Each entry looks like this: "a,8,1,R,W"
// There are 64 entries delimited by pipe.
//------------------------------------------
function serializeBoardIn(sGameState)
{
	var gameState = "";
	var gameMessage = "";
	var mainParts;
	
	if(sGameState.indexOf("|") > -1)
	{
		mainParts = sGameState.split("|"); 			
		if(mainParts.length == 2)
		{
			gameState = mainParts[0];
			document.CHESSGAMEFORM.GAMEID.value = mainParts[1];
		}
		else if(mainParts.length == 3)
		{
			gameState = mainParts[0];
			document.CHESSGAMEFORM.GAMEID.value = mainParts[1];
			gameMessage = mainParts[2];
		}
		else
		{
			reportGameStateError(sGameState);
			return;			
		}
	}
	else
	{
		reportGameStateError(sGameState);
		return;			
	}
	
	var sGameStateArray = gameState.split(",");
	if(sGameStateArray.length != 64)
	{
		reportGameStateError(sGameState);
		return;
	}
	
	for(var i=0; i<64; ++i)
	{
		
		var sTok = sGameStateArray[i];	
		var currentPieceAtPos = boardPosArray[i].chessPiece;
				
		//----------------------------------------------------------
		// "X" indicates no piece at position in sGameState
		//----------------------------------------------------------
		if(sTok == "X")
		{
			if(currentPieceAtPos)
			{
				document.getElementById("board").removeChild(currentPieceAtPos.chessPieceDiv);
				boardPosArray[i].chessPiece = null;
				
//DEBUGWRITE("Removed piece...");				
				
			}
		}
		else // sGameState contains chess piece for this position.
		{
			if(currentPieceAtPos)
			{
				if(currentPieceAtPos.sToken != sTok)
				{
					document.getElementById("board").removeChild(currentPieceAtPos.chessPieceDiv);
					boardPosArray[i].chessPiece = null;
					boardPosArray[i].chessPiece = makeChessPieceFromToken(sTok, i);
					
//DEBUGWRITE("Replaced piece...");					
					
				}
			}
			else 
			{
				boardPosArray[i].chessPiece = makeChessPieceFromToken(sTok, i);
				
//DEBUGWRITE("currentPieceAtPos = false so Created piece...");
				
			}
		}
	}
	displayGameState(sGameStateArray);
	if(gameMessage.length > 0)
	{
		alert(gameMessage);
	}
}

function makeChessPieceFromToken(sToken, idnum)
{
	if(!sToken)
		return null;

	sToken = sToken.trim();
	

	if(sToken == 'X')
		return null;
	
	var bColor = (sToken.charAt(0) == 'W');

	var sName;
	var imgRef;
	var letterName;
	switch(sToken)
	{
		case "BP":
		case "WP":
			sName = "Pawn";
			imgRef = (bColor) ? wpawnimg : bpawnimg;
			letterName = 'p';
			break;
		case "WRK":
		case "BRQ":
		case "BRK":
		case "WRQ":
			sName = "Rook";
			imgRef = (bColor) ? wrookimg : brookimg;
			letterName = 'R';
			break;
		case "BKT":
		case "WKT":
			sName = "Knight";
			imgRef = (bColor) ? wknightimg : bknightimg;
			letterName = 'N';
			break;
		case "BB":
		case "WB":
			sName = "Bishop";
			imgRef = (bColor) ? wbishopimg : bbishopimg;
			letterName = 'B';
			break;
		case "BQ":
		case "WQ":
			sName = "Queen";
			imgRef = (bColor) ? wqueenimg : bqueenimg;
			letterName = 'Q';
			break;
		case "BKG":
		case "WKG":
			sName = "King";
			imgRef = (bColor) ? wkingimg : bkingimg;
			letterName = 'K';
			break;
	}
	return new ChessPiece(imgRef, bColor, letterName, sName, idnum, sToken);
}

function ChessPiece(pcimg, pccolor, pctype, pcname, inx, sTok)
{
	this.sToken = sTok;
	this.pcimg  = pcimg;   // Reference to chess piece image.
	this.inx    = inx;
	this.color  = pccolor; // true = white, false = black.
	this.pcType = pctype;  // B,N,R,K,Q,p
	this.name   = pcname;  // Bishop, Knight, Rook, Queen, King, Rook, Pawn
	this.moves  = 0;       // number of times moved during game.
	this.moveno = 0;       // Game Move Index at the time this piece was moved.
                           // used to determine en-passant capture.		

	this.pieceDivId = ((pccolor) ? "W_" : "B_") + pctype + "_"+inx;
    var tmp = document.createElement('div');
	tmp.setAttribute('id',this.pieceDivId);
	tmp.className = "chesspiece";
	tmp.innerHTML = "<img src='" + pcimg.src + "' height='" + cpImgSize + "' width='" + cpImgSize + "' >";
	this.chessPieceDiv = tmp;
	Drag.init(this.chessPieceDiv);
}

var boardPosArray = new Array();
function createBoardPositions()
{
	var boardPosIndex = 0;
	for(var k = 0; k < ranks.length; ++k)
	{
		for(var i = 0; i < files.length; ++i)
		{
			boardPosArray[boardPosIndex++] = new BoardPosition(files[i], ranks[k]);				
		}	
	}
}

function BoardPosition(file, rank)
{
	this.file = file;
	this.rank = rank;
	this.color = positionColorFromFileRank(file, rank);
	this.chessPiece = null;
	this.fr = "" + file + "-" + rank;
		
	var tmp = document.createElement('div');
	tmp.setAttribute('id', this.fr);
	tmp.className = (positionColorFromFileRank(file, rank) ? "whitesquare" : "blacksquare");
	this.holdClass = tmp.className;
	
	tmp.style.height = brdPosSize;
	tmp.style.width = brdPosSize;
	if(Math.floor(brdPosSize/5) > 3)
	{
		tmp.style.fontSize = Math.floor(brdPosSize/5) + "pt";
	}
	else
	{
		tmp.style.fontSize = "3pt";
	}
	
	tmp.style.position = "absolute";
	tmp.style.top = "0px";
	tmp.style.left = "0px";
	tmp.innerHTML = "<BR>" + file + rank;
	this.positionDIV = tmp;
}


function debugOutput(s)
{
	var tmp = document.getElementById("outputwindow").value;
	var addtmp = s + "\n";
	tmp += addtmp; 
	document.getElementById("outputwindow").value = tmp;
	document.getElementById("outputwindow").scrollTop = document.getElementById("outputwindow").scrollHeight; 
}

function showSerialized(s, clearFirst)
{
	if(clearFirst)
	{
		document.getElementById("outputwindow2").value = "";
	}
	var tmp = document.getElementById("outputwindow2").value;
	var addtmp = s + "\n";
	tmp += addtmp; 
	document.getElementById("outputwindow2").value = tmp;
	document.CHESSGAMEFORM.GAMESTATE.value = tmp;
}

function startGame()
{
	sendMove(true);	
}

function sendMove(bStartNewGame)
{
    document.getElementById("popupMask").style.display = "block";
    document.getElementById("waitmessage").style.display = "block";
    globalHoldPreviousState = serializeBoardOut(); 
    
    var req;
    if(mozilla)
    {
        req = new XMLHttpRequest();        
    }
    else
    {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }
	try	
	{
	    req.open('POST', '/chess/move', true);
	}
	catch(e)
	{
		DEBUGWRITE(e);    	
	}
    req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); 
    req.onreadystatechange = function () {
        if (req.readyState == 4) 
        {
            if(req.status == 200)
            {		
                var message = req.responseText;
                postSendMove(message);
            } 
            else
            {
                alert("Error occurred during send move: " + " - " + req.status);
            }
        }
    };
    try
    {
    	if(bStartNewGame)
    	{
    		document.CHESSGAMEFORM.GAMESTATE.value = "STARTGAME";
			req.send(createFormPostContent());
    	}
    	else
    	{
    		req.send(createFormPostContent());
    	}
    }
    catch(ex)
    {
		DEBUGWRITE(ex);
    }
}

function postSendMove(message)
{
	document.CHESSGAMEFORM.GAMESTATE.value = message;
	serializeBoardIn(document.CHESSGAMEFORM.GAMESTATE.value);
	highlightBlackMove(message, globalHoldPreviousState);
	document.getElementById("popupMask").style.display = "none";		
	document.getElementById("waitmessage").style.display = "none";
}

function createFormPostContent()
{
	var pageForm = document.CHESSGAMEFORM;
	var elemType;
	var elemObject;
	var addAmpersand = false;
	var sb = new StringBuffer();
	for(var i=0;i<pageForm.elements.length;++i)
	{
		elemObject = pageForm.elements[i];
		if(elemObject.type && elemObject.name && elemObject.name.length > 0)
		{
			var elemType = elemObject.type.toUpperCase();
			
			if(elemType == "SELECT")
				continue;
				
			if(elemType == "BUTTON")
				continue;
			
			if(addAmpersand)
				sb.append("&");
			
			sb.append(elemObject.name);
			sb.append("=");
			sb.append(escape(elemObject.value));
			
			addAmpersand = true;
		}
	}
	return sb.toString();
}

function initBoardPositions()
{
	var idnum = 0;
	boardPosArray[0].chessPiece = makeChessPieceFromToken('BRQ', idnum++);
	boardPosArray[1].chessPiece = makeChessPieceFromToken('BKT', idnum++);
	boardPosArray[2].chessPiece = makeChessPieceFromToken('BB',  idnum++);
	boardPosArray[3].chessPiece = makeChessPieceFromToken('BQ',  idnum++);	
	boardPosArray[4].chessPiece = makeChessPieceFromToken('BKG', idnum++); 
	boardPosArray[5].chessPiece = makeChessPieceFromToken('BB',  idnum++);	
	boardPosArray[6].chessPiece = makeChessPieceFromToken('BKT', idnum++);	
	boardPosArray[7].chessPiece = makeChessPieceFromToken('BRK', idnum++);
	for(var i=8;i<16;++i)
	{
		boardPosArray[i].chessPiece = makeChessPieceFromToken('BP', idnum++);
	}
	
	idnum = 0;
	for(var i=48;i<56;++i)
	{
		boardPosArray[i].chessPiece = makeChessPieceFromToken('WP', idnum++);
	}
	boardPosArray[56].chessPiece = makeChessPieceFromToken('WRQ', idnum++);
	boardPosArray[57].chessPiece = makeChessPieceFromToken('WKT', idnum++);
	boardPosArray[58].chessPiece = makeChessPieceFromToken('WB',  idnum++);
	boardPosArray[59].chessPiece = makeChessPieceFromToken('WQ',  idnum++);
	boardPosArray[60].chessPiece = makeChessPieceFromToken('WKG', idnum++);
	boardPosArray[61].chessPiece = makeChessPieceFromToken('WB',  idnum++);
	boardPosArray[62].chessPiece = makeChessPieceFromToken('WKT', idnum++);
	boardPosArray[63].chessPiece = makeChessPieceFromToken('WRK', idnum++);
}
