/*
Simple Image Trail script- By JavaScriptKit.com
Visit http://www.javascriptkit.com for this script and more
This notice must stay intact
*/

var offsetfrommouse = [15,15]; //image x,y offsets from cursor position in pixels. Enter 0,0 for no offset
var displayduration = 0; //duration in seconds image should remain visible. 0 for always.
var currentimageheight = 270; // maximum image size.

if (document.getElementById || document.all) {
    document.write('<div id="trailimageid">');
    document.write('</div>');
}

function popup(mylink) {
    var href;

    if (! window.focus) {
        return true;
    }

    if (typeof(mylink) == 'string') {
       href=mylink;
    } else {
        href=mylink.href;
    }

    window.open(href, 'notes', 'width=1000,height=500,scrollbars=yes,screenX=100,screenY=100,left=100,top=100');

    return false;
}

function gettrailobj() {
    if (document.getElementById)
        return document.getElementById("trailimageid").style
    else if (document.all)
        return document.all.trailimagid.style
}

function gettrailobjnostyle() {
    if (document.getElementById)
        return document.getElementById("trailimageid")
    else if (document.all)
        return document.all.trailimagid
}


function truebody() {
    return (!window.opera && document.compatMode && document.compatMode != "BackCompat") ? document.documentElement : document.body
}

function showtrail(imagename, title, description, showthumb, height) {

    if (height > 0) {
        currentimageheight = height;
    }

    document.onmousemove = followmouse;

    newHTML = '<div style="padding: 5px; background-color: #FFFFFF; border: 1px solid #888888;">';
    newHTML = newHTML + '<h2>' + title + '</h2>';
    newHTML = newHTML + description + '<br/>';

    if (showthumb > 0) {
        newHTML = newHTML + '<div align="center" style="padding: 8px 2px 2px 2px;">';
        newHTML = newHTML + '<img src="' + imagename + '" border="0"></div>';
    }

    gettrailobjnostyle().innerHTML = newHTML;
    gettrailobj().visibility = "visible";

}

function showresource(details) {
    document.onmousemove = followmouse;

    newHTML = '<div style="padding: 5px; background-color: #FFFFFF; border: 1px solid #888888;">';
    newHTML = newHTML + details + '</div>';

    gettrailobjnostyle().innerHTML = newHTML;
    gettrailobj().visibility = "visible";
}

function hidetrail() {
    gettrailobj().visibility = "hidden"
    document.onmousemove = ""
    gettrailobj().left = "-500px"

}

function followmouse(e) {

    var xcoord = offsetfrommouse[0]
    var ycoord = offsetfrommouse[1]

    //var docwidth = document.all ? truebody().scrollLeft + truebody().clientWidth : pageXOffset + window.innerWidth - 15
    var docwidth = 1000
    var docheight = document.all ? Math.min(truebody().scrollHeight, truebody().clientHeight) : Math.min(window.innerHeight)

//if (document.all){
    // gettrailobjnostyle().innerHTML = 'A = ' + truebody().scrollHeight + '<br>B = ' + truebody().clientHeight;
    //} else {
    // gettrailobjnostyle().innerHTML = 'C = ' + document.body.offsetHeight + '<br>D = ' + window.innerHeight;
    //}

    if (typeof e != "undefined") {
        if (docwidth - e.pageX < 380) {
            xcoord = e.pageX - xcoord - 800; // Move to the left side of the cursor
        } else {
            xcoord += e.pageX;
        }
        if (docheight - e.pageY < (currentimageheight + 110)) {
            ycoord += e.pageY - Math.max(0, (110 + currentimageheight + e.pageY - docheight - truebody().scrollTop));
        } else {
            ycoord += e.pageY;
        }

    } else if (typeof window.event != "undefined") {
        if (docwidth - event.clientX < 380) {
            xcoord = event.clientX + truebody().scrollLeft - xcoord - 800; // Move to the left side of the cursor
        } else {
            xcoord += truebody().scrollLeft + event.clientX
        }
        if (docheight - event.clientY < (currentimageheight + 110)) {
            ycoord += event.clientY + truebody().scrollTop - Math.max(0, (110 + currentimageheight + event.clientY - docheight));
        } else {
            ycoord += truebody().scrollTop + event.clientY;
        }
    }

    //var docwidth = document.all ? truebody().scrollLeft + truebody().clientWidth : pageXOffset + window.innerWidth - 15
    var docwidth = 1000
    var docheight = document.all ? Math.max(truebody().scrollHeight, truebody().clientHeight) : Math.max(document.body.offsetHeight, window.innerHeight)
    if (ycoord < 0) {
        ycoord = ycoord * -1;
    }
    gettrailobj().left = xcoord + "px"
    gettrailobj().top = ycoord + "px"

}