var agt		= navigator.userAgent.toLowerCase();
var versInt	= parseInt(navigator.appVersion);
var is_aol	= (agt.indexOf("aol") != -1);
var cnnDomainArray = location.hostname.split( '.' );
var EditionDomain = ( cnnDomainArray.length > 1 ) ? '.' + cnnDomainArray[cnnDomainArray.length-2] + '.' + cnnDomainArray[cnnDomainArray.length-1] : '';

function CNN_goTo( url ) {
	window.location.href = url;
}

function CNN_roofBar( tableCellRef, hoverFlag ) {
	if ( hoverFlag ) {
		tableCellRef.style.backgroundImage = 'url("http://i.cnn.net/cnn/.element/img/1.0/main/roofbar_bg2.gif")';
		if ( document.getElementsByTagName ) {
			tableCellRef.getElementsByTagName( 'a' )[0].style.color = '#c00';
		}
	} else {
		tableCellRef.style.backgroundImage = 'url("http://i.cnn.net/cnn/.element/img/1.0/main/roofbar_bg.gif")';
		if ( document.getElementsByTagName ) {
			tableCellRef.getElementsByTagName( 'a' )[0].style.color = '#333';
		}
	}
}

function CNN_roofBarClick( tableCellRef, url ) {
	CNN_roofBar( tableCellRef, 0 );
	CNN_goTo( url );
}

function CNN_navBar( tableCellRef, hoverFlag, navStyle ) {
	if ( hoverFlag ) {
		switch ( navStyle ) {
			case 1:
				tableCellRef.style.backgroundColor = '#69c';
				break;
			default:
				if ( document.getElementsByTagName ) {
					tableCellRef.getElementsByTagName( 'a' )[0].style.color = '#c00';
				}
		}
	} else {
		switch ( navStyle ) {
			case 1:
				tableCellRef.style.backgroundColor = '#036';
				break;
			default:
				if ( document.getElementsByTagName ) {
					tableCellRef.getElementsByTagName( 'a' )[0].style.color = '#000';
				}
		}
	}
}

function CNN_navBarClick( tableCellRef, navStyle, url ) {
	CNN_navBar( tableCellRef, 0, navStyle );
	CNN_goTo( url );
}


// this function is used to redirect the search form if necessary
function validate( theFormValidate ) { return CNN_validateSearchForm( theFormValidate ); }
function CNN_validateSearchForm( theForm )
{
	var site = 'cnn';
	var queryString = '';
	
	if ( theForm.sites )
	{
		if ( theForm.sites.options ) {		//	"sites" should be a select
			site = theForm.sites.options[theForm.sites.selectedIndex].value;
		} else {
			if ( theForm.sites.length )
			{
				for ( i = 0; i < theForm.sites.length; i++ )
				{
					if ( theForm.sites[i].checked ) {
						site = theForm.sites[i].value;
					}
				}
			}
			else
			{
				site = theForm.sites.value;
			}
		}
	}
	
	if ( theForm.qt ) {
		queryString = theForm.qt.value;
	} else {
		if ( theForm.QueryText ) {
			queryString = theForm.QueryText.value;
		} else {
			if ( theForm.query ) {
				queryString = theForm.query.value;
			}
		}
	}
	
	if ( !queryString ) {
		return false;
	}
	
	switch ( site.toLowerCase() ) {
		case "web":
		case "google":
			theForm.action = ( location.hostname.indexOf( 'edition' ) != -1 ) ? "http://websearch.edition.cnn.com/search/search" : "http://websearch.cnn.com/search/search";
			theForm.query.value = queryString;
			return true;
		
		case "internet":
		case "cnnasiaweb":
		case "cnneuropeweb":
			theForm.action = "http://search.netscape.com/nscp_results.adp";
			theForm.query.value = queryString;
			return true;
		
		case "cnn":
			theForm.action = "http://search.cnn.com/cnn/search";
			theForm.query.value = queryString;
			return true;
			
		case "edition":
			theForm.action = "http://search.edition.cnn.com/pages/search/advanced.jsp";
			return true;	
		
		case "cnnasia":
			theForm.action = "http://search.cnn.com/asia/search";
			theForm.query.value = queryString;
			theForm.source.value = 'cnn';
			return true;
		
		case "cnneurope":
		case "cnneuropeir":
		case "cnneuropeit":
		case "cnneuropenl":
		case "cnneuropeswz":
		case "cnneuropeuk":
			theForm.action = "http://search.cnn.com/europe/search";
			theForm.query.value = queryString;
			theForm.source.value = 'cnn';
			return true;
		
		case "cnnsi":
			theForm.action = "http://search.si.cnn.com/si/search";
			theForm.query.value = queryString;
			theForm.source.value = 'si';
			return true;
		
		case "cnnfn":
		case "cnnmoney":
			theForm.action = "http://search.money.cnn.com/money/search";
			theForm.query.value = queryString;
			theForm.source.value = 'money';
			return true;
		
		case "time":
			theForm.action = "http://www.pathfinder.com/time/daily/searchresults/1,2645,,00.html";
			theForm.keyword.value = queryString;
			theForm.col.value = 'time';
			return true;
		
		case "cnnfyi":
			theForm.action = "http://websearch.cnn.com/search/snsearch";
			theForm.source.value = 'fyi';
			return true;
		
		case "cnnlaw":
		default:
			return true;						//	unsupported site?
	}
}


