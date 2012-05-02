<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>my blog</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="/feed/atom_1.0.xml" type="application/atom+xml" rel="alternate" title="Sitewide ATOM Feed" />
    <link href="/feed/rss_1.0.xml" type="application/rss+xml" rel="alternate" title="Sitewide RSS 1.0 Feed" />
    <link href="/feed/rss_2.0.xml" type="application/rss2+xml" rel="alternate" title="Sitewide RSS 2.0 Feed" />

    <!-- Le styles -->
    <link href="/assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/assets/css/jquery.tagsinput.css" rel="stylesheet">
    <link href="/assets/css/jquery.qp_tagcloud.css" rel="stylesheet">
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/ui-lightness/jquery-ui.css" rel="stylesheet">
    <style>
      body {
        padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
      }
    </style>
    <link href="/assets/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
  </head>

  <body>

    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="#">my blog</a>
          <div class="nav-collapse">
            <ul class="nav">
              <li class="active"><a href="/">Home</a></li>
              <li><a data-toggle="modal" href="#aboutModal">About</a></li>
              <li><a data-toggle="modal" href="#contactModal">Contact</a></li>
            </ul>
            <form class="navbar-search pull-left">
              <input type="text" class="search-query" placeholder="Search" id="search">
            </form>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

    <div class="container">
    
    <div class="row">
    <div class="span2"><h3>Tag cloud</h3><div id="tagcloud"></div></div>
    
    <div class="span10">
    