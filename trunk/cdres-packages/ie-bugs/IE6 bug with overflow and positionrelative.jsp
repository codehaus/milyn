<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<!-- saved from url=(0096)http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=xhtmlstrict&overflow=auto -->
<HTML xml:lang="en" xmlns="http://www.w3.org/1999/xhtml"><HEAD><TITLE>IE6 bug with overflow and position:relative</TITLE>
<META http-equiv=content-type content="application/xhtml+xml; charset=UTF-8">
<STYLE type=text/css>BODY {
	FONT-SIZE: 0.9em; COLOR: black; FONT-FAMILY: sans-serif; HEIGHT: 65em; BACKGROUND-COLOR: white
}
H1 {
	FONT-WEIGHT: bold; FONT-SIZE: 1.3em; COLOR: #666
}
UL.nav {
	PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; MARGIN: 1em 0px; PADDING-TOP: 0px
}
UL.nav LI {
	PADDING-RIGHT: 0px; DISPLAY: inline; PADDING-LEFT: 5px; PADDING-BOTTOM: 0px; MARGIN: 0px; BORDER-LEFT: black 1px solid; PADDING-TOP: 0px; WHITE-SPACE: nowrap
}
UL.nav LI.title {
	PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; BORDER-TOP-STYLE: none; PADDING-TOP: 0px; BORDER-RIGHT-STYLE: none; BORDER-LEFT-STYLE: none; BORDER-BOTTOM-STYLE: none
}
A {
	COLOR: #66c
}
.selected A {
	FONT-WEIGHT: bold; COLOR: black; TEXT-DECORATION: none
}
ADDRESS {
	FONT-SIZE: 0.8em; RIGHT: 5px; POSITION: absolute; TOP: 4px
}
.htmlcode {
	PADDING-RIGHT: 0.5em; DISPLAY: block; PADDING-LEFT: 0.5em; FONT-SIZE: 0.75em; PADDING-BOTTOM: 0.5em; MARGIN: 0.5em 1em; PADDING-TOP: 0.5em; WHITE-SPACE: pre; BACKGROUND-COLOR: #eea
}
.csscode {
	PADDING-RIGHT: 0.5em; DISPLAY: block; PADDING-LEFT: 0.5em; FONT-SIZE: 0.75em; PADDING-BOTTOM: 0.5em; MARGIN: 0.5em 1em; PADDING-TOP: 0.5em; WHITE-SPACE: pre; BACKGROUND-COLOR: #eea
}
.scrollme {
	OVERFLOW: auto; HEIGHT: 70px; BACKGROUND-COLOR: #ccc
}
.scrollme P {
	WIDTH: 100px; POSITION: relative; BACKGROUND-COLOR: #fcf
}
</STYLE>

<META content="MSHTML 6.00.2900.2722" name=GENERATOR></HEAD>
<BODY>
<H1>IE6 bug with overflow and position:relative</H1>
<P>This page demonstrates a bug in Windows Internet Explorer 6 
strict/transitional modes which causes relative positioned content to spill 
outside of an overflow constricted container. Static positioned content within 
that container will appear correctly (ie, not overflowing), exactly where it 
would have if the relative positioned content was rendered correctly. Scrollbars 
will appear and work for <CODE>overflow:scroll</CODE> and 
<CODE>overflow:auto</CODE>, even if they scroll through only empty space.</P>
<P>This bug does not appear in quirks mode, which is the only known workaround 
(to this author) at this time. Also, note that I had to make body hardcoded 
longer than height:auto provides, because the overflowing relative positioned 
content will (correctly) not cause the page to scroll further.</P>
<P>CSS for this example:</P><CODE class=csscode>.scrollme{ height:70px; 
overflow:auto; background-color:#ccc; } .scrollme p{ position:relative; 
background-color:#fcf; width:100px; } </CODE>
<P>HTML for this example:</P><CODE class=htmlcode>&lt;div class="scrollme"&gt; 
&lt;p&gt;blix&lt;/p&gt; &lt;p&gt;blix&lt;/p&gt; blarg &lt;p&gt;blix&lt;/p&gt; 
&lt;p&gt;blix&lt;/p&gt; &lt;p&gt;blix&lt;/p&gt; blarg &lt;/div&gt; </CODE>
<UL class="nav overflow">
  <LI class=title>Select overflow: 
  <LI class=selected><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=xhtmlstrict&amp;overflow=auto">auto</A> 

  <LI><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=xhtmlstrict&amp;overflow=scroll">scroll</A> 

  <LI><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=xhtmlstrict&amp;overflow=hidden">hidden</A> 
  </LI></UL>
<UL class="nav doctype">
  <LI class=title>Select doctype: 
  <LI class=selected><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=xhtmlstrict&amp;overflow=auto">xhtml 
  strict</A> 
  <LI><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=xhtmltransitional&amp;overflow=auto">xhtml 
  transitional</A> 
  <LI><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=htmlstrict&amp;overflow=auto">html 
  strict</A> 
  <LI><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=htmltransitional&amp;overflow=auto">html 
  transitional</A> 
  <LI><A 
  href="http://mt-olympus.com/emmett/bug_overflow_positionrelative.php?doctype=none&amp;overflow=auto">none</A> 
  </LI></UL>
<DIV class=scrollme>
<P>blix</P>
<P>blix</P>blarg 
<P>blix</P>
<P>blix</P>
<P>blix</P>blarg </DIV>
<ADDRESS class=contactinfo>Contact (click to email): <A 
href="http://www.mt-olympus.com/apollo/email.php?m=emmett">Emmett the Sane</A> 
</ADDRESS></BODY></HTML>