// used to open popup windows from the dateline
function CNN_openMap( location )
{
	CNN_openPopup( location, '620x430', "width=620,height=430,scrollbars=no,resizable=no" );
}


// this is for opening pop-up windows
function CNN_openPopup( url, name, widgets, openerUrl )
{
	var host = location.hostname;
	window.top.name = "opener";
	var popupWin = window.open( url, name, widgets );
	
	if ( popupWin && popupWin.opener ) {
		if ( openerUrl )
		{
			popupWin.opener.location = openerUrl;
			popupWin.focus();
		}
	}
}


function email()
{
	address=document.emailform.emailinput.value;
	location='http://www.cnn.com/EMAIL/index.html?'+address;
}

function asiaEmail()
{
	address=document.emailform.emailinput.value;
	location='http://asia.cnn.com/EMAIL/asia.index.html?'+address;
}

function euroEmail()
{
	address=document.emailform.emailinput.value;
	location='http://europe.cnn.com/EMAIL/europe_index.html?'+address;
}

function italiaEmail()
{
	address=document.emailform.emailinput.value;
	location='http://www.cnnitalia.it/EMAIL/index.html?'+address;
}

function splitWindow(s) 
{
	window.name="_mainWindow";
	var w=127;h=153;
	var v=navigator.appVersion.substring(0,1);
	if (navigator.appName=="Netscape")
	{
		if ((v==3)||(v==4)) {w=127,h=168;} 
		else {w=132,h=179;}
	} 
	else {w=112,h=137;}
	if (!s) s="*itn/ord";
	else if (s.indexOf("*,")!=-1) 
	s = s.substring(0,s.indexOf("*"))+"*itn/ord"+s.substring(s.indexOf(","));
	var f="http://cnn.com/event.ng/Type=click&RunID=17006&ProfileID=749&AdID=11567&GroupID=313&FamilyID=2433&TagValues=4.8.435.487.1098&Redirect=http:%2F%2Fwww.itn.net%2Fcgi%2Fget%3Fjava%2FFlightTicker%2FsplitWindow.html&Stamp="+s;
	var win=window.open(f,"ticker","status=0,scrollbars=0,resizable=0,width="+w+",height="+h);
}

//this is for the dropdowns on the pages like TRAVEL


function napVector (vectorChoice) {
	   location.href = document.nap.vector.options[document.nap.vector.selectedIndex].value;
	   }
function ipVector (vectorChoice) {
	   location.href = document.ip.vector.options[document.ip.vector.selectedIndex].value;
	   }


