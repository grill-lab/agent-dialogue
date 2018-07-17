var awaitingResponses = 0;

/**
 * Send message request to servlet when the Submit button is pressed.
 * Use AJAX.
 */
function sendRequestToServlet() {
    let textInput = $('textarea#message').val();
    let language = $('input[name=language]:checked', '#language-form').val();
    $('textarea#message').val("");
    $("#output").append($('<div id="request"/>').append(textInput));
    awaitingResponses += 1;
    $('#awaiting-responses').text(awaitingResponses);
    $.ajax({
        url: "java-script-linker",
        type: 'POST',
        dataType: 'json',
        data: {
            textInput: textInput,
            language: language
        },
        success: function (response) {
            $("#output").append($('<div id="response">-  </div>')
                .append(response.message)
            );
            if ($('input[name=rating-enabled]:checked', '#rating-options-form').val()) {
                createRating(response.responseId);
            }
            requestDetails = response.interactionRequest;
            responseDetails = response.interactionResponse;
        },
        error: function (data, status, error) {
            alert("Error data: " + data + "\nStatus: " + status + "\nError message:" + error);
        },
        complete: function () {
            awaitingResponses -= 1;
            $('#awaiting-responses').text(awaitingResponses);
        }
    });
}

/**
 * When user presses "enter" in textarea, the request is being sent.
 */
$(document).ready(function(){
    $('#message').keypress(function(keyPressed){
        if(keyPressed.which == 13 && !keyPressed.shiftKey){
            keyPressed.preventDefault();
            sendRequestToServlet();
        }
    });
});