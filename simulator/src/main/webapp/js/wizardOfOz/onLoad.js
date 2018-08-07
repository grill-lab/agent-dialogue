/**
 * Do on loading HTML.
 */
$(document).ready(function () {
    // When user presses "enter" in textarea, the request is being sent.
    $('#message').keypress(function(keyPressed){
        if(keyPressed.which == 13 && !keyPressed.shiftKey){
            keyPressed.preventDefault();
            sendRequestToAgents();
        }
    });
});