function CNN_setOptionsFromArray( selectOptions, array ) {
//	selectOptions = options reference -	document.forms['{formname}'].{selectname}.options
//								   or -	document.{formname}.{selectname}.options
//	array = Array - { 'text', 'value', 'text', 'value', ... }
	var length = selectOptions.length;
	var i = 0;
	if ( new Option() ) {
		length = 2;								//	start at the top
		for ( i = 0; i < array.length; i+=2 ) {
			selectOptions[length++] = new Option( array[i], array[i+1], false );
		}
	} else if ( document.createElement( "OPTION" ) ) {
		if ( selectOptions.length > 1 ) {
			for ( i = 2; i < selectOptions.length; i++ ) {
				selectOptions.remove( 2 );		//	strip off options because we add them later
			}
		}
		for ( i = 0; i < array.length; i+=2 ) {
			var newOption = document.createElement( "OPTION" );
			newOption.text = array[i];
			newOption.value = array[i+1];
			selectOptions.add( newOption );
		}
	}
}


// _________________________________________________________________________
// select your edition popup
var EditionURL = '/virtual/editions/europe/2000/roof/change.pop/frameset.exclude.html';  // URL for editions popup HTML
var EdPopWidth = 300;	// Width of popup window
var EdPopHeight = 300;	// Height for popup window

if ( document.cookie && ( document.cookie.indexOf( 'SelectedEdition' ) == -1 ) && ( location.pathname.substr( -4 ) != '.jsp' ) ) {
	//CNN_openPopup( EditionURL, "AdInterstitial", "scrollbars=auto,width="+EdPopWidth+",height="+EdPopHeight );
}

// _________________________________________________________________________
// The following code was added to launch the 'change editions' popup.
// It checks first if it's able to set a cookie before launching the window.
function launchEditionPopup() {
	if ( ! WM_browserAcceptsCookies() ) {
		alert( "In order to set your default edition you must accept cookies." );
	} else {
		CNN_openPopup( EditionURL, 'defaultpopup', 'scrollbars=auto,width=' + EdPopWidth + ',height=' + EdPopHeight );
	}
}

function clickEdLink() {
	if ( document.cookie && ( document.cookie.indexOf( 'SelectedEdition' ) == -1 ) && ( location.pathname.substr( -4 ) != '.jsp' ) ) {
		launchEditionPopup();
	}
}

// Extend life of edition cookie
var editionCookie = WM_readCookie( 'SelectedEdition' );
if ( editionCookie == 'edition' ) {
	document.cookie = 'SelectedEdition=' + escape(editionCookie) + ';expires=' + new Date( '1/1/2037' ).toGMTString() + ';path=/;domain=' + EditionDomain;
}


// _____________________________________________________________ WebMonkey code
/*
WM_setCookie(), WM_readCookie(), WM_killCookie()
A set of functions that eases the pain of using cookies.

Source: Webmonkey Code Library
(http://www.hotwired.com/webmonkey/javascript/code_library/)

Author: Nadav Savio
*/

// This next little bit of code tests whether the user accepts cookies.
function WM_browserAcceptsCookies() {
	var WM_acceptsCookies = false;
	if ( document.cookie == '' ) {
		document.cookie = 'WM_acceptsCookies=yes'; // Try to set a cookie.
		if ( document.cookie.indexOf( 'WM_acceptsCookies=yes' ) != -1 ) {
			WM_acceptsCookies = true;
		} // If it succeeds, set variable
	} else { // there was already a cookie
		WM_acceptsCookies = true;
	}
	
	return ( WM_acceptsCookies );
}

