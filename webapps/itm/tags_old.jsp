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
<%

%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,  initial-scale=1.0">
    <meta name="author" content="Aichinger Mara & Eichinger Rene" />
    <meta name="description" content="Welcome to our ITM media library!" />
    <meta name="keywords" content="ITM, Media Library, Assignment 3, Aichinger, Eichinger" />
    <meta name="robots" content="noindex, nofollow" /> <!-- because we're in development -->

    <meta name="title" content="ITM Media Library | Tagged">

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


<%

    String tag = null;

    // ***************************************************************
    //  Fill in your code here!
    // ***************************************************************

    // get "tag" parameter

    // if no param was passed, forward to index.jsp (using jsp:forward)

    tag = request.getParameter("tag");
    if (tag==null || tag.isEmpty()) {
%>
<jsp:forward page="index.jsp"/>
<%
    }
%>
<h1>Media that is tagged with <%= tag %></h1>
<a href="index.jsp">back</a>

<%

    // ***************************************************************
    //  Fill in your code here!
    // ***************************************************************

    // get all media objects that are tagged with the passed tag

    // iterate over all available media objects and display them

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
    // iterate over all available media objects
    for ( AbstractMedia medium : media ) {
        boolean tagged = false;
        for (String mtag : medium.getTags())
            if (tag.equalsIgnoreCase(mtag)) {
                tagged = true;
                break;
            }
        if (!tagged) continue;

        c++;
%>
<div class="media-container">
    <%

        // handle images
        if ( medium instanceof ImageMedia ) {
            // ***************************************************************
            //  Fill in your code here!
            // ***************************************************************

            // show the histogram of the image on mouse-over --> did it! jQ all the way!

            // display image thumbnail and metadata
            ImageMedia img = (ImageMedia) medium;
    %>
    <div class="img-container">
        <a href="media/img/<%= img.getInstance().getName()%>">
            <img class="img" src="media/md/<%= img.getInstance().getName() %>.thumb.png" border="0"/>
            <img class="hist" src="media/img_hist/<%= img.getInstance().getName() %>.hist.png"/>
        </a>
    </div>
    <div>
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
    </div>
    <%
    } else
    if ( medium instanceof AudioMedia ) {
        // display audio thumbnail and metadata
        AudioMedia audio = (AudioMedia) medium;
    %>
    <div style="width:200px;height:200px;padding:10px;">
        <br/><br/><br/><br/>
        <embed src="media/md/<%= audio.getInstance().getName() %>.wav" autostart="false" width="150" height="30" />
        <br/>
        <a href="media/audio/<%= audio.getInstance().getName()%>">
            Download <%= audio.getInstance().getName()%>
        </a>
    </div>
    <div>
        Name: <%= audio.getName() %><br/>
        Duration: <%= audio.getDuration() %><br/>
        Tags: <% for ( String t : audio.getTags() ) { %><a href="tags.jsp?tag=<%= t %>"><%= t %></a> <% } %><br/>
    </div>
    <%
    } else
    if ( medium instanceof VideoMedia ) {
        // handle videos thumbnail and metadata...
        VideoMedia video = (VideoMedia) medium;
    %>
    <div style="width:200px;height:200px;padding:10px;">
        <a href="media/video/<%= video.getInstance().getName()%>">

            <object width="200" height="200">
                <param name="movie" value="media/md/<%= video.getInstance().getName() %>_thumb.avi">
                <embed src="media/md/<%= video.getInstance().getName() %>_thumb.avi" width="200" height="200">
                </embed>
            </object>

        </a>
    </div>
    <div>
        Name: <a href="media/video/<%= video.getInstance().getName()%>"><%= video.getName() %></a><br/>
        Tags: <% for ( String t : video.getTags() ) { %><a href="tags.jsp?tag=<%= t %>"><%= t %></a> <% } %><br/>
    </div>
    <%
        } else {
        }

    %>
</div>
<%
    if ( c % 3 == 0 ) {
%>
<div style="clear:left"/>
<%
        }

    } // for

%>

</body>
</html>
