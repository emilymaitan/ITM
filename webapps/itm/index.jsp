<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="itm.image.*" %>
<%@ page import="itm.model.*" %>
<%@ page import="itm.util.*" %>
<%@ page import="java.awt.color.ColorSpace" %>
<!--
/*******************************************************************************
This file is part of the WM.II.ITM course 2016
(c) University of Vienna 2009-2016
*******************************************************************************/
-->

<!DOCTYPE html>
<html lang="en-US">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,  initial-scale=1.0">
    <meta name="author" content="Aichinger Mara & Eichinger Rene" />
    <meta name="description" content="Welcome to our ITM media library!" />
    <meta name="keywords" content="ITM, Media Library, Assignment 3, Aichinger, Eichinger" />
    <meta name="robots" content="noindex, nofollow" /> <!-- because we're in development -->

    <meta name="title" content="ITM Media Library | Home">

    <link rel="stylesheet" href="css/bootstrap.css">
    <link rel="stylesheet" href="css/style.css">

    <script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
    <script type="text/javascript" src="js/bootstrap.js"></script>

    <script type="text/javascript" src="js/raphael.js"></script>
    <script type="text/javascript" src="js/dracula_graffle.js"></script>
    <script type="text/javascript" src="js/dracula_graph.js"></script>
    <script type="text/javascript" src="js/index.js"></script>
</head>

