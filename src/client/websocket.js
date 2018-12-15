var wsUri 		= "ws://localhost:9090";
var webSocket 	= null;

$(document).ready(function() {
	intialize();
});

function selectPuzzle(event) {
	var selectedPuzzle = event.options[event.selectedIndex].value;
	console.log(selectedPuzzle);
	
	if (selectedPuzzle === 'today') {
		alert("Getting today's puzzle.");
		getPuzzle();
	}
	else {
		alert("Getting old puzzle from archive. (" + selectedPuzzle + ")");
		getPuzzleFromArchive(selectedPuzzle);
	}
}

function intialize() {
	console.log('Initializing...');
	toggleLoading(true);
	
	try {
		webSocket = new WebSocket(wsUri);
		webSocket.onopen = function(event) { 
			onOpen(event);
		};
		webSocket.onclose = function(event) { 
			onClose(event);
		};
		webSocket.onmessage = function(event) {
			onMessage(event);
		};
		webSocket.onerror = function(event) {
			onError(event);
		};
	}
	catch(e) {
		console.log('Exception: ' + e);
		var event = new Object();
		event.code = 4002;
		onClose(event);
	}
}

function onOpen(event) {
	var optJSON = { "type":"getDate" };
	var requestJSON = JSON.stringify(optJSON);

	webSocket.send(requestJSON);
}

function onClose(event) {
    if (event.code === 4002) {
        alert('» Connection Error...');
    }
}

var msgObj;
function onMessage(event) {
	console.log(event.data);
	msgObj = JSON.parse(event.data);
	
	if (msgObj.type === 'puzzle') {
		clearClues();
		clearPuzzle();
		
		if (msgObj.blackCells.length > 0) {
			for (var i = 0; i < msgObj.blackCells.length; i++) {
				setCellBlack("#sol_cell_" + msgObj.blackCells[i]);
				setCellBlack("#cell_" + msgObj.blackCells[i]);
			}
		}
		
		if (msgObj.circles.length > 0) {
			for (var i = 0; i < msgObj.circles.length; i++) {
				setCellCircle("#sol_cell_" + msgObj.circles[i], true);
				setCellCircle("#cell_" + msgObj.circles[i], true);
			}
		}
		
		if (msgObj.acrossClues.length > 0) {
			for (var i = 0; i < msgObj.acrossClues.length; i++) {
				var clue = msgObj.acrossClues[i];
				addClue("#acrossTable", clue.no, clue.clue);
			}
		}
		
		if (msgObj.downClues.length > 0) {
			for (var i = 0; i < msgObj.downClues.length; i++) {
				var clue = msgObj.downClues[i];
				addClue("#downTable", clue.no, clue.clue);
			}
		}
		
		if (msgObj.questionPos.length > 0) {
			for (var i = 0; i < msgObj.questionPos.length; i++) {
				var questionPos = msgObj.questionPos[i];
				setCellCornerText("#sol_cell_" + questionPos.pos, questionPos.no);
				setCellCornerText("#cell_" + questionPos.pos, questionPos.no);
			}
		}
		
		if (msgObj.grid.length > 0) {
			for (var i = 0; i < msgObj.grid.length; i++) {
				var grid = msgObj.grid[i];
				setCellCenterText("#sol_cell_" + grid.pos, grid.char);
			}
		}
		
		
		setTimeout(showPuzzle, 1);
	}
	else if (msgObj.type === 'date') {
		$("#date").text(msgObj.date);
		
		var optJSON = { "type":"getPuzzleArchiveList" };
		var requestJSON = JSON.stringify(optJSON);

		webSocket.send(requestJSON);
	}
	else if (msgObj.type === 'puzzleFromArchive') {
		clearClues();
		clearPuzzle();
		
		if (msgObj.blackCells.length > 0) {
			for (var i = 0; i < msgObj.blackCells.length; i++) {
				setCellBlack("#sol_cell_" + msgObj.blackCells[i]);
				setCellBlack("#cell_" + msgObj.blackCells[i]);
			}
		}
		
		if (msgObj.circles.length > 0) {
			for (var i = 0; i < msgObj.circles.length; i++) {
				setCellCircle("#sol_cell_" + msgObj.circles[i], true);
				setCellCircle("#cell_" + msgObj.circles[i], true);
			}
		}
		
		if (msgObj.acrossClues.length > 0) {
			for (var i = 0; i < msgObj.acrossClues.length; i++) {
				var clue = msgObj.acrossClues[i];
				addClue("#acrossTable", clue.no, clue.clue);
			}
		}
		
		if (msgObj.downClues.length > 0) {
			for (var i = 0; i < msgObj.downClues.length; i++) {
				var clue = msgObj.downClues[i];
				addClue("#downTable", clue.no, clue.clue);
			}
		}
		
		if (msgObj.questionPos.length > 0) {
			for (var i = 0; i < msgObj.questionPos.length; i++) {
				var questionPos = msgObj.questionPos[i];
				setCellCornerText("#sol_cell_" + questionPos.pos, questionPos.no);
				setCellCornerText("#cell_" + questionPos.pos, questionPos.no);
			}
		}
		
		if (msgObj.grid.length > 0) {
			for (var i = 0; i < msgObj.grid.length; i++) {
				var grid = msgObj.grid[i];
				setCellCenterText("#sol_cell_" + grid.pos, grid.char);
			}
		}
		
		toggleLoading(false);
	}
	else if (msgObj.type === 'puzzleArchiveList') {
		var puzzleSelector = $("#puzzleSelector");

		for (var i = 0; i < msgObj.puzzles.length; i++) {
			var opt = document.createElement("option");
			opt.text 	= "Puzzle on " + msgObj.puzzles[i];
			opt.value 	= msgObj.puzzles[i];
			$(puzzleSelector).append(opt);
		}
		
		getPuzzle();
	}
	else if (msgObj.type === 'infoMessage') {
		//$("#infoMessage").text($("#infoMessage").text() + "\n - " + msgObj.msg + "...");
	}
}

function showPuzzle() {
	toggleLoading(false);
}
	
function toggleLoading(isLoading) {
	if (isLoading) {
		$("#box").hide();
		$("#loading").show();
	}
	else {
		$("#loading").hide();
		$("#box").show();
	}
}

function getPuzzleFromArchive(date) {
	var optJSON = { "type":"getPuzzleFromArchive", "date":date };
	var requestJSON = JSON.stringify(optJSON);

	webSocket.send(requestJSON);
	toggleLoading(true);
}

function getPuzzle() {
	var optJSON = { "type":"getPuzzle" };
	var requestJSON = JSON.stringify(optJSON);

	webSocket.send(requestJSON);
	toggleLoading(true);
}

function solve() {
	var optJSON = {"type":"solve"}
	var requestJSON = JSON.stringify(optJSON);

	webSocket.send(requestJSON);
}