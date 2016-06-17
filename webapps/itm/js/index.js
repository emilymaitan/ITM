/**
 * Created by Mai on 6/10/2016.
 */

$(document).ready(function() {

    // enable histogram on mouseover
    $('div.img-container').mouseenter( function() {
        console.log("Hovering over an image!");
        $(this).find("img.hist").slideDown();
    }).mouseleave( function() {
        console.log("Hovering out...");
        $(this).find("img.hist").slideUp();
    });

    // enable popovers
    $('[data-toggle="popover"]').popover();
});