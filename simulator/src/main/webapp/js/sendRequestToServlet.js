var awaitingResponses = 0;

/**
 * Send message request to servlet when the Submit button is pressed.
 * Use AJAX.
 */
function sendRequestToAgents() {
    let textInput = $('textarea#message').val();
    let language = $('input[name=language]:checked', '#language-form').val();
    let createRatingBool = $('input[name=rating-enabled]:checked', '#rating-options-form').val();
    $('textarea#message').val("");
    $("#output").append($('<div id="request"/>').append(textInput));
    awaitingResponses += 1;
    $('#awaiting-responses').text(awaitingResponses);
    $.ajax({
        url: "java-script-linker",
        type: 'POST',
        headers: {"Operation": "sendRequestToAgents"},
        dataType: 'json',
        data: {
            textInput: textInput,
            language: language
        },
        success: function (response) {
            $("#output").append($('<div id="response">-  </div>')
                .append(response.message)
            );
            if (createRatingBool == "true") {
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


function updateRating() {

}