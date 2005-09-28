<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html><head>
<link rel="stylesheet" type="text/css" href="span-width-bug-via-fixed-width-div_files/style-for-bug-report.css"></head>

<body>
<div class="body">

  <div id="banner">
    <span id="blurb">
      When the browser window is made less wide than the fixed width specified
      for the empty &lt;div&gt; below this one (in this case, 800 pixels), this &lt;span&gt; will become
      less wide than its own containing &lt;div&gt; (notice the background
      color of this span does not fill out to the right-hand border, but
      rather the red &lt;body&gt; background color shows on the right).
      By contrast, when the browser window is made at least 800 pixels wide,
      this span's background color fills all the way out to the right border.
    </span>
  </div>


  <div class="centered">
  </div>


</div>
</body></html>