function WM_setCookie( name, value, hours, path, domain, secure ) {
	if ( WM_browserAcceptsCookies() ) { // Don't waste your time if the browser doesn't accept cookies.
		var numHours = 0;
		var not_NN2 = ( navigator && navigator.appName
					&& (navigator.appName == 'Netscape')
					&& navigator.appVersion
					&& (parseInt(navigator.appVersion) == 2) ) ? false : true;

		if ( hours && not_NN2 ) { // NN2 cannot handle Dates, so skip this part
			if ( (typeof(hours) == 'string') && Date.parse(hours) ) { // already a Date string
				numHours = hours;
			} else if ( typeof(hours) == 'number' ) { // calculate Date from number of hours
				numHours = ( new Date((new Date()).getTime() + hours*3600000) ).toGMTString();
			}
		}
		
		document.cookie = name + '=' + escape(value) + ((numHours)?(';expires=' + numHours):'') + ((path)?';path=' + path:'') + ((domain)?';domain=' + domain:'') + ((secure && (secure == true))?'; secure':''); // Set the cookie, adding any parameters that were specified.
	}
} // WM_setCookie

function WM_readCookie( name ) {
	if ( document.cookie == '' ) { // there's no cookie, so go no further
	    return false;
	} else { // there is a cookie
	    var firstChar, lastChar;
		var theBigCookie = document.cookie;
		firstChar = theBigCookie.indexOf(name);	// find the start of 'name'
		var NN2Hack = firstChar + name.length;
		if ( (firstChar != -1) && (theBigCookie.charAt(NN2Hack) == '=') ) { // if you found the cookie
			firstChar += name.length + 1; // skip 'name' and '='
			lastChar = theBigCookie.indexOf(';', firstChar); // Find the end of the value string (i.e. the next ';').
			if (lastChar == -1) lastChar = theBigCookie.length;
			return unescape( theBigCookie.substring(firstChar, lastChar) );
		} else { // If there was no cookie of that name, return false.
			return false;
		}
	}	
} // WM_readCookie

function WM_killCookie( name, path, domain ) {
	var theValue = WM_readCookie( name ); // We need the value to kill the cookie
	if ( theValue ) {
		document.cookie = name + '=' + theValue + '; expires=Fri, 13-Apr-1970 00:00:00 GMT' + ((path)?';path=' + path:'') + ((domain)?';domain=' + domain:''); // set an already-expired cookie
	}
} // WM_killCookie


// ______________________________________________________________________ Apple
// Copyright © 2000 by Apple Computer, Inc., All Rights Reserved.

// initialize global variables
var detectableWithVB = false;
var pluginFound = false;


function canDetectPlugins() {
	if ( detectableWithVB || (navigator.plugins && navigator.plugins.length > 0) ) {
		return true;
	}
	return false;
}

function detectFlash() {
	pluginFound = detectPlugin( 'Shockwave', 'Flash' );
	// if not found, try to detect with VisualBasic
	if ( !pluginFound && detectableWithVB ) {
		pluginFound = detectActiveXControl( 'ShockwaveFlash.ShockwaveFlash.1' );
	}
	return pluginFound;
}

function detectDirector() {
	pluginFound = detectPlugin( 'Shockwave', 'Director' );
	// if not found, try to detect with VisualBasic
	if ( !pluginFound && detectableWithVB ) {
		pluginFound = detectActiveXControl( 'SWCtl.SWCtl.1' );
	}
	return pluginFound;
}

function detectQuickTime() {
	pluginFound = detectPlugin( 'QuickTime' );
	// if not found, try to detect with VisualBasic
	if ( !pluginFound && detectableWithVB ) {
		pluginFound = detectQuickTimeActiveXControl();
	}
	return pluginFound;
}

function detectReal() {
	pluginFound = detectPlugin( 'RealPlayer' );
	// if not found, try to detect with VisualBasic
	if ( !pluginFound && detectableWithVB ) {
		pluginFound = ( detectActiveXControl('rmocx.RealPlayer G2 Control') ||
			detectActiveXControl('RealPlayer.RealPlayer(tm) ActiveX Control (32-bit)') ||
			detectActiveXControl('RealVideo.RealVideo(tm) ActiveX Control (32-bit)')
		);
	}
	return pluginFound;
}

