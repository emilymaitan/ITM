/**
 * Created by Mai on 6/10/2016.
 */

$(document).ready(function() {
    $(".img").hover(function() {
        console.log("Hovering over an image...");
        $(this).closest(".hist").show();
    });
});