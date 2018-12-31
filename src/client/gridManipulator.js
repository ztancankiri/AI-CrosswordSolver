var clicked = "";
var directionAcross = true;
var old_clicked = "-1";

$(document).ready(function(){
    for (var i = 1; i <= 25; i++) {
		$("#cell_" + i).click(function(event) {
			old_clicked = clicked;
			if (event.target.id.includes("cell_"))
				clicked = event.target.id;
			else
				clicked = event.target.parentNode.id;
			
			if (old_clicked === clicked)
				directionAcross = !directionAcross;
			
			setAllCellsNormal();
			setCellHighlighted("#" + clicked);
		});
	}
});

$(document).keydown(function(e) {
	if (clicked.includes("cell_")) {
		if (isLetter(e.key)) {
			setCellCenterText("#" + clicked, e.key.toUpperCase());
			goNextCell();
		}
		else if (e.keyCode == 8 || e.keyCode == 46) {
			setCellCenterText("#" + clicked, "");
			goPrevCell();
		}
	}
});

function clearPuzzle() {
	for (var i = 1; i <= 25; i++) {
		setCellCornerText("#cell_" + i, "");
		setCellCenterText("#cell_" + i, "");
		setCellCircle("#cell_" + i, false);
		setCellNormal("#cell_" + i);

		setCellCornerText("#sol_cell_" + i, "");
		setCellCenterText("#sol_cell_" + i, "");
		setCellCircle("#sol_cell_" + i, false);
		setCellNormal("#sol_cell_" + i);
	}
	setAllCellsNormal();
}

function clearClues() {
	$("#acrossTable").empty();
	$("#downTable").empty();
}

function goNextCell() {
	var splitted = clicked.split("_");
	var id = splitted[1];
	
	if (id % 5 == 0 && directionAcross)
		return;
	
	if (id > 20 && !directionAcross)
		return;
	
	var next_id;
	
	if (directionAcross)
		next_id = parseInt(id) + 1;
	else
		next_id = parseInt(id) + 5;
	
	clicked = "cell_" + next_id;
	
	setAllCellsNormal();
	setCellHighlighted("#" + clicked);
}

function goPrevCell() {
	var splitted = clicked.split("_");
	var id = splitted[1];
	
	if (id % 5 == 1 && directionAcross)
		return;
	
	if (id < 6 && !directionAcross)
		return;
	
	var prev_id;
	
	if (directionAcross)
		prev_id = parseInt(id) - 1;
	else
		prev_id = parseInt(id) - 5;
	
	clicked = "cell_" + prev_id;
	
	setAllCellsNormal();
	setCellHighlighted("#" + clicked);
}

function setCellCornerText(cellid, cornertext) {
	if (!isBlackCell(cellid))
		$(cellid).find(".cornertext")[0].innerText = cornertext;
}

function setCellCenterText(cellid, centertext) {
	if (!isBlackCell(cellid))
		$(cellid).find(".centertext")[0].innerText = centertext;
}

function setCellCircle(cellid, isCircled) {
	if (!isBlackCell(cellid)) {
		if (isCircled)
			$(cellid).find(".circle")[0].style = "display: block";
		else
			$(cellid).find(".circle")[0].style = "display: none";
	}
}

function setCellBlack(cellid) {
	$(cellid).addClass("blackcell");
}

function setCellNormal(cellid) {
	$(cellid).removeClass("blackcell");
}

function setCellHighlighted(cellid) {
	if (!isBlackCell(cellid)) {
		$(cellid).addClass("highlightedcell");
		highlightNeighbours(cellid);
	}
}

function setAllCellsNormal() {
	for (var i = 1; i <= 25; i++) {
		if ($("#cell_" + i).hasClass("highlightedcell"))
			$("#cell_" + i).removeClass("highlightedcell");
		
		if ($("#cell_" + i).hasClass("nhighlightedcell"))
			$("#cell_" + i).removeClass("nhighlightedcell");
	}
}

function isLetter(str) {
	return str.length === 1 && str.match(/[a-z]/i);
}

function isBlackCell(cellid) {
	return $(cellid).hasClass("blackcell");
}

function addClue(tableID, clueNo, clueText) {
	var tr = document.createElement('tr');        
	var td = document.createElement('td');
	var divClueNo = document.createElement('div');
	var divClueText = document.createElement('div');
	
	divClueNo.setAttribute("class", "clueNo");
	divClueNo.innerText = clueNo;
	
	divClueText.setAttribute("class", "clueText");
	divClueText.innerText = clueText;
	
	$(td).append(divClueNo, divClueText);
	$(tr).append(td);
	$(tableID).append(tr);
}

function setCellNHighlighted(cellid) {
	if (!isBlackCell(cellid)) {
		$(cellid).addClass("nhighlightedcell");
	}
}

function highlightNeighbours(cellid) {
	var splitted = cellid.split("_");
	var id = splitted[1];
	
	if (directionAcross) {
		if (id >= 1 && id <= 5) {
			for (var i = 1; i <= 5; i++) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
		else if (id >= 6 && id <= 10) {
			for (var i = 6; i <= 10; i++) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
		else if (id >= 11 && id <= 15) {
			for (var i = 11; i <= 15; i++) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}		
		else if (id >= 16 && id <= 20) {
			for (var i = 16; i <= 20; i++) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
		else if (id >= 21 && id <= 25) {
			for (var i = 21; i <= 25; i++) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
	}
	else {
		if (id % 5 == 1) {
			for (var i = 1; i <= 21; i += 5) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
		else if (id % 5 == 2) {
			for (var i = 2; i <= 22; i += 5) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
		else if (id % 5 == 3) {
			for (var i = 3; i <= 23; i += 5) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}		
		else if (id % 5 == 4) {
			for (var i = 4; i <= 24; i += 5) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
		else if (id % 5 == 0) {
			for (var i = 5; i <= 25; i += 5) {
				if (i != id) {
					setCellNHighlighted("#cell_" + i);
				}
			}
		}
	}
}