function detectRealOne() {
	pluginFound = detectPlugin( 'RealOne Player Version Plugin' ) || detectPlugin( 'RealPlayer Version Plugin' );
	// if not found, try to detect with VisualBasic
	if ( !pluginFound && detectableWithVB ) {
		pluginFound = detectRealOneActiveXControl();
	}
	return pluginFound;
}

function detectWindowsMedia() {
	pluginFound = detectPlugin( 'Windows Media' );
	// if not found, try to detect with VisualBasic
	if ( !pluginFound && detectableWithVB ) {
		pluginFound = detectActiveXControl( 'MediaPlayer.MediaPlayer.1' );
	}
	return pluginFound;
}

function detectPlugin() {
	// allow for multiple checks in a single pass
	var daPlugins = arguments;
	// consider pluginFound to be false until proven true
	var pluginFound = false;
	// if plugins array is there and not fake
	if ( navigator.plugins && navigator.plugins.length > 0 ) {
		var pluginsArrayLength = navigator.plugins.length;
		// for each plugin...
		for ( var pluginsArrayCounter = 0; pluginsArrayCounter < pluginsArrayLength; pluginsArrayCounter++ ) {
			// loop through all desired names and check each against the current plugin name
			var numFound = 0;
			for ( var namesCounter = 0; namesCounter < daPlugins.length; namesCounter++ ) {
				// if desired plugin name is found in either plugin name or description
				if ( (navigator.plugins[pluginsArrayCounter].name.indexOf(daPlugins[namesCounter]) >= 0) ||
					(navigator.plugins[pluginsArrayCounter].description.indexOf(daPlugins[namesCounter]) >= 0) ) {
					// this name was found
					numFound++;
				}
			}
			// now that we have checked all the required names against this one plugin,
			// if the number we found matches the total number provided then we were successful
			if ( numFound == daPlugins.length ) {
				pluginFound = true;
				// if we've found the plugin, we can stop looking through at the rest of the plugins
				break;
			}
		}
	}
	return pluginFound;
} // detectPlugin


// Here we write out the VBScript block for MSIE Windows
if ( (navigator.userAgent.indexOf('MSIE') != -1) && (navigator.userAgent.indexOf('Win') != -1) ) {
	document.writeln( '<script language="VBscript">' );

	document.writeln( '\'do a one-time test for a version of VBScript that can handle this code' );
	document.writeln( 'detectableWithVB = False' );
	document.writeln( 'If ScriptEngineMajorVersion >= 2 then' );
	document.writeln( '  detectableWithVB = True' );
	document.writeln( 'End If' );

	document.writeln( '\'this next function will detect most plugins' );
	document.writeln( 'Function detectActiveXControl( activeXControlName )' );
	document.writeln( '  on error resume next' );
	document.writeln( '  detectActiveXControl = False' );
	document.writeln( '  If detectableWithVB Then' );
	document.writeln( '     detectActiveXControl = IsObject( CreateObject( activeXControlName ) )' );
	document.writeln( '  End If' );
	document.writeln( 'End Function' );

	document.writeln( '\'and the following function handles QuickTime' );
	document.writeln( 'Function detectQuickTimeActiveXControl()' );
	document.writeln( '  on error resume next' );
	document.writeln( '  detectQuickTimeActiveXControl = False' );
	document.writeln( '  If detectableWithVB Then' );
	document.writeln( '    detectQuickTimeActiveXControl = False' );
	document.writeln( '    hasQuickTimeChecker = false' );
	document.writeln( '    Set hasQuickTimeChecker = CreateObject( "QuickTimeCheckObject.QuickTimeCheck.1" )' );
	document.writeln( '    If IsObject( hasQuickTimeChecker ) Then' );
	document.writeln( '      If hasQuickTimeChecker.IsQuickTimeAvailable( 0 ) Then ' );
	document.writeln( '        detectQuickTimeActiveXControl = True' );
	document.writeln( '      End If' );
	document.writeln( '    End If' );
	document.writeln( '  End If' );
	document.writeln( 'End Function' );

	document.writeln( '\'and the following function handles RealOne' );
	document.writeln( 'Function detectRealOneActiveXControl()' );
	document.writeln( '  on error resume next' );
	document.writeln( '  detectRealOneActiveXControl = False' );
	document.writeln( '  If detectableWithVB Then' );
	document.writeln( '    detectRealOneActiveXControl = False' );
	document.writeln( '    hasRealOneVersionPlugin = false' );
	document.writeln( '    Set hasRealOneVersionPlugin = CreateObject( "IERPCtl.IERPCtl.1" )' );
	document.writeln( '    If IsObject( hasRealOneVersionPlugin ) Then' );
	document.writeln( '      If hasRealOneVersionPlugin.RealPlayerVersion Then ' );
	document.writeln( '        detectRealOneActiveXControl = True' );
	document.writeln( '      End If' );
	document.writeln( '    End If' );
	document.writeln( '  End If' );
	document.writeln( 'End Function' );

	document.writeln( '<\/scr' + 'ipt>' );
}


