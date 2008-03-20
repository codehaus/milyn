/*
	Milyn - Copyright (C) 2006

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License (version 2.1) as published by the Free Software
	Foundation.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

	See the GNU Lesser General Public License for more details:
	http://www.gnu.org/licenses/lgpl.txt
*/

function selectElement(elementId) {
    hideContent("righttop")
    hideContent("rightbottom")
    showContent(elementId);
}

function selectVisitor(elementId) {
    hideContent("rightbottom")
    showContent(elementId);
}

function showContent(newContentId) {
    var newContent = document.getElementById(newContentId)
    if(newContent != null) {
        newContent.style.visibility = "visible";
        newContent.style.display = "block";
    }
}

function hideContent(contentContainerId) {
    var contentContainer = document.getElementById(contentContainerId)

    // Hide the currently selected content in that container...
    if(contentContainer != null) {
        var contentElements = contentContainer.getElementsByTagName("div");

        for(var i = 0; i < contentElements.length; i++) {
            if(contentElements.item(i).getAttribute("class") == "report-container") {
                contentElements.item(i).style.display = "none";
                contentElements.item(i).style.visibility = "hidden";
            }
        }
    } else {
        alert("Page error.  Unknown content container ID '" + contentContainerId + "'.");
    }
}