<body>

    <!-- NAV -->
    <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed"
                        data-toggle="collapse" data-target="#navbar" aria-expanded="false"
                        aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span> <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="index.jsp">ITM Media Library</a>
            </div>
            <div id="navbar" class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="index.jsp">Home</a></li>
                </ul>

                <ul class="nav navbar-nav navbar-right">

                    <li>
                        <form class="navbar-form" action="tags.jsp" method="get">
                            <div class="input-group">
                                <input type="text" id="searchtag" name="tag" placeholder="search library by tag..." class="form-control">
                               <span class="input-group-btn">
                                <button class="btn btn-primary" type="submit" value="search">
                                    <span class="glyphicon glyphicon-search"></span>
                                </button>
                              </span>
                            </div>
                        </form>
                    </li>
                    <li>
                        <a href="graph.jsp">Graph</a>
                    </li>
                </ul>

            </div>
        </div> <!-- Container -->
    </nav>

    <div class="page-container">
        <div class="container">

            <%
                // get the file paths - this is NOT good style (resources should be loaded via inputstreams...)
                // we use it here for the sake of simplicity.
                String basePath = getServletConfig().getServletContext().getRealPath( "media"  );
                if ( basePath == null )
                    throw new NullPointerException( "could not determine base path of media directory! please set manually in JSP file!" );
                File base = new File( basePath );
                File imageDir = new File( basePath, "img");
                File audioDir = new File( basePath, "audio");
                File videoDir = new File( basePath, "video");
                File metadataDir = new File( basePath, "md");
                MediaFactory.init( imageDir, audioDir, videoDir, metadataDir );

                // get all media objects
                ArrayList<AbstractMedia> media = MediaFactory.getMedia();

                int c=0; // counter for rowbreak after 3 thumbnails.

                /* So I had a look at the Medialibrary. It loads the different types
                * sequentially - in the order IMAGES - AUDIO - VIDEO
                * We can work with the fact that they're in order to create a nicer layout!
                * */

                boolean firstImage = true, firstAudio = true, firstVideo = true;

                // iterate over all available media objects
                for ( AbstractMedia medium : media ) {
                    c++;

                    if (c % 3 == 1) { %> <div class="row"> <% } %>

            <% // handle images

                if ( medium instanceof ImageMedia ) {
                    // ***************************************************************
                    //  Fill in your code here!
                    // ***************************************************************

                    // show the histogram of the image on mouse-over --> did it! jQ all the way!

                    // display image thumbnail and metadata
                    ImageMedia img = (ImageMedia) medium;

                    if (firstImage) {
                        firstImage = false;
            %>
                <h2 id ="images" class="page-header">Images</h2>
            <%
                } // endif first image
            %>

                <div class="col-lg-4">
                    <div class="panel panel-info">
                        <div class="panel-heading">
                            <a href="media/img/<%= img.getInstance().getName()%>">
                                <%= img.getInstance().getName() %>
                                | <a href="#" data-toggle="popover" data-html="true"  data-placement="auto"
                                     title="Metadata for File: <%= img.getInstance().getName()%>"
                                     data-content='
                                        <b>Name:</b> <%= img.getName() %><br/>
                                        <b>Dimensions:</b> <%= img.getWidth() %>x<%= img.getHeight() %>px<br/>
                                        <b>Tags: </b><% for ( String t : img.getTags() ) { %><a href="tags.jsp?tag=<%= t %>"><%= t %></a> <% } %><br/>
                                        <b>Full-Sized Histogram:</b> <a href = "media/img_hist/<%= img.getInstance().getName() %>.hist.png">Click Here</a><br/>
                                        <br/>
                                        <!-- More metadata -->
                                        <b>Orientation:</b> <% if (img.getOrientation() == 0) { %> Landscape <% } else {%> Portrait <% } %><br/>
                                        <b>Number of Components: </b> <%= img.getNumComponents() %> <br/>
                                        <b>Number of Color Comp.: </b>  <%= img.getNumColorComponents() %> <br/>
                                        <b>Colorspace: </b>
                                            <% if (img.getColorSpaceType().equals(ColorSpace.TYPE_RGB)) { %> RGB <% }
                                                else if (img.getColorSpaceType().equals(ColorSpace.TYPE_GRAY)) {%> Gray <% }
                                                else { %> uncommon (ColorSpace <%= img.getColorSpaceType() %>) <% } %>
                                        <br/>
                                        <b>Transparency: </b>  <%= img.getTransparency() %> <br/>
                                        <b>Pixelsize: </b> <%= img.getPixelSize() %>  <br/>
                                    '>
                                    Metadata
                                  </a>
                                | <a href = "media/img_hist/<%= img.getInstance().getName() %>.hist.png">Full-Sized Hist.</a><br/>
                            </a>
                        </div>
                        <div class="panel-body img-container">
                            <a href="media/img/<%= img.getInstance().getName()%>">
                                <img class="img" src="media/md/<%= img.getInstance().getName() %>.thumb.png" border="0"/>
                                <img class="hist" src="media/img_hist/<%= img.getInstance().getName() %>.hist.png"/>
                            </a>
                        </div>
                    </div>
                </div>


            <%
            } else if ( medium instanceof AudioMedia ) {
                // display audio thumbnail and metadata
                AudioMedia audio = (AudioMedia) medium;

                if (firstAudio) {
                    firstAudio = false;
            %>
                <h2 id ="audio" class="page-header">Audios</h2>
            <%
                }
            %>

            <div class="col-lg-4">
                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <a href="media/audio/<%= audio.getInstance().getName()%>">
                            <%= audio.getInstance().getName()%>
                        </a>
                    </div>
                    <div class="panel-body">
                        <embed src="media/md/<%= audio.getInstance().getName() %>.wav"
                               autostart="false" width="150" height="30" />
                    </div>
                    <div class="panel-footer">
                        <b>Name:</b> <%= audio.getName() %><br/>
                        <b>Duration:</b> <%= audio.getDuration() %> sec.<br/>
                        <b>Tags:</b> <% for ( String t : audio.getTags() ) { %><a href="tags.jsp?tag=<%= t %>"><%= t %></a> <% } %><br/>

                        <b>Genre:</b> <%= audio.getGenre() != null ? audio.getGenre() : "unknown" %> <br/>
                        <b>Interpret:</b> <%= audio.getAuthor() != null ? audio.getAuthor() : "unknown" %><br/>
                        <b>Album:</b> <%= audio.getAlbum() != null ? audio.getAlbum() : "unknown" %><br/>
                        <b>Track:</b> <%= audio.getTrack() != -1 ? audio.getTrack() : "unknown" %><br/>
                        <b>Date:</b> <% if (audio.getDate() != null) audio.getDate(); else {%> unknown <%}%><br/>
                        <b>Comment:</b> <%= audio.getComment() != null ? audio.getComment() : "unknown" %><br/>

                        <b>Composer:</b> <%= audio.getComposer() != null ? audio.getComposer() : "unknown" %><br/>
                        <b>frequency:</b> <%= audio.getFrequency() != -1 ? audio.getFrequency() : "unknown" %><br/>
                        <b>Bitrate:</b> <%= audio.getBitrate() != -1 ? audio.getBitrate() : "unknown" %><br/>
                        <b>Channels:</b> <%= audio.getChannels() != -1 ? audio.getChannels() : "unknown" %><br/>

                        <b>Encoding:</b> <%= audio.getEncoding() != null ? audio.getEncoding() : "unknown" %><br/>
                    </div>
                </div>
            </div>

            <%
            } else if ( medium instanceof VideoMedia ) {
                // handle videos thumbnail and metadata...
                VideoMedia video = (VideoMedia) medium;

                if (firstVideo) {
                    firstVideo = false;

            %>
                <h2 id="video" class="page-header">Videos</h2>
            <%
                }
            %>

                <!-- VID -->

            <%
            } else {}

            if (c % 3 == 0) {

            %>

            </div> <!-- row -->

            <%
            }
                } // for
            %>

        </div>
    </div>

</body>
</html>