// ________________________________________________________________ LaunchVideo

function LV_getRealOneStatus() {	// returns ('undetermined'|'installed'|'notinstalled'|'using')
	var RealOneInst = "undetermined";

	if ( canDetectPlugins() ) {
		if ( detectRealOne() ) {
			RealOneInst = "installed";
			if ( agt.indexOf( "(r1 " ) != -1 ) {
				RealOneInst = "using";
			}
		} else {
			RealOneInst = "notinstalled";
		}
	}

	return RealOneInst;
}

function LV_getVideoUrl( videoUrlPath, format, realOneStatus ) {
	var fullUrl;
	var preferredEdition = "www";
	var selectedEdition = WM_readCookie( "SelectedEdition" );
	var premiumUrlPrefix = "http://premium.cnn.com/pr/video";
	var premiumEdition = "premium.cnn.com";
	
	if ( selectedEdition ) {
		preferredEdition = selectedEdition;
	}
	
	if ( preferredEdition == "asia" || preferredEdition == "europe" ) {
		preferredEdition = "edition";
	}
	
	if ( preferredEdition != "www" ) {
		premiumEdition = "premium." + preferredEdition + ".cnn.com";
		premiumUrlPrefix = "http://" + premiumEdition + "/pr/video";
	}
	
	switch( realOneStatus ) {
		case "using":
			fullUrl = premiumUrlPrefix + "/meta" + videoUrlPath + "r1.smil"; 
			break;
		case "installed":
			fullUrl = premiumUrlPrefix + "/meta" + videoUrlPath + "np.smil"; 
			break;
		case "notinstalled":
		default:
			fullUrl = premiumUrlPrefix + videoUrlPath + "exclude.html";
			if ( format == "public" ) {
				fullUrl = "http://premium.cnn.com/video" + videoUrlPath + "exclude.html";
			}
			break;
	}
	
	return ( fullUrl );
}

function LaunchVideo( videoPath, videoFormat ) {
	var VIDEO_POPUP_WIDTH = 620;
	var VIDEO_POPUP_HEIGHT = 480;
	var realOneStatus = LV_getRealOneStatus();
	var videoUrl;

	if ( realOneStatus != "using" ) {	// if you're not using RealOne..
		var isSynacor = WM_readCookie( "synacor" );
		if ( is_aol || isSynacor ) {	// if you're using AOL or from Synacor, you're getting the popup
			realOneStatus = "notinstalled";
		} else {	// let's check your cookie
			var playerPref = WM_readCookie( "player" );
			if ( playerPref ) {
				switch ( playerPref.toUpperCase() ) {
					case "REALONE":
					case "REAL":	// if your preference is Real, but it's not installed..
						if ( realOneStatus != "installed" ) {
							realOneStatus = "notinstalled";	// ..you'll get the popup
						}
						break;
					case "WINDOWS MEDIA":
					case "QUICKTIME":
					default:
						realOneStatus = "notinstalled";	// popup window
						break;
				}
			}
		}
	}
		
	videoUrl = LV_getVideoUrl( videoPath, videoFormat, realOneStatus );
	
	if ( videoUrl.indexOf( ".exclude.html" ) > 0 ) {
		CNN_openPopup( videoUrl, '' + VIDEO_POPUP_WIDTH + 'x' + VIDEO_POPUP_HEIGHT, 'width=' + VIDEO_POPUP_WIDTH + ',height=' + VIDEO_POPUP_HEIGHT + ',scrollbars=no,resizable=no' );
	} else {
		top.location.href = videoUrl;
	} 
}


function med_vod( vidlocation ) {
	if ( ( agt.indexOf( "r1" ) != -1 ) && ( vidlocation.indexOf( "med.exclude.html" ) != -1 ) ) {
		var url = vidlocation.replace( "med.exclude.html", "r1.smi" );
		location.href = url;
	} else {
		CNN_openPopup( vidlocation, '620x460', 'width=620,height=460' );
	}
}


// _____________________________________________________ Netscape Hat

var NS_HAT_COOKIE_NAME = "nsHat";
var NS_HAT_COOKIE_HOURS = '';
var NS_HAT_COOKIE_PATH = "/";
var NS_HAT_COOKIE_DOMAIN = EditionDomain;

if ( !WM_readCookie( NS_HAT_COOKIE_NAME ) && document.referrer && WM_browserAcceptsCookies() )
{
	var referrerMatchArray = document.referrer.toLowerCase().match( "^[^:]+[:/]+([^@]+@)?([^:/]+)[:/]" );
	if ( referrerMatchArray && referrerMatchArray.length > 2 )
	{
		var referrerHostname = referrerMatchArray[2];
		if ( referrerHostname.match( "channels.netscape.com$" ) || referrerHostname.match( "cnn.netscape.cnn.com$" ) )
		{
			WM_setCookie( NS_HAT_COOKIE_NAME, "netscape", '', NS_HAT_COOKIE_PATH, NS_HAT_COOKIE_DOMAIN, '' );
		}
		else
		{
			WM_setCookie( NS_HAT_COOKIE_NAME, "cnn", '', NS_HAT_COOKIE_PATH, NS_HAT_COOKIE_DOMAIN, '' );
		}
	}
}

function displayHat()
{
	var imageDir = ( arguments.length > 0 ) ? ( '/' + arguments[0] + '/' ) : '/white/';
	var referrerHost = WM_readCookie( NS_HAT_COOKIE_NAME );
	switch ( referrerHost )
	{
		case "netscape":
			document.write( '<a href="http://www.netscape.com/" target="new"><img src="http://i.cnn.net/cnn/.element/img/1.1/nshat' + imageDir + 'ns.logo.gif" width="95" height="27" hspace="0" vspace="0" border="0"><\/a><a href="http://www.netscape.com/" target="new"><img src="http://i.cnn.net/cnn/.element/img/1.1/nshat' + imageDir + 'ns.home.gif" width="53" height="27" hspace="0" vspace="0" border="0"><\/a><a href="http://cnn.netscape.cnn.com/news/default.jsp" target="new"><img src="http://i.cnn.net/cnn/.element/img/1.1/nshat' + imageDir + 'ns.news.gif" width="51" height="27" hspace="0" vspace="0" border="0"><\/a><a href="http://sportsillustrated.netscape.cnn.com/" target="new"><img src="http://i.cnn.net/cnn/.element/img/1.1/nshat' + imageDir + 'ns.sports.gif" width="61" height="27" hspace="0" vspace="0" border="0"><\/a><a href="http://channels.netscape.com/ns/pf/default.jsp" target="new"><img src="http://i.cnn.net/cnn/.element/img/1.1/nshat' + imageDir + 'ns.money.gif" width="63" height="27" hspace="0" vspace="0" border="0"><\/a>' );
			break;
		default:
			document.write( '<a href="http://www.netscape.com/" target="new"><img src="http://i.cnn.net/cnn/.element/img/1.1/nshat' + imageDir + 'ns.logo.standalone.gif" width="91" height="27" hspace="2" vspace="0" border="0"><\/a>' );
			break;
	}
}

//CSI functions
var cnnCSIs = new Array();
var cnnUseDelayedCSI = 0;
var localUserAgent = navigator.userAgent.toLowerCase();
if((localUserAgent.indexOf('msie')>-1) && (localUserAgent.indexOf('mac')>-1)){cnnUseDelayedCSI = 1;}

function cnnAddCSI(id,source,args,breakCache)
{
	if(!args) { args='';}
	breakCache = ( !breakCache && ( source.charAt( 0 ) == '/' ) ) ? false : true;
	if(cnnUseDelayedCSI)
	{
		var newCSI = new Object();
		newCSI.src = source;
		newCSI.id  = id;
		newCSI.args = args;
		newCSI.breakCache = breakCache;
		cnnCSIs[cnnCSIs.length]=newCSI;
	}
	else
	{
		var today = new Date();
		var currTime = today.getTime();
		var iframeArgs = ( breakCache ) ? '&time='+currTime : '';
		if(args)
		{
			iframeArgs=iframeArgs+'&'+args;
		}
		var iframeHtmlSrc='<iframe src="'+source+'?domId='+id+iframeArgs+'" name="iframe'+id+'" id="iframe'+id+'" width="0" height="0" align="right" style="visibility:hidden"></iframe>';
		document.write(iframeHtmlSrc);
	}
}

function cnnUpdateCSI(html, id)
{
	var htmlContainerObj = document.getElementById( id ) || document.all[ id ];
	if(htmlContainerObj)
	{
		htmlContainerObj.innerHTML = html;
	}
	//force a refresh of the content area
	var htmlContentArea = document.body;
	if(htmlContentArea)
	{
		var previousTopVal = htmlContentArea.style.top || '0px';
		htmlContentArea.style.top = '1px';
		htmlContentArea.style.top = previousTopVal;
	}
}

function cnnHandleCSIs()
{
	if(document.body && document.body.innerHTML && cnnUseDelayedCSI)
	{
		var iframeOwner = document.getElementById( 'csiIframe' ) || document.all[ 'csiIframe' ];
		var iframeHtmlSrc = '';

		for(var incCounter=0;incCounter<cnnCSIs.length;incCounter++)
		{
			var src = cnnCSIs[incCounter].src;
			var id = cnnCSIs[incCounter].id;
			var today = new Date();
			var breakCache = cnnCSIs[incCounter].breakCache;
			var currTime = today.getTime();
			var args = ( breakCache ) ? '&time='+currTime : '';
			if(cnnCSIs[incCounter].args)
			{
				args=args+'&'+cnnCSIs[incCounter].args;
			}
			
			iframeHtmlSrc+='<iframe src="'+src+'?domId='+id+args+'" name="iframe'+id+'" id="iframe'+id+'" width="0" height="0" align="right"></iframe>';
		}
		if(iframeOwner)
		{
			iframeOwner.innerHTML=iframeHtmlSrc;
		}
	}
}

var cnnEnableCL = true;
if(window.location.hostname.indexOf('cnnstudentnews.')>-1) { cnnEnableCL = false; }
if(window.location.hostname.indexOf('edition.')>-1) { cnnEnableCL = false; }
// End CSI functions

// end

var cnnDocDomain = '';
if(location.hostname.indexOf('cnn.com')>0) {cnnDocDomain='cnn.com';}
if(location.hostname.indexOf('turner.com')>0) {if(document.layers){cnnDocDomain='turner.com:'+location.port;}else{cnnDocDomain='turner.com';}}
if(cnnDocDomain) {document.domain = cnnDocDomain